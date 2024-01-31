import java.io.DataInputStream;
import java.net.Socket;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame implements ActionListener {
    // Network data
    private Socket socket = null;
    private DataInputStream in = null;

    private String address;
    private int port;

    // GUI info
    JTextPane text;
    JPanel buttonPanel;

    JPanel coverText;
    JPanel coverButton;

    JButton clientConnect;


    public Client(String addr, int port){
        // Connection Data
        this.address = addr;
        this.port = port;

        // GUI
        setTitle("Client");
        setSize(400, 200);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane;
        contentPane = getContentPane();
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        clientConnect = new JButton("Start Connection");

        buttonPanel.add(clientConnect);
        clientConnect.addActionListener(this);

        text = new JTextPane();
        text.setText("Awaiting Data...");


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
    public void actionPerformed(ActionEvent e) {
        try {
            socket = new Socket(address, port);

            in = new DataInputStream(socket.getInputStream());
            
            System.out.println("Connected");
            clientConnect.setText("Connected!");

            String message = in.readUTF();
            text.setText("Your Message!: " + message);

        } catch (Exception exc) {
            System.out.println("Connection failed bruv: " + exc);
        }

    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 5000);
    }
}
