package Constructors;

import java.io.Serializable;
import java.util.*;

public class Message implements Serializable {

    // Serializable is used to send objects over the network

    // Message Properties
    private byte[] message;
    private Date time;
    private int senderID;
    private String username;

    private int receiverID;

    // Constructor - uses the sender's ID, username, the encrypted message and the receiver's ID for server handling
    public Message(int id, String user, byte[] encrypted, int destId){
        this.time = new Date();         // Set the time of the message - to be used for GUI

        this.senderID = id;
        this.username = user;

        this.message = encrypted;
        this.receiverID = destId;
    }

    // Empty constructor for publicKeyMessage sending
    public Message(){
        this.time = new Date();
    }

    // Getters
    public byte[] getMessage(){
        return this.message;
    }

    public String getUsername(){
        return this.username;
    }

    public int getSenderID(){
        return this.senderID;
    }

    public int getReceiverID(){
        return this.receiverID;
    }

    public Date getTime(){
        return this.time;
    }
}
