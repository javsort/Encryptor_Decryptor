package Constructors;

import java.io.Serializable;
import java.util.*;

public class Message implements Serializable {

    private byte[] message;
    private Date time;
    private int senderID;
    private String senderName;
    private int receiverID;

    // constructor uses the sender's ID, username(sender's name), the encrypted message and receiver's ID for server handling
    public Message(int id, String user, byte[] encrypted, int destId){
        this.time = new Date();
        this.senderID = id;
        this.senderName = user;
        this.message = encrypted;
        this.receiverID = destId;
    }

    // Empty constructor for publicKeyMessage sending
    public Message(){
        this.time = new Date();
    }

    public byte[] getMessage(){
        return this.message;
    }

    public String getSenderName() {
        return this.senderName;
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