package com.wpclimate.shell;

import java.util.Map;

/**
 * The {@code Command} class implements the {@link Shell} interface and provides methods 
 * to execute shell commands in various environments.
 * 
 * <p>
 * This class serves as a primary implementation of the {@link Shell} interface, providing
 * functionality to execute shell commands with different configurations such as working
 * directory and environment variables. It uses {@link CommandBuilder} to parse command
 * lines and {@link CommandExecutor} to handle the actual execution of commands.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Parse and execute shell commands.</li>
 *   <li>Configure execution environments for commands (working directory, environment variables).</li>
 *   <li>Provide real-time feedback of command execution through a {@link RealTimeConsoleSpoofer}.</li>
 *   <li>Collect and return command output for further processing.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * RealTimeConsoleSpoofer spoofer = new ConsoleSpoofer();
 * Command command = new Command("/path/to/working/directory", spoofer);
 * 
 * // Execute a simple command
 * CommandOutput output = command.executeCommand("ls -la");
 * 
 * // Execute a command with custom environment variables
 * Map&lt;String, String&gt; env = new HashMap&lt;&gt;();
 * env.put("DEBUG", "true");
 * CommandOutput outputWithEnv = command.executeCommand("./run_script.sh", env);
 * 
 * // Check if there were any errors
 * if (output.hasErrors()) {
 *     System.err.println("Command execution failed: " + output.getErrorOutput());
 * }
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. Each instance maintains state related to command
 * execution that could be affected by concurrent modification. For thread-safe 
 * execution, create separate instances for each thread or provide external synchronization.
 * </p>
 * 
 * @see Shell
 * @see CommandBuilder
 * @see CommandExecutor
 * @see CommandOutput
 * @see RealTimeConsoleSpoofer
 */
public class Command implements Shell
{
    private String workingDirectory;
    private CommandExecutor executor;
    private RealTimeConsoleSpoofer spoofer;

    /**
     * Constructs a {@code Command} object with the specified working directory and console spoofer.
     *
     * <p>
     * This constructor initializes a Command object that will execute commands in the specified
     * working directory and use the provided console spoofer for real-time command output display.
     * </p>
     *
     * @param workingDirectory The directory in which to execute commands.
     * @param spoofer The {@link RealTimeConsoleSpoofer} used to display command output in real-time.
     * @throws IllegalArgumentException If workingDirectory or spoofer is null.
     */
    public Command(String workingDirectory, RealTimeConsoleSpoofer spoofer) 
    {
        if (workingDirectory == null)
            throw new IllegalArgumentException("Working directory cannot be null");
        if (spoofer == null)
            throw new IllegalArgumentException("Console spoofer cannot be null");
            
        this.workingDirectory = workingDirectory;
        this.spoofer = spoofer;
    }

    /**
     * Constructs a {@code Command} object with the current working directory and the specified console spoofer.
     *
     * <p>
     * This convenience constructor uses the current working directory (as defined by the
     * "user.dir" system property) and the provided console spoofer for command execution.
     * </p>
     *
     * @param spoofer The {@link RealTimeConsoleSpoofer} used to display command output in real-time.
     * @throws IllegalArgumentException If spoofer is null.
     */
    public Command(RealTimeConsoleSpoofer spoofer) 
    {
        this(System.getProperty("user.dir"), spoofer);
    }

    /**
     * Executes a shell command in the current working directory.
     *
     * <p>
     * This method parses the command line string, creates a {@link CommandBuilder},
     * and executes the command in the current working directory without any custom
     * environment variables.
     * </p>
     *
     * @param commandLine The command line to execute.
     * @return A {@link CommandOutput} object containing the standard output and error output of the command.
     * @throws IllegalArgumentException If commandLine is null or empty.
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
     * Executes a shell command in the current working directory with custom environment variables.
     *
     * <p>
     * This method parses the command line string, creates a {@link CommandBuilder},
     * and executes the command in the current working directory with the specified
     * environment variables.
     * </p>
     *
     * @param commandLine The command line to execute.
     * @param environment A map of environment variables to set for the command execution.
     * @return A {@link CommandOutput} object containing the standard output and error output of the command.
     * @throws IllegalArgumentException If commandLine is null or empty.
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
     * Executes a shell command in a specified directory with custom environment variables.
     *
     * <p>
     * This method parses the command line string, creates a {@link CommandBuilder},
     * and executes the command in the specified directory with the specified
     * environment variables.
     * </p>
     *
     * @param commandLine The command line to execute.
     * @param directory The directory in which to execute the command.
     * @param environment A map of environment variables to set for the command execution.
     * @return A {@link CommandOutput} object containing the standard output and error output of the command.
     * @throws IllegalArgumentException If commandLine or directory is null or empty.
     */
    @Override
    public CommandOutput executeCommand(String commandLine, String directory, Map<String, String> environment) 
    {
        CommandBuilder command = new CommandBuilder(commandLine);
        executor = new CommandExecutor(directory, command, environment);
        this.executor.setConsoleSpoofer(this.spoofer);
        return executor.execute();
    }
}