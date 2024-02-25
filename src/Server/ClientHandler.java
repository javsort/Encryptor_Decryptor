package Server;

import Constructors.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;


public class ClientHandler implements Runnable {
    // use ObjectInputStream instead of DataInputStream for object communication
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private Functions functions = new Functions();
    private Server server;
    private int ClientHandlerID;

    private PublicKey publicKey;


    public ClientHandler(int Id, Socket socket, Server server) throws IOException, ClassNotFoundException {
        this.ClientHandlerID = Id;

        this.server = server;
        this.clientSocket = socket;
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());

        // Send the client its ID
        out.writeObject(ClientHandlerID);

        this.publicKey = ((PublicKeyMessage) in.readObject()).getPublicKey();

        System.out.println("ClientHandler " + ClientHandlerID + ", created with addr: " + socket.getInetAddress() + " & port: " + socket.getPort());
    }

    @Override
    public void run() {
        try{
            while(server.isRunning){
                // Create message by reading mssg
                Message message = (Message) in.readObject();

                if(message != null && message.getMessage()!= null ){
                    System.out.println("Message has been received from: " + message.getSenderID() + ". " + message.getUsername() + " forwarding mssg to server & users");
                }

                // After receiving it in server, send it back out
                server.forwardMessage(message, this);
            }

        } catch (Exception e) {
            // Handle client disconnection
            System.out.println("Client disconnected!: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            server.removeClient(this);

        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PublicKey getPublicKey(){
        return this.publicKey;
    }

    public int getId(){
        return this.ClientHandlerID;
    }

    public void updateClientList(){
        try {
            System.out.println("Updating client list for client: " + this.ClientHandlerID);
            out.writeObject(server.getPublicKeys());

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws Exception {
        // Send to client all public keys
        //out.writeObject(server.getPublicKeys());

        // Send message to client
        out.writeObject(message);
    }
}
