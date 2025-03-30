package com.wpclimate.Cli;

import com.wpclimate.Cli.Core.Context;
import com.wpclimate.Cli.Core.Dependency;
import com.wpclimate.Cli.Core.WpCliModel;
import com.wpclimate.Cli.Exceptions.PHPNotInstalledException;
import com.wpclimate.Cli.Exceptions.WPCliNotInstalledException;
import com.wpclimate.Cli.WpCommands.SearchReplaceCommand;
import com.wpclimate.Configurator.*;
import com.wpclimate.Constants.FileManager;
import com.wpclimate.Constants.FileName;
import com.wpclimate.Shell.Command;
import com.wpclimate.Shell.CommandOutput;
import com.wpclimate.Shell.Shell;

import java.util.Scanner;

/**
 * The {@code WpCli} class serves as the entry point for the WP-CLI application.
 * It initializes the core components and checks for dependencies.
 */
public class WpCli 
{
    private Dependency dependency;
    private Context context;

    /**
     * Constructs a {@code WpCli} instance with the specified working directory.
     *
     * @param workingDirectory The working directory for the application.
     * @throws PHPNotInstalledException If PHP is not installed.
     * @throws WPCliNotInstalledException If WP-CLI is not installed.
     */
    public WpCli(String workingDirectory) throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        FileManager fileManager = this.initializeFileManager(workingDirectory);
        Configurator configurator = this.initializeConfigurator(fileManager);
        WpCliModel model = this.initializeModel(configurator);
        Shell shell = this.initializeShell(fileManager);

        this.context = new Context(model, shell, configurator, fileManager);
        this.dependency = new Dependency(this.context);

        this.performDependencyChecks();
    }

    /**
     * Initializes the {@link FileManager} with the specified working directory.
     *
     * @param workingDirectory The working directory for the application.
     * @return An instance of {@link FileManager}.
     */
    private FileManager initializeFileManager(String workingDirectory) 
    {
        if (workingDirectory == null || workingDirectory.isEmpty())
            return new FileManager();

        return new FileManager(workingDirectory);
    }

    /**
     * Initializes the {@link Configurator} using the {@link FileManager}.
     *
     * @param fileManager The {@link FileManager} instance.
     * @return An instance of {@link Configurator}.
     */
    private Configurator initializeConfigurator(FileManager fileManager) 
    {
        String configFilePath = fileManager.getFilePath(FileName.WPCLI_FILE_NAME);
        return new Configuration(configFilePath);
    }

    /**
     * Initializes the {@link WpCliModel} by reading the configuration or prompting the user.
     *
     * @param configurator The {@link Configurator} instance.
     * @return An instance of {@link WpCliModel}.
     */
    private WpCliModel initializeModel(Configurator configurator) 
    {
        WpCliModel model = new WpCliModel();

        try 
        {
            model.setFromModel(configurator.read());
        } 
        catch (Exception e) 
        {
            this.promptForConfiguration(model, configurator);
        }

        return model;
    }

    /**
     * Prompts the user to provide configuration parameters and saves them.
     *
     * @param model        The {@link WpCliModel} instance to populate.
     * @param configurator The {@link Configurator} instance to save the configuration.
     */
    private void promptForConfiguration(WpCliModel model, Configurator configurator) 
    {
        System.out.println("Configuration file not found. Please set the required parameters.");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the path to the PHP executable: ");
        String phpPath = scanner.nextLine();

        System.out.print("Enter the path to the WP-CLI executable: ");
        String wpCliPath = scanner.nextLine();

        model.setPhp(phpPath);
        model.setWp(wpCliPath);

        try 
        {
            configurator.save(model);
            System.out.println("Configuration saved successfully.");
        } 
        catch (Exception saveException) 
        {
            System.err.println("Failed to save configuration: " + saveException.getMessage());
        } 
        finally 
        {
            scanner.close();
        }
    }

    /**
     * Initializes the {@link Shell} for executing commands.
     *
     * @param fileManager The {@link FileManager} instance.
     * @return An instance of {@link Shell}.
     */
    private Shell initializeShell(FileManager fileManager) 
    {
        return new Command(fileManager.getWorkingDirectory().getAbsolutePath());
    }

    /**
     * Performs dependency checks and prints the results.
     *
     * @throws PHPNotInstalledException If PHP is not installed.
     * @throws WPCliNotInstalledException If WP-CLI is not installed.
     */
    private void performDependencyChecks() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        System.out.println("PHP Installed: " + this.dependency.isPHPInstalled());
        System.out.println("WP-CLI Installed: " + this.dependency.isWpCliInstalled());
        System.out.println("Is WordPress Directory: " + this.dependency.isAWordpressDirectory());
    }

    public boolean doSearchReplace(String oldVal, String newVal, boolean allTables, boolean dryRun) throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        SearchReplaceCommand cmd = new SearchReplaceCommand(this.context, this.dependency, oldVal, newVal, allTables, dryRun);
        
        return cmd.execute().isSuccessful();
    }
}