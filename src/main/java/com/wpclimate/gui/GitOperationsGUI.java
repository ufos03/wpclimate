package com.wpclimate.gui;

import com.wpclimate.core.AppContext;
import com.wpclimate.git.Git;
import com.wpclimate.mateflow.MateFlow;
import com.wpclimate.mateflow.MateFlowExecutor;
import com.wpclimate.mateflow.MateFlowManager;
import com.wpclimate.shell.CommandOutput;
import com.wpclimate.shell.RealTimeConsoleSpoofer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Main GUI application for WP Climate that provides functionality to:
 * - View Git repository status
 * - Perform Git operations (commit, push, pull)
 * - Create and execute MateFlows
 */
public class GitOperationsGUI extends JFrame implements AppContextProvider {
    private JTabbedPane tabbedPane;
    private StatusPanel statusPanel;
    private GitCommitPanel gitCommitPanel;
    private MateFlowPanel mateFlowPanel;
    private JTextArea consoleArea;
    private JSplitPane mainSplitPane;
    private JPanel consolePanel;
    private JToggleButton toggleConsoleButton;
    
    private AppContext appContext;
    private File currentRepo;
    private GuiConsoleSpoofer consoleSpoofer;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean isConsoleVisible = false;
    private int lastDividerLocation = 500;

    public GitOperationsGUI() {
        setTitle("WP Climate Git Operations");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        tabbedPane = new JTabbedPane();
        statusPanel = new StatusPanel(this);
        gitCommitPanel = new GitCommitPanel(this);
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
        
        // Setup UI
        setupConsolePanel();
        setupMainSplitPane();
        setupMenuBar();
        setupToolbar();
    }
    
    private void setupConsolePanel() {
        consolePanel = new JPanel(new BorderLayout());
        consolePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Console",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Dialog", Font.BOLD, 12)
        ));
        
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
        
        // Add tabs
        tabbedPane.addTab("Git Status", new JScrollPane(statusPanel));
        tabbedPane.addTab("Commit", gitCommitPanel);
        tabbedPane.addTab("MateFlow", mateFlowPanel);
        
        mainSplitPane.setTopComponent(tabbedPane);
        mainSplitPane.setBottomComponent(consolePanel);
        
        // Inizialmente nasconde la console
        mainSplitPane.setDividerLocation(1.0);
        mainSplitPane.setDividerSize(0);
        
        getContentPane().add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem openItem = new JMenuItem("Open WordPress Folder");
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.addActionListener(this::openWordPressFolder);
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Git menu
        JMenu gitMenu = new JMenu("Git");
        gitMenu.setMnemonic(KeyEvent.VK_G);
        
        JMenuItem pullItem = new JMenuItem("Pull");
        pullItem.addActionListener(e -> executeGitCommand("git-pull", new HashMap<>()));
        
        JMenuItem pushItem = new JMenuItem("Push");
        pushItem.addActionListener(e -> executeGitCommand("git-push", new HashMap<>()));
        
        gitMenu.add(pullItem);
        gitMenu.add(pushItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JMenuItem toggleConsoleItem = new JMenuItem("Toggle Console");
        toggleConsoleItem.setMnemonic(KeyEvent.VK_C);
        toggleConsoleItem.addActionListener(e -> toggleConsoleVisibility());
        
        viewMenu.add(toggleConsoleItem);
        
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
            toggleConsoleButton.setForeground(null); // Reset colore
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
        appendToConsole("Console cleared", false);
    }
    
    /**
     * Copia il contenuto della console negli appunti
     */
    private void copyConsoleText() {
        consoleArea.selectAll();
        consoleArea.copy();
        consoleArea.select(consoleArea.getText().length(), consoleArea.getText().length());
        appendToConsole("Console content copied to clipboard", false);
    }
    
    /**
     * Aggiunge un messaggio alla console
     */
    private void appendToConsole(String message, boolean isError) {
        String timestamp = timeFormat.format(new Date());
        String prefix = isError ? "[ERROR] " : "[INFO] ";
        consoleArea.append("[" + timestamp + "] " + prefix + message + "\n");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }
    
    private void openWordPressFolder(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select WordPress Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentRepo = fileChooser.getSelectedFile();
            initializeAppContext(currentRepo.getAbsolutePath());
        }
    }
    
    private void initializeAppContext(String workingDirectory) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                consoleSpoofer.displayMessage("Initializing application context for: " + workingDirectory, false);
                
                // Create AppContext for the selected directory with our console spoofer
                appContext = new AppContext(workingDirectory, consoleSpoofer);
                
                consoleSpoofer.displayMessage("Application context initialized successfully", false);
                return null;
            }
            
            @Override
            protected void done() {
                refreshGitStatus();
                refreshMateFlows();
            }
        };
        
        worker.execute();
    }
    
    private void refreshGitStatus() {
        if (appContext != null) {
            SwingWorker<CommandOutput, Void> worker = new SwingWorker<>() {
                @Override
                protected CommandOutput doInBackground() throws Exception {
                    Git git = appContext.getGit();
                    Map<String, Object> params = new HashMap<>();
                    
                    // Execute git status - output will go through the console spoofer
                    CommandOutput out = git.execute("git-status", params);
                    
                    // Update status panel using the refresh method
                    if (out.isSuccessful()) {
                        SwingUtilities.invokeLater(() -> {
                            statusPanel.update(out.getStandardOutput());
                        });
                    }
                    
                    return out;
                }
            };
            worker.execute();
        }
    }
    
    private void refreshMateFlows() {
        if (appContext != null) {
            mateFlowPanel.refreshFlows(appContext.getMateFlowManager());
        }
    }
    
    private void commitChanges(String message) {
        if (appContext == null) {
            JOptionPane.showMessageDialog(this, 
                "No WordPress folder open", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("message", message);
        
        executeGitCommand("git-commit", params);
    }
    
    private void executeGitCommand(String commandName, Map<String, Object> params) {
        if (appContext == null) {
            JOptionPane.showMessageDialog(this, 
                "No WordPress folder open", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Mostra la console se nascosta quando si sta per eseguire un comando
        if (!isConsoleVisible) {
            toggleConsoleVisibility();
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                consoleSpoofer.displayMessage("Executing Git command: " + commandName, false);
                Git git = appContext.getGit();
                
                // Execute the git command - output goes through the console spoofer
                boolean result = git.execute(commandName, params).isSuccessful();
                
                consoleSpoofer.displayMessage(result ? 
                    "Command succeeded" : "Command failed", false);
                return result;
            }
            
            @Override
            protected void done() {
                refreshGitStatus();
            }
        };
        
        worker.execute();
    }
    
    private void executeMateFlow(MateFlow flow) {
        if (appContext == null) {
            JOptionPane.showMessageDialog(this, 
                "No WordPress folder open", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
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
                    MateFlowExecutor executor = appContext.getMateFlowExecutor();
                    executor.execute(flow);
                    consoleSpoofer.displayMessage("MateFlow execution completed", false);
                } catch (Exception e) {
                    consoleSpoofer.displayMessage("Error executing MateFlow: " + e.getMessage(), true);
                }
                return null;
            }
            
            @Override
            protected void done() {
                refreshGitStatus();
            }
        };
        
        worker.execute();
    }
    
    /**
     * Implementation of RealTimeConsoleSpoofer for GUI operations
     */
    private class GuiConsoleSpoofer implements RealTimeConsoleSpoofer {
        @Override
        public void displayMessage(String output, boolean isError) {
            if (output != null && !output.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    appendToConsole(output, isError);
                    
                    // Se il messaggio è un errore e la console è nascosta, 
                    // mostriamo automaticamente la console
                    if (isError && !isConsoleVisible) {
                        toggleConsoleVisibility();
                    }
                });
            }
        }
    }
    
    @Override
    public AppContext getAppContext() {
        return appContext;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new GitOperationsGUI().setVisible(true);
        });
    }
}