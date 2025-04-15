package com.wpclimate.configurator.model;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: Aggiungere metodo per costruire un certo modello da quello base.

/**
 * The {@code Model} class represents a generic configuration model.
 * It uses a list of {@code Token} objects to store key-value pairs for configuration settings.
 * This class provides methods to add, update, retrieve, and manage tokens.
 * 
 * <p>
 * Each token is represented by a {@code Token} object, which contains a key, a value,
 * and an optional flag indicating whether the value is encrypted.
 * </p>
 * 
 * <p>
 * This class is serializable, allowing instances to be saved and loaded from a file.
 * </p>
 */
public class Model implements Serializable 
{
    private static final long serialVersionUID = 1L; // Version ID for serialization compatibility
    private ArrayList<Token> tokens; // List of tokens representing key-value pairs

    /**
     * Default constructor.
     * Initializes an empty {@code Model} instance with an empty list of tokens.
     */
    public Model() 
    {
        this.tokens = new ArrayList<>();
    }

    /**
     * Adds or updates a token in the model.
     * If a token with the specified key already exists, its value is updated
     * only if the new value is different. Otherwise, a new token is added.
     *
     * @param key         The key of the token. Must not be {@code null}.
     * @param value       The value of the token. Must not be {@code null}.
     * @param isEncrypted A flag indicating whether the value is encrypted.
     * @return {@code true} if the token was added or updated successfully, {@code false} otherwise.
     * @throws IllegalArgumentException If the key or value is {@code null}.
     */
    public boolean set(String key, String value, Boolean isEncrypted) 
    {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value must not be null.");
        }

        for (Token token : this.tokens) 
        {
            if (token.getKey().equals(key)) 
            {
                if (!token.getValue().equals(value)) 
                    token.setValue(value, isEncrypted);
                return true;
            }
        }

        Token token = new Token(key, value, isEncrypted);
        return this.tokens.add(token);
    }

    /**
     * Retrieves the list of tokens in the model.
     *
     * @return An {@code ArrayList} of {@code Token} objects representing the key-value pairs.
     */
    public ArrayList<Token> getTokensList() 
    {
        return this.tokens;
    }

    /**
     * Retrieves the value associated with the specified key. If encrypted, returns it decrypted.
     *
     * @param key The key of the token to retrieve.
     * @return The value associated with the key, or {@code null} if the key does not exist.
     */
    public String get(String key) 
    {
        for (Token token : this.tokens) 
        {
            if (token.getKey().equals(key))
                return token.getValue();
        }
        return null;
    }

    /**
     * Removes the token associated with the specified key.
     *
     * @param key The key of the token to remove.
     * @return {@code true} if the token was removed, {@code false} if the key does not exist.
     */
    public boolean remove(String key) 
    {
        return this.tokens.removeIf(token -> token.getKey().equals(key));
    }

    /**
     * Checks if a token with the specified key exists in the model.
     *
     * @param key The key to check.
     * @return {@code true} if a token with the key exists, {@code false} otherwise.
     */
    public boolean containsKey(String key) 
    {
        for (Token token : this.tokens) 
        {
            if (token.getKey().equals(key))
                return true;
        }
        return false;
    }

    /**
     * Clears all tokens from the model.
     */
    public void clear() 
    {
        this.tokens.clear();
    }

    /**
     * Returns the number of tokens in the model.
     *
     * @return The number of tokens.
     */
    public int size() 
    {
        return this.tokens.size();
    }
}