package BSCardGameServer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import bscardgameserver.Game;
import bscardgameserver.BSServerCommunication;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
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
public class GameTest {
    
    public GameTest() {
    }
    
    @Test
	public void portCounter()
	{
            try
            {
                Client client = new Client();
                Kryo kryo = client.getKryo();
                kryo.register(BSServerCommunication.class);
                kryo.register(java.util.ArrayList.class);
                new Thread(client).start();
                
                Game g = new Game();
                for(int i = 0; i < 100; i++){
                    client.connect(5000, "127.0.0.1", 54777, 54777);
                    
                assertEquals(600, g.portcounter);
                
                if (object instanceof BSServerCommunication) 
		    {
			comm = (BSServerCommunication)object;
			gameCode = comm.lobby.toString();
			lobbyPort = BASE_PORT + Integer.parseInt(gameCode);
			System.out.println("Connected");
		    }
		    connection.close();
                }
            }
            catch(Exception e)
            {
                fail("here");
            }
            
        }
}
