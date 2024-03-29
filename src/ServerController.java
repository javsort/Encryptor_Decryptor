import Server.ServerFrame;

import javax.swing.*;

public class ServerController {
    public static void main(String[] args) {
        System.out.println("Initializing Server");

        SwingUtilities.invokeLater((new Runnable() {
            @Override
            public void run() {
                try {
                    ServerFrame server = new ServerFrame(5000, 50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}
