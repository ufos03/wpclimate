package com.wpclimate.resourcer;

import com.wpclimate.resourcer.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The {@code ResourceManager} class provides centralized management of resources such as files
 * and directories within the application.
 *
 * <p>
 * This class implements the Singleton pattern to ensure a single instance is used throughout
 * the application. It manages resources based on their {@link ResourceType}, including creating,
 * retrieving, and verifying the existence of files and directories. It also supports creating
 * custom files and directories within predefined resource directories.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Manages resources such as files and directories based on {@link ResourceType}.</li>
 *   <li>Provides thread-safe access to resources using read/write locks.</li>
 *   <li>Initializes resources during application startup.</li>
 *   <li>Supports creating custom files and directories within resource directories.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * The {@code ResourceManager} class is typically used to manage application resources such as
 * configuration files, logs, and working directories. For example:
 * </p>
 * <pre>
 * ResourceManager manager = ResourceManager.getInstance();
 * 
 * // Set the working directory
 * manager.setWorkingDirectory("/path/to/working/directory");
 * 
 * // Retrieve a resource
 * FileResource configFile = manager.getResource(ResourceType.CONFIG_FILE);
 * 
 * // Create a custom file
 * FileResource customFile = manager.createCustomFile(ResourceType.SETTINGS_DIRECTORY, "custom-config.json");
 * 
 * // Create a custom directory
 * DirectoryResource customDir = manager.createCustomDirectory(ResourceType.SETTINGS_DIRECTORY, "custom-settings");
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * The {@code ResourceManager} class is thread-safe. It uses {@link ReentrantReadWriteLock} to
 * synchronize access to resources, ensuring safe concurrent access.
 * </p>
 *
 * @see ResourceType
 * @see FileResource
 * @see DirectoryResource
 * @see ResourceException
 */
public class ResourceManager {

    private static final ResourceManager INSTANCE = new ResourceManager();

    private final Map<ResourceType, FileResource> resources = new EnumMap<>(ResourceType.class);
    private final Map<Path, FileResource> pathToResource = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Path workingDirectory;

    /**
     * Private constructor for the Singleton pattern.
     *
     * <p>
     * Initializes the {@code ResourceManager} with the default working directory and
     * sets up resources based on their {@link ResourceType}.
     * </p>
     */
    private ResourceManager() {
        this.workingDirectory = Paths.get(System.getProperty("user.dir"));
        initializeResources();
    }

    /**
     * Retrieves the singleton instance of {@code ResourceManager}.
     *
     * @return The singleton instance of {@code ResourceManager}.
     */
    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the working directory for the application.
     *
     * <p>
     * This method clears the existing resource cache and reinitializes resources based
     * on the new working directory.
     * </p>
     *
     * @param path The path to the new working directory.
     * @throws ResourceException If the specified path is invalid or not a directory.
     */
    public void setWorkingDirectory(String path) {
        lock.writeLock().lock();
        try {
            Path newDir = Paths.get(path).toAbsolutePath();
            if (!Files.exists(newDir) || !Files.isDirectory(newDir)) {
                throw new ResourceException("Invalid working directory: " + path, null);
            }
            this.workingDirectory = newDir;
            resources.clear();
            pathToResource.clear();
            initializeResources();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves the current working directory.
     *
     * @return The {@link Path} to the current working directory.
     */
    public Path getWorkingDirectory() {
        lock.readLock().lock();
        try {
            return workingDirectory;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Initializes resources based on their {@link ResourceType}.
     *
     * <p>
     * This method creates directories and files as defined by the {@link ResourceType}
     * enumeration, ensuring that all required resources are available.
     * </p>
     */
    private void initializeResources() {
        lock.writeLock().lock();
        try {
            for (ResourceType type : ResourceType.values()) {
                if (type.isDirectory() && type.getParent() == null) {
                    createResource(type);
                }
            }
            for (ResourceType type : ResourceType.values()) {
                if (type.isDirectory() && type.getParent() != null) {
                    createResource(type);
                }
            }
            for (ResourceType type : ResourceType.values()) {
                if (!type.isDirectory()) {
                    createResource(type);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Creates a resource based on its {@link ResourceType}.
     *
     * @param type The {@link ResourceType} of the resource to create.
     * @return The created {@link FileResource}.
     * @throws ResourceException If the resource cannot be created.
     */
    private FileResource createResource(ResourceType type) {
        if (resources.containsKey(type)) {
            return resources.get(type);
        }

        Path path;
        if (type.getParent() == null) {
            path = workingDirectory.resolve(type.getName());
        } else {
            DirectoryResource parent = (DirectoryResource) createResource(type.getParent());
            path = parent.getPath().resolve(type.getName());
        }

        try {
            if (type.isDirectory()) {
                FileUtils.createDirectoryIfNotExists(path);
                DirectoryResource dir = new DirectoryResource(type, path, Files.getLastModifiedTime(path).toInstant());
                resources.put(type, dir);
                pathToResource.put(path, dir);
                return dir;
            } else {
                FileUtils.createFileIfNotExists(path);
                FileResource file = new FileResource(type, path, Files.getLastModifiedTime(path).toInstant());
                resources.put(type, file);
                pathToResource.put(path, file);

                if (type.getParent() != null) {
                    DirectoryResource parent = (DirectoryResource) resources.get(type.getParent());
                    parent.addChild(file);
                }
                return file;
            }
        } catch (IOException e) {
            throw new ResourceException("Failed to initialize resource: " + type, type, e);
        }
    }

    /**
     * Retrieves a resource by its {@link ResourceType}.
     *
     * @param type The {@link ResourceType} of the resource to retrieve.
     * @return The {@link FileResource} associated with the specified type.
     * @throws ResourceException If the resource is not found.
     */
    public FileResource getResource(ResourceType type) {
        lock.readLock().lock();
        try {
            FileResource resource = resources.get(type);
            if (resource == null) {
                throw new ResourceException("Resource not found: " + type, type);
            }
            return resource;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Retrieves the absolute path of a resource by its {@link ResourceType}.
     *
     * @param type The {@link ResourceType} of the resource.
     * @return The absolute path of the resource as a string.
     */
    public String getPath(ResourceType type) {
        return getResource(type).getAbsolutePath();
    }

    /**
     * Retrieves the {@link File} object of a resource by its {@link ResourceType}.
     *
     * @param type The {@link ResourceType} of the resource.
     * @return The {@link File} object of the resource.
     */
    public File getFile(ResourceType type) {
        return getResource(type).getFile();
    }

    /**
     * Checks if a resource exists by its {@link ResourceType}.
     *
     * @param type The {@link ResourceType} of the resource.
     * @return {@code true} if the resource exists, {@code false} otherwise.
     */
    public boolean exists(ResourceType type) {
        lock.readLock().lock();
        try {
            if (!resources.containsKey(type)) {
                return false;
            }
            return Files.exists(resources.get(type).getPath());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Creates a custom file within a specified parent directory resource.
     *
     * @param parentDir The parent directory {@link ResourceType}.
     * @param fileName  The name of the custom file to create.
     * @return The created {@link FileResource}.
     * @throws ResourceException If the parent is not a directory or the file cannot be created.
     */
    public FileResource createCustomFile(ResourceType parentDir, String fileName) {
        lock.writeLock().lock();
        try {
            if (!parentDir.isDirectory()) {
                throw new ResourceException("Parent must be a directory: " + parentDir, parentDir);
            }

            DirectoryResource parent = (DirectoryResource) getResource(parentDir);
            Path filePath = parent.getPath().resolve(fileName);

            if (pathToResource.containsKey(filePath)) {
                return pathToResource.get(filePath);
            }

            FileUtils.createFileIfNotExists(filePath);

            try {
                FileResource file = new FileResource(null, filePath, Files.getLastModifiedTime(filePath).toInstant());
                parent.addChild(file);
                pathToResource.put(filePath, file);
                return file;
            } catch (IOException e) {
                throw new ResourceException("Failed to create custom file: " + fileName, parentDir, e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Creates a custom directory within a specified parent directory resource.
     *
     * @param parentDir The parent directory {@link ResourceType}.
     * @param dirName   The name of the custom directory to create.
     * @return The created {@link DirectoryResource}.
     * @throws ResourceException If the parent is not a directory or the directory cannot be created.
     */
    public DirectoryResource createCustomDirectory(ResourceType parentDir, String dirName) {
        lock.writeLock().lock();
        try {
            if (!parentDir.isDirectory()) {
                throw new ResourceException("Parent must be a directory: " + parentDir, parentDir);
            }

            DirectoryResource parent = (DirectoryResource) getResource(parentDir);
            Path dirPath = parent.getPath().resolve(dirName);

            if (pathToResource.containsKey(dirPath)) {
                FileResource resource = pathToResource.get(dirPath);
                if (resource instanceof DirectoryResource) {
                    return (DirectoryResource) resource;
                }
                throw new ResourceException("Path exists but is not a directory: " + dirPath, parentDir);
            }

            FileUtils.createDirectoryIfNotExists(dirPath);

            try {
                DirectoryResource dir = new DirectoryResource(null, dirPath, Files.getLastModifiedTime(dirPath).toInstant());
                parent.addChild(dir);
                pathToResource.put(dirPath, dir);
                return dir;
            } catch (IOException e) {
                throw new ResourceException("Failed to create custom directory: " + dirName, parentDir, e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}