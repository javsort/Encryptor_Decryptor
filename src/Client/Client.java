package Client;

import Constructors.*;

import java.net.Socket;
import java.util.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.io.*;

public class Client {
    private String username = "Client";
    private int id;
    private Socket socket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private String address;
    private int port;
    private Functions functions = new Functions();
    private HashMap<Integer, String> messageQueue = new HashMap<>();
    private boolean isSendingAllowed = true; // Default to true for simplicity
    private KeyPair KeyPair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private HashMap<Integer, PublicKey> localUserList = new HashMap<>();
    private ArrayList<String> messageHistory = new ArrayList<>();
    private ClientObserver observer;

    public Client(String addr, int port) {
        this.address = addr;
        this.port = port;
        try {
            this.KeyPair = functions.generateKey();
            this.privateKey = KeyPair.getPrivate();
            this.publicKey = KeyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(this::handleTimingsAndQueuedMessages).start();
    }

    public void registerObserver(ClientObserver observer) {
        this.observer = observer;
    }

    public void connectToServer() throws Exception {
        try {
            socket = new Socket(address, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected");
            this.id = (int) in.readObject();
            System.out.println("Client ID has been set!: " + id);
            PublicKeyMessage keyToSend = new PublicKeyMessage(publicKey);
            out.writeObject(keyToSend);
        } catch (Exception exc) {
            System.out.println("Connection failed: " + exc.getMessage());
        }
    }

    public void receiveMessages() {
        try {
            Object receivedObject = in.readObject();
            if (receivedObject instanceof HashMap) {
                updateKeys((HashMap<Integer, PublicKey>) receivedObject);
            } else if (receivedObject instanceof Message) {
                Message message = (Message) receivedObject;
                if (message.getSenderID() == -1) { // Assuming -1 is reserved for system messages
                    observer.updateSystemNotification(new String(message.getMessage()));
                } else {
                    String decryptedMessage = message.getSenderName() + ": " + functions.decryptData(message, privateKey);
                    observer.updateMessage(decryptedMessage);
                    updateMessageHistory(decryptedMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateKeys(HashMap<Integer, PublicKey> receivedKeys) {
        this.localUserList.clear();
        this.localUserList.putAll(receivedKeys);
        observer.updateClients(new ArrayList<>(localUserList.keySet()));
    }

    public void startReceiving() {
        new Thread(() -> {
            while (true) {
                receiveMessages();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(String message, Integer destClient) {
        if (isSendingAllowed) {
            sendMessageImmediately(destClient, message);
        } else {
            messageQueue.put(destClient, message);
        }
    }

    private void handleTimingsAndQueuedMessages() {
        // Simplified to always allow sending for this example
        isSendingAllowed = true;
        if (isSendingAllowed) {
            messageQueue.forEach(this::sendMessageImmediately);
            messageQueue.clear();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageImmediately(Integer destClient, String message) {
        try {
            PublicKey ExtPublicKey = localUserList.get(destClient);
            out.writeObject(new Message(this.id, this.username, functions.encryptData(message, ExtPublicKey), destClient));
        } catch (Exception e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void updateMessageHistory(String decryptedMessage) {
        if (messageHistory.size() == 3) {
            messageHistory.remove(0);
        }
        messageHistory.add(decryptedMessage);
    }

    // Add this method to allow ClientFrame to display system notifications
    public interface ClientObserver {
        void updateMessage(String message);
        void updateClients(ArrayList<Integer> clientList);
        void updateSystemNotification(String notification); // Method to update UI with system notifications
    }
}
