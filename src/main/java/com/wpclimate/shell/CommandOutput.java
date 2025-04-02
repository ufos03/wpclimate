package com.wpclimate.shell;

/**
 * The CommandOutput class is used to store and manage the output of a command execution.
 */
public class CommandOutput 
{
    private StringBuilder standardOutput;
    private StringBuilder errorOutput;

    /**
     * Default constructor initializes the standard and error output buffers.
     */
    public CommandOutput() 
    {
        this.standardOutput = new StringBuilder();
        this.errorOutput = new StringBuilder();
    }

    /**
     * Appends a line to the standard output buffer.
     *
     * @param line The line to append.
     */
    public void appendStandardOutput(String line) 
    {
        this.standardOutput.append(line).append(System.lineSeparator());
    }

    /**
     * Appends a line to the error output buffer.
     *
     * @param line The line to append.
     */
    public void appendErrorOutput(String line) 
    {
        this.errorOutput.append(line).append(System.lineSeparator());
    }

    /**
     * Returns the standard output as a string.
     *
     * @return The standard output.
     */
    public String getStandardOutput() 
    {
        return this.standardOutput.toString();
    }

    /**
     * Returns the error output as a string.
     *
     * @return The error output.
     */
    public String getErrorOutput() 
    {
        return this.errorOutput.toString();
    }

    /**
     * Checks if there are any errors in the error output.
     *
     * @return True if there are errors, false otherwise.
     */
    public boolean hasErrors() 
    {
        return this.errorOutput.length() > 0;
    }

    /**
     * Checks if the command execution was successful.
     *
     * @return True if there are no errors, false otherwise.
     */
    public boolean isSuccessful() 
    {
        return !hasErrors();
    }

    /**
     * Checks if the standard output contains the specified string.
     *
     * @param searchString The string to search for.
     * @return True if the standard output contains the string, false otherwise.
     */
    public boolean containsInStandardOutput(String searchString) 
    {
        return this.standardOutput.toString().contains(searchString);
    }

    /**
     * Checks if the error output contains the specified string.
     *
     * @param searchString The string to search for.
     * @return True if the error output contains the string, false otherwise.
     */
    public boolean containsInErrorOutput(String searchString) 
    {
        return this.errorOutput.toString().contains(searchString);
    }
}