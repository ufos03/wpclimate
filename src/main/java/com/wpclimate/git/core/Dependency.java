package com.wpclimate.git.core;

import com.wpclimate.git.exceptions.GitNotInstalled;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

//TODO: Implementare autoload di git se non installato

/**
 * The {@code Dependency} class is responsible for managing and verifying the dependencies
 * required by the application, specifically the presence of Git on the operating system.
 * 
 * <p>
 * This class provides methods to check if Git is installed and accessible from the command line.
 * It uses a {@link Shell} instance to execute shell commands and verifies the results to determine 
 * if the required dependency is available.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Verifies if Git is installed on the operating system.</li>
 *   <li>Throws exceptions when required dependencies are not found.</li>
 *   <li>Provides a clean interface for dependency checking.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Shell shell = new Command("/path/to/working/directory");
 * Dependency dependency = new Dependency(shell);
 * 
 * try {
 *     dependency.isGitInstalled();
 *     System.out.println("Git is installed.");
 * } catch (GitNotInstalled e) {
 *     System.out.println("Git is not installed: " + e.getMessage());
 * }
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is thread-safe as it does not maintain any mutable state that could be affected by 
 * concurrent access. The thread safety of the underlying {@link Shell} instance depends on its
 * implementation.
 * </p>
 * 
 * <h2>Future Enhancements:</h2>
 * <p>
 * Future versions may implement automatic installation of Git if not found on the system.
 * </p>
 * 
 * @see Shell
 * @see CommandOutput
 * @see GitNotInstalled
 */
public class Dependency 
{

    private static final String GIT_VERSION_COMMAND = "--version";

    private final Shell shell;
    
    /**
     * Constructs a {@code Dependency} instance with the specified {@link Shell}.
     * 
     * <p>
     * The provided {@link Shell} instance will be used to execute shell commands
     * necessary for dependency verification.
     * </p>
     * 
     * @param shell The {@link Shell} instance used to execute shell commands.
     * @throws IllegalArgumentException If the {@code shell} parameter is {@code null}.
     */
    public Dependency(Shell shell) 
    {
        if (shell == null)
            throw new IllegalArgumentException("Shell must not be null.");

        this.shell = shell;
    }

    /**
     * Checks if Git is installed on the operating system.
     * 
     * <p>
     * This method executes the command {@code git --version} using the provided {@link Shell}
     * instance. If the command executes successfully without errors, Git is considered installed
     * and the method returns {@code true}.
     * </p>
     * 
     * <p>
     * If Git is not installed or not accessible, a {@link GitNotInstalled} exception is thrown
     * with an appropriate error message.
     * </p>
     * 
     * @return {@code true} if Git is installed and accessible.
     * @throws GitNotInstalled If Git is not installed or not accessible from the command line.
     */
    public boolean isGitInstalled() throws GitNotInstalled
    {
        String command = String.format("git %s", GIT_VERSION_COMMAND);
        CommandOutput output = this.shell.executeCommand(command);
        if (output.isSuccessful())
            return true;
        throw new GitNotInstalled("Git command not installed!");
    }
}