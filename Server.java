import javax.crypto.SecretKey;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;



public class Server extends JFrame implements ActionListener {

    private ServerSocket server = null;
    private Socket socket = null;
    private DataInputStream in = null;
    private ObjectOutputStream out = null;

    private Functions functions = new Functions();

    private int port;

    // GUI info
    JTextPane text;
    JPanel buttonPanel;

    JPanel coverText;
    JPanel coverButton;

    JButton connect;

    public Server(int port){
        // Connection Data
        this.port = port;

        // GUI
        setTitle("Server");
        setSize(400, 200);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane;
        contentPane = getContentPane();
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        connect = new JButton("Turn On");

        buttonPanel.add(connect);
        connect.addActionListener(this);

        text = new JTextPane();
        text.setText("Server About to Connect!");


        // Covers
        coverButton = new JPanel();
        coverButton.setLayout(new FlowLayout());
        coverButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        coverButton.setBackground(Color.LIGHT_GRAY);
        
        coverText = new JPanel();
        coverText.setLayout(new FlowLayout());
        coverText.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        coverText.setBackground(Color.LIGHT_GRAY);


        coverText.add(text);
        coverButton.add(buttonPanel);

        contentPane.add(coverText, BorderLayout.CENTER);
        contentPane.add(coverButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent action) {
        try {
            connect.setText("Connected!");
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();

            if(socket != null){
                sendMessage();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendMessage() throws Exception {
        System.out.println("Client accepted");
        text.setText("Client accepted");

        try {
            out = new ObjectOutputStream(socket.getOutputStream());

            SecretKey instanceKey = functions.generateKey();

            Message toSend = new Message(instanceKey, functions.encryptData("Esto es en branch main", instanceKey));
            out.writeObject(toSend);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        System.out.println("Server is running");
    }
}
