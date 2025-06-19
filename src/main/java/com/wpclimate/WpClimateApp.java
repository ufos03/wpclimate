package com.wpclimate;

import com.wpclimate.gui.WpClimateMainFrame;

import javax.swing.*;

/**
 * Main application entry point for WP Climate.
 */
public class WpClimateApp {
    public static void main(String[] args) {
        try {
            // Set system look and feel for better integration with the OS
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            WpClimateMainFrame mainFrame = new WpClimateMainFrame();
            mainFrame.setVisible(true);
        });
    }
}