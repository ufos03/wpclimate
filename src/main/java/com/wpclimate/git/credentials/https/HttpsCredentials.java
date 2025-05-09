package com.wpclimate.git.credentials.https;

import java.io.IOException;
import java.util.Map;

import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.SettingsUtils.SettingsFilesNames;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.configurator.exceptions.NoModelProvided;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.credentials.CredentialsType;
import com.wpclimate.git.exceptions.ConfigurationMissing;

/**
 * The {@code HttpsCredentials} class implements the {@link Credential} interface and
 * provides functionality for managing HTTPS-based Git credentials.
 * 
 * <p>
 * This class is optimized for caching and includes methods for updating both the cached
 * model and the configuration file on disk.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Configure HTTPS credentials using a key-value map.</li>
 *   <li>Save HTTPS credentials to a configuration file.</li>
 *   <li>Read HTTPS credentials from a configuration file with caching.</li>
 *   <li>Update the cached model and the configuration file.</li>
 *   <li>Check if credentials configuration already exists.</li>
 *   <li>Identify the type of credentials as HTTPS.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Configurator configurator = new Configurator();
 * Settings settings = new Settings();
 * HttpsCredentials credentials = new HttpsCredentials(configurator, settings);
 * 
 * // Configure credentials
 * Map<String, String> config = Map.of(
 *     "url", "https://github.com/user/repo.git",
 *     "username", "myuser",
 *     "password", "mypassword"
 * );
 * credentials.configure(config);
 * 
 * // Generate a Git command
 * String gitCommand = credentials.getGitCommand("clone");
 * System.out.println(gitCommand);
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If multiple threads need to access the same credentials,
 * external synchronization is required.
 * </p>
 * 
 * @see Credential
 * @see HttpsCredentialModel
 * @see Configurator
 * @see ConfigurationMissing
 */
public class HttpsCredentials implements Credential 
{

    private final Configurator configurator;
    private final Settings settings;
    private HttpsCredentialModel httpsModel;
    private final String pathModel;

    /**
     * Constructs an {@code HttpsCredentials} instance with the specified 
     * {@link Configurator} and {@link Settings}.
     * 
     * @param configurator The {@link Configurator} instance used for saving and loading configurations.
     * @param settings The {@link Settings} instance used for retrieving file paths.
     */
    public HttpsCredentials(Configurator configurator, Settings settings) 
    {
        this.configurator = configurator;
        this.settings = settings;
        this.pathModel = this.settings.getSetting(SettingsFilesNames.GIT_CONF_FILE_NAME);
        this.httpsModel = new HttpsCredentialModel();
    }

    /**
     * Configures the HTTPS credentials using the provided key-value map.
     * 
     * <p>
     * This method populates the {@link HttpsCredentialModel} with the provided configuration
     * data and saves it to the configuration file. The cached model is updated to reflect
     * the new configuration.
     * </p>
     * 
     * @param configuration A map containing the HTTPS credential data.
     * @throws ConfigurationMissing If the configuration map is empty.
     * @throws IOException If an error occurs while saving the configuration.
     */
    @Override
    public void configure(Map<String, String> configuration) throws ConfigurationMissing, IOException 
    {
        if (configuration.isEmpty())
            throw new ConfigurationMissing("The HTTPS configuration is empty.");

        HttpsCredentialModel newModel = new HttpsCredentialModel();

        if (configuration.containsKey("name"))
            newModel.setRepoName(configuration.get("name"));

        if (configuration.containsKey("url"))
        {
            this.extractAndRemoveCredentials(configuration.get("url"));
            newModel.setRepoUrl(configuration.get("url"));
        }

        if (configuration.containsKey("username"))
            newModel.setUsername(configuration.get("username"));

        if (configuration.containsKey("password"))
            newModel.setPsw(configuration.get("password"));

        newModel.setCredentialType();

        this.configurator.save(this.pathModel, newModel);

        // Update cached model
        this.httpsModel = newModel;
    }

    /**
     * Updates the cached HTTPS credential model and the configuration file on disk.
     * 
     * <p>
     * This method allows updating specific fields of the cached model and ensures that
     * the changes are persisted to the configuration file.
     * </p>
     * 
     * @param updates A map containing the fields to update and their new values.
     * @throws IOException If an error occurs while saving the updated configuration.
     */
    @Override
    public void update(Map<String, String> updates) throws IOException 
    {
        if (updates.isEmpty())
            return; // No updates to apply

        if (this.httpsModel == null || !this.httpsModel.isValid())
            throw new IllegalStateException("Cannot update credentials: No valid cached model found.");

        // Update the cached model
        if (updates.containsKey("name"))
            this.httpsModel.setRepoName(updates.get("name"));

        if (updates.containsKey("url"))
            this.httpsModel.setRepoUrl(updates.get("url"));

        if (updates.containsKey("username"))
            this.httpsModel.setUsername(updates.get("username"));

        if (updates.containsKey("password"))
            this.httpsModel.setPsw(updates.get("password"));

        // Save the updated model to disk
        this.configurator.save(this.pathModel, this.httpsModel);
    }

    /**
     * Returns the type of credentials managed by this class.
     * 
     * @return The {@link CredentialsType#HTTPS} constant.
     */
    @Override
    public CredentialsType getType() 
    {
        return CredentialsType.HTTPS;
    }

    /**
     * Reads the HTTPS credentials from the configuration file or cache.
     * 
     * <p>
     * This method returns the HTTPS credential data, using the cached model if it's valid
     * or reading from the configuration file if necessary.
     * </p>
     * 
     * @return An {@link HttpsCredentialModel} instance containing the HTTPS credential data.
     * @throws NoModelProvided If the configuration file is missing or invalid.
     * @throws IOException If an error occurs while reading the configuration.
     */
    @Override
    public HttpsCredentialModel read() throws NoModelProvided, IOException 
    {
        if (!this.httpsModel.isValid())
            this.httpsModel = HttpsCredentialModel.fromModel(this.configurator.read(this.pathModel));

        return this.httpsModel;
    }

    /**
     * Checks if the HTTPS credentials configuration exists.
     * 
     * <p>
     * This method attempts to read the HTTPS credential data from the configuration file
     * to determine if valid credentials already exist. Unlike the {@link #read()} method,
     * this method handles exceptions internally, making it safer to use when you only
     * need to check for existence without disrupting program flow.
     * </p>
     * 
     * @return {@code true} if the HTTPS credentials configuration exists and can be loaded;
     *         {@code false} otherwise.
     */
    @Override
    public boolean exists() 
    {
        try 
        {
            this.httpsModel = HttpsCredentialModel.fromModel(this.configurator.read(this.pathModel));
            if (!this.httpsModel.isValid())
            {
                this.httpsModel = null;
                return false;
            }
            
            return true;
        } 
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Extracts credentials from a repository URL if they exist and removes them from the URL.
     * 
     * <p>
     * This method checks if the provided URL contains embedded credentials (e.g., "https://username:password@repo.url").
     * If credentials are found, they are removed from the URL, and the cleaned URL is returned.
     * The extracted credentials are returned as a string in the format "username:password".
     * </p>
     * 
     * @param repoUrl The repository URL to check for embedded credentials.
     * @return A string containing the extracted credentials in the format "username:password",
     *         or {@code null} if no credentials are found.
     */
    private String extractAndRemoveCredentials(String repoUrl) 
    {
        if (repoUrl == null || repoUrl.isEmpty())
            return null; // No URL provided

        // Check if the URL contains credentials (e.g., "https://username:password@repo.url")
        int atIndex = repoUrl.indexOf('@');
        if (atIndex == -1)
            return null;

        String credentialsPart = repoUrl.substring(repoUrl.indexOf("://") + 3, atIndex);

        // Remove the credentials from the URL
        String cleanedUrl = repoUrl.substring(0, repoUrl.indexOf("://") + 3) + repoUrl.substring(atIndex + 1);

        System.out.println("Cleaned URL: " + cleanedUrl);

        return credentialsPart; // Return the extracted credentials
    }

    /**
     * Generates a Git command string with embedded HTTPS credentials.
     * 
     * <p>
     * This method constructs a Git repository URL that includes the username and password
     * for authentication. The URL is built using the cached {@link HttpsCredentialModel},
     * which must contain valid credentials. The resulting URL can be used to execute Git
     * commands that require authentication.
     * </p>
     * 
     * <p>
     * The format of the returned URL is as follows:
     * <ul>
     *   <li>{@code https://username:password@repository.url}</li>
     * </ul>
     * </p>
     * 
     * <p>
     * For security reasons, the returned URL should be handled carefully to avoid exposing
     * sensitive information in logs or displays.
     * </p>
     * 
     * <h3>Example:</h3>
     * <pre>
     * HttpsCredentials credentials = new HttpsCredentials(configurator, settings);
     * String gitCommand = credentials.getGitCommand("clone");
     * System.out.println(gitCommand);
     * // Output: git clone -q --progress https://username:password@github.com/user/repo.git
     * </pre>
     * 
     * @param operation The Git operation to perform (e.g., "clone", "pull").
     * @return A Git command string with embedded credentials for authentication.
     * @throws ConfigurationMissing If the credential model is invalid or the URL does not contain "https://".
     */
    @Override
    public String getGitCommand(String operation) throws ConfigurationMissing 
    {
        StringBuilder url = new StringBuilder();
        StringBuilder username = new StringBuilder();
        StringBuilder password = new StringBuilder();
    
        // Verifica se il modello è valido
        if (!this.httpsModel.isValid())
            throw new ConfigurationMissing("The configuration readen is not valid!");
    
        // Ottieni i dati dal modello
        url.append(this.httpsModel.getRepoUrl());
        username.append(this.httpsModel.getUsername());
        password.append(this.httpsModel.getPsw());
    
        // Trova l'indice dopo "https://"
        int index = url.indexOf("https://");
        if (index == -1) {
            throw new ConfigurationMissing("The URL does not contain 'https://'.");
        }
        index += "https://".length();
    
        // Costruisci l'URL con le credenziali
        StringBuilder gitCommand = new StringBuilder();
        gitCommand.append("git " + operation + " -q --progress ");
        gitCommand.append("https://")
                  .append(username).append(":").append(password).append("@")
                  .append(url.substring(index));
        
        return gitCommand.toString();
    }

    /**
     * Returns the environment variables required for Git operations.
     * 
     * <p>
     * This method provides a map of environment variables that should be set when executing
     * Git commands. For HTTPS credentials, this includes variables like {@code GIT_FLUSH}.
     * </p>
     * 
     * @return A map of environment variables for Git operations.
     * @throws ConfigurationMissing If the credential model is invalid.
     */
    @Override
    public Map<String, String> getGitEnvironment() throws ConfigurationMissing 
    {
        if (this.httpsModel == null || !this.httpsModel.isValid())
            throw new ConfigurationMissing("SSH credentials are missing or invalid.");

        return Map.of("GIT_FLUSH", "'1'");
    }

    @Override
    public String toString() {
        return this.httpsModel.toString();
    }
}