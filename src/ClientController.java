import Client.*;

public class ClientController {
    public static void main(String[] args) {
        System.out.println("Initializing Client");

        ClientFrame client = new ClientFrame("127.0.0.1", 5000);

    }
}
