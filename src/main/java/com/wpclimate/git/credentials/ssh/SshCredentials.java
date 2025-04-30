package com.wpclimate.git.credentials.ssh;

import java.io.IOException;
import java.util.Map;

import com.wpclimate.SettingsUtils.SettingsFilesNames;
import com.wpclimate.configurator.exceptions.NoModelProvided;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.credentials.CredentialsType;
import com.wpclimate.git.exceptions.ConfigurationMissing;

/**
 * The {@code SshCredentials} class implements the {@link Credential} interface and
 * provides functionality for managing SSH-based Git credentials.
 * 
 * <p>
 * This class is responsible for configuring, saving, and reading SSH credentials
 * required for accessing Git repositories. It uses the {@link SshCredentialModel}
 * to store credential data and interacts with the {@link GitContext} for configuration
 * persistence.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Configure SSH credentials using a key-value map.</li>
 *   <li>Save SSH credentials to a configuration file.</li>
 *   <li>Read SSH credentials from a configuration file.</li>
 *   <li>Identify the type of credentials as SSH.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * GitContext context = new GitContext(...);
 * SshCredentials sshCredentials = new SshCredentials(context);
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
 * @see GitContext
 */
public class SshCredentials implements Credential 
{

    private final GitContext context;
    private SshCredentialModel sshModel;
    private final String pathModel;

    /**
     * Constructs an {@code SshCredentials} instance with the specified {@link GitContext}.
     * 
     * <p>
     * This constructor initializes the SSH credential model and determines the path
     * to the configuration file using the {@link SettingsFilesNames#GIT_SSH_FILE_NAME}.
     * </p>
     * 
     * @param context The {@link GitContext} instance used for configuration management.
     */
    public SshCredentials(GitContext context) 
    {
        this.context = context;
        this.pathModel = this.context.getSettings().getSetting(SettingsFilesNames.GIT_CONF_FILE_NAME);
        this.sshModel = new SshCredentialModel();
    }

    /**
     * Configures the SSH credentials using the provided key-value map.
     * 
     * <p>
     * This method populates the {@link SshCredentialModel} with the provided configuration
     * data and saves it to the configuration file.
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
     * @throws IllegalArgumentException If any of the provided values are invalid.
     */
    @Override
    public void configure(Map<String, String> configuration) throws ConfigurationMissing, IOException, IllegalArgumentException 
    {
        if (configuration.size() == 0)
            throw new ConfigurationMissing("The SSH Configuration is empty.");

        if (configuration.containsKey("name"))
            this.sshModel.setRepoName(configuration.get("name"));

        if (configuration.containsKey("url"))
            this.sshModel.setRepoUrl(configuration.get("url"));

        if (configuration.containsKey("pubPath"))

        if (configuration.containsKey("privPath"))
            this.sshModel.setPrivateCertPath(configuration.get("privPath"));

        this.context.getConfigurator().save(this.pathModel, this.sshModel);

    }

    /**
     * Returns the type of credentials managed by this class.
     * 
     * <p>
     * This method identifies the credentials as SSH-based by returning
     * {@link CredentialsType#SSH}.
     * </p>
     * 
     * @return The {@link CredentialsType#SSH} constant.
     */
    @Override
    public CredentialsType getType() 
    {
        return CredentialsType.SSH;
    }

    /**
     * Reads the SSH credentials from the configuration file.
     * 
     * <p>
     * This method retrieves the SSH credential data from the configuration file
     * and populates an {@link SshCredentialModel} instance.
     * </p>
     * 
     * @return An {@link SshCredentialModel} instance containing the SSH credential data.
     * @throws Exception If an error occurs while reading the configuration.
     */
    @Override
    public SshCredentialModel read() throws NoModelProvided, IOException, IllegalArgumentException, ConfigurationMissing 
    {
        if (!this.sshModel.isValid())
            this.sshModel = SshCredentialModel.fromModel(this.context.getConfigurator().read(this.pathModel));
        
        return this.sshModel;
    }   

    @Override
    public boolean existsConfiguration()
    {
        try 
        {
            this.sshModel = SshCredentialModel.fromModel(this.context.getConfigurator().read(this.pathModel));
            return true;
        } 
        catch (Exception e) 
        {
            return false;
        }
    }
}