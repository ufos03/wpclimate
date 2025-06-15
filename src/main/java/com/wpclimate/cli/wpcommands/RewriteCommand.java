package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
import com.wpclimate.cli.wpcommands.registrar.WpCommandFactory;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code RewriteCommand} class implements the WP-CLI "rewrite flush" command.
 *
 * <p>
 * This command is used to flush the rewrite rules in a WordPress installation. Flushing the
 * rewrite rules ensures that the WordPress permalink structure is updated and functioning
 * correctly. The command is executed using the WP-CLI tool and requires PHP and WP-CLI to
 * be installed and configured properly.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Validates that WP-CLI and PHP are installed and accessible.</li>
 *   <li>Ensures that the current directory is a valid WordPress installation.</li>
 *   <li>Constructs and executes the "rewrite flush" WP-CLI command.</li>
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
 * RewriteCommand command = new RewriteCommand(context);
 * CommandOutput output = command.execute();
 * if (output.isSuccessful()) {
 *     System.out.println("Rewrite rules flushed successfully.");
 * } else {
 *     System.err.println("Failed to flush rewrite rules: " + output.getErrorOutput());
 * }
 * </pre>
 *
 * @see BaseWpCommand
 * @see WpCommandFactory
 * @see WpCliCommandExecutor
 */
@WpCommand("rewrite")
public class RewriteCommand extends BaseWpCommand
{
    /**
     * Constructs a {@code RewriteCommand} with the specified context and dependency.
     *
     * @param context    The application context, providing access to core components.
     */
    public RewriteCommand(WpCliContext context)
    {
        super(context);
    }

    /**
     * Executes the "rewrite flush" WP-CLI command.
     *
     * <p>
     * Constructs and executes the "rewrite flush"
     * command to flush the WordPress rewrite rules.
     * </p>
     *
     * @return The output of the command as a {@link CommandOutput} object.
     */
    @Override 
    public CommandOutput execute()
    {
        // Construct the WP-CLI command
        String command = String.format(
            "%s %s --path=%s rewrite flush",
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getResourceManager().getWorkingDirectory().toString()
        );

        // Execute the command and return the result
        return super.context.getShell().executeCommand(command);
    }
}