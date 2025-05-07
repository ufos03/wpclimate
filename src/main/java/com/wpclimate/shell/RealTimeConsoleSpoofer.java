package com.wpclimate.shell;

/**
 * The {@code InteractionHandler} interface defines methods for handling
 * interactive input and output during command execution.
 */
public interface RealTimeConsoleSpoofer // TODO: DOC
{
    /**
     * Displays a message to the user.
     *
     * @param message The message to display.
     * @param isError Indicates if a message must be treated as an error
     */
    void displayMessage(String message, boolean isError);
}