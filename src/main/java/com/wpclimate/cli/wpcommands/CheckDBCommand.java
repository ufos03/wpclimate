package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
import com.wpclimate.cli.wpcommands.registrar.WpCommandFactory;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code CheckDBCommand} class implements the WP-CLI "db check" command.
 *
 * <p>
 * This command is used to check the integrity of the WordPress database. It ensures that the
 * database is accessible and functioning correctly. The command is executed using the WP-CLI
 * tool and requires PHP and WP-CLI to be installed and configured properly.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Validates that WP-CLI and PHP are installed and accessible.</li>
 *   <li>Ensures that the current directory is a valid WordPress installation.</li>
 *   <li>Constructs and executes the "db check" WP-CLI command.</li>
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
 * CheckDBCommand command = new CheckDBCommand(context);
 * CommandOutput output = command.execute();
 * if (output.isSuccessful()) {
 *     System.out.println("Database check passed.");
 * } else {
 *     System.err.println("Database check failed: " + output.getErrorOutput());
 * }
 * </pre>
 *
 * @see BaseWpCommand
 * @see WpCommandFactory
 * @see WpCliCommandExecutor
 */
@WpCommand("check-db")
public class CheckDBCommand extends BaseWpCommand
{
    /**
     * Constructs a {@code CheckDBCommand} with the specified context and dependency.
     *
     * @param context    The application context, providing access to core components.
     */
    public CheckDBCommand(Context context) 
    {
        super(context);
    }

    /**
     * Executes the "db check" WP-CLI command.
     *
     * <p>
     * This method validates that WP-CLI and PHP are installed and that the current directory
     * is a valid WordPress installation. It then constructs and executes the "db check" command
     * to verify the integrity of the WordPress database.
     * </p>
     *
     * @return The output of the command as a {@link CommandOutput} object.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     */
    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        // Ensure WP-CLI is installed
        super.context.getDependency().isWpCliInstalled();

        // Ensure the current directory is a valid WordPress installation
        super.context.getDependency().isAWordpressDirectory();

        // Construct the WP-CLI command
        String command = String.format(
            "%s %s --path=%s db check", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getFileManager().getWorkingDirectory()
        );

        // Execute the command and return the result
        return super.context.getShell().executeCommand(
            command, 
            super.configureEnvironmentVariables(super.context.getWpModel().getMYSQL())
        );
    }
}