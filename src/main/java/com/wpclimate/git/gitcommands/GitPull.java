package com.wpclimate.git.gitcommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.exceptions.ConfigurationMissing;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

/**
 * Implementation of Git pull command to fetch and integrate changes from a remote repository.
 * Critical for WordPress development to keep local environments in sync with remote changes.
 */
@GitCommand("git-pull")
public class GitPull extends BaseGitCommand 
{
    
    private String remote;
    private String branch;
    private boolean rebase;
    private boolean quiet;

    /**
     * Constructs a new GitPull command with specified parameters.
     *
     * @param context The Git context containing environment information
     * @param params Parameters for the pull operation, with support for:
     *               - "remote": The remote repository name (default: "origin")
     *               - "branch": The branch to pull from (default: current branch)
     *               - "rebase": Whether to use rebase instead of merge (default: false)
     *               - "quiet": Whether to suppress output except for errors (default: false)
     */
    public GitPull(GitContext context, Map<String, String> params) 
    {
        super(context);
        
        // Default values
        this.remote = "origin";
        this.branch = null; // Will determine current branch during execution
        this.rebase = false;
        this.quiet = false;
        
        if (params != null) 
        {
            if (params.containsKey("remote"))
                this.remote = params.get("remote");
            
            if (params.containsKey("branch"))
                this.branch = params.get("branch");
            
            if (params.containsKey("rebase"))
                this.rebase = Boolean.parseBoolean(params.get("rebase"));
            
            if (params.containsKey("quiet"))
                this.quiet = Boolean.parseBoolean(params.get("quiet"));
        }
    }

    /**
     * Executes the git pull command.
     * 
     * @return A CommandOutput object containing the result of the operation.
     * @throws Exception if the pull operation fails
     */
    @Override
    public CommandOutput execute() throws Exception 
    {
        List<String> parameters = new ArrayList<>();
        
        if (this.rebase)
            parameters.add("--rebase");
        
        if (this.quiet)
            parameters.add("--quiet");
        
        parameters.add(this.remote);
        
        if (this.branch != null && !this.branch.isEmpty())
            parameters.add(this.branch);
        
        Map<String, String> environment = new HashMap<>();
        String command = "";
        
        try 
        {
            environment = context.getCredentials().getGitEnvironment();
            command = context.getCredentials().getGitCommand("pull", parameters.toArray(new String[0]));
        } 
        catch (ConfigurationMissing e) 
        {
            System.out.println("Warning: No Git credentials configured. Pull may fail if authentication is required.");
            StringBuilder commandBuilder = new StringBuilder("git pull");
            
            for (String param : parameters)
                commandBuilder.append(" ").append(param);
            
            command = commandBuilder.toString();
        }

        return super.context.getShell().executeCommand(command, environment);
    }
}