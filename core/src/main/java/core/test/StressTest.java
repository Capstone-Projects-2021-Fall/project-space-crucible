package core.test;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import core.server.Network;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;


public class StressTest implements Listener {
    ArrayList<Client> masterClient = new ArrayList<>();
    Client gameClient;
    static String ip = "localhost";//100.19.127.86
    File file = new File("C:\\Users\\Parth\\Documents\\GitHub\\project-space-crucible\\core\\src\\main\\java\\core\\test\\stressTest2.txt");

    int numOfInstance = 0;

    public StressTest() throws IOException, InterruptedException {
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter write = new FileWriter(file);

        while(true) {

            masterClient.add(new Client(8192, 8192));
            Client mclient = masterClient.get(numOfInstance);
            mclient.start();
            //register the packets
            Network.register(mclient);

            Client finalMclient = mclient;
            mclient.addListener(new ThreadedListener(new Listener() {
                public void connected(Connection connection) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String msg = timestamp + " Client " + connection.getID() + " has connected to the master server\n";
                    try {
                        write.append(msg);
                        write.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Network.Ping ping = new Network.Ping();
                    finalMclient.sendTCP(ping);
                }

                public void received(Connection connection, Object object) {
                    if(object instanceof Network.Ping){
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        try {
                            write.append( timestamp + " Client " + connection.getID() +" Sent a ping to server and send sent a ping back\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                public void disconnected(Connection connection) {
                }
            }));
            //Connect the client to the master server
            try {
                mclient.connect(5000, ip, Network.tcpPort);
            } catch (IOException e) {
                System.out.println("Master Server is not running!");
                mclient = null;
            }
            numOfInstance++;
        }//end while loop
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new StressTest();
    }
}
