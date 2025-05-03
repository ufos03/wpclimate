package com.wpclimate.git.gitcommands.registrar;

import java.lang.reflect.Constructor;
import java.util.Map;

import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.gitcommands.BaseGitCommand;

/**
 * The {@code GitCommandFactory} class creates instances of Git commands.
 */
public class GitCommandFactory {

    /**
     * Creates an instance of a Git command.
     *
     * @param name    The name of the command.
     * @param context The {@link GitContext} object providing access to core components.
     * @param params  A map of parameters to pass to the command, or {@code null} if no parameters are required.
     * @return An instance of the specified command.
     * @throws Exception If the command cannot be created.
     */
    public static BaseGitCommand createCommand(String name, GitContext context, Map<String, Object> params) throws Exception {
        Class<? extends BaseGitCommand> commandClass = BaseGitCommand.getCommandClass(name);

        if (commandClass == null) {
            throw new IllegalArgumentException("Command not found: " + name);
        }

        try {
            Constructor<? extends BaseGitCommand> constructor = commandClass.getConstructor(GitContext.class, Map.class);
            return constructor.newInstance(context, params != null ? params : Map.of());
        } catch (NoSuchMethodException e) {
            Constructor<? extends BaseGitCommand> constructor = commandClass.getConstructor(GitContext.class);
            return constructor.newInstance(context);
        }
    }
}