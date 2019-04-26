package bscardgameserver;

import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryonet.*;
import java.io.*;
import java.util.*;

public class Game 
{
    public static int portcounter = 500;
    public static ArrayList<Lobby> lobbies = new ArrayList<>();
    public static void main(String[] args) throws IOException 
    {
        
        Server server = new Server();
        Kryo kryo = server.getKryo(); 
	kryo.register(BSServerCommunication.class);
        kryo.register(java.util.ArrayList.class);
        server.start();
        server.bind(54777, 54777); //54500-54599 are reserved for lobbies
        
        server.addListener(new Listener() {
	    @Override
            public void connected (Connection connection) 
            {
                BSServerCommunication lobbyCreated = new BSServerCommunication();
                lobbyCreated.lobby=portcounter;
                lobbies.add(new Lobby(lobbyCreated));
                connection.sendTCP(lobbyCreated);
		System.out.println(portcounter);
                portcounter++;
            }
	    @Override
	    public void received (Connection connection, Object object) {
              if (object instanceof BSServerCommunication) {
                 BSServerCommunication request = (BSServerCommunication)object;
                 System.out.println("Lobby created");

                 connection.sendTCP(request);
              }
	    }
        });
    }
}
