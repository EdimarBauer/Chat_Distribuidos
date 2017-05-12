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

/**
 *
 * @author dion
 */
public class ServidorBO implements Runnable{
    private Servidor servidor;
    private byte[] dados;
    private static DatagramSocket datagrama;
    private List<Cliente> salaChat = new ArrayList<>();
    private Thread thread;
   //  StringUtil strUtil = new StringUtil();
  //  ValidacaoDatagramas validaDatagrama = new ValidacaoDatagramas();
  //  EnvioDatagramas enviaDatagramas = new EnvioDatagramas();
   
    public ServidorBO(Servidor servidor) {
        this.servidor = servidor;
        try {
            iniciarChat();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void iniciarChat() throws Exception {
        datagrama = new DatagramSocket(servidor.getPort());
        
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Esperando conexao...");
            while (true) {
                dados = new byte[1024];
                DatagramPacket entrada = new DatagramPacket(dados, dados.length);
                datagrama.receive(entrada);
                System.out.println("Servidor recebeu: " + new String(entrada.getData()).trim());
                receberPacote(entrada);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (datagrama != null) {
                this.datagrama.close();
            }
        }
    }

    private void receberPacote(DatagramPacket entrada) throws UnknownHostException, IOException {       
        String dados = new String(entrada.getData()).trim(); 
        String[] protocolo = dados.split("#");
        
        switch (protocolo[0]) {
            case "00":
                addSala(entrada);
                break;
            case "01":
                sair(entrada);
                break;
            case "02":
                mensagem(entrada);
                break;
            default:
                System.out.println("Entrada nao reconhecida " + protocolo[0]);
        }
    }

    private void addSala(DatagramPacket entrada) throws UnknownHostException, IOException {
        String username = getUsernameDatagrama(new String(entrada.getData()));
        String ip = entrada.getAddress().toString();
        if (ip.charAt(0) == '/'){
            ip = ip.substring(1);
        }
        Cliente cliente = new Cliente(ip, entrada.getPort(), username);
        if (nomeValido(username) == false){
            enviarMensagem("80#", cliente);
        }else{
            salaChat.add(cliente);
            enviarMensagem("40#", cliente);
        }
        //enviarMensagensBroadcast("42#"+username.trim()+" entrou na sala...");
    }

    private void sair(DatagramPacket entrada) throws IOException {     
        Cliente cliente = null;
        String ip = entrada.getAddress().toString();
        if (ip.charAt(0) == '/') {
            ip = ip.substring(1);
        }
        for (int i = 0; i < salaChat.size(); i++) {
            //System.out.println("Nome: " + salaChat.get(i).getNome() + " Porta: " + salaChat.get(i).getPorta() + 
            //        " IP: " + salaChat.get(i).getIP() + " i= " + i);
            if (ip.equals(salaChat.get(i).getIP())) {
                cliente = salaChat.get(i);
                cliente.setPorta(entrada.getPort());
                salaChat.remove(i);
                break;
            }
        }
        if (cliente == null){
            System.out.println("Cliente nao encontrado.");
            //System.out.println("Porta: " + entrada.getPort() + " Ip: " + ip);
            return;
        }
        enviarMensagem("41#", cliente);
    }

    private boolean nomeValido(String username) {
        for(Cliente cliente : salaChat){
            if (cliente.getNome().equals(username)){
                return false;
            }
        }        
        return true;
    }
     
    public String getUsernameDatagrama(String datagrama){
        String username = "";
        int i = 3;
        
        while(i < datagrama.length()){
            username += datagrama.substring(i, i + 1);
            i++;
        }       
        return username;
    }
    
    private void enviarMensagem(String mensagem, Cliente cliente) throws UnknownHostException, IOException {      
        DatagramPacket envioPacote = new DatagramPacket(mensagem.getBytes(), mensagem.length(), 
                InetAddress.getByName(cliente.getIP()), cliente.getPorta());
        datagrama.send(envioPacote);
        System.out.println("Enviou " + mensagem + " para " + cliente.getNome());
    }
    
    private void enviarMensagensBroadcast(String msg) {
        System.out.println("Servidor enviou: " + msg);
        salaChat.stream().forEach((cliente) -> {
            try {
                DatagramPacket saida = new DatagramPacket(msg.getBytes(),
                        msg.length(), InetAddress.getByName(cliente.getIP()), cliente.getPorta());
                datagrama.send(saida);
            } catch (IOException ex) {
                System.out.println("BO.ServidorBO.enviarMensagensBroadcast()"+ ex); 
            }
        });
    }

    private void enviarPacote(String mensagem, DatagramPacket entrada) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void mensagem(DatagramPacket entrada) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
