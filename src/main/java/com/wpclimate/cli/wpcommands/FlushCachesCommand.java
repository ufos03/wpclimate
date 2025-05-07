package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.exceptions.*;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code FlushCachesCommand} class implements the WP-CLI "cache flush" command.
 *
 * <p>
 * This command clears all cached data stored in the WordPress database, ensuring that
 * the site operates with the most up-to-date information. It is particularly useful
 * for troubleshooting or after making significant changes to the site.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Validates that WP-CLI and PHP are installed and accessible.</li>
 *   <li>Ensures that the current directory is a valid WordPress installation.</li>
 *   <li>Constructs and executes the "cache flush" WP-CLI command.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * This class is instantiated dynamically by the {@link WpCommandFactory} and executed via the
 * {@link WpCliCommandExecutor}. It does not require additional parameters.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * FlushCachesCommand command = new FlushCachesCommand(context);
 * CommandOutput output = command.execute();
 * if (output.isSuccessful()) {
 *     System.out.println("Caches flushed successfully.");
 * } else {
 *     System.err.println("Failed to flush caches: " + output.getErrorOutput());
 * }
 * </pre>
 *
 * @see BaseWpCommand
 * @see WpCommandFactory
 * @see WpCliCommandExecutor
 */
@WpCommand("flush-caches")
public class FlushCachesCommand extends BaseWpCommand 
{
    /**
     * Constructs a new {@code FlushCachesCommand} with the specified context and dependency.
     *
     * @param context    The {@link WpCliContext} object providing access to the environment and configuration.
     */
    public FlushCachesCommand(WpCliContext context) 
    {
        super(context);
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
        String command = String.format(
            "%s %s --path=%s cache flush", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getFileManager().getWorkingDirectory()
        );

        return super.context.getShell().executeCommand(command);
    }
}