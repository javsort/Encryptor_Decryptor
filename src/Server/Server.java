package Server;

import Constructors.Message;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<ClientHandler> clients = new ArrayList<>();

    private HashMap<Integer, PublicKey> publicKeys = new HashMap<>();

    boolean isRunning;
    private int port;
    private boolean newClient = false;
    private boolean clientDisconnected = false;
    private boolean isSleeping = false;
    private ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();

    private ServerObserver observer;

    private int maxClientSize;
    private int activeClients = 0;

    public Server(int port, int clientSize) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(this.port);
            executorService = Executors.newFixedThreadPool(clientSize);
            this.maxClientSize = clientSize;
            isRunning = true;
            System.out.println("Server started with port: " + this.port + " and max clients: " + clientSize + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerObserver(ServerObserver observer) {
        this.observer = observer;
    }

    public void establishConnection() {
        while (isRunning) {
            try {
                if(activeClients != maxClientSize && activeClients <= maxClientSize && !isSleeping) {
                    activeClients++;
                    Socket socket = serverSocket.accept();

                    // Perform the 3-way handshake
                    performThreeWayHandshake(socket);
                    if (socket != null) {
                        System.out.println("Client accepted" + socket.getInetAddress() + ":" + socket.getPort());
                        ClientHandler clientHandler = new ClientHandler(generateID(), socket, this);
                        clients.add(clientHandler);
                        executorService.execute(clientHandler);
                        publicKeys.put(clientHandler.getId(), clientHandler.getPublicKey());
                        newClient = true;
                    }
                    if (!clients.isEmpty() && (newClient || clientDisconnected)) {
                        newClient = false;
                        clientDisconnected = false;
                        updateClientLists();
                    }
                    Thread.sleep(500);
                }
                /*} else {
                    Socket socket = serverSocket.accept();
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject("Server is full");
                    oos.flush();
                    System.out.println("Server is full");
                }*/
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Perform the 3-way handshake
    public void performThreeWayHandshake(Socket socket) {
        try {
            System.out.println("Performing 3-way handshake Client");
    
            // Create ObjectOutputStream for sending data
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    
            // Create ObjectInputStream for receiving data
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
    
            // Step 1: Receive the SYN from the client
            String receivedSYN = (String) ois.readObject();
            System.out.println("Received SYN: " + receivedSYN);
    
            // Step 2: Send the SYN-ACK to the client
            oos.writeObject("SYN-ACK");
            oos.flush(); // Ensure it's sent immediately
            System.out.println("Sent SYN-ACK");
    
            // Step 3: Receive the ACK from the client
            String receivedACK = (String) ois.readObject();
            System.out.println("Received ACK: " + receivedACK);
    
            System.out.println("3-way handshake completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Handshake failed: " + e.getMessage());
        }
    }
    

    private void updateClientLists() {
        for (ClientHandler clientHandler : clients) {
            clientHandler.updateClientList();
        }
    }

    public int generateID() {
        return clients.size() + 1;
    }

    public void removeClient(ClientHandler disconnected) {
        clients.remove(disconnected);
        publicKeys.remove(disconnected.getId());
        clientDisconnected = true;
    }

    public synchronized void stopServer() {
        isRunning = false;

        for(ClientHandler client : clients) {
            try {
                client.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        try {
            if(serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server stopped");
    }

    public HashMap<Integer, PublicKey> getPublicKeys() {
        return new HashMap<>(publicKeys);
    }
    
    public synchronized void setSleeping(boolean sleeping) {
        this.isSleeping = sleeping;
        if (!isSleeping) {
            System.out.println("Waking up server");
            processQueuedMessages();
        } else {
            System.out.println("Server is now sleeping");
        }
    }

    private void processQueuedMessages() {
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();
            forwardMessage(message, null); // Assuming forwardMessage can handle a null sender
        }
    }

    public void forwardMessage(Message message, ClientHandler sender) {
        if (isSleeping) {
            messageQueue.add(message);
        } else {
            int destination = message.getReceiverID();
            if (destination != 0) {
                clients.stream()
                        .filter(client -> client.getId() == destination)
                        .findFirst()
                        .ifPresent(client -> {
                            try {
                                client.sendMessage(message);
                                observer.updateLog(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            } else {
                broadcastMessage(message, sender);
            }
        }
    }

    public void broadcastMessage(Message message, ClientHandler sender) {
        clients.forEach(client -> {
            if (client != sender) {
                try {
                    client.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
