package Constructors;

import Constructors.Message;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Functions {

    // Utilize RSA Encryption (we have 2 keys, public - used by everyone and private - only usable by the owner)
    // CAMBIAR ESTE PEDO APENAS SE PUEDA!!!
    public SecretKey generateKey() throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);

        return keyGenerator.generateKey();
    }

    public byte[] encryptData(String data, SecretKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data.getBytes());
    }

    public String decryptData(Message toDecrypt) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, toDecrypt.getKey());
        byte[] decryptedData = cipher.doFinal(toDecrypt.getMessage());

        return new String(decryptedData);
    }
}