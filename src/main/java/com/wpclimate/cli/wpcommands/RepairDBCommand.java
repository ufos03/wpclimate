package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
import com.wpclimate.cli.wpcommands.registrar.WpCommandFactory;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code RepairDBCommand} class implements the WP-CLI "db repair" command.
 *
 * <p>
 * This command is used to repair the WordPress database. It ensures that the database is
 * functioning correctly by attempting to fix any issues. The command is executed using the
 * WP-CLI tool and requires PHP and WP-CLI to be installed and configured properly.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Validates that WP-CLI and PHP are installed and accessible.</li>
 *   <li>Ensures that the current directory is a valid WordPress installation.</li>
 *   <li>Constructs and executes the "db repair" WP-CLI command.</li>
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
 * RepairDBCommand command = new RepairDBCommand(context);
 * CommandOutput output = command.execute();
 * if (output.isSuccessful()) {
 *     System.out.println("Database repair completed successfully.");
 * } else {
 *     System.err.println("Database repair failed: " + output.getErrorOutput());
 * }
 * </pre>
 *
 * @see BaseWpCommand
 * @see WpCommandFactory
 * @see WpCliCommandExecutor
 */
@WpCommand("repair-db")
public class RepairDBCommand extends BaseWpCommand
{
    /**
     * Constructs a {@code RepairDBCommand} with the specified context and dependency.
     *
     * @param context    The application context, providing access to core components.
     */
    public RepairDBCommand(WpCliContext context) 
    {
        super(context);
    }

    /**
     * Executes the "db repair" WP-CLI command.
     *
     * <p>
     * Constructs and executes the "db repair" command
     * to attempt to fix any issues with the WordPress database.
     * </p>
     *
     * @return The output of the command as a {@link CommandOutput} object.
     */
    @Override
    public CommandOutput execute()
    {
        // Construct the WP-CLI command
        String command = String.format(
            "%s %s --path=%s db repair", 
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