package com.wpclimate.gui;

import com.wpclimate.core.AppContext;
import com.wpclimate.core.command.CommandRegistry.CommandInfo;
import com.wpclimate.core.command.CommandRegistry.ParamInfo;
import com.wpclimate.mateflow.MateFlow;
import com.wpclimate.mateflow.MateFlowStep;
import com.wpclimate.mateflow.MateFlowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialog per la creazione di un nuovo MateFlow
 */
public class NewMateFlowDialog extends JDialog {
    private final AppContext appContext;
    private JTextField flowNameField;
    private JTextArea descriptionArea;
    private DefaultListModel<MateFlowStep> stepsModel;
    private JList<MateFlowStep> stepsList;
    private JButton createFlowButton;
    private JButton cancelButton;
    private JButton addStepButton;
    private JButton removeStepButton;
    private JButton editStepButton;
    private Map<String, CommandInfo> availableCommands;
    
    public NewMateFlowDialog(Frame owner, AppContext appContext) {
        super(owner, "Create New MateFlow", true);
        this.appContext = appContext;
        availableCommands = appContext.getAvaliableCommands();
        initComponents();
        layoutUI();
        setSize(800, 600);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        // Campi per il nome e la descrizione del flow
        flowNameField = new JTextField(30);
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // Lista degli step
        stepsModel = new DefaultListModel<>();
        stepsList = new JList<>(stepsModel);
        stepsList.setCellRenderer(new StepListRenderer());
        stepsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Pulsanti per gestire gli step
        addStepButton = new JButton("Add Step");
        removeStepButton = new JButton("Remove Step");
        editStepButton = new JButton("Edit Step");
        removeStepButton.setEnabled(false);
        editStepButton.setEnabled(false);
        
        // Pulsanti per salvare o annullare
        createFlowButton = new JButton("Create Flow");
        cancelButton = new JButton("Cancel");
        
        // Abilita/disabilita pulsanti in base alla selezione
        stepsList.addListSelectionListener(e -> {
            boolean hasSelection = !stepsList.isSelectionEmpty();
            removeStepButton.setEnabled(hasSelection);
            editStepButton.setEnabled(hasSelection);
        });
        
        // Azioni dei pulsanti
        addStepButton.addActionListener(e -> showAddStepDialog());
        removeStepButton.addActionListener(e -> removeSelectedStep());
        editStepButton.addActionListener(e -> editSelectedStep());
        createFlowButton.addActionListener(e -> createFlow());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void layoutUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Pannello superiore per le informazioni del flow
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder("Flow Information")
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        infoPanel.add(new JLabel("Flow Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        infoPanel.add(flowNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        infoPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        infoPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Pannello centrale per gli step
        JPanel stepsPanel = new JPanel(new BorderLayout(5, 5));
        stepsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder("Flow Steps")
        ));
        
        JPanel stepsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stepsButtonPanel.add(addStepButton);
        stepsButtonPanel.add(removeStepButton);
        stepsButtonPanel.add(editStepButton);
        
        stepsPanel.add(stepsButtonPanel, BorderLayout.NORTH);
        stepsPanel.add(new JScrollPane(stepsList), BorderLayout.CENTER);
        
        // Pannello inferiore per i pulsanti principali
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(createFlowButton);
        buttonPanel.add(cancelButton);
        
        // Aggiungi tutto al frame
        add(infoPanel, BorderLayout.NORTH);
        add(stepsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void showAddStepDialog() {
        // Dialog per aggiungere un nuovo step
        StepDialog dialog = new StepDialog(this);
        dialog.setVisible(true);
        
        // Se uno step è stato creato, aggiungilo alla lista
        if (dialog.getCreatedStep() != null) {
            stepsModel.addElement(dialog.getCreatedStep());
        }
    }
    
    private void removeSelectedStep() {
        int selectedIndex = stepsList.getSelectedIndex();
        if (selectedIndex != -1) {
            stepsModel.remove(selectedIndex);
        }
    }
    
    private void editSelectedStep() {
        int selectedIndex = stepsList.getSelectedIndex();
        if (selectedIndex != -1) {
            MateFlowStep selectedStep = stepsModel.getElementAt(selectedIndex);
            
            // Dialog per modificare lo step esistente
            StepDialog dialog = new StepDialog(this, selectedStep);
            dialog.setVisible(true);
            
            // Se lo step è stato modificato, aggiorna la lista
            if (dialog.getCreatedStep() != null) {
                stepsModel.setElementAt(dialog.getCreatedStep(), selectedIndex);
            }
        }
    }
    
    private void createFlow() {
        String flowName = flowNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (flowName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a name for the flow.", 
                "Missing Information", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (stepsModel.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Flow has no steps. Create it anyway?",
                "Empty Flow",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        try {
            // Crea un nuovo MateFlow con gli step aggiunti
            MateFlow newFlow = new MateFlow(flowName, description);
            for (int i = 0; i < stepsModel.getSize(); i++) {
                newFlow.addCommand(stepsModel.getElementAt(i));
            }
            
            // Salva il flow tramite il manager
            MateFlowManager manager = appContext.getMateFlowManager();
            manager.saveMateFlow(newFlow);
            
            JOptionPane.showMessageDialog(this,
                "Flow '" + flowName + "' created successfully with " + stepsModel.getSize() + " steps.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error creating flow: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Dialog per aggiungere/modificare uno step
     */
    private class StepDialog extends JDialog {
        private final JComboBox<String> groupCombo;
        private final JComboBox<String> commandCombo;
        private final JTable paramsTable;
        private final DefaultTableModel paramsModel;
        private MateFlowStep createdStep;
        private final Map<String, CommandInfo> commandsMap;
        
        public StepDialog(JDialog parent) {
            this(parent, null);
        }
        
        public StepDialog(JDialog parent, MateFlowStep existingStep) {
            super(parent, existingStep == null ? "Add Step" : "Edit Step", true);
            setSize(600, 400);
            setLocationRelativeTo(parent);
            
            this.commandsMap = availableCommands;
            
            // Componenti per selezionare gruppo e comando
            groupCombo = new JComboBox<>(new String[]{"GIT", "WP"});
            
            // Lista dei comandi disponibili, filtrati per gruppo
            commandCombo = new JComboBox<>();
            commandCombo.setRenderer(new CommandRenderer());
            
            // Aggiorna i comandi quando cambia il gruppo
            groupCombo.addActionListener(e -> updateCommandsForSelectedGroup());
            
            // Tabella dei parametri per il comando selezionato
            String[] columnNames = {"Parameter", "Type", "Required", "Description", "Value"};
            paramsModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4; // Solo la colonna dei valori è modificabile
                }
            };
            
            paramsTable = new JTable(paramsModel);
            setupParamsTable();
            
            // Aggiorna i parametri quando cambia il comando
            commandCombo.addActionListener(e -> updateParamsForSelectedCommand());
            
            // Popola i dati se stiamo modificando uno step esistente
            if (existingStep != null) {
                populateFromExistingStep(existingStep);
            } else {
                updateCommandsForSelectedGroup();
            }
            
            // Layout
            setLayout(new BorderLayout(10, 10));
            
            JPanel selectionPanel = new JPanel(new GridBagLayout());
            selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            selectionPanel.add(new JLabel("Group:"), gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            selectionPanel.add(groupCombo, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0.0;
            selectionPanel.add(new JLabel("Command:"), gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            selectionPanel.add(commandCombo, gbc);
            
            JPanel paramsPanel = new JPanel(new BorderLayout());
            paramsPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
            paramsPanel.add(new JScrollPane(paramsTable), BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            
            okButton.addActionListener(e -> createStepFromForm());
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            
            add(selectionPanel, BorderLayout.NORTH);
            add(paramsPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
    private void setupParamsTable() {
        paramsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Parameter
        paramsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Type
        paramsTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Required
        paramsTable.getColumnModel().getColumn(3).setPreferredWidth(180); // Description
        paramsTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Value
        
        // Rendering personalizzato per le celle di "Required" - usando DefaultTableCellRenderer
        paramsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Boolean) {
                    setText(((Boolean)value) ? "Yes" : "No");
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });
        
        // Editor personalizzato per celle di tipo boolean
        paramsTable.setDefaultEditor(Boolean.class, new DefaultCellEditor(new JCheckBox()));
    }
        
        private void updateCommandsForSelectedGroup() {
            String selectedGroup = (String) groupCombo.getSelectedItem();
            commandCombo.removeAllItems();
            
            // Filtra i comandi per il gruppo selezionato
            List<String> filteredCommands = commandsMap.keySet().stream()
                .filter(cmd -> {
                    if ("GIT".equalsIgnoreCase(selectedGroup)) {
                        return cmd.startsWith("git-");
                    } else if ("WP".equalsIgnoreCase(selectedGroup)) {
                        return !cmd.startsWith("git-");
                    }
                    return false;
                })
                .sorted()
                .collect(Collectors.toList());
            
            // Aggiungi tutti i comandi filtrati
            for (String cmd : filteredCommands) {
                commandCombo.addItem(cmd);
            }
            
            if (commandCombo.getItemCount() > 0) {
                commandCombo.setSelectedIndex(0);
                updateParamsForSelectedCommand();
            } else {
                paramsModel.setRowCount(0);
            }
        }
        
        private void updateParamsForSelectedCommand() {
            paramsModel.setRowCount(0);
            
            String selectedCommand = (String) commandCombo.getSelectedItem();
            if (selectedCommand == null || selectedCommand.isEmpty()) {
                return;
            }
            
            CommandInfo cmdInfo = commandsMap.get(selectedCommand);
            if (cmdInfo != null && cmdInfo.getParameters() != null) {
                Map<String, ParamInfo> paramsArray = cmdInfo.getParameters();
                for (ParamInfo param : paramsArray.values()) {
                    paramsModel.addRow(new Object[]{
                        param.getName(),
                        param.getType(),
                        param.isRequired(),
                        param.getDescription(),
                        param.getDefaultValue()
                    });
                }
            }
        }
        
        private void populateFromExistingStep(MateFlowStep step) {
            String group = step.getGroup();
            String command = step.getCommand();
            Map<String, Object> params = step.getParametes();
            
            // Imposta il gruppo
            groupCombo.setSelectedItem(group);
            
            // Aggiorna i comandi disponibili e seleziona quello corretto
            updateCommandsForSelectedGroup();
            commandCombo.setSelectedItem(command);
            
            // Aggiorna i parametri e imposta i valori salvati
            updateParamsForSelectedCommand();
            if (params != null) {
                for (int i = 0; i < paramsModel.getRowCount(); i++) {
                    String paramName = (String) paramsModel.getValueAt(i, 0);
                    if (params.containsKey(paramName)) {
                        paramsModel.setValueAt(params.get(paramName), i, 4);
                    }
                }
            }
        }
        
        private void createStepFromForm() {
            String group = (String) groupCombo.getSelectedItem();
            String command = (String) commandCombo.getSelectedItem();
            
            if (command == null || command.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a command.", 
                    "Missing Command", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Crea una mappa con tutti i parametri e valori
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < paramsModel.getRowCount(); i++) {
                String paramName = (String) paramsModel.getValueAt(i, 0);
                Object paramValue = paramsModel.getValueAt(i, 4);
                boolean isRequired = (Boolean) paramsModel.getValueAt(i, 2);
                
                if (isRequired && (paramValue == null || paramValue.toString().isEmpty())) {
                    JOptionPane.showMessageDialog(this, 
                        "Required parameter '" + paramName + "' is missing.", 
                        "Missing Parameter", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (paramValue != null && !paramValue.toString().isEmpty()) {
                    params.put(paramName, paramValue);
                }
            }
            
            // Crea lo step con i parametri raccolti
            createdStep = new MateFlowStep(command, params, group);
            dispose();
        }
        
        public MateFlowStep getCreatedStep() {
            return createdStep;
        }
        
        /**
         * Renderer personalizzato per comandi
         */
        private class CommandRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                String command = (String) value;
                if (command != null && !command.isEmpty()) {
                    CommandInfo info = commandsMap.get(command);
                    if (info != null && info.getDescription() != null) {
                        setToolTipText(info.getDescription());
                    } else {
                        setToolTipText(command);
                    }
                }
                
                return this;
            }
        }
    }
    
    /**
     * Renderer personalizzato per gli step nella lista
     */
    private class StepListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof MateFlowStep) {
                MateFlowStep step = (MateFlowStep) value;
                setText((index + 1) + ". " + step.getGroup() + " - " + step.getCommand());
                
                Map<String, Object> params = step.getParametes();
                StringBuilder tooltip = new StringBuilder("<html><b>")
                    .append(step.getGroup())
                    .append(" - ")
                    .append(step.getCommand())
                    .append("</b>");
                
                if (params != null && !params.isEmpty()) {
                    tooltip.append("<br>Parameters:<br>");
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        tooltip.append(" - ")
                            .append(entry.getKey())
                            .append(": ")
                            .append(entry.getValue())
                            .append("<br>");
                    }
                } else {
                    tooltip.append("<br>No parameters");
                }
                
                tooltip.append("</html>");
                setToolTipText(tooltip.toString());
            }
            
            return this;
        }
    }
    
    /**
     * Ottieni l'istanza del MateFlow creato
     */
    public MateFlow getCreatedFlow() {
        String flowName = flowNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (flowName.isEmpty() || stepsModel.isEmpty()) {
            return null;
        }
        
        MateFlow flow = new MateFlow(flowName, description);
        for (int i = 0; i < stepsModel.getSize(); i++) {
            flow.addCommand(stepsModel.getElementAt(i));
        }
        
        return flow;
    }
}