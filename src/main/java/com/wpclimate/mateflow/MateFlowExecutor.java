package com.wpclimate.mateflow;

import com.wpclimate.cli.WpCli;
import com.wpclimate.git.Git;
import com.wpclimate.shell.CommandOutput;

import java.util.List;
import java.util.Map;

/**
 * Executes a MateFlow by dispatching each step to the appropriate command group (WP or GIT).
 */
public class MateFlowExecutor {

    private final WpCli wpCli;
    private final Git gitCli;

    /**
     * Constructs a MateFlowExecutor with the required WP and Git executors.
     *
     * @param wpCli  The WP-CLI executor.
     * @param gitCli The Git executor.
     */
    public MateFlowExecutor(WpCli wpCli, Git gitCli) 
    {
        this.wpCli = wpCli;
        this.gitCli = gitCli;
    }

    /**
     * Executes the given MateFlow.
     *
     * @param mateFlow The MateFlow to execute.
     */
    public void execute(MateFlow mateFlow) 
    {
        List<MateFlowStep> steps = mateFlow.getCommands();
        for (MateFlowStep step : steps) 
        {
            String group = step.getGroup();
            String command = step.getCommand();
            Map<String, Object> params = step.getParametes();

            CommandOutput result;
            if ("WP".equalsIgnoreCase(group))
                result = wpCli.execute(command, params);
            else if ("GIT".equalsIgnoreCase(group)) 
                result = gitCli.execute(command, params);
            else 
            {
                System.err.println("Unknown group: " + group + " for step: " + command);
                continue;
            }

            if (result.hasErrors()) 
            {
                System.err.println("Step failed: " + command + " (group: " + group + ")");
                break;
            } 
            else
                System.out.println("Step succeeded: " + command + " (group: " + group + ")");
        }
    }
}