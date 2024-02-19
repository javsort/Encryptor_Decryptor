package Server;

import Constructors.Functions;
import Constructors.Message;

import java.io.ObjectOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.crypto.SecretKey;


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private DataInputStream in;
    private Functions functions = new Functions();


    public ClientHandler(Socket socket){
        this.clientSocket = socket;
    }

    @Override
    public void run(){
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            SecretKey instanceKey = functions.generateKey();

            Message toSend = new Message(instanceKey, functions.encryptData("Wut wut bro", instanceKey));
            out.writeObject(toSend);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
