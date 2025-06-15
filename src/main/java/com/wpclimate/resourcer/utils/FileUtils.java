package com.wpclimate.resourcer.utils;

import com.wpclimate.resourcer.ResourceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The {@code FileUtils} class provides utility methods for performing common file and directory
 * operations, such as checking existence, creating files or directories, and deleting files.
 *
 * <p>
 * This class is designed to simplify file management tasks and handle exceptions gracefully
 * by throwing {@link ResourceException} for errors encountered during file operations.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Check if a file or directory exists.</li>
 *   <li>Verify if a path is a directory.</li>
 *   <li>Create directories if they do not exist.</li>
 *   <li>Create files if they do not exist, ensuring parent directories are created.</li>
 *   <li>Delete files or directories.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code FileUtils} class is a static utility class and does not require instantiation.
 * It can be used directly to perform file operations. For example:
 * </p>
 * <pre>
 * Path path = Paths.get("/path/to/file.txt");
 * 
 * // Check if the file exists
 * boolean exists = FileUtils.exists(path);
 * 
 * // Create the file if it does not exist
 * if (!exists) {
 *     FileUtils.createFileIfNotExists(path);
 * }
 * 
 * // Delete the file
 * FileUtils.delete(path);
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code FileUtils} class is thread-safe as it does not maintain any mutable state.
 * However, concurrent access to the same file or directory may lead to race conditions
 * and should be handled externally.
 * </p>
 *
 * @see ResourceException
 */
public class FileUtils {

    /**
     * Checks if a file or directory exists at the specified path.
     *
     * @param path The {@link Path} to check.
     * @return {@code true} if the file or directory exists, {@code false} otherwise.
     */
    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    /**
     * Checks if the specified path is a directory.
     *
     * @param path The {@link Path} to check.
     * @return {@code true} if the path is a directory, {@code false} otherwise.
     */
    public static boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    /**
     * Creates a directory at the specified path if it does not already exist.
     *
     * <p>
     * If the directory already exists, this method does nothing and returns {@code false}.
     * If the directory does not exist, it is created along with any necessary parent directories.
     * </p>
     *
     * @param path The {@link Path} where the directory should be created.
     * @return {@code true} if the directory was created, {@code false} if it already exists.
     * @throws ResourceException If an error occurs while creating the directory.
     */
    public static boolean createDirectoryIfNotExists(Path path) throws ResourceException {
        if (exists(path))
            return false;

        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            throw new ResourceException("Failed to create directory: " + path, null, e);
        }
    }

    /**
     * Creates a file at the specified path if it does not already exist.
     *
     * <p>
     * If the file already exists, this method does nothing and returns {@code false}.
     * If the file does not exist, it is created along with any necessary parent directories.
     * </p>
     *
     * @param path The {@link Path} where the file should be created.
     * @return {@code true} if the file was created, {@code false} if it already exists.
     * @throws ResourceException If an error occurs while creating the file.
     */
    public static boolean createFileIfNotExists(Path path) throws ResourceException {
        if (exists(path))
            return false;

        Path parent = path.getParent();
        if (parent != null)
            createDirectoryIfNotExists(parent);

        try {
            Files.createFile(path);
            return true;
        } catch (IOException e) {
            throw new ResourceException("Failed to create file: " + path, null, e);
        }
    }

    /**
     * Deletes a file or directory at the specified path.
     *
     * <p>
     * If the file or directory does not exist, this method does nothing and returns {@code false}.
     * If the file or directory exists, it is deleted. Note that directories must be empty
     * to be deleted.
     * </p>
     *
     * @param path The {@link Path} to delete.
     * @return {@code true} if the file or directory was deleted, {@code false} if it did not exist.
     * @throws ResourceException If an error occurs while deleting the file or directory.
     */
    public static boolean delete(Path path) throws ResourceException {
        if (!exists(path))
            return false;

        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            throw new ResourceException("Failed to delete: " + path, null, e);
        }
    }
}
