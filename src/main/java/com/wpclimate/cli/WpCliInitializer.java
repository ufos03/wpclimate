package com.wpclimate.cli;

import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.resourcer.ResourceManager;
import com.wpclimate.resourcer.ResourceType;
import com.wpclimate.configurator.Configuration;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.RealTimeConsoleSpoofer;
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
 *   <li>{@link ResourceManager} - Manages file operations and the working directory.</li>
 *   <li>{@link Configurator} - Handles the persistence and retrieval of configuration data.</li>
 *   <li>{@link WpCliModel} - Represents the WP-CLI configuration model, including paths
 *       to the PHP executable, WP-CLI executable, and MySQL executable.</li>
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
 *   <li>Initializes the {@link ResourceManager} with the specified working directory.</li>
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
 * ResourceManager resourceManager = initializer.loadResources("/path/to/working/directory");
 * Configurator configurator = initializer.initializeConfigurator(resourceManager);
 * WpCliModel wpCliModel = initializer.initializeModel(resourceManager, configurator);
 * Shell shell = initializer.initializeShell(resourceManager, new RealTimeConsoleSpoofer());
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe and should be used in a single-threaded context during
 * the initialization phase of the application.
 * </p>
 *
 * @see ResourceManager
 * @see Configurator
 * @see WpCliModel
 * @see Shell
 */
public class WpCliInitializer {

    /**
     * Initializes the {@link ResourceManager} with the specified working directory.
     *
     * <p>
     * The {@link ResourceManager} is responsible for managing file operations, including
     * accessing the working directory and configuration files. If the specified working
     * directory is {@code null} or empty, the default working directory is used.
     * </p>
     *
     * @param workingDirectory The working directory for the application.
     * @return An instance of {@link ResourceManager}.
     */
    public ResourceManager loadResources(String workingDirectory) {
        ResourceManager manager = ResourceManager.getInstance();

        if (workingDirectory == null || workingDirectory.isEmpty()) {
            return manager;
        }

        manager.setWorkingDirectory(workingDirectory);
        return manager;
    }

    /**
     * Initializes the {@link Configurator} using the {@link ResourceManager}.
     *
     * <p>
     * The {@link Configurator} is responsible for saving and loading configuration data.
     * This method uses the {@link ResourceManager} to determine the path to the configuration
     * file and creates a {@link Configuration} instance for managing the configuration.
     * </p>
     *
     * @param resource The {@link ResourceManager} instance.
     * @return An instance of {@link Configurator}.
     */
    public Configurator initializeConfigurator(ResourceManager resource) {
        return new Configuration();
    }

    /**
     * Initializes the {@link WpCliModel} by reading the configuration or prompting the user.
     *
     * <p>
     * The {@link WpCliModel} represents the WP-CLI configuration, including paths to the
     * PHP executable, WP-CLI executable, and MySQL executable. This method attempts to
     * read the configuration from the {@link Configurator}. If the configuration file is
     * not found or cannot be read, the user is prompted to provide the required configuration
     * parameters.
     * </p>
     *
     * @param resource      The {@link ResourceManager} instance.
     * @param configurator  The {@link Configurator} instance.
     * @return An instance of {@link WpCliModel}.
     */
    public WpCliModel initializeModel(ResourceManager resource, Configurator configurator) {
        WpCliModel model = new WpCliModel();

        try {
            model.setFromModel(configurator.read(resource.getFile(ResourceType.WPCLI_CONFIG).toString()));
        } catch (Exception e) {
            this.promptForConfiguration(resource, model, configurator);
        }

        return model;
    }

    /**
     * Prompts the user to provide configuration parameters and saves them.
     *
     * <p>
     * If the configuration file is not found or cannot be read, this method prompts
     * the user to provide the paths to the PHP executable, WP-CLI executable, and
     * MySQL executable. The provided configuration is then saved using the
     * {@link Configurator}.
     * </p>
     *
     * @param resource      The {@link ResourceManager} instance.
     * @param model         The {@link WpCliModel} instance to populate.
     * @param configurator  The {@link Configurator} instance to save the configuration.
     */
    private void promptForConfiguration(ResourceManager resource, WpCliModel model, Configurator configurator) {
        System.out.println("Configuration file not found. Please set the required parameters.");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the path to the PHP executable: ");
        String phpPath = scanner.nextLine();

        System.out.print("Enter the path to the WP-CLI executable: ");
        String wpCliPath = scanner.nextLine();

        System.out.print("Enter the path to the MySQL executable: ");
        String sqlPath = scanner.nextLine();

        model.setPhp(phpPath);
        model.setWp(wpCliPath);
        model.setMYSQL(sqlPath);

        try {
            configurator.save(resource.getFile(ResourceType.WPCLI_CONFIG).toString(), model);
            System.out.println("Configuration saved successfully.");
        } catch (Exception saveException) {
            System.err.println("Failed to save configuration: " + saveException.getMessage());
        }
    }

    /**
     * Initializes the {@link Shell} for executing commands.
     *
     * <p>
     * The {@link Shell} provides an interface for executing shell commands. This method
     * creates a {@link Command} instance using the working directory provided by the
     * {@link ResourceManager}.
     * </p>
     *
     * @param manager    The {@link ResourceManager} instance.
     * @param interactor The {@link RealTimeConsoleSpoofer} instance for handling console interactions.
     * @return An instance of {@link Shell}.
     */
    public Shell initializeShell(ResourceManager manager, RealTimeConsoleSpoofer interactor) {
        return new Command(manager.getWorkingDirectory().toString(), interactor);
    }
}