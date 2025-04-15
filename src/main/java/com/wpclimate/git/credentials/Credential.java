package com.wpclimate.git.credentials;

import java.util.Map;

import com.wpclimate.configurator.model.Model;

/**
 * The {@code Credential} interface defines the contract for managing Git credentials.
 * 
 * <p>
 * Implementations of this interface are responsible for handling specific types of
 * credentials (e.g., SSH, HTTPS) required to access Git repositories. Each implementation
 * must provide methods to configure, read, and identify the type of credentials.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Configure the credentials using a set of key-value pairs.</li>
 *   <li>Read the credentials from a persistent storage or configuration source.</li>
 *   <li>Identify the type of credentials (e.g., SSH or HTTPS).</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Credential credential = new HttpsCredentials();
 * credential.configure(Map.of("username", "user", "password", "pass"));
 * Model model = credential.read();
 * System.out.println("Credential Type: " + credential.getType());
 * </pre>
 * 
 * @see CredentialsType
 * @see Model
 */
public interface Credential 
{

    /**
     * Configures the credentials using the provided key-value pairs.
     * 
     * <p>
     * This method allows the implementation to set up the credentials based on the
     * provided configuration. For example, an HTTPS implementation might require
     * a username and password, while an SSH implementation might require paths to
     * private and public keys.
     * </p>
     * 
     * @param configuration A map containing key-value pairs for configuring the credentials.
     * @throws Exception If an error occurs during the configuration process.
     */
    public void configure(Map<String, String> configuration) throws Exception;

    /**
     * Reads the credentials from a persistent storage or configuration source.
     * 
     * <p>
     * This method retrieves the credentials as a {@link Model} object, which can
     * then be used by other components of the application.
     * </p>
     * 
     * @return A {@link Model} object containing the credentials.
     * @throws Exception If an error occurs while reading the credentials.
     */
    public Model read() throws Exception;

    /**
     * Returns the type of credentials represented by this implementation.
     * 
     * <p>
     * This method identifies the type of credentials (e.g., SSH or HTTPS) using
     * the {@link CredentialsType} enum.
     * </p>
     * 
     * @return The {@link CredentialsType} of the credentials.
     */
    public CredentialsType getType();
}