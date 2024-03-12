import javax.swing.SwingUtilities;

import Client.*;

public class ClientController {
    public static void main(String[] args) {
        System.out.println("Initializing Client");

        SwingUtilities.invokeLater((new Runnable() {
            @Override
            public void run() {
                try {
                    ClientFrame client = new ClientFrame("127.0.0.1", 5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}
