package com.wpclimate.shell;

import java.util.Map;

/**
 * The Shell interface defines methods for executing shell commands.
 */
public interface Shell 
{
    /**
     * Executes a shell command in the current working directory.
     *
     * @param commandLine The command line to execute.
     * @return A CommandOutput object containing the standard output and error output of the command.
     */
    CommandOutput executeCommand(String commandLine);


    /**
     * Executes a shell command in a specified directory.
     *
     * @param commandLine The command line to execute.
     * @param environment A map of environment variables to set for the command.
     * @return A CommandOutput object containing the standard output and error output of the command.
     */
    CommandOutput executeCommand(String commandLine, Map<String, String> environment);

    /**
     * Executes a shell command in a specified directory.
     *
     * @param commandLine The command line to execute.
     * @param directory The directory in which to execute the command.
     * @param environment A map of environment variables to set for the command.
     * @return A CommandOutput object containing the standard output and error output of the command.
     */
    CommandOutput executeCommand(String commandLine, String directory, Map<String, String> environment);
}