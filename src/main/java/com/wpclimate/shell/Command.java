package com.wpclimate.shell;

import java.util.Map;

/**
 * The Command class implements the Shell interface and provides methods to execute shell commands.
 */
public class Command implements Shell  //TODO: DOC
{
    private String workingDirectory;
    private CommandExecutor executor;
    private RealTimeConsoleSpoofer spoofer;

    /**
     * Constructs a Command object with the specified working directory.
     *
     * @param workingDirectory The directory in which to execute the command.
     */
    public Command(String workingDirectory, RealTimeConsoleSpoofer spoofer) 
    {
        this.workingDirectory = workingDirectory;
        this.spoofer = spoofer;
    }

    /**
     * Constructs a Command object with the current working directory.
     */
    public Command(RealTimeConsoleSpoofer interactionHandler) 
    {
        this(System.getProperty("user.dir"), interactionHandler);
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
        this.executor.setConsoleSpoofer(this.spoofer);
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
        this.executor.setConsoleSpoofer(this.spoofer);
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
        this.executor.setConsoleSpoofer(this.spoofer);
        return executor.execute();
    }
}