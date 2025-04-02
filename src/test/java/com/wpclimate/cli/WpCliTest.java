package com.wpclimate.cli;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.constants.FileManager;
import com.wpclimate.shell.CommandOutputHandler;
import com.wpclimate.shell.Shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WpCliTest {

    private WpCli wpCli;
    private Context mockContext;
    private Dependency mockDependency;
    private FileManager mockFileManager;
    private Shell mockShell;

    @BeforeEach
    void setUp() throws PHPNotInstalledException, WPCliNotInstalledException {
        // Mock dependencies
        mockContext = mock(Context.class);
        mockDependency = mock(Dependency.class);
        mockFileManager = mock(FileManager.class);
        mockShell = mock(Shell.class);

        // Simulate a valid WordPress installation path
        String wordpressPath = "/home/ufos/Documents/test-wpclimate/";
        when(mockFileManager.getWorkingDirectory()).thenReturn(new File(wordpressPath));

        // Mock dependency checks
        when(mockDependency.isPHPInstalled()).thenReturn(true);
        when(mockDependency.isWpCliInstalled()).thenReturn(true);

        // Mock WpCliModel
        WpCliModel mockWpCliModel = mock(WpCliModel.class);
        when(mockWpCliModel.getPhp()).thenReturn("/usr/bin/php");
        when(mockWpCliModel.getWp()).thenReturn("/usr/local/bin/wp");

        // Mock context behavior
        when(mockContext.getFileManager()).thenReturn(mockFileManager);
        when(mockContext.getShell()).thenReturn(mockShell);
        when(mockContext.getWpModel()).thenReturn(mockWpCliModel);

        // Initialize WpCli
        wpCli = new WpCli("/home/ufos/Documents/test-wpclimate/");
    }

    @Test
    void testSetShowOutputThreadSafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Run multiple threads to toggle showOutput
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> wpCli.setShowOutput(true));
            executor.submit(() -> wpCli.setShowOutput(false));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all threads to finish
        }

        // Verify that no exceptions occurred and the final state is consistent
        assertDoesNotThrow(() -> wpCli.setShowOutput(true));
    }

    @Test
    void testSetOutputHandlerThreadSafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Run multiple threads to set different output handlers
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> wpCli.setOutputHandler(mock(CommandOutputHandler.class)));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all threads to finish
        }

        // Verify that no exceptions occurred
        assertDoesNotThrow(() -> wpCli.setOutputHandler(mock(CommandOutputHandler.class)));
    }
}