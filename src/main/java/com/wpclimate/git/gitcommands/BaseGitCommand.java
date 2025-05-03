package com.wpclimate.git.gitcommands;

import java.util.HashMap;
import java.util.Map;

import com.wpclimate.git.core.GitContext;
import com.wpclimate.shell.CommandOutput;

/**
 * The {@code BaseGitCommand} class serves as an abstract base class for all Git commands.
 *
 * <p>
 * This class provides a foundation for implementing specific Git commands by encapsulating
 * the shared functionality and dependencies required for executing commands.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Maintains a registry of available commands for dynamic command creation.</li>
 *   <li>Provides access to the {@link GitContext} object, which contains the core components of the application.</li>
 * </ul>
 */
public abstract class BaseGitCommand {
    protected final GitContext context;

    private static final Map<String, Class<? extends BaseGitCommand>> COMMAND_REGISTRY = new HashMap<>();

    public BaseGitCommand(GitContext context) {
        this.context = context;
    }

    /**
     * Registers a command in the registry.
     *
     * @param name  The name of the command.
     * @param clazz The class of the command.
     */
    public static void registerCommand(String name, Class<? extends BaseGitCommand> clazz) {
        COMMAND_REGISTRY.put(name, clazz);
    }

    /**
     * Retrieves the command class from the registry.
     *
     * @param name The name of the command.
     * @return The class of the command, or {@code null} if the command is not registered.
     */
    public static Class<? extends BaseGitCommand> getCommandClass(String name) {
        return COMMAND_REGISTRY.get(name);
    }

    /**
     * Retrieves all registered commands.
     *
     * @return A map of all registered commands.
     */
    public static Map<String, Class<? extends BaseGitCommand>> getRegisteredCommands() {
        return COMMAND_REGISTRY;
    }

    /**
     * Executes the Git command.
     *
     * @return The result of the command execution.
     * @throws Exception If an error occurs during execution.
     */
    public abstract CommandOutput execute() throws Exception;
}