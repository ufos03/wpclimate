package com.wpclimate.git.gitcommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wpclimate.core.command.CommandParam;
import com.wpclimate.core.command.ParamType;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

/**
 * Implementation of Git reset command to unstage changes.
 */
@GitCommand("git-reset")
public class GitReset extends BaseGitCommand {
    
    @CommandParam(name="files", type=ParamType.LIST, required=true, description="Lista di file da rimuovere dalla staging area")
    private List<String> files;
    
    @CommandParam(name="hard", type=ParamType.BOOLEAN, required=false, defaultValue="false", description="Se true, esegue reset --hard")
    private boolean hard;
    
    /**
     * Constructs a new GitReset command with specified parameters.
     *
     * @param context The Git context containing environment information
     * @param params Parameters for the reset operation, with support for:
     *               - "files": List of files to unstage
     *               - "hard": Boolean indicating whether to perform a hard reset
     */
    public GitReset(GitContext context, Map<String, Object> params) {
        super(context);
        
        if (params != null) {
            // Handle files parameter
            if (params.containsKey("files")) {
                Object filesObj = params.get("files");
                if (filesObj instanceof List) {
                    this.files = (List<String>) filesObj;
                } else if (filesObj instanceof String) {
                    this.files = new ArrayList<>();
                    this.files.add((String) filesObj);
                }
            } else {
                this.files = new ArrayList<>();
            }
            
            // Handle hard reset parameter
            if (params.containsKey("hard")) {
                Object hardObj = params.get("hard");
                if (hardObj instanceof Boolean) {
                    this.hard = (Boolean) hardObj;
                } else if (hardObj instanceof String) {
                    this.hard = Boolean.parseBoolean((String) hardObj);
                }
            }
        } else {
            this.files = new ArrayList<>();
        }
    }
    
    /**
     * Executes the git reset command.
     * 
     * @return A CommandOutput object containing the result of the operation.
     * @throws Exception if the reset operation fails
     */
    @Override
    public CommandOutput execute() throws Exception {
        StringBuilder commandBuilder = new StringBuilder("git reset");
        
        // Add --hard flag if requested
        if (hard) {
            commandBuilder.append(" --hard");
        } else {
            commandBuilder.append(" HEAD"); // Default to unstaging changes (keeping them in working dir)
        }
        
        // Add files to the command
        if (!this.files.isEmpty()) {
            for (String file : this.files) {
                // Escape quotes in filenames
                commandBuilder.append(" \"").append(file.replace("\"", "\\\"")).append("\"");
            }
        }
        
        // Execute the command
        return context.getShell().executeCommand(commandBuilder.toString());
    }
}