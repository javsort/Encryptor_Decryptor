package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientFrame extends JFrame implements ActionListener {

    // GUI info
    private JTextPane text;
    private JPanel buttonPanel;

    private JPanel coverText;
    private JPanel coverButton;

    private JButton clientConnect;

    private Client client;

    public ClientFrame(String addr, int port){
        // Client
        client = new Client(addr, port);

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

    private void startReceiving(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                while (true){
                    String message = client.receiveMessage();

                    if(message != null){
                        SwingUtilities.invokeLater(new Runnable(){
                            @Override
                            public void run(){
                                text.setText("Your Message!: " + message);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == clientConnect){
            try {
                client.connectToServer();
                clientConnect.setText("Connected!");
                startReceiving();

            } catch (Exception exc) {
                System.out.println("Connection failed bruv: " + exc);
            }
        }

    }
}
