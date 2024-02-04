import java.io.Serializable;
import javax.crypto.SecretKey;

public class Message implements Serializable {

    private SecretKey key;
    private byte[] message;

    public Message(SecretKey key, byte[] encrypted){
        this.key = key;
        this.message = encrypted;
    }

    public SecretKey getKey(){
        return this.key;
    }

    public byte[] getMessage(){
        return this.message;
    }
}
