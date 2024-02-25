package Constructors;

import java.security.PublicKey;

public class PublicKeyMessage extends  Message {
    private PublicKey publicKey;
    public PublicKeyMessage(PublicKey publicKey){
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey(){
        return this.publicKey;
    }
}
