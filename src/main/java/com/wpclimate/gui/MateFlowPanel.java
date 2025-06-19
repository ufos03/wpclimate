package com.wpclimate.gui;

import com.wpclimate.core.AppContext;
import com.wpclimate.mateflow.MateFlow;
import com.wpclimate.mateflow.MateFlowException;
import com.wpclimate.mateflow.MateFlowManager;
import com.wpclimate.mateflow.MateFlowStep;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class MateFlowPanel extends JPanel {
    private JList<MateFlow> flowList;
    private DefaultListModel<MateFlow> flowListModel;
    private JButton executeButton;
    private JButton viewDetailsButton;
    private JButton saveButton;
    private Consumer<MateFlow> executeHandler;
    private JLabel statusLabel;
    
    public MateFlowPanel(Consumer<MateFlow> executeHandler) {
        this.executeHandler = executeHandler;
        setLayout(new BorderLayout(10, 10));
        
        // Create list model and list
        flowListModel = new DefaultListModel<>();
        flowList = new JList<>(flowListModel);
        flowList.setCellRenderer(new MateFlowListRenderer());
        flowList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Create list panel with border
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createEtchedBorder()
            ), 
            "Available MateFlows",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Dialog", Font.BOLD, 12)
        ));
        listPanel.add(new JScrollPane(flowList), BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Add status label with modern styling
        statusLabel = new JLabel("No flows available");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        buttonPanel.add(statusLabel, BorderLayout.WEST);
        
        // Modern button panel
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        
        // Aggiungi il nuovo pulsante "New Flow"
        JButton newFlowButton = new JButton("New Flow");
        newFlowButton.setIcon(UIManager.getIcon("FileChooser.newFolderIcon"));
        newFlowButton.addActionListener(e -> createNewFlow());
        
        // Create buttons with icons (you can replace these with actual icons)
        executeButton = new JButton("Execute Flow");
        executeButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        
        viewDetailsButton = new JButton("View Steps");
        viewDetailsButton.setIcon(UIManager.getIcon("Tree.openIcon"));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setIcon(UIManager.getIcon("FileView.refreshIcon"));
        
        saveButton = new JButton("Save Changes");
        saveButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        saveButton.setEnabled(false);
        
        executeButton.addActionListener(e -> executeSelectedFlow());
        viewDetailsButton.addActionListener(e -> showSelectedFlowDetails());
        refreshButton.addActionListener(e -> {
            // If we're in WpClimateMainFrame, we can get the manager
            Container parent = getParent();
            while (parent != null) {
                if (parent instanceof AppContextProvider) {
                    AppContextProvider provider = (AppContextProvider) parent;
                    if (provider.getAppContext() != null) {
                        refreshFlows(provider.getAppContext().getMateFlowManager());
                    }
                    break;
                }
                parent = parent.getParent();
            }
        });
        
        rightButtonPanel.add(newFlowButton);
        rightButtonPanel.add(refreshButton);
        rightButtonPanel.add(viewDetailsButton);
        rightButtonPanel.add(executeButton);
        
        buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
        
        // Add components to panel
        add(listPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Disable buttons initially
        executeButton.setEnabled(false);
        viewDetailsButton.setEnabled(false);
        
        // Enable buttons when selection changes
        flowList.addListSelectionListener(e -> {
            boolean hasSelection = !flowList.isSelectionEmpty();
            executeButton.setEnabled(hasSelection);
            viewDetailsButton.setEnabled(hasSelection);
        });
    }
    
    public void refreshFlows(MateFlowManager mateFlowManager) {
        if (mateFlowManager == null) {
            statusLabel.setText("No MateFlow manager available");
            return;
        }
        
        flowListModel.clear();
        
        try {
            Collection<MateFlow> flows = mateFlowManager.getAvailableFlows();
            
            if (flows == null || flows.isEmpty()) {
                statusLabel.setText("No flows available");
                return;
            }
            
            for (MateFlow flow : flows) {
                flowListModel.addElement(flow);
            }
            
            statusLabel.setText(flows.size() + " flows available");
            
        } catch (IOException e) {
            statusLabel.setText("Error loading flows: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Could not load MateFlows: " + e.getMessage(),
                "MateFlow Loading Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MateFlowException e) {
            statusLabel.setText("Error parsing flows: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error parsing MateFlows: " + e.getMessage(),
                "MateFlow Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            statusLabel.setText("Unexpected error: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Unexpected error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void executeSelectedFlow() {
        MateFlow selected = flowList.getSelectedValue();
        if (selected != null && executeHandler != null) {
            executeHandler.accept(selected);
        }
    }
    
    private void showSelectedFlowDetails() {
        MateFlow selectedFlow = flowList.getSelectedValue();
        if (selectedFlow == null) return;
        
        // Create a dialog to show flow details
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                    "MateFlow Steps: " + selectedFlow.getFlowName(), 
                                    true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600); // Larger dialog for better visibility
        dialog.setLocationRelativeTo(this);
        
        // Add flow info at the top
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        ));
        
        Font titleFont = new Font(Font.DIALOG, Font.BOLD, 14);
        
        JLabel nameLabel = new JLabel("<html><b>Flow Name:</b> " + selectedFlow.getFlowName() + "</html>");
        nameLabel.setFont(titleFont);
        
        String description = selectedFlow.getDescription();
        if (description == null) description = "No description available";
        JLabel descLabel = new JLabel("<html><b>Description:</b> " + description + "</html>");
        
        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);
        
        try {
            // Verifica che i comandi non siano null
            if (selectedFlow.getCommands() == null) {
                selectedFlow.addCommands(new ArrayList<MateFlowStep>());
            }
            
            // Create table model for steps
            StepTableModel tableModel = new StepTableModel(selectedFlow);
            
            // Create table and scroll pane with drag & drop support
            JTable stepsTable = createDraggableTable(tableModel);
            stepsTable.setRowHeight(50); // Make rows taller for better parameter viewing
            
            // Set up table appearance
            setupTableColumns(stepsTable);
            
            // Add double click listener for parameter editing
            stepsTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Point p = e.getPoint();
                        int row = stepsTable.rowAtPoint(p);
                        int col = stepsTable.columnAtPoint(p);
                        
                        // If double click on the Parameters column
                        if (col == 3 && row >= 0) {
                            editParameters(stepsTable, tableModel, row);
                        }
                    }
                }
            });
            
            JScrollPane tableScrollPane = new JScrollPane(stepsTable);
            
            // Aggiungi pannello per bottoni di gestione step
            JPanel stepControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            // Bottone per aggiungere un nuovo step
            JButton addStepButton = new JButton("Add Step");
            addStepButton.setIcon(UIManager.getIcon("FileView.fileIcon"));
            addStepButton.addActionListener(e -> addNewStep(tableModel));
            stepControlPanel.add(addStepButton);
            
            // Bottone per eliminare uno step selezionato
            JButton deleteStepButton = new JButton("Delete Step");
            deleteStepButton.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
            deleteStepButton.addActionListener(e -> {
                int selectedRow = stepsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    deleteStep(tableModel, selectedRow);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Please select a step to delete.",
                        "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
            stepControlPanel.add(deleteStepButton);
            
            // Pannello contenitore per tabella e controlli
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10),
                BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), 
                    "Execution Steps (Drag rows to reorder)",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    titleFont
                )
            ));
            
            tablePanel.add(stepControlPanel, BorderLayout.NORTH);
            tablePanel.add(tableScrollPane, BorderLayout.CENTER);
            
            // Add buttons
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            
            JButton saveButton = new JButton("Save Changes");
            saveButton.addActionListener(e -> {
                // Save the updated flow (with reordered steps)
                saveChanges(selectedFlow, tableModel.getSteps());
                dialog.dispose();
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            buttonPanel.add(saveButton);
            buttonPanel.add(closeButton);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            
            // Add components to dialog - only once!
            dialog.add(infoPanel, BorderLayout.NORTH);
            dialog.add(tablePanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
        } catch (Exception ex) {
            // Intercetta qualsiasi eccezione e mostra un messaggio di errore
            JOptionPane.showMessageDialog(dialog,
                "Error loading flow details: " + ex.getMessage() + "\n\n" +
                "Stack trace: " + Arrays.toString(ex.getStackTrace()).substring(0, 200) + "...",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            
            // Aggiungi solo il pannello info e un messaggio di errore
            dialog.add(infoPanel, BorderLayout.NORTH);
            dialog.add(new JLabel("Error loading flow details: " + ex.getMessage(), JLabel.CENTER), BorderLayout.CENTER);
            
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    private void editParameters(JTable table, StepTableModel model, int row) {
        // Get the step and its parameters
        MateFlowStep step = model.getStepAt(row);
        Map<String, Object> params = new HashMap<>();
        
        // Clone existing parameters to avoid direct reference modification
        if (step.getParametes() != null) {
            params.putAll(step.getParametes());
        }
        
        // Create a dialog to edit parameters
        JDialog paramsDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                        "Edit Parameters", true);
        paramsDialog.setLayout(new BorderLayout(10, 10));
        paramsDialog.setSize(600, 400); // Make dialog larger
        paramsDialog.setLocationRelativeTo(table);
        
        // Create parameter table
        String[] columnNames = {"Parameter", "Value"};
        DefaultTableModel paramsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Only value column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // Treat all values as strings for editing
            }
        };
        
        // Add parameters to the model
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                paramsModel.addRow(new Object[]{entry.getKey(), String.valueOf(entry.getValue())});
            }
        }
        
        // Create parameter table with better row height
        JTable paramsTable = new JTable(paramsModel);
        paramsTable.setRowHeight(30); // Increase row height for better visibility
        paramsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        paramsTable.getColumnModel().getColumn(1).setPreferredWidth(450);
        
        // Add custom editor for long texts
        paramsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, 
                                                        boolean isSelected, int row, int column) {
                JTextField field = (JTextField)super.getTableCellEditorComponent(
                                    table, value, isSelected, row, column);
                field.setBorder(BorderFactory.createCompoundBorder(
                    field.getBorder(),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return field;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(paramsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add buttons to add/remove parameters
        JButton addButton = new JButton("Add Parameter");
        addButton.addActionListener(e -> {
            paramsModel.addRow(new Object[]{"new_param", ""});
        });
        
        JButton removeButton = new JButton("Remove Parameter");
        removeButton.addActionListener(e -> {
            int selectedRow = paramsTable.getSelectedRow();
            if (selectedRow >= 0) {
                paramsModel.removeRow(selectedRow);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Add OK/Cancel buttons
        JButton okButton = new JButton("Apply");
        okButton.addActionListener(e -> {
            // Ensure we're not editing a cell when Apply is clicked
            if (paramsTable.isEditing()) {
                paramsTable.getCellEditor().stopCellEditing();
            }
            
            // Clear existing parameters and add all from table
            Map<String, Object> newParams = new HashMap<>();
            for (int i = 0; i < paramsModel.getRowCount(); i++) {
                String key = (String) paramsModel.getValueAt(i, 0);
                String value = (String) paramsModel.getValueAt(i, 1);
                if (key != null && !key.isEmpty()) {
                    newParams.put(key, value);
                }
            }
            
            // Update the step with new parameters map - fix method name
            step.setParamteres(newParams);
            
            // Update the main table
            model.fireTableRowsUpdated(row, row);
            
            paramsDialog.dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> paramsDialog.dispose());
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel okCancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okCancelPanel.add(okButton);
        okCancelPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(okCancelPanel, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        
        // Add title with command information 
        JLabel titleLabel = new JLabel("  Edit parameters for: " + step.getGroup() + " - " + step.getCommand());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        
        // Add components to dialog
        paramsDialog.add(titleLabel, BorderLayout.NORTH);
        paramsDialog.add(scrollPane, BorderLayout.CENTER);
        paramsDialog.add(bottomPanel, BorderLayout.SOUTH);
        
        paramsDialog.setVisible(true);
    }
    
    /**
     * Table model for MateFlow steps - updated to handle null commands
     */
    private class StepTableModel extends DefaultTableModel {
        private final MateFlow flow;
        private final String[] columnNames = {"#", "Group", "Command", "Parameters"};
        
        public StepTableModel(MateFlow flow) {
            super();
            this.flow = flow;
            
            // Inizializza i comandi se sono null
            if (flow != null && flow.getCommands() == null) {
                // Crea una lista vuota di comandi
                flow.addCommands(new ArrayList<>());
            }
        }
        
        @Override
        public int getRowCount() {
            if (flow == null || flow.getCommands() == null) {
                return 0;
            }
            return flow.getCommands().size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 3) {
                return Map.class; // Use custom renderer for parameters
            }
            return Object.class;
        }
        
        @Override
        public Object getValueAt(int row, int column) {
            if (flow == null || flow.getCommands() == null || 
                row < 0 || row >= flow.getCommands().size()) {
                return null;
            }
            
            MateFlowStep step = flow.getCommands().get(row);
            if (step == null) {
                return null;
            }
            
            switch (column) {
                case 0: return row + 1;
                case 1: return step.getGroup();
                case 2: return step.getCommand();
                case 3: return step.getParametes();
                default: return null;
            }
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3; // Only parameters are editable directly
        }
        
        public MateFlowStep getStepAt(int row) {
            if (flow == null || flow.getCommands() == null || 
                row < 0 || row >= flow.getCommands().size()) {
                return null;
            }
            return flow.getCommands().get(row);
        }
        
        /**
         * Sposta uno step verso l'alto
         */
        public void moveStepUp(int row) {
            if (flow != null && flow.getCommands() != null) {
                flow.moveCommandUp(row);
                fireTableDataChanged();
            }
        }
        
        /**
         * Sposta uno step verso il basso
         */
        public void moveStepDown(int row) {
            if (flow != null && flow.getCommands() != null) {
                flow.moveCommandDown(row);
                fireTableDataChanged();
            }
        }
        
        /**
         * Rimuove uno step
         */
        public void removeStep(int row) {
            if (flow != null && flow.getCommands() != null) {
                flow.removeCommand(row);
                fireTableDataChanged();
            }
        }
        
        /**
         * Aggiunge un nuovo step
         */
        public void addStep(MateFlowStep step) {
            if (flow != null) {
                if (flow.getCommands() == null) {
                    flow.addCommands(new ArrayList<>());
                }
                flow.addCommand(step);
                fireTableDataChanged();
            }
        }
        
        public List<MateFlowStep> getSteps() {
            if (flow == null || flow.getCommands() == null) {
                return new ArrayList<>();
            }
            return flow.getCommands();
        }
        
        public MateFlow getFlow() {
            return flow;
        }
    }
    
    /**
     * Transfer handler for drag and drop row reordering
     */
    private class TableRowTransferHandler extends TransferHandler {
        private final DataFlavor localObjectFlavor;
        private int[] indices;
        private final JTable table;
        
        public TableRowTransferHandler(JTable table) {
            this.table = table;
            try {
                // Utilizza direttamente Integer[] come DataFlavor invece di TransferableRowData
                localObjectFlavor = new DataFlavor(
                    DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + Integer[].class.getName() + "\"");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("ClassNotFound: " + e.getMessage());
            }
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            JTable source = (JTable) c;
            indices = source.getSelectedRows();
            
            Integer[] rowData = new Integer[indices.length];
            for (int i = 0; i < indices.length; i++) {
                rowData[i] = indices[i];
            }
            
            // Crea un Transferable che contiene direttamente l'array Integer[]
            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[] { localObjectFlavor };
                }
                
                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.equals(localObjectFlavor);
                }
                
                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                    if (flavor.equals(localObjectFlavor)) {
                        return rowData;
                    }
                    throw new UnsupportedFlavorException(flavor);
                }
            };
        }
        
        @Override
        public boolean canImport(TransferSupport info) {
            if (!info.isDrop() || !info.isDataFlavorSupported(localObjectFlavor)) {
                return false;
            }
            
            JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
            if (dl.getRow() == -1) {
                return false;
            }
            
            return true;
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }
        
        @Override
        public boolean importData(TransferSupport info) {
            if (!canImport(info)) {
                return false;
            }
            
            JTable target = (JTable) info.getComponent();
            JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
            StepTableModel model = (StepTableModel) target.getModel();
            int dropIndex = dl.getRow();
            
            try {
                // Ottieni direttamente l'array di Integer invece di TransferableRowData
                Integer[] rowData = (Integer[]) info.getTransferable()
                    .getTransferData(localObjectFlavor);
                    
                if (rowData.length == 0) {
                    return false;
                }
                    
                int sourceIndex = rowData[0];
                
                // Se stai droppando la riga sulla stessa posizione, ignora
                if (sourceIndex == dropIndex || sourceIndex + 1 == dropIndex) {
                    return false;
                }
                
                // Ottieni il riferimento al flow
                MateFlow flow = model.getFlow();
                
                // Gestisci lo spostamento dell'elemento
                if (sourceIndex < dropIndex) {
                    // Spostamento verso il basso
                    for (int i = 0; i < dropIndex - sourceIndex - 1; i++) {
                        flow.moveCommandDown(sourceIndex + i);
                    }
                } else {
                    // Spostamento verso l'alto
                    for (int i = 0; i < sourceIndex - dropIndex; i++) {
                        flow.moveCommandUp(sourceIndex - i);
                    }
                }
                
                // Aggiorna la tabella e seleziona la riga spostata
                model.fireTableDataChanged();
                target.getSelectionModel().setSelectionInterval(
                    dropIndex > sourceIndex ? dropIndex - 1 : dropIndex, 
                    dropIndex > sourceIndex ? dropIndex - 1 : dropIndex);
                
                return true;
                
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            // Non facciamo nulla qui perché usiamo i metodi di MateFlow
        }
    }
    
    /**
     * Custom renderer for parameter cells
     */
    private class ParameterCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value instanceof Map) {
                Map<String, Object> params = (Map<String, Object>) value;
                
                if (params == null || params.isEmpty()) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, "No parameters", isSelected, hasFocus, row, column);
                    label.setVerticalAlignment(JLabel.TOP);
                    return label;
                }
                
                // Create a panel instead of using HTML for better layout
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                
                // Set background and foreground colors based on selection state
                if (isSelected) {
                    panel.setBackground(table.getSelectionBackground());
                    panel.setForeground(table.getSelectionForeground());
                } else {
                    panel.setBackground(table.getBackground());
                    panel.setForeground(table.getForeground());
                }
                
                // Add a small border for spacing
                panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                // Create a label for each parameter
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    JPanel paramPanel = new JPanel(new BorderLayout(5, 0));
                    paramPanel.setOpaque(false); // Don't show panel background
                    
                    JLabel keyLabel = new JLabel(entry.getKey() + ":");
                    keyLabel.setFont(keyLabel.getFont().deriveFont(Font.BOLD));
                    keyLabel.setForeground(new Color(0, 0, 150)); // Dark blue for keys
                    
                    String valueText = String.valueOf(entry.getValue());
                    JLabel valueLabel = new JLabel(valueText);
                    
                    paramPanel.add(keyLabel, BorderLayout.WEST);
                    paramPanel.add(valueLabel, BorderLayout.CENTER);
                    
                    panel.add(paramPanel);
                }
                
                panel.setToolTipText("Double-click to edit parameters");
                return panel;
            } else {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setVerticalAlignment(JLabel.TOP);
                return label;
            }
        }
    }
    
    /**
     * TableCellRenderer wrapper for the parameter panel to properly show in the table
     */
    private class ParameterWrapperRenderer implements javax.swing.table.TableCellRenderer {
        private final ParameterCellRenderer renderer = new ParameterCellRenderer();
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component comp = renderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
                
            // If it's a panel returned from our custom renderer
            if (comp instanceof JPanel) {
                return comp;
            }
            
            // Fall back to the regular renderer
            return renderer;
        }
    }
    
    private JTable createDraggableTable(StepTableModel model) {
        JTable table = new JTable(model);
        
        // Enable drag and drop reordering
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new TableRowTransferHandler(table));
        
        // Increase row height for better display of parameters
        table.setRowHeight(75);
        
        // Use custom renderer for parameters that prevents squishing
        table.getColumnModel().getColumn(3).setCellRenderer(new ParameterWrapperRenderer());
        
        return table;
    }
    
    private void setupTableColumns(JTable table) {
        TableColumn stepColumn = table.getColumnModel().getColumn(0);
        TableColumn groupColumn = table.getColumnModel().getColumn(1);
        TableColumn commandColumn = table.getColumnModel().getColumn(2);
        TableColumn paramsColumn = table.getColumnModel().getColumn(3);
        
        stepColumn.setPreferredWidth(40);
        groupColumn.setPreferredWidth(80);
        commandColumn.setPreferredWidth(160);
        paramsColumn.setPreferredWidth(520);
        
        // Center align the first two columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        stepColumn.setCellRenderer(centerRenderer);
        groupColumn.setCellRenderer(centerRenderer);
    }
    
    private void saveChanges(MateFlow flow, List<MateFlowStep> updatedSteps) {
        // Assicurati che i comandi non siano null
        if (flow == null) {
            JOptionPane.showMessageDialog(this,
                "Flow is null and cannot be saved.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Non abbiamo bisogno di aggiornare i comandi perché stiamo lavorando
        // direttamente con flow.getCommands() nel nostro modello
        
        // Save to disk
        try {
            // Find the manager
            Container parent = getParent();
            while (parent != null) {
                if (parent instanceof AppContextProvider) {
                    AppContextProvider provider = (AppContextProvider) parent;
                    if (provider.getAppContext() != null) {
                        // Save the updated flow
                        provider.getAppContext().getMateFlowManager().saveMateFlow(flow);
                        
                        int commandCount = (flow.getCommands() != null) ? flow.getCommands().size() : 0;
                        JOptionPane.showMessageDialog(this,
                            "Flow saved successfully with " + commandCount + " steps.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    break;
                }
                parent = parent.getParent();
            }
            
            JOptionPane.showMessageDialog(this,
                "Could not find MateFlowManager to save changes.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving flow: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Custom renderer for MateFlow entries
     */
    private class MateFlowListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
                
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof MateFlow) {
                MateFlow flow = (MateFlow) value;
                setText(flow.getFlowName());
                setToolTipText("<html><b>" + flow.getFlowName() + "</b><br>" + 
                    flow.getDescription() + "<br>Steps: " + 
                    flow.getCommands().size() + "</html>");
                
                // Add some padding
                setBorder(BorderFactory.createCompoundBorder(
                    getBorder(),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
            
            return this;
        }
    }
    
    /**
     * Elimina uno step dalla tabella usando i metodi di MateFlow
     */
    private void deleteStep(StepTableModel model, int rowIndex) {
        if (rowIndex < 0 || rowIndex >= model.getRowCount()) {
            return;
        }
        
        // Chiedi conferma prima di eliminare
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete step " + (rowIndex + 1) + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeStep(rowIndex);
        }
    }
    
    /**
     * Aggiunge un nuovo step vuoto al flow
     */
    private void addNewStep(StepTableModel model) {
        // Crea dialog per inserimento dati del nuovo step
        JDialog addDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                      "Add New Step", true);
        addDialog.setLayout(new BorderLayout(10, 10));
        addDialog.setSize(500, 300);
        addDialog.setLocationRelativeTo(this);
        
        // Crea pannello per input
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel groupLabel = new JLabel("Group:");
        JComboBox<String> groupCombo = new JComboBox<>(new String[]{"GIT", "WP"});
        
        JLabel commandLabel = new JLabel("Command:");
        JTextField commandField = new JTextField(20);
        
        inputPanel.add(groupLabel);
        inputPanel.add(groupCombo);
        inputPanel.add(commandLabel);
        inputPanel.add(commandField);
        
        // Bottoni
        JButton okButton = new JButton("Add");
        okButton.addActionListener(e -> {
            String group = (String) groupCombo.getSelectedItem();
            String command = commandField.getText().trim();
            
            if (command.isEmpty()) {
                JOptionPane.showMessageDialog(addDialog,
                    "Please enter a command.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Crea nuovo step con il costruttore corretto
            MateFlowStep newStep = new MateFlowStep(command, new HashMap<>(), group);
            
            model.addStep(newStep);
            addDialog.dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> addDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Aggiungi input e pannello bottoni al dialog
        addDialog.add(inputPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        addDialog.setVisible(true);
    }
    
    // Aggiungi questo metodo al MateFlowPanel
    private void createNewFlow() {
        Container parent = getParent();
        AppContext appContext = null;
        
        while (parent != null) {
            if (parent instanceof AppContextProvider) {
                AppContextProvider provider = (AppContextProvider) parent;
                appContext = provider.getAppContext();
                break;
            }
            parent = parent.getParent();
        }
        
        if (appContext == null) {
            JOptionPane.showMessageDialog(this, 
                "Cannot create flow: No AppContext available", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        NewMateFlowDialog dialog = new NewMateFlowDialog(owner, appContext);
        dialog.setVisible(true);
        
        // Dopo la chiusura del dialog, aggiorna la lista
        refreshFlows(appContext.getMateFlowManager());
    }
}