package com.wpclimate.gui;

import com.wpclimate.core.AppContext;
import com.wpclimate.git.Git;
import com.wpclimate.shell.CommandOutput;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pannello per la gestione dei commit Git.
 */
public class GitCommitPanel extends JPanel implements ActionListener {
    private final AppContextProvider contextProvider;
    
    // UI Components
    private JTable changedFilesTable;
    private DefaultTableModel fileTableModel;
    private JTextArea commitMessageArea;
    private JButton refreshButton;
    private JButton addSelectedButton;
    private JButton addAllButton;
    private JButton resetButton;
    private JButton commitButton;
    private JLabel statusLabel;
    
    // File tracking
    private List<FileStatus> changedFiles = new ArrayList<>();
    private List<String> stagedFiles = new ArrayList<>();
    
    // Constants
    private static final String COL_STATUS = "Status";
    private static final String COL_STAGED = "Staged";
    private static final String COL_FILE = "File";
    
    // Debug flag - imposta su true per visualizzare output di debug
    private static final boolean DEBUG = false;

    public GitCommitPanel(AppContextProvider contextProvider) {
        this.contextProvider = contextProvider;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        initUI();
        
        // Esegui il refresh appena il pannello viene creato
        SwingUtilities.invokeLater(this::refreshChangedFiles);
    }
    
    private void initUI() {
        // Pannello superiore per i file modificati
        JPanel changedFilesPanel = createChangedFilesPanel();
        
        // Pannello centrale per il messaggio di commit
        JPanel commitMessagePanel = createCommitMessagePanel();
        
        // Pannello inferiore per i pulsanti
        JPanel buttonPanel = createButtonPanel();
        
        // Status label
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Layout generale
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                             changedFilesPanel, 
                                             commitMessagePanel);
        splitPane.setResizeWeight(0.6);
        
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);
    }
    
    private JPanel createChangedFilesPanel() {
        // Crea tabella per i file modificati
        String[] columnNames = {COL_STATUS, COL_STAGED, COL_FILE};
        fileTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return Boolean.class; // Colonna Staged è un checkbox
                }
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Solo la colonna Staged è modificabile
            }
        };
        
        changedFilesTable = new JTable(fileTableModel);
        changedFilesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        changedFilesTable.setRowHeight(24);
        
        // Imposta larghezza colonne
        changedFilesTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Status
        changedFilesTable.getColumnModel().getColumn(1).setPreferredWidth(60);  // Staged
        changedFilesTable.getColumnModel().getColumn(2).setPreferredWidth(500); // File
        
        // Sorter per la tabella
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(fileTableModel);
        changedFilesTable.setRowSorter(sorter);
        
        // Listener per i checkbox
        changedFilesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = changedFilesTable.rowAtPoint(e.getPoint());
                int col = changedFilesTable.columnAtPoint(e.getPoint());
                
                if (col == 1 && row >= 0) { // Clicked on checkbox column
                    int modelRow = changedFilesTable.convertRowIndexToModel(row);
                    String file = (String) fileTableModel.getValueAt(modelRow, 2);
                    Boolean isStaged = (Boolean) fileTableModel.getValueAt(modelRow, 1);
                    
                    if (isStaged) {
                        // File deve essere aggiunto
                        addFiles(List.of(file));
                    } else {
                        // File deve essere rimosso dalla staging area
                        resetFiles(List.of(file));
                    }
                }
            }
        });
        
        // Menu contestuale
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addItem = new JMenuItem("Add to Staging");
        JMenuItem resetItem = new JMenuItem("Remove from Staging");
        JMenuItem viewDiffItem = new JMenuItem("View Changes");
        
        addItem.addActionListener(e -> addSelectedFiles());
        resetItem.addActionListener(e -> resetSelectedFiles());
        viewDiffItem.addActionListener(e -> viewDiffForSelectedFile());
        
        popupMenu.add(addItem);
        popupMenu.add(resetItem);
        popupMenu.add(new JSeparator());
        popupMenu.add(viewDiffItem);
        
        changedFilesTable.setComponentPopupMenu(popupMenu);
        
        // Pannello contenitore con titolo
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Changed Files",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        JScrollPane scrollPane = new JScrollPane(changedFilesTable);
        
        // Toolbar per le azioni sui file
        JToolBar fileToolbar = new JToolBar();
        fileToolbar.setFloatable(false);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setIcon(UIManager.getIcon("FileView.refreshIcon"));
        refreshButton.addActionListener(this);
        
        addSelectedButton = new JButton("Stage Selected");
        addSelectedButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        addSelectedButton.addActionListener(this);
        
        addAllButton = new JButton("Stage All");
        addAllButton.addActionListener(this);
        
        resetButton = new JButton("Unstage Selected");
        resetButton.addActionListener(this);
        
        fileToolbar.add(refreshButton);
        fileToolbar.addSeparator();
        fileToolbar.add(addSelectedButton);
        fileToolbar.add(addAllButton);
        fileToolbar.add(resetButton);
        
        panel.add(fileToolbar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCommitMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Commit Message",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        commitMessageArea = new JTextArea();
        commitMessageArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        commitMessageArea.setLineWrap(true);
        commitMessageArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(commitMessageArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        commitButton = new JButton("Commit Changes");
        commitButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        commitButton.addActionListener(this);
        
        panel.add(commitButton);
        
        return panel;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            refreshChangedFiles();
        } else if (e.getSource() == addSelectedButton) {
            addSelectedFiles();
        } else if (e.getSource() == addAllButton) {
            addAllFiles();
        } else if (e.getSource() == resetButton) {
            resetSelectedFiles();
        } else if (e.getSource() == commitButton) {
            commitChanges();
        }
    }
    
    /**
     * Aggiorna la lista dei file modificati eseguendo git status
     */
    public void refreshChangedFiles() {
        AppContext appContext = contextProvider.getAppContext();
        if (appContext == null) {
            setStatus("Error: No active AppContext available", true);
            return;
        }
        
        setStatus("Getting Git status...", false);
        
        SwingWorker<List<FileStatus>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<FileStatus> doInBackground() throws Exception {
                Git git = appContext.getGit();
                Map<String, Object> params = new HashMap<>();
                
                // Usa git status --porcelain per un formato più semplice da parsare
                params.put("porcelain", true);
                
                CommandOutput output = git.execute("git-status", params);
                
                if (!output.isSuccessful()) {
                    throw new Exception("Git status failed: " + output.getErrorOutput());
                }
                
                // Debug: stampa l'output completo sulla console di sistema
                System.out.println("\n\n======= RAW GIT STATUS OUTPUT =======");
                System.out.println(output.getStandardOutput());
                System.out.println("======= END OF GIT STATUS OUTPUT =======\n\n");
                
                List<FileStatus> files = parseGitStatus(output.getStandardOutput());
                return files;
            }
            
            @Override
            protected void done() {
                try {
                    changedFiles = get();
                    if (DEBUG) {
                        debugLog("Found " + changedFiles.size() + " changed files");
                    }
                    updateFileTable();
                    setStatus("Ready - " + changedFiles.size() + " files with changes", false);
                } catch (Exception ex) {
                    setStatus("Error: " + ex.getMessage(), true);
                    JOptionPane.showMessageDialog(GitCommitPanel.this,
                        "Error refreshing file status: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Aggiorna la tabella con la lista dei file modificati
     */
    private void updateFileTable() {
        // Cancella la tabella
        fileTableModel.setRowCount(0);
    
        AppContext appContext = contextProvider.getAppContext();
        if (appContext == null) {
            // Aggiungi una riga speciale indicando che serve aprire un progetto
            fileTableModel.addRow(new Object[] {
                "N/A",
                Boolean.FALSE,
                "Open a WordPress folder to see changed files"
            });
            
            // Disabilita tutti i pulsanti
            refreshButton.setEnabled(false);
            addAllButton.setEnabled(false);
            addSelectedButton.setEnabled(false);
            resetButton.setEnabled(false);
            commitButton.setEnabled(false);
            
            setStatus("Ready - No workspace open", false);
            return;
        }
        
        // Riabilita il pulsante di refresh quando c'è un contesto valido
        refreshButton.setEnabled(true);
        
        // Ottiene la lista dei file in staging area
        fetchStagedFiles();
        
        // Aggiorna la tabella con i file modificati
        for (FileStatus file : changedFiles) {
            boolean isStaged = stagedFiles.contains(file.getFileName());
            fileTableModel.addRow(new Object[] {
                file.getStatus(),
                isStaged,
                file.getFileName()
            });
        }
        
        // Abilita/disabilita i pulsanti in base ai file disponibili
        boolean hasFiles = !changedFiles.isEmpty();
        addAllButton.setEnabled(hasFiles);
        addSelectedButton.setEnabled(hasFiles);
        resetButton.setEnabled(!stagedFiles.isEmpty());
        commitButton.setEnabled(!stagedFiles.isEmpty());
        
        if (DEBUG) {
            debugLog("Table updated with " + fileTableModel.getRowCount() + " rows");
        }
    }
    
    /**
     * Ottiene la lista dei file nella staging area (git ls-files --stage)
     */
    private void fetchStagedFiles() {
        stagedFiles.clear();
        
        if (contextProvider == null) return;
        
        try {
            Git git = contextProvider.getAppContext().getGit();
            Map<String, Object> params = new HashMap<>();
            CommandOutput output = git.execute("git-ls-files", params);
            
            if (output.isSuccessful()) {
                String[] lines = output.getStandardOutput().split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        // Estrai solo il nome del file dall'output formato "mode commit stage\tfilename"
                        String[] parts = line.split("\t");
                        String fileName = parts.length > 1 ? parts[1] : line;
                        stagedFiles.add(fileName);
                    }
                }
                
                if (DEBUG) {
                    debugLog("Found " + stagedFiles.size() + " staged files");
                    debugLog("Staged files: " + String.join(", ", stagedFiles));
                }
            }
        } catch (Exception ex) {
            setStatus("Error getting staged files: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }
    
    /**
     * Aggiunge i file selezionati alla staging area
     */
    private void addSelectedFiles() {
        int[] selectedRows = changedFilesTable.getSelectedRows();
        if (selectedRows.length == 0) return;
        
        List<String> filesToAdd = new ArrayList<>();
        for (int row : selectedRows) {
            int modelRow = changedFilesTable.convertRowIndexToModel(row);
            String file = (String) fileTableModel.getValueAt(modelRow, 2);
            filesToAdd.add(file);
        }
        
        if (DEBUG) {
            debugLog("Adding files: " + String.join(", ", filesToAdd));
        }
        
        addFiles(filesToAdd);
    }
    
    /**
     * Aggiunge tutti i file modificati alla staging area
     */
    private void addAllFiles() {
        List<String> filesToAdd = new ArrayList<>();
        for (FileStatus file : changedFiles) {
            filesToAdd.add(file.getFileName());
        }
        
        if (DEBUG) {
            debugLog("Adding all files: " + String.join(", ", filesToAdd));
        }
        
        addFiles(filesToAdd);
    }
    
    /**
     * Rimuove i file selezionati dalla staging area
     */
    private void resetSelectedFiles() {
        int[] selectedRows = changedFilesTable.getSelectedRows();
        if (selectedRows.length == 0) return;
        
        List<String> filesToReset = new ArrayList<>();
        for (int row : selectedRows) {
            int modelRow = changedFilesTable.convertRowIndexToModel(row);
            String file = (String) fileTableModel.getValueAt(modelRow, 2);
            Boolean isStaged = (Boolean) fileTableModel.getValueAt(modelRow, 1);
            
            if (isStaged) {
                filesToReset.add(file);
            }
        }
        
        if (DEBUG) {
            debugLog("Resetting files: " + String.join(", ", filesToReset));
        }
        
        resetFiles(filesToReset);
    }
    
    /**
     * Esegue git add sui file specificati
     */
    private void addFiles(List<String> files) {
        if (files.isEmpty()) return;
        
        if (contextProvider == null) return;
        
        setStatus("Adding files to staging area...", false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Git git = contextProvider.getAppContext().getGit();
                Map<String, Object> params = new HashMap<>();
                params.put("files", files);
                
                if (DEBUG) {
                    debugLog("Executing git-add with parameters: " + params);
                }
                
                CommandOutput output = git.execute("git-add", params);
                
                if (DEBUG) {
                    debugLog("git-add result: " + output.isSuccessful());
                    debugLog("Output: " + output.getStandardOutput());
                    debugLog("Error: " + output.getErrorOutput());
                }
                
                return output.isSuccessful();
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        setStatus("Files added to staging area", false);
                        // Aggiorna l'interfaccia dopo aver aggiunto i file
                        refreshChangedFiles();
                    } else {
                        setStatus("Failed to add files to staging area", true);
                        JOptionPane.showMessageDialog(GitCommitPanel.this,
                            "Failed to add files to staging area",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    setStatus("Error: " + ex.getMessage(), true);
                    JOptionPane.showMessageDialog(GitCommitPanel.this,
                        "Error adding files to staging area: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Esegue git reset sui file specificati
     */
    private void resetFiles(List<String> files) {
        if (files.isEmpty()) return;
        
        if (contextProvider == null) return;
        
        setStatus("Removing files from staging area...", false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Git git = contextProvider.getAppContext().getGit();
                Map<String, Object> params = new HashMap<>();
                params.put("files", files);
                
                if (DEBUG) {
                    debugLog("Executing git-reset with parameters: " + params);
                }
                
                CommandOutput output = git.execute("git-reset", params);
                
                if (DEBUG) {
                    debugLog("git-reset result: " + output.isSuccessful());
                    debugLog("Output: " + output.getStandardOutput());
                    debugLog("Error: " + output.getErrorOutput());
                }
                
                return output.isSuccessful();
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        setStatus("Files removed from staging area", false);
                        // Aggiorna l'interfaccia dopo aver resettato i file
                        refreshChangedFiles();
                    } else {
                        setStatus("Failed to remove files from staging area", true);
                        JOptionPane.showMessageDialog(GitCommitPanel.this,
                            "Failed to remove files from staging area",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    setStatus("Error: " + ex.getMessage(), true);
                    JOptionPane.showMessageDialog(GitCommitPanel.this,
                        "Error removing files from staging area: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Visualizza le differenze per il file selezionato
     */
    private void viewDiffForSelectedFile() {
        int selectedRow = changedFilesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int modelRow = changedFilesTable.convertRowIndexToModel(selectedRow);
        String file = (String) fileTableModel.getValueAt(modelRow, 2);
        Boolean isStaged = (Boolean) fileTableModel.getValueAt(modelRow, 1);
        
        if (contextProvider == null) return;
        
        setStatus("Getting diff for " + file + "...", false);
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                Git git = contextProvider.getAppContext().getGit();
                Map<String, Object> params = new HashMap<>();
                params.put("file", file);
                params.put("staged", isStaged);
                
                if (DEBUG) {
                    debugLog("Executing git-diff with parameters: " + params);
                }
                
                CommandOutput output = git.execute("git-diff", params);
                if (!output.isSuccessful()) {
                    throw new Exception("Git diff failed: " + output.getErrorOutput());
                }
                
                if (DEBUG) {
                    debugLog("git-diff successful, output length: " + output.getStandardOutput().length());
                }
                
                return output.getStandardOutput();
            }
            
            @Override
            protected void done() {
                try {
                    String diffOutput = get();
                    setStatus("Ready", false);
                    showDiffDialog(file, diffOutput);
                } catch (Exception ex) {
                    setStatus("Error: " + ex.getMessage(), true);
                    JOptionPane.showMessageDialog(GitCommitPanel.this,
                        "Error getting diff: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Mostra una finestra di dialogo con le differenze
     */
    private void showDiffDialog(String file, String diffOutput) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
                                      "Diff for " + file,
                                      true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        JTextArea diffArea = new JTextArea();
        diffArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        diffArea.setEditable(false);
        diffArea.setText(diffOutput);
        diffArea.setCaretPosition(0);
        
        // Crea pannello con pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(diffArea), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Esegue il commit con il messaggio inserito
     */
    private void commitChanges() {
        String message = commitMessageArea.getText().trim();
        
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a commit message",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (stagedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No files staged for commit",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (contextProvider == null) return;
        
        setStatus("Committing changes...", false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Git git = contextProvider.getAppContext().getGit();
                Map<String, Object> params = new HashMap<>();
                params.put("message", message);
                
                if (DEBUG) {
                    debugLog("Executing git-commit with message: " + message);
                }
                
                CommandOutput output = git.execute("git-commit", params);
                
                if (DEBUG) {
                    debugLog("git-commit result: " + output.isSuccessful());
                    debugLog("Output: " + output.getStandardOutput());
                    debugLog("Error: " + output.getErrorOutput());
                }
                
                return output.isSuccessful();
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        // Pulisci il messaggio e aggiorna la lista dei file
                        commitMessageArea.setText("");
                        refreshChangedFiles();
                        
                        setStatus("Commit successful", false);
                        JOptionPane.showMessageDialog(GitCommitPanel.this,
                            "Commit successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        setStatus("Commit failed", true);
                        JOptionPane.showMessageDialog(GitCommitPanel.this,
                            "Commit failed",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    setStatus("Error: " + ex.getMessage(), true);
                    JOptionPane.showMessageDialog(GitCommitPanel.this,
                        "Error during commit: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Imposta il messaggio di stato
     */
    private void setStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setForeground(isError ? Color.RED : Color.BLACK);
        }
        
        if (DEBUG) {
            debugLog("Status: " + message + (isError ? " (ERROR)" : ""));
        }
    }
    
    /**
     * Stampa un messaggio di debug sulla console
     */
    private void debugLog(String message) {
        if (DEBUG) {
            System.out.println("[GitCommitPanel] " + message);
        }
    }
    
    /**
     * Esegue il refresh del pannello quando il contesto cambia
     */
    public void refresh() {
        if (DEBUG) {
            debugLog("Manual refresh triggered");
        }
        refreshChangedFiles();
    }
    
    /**
     * Classe per rappresentare lo stato di un file
     */
    private static class FileStatus {
        private final String status;
        private final String fileName;
        
        public FileStatus(String status, String fileName) {
            this.status = status;
            this.fileName = fileName;
        }
        
        public String getStatus() {
            return status;
        }
        
        public String getFileName() {
            return fileName;
        }
    }
    
    /**
     * Elabora l'output di git status per ottenere la lista dei file modificati
     */
    private List<FileStatus> parseGitStatus(String output) {
        List<FileStatus> files = new ArrayList<>();
        
        if (output == null || output.trim().isEmpty()) {
            debugLog("git status output is empty");
            return files;
        }
        
        // Debug: stampiamo l'output completo per analizzarlo
        debugLog("Raw git status output:\n" + output);
        
        // Vari pattern per riconoscere i file modificati in diversi formati di output
    
        // Pattern per git status --porcelain (formato breve)
        Pattern porcelainPattern = Pattern.compile("^([MADRCU\\?\\! ])([ MADRCU\\?\\!])\\s+(.+)$");
    
        // Pattern per git status normale: modified/new file/deleted/etc.
        Pattern stdPattern = Pattern.compile("\\s*(modified|deleted|new file|renamed|added):\\s+(.+)");
    
        // Pattern per git status più semplice
        Pattern simplePattern = Pattern.compile("\\s*([MADRCU\\?]{1,2})\\s+(.+)");
    
        String[] lines = output.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Prova prima il formato porcelain
            Matcher porcelainMatcher = porcelainPattern.matcher(line);
            if (porcelainMatcher.matches()) {
                char index = porcelainMatcher.group(1).charAt(0);
                char workTree = porcelainMatcher.group(2).charAt(0);
                String file = porcelainMatcher.group(3);
                
                // Determina lo stato dal carattere
                String status = getStatusFromCode(index != ' ' ? index : workTree);
                files.add(new FileStatus(status, file));
                debugLog("Found file (porcelain format): " + file + " with status: " + status);
                continue;
            }
            
            // Prova il formato standard
            Matcher stdMatcher = stdPattern.matcher(line);
            if (stdMatcher.matches()) {
                String status = stdMatcher.group(1).trim();
                String file = stdMatcher.group(2).trim();
                
                String readableStatus = getReadableStatus(status);
                files.add(new FileStatus(readableStatus, file));
                debugLog("Found file (standard format): " + file + " with status: " + readableStatus);
                continue;
            }
            
            // Prova il formato semplice
            Matcher simpleMatcher = simplePattern.matcher(line);
            if (simpleMatcher.matches()) {
                String statusCode = simpleMatcher.group(1);
                String file = simpleMatcher.group(2).trim();
                
                String readableStatus = getStatusFromCode(statusCode.charAt(0));
                files.add(new FileStatus(readableStatus, file));
                debugLog("Found file (simple format): " + file + " with status: " + readableStatus);
            }
        }
        
        debugLog("Total files found: " + files.size());
        
        return files;
    }

    private String getStatusFromCode(char code) {
        switch (code) {
            case 'M': return "Modified";
            case 'A': return "Added";
            case 'D': return "Deleted";
            case 'R': return "Renamed";
            case 'C': return "Copied";
            case 'U': return "Updated";
            case '?': return "Untracked";
            default: return String.valueOf(code);
        }
    }

    private String getReadableStatus(String status) {
        switch (status) {
            case "modified": return "Modified";
            case "deleted": return "Deleted";
            case "new file": return "Added";
            case "renamed": return "Renamed";
            case "added": return "Added";
            default: return status;
        }
    }
}