package com.wpclimate.cli;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.constants.FileManager;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

/**
 * The {@code WpCli} class serves as the entry point for the WP-CLI application.
 */
public class WpCli {

    private final Dependency dependency;
    private final Context context;
    private final WpCliCommandExecutor commandExecutor;
    private boolean showOutput;
    private ConsoleOutputHandler consolePrinter;

    /**
     * Constructs a {@code WpCli} instance with the specified working directory.
     *
     * @param workingDirectory The working directory for the application.
     */
    public WpCli(String workingDirectory) 
    {
        WpCliInitializer initializer = new WpCliInitializer();

        FileManager fileManager = initializer.initializeFileManager(workingDirectory);
        Shell shell = initializer.initializeShell(fileManager);
        WpCliModel model = initializer.initializeModel(initializer.initializeConfigurator(fileManager));

        this.context = new Context(model, shell, initializer.initializeConfigurator(fileManager), fileManager);
        this.dependency = new Dependency(context);
        this.commandExecutor = new WpCliCommandExecutor(context, dependency);
        this.showOutput = false;
        this.consolePrinter = new ConsoleOutputHandler();
    }

    public void setShowOutput(boolean value)
    {
        this.showOutput = value;
    }

    public void printOutputToConsole(CommandOutput output)
    {
        if (this.showOutput)
            this.consolePrinter.handleOutput(output);
    }

    public boolean doSearchReplace(String oldVal, String newVal, boolean allTables, boolean dryRun) 
    {
        try 
        {
            CommandOutput output = this.commandExecutor.doSearchReplace(oldVal, newVal, allTables, dryRun);
            printOutputToConsole(output);
            return output.isSuccessful();
        } 
        catch (Exception e) 
        {
            return false;
        }
    }

    public boolean doFlushTransient()
    {
        try 
        {
            CommandOutput output = this.commandExecutor.doFlushTransient();
            this.printOutputToConsole(output);
            return output.isSuccessful();
        } 
        catch (Exception e) 
        {
            return false;
        }
    }

    public boolean doRewriteRules() 
    {
        try 
        {
            CommandOutput output = this.commandExecutor.doRewriteRules();
            this.printOutputToConsole(output);
            return output.isSuccessful();
        } 
        catch (Exception e) 
        {
            return false;
        }
    }
}