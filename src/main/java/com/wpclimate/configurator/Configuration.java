package com.wpclimate.configurator;

import com.wpclimate.configurator.model.Model;

/**
 * The Configuration class implements the Configurator interface and provides
 * methods to read and save configuration data using a ConfiguratorIO instance.
 */
public class Configuration implements Configurator 
{
    private final ConfiguratorIO configuratorIO;

    /**
     * Constructs a Configuration object with the specified file path.
     *
     * @param filepath The path to the configuration file.
     */
    public Configuration(String filepath) 
    {
        if (filepath == null || filepath.isEmpty())
            throw new IllegalArgumentException("File path cannot be null or empty.");

        this.configuratorIO = new ConfiguratorIO(filepath);
    }

    /**
     * Reads the configuration data from the file.
     *
     * @return A Model object containing the configuration data.
     * @throws IllegalStateException If the ConfiguratorIO instance is not initialized.
     */
    @Override
    public Model read() throws Exception 
    {
        return configuratorIO.read();
    }

    /**
     * Saves the configuration data to the file.
     *
     * @param configurationModel The Model object containing the configuration data to save.
     * @throws IllegalStateException If the ConfiguratorIO instance is not initialized.
     */
    @Override
    public void save(Model configurationModel) throws Exception {
        configuratorIO.write(configurationModel);
    }
}