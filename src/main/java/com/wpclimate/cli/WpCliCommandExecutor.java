package com.wpclimate.cli;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.cli.wpcommands.FlushTransientCommand;
import com.wpclimate.cli.wpcommands.RewriteCommand;
import com.wpclimate.cli.wpcommands.SearchReplaceCommand;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code WpCliCommandExecutor} class is responsible for executing WP-CLI commands.
 * 
 * <p>
 * This class provides methods to execute various WP-CLI commands, such as:
 * </p>
 * <ul>
 *   <li>Search-and-replace operations in the WordPress database.</li>
 *   <li>Flushing all transients stored in the WordPress database.</li>
 *   <li>Rewriting WordPress rules.</li>
 * </ul>
 * 
 * <p>
 * The class relies on the {@link Context} to access core components like the {@link Shell}
 * and {@link FileManager}, and on the {@link Dependency} to ensure that required dependencies
 * (e.g., PHP and WP-CLI) are installed before executing commands.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Constructs and executes WP-CLI commands using the provided {@link Context} and {@link Dependency}.</li>
 *   <li>Handles the results of the commands and returns them as {@link CommandOutput} objects.</li>
 *   <li>Ensures that the environment is properly configured before executing commands.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * To use this class, create an instance by providing a {@link Context} and {@link Dependency},
 * and then call the appropriate method to execute a specific WP-CLI command.
 * </p>
 * 
 * <h2>Example:</h2>
 * <pre>
 * Context context = ...; // Obtain the application context
 * Dependency dependency = ...; // Obtain the dependency checker
 * 
 * WpCliCommandExecutor executor = new WpCliCommandExecutor(context, dependency);
 * 
 * try {
 *     // Execute a search-and-replace command
 *     CommandOutput output = executor.doSearchReplace("http://old-url.com", "http://new-url.com", true, false);
 *     if (output.isSuccessful()) {
 *         System.out.println("Search-replace operation completed successfully.");
 *     } else {
 *         System.err.println("Search-replace operation failed: " + output.getErrorOutput());
 *     }
 * 
 *     // Execute a flush transients command
 *     CommandOutput flushOutput = executor.doFlushTransient();
 *     if (flushOutput.isSuccessful()) {
 *         System.out.println("All transients deleted successfully.");
 *     } else {
 *         System.err.println("Failed to delete transients: " + flushOutput.getErrorOutput());
 *     }
 * } catch (PHPNotInstalledException | WPCliNotInstalledException e) {
 *     System.err.println("Error: " + e.getMessage());
 * }
 * </pre>
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
 * @see Context
 * @see Dependency
 * @see CommandOutput
 * @see SearchReplaceCommand
 * @see FlushTransientCommand
 * @see RewriteCommand
 */
public class WpCliCommandExecutor 
{

    private final Context context;
    private final Dependency dependency;

    /**
     * Constructs a {@code WpCliCommandExecutor} with the specified context and dependency.
     *
     * @param context    The {@link Context} object providing access to core components.
     * @param dependency The {@link Dependency} object used to check for required dependencies.
     */
    public WpCliCommandExecutor(Context context, Dependency dependency) 
    {
        this.context = context;
        this.dependency = dependency;
    }

    /**
     * Executes a search-and-replace command in the WordPress database.
     *
     * <p>
     * This command replaces occurrences of a specific value in the database with a new value.
     * It supports additional options such as targeting all tables and performing a dry run
     * to preview the changes without applying them.
     * </p>
     *
     * @param oldVal    The value to search for.
     * @param newVal    The value to replace with.
     * @param allTables Whether to target all tables in the database.
     * @param dryRun    Whether to perform a dry run (preview changes without applying them).
     * @return A {@link CommandOutput} object containing the result of the command execution.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     */
    public CommandOutput doSearchReplace(String oldVal, String newVal, boolean allTables, boolean dryRun) throws PHPNotInstalledException, WPCliNotInstalledException
    {
        SearchReplaceCommand cmd = new SearchReplaceCommand(context, dependency, oldVal, newVal, allTables, dryRun);
        return cmd.execute();
    }

    /**
     * Executes a command to flush all transients stored in the WordPress database.
     *
     * <p>
     * Transients are temporary options stored in the WordPress database, often used
     * for caching purposes. This command deletes all transients.
     * </p>
     *
     * @return A {@link CommandOutput} object containing the result of the command execution.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     */
    public CommandOutput doFlushTransient() throws PHPNotInstalledException, WPCliNotInstalledException
    {
        FlushTransientCommand cmd = new FlushTransientCommand(context, dependency);
        return cmd.execute();
    }

    /**
     * Executes a command to rewrite WordPress rules.
     *
     * <p>
     * This command regenerates the rewrite rules for the WordPress installation.
     * </p>
     *
     * @return A {@link CommandOutput} object containing the result of the command execution.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     */
    public CommandOutput doRewriteRules() throws PHPNotInstalledException, WPCliNotInstalledException
    {
        RewriteCommand cmd = new RewriteCommand(context, dependency);
        return cmd.execute();
    }
}