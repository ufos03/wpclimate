package com.wpclimate.core;

import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.CommandOutputHandler;

/**
 * The {@code OutputHandlerFactory} class is responsible for managing the output
 * of shell commands executed in the application.
 *
 * <p>
 * This class provides a mechanism to handle and optionally display the output
 * of shell commands using a {@link CommandOutputHandler}. It allows enabling
 * or disabling the display of output dynamically.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Encapsulates a {@link CommandOutputHandler} to process command outputs.</li>
 *   <li>Provides a toggle to enable or disable the display of command outputs.</li>
 *   <li>Handles the printing of command outputs based on the configuration.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * CommandOutputHandler handler = new CustomOutputHandler();
 * OutputHandlerFactory factory = new OutputHandlerFactory(handler, true);
 * factory.print(commandOutput);
 * </pre>
 *
 * @see CommandOutput
 * @see CommandOutputHandler
 */
public class OutputHandlerFactory 
{
    private CommandOutputHandler handler = null;
    private boolean showOutput;

    /**
     * Constructs an {@code OutputHandlerFactory} with the specified handler and output visibility.
     *
     * @param handler    The {@link CommandOutputHandler} used to process command outputs.
     *                   If {@code null}, no handler will be set.
     * @param showOutput A boolean indicating whether the output should be displayed.
     */
    public OutputHandlerFactory(CommandOutputHandler handler, boolean showOutput)
    {
        if (handler == null)
            return;
        
        this.handler = handler;
        this.setShowOutput(showOutput);
    }

    /**
     * Sets whether the output should be displayed.
     *
     * @param value A boolean indicating whether the output should be displayed.
     */
    public void setShowOutput(boolean value)
    {
        this.showOutput = value;
    }

    /**
     * Prints the command output using the configured {@link CommandOutputHandler},
     * if output display is enabled.
     *
     * @param output The {@link CommandOutput} to be processed and displayed.
     *               If {@code showOutput} is {@code false} or the handler is {@code null},
     *               the output will not be displayed.
     */
    public void print(CommandOutput output)
    {
        if (this.showOutput && this.handler != null)
            this.handler.handleOutput(output);
    }
}