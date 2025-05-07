package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

@GitCommand("git-clone")
public class GitClone extends BaseGitCommand
{
    private final String repoUrl;

    public GitClone(GitContext context, Map<String, String> remoteRepo) throws Exception
    {
        super(context);
        if (remoteRepo == null || remoteRepo.isEmpty())
            throw new IllegalArgumentException("The remote repository link must be provided!");
        this.repoUrl = context.getCredentials().getGitCommand();
    }

    @Override
    public CommandOutput execute()
    {        
        String command = String.format(
            "git clone -q --progress %s",
            this.repoUrl
        );

        return super.context.getShell().executeCommand(command, Map.of("GIT_FLUSH", "1"));
    }

    @Override
    public String toString() {
        return String.format("GitClone Command [repoUrl=%s]", this.repoUrl);
    }
}
