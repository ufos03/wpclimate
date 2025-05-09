package com.wpclimate.core;

import java.util.Scanner;

import com.wpclimate.shell.RealTimeConsoleSpoofer;

/**
 * The {@code ConsoleRCS} class implements the {@link RealTimeConsoleSpoofer} interface 
 * to provide console-based output display for command execution.
 * 
 * <p>
 * This class handles the display of real-time output messages from command execution,
 * formatting them appropriately for console display with color-coding to distinguish
 * between standard output and error messages. Standard messages are prefixed with a green
 * "LOG:" label, while error messages are prefixed with a red "ERROR:" label.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Display command output messages in the console with appropriate formatting.</li>
 *   <li>Apply color-coding to distinguish between standard output and error messages.</li>
 *   <li>Prefix messages with an indicator to identify the source ("RCS" - Real-time Console Spoofer).</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * RealTimeConsoleSpoofer spoofer = new ConsoleRCS();
 * 
 * // Display a standard message
 * spoofer.displayMessage("Command execution started", false);
 * 
 * // Display an error message
 * spoofer.displayMessage("Command failed: File not found", true);
 * </pre>
 * 
 * <h2>Terminal Compatibility:</h2>
 * <p>
 * The color formatting uses ANSI escape codes which may not be supported in all terminal
 * environments. For Windows terminals, consider using Windows Console API or a library
 * like Jansi for broader compatibility.
 * </p>
 */
public class ConsoleRCS implements RealTimeConsoleSpoofer 
{
    private final Scanner scanner;

    /**
     * Constructs a new {@code ConsoleRCS} instance.
     * 
     * <p>
     * Initializes a Scanner to read from standard input, which can be used for
     * future interactive features requiring user input.
     * </p>
     */
    public ConsoleRCS() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays a message to the console with appropriate formatting based on whether
     * it represents an error or standard output.
     * 
     * <p>
     * This method formats the message with color coding and prefixes:
     * <ul>
     *   <li>All messages are prefixed with "(RCS)" to indicate the source.</li>
     *   <li>Standard messages (isError = false) are prefixed with a green "LOG:" label.</li>
     *   <li>Error messages (isError = true) are prefixed with a red "ERROR:" label.</li>
     * </ul>
     * </p>
     * 
     * @param message The message to display.
     * @param isError If {@code true}, the message is displayed as an error with red formatting.
     *                If {@code false}, the message is displayed as a standard log with green formatting.
     */
    @Override
    public void displayMessage(String message, boolean isError) 
    {
        String RED = "\033[31m";
        String GREEN = "\033[32m"; 
        String RESET = "\033[0m";

        if (isError)
            System.out.println("(RCS)" + RED + " ERROR: " + RESET + message);
        else
            System.out.println("(RCS)" + GREEN + " LOG: " + RESET + message);  
    }
}