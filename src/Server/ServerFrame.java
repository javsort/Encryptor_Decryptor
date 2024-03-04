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
        coverButton.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor), thickness));
        coverButton.setBackground(Color.decode(buttonFieldColor));
        coverButton.add(buttonPanel);

        coverText = new JPanel(new FlowLayout());
        coverText.setBorder(BorderFactory.createLineBorder(Color.decode(borderColor), thickness));
        coverText.setBackground(Color.decode(textAreaColor));
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
