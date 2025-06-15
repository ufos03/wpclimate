package com.wpclimate.cli;

import java.util.Map;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.core.ConsoleRCS;
import com.wpclimate.resourcer.ResourceManager;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.RealTimeConsoleSpoofer;
import com.wpclimate.shell.Shell;

/**
 * The {@code WpCli} class serves as the entry point for interacting with WP-CLI commands.
 *
 * <p>
 * This class provides a high-level interface for executing WP-CLI commands, such as
 * search-and-replace operations, database exports, cache flushing, and more. It manages
 * the initialization of core components and delegates command execution to the
 * {@link WpCliCommandExecutor}.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Initializes the core components required by the WP-CLI application, such as
 *       {@link WpCliContext}, {@link Dependency}, and {@link WpCliCommandExecutor}.</li>
 *   <li>Provides methods for executing WP-CLI commands with customizable parameters.</li>
 *   <li>Handles dependency checking to ensure the environment is properly configured.</li>
 *   <li>Manages command output through a configurable console interface.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code WpCli} class is typically instantiated with a working directory and used
 * to execute WP-CLI commands. For example:
 * </p>
 * <pre>
 * WpCli wpCli = new WpCli("/path/to/wordpress");
 * 
 * // Execute a search-and-replace command
 * boolean success = wpCli.execute("search-replace", Map.of(
 *     "oldValue", "http://old-url.com", 
 *     "newValue", "http://new-url.com",
 *     "allTables", true
 * ));
 * 
 * // Export database
 * wpCli.execute("export-db", Map.of("fileName", "backup.sql"));
 * </pre>
 *
 * <h2>Command Execution:</h2>
 * <p>
 * Commands are executed through the {@link WpCliCommandExecutor}, which translates high-level 
 * command names and parameters into appropriate WP-CLI shell commands. The execution results
 * are captured in a {@link CommandOutput} object that includes standard output, error output,
 * and success status.
 * </p>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code WpCli} class is not inherently thread-safe. If multiple threads need to execute
 * commands concurrently, synchronization must be handled externally.
 * </p>
 *
 * @see WpCliCommandExecutor
 * @see WpCliContext
 * @see Dependency
 * @see CommandOutput
 */
public class WpCli 
{
    private final WpCliContext context;
    private final WpCliCommandExecutor commandExecutor;

    /**
     * Constructs a {@code WpCli} instance with the specified working directory.
     *
     * <p>
     * This constructor initializes the core components required by the WP-CLI application,
     * including the {@link WpCliContext}, {@link Dependency}, and {@link WpCliCommandExecutor}.
     * It also performs dependency checks to ensure the environment is properly configured.
     * </p>
     *
     * @param workingDirectory The working directory where the WordPress installation is located.
     */
    public WpCli(String workingDirectory) {
        WpCliInitializer initializer = new WpCliInitializer();

        ResourceManager fileManager = initializer.loadResources(workingDirectory);
        RealTimeConsoleSpoofer consoleInteractor = new ConsoleRCS();
        Shell shell = initializer.initializeShell(fileManager, consoleInteractor);
        WpCliModel model = initializer.initializeModel(fileManager, initializer.initializeConfigurator(fileManager));
        Dependency dependency = new Dependency(shell, model);

        this.context = new WpCliContext(model, shell, initializer.initializeConfigurator(fileManager), fileManager, dependency);
        this.commandExecutor = new WpCliCommandExecutor(this.context);

        this.runDependencyCheck();
    }

    /**
     * Executes a WP-CLI command by name with optional parameters.
     *
     * <p>
     * This method delegates the execution of the command to the {@link WpCliCommandExecutor}.
     * The command is identified by its name, and its behavior can be customized using the
     * provided parameters. The method returns a boolean indicating whether the command
     * execution was successful.
     * </p>
     *
     * <p>
     * Available commands include:
     * <ul>
     *   <li><code>search-replace</code>: Replace strings in the database</li>
     *   <li><code>flush-caches</code>: Clear WordPress caches</li>
     *   <li><code>export-db</code>: Export WordPress database</li>
     *   <li><code>import-db</code>: Import WordPress database</li>
     *   <li><code>check-db</code>: Check WordPress database</li>
     *   <li><code>repair-db</code>: Repair WordPress database</li>
     * </ul>
     * </p>
     *
     * @param commandName The name of the command to execute.
     * @param params      A map of parameters to pass to the command, or {@code null} if no parameters are required.
     * @return {@code true} if the command was successful, {@code false} otherwise.
     */
    public boolean execute(String commandName, Map<String, Object> params) {
        try 
        {
            // Pass the parameters to the command executor
            CommandOutput output = commandExecutor.executeCommand(commandName, params);
            return output.isSuccessful();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Runs dependency checks to ensure the environment is properly configured for WP-CLI operations.
     *
     * <p>
     * This method verifies that:
     * <ul>
     *   <li>PHP is installed and available in the system path</li>
     *   <li>WP-CLI is installed and available</li>
     *   <li>The working directory contains a valid WordPress installation</li>
     * </ul>
     * </p>
     *
     * <p>
     * If any of these checks fail, an error message is printed to stderr.
     * </p>
     *
     * @return {@code true} if all dependency checks pass, {@code false} otherwise.
     */
    private boolean runDependencyCheck() {
        try
        {
            this.context.getDependency().isPHPInstalled();
            this.context.getDependency().isWpCliInstalled();
            return this.context.getDependency().isAWordpressDirectory();
        } catch (Exception e) 
        {
            System.err.println(e.getMessage());
            return false;
        }
    }
}