package com.wpclimate.cli;

import java.util.Map;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.wpcommands.BaseWpCommand;
import com.wpclimate.cli.wpcommands.CommandRegistrar;
import com.wpclimate.cli.wpcommands.WpCommandFactory;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code WpCliCommandExecutor} class is responsible for executing WP-CLI commands.
 *
 * <p>
 * This class acts as a bridge between the application and the WP-CLI commands. It initializes
 * the command registry by dynamically registering all available commands using the {@link CommandRegistrar}.
 * It then uses the {@link WpCommandFactory} to create and execute the specified command.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Initializes the command registry by registering all commands at startup.</li>
 *   <li>Executes WP-CLI commands by name using the {@link WpCommandFactory}.</li>
 *   <li>Handles the lifecycle of command execution, including error handling.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * Create an instance of this class by providing the application {@link Context} and {@link Dependency}.
 * Use the {@link #executeCommand(String)} method to execute a WP-CLI command by its name.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * Context context = new Context();
 * Dependency dependency = new Dependency();
 * WpCliCommandExecutor executor = new WpCliCommandExecutor(context, dependency);
 *
 * try {
 *     CommandOutput output = executor.executeCommand("rewrite");
 *     if (output.isSuccessful()) {
 *         System.out.println("Command executed successfully.");
 *     } else {
 *         System.err.println("Command failed: " + output.getErrorOutput());
 *     }
 * } catch (Exception e) {
 *     e.printStackTrace();
 * }
 * </pre>
 *
 * @see CommandRegistrar
 * @see WpCommandFactory
 * @see BaseWpCommand
 */
public class WpCliCommandExecutor 
{

    private final Context context;
    private final Dependency dependency;

    /**
     * Constructs a {@code WpCliCommandExecutor} with the specified {@link Context} and {@link Dependency}.
     *
     * <p>
     * This constructor initializes the command registry by calling {@link CommandRegistrar#registerAllCommands()}.
     * </p>
     *
     * @param context    The application context, providing access to core components.
     * @param dependency The dependency checker, ensuring required dependencies are available.
     */
    public WpCliCommandExecutor(Context context, Dependency dependency) 
    {
        this.context = context;
        this.dependency = dependency;
        CommandRegistrar.registerAllCommands();
    }

    /**
     * Executes a WP-CLI command by name.
     *
     * <p>
     * This method retrieves the command class from the registry using the {@link WpCommandFactory},
     * creates an instance of the command, and executes it. If the command execution fails, an exception
     * is thrown.
     * </p>
     *
     * @param commandName The name of the command to execute.
     * @param params A map of parameters to pass to the command.
     * @return The output of the command as a {@link CommandOutput} object.
     * @throws Exception If the command execution fails.
     */
    public CommandOutput executeCommand(String commandName, Map<String, Object> params) throws Exception 
    {
        BaseWpCommand command = WpCommandFactory.createCommand(commandName, context, dependency, params);
        return command.execute();
    }
}