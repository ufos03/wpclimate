package com.wpclimate.shell;

/**
 * The {@code RealTimeConsoleSpoofer} interface defines methods for handling
 * real-time console output during command execution.
 * 
 * <p>
 * This interface is responsible for displaying messages to the user in real-time
 * as they are produced by executing commands. Implementations can determine how
 * the messages are displayed (e.g., console output, GUI, log file) and may apply
 * different formatting based on whether the message represents an error or standard output.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Display command output messages in real-time.</li>
 *   <li>Distinguish between standard output and error messages.</li>
 *   <li>Handle formatting and presentation of command output.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * RealTimeConsoleSpoofer spoofer = new ConsoleSpoofer();
 * spoofer.displayMessage("Command execution started", false);
 * 
 * // During command execution
 * spoofer.displayMessage("Processing file: example.txt", false);
 * 
 * // If an error occurs
 * spoofer.displayMessage("File not found: example.txt", true);
 * </pre>
 * 
 * <h2>Implementation Note:</h2>
 * <p>
 * Implementations should ensure that messages are displayed immediately without
 * buffering to provide a truly real-time experience. Additionally, implementations
 * should handle concurrent access appropriately if used in a multi-threaded environment.
 * </p>
 */
public interface RealTimeConsoleSpoofer
{
    /**
     * Displays a message to the user in real-time.
     * 
     * <p>
     * This method is called during command execution to display output as it's
     * generated. It can be used to show both standard output and error messages
     * from commands being executed.
     * </p>
     * 
     * <p>
     * Implementations should display the message immediately without buffering
     * to provide real-time feedback to the user.
     * </p>
     *
     * @param message The message to display.
     * @param isError If {@code true}, indicates that the message is an error message
     *                and should be treated accordingly (e.g., displayed in red or
     *                logged as an error). If {@code false}, the message is standard output.
     */
    void displayMessage(String message, boolean isError);
}