package com.wpclimate.git.credentials.https;

import java.io.IOException;
import java.util.Map;

import com.wpclimate.SettingsUtils.SettingsFilesNames;
import com.wpclimate.configurator.exceptions.NoModelProvided;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.credentials.CredentialsType;
import com.wpclimate.git.exceptions.ConfigurationMissing;

/**
 * The {@code HttpsCredentials} class implements the {@link Credential} interface and
 * provides functionality for managing HTTPS-based Git credentials.
 * 
 * <p>
 * This class is responsible for configuring, saving, and reading HTTPS credentials
 * required for accessing Git repositories. It uses the {@link HttpsCredentialModel}
 * to store credential data and interacts with the {@link GitContext} for configuration
 * persistence.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Configure HTTPS credentials using a key-value map.</li>
 *   <li>Save HTTPS credentials to a configuration file.</li>
 *   <li>Read HTTPS credentials from a configuration file.</li>
 *   <li>Identify the type of credentials as HTTPS.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * GitContext context = new GitContext(...);
 * HttpsCredentials httpsCredentials = new HttpsCredentials(context);
 * 
 * // Configure credentials
 * httpsCredentials.configure(Map.of(
 *     "name", "myRepo",
 *     "url", "https://github.com/myRepo.git",
 *     "username", "myUsername",
 *     "password", "myPassword"
 * ));
 * 
 * // Read credentials
 * HttpsCredentialModel model = httpsCredentials.read();
 * System.out.println("Repository Name: " + model.getRepoName());
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If multiple threads need to access the same instance,
 * external synchronization is required.
 * </p>
 * 
 * @see Credential
 * @see HttpsCredentialModel
 * @see GitContext
 */
public class HttpsCredentials implements Credential 
{

    private final GitContext context;
    private HttpsCredentialModel httpsModel;
    private final String pathModel;

    /**
     * Constructs an {@code HttpsCredentials} instance with the specified {@link GitContext}.
     * 
     * <p>
     * This constructor initializes the HTTPS credential model and determines the path
     * to the configuration file using the {@link SettingsFilesNames#GIT_HTTPS_FILE_NAME}.
     * </p>
     * 
     * @param context The {@link GitContext} instance used for configuration management.
     */
    public HttpsCredentials(GitContext context) 
    {
        this.context = context;
        this.pathModel = this.context.getSettings().getSetting(SettingsFilesNames.GIT_CONF_FILE_NAME);
        this.httpsModel = new HttpsCredentialModel();
    }

    /**
     * Configures the HTTPS credentials using the provided key-value map.
     * 
     * <p>
     * This method populates the {@link HttpsCredentialModel} with the provided configuration
     * data and saves it to the configuration file.
     * </p>
     * 
     * @param configuration A map containing the HTTPS credential data. The following keys
     *                      are supported:
     *                      <ul>
     *                        <li>{@code "name"} - The repository name.</li>
     *                        <li>{@code "url"} - The repository URL.</li>
     *                        <li>{@code "username"} - The username for authentication.</li>
     *                        <li>{@code "password"} - The password for authentication.</li>
     *                      </ul>
     * @throws ConfigurationMissing If the configuration map is empty.
     * @throws IOException If an error occurs while saving the configuration.
     * @throws IllegalArgumentException If any of the provided values are invalid.
     */
    @Override
    public void configure(Map<String, String> configuration) throws ConfigurationMissing, IOException, IllegalArgumentException 
    {
        if (configuration.size() == 0)
            throw new ConfigurationMissing("The HTTPS configuration is empty.");

        if (configuration.containsKey("name"))
            this.httpsModel.setRepoName(configuration.get("name"));

        if (configuration.containsKey("url"))
            this.httpsModel.setRepoUrl(configuration.get("url"));

        if (configuration.containsKey("username"))
            this.httpsModel.setUsername(configuration.get("username"));

        if (configuration.containsKey("password"))
            this.httpsModel.setPsw(configuration.get("password"));

        this.context.getConfigurator().save(this.pathModel, this.httpsModel);
    }

    /**
     * Returns the type of credentials managed by this class.
     * 
     * <p>
     * This method identifies the credentials as HTTPS-based by returning
     * {@link CredentialsType#HTTPS}.
     * </p>
     * 
     * @return The {@link CredentialsType#HTTPS} constant.
     */
    @Override
    public CredentialsType getType() 
    {
        return CredentialsType.HTTPS;
    }

    /**
     * Reads the HTTPS credentials from the configuration file.
     * 
     * <p>
     * This method retrieves the HTTPS credential data from the configuration file
     * and populates an {@link HttpsCredentialModel} instance.
     * </p>
     * 
     * @return An {@link HttpsCredentialModel} instance containing the HTTPS credential data.
     * @throws NoModelProvided If the configuration file is missing or invalid.
     * @throws IOException If an error occurs while reading the configuration.
     * @throws IllegalArgumentException If the configuration data is invalid.
     */
    @Override
    public HttpsCredentialModel read() throws NoModelProvided, IOException, IllegalArgumentException 
    {
        if (!this.httpsModel.isValid())
            this.httpsModel = HttpsCredentialModel.fromModel(this.context.getConfigurator().read(this.pathModel));
    
        return this.httpsModel;
    }

    @Override
    public boolean existsConfiguration()
    {
        try 
        {
            HttpsCredentialModel.fromModel(this.context.getConfigurator().read(this.pathModel));
            return true;
        } 
        catch (Exception e) 
        {
            return false;
        }
    }
}