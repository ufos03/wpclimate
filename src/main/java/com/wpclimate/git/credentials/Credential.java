package com.wpclimate.git.credentials;

import java.util.Map;

import com.wpclimate.configurator.model.Model;

public interface Credential 
{
    public void configure(Map<String, String> configuration) throws Exception;

    public Model read() throws Exception;

    public CredentialsType getType();
}
