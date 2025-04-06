package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.constants.FileManager;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

/**
 * The {@code FlushTransientCommand} class represents a WP-CLI command for deleting
 * all transients from a WordPress installation.
 * 
 * <p>
 * Transients are temporary options stored in the WordPress database, often used
 * for caching purposes. This command allows you to delete all transients, which
 * can be useful for clearing cached data or troubleshooting issues.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Constructs the WP-CLI command for deleting all transients.</li>
 *   <li>Executes the command using the {@link Shell} provided by the {@link Context}.</li>
 *   <li>Validates that WP-CLI is installed before executing the command.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * To use this class, create an instance by providing the required {@link Context}
 * and {@link Dependency}, and then call the {@link #execute()} method to run the command.
 * </p>
 * 
 * <h2>Example:</h2>
 * <pre>
 * Context context = ...; // Obtain the application context
 * Dependency dependency = ...; // Obtain the dependency checker
 * 
 * FlushTransientCommand command = new FlushTransientCommand(context, dependency);
 * 
 * try {
 *     CommandOutput output = command.execute();
 *     if (output.isSuccessful()) {
 *         System.out.println("All transients deleted successfully.");
 *     } else {
 *         System.err.println("Failed to delete transients: " + output.getErrorOutput());
 *     }
 * } catch (PHPNotInstalledException | WPCliNotInstalledException e) {
 *     System.err.println("Error: " + e.getMessage());
 * }
 * </pre>
 * 
 * <h2>Dependencies:</h2>
 * <p>
 * This class relies on the following components:
 * </p>
 * <ul>
 *   <li>{@link Context} - Provides access to the core components of the application, such as the {@link Shell} and {@link FileManager}.</li>
 *   <li>{@link Dependency} - Ensures that WP-CLI is installed before executing the command.</li>
 * </ul>
 * 
 * @see BaseWpCommand
 * @see CommandOutput
 */

@WpCommand("flush-transient")
public class FlushTransientCommand extends BaseWpCommand
{
    /**
     * Constructs a {@code FlushTransientCommand} instance with the specified parameters.
     *
     * @param context The {@link Context} object providing access to core components.
     * @param dependency The {@link Dependency} object used to check for required dependencies.
     */
    public FlushTransientCommand(Context context, Dependency dependency)
    {
        super(context, dependency);
    }

    /**
     * Executes the command to delete all transients using WP-CLI.
     *
     * <p>
     * This method constructs the WP-CLI command for deleting all transients and
     * executes it using the {@link Shell} from the {@link Context}.
     * </p>
     *
     * @return A {@link CommandOutput} object containing the result of the command execution.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     */
    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        super.dependency.isWpCliInstalled();
        super.dependency.isAWordpressDirectory();

        String command = String.format(
            "%s %s --path=%s transient delete --all", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getFileManager().getWorkingDirectory()
        );

        return super.context.getShell().executeCommand(command);
    }
}