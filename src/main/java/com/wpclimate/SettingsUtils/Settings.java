package com.wpclimate.SettingsUtils;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The {@code Settings} class provides utility methods for managing configuration files
 * and directories in the application.
 * <p>
 * This class allows you to set a working directory explicitly or use the current working
 * directory by default. It also provides methods to retrieve file paths for specific
 * configuration files within the working directory.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Manage the working directory for the application.</li>
 *   <li>Retrieve paths for specific configuration files.</li>
 *   <li>Ensure thread-safe access to the working directory and settings files.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Initialize with a specific working directory
 * Settings settings = new Settings("/path/to/working/directory");
 * 
 * // Retrieve the working directory
 * File workingDir = settings.getWorkingDirectory();
 * 
 * // Retrieve the path to a specific settings file
 * String filePath = settings.getSetting(SettingsFilesNames.WPCLI_FILE_NAME);
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class uses a {@link ReentrantLock} to ensure thread-safe access to its methods.
 * </p>
 *
 * @see SettingFile
 * @see SettingsFilesNames
 */
public class Settings {

    private final String workingDirectory;
    private final File settingsDirectory;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructs a {@code Settings} instance with the specified working directory.
     * <p>
     * If the specified working directory does not exist or is not a directory, an
     * {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param workingDirectory The working directory as a string.
     * @throws IllegalArgumentException If the working directory is null, empty, or invalid.
     */
    public Settings(String workingDirectory) 
    {
        if (workingDirectory == null || workingDirectory.isEmpty())
            throw new IllegalArgumentException("Working directory cannot be null or empty.");

        if (!SettingFile.fileExists(workingDirectory) || !SettingFile.isDirectory(workingDirectory))
            throw new IllegalArgumentException("The specified working directory does not exist or is not a directory.");

        this.workingDirectory = workingDirectory;
        this.settingsDirectory = SettingFile.getFilePath(SettingsFilesNames.SETTINGS_DIRECTORY.getFileName());
    }

    /**
     * Constructs a {@code Settings} instance with the current working directory.
     * <p>
     * The current working directory is determined using the {@code user.dir} system property.
     * </p>
     */
    public Settings() 
    {
        this.workingDirectory = new File(System.getProperty("user.dir")).toString();
        this.settingsDirectory = SettingFile.getFilePath(SettingsFilesNames.SETTINGS_DIRECTORY.getFileName());
    }

    /**
     * Retrieves the working directory.
     * <p>
     * This method ensures thread-safe access to the working directory.
     * </p>
     *
     * @return The working directory as a {@link File}.
     */
    public File getWorkingDirectory() 
    {
        this.lock.lock();
        try 
        {
            return SettingFile.getFilePath(this.workingDirectory);
        } 
        finally 
        {
            lock.unlock();
        }
    }

    /**
     * Retrieves the absolute path of the specified file in the working directory.
     * <p>
     * This method constructs the full path to the specified settings file by appending
     * its name to the working directory path.
     * </p>
     *
     * @param fileName The {@link SettingsFilesNames} enum constant representing the file.
     * @return The absolute path of the file as a string.
     * @throws IllegalArgumentException If the file name is null or invalid.
     */
    public String getSetting(SettingsFilesNames fileName) 
    {
        this.lock.lock();
        try 
        {
            StringBuilder settingFile = new StringBuilder();

            settingFile.append(this.workingDirectory);
            settingFile.append(this.settingsDirectory.toString());
            settingFile.append("/");
            settingFile.append(SettingFile.getFilePath(fileName.getFileName()));
            return settingFile.toString();
        } 
        finally 
        {
            this.lock.unlock();
        }
    }
}