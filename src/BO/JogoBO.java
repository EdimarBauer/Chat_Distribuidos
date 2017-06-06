/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import VO.Cliente;

/**
 *
 * @author Edimar
 */
public class JogoBO {
    
    public JogoBO(){
        iniciarThread();
    }
    
    public void buscarPartida(Cliente cliente){
        enviarMensagem("10#" , cliente);
    }
  
    private void enviarMensagem(String msg, Cliente cliente){
        
    }
    
    private void iniciarThread(){
        new Thread(){
            @Override
            public void run(){
                
            }
        }.start();
    }
}
