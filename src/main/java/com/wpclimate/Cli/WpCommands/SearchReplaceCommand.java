package com.wpclimate.Cli.WpCommands;

import com.wpclimate.Cli.Core.Context;
import com.wpclimate.Cli.Core.Dependency;
import com.wpclimate.Cli.Exceptions.*;
import com.wpclimate.Shell.CommandOutput;

public class SearchReplaceCommand extends BaseWpCommand 
{
    private final String oldValue;
    private final String newValue;
    private final boolean allTables;
    private final boolean dryRun;

    public SearchReplaceCommand(Context context, Dependency dependency, String oldValue, String newValue, boolean allTables, boolean dryRun) 
    {
        super(context, dependency);
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.allTables = allTables;
        this.dryRun = dryRun;
    }

    @Override
    public CommandOutput execute() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        dependency.isWpCliInstalled();

        String command = String.format
        (
            "%s %s search-replace --path=%s '%s' '%s' %s %s",
            this.context.getWpModel().getPhp(),
            this.context.getWpModel().getWp(),
            this.context.getFileManager().getWorkingDirectory(),
            this.oldValue,
            this.newValue,
            this.allTables ? "--all-tables" : "",
            this.dryRun ? "--dry-run" : ""
        );
        //System.out.println(this.context.getFileManager().getWorkingDirectory());
        //System.out.println(command);
        return context.getShell().executeCommand(command);
    }
}
