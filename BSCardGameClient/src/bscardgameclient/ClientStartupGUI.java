/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bscardgameclient;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 *
 * @author Shravan Jambukesan
 */
public class ClientStartupGUI extends javax.swing.JDialog {

    /**
     * Creates new form ClientStartupGUI
     */
    String gameCode = "";
    public ClientStartupGUI(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        setResizable(false);
        initComponents();
        gameCodeEntry.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                gameCodeEntry.setText("");
            }
        });
    }
    
    public static boolean isNumeric(String s) 
    {
        try 
        {
            int i = Integer.parseInt(s);
        } 
        catch (NumberFormatException | NullPointerException ex) 
        {
            return false;
        }
        return true;
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startupLabel = new javax.swing.JLabel();
        gameCodeEntry = new javax.swing.JTextField();
        createLobbyButton = new javax.swing.JButton();
        joinGameButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        startupLabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        startupLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        startupLabel.setText("BS Card Game");

        gameCodeEntry.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gameCodeEntry.setText("Enter Game Code to Join Game");

        createLobbyButton.setText("Create Lobby");
        createLobbyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createLobbyButtonActionPerformed(evt);
            }
        });

        joinGameButton1.setText("Join Game");
        joinGameButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinGameButton1ActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bscardgameclient/Resources/pips.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(123, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel1))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(joinGameButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(createLobbyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(startupLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(153, 153, 153))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(gameCodeEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(108, 108, 108))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(startupLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(gameCodeEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(joinGameButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(createLobbyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /*
        Submit button click event handler
    */
    private void createLobbyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createLobbyButtonActionPerformed
       /* 
	gameCode = gameCodeEntry.getText();
       // Check if the game code entered meets out requirements
       if(gameCode.isEmpty() || gameCode.length() != 3 || gameCode.contains(" ") || !isNumeric(gameCode) || Integer.parseInt(gameCode) < 500 || Integer.parseInt(gameCode) > 599)
       {
           JOptionPane.showMessageDialog(null, "Please enter a valid Game Code. A valid Game Code is 3 digits (numerical characters) with no spaces.", "Invalid Game Code", JOptionPane.ERROR_MESSAGE);
       }
       else
       {
           System.out.println(gameCode);
           // Start the game runner
           GameRunner runner = new GameRunner(gameCode, true);
       }
	*/
       GameRunner runner = new GameRunner("-1", true);
    }//GEN-LAST:event_createLobbyButtonActionPerformed

    private void joinGameButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinGameButton1ActionPerformed
        gameCode = gameCodeEntry.getText();
       // Check if the game code entered meets out requirements
       if(gameCode.isEmpty() || gameCode.length() != 3 || gameCode.contains(" ") || !isNumeric(gameCode) || Integer.parseInt(gameCode) < 500 || Integer.parseInt(gameCode) > 599)
       {
           JOptionPane.showMessageDialog(null, "Please enter a valid Game Code", "Invalid Game Code", JOptionPane.ERROR_MESSAGE);
       }
       else
       {
           //System.out.println(gameCode);
           // Start the game runner
           GameRunner runner = new GameRunner(gameCode, false);
       }
    }//GEN-LAST:event_joinGameButton1ActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createLobbyButton;
    private javax.swing.JTextField gameCodeEntry;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton joinGameButton1;
    private javax.swing.JLabel startupLabel;
    // End of variables declaration//GEN-END:variables
}
