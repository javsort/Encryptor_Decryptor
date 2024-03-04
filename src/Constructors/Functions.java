package Constructors;

import javax.crypto.Cipher;

import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.KeyPair;

public class Functions {

    // Utilize RSA Encryption (we have 2 keys, public - used by everyone and private - only usable by the owner)

    // Generate an asymmetric key
    public KeyPair generateKey() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());

        return keyPairGenerator.generateKeyPair();
    }

    // Encrypt with a public key
    public byte[] encryptData(String data, PublicKey publicKey) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data.getBytes());
    }

    // Decrypt with user's private key
    public String decryptData(Message toDecrypt, PrivateKey privateKey) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(toDecrypt.getMessage());

        return new String(decryptedData);
    }
}