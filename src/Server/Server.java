package Server;

import Constructors.Functions;
import Constructors.Message;
import Client.Client;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class Server {

    private ServerSocket serverSocket;
    private ExecutorService executorService;

    private List<ClientHandler> clients = new ArrayList<>();
    private HashMap<Integer, PublicKey> publicKeys = new HashMap<>();

    boolean isRunning;

    private Socket socket = null;
    private DataInputStream in = null;
    private ObjectOutputStream out = null;

    private Functions functions = new Functions();

    private int port;

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

    public void establishConnection(){
        while(isRunning){
            try {
                Socket socket = serverSocket.accept();

                if(socket != null){
                    System.out.println("Client accepted" + socket.getInetAddress() + ":" + socket.getPort());

                    ClientHandler clientHandler = new ClientHandler(generateID(), socket,this);

                    // Add it to the clients list
                    clients.add(clientHandler);

                    executorService.execute(clientHandler);

                    // Add the public key to the publicKeys list along with ClientHandlerId
                    publicKeys.put(clientHandler.getId(), clientHandler.getPublicKey());

                    // Update the client list for all clients
                    for(ClientHandler client : clients){
                        client.updateClientList();
                        System.out.println("Client " + client.getId() + " has been updated");

                    }
                }

            } catch (Exception e){
                e.printStackTrace();

            }
        }
    }

    public int generateID(){
        return clients.size() + 1;
    }

    public void removeClient(ClientHandler disconnected){
        clients.remove(disconnected);
    }

    public void stopServer(){
        isRunning = false;
        executorService.shutdown();

    }

    public HashMap<Integer, PublicKey> getPublicKeys(){
        return this.publicKeys;
    }

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
