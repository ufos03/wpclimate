package com.wpclimate.cli.core;

import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

import java.util.concurrent.locks.ReentrantLock;

/**
 * The {@code Dependency} class checks the availability of PHP, WP-CLI, and WordPress.
 * It uses the {@link Shell} to execute commands and the {@link WpCliModel} for configuration.
 */
public class Dependency 
{
    private static final String PHP_VERSION_COMMAND = "--version";
    private static final String WPCLI_VERSION_COMMAND = "--version";
    private static final String WP_CORE_VERSION_COMMAND = "core version";

    private final Shell shell;
    private final WpCliModel model;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructs a {@code Dependency} instance with the specified {@code Context}.
     *
     * @param context The {@link Context} containing the shell and WP-CLI model.
     * @throws IllegalStateException If the context is {@code null}.
     */
    public Dependency(Context context) 
    {
        if (context == null)
            throw new IllegalStateException("The context must be provided.");

        this.shell = context.getShell();
        this.model = context.getWpModel();
    }

    /**
     * Checks if PHP is installed on the system.
     *
     * @return {@code true} if PHP is installed, {@code false} otherwise.
     * @throws PHPNotInstalledException If PHP is not installed or the command fails.
     */
    public boolean isPHPInstalled() throws PHPNotInstalledException 
    {
        this.lock.lock();
        try 
        {
            this.validatePhpPath();
            CommandOutput output = executeCommand(model.getPhp(), PHP_VERSION_COMMAND);
            if (output.hasErrors())
                throw new PHPNotInstalledException("PHP is not installed.");
            return true;
        } 
        finally 
        {
            lock.unlock();
        }
    }

    /**
     * Checks if WP-CLI is installed on the system.
     *
     * @return {@code true} if WP-CLI is installed, {@code false} otherwise.
     * @throws PHPNotInstalledException If PHP is not installed.
     * @throws WPCliNotInstalledException If WP-CLI is not installed or the command fails.
     */
    public boolean isWpCliInstalled() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        lock.lock();
        try 
        {
            if (isPHPInstalled()) 
            {
                validateWpCliPath();
                CommandOutput output = executeCommand(model.getPhp(), model.getWp(), WPCLI_VERSION_COMMAND);
                if (output.hasErrors())
                    throw new WPCliNotInstalledException("WP-CLI is not installed.");
                return true;
            }
            return false;
        } 
        finally 
        {
            lock.unlock();
        }
    }

    /**
     * Checks if the current directory is a WordPress installation.
     *
     * @return {@code true} if the current directory is a WordPress installation, {@code false} otherwise.
     * @throws PHPNotInstalledException If PHP is not installed.
     * @throws WPCliNotInstalledException If WP-CLI is not installed.
     */
    public boolean isAWordpressDirectory() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        lock.lock();
        try 
        {
            if (isWpCliInstalled()) 
            {
                CommandOutput output = executeCommand(model.getPhp(), model.getWp(), WP_CORE_VERSION_COMMAND);
                return output.isSuccessful();
            }
            return false;
        } 
        finally 
        {
            lock.unlock();
        }
    }

    /**
     * Executes a command using the shell.
     *
     * @param commandParts The parts of the command to execute.
     * @return The {@link CommandOutput} containing the result of the command execution.
     */
    private CommandOutput executeCommand(String... commandParts) 
    {
        String command = String.join(" ", commandParts);
        return shell.executeCommand(command);
    }

    /**
     * Validates that the PHP path is configured in the model.
     *
     * @throws PHPNotInstalledException If the PHP path is not configured.
     */
    private void validatePhpPath() throws PHPNotInstalledException 
    {
        if (model.getPhp() == null || model.getPhp().isEmpty())
            throw new PHPNotInstalledException("PHP path is not configured.");
    }

    /**
     * Validates that the WP-CLI path is configured in the model.
     *
     * @throws WPCliNotInstalledException If the WP-CLI path is not configured.
     */
    private void validateWpCliPath() throws WPCliNotInstalledException 
    {
        if (model.getWp() == null || model.getWp().isEmpty())
            throw new WPCliNotInstalledException("WP-CLI path is not configured.");
    }
}