package com.wpclimate.git.gitcommands.registrar;

import java.util.Set;

import org.reflections.Reflections;

import com.wpclimate.git.gitcommands.BaseGitCommand;

/**
 * The {@code GitCommandRegistrar} class registers all Git commands annotated with {@link GitCommand}.
 *
 * <p>
 * This class uses the Reflections library to scan the specified package for classes annotated
 * with {@link GitCommand}. It ensures that all commands are automatically registered in the
 * {@link BaseGitCommand} registry, eliminating the need for manual registration.
 * </p>
 */
public class GitCommandRegistrar {

    /**
     * Scans the package {@code com.wpclimate.git.gitcommands} and registers all classes annotated
     * with {@link GitCommand}.
     */
    public static void registerAllCommands() {
        Reflections reflections = new Reflections("com.wpclimate.git.gitcommands");

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(GitCommand.class);

        for (Class<?> clazz : annotatedClasses) 
        {
            if (BaseGitCommand.class.isAssignableFrom(clazz)) 
            {
                GitCommand commandAnnotation = clazz.getAnnotation(GitCommand.class);
                String commandName = commandAnnotation.value();

                @SuppressWarnings("unchecked")
                Class<? extends BaseGitCommand> commandClass = (Class<? extends BaseGitCommand>) clazz;
                BaseGitCommand.registerCommand(commandName, commandClass);
            } 
            else 
            {
                System.err.println("Class " + clazz.getName() + " does not extend BaseGitCommand.");
            }
        }
    }
}