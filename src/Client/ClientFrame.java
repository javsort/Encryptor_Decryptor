package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientFrame extends JFrame implements ActionListener, ClientObserver {
    // GUI info
    private JTextPane text;
    private JPanel buttonPanel;

    private JPanel coverText;
    private JPanel coverButton;

    private JTextField inputField;
    private JTextField usernameField;
    private JButton sendButton;
    private JButton setUsernameButton;
    private JButton clientConnect;

    // Client list is updated as updated
    private JComboBox<Integer> clientList = null;

    // Client assigned to the frame
    private Client client;

    public ClientFrame(String addr, int port){
        // Client
        client = new Client(addr, port);
        client.registerObserver(this);

        // GUI
        setTitle("Client");
        setSize(500, 300);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane;
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        buttonPanel = new JPanel();  // You need to initialize buttonPanel before adding components
        buttonPanel.setLayout(new FlowLayout());

        inputField = new JTextField(15);
        usernameField = new JTextField(15);
        
        // Same for the buttons
        sendButton = new JButton("Send");
        setUsernameButton = new JButton("Set Username");
        clientConnect = new JButton("Start Connection"); 

        //add action listeners
        sendButton.addActionListener(this);
        setUsernameButton.addActionListener(this);
        clientConnect.addActionListener(this);

        buttonPanel.add(inputField);
        buttonPanel.add(sendButton);
        buttonPanel.add(usernameField);
        buttonPanel.add(setUsernameButton);

        clientList = new JComboBox<>();
        buttonPanel.add(clientList);
        clientList.addActionListener(this);

        buttonPanel.add(clientConnect);

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

    // Observer(this) reads message from client
    @Override
public void updateMessage(String message) {
    SwingUtilities.invokeLater(() -> {
        String existingContent = text.getText();
        String[] messages = existingContent.split("\n");
        ArrayList<String> messageList = new ArrayList<>(Arrays.asList(messages));
        messageList.add(message);
        
        // Get only last 3 messages
        int start = messageList.size() > 3 ? messageList.size() - 3 : 0;
        messageList = new ArrayList<>(messageList.subList(start, messageList.size()));

        String newContent = String.join("\n", messageList);
        text.setText(newContent);
    });
}
    // Observer(this) receives updated client list
    @Override
    public void updateClients(ArrayList<Integer> clientList) {
        SwingUtilities.invokeLater(() -> {
            this.clientList.removeAllItems();

            for (Integer client : clientList) {
                this.clientList.addItem(client);
                System.out.println("Client Frame has added: " + client + " to list");
            }
        });
    }

    // Operations on button click
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == setUsernameButton) {
            String username = usernameField.getText();
            client.setUsername(username);
            usernameField.setText("");
        }

        else if (e.getSource() == clientConnect) {
            try {
                client.connectToServer();
                clientConnect.setText("Connected!");
                client.startReceiving();

            } catch (Exception exc) {
                System.out.println("Connection failed bruv: " + exc);
            }

        } else if (e.getSource() == sendButton && clientList.getSelectedItem() != null) {
            String message = inputField.getText();
            client.sendMessage(message, clientList.getItemAt(clientList.getSelectedIndex()));
            inputField.setText("");
        }
    }
}