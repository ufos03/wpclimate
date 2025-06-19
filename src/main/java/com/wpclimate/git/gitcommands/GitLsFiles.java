package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.core.command.CommandParam;
import com.wpclimate.core.command.ParamType;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

/**
 * Implementation of Git ls-files command to show information about files in the index and the working tree.
 */
@GitCommand("git-ls-files")
public class GitLsFiles extends BaseGitCommand {
    
    @CommandParam(name="stage", type=ParamType.BOOLEAN, required=false, defaultValue="true", description="Mostra informazioni aggiuntive sulla staging area")
    private boolean stage;
    
    @CommandParam(name="cached", type=ParamType.BOOLEAN, required=false, defaultValue="false", description="Mostra file nella staging area")
    private boolean cached;
    
    @CommandParam(name="deleted", type=ParamType.BOOLEAN, required=false, defaultValue="false", description="Mostra anche file cancellati")
    private boolean deleted;
    
    @CommandParam(name="others", type=ParamType.BOOLEAN, required=false, defaultValue="false", description="Mostra anche file non tracciati")
    private boolean others;
    
    /**
     * Constructs a new GitLsFiles command with specified parameters.
     *
     * @param context The Git context containing environment information
     * @param params Parameters for the ls-files operation, with support for:
     *               - "stage": Show staged contents' mode bits, object name and stage number (default: true)
     *               - "cached": Show files in the index (default: false)
     *               - "deleted": Show deleted files (default: false)
     *               - "others": Show untracked files (default: false)
     */
    public GitLsFiles(GitContext context, Map<String, Object> params) {
        super(context);
        
        // Default values
        this.stage = true;
        this.cached = false;
        this.deleted = false;
        this.others = false;
        
        if (params != null) {
            if (params.containsKey("stage")) {
                Object stageObj = params.get("stage");
                if (stageObj instanceof Boolean) {
                    this.stage = (Boolean) stageObj;
                } else if (stageObj instanceof String) {
                    this.stage = Boolean.parseBoolean((String) stageObj);
                }
            }
            
            if (params.containsKey("cached")) {
                Object cachedObj = params.get("cached");
                if (cachedObj instanceof Boolean) {
                    this.cached = (Boolean) cachedObj;
                } else if (cachedObj instanceof String) {
                    this.cached = Boolean.parseBoolean((String) cachedObj);
                }
            }
            
            if (params.containsKey("deleted")) {
                Object deletedObj = params.get("deleted");
                if (deletedObj instanceof Boolean) {
                    this.deleted = (Boolean) deletedObj;
                } else if (deletedObj instanceof String) {
                    this.deleted = Boolean.parseBoolean((String) deletedObj);
                }
            }
            
            if (params.containsKey("others")) {
                Object othersObj = params.get("others");
                if (othersObj instanceof Boolean) {
                    this.others = (Boolean) othersObj;
                } else if (othersObj instanceof String) {
                    this.others = Boolean.parseBoolean((String) othersObj);
                }
            }
        }
    }
    
    /**
     * Executes the git ls-files command.
     * 
     * @return A CommandOutput object containing the result of the operation.
     * @throws Exception if the ls-files operation fails
     */
    @Override
    public CommandOutput execute() throws Exception {
        StringBuilder commandBuilder = new StringBuilder("git ls-files");
        
        // Add flags based on parameters
        if (stage) {
            commandBuilder.append(" --stage");
        }
        
        if (cached) {
            commandBuilder.append(" --cached");
        }
        
        if (deleted) {
            commandBuilder.append(" --deleted");
        }
        
        if (others) {
            commandBuilder.append(" --others");
        }
        
        // Execute the command
        return context.getShell().executeCommand(commandBuilder.toString());
    }
}