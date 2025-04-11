package com.wpclimate.git.core;

import java.util.concurrent.locks.ReentrantLock;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

// TODO: Implentare autoload di GIT se non installato nell'OS

public class Dependency 
{
    private static final String GIT_VERSION_COMMAND = "--version";

    private final Shell shell;
    private final ReentrantLock lock = new ReentrantLock();


    public Dependency(Shell shell) 
    {
        if (shell == null)
            throw new IllegalArgumentException("Shell must not be null.");

        this.shell = shell;
    }

    public boolean isGitInstalled()
    {
        this.lock.lock();
        try
        {
            String command = String.format(
                "git %s",
                GIT_VERSION_COMMAND
            
            );
            CommandOutput output = this.shell.executeCommand(command);
            if (output.hasErrors())
                return false;
            return true;
        }
        finally
        {
            this.lock.unlock();
        }

    }
}
