package com.wpclimate.git;

import java.util.Map;

import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.git.core.Dependency;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.ssh.SshCredentials;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.Shell;

public class Git 
{
    public Git(String workingDirectory) throws Exception
    {
        Settings flm = new Settings(workingDirectory);
        Shell shl = new Command(flm.getWorkingDirectory().getAbsolutePath());
        Dependency gtd = new Dependency(shl);
        Configurator cfg = new com.wpclimate.configurator.Configuration();
        GitContext ctx = new GitContext(shl, flm, gtd, cfg);
        SshCredentials dd = new SshCredentials(ctx);
        dd.configure(
            Map.of
            (
                "name", "test",
                "url", "dd",
                "pubPath", "ufos",
                "privPath", "dd"
            ));
        System.out.println(dd.read().toString());
    }
}
