package com.wpclimate.shell;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code CommandBuilder} class creates a secure and readable representation of
 * a command to be executed via {@link CommandExecutor}.
 * It handles both simple commands and pipelines (using the '|' character).
 */
public class CommandBuilder
{
    // The list of sub-commands (for a pipeline). Each sub-command is a list of tokens.
    private final List<List<String>> commands = new ArrayList<>();

    /**
     * Default constructor.
     */
    public CommandBuilder() {}

    /**
     * Constructor that accepts a command string (with or without a pipeline).
     *
     * @param commandLine The command (with optional arguments) as a string.
     */
    public CommandBuilder(String commandLine) {
        setCommand(commandLine);
    }

    /**
     * Sets the command by parsing the input string.
     * If the pipe character ('|') is detected, the command is interpreted as a pipeline,
     * otherwise as a single command.
     *
     * @param commandLine The string containing the command.
     */
    public void setCommand(String commandLine) {
        this.commands.clear();

        // Clean the string by removing disallowed characters
        commandLine = sanitizeCommandLine(commandLine);

        // Split by the '|' character to determine if it's a pipeline
        if (commandLine.contains("|")) {
            String[] parts = commandLine.split("\\|");
            for (String part : parts) {
                List<String> tokenList = parseTokens(part.trim());
                this.commands.add(tokenList);
            }
        } else {
            // Single command
            List<String> tokenList = parseTokens(commandLine.trim());
            this.commands.add(tokenList);
        }
    }

    /**
     * Returns the list of sub-commands (each is a list of tokens).
     * If there is more than one list, it means the command represents a pipeline.
     *
     * @return A list of sub-commands, where each sub-command is a list of tokens.
     * @throws IllegalStateException If the command has not been initialized.
     */
    public List<List<String>> getCommand() {
        if (commands.isEmpty()) {
            throw new IllegalStateException("Command not initialized. " +
                    "Call setCommand(...) before getCommand().");
        }
        // Return a defensive copy
        List<List<String>> copy = new ArrayList<>();
        for (List<String> cmd : commands) {
            copy.add(new ArrayList<>(cmd));
        }
        return copy;
    }

    /**
     * Indicates whether the command represents a pipeline (i.e., contains more than one sub-command).
     *
     * @return {@code true} if it is a pipeline, {@code false} otherwise.
     */
    public boolean isPipeline() {
        return commands.size() > 1;
    }

    /**
     * Parses a command string (without the '|') into a list of tokens, handling
     * the removal of any leading and trailing quotes and splitting by spaces.
     *
     * @param input The command (or sub-command) to parse.
     * @return A list of tokens.
     */
    private List<String> parseTokens(String input) {
        // Split by spaces
        String[] rawTokens = input.split("\\s+");
        List<String> tokenList = new ArrayList<>();

        for (String token : rawTokens) {
            // Remove any leading/trailing single or double quotes (e.g., "'http://...'")
            token = token.replaceAll("^['\"]+", "");
            token = token.replaceAll("['\"]+$", "");
            
            // Ignore empty tokens (may occur if there are multiple spaces)
            if (!token.isEmpty()) {
                tokenList.add(token);
            }
        }
        return tokenList;
    }

    /**
     * Cleans the string by removing disallowed characters. Allowed characters include:
     * - Letters and numbers [a-zA-Z0-9]
     * - Spaces \s
     * - Underscore _
     * - Slash /
     * - Dash -
     * - Pipe |
     * - Quotes " and '
     * - Equals sign =
     * - Period .
     * - Colon :
     * - At sign: @
     *
     * Add/remove characters as needed based on what you want to support.
     *
     * @param commandLine The command line string to sanitize.
     * @return The sanitized command line string.
     */
    private String sanitizeCommandLine(String commandLine) {
        return commandLine.replaceAll("[^a-zA-Z0-9\\s/_\\-\\|=\"'\\.:@]", "")
        .trim();
    }
}