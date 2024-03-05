package Client;

import java.util.ArrayList;

public interface ClientObserver {
    void updateMessage(String message);
    void updateClients(ArrayList<Integer> clientList);
    void updateSystemNotification(String notification); 
}

