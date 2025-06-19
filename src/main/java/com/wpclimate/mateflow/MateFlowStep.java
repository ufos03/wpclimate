package com.wpclimate.mateflow;

import java.io.Serializable;
import java.util.Map;

public class MateFlowStep implements Serializable
{
    private final String group;
    private final String command;
    private Map<String, Object> parametes;

    public MateFlowStep(String command, Map<String, Object> parametes, String group)
    {
        this.command = command;
        this.parametes = parametes;
        this.group = group;
    }

    @Override
    public String toString()
    {
        return "[Command: " + command + "\nParameters: " + this.parametes.toString() + "\nGroup: " + this.group + "]";
    }

    public String getGroup() {
        return group;
    }

    public String getCommand() {
        return command;
    }

    public Map<String, Object> getParametes() {
        return parametes;
    }

    public void setParamteres(Map<String, Object> para)
    {
        this.parametes = para;
    }
}
