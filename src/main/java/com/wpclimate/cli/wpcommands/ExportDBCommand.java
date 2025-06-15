package com.wpclimate.cli.wpcommands;

import java.util.Map;

import com.wpclimate.cli.WpCliCommandExecutor;
import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
import com.wpclimate.cli.wpcommands.registrar.WpCommandFactory;
import com.wpclimate.core.command.CommandParam;
import com.wpclimate.resourcer.ResourceType;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code ExportDBCommand} class implements the WP-CLI "db export" command.
 *
 * <p>
 * This command is used to export the WordPress database to a file. The exported file
 * can be used for backups, migrations, or other purposes. The command is executed
 * using the WP-CLI tool and requires PHP and WP-CLI to be installed and configured properly.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Validates that WP-CLI and PHP are installed and accessible.</li>
 *   <li>Ensures that the current directory is a valid WordPress installation.</li>
 *   <li>Constructs and executes the "db export" WP-CLI command with the specified file name.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * This class is instantiated dynamically by the {@link WpCommandFactory} and executed via the
 * {@link WpCliCommandExecutor}. It requires the file name for the exported database as a parameter.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * ExportDBCommand command = new ExportDBCommand(context, "backup.sql");
 * CommandOutput output = command.execute();
 * if (output.isSuccessful()) {
 *     System.out.println("Database exported successfully to backup.sql.");
 * } else {
 *     System.err.println("Failed to export database: " + output.getErrorOutput());
 * }
 * </pre>
 *
 * @see BaseWpCommand
 * @see WpCommandFactory
 * @see WpCliCommandExecutor
 */
@WpCommand("export-db")

public class ExportDBCommand extends BaseWpCommand
{
    @CommandParam(name="fileName", required=true, description="Nome del file SQL di export")
    private final String fileName;

    /**
     * Constructs an {@code ExportDBCommand} with the specified context and file name.
     *
     * @param context  The application context, providing access to core components.
     * @param fileName The name of the file to which the database will be exported.
     * @throws IllegalArgumentException If the file name is null or empty.
     */
    public ExportDBCommand(WpCliContext context, @CommandParam(name="fileName", required=true, description="Nome del file SQL di export")Map<String, String> fileName) 
    {
        super(context);
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("The fileName must be provided!");
        }
        this.fileName = fileName.get("fileName");
    }

    /**
     * Executes the "db export" WP-CLI command.
     *
     * <p>
     * Constructs and executes the "db export" command
     * to export the database to the specified file.
     * </p>
     *
     * @return The output of the command as a {@link CommandOutput} object.
     * @throws IllegalArgumentException If the file name is null or empty.
     */
    @Override
    public CommandOutput execute() throws IllegalArgumentException
    {
        if (this.fileName.isEmpty() || this.fileName == null)
            throw new IllegalArgumentException("The fileName must be provided");

        // Construct the WP-CLI command
        String command = String.format(
            "%s %s --path=%s db export %s", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getResourceManager().getWorkingDirectory().toString(),
            super.context.getResourceManager().getPath(ResourceType.SQL_DUMP_DIRECTORY)
        );

        // Execute the command and return the result
        return super.context.getShell().executeCommand(
            command, 
            super.configureEnvironmentVariables(super.context.getWpModel().getMYSQL())
        );
    }
}