package gamelogic.clientHandling;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import gameengine.*;

public class Server {
    private static int CURRENT_CONNECTIONS = 0;
    public static final int LISTENING_PORT = 9876;
    private List<ConnectionHandler> connections = Collections.synchronizedList(new ArrayList<>());{
    try {
            InetAddress host = InetAddress.getLocalHost();
            final Socket socket = new Socket(host, LISTENING_PORT);
            final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            AtomicBoolean running = new AtomicBoolean(true);
        } 
        catch (Exception e) {
            System.out.println("Haha");
        }
    }

    public Server() {
        ServerSocket listener;  // Listens for incoming connections.
        Socket connection;      // For communication with the connecting program.
        
        /* Accept and process connections forever, or until some error occurs. */

        // pre: none
        // post: the server is running, and is accepting and processing connection requests until some error occurs.  
        // If an error occurs, a message is printed and the server is shut down.
        try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Listening on port " + LISTENING_PORT);
            while (true) {
                  // Accept next connection request and handle it.
                connection = listener.accept();
                System.out.println("Connection received from " + connection.getInetAddress());
                ConnectionHandler handler = new ConnectionHandler(connection, CURRENT_CONNECTIONS);
                connections.add(handler);
                CURRENT_CONNECTIONS++;
                handler.start();
            }
        }
        catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }

    }


    /**
     *  Defines a thread that handles the connection with one
     *  client.
     */
    // pre: none
    // post: the thread is running, and is handling the connection with one client.
    private class ConnectionHandler extends Thread {
        Socket client;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        int number;

        ConnectionHandler(Socket socket, int newNum) {
            client = socket;
            number = newNum;
        }
        
        public void run() {
            String clientAddress = "User " + number;
            try {
                oos = new ObjectOutputStream(client.getOutputStream());
                ois = new ObjectInputStream(client.getInputStream());
                
                while (true) {
                    Information message = (Information) ois.readObject();
                    System.out.println("Message Received from " + clientAddress);
                    
                    // Broadcast the message to all other clients
                    synchronized (connections) {
                        for (ConnectionHandler handler : connections) {
                            if (handler != this) {
                                try {
                                    handler.oos.writeObject(clientAddress + ": " + message);
                                    System.out.println("Hehe");
                                    handler.oos.flush();
                                    
                                } catch (IOException e) {
                                    System.out.println("Error sending to client: " + e);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Error on connection with: " + clientAddress + ": " + e);
            } finally {
                // Remove this handler from the list when connection closes
                synchronized (connections) {
                    connections.remove(this);
                }
                try {
                    client.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }


}
