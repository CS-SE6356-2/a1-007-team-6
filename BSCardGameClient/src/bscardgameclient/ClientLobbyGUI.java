/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bscardgameclient;

import com.esotericsoftware.kryonet.*;
import com.esotericsoftware.kryo.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Shravan Jambukesan
 */
public class ClientLobbyGUI extends javax.swing.JFrame {

    /**
     * Creates new form ClientLobbyGUI
     */
    public String gameCode = "";
    String currentAction = "";
    String previousAction = "";
    final String SERVER_IP = "127.0.0.1";
    Client client;
    int port;
    public static volatile int playernum;
    public static volatile BSServerCommunication comms;
    Listener LobbyListener;
    
    public ClientLobbyGUI(String gameCode) 
    {
	playernum = 0;
        this.gameCode = gameCode;
        initComponents();
        startGameNowButton.setVisible(false);
        gameCodeLabel.setText("Game Code: " + gameCode);
    }
    
    public void setLobbyPort(int port)
    {
        this.port = port;
    }
    
    public void connectToServer()
    {
        initializeCommClient();
        try
        {
	    //System.out.println(port);
            client.connect(5000, SERVER_IP, port, port);
            client.addListener(LobbyListener = new Listener() 
            {
                @Override
                public void received (Connection connection, Object object) 
                {
		    synchronized(client)    
		    { 
		    if (object instanceof BSServerCommunication) 
		    {
			comms = (BSServerCommunication)object;
			//System.out.println("Player has connected to: " + comms.lobby);
			if(playernum == 0)
			{
			    System.out.println("first" + comms.numPlayers);
			    comms.numPlayers = comms.numPlayers + 1;
			    playernum = comms.numPlayers;
			    System.out.println(2 + comms.numPlayers);
			}
			if(comms.started)
			{			    
			    client.notify();
			    //System.out.println("launch request recieved");
			    launchGameGUI();
			}
		    }
		    }
                }
            });
            
        } /*catch (IOException ex) {
	    Logger.getLogger(ClientLobbyGUI.class.getName()).log(Level.SEVERE, null, ex);
	}*/
        catch(Exception e)
        {
            System.out.println("Exception in connectToServer(): " + e);
        }
    }
    
    public void initializeCommClient()
    {
        try
        {
            client = new Client();
            Kryo kryo = client.getKryo();
            kryo.register(BSServerCommunication.class);
            kryo.register(java.util.ArrayList.class);
            client.start();
        }
        catch(Exception e)
        {
            System.out.println("Unable to initialize communication client");
        }
    }
    
    public void enableLobbyCreatorInterface()
    {
        startGameNowButton.setVisible(true);
        waitingLabel.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lobbyLabel = new javax.swing.JLabel();
        gameCodeLabel = new javax.swing.JLabel();
        startGameNowButton = new javax.swing.JButton();
        waitingLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lobbyLabel.setText("Game Lobby");

        gameCodeLabel.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        gameCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        startGameNowButton.setText("Start Game Now");
        startGameNowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startGameNowButtonActionPerformed(evt);
            }
        });

        waitingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        waitingLabel.setText("Waiting on host to start game...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lobbyLabel)
                        .addGap(158, 158, 158))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(gameCodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startGameNowButton, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(waitingLabel))
                        .addGap(95, 95, 95))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lobbyLabel)
                .addGap(41, 41, 41)
                .addComponent(gameCodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(waitingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(startGameNowButton)
                .addGap(44, 44, 44))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startGameNowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startGameNowButtonActionPerformed
	synchronized(client)
	{
            if(comms.numPlayers >= 3)
            {
                comms.started = true;
                client.sendTCP(comms);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "You need at least 3 players to start the game", "Not Enough Players", JOptionPane.ERROR_MESSAGE);
            }

	}
    }//GEN-LAST:event_startGameNowButtonActionPerformed
    public void launchGameGUI()
    {
	this.client.removeListener(LobbyListener);
	client.stop();
	//System.out.println("We're in the inGame now");
	ClientInGameGUI inGame = new ClientInGameGUI(this, true);
        inGame.setGameCode(gameCode);
        inGame.setLobbyPort(port);
	inGame.setNet(comms, playernum);
        this.setVisible(false);
        inGame.setVisible(true);
        inGame.toFront();
        inGame.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel gameCodeLabel;
    private javax.swing.JLabel lobbyLabel;
    private javax.swing.JButton startGameNowButton;
    private javax.swing.JLabel waitingLabel;
    // End of variables declaration//GEN-END:variables
}
