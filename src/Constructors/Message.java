package Constructors;

import java.io.Serializable;
import javax.crypto.SecretKey;
import java.util.*;

public class Message implements Serializable {

    private SecretKey key;
    private byte[] message;
    private Date time;

    public Message(SecretKey key, byte[] encrypted){
        this.time = new Date();
        this.key = key;
        this.message = encrypted;
    }

    public SecretKey getKey(){
        return this.key;
    }

    public byte[] getMessage(){
        return this.message;
    }

    public Date getTime(){
        return this.time;
    }
}
