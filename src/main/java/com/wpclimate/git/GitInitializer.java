package com.wpclimate.git;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.git.core.Dependency;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.credentials.https.HttpsCredentials;
import com.wpclimate.git.credentials.ssh.SshCredentials;
import com.wpclimate.git.exceptions.ConfigurationMissing;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.Shell;

/**
 * The {@code GitInitializer} class provides functionality to initialize the {@link Git}
 * class with either HTTPS or SSH credentials based on user input.
 * 
 * <p>
 * This class prompts the user to choose the type of credentials and configures the
 * {@link Git} instance accordingly.
 * </p>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * GitInitializer initializer = new GitInitializer();
 * initializer.initializeGit("/path/to/working/directory");
 * </pre>
 */
public class GitInitializer 
{

    /**
     * Initializes the {@link Git} class with user-selected credentials (HTTPS or SSH).
     * 
     * @param workingDirectory The working directory for the Git instance.
     */
    public void initializeGit(String workingDirectory) 
    {
        Scanner scanner = new Scanner(System.in);

        try 
        {
            System.out.println("Choose the type of credentials to use:");
            System.out.println("1. HTTPS");
            System.out.println("2. SSH");
            System.out.print("Enter your choice (1 or 2): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // TODO: Inserire una funzione che controlli che non ci siamo già credenziali

            Credential credential;
            GitContext context = this.createGitContext(workingDirectory);

            

            if (choice == 1) 
            {
                credential = new HttpsCredentials(context);
                this.configureHttpsCredentials((HttpsCredentials) credential, scanner);
            } 
            else if (choice == 2) 
            {
                credential = new SshCredentials(context);
                this.configureSshCredentials((SshCredentials) credential, scanner);
            } 
            else 
            {
                System.out.println("Invalid choice. Exiting.");
                return;
            }

            System.out.println("Configured credentials:");
            System.out.println(credential.read().toString());

        } 
        catch (ConfigurationMissing e) 
        {
            System.err.println("Configuration error: " + e.getMessage());
        } 
        catch (IOException e) 
        {
            System.err.println("I/O error occurred: " + e.getMessage());
        } 
        catch (Exception e) 
        {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        } 
        finally 
        {
            scanner.close();
        }
    }

    /**
     * Creates a {@link GitContext} instance for the specified working directory.
     * 
     * @param workingDirectory The working directory for the Git instance.
     * @return A {@link GitContext} instance.
     * @throws Exception If an error occurs during context creation.
     */
    private GitContext createGitContext(String workingDirectory) throws Exception 
    {
        try 
        {
            Settings settings = new Settings(workingDirectory);
            Shell shell = new Command(settings.getWorkingDirectory().getAbsolutePath());
            Dependency dependency = new Dependency(shell);
            Configurator configurator = new com.wpclimate.configurator.Configuration();
            return new GitContext(shell, settings, dependency, configurator);
        } 
        catch (Exception e) 
        {
            throw new Exception("Failed to create Git context: " + e.getMessage(), e);
        }
    }

    /**
     * Configures HTTPS credentials by prompting the user for input.
     * 
     * @param httpsCredentials The {@link HttpsCredentials} instance to configure.
     * @param scanner The {@link Scanner} instance for user input.
     * @throws ConfigurationMissing If the configuration is invalid.
     * @throws IOException If an error occurs while saving the configuration.
     */
    private void configureHttpsCredentials(HttpsCredentials httpsCredentials, Scanner scanner) throws ConfigurationMissing, IOException 
    {
        try 
        {
            System.out.print("Enter the repository name: ");
            String repoName = scanner.nextLine();

            System.out.print("Enter the repository URL: ");
            String repoUrl = scanner.nextLine();

            System.out.print("Enter the username: ");
            String username = scanner.nextLine();

            System.out.print("Enter the password: ");
            String password = scanner.nextLine();

            httpsCredentials.configure(Map.of(
                "name", repoName,
                "url", repoUrl,
                "username", username,
                "password", password
            ));
        } 
        catch (IllegalArgumentException e) 
        {
            throw new ConfigurationMissing("Invalid HTTPS configuration: " + e.getMessage());
        }
    }

    /**
     * Configures SSH credentials by prompting the user for input.
     * 
     * @param sshCredentials The {@link SshCredentials} instance to configure.
     * @param scanner The {@link Scanner} instance for user input.
     * @throws ConfigurationMissing If the configuration is invalid.
     * @throws IOException If an error occurs while saving the configuration.
     */
    private void configureSshCredentials(SshCredentials sshCredentials, Scanner scanner) throws ConfigurationMissing, IOException {
        try 
        {
            System.out.print("Enter the repository name: ");
            String repoName = scanner.nextLine();

            System.out.print("Enter the repository URL: ");
            String repoUrl = scanner.nextLine();

            System.out.print("Enter the path to the public key: ");
            String pubPath = scanner.nextLine();

            System.out.print("Enter the path to the private key: ");
            String privPath = scanner.nextLine();

            sshCredentials.configure(Map.of(
                "name", repoName,
                "url", repoUrl,
                "pubPath", pubPath,
                "privPath", privPath
            ));
        } 
        catch (IllegalArgumentException e) 
        {
            throw new ConfigurationMissing("Invalid SSH configuration: " + e.getMessage());
        }
    }
}