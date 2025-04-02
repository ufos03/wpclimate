package com.wpclimate.configurator.exceptions;

public class EncryptFailedExeception extends Exception
{
    public EncryptFailedExeception(String message)
    {
        super(message);
    }

    public EncryptFailedExeception(String message, Exception e)
    {
        super(message, e);
    }
}
