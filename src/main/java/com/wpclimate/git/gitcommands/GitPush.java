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
 * Implementation of Git push command to send local changes to a remote repository.
 * This command is particularly important for WordPress development to deploy
 * changes to staging or production environments.
 */
@GitCommand("git-push")
public class GitPush extends BaseGitCommand 
{
    
    private String remote;
    private String branch;
    private boolean setUpstream;
    private boolean force;

    /**
     * Constructs a new GitPush command with specified parameters.
     *
     * @param context The Git context containing environment information
     * @param params Parameters for the push operation, with support for:
     *               - "remote": The remote repository name (default: "origin")
     *               - "branch": The branch to push (default: "main" or "master")
     *               - "setUpstream": Whether to set upstream (default: false)
     *               - "force": Whether to force push (default: false)
     */
    public GitPush(GitContext context, Map<String, String> params) 
    {
        super(context);
        
        // Default values
        this.remote = "origin";
        this.branch = "main"; // Default to main, will try to detect if not specified
        this.setUpstream = false;
        this.force = false;
        
        // Parse parameters if provided
        if (params != null) 
        {
            if (params.containsKey("remote"))
                this.remote = params.get("remote");
            
            if (params.containsKey("branch"))
                this.branch = params.get("branch");
            
            if (params.containsKey("setUpstream"))
                this.setUpstream = Boolean.parseBoolean(params.get("setUpstream"));
            
            if (params.containsKey("force"))
                this.force = Boolean.parseBoolean(params.get("force"));
        }
    }

    /**
     * Executes the git push command.
     * 
     * @return A CommandOutput object containing the result of the operation.
     * @throws Exception if the push operation fails
     */
    @Override
    public CommandOutput execute() throws Exception 
    {
        if (branch == null || branch.isEmpty()) 
        {
            CommandOutput branchOutput = context.getShell().executeCommand("git rev-parse --abbrev-ref HEAD");
            if (branchOutput.isSuccessful())
                branch = branchOutput.getStandardOutput().trim();
            else
                branch = "main";
        }
        
        List<String> parameters = new ArrayList<>();
        
        if (this.force)
            parameters.add("--force");
        
        if (this.setUpstream)
            parameters.add("--set-upstream");
        
        parameters.add(this.remote);
        parameters.add(this.branch);
        
        Map<String, String> environment = new HashMap<>();
        String command = "";
        
        try 
        {
            environment = context.getCredentials().getGitEnvironment();
            command = context.getCredentials().getGitCommand("push", parameters.toArray(new String[0]));
        } 
        catch (ConfigurationMissing e) 
        {
            System.out.println("Warning: No Git credentials configured. Push may fail if authentication is required.");
            StringBuilder commandBuilder = new StringBuilder("git push");
            
            for (String param : parameters)
                commandBuilder.append(" ").append(param);
            
            command = commandBuilder.toString();
        }
        
        return super.context.getShell().executeCommand(command, environment);
    }
}