package com.wpclimate.cli.wpcommands;

import java.util.Map;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.exceptions.*;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code SearchReplaceCommand} class implements the WP-CLI "search-replace" command.
 *
 * <p>
 * This command is used to search and replace text in the WordPress database. It supports
 * additional options such as performing the operation on all tables and running in dry-run mode.
 * The parameters for the command are passed as a {@link Map} and extracted during initialization.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Extracts and validates parameters required for the "search-replace" command.</li>
 *   <li>Constructs the WP-CLI command string based on the provided parameters.</li>
 *   <li>Executes the command and returns the result.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * This class is instantiated dynamically by the {@link WpCommandFactory} and executed via the
 * {@link WpCliCommandExecutor}. The parameters for the command must include:
 * </p>
 * <ul>
 *   <li>{@code oldValue}: The value to search for in the database.</li>
 *   <li>{@code newValue}: The value to replace the search value with.</li>
 *   <li>{@code allTables} (optional): A boolean indicating whether to perform the operation on all tables.</li>
 *   <li>{@code dryRun} (optional): A boolean indicating whether to run the command in dry-run mode.</li>
 * </ul>
 *
 * <h2>Example:</h2>
 * <pre>
 * Map<String, Object> params = Map.of(
 *     "oldValue", "http://old-url.com",
 *     "newValue", "http://new-url.com",
 *     "allTables", true,
 *     "dryRun", true
 * );
 * SearchReplaceCommand command = new SearchReplaceCommand(context, dependency, params);
 * CommandOutput output = command.execute();
 * </pre>
 *
 * @see BaseWpCommand
 * @see WpCommandFactory
 * @see WpCliCommandExecutor
 */
@WpCommand("search-replace")
public class SearchReplaceCommand extends BaseWpCommand 
{
    private String oldValue;
    private String newValue;
    private boolean allTables;
    private boolean dryRun;

    /**
     * Constructs a {@code SearchReplaceCommand} with the specified context, dependency, and parameters.
     *
     * <p>
     * The parameters are extracted from the provided {@link Map} and used to configure the command.
     * </p>
     *
     * @param context    The application context, providing access to core components.
     * @param dependency The dependency checker, ensuring required dependencies are available.
     * @param params     A map of parameters for the command. Must include {@code oldValue} and {@code newValue}.
     */
    public SearchReplaceCommand(Context context, Dependency dependency, Map<String, Object> params) 
    {
        super(context, dependency);

        // Extract parameters from the map
        this.oldValue = (String) params.get("oldValue");
        this.newValue = (String) params.get("newValue");
        this.allTables = params.getOrDefault("allTables", false) instanceof Boolean && (Boolean) params.get("allTables");
        this.dryRun = params.getOrDefault("dryRun", false) instanceof Boolean && (Boolean) params.get("dryRun");
    }

    /**
     * Executes the "search-replace" WP-CLI command.
     *
     * <p>
     * This method constructs the WP-CLI command string based on the provided parameters and executes it.
     * It validates that both {@code oldValue} and {@code newValue} are not {@code null} before execution.
     * </p>
     *
     * @return The output of the command as a {@link CommandOutput} object.
     * @throws PHPNotInstalledException If PHP is not installed or cannot be found.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or cannot be found.
     * @throws IllegalArgumentException If {@code oldValue} or {@code newValue} is {@code null}.
     */
    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        // Ensure WP-CLI is installed
        dependency.isWpCliInstalled();

        // Validate that oldValue and newValue are not null
        if (this.oldValue == null || this.newValue == null) {
            throw new IllegalArgumentException("Both 'oldValue' and 'newValue' must be provided.");
        }

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
        
        return context.getShell().executeCommand(command);
    }
}