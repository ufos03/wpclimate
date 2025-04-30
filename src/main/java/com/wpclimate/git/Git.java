package com.wpclimate.git;

import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.git.core.Dependency;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.credentials.ssh.SshCredentials;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.Shell;

/**
 * The {@code Git} class serves as the entry point for managing Git-related operations.
 * 
 * <p>
 * This class initializes the core components required for Git operations, including
 * the {@link GitContext}, {@link Dependency}, and {@link Shell}. It provides a centralized
 * context for executing Git commands and managing Git credentials.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Initialize the {@link GitContext} with the specified working directory.</li>
 *   <li>Provide access to Git-related operations and credentials management.</li>
 *   <li>Ensure that all dependencies, such as {@link Shell} and {@link Configurator}, are properly initialized.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Git git = new Git("/path/to/working/directory");
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If multiple threads need to access the same instance,
 * external synchronization is required.
 * </p>
 * 
 * <h2>Dependencies:</h2>
 * <p>
 * The {@code Git} class relies on the following components:
 * </p>
 * <ul>
 *   <li>{@link GitContext} - Provides a centralized context for Git operations.</li>
 *   <li>{@link Shell} - Executes shell commands for Git operations.</li>
 *   <li>{@link Dependency} - Verifies and manages Git dependencies.</li>
 *   <li>{@link Configurator} - Handles configuration persistence and retrieval.</li>
 *   <li>{@link Credential} - Manages authentication credentials for Git repositories.</li>
 * </ul>
 * 
 * @see GitContext
 * @see Dependency
 * @see Shell
 * @see Configurator
 * @see Credential
 */
public class Git 
{
    private final GitContext context;

    /**
     * Constructs a {@code Git} instance with the specified working directory.
     * 
     * <p>
     * This constructor initializes the {@link GitContext} and its dependencies, including
     * the {@link Shell}, {@link Dependency}, {@link Configurator}, and {@link Credential}.
     * It ensures that all components are properly configured and ready for use.
     * </p>
     * 
     * @param workingDirectory The working directory for Git operations.
     * @throws Exception If an error occurs during initialization, such as missing configurations
     *                   or invalid paths.
     */
    public Git(String workingDirectory) throws Exception 
    {
        GitInitializer initializer = new GitInitializer();
        Settings settings = initializer.loadSettings(workingDirectory);
        Configurator configurator = initializer.initializeConfigurator(settings);
        Shell shell = initializer.initializeShell(settings);
        Credential credentials = initializer.initializeCredentials(settings, configurator);
        Dependency dependency = new Dependency(shell);

        this.context = new GitContext(shell, settings, dependency, configurator, credentials);
    }
}