package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

/**
 * The {@code FlushTransientCommand} class implements the WP-CLI "cache flush" command.
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
 * FlushTransientCommand command = new FlushTransientCommand(context);
 * CommandOutput output = command.execute();
 * if (output.isSuccessful()) {
 *     System.out.println("Transients flushed successfully.");
 * } else {
 *     System.err.println("Failed to flush transients: " + output.getErrorOutput());
 * }
 * </pre>
 *
 * @see BaseWpCommand
 * @see WpCommandFactory
 * @see WpCliCommandExecutor
 */
@WpCommand("flush-transient")
public class FlushTransientCommand extends BaseWpCommand
{
    /**
     * Constructs a {@code FlushTransientCommand} instance with the specified parameters.
     *
     * @param context The {@link WpCliContext} object providing access to core components.
     */
    public FlushTransientCommand(WpCliContext context)
    {
        super(context);
    }

    /**
     * Executes the command to delete all transients using WP-CLI.
     *
     * <p>
     * This method constructs the WP-CLI command for deleting all transients and
     * executes it using the {@link Shell} from the {@link WpCliContext}.
     * </p>
     *
     * @return A {@link CommandOutput} object containing the result of the command execution.
     */
    @Override
    public CommandOutput execute()
    {
        String command = String.format(
            "%s %s --path=%s transient delete --all", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getFileManager().getWorkingDirectory()
        );

        return super.context.getShell().executeCommand(command);
    }
}