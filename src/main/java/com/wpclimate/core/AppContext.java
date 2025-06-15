package com.wpclimate.core;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.cli.WpCli;
import com.wpclimate.git.Git;

/**
 * The {@code AppContext} class provides a centralized context for managing the core
 * components of the application, including WP-CLI and Git.
 * 
 * <p>
 * This class is responsible for initializing and providing access to the following components:
 * </p>
 * <ul>
 *   <li>{@link WpCli} - Manages WP-CLI-related operations.</li>
 *   <li>{@link Git} - Manages Git-related operations.</li>
 * </ul>
 * 
 * <p>
 * The {@code AppContext} also ensures that the required settings directory is created
 * during initialization and provides thread-safe access to its components using a
 * {@link ReentrantLock}.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Initialize and manage the {@link WpCli} and {@link Git} components.</li>
 *   <li>Create the settings directory if it does not exist.</li>
 *   <li>Provide thread-safe access to the {@link WpCli} and {@link Git} instances.</li>
 *   <li>Manage the visibility of command outputs using the {@link OutputHandlerFactory}.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * AppContext appContext = new AppContext("/path/to/working/directory");
 * 
 * WpCli wpCli = appContext.getWpCli();
 * Git git = appContext.getGit();
 * 
 * appContext.setOutputVisible(false); // Disable output visibility
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class uses a {@link ReentrantLock} to ensure thread-safe access to its components.
 * </p>
 * 
 * @see WpCli
 * @see Git
 * @see SettingsFilesNames
 */
public class AppContext 
{
    private final WpCli wpCli;
    private final Git git;


    private final ReentrantLock lock;

    /**
     * Constructs an {@code AppContext} instance with the specified working directory.
     * 
     * <p>
     * This constructor initializes the {@link WpCli} and {@link Git} components and
     * ensures that the settings directory is created within the specified working directory.
     * </p>
     * 
     * @param workingDirectory The working directory for the application.
     * @throws Exception If an error occurs during the initialization of {@link WpCli} or {@link Git}.
     */
    public AppContext(String workingDirectory) throws Exception 
    {
        this.lock = new ReentrantLock();
        
        this.wpCli = new WpCli(workingDirectory);
        this.git = new Git(workingDirectory);
    }

    /**
     * Returns the {@link WpCli} instance.
     * 
     * <p>
     * This method provides thread-safe access to the {@link WpCli} instance.
     * </p>
     * 
     * @return The {@link WpCli} instance.
     */
    public WpCli getWpCli() 
    {
        this.lock.lock();
        try 
        {
            return this.wpCli;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    /**
     * Returns the {@link Git} instance.
     * 
     * <p>
     * This method provides thread-safe access to the {@link Git} instance.
     * </p>
     * 
     * @return The {@link Git} instance.
     */
    public Git getGit() 
    {
        this.lock.lock();
        try 
        {
            return this.git;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }
}