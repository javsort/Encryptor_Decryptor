package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerFrame extends JFrame implements ActionListener {
    private final Server server;

    Thread serverThread;

    // GUI info
    JTextPane text;
    JPanel buttonPanel;

    JPanel coverText;
    JPanel coverButton;

    JButton connect;

    public ServerFrame(int port){
        server = new Server(port);

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
            server.establishConnection();
            connect.setText("Connected!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
