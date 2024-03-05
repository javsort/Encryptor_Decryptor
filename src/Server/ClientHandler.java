package Server;

import Constructors.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.HashMap;


// ClientHandler class - to handle each of the clients as a new Thread
public class ClientHandler implements Runnable {
    // use ObjectInputStream instead of DataInputStream for object communication
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Socket for the client
    private Socket clientSocket;

    // Server reference
    private Server server;

    // Client ID
    private int ClientHandlerID;

    // Public Key of the assigned Client
    private PublicKey publicKey;


    // Constructor, takes the client's ID, socket and the server
    public ClientHandler(int Id, Socket socket, Server server) throws IOException, ClassNotFoundException {
        this.ClientHandlerID = Id;

        this.server = server;
        this.clientSocket = socket;
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());

        // Send the client its assigned ID
        out.writeObject(ClientHandlerID);

        // Get the public key from the client
        this.publicKey = ((PublicKeyMessage) in.readObject()).getPublicKey();

        System.out.println("ClientHandler " + ClientHandlerID + ", created with addr: " + socket.getInetAddress() + " & port: " + socket.getPort());
    }


    // Run method for the thread
    @Override
    public void run() {
        try{
            while(server.isRunning){
                // Create message by reading mssg from Client
                Message message = (Message) in.readObject();

                if(message != null && message.getMessage()!= null ){
                    System.out.println("Message has been received from: " + message.getSenderID() + ". " + message.getSenderName() + " forwarding mssg to: " + message.getReceiverID() + ".\n");
                }

                // After receiving it in server, send it back out
                server.forwardMessage(message, this);
            }

        } catch (Exception e) {
            // Handle client disconnection
            System.out.println("Client " + getId() + " disconnected!: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            server.removeClient(this);

        } finally {
            // Close the streams and the socket
            try {
                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    // Update the client list at Client's local list
    public void updateClientList(){
        try {
            System.out.println("Updating client list for client: " + this.ClientHandlerID);
            HashMap<Integer, PublicKey> newKeys = server.getPublicKeys();

            System.out.println("Size of keys: " + newKeys.size());
            System.out.println("Keys: \n" + newKeys);

            out.reset();
            out.writeObject(newKeys);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Send message to assigned Client: Server -> ClientHandler -> Client -> ClientFrame
    public void sendMessage(Message message) throws Exception {

        // Send message to client
        out.writeObject(message);
    }

    // Getters
    public PublicKey getPublicKey(){
        return this.publicKey;
    }

    public int getId(){
        return this.ClientHandlerID;
    }
}