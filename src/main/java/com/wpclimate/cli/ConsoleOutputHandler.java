package com.wpclimate.cli;

import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.CommandOutputHandler;


/**
 * The {@code ConsoleOutputHandler} class implements {@link CommandOutputHandler}
 * to print the output of a WP-CLI command to the console.
 */
public class ConsoleOutputHandler implements CommandOutputHandler 
{

    @Override
    public void handleOutput(CommandOutput output) 
    {
        if (output.isSuccessful()) 
        {
            System.out.println("Command executed successfully:");
            System.out.println(output.getStandardOutput());
        } 
        else 
        {
            System.err.println("Command execution failed:");
            System.err.println(output.getErrorOutput());
        }
    }
}