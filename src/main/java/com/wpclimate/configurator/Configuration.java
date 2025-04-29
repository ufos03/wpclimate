package com.wpclimate.configurator;

import java.io.IOException;

import com.wpclimate.configurator.model.Model;

// TODO: Aggiorna doc

/**
 * The {@code Configuration} class implements the {@link Configurator} interface and provides
 * methods to read and save configuration data using a {@link ConfiguratorIO} instance.
 * 
 * <p>
 * This class acts as a bridge between the application and the underlying configuration
 * storage mechanism. It uses the {@link ConfiguratorIO} class to handle the actual
 * file operations, such as reading from and writing to JSON files.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Reads configuration data from a specified file path.</li>
 *   <li>Saves configuration data to a specified file path.</li>
 *   <li>Ensures that the configuration data is handled in a structured and consistent way.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * The {@code Configuration} class is typically used to manage application settings,
 * credentials, or other configuration data that needs to be persisted across sessions.
 * For example:
 * </p>
 * <pre>
 * Configuration configuration = new Configuration();
 * 
 * // Read configuration
 * Model model = configuration.read("/path/to/config.json");
 * 
 * // Modify and save configuration
 * model.set("key", "value");
 * configuration.save("/path/to/config.json", model);
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If multiple threads need to access the same configuration
 * file, external synchronization is required.
 * </p>
 * 
 * @see Configurator
 * @see ConfiguratorIO
 * @see Model
 */
public class Configuration implements Configurator 
{
    private final ConfiguratorIO configuratorIO;

    /**
     * Constructs a {@code Configuration} object.
     * 
     * <p>
     * This constructor initializes a new {@link ConfiguratorIO} instance, which is used
     * for all file operations related to configuration data.
     * </p>
     */
    public Configuration() 
    {
        this.configuratorIO = new ConfiguratorIO();
    }

    /**
     * Reads the configuration data from the specified file path.
     * 
     * <p>
     * This method uses the {@link ConfiguratorIO} instance to deserialize the configuration
     * data from a JSON file into a {@link Model} object. If the file does not exist or
     * cannot be read, an exception is thrown.
     * </p>
     * 
     * @param path The path to the configuration file.
     * @return A {@link Model} object containing the configuration data.
     * @throws Exception If an error occurs while reading the configuration file.
     * @throws IllegalArgumentException If the file path is null, empty, or invalid.
     */
    @Override
    public Model read(String path) throws IOException, IllegalArgumentException  
    {
        return configuratorIO.read(path);
    }

    /**
     * Saves the configuration data to the specified file path.
     * 
     * <p>
     * This method uses the {@link ConfiguratorIO} instance to serialize the configuration
     * data from a {@link Model} object into a JSON file. If the file cannot be written,
     * an exception is thrown.
     * </p>
     * 
     * @param path The path to the configuration file.
     * @param configurationModel The {@link Model} object containing the configuration data to save.
     * @throws Exception If an error occurs while writing to the configuration file.
     * @throws IllegalArgumentException If the file path is null, empty, or invalid.
     */
    @Override
    public void save(String path, Model configurationModel) throws IOException, IllegalArgumentException  
    {
        configuratorIO.write(path, configurationModel);
    }
}