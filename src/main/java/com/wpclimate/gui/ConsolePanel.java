package com.wpclimate.gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsolePanel extends JPanel {
    private JTextArea consoleArea;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    public ConsolePanel() {
        setLayout(new BorderLayout());
        
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // Add console text area to a scroll pane
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        
        // Add to panel
        add(scrollPane, BorderLayout.CENTER);
        
        // Add clear button
        JButton clearButton = new JButton("Clear Console");
        clearButton.addActionListener(e -> clearConsole());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void appendToConsole(String message) {
        String timestamp = timeFormat.format(new Date());
        consoleArea.append("[" + timestamp + "] " + message + "\n");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }
    
    public void clearConsole() {
        consoleArea.setText("");
    }
}