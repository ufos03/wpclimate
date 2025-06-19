package com.wpclimate.gui;

import com.wpclimate.shell.CommandOutput;

import javax.swing.*;
import java.awt.*;

/**
 * Panel displaying Git status information.
 */
public class StatusPanel extends JPanel {
    private AppContextProvider contextProvider;
    private final JTextPane statusPane;
    
    public StatusPanel(AppContextProvider contextProvider) {
        this.contextProvider = contextProvider;
        setLayout(new BorderLayout());
        
        statusPane = new JTextPane();
        statusPane.setContentType("text/html");
        statusPane.setEditable(false);
        
        add(new JScrollPane(statusPane), BorderLayout.CENTER);
    }
    
    // Metodo temporaneo per compatibilità con GitOperationsGUI
    public void update(String statusText) {
        updateStatusDisplay(statusText);
    }
    
    public void refresh() {
        if (contextProvider == null || contextProvider.getAppContext() == null) {
            statusPane.setText("<html><body>No WordPress folder open</body></html>");
            return;
        }
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                CommandOutput output = contextProvider.getAppContext().getGit().execute("git-status", null);
                return output.getStandardOutput();
            }
            
            @Override
            protected void done() {
                try {
                    String status = get();
                    updateStatusDisplay(status);
                } catch (Exception e) {
                    statusPane.setText("<html><body>Error getting status: " + e.getMessage() + "</body></html>");
                }
            }
        };
        
        worker.execute();
    }
    
    private void updateStatusDisplay(String statusText) {
        // Format git status output as HTML with colors
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: monospace; font-size: 12px;'>");
        
        for (String line : statusText.split("\n")) {
            if (line.contains("modified:")) {
                html.append("<span style='color: orange;'>").append(line).append("</span><br/>");
            } else if (line.contains("new file:")) {
                html.append("<span style='color: green;'>").append(line).append("</span><br/>");
            } else if (line.contains("deleted:")) {
                html.append("<span style='color: red;'>").append(line).append("</span><br/>");
            } else if (line.contains("branch")) {
                html.append("<span style='color: blue; font-weight: bold;'>").append(line).append("</span><br/>");
            } else {
                html.append(line).append("<br/>");
            }
        }
        
        html.append("</body></html>");
        statusPane.setText(html.toString());
    }
}