package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerFrame extends JFrame implements ActionListener {
    private final Server server;

    // GUI components
    JTextPane text;
    JPanel buttonPanel;
    JPanel coverText;
    JPanel coverButton;
    JButton connect;
    JButton shutoff;
    JButton sleep;
    JButton reactivate;

    // Constructor for frame
    public ServerFrame(int port) {
        // Initialize the server
        server = new Server(port);

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

        // Add buttons to panel
        buttonPanel.add(connect);
        buttonPanel.add(shutoff);
        buttonPanel.add(sleep);
        buttonPanel.add(reactivate);

        // Register action listeners
        connect.addActionListener(this);
        shutoff.addActionListener(this);
        sleep.addActionListener(this);
        reactivate.addActionListener(this);

        // Text pane for server status
        text = new JTextPane();
        text.setText("Server status: Ready to connect");

        // Setup covers for text and buttons
        coverButton = new JPanel(new FlowLayout());
        coverButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        coverButton.setBackground(Color.LIGHT_GRAY);
        coverButton.add(buttonPanel);

        coverText = new JPanel(new FlowLayout());
        coverText.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        coverText.setBackground(Color.LIGHT_GRAY);
        coverText.add(text);

        // Add components to content pane
        contentPane.add(coverText, BorderLayout.CENTER);
        contentPane.add(coverButton, BorderLayout.SOUTH);

        // Make the frame visible
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent action) {
        if (action.getSource() == connect) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    server.establishConnection();
                    return null;
                }

                @Override
                protected void done() {
                    connect.setText("Connected");
                    text.setText("Server status: Connected");
                }
            };
            worker.execute();
        } else if (action.getSource() == shutoff) {
            server.stopServer();
            text.setText("Server status: Shut Down");
        } else if (action.getSource() == sleep) {
            server.setSleeping(true);
            text.setText("Server status: Sleeping");
        } else if (action.getSource() == reactivate) {
            server.setSleeping(false);
            text.setText("Server status: Reactivated");
        }
    }
}
