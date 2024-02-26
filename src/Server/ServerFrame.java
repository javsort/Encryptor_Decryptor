package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerFrame extends JFrame implements ActionListener {
    private final Server server;

    // GUI info
    JTextPane text;
    JPanel buttonPanel;

    JPanel coverText;
    JPanel coverButton;

    JButton connect;
    JButton shutoff;

    // Constructor for frame
    public ServerFrame(int port){
        // Server
        server = new Server(port);

        // GUI
        setTitle("Server");
        setSize(500, 300);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane;
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        connect = new JButton("Turn On");
        shutoff = new JButton("Turn Off");

        buttonPanel.add(connect);
        buttonPanel.add(shutoff);
        shutoff.addActionListener(this);
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

    // Action Listener
    @Override
    public void actionPerformed(ActionEvent action) {
        // Turn off server
        if(action.getSource() == shutoff){
            if(!server.isRunning){
                text.setText("Server is already off");
                return;
            } else {
                server.stopServer();
                text.setText("Server Shut Down");
            }
        }

        // Turn on server
        if(action.getSource() == connect){
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    server.establishConnection();
                    return null;
                }

                @Override
                protected void done() {
                    connect.setText("Connected!");
                }
            };
            worker.execute();

        }
    }
}
