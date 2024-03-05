package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientFrame extends JFrame implements ActionListener, Client.ClientObserver {
    private JTextPane text;
    private JPanel buttonPanel;
    private JTextField inputField;
    private JTextField usernameField;
    private JButton sendButton;
    private JButton setUsernameButton;
    private JButton clientConnect;
    private JComboBox<Integer> clientList;
    private Client client;
    private JLabel systemNotificationLabel; // Label for displaying system notifications

    public ClientFrame(String addr, int port) {
        client = new Client(addr, port);
        client.registerObserver(this);

        setTitle("Client");
        setSize(500, 300);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        buttonPanel = new JPanel(new FlowLayout());
        inputField = new JTextField(15);
        usernameField = new JTextField(15);
        sendButton = new JButton("Send");
        setUsernameButton = new JButton("Set Username");
        clientConnect = new JButton("Start Connection");
        clientList = new JComboBox<>();
        systemNotificationLabel = new JLabel("Server Status: Active"); // Initialize the label

        sendButton.addActionListener(this);
        setUsernameButton.addActionListener(this);
        clientConnect.addActionListener(this);
        clientList.addActionListener(this);

        buttonPanel.add(inputField);
        buttonPanel.add(sendButton);
        buttonPanel.add(usernameField);
        buttonPanel.add(setUsernameButton);
        buttonPanel.add(clientList);
        buttonPanel.add(clientConnect);

        text = new JTextPane();
        text.setText("Awaiting Data...");

        JPanel coverButton = new JPanel(new FlowLayout());
        coverButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        coverButton.setBackground(Color.LIGHT_GRAY);
        coverButton.add(buttonPanel);

        JPanel coverText = new JPanel(new FlowLayout());
        coverText.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        coverText.setBackground(Color.LIGHT_GRAY);
        coverText.add(text);

        // Add the system notification label to the layout
        JPanel notificationPanel = new JPanel(new FlowLayout());
        notificationPanel.add(systemNotificationLabel);

        contentPane.add(coverText, BorderLayout.CENTER);
        contentPane.add(coverButton, BorderLayout.SOUTH);
        contentPane.add(notificationPanel, BorderLayout.NORTH); // Add the notification panel to the top

        setVisible(true);
    }

    @Override
    public void updateMessage(String message) {
        SwingUtilities.invokeLater(() -> text.setText(text.getText() + "\n" + message));
    }

    @Override
    public void updateClients(ArrayList<Integer> clientList) {
        SwingUtilities.invokeLater(() -> {
            this.clientList.removeAllItems();
            clientList.forEach(this.clientList::addItem);
        });
    }

    // Implement the method to update the UI with system notifications
    @Override
    public void updateSystemNotification(String notification) {
        SwingUtilities.invokeLater(() -> systemNotificationLabel.setText(notification));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == setUsernameButton) {
            client.setUsername(usernameField.getText());
            usernameField.setText("");
        } else if (e.getSource() == clientConnect) {
            try {
                client.connectToServer();
                clientConnect.setText("Connected");
                client.startReceiving();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else if (e.getSource() == sendButton && clientList.getSelectedItem() != null) {
            client.sendMessage(inputField.getText(), (Integer) clientList.getSelectedItem());
            inputField.setText("");
        }
    }
}
