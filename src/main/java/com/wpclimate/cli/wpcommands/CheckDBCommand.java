package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
import com.wpclimate.cli.wpcommands.registrar.WpCommandFactory;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code CheckDBCommand} class implements the WP-CLI "db check" command.
 *
 * <p>
 * This command is used to verify the integrity of the WordPress database. It ensures that the
 * database is accessible and functioning correctly. The command is executed using the WP-CLI
 * tool and requires PHP and WP-CLI to be installed and properly configured.
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
 * The {@code CheckDBCommand} class is instantiated dynamically by the {@link WpCommandFactory}
 * and executed via the {@link WpCliCommandExecutor}. It does not require additional parameters.
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
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code CheckDBCommand} class is not inherently thread-safe. If multiple threads need to
 * execute commands concurrently, synchronization must be handled externally.
 * </p>
 *
 * <h2>Dependencies:</h2>
 * <p>
 * This class relies on the following components provided by the {@link WpCliContext}:
 * </p>
 * <ul>
 *   <li>{@link Shell} - Executes shell commands.</li>
 *   <li>{@link ResourceManager} - Manages resources such as files and directories.</li>
 *   <li>{@link WpCliModel} - Provides configuration data for WP-CLI, including paths to executables.</li>
 * </ul>
 *
 * <h2>Command Details:</h2>
 * <p>
 * The "db check" command verifies the integrity of the WordPress database. It checks for
 * issues such as missing tables or corrupted data. The command is executed in the context
 * of the current WordPress installation directory.
 * </p>
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
    public CheckDBCommand(WpCliContext context) 
    {
        super(context);
    }

    /**
     * Executes the "db check" WP-CLI command.
     *
     * <p>
     * Constructs and executes the "db check" command
     * to verify the integrity of the WordPress database.
     * </p>
     *
     * @return The output of the command as a {@link CommandOutput} object.
     */
    @Override
    public CommandOutput execute()
    {
        // Construct the WP-CLI command
        String command = String.format(
            "%s %s --path=%s db check", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getResourceManager().getWorkingDirectory().toString()
        );

        // Execute the command and return the result
        return super.context.getShell().executeCommand(
            command, 
            super.configureEnvironmentVariables(super.context.getWpModel().getMYSQL())
        );
    }
}