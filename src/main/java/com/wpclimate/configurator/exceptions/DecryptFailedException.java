package com.wpclimate.configurator.exceptions;

public class DecryptFailedException extends Exception
{
    public DecryptFailedException(String message)
    {
        super(message);
    }

    public DecryptFailedException(String message, Exception e)
    {
        super(message, e);
    }
}
