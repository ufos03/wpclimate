package com.wpclimate.git;

import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.git.core.Dependency;
import com.wpclimate.git.core.GitContext;
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
 * @see GitContext
 * @see Dependency
 * @see Shell
 */
public class Git {

    private final GitContext context;

    /**
     * Constructs a {@code Git} instance with the specified working directory.
     * 
     * <p>
     * This constructor initializes the {@link GitContext} and its dependencies, including
     * the {@link Shell}, {@link Dependency}, and {@link Configurator}.
     * </p>
     * 
     * @param workingDirectory The working directory for Git operations.
     * @throws Exception If an error occurs during initialization.
     */
    public Git(String workingDirectory) throws Exception 
    {
        this.context = initializeGitContext(workingDirectory);
    }

    /**
     * Initializes the {@link GitContext} with the specified working directory.
     * 
     * <p>
     * This method creates the required components, such as {@link Shell}, {@link Dependency},
     * and {@link Configurator}, and uses them to construct the {@link GitContext}.
     * </p>
     * 
     * @param workingDirectory The working directory for Git operations.
     * @return An instance of {@link GitContext}.
     * @throws Exception If an error occurs during initialization.
     */
    private GitContext initializeGitContext(String workingDirectory) throws Exception 
    {
        try 
        {
            // Initialize the settings manager
            Settings settings = new Settings(workingDirectory);

            // Initialize the shell for executing commands
            Shell shell = new Command(settings.getWorkingDirectory().getAbsolutePath());

            // Initialize dependencies
            Dependency dependency = new Dependency(shell);

            // Initialize the configurator for managing configurations
            Configurator configurator = new com.wpclimate.configurator.Configuration();

            // Create and return the GitContext
            return new GitContext(shell, settings, dependency, configurator);
        } catch (Exception e) {
            throw new Exception("Failed to initialize GitContext: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the {@link GitContext} instance.
     * 
     * <p>
     * This method provides access to the {@link GitContext} for executing Git-related
     * operations and managing credentials.
     * </p>
     * 
     * @return The {@link GitContext} instance.
     */
    public GitContext getContext() {
        return this.context;
    }
}