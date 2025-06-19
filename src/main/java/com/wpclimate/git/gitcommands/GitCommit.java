package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.core.command.CommandParam;
import com.wpclimate.core.command.ParamType;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

/**
 * Implementation of Git commit command to record changes to the repository.
 */
@GitCommand("git-commit")
public class GitCommit extends BaseGitCommand {
    
    @CommandParam(name="message", type=ParamType.STRING, required=true, description="Messaggio di commit")
    private String message;
    
    @CommandParam(name="amend", type=ParamType.BOOLEAN, required=false, defaultValue="false", description="Modifica l'ultimo commit invece di crearne uno nuovo")
    private boolean amend;
    
    /**
     * Constructs a new GitCommit command with specified parameters.
     *
     * @param context The Git context containing environment information
     * @param params Parameters for the commit operation, with support for:
     *               - "message": Commit message
     *               - "amend": Boolean indicating whether to amend the previous commit
     */
    public GitCommit(GitContext context, Map<String, Object> params) {
        super(context);
        
        if (params != null) {
            if (params.containsKey("message")) {
                this.message = params.get("message").toString();
            } else {
                throw new IllegalArgumentException("Commit message is required");
            }
            
            if (params.containsKey("amend")) {
                Object amendObj = params.get("amend");
                if (amendObj instanceof Boolean) {
                    this.amend = (Boolean) amendObj;
                } else if (amendObj instanceof String) {
                    this.amend = Boolean.parseBoolean((String) amendObj);
                }
            }
        } else {
            throw new IllegalArgumentException("Parameters are required for commit");
        }
        
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Commit message cannot be empty");
        }
    }
    
    /**
     * Executes the git commit command.
     * 
     * @return A CommandOutput object containing the result of the operation.
     * @throws Exception if the commit operation fails
     */
    @Override
    public CommandOutput execute() throws Exception {
        StringBuilder commandBuilder = new StringBuilder("git commit -m \"");
        
        // Add commit message (properly escaped)
        commandBuilder.append(message.replace("\"", "\\\"")).append("\"");
        
        // Add --amend flag if requested
        if (amend) {
            commandBuilder.append(" --amend");
        }
        
        // Execute the command
        return context.getShell().executeCommand(commandBuilder.toString());
    }
}