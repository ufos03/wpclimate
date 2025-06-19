package com.wpclimate.git.gitcommands;

import java.util.Map;

import com.wpclimate.core.command.CommandParam;
import com.wpclimate.core.command.ParamType;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.gitcommands.registrar.GitCommand;
import com.wpclimate.shell.CommandOutput;

/**
 * Implementation of Git diff command to show changes between commits, commit and working tree, etc.
 */
@GitCommand("git-diff")
public class GitDiff extends BaseGitCommand {
    
    @CommandParam(name="file", type=ParamType.STRING, required=false, description="File di cui mostrare le differenze")
    private String file;
    
    @CommandParam(name="staged", type=ParamType.BOOLEAN, required=false, defaultValue="false", description="Se true, mostra le differenze per i file nell'area di staging")
    private boolean staged;
    
    @CommandParam(name="commit1", type=ParamType.STRING, required=false, description="Prima commit da confrontare")
    private String commit1;
    
    @CommandParam(name="commit2", type=ParamType.STRING, required=false, description="Seconda commit da confrontare")
    private String commit2;
    
    /**
     * Constructs a new GitDiff command with specified parameters.
     *
     * @param context The Git context containing environment information
     * @param params Parameters for the diff operation, with support for:
     *               - "file": Specific file to show diff for
     *               - "staged": Boolean indicating whether to show staged changes
     *               - "commit1": First commit reference for comparison
     *               - "commit2": Second commit reference for comparison
     */
    public GitDiff(GitContext context, Map<String, Object> params) {
        super(context);
        
        if (params != null) {
            if (params.containsKey("file")) {
                this.file = params.get("file").toString();
            }
            
            if (params.containsKey("staged")) {
                Object stagedObj = params.get("staged");
                if (stagedObj instanceof Boolean) {
                    this.staged = (Boolean) stagedObj;
                } else if (stagedObj instanceof String) {
                    this.staged = Boolean.parseBoolean((String) stagedObj);
                }
            }
            
            if (params.containsKey("commit1")) {
                this.commit1 = params.get("commit1").toString();
            }
            
            if (params.containsKey("commit2")) {
                this.commit2 = params.get("commit2").toString();
            }
        }
    }
    
    /**
     * Executes the git diff command.
     * 
     * @return A CommandOutput object containing the result of the operation.
     * @throws Exception if the diff operation fails
     */
    @Override
    public CommandOutput execute() throws Exception {
        StringBuilder commandBuilder = new StringBuilder("git diff");
        
        // Handle staged diff (--staged or --cached)
        if (staged) {
            commandBuilder.append(" --staged");
        }
        
        // Handle commit comparison
        if (commit1 != null && commit2 != null) {
            commandBuilder.append(" ").append(commit1).append(" ").append(commit2);
        } else if (commit1 != null) {
            commandBuilder.append(" ").append(commit1);
        }
        
        // Add file if specified
        if (file != null && !file.isEmpty()) {
            commandBuilder.append(" \"").append(file.replace("\"", "\\\"")).append("\"");
        }
        
        // Execute the command
        return context.getShell().executeCommand(commandBuilder.toString());
    }
}