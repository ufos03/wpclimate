package com.wpclimate.cli.wpcommands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code WpCommand} annotation is used to mark classes that implement a WP-CLI command.
 *
 * <p>
 * This annotation allows the dynamic registration of WP-CLI commands by associating a unique
 * name with each command. The {@code value} parameter specifies the name under which the command
 * will be registered in the {@link BaseWpCommand} registry.
 * </p>
 *
 * <h2>Usage:</h2>
 * <p>
 * Apply this annotation to any class that extends {@link BaseWpCommand} to define a new WP-CLI command.
 * The annotated class will be automatically discovered and registered by the {@link CommandRegistrar}.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * {@literal @}WpCommand("rewrite")
 * public class RewriteCommand extends BaseWpCommand {
 *     public RewriteCommand(Context context, Dependency dependency) {
 *         super(context, dependency);
 *     }
 *
 *     {@literal @}Override
 *     public CommandOutput execute() {
 *         // Command logic here
 *     }
 * }
 * </pre>
 *
 * <h2>Retention Policy:</h2>
 * <p>
 * This annotation is retained at runtime ({@link RetentionPolicy#RUNTIME}) to allow dynamic
 * discovery and processing using reflection.
 * </p>
 *
 * @see BaseWpCommand
 * @see CommandRegistrar
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WpCommand 
{   
     /**
     * Specifies the name under which the command will be registered.
     *
     * @return The name of the command.
     */
    String value();
}