package Controller;

import Client.ClientFrame;
import Server.ServerFrame;

public class ClientController {
    public static void main(String[] args) {
        System.out.println("Initializing Client, branch Jasbolas");

        //ServerFrame server = new ServerFrame(5000);
        ClientFrame client = new ClientFrame("127.0.0.1", 5000);
    }
}
