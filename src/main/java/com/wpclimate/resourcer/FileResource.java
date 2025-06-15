package com.wpclimate.resourcer;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;

/**
 * The {@code FileResource} class represents a file resource in the system.
 *
 * <p>
 * This class encapsulates metadata about a file resource, including its type, path,
 * and last modified timestamp. It provides utility methods to retrieve information
 * about the file, such as its absolute path and {@link File} representation.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Represents a file resource with associated metadata.</li>
 *   <li>Provides methods to access the file's type, path, and last modified timestamp.</li>
 *   <li>Offers utility methods to retrieve the file's absolute path and {@link File} object.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code FileResource} class is typically used to represent files in the resource
 * management system. For example:
 * </p>
 * <pre>
 * Path filePath = Paths.get("/path/to/file.txt");
 * Instant lastModified = Instant.now();
 * 
 * FileResource fileResource = new FileResource(ResourceType.CONFIG_FILE, filePath, lastModified);
 * 
 * // Access file metadata
 * System.out.println("File Type: " + fileResource.getType());
 * System.out.println("File Path: " + fileResource.getPath());
 * System.out.println("Last Modified: " + fileResource.getLastModified());
 * 
 * // Get the absolute path
 * System.out.println("Absolute Path: " + fileResource.getAbsolutePath());
 * 
 * // Get the File object
 * File file = fileResource.getFile();
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code FileResource} class is immutable and thread-safe. Once an instance is created,
 * its state cannot be modified.
 * </p>
 *
 * @see ResourceType
 */
public class FileResource {

    private final ResourceType type;
    private final Path path;
    private final Instant lastModified;

    /**
     * Constructs a {@code FileResource} instance with the specified type, path, and last modified timestamp.
     *
     * @param type         The {@link ResourceType} of the file resource.
     * @param path         The {@link Path} to the file.
     * @param lastModified The {@link Instant} representing the last modified timestamp of the file.
     */
    public FileResource(ResourceType type, Path path, Instant lastModified) {
        this.type = type;
        this.path = path;
        this.lastModified = lastModified;
    }

    /**
     * Retrieves the type of the file resource.
     *
     * @return The {@link ResourceType} of the file resource.
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * Retrieves the path to the file resource.
     *
     * @return The {@link Path} to the file resource.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Retrieves the {@link File} object representing the file resource.
     *
     * @return The {@link File} object representing the file resource.
     */
    public File getFile() {
        return path.toFile();
    }

    /**
     * Retrieves the last modified timestamp of the file resource.
     *
     * @return The {@link Instant} representing the last modified timestamp of the file resource.
     */
    public Instant getLastModified() {
        return lastModified;
    }

    /**
     * Retrieves the absolute path of the file resource as a string.
     *
     * @return The absolute path of the file resource.
     */
    public String getAbsolutePath() {
        return path.toAbsolutePath().toString();
    }

    /**
     * Returns a string representation of the file resource.
     *
     * <p>
     * The string representation includes the type, path, and last modified timestamp
     * of the file resource.
     * </p>
     *
     * @return A string representation of the file resource.
     */
    @Override
    public String toString() {
        return "FileResource{type=" + type +
               ", path=" + path +
               ", lastModified=" + lastModified + "}";
    }
}
