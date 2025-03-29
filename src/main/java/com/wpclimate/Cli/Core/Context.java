package com.wpclimate.Cli.Core;

import com.wpclimate.Configurator.Configurator;
import com.wpclimate.Shell.CommandOutput;
import com.wpclimate.Shell.Shell;

// TODO: Aggiugngere un logger (ERR, DBG, INF).
// TODO: Aggiungere un EventLogger


/**
 * The {@code Context} class provides a centralized container for managing
 * the core components required by the WP-CLI application.
 * 
 * <p>
 * This class encapsulates the following components:
 * </p>
 * <ul>
 *   <li>{@link WpCliModel} - The model containing WP-CLI configuration data.</li>
 *   <li>{@link Shell} - The shell interface for executing commands.</li>
 *   <li>{@link Configurator} - The configurator for managing configuration persistence.</li>
 * </ul>
 * 
 * <p>
 * The {@code Context} class is designed to simplify the management and access
 * of these components throughout the application.
 * </p>
 */
public class Context 
{
    private final Shell shell; // The shell interface for executing commands
    private final Configurator configurator; // The configurator for managing configuration persistence
    private final WpCliModel wpModel; // The WP-CLI configuration model

    /**
     * Constructs a {@code Context} instance with the specified components.
     *
     * @param wpModel      The {@link WpCliModel} instance containing WP-CLI configuration data.
     * @param shell        The {@link Shell} instance for executing commands.
     * @param configurator The {@link Configurator} instance for managing configuration persistence.
     * @param commandOutput The {@link CommandOutput} instance to store and analyze the output of a command.
     */
    public Context(WpCliModel wpModel, Shell shell, Configurator configurator) 
    {
        this.wpModel = wpModel;
        this.shell = shell;
        this.configurator = configurator;
    }

    /**
     * Retrieves the WP-CLI configuration model.
     *
     * @return The {@link WpCliModel} instance containing WP-CLI configuration data.
     */
    public WpCliModel getWpModel() 
    {
        return this.wpModel;
    }

    /**
     * Retrieves the shell interface for executing commands.
     *
     * @return The {@link Shell} instance for executing commands.
     */
    public Shell getShell() 
    {
        return this.shell;
    }

    /**
     * Retrieves the configurator for managing configuration persistence.
     *
     * @return The {@link Configurator} instance for managing configuration persistence.
     */
    public Configurator getConfigurator() 
    {
        return this.configurator;
    }
}