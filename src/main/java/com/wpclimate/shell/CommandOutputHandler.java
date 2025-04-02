package com.wpclimate.shell;


/**
 * The {@code CommandOutputHandler} interface defines a method for handling
 * the output of a WP-CLI command.
 */
public interface CommandOutputHandler 
{

    /**
     * Handles the output of a WP-CLI command.
     *
     * @param output The {@link CommandOutput} object containing the command's output.
     */
    void handleOutput(CommandOutput output);
}