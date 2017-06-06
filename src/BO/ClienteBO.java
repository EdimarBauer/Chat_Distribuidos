/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import GUI.Chat;
import VO.Cliente;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dion and Edimar
 */
public class ClienteBO implements Runnable{
    
    private final int TAM_DATAGRAMA = 1024;
    private volatile boolean conectado = false; 
    public Cliente cliente = null;
    private byte[] aReceber;
    private String mensagem;
    private volatile DatagramSocket datagrama;
    private final Thread thread;
    private volatile Chat chat;

    /*
    * Construtor. Cria a thread responsável por receber mensagens do socket
    */
    public ClienteBO() throws SocketException, InterruptedException, IOException {
        this.datagrama = new DatagramSocket();
        thread = new Thread(this);
        thread.start();
    }
    
    /*
    * Retorna true caso consiga se coectar ao servidor
    */
    public boolean logar(String ip, int porta, String username) throws IOException, InterruptedException{     
        if (cliente == null)
            cliente = new Cliente(ip, porta, username);
        else{
            cliente.atualizar(ip, porta, username);
        }
        enviarMensagem("00#" + username);
        long tempo = System.currentTimeMillis();
        while(conectado == false){
            if ( (System.currentTimeMillis() - tempo) > 3000){
                System.out.println("Não conseguiu conexão");
                return false;
            }
        }
        
        //pingar();
        return true;
    }
    
    /*
    * Fica pingando no servidor de 5 em 5 segundos
    */
    private void pingar() throws InterruptedException, IOException{
        new Thread(){
            @Override
            public void run(){
                while(!datagrama.isClosed()){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClienteBO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        enviarMensagem("03#" + cliente.getNome());
                    } catch (IOException ex) {
                        Logger.getLogger(ClienteBO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }
    
    /*
    * Thread responsável por receber mensagens do socket
    */
    @Override
    public void run() {
        try {
            while (true) {
                aReceber = new byte[TAM_DATAGRAMA];
                DatagramPacket entrada = new DatagramPacket(aReceber, aReceber.length);
                datagrama.receive(entrada);
                if (receberPacote(entrada) == false) {
                    datagrama.close();
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            if (datagrama != null) {
                datagrama.close();
            }
        }
        
        chat.dispose();
    }
    
    /*
    * Verifica a mensagem recebida e a desloca para a função responsável
    */
    private boolean receberPacote(DatagramPacket entrada) {
        String dados = new String(entrada.getData()).trim();
        if (dados.length() > TAM_DATAGRAMA){
            System.err.println("Recebido datagrama maior do que o esperado");
            return true;
        }
        if (!dados.contains("#")){
            System.err.println("Falta de Hashtags");
            return true;
        }
        
        String protocolo[] = dados.split("#");
        if (protocolo[0].equals("82")) 
            System.err.println("Recebeu: " + new String(entrada.getData()).trim());
        else
            System.out.println("Recebeu: " + new String(entrada.getData()).trim());
        
        switch (protocolo[0]) {
            case "02":
                tratarMensagem(protocolo);
                break;
            case "40":
                setConectado(true);
                break;
            case "41":
                System.out.println("Logout ok");
                return false;
            case "42":
                listarUsuarios(protocolo);
                break;
            case "80":
                System.err.println("Este usuário já está conectado");
                break;
            case "81":
                System.err.println("Mensagem inválida");
                break;
            case "82":
                break;
            default:
                System.err.println("Protocolo não identificado.");
        }
        
        return true;
    }
    
    /*
    * Envia todas as mensagens ao servidor
    */
    public void enviarMensagem(String mensagem) throws UnknownHostException, IOException{
        DatagramPacket saida = new DatagramPacket(mensagem.getBytes(), mensagem.getBytes().length, 
                    InetAddress.getByName(cliente.getIP()), cliente.getPorta());   
        if (!datagrama.isClosed())
            datagrama.send(saida);
        
        if (mensagem.length() > 1 && mensagem.charAt(1) != '3')
            System.out.println("Enviou: " + mensagem);
    }
    
    /*
    * Faz o tratamento das mensagens recebidas de outros usuários
    */
    private void tratarMensagem(String[] protocolo){
        if (protocolo.length != 4){
            System.err.println("Protocolo com erro na quantidade de Hashtags");
            return;
        }
        if (protocolo[1].equals("all")){
            chat.setArea(protocolo[2] + ": " + protocolo[3]);
        }else{
            chat.setArea(protocolo[2] + " -> " + protocolo[1] + ": " + protocolo[3]);
        }
    }
    
    /*
    * Lista todos os usuários ativos
    */
    private void listarUsuarios(String[] dados) {
        while (chat == null){}
        String ativos = "";
        for (int i = 1; i < dados.length; i++){
            ativos += dados[i] + "\n";
        }
        chat.setLista(ativos);
    }
    
    public void setChat(Chat chat){
        this.chat = chat;
    }
    
    private void setConectado(boolean flag){
        conectado = flag;
    }
    
    public String getMensagem(){
        return mensagem;
    }
}
