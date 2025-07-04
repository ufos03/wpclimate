package com.wpclimate.git.core;

import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.configurator.Configurator;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.resourcer.ResourceManager;
import com.wpclimate.shell.Shell;

/**
 * The {@code GitContext} class provides a centralized context for managing Git-related
 * operations and dependencies within the application.
 * 
 * <p>
 * This class encapsulates the core components required for Git operations, including:
 * </p>
 * <ul>
 *   <li>{@link Shell} - Used to execute shell commands.</li>
 *   <li>{@link Settings} - Manages file operations and paths related to Git.</li>
 *   <li>{@link Dependency} - Verifies and manages Git dependencies.</li>
 *   <li>{@link Configurator} - Handles configuration persistence and retrieval.</li>
 *   <li>{@link Credential} - Manages authentication credentials for Git repositories.</li>
 * </ul>
 * 
 * <p>
 * The {@code GitContext} class ensures thread-safe access to its components using a
 * {@link ReentrantLock}.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Provides access to the {@link Shell} instance for executing commands.</li>
 *   <li>Manages file paths and operations through the {@link Settings}.</li>
 *   <li>Verifies Git dependencies using the {@link Dependency} class.</li>
 *   <li>Handles configuration data using the {@link Configurator}.</li>
 *   <li>Manages authentication credentials using the {@link Credential}.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Shell shell = new Command("/path/to/working/directory");
 * FileManager fileManager = new FileManager("/path/to/files");
 * Dependency dependency = new Dependency(shell);
 * Configurator configurator = new Configuration();
 * Credential credentials = new HttpsCredentials( ... );
 * 
 * GitContext gitContext = new GitContext(shell, fileManager, dependency, configurator, credentials);
 * 
 * Shell shellInstance = gitContext.getShell();
 * FileManager fileManagerInstance = gitContext.getFileManager();
 * Dependency dependencyInstance = gitContext.getDependency();
 * Configurator configuratorInstance = gitContext.getConfigurator();
 * Credential credentialsInstance = gitContext.getCredentials();
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is thread-safe. It uses a {@link ReentrantLock} to synchronize access to
 * its components, ensuring that multiple threads can safely interact with the context.
 * </p>
 * 
 * @see Shell
 * @see Settings
 * @see Dependency
 * @see Configurator
 * @see Credential // In fututo, Credential renderlo privato (senza getter?). Passare per CredentialManager
 */
public class GitContext 
{

    private final Shell shell;
    private final ResourceManager manager;
    private final Dependency dependency;
    private final Configurator configurator;
    private final Credential credentials;

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructs a {@code GitContext} instance with the specified components.
     * 
     * @param shell The {@link Shell} instance used for executing shell commands.
     * @param fileManager The {@link Settings} instance for managing file operations.
     * @param dependency The {@link Dependency} instance for verifying Git dependencies.
     * @param configurator The {@link Configurator} instance for managing configuration data.
     * @throws IllegalArgumentException If any of the parameters are {@code null}.
     */
    public GitContext(Shell shell, ResourceManager manager, Dependency dependency, Configurator configurator, Credential credentials) 
    {
        if (shell == null || manager == null || dependency == null || configurator == null)
            throw new IllegalArgumentException("None of the parameters can be null.");

        this.shell = shell;
        this.manager = manager;
        this.dependency = dependency;
        this.configurator = configurator;
        this.credentials = credentials;
    }

    /**
     * Returns the {@link Shell} instance used for executing shell commands.
     * 
     * <p>
     * This method ensures thread-safe access to the {@link Shell} instance using a
     * {@link ReentrantLock}.
     * </p>
     * 
     * @return The {@link Shell} instance.
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
     * Returns the {@link Settings} instance used for managing file operations.
     * 
     * <p>
     * This method ensures thread-safe access to the {@link Settings} instance using a
     * {@link ReentrantLock}.
     * </p>
     * 
     * @return The {@link Settings} instance.
     */
    public ResourceManager getResourceManager() 
    {
        this.lock.lock();
        try 
        {
            return this.manager;
        } finally 
        {
            this.lock.unlock();
        }
    }

    /**
     * Returns the {@link Dependency} instance used for verifying Git dependencies.
     * 
     * <p>
     * This method ensures thread-safe access to the {@link Dependency} instance using a
     * {@link ReentrantLock}.
     * </p>
     * 
     * @return The {@link Dependency} instance.
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

    /**
     * Returns the {@link Configurator} instance used for managing configuration data.
     * 
     * <p>
     * This method ensures thread-safe access to the {@link Configurator} instance using a
     * {@link ReentrantLock}.
     * </p>
     * 
     * @return The {@link Configurator} instance.
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

    public Credential getCredentials() 
    {
        this.lock.lock();
        try 
        {
            return this.credentials;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }
}