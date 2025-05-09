package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

@GitCommand("git-clone")
public class GitClone extends BaseGitCommand
{
    private final String gitCommand;
    private final Map<String, String> env;

    public GitClone(GitContext context, Map<String, String> remoteRepo) throws Exception
    {
        super(context);
        if (remoteRepo == null || remoteRepo.isEmpty())
            throw new IllegalArgumentException("The remote repository link must be provided!");
       
        this.gitCommand = context.getCredentials().getGitCommand("clone");
        this.env = context.getCredentials().getGitEnvironment();
    }

    @Override
    public CommandOutput execute()
    {        
        String command = String.format(
            "%s",
            this.gitCommand
        );

        return super.context.getShell().executeCommand(command, this.env);
    }

    @Override
    public String toString() {
        return String.format("GitClone Command [repoUrl=%s]", this.gitCommand);
    }
}
