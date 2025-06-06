package com.wpclimate.git.credentials;

import java.io.IOException;
import java.util.Map;

import com.wpclimate.configurator.exceptions.NoModelProvided;
import com.wpclimate.configurator.model.Model;
import com.wpclimate.git.exceptions.ConfigurationMissing;


/**
 * The {@code Credential} interface defines the contract for managing Git credentials.
 * 
 * <p>
 * Implementations of this interface are responsible for handling specific types of
 * credentials (e.g., SSH, HTTPS) required to access Git repositories. Each implementation
 * must provide methods to configure, read, and identify the type of credentials, as well
 * as generate Git commands and environment variables for authentication.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Configure the credentials using a set of key-value pairs.</li>
 *   <li>Read the credentials from a persistent storage or configuration source.</li>
 *   <li>Identify the type of credentials (e.g., SSH or HTTPS).</li>
 *   <li>Check if credentials are already configured in the system.</li>
 *   <li>Update specific fields of the credentials and persist the changes.</li>
 *   <li>Generate Git commands with embedded credentials for authentication.</li>
 *   <li>Provide environment variables required for Git operations.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Credential credential = new HttpsCredentials();
 * 
 * // Check if credentials already exist
 * if (!credential.exists()) {
 *     credential.configure(Map.of("username", "user", "password", "pass"));
 * }
 * 
 * Model model = credential.read();
 * System.out.println("Credential Type: " + credential.getType());
 * 
 * // Update credentials
 * credential.update(Map.of("password", "newPassword"));
 * 
 * // Generate a Git command
 * String gitCommand = credential.getGitCommand("clone");
 * System.out.println(gitCommand);
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This interface does not enforce thread safety. Implementations should document whether
 * they are thread-safe or require external synchronization.
 * </p>
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
     * @throws ConfigurationMissing If the configuration is invalid or missing required fields.
     * @throws IOException If an error occurs during the configuration process.
     */
    public void configure(Map<String, String> configuration) throws ConfigurationMissing, IOException;

    /**
     * Reads the credentials from a persistent storage or configuration source.
     * 
     * <p>
     * This method retrieves the credentials as a {@link Model} object, which can
     * then be used by other components of the application.
     * </p>
     * 
     * @return A {@link Model} object containing the credentials.
     * @throws NoModelProvided If the configuration file is missing or invalid.
     * @throws IOException If an error occurs while reading the credentials.
     */
    public Model read() throws NoModelProvided, IOException;

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

    /**
     * Checks if credentials are already configured in the system.
     * 
     * <p>
     * This method attempts to read the credentials from the configuration source
     * without throwing exceptions if the configuration doesn't exist. It's a safer
     * alternative to {@link #read()} when you just want to check for existence.
     * </p>
     * 
     * <p>
     * Implementations may optimize by loading the credentials into memory when they exist,
     * making subsequent calls to {@link #read()} more efficient by avoiding additional
     * reads from the configuration source.
     * </p>
     * 
     * @return {@code true} if credentials are already configured; {@code false} otherwise.
     */
    public boolean exists();

    /**
     * Updates specific fields of the credentials and persists the changes.
     * 
     * <p>
     * This method allows modifying specific fields of the credentials without requiring
     * a full reconfiguration. The changes are applied to the cached model and saved to
     * the persistent storage.
     * </p>
     * 
     * @param updates A map containing the fields to update and their new values.
     * @throws IOException If an error occurs while saving the updated configuration.
     * @throws IllegalArgumentException If any of the provided keys or values are invalid.
     */
    public void update(Map<String, String> updates) throws IOException, IllegalArgumentException;

    /**
     * Returns a Git command string with properly embedded credentials.
     * 
     * <p>
     * This method generates a command string that can be used to authenticate with Git repositories
     * using the configured credentials. The format of the returned string depends on the specific
     * credential type implementation (e.g., HTTPS URL with embedded username/password or SSH command
     * with identity file).
     * </p>
     * 
     * <p>
     * The method always includes the base Git command with the specified operation, and appends any
     * additional parameters provided. Implementations should ensure that credential information is
     * properly embedded in the command or set through environment variables.
     * </p>
     * 
     * <p>
     * For security reasons, the returned command string should be treated carefully to avoid
     * exposing sensitive credential information in logs or displays.
     * </p>
     * 
     * <h3>Example usage:</h3>
     * <pre>
     * // Simple clone command
     * String cloneCmd = credential.getGitCommand("clone");
     * 
     * // Clone with additional parameters
     * String cloneWithParams = credential.getGitCommand("clone", "--depth=1", "--single-branch");
     * 
     * // Push to specific remote and branch
     * String pushCmd = credential.getGitCommand("push", "origin", "main", "--force");
     * </pre>
     * 
     * <h3>Example output formats:</h3>
     * <ul>
     *   <li>HTTPS: {@code git clone --progress https://username:password@github.com/user/repo.git}</li>
     *   <li>SSH: {@code git clone --progress git@github.com:user/repo.git}</li>
     * </ul>
     * 
     * @param operation The Git operation to perform (e.g., "clone", "pull", "push").
     * @param parameters Optional additional parameters to include in the Git command
     *                  (e.g., "--depth=1", "origin", "main", "--force").
     * @return A Git command string with embedded credentials for authentication.
     * @throws ConfigurationMissing If the required credential configuration is missing or invalid.
     */
    public String getGitCommand(String operation, String ...parameters) throws ConfigurationMissing;

    /**
     * Returns the environment variables required for Git operations.
     * 
     * <p>
     * This method provides a map of environment variables that should be set when executing
     * Git commands. For example, SSH credentials may require the {@code GIT_SSH_COMMAND}
     * variable to be set.
     * </p>
     * 
     * @return A map of environment variables for Git operations.
     * @throws ConfigurationMissing If the credential model is invalid.
     */
    public Map<String, String> getGitEnvironment() throws ConfigurationMissing;
}