package com.wpclimate.git.credentials;

public enum CredentialsType 
{
    SSH("SSH"),
    HTTPS("HTTPS");

    private final String type;

    CredentialsType(String type) 
    {
        this.type = type;
    }

    public String getTypeS() 
    {
        return type;
    }
}
