package com.wpclimate.cli.core;

import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.configurator.Configurator;
import com.wpclimate.constants.FileManager;
import com.wpclimate.shell.Shell;

/**
 * The {@code Context} class serves as the central container for managing and providing
 * access to the core components required by the WP-CLI application.
 * 
 * <p>
 * This class acts as a shared resource that encapsulates and organizes the following
 * key components of the application:
 * </p>
 * <ul>
 *   <li>{@link WpCliModel} - Represents the WP-CLI configuration model, including paths
 *       to the PHP executable, WP-CLI executable, and other configuration data.</li>
 *   <li>{@link Shell} - Provides an interface for executing shell commands, enabling
 *       interaction with the underlying system.</li>
 *   <li>{@link Configurator} - Manages the persistence and retrieval of configuration
 *       data, allowing the application to save and load settings.</li>
 *   <li>{@link FileManager} - Handles file operations, including managing the working
 *       directory and accessing configuration files.</li>
 * </ul>
 * 
 * <p>
 * By centralizing these components, the {@code Context} class simplifies their
 * management and ensures consistent access throughout the application. It also
 * provides thread-safe access to these components using a {@link ReentrantLock}.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Encapsulates the core components required by the application.</li>
 *   <li>Provides thread-safe access to the components using synchronized methods.</li>
 *   <li>Acts as a shared resource that can be passed to other classes and commands
 *       to ensure consistent access to the application's core functionality.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * The {@code Context} class is typically instantiated during the initialization phase
 * of the application and passed to other components or commands that require access
 * to the core functionality. For example:
 * </p>
 * <pre>
 * WpCliModel wpModel = ...; // Initialize WP-CLI configuration model
 * Shell shell = ...; // Initialize shell interface
 * Configurator configurator = ...; // Initialize configurator
 * FileManager fileManager = ...; // Initialize file manager
 * 
 * Context context = new Context(wpModel, shell, configurator, fileManager);
 * 
 * // Access components through the context
 * Shell shell = context.getShell();
 * FileManager fileManager = context.getFileManager();
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code Context} class ensures thread-safe access to its components by using
 * a {@link ReentrantLock} to synchronize access to its getter methods. This makes
 * it safe to use in multi-threaded environments where multiple threads may need
 * to access the same components concurrently.
 * </p>
 * 
 * @see WpCliModel
 * @see Shell
 * @see Configurator
 * @see FileManager
 */
public class WpCliContext 
{
    private final Shell shell; // The shell interface for executing commands
    private final Configurator configurator; // The configurator for managing configuration persistence
    private final WpCliModel wpModel; // The WP-CLI configuration model
    private final FileManager fileManager; // The file manager for handling file operations
    private final Dependency dependency; // The dependency to check is the below system has all the requirements for wp-cli.

    private final ReentrantLock lock = new ReentrantLock(); // Allows thread-safe access to components

    /**
     * Constructs a {@code Context} instance with the specified components.
     *
     * @param wpModel      The {@link WpCliModel} instance containing WP-CLI configuration data.
     * @param shell        The {@link Shell} instance for executing commands.
     * @param configurator The {@link Configurator} instance for managing configuration persistence.
     * @param fileManager  The {@link FileManager} instance for handling file operations.
     */
    public WpCliContext(WpCliModel wpModel, Shell shell, Configurator configurator, FileManager fileManager, Dependency dependency) 
    {
        this.wpModel = wpModel;
        this.shell = shell;
        this.configurator = configurator;
        this.fileManager = fileManager;
        this.dependency = dependency;
    }

    /**
     * Retrieves the WP-CLI configuration model.
     *
     * <p>
     * The {@link WpCliModel} contains configuration data such as the paths to the PHP
     * executable and WP-CLI executable, as well as other settings required by the application.
     * </p>
     *
     * @return The {@link WpCliModel} instance containing WP-CLI configuration data.
     */
    public WpCliModel getWpModel() 
    {
        this.lock.lock();
        try 
        {
            return this.wpModel;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Retrieves the shell interface for executing commands.
     *
     * <p>
     * The {@link Shell} provides methods for executing shell commands and interacting
     * with the underlying operating system.
     * </p>
     *
     * @return The {@link Shell} instance for executing commands.
     */
    public Shell getShell() 
    {
        this.lock.lock();
        try 
        {
            return this.shell;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Retrieves the configurator for managing configuration persistence.
     *
     * <p>
     * The {@link Configurator} is responsible for saving and loading configuration
     * data, allowing the application to persist settings across sessions.
     * </p>
     *
     * @return The {@link Configurator} instance for managing configuration persistence.
     */
    public Configurator getConfigurator() 
    {
        this.lock.lock();
        try 
        {
            return this.configurator;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Retrieves the file manager for handling file operations.
     *
     * <p>
     * The {@link FileManager} provides methods for managing files and directories,
     * including the working directory and configuration files.
     * </p>
     *
     * @return The {@link FileManager} instance for handling file operations.
     */
    public FileManager getFileManager() 
    {
        this.lock.lock();
        try 
        {
            return this.fileManager;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public Dependency getDependency()
    {
        this.lock.lock();
        try 
        {
            return this.dependency;
        }
        finally
        {
            this.lock.unlock();
        }
    }
}