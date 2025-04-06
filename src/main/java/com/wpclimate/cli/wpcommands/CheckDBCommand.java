package com.wpclimate.cli.wpcommands;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.shell.CommandOutput;

public class CheckDBCommand extends BaseWpCommand
{
    public CheckDBCommand(Context context, Dependency dependency) 
    {
        super(context, dependency);
    }

    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        super.dependency.isWpCliInstalled();
        super.dependency.isAWordpressDirectory();


        String command = String.format(
            "%s %s --path=%s db check", 
            super.context.getWpModel().getPhp(),
            super.context.getWpModel().getWp(),
            super.context.getFileManager().getWorkingDirectory()
        );

        return super.context.getShell().executeCommand(command, super.configureEnvironmentVariables(super.context.getWpModel().getMYSQL()));
    }
}
