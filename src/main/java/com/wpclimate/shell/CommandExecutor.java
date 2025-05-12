package com.wpclimate.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
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
 *   <li>Supports custom environment variables for command execution.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * To execute a command, create an instance of {@code CommandExecutor} with the 
 * desired working directory, command, and environment variables, then call the {@link #execute()} method.
 * </p>
 * 
 * <h2>Example:</h2>
 * <pre>
 * CommandBuilder commandBuilder = new CommandBuilder("ls -la");
 * Map<String, String> envVars = Map.of("MY_ENV_VAR", "value");
 * CommandExecutor executor = new CommandExecutor("/path/to/directory", commandBuilder, envVars);
 * CommandOutput output = executor.execute();
 * System.out.println("Standard Output: " + output.getStandardOutput());
 * System.out.println("Error Output: " + output.getErrorOutput());
 * </pre>
 */
public class CommandExecutor 
{
    private final ProcessBuilder processBuilder; // The ProcessBuilder for executing commands
    private Process process; // The process for a single command
    private List<Process> pipelineProcesses; // List of processes for a pipeline
    private final int WAIT_TO_TERMINATE = 2; // Timeout in seconds for process termination
    private final CommandBuilder command; // The command to execute
    private final File workDir; // The working directory for the command
    private final Map<String, String> envVars; // Environment variables for the command
    private final ReentrantLock lock = new ReentrantLock(); // Lock for thread safety

    private RealTimeConsoleSpoofer spoofer;

    /**
     * Constructs a {@code CommandExecutor} with the specified working directory, command, and environment variables.
     *
     * @param directory The working directory in which the command will be executed.
     * @param commandToExecute The {@link CommandBuilder} object representing the command.
     * @param envVars A map of environment variables to set for the command execution.
     */
    public CommandExecutor(String directory, CommandBuilder commandToExecute, Map<String, String> envVars) 
    {
        this.command = commandToExecute;
        this.workDir = new File(directory);
        this.envVars = envVars;
        this.processBuilder = new ProcessBuilder();
    }

    /**
     * Constructs a {@code CommandExecutor} using the current working directory and environment variables.
     *
     * @param commandToExecute The {@link CommandBuilder} object representing the command.
     * @param envVars A map of environment variables to set for the command execution.
     */
    public CommandExecutor(CommandBuilder commandToExecute, Map<String, String> envVars) 
    {
        this(getCurrentDir(), commandToExecute, envVars);
    }

    /**
     * Constructs a {@code CommandExecutor} with the specified working directory, command.
     * @param directory The working directory in which the command will be executed.
     * @param commandToExecute The {@link CommandBuilder} object representing the command.
     */
    public CommandExecutor(String directory, CommandBuilder commandToExecute) 
    {
        this.workDir = new File(directory);
        this.command = commandToExecute;
        this.envVars = null;
        this.processBuilder = new ProcessBuilder();
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

    public void setConsoleSpoofer(RealTimeConsoleSpoofer spoofer)
    {
        this.spoofer = spoofer;
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
            List<List<String>> commandsList = this.command.getCommand();

            if (this.command.isPipeline()) 
            {
                // Handle pipeline execution
                List<ProcessBuilder> builders = new ArrayList<>();
                for (List<String> cmdTokens : commandsList) 
                {
                    ProcessBuilder pb = new ProcessBuilder(cmdTokens);
                    pb.directory(workDir);

                    // Set environment variables
                    if (envVars != null)
                        pb.environment().putAll(envVars);

                    builders.add(pb);
                }
                pipelineProcesses = ProcessBuilder.startPipeline(builders);
                Process lastProcess = pipelineProcesses.get(pipelineProcesses.size() - 1);
                this.lock.unlock();
                this.handleProcessStreams(lastProcess, commandOutput);

                lastProcess.waitFor();
            } 
            else 
            {
                // Handle single command execution
                if (commandsList.isEmpty())
                    throw new IllegalStateException("Command not initialized.");

                List<String> simpleCommand = commandsList.get(0);
                this.processBuilder.command(simpleCommand);
                this.processBuilder.directory(workDir);

                // Set environment variables
                if (envVars != null)
                    this.processBuilder.environment().putAll(envVars);

                process = this.processBuilder.start();
                this.lock.unlock();
                this.handleProcessStreams(process, commandOutput);
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
            if (this.lock.isHeldByCurrentThread())
                this.lock.unlock();
        }
    }

    /**
     * Terminates the execution of the command. For a pipeline, all processes are terminated.
     * For a single command, the single process is terminated.
     */
    public void stop() 
    {
        this.lock.lock();
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
            this.lock.unlock();
        }
    }

    private void handleProcessStreams(Process process, CommandOutput commandOutput) {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    
        // Thread per leggere l'output standard
        Thread outputThread = new Thread(() -> {
            try {
                String outputLine;
                while ((outputLine = stdInput.readLine()) != null) 
                {
                    commandOutput.appendStandardOutput(outputLine);
                    if (this.spoofer != null)
                        this.spoofer.displayMessage(outputLine, false);
                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        });
    
        // Thread per leggere l'output di errore
        Thread errorThread = new Thread(() -> {
            try {
                String errorLine;
                while ((errorLine = stdError.readLine()) != null) 
                {
                    if (errorLine.contains("remote")) 
                    {
                        commandOutput.appendStandardOutput(errorLine);
                        if (this.spoofer != null)
                           this.spoofer.displayMessage(errorLine, false);
                        
                    } 
                    else 
                    {
                        commandOutput.appendErrorOutput(errorLine);
                        if (this.spoofer != null)
                            this.spoofer.displayMessage(errorLine, true);
                    }
                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        });
    
        outputThread.setDaemon(true); // Thread daemon per terminare con il programma principale
        errorThread.setDaemon(true);
        outputThread.start();
        errorThread.start();
        

        try 
        {
            process.waitFor();
            outputThread.join(1000);
            errorThread.join(1000);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
    }
}