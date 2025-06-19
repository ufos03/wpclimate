package com.wpclimate.gui;

import com.wpclimate.core.AppContext;
import com.wpclimate.mateflow.MateFlow;
import com.wpclimate.shell.RealTimeConsoleSpoofer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application frame for WP Climate.
 * This class coordinates all GUI components and manages the application context.
 */
public class WpClimateMainFrame extends JFrame implements AppContextProvider {
    private final JTabbedPane tabbedPane;
    private final StatusPanel statusPanel;
    private final GitCommitPanel commitPanel;
    private final MateFlowPanel mateFlowPanel;
    private final JTextArea consoleArea;
    private JSplitPane mainSplitPane;
    private JPanel consolePanel;
    private JToggleButton toggleConsoleButton;
    
    private AppContext appContext;
    private GuiConsoleSpoofer consoleSpoofer;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean isConsoleVisible = false;
    private int lastDividerLocation = 500; // Valore di default
    
    public WpClimateMainFrame() {
        setTitle("WP Climate");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        tabbedPane = new JTabbedPane();
        statusPanel = new StatusPanel(this);
        commitPanel = new GitCommitPanel(this);
        mateFlowPanel = new MateFlowPanel(this::executeMateFlow);
        
        // Create console
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(30, 30, 30));
        consoleArea.setForeground(Color.LIGHT_GRAY);
        consoleArea.setCaretColor(Color.WHITE);
        consoleArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // Create console spoofer for output redirection
        consoleSpoofer = new GuiConsoleSpoofer();
        
        // Setup UI components
        setupConsolePanel();
        setupMainSplitPane();
        setupMenu();
        setupToolbar();
    }
    
    private void setupConsolePanel() {
        consolePanel = new JPanel(new BorderLayout());
        consolePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Console"));
        
        // Console toolbar with buttons
        JToolBar consoleToolbar = new JToolBar();
        consoleToolbar.setFloatable(false);
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearConsole());
        
        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> copyConsoleText());
        
        consoleToolbar.add(clearButton);
        consoleToolbar.add(copyButton);
        
        consolePanel.add(consoleToolbar, BorderLayout.NORTH);
        consolePanel.add(new JScrollPane(consoleArea), BorderLayout.CENTER);
    }
    
    private void setupMainSplitPane() {
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setContinuousLayout(true);
        mainSplitPane.setResizeWeight(0.8);
        
        // Add tabs for the top component
        tabbedPane.addTab("Status", new JScrollPane(statusPanel));
        tabbedPane.addTab("Commit", commitPanel);
        tabbedPane.addTab("MateFlow", mateFlowPanel);
        
        mainSplitPane.setTopComponent(tabbedPane);
        mainSplitPane.setBottomComponent(consolePanel);
        
        // Inizialmente nasconde la console
        mainSplitPane.setDividerLocation(1.0);
        mainSplitPane.setDividerSize(0);
        
        getContentPane().add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem openItem = new JMenuItem("Open WordPress Folder");
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.addActionListener(e -> openWordpressFolder());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Git menu
        JMenu gitMenu = new JMenu("Git");
        gitMenu.setMnemonic(KeyEvent.VK_G);
        
        JMenuItem statusItem = new JMenuItem("Status");
        statusItem.addActionListener(e -> executeGitCommand("git-status"));
        
        JMenuItem pullItem = new JMenuItem("Pull");
        pullItem.addActionListener(e -> executeGitCommand("git-pull"));
        
        JMenuItem pushItem = new JMenuItem("Push");
        pushItem.addActionListener(e -> executeGitCommand("git-push"));
        
        gitMenu.add(statusItem);
        gitMenu.add(pullItem);
        gitMenu.add(pushItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JMenuItem toggleConsoleItem = new JMenuItem("Toggle Console");
        toggleConsoleItem.setMnemonic(KeyEvent.VK_C);
        toggleConsoleItem.addActionListener(e -> toggleConsoleVisibility());
        
        viewMenu.add(toggleConsoleItem);
        
        // Add menus to menubar
        menuBar.add(fileMenu);
        menuBar.add(gitMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // Toggle console button
        toggleConsoleButton = new JToggleButton("Show Console");
        toggleConsoleButton.setIcon(UIManager.getIcon("FileView.computerIcon"));
        toggleConsoleButton.addActionListener(e -> toggleConsoleVisibility());
        
        toolbar.add(toggleConsoleButton);
        
        getContentPane().add(toolbar, BorderLayout.NORTH);
    }
    
    /**
     * Mostra o nasconde la console
     */
    private void toggleConsoleVisibility() {
        isConsoleVisible = !isConsoleVisible;
        
        if (isConsoleVisible) {
            // Mostra la console
            mainSplitPane.setDividerSize(5);
            mainSplitPane.setDividerLocation(lastDividerLocation);
            toggleConsoleButton.setText("Hide Console");
        } else {
            // Memorizza la posizione attuale del divisore
            lastDividerLocation = mainSplitPane.getDividerLocation();
            // Nasconde la console
            mainSplitPane.setDividerSize(0);
            mainSplitPane.setDividerLocation(1.0);
            toggleConsoleButton.setText("Show Console");
        }
    }
    
    /**
     * Pulisce il contenuto della console
     */
    private void clearConsole() {
        consoleArea.setText("");
    }
    
    /**
     * Copia il contenuto della console negli appunti
     */
    private void copyConsoleText() {
        consoleArea.selectAll();
        consoleArea.copy();
        consoleArea.select(consoleArea.getText().length(), consoleArea.getText().length());
    }
    
    public void openWordpressFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select WordPress Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            initializeAppContext(selectedFolder.getAbsolutePath());
        }
    }
    
    private void initializeAppContext(String workingDirectory) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                consoleSpoofer.displayMessage("Initializing application for: " + workingDirectory, false);
                
                // Create AppContext with the console spoofer
                appContext = new AppContext(workingDirectory, consoleSpoofer);
                
                consoleSpoofer.displayMessage("Application initialized successfully", false);
                return null;
            }
            
            @Override
            protected void done() {
                refreshUI();
            }
        };
        
        worker.execute();
    }
    
    private void refreshUI() {
        if (appContext != null) {
            // Update UI components
            statusPanel.refresh();
            commitPanel.refresh();
            mateFlowPanel.refreshFlows(appContext.getMateFlowManager());
        }
    }
    
    /**
     * Executes a Git command with default parameters (empty map)
     * 
     * @param command the Git command to execute
     */
    private void executeGitCommand(String command) {
        executeGitCommand(command, new HashMap<>());
    }

    /**
     * Executes a Git command with the specified parameters
     * 
     * @param command the Git command to execute
     * @param params the parameters for the command
     */
    private void executeGitCommand(String command, Map<String, Object> params) {
        if (appContext == null) {
            JOptionPane.showMessageDialog(this, 
                "Open a WordPress folder first.", 
                "No folder open", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Mostra la console se nascosta quando si sta per eseguire un comando
        if (!isConsoleVisible) {
            toggleConsoleVisibility();
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return appContext.getGit().execute(command, params).isSuccessful();
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        statusPanel.refresh();
                    }
                } catch (Exception e) {
                    consoleSpoofer.displayMessage("Error executing command: " + e.getMessage(), true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void executeMateFlow(MateFlow flow) {
        if (appContext == null) {
            JOptionPane.showMessageDialog(this, 
                "Open a WordPress folder first.", 
                "No folder open", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Mostra la console se nascosta quando si sta per eseguire un flow
        if (!isConsoleVisible) {
            toggleConsoleVisibility();
        }
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                consoleSpoofer.displayMessage("Executing MateFlow: " + flow.getFlowName(), false);
                
                try {
                    // Execute MateFlow
                    appContext.getMateFlowExecutor().execute(flow);
                    consoleSpoofer.displayMessage("MateFlow execution completed", false);
                } catch (Exception e) {
                    consoleSpoofer.displayMessage("Error executing MateFlow: " + e.getMessage(), true);
                }
                return null;
            }
            
            @Override
            protected void done() {
                refreshUI();
            }
        };
        
        worker.execute();
    }
    
    @Override
    public AppContext getAppContext() {
        return appContext;
    }
    
    /**
     * Implementation of RealTimeConsoleSpoofer for GUI operations
     */
    private class GuiConsoleSpoofer implements RealTimeConsoleSpoofer {
        @Override
        public void displayMessage(String output, boolean isError) {
            if (output != null && !output.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    String timestamp = timeFormat.format(new Date());
                    String prefix = isError ? "[ERROR] " : "[INFO] ";
                    
                    // Add timestamp and prefix
                    consoleArea.append("[" + timestamp + "] " + prefix + output + "\n");
                    
                    // Scroll to bottom
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                    
                    // Se il messaggio è un errore e la console è nascosta, 
                    // mostriamo automaticamente la console
                    if (isError && !isConsoleVisible) {
                        toggleConsoleVisibility();
                    }
                });
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new WpClimateMainFrame().setVisible(true);
        });
    }
}