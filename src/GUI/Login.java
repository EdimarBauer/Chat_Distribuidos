/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import BO.ClienteBO;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dion and Edimar
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form ClienteGUI
     */
    private final ClienteBO clienteBO;
    
    public Login() throws SocketException, InterruptedException, IOException {
        clienteBO = new ClienteBO();
        initComponents();
        jtfNome.grabFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jtfPorta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jtfIP = new javax.swing.JTextField();
        jtfNome = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Nome:");

        jLabel3.setText("Porta:");

        jtfPorta.setText("20000");
        jtfPorta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfPortaKeyTyped(evt);
            }
        });

        jLabel1.setText("IP:");

        jtfIP.setText("127.0.0.1");
        jtfIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfIPKeyTyped(evt);
            }
        });

        jtfNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfNomeActionPerformed(evt);
            }
        });
        jtfNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfNomeKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addComponent(jLabel1)
                                .addGap(42, 42, 42))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jtfPorta)
                            .addComponent(jtfIP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(jtfNome, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtfIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtfPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtfNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jButton1)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
    * Faz a validação dos campos e tenta se logar
    */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jtfPorta.getText().length() == 0 || jtfNome.getText().length() == 0 || jtfIP.getText().length() == 0){
            System.out.println("Preencha os campos");
            return;
        }
        int porta = Integer.parseInt(jtfPorta.getText());
        if (porta < 10000 || porta > 65000){
            System.out.println("Porta inválida. Digite uma porta entre 10000 e 65000");
            jtfPorta.setText("");
            return;
        }
        String ip = jtfIP.getText();
        if (validarIP(ip) == false){
            return;
        }
        
        String name = jtfNome.getText();
        if (name.equals("all")){
            System.out.println("Nome não pode ser 'all'");
            jtfNome.setText("");
            return;
        }
        try {
            if (clienteBO.logar(ip, porta, name) == false)
                return;
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        dispose();
        Chat chat = new Chat(clienteBO);
    }//GEN-LAST:event_jButton1ActionPerformed

    /*
    * Faz a validação básica que um ip deve conter como 3 pontos e números menores que 256 em cada octeto
    */
    private boolean validarIP(String ip){
        if (ip.length() < 7){
            System.out.println("Faltam números no IP");
            return false;
        }
        String s;
        long j = 0;
        int k = 0;
        for (int i = 0; i < ip.length(); i++){
            if (ip.charAt(i) == '.'){
                if (j > 255){
                    return false;
                }else{
                    j = 0;
                    k++;
                }
            }else
                j = j * 10 + ip.charAt(i) - 48;
        }
        if (k != 3 || j > 255){
            return false;
        }
        return true;
    }
    
    /*
    * Não admite outros caracteres que não sejam números no campo porta nem que a porta tenha mais que 5 dígitos
    */
    private void jtfPortaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfPortaKeyTyped
        if (evt.getKeyChar() < '0' || evt.getKeyChar() > '9' || jtfPorta.getText().length() > 5){
            evt.consume();
        }
    }//GEN-LAST:event_jtfPortaKeyTyped

    /*
    * Admite apenas números e pontos para o IP e não aceita mais do que 15 caracteres
    */
    private void jtfIPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfIPKeyTyped
        if (evt.getKeyChar() != '.' && (evt.getKeyChar() < '0' || evt.getKeyChar() > '9') || jtfIP.getText().length() > 15) 
            evt.consume();
    }//GEN-LAST:event_jtfIPKeyTyped

    /*
    * Redireciona para o Login quando dado o enter no campo nome
    */
    private void jtfNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfNomeActionPerformed
        jButton1ActionPerformed(evt);
    }//GEN-LAST:event_jtfNomeActionPerformed

    /*
    * Não admite alguns caracteres especiais nem que o nome seja maior que 16 caracteres
    */
    private void jtfNomeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfNomeKeyTyped
        char c = evt.getKeyChar();
        if (c == '#' || c == ',' || c == ';' || c == ' ' || c == '.' || jtfNome.getText().length() > 16){
            evt.consume();
        }
    }//GEN-LAST:event_jtfNomeKeyTyped

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Login().setVisible(true);
            } catch (SocketException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jtfIP;
    private javax.swing.JTextField jtfNome;
    private javax.swing.JTextField jtfPorta;
    // End of variables declaration//GEN-END:variables
}
