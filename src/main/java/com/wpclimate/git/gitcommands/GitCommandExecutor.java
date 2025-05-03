package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.git.gitcommands.registrar.GitCommandRegistrar;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.gitcommands.registrar.GitCommandFactory;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code WpCliCommandExecutor} class is responsible for executing WP-CLI commands.
 *
 * <p>
 * This class acts as a bridge between the application and the WP-CLI commands. It initializes
 * the command registry by dynamically registering all available commands using the {@link CommandRegistrar}.
 * It then uses the {@link WpCommandFactory} to create and execute the specified command.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Initializes the command registry by registering all commands at startup.</li>
 *   <li>Executes WP-CLI commands by name using the {@link WpCommandFactory}.</li>
 *   <li>Handles the lifecycle of command execution, including error handling.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * Create an instance of this class by providing the application {@link WpCliContext}.
 * Use the {@link #executeCommand(String)} method to execute a WP-CLI command by its name.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * Context context = new Context(..params);
 * WpCliCommandExecutor executor = new WpCliCommandExecutor(context);
 *
 * try {
 *     CommandOutput output = executor.executeCommand("rewrite");
 *     if (output.isSuccessful()) {
 *         System.out.println("Command executed successfully.");
 *     } else {
 *         System.err.println("Command failed: " + output.getErrorOutput());
 *     }
 * } catch (Exception e) {
 *     e.printStackTrace();
 * }
 * </pre>
 *
 * @see CommandRegistrar
 * @see WpCommandFactory
 * @see BaseWpCommand
 */
public class GitCommandExecutor 
{

    private final GitContext context;

    /**
     * Constructs a {@code WpCliCommandExecutor} with the specified {@link WpCliContext}.
     *
     * <p>
     * This constructor initializes the command registry by calling {@link CommandRegistrar#registerAllCommands()}.
     * </p>
     *
     * @param context    The application context, providing access to core components.
     */
    public GitCommandExecutor(GitContext context) 
    {
        this.context = context;
        GitCommandRegistrar.registerAllCommands();
    }

    /**
     * Executes a WP-CLI command by name.
     *
     * <p>
     * This method retrieves the command class from the registry using the {@link WpCommandFactory},
     * creates an instance of the command, and executes it. If the command execution fails, an exception
     * is thrown.
     * </p>
     *
     * @param commandName The name of the command to execute.
     * @param params A map of parameters to pass to the command.
     * @return The output of the command as a {@link CommandOutput} object.
     * @throws Exception If the command execution fails.
     */
    public CommandOutput executeCommand(String commandName, Map<String, Object> params) throws Exception 
    {
        BaseGitCommand command = GitCommandFactory.createCommand(commandName, this.context, params);
        return command.execute();
    }
}