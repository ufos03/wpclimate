package com.wpclimate.resourcer;

/**
 * The {@code ResourceType} enumeration defines the types of resources managed by the application.
 *
 * <p>
 * This enumeration is used to represent both files and directories within the application's
 * resource management system. Each resource type is associated with a name, a parent resource,
 * and a flag indicating whether it is a directory or a file.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Defines resource types for files and directories.</li>
 *   <li>Specifies hierarchical relationships between resources using parent-child relationships.</li>
 *   <li>Indicates whether a resource is a directory or a file.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code ResourceType} enumeration is typically used in conjunction with the {@link ResourceManager}
 * to manage resources such as configuration files, workflows, and directories. For example:
 * </p>
 * <pre>
 * ResourceType type = ResourceType.WPCLI_CONFIG;
 * System.out.println("Resource Name: " + type.getName());
 * System.out.println("Is Directory: " + type.isDirectory());
 * System.out.println("Parent Resource: " + (type.getParent() != null ? type.getParent().getName() : "None"));
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code ResourceType} enumeration is immutable and thread-safe. Once defined, its state cannot be modified.
 * </p>
 *
 * @see ResourceManager
 * @see FileResource
 * @see DirectoryResource
 */
public enum ResourceType {
    /**
     * Represents the settings directory.
     *
     * <p>
     * This directory contains configuration files and other settings-related resources.
     * </p>
     */
    SETTINGS_DIRECTORY(".settings", null, true),

    /**
     * Represents the SQL dump directory.
     *
     * <p>
     * This directory is used to store database dump files.
     * </p>
     */
    SQL_DUMP_DIRECTORY(".dump_directory", null, true),

    /**
     * Represents the WP-CLI configuration file.
     *
     * <p>
     * This file contains configuration data for WP-CLI operations.
     * </p>
     */
    WPCLI_CONFIG("wpCliConf.json", SETTINGS_DIRECTORY, false),

    /**
     * Represents the Git configuration file.
     *
     * <p>
     * This file contains configuration data for Git operations.
     * </p>
     */
    GIT_CONFIG("gitConf.json", SETTINGS_DIRECTORY, false),

    /**
     * Represents the workflow directory.
     *
     * <p>
     * This directory contains workflow-related files.
     * </p>
     */
    WORKFLOW_DIRECTORY("workflows", SETTINGS_DIRECTORY, true),

    /**
     * Represents the default workflow file.
     *
     * <p>
     * This file contains the default workflow configuration.
     * </p>
     */
    DEFAULT_WORKFLOW("default.json", WORKFLOW_DIRECTORY, false);

    private final String name;
    private final ResourceType parent;
    private final boolean isDirectory;

    /**
     * Constructs a {@code ResourceType} instance with the specified name, parent, and directory flag.
     *
     * @param name        The name of the resource.
     * @param parent      The parent {@link ResourceType}, or {@code null} if the resource has no parent.
     * @param isDirectory {@code true} if the resource is a directory, {@code false} if it is a file.
     */
    ResourceType(String name, ResourceType parent, boolean isDirectory) {
        this.name = name;
        this.parent = parent;
        this.isDirectory = isDirectory;
    }

    /**
     * Retrieves the name of the resource.
     *
     * @return The name of the resource.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the parent resource type.
     *
     * <p>
     * The parent resource type represents the hierarchical relationship between resources.
     * If the resource has no parent, this method returns {@code null}.
     * </p>
     *
     * @return The parent {@link ResourceType}, or {@code null} if the resource has no parent.
     */
    public ResourceType getParent() {
        return parent;
    }

    /**
     * Checks if the resource is a directory.
     *
     * @return {@code true} if the resource is a directory, {@code false} if it is a file.
     */
    public boolean isDirectory() {
        return isDirectory;
    }
}