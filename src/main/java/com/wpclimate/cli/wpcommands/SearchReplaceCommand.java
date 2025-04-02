package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.exceptions.*;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code SearchReplaceCommand} class represents a WP-CLI command for performing
 * a search-and-replace operation on a WordPress database.
 * 
 * <p>
 * This command allows replacing occurrences of a specific value in the database
 * with a new value. It supports additional options such as targeting all tables
 * and performing a dry run to preview the changes without applying them.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Constructs the WP-CLI command for search-and-replace operations.</li>
 *   <li>Executes the command using the {@link Shell} provided by the {@link Context}.</li>
 *   <li>Validates that WP-CLI is installed before executing the command.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * To use this class, create an instance by providing the required parameters
 * (e.g., old value, new value, whether to target all tables, and whether to perform a dry run),
 * and then call the {@link #execute()} method to run the command.
 * </p>
 * 
 * <h2>Example:</h2>
 * <pre>
 * Context context = ...; // Obtain the application context
 * Dependency dependency = ...; // Obtain the dependency checker
 * 
 * SearchReplaceCommand command = new SearchReplaceCommand(
 *     context,
 *     dependency,
 *     "http://old-url.com",
 *     "http://new-url.com",
 *     true,  // Target all tables
 *     false  // Do not perform a dry run
 * );
 * 
 * try {
 *     CommandOutput output = command.execute();
 *     if (output.isSuccessful()) {
 *         System.out.println("Search-replace operation completed successfully.");
 *     } else {
 *         System.err.println("Search-replace operation failed: " + output.getErrorOutput());
 *     }
 * } catch (PHPNotInstalledException | WPCliNotInstalledException e) {
 *     System.err.println("Error: " + e.getMessage());
 * }
 * </pre>
 * 
 * <h2>Parameters:</h2>
 * <ul>
 *   <li>{@code oldValue} - The value to search for in the database.</li>
 *   <li>{@code newValue} - The value to replace the old value with.</li>
 *   <li>{@code allTables} - Whether to target all tables in the database.</li>
 *   <li>{@code dryRun} - Whether to perform a dry run (preview changes without applying them).</li>
 * </ul>
 * 
 * <h2>Dependencies:</h2>
 * <p>
 * This class relies on the following components:
 * </p>
 * <ul>
 *   <li>{@link Context} - Provides access to the core components of the application, such as the {@link Shell} and {@link FileManager}.</li>
 *   <li>{@link Dependency} - Ensures that WP-CLI is installed before executing the command.</li>
 * </ul>
 * 
 * @see BaseWpCommand
 * @see CommandOutput
 */
public class SearchReplaceCommand extends BaseWpCommand 
{
    private final String oldValue;
    private final String newValue;
    private final boolean allTables;
    private final boolean dryRun;

    /**
     * Constructs a {@code SearchReplaceCommand} instance with the specified parameters.
     *
     * @param context The {@link Context} object providing access to core components.
     * @param dependency The {@link Dependency} object used to check for required dependencies.
     * @param oldValue The value to search for in the database.
     * @param newValue The value to replace the old value with.
     * @param allTables Whether to target all tables in the database.
     * @param dryRun Whether to perform a dry run (preview changes without applying them).
     */
    public SearchReplaceCommand(Context context, Dependency dependency, String oldValue, String newValue, boolean allTables, boolean dryRun) 
    {
        super(context, dependency);
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.allTables = allTables;
        this.dryRun = dryRun;
    }

    /**
     * Executes the search-and-replace command using WP-CLI.
     *
     * <p>
     * This method constructs the WP-CLI command based on the provided parameters
     * and executes it using the {@link Shell} from the {@link Context}.
     * </p>
     *
     * @return A {@link CommandOutput} object containing the result of the command execution.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     */
    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        // Ensure WP-CLI is installed
        dependency.isWpCliInstalled();

        // Construct the WP-CLI command
        String command = String.format(
            "%s %s search-replace --path=%s '%s' '%s' %s %s",
            this.context.getWpModel().getPhp(),
            this.context.getWpModel().getWp(),
            this.context.getFileManager().getWorkingDirectory(),
            this.oldValue,
            this.newValue,
            this.allTables ? "--all-tables" : "",
            this.dryRun ? "--dry-run" : ""
        );

        // Execute the command and return the result
        return context.getShell().executeCommand(command);
    }
}