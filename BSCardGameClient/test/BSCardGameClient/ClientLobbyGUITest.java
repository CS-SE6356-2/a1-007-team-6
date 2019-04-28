/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BSCardGameClient;

import bscardgameclient.ClientLobbyGUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alinta Wang
 */
public class ClientLobbyGUITest {
    
    ClientLobbyGUI l = new ClientLobbyGUI("code");
    
   @Test
   public void correctGameCode() {
       assertEquals("code", l.gameCode);
   }
}
