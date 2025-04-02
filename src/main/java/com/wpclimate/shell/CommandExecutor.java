package com.wpclimate.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The {@code CommandExecutor} class is responsible for executing shell commands, 
 * including both simple commands and pipelines. It provides mechanisms to handle 
 * command execution, process output, and process termination.
 * 
 * <p>
 * This class ensures thread safety using a {@link ReentrantLock}, allowing multiple 
 * threads to safely execute commands without interfering with each other.
 * </p>
 * 
 * <h2>Features:</h2>
 * <ul>
 *   <li>Supports executing simple commands and pipelines.</li>
 *   <li>Handles standard output and error streams from the executed processes.</li>
 *   <li>Provides a mechanism to terminate running processes.</li>
 *   <li>Thread-safe implementation using {@link ReentrantLock}.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * To execute a command, create an instance of {@code CommandExecutor} with the 
 * desired working directory and command, then call the {@link #execute()} method.
 * </p>
 * 
 * <h2>Example:</h2>
 * <pre>
 * CommandBuilder commandBuilder = new CommandBuilder("ls -la");
 * CommandExecutor executor = new CommandExecutor("/path/to/directory", commandBuilder);
 * CommandOutput output = executor.execute();
 * System.out.println("Standard Output: " + output.getStandardOutput());
 * System.out.println("Error Output: " + output.getErrorOutput());
 * </pre>
 */
public class CommandExecutor 
{
    private ProcessBuilder processBuilder; // The ProcessBuilder for executing commands
    private Process process; // The process for a single command
    private List<Process> pipelineProcesses; // List of processes for a pipeline
    private final int WAIT_TO_TERMINATE = 1; // Timeout in seconds for process termination
    private CommandBuilder command; // The command to execute
    private File workDir; // The working directory for the command
    private final ReentrantLock lock = new ReentrantLock(); // Lock for thread safety

    /**
     * Default constructor.
     */
    public CommandExecutor() {}

    /**
     * Constructs a {@code CommandExecutor} with the specified working directory and command.
     *
     * @param directory The working directory in which the command will be executed.
     * @param commandToExecute The {@link CommandBuilder} object representing the command.
     */
    public CommandExecutor(String directory, CommandBuilder commandToExecute) 
    {
        this.command = commandToExecute;
        this.workDir = new File(directory);
    }

    /**
     * Constructs a {@code CommandExecutor} using the current working directory.
     *
     * @param commandToExecute The {@link CommandBuilder} object representing the command.
     */
    public CommandExecutor(CommandBuilder commandToExecute) 
    {
        this(getCurrentDir(), commandToExecute);
    }

    /**
     * Retrieves the current working directory.
     *
     * @return The current working directory as a string.
     */
    private static String getCurrentDir() 
    {
        return System.getProperty("user.dir");
    }

    /**
     * Executes the command (simple or pipeline) and returns a {@link CommandOutput} object 
     * containing the standard output and error output.
     *
     * @return A {@link CommandOutput} object containing the command's output.
     */
    public CommandOutput execute() 
    {
        this.lock.lock();
        try 
        {
            CommandOutput commandOutput = new CommandOutput();
            List<List<String>> commandsList = command.getCommand();

            if (command.isPipeline()) 
            {
                // Handle pipeline execution
                List<ProcessBuilder> builders = new ArrayList<>();
                for (List<String> cmdTokens : commandsList) 
                {
                    ProcessBuilder pb = new ProcessBuilder(cmdTokens);
                    pb.directory(workDir);
                    builders.add(pb);
                }
                pipelineProcesses = ProcessBuilder.startPipeline(builders);
                Process lastProcess = pipelineProcesses.get(pipelineProcesses.size() - 1);

                // Handle standard output
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(lastProcess.getInputStream()));
                String line;
                while ((line = stdInput.readLine()) != null)
                    commandOutput.appendStandardOutput(line);

                // Handle error output
                BufferedReader stdError = new BufferedReader(new InputStreamReader(lastProcess.getErrorStream()));
                while ((line = stdError.readLine()) != null)
                    commandOutput.appendErrorOutput(line);

                lastProcess.waitFor();
            } 
            else 
            {
                // Handle single command execution
                if (commandsList.isEmpty())
                    throw new IllegalStateException("Command not initialized.");

                List<String> simpleCommand = commandsList.get(0);
                processBuilder = new ProcessBuilder(simpleCommand);
                processBuilder.directory(workDir);
                process = processBuilder.start();

                // Handle standard output
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = stdInput.readLine()) != null)
                    commandOutput.appendStandardOutput(line);

                // Handle error output
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = stdError.readLine()) != null)
                    commandOutput.appendErrorOutput(line);
            }
            return commandOutput;
        } 
        catch (IOException | InterruptedException e) 
        {
            e.printStackTrace();
            return new CommandOutput();
        } 
        finally 
        {
            lock.unlock();
        }
    }

    /**
     * Terminates the execution of the command. For a pipeline, all processes are terminated.
     * For a single command, the single process is terminated.
     */
    public void stop() 
    {
        lock.lock();
        try 
        {
            if (command.isPipeline() && pipelineProcesses != null) 
            {
                for (Process p : pipelineProcesses)
                    p.destroy();

                for (Process p : pipelineProcesses) 
                {
                    if (!p.waitFor(WAIT_TO_TERMINATE, TimeUnit.SECONDS))
                        p.destroyForcibly();
                }
            } 
            else if (process != null) 
            {
                process.destroy();
                if (!process.waitFor(WAIT_TO_TERMINATE, TimeUnit.SECONDS))
                    process.destroyForcibly();
            }
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            lock.unlock();
        }
    }
}