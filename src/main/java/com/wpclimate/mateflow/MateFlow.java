package com.wpclimate.mateflow;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class MateFlow
{
    private final String flowName;
    private final String description;
    private final List<MateFlowStep> commands;

    public MateFlow(String flowname, String description)
    {
        this.flowName = flowname;
        this.description = description;
        this.commands = new ArrayList<>();
    }

    public boolean addCommand(MateFlowStep command)
    {
        return this.commands.add(command);
    }

    public void addCommands(List<MateFlowStep> commandsP)
    {
        this.commands.addAll(commandsP);
    }

    public void moveCommandUp(int index) 
    {
        if (index > 0 && index < this.commands.size()) 
        {
            MateFlowStep command = this.commands.remove(index);
            this.commands.add(index - 1, command);
        }
    }
    
    public void moveCommandDown(int index) 
    {
        if (index >= 0 && index < this.commands.size() - 1) 
        {
            MateFlowStep command = this.commands.remove(index);
            this.commands.add(index + 1, command);
        }
    }
    
    public void removeCommand(int index) 
    {
        if (index >= 0 && index < this.commands.size())
            this.commands.remove(index);
    }

    public List<MateFlowStep> getCommands()
    {
        return this.commands;
    }
    
    public String getFlowName() 
    {
        return flowName;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String toJson() 
    {
        return new Gson().toJson(this);
    }

    public static MateFlow fromJson(String json) 
    {
        return new Gson().fromJson(json, MateFlow.class);
    }
}
