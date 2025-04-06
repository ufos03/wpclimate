/*package com.wpclimate.cli;

import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.constants.FileManager;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WpCliCommandExecutorTest {

    private WpCliCommandExecutor executor;
    private Context mockContext;
    private Dependency mockDependency;
    private Shell mockShell;
    private WpCliModel mockWpCliModel;
    private FileManager mockFileManager;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        mockContext = mock(Context.class);
        mockDependency = mock(Dependency.class);
        mockShell = mock(Shell.class);
        mockWpCliModel = mock(WpCliModel.class);
        mockFileManager = mock(FileManager.class);

        // Configure the mock context to return the mock shell, WpCliModel, and FileManager
        when(mockContext.getShell()).thenReturn(mockShell);
        when(mockContext.getWpModel()).thenReturn(mockWpCliModel);
        when(mockContext.getFileManager()).thenReturn(mockFileManager);

        // Configure the mock WpCliModel to return valid paths
        when(mockWpCliModel.getPhp()).thenReturn("/usr/bin/php");
        when(mockWpCliModel.getWp()).thenReturn("/usr/local/bin/wp");

        // Configure the mock FileManager to return a valid working directory
        when(mockFileManager.getWorkingDirectory()).thenReturn(new java.io.File("/home/ufos/Documents/test-wpclimate"));

        // Initialize the executor
        executor = new WpCliCommandExecutor(mockContext, mockDependency);
    }

    @Test
    void testDoSearchReplace() throws Exception {
        // Mock CommandOutput
        CommandOutput mockOutput = mock(CommandOutput.class);
        when(mockOutput.isSuccessful()).thenReturn(true);

        // Configure the mock shell to return the mock output
        when(mockShell.executeCommand(anyString())).thenReturn(mockOutput);

        // Execute the method
        boolean result = executor.doSearchReplace("oldValue", "newValue", true, false).isSuccessful();

        // Verify behavior
        assertTrue(result);
        verify(mockShell, times(1)).executeCommand(contains("search-replace"));
    }
}*/