package Server;

import Constructors.Message;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server class - to handle the server and the clientHandlers
public class Server {
    private ServerSocket serverSocket;

    // ExecutorService for the threads - clients
    private ExecutorService executorService;

    // List of clients
    private List<ClientHandler> clients = new ArrayList<>();

    // List of public keys
    private HashMap<Integer, PublicKey> publicKeys = new HashMap<>();

    // Server settings
    boolean isRunning;
    private int port;

    // flags for updating client list
    private boolean newClient = false;
    private boolean clientDisconnected = false;

    // Constructor
    public Server(int port){
        this.port = port;

        try {
            serverSocket = new ServerSocket(this.port);
            executorService = Executors.newFixedThreadPool(10);     // # of threads for clients ig
            isRunning = true;

            System.out.println("Server started");
        } catch (IOException e){
            e.printStackTrace();

        }
    }

    // Turn on server and establish connection to receive new clients and update the client list
    public void establishConnection(){
        while(isRunning){
            try {

                // Accept the client
                Socket socket = serverSocket.accept();

                if(socket != null){
                    System.out.println("Client accepted" + socket.getInetAddress() + ":" + socket.getPort());

                    // Create a new ClientHandler
                    ClientHandler clientHandler = new ClientHandler(generateID(), socket,this);

                    // Add it to the clients list
                    clients.add(clientHandler);

                    // Execute the clientHandler
                    executorService.execute(clientHandler);

                    // Add the public key to the publicKeys list along with ClientHandlerId
                    publicKeys.put(clientHandler.getId(), clientHandler.getPublicKey());

                    // Flag to update clientlist
                    newClient = true;
                }

                // Update the client list every time a new client is added or disconnected
                if(!clients.isEmpty() && newClient || !clients.isEmpty() && clientDisconnected){
                    newClient = false;
                    clientDisconnected = false;

                    // Update the client list
                    for(ClientHandler clientHndlr : clients){
                        clientHndlr.updateClientList();
                        System.out.println("ClientHandler " + clientHndlr.getId() + ", has been updated");

                    }
                }

                // Sleep for a bit
                Thread.sleep(500);
            } catch (Exception e){
                e.printStackTrace();

            }
        }
    }

    // Generate ID for the new Client & ClientHandler
    public int generateID(){
        return clients.size() + 1;
    }

    // Remove the client from the list when disconnected
    public void removeClient(ClientHandler disconnected){
        clients.remove(disconnected);
        clientDisconnected = true;
    }

    // Stop the server
    public void stopServer(){
        isRunning = false;
        executorService.shutdown();

    }

    // Get the public keys
    public HashMap<Integer, PublicKey> getPublicKeys(){
        return this.publicKeys;
    }

    // Forward the message to the client
    public void forwardMessage(Message message, ClientHandler sender){
        int destination = message.getReceiverID();

        if(destination != 0){
            for(ClientHandler client : clients){
                if(client.getId() == destination){
                    try {
                        client.sendMessage(message);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } else {
            broadcastMessage(message, sender);
        }
    }

    // Broadcast the message to all clients
    public void broadcastMessage(Message message, ClientHandler sender){
        for(ClientHandler client : clients){
            if(client.getId() != sender.getId()){
                try {
                    client.sendMessage(message);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
