package com.wpclimate.git;

import java.util.Map;

import com.wpclimate.configurator.Configurator;
import com.wpclimate.core.ConsoleRCS;
import com.wpclimate.git.core.Dependency;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.git.gitcommands.GitCommandExecutor;
import com.wpclimate.resourcer.ResourceManager;
import com.wpclimate.shell.CommandOutput;
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
 *   <li>Execute Git commands and handle their output using the {@link OutputHandlerFactory}.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Git git = new Git("/path/to/working/directory", new OutputHandlerFactory(new ConsoleOutputHandler(), true));
 * 
 * boolean success = git.execute("clone", Map.of("remote", "https://github.com/example/repo.git"));
 * if (success) {
 *     System.out.println("Git clone operation completed successfully.");
 * } else {
 *     System.err.println("Git clone operation failed.");
 * }
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
    private final GitCommandExecutor commandExecutor;

    /**
     * Constructs a {@code Git} instance with the specified working directory and output handler.
     * 
     * <p>
     * This constructor initializes the {@link GitContext} and its dependencies, including
     * the {@link Shell}, {@link Dependency}, {@link Configurator}, and {@link Credential}.
     * It ensures that all components are properly configured and ready for use.
     * </p>
     * 
     * @param workingDirectory The working directory for Git operations.
     * @param outputHandler    The {@link OutputHandlerFactory} used to handle command outputs.
     * @throws Exception If an error occurs during initialization, such as missing configurations
     *                   or invalid paths.
     */
    public Git(String workingDirectory) throws Exception
    {
        GitInitializer initializer = new GitInitializer();
        ResourceManager settings = initializer.loadResources(workingDirectory);
        Configurator configurator = initializer.initializeConfigurator(settings);
        ConsoleRCS consoleInteractor = new ConsoleRCS();
        Shell shell = initializer.initializeShell(settings, consoleInteractor);
        Credential credentials = initializer.initializeCredentials(settings, configurator);
        Dependency dependency = new Dependency(shell);

        this.context = new GitContext(shell, settings, dependency, configurator, credentials);
        this.commandExecutor = new GitCommandExecutor(context);
    }

    /**
     * Executes a Git command by name with optional parameters.
     * 
     * <p>
     * This method delegates the execution of the command to the {@link GitCommandExecutor}
     * and handles the output using the configured {@link OutputHandlerFactory}.
     * </p>
     * 
     * @param commandName The name of the Git command to execute.
     * @param params      A map of parameters to pass to the command, or {@code null} if no parameters are required.
     * @return {@code true} if the command was successful, {@code false} otherwise.
     */
    public boolean execute(String commandName, Map<String, Object> params) 
    {
        try 
        {
            // Pass the parameters to the command executor
            CommandOutput output = this.commandExecutor.executeCommand(commandName, params);
            return output.isSuccessful();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }
}