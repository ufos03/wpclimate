package com.wpclimate.Constants;

/**
 * The {@code FileName} enum represents file names used in the application.
 * Each constant is associated with a specific file name as a string.
 */
public enum FileName 
{
    WPCLI_FILE_NAME("wpCliConf.json");

    private final String fileName;

    /**
     * Constructor for the {@code FileName} enum.
     *
     * @param fileName The file name associated with the constant.
     */
    FileName(String fileName) 
    {
        this.fileName = fileName;
    }

    /**
     * Retrieves the file name associated with the constant.
     *
     * @return The file name as a string.
     */
    public String getFileName() 
    {
        return this.fileName;
    }
}