package Client;

import Constructors.*;

import java.net.Socket;

import java.util.Calendar;
import java.util.Queue;
import java.util.LinkedList;

import javax.crypto.SecretKey;
import java.io.*;

public class Client {
    private Socket socket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    private String address;
    private int port;
    private Functions functions = new Functions();
    private Queue<String> messageQueue;
    private boolean isSendingAllowed;

    private ClientObserver observer;

    public Client(String addr, int port) {
        this.address = addr;
        this.port = port;

        //initialize message queue
        this.messageQueue = new LinkedList<>();

        // this.KeyPair = generateKeyPair();

        new Thread(this::handleTimingsAndQueuedMessages).start();
    }

    public void registerObserver(ClientObserver observer){
        this.observer = observer;
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

    public String receiveMessage() {
        try {
            Message message = (Message) in.readObject();

            if (message != null) {
                return functions.decryptData(message);

            } else {
                throw new Exception("Message is null");

            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startReceiving(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                while (true){
                    String message = receiveMessage();

                    if(message != null){
                        if(observer != null) {
                            observer.updateMessage(message);
                        }
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        if (isSendingAllowed) {
            sendMessageImmediately(message);
        } else {
            messageQueue.offer(message);
        }
    }

    private void handleTimingsAndQueuedMessages() {
        while (true) {
            //Monday to Friday 9AM to 5PM
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            //isSendingAllowed = ((day >= Calendar.MONDAY && day <= Calendar.FRIDAY) && (hour >= 9 && hour < 17));
            isSendingAllowed = true;

            //process the queue if sending is allowed
            if (isSendingAllowed) {
                while (!messageQueue.isEmpty()) {
                    sendMessageImmediately(messageQueue.remove());
                }
            }

            //sleep for 10 sec and check
            try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void sendMessageImmediately(String message) {
        try {
            SecretKey key = functions.generateKey();

            // Change for server
            out.writeObject(new Message(key, functions.encryptData(message, key)));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send message");
        }
    }
}