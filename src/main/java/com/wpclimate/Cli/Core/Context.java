package com.wpclimate.Cli.Core;

import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.Configurator.Configurator;
import com.wpclimate.Shell.Shell;
import com.wpclimate.Constants.FileManager;

/**
 * The {@code Context} class provides a centralized container for managing
 * the core components required by the WP-CLI application.
 * 
 * <p>
 * This class encapsulates the following components:
 * </p>
 * <ul>
 *   <li>{@link WpCliModel} - The model containing WP-CLI configuration data.</li>
 *   <li>{@link Shell} - The shell interface for executing commands.</li>
 *   <li>{@link Configurator} - The configurator for managing configuration persistence.</li>
 *   <li>{@link FileManager} - The file manager for handling file operations and the working directory.</li>
 * </ul>
 * 
 * <p>
 * The {@code Context} class is designed to simplify the management and access
 * of these components throughout the application.
 * </p>
 */
public class Context 
{
    private final Shell shell; // The shell interface for executing commands
    private final Configurator configurator; // The configurator for managing configuration persistence
    private final WpCliModel wpModel; // The WP-CLI configuration model
    private final FileManager fileManager; // The file manager for handling file operations

    private final ReentrantLock lock = new ReentrantLock(); // Allows sync on methods 

    /**
     * Constructs a {@code Context} instance with the specified components.
     *
     * @param wpModel      The {@link WpCliModel} instance containing WP-CLI configuration data.
     * @param shell        The {@link Shell} instance for executing commands.
     * @param configurator The {@link Configurator} instance for managing configuration persistence.
     * @param fileManager  The {@link FileManager} instance for handling file operations.
     */
    public Context(WpCliModel wpModel, Shell shell, Configurator configurator, FileManager fileManager) 
    {
        this.wpModel = wpModel;
        this.shell = shell;
        this.configurator = configurator;
        this.fileManager = fileManager;
    }

    /**
     * Retrieves the WP-CLI configuration model.
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
}