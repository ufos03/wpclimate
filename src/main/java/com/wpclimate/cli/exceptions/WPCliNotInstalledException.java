package com.wpclimate.cli.exceptions;

/**
 * Exception thrown when WPCli is not installed.
 */
public class WPCliNotInstalledException extends Exception 
{
    public WPCliNotInstalledException(String message) 
    {
        super(message);
    }
}