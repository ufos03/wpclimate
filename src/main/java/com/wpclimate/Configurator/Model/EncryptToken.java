package com.wpclimate.Configurator.Model;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.wpclimate.Configurator.exceptions.DecryptFailedException;
import com.wpclimate.Configurator.exceptions.EncryptFailedExeception;

import java.io.*;
import java.util.Base64;

/**
 * The EncryptedToken class provides static methods to encrypt and decrypt strings using AES encryption.
 * It ensures the security of sensitive data by using a 256-bit AES key.
 * The key is stored in a file and reused for encryption and decryption.
 */
public class EncryptToken 
{
    private static final String KEY_FILE = "./config.key";
    private static SecretKey key;

    // Static block to initialize the encryption key
    static {
        try {
            key = loadKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize encryption key", e);
        }
    }

    /**
     * Loads the AES encryption key from a file.
     * If the key file does not exist, a new key is generated and saved.
     *
     * @return The AES encryption key.
     * @throws Exception If an error occurs while loading or generating the key.
     */
    private static SecretKey loadKey() throws Exception 
    {
        File keyFile = new File(KEY_FILE);
        if (keyFile.exists()) 
        {
            // Read the key from the file
            try (FileInputStream fis = new FileInputStream(keyFile)) 
            {
                byte[] encodedKey = fis.readAllBytes();
                return new SecretKeySpec(encodedKey, "AES");
            }
        } 
        else 
        {
            // Generate a new key and save it
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // 256-bit AES key
            SecretKey newKey = keyGen.generateKey();
            try (FileOutputStream fos = new FileOutputStream(keyFile)) 
            {
                fos.write(newKey.getEncoded());
            }
            return newKey;
        }
    }

    /**
     * Encrypts a string using AES encryption.
     *
     * @param plainText The string to encrypt.
     * @return A Base64-encoded string representing the encrypted data.
     * @throws EncryptFailedExeception If the encryption process fails.
     */
    public static String encrypt(String plainText) throws EncryptFailedExeception 
    {
        try 
        {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } 
        catch (Exception e) 
        {
            throw new EncryptFailedExeception("The encryption process has failed.", e);
        }
    }

    /**
     * Decrypts a Base64-encoded string into its original plain text.
     *
     * @param encryptedString The Base64-encoded string representing the encrypted data.
     * @return The decrypted plain text.
     * @throws DecryptFailedException If the decryption process fails.
     */
    public static String decrypt(String encryptedString) throws DecryptFailedException 
    {
        try 
        {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedString);

            if (decoded.length == 0) 
            {
                throw new DecryptFailedException("The decryption process has failed: empty input.");
            }

            return new String(cipher.doFinal(decoded));
        } 
        catch (Exception e) 
        {
            throw new DecryptFailedException("The decryption process has failed.", e);
        }
    }
}