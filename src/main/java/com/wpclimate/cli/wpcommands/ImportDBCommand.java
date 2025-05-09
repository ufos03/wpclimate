package com.wpclimate.cli.wpcommands;

import java.util.Map;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code ImportDBCommand} class implements the WP-CLI "db import" command.
 *
 * <p>
 * This command is used to import a WordPress database from a file. The imported file
 * can be used to restore a backup, migrate a site, or populate a database with predefined data.
 * The command is executed using the WP-CLI tool and requires PHP and WP-CLI to be installed
 * and configured properly.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Validates that WP-CLI and PHP are installed and accessible.</li>
 *   <li>Ensures that the current directory is a valid WordPress installation.</li>
 *   <li>Constructs and executes the "db import" WP-CLI command with the specified file name.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * This class is instantiated dynamically by the {@link WpCommandFactory} and executed via the
 * {@link WpCliCommandExecutor}. It requires the file name of the database to be imported as a parameter.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * ImportDBCommand command = new ImportDBCommand(context, Map.of("fileName", "backup.sql"));
 * CommandOutput output = command.execute();
 * if (output.isSuccessful()) {
 *     System.out.println("Database imported successfully from backup.sql.");
 * } else {
 *     System.err.println("Failed to import database: " + output.getErrorOutput());
 * }
 * </pre>
 *
 * @see BaseWpCommand
 * @see WpCommandFactory
 * @see WpCliCommandExecutor
 */
@WpCommand("import-db")
public class ImportDBCommand extends BaseWpCommand
{
    private final String fileName;

    /**
     * Constructs an {@code ImportDBCommand} with the specified context and file name.
     *
     * @param context  The application context, providing access to core components.
     * @param fileName A map containing the name of the file from which the database will be imported.
     * @throws IllegalArgumentException If the file name is null or empty.
     */
    public ImportDBCommand(WpCliContext context, Map<String, String> fileName) 
    {
        super(context);
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("The fileName must be provided!");
        }
        this.fileName = fileName.get("fileName");
    }

    /**
     * Executes the "db import" WP-CLI command.
     *
     * <p>
     * Constructs and executes the "db import" command
     * to import the database from the specified file.
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
            "%s %s --path=%s db import %s", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getFileManager().getWorkingDirectory(),
            this.fileName
        );

        // Execute the command and return the result
        return super.context.getShell().executeCommand(
            command, 
            super.configureEnvironmentVariables(super.context.getWpModel().getMYSQL())
        );
    }
}