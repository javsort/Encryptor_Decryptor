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

    public Server(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(this.port);
            executorService = Executors.newFixedThreadPool(10);
            isRunning = true;
            System.out.println("Server started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void establishConnection() {
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void stopServer() {
        isRunning = false;
        executorService.shutdown();
    }

    public HashMap<Integer, PublicKey> getPublicKeys() {
        return new HashMap<>(publicKeys);
    }

    public synchronized void setSleeping(boolean sleeping) {
        this.isSleeping = sleeping;
        if (!isSleeping) {
            processQueuedMessages();
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
