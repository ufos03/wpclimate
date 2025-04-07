package com.wpclimate.cli.wpcommands.registrar;

import java.util.Set;
import org.reflections.Reflections;

import com.wpclimate.cli.wpcommands.BaseWpCommand;

/**
 * The {@code CommandRegistrar} class is responsible for dynamically scanning and registering
 * all WP-CLI commands annotated with {@link WpCommand}.
 *
 * <p>
 * This class uses the Reflections library to scan the specified package for classes annotated
 * with {@link WpCommand}. It ensures that all commands are automatically registered in the
 * {@link BaseWpCommand} registry, eliminating the need for manual registration.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Scans the package {@code com.wpclimate.cli.wpcommands} for classes annotated with {@link WpCommand}.</li>
 *   <li>Verifies that the annotated classes extend {@link BaseWpCommand}.</li>
 *   <li>Registers the commands in the {@link BaseWpCommand} registry using the name provided in the annotation.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * Call the {@link #registerAllCommands()} method at the application startup to ensure all commands
 * are registered before they are used.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * public class Main {
 *     public static void main(String[] args) {
 *         // Register all commands dynamically
 *         CommandRegistrar.registerAllCommands();
 *
 *         // Now you can execute commands using WpCli
 *         WpCli wpCli = new WpCli(...);
 *         wpCli.execute("rewrite");
 *     }
 * }
 * </pre>
 *
 * <h2>Dependencies:</h2>
 * <p>
 * This class depends on the Reflections library to perform package scanning. Ensure that the
 * Reflections library is included in your project dependencies.
 * </p>
 *
 * @see WpCommand
 * @see BaseWpCommand
 */
public class CommandRegistrar 
{
    /**
     * Scans the package {@code com.wpclimate.cli.wpcommands} and registers all classes annotated
     * with {@link WpCommand}.
     *
     * <p>
     * This method uses the Reflections library to find all classes annotated with {@link WpCommand}.
     * It verifies that each class extends {@link BaseWpCommand} and registers it in the
     * {@link BaseWpCommand} registry using the name provided in the annotation.
     * </p>
     *
     * <h2>Responsibilities:</h2>
     * <ul>
     *   <li>Initializes the Reflections library for the specified package.</li>
     *   <li>Finds all classes annotated with {@link WpCommand}.</li>
     *   <li>Ensures that each annotated class extends {@link BaseWpCommand}.</li>
     *   <li>Registers the command in the {@link BaseWpCommand} registry.</li>
     * </ul>
     *
     * <h2>Exceptions:</h2>
     * <p>
     * If a class annotated with {@link WpCommand} does not extend {@link BaseWpCommand}, it will
     * not be registered, and a warning may be logged.
     * </p>
     *
     * <h2>Dependencies:</h2>
     * <p>
     * This method depends on the Reflections library to perform package scanning. Ensure that the
     * Reflections library is included in your project dependencies.
     * </p>
     */
    public static void registerAllCommands() 
    {
        // Initialize Reflections for the package
        Reflections reflections = new Reflections("com.wpclimate.cli.wpcommands");

        // Find all classes annotated with WpCommand
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(WpCommand.class);

        for (Class<?> clazz : annotatedClasses) 
        {
            // Verify that the class extends BaseWpCommand
            if (BaseWpCommand.class.isAssignableFrom(clazz)) 
            {
                // Retrieve the annotation value (command name)
                WpCommand commandAnnotation = clazz.getAnnotation(WpCommand.class);
                String commandName = commandAnnotation.value();

                // Register the command
                @SuppressWarnings("unchecked")
                Class<? extends BaseWpCommand> commandClass = (Class<? extends BaseWpCommand>) clazz;
                BaseWpCommand.registerCommand(commandName, commandClass);
            }
        }
    }
}