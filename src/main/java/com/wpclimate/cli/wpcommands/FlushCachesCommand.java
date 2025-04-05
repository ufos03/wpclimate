package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.exceptions.*;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code FlushCachesCommand} class is responsible for executing the WP-CLI command
 * to flush all caches in a WordPress installation.
 *
 * <p>
 * This command clears all cached data stored in the WordPress database, ensuring that
 * the site operates with the most up-to-date information. It is particularly useful
 * for troubleshooting or after making significant changes to the site.
 * </p>
 *
 * <h2>Usage:</h2>
 * <p>
 * To use this command, ensure that the environment meets the following requirements:
 * </p>
 * <ul>
 *   <li>WP-CLI is installed and accessible.</li>
 *   <li>The current working directory is a valid WordPress installation.</li>
 *   <li>PHP is installed and accessible.</li>
 * </ul>
 *
 * <h2>Example:</h2>
 * <pre>
 * Context context = ...; // Initialize the context
 * Dependency dependency = ...; // Initialize the dependency
 * 
 * FlushCachesCommand command = new FlushCachesCommand(context, dependency);
 * try {
 *     CommandOutput output = command.execute();
 *     if (output.isSuccessful()) {
 *         System.out.println("Caches flushed successfully.");
 *     } else {
 *         System.err.println("Failed to flush caches: " + output.getErrorOutput());
 *     }
 * } catch (PHPNotInstalledException | WPCliNotInstalledException e) {
 *     System.err.println("Error: " + e.getMessage());
 * }
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If multiple threads need to execute this command,
 * synchronization must be handled externally.
 * </p>
 *
 * @see BaseWpCommand
 * @see CommandOutput
 * @see PHPNotInstalledException
 * @see WPCliNotInstalledException
 */
public class FlushCachesCommand extends BaseWpCommand 
{

    /**
     * Constructs a new {@code FlushCachesCommand} with the specified context and dependency.
     *
     * @param context    The {@link Context} object providing access to the environment and configuration.
     * @param dependency The {@link Dependency} object for checking prerequisites.
     */
    public FlushCachesCommand(Context context, Dependency dependency) 
    {
        super(context, dependency);
    }

    /**
     * Executes the WP-CLI command to flush all caches in the WordPress installation.
     *
     * <p>
     * This method verifies that the environment meets the prerequisites (e.g., WP-CLI and PHP
     * are installed, and the current directory is a valid WordPress installation) before
     * executing the command.
     * </p>
     *
     * @return A {@link CommandOutput} object containing the result of the command execution.
     * @throws PHPNotInstalledException    If PHP is not installed or accessible.
     * @throws WPCliNotInstalledException  If WP-CLI is not installed or accessible.
     */
    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        super.dependency.isWpCliInstalled();
        super.dependency.isAWordpressDirectory();

        String command = String.format(
            "%s %s --path=%s cache flush", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getFileManager().getWorkingDirectory()
        );

        return super.context.getShell().executeCommand(command);
    }
}