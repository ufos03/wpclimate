package com.wpclimate;
import java.util.Map;

import com.wpclimate.cli.WpCli;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.git.Git;

public class Main {
    public static void main(String[] args) throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        /*WpCli wp = new WpCli("/home/ufos/Documents/test-wpclimate/");


        wp.setShowOutput(true);
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
            "fileName", "../test.sql"
        ));

        wp.execute("import-db", Map.of(
            "fileName", "../test.sql"
        ));*/

        Git g = new Git();
    }
}