package com.wpclimate.configurator;

import com.wpclimate.configurator.model.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * The MultiFileConfiguration class manages multiple configuration files
 * and implements the Configurator interface.
 */
public class MultiFileConfiguration implements Configurator 
{   
    private final Map<String, Configuration> configurations;

    public MultiFileConfiguration()
    {
        this.configurations = new HashMap<>();
    }

    /**
     * Constructs a MultiFileConfiguration with a map of file paths.
     *
     * @param filePaths A map associating a name with its file path.
     */
    public MultiFileConfiguration(Map<String, String> filePaths) {
        this.configurations = new HashMap<>();
        filePaths.forEach(this::addConfiguration);
    }

    /**
     * Adds a new configuration file to the manager.
     *
     * @param name     The name of the configuration.
     * @param filePath The file path of the configuration.
     */
    public void addConfiguration(String name, String filePath) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Configuration name cannot be null or empty.");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        configurations.put(name, new Configuration(filePath));
    }

    /**
     * Reads the configuration data for the specified name.
     *
     * @param name The name of the configuration to read.
     * @return A Model object containing the configuration data.
     * @throws Exception If an error occurs while reading the configuration.
     */
    public Model read(String name) throws Exception
    {
        Configuration configuration = configurations.get(name);
        if (configuration == null) {
            throw new IllegalArgumentException("No configuration found for name: " + name);
        }
        return configuration.read();
    }

    /**
     * Saves the configuration data for the specified name.
     *
     * @param name               The name of the configuration to save.
     * @param configurationModel The Model object containing the configuration data to save.
     * @throws Exception If an error occurs while saving the configuration.
     */
    public void save(String name, Model configurationModel) throws Exception {
        Configuration configuration = configurations.get(name);
        if (configuration == null) {
            throw new IllegalArgumentException("No configuration found for name: " + name);
        }
        configuration.save(configurationModel);
    }

    /**
     * Reads the default configuration (not supported for MultiFileConfiguration).
     *
     * @return Throws UnsupportedOperationException.
     */
    @Override
    public Model read() throws Exception {
        throw new UnsupportedOperationException("Use read(String name) to read a specific configuration.");
    }

    /**
     * Saves the default configuration (not supported for MultiFileConfiguration).
     *
     * @param configurationModel The Model object containing the configuration data to save.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public void save(Model configurationModel) throws Exception {
        throw new UnsupportedOperationException("Use save(String name, Model configurationModel) to save a specific configuration.");
    }
}