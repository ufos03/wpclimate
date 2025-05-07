package com.wpclimate.cli;

import java.util.Map;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.core.ConsoleRCS;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.RealTimeConsoleSpoofer;
import com.wpclimate.shell.Shell;

/**
 * The {@code WpCli} class serves as the entry point for the WP-CLI application.
 * 
 * <p>
 * This class provides a high-level interface for executing WP-CLI commands, such as
 * search-and-replace operations, flushing transients, and rewriting rules. It manages
 * the initialization of core components and delegates command execution to the
 * {@link WpCliCommandExecutor}.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Initializes the core components required by the WP-CLI application, such as
 *       {@link WpCliContext}, {@link Dependency}, and {@link WpCliCommandExecutor}.</li>
 *   <li>Provides methods for executing WP-CLI commands, including search-and-replace,
 *       flushing transients, and rewriting rules.</li>
 *   <li>Handles the output of commands using a customizable {@link CommandOutputHandler}.</li>
 *   <li>Supports conditional output to the console based on the {@code showOutput} flag.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * The {@code WpCli} class is typically instantiated with a working directory and used
 * to execute WP-CLI commands. For example:
 * </p>
 * <pre>
 * WpCli wpCli = new WpCli("/path/to/working/directory", new OutputHandlerFactory(new ConsoleOutputHandler(), true));
 * 
 * // Execute a search-and-replace command
 * boolean success = wpCli.execute("search-replace", Map.of("search", "http://old-url.com", "replace", "http://new-url.com"));
 * if (success) {
 *     System.out.println("Search-and-replace completed successfully.");
 * } else {
 *     System.err.println("Search-and-replace failed.");
 * }
 * </pre>
 * 
 * <h2>Output Handling:</h2>
 * <p>
 * The {@code WpCli} class uses a {@link CommandOutputHandler} to handle the output of
 * WP-CLI commands. By default, it uses a {@link ConsoleOutputHandler} to print output
 * to the console. You can customize the output handling by providing a different
 * implementation of {@link CommandOutputHandler} using the {@link OutputHandlerFactory}.
 * </p>
 * 
 * @see WpCliCommandExecutor
 * @see CommandOutputHandler
 * @see ConsoleOutputHandler
 */
public class WpCli {
    private final WpCliContext context;
    private final WpCliCommandExecutor commandExecutor;

    /**
     * Constructs a {@code WpCli} instance with the specified working directory and output handler.
     *
     * <p>
     * This constructor initializes the core components required by the WP-CLI application,
     * including the {@link WpCliContext}, {@link Dependency}, and {@link WpCliCommandExecutor}.
     * The output handler is used to manage the display of command outputs.
     * </p>
     *
     * @param workingDirectory The working directory for the application.
     * @param outputHandler    The {@link OutputHandlerFactory} used to handle command outputs.
     */
    public WpCli(String workingDirectory) {
        WpCliInitializer initializer = new WpCliInitializer();

        Settings fileManager = initializer.loadSettings(workingDirectory);
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
     * This method delegates the execution of the command to the {@link WpCliCommandExecutor}
     * and handles the output using the configured {@link OutputHandlerFactory}.
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

    private boolean runDependencyCheck() // TODO DOC
    {
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