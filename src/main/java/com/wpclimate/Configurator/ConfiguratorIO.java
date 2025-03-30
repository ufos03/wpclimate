package com.wpclimate.Configurator;

import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.wpclimate.Configurator.Model.Model;
import com.wpclimate.Configurator.Model.Token;

/**
 * The {@code ConfiguratorIO} class provides functionality for reading and writing
 * configuration data to and from a file. It uses JSON as the format for serialization
 * and deserialization of the configuration data.
 */
public class ConfiguratorIO 
{
    private final String filePath; // Path to the configuration file
    private final Gson gson; // Gson instance for JSON serialization and deserialization

    /**
     * Constructs a {@code ConfiguratorIO} instance with the specified file path.
     *
     * @param filePath The path to the configuration file.
     */
    public ConfiguratorIO(String filePath) 
    {
        this.filePath = filePath;
        this.gson = new Gson();
    }

    /**
     * Writes the configuration data to the file.
     * The configuration is serialized into JSON format and saved to the specified file path.
     *
     * @param config The {@code Model} object containing the configuration data to write.
     * @throws IOException If an error occurs while writing to the file.
     */
    public void write(Model config) throws IOException 
    {
        try (FileWriter writer = new FileWriter(this.filePath)) 
        {
            String json = gson.toJson(config.getTokensList());
            writer.write(json);
        }
    }

    /**
     * Reads the configuration data from the file.
     * The configuration is deserialized from JSON format into a {@code Model} object.
     * If the file does not exist, an empty {@code Model} instance is returned.
     *
     * @return A {@code Model} object populated with the configuration data from the file.
     * @throws IOException If an error occurs while reading or parsing the file.
     */
    public Model read() throws Exception 
    {
        File file = new File(this.filePath);
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