/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VO;

/**
 *
 * @author dion
 */
public class Cliente {
    private String ip;
    private int port;
    private String username;
    
    public Cliente( String ip, int port, String username) {
        this.port = port;
        this.ip = ip;
        this.username = username;        
    }
    
    public int getPorta() {
        return port;
    }

    public void setPorta(int port) {
        this.port = port;
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getNome() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}
