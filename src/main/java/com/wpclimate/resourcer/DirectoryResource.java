package com.wpclimate.resourcer;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code DirectoryResource} class represents a directory resource that can contain other resources.
 *
 * <p>
 * This class extends {@link FileResource} and adds functionality for managing child resources
 * within a directory. It provides methods to add, retrieve, and count child resources, as well
 * as check if the directory is empty.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Represents a directory resource in the system.</li>
 *   <li>Manages child resources within the directory.</li>
 *   <li>Provides methods to retrieve child resources and check directory properties.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code DirectoryResource} class is typically used to represent directories in the resource
 * management system. It can be used to add child resources and retrieve information about the
 * directory's contents. For example:
 * </p>
 * <pre>
 * DirectoryResource directory = new DirectoryResource(ResourceType.SETTINGS_DIRECTORY, path, Instant.now());
 * 
 * // Add child resources
 * directory.addChild(new FileResource(ResourceType.WPCLI_CONFIG, path.resolve("wpCliConf.json"), Instant.now()));
 * 
 * // Retrieve child resources
 * List<FileResource> children = directory.getChildren();
 * 
 * // Check if the directory is empty
 * boolean isEmpty = directory.isEmpty();
 * 
 * // Count the number of child resources
 * int count = directory.count();
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code DirectoryResource} class is not thread-safe. If multiple threads need to access
 * or modify the same instance, external synchronization is required.
 * </p>
 *
 * @see FileResource
 * @see ResourceType
 */
public class DirectoryResource extends FileResource {

    private final List<FileResource> children = new ArrayList<>();

    /**
     * Constructs a {@code DirectoryResource} instance with the specified type, path, and last modified timestamp.
     *
     * <p>
     * This constructor initializes the directory resource and ensures that the specified
     * {@link ResourceType} represents a directory.
     * </p>
     *
     * @param type         The {@link ResourceType} of the directory resource.
     * @param path         The {@link Path} to the directory.
     * @param lastModified The {@link Instant} representing the last modified timestamp of the directory.
     * @throws IllegalArgumentException If the {@link ResourceType} does not represent a directory.
     */
    public DirectoryResource(ResourceType type, Path path, Instant lastModified) {
        super(type, path, lastModified);
        if (!type.isDirectory())
            throw new IllegalArgumentException("ResourceType must be a directory: " + type);
    }

    /**
     * Adds a child resource to the directory.
     *
     * <p>
     * This method adds the specified {@link FileResource} to the list of child resources
     * managed by this directory.
     * </p>
     *
     * @param child The {@link FileResource} to add as a child.
     */
    public void addChild(FileResource child) {
        children.add(child);
    }

    /**
     * Retrieves an unmodifiable list of child resources in the directory.
     *
     * <p>
     * This method returns a list of all {@link FileResource} objects contained within
     * the directory. The returned list is unmodifiable to ensure the integrity of the
     * directory's contents.
     * </p>
     *
     * @return An unmodifiable list of child resources.
     */
    public List<FileResource> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Checks if the directory is empty.
     *
     * <p>
     * This method returns {@code true} if the directory contains no child resources,
     * and {@code false} otherwise.
     * </p>
     *
     * @return {@code true} if the directory is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return children.isEmpty();
    }

    /**
     * Counts the number of child resources in the directory.
     *
     * <p>
     * This method returns the total number of {@link FileResource} objects contained
     * within the directory.
     * </p>
     *
     * @return The number of child resources in the directory.
     */
    public int count() {
        return children.size();
    }

    /**
     * Returns a string representation of the directory resource.
     *
     * <p>
     * The string representation includes the type, path, and the number of child resources
     * in the directory.
     * </p>
     *
     * @return A string representation of the directory resource.
     */
    @Override
    public String toString() {
        return "DirectoryResource{type=" + getType() +
               ", path=" + getPath() +
               ", childCount=" + children.size() + "}";
    }
}
