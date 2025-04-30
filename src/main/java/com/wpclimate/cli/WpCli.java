package com.wpclimate.cli;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.CommandOutputHandler;
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
 *   <li>Ensures thread-safe access to shared resources like {@code showOutput} and
 *       {@code outputHandler} using a {@link ReentrantLock}.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * The {@code WpCli} class is typically instantiated with a working directory and used
 * to execute WP-CLI commands. For example:
 * </p>
 * <pre>
 * WpCli wpCli = new WpCli("/path/to/working/directory");
 * 
 * // Enable output to the console
 * wpCli.setShowOutput(true);
 * 
 * // Execute a search-and-replace command
 * boolean success = wpCli.doSearchReplace("http://old-url.com", "http://new-url.com", true, false);
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
 * implementation of {@link CommandOutputHandler} using the {@link #setOutputHandler(CommandOutputHandler)}
 * method.
 * </p>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is thread-safe. It uses a {@link ReentrantLock} to synchronize access to
 * shared resources like {@code showOutput} and {@code outputHandler}. This ensures
 * consistent behavior when the class is used in a multi-threaded environment.
 * </p>
 * 
 * <h2>Future Extensions:</h2>
 * <p>
 * The design of this class allows for easy integration with a graphical user interface (GUI).
 * For example, you can implement a custom {@link CommandOutputHandler} to display command
 * output in a GUI component instead of the console.
 * </p>
 * 
 * @see WpCliCommandExecutor
 * @see CommandOutputHandler
 * @see ConsoleOutputHandler
 */
public class WpCli 
{
    private final ReentrantLock lock = new ReentrantLock();
    private final WpCliContext context;
    private final WpCliCommandExecutor commandExecutor;
    private boolean showOutput;
    private CommandOutputHandler outputHandler;

    /**
     * Constructs a {@code WpCli} instance with the specified working directory.
     *
     * <p>
     * This constructor initializes the core components required by the WP-CLI application,
     * including the {@link WpCliContext}, {@link Dependency}, and {@link WpCliCommandExecutor}.
     * By default, the output handler is set to {@link ConsoleOutputHandler}, and the
     * {@code showOutput} flag is set to {@code false}.
     * </p>
     *
     * @param workingDirectory The working directory for the application.
     */
    public WpCli(String workingDirectory) 
    {
        WpCliInitializer initializer = new WpCliInitializer();

        Settings fileManager = initializer.loadSettings(workingDirectory);
        Shell shell = initializer.initializeShell(fileManager);
        WpCliModel model = initializer.initializeModel(fileManager, initializer.initializeConfigurator(fileManager));
        Dependency dependency = new Dependency(shell, model);

        this.context = new WpCliContext(model, shell, initializer.initializeConfigurator(fileManager), fileManager, dependency);
        this.commandExecutor = new WpCliCommandExecutor(this.context);
        this.showOutput = false;
        this.outputHandler = new ConsoleOutputHandler();
    }

    /**
     * Enables or disables output to the console.
     *
     * @param value {@code true} to enable output to the console, {@code false} to disable it.
     */
    public void setShowOutput(boolean value) 
    {
        this.lock.lock();
        try 
        {
            this.showOutput = value;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    /**
     * Sets a custom output handler for handling command output.
     *
     * <p>
     * The output handler is responsible for processing the output of WP-CLI commands.
     * By default, the {@link ConsoleOutputHandler} is used to print output to the console.
     * </p>
     *
     * @param handler The {@link CommandOutputHandler} to use.
     */
    public void setOutputHandler(CommandOutputHandler handler) 
    {
        this.lock.lock();
        try
        {
            this.outputHandler = handler;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    /**
     * Prints the output of a command to the console if {@code showOutput} is enabled.
     *
     * @param output The {@link CommandOutput} object containing the command's output.
     */
    public void printOutputToConsole(CommandOutput output) 
    {
        this.lock.lock();
        try
        {
            if (this.showOutput && this.outputHandler != null)
                this.outputHandler.handleOutput(output);
            
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    /**
     * Executes a WP-CLI command by name with optional parameters.
     *
     * @param commandName The name of the command.
     * @param params      A map of parameters to pass to the command, or {@code null} if no parameters are required.
     * @return {@code true} if the command was successful, {@code false} otherwise.
     */
    public boolean execute(String commandName, Map<String, Object> params) 
    {
        try 
        {
            // Pass the parameters to the command executor
            CommandOutput output = commandExecutor.executeCommand(commandName, params);
            this.printOutputToConsole(output);
            return output.isSuccessful();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }
}