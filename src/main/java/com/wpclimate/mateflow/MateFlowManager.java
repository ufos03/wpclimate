package com.wpclimate.mateflow;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import com.wpclimate.resourcer.ResourceManager;
import com.wpclimate.resourcer.ResourceType;

/**
 * Manages the saving and loading of mateFlows.
 */
public class MateFlowManager 
{
    private final ResourceManager manager;
    private final Map<String, Path> flowFiles = new HashMap<>();
    private final Path flowDirectory;

    /**
     * Constructs a {@code MateFlowManager} with the specified flow directory.
     *
     * @param flowDirectoryPath The path to the directory where mateFlows are stored.
     */
    public MateFlowManager(String workingDirectory) 
    {
        this.manager = ResourceManager.getInstance();
        this.manager.setWorkingDirectory(workingDirectory);
        this.flowDirectory = Paths.get(manager.getPath(ResourceType.WORKFLOW_DIRECTORY));

        this.loadLinkFlows();
    }


    private void loadLinkFlows()
    {
        try 
        {
            List<Path> flowPaths = Files.list(this.flowDirectory)
                    .filter(path -> path.toString().endsWith(".json"))
                    .collect(Collectors.toList());
            for (Path flowPath : flowPaths) 
            {
                String flowName = flowPath.getFileName().toString().replace(".json", "");
                this.flowFiles.put(flowName, flowPath);
            }

            System.out.println("Loaded " + flowFiles.size() + " flow file names into memory.");
        } 
        catch (IOException e) 
        {
            throw new RuntimeException("Failed to load flow file names from disk.", e);
        }
    }

    public void saveMateFlow(MateFlow mateFlow) throws IOException
    {
        Path flowPath = this.flowDirectory.resolve(mateFlow.getFlowName() + ".json");
        
        try (BufferedWriter writer = Files.newBufferedWriter(flowPath)) 
        {
            writer.write(mateFlow.toJson());
        }

        this.flowFiles.put(mateFlow.getFlowName(), flowPath);
    }
   

    public MateFlow getMateFlow(String flowName) throws IOException, MateFlowException
    {
        Path flowPath = flowFiles.get(flowName);
        if (flowPath == null) 
            throw new MateFlowException("There is no flow named " + flowName);

        try (BufferedReader reader = Files.newBufferedReader(flowPath)) 
        {
            String json = reader.lines().collect(Collectors.joining());
            return MateFlow.fromJson(json);
        }
    }

    public Collection<String> getAllFlowNames() 
    {
        return flowFiles.keySet();
    }

    public boolean deleteMateFlow(String flowName) throws IOException, MateFlowException
    {
        Path flowPath = flowFiles.get(flowName);
        if (flowPath == null) 
            throw new MateFlowException("There is no flow named " + flowName);

        Files.delete(flowPath);
        flowFiles.remove(flowName);
        return true;
    }
}   