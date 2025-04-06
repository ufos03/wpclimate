/*package com.wpclimate.cli;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.constants.FileManager;
import com.wpclimate.shell.Shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContextTest {

    private Context context;
    private FileManager mockFileManager;
    private Configurator mockConfigurator;
    private Shell mockShell;
    private WpCliModel mockWpCliModel;

    @BeforeEach
    void setUp() {
        mockFileManager = mock(FileManager.class);
        mockConfigurator = mock(Configurator.class);
        mockShell = mock(Shell.class);
        mockWpCliModel = mock(WpCliModel.class);

        context = new Context(mockWpCliModel, mockShell, mockConfigurator, mockFileManager);
    }

    @Test
    void testGettersThreadSafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Run multiple threads to access getters
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> assertNotNull(context.getFileManager()));
            executor.submit(() -> assertNotNull(context.getShell()));
            executor.submit(() -> assertNotNull(context.getConfigurator()));
            executor.submit(() -> assertNotNull(context.getWpModel()));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all threads to finish
        }

        // Verify that no exceptions occurred
        assertDoesNotThrow(() -> context.getFileManager());
    }
}*/