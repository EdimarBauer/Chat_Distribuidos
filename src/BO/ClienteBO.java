/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import GUI.Chat;
import VO.Cliente;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

/**
 *
 * @author dion
 */
public class ClienteBO implements Runnable{
    public Cliente cliente;
    byte[] aEnviar;
    byte[] aReceber;
    String mensagem;
    DatagramSocket datagrama;
    private Thread thread;

    public ClienteBO() throws SocketException {
        this.datagrama = new DatagramSocket();
    }
    
    public boolean logar(int porta, String username, String ip){ 
        try {
            mensagem = "00#" + username;
            aEnviar = new byte[1024];
            aReceber = new byte[1024];
            aEnviar = mensagem.getBytes();
            cliente = new Cliente(ip, porta, username);
            DatagramPacket saida = new DatagramPacket(mensagem.getBytes(), mensagem.length(), 
                    InetAddress.getByName(cliente.getIP()), cliente.getPorta());   
            datagrama.send(saida);
            System.out.println("Enviou: " + mensagem);

            DatagramPacket entrada = new DatagramPacket(aReceber, aReceber.length);
            datagrama.receive(entrada);
            String dados = new String(entrada.getData()).trim();
            String[] validar = dados.split("#");
            System.out.println("Recebeu " + dados);

            if (validar[0].equals("40")) {
                JOptionPane.showMessageDialog(null, "Login Confirmado");
            }else if (validar[0].equals("80")) {
                JOptionPane.showMessageDialog(null, username + " já existe. Use outro nome");
                return false;
            }
            
        } catch (UnknownHostException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        thread = new Thread(this);
        thread.start();
        
        return true;
    }
    
    private boolean receberPacote(DatagramPacket entrada) {
        String dados = new String(entrada.getData()).trim();
        System.out.println("Recebeu " + dados);
        String validar[] = dados.split("#");
        switch (validar[0]) {
            case "41":
                System.out.println("Logout ok");
                return false;
            case "42":
                System.err.println("Lista de clientes");;
                break;
            case "81":
                listarErros(dados);
                System.out.println("mensagem invalida");
                break;
            default:
                System.out.println("Protocolo não identificado.");;
        }
        return true;
    }
    
    public boolean sair(){
        try{
            mensagem = "01#" + cliente.getNome();
            aEnviar = new byte[1024];
            aReceber = new byte[1024];
            aEnviar = mensagem.getBytes();
            DatagramPacket saida = new DatagramPacket(aEnviar, aEnviar.length, InetAddress.getByName(cliente.getIP()), cliente.getPorta());   
            datagrama.send(saida);
            System.out.println("Enviou " + mensagem);
        }catch (Exception e){
            System.out.println("Falha");
            return false;
        }
        return true;
    }
   
    private void listarUsuarios(String dados) {
        System.out.println("BO.ClienteBO.listarUsuarios() "+ dados);
    }

    private void listarErros(String dados) {
        System.err.println("BO.ClienteBO.listarErros() "+ dados);
    }
    
     private void enviarPacote(String msg) {
        try {
            aEnviar = new byte[1024];
            System.out.println("Cliente enviou: " + msg);
            aEnviar = msg.getBytes();
            DatagramPacket saida = new DatagramPacket(aEnviar, aEnviar.length,InetAddress.getByName(cliente.getIP()), cliente.getPorta());
           datagrama.send(saida);
        } catch (IOException ex) {
            String erro = "Deu ruim ao enviar";
            System.out.println(erro + ex);
        }
    }
     
    @Override
    public void run() {
        try {
            while (true) {
                aReceber = new byte[1024];
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
    }
}
