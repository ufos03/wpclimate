package com.wpclimate.Cli.Core;

import com.wpclimate.Cli.exceptions.PHPNotInstalledException;
import com.wpclimate.Cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.Shell.CommandOutput;
import com.wpclimate.Shell.Shell;

/**
 * The {@code Dependency} class checks the availability of PHP, WP-CLI, and WordPress.
 * It uses the {@link Shell} to execute commands and the {@link WpCliModel} for configuration.
 */
public final class Dependency 
{
    private static final String PHP_VERSION_COMMAND = "--version";
    private static final String WPCLI_VERSION_COMMAND = "--version";
    private static final String WP_CORE_VERSION_COMMAND = "core version";

    private final Shell shell;
    private final WpCliModel model;
    private CommandOutput commandOutput;

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
        this.validatePhpPath();

        this.commandOutput = this.executeCommand(this.model.getPhp(), PHP_VERSION_COMMAND);
        
        if (this.commandOutput.hasErrors())
            throw new PHPNotInstalledException("PHP is not installed on your system.");

        return true;
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
        if (isPHPInstalled()) 
        {
            this.validateWpCliPath();
            this.commandOutput = this.executeCommand(this.model.getPhp(), this.model.getWp(), WPCLI_VERSION_COMMAND);

            if (this.commandOutput.hasErrors())
                throw new WPCliNotInstalledException("WP-CLI is not installed on your system.");

            return true;
        }
        return false;
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
        if (isWpCliInstalled()) 
        {
            this.commandOutput = this.executeCommand(this.model.getPhp(), this.model.getWp(), WP_CORE_VERSION_COMMAND);
            return this.commandOutput.isSuccessful();
        }
        return false;
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
        return this.shell.executeCommand(command);
    }

    /**
     * Validates that the PHP path is configured in the model.
     *
     * @throws PHPNotInstalledException If the PHP path is not configured.
     */
    private void validatePhpPath() throws PHPNotInstalledException 
    {
        if (this.model.getPhp() == null || this.model.getPhp().isEmpty())
            throw new PHPNotInstalledException("PHP path is not configured in the model.");
    }

    /**
     * Validates that the WP-CLI path is configured in the model.
     *
     * @throws WPCliNotInstalledException If the WP-CLI path is not configured.
     */
    private void validateWpCliPath() throws WPCliNotInstalledException 
    {
        if (this.model.getWp() == null || this.model.getWp().isEmpty())
            throw new WPCliNotInstalledException("WP-CLI path is not configured in the model.");
    }
}