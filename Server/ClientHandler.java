package Server;

import Constructors.Functions;
import Constructors.Message;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.crypto.SecretKey;


public class ClientHandler implements Runnable {
    // use ObjectInputStream instead of DataInputStream for object communication
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private Functions functions = new Functions();


    public ClientHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = (Message) in.readObject();
                String decryptedMessage = functions.decryptData(message);
                System.out.println("Received message: " + decryptedMessage);

                SecretKey instanceKey = functions.generateKey();
                Message toSend = new Message(instanceKey, functions.encryptData("Reply: " + decryptedMessage, instanceKey));
                out.writeObject(toSend);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
