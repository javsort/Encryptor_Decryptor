package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientFrame extends JFrame implements ActionListener, ClientObserver {
    // GUI info
    private JTextPane text;
    private JPanel buttonPanel;

    private JPanel coverText;
    private JPanel coverButton;
    
    private String usernamePlaceHolder = "Username: ";
    private JLabel usernameDisplay = new JLabel(usernamePlaceHolder + "Your Username");
    
    private JTextField inputField;
    private JTextField usernameField;
    private JButton sendButton;
    private JButton setUsernameButton;
    private JButton clientConnect;

    // Username dialog setup
    private JDialog popUpWindow;
    private JPanel popUpPanel;

    // Color and texturing
    private String buttonFieldColor = "#01A7C2";
    private String buttonColor = "#020887";
    private String buttonBorderColor = "#2081C3";
    private String textAreaColor = "#F5F5F5";
    private String borderColor = "#37392E";

    private int thickness = 3;
    private int buttonHeight = 30;
    private int fontsize = 13;

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
        setSize(800, 300);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane;
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        buttonPanel = new JPanel();  // You need to initialize buttonPanel before adding components
        buttonPanel.setLayout(new FlowLayout());

        inputField = new JTextField(25);
        
        // Same for the buttons
        sendButton = new JButton(" Send ");
        clientConnect = new JButton(" Start Connection ");
        
        // Color buttons
        sendButton.setBackground(Color.decode(buttonColor));
        sendButton.setBorder(BorderFactory.createLineBorder(Color.decode(buttonBorderColor)));
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(100, buttonHeight));
        sendButton.setFont(new Font("Arial", Font.BOLD, fontsize));

        clientConnect.setBackground(Color.decode(buttonColor));
        clientConnect.setBorder(BorderFactory.createLineBorder(Color.decode(buttonBorderColor)));
        clientConnect.setForeground(Color.WHITE);
        clientConnect.setPreferredSize(new Dimension(150, buttonHeight));
        clientConnect.setFont(new Font("Arial", Font.BOLD, fontsize));

        //add action listeners
        sendButton.addActionListener(this); 
        clientConnect.addActionListener(this);

        buttonPanel.add(usernameDisplay);
        buttonPanel.add(inputField);
        buttonPanel.add(sendButton);

        clientList = new JComboBox<>();
        buttonPanel.add(clientList);
        clientList.addActionListener(this);

        buttonPanel.add(clientConnect);
        buttonPanel.setBackground(Color.decode(buttonFieldColor));

        text = new JTextPane();
        text.setText("Awaiting Data...");

        // Covers
        coverButton = new JPanel();
        coverButton.setLayout(new FlowLayout());
        coverButton.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor), thickness));
        coverButton.setBackground(Color.decode(buttonFieldColor));

        coverText = new JPanel();
        coverText.setLayout(new FlowLayout());
        coverText.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor), thickness));
        coverText.setBackground(Color.decode(textAreaColor));

        coverText.add(text);
        coverButton.add(buttonPanel);

        contentPane.add(coverText, BorderLayout.CENTER);
        contentPane.add(coverButton, BorderLayout.SOUTH);

        setVisible(true);


        // JDialog popUpWindow to set username before accessing system
        popUpWindow = new JDialog();
        popUpWindow.setTitle("Enter Username");
        popUpWindow.setModal(true);
        popUpWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Create a panel to hold the components
        popUpPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Create a text field and button for the panel
        usernameField = new JTextField(15);
        setUsernameButton = new JButton(" Set Username ");

        setUsernameButton.setBackground(Color.decode(buttonColor));
        setUsernameButton.setBorder(BorderFactory.createLineBorder(Color.decode(buttonBorderColor)));
        setUsernameButton.setForeground(Color.WHITE);
        setUsernameButton.setPreferredSize(new Dimension(100, buttonHeight));
        setUsernameButton.setFont(new Font("Arial", Font.BOLD, fontsize));

        setUsernameButton.addActionListener(this);

        // Add the usernameField and setUsernameButton to the panel
        popUpPanel.add(usernameField);
        popUpPanel.add(setUsernameButton);

        // Add the panel to the dialog
        popUpWindow.getContentPane().add(popUpPanel);

        // Set the size of the dialog
        popUpWindow.setSize(300, 100);

        // Show the dialog
        popUpWindow.setLocationRelativeTo(this);
        popUpWindow.setVisible(true);
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
            usernameDisplay.setText(usernamePlaceHolder + username);
            client.setUsername(username);
            popUpWindow.dispose();
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