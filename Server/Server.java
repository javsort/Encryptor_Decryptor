package Server;

import Constructors.Functions;
import Constructors.Message;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    //private List<ClientHandler> clients;

    private Socket socket = null;
    private DataInputStream in = null;
    private ObjectOutputStream out = null;

    private Functions functions = new Functions();

    private int port;

    public Server(int port){
        try {
            serverSocket = new ServerSocket(port);
            executorService = Executors.newFixedThreadPool(10);     // # of threads for clients ig

        } catch (IOException e){
            e.printStackTrace();
        }
        // Connection Data
        //this.port = port;
    }

    public void establishConnection(){
        System.out.println("Server started");

        try {
            System.out.println("Waiting for a client ...");
            Socket socket = serverSocket.accept();
            System.out.println("Client accepted" + socket.getInetAddress() + ":" + socket.getPort());

            ClientHandler clientHandler = new ClientHandler(socket);
            executorService.execute(clientHandler);

        } catch (IOException e){
            e.printStackTrace();

        }
    }



    // CHECKKKKK
    public void sendMessage() throws Exception {
        System.out.println("Client accepted");

        try {
            out = new ObjectOutputStream(socket.getOutputStream());

            SecretKey instanceKey = functions.generateKey();

            Message toSend = new Message(instanceKey, functions.encryptData("Esto es en branch main", instanceKey));
            out.writeObject(toSend);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
