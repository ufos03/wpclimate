package com.wpclimate.cli.core;

import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.configurator.Configurator;
import com.wpclimate.resourcer.ResourceManager;
import com.wpclimate.shell.Shell;

/**
 * The {@code WpCliContext} class serves as the central container for managing and providing
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
 *   <li>{@link ResourceManager} - Handles resource management, including file operations 
 *       and working directory access.</li>
 *   <li>{@link Dependency} - Verifies system requirements for WP-CLI functionality.</li>
 * </ul>
 * 
 * <p>
 * By centralizing these components, the {@code WpCliContext} class simplifies their
 * management and ensures consistent access throughout the application. It also
 * provides thread-safe access to these components using a {@link ReentrantLock}.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Encapsulates the core components required by the application.</li>
 *   <li>Provides thread-safe access to the components using a lock-based mechanism.</li>
 *   <li>Acts as a shared resource that can be passed to commands and other objects
 *       to ensure consistent access to the application's core functionality.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * The {@code WpCliContext} class is typically instantiated during the initialization phase
 * of the application and passed to other components or commands that require access
 * to the core functionality. For example:
 * </p>
 * <pre>
 * WpCliModel wpModel = ...; // Initialize WP-CLI configuration model
 * Shell shell = ...; // Initialize shell interface
 * Configurator configurator = ...; // Initialize configurator
 * ResourceManager resourceManager = ...; // Initialize resource manager
 * Dependency dependency = ...; // Initialize dependency checker
 * 
 * WpCliContext context = new WpCliContext(wpModel, shell, configurator, resourceManager, dependency);
 * 
 * // Access components through the context
 * Shell shell = context.getShell();
 * ResourceManager resourceManager = context.getResourceManager();
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code WpCliContext} class ensures thread-safe access to its components by using
 * a {@link ReentrantLock} to synchronize access to its getter methods. This makes
 * it safe to use in multi-threaded environments where multiple threads may need
 * to access the same components concurrently.
 * </p>
 * 
 * @see WpCliModel
 * @see Shell
 * @see Configurator
 * @see ResourceManager
 * @see Dependency
 */
public class WpCliContext 
{
    private final Shell shell; // The shell interface for executing commands
    private final Configurator configurator; // The configurator for managing configuration persistence
    private final WpCliModel wpModel; // The WP-CLI configuration model
    private final ResourceManager manager; // The resource manager for handling resources and files
    private final Dependency dependency; // The dependency checker for WP-CLI requirements

    private final ReentrantLock lock = new ReentrantLock(); // Allows thread-safe access to components

    /**
     * Constructs a {@code WpCliContext} instance with the specified components.
     *
     * @param wpModel      The {@link WpCliModel} instance containing WP-CLI configuration data.
     * @param shell        The {@link Shell} instance for executing commands.
     * @param configurator The {@link Configurator} instance for managing configuration persistence.
     * @param manager      The {@link ResourceManager} instance for handling resources and file operations.
     * @param dependency   The {@link Dependency} instance for checking system requirements.
     */
    public WpCliContext(WpCliModel wpModel, Shell shell, Configurator configurator, ResourceManager manager, Dependency dependency) 
    {
        this.wpModel = wpModel;
        this.shell = shell;
        this.configurator = configurator;
        this.manager = manager;
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
     * Retrieves the resource manager for handling resource and file operations.
     *
     * <p>
     * The {@link ResourceManager} provides centralized management of application resources,
     * including files, directories, and their relationships. It handles file operations
     * and maintains the working directory context.
     * </p>
     *
     * @return The {@link ResourceManager} instance for resource management.
     */
    public ResourceManager getResourceManager() 
    {
        this.lock.lock();
        try 
        {
            return this.manager;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Retrieves the dependency checker for system requirements.
     *
     * <p>
     * The {@link Dependency} verifies that the system meets all requirements
     * necessary for WP-CLI functionality, such as having PHP and WP-CLI installed
     * and properly configured.
     * </p>
     *
     * @return The {@link Dependency} instance for checking system requirements.
     */
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