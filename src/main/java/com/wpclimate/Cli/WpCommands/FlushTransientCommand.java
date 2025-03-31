package com.wpclimate.Cli.WpCommands;

import com.wpclimate.Cli.Core.Context;
import com.wpclimate.Cli.Core.Dependency;
import com.wpclimate.Cli.Exceptions.PHPNotInstalledException;
import com.wpclimate.Cli.Exceptions.WPCliNotInstalledException;
import com.wpclimate.Shell.CommandOutput;

public class FlushTransientCommand extends BaseWpCommand
{
    public FlushTransientCommand(Context context, Dependency dependency)
    {
        super(context, dependency);
    }

    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        dependency.isWpCliInstalled();

        String command = String.format
        (
            "%s %s --path=%s transient delete --all", 
            context.getWpModel().getPhp(),
            context.getWpModel().getWp(),
            this.context.getFileManager().getWorkingDirectory()
        );

        return context.getShell().executeCommand(command);
    }
}
