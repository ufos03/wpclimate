package com.wpclimate.git.credentials.ssh;

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
 * The {@code SshCredentials} class implements the {@link Credential} interface and
 * provides functionality for managing SSH-based Git credentials.
 * 
 * <p>
 * This class is optimized for caching and includes methods for updating both the cached
 * model and the configuration file on disk. It is designed to be integrated into the
 * {@link GitContext}.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Configure SSH credentials using a key-value map.</li>
 *   <li>Save SSH credentials to a configuration file.</li>
 *   <li>Read SSH credentials from a configuration file with caching.</li>
 *   <li>Update the cached model and the configuration file.</li>
 *   <li>Check if credentials configuration already exists.</li>
 *   <li>Identify the type of credentials as SSH.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Configurator configurator = new Configurator(...);
 * Settings settings = new Settings(...);
 * SshCredentials sshCredentials = new SshCredentials(configurator, settings);
 * 
 * // Configure credentials
 * sshCredentials.configure(Map.of(
 *     "name", "myRepo",
 *     "url", "git@github.com:myRepo.git",
 *     "pubPath", "/path/to/public/key",
 *     "privPath", "/path/to/private/key"
 * ));
 * 
 * // Read credentials
 * SshCredentialModel model = sshCredentials.read();
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
 * @see SshCredentialModel
 * @see Configurator
 */
public class SshCredentials implements Credential 
{

    private final Configurator configurator;
    private final Settings settings;
    private SshCredentialModel sshModel;
    private final String pathModel;

    /**
     * Constructs an {@code SshCredentials} instance with the specified {@link Configurator}
     * and {@link Settings}.
     * 
     * <p>
     * This constructor initializes the SSH credential model and determines the path
     * to the configuration file using the {@link SettingsFilesNames#GIT_CONF_FILE_NAME}.
     * </p>
     * 
     * @param configurator The {@link Configurator} instance used for saving and loading configurations.
     * @param settings The {@link Settings} instance used for retrieving file paths.
     */
    public SshCredentials(Configurator configurator, Settings settings) 
    {
        this.configurator = configurator;
        this.settings = settings;
        this.pathModel = this.settings.getSetting(SettingsFilesNames.GIT_CONF_FILE_NAME);
        this.sshModel = new SshCredentialModel();
    }

    /**
     * Configures the SSH credentials using the provided key-value map.
     * 
     * <p>
     * This method populates the {@link SshCredentialModel} with the provided configuration
     * data and saves it to the configuration file. The cached model is updated to reflect
     * the new configuration.
     * </p>
     * 
     * @param configuration A map containing the SSH credential data. The following keys
     *                      are supported:
     *                      <ul>
     *                        <li>{@code "name"} - The repository name.</li>
     *                        <li>{@code "url"} - The repository URL.</li>
     *                        <li>{@code "pubPath"} - The path to the public SSH key.</li>
     *                        <li>{@code "privPath"} - The path to the private SSH key.</li>
     *                      </ul>
     * @throws ConfigurationMissing If the configuration map is empty.
     * @throws IOException If an error occurs while saving the configuration.
     */
    @Override
    public void configure(Map<String, String> configuration) throws ConfigurationMissing, IOException 
    {
        if (configuration.isEmpty())
            throw new ConfigurationMissing("The SSH configuration is empty.");

        SshCredentialModel newModel = new SshCredentialModel();

        if (configuration.containsKey("name"))
            newModel.setRepoName(configuration.get("name"));

        if (configuration.containsKey("url"))
            newModel.setRepoUrl(configuration.get("url"));

        if (configuration.containsKey("pubPath"))
            newModel.setPublicCertPath(configuration.get("pubPath"));

        if (configuration.containsKey("privPath"))
            newModel.setPrivateCertPath(configuration.get("privPath"));

        newModel.setCredentialType();
        // Save to disk
        this.configurator.save(this.pathModel, newModel);

        // Update cached model
        this.sshModel = newModel;
    }

    /**
     * Updates the cached SSH credential model and the configuration file on disk.
     * 
     * <p>
     * This method allows updating specific fields of the cached model and ensures that
     * the changes are persisted to the configuration file.
     * </p>
     * 
     * @param updates A map containing the fields to update and their new values.
     * @throws IOException If an error occurs while saving the updated configuration.
     * @throws IllegalArgumentException If a key is not configured properly
     */
    @Override
    public void update(Map<String, String> updates) throws IOException, IllegalArgumentException 
    {
        if (updates.isEmpty())
            return; // No updates to apply

        if (this.sshModel == null || !this.sshModel.isValid())
            throw new IllegalStateException("Cannot update credentials: No valid cached model found.");

        // Update the cached model
        if (updates.containsKey("name"))
            this.sshModel.setRepoName(updates.get("name"));

        if (updates.containsKey("url"))
            this.sshModel.setRepoUrl(updates.get("url"));

        if (updates.containsKey("pubPath"))
            this.sshModel.setPublicCertPath(updates.get("pubPath"));

        if (updates.containsKey("privPath"))
            this.sshModel.setPrivateCertPath(updates.get("privPath"));

        // Save the updated model to disk
        this.configurator.save(this.pathModel, this.sshModel);
    }

    /**
     * Returns the type of credentials managed by this class.
     * 
     * @return The {@link CredentialsType#SSH} constant.
     */
    @Override
    public CredentialsType getType() 
    {
        return CredentialsType.SSH;
    }

    /**
     * Reads the SSH credentials from the configuration file or cache.
     * 
     * <p>
     * This method returns the SSH credential data, using the cached model if it's valid
     * or reading from the configuration file if necessary.
     * </p>
     * 
     * @return An {@link SshCredentialModel} instance containing the SSH credential data.
     * @throws NoModelProvided If the configuration file is missing or invalid.
     * @throws IOException If an error occurs while reading the configuration.
     */
    @Override
    public SshCredentialModel read() throws NoModelProvided, IOException 
    {
        System.out.println(settings.getWorkingDirectory().getAbsolutePath());
        System.out.println(this.pathModel);
        if (!this.sshModel.isValid())
            this.sshModel = SshCredentialModel.fromModel(this.configurator.read(this.pathModel));

        return this.sshModel;
    }

    /**
     * Checks if the SSH credentials configuration exists.
     * 
     * <p>
     * This method attempts to read the SSH credential data from the configuration file
     * to determine if valid credentials already exist. Unlike the {@link #read()} method,
     * this method handles exceptions internally, making it safer to use when you only
     * need to check for existence without disrupting program flow.
     * </p>
     * 
     * @return {@code true} if the SSH credentials configuration exists and can be loaded;
     *         {@code false} otherwise.
     */
    @Override
    public boolean exists() 
    {
        System.out.println(this.sshModel.getCredentialType());
        try 
        {
            this.sshModel = SshCredentialModel.fromModel(this.configurator.read(this.pathModel));
            System.out.println(this.sshModel.getCredentialType() != null && this.sshModel.getCredentialType() == CredentialsType.SSH.getType());
            if (this.sshModel.getCredentialType() == null && this.sshModel.getCredentialType() == CredentialsType.SSH.getType())
                return true;
            
            this.sshModel = null;
            return false;
        } 
        catch (Exception e)
        {
            return false;
        }
    }
}