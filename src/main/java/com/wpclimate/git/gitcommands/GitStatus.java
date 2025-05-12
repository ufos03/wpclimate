package com.wpclimate.git.gitcommands;

import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.exceptions.ConfigurationMissing;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

@GitCommand("git-status")
public class GitStatus extends BaseGitCommand
{
    public GitStatus(GitContext context)
    {
        super(context);
    }

    @Override
    public CommandOutput execute() throws ConfigurationMissing
    {    
        return super.context.getShell().executeCommand("git status", super.context.getCredentials().getGitEnvironment());
    }
}
