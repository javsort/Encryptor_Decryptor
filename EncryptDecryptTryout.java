import Constructors.Message;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

// This version of the Encryptor_Decryptor is a simple tryout of the encryption and decryption process within the same class file
// The main method is used to test the encryption and decryption of a message

public class EncryptDecryptTryout {
    
    public static void main(String[] args) throws Exception {

        Message toSend;

        SecretKey keyToUse = generateKey();

        String textPlain = "Ayo this is a message hermano que pedo como estás";
        byte[] encryptedData = encryptData(textPlain, keyToUse);

        String decryptedText = decryptData(new Message(keyToUse, encryptedData));


        System.out.println("Original: " + textPlain);
        System.out.println("Encrypted; " + Base64.getEncoder().encodeToString(encryptedData));
        System.out.println("Decrypted: " + decryptedText);
        
    }

    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);

        return keyGenerator.generateKey();
    }

    private static byte[] encryptData(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data.getBytes());
    }

    private static String decryptData(Message toDecrypt) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, toDecrypt.getKey());
        byte[] decryptedData = cipher.doFinal(toDecrypt.getMessage());

        return new String(decryptedData);
    }
}


