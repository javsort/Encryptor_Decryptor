package Constructors;

import java.io.Serializable;
import java.util.*;

public class Message implements Serializable {

    //private SecretKey key;
    private byte[] message;
    private Date time;
    private int senderID;
    private String username;

    private int receiverID;

    public Message(int id, String user, byte[] encrypted, int destId){
        this.time = new Date();

        this.senderID = id;
        this.username = user;

        this.message = encrypted;
        this.receiverID = destId;
    }

    public Message(){
        this.time = new Date();
    }

    //public SecretKey getKey(){
    //    return this.key;
    //}

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
