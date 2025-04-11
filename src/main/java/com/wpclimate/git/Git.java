package com.wpclimate.git;

import com.wpclimate.shell.Command;
import com.wpclimate.shell.Shell;

public class Git 
{
    public Git()
    {
        Shell shell = new Command();
        System.out.println(shell.executeCommand("git --version").getStandardOutput());
    }
}
