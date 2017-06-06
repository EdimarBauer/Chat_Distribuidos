/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VO;

/**
 *
 * @author Dion and Edimar
 */
public class Cliente {
    private String ip;
    private int porta;
    private String nome;
    private long ping;
    
    public Cliente( String ip, int porta, String nome) {
        this.porta = porta;
        this.ip = ip;
        this.nome = nome;        
    }
    
    public void atualizar(String ip, int porta, String nome){
        setIP(ip);
        setPorta(porta);
        setNome(nome);
    }
    
    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public void setPing(long ping){
        this.ping = ping;
    }
    
    public long getPing(){
        return ping;
    }
}
