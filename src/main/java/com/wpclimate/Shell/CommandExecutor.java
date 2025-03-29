package com.wpclimate.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class CommandExecutor
{
    private ProcessBuilder processBuilder;
    private Process process;
    private List<Process> pipelineProcesses; // utilizzato per pipeline
    private final int WAIT_TO_TERMINATE = 1;
    private CommandBuilder command;
    private File workDir;

    /**
     * Costruttore di default.
     */
    public CommandExecutor() {};
    
    /**
     * Costruttore che imposta la directory di lavoro e il comando da eseguire.
     * @param directory La directory in cui eseguire il comando
     * @param commandToExecute L'oggetto Command (semplice o pipeline)
     */
    public CommandExecutor(String directory, CommandBuilder commandToExecute) 
    {
        this.command = commandToExecute;
        this.workDir = new File(directory);
    }
    
    /**
     * Costruttore che utilizza la directory corrente.
     * @param commandToExecute L'oggetto Command (semplice o pipeline)
     */
    public CommandExecutor(CommandBuilder commandToExecute) 
    {
        this(getCurrentDir(), commandToExecute);
    }
    
    private static String getCurrentDir() 
    {
        return System.getProperty("user.dir");
    }
    
    /**
     * Esegue il comando (semplice o pipeline) e ritorna un oggetto CommandOutput contenente gli output.
     */
    public CommandOutput execute() 
    {
        CommandOutput commandOutput = new CommandOutput();
        try 
        {
            List<List<String>> commandsList = command.getCommand();
            if (command.isPipeline()) 
            {
                // Pipeline: creiamo un ProcessBuilder per ogni sotto-comando
                List<ProcessBuilder> builders = new ArrayList<>();
                for (List<String> cmdTokens : commandsList)
                {
                    ProcessBuilder pb = new ProcessBuilder(cmdTokens);
                    pb.directory(workDir);
                    builders.add(pb);
                }
                // Avvio della pipeline in maniera nativa (Java 9+)
                pipelineProcesses = ProcessBuilder.startPipeline(builders);
                Process lastProcess = pipelineProcesses.get(pipelineProcesses.size() - 1);
                
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(lastProcess.getInputStream()));
                String line;
                while ((line = stdInput.readLine()) != null)
                    commandOutput.appendStandardOutput(line);

                BufferedReader stdError = new BufferedReader(new InputStreamReader(lastProcess.getErrorStream()));
                while ((line = stdError.readLine()) != null) 
                    commandOutput.appendErrorOutput(line);

                int exitCode = lastProcess.waitFor();
                System.out.println("Pipeline terminata con codice: " + exitCode);
            } 
            else 
            {
                if (commandsList.isEmpty())
                    throw new IllegalStateException("CCommand not initialized.");

                List<String> simpleCommand = commandsList.get(0);
                processBuilder = new ProcessBuilder(simpleCommand);
                processBuilder.directory(workDir);
                process = processBuilder.start();
                
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = stdInput.readLine()) != null)
                    commandOutput.appendStandardOutput(line);

                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = stdError.readLine()) != null)
                    commandOutput.appendErrorOutput(line);
            }
        } 
        catch (IOException | InterruptedException e) 
        {
            e.printStackTrace();
            commandOutput.appendErrorOutput(e.getMessage());
        }
        
        return commandOutput;
    }
    
    /**
     * Termina l'esecuzione del comando: per una pipeline vengono terminati tutti i processi, per un comando semplice il singolo processo.
     */
    public void stop() 
    {
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
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}