package com.wpclimate.configurator.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Token} class represents a key-value pair used for configuration settings.
 * It supports optional encryption of the value using the {@code EncryptedToken} class.
 * Each token contains a key, a value, and a flag indicating whether the value is encrypted.
 */
public class Token 
{
    private String key;
    private String value;
    private Boolean isEncrypted = false;

    /**
     * Default constructor.
     * Creates an empty token.
     */
    public Token() {}

    /**
     * Constructs a token with the specified key and value.
     * The value is stored in plain text.
     *
     * @param key   The key of the token. Must not be empty.
     * @param value The value of the token. Must not be empty.
     * @throws IllegalArgumentException If the key or value is empty.
     */
    public Token(String key, String value) 
    {
        if (key == null || key.isEmpty())
            throw new IllegalArgumentException("The key cannot be null or empty.");
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException("The value cannot be null or empty.");

        this.setKey(key);
        this.setValue(value, false);
    }

    /**
     * Constructs a token with the specified key, value, and encryption flag.
     * If {@code isEncrypted} is {@code true}, the value is encrypted.
     *
     * @param key         The key of the token. Must not be empty.
     * @param value       The value of the token. Must not be empty.
     * @param isEncrypted {@code true} if the value should be encrypted; {@code false} otherwise.
     * @throws IllegalArgumentException If the key or value is empty.
     */
    public Token(String key, String value, Boolean isEncrypted)
    {
        if (key.isEmpty() || value.isEmpty())
            throw new IllegalArgumentException("The arguments cannot be null");
        
        this.setKey(key);
        this.setValue(value, isEncrypted);
    }

    public void setKey(String key)
    {
        if (key.isEmpty())
            throw new IllegalArgumentException("The key parameter cannot be null");
        
            this.key = key;
    }

    public void setValue(String value, Boolean isEncrypted)
    {
        if (value.isEmpty())
            throw new IllegalArgumentException("The value parameter cannot be null");

        if (isEncrypted)
            this.value = this.encryptValue(value);
        else
            this.value = value;
        this.isEncrypted = isEncrypted;
    }

    /**
     * Encrypts the specified value using the {@code EncryptToken} class.
     *
     * @param value The value to encrypt.
     * @return The encrypted value as a Base64-encoded string.
     *         Returns an empty string if encryption fails.
     */
    private String encryptValue(String value)
    {
        try 
        {
            return EncryptToken.encrypt(value);
        } 
        catch (Exception e) 
        {
            return "";
        }
    }

    /**
     * Decrypts the specified value using the {@code EncryptToken} class.
     *
     * @param value The encrypted value to decrypt.
     * @return The decrypted value as plain text.
     *         Returns an empty string if decryption fails.
     */
    private String decryptValue(String value)
    {
        try 
        {
            return EncryptToken.decrypt(value);
        } 
        catch (Exception e) 
        {
            return "";
        }
    }

    /**
     * Returns a map representation of the token.
     * The map contains the key and the value. If the value is encrypted, it is decrypted before being added to the map.
     *
     * @return A map containing the token data:
     *         - {@code "key"}: The key of the token.
     *         - {@code "value"}: The value of the token (decrypted if encrypted).
     */
    public Map<String, String> getToken()
    {
        Map<String, String> token = new HashMap<>();
        token.put("key", this.key);

        if (this.isEncrypted)
            token.put("value", this.decryptValue(value));

        return token;
    }

    /**
     * Returns the value of the token.
     * If the value is encrypted, the value is automatic decrypted.
     *
     * @return The value of the token.
     */
    public String getValue()
    {
        if (this.isEncrypted)
            return this.decryptValue(this.value);
        
        return this.value;
    }

    /**
     * Returns the key of the token.
     *
     * @return The key of the token.
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * Returns the status os encryption of a token.
     * @return true if the token is encrypted, false otherwise
     */
    public Boolean isEncrypted()
    {
        return this.isEncrypted;
    }
}