package Constructors;

import java.security.PublicKey;

// Message for sending public key when a client connects
public class PublicKeyMessage extends  Message {
    private PublicKey publicKey;

    // Constructor
    public PublicKeyMessage(PublicKey publicKey){
        this.publicKey = publicKey;
    }

    // Getter
    public PublicKey getPublicKey(){
        return this.publicKey;
    }
}
