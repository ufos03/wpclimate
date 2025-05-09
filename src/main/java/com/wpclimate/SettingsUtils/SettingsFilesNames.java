package com.wpclimate.SettingsUtils;

/**
 * The {@code SettingsFilesNames} enum defines the names of the configuration files
 * and directories used by the application.
 * <p>
 * Each constant in this enum represents a specific file or directory name, making it
 * easier to reference and manage configuration files consistently throughout the application.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Provide a centralized definition of file and directory names used in the application.</li>
 *   <li>Associate each constant with its corresponding file or directory name as a string.</li>
 *   <li>Provide a method to retrieve the file or directory name associated with each constant.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Retrieve the file name for the WP-CLI configuration file
 * String wpCliFileName = SettingsFilesNames.WPCLI_FILE_NAME.getFileName();
 * System.out.println("WP-CLI File Name: " + wpCliFileName);
 * 
 * // Retrieve the directory name for settings
 * String settingsDirectory = SettingsFilesNames.SETTINGS_DIRECTORY.getFileName();
 * System.out.println("Settings Directory: " + settingsDirectory);
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * Enums in Java are inherently thread-safe and can be safely used in multi-threaded
 * environments.
 * </p>
 */
public enum SettingsFilesNames 
{

    /**
     * Represents the directory where all settings files are stored.
     */
    SETTINGS_DIRECTORY(".settings"),

    /**
     * Rapresents the directory where all SQL dumps are stored.
     */
    SQL_DUMP_DIRECTORY(".dump_directory"),

    /**
     * Represents the file name for the WP-CLI configuration file.
     */
    WPCLI_FILE_NAME("wpCliConf.json"),

    /**
     * Represents the file name for the Git configuration file.
     */
    GIT_CONF_FILE_NAME("gitConf.json");

    private final String fileName;

    /**
     * Constructs a {@code SettingsFilesNames} enum constant with the specified file name.
     *
     * @param name The file name or directory name associated with the constant.
     */
    SettingsFilesNames(String name) 
    {
        this.fileName = name;
    }

    /**
     * Retrieves the file name or directory name associated with the constant.
     *
     * @return The file name or directory name as a string.
     */
    public String getFileName() 
    {
        return this.fileName;
    }
}