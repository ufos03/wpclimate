package com.wpclimate.configurator;

import java.io.IOException;

import com.wpclimate.configurator.model.Model;

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
 *   <li>Abstracts the underlying storage mechanism from the rest of the application.</li>
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
 * try {
 *     // Read configuration
 *     Model model = configuration.read("/path/to/config.json");
 *     
 *     // Modify and save configuration
 *     model.set("key", "value");
 *     configuration.save("/path/to/config.json", model);
 * } catch (IOException e) {
 *     // Handle exception
 *     System.err.println("Error handling configuration: " + e.getMessage());
 * }
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
     * cannot be read, an IOException is thrown.
     * </p>
     * 
     * @param path The path to the configuration file.
     * @return A {@link Model} object containing the configuration data.
     * @throws IOException If an error occurs while reading the configuration file,
     *         such as the file not existing or lacking read permissions.
     * @throws IllegalArgumentException If the file path is null, empty, or invalid,
     *         or if the file content cannot be parsed into a valid model.
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
     * an IOException is thrown. This method will create the file if it doesn't exist,
     * or overwrite it if it does.
     * </p>
     * 
     * @param path The path to the configuration file.
     * @param configurationModel The {@link Model} object containing the configuration data to save.
     * @throws IOException If an error occurs while writing to the configuration file,
     *         such as the directory not existing or lacking write permissions.
     * @throws IllegalArgumentException If the file path is null, empty, or invalid,
     *         or if the model is null or contains invalid data.
     */
    @Override
    public void save(String path, Model configurationModel) throws IOException, IllegalArgumentException  
    {
        configuratorIO.write(path, configurationModel);
    }
}