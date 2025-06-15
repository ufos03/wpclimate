package com.wpclimate.cli.wpcommands;

import java.util.HashMap;
import java.util.Map;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.resourcer.ResourceManager;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.exceptions.*;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

/**
 * The {@code BaseWpCommand} class serves as an abstract base class for all WP-CLI commands.
 * 
 * <p>
 * This class provides a foundation for implementing specific WP-CLI commands by encapsulating
 * shared functionality and dependencies required for executing commands. It simplifies the
 * development of WP-CLI commands by providing access to core components and utility methods.
 * </p>
 * 
 * <p>
 * Subclasses of {@code BaseWpCommand} must implement the {@link #execute()} method, which defines
 * the specific logic for executing a WP-CLI command. Commands can also be dynamically registered
 * and retrieved using the {@link #registerCommand(String, Class)} and {@link #getCommandClass(String)} methods.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Provides access to the {@link WpCliContext} object, which contains the core components of the application.</li>
 *   <li>Defines an abstract {@link #execute()} method that subclasses must implement to define the behavior of a specific command.</li>
 *   <li>Provides utility methods, such as {@link #configureEnvironmentVariables(String)}, to assist with command execution.</li>
 *   <li>Maintains a registry of available commands for dynamic command creation.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * To create a new WP-CLI command, extend this class and implement the {@link #execute()} method.
 * The {@link #execute()} method should contain the logic for the specific command and return a {@link CommandOutput}
 * object representing the result of the command execution.
 * </p>
 * 
 * <h3>Example: Subclass Implementation</h3>
 * <pre>
 * public class ListPluginsCommand extends BaseWpCommand {
 *     public ListPluginsCommand(WpCliContext context) {
 *         super(context);
 *     }
 * 
 *     {@literal @}Override
 *     public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException {
 *         // Logic for listing plugins using WP-CLI
 *         return context.getShell().executeCommand(context.getWpModel().getWp() + " plugin list");
 *     }
 * }
 * </pre>
 * 
 * <h3>Example: Dynamic Command Registration</h3>
 * <pre>
 * // Register a command
 * BaseWpCommand.registerCommand("list-plugins", ListPluginsCommand.class);
 * 
 * // Retrieve and execute the command
 * Class<? extends BaseWpCommand> commandClass = BaseWpCommand.getCommandClass("list-plugins");
 * if (commandClass != null) {
 *     BaseWpCommand command = commandClass.getConstructor(WpCliContext.class).newInstance(context);
 *     CommandOutput output = command.execute();
 *     System.out.println(output.getOutput());
 * }
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If multiple threads need to execute commands concurrently,
 * synchronization must be handled externally. Additionally, the {@code COMMAND_REGISTRY} is
 * a static map and is not synchronized, so concurrent modifications must be avoided.
 * </p>
 * 
 * <h2>Dependencies:</h2>
 * <p>
 * This class relies on the following components provided by the {@link WpCliContext}:
 * </p>
 * <ul>
 *   <li>{@link Shell} - Executes shell commands.</li>
 *   <li>{@link ResourceManager} - Manages resources such as files and directories.</li>
 *   <li>{@link Configurator} - Handles configuration persistence.</li>
 * </ul>
 * 
 * @see WpCliContext
 * @see Dependency
 * @see CommandOutput
 * @see PHPNotInstalledException
 * @see WPCliNotInstalledException
 */
public abstract class BaseWpCommand
{
    /** The {@link WpCliContext} object providing access to core components like the {@link ResourceManager}, {@link Shell}, and {@link Configurator}. */
    protected final WpCliContext context;

    private static final Map<String, Class<? extends BaseWpCommand>> COMMAND_REGISTRY = new HashMap<>();

    /**
     * Constructs a {@code BaseWpCommand} with the specified {@link WpCliContext}}.
     *
     * @param context    The {@link WpCliContext} object providing access to core components.
     */
    public BaseWpCommand(WpCliContext context) 
    {
        this.context = context;
    }

    /**
     * Configures the environment variables required for executing WP-CLI commands.
     *
     * <p>
     * This method updates the {@code PATH} environment variable by appending the specified path
     * to the current {@code PATH}. This ensures that WP-CLI can locate required executables,
     * such as {@code mysqlcheck}, during command execution.
     * </p>
     *
     * @param path The path to be added to the {@code PATH} environment variable. 
     *             Must not be {@code null} or empty.
     * @return A {@link Map} containing the updated environment variables, including the modified {@code PATH}.
     * @throws IllegalArgumentException If the specified path is {@code null} or empty.
     */
    public Map<String, String> configureEnvironmentVariables(String path) 
    {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("The path cannot be null or empty");
        }

        String currentPath = System.getenv("PATH");
        String updatedPath = path + ":" + currentPath;

        return Map.of("PATH", updatedPath);
    }

    /**
     * Registers a command in the registry.
     *
     * <p>
     * This method allows dynamic registration of WP-CLI commands. Each command is associated
     * with a unique name, which can be used to retrieve and execute the command dynamically.
     * </p>
     *
     * @param name  The name of the command.
     * @param clazz The class of the command.
     */
    public static void registerCommand(String name, Class<? extends BaseWpCommand> clazz) 
    {
        COMMAND_REGISTRY.put(name, clazz);
    }

    public static Map<String, Class<? extends BaseWpCommand>> getRegisteredCommands() {
        return COMMAND_REGISTRY;
    }

    /**
     * Retrieves the command class from the registry.
     *
     * <p>
     * This method retrieves the class of a registered command by its name. If the command
     * is not found, it returns {@code null}.
     * </p>
     *
     * @param name The name of the command.
     * @return The class of the command, or {@code null} if the command is not registered.
     */
    public static Class<? extends BaseWpCommand> getCommandClass(String name) 
    {
        return COMMAND_REGISTRY.get(name);
    }

    /**
     * Executes the WP-CLI command.
     * 
     * <p>
     * Subclasses must implement this method to define the specific logic for executing a WP-CLI command.
     * </p>
     * 
     * @return A {@link CommandOutput} object representing the result of the command execution.
     */
    public abstract CommandOutput execute();
}