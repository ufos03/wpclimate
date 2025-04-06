package com.wpclimate.shell;

import java.util.Map;

/**
 * The Command class implements the Shell interface and provides methods to execute shell commands.
 */
public class Command implements Shell 
{

    private String workingDirectory;
    private CommandExecutor executor;

    /**
     * Constructs a Command object with the specified working directory.
     *
     * @param workingDirectory The directory in which to execute the command.
     */
    public Command(String workingDirectory) 
    {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Constructs a Command object with the current working directory.
     */
    public Command() 
    {
        this(System.getProperty("user.dir"));
    }

    /**
     * Executes a shell command in the current working directory.
     *
     * @param commandLine The command line to execute.
     * @return A CommandOutput object containing the standard output and error output of the command.
     */
    @Override
    public CommandOutput executeCommand(String commandLine) 
    {
        CommandBuilder command = new CommandBuilder(commandLine);
        executor = new CommandExecutor(this.workingDirectory, command);
        return executor.execute();
    }

    /**
     * Executes a shell command in a specified directory.
     *
     * @param commandLine The command line to execute.
     * @param environment A map of environment variables to set for the command.
     * @return A CommandOutput object containing the standard output and error output of the command.
     */
    @Override
    public CommandOutput executeCommand(String commandLine, Map<String, String> environment) 
    {
        CommandBuilder command = new CommandBuilder(commandLine);
        executor = new CommandExecutor(this.workingDirectory, command, environment);
        return executor.execute();
    }

    /**
     * Executes a shell command in a specified directory.
     *
     * @param commandLine The command line to execute.
     * @param directory The directory in which to execute the command.
     * @param environment A map of environment variables to set for the command.
     * @return A CommandOutput object containing the standard output and error output of the command.
     */
    @Override
    public CommandOutput executeCommand(String commandLine, String directory, Map<String, String> environment) 
    {
        CommandBuilder command = new CommandBuilder(commandLine);
        executor = new CommandExecutor(this.workingDirectory, command, environment);
        return executor.execute();
    }
}