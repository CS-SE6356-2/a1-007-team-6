/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BSCardGameClient;

import bscardgameclient.ClientStartupGUI;
import static bscardgameclient.ClientStartupGUI.isNumeric;
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
public class ClientStartupGUITest {
    
    @Test
    public void isNumTest(){
        String t1 = "584";
        String t2 = "code234";
        assertEquals(true, isNumeric(t1));
        assertEquals(false, isNumeric(t2));
    }
}
