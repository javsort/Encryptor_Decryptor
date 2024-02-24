package Server;

import Constructors.Functions;
import Constructors.Message;
import Client.Client;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class Server {

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<ClientHandler> clients = new ArrayList<>();
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

                    ClientHandler clientHandler = new ClientHandler(socket, this);

                    clients.add(clientHandler);
                    executorService.execute(clientHandler);
                }

            } catch (IOException e){
                e.printStackTrace();

            }
        }


    }

    public void removeClient(ClientHandler disconnected){
        clients.remove(disconnected);
    }

    public void stopServer(){
        isRunning = false;
        executorService.shutdown();

    }

    public void forwardMessage(Message message, ClientHandler sender){
        for(ClientHandler client : clients){
            if(client != sender){
                try {
                    client.sendMessage(message);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
