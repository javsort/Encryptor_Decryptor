package Server;

import Constructors.Functions;
import Constructors.Message;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.crypto.SecretKey;


public class ClientHandler implements Runnable {
    // use ObjectInputStream instead of DataInputStream for object communication
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private Functions functions = new Functions();
    private Server server;


    public ClientHandler(Socket socket, Server server) throws IOException {
        System.out.println("ClientHandler created with addr: " + socket.getInetAddress() + " & port: " + socket.getPort());

        this.server = server;
        this.clientSocket = socket;
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    @Override
    public void run() {
        try{
            while(true){
                // Create message by reading mssg
                Message message = (Message) in.readObject();

                // Decript and display mssg
                String decryptedMessage = functions.decryptData(message);
                System.out.println("Received message: " + decryptedMessage);

                // After receiving it in server, send it back out
                server.forwardMessage(message, this);

                // DIZ SUM OTHER SHIT
                // Prepare response
                SecretKey instanceKey = functions.generateKey();
                Message toSend = new Message(instanceKey, functions.encryptData("Reply: " + decryptedMessage, instanceKey));

                // Send response back to client
                //out.writeObject(toSend);
            }

        } catch (Exception e) {
            // Handle client disconnection
            System.out.println("Client disconnected!: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            try {
                System.out.println("Attempting to reconnect...");
                clientSocket = new Socket(clientSocket.getInetAddress(), clientSocket.getPort());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                System.out.println("Reconnection Successful!");

            } catch (IOException ex){
                System.out.println("Reconnect failed, closing client socket... mssg: " + ex.getMessage());
            }

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

    public void sendMessage(Message message) throws Exception {
        out.writeObject(message);
    }
}
