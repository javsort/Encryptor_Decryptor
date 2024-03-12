package Client;

import java.util.ArrayList;

// Interface for the observer (frame) to operate with Client
public interface ClientObserver {
    public void updateMessage(String message, String sender, String time);

    public void updateClients(ArrayList<Integer> clientList);
}