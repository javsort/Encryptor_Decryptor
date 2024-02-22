package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientFrame extends JFrame implements ActionListener, ClientObserver {

    // GUI info
    private JTextPane text;
    private JPanel buttonPanel;

    private JPanel coverText;
    private JPanel coverButton;

    private JTextField inputField;
    private JButton sendButton;

    private JButton clientConnect;

    private Client client;

    public ClientFrame(String addr, int port){
        // Client
        client = new Client(addr, port);
        client.registerObserver(this);
    
        // GUI
        setTitle("Client");
        setSize(400, 200);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        Container contentPane;
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
    
        buttonPanel = new JPanel();  // You need to initialize buttonPanel before adding components
        buttonPanel.setLayout(new FlowLayout());
    
        inputField = new JTextField(15); // Ensure this line is before buttonPanel.add(inputField);
    
        // Same for the sendButton
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
    
        buttonPanel.add(inputField);
        buttonPanel.add(sendButton);
    
        clientConnect = new JButton("Start Connection"); // Initialize clientConnect as well
    
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
    public void updateMessage(String message) {
        text.setText(message);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == clientConnect) {
            try {
                client.connectToServer();
                clientConnect.setText("Connected!");
                client.startReceiving();

            } catch (Exception exc) {
                System.out.println("Connection failed bruv: " + exc);
            }
        } else if (e.getSource() == sendButton) {
            String message = inputField.getText();
            client.sendMessage(message);
            inputField.setText(""); 
        }
    }
}
