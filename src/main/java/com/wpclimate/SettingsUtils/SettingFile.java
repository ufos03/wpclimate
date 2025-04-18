package com.wpclimate.SettingsUtils;

import java.io.File;
import java.io.IOException;

/**
 * The {@code SettingFile} class provides utility methods for managing files and directories.
 * <p>
 * This class includes static methods to check if a file exists, retrieve file paths,
 * create files if they do not exist, and verify if a given path is a directory.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Check if a file exists at a given path.</li>
 *   <li>Retrieve a {@link File} object for a specified file path.</li>
 *   <li>Create a file if it does not already exist.</li>
 *   <li>Verify if a given path is a directory.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Check if a file exists
 * boolean exists = SettingFile.fileExists("/path/to/file");
 * 
 * // Retrieve a File object
 * File file = SettingFile.getFilePath("/path/to/file");
 * 
 * // Create a file if it does not exist
 * boolean created = SettingFile.createFileIfNotExists("/path/to/file");
 * 
 * // Check if a path is a directory
 * boolean isDir = SettingFile.isDirectory("/path/to/directory");
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is thread-safe as all methods are static and operate independently.
 * </p>
 */
public class SettingFile 
{

    /**
     * Checks if a file exists at the specified path.
     *
     * @param fileName The path to the file.
     * @return {@code true} if the file exists; {@code false} otherwise.
     * @throws IllegalArgumentException If the file name is null, empty, or blank.
     */
    public static boolean fileExists(String fileName) 
    {
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("The path must be provided");

        File file = new File(fileName);
        return file.exists();
    }

    /**
     * Retrieves a {@link File} object for the specified file path.
     *
     * @param fileName The path to the file.
     * @return A {@link File} object representing the specified file path.
     * @throws IllegalArgumentException If the file name is null, empty, or blank.
     */
    public static File getFilePath(String fileName) 
    {
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("The path must be provided");

        return new File(fileName);
    }

    /**
     * Creates a file at the specified path if it does not already exist.
     *
     * @param fileName The path to the file.
     * @return {@code true} if the file was created; {@code false} if the file already exists.
     * @throws IOException If an I/O error occurs while creating the file.
     * @throws SecurityException If a security manager denies write access to the file.
     */
    public static boolean createFileIfNotExists(String fileName) throws IOException, SecurityException 
    {
        File file = new File(fileName);
        if (!file.exists())
            return file.createNewFile();

        return false;
    }

    /**
     * Checks if the specified path is a directory.
     *
     * @param directory The path to check.
     * @return {@code true} if the path is a directory; {@code false} otherwise.
     */
    public static boolean isDirectory(String directory) 
    {
        File directoryToCheck = new File(directory);
        return directoryToCheck.isDirectory();
    }
}