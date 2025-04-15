package com.wpclimate.configurator;

import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.wpclimate.configurator.model.Model;
import com.wpclimate.configurator.model.Token;

/**
 * The {@code ConfiguratorIO} class provides functionality for reading and writing
 * configuration data to and from a file. It uses JSON as the format for serialization
 * and deserialization of the configuration data.
 * <p>
 * This class is designed to handle configuration files in a structured way, allowing
 * the storage and retrieval of key-value pairs with optional encryption flags.
 * </p>
 * <p>
 * The configuration data is represented by the {@link Model} class, which contains
 * a list of {@link Token} objects. Each token represents a key-value pair with an
 * optional encryption flag.
 * </p>
 */
public class ConfiguratorIO 
{
    private final Gson gson; // Gson instance for JSON serialization and deserialization

    /**
     * Constructs a {@code ConfiguratorIO} instance.
     * <p>
     * This constructor initializes the Gson instance used for JSON serialization
     * and deserialization.
     * </p>
     */
    public ConfiguratorIO() 
    {
        this.gson = new Gson();
    }

    /**
     * Writes the configuration data to the specified file.
     * <p>
     * The configuration data is serialized into JSON format and saved to the specified
     * file path. If the file does not exist, it will be created.
     * </p>
     *
     * @param filePath The path to the file where the configuration data will be saved.
     * @param config   The {@link Model} object containing the configuration data to write.
     * @throws IOException              If an error occurs while writing to the file.
     * @throws IllegalArgumentException If the file path is null, empty, or blank.
     */
    public void write(String filePath, Model config) throws IOException, IllegalArgumentException 
    {
        if (filePath == null || filePath.isEmpty() || filePath.isBlank())
            throw new IllegalArgumentException("The filePath isn't valid");

        try (FileWriter writer = new FileWriter(filePath)) 
        {
            String json = gson.toJson(config.getTokensList());
            writer.write(json);
        }
    }

    /**
     * Reads the configuration data from the specified file.
     * <p>
     * The configuration data is deserialized from JSON format into a {@link Model} object.
     * If the file does not exist, a {@link FileNotFoundException} is thrown.
     * </p>
     *
     * @param filePath The path to the file from which the configuration data will be read.
     * @return A {@link Model} object populated with the configuration data from the file.
     * @throws IOException              If an error occurs while reading or parsing the file.
     * @throws IllegalArgumentException If the file path is null, empty, or blank.
     * @throws FileNotFoundException    If the specified file does not exist.
     */
    public Model read(String filePath) throws IOException, IllegalArgumentException 
    {
        if (filePath == null || filePath.isEmpty() || filePath.isBlank())
            throw new IllegalArgumentException("The filePath isn't valid");

        File file = new File(filePath);
        if (!file.exists())
            throw new FileNotFoundException("The specified filepath doesn't exist!");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                jsonBuilder.append(line);

            String json = jsonBuilder.toString();

            ArrayList<Token> tokens = gson.fromJson(json, new com.google.gson.reflect.TypeToken<ArrayList<Token>>() {}.getType());

            Model model = new Model();
            for (Token token : tokens)
                model.set(token.getKey(), token.getValue(), token.isEncrypted());

            return model;
        } 
        catch (Exception e) 
        {
            throw new IOException("Failed to read or parse the configuration file.", e);
        }
    }
}