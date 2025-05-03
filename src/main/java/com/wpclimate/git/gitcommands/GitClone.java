package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.exceptions.GitNotInstalled;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

@GitCommand("git-clone")
public class GitClone extends BaseGitCommand
{
    private final String repoUrl;

    public GitClone(GitContext context, Map<String, String> remoteRepo)
    {
        super(context);
        if (remoteRepo == null || remoteRepo.isEmpty())
            throw new IllegalArgumentException("The remote repository link must be provided!");
        this.repoUrl = remoteRepo.get("remote");
    }

    @Override
    public CommandOutput execute() throws GitNotInstalled
    {
        super.context.getDependency().isGitInstalled(); //TODO: Aggiungere supporto alle credenziali: Costruire configuratore esterno comone?
        
        String command = String.format(
            "git clone %s",
            this.repoUrl 
        );

        return super.context.getShell().executeCommand(command);
    }

    @Override
    public String toString() {
        return String.format("GitClone Command [repoUrl=%s]", this.repoUrl);
    }
}
