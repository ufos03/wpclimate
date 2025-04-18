package com.wpclimate.git;

import java.util.Map;

import com.wpclimate.SettingsUtils.Settings;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.git.core.Dependency;
import com.wpclimate.git.core.GitContext;
import com.wpclimate.git.credentials.https.HttpsCredentials;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.Shell;

public class Git 
{
    public Git(String workingDirectory) throws Exception
    {
        Settings flm = new Settings("/home/ufos/Documents/test-wpclimate");
        Shell shl = new Command(flm.getWorkingDirectory().getAbsolutePath());
        Dependency gtd = new Dependency(shl);
        Configurator cfg = new com.wpclimate.configurator.Configuration();
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
