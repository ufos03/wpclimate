package com.wpclimate.cli.wpcommands.registrar;

import com.wpclimate.cli.core.WpCliContext;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.wpcommands.BaseWpCommand;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * The {@code WpCommandFactory} class is responsible for creating instances of WP-CLI commands.
 *
 * <p>
 * This factory dynamically creates instances of classes that extend {@link BaseWpCommand}.
 * It uses reflection to find and invoke the appropriate constructor for the specified command.
 * The factory supports two types of constructors:
 * </p>
 * <ul>
 *   <li>A constructor that accepts {@code Context}, {@code Dependency}, and {@code Map<String, Object>}.</li>
 *   <li>A fallback constructor that accepts only {@code Context} and {@code Dependency}.</li>
 * </ul>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Retrieves the class of the command from the {@link BaseWpCommand} registry.</li>
 *   <li>Attempts to create an instance of the command using the appropriate constructor.</li>
 *   <li>Handles fallback logic if the primary constructor is not available.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * Use this factory to create instances of WP-CLI commands dynamically at runtime. The factory
 * ensures that the correct constructor is used based on the parameters provided.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * Map<String, Object> params = Map.of("option", "value");
 * BaseWpCommand command = WpCommandFactory.createCommand("rewrite", context, dependency, params);
 * command.execute();
 * </pre>
 *
 * @see BaseWpCommand
 */
public class WpCommandFactory 
{

    /**
     * Creates an instance of a WP-CLI command.
     *
     * <p>
     * This method retrieves the class of the specified command from the {@link BaseWpCommand} registry
     * and attempts to create an instance using reflection. It first tries to use a constructor that
     * accepts {@code Context}, {@code Dependency}, and {@code Map<String, Object>}. If such a constructor
     * is not available, it falls back to a constructor that accepts only {@code Context} and {@code Dependency}.
     * </p>
     *
     * <h2>Responsibilities:</h2>
     * <ul>
     *   <li>Retrieves the command class from the {@link BaseWpCommand} registry.</li>
     *   <li>Attempts to create an instance using the constructor with {@code Map<String, Object>}.</li>
     *   <li>Falls back to the simpler constructor if the primary one is not available.</li>
     * </ul>
     *
     * <h2>Exceptions:</h2>
     * <p>
     * Throws an {@link IllegalArgumentException} if the command is not found in the registry.
     * Throws a {@link ReflectiveOperationException} if the command cannot be instantiated.
     * </p>
     *
     * @param name       The name of the command to create.
     * @param context    The application context.
     * @param dependency The dependency checker.
     * @param params     A map of parameters to pass to the command.
     * @return An instance of the specified command.
     * @throws Exception If the command cannot be instantiated.
     */
    public static BaseWpCommand createCommand(String name, WpCliContext context, Dependency dependency, Map<String, Object> params) throws Exception 
    {
        Class<? extends BaseWpCommand> commandClass = BaseWpCommand.getCommandClass(name);
        
        if (commandClass == null)
            throw new IllegalArgumentException("Command not found: " + name);

        try 
        {
            Constructor<? extends BaseWpCommand> constructor = commandClass.getConstructor(WpCliContext.class, Map.class);
            return constructor.newInstance(context, params != null ? params : Map.of());
        } 
        catch (NoSuchMethodException e) 
        {
            Constructor<? extends BaseWpCommand> constructor = commandClass.getConstructor(WpCliContext.class);
            return constructor.newInstance(context);
        }
    }
}