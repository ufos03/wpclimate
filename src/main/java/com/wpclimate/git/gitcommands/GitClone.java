package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.exceptions.ConfigurationMissing;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

@GitCommand("git-clone")
public class GitClone extends BaseGitCommand
{
    //TODO: Pensare agli ambienti.
    public GitClone(GitContext context, Map<String, String> remoteRepo) throws IllegalArgumentException
    {
        super(context);
        if (remoteRepo == null || remoteRepo.isEmpty())
            throw new IllegalArgumentException("The remote repository link must be provided!");
    }

    @Override
    public CommandOutput execute() throws ConfigurationMissing
    {        
        String command = String.format(
            "%s",
            super.context.getCredentials().getGitCommand("clone")
        );

        return super.context.getShell().executeCommand(command, super.context.getCredentials().getGitEnvironment());
    }
}
