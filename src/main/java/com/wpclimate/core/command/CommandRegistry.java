package com.wpclimate.core.command;

import com.wpclimate.cli.wpcommands.BaseWpCommand;
import com.wpclimate.git.gitcommands.BaseGitCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * The {@code CommandRegistry} class provides a centralized registry for discovering and cataloging
 * all available commands in the application using reflection.
 *
 * <p>
 * This class dynamically scans and registers commands from the WP-CLI and Git command groups.
 * It extracts metadata about commands and their parameters using annotations such as {@link CommandParam}.
 * The registry allows querying commands by name, group, or retrieving all registered commands.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Discovers and registers commands dynamically using reflection.</li>
 *   <li>Extracts metadata about command parameters using {@link CommandParam} annotations.</li>
 *   <li>Provides methods to query commands by name, group, or retrieve all commands.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code CommandRegistry} is initialized once during the application's startup phase.
 * Commands can then be queried dynamically. For example:
 * </p>
 * <pre>
 * CommandRegistry.initialize();
 * 
 * // Retrieve all commands
 * Map<String, CommandRegistry.CommandInfo> allCommands = CommandRegistry.getAllCommands();
 * 
 * // Retrieve a specific command
 * CommandRegistry.CommandInfo commandInfo = CommandRegistry.getCommand("search-replace");
 * 
 * // Retrieve commands by group
 * Map<String, CommandRegistry.CommandInfo> gitCommands = CommandRegistry.getCommandsByGroup("GIT");
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code CommandRegistry} class is thread-safe during initialization and querying.
 * The {@link #initialize()} method is synchronized to ensure that commands are registered
 * only once. Query methods such as {@link #getCommand(String)} and {@link #getAllCommands()}
 * are safe for concurrent access.
 * </p>
 *
 * @see CommandParam
 * @see BaseWpCommand
 * @see BaseGitCommand
 */
public class CommandRegistry {
    private static final Map<String, CommandInfo> COMMANDS = new HashMap<>();
    private static boolean initialized = false;
    
    /**
     * Initializes the command registry by discovering and registering all available commands.
     *
     * <p>
     * This method scans the WP-CLI and Git command groups using reflection and extracts
     * metadata about commands and their parameters. It ensures that the registry is
     * initialized only once.
     * </p>
     */
    public static synchronized void initialize() 
    {
        if (initialized) return;
        
        Map<String, Class<? extends BaseGitCommand>> gitCommands = BaseGitCommand.getRegisteredCommands();
        Map<String, Class<? extends BaseWpCommand>> wpCommands = BaseWpCommand.getRegisteredCommands();
        
        // Git
        for (Map.Entry<String, Class<? extends BaseGitCommand>> entry : gitCommands.entrySet()) 
        {
            String commandName = entry.getKey();
            Class<? extends BaseGitCommand> commandClass = entry.getValue();
            
            CommandInfo info = new CommandInfo(commandName, "GIT", commandClass);
            extractParameters(info, commandClass);
            
            COMMANDS.put(commandName, info);
        }
        
        // WP
        for (Map.Entry<String, Class<? extends BaseWpCommand>> entry : wpCommands.entrySet()) 
        {
            String commandName = entry.getKey();
            Class<? extends BaseWpCommand> commandClass = entry.getValue();
            
            CommandInfo info = new CommandInfo(commandName, "WP", commandClass);
            extractParameters(info, commandClass);
            
            COMMANDS.put(commandName, info);
        }
        
        initialized = true;
    }
    
    /**
     * Extracts parameters from the constructors of the specified command class using reflection.
     *
     * <p>
     * This method scans the constructors of the command class for {@link CommandParam} annotations
     * and adds the extracted parameter metadata to the {@link CommandInfo} object.
     * </p>
     *
     * @param info        The {@link CommandInfo} object to populate with parameter metadata.
     * @param commandClass The command class to scan for parameters.
     */
    private static void extractParameters(CommandInfo info, Class<?> commandClass) 
    {
        // Prima cerca le annotazioni CommandParam sui parametri dei costruttori
        boolean paramsFound = extractAnnotatedParameters(info, commandClass);

        if (!paramsFound)
            return;
    }
    
    /**
     * Extracts parameters annotated with {@link CommandParam} from the constructors of the specified command class.
     *
     * <p>
     * This method scans the constructors of the command class and retrieves metadata about
     * parameters annotated with {@link CommandParam}. If no annotated parameters are found,
     * it attempts to extract metadata from fields annotated with {@link CommandParam}.
     * </p>
     *
     * @param info        The {@link CommandInfo} object to populate with parameter metadata.
     * @param commandClass The command class to scan for annotated parameters.
     * @return {@code true} if annotated parameters were found, {@code false} otherwise.
     */
    private static boolean extractAnnotatedParameters(CommandInfo info, Class<?> commandClass) 
    {
        boolean found = false;
        
        Constructor<?>[] constructors = commandClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();
            if (parameters.length > 1) 
            {
                // Cerca nei parametri dopo il context
                for (int i = 1; i < parameters.length; i++) 
                {
                    Parameter param = parameters[i];
                    CommandParam annotation = param.getAnnotation(CommandParam.class);
                    if (annotation != null) 
                    {
                        info.addParam(
                            annotation.name(),
                            annotation.type(),
                            annotation.required(),
                            annotation.defaultValue(),
                            annotation.description()
                        );
                        found = true;
                    }

                    else if (Map.class.isAssignableFrom(param.getType()))
                        found = found || extractFieldAnnotations(info, commandClass);
                }
            }
        }
        
        return found;
    }
    
    /**
     * Extracts parameters annotated with {@link CommandParam} from the fields of the specified command class.
     *
     * <p>
     * This method scans the fields of the command class for {@link CommandParam} annotations
     * and adds the extracted parameter metadata to the {@link CommandInfo} object.
     * </p>
     *
     * @param info        The {@link CommandInfo} object to populate with parameter metadata.
     * @param commandClass The command class to scan for annotated fields.
     * @return {@code true} if annotated fields were found, {@code false} otherwise.
     */
    private static boolean extractFieldAnnotations(CommandInfo info, Class<?> commandClass) {
        boolean found = false;
        
        java.lang.reflect.Field[] fields = commandClass.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            CommandParam annotation = field.getAnnotation(CommandParam.class);
            if (annotation != null) 
            {
                info.addParam(
                    annotation.name(),
                    annotation.type(),
                    annotation.required(),
                    annotation.defaultValue(),
                    annotation.description()
                );
                found = true;
            }
        }
        
        return found;
    }
    
    /**
     * Retrieves all registered commands.
     *
     * <p>
     * This method returns an unmodifiable map of all registered commands, where the keys
     * are command names and the values are {@link CommandInfo} objects containing metadata
     * about the commands.
     * </p>
     *
     * @return An unmodifiable map of all registered commands.
     */
    public static Map<String, CommandInfo> getAllCommands() 
    {
        if (!initialized) initialize();
        return Collections.unmodifiableMap(COMMANDS);
    }
    
    /**
     * Retrieves metadata about a specific command by name.
     *
     * <p>
     * This method returns a {@link CommandInfo} object containing metadata about the specified
     * command, or {@code null} if the command is not found.
     * </p>
     *
     * @param name The name of the command to retrieve.
     * @return A {@link CommandInfo} object containing metadata about the command, or {@code null} if not found.
     */
    public static CommandInfo getCommand(String name) 
    {
        if (!initialized) initialize();
        return COMMANDS.get(name);
    }
    
    /**
     * Retrieves all commands belonging to a specific group.
     *
     * <p>
     * This method returns a map of commands belonging to the specified group, where the keys
     * are command names and the values are {@link CommandInfo} objects containing metadata
     * about the commands.
     * </p>
     *
     * @param group The name of the group to retrieve commands for (e.g., "GIT", "WP").
     * @return A map of commands belonging to the specified group.
     */
    public static Map<String, CommandInfo> getCommandsByGroup(String group) 
    {
        if (!initialized) initialize();
        
        Map<String, CommandInfo> result = new HashMap<>();
        for (Map.Entry<String, CommandInfo> entry : COMMANDS.entrySet()) {
            if (entry.getValue().getGroup().equals(group)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * Represents metadata about a command.
     *
     * <p>
     * This class encapsulates information about a command, including its name, group,
     * associated class, parameters, and description.
     * </p>
     */
    public static class CommandInfo {
        private final String name;
        private final String group;
        private final Class<?> commandClass;
        private final Map<String, ParamInfo> parameters = new LinkedHashMap<>();
        private String description;
        
        public CommandInfo(String name, String group, Class<?> commandClass) {
            this.name = name;
            this.group = group;
            this.commandClass = commandClass;
            
            // Estrai la descrizione dalla classe
            this.extractDescription();
        }
        
        private void extractDescription() {
            // Prova a estrarre dalla Javadoc o dalle annotazioni disponibili
            try {
                if (commandClass.isAnnotationPresent(java.lang.annotation.Documented.class)) {
                    this.description = commandClass.getAnnotation(java.lang.annotation.Documented.class).toString();
                } else {
                    // Fallback sul nome della classe
                    this.description = commandClass.getSimpleName();
                }
            } catch (Exception e) {
                this.description = commandClass.getSimpleName();
            }
        }
        
        /**
         * Adds metadata about a parameter to the command.
         *
         * @param name        The name of the parameter.
         * @param type        The type of the parameter.
         * @param required    Whether the parameter is required.
         * @param defaultValue The default value of the parameter.
         * @param description A description of the parameter.
         * @return The {@link CommandInfo} object for method chaining.
         */
        public CommandInfo addParam(String name, ParamType type, boolean required, String defaultValue, String description) {
            parameters.put(name, new ParamInfo(name, type, required, defaultValue, description));
            return this;
        }
        
        // Getters
        public String getName() { return name; }
        public String getGroup() { return group; }
        public Class<?> getCommandClass() { return commandClass; }
        public Map<String, ParamInfo> getParameters() { return parameters; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        @Override
        public String toString() {
            return name + " (" + group + ")";
        }
    }
    
    /**
     * Represents metadata about a command parameter.
     *
     * <p>
     * This class encapsulates information about a parameter, including its name, type,
     * whether it is required, its default value, and a description.
     * </p>
     */
    public static class ParamInfo {
        private final String name;
        private final ParamType type;
        private final boolean required;
        private final String defaultValue;
        private final String description;
        
        /**
         * Constructs a {@code ParamInfo} object with the specified metadata.
         *
         * @param name        The name of the parameter.
         * @param type        The type of the parameter.
         * @param required    Whether the parameter is required.
         * @param defaultValue The default value of the parameter.
         * @param description A description of the parameter.
         */
        public ParamInfo(String name, ParamType type, boolean required, String defaultValue, String description) {
            this.name = name;
            this.type = type;
            this.required = required;
            this.defaultValue = defaultValue;
            this.description = description;
        }
        
        // Getters
        public String getName() { return name; }
        public ParamType getType() { return type; }
        public boolean isRequired() { return required; }
        public String getDefaultValue() { return defaultValue; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return name + (required ? " (required)" : "") + ": " + description;
        }
    }
}
