package com.wpclimate.cli.exceptions;

/**
 * Exception thrown when PHP is not installed.
 */
public class PHPNotInstalledException extends Exception 
{
    public PHPNotInstalledException(String message) 
    {
        super(message);
    }
}