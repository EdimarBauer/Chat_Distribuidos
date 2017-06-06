/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import VO.Cliente;
import VO.Servidor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dion and Edimar
 */
public final class ServidorBO implements Runnable{
    private Servidor servidor;
    private final int TAM_DATAGRAMA = 1024;
    private final int TEMPO = 11000;
    private byte[] dados;
    private static DatagramSocket datagrama;
    private List<Cliente> salaChat = new ArrayList<>();
    private Thread thread;
   
    /*
    * Construtor. Inicia a escuta do socket do servidor
    */
    public ServidorBO(Servidor servidor) throws InterruptedException {
        this.servidor = servidor;
        try {
            datagrama = new DatagramSocket(servidor.getPort());   
            thread = new Thread(this);
            thread.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        //atualizaLista();
    }

    @Override
    public void run() {
        try {
            System.out.println("Esperando conexao...");
            while (true) {
                dados = new byte[TAM_DATAGRAMA];
                DatagramPacket entrada = new DatagramPacket(dados, dados.length);
                datagrama.receive(entrada);
                String s = new String(entrada.getData()).trim();
                if (s.length() > 1 && s.charAt(1) != '3')
                    System.out.println("Servidor recebeu: " + s);
                receberPacote(entrada);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (datagrama != null) {
                ServidorBO.datagrama.close();
            }
        }
    }

    /*
    * Faz a verificação do pacote recebido e desloca ele para a função correspondente
    */
    private void receberPacote(DatagramPacket entrada) throws UnknownHostException, IOException {       
        
        Cliente cliente = verificar(entrada);
        if (cliente == null) return;
        
        cliente.setPing(System.currentTimeMillis());
        String[] protocolo = new String(entrada.getData()).trim().split("#");
        
        switch (protocolo[0]) {
            case "00":
                addSala(cliente);
                break;
            case "01":
                desconectar(cliente);
                break;
            case "02":
                mensagem(cliente, protocolo);
                break;
            case "03":
                break;
            default:
                enviarMensagem("82#Protocolo não reconhecido", cliente);
        }
    }
    
    /*
    * Faz as verificações do datagrama para ver se estão de acordo com o protocolo
    */
    private Cliente verificar(DatagramPacket entrada) throws IOException{
        String ip = entrada.getAddress().toString();
        if (ip.charAt(0) == '/') ip = ip.substring(1);
        
        if (entrada.getData().length > TAM_DATAGRAMA){
            enviarMensagem("82#Datagrama muito grande", new Cliente(ip, entrada.getPort(), "Desconhecido"));
            return null;
        }
        String s = new String(entrada.getData()).trim();
        if (!s.contains("#")){
            enviarMensagem("82#Protocolo não contém hashtag", new Cliente(ip, entrada.getPort(), "Desconhecido"));
            return null;
        }
        
        String[] protocolo = s.split("#");
        Cliente cliente, remetente;
        if (protocolo[0].equals("02")){
            if (protocolo.length != 4){
                if (protocolo.length > 2)
                    enviarMensagem("82#Protocolo com erro", new Cliente(ip, entrada.getPort(), protocolo[2]));
                else
                    enviarMensagem("82#Protocolo com erro", new Cliente(ip, entrada.getPort(), "Desconhecido"));
                return null;
            }
            remetente = new Cliente(ip, entrada.getPort(), protocolo[2]);
            
            if (!protocolo[1].equals("all")){
                if (protocolo[1].length() > 16 || getCliente(protocolo[1], "") == null){
                    enviarMensagem("82#O usuário " + protocolo[1] + " não está logado", remetente);
                    return null;
                }
            }
            if (protocolo[3].length() > 128){
                enviarMensagem("81#Mensagem muito grande", remetente);
                return null;
            }
        }else{
            if (protocolo.length != 2){
                if (protocolo.length > 1)
                    enviarMensagem("82#Protocolo com erro", new Cliente(ip, entrada.getPort(), protocolo[1]));
                else
                    enviarMensagem("82#Protocolo com erro", new Cliente(ip, entrada.getPort(), "Desconhecido"));
                return null;
            }
            remetente = new Cliente(ip, entrada.getPort(), protocolo[1]);
        }
        
        cliente = getCliente(remetente.getNome(), ip);
        if (protocolo[0].equals("00")){
            if (remetente.getNome().equals("all")){
                enviarMensagem("80#Nome all está reservado para o sistema", remetente);
                return null;
            }
            if (cliente != null){
                enviarMensagem("80#O usuário " + remetente.getNome() + " já está logado", remetente);
                return null;
            }
            if (ip.length() < 4){
                enviarMensagem("80#Ip inválido", remetente);
                return null;
            }
            s = remetente.getNome();
            if (s.isEmpty() || s.contains(" ") || s.contains(",") || s.contains(";") || s.contains(".")){
                enviarMensagem("80#Nome de usuário inválido", remetente);
                return null;
            }
            cliente = remetente;
        }else{
            if (cliente == null){
                enviarMensagem("82#Você não está logado", remetente);
                return null;
            }
        }
        
        return cliente;
    }

    /*
    * Adiciona o cliente na sala de chat
    */
    private void addSala(Cliente cliente) throws UnknownHostException, IOException {
        salaChat.add(cliente);
        enviarMensagem("40#", cliente);
        enviarListaClientesConectados();
    }

    /*
    * Recebe uma mensagem do cliente para ser enviada a todos ou a alguém específico
    */
    private void mensagem(Cliente cliente, String[] protocolo) throws IOException {
        String msg = "02#" + protocolo[1] + "#" + protocolo[2] + "#" + protocolo[3];
        if (protocolo[1].equals("all")){
            enviarBroadcast(msg);
        }else{
            enviarMensagem(msg, cliente);
            if (!cliente.getNome().equals(protocolo[1]))
                enviarMensagem(msg, getCliente(protocolo[1], ""));
        }
    }
    
    /*
    * Envia a mensagem para todos
    */
    private void enviarBroadcast(String msg){
        salaChat.stream().forEach((cliente) -> {
            try {
                enviarMensagem(msg, cliente);
            } catch (IOException ex) {
                Logger.getLogger(ServidorBO.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /*
    * Envia a mensagem para o cliente
    */
    private void enviarMensagem(String mensagem, Cliente cliente) throws UnknownHostException, IOException {      
        DatagramPacket envioPacote = new DatagramPacket(mensagem.getBytes(), mensagem.getBytes().length, 
                InetAddress.getByName(cliente.getIP()), cliente.getPorta());
        datagrama.send(envioPacote);
        char c = mensagem.charAt(1);
        if (mensagem.charAt(0) == '8' && (c == '2' || c == '1' || c == '0'))
            System.err.println("Enviou " + mensagem + "  -> " + cliente.getNome());
        else
            System.out.println("Enviou " + mensagem + "  -> " + cliente.getNome());
    }
    
    /*
    * Retorna um cliente da lista ou null se não encontrar
    */
    private Cliente getCliente(String nome, String ip){
        for(Cliente cliente : salaChat){
            if (cliente.getNome().equals(nome)){
                if (ip.equals("") || cliente.getIP().equals(ip))
                    return cliente;
            }
        }
        return null;
    }
    
    /*
    * Envia lista de todos os clientes que estão conectados 
    */
    private void enviarListaClientesConectados(){
        String lista = "42";
        lista = salaChat.stream().map((cliente) -> "#" + cliente.getNome()).reduce(lista, String::concat);
        enviarBroadcast(lista);
    }
    
    /*
    * Atualiza a lista dos clientes conectados
    */
    private void atualizaLista() throws InterruptedException{
        new Thread(){
            @Override
            public void run(){
                while(true){
                    try {
                        Thread.sleep(TEMPO);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServidorBO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //System.out.println("Iniciando verificação de logins...");
                    boolean change = false;
                    long tempo = System.currentTimeMillis();
                    while(true){
                        boolean flag = false;
                        for (Cliente cliente : salaChat){
                            if ((tempo - cliente.getPing()) > TEMPO){
                                flag = true;
                                change = true;
                                System.out.println(cliente.getNome() + " deslogado");
                                salaChat.remove(cliente);
                                break;
                            }
                        }
                        if (flag == false) break;
                    }
                    if (change){
                        enviarListaClientesConectados();
                    }else{
                        //System.out.println("Nenhuma alteração");
                    }
                }
            }
        }.start();
    }
    
    /*
    * Desconecta o cliente
    */
    private void desconectar(Cliente cliente) throws IOException {     
        enviarMensagem("41#", cliente);
        salaChat.remove(cliente);
        enviarListaClientesConectados();
    }
}
