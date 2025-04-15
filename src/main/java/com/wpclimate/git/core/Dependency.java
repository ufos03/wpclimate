package com.wpclimate.git.core;

import java.util.concurrent.locks.ReentrantLock;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

//TODO: Implementare autoload di git se non installato

/**
 * The {@code Dependency} class is responsible for managing and verifying the dependencies
 * required by the application, specifically the presence of Git on the operating system.
 * 
 * <p>
 * This class provides methods to check if Git is installed and accessible from the command line.
 * It uses a {@link Shell} instance to execute shell commands and a {@link ReentrantLock} to ensure
 * thread-safe operations.
 * </p>
 * 
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Verifies if Git is installed on the operating system.</li>
 *   <li>Ensures thread-safe execution of shell commands using a lock.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * Shell shell = new Command("/path/to/working/directory");
 * Dependency dependency = new Dependency(shell);
 * 
 * if (dependency.isGitInstalled()) {
 *     System.out.println("Git is installed.");
 * } else {
 *     System.out.println("Git is not installed.");
 * }
 * </pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is thread-safe. It uses a {@link ReentrantLock} to synchronize access to the
 * {@link Shell} instance when executing commands.
 * </p>
 * 
 * @see Shell
 * @see CommandOutput
 */
public class Dependency 
{

    private static final String GIT_VERSION_COMMAND = "--version";

    private final Shell shell;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructs a {@code Dependency} instance with the specified {@link Shell}.
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
     * instance. If the command executes successfully without errors, Git is considered installed.
     * </p>
     * 
     * <h2>Thread Safety:</h2>
     * <p>
     * This method is thread-safe. It uses a {@link ReentrantLock} to ensure that only one thread
     * can execute the command at a time.
     * </p>
     * 
     * @return {@code true} if Git is installed and accessible; {@code false} otherwise.
     */
    public boolean isGitInstalled() 
    {
        this.lock.lock();
        try 
        {
            String command = String.format("git %s", GIT_VERSION_COMMAND);
            CommandOutput output = this.shell.executeCommand(command);
            return output.isSuccessful();
        }
        finally 
        {
            this.lock.unlock();
        }
    }
}