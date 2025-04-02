package com.wpclimate.cli;

import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.configurator.Configuration;
import com.wpclimate.constants.FileManager;
import com.wpclimate.constants.FileName;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.Shell;

import java.util.Scanner;

/**
 * The {@code WpCliInitializer} class is responsible for initializing the core components
 * required by the WP-CLI application.
 * 
 * <p>
 * This class centralizes the initialization logic for the following components:
 * </p>
 * <ul>
 *   <li>{@link FileManager} - Manages file operations and the working directory.</li>
 *   <li>{@link Configurator} - Handles the persistence and retrieval of configuration data.</li>
 *   <li>{@link WpCliModel} - Represents the WP-CLI configuration model, including paths
 *       to the PHP executable and WP-CLI executable.</li>
 *   <li>{@link Shell} - Provides an interface for executing shell commands.</li>
 * </ul>
 * 
 * <p>
 * By encapsulating the initialization logic, this class simplifies the setup process
 * for the WP-CLI application and ensures that all components are properly configured
 * before use.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Initializes the {@link FileManager} with the specified working directory.</li>
 *   <li>Initializes the {@link Configurator} for managing configuration persistence.</li>
 *   <li>Initializes the {@link WpCliModel} by reading the configuration or prompting the user.</li>
 *   <li>Initializes the {@link Shell} for executing commands in the specified working directory.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * The {@code WpCliInitializer} class is typically used during the startup phase of the
 * application to set up the required components. For example:
 * </p>
 * <pre>
 * WpCliInitializer initializer = new WpCliInitializer();
 * 
 * FileManager fileManager = initializer.initializeFileManager("/path/to/working/directory");
 * Configurator configurator = initializer.initializeConfigurator(fileManager);
 * WpCliModel wpCliModel = initializer.initializeModel(configurator);
 * Shell shell = initializer.initializeShell(fileManager);
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe and should be used in a single-threaded context during
 * the initialization phase of the application.
 * </p>
 * 
 * @see FileManager
 * @see Configurator
 * @see WpCliModel
 * @see Shell
 */
public class WpCliInitializer 
{

    /**
     * Initializes the {@link FileManager} with the specified working directory.
     *
     * <p>
     * The {@link FileManager} is responsible for managing file operations, including
     * accessing the working directory and configuration files. If the specified working
     * directory is {@code null} or empty, the default working directory is used.
     * </p>
     *
     * @param workingDirectory The working directory for the application.
     * @return An instance of {@link FileManager}.
     */
    public FileManager initializeFileManager(String workingDirectory) 
    {
        if (workingDirectory == null || workingDirectory.isEmpty())
            return new FileManager();
        return new FileManager(workingDirectory);
    }

    /**
     * Initializes the {@link Configurator} using the {@link FileManager}.
     *
     * <p>
     * The {@link Configurator} is responsible for saving and loading configuration data.
     * This method uses the {@link FileManager} to determine the path to the configuration
     * file and creates a {@link Configuration} instance for managing the configuration.
     * </p>
     *
     * @param fileManager The {@link FileManager} instance.
     * @return An instance of {@link Configurator}.
     */
    public Configurator initializeConfigurator(FileManager fileManager) 
    {
        String configFilePath = fileManager.getFilePath(FileName.WPCLI_FILE_NAME);
        return new Configuration(configFilePath);
    }

    /**
     * Initializes the {@link WpCliModel} by reading the configuration or prompting the user.
     *
     * <p>
     * The {@link WpCliModel} represents the WP-CLI configuration, including paths to the
     * PHP executable and WP-CLI executable. This method attempts to read the configuration
     * from the {@link Configurator}. If the configuration file is not found or cannot be
     * read, the user is prompted to provide the required configuration parameters.
     * </p>
     *
     * @param configurator The {@link Configurator} instance.
     * @return An instance of {@link WpCliModel}.
     */
    public WpCliModel initializeModel(Configurator configurator) 
    {
        WpCliModel model = new WpCliModel();

        try 
        {
            model.setFromModel(configurator.read());
        } 
        catch (Exception e) 
        {
            this.promptForConfiguration(model, configurator);
        }

        return model;
    }

    /**
     * Prompts the user to provide configuration parameters and saves them.
     *
     * <p>
     * If the configuration file is not found or cannot be read, this method prompts
     * the user to provide the paths to the PHP executable and WP-CLI executable.
     * The provided configuration is then saved using the {@link Configurator}.
     * </p>
     *
     * @param model        The {@link WpCliModel} instance to populate.
     * @param configurator The {@link Configurator} instance to save the configuration.
     */
    private void promptForConfiguration(WpCliModel model, Configurator configurator) 
    {
        System.out.println("Configuration file not found. Please set the required parameters.");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the path to the PHP executable: ");
        String phpPath = scanner.nextLine();

        System.out.print("Enter the path to the WP-CLI executable: ");
        String wpCliPath = scanner.nextLine();

        model.setPhp(phpPath);
        model.setWp(wpCliPath);

        try 
        {
            configurator.save(model);
            System.out.println("Configuration saved successfully.");
        } 
        catch (Exception saveException) 
        {
            System.err.println("Failed to save configuration: " + saveException.getMessage());
        } 
        finally 
        {
            scanner.close();
        }
    }

    /**
     * Initializes the {@link Shell} for executing commands.
     *
     * <p>
     * The {@link Shell} provides an interface for executing shell commands. This method
     * creates a {@link Command} instance using the working directory provided by the
     * {@link FileManager}.
     * </p>
     *
     * @param fileManager The {@link FileManager} instance.
     * @return An instance of {@link Shell}.
     */
    public Shell initializeShell(FileManager fileManager) 
    {
        return new Command(fileManager.getWorkingDirectory().getAbsolutePath());
    }
}