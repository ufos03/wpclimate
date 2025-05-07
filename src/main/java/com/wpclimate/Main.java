package com.wpclimate;
import java.util.Map;

import com.wpclimate.core.AppContext;
import com.wpclimate.core.ConsoleRCS;
import com.wpclimate.git.Git;
import com.wpclimate.shell.Command;
import com.wpclimate.shell.CommandBuilder;
import com.wpclimate.shell.CommandExecutor;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.RealTimeConsoleSpoofer;
import com.wpclimate.shell.Shell;

import com.wpclimate.cli.WpCli;


public class Main {
    public static void main(String[] args) throws Exception
    {
        AppContext app = new AppContext("/home/ufos/Documents/test-wpclimate/");
        WpCli wp = app.getWpCli();
        Git git = app.getGit();

        git.execute("git-clone", Map.of("remote", "https://github.com/ufos03/ArduinoAlarmSystem.git"));

        wp.execute("rewrite", null);
        wp.execute("flush-transient", null);
        wp.execute("flush-caches", null);
        wp.execute("check-db", null);
        wp.execute("repair-db", null);

        wp.execute("search-replace", Map.of(
            "oldValue", "http://test.local",
            "newValue", "http://test2.local",
            "allTables", true,
            "dryRun", true
        ));

        wp.execute("export-db", Map.of(
            "fileName", "test.sql"
        ));

        wp.execute("import-db", Map.of(
            "fileName", "test.sql"
        ));
    }
}