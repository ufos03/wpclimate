package com.wpclimate.Shell;

import java.util.ArrayList;
import java.util.List;

/**
 * The CommandBuilder class creates a secure and readable representation of a command to be executed via ProcessBuilder.
 * It transparently handles both simple commands and pipelines.
 * 
 * A simple command is represented as a list of tokens (contained in a single list),
 * while a pipeline is represented as a list of sub-commands, each of which is itself
 * a list of tokens.
 */
public class CommandBuilder
{
    // The data structure that contains one or more commands (each as a list of tokens).
    private List<List<String>> commands = new ArrayList<>();

    /**
     * Default constructor.
     */
    public CommandBuilder() {}

    /**
     * Constructor that accepts a command string (with or without a pipeline).
     * @param commandLine The command (with optional arguments) as a string.
     */
    public CommandBuilder(String commandLine) 
    {
        setCommand(commandLine);
    }

    /**
     * Sets the command by parsing the input string.
     * If the pipe character ("|") is detected, the command is interpreted as a pipeline,
     * otherwise as a simple command.
     * @param commandLine The string containing the command.
     */
    public void setCommand(String commandLine) 
    {
        this.commands.clear();
        // Clean the string by removing any disallowed characters
        commandLine = sanitizeCommandLine(commandLine);
        // If it contains the pipe character, consider it a pipeline
        if (commandLine.contains("|")) {
            String[] parts = commandLine.split("\\|");
            for (String part : parts) 
            {
                String trimmed = part.trim();
                String[] tokens = trimmed.split("\\s+");
                List<String> tokenList = new ArrayList<>();
                
                for (String token : tokens)
                    tokenList.add(token);
                this.commands.add(tokenList);
            }
        } 
        else 
        {
            // Simple command
            String[] tokens = commandLine.split("\\s+");
            List<String> tokenList = new ArrayList<>();
            
            for (String token : tokens)
                tokenList.add(token);

            commands.add(tokenList);
        }
    }

    /**
     * Returns the representation of the command.
     * For a simple command, the list contains a single element, while for a pipeline it contains more than one.
     * @return The list of commands, each represented as a list of tokens.
     */
    public List<List<String>> getCommand() 
    {
        if (commands.isEmpty()) {
            throw new IllegalStateException("Command not initialized. Set the command before getting it.");
        }
        List<List<String>> copy = new ArrayList<>();
        
        for (List<String> cmd : commands)
            copy.add(new ArrayList<>(cmd));

        //System.out.println(copy);
        return copy;
    }
    
    /**
     * Indicates whether the command represents a pipeline (i.e., if it contains more than one sub-command).
     * @return true if the command is a pipeline, false otherwise.
     */
    public boolean isPipeline() 
    {
        return commands.size() > 1;
    }

    /**
     * Cleans the command string by removing disallowed characters.
     * Only letters, numbers, spaces, underscores, slashes, dashes, and pipes are kept.
     * @param commandLine The command string to sanitize.
     * @return The sanitized string.
     */
    private String sanitizeCommandLine(String commandLine) 
    {
        commandLine = commandLine.replaceAll("[^a-zA-Z0-9\\s/_\\-\\|]", "");
        return commandLine.trim();
    }
}