package Controller;

import Server.ServerFrame;

public class ServerController {
    public static void main(String[] args) {
        System.out.println("Initializing Server");

        ServerFrame server = new ServerFrame(5000);
    }
}
