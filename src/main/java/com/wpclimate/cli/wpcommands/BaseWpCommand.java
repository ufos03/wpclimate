package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.exceptions.*;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code BaseWpCommand} class serves as an abstract base class for all WP-CLI commands.
 * 
 * <p>
 * This class provides a foundation for implementing specific WP-CLI commands by encapsulating
 * the shared functionality and dependencies required for executing commands.
 * </p>
 * 
 * <p>
 * Subclasses of {@code BaseWpCommand} must implement the {@link #execute()} method, which defines
 * the specific logic for executing a WP-CLI command.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Provides access to the {@link Context} object, which contains the core components of the application.</li>
 *   <li>Provides access to the {@link Dependency} object, which is used to check for required dependencies like PHP and WP-CLI.</li>
 *   <li>Defines an abstract {@link #execute()} method that subclasses must implement to define the behavior of a specific command.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * To create a new WP-CLI command, extend this class and implement the {@link #execute()} method.
 * The {@link #execute()} method should contain the logic for the specific command and return a {@link CommandOutput}
 * object representing the result of the command execution.
 * </p>
 * 
 * <h2>Example:</h2>
 * <pre>
 * public class ListPluginsCommand extends BaseWpCommand {
 *     public ListPluginsCommand(Context context, Dependency dependency) {
 *         super(context, dependency);
 *     }
 * 
 *     {@literal @}Override
 *     public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException {
 *         // Logic for listing plugins using WP-CLI
 *         return context.getShell().executeCommand(context.getWpModel().getWp() + " plugin list");
 *     }
 * }
 * </pre>
 */
public abstract class BaseWpCommand 
{
    /** The {@link Context} object providing access to core components like the {@link FileManager}, {@link Shell}, and {@link Configurator}. */
    protected final Context context;

    /** The {@link Dependency} object used to check for required dependencies like PHP and WP-CLI. */
    protected final Dependency dependency;

    /**
     * Constructs a {@code BaseWpCommand} with the specified {@link Context} and {@link Dependency}.
     *
     * @param context    The {@link Context} object providing access to core components.
     * @param dependency The {@link Dependency} object used to check for required dependencies.
     */
    public BaseWpCommand(Context context, Dependency dependency) 
    {
        this.context = context;
        this.dependency = dependency;
    }

    /**
     * Executes the WP-CLI command.
     * 
     * <p>
     * Subclasses must implement this method to define the specific logic for executing a WP-CLI command.
     * </p>
     * 
     * @return A {@link CommandOutput} object representing the result of the command execution.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     */
    public abstract CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException;
}