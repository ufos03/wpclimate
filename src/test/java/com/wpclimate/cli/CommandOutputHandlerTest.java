/*package com.wpclimate.cli;

import com.wpclimate.shell.CommandOutput;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CommandOutputHandlerTest {

    @Test
    void testConsoleOutputHandler() {
        // Mock CommandOutput
        CommandOutput mockOutput = mock(CommandOutput.class);
        when(mockOutput.isSuccessful()).thenReturn(true);
        when(mockOutput.getStandardOutput()).thenReturn("Command executed successfully.");
        when(mockOutput.getErrorOutput()).thenReturn("");

        // Test ConsoleOutputHandler
        ConsoleOutputHandler handler = new ConsoleOutputHandler();
        handler.handleOutput(mockOutput);

        // Verify behavior (you can check console output manually or mock System.out)
        verify(mockOutput, times(1)).isSuccessful();
    }
}*/