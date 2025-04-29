package com.wpclimate.configurator;

import java.io.IOException;

import com.wpclimate.configurator.model.Model;

/**
 * The Configurator interface defines methods for managing configuration data.
 * It provides functionality to read, save, and set the file name for configuration storage.
 */
public interface Configurator 
{
    /**
     * Reads the configuration data from the file or other storage.
     *
     * @return A Model object containing the configuration data.
     */
    public Model read(String path)throws IOException, IllegalArgumentException;

    /**
     * Saves the configuration data to the file or other storage.
     *
     * @param configurationModel The Model object containing the configuration data to save.
     */
    public void save(String path, Model configurationModel) throws IOException, IllegalArgumentException ;
}