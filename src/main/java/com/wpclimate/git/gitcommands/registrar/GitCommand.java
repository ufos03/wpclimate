package com.wpclimate.git.gitcommands.registrar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code GitCommand} annotation is used to mark classes that implement a Git command.
 *
 * <p>
 * This annotation allows the dynamic registration of Git commands by associating a unique
 * name with each command. The {@code value} parameter specifies the name under which the command
 * will be registered in the {@link BaseGitCommand} registry.
 * </p>
 *
 * <h2>Usage:</h2>
 * <p>
 * Apply this annotation to any class that extends {@link BaseGitCommand} to define a new Git command.
 * The annotated class will be automatically discovered and registered by the {@link CommandRegistrar}.
 * </p>
 *
 * <h2>Example:</h2>
 * <pre>
 * {@literal @}GitCommand("clone")
 * public class GitCloneCommand extends BaseGitCommand {
 *     public GitCloneCommand(GitContext context) {
 *         super(context);
 *     }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GitCommand 
{
    String value();
}