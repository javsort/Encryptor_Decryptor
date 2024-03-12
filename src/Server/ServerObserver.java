package Server;

import Constructors.Message;

public interface ServerObserver {
    public void updateLog(Message message);
}