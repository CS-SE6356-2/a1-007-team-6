/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bscardgameclient;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.JToggleButton;

/**
 *
 * @author Alinta Wang
 */
public class ClientInGameGUI extends javax.swing.JDialog {

    /**
     * Creates new form ex2
     */
    String gameCode = "";
    final String SERVER_IP = "97.99.238.31";
    int lobbyPort;
    int playerNum;
    int pageNumber = 0;
    String previousActionLog = "";
    String currentActionLog = "";
    Client client;
    Listener GameListener;
    BSServerCommunication comms;
    int index = 0;
    boolean donePlaying;
    ArrayList<JToggleButton> buttons = new ArrayList<>();
    ArrayList<ArrayList<Integer>> hands = new ArrayList<>();
    ArrayList<Integer> currentHand;
    
    
    public ClientInGameGUI(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        setResizable(false);
        donePlaying = false;
    }
    public void setNet(BSServerCommunication comm, int playerNum)
    {
	//this.client.removeListener(LobbyListener);
	setcomm(comm);
	this.playerNum = playerNum; //starts at 1
	initializeCommClient();
	//System.out.println(comms.lobby);
	setupCards();
    }
        
    public void initializeCommClient()
    {
	try {
	    client = new Client();
	    Kryo kryo = client.getKryo();
	    kryo.register(BSServerCommunication.class);
	    kryo.register(java.util.ArrayList.class);
	    client.start();

	    client.addListener(new Listener() 
	    {
		@Override
		public void received (Connection connection, Object object) 
		{
		    synchronized(client)
		    {
			if (object instanceof BSServerCommunication)
			{
			    setcomm((BSServerCommunication)object);
			    //System.out.println("gotcha");
			    //add button update sequence here for anytime server pushes something new(next turn or action performed)
			    currentActionLogLabel.setText(comms.currentActionLog);
			    previousActionLogLabel.setText(comms.previousActionLog);
			    //System.out.println("Recieved: " + comms.currentActionLog);
			    
			    cardToPlayLabel.setText("Card to play is: " + toCard(comms.CurrentCard));
			    currentHand = comms.PlayerHands.get(playerNum - 1);
			    int size = currentHand.size();
                            pageNumber = 0;
			    setCardIcons((currentHand.subList(pageNumber * 8, (pageNumber+1)*8 > size ? size : (pageNumber+1)*8)));
                            if(comms.currentTurn == playerNum && !donePlaying)
                            {
                                playCardButton.setEnabled(true);
                            }
                            else
                            {
                                playCardButton.setEnabled(false);
                            }
                            
                            if(comms.previousTurn == playerNum || comms.emptyPile || donePlaying)
                            {
                                callBSButton.setEnabled(false);
                            }
                            else
                            {
                                callBSButton.setEnabled(true);
                            }
                            
                            if(currentHand.isEmpty() && comms.currentTurn == playerNum && !donePlaying)
                            {
                                donePlaying = true;
                                comms.actor = playerNum - 1;
                                comms.action = 2;
                                callBSButton.setEnabled(false);
                                playCardButton.setEnabled(false);
                                updateComms();
                                //JOptionPane.showMessageDialog(null, "You came in place: " + comms.numWinners, "Game Ended", JOptionPane.INFORMATION_MESSAGE);
                            }
                            
                            if(comms.isGameOver)
                            {
                                donePlaying = true;
                                callBSButton.setEnabled(false);
                                playCardButton.setEnabled(false);
                                //JOptionPane.showMessageDialog(null, "You came in place: " + comms.numWinners, "Game Ended", JOptionPane.INFORMATION_MESSAGE);
                            }
                            
			}
		    }
		}
	    });
	    client.connect(5000, SERVER_IP, lobbyPort, lobbyPort);
	} catch (IOException ex) {
	    Logger.getLogger(ClientInGameGUI.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    
    //met
    public void setLobbyPort(int lobbyPort)
    {
        this.lobbyPort = lobbyPort;
    }
        
    public void setupCards()
    {
        hands = comms.PlayerHands;
        currentHand = hands.get(playerNum - 1);
        
        connectionLabel.setText("Connected as: Player " + playerNum);
        cardToPlayLabel.setText("Card to play is: " + toCard(comms.CurrentCard));

        /*for(Integer i : currentHand)
        {
            System.out.println("Card number: " + i);
        }*/
        buttons.add(card1Button);
        buttons.add(card2Button);
        buttons.add(card3Button);
        buttons.add(card4Button);
        buttons.add(card5Button);
        buttons.add(card6Button);
        buttons.add(card7Button);
        buttons.add(card8Button);
        setCardIcons(currentHand.subList(0, 8));
	//updateComms();
    }
    
    public String toCard(int c)
    {
	switch(c)
	{
	    case 0:
		return "Ace";
	    case 1:
		return "Two";
	    case 2:
		return "Three";
	    case 3:
		return "Four";
	    case 4:
		return "Five";
	    case 5:
		return "Six";
	    case 6:
		return "Seven";
	    case 7:
		return "Eight";
	    case 8:
		return "Nine";
	    case 9:
		return "Ten";
	    case 10:
		return "Jack";
	    case 11:
		return "Queen";
	    case 12:
		return "King";
	    default:
		return "Invalid Card";
	}
    }
    
    public void setGameCode(String gameCode)
    {
        this.gameCode = gameCode;
    }
    
    public void setCardIcons(List<Integer> cards)
    {
        try 
        {
            Iterator<Integer> iter = cards.iterator();
            for(JToggleButton button : buttons)
            {

                if(iter.hasNext())
                {
                    int cardNum = iter.next();
                    String fileName = "Resources/" + cardNum + ".png";
                    InputStream stream = getClass().getResourceAsStream(fileName);
                    ImageIcon cardImage = new ImageIcon(ImageIO.read(stream));
                    button.setIcon(cardImage);
                    button.setToolTipText(Integer.toString(cardNum));
                }
                else
                {
                    button.setEnabled(false);
                }


            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(ClientInGameGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playCardButton = new javax.swing.JButton();
        callBSButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        connectionLabel = new javax.swing.JLabel();
        card2Button = new javax.swing.JToggleButton();
        card1Button = new javax.swing.JToggleButton();
        card3Button = new javax.swing.JToggleButton();
        card5Button = new javax.swing.JToggleButton();
        card6Button = new javax.swing.JToggleButton();
        card7Button = new javax.swing.JToggleButton();
        card8Button = new javax.swing.JToggleButton();
        card4Button = new javax.swing.JToggleButton();
        previousActionLogLabel = new javax.swing.JLabel();
        currentActionLogLabel = new javax.swing.JLabel();
        cardToPlayLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        playCardButton.setText("Play");
        playCardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playCardButtonActionPerformed(evt);
            }
        });

        callBSButton.setText("Call BS");
        callBSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                callBSButtonActionPerformed(evt);
            }
        });

        previousButton.setText("Prev");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        connectionLabel.setText("Connected as...");

        previousActionLogLabel.setText("Status: ");

        currentActionLogLabel.setText("Status:");

        cardToPlayLabel.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(connectionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(callBSButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(playCardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(previousButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 165, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(card1Button, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card5Button, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(card2Button, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card6Button, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(card7Button, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card3Button, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(card4Button, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(182, 182, 182))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(card8Button, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                                .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(currentActionLogLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(previousActionLogLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(530, 530, 530)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cardToPlayLabel)
                .addGap(393, 393, 393))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(cardToPlayLabel)
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card1Button, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card2Button, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card3Button, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card4Button, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card6Button, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(previousButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(card5Button, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card8Button, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card7Button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(previousActionLogLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(currentActionLogLabel)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(connectionLabel)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(callBSButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(playCardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        // TODO add your handling code here:
	int size = currentHand.size();
	if (++pageNumber >= size/8.0)
	    pageNumber = 0;
        setCardIcons((currentHand.subList(pageNumber * 8, (pageNumber+1)*8 > size ? size : (pageNumber+1)*8)));
	//System.out.println("after cardup date" + pageNumber);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        // TODO add your handling code here:
	int size = currentHand.size();
	if (--pageNumber < 0)   //change to accomodate going below zero
	{
	    if (size/8.0 == size/8)//cycles pagenumber to the last page
		pageNumber = size/8 - 1;
	    else
		pageNumber = size/8;
	}
        setCardIcons((currentHand.subList(pageNumber * 8, (pageNumber+1)*8 > size ? size : (pageNumber+1)*8)));
	//System.out.println("after cardup date" + pageNumber);
    }//GEN-LAST:event_previousButtonActionPerformed

    private void playCardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playCardButtonActionPerformed
        int counter = 0;
        for(JToggleButton button : buttons)
        {
            if(button.isSelected())
            {
                counter++;
                System.out.println("Selected: " + Integer.valueOf(button.getToolTipText()));
            }
        }
        if(counter > 4)
        {
            JOptionPane.showMessageDialog(null, "You can only play a max of 4 cards at a time", "Invalid Number of Cards", JOptionPane.ERROR_MESSAGE);
            for(JToggleButton button : buttons)
            {
                button.setSelected(false);
            }
        }
        else if(counter == 0)
        {
            JOptionPane.showMessageDialog(null, "You must play at least one card", "Invalid Number of Cards", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            for(JToggleButton button : buttons)
            {
                if(button.isSelected())
                {
                    //System.out.println(button.getToolTipText());
                    comms.cardsPlayed.add(Integer.valueOf(button.getToolTipText()));
                    System.out.println("Selected: " + Integer.valueOf(button.getToolTipText()));
                }
            }
            comms.action = 0;
            comms.actor = playerNum - 1;
            updateComms();
        }
    }//GEN-LAST:event_playCardButtonActionPerformed

    private void callBSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_callBSButtonActionPerformed
        // TODO add your handling code here:
        comms.action = 1;
        comms.actor = playerNum - 1;
	updateComms();
        //client.sendTCP(comms);
    }//GEN-LAST:event_callBSButtonActionPerformed

    public void updateComms()
    {
	client.sendTCP(comms);
    }
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
            java.util.logging.Logger.getLogger(ClientInGameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientInGameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientInGameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientInGameGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ClientInGameGUI dialog = new ClientInGameGUI(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    public void setcomm(BSServerCommunication com)
    {
	comms = com;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton callBSButton;
    private javax.swing.JToggleButton card1Button;
    private javax.swing.JToggleButton card2Button;
    private javax.swing.JToggleButton card3Button;
    private javax.swing.JToggleButton card4Button;
    private javax.swing.JToggleButton card5Button;
    private javax.swing.JToggleButton card6Button;
    private javax.swing.JToggleButton card7Button;
    private javax.swing.JToggleButton card8Button;
    private javax.swing.JLabel cardToPlayLabel;
    private javax.swing.JLabel connectionLabel;
    private javax.swing.JLabel currentActionLogLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton playCardButton;
    private javax.swing.JLabel previousActionLogLabel;
    private javax.swing.JButton previousButton;
    // End of variables declaration//GEN-END:variables
}
