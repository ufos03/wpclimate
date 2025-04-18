package com.wpclimate.core;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import com.wpclimate.SettingsUtils.SettingsFilesNames;
import com.wpclimate.cli.WpCli;
import com.wpclimate.git.Git;

public class AppContext 
{
    private final WpCli wpCli;
    private final Git git;

    private final ReentrantLock lock;

    public AppContext(String workingDirectory) throws Exception
    {
        this.lock = new ReentrantLock();
        this.createDirectorySettings(workingDirectory);

        this.git = new Git(workingDirectory);
        this.wpCli = new WpCli(workingDirectory);
    }

    private boolean createDirectorySettings(String workingDirectory)
    {
        SettingsFilesNames settingNameDirectory = SettingsFilesNames.SETTINGS_DIRECTORY;
        File settingsDirectory = new File(workingDirectory.concat("/").concat(settingNameDirectory.getFileName()));
        
        if (settingsDirectory.exists())
            return true;
        return settingsDirectory.mkdir();
    }

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
}
