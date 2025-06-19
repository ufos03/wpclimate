package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.core.command.CommandParam;
import com.wpclimate.core.command.ParamType;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.exceptions.ConfigurationMissing;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

@GitCommand("git-clone")
public class GitClone extends BaseGitCommand
{
    public GitClone(GitContext context, @CommandParam(name="remote", type=ParamType.STRING, required=true, description="Url SSH/HTTP della repo remota")Map<String, String> remoteRepo) throws IllegalArgumentException
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
