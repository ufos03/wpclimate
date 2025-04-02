package com.wpclimate;
import com.wpclimate.cli.WpCli;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;

public class Main {
    public static void main(String[] args) throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        WpCli wp = new WpCli("/home/ufos/Documents/test-wpclimate/");
        wp.setShowOutput(true);
        wp.doSearchReplace("http://test.local", "http://test2.local", true, true);
        wp.setShowOutput(true);
        wp.doFlushTransient();
    }
}