package Server;

import javax.swing.*;

import Constructors.Message;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerFrame extends JFrame implements ActionListener, ServerObserver {
    private Server server;
    public int serverPort;
    public int maxClients;
    public boolean isSleeping = false;
    public boolean isRunning = false;

    // GUI components
    JPanel buttonPanel;
    JPanel coverText;
    JPanel loggingArea;
    JPanel coverButton;

    JButton connect;
    JButton shutoff;
    JButton sleep;
    JButton reactivate;

    JTextPane StatusLog;

    JTextArea dateArea;
    JTextArea exchangeArea;
    JTextArea messageArea;
    //JTextArea receiverArea;

    // Color and texturing
    private String buttonFieldColor = "#01A7C2";
    private String buttonColor = "#020887";
    private String buttonBorderColor = "#2081C3";
    private String textAreaColor = "#F5F5F5";
    private String borderColor = "#37392E";

    private int thickness = 3;
    private int buttonHeight = 30;
    private int fontsize = 13;

    // Constructor for frame
    public ServerFrame(int port, int maxClients) {
        // Initialize the server
        //server = new Server(port);
        this.serverPort = port;
        this.maxClients = maxClients;

        // Setup the GUI
        setTitle("Server");
        setSize(500, 300);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Buttons
        connect = new JButton("Turn On");
        shutoff = new JButton("Turn Off");
        sleep = new JButton("Sleep");
        reactivate = new JButton("Reactivate");

        // Button styling
        connect.setBackground(Color.decode(buttonColor));
        connect.setBorder(BorderFactory.createLineBorder(Color.decode(buttonBorderColor)));
        connect.setForeground(Color.WHITE);
        connect.setPreferredSize(new Dimension(100, buttonHeight));
        connect.setFont(new Font("Arial", Font.BOLD, fontsize));

        shutoff.setBackground(Color.decode(buttonColor));
        shutoff.setBorder(BorderFactory.createLineBorder(Color.decode(buttonBorderColor)));
        shutoff.setForeground(Color.WHITE);
        shutoff.setPreferredSize(new Dimension(100, buttonHeight));
        shutoff.setFont(new Font("Arial", Font.BOLD, fontsize));

        sleep.setBackground(Color.decode(buttonColor));
        sleep.setBorder(BorderFactory.createLineBorder(Color.decode(buttonBorderColor)));
        sleep.setForeground(Color.WHITE);
        sleep.setPreferredSize(new Dimension(100, buttonHeight));
        sleep.setFont(new Font("Arial", Font.BOLD, fontsize));

        reactivate.setBackground(Color.decode(buttonColor));
        reactivate.setBorder(BorderFactory.createLineBorder(Color.decode(buttonBorderColor)));
        reactivate.setForeground(Color.WHITE);
        reactivate.setPreferredSize(new Dimension(100, buttonHeight));
        reactivate.setFont(new Font("Arial", Font.BOLD, fontsize));

        // Add buttons to panel
        buttonPanel.add(connect);
        buttonPanel.add(shutoff);
        buttonPanel.add(sleep);
        buttonPanel.add(reactivate);
        buttonPanel.setBackground(Color.decode(buttonFieldColor));


        // register things for logging panel
        loggingArea = new JPanel();
        loggingArea.setLayout(new BorderLayout());
        dateArea = new JTextArea();
        exchangeArea = new JTextArea();
        messageArea = new JTextArea();

        dateArea.setColumns(12);
        dateArea.setRows(10);
        dateArea.setLineWrap(true);
        dateArea.setEditable(false);
        dateArea.setBackground(Color.decode(textAreaColor));
        dateArea.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor)));

        messageArea.setColumns(10);
        messageArea.setRows(10);
        messageArea.setLineWrap(true);
        messageArea.setEditable(false);
        messageArea.setBackground(Color.decode(textAreaColor));
        messageArea.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor)));

        exchangeArea.setColumns(12);
        exchangeArea.setRows(10);
        exchangeArea.setLineWrap(true);
        exchangeArea.setEditable(false);
        exchangeArea.setBackground(Color.decode(textAreaColor));
        exchangeArea.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor)));

        loggingArea.add(dateArea, BorderLayout.WEST);
        loggingArea.add(messageArea, BorderLayout.CENTER);
        loggingArea.add(exchangeArea, BorderLayout.EAST);
        

        // Register action listeners
        connect.addActionListener(this);
        shutoff.addActionListener(this);
        sleep.addActionListener(this);
        reactivate.addActionListener(this);

        // Text pane for server status
        StatusLog = new JTextPane();
        StatusLog.setText("Server status: Ready to connect");
        StatusLog.setEditable(false);

        // Setup covers for text and buttons
        coverButton = new JPanel(new FlowLayout());
        coverButton.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor), thickness));
        coverButton.setBackground(Color.decode(buttonFieldColor));
        coverButton.add(buttonPanel);

        coverText = new JPanel(new FlowLayout());
        coverText.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor), thickness));
        coverText.setBackground(Color.decode(textAreaColor));
        coverText.add(StatusLog);

        // Add components to content pane
        contentPane.add(coverText, BorderLayout.NORTH);
        contentPane.add(loggingArea, BorderLayout.CENTER);
        contentPane.add(coverButton, BorderLayout.SOUTH);

        // Make the frame visible
        setVisible(true);
    }

    @Override
    public void updateLog(Message message) {

        SwingUtilities.invokeLater(() -> {
            String existingDates = dateArea.getText();
            String existingExchanges = exchangeArea.getText();
            String existingMessages = messageArea.getText();

            String[] dates = existingDates.split("\n");
            String[] exchanges = existingExchanges.split("\n");
            String[] messageArray = existingMessages.split("\n");

            ArrayList<String> messageList = new ArrayList<>(Arrays.asList(messageArray));
            ArrayList<String> exchangeList = new ArrayList<>(Arrays.asList(exchanges));
            ArrayList<String> dateList = new ArrayList<>(Arrays.asList(dates));

            messageList.add(message.getMessage().toString());
            exchangeList.add("From: " + message.getSenderName().toString() + " To: " + message.getReceiverID());
            dateList.add(message.getTime());
            
            // Get only last 10 messages
            int startMessage = messageList.size() > 10 ? messageList.size() - 10 : 0;
            messageList = new ArrayList<>(messageList.subList(startMessage, messageList.size()));

            int startDate = dateList.size() > 10 ? dateList.size() - 10 : 0;
            dateList = new ArrayList<>(dateList.subList(startDate, dateList.size()));

            int startExchange = exchangeList.size() > 10 ? exchangeList.size() - 10 : 0;
            exchangeList = new ArrayList<>(exchangeList.subList(startExchange, exchangeList.size()));

            String newMessage = String.join("\n", messageList);
            String newExchanges = String.join("\n", exchangeList);
            String newDates = String.join("\n", dateList);

            dateArea.setText(newDates);
            exchangeArea.setText(newExchanges);
            messageArea.setText(newMessage);
        });
    }

    @Override
    public void actionPerformed(ActionEvent action) {
        if (action.getSource() == connect) {
            if(!isRunning){
                server = new Server(serverPort, maxClients);
                server.registerObserver(this);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        server.establishConnection();
                        return null;
                    }

                    @Override
                    protected void done() {
                        connect.setText("Connected");
                    }
                };

                worker.execute();
                isRunning = true;
                StatusLog.setText("Server status: Connected");

            } else {
                StatusLog.setText("Server status: Already connected");
            }
            
        } else if (action.getSource() == shutoff) {
            if(isRunning){
                server.stopServer();
                StatusLog.setText("Server status: Shut Down");
                isRunning = false;
            } else {
                StatusLog.setText("Server status: Already shut down");
            }

        } else if (action.getSource() == sleep) {
            if(isSleeping){
                StatusLog.setText("Server status: Already sleeping");

            } else {
                server.setSleeping(true);
                StatusLog.setText("Server status: Sleeping");
                isSleeping = true;
            }

        } else if (action.getSource() == reactivate) {
            if(!isSleeping){
                StatusLog.setText("Server status: Already reactivated");

            } else {
                isSleeping = false;
                server.setSleeping(false);
                StatusLog.setText("Server status: Reactivated");
            }
        }
    }
}
