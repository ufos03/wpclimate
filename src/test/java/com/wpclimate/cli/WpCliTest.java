package com.wpclimate.cli;

import com.wpclimate.cli.WpCli;
import com.wpclimate.cli.core.Context;
import com.wpclimate.cli.core.Dependency;
import com.wpclimate.cli.core.WpCliModel;
import com.wpclimate.cli.exceptions.PHPNotInstalledException;
import com.wpclimate.cli.exceptions.WPCliNotInstalledException;
import com.wpclimate.configurator.Configurator;
import com.wpclimate.constants.FileManager;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.Shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;

class WpCliTest {

    private WpCli wpCli;
    private Context mockContext;
    private Dependency mockDependency;
    private FileManager mockFileManager;
    private Configurator mockConfigurator;
    private Shell mockShell;

    @BeforeEach
    void setUp() throws PHPNotInstalledException, WPCliNotInstalledException 
    {
        // Mock dependencies
        mockContext = mock(Context.class);
        mockDependency = mock(Dependency.class);
        mockFileManager = mock(FileManager.class);
        mockConfigurator = mock(Configurator.class);
        mockShell = mock(Shell.class);

        // Simulate a valid WordPress installation path
        String wordpressPath = "/home/ufos/Documents/test-wpclimate/";
        when(mockFileManager.getWorkingDirectory()).thenReturn(new File(wordpressPath));

        // Mock behavior for dependency checks
        when(mockDependency.isPHPInstalled()).thenReturn(true);
        when(mockDependency.isWpCliInstalled()).thenReturn(true);
        when(mockDependency.isAWordpressDirectory()).thenReturn(true);

        // Mock WpCliModel
        WpCliModel mockWpCliModel = mock(WpCliModel.class);
        when(mockWpCliModel.getPhp()).thenReturn("/usr/bin/php");
        when(mockWpCliModel.getWp()).thenReturn("/usr/local/bin/wp");

        // Mock context behavior
        when(mockContext.getFileManager()).thenReturn(mockFileManager);
        when(mockContext.getShell()).thenReturn(mockShell);
        when(mockContext.getWpModel()).thenReturn(mockWpCliModel);

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
        verify(mockShell, times(1)).executeCommand(
            eq("/usr/bin/php /usr/local/bin/wp --path=/home/ufos/Documents/test-wpclimate transient delete --all")
        );
    }

    @Test
    void testDoFlushTransient_Failure() throws PHPNotInstalledException, WPCliNotInstalledException {
        // Mock CommandOutput for successful execution
        CommandOutput mockOutput = mock(CommandOutput.class);
        when(mockOutput.isSuccessful()).thenReturn(false);
        when(mockShell.executeCommand(anyString())).thenReturn(mockOutput);

        // Execute the method
        boolean result = wpCli.doFlushTransient();

        // Verify behavior and assert result
        assertFalse(result);
        verify(mockShell, times(1)).executeCommand(
            eq("/usr/bin/php /usr/local/bin/wp --path=/home/ufos/Documents/test-wpclimate transient delete --all")
        );
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