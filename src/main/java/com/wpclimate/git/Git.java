package com.wpclimate.git;

import java.util.Map;

import com.wpclimate.configurator.Configurator;
import com.wpclimate.constants.FileManager;
import com.wpclimate.constants.FileName;
import com.wpclimate.git.core.Dependency;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.https.HttpsCredentials;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.Shell;

public class Git 
{
    public Git() throws Exception
    {
        FileManager flm = new FileManager();
        Shell shl = new Command(flm.getWorkingDirectory().getAbsolutePath());
        Dependency gtd = new Dependency(shl);
        Configurator cfg = new com.wpclimate.configurator.MultiFileConfiguration();
        GitContext ctx = new GitContext(shl, flm, gtd, cfg);
        HttpsCredentials dd = new HttpsCredentials(ctx);
        dd.configure(
            Map.of
            (
                "name", "test",
                "url", "dd",
                "username", "ufos",
                "password", "dd"
            ));
        System.out.println(dd.read().toString());
    }
}
