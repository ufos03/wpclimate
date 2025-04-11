package com.wpclimate.cli.wpcommands;

import java.util.Map;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.cli.wpcommands.registrar.WpCommand;
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
    private final String fileName;

    /**
     * Constructs an {@code ExportDBCommand} with the specified context and file name.
     *
     * @param context  The application context, providing access to core components.
     * @param fileName The name of the file to which the database will be exported.
     * @throws IllegalArgumentException If the file name is null or empty.
     */
    public ExportDBCommand(WpCliContext context, Map<String, String> fileName) 
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
     * This method validates that WP-CLI and PHP are installed and that the current directory
     * is a valid WordPress installation. It then constructs and executes the "db export" command
     * to export the database to the specified file.
     * </p>
     *
     * @return The output of the command as a {@link CommandOutput} object.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     * @throws IllegalArgumentException If the file name is null or empty.
     */
    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException, IllegalArgumentException
    {
        // Ensure WP-CLI is installed
        super.context.getDependency().isWpCliInstalled();

        // Ensure the current directory is a valid WordPress installation
        super.context.getDependency().isAWordpressDirectory();

        if (this.fileName.isEmpty() || this.fileName == null)
            throw new IllegalArgumentException("The fileName must be provided");

        // Construct the WP-CLI command
        String command = String.format(
            "%s %s --path=%s db export %s", 
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