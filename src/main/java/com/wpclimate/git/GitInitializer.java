package com.wpclimate.git;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.configurator.Configuration;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.configurator.exceptions.NoModelProvided;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.RealTimeConsoleSpoofer;
import com.wpclimate.shell.Shell;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.credentials.https.HttpsCredentials;
import com.wpclimate.git.credentials.ssh.SshCredentials;
import com.wpclimate.git.exceptions.ConfigurationMissing;


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
     * Initializes the {@link Settings} with the specified working directory.
     *
     * <p>
     * The {@link Settings} is responsible for managing file operations, including
     * accessing the working directory and configuration files. If the specified working
     * directory is {@code null} or empty, the default working directory is used.
     * </p>
     *
     * @param workingDirectory The working directory for the application.
     * @return An instance of {@link Settings}.
     */
    public Settings loadSettings(String workingDirectory) 
    {
        if (workingDirectory == null || workingDirectory.isEmpty())
            return new Settings();

        return new Settings(workingDirectory);
    }

    /**
     * Initializes the {@link Configurator} using the {@link Settings}.
     *
     * <p>
     * The {@link Configurator} is responsible for saving and loading configuration data.
     * This method uses the {@link Settings} to determine the path to the configuration
     * file and creates a {@link Configuration} instance for managing the configuration.
     * </p>
     *
     * @param settings The {@link Settings} instance.
     * @return An instance of {@link Configurator}.
     */
    public Configurator initializeConfigurator(Settings settings) 
    {
        return new Configuration();
    }

    /**
     * Initializes the {@link Shell} for executing commands.
     *
     * <p>
     * The {@link Shell} provides an interface for executing shell commands. This method
     * creates a {@link Command} instance using the working directory provided by the
     * {@link Settings}.
     * </p>
     *
     * @param settings The {@link Settings} instance.
     * @return An instance of {@link Shell}.
     */
    public Shell initializeShell(Settings settings, RealTimeConsoleSpoofer interactor) 
    {
        return new Command(settings.getWorkingDirectory().getAbsolutePath(), interactor);
    }

    /**
     * Initializes the credentials for Git operations by prompting the user to choose
     * between HTTPS and SSH credentials.
     * 
     * <p>
     * This method interacts with the user via the console to determine the type of
     * credentials to configure. Based on the user's choice, it creates an instance of
     * either {@link HttpsCredentials} or {@link SshCredentials}, prompts the user for
     * the necessary input, and configures the credentials. If the user does not provide
     * a valid choice, HTTPS credentials are selected by default.
     * </p>
     * 
     * <p>
     * The method also handles exceptions that may occur during the configuration process,
     * such as missing configuration or I/O errors, and prints appropriate error messages
     * to the console.
     * </p>
     * 
     * @param settings The {@link Settings} instance used to manage file paths and operations.
     * @param configurator The {@link Configurator} instance used to save and load configurations.
     * @return A {@link Credential} instance representing the configured credentials, or {@code null}
     *         if no credentials were configured.
     * 
     * @throws ConfigurationMissing If the configuration is invalid.
     * @throws IOException If an error occurs while saving the configuration.
     * @throws NoModelProvided If the configuration file is missing or invalid.
     */
    public Credential initializeCredentials(Settings settings, Configurator configurator)
    {
        Credential httpsCredentials = new HttpsCredentials(configurator, settings);
        Credential sshCredentials = new SshCredentials(configurator, settings);

        Credential credentialsToUse = null; // Messo solo perche' rompe le balle


        if (sshCredentials.exists()) 
        {
            System.out.println("SSH credentials found and loaded.");
            return sshCredentials;
        } 
        else if (httpsCredentials.exists()) {
            System.out.println("HTTPS credentials found and loaded.");
            return httpsCredentials;
        }

            System.out.println("Credentials not founded! Proceed to create credentials");
            Credential credentialToCreate = null;
            Scanner scanner = new Scanner(System.in);
    
        try 
        {
            System.out.println("Choose the type of credentials to use:");
            System.out.println("1. HTTPS (default)");
            System.out.println("2. SSH");
            System.out.print("Enter your choice (1 or 2): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character


            if (choice == 1) 
            {
                credentialToCreate = new HttpsCredentials(configurator, settings);
                this.configureHttpsCredentials((HttpsCredentials) credentialToCreate, scanner);
            } 
            else if (choice == 2) 
            {
                credentialToCreate = new SshCredentials(configurator, settings);
                this.configureSshCredentials((SshCredentials) credentialToCreate, scanner);
            } 
            else if (choice != 1 && choice != 2)
            {
                credentialToCreate = new HttpsCredentials(configurator, settings);
                this.configureHttpsCredentials((HttpsCredentials) credentialToCreate, scanner);
            }

            System.out.println("Configured credentials:");
            System.out.println(credentialToCreate != null ? credentialToCreate.read().toString() : "No credentials configured.");

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
            System.err.println("An unexpect ed error occurred: " + e.getMessage());
        } 
        finally 
        {
            scanner.close();
        }    

        return credentialsToUse;
       
    }

    /** 
     * Configures HTTPS credentials by prompting the user for input.
     * 
     * @param httpsCredentials The {@link HttpsCredentials} instance to configure.
     * @param scanner The {@link Scanner} instance for user input.
     * @throws ConfigurationMissing If the configuration is invalid.
     * @throws IOException If an error occurs while saving the configuration.
     */
    private void configureHttpsCredentials(HttpsCredentials httpsCredentials, Scanner scanner) throws ConfigurationMissing, IOException, NoModelProvided
    {   
        if (httpsCredentials.exists())
            return;
        
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
    private void configureSshCredentials(SshCredentials sshCredentials, Scanner scanner) throws ConfigurationMissing, IOException 
    {
        if (sshCredentials.exists())
            return;

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