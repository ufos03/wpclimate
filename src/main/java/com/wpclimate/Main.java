package com.wpclimate;
import com.wpclimate.Cli.WpCli;
import com.wpclimate.Cli.Exceptions.PHPNotInstalledException;
import com.wpclimate.Cli.Exceptions.WPCliNotInstalledException;

public class Main {
    public static void main(String[] args) throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        WpCli wp = new WpCli("/home/ufos/Documents/test-wpclimate/");
        wp.doSearchReplace("http://test.local", "http://test2.local", true, true);
        wp.doFlushTransient();
    }
}