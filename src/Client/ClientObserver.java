package Client;

import java.util.ArrayList;

public interface ClientObserver {
    public void updateMessage(String message);

    public void updateClients(ArrayList<Integer> clientList);
}
