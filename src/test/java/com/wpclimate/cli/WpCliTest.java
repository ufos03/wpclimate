package com.wpclimate.cli;

import com.wpclimate.Cli.WpCli;
import com.wpclimate.Cli.Core.Context;
import com.wpclimate.Cli.Core.Dependency;
import com.wpclimate.Cli.Exceptions.PHPNotInstalledException;
import com.wpclimate.Cli.Exceptions.WPCliNotInstalledException;
import com.wpclimate.Configurator.Configurator;
import com.wpclimate.Constants.FileManager;
import com.wpclimate.Shell.CommandOutput;
import com.wpclimate.Shell.Shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WpCliTest {

    private WpCli wpCli;
    private Context mockContext;
    private Dependency mockDependency;
    private FileManager mockFileManager;
    private Configurator mockConfigurator;
    private Shell mockShell;

    @BeforeEach
    void setUp() throws PHPNotInstalledException, WPCliNotInstalledException {
        // Mock dependencies
        mockContext = mock(Context.class);
        mockDependency = mock(Dependency.class);
        mockFileManager = mock(FileManager.class);
        mockConfigurator = mock(Configurator.class);
        mockShell = mock(Shell.class);

        // Mock behavior for dependency checks
        when(mockDependency.isPHPInstalled()).thenReturn(true);
        when(mockDependency.isWpCliInstalled()).thenReturn(true);

        // Mock context behavior
        when(mockContext.getFileManager()).thenReturn(mockFileManager);
        when(mockContext.getShell()).thenReturn(mockShell);

        // Initialize WpCli with mocked dependencies
        wpCli = new WpCli(mockContext, mockDependency);
    }

    @Test
    void testDoSearchReplace_Success() throws PHPNotInstalledException, WPCliNotInstalledException {
        // Mock CommandOutput for successful execution
        CommandOutput mockOutput = mock(CommandOutput.class);
        when(mockOutput.isSuccessful()).thenReturn(true);
        when(mockShell.executeCommand(anyString())).thenReturn(mockOutput);

        // Execute the method
        boolean result = wpCli.doSearchReplace("oldValue", "newValue", true, false);

        // Verify behavior and assert result
        assertTrue(result);
        verify(mockShell, times(1)).executeCommand(contains("search-replace"));
    }

    @Test
    void testDoSearchReplace_Failure() throws PHPNotInstalledException, WPCliNotInstalledException {
        // Mock CommandOutput for failed execution
        CommandOutput mockOutput = mock(CommandOutput.class);
        when(mockOutput.isSuccessful()).thenReturn(false);
        when(mockShell.executeCommand(anyString())).thenReturn(mockOutput);

        // Execute the method
        boolean result = wpCli.doSearchReplace("oldValue", "newValue", true, false);

        // Verify behavior and assert result
        assertFalse(result);
        verify(mockShell, times(1)).executeCommand(contains("search-replace"));
    }

    @Test
    void testDoFlushTransient_Success() throws PHPNotInstalledException, WPCliNotInstalledException {
        // Mock CommandOutput for successful execution
        CommandOutput mockOutput = mock(CommandOutput.class);
        when(mockOutput.isSuccessful()).thenReturn(true);
        when(mockShell.executeCommand(anyString())).thenReturn(mockOutput);

        // Execute the method
        boolean result = wpCli.doFlushTransient();

        // Verify behavior and assert result
        assertTrue(result);
        verify(mockShell, times(1)).executeCommand(contains("flush-transients"));
    }

    @Test
    void testDoFlushTransient_Failure() throws PHPNotInstalledException, WPCliNotInstalledException {
        // Mock CommandOutput for failed execution
        CommandOutput mockOutput = mock(CommandOutput.class);
        when(mockOutput.isSuccessful()).thenReturn(false);
        when(mockShell.executeCommand(anyString())).thenReturn(mockOutput);

        // Execute the method
        boolean result = wpCli.doFlushTransient();

        // Verify behavior and assert result
        assertFalse(result);
        verify(mockShell, times(1)).executeCommand(contains("flush-transients"));
    }

    @Test
    void testDependencyChecks() throws PHPNotInstalledException, WPCliNotInstalledException {
        // Verify dependency checks
        assertTrue(mockDependency.isPHPInstalled());
        assertTrue(mockDependency.isWpCliInstalled());
        verify(mockDependency, times(1)).isPHPInstalled();
        verify(mockDependency, times(1)).isWpCliInstalled();
    }
}