
package com.wpclimate.git.core;

import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.constants.FileManager;
import com.wpclimate.shell.Shell;

public class GitContext 
{
    private final Shell shell;
    private final FileManager fileManager;
    private final Dependency dependency;

    private final ReentrantLock lock = new ReentrantLock();

    public GitContext(Shell shell, FileManager fileManager, Dependency dependency)
    {
        this.shell = shell;
        this.fileManager = fileManager;
        this.dependency = dependency;
    }

    public Shell getShell() 
    {
        this.lock.lock();
        try 
        {
            return this.shell;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public FileManager getFileManager() 
    {
        this.lock.lock();
        try 
        {
            return this.fileManager;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public Dependency getDependency()
    {
        this.lock.lock();
        try 
        {
            return this.dependency;
        }
        finally
        {
            this.lock.unlock();
        }
    }
}
