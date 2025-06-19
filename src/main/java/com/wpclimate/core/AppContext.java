package com.wpclimate.core;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.cli.WpCli;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.core.command.CommandRegistry;
import com.wpclimate.core.command.CommandRegistry.CommandInfo;
import com.wpclimate.git.Git;
import com.wpclimate.git.credentials.Credential;
import com.wpclimate.mateflow.MateFlowExecutor;
import com.wpclimate.mateflow.MateFlowManager;
import com.wpclimate.shell.RealTimeConsoleSpoofer;

/**
 * The {@code AppContext} class provides a centralized context for managing the core
 * components of the application, including WP-CLI and Git.
 * ...
 */
public class AppContext 
{
    private final WpCli wpCli;
    private final Git git;
    private final MateFlowManager mateFlowManager;
    private final MateFlowExecutor mateFlowExecutor;
    private final String workingDir;

    private final ReentrantLock lock;

    /**
     * Constructs an {@code AppContext} instance with the specified working directory,
     * WpCliModel and GitModel.
     *
     * @param workingDirectory The working directory for the application.
     * @param wpCliModel The WP-CLI configuration model.
     * @param gitModel The Git configuration model.
     * @throws Exception If an error occurs during the initialization.
     */
    public AppContext(String workingDirectory, RealTimeConsoleSpoofer spoofer) 
    {
        this.workingDir = workingDirectory;
        this.lock = new ReentrantLock();

        this.wpCli = new WpCli(this.workingDir, spoofer);
        this.git = new Git(this.workingDir, spoofer);
        CommandRegistry.initialize();

        this.mateFlowManager = new MateFlowManager(this.workingDir);
        this.mateFlowExecutor = new MateFlowExecutor(this.wpCli, this.git);
    }

    // --- Rest of the class remains unchanged, except typo fix for workingDir ---

    public WpCli getWpCli() 
    {
        this.lock.lock();
        try 
        {
            return this.wpCli;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    public Git getGit() 
    {
        this.lock.lock();
        try 
        {
            return this.git;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    public MateFlowManager getMateFlowManager()
    {
        this.lock.lock();
        try 
        {
            return this.mateFlowManager;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    public MateFlowExecutor getMateFlowExecutor()
    {
        this.lock.lock();
        try 
        {
            return this.mateFlowExecutor;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    public String getWorkingDirectory()
    {
        this.lock.lock();
        try 
        {
            return this.workingDir;
        } 
        finally 
        {
            this.lock.unlock();
        }
    }

    public Map<String, CommandInfo> getAvaliableCommands()
    {
        this.lock.lock();
        try 
        {
            return CommandRegistry.getAllCommands();
        } 
        finally 
        {
            this.lock.unlock();
        }
    }
}