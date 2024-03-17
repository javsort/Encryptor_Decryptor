package Client;

import Constructors.*;

import java.net.Socket;
import java.util.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.io.*;

public class Client {
    // Client personal data - to be usable in the future
    private String username = "Client";
    private int id;

    // Socket and I/O streams
    private Socket socket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private String address;
    private int port;

    // Functionalities for Client
    private Functions functions = new Functions();

    // Message queue - destinatary id + message
    private HashMap<Integer, String> messageQueue;
    private boolean isSendingAllowed;

    // Keys and their settings
    private KeyPair KeyPair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    // List of other clients public keys
    private HashMap<Integer, PublicKey> localUserList;

    // ArrayList to save last three messages
    private ArrayList<String> messageHistory = new ArrayList<>();

    // Observer for JFrame
    private ClientObserver observer;

    public boolean serverIsConnected = false;

    // Constructor
    public Client(String addr, int port) {
        // Set the address and port
        this.address = addr;
        this.port = port;

        // Initialize the local public keys
        localUserList = new HashMap<>();

        // Generate the key pair
        try {
            this.KeyPair = functions.generateKey();

            this.privateKey = KeyPair.getPrivate();
            this.publicKey = KeyPair.getPublic();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Initialize message queue
        this.messageQueue = new HashMap<>();

        // Start the thread for handling timings and queued messages
        new Thread(this::handleTimingsAndQueuedMessages).start();
        
    }

    // Register observer for frame
    public void registerObserver(ClientObserver observer){
        this.observer = observer;

    }

    // Connect to the server
    public void connectToServer() throws Exception {
        try {
            // Connect to the server
            try {
                System.out.println("Connecting to server...");
                socket = new Socket(address, port);

                /*in = new ObjectInputStream(socket.getInputStream());
                Object serverResponse = in.readObject();
                if(serverResponse.equals("Server is full")){
                    System.out.println("Server is full");
                    return;
                }*/

            } catch (Exception e) {
                System.out.println("Connection failed: " + e);
            }

            // Perform the simulated TCP three-way handshake
            performHandshakeClient();

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected");
            setServerStat(true);

            // get ID from server/client handler
            this.id = (int) in.readObject();
            System.out.println("Client ID has been set!: " + id);

            // Send this user's public key to the server
            PublicKeyMessage keyToSend = new PublicKeyMessage(publicKey);
            System.out.println("Sending public key to server from Client: " + id);
            out.writeObject(keyToSend);

        } catch (Exception exc) {
            System.out.println("Connection failed bruv: " + exc);
        }
    }

    // Perform the simulated TCP three-way handshake
    private void performHandshakeClient() {
        try {

            System.out.println("Performing 3-way handshake Client");
            // Send SYN request to server
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            
            // Step 1: Send SYN
            oos.writeObject("SYN");
            System.out.println("Sent SYN");
            System.out.println("Step 1 done!");

            // Step 2: Receive SYN-ACK
            String receivedSYNACK = (String) ois.readObject();
            System.out.println("Received SYN-ACK: " + receivedSYNACK);
            System.out.println("Step 2 done!");

            // Step 3: Send ACK
            oos.writeObject("ACK");
            System.out.println("Sent ACK");

            System.out.println("3-way handshake completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Receive message from server
    public void receiveMessages() {
        try {
            Object receivedObject = in.readObject();

            // Update the local public keys
            if(receivedObject instanceof HashMap){
                System.out.println("Client " + id + " Updating keys");
                updateKeys((HashMap<Integer, PublicKey>) receivedObject);

                // Create message by reading mssg
            } else if(receivedObject instanceof Message){
                Message message = (Message) receivedObject;
                System.out.println("Message has been received from: " + message.getSenderID() + "-" + message.getSenderName() + " forwarding mssg to server & users");

                if(message.getMessage() != null) {
                    String decryptedMessage =  message.getSenderName() + ": " + functions.decryptData(message, privateKey);
                    //String decryptedMessage = functions.decryptData(message, privateKey);
                    observer.updateMessage(decryptedMessage, message.getSenderName(), message.getTime());

                    // Add the message to the message history
                    if (messageHistory.size() == 3) {
                        messageHistory.remove(0); // Remove the oldest message if history size is 3
                    }
                    messageHistory.add(decryptedMessage); // Add the new message
                }

            } else {
                System.out.println("Unclassified object received: " + receivedObject.getClass());
            }

        } catch (Exception e) {
            setServerStat(false);
            System.out.println("Server has disconnected, throwing exception: " + e  + "\n" + "Server is connected: " + serverIsConnected);
            e.printStackTrace();
        }
    }

    // Update the local public keys
    public void updateKeys(HashMap<Integer, PublicKey> receivedKeys){
        HashMap<Integer, PublicKey> updatedKeys = receivedKeys;

        System.out.println("Client " + id + " has received keys from server." + "Size of keys: " + updatedKeys.size() + " and clients: " + updatedKeys.keySet() + " from server.");

        this.localUserList.clear();
        this.localUserList.putAll(updatedKeys);

        ArrayList<Integer> clients = new ArrayList<>(localUserList.keySet());

        System.out.println("Client " + id + " has updated keys and sending to observer.");
        System.out.println("Size of keys: " + localUserList.size() + " and clients: " + clients.size() + " to observer.");
        System.out.println("Keys: \n" + localUserList);

        observer.updateClients(clients);

    }

    // Start receiving messages
    public void startReceiving(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                while (serverIsConnected){
                    receiveMessages();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }).start();
    }

    // Send message to server
    public void sendMessage(String message, Integer destClient) {
        // Verify if sending is allowed (Monday to Friday 9AM to 5PM)
        if (isSendingAllowed) {
            // Send the message
            sendMessageImmediately(destClient, message);

        } else {
            messageQueue.put(destClient, message);
        }
    }

    // Get username
    public String getUsername() {
        return username;
    }

    // Handling queue
    private void handleTimingsAndQueuedMessages() {
        while (true) {
            //Monday to Friday 9AM to 5PM
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            //isSendingAllowed = ((day >= Calendar.MONDAY && day <= Calendar.FRIDAY) && (hour >= 9 && hour < 17));
            // CHANGE THIS WHEN NOT TESTING
            isSendingAllowed = true;

            //process the queue if sending is allowed
            if (isSendingAllowed) {
                while (!messageQueue.isEmpty()) {
                    // Get the first message
                    HashMap.Entry<Integer, String> firstEntry = messageQueue.entrySet().iterator().next();

                    Integer dest = firstEntry.getKey();
                    String message = firstEntry.getValue();

                    // Remove the message from the queue
                    messageQueue.remove(dest);

                    // Send the message
                    sendMessageImmediately(dest, message);
                }
            }

            //sleep for 10 sec and check
            try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    // Send message to server
    private void sendMessageImmediately(Integer destClient, String message) {
        try {
            // Change to RETRIEVE KEY FROM SERVER
            PublicKey ExtPublicKey = localUserList.get(destClient);

            // Change for server
            out.writeObject(new Message(this.id, this.username, functions.encryptData(message, ExtPublicKey), destClient));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send message");
        }
    }

    public void setServerStat(boolean isConnected){
        serverIsConnected = isConnected;
    }

    // Set username
    public void setUsername(String username) {
        this.username = username;
}
}