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
 * Implementation of Git add command to stage changes for commit.
 */
@GitCommand("git-add")
public class GitAdd extends BaseGitCommand {
    
    @CommandParam(name="files", type=ParamType.LIST, required=true, description="Lista di file da aggiungere alla staging area")
    private List<String> files;
    
    /**
     * Constructs a new GitAdd command with specified parameters.
     *
     * @param context The Git context containing environment information
     * @param params Parameters for the add operation, with support for:
     *               - "files": List of files to add to the staging area
     */
    public GitAdd(GitContext context, Map<String, Object> params) {
        super(context);
        
        if (params != null && params.containsKey("files")) {
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
        
        if (this.files.isEmpty()) {
            throw new IllegalArgumentException("No files specified for git add");
        }
    }
    
    /**
     * Executes the git add command.
     * 
     * @return A CommandOutput object containing the result of the operation.
     * @throws Exception if the add operation fails
     */
    @Override
    public CommandOutput execute() throws Exception {
        StringBuilder commandBuilder = new StringBuilder("git add");
        
        // Add all files to the command
        for (String file : this.files) {
            commandBuilder.append(" \"").append(file.replace("\"", "\\\"")).append("\"");
        }
        
        // Execute the command
        return context.getShell().executeCommand(commandBuilder.toString());
    }
}