package com.wpclimate.git.credentials;

/**
 * The {@code CredentialsType} enum represents the types of credentials supported
 * for accessing a Git repository.
 * 
 * <p>
 * This enum defines two types of credentials:
 * </p>
 * <ul>
 *   <li>{@link #SSH} - Represents SSH-based credentials.</li>
 *   <li>{@link #HTTPS} - Represents HTTPS-based credentials.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * CredentialsType type = CredentialsType.SSH;
 * System.out.println("Credential Type: " + type.getTypeS());
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * Enums in Java are inherently thread-safe and can be safely used in multi-threaded
 * environments.
 * </p>
 */
public enum CredentialsType 
{

    /**
     * Represents SSH-based credentials.
     */
    SSH("SSH"),

    /**
     * Represents HTTPS-based credentials.
     */
    HTTPS("HTTPS");

    private final String type;

    /**
     * Constructs a {@code CredentialsType} enum with the specified type.
     * 
     * @param type The string representation of the credential type.
     */
    CredentialsType(String type) 
    {
        this.type = type;
    }

    /**
     * Returns the string representation of the credential type.
     * 
     * @return The string representation of the credential type.
     */
    public String getType() 
    {
        return type;
    }
}