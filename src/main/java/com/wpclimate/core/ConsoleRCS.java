package com.wpclimate.core;

import java.util.Scanner;

import com.wpclimate.shell.RealTimeConsoleSpoofer;

/**
 * The {@code ConsoleInteractionHandler} class handles interactive input and output
 * via the console.
 */
public class ConsoleRCS implements RealTimeConsoleSpoofer 
{

    private final Scanner scanner;

    public ConsoleRCS() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMessage(String message, boolean isError) 
    {
        String RED = "\033[31m";
        String GREEN = "\033[32m"; 
        String RESET = "\033[0m";

        if (isError)
            System.out.println("(RCS)" + RED + " ERROR: " + RESET + message);
        else
            System.out.println("(RCS)" + GREEN + " LOG: " + RESET + message);  
    }
}