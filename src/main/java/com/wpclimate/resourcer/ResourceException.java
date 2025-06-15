package com.wpclimate.resourcer;

/**
 * The {@code ResourceException} class represents a custom exception for errors related
 * to resource management in the application.
 *
 * <p>
 * This exception is thrown when operations on resources, such as files or directories,
 * fail due to invalid paths, missing resources, or other issues. It provides additional
 * context by including the {@link ResourceType} associated with the error.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Encapsulates error messages related to resource management.</li>
 *   <li>Provides the {@link ResourceType} associated with the error for better context.</li>
 *   <li>Supports chaining of exceptions using a {@link Throwable} cause.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code ResourceException} class is typically used in the {@link ResourceManager} and
 * related classes to handle errors during file and directory operations. For example:
 * </p>
 * <pre>
 * if (!Files.exists(path)) {
 *     throw new ResourceException("Resource not found: " + path, ResourceType.CONFIG_FILE);
 * }
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code ResourceException} class is immutable and thread-safe. Once an instance is created,
 * its state cannot be modified.
 * </p>
 *
 * @see ResourceType
 */
public class ResourceException extends RuntimeException 
{
    private final ResourceType resourceType;

    /**
     * Constructs a {@code ResourceException} with the specified error message and resource type.
     *
     * @param message      The error message describing the issue.
     * @param resourceType The {@link ResourceType} associated with the error.
     */
    public ResourceException(String message, ResourceType resourceType) 
    {
        super(message);
        this.resourceType = resourceType;
    }

    /**
     * Constructs a {@code ResourceException} with the specified error message, resource type,
     * and cause.
     *
     * @param message      The error message describing the issue.
     * @param resourceType The {@link ResourceType} associated with the error.
     * @param cause        The {@link Throwable} cause of the exception.
     */
    public ResourceException(String message, ResourceType resourceType, Throwable cause)
    {
        super(message, cause);
        this.resourceType = resourceType;
    }

    /**
     * Retrieves the {@link ResourceType} associated with the exception.
     *
     * <p>
     * The {@link ResourceType} provides additional context about the resource that caused
     * the error, such as whether it was a file, directory, or configuration resource.
     * </p>
     *
     * @return The {@link ResourceType} associated with the exception.
     */
    public ResourceType getResourceType() 
    {
        return resourceType;
    }
}
