package com.wpclimate.Cli;

import com.wpclimate.Cli.Core.Context;
import com.wpclimate.Cli.Core.Dependency;
import com.wpclimate.Cli.Core.WpCliModel;
import com.wpclimate.Cli.exceptions.PHPNotInstalledException;
import com.wpclimate.Cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.Configurator.*;
import com.wpclimate.Shell.Command;
import com.wpclimate.Shell.Shell;

import java.util.Scanner;

public class WpCli 
{
    private Dependency dependency;
    private Context context;

    public WpCli() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        Configurator configurator = new Configuration("/home/ufos/Documents/petbuy/");
        WpCliModel model = this.initializeModel(configurator);
        Shell shell = new Command("/home/ufos/Documents/petbuy");
    
        this.context = new Context(model, shell, configurator);
    
        this.dependency = new Dependency(this.context);
    
        System.out.println("PHP Installed: " + dependency.isPHPInstalled());
        System.out.println("WP-CLI Installed: " + dependency.isWpCliInstalled());
        System.out.println("Is WordPress Directory: " + dependency.isAWordpressDirectory());
    }


    private WpCliModel initializeModel(Configurator configurator) 
    {
        WpCliModel model = new WpCliModel();
    
        try
        {
            model.setFromModel(configurator.read());
        } 
        catch (Exception e) 
        {
            System.out.println("Configuration file not found. Please set the required parameters.");
            Scanner scanner = new Scanner(System.in);
    
            System.out.print("Enter the path to the PHP executable: ");
            String phpPath = scanner.nextLine();
    
            System.out.print("Enter the path to the WP-CLI executable: ");
            String wpCliPath = scanner.nextLine();
    
            scanner.close();
    
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
        }
    
        return model;
    }
}