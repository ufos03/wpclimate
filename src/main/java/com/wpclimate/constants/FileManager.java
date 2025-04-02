package com.wpclimate.constants;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The {@code FileManager} class provides utility methods for managing configuration files.
 * It supports setting a working directory explicitly or detecting it automatically.
 */
public class FileManager 
{
    private final File workingDirectory;
        private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructs a {@code FileManager} with the specified working directory.
     *
     * @param workingDirectory The working directory as a string.
     */
    public FileManager(String workingDirectory) 
    {
        if (workingDirectory == null || workingDirectory.isEmpty())
            throw new IllegalArgumentException("Working directory cannot be null or empty.");

        this.workingDirectory = new File(workingDirectory);
        if (!this.workingDirectory.exists() || !this.workingDirectory.isDirectory())
            throw new IllegalArgumentException("The specified working directory does not exist or is not a directory.");
    }

    /**
     * Constructs a {@code FileManager} with the current working directory.
     */
    public FileManager() 
    {
        this.workingDirectory = new File(System.getProperty("user.dir"));
    }

    /**
     * Retrieves the working directory.
     *
     * @return The working directory as a {@link File}.
     */
    public File getWorkingDirectory() 
    {
        this.lock.lock();
        try
        {
            return this.workingDirectory;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * Checks if the specified file exists in the working directory.
     *
     * @param fileName The {@link FileName} enum constant representing the file.
     * @return {@code true} if the file exists in the working directory, {@code false} otherwise.
     */
    public boolean fileExists(FileName fileName) 
    {
        this.lock.lock();
        try
        {
            File file = new File(this.workingDirectory, fileName.getFileName());
            return file.exists();
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Retrieves the absolute path of the specified file in the working directory.
     *
     * @param fileName The {@link FileName} enum constant representing the file.
     * @return The absolute path of the file as a string.
     */
    public String getFilePath(FileName fileName) 
    {
        this.lock.lock();
        try
        {
            File file = new File(this.workingDirectory, fileName.getFileName());
            return file.getAbsolutePath();
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Creates the specified file in the working directory if it does not exist.
     *
     * @param fileName The {@link FileName} enum constant representing the file.
     * @return {@code true} if the file was created, {@code false} if it already exists.
     * @throws Exception If an error occurs while creating the file.
     */
    public boolean createFileIfNotExists(FileName fileName) throws Exception 
    {
        this.lock.lock();
        try 
        {
            File file = new File(this.workingDirectory, fileName.getFileName());
            if (!file.exists()) 
                return file.createNewFile();
            return false;
        } 
        finally 
        {
            lock.unlock();
        }
    }
}