package Client;

import Constructors.Functions;
import Constructors.Message;

import java.net.Socket;

import java.util.Random;

import javax.crypto.SecretKey;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Client {
    // Network data
    private Socket socket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    private String address;
    private int port;

    private Functions functions = new Functions();

    Random key;


    public Client(String addr, int port){
        // Connection Data
        this.address = addr;
        this.port = port;

    }

    public void connectToServer() throws Exception {
        try {
            socket = new Socket(address, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected");

        } catch (Exception exc) {
            System.out.println("Connection failed bruv: " + exc);
        }
    }

    public String receiveMessage(){
        try {
            Message message = (Message) in.readObject();

            if(message != null){
                return functions.decryptData(message);

            } else {
                throw new Exception("Message is null");

            }

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void sendMessage(String message){
        try {
            SecretKey key = functions.generateKey();

            out.writeObject(new Message(key, functions.encryptData(message, key)));

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed to send message");
        }
    }
}
