/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.net16.smartcrew;

import com.fazecast.jSerialComm.SerialPortEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.net16.smartcrew.plotter.GraphPanel;

/**
 *
 * @author Robert Hutter
 */
public class GraphPlotter extends javax.swing.JFrame
{
    public final static int VARIABLE_NAME_COL = 0;
    public final static int VARIABLE_POSITION_COL = 1;
    public final static int VARIABLE_SIZE_COL = 2;
    public final static int PROCESSING_RULE_COL = 3;
    public final static int EXAMPLE_VALUE_COL = 4;
    public final static int FORMATTED_VALUE_COL = 5;
    
    ArrayList<GraphPanel> graphs;
    DefaultTableModel processingModel;
    DefaultTableModel dataPacketModel;

    /**
     * Creates new form SerialPlotter
     */
    public GraphPlotter() {
        initComponents();
        
        super.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                GraphPlotter.this.setVisible(false);
            }
        });
        
        processingModel = (DefaultTableModel) processingTable.getModel();
        dataPacketModel = (DefaultTableModel) dataPacketModelTable.getModel();
        
        DefaultTableCellRenderer r1 = new DefaultTableCellRenderer();
        r1.setToolTipText("Variable position in data packet recieved over serial counting from 0.");
        processingTable.getColumnModel().getColumn(VARIABLE_POSITION_COL).setCellRenderer(r1);
        
        DefaultTableCellRenderer r2 = new DefaultTableCellRenderer();
        r2.setToolTipText("Size of number arriving in specified position of data packet (in bytes).");
        processingTable.getColumnModel().getColumn(VARIABLE_SIZE_COL).setCellRenderer(r2);
        
        DefaultTableCellRenderer r3 = new DefaultTableCellRenderer();
        r3.setToolTipText("Format code for processing incomming numbers before use.");
        processingTable.getColumnModel().getColumn(PROCESSING_RULE_COL).setCellRenderer(r3);
        
        DefaultTableCellRenderer r4 = new DefaultTableCellRenderer();
        r4.setToolTipText("Example input value to test processing rule. Transformed value visible in \"Formatted value\" column.");
        processingTable.getColumnModel().getColumn(EXAMPLE_VALUE_COL).setCellRenderer(r4);
        
        customValueDelimiterTextField.setVisible(false);
        
        graphs = new ArrayList<>();
        addGraph();
    }
    
    /**
     * 
     */
    private void addGraph()
    {
        GraphPanel gp = new GraphPanel(graphs.size());
        gp.attachProcessingTableModel(processingModel);
        graphs.add(gp);
        contentPanel.add(gp, gp.constraints);
        contentPanel.revalidate();
        refreshRemoveGraphComboBox();
    }
    
    /**
     * 
     */
    private void refreshRemoveGraphComboBox()
    {
        removeGraphComboBox.removeAllItems();
       
        for (int i = 0; i < graphs.size(); i++)
        {
            removeGraphComboBox.addItem(graphs.get(i).getGraphName());
}
            
        if (graphs.isEmpty())
        {
            removeGraphComboBox.addItem("Select");
        }
    }
    
    /**
     * 
     */
    private void updateDeleteVariableComboBox()
    {
        deleteVariableComboBox.removeAllItems();
        for (int i = 0; i < processingModel.getRowCount(); i++)
        {
            deleteVariableComboBox.addItem((String) processingModel.getValueAt(i, VARIABLE_NAME_COL));
        }
    }
    
    /**
     * 
     * @return 
     */
    private String getNewProcessingVariableName()
    {
        // Get index of var
        // 1. Get variable names
        String[] names = new String[processingModel.getRowCount()];
        for (int i = 0; i < names.length; i++)
        {
            names[i] = (String) processingModel.getValueAt(i, VARIABLE_NAME_COL);
        }
        
        // 2. Sort variable names
        for (int x = 0; x < names.length-1; x++)
        {
            for (int i = 0; i < names.length-1; i++)
            {
                if (names[i].compareToIgnoreCase(names[i+1]) > 0)
                {
                    String s = names[i];
                    names[i] = names[i+1];
                    names[i+1] = s;
                }
            }
        }
        
        // 3. Count variables in format "varX"
        int count = 0;
        for (String name : names)
        {
            if (name.equalsIgnoreCase("var"+(count+1))) {
                count++;
            }
        }
        
        return ("var"+(++count));
    }
    
    /**
     * 
     * @param format
     * @return 
     */
    double formatValue(double value, String format)
    {
        return 0;
    }
    
    public void SerialEvent (SerialPortEvent e)
    {
        // Parse data
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jProgressBar1 = new javax.swing.JProgressBar();
        optionsPanel = new javax.swing.JTabbedPane();
        toolsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        topAddGraphButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        removeGraphButton = new javax.swing.JButton();
        removeGraphComboBox = new javax.swing.JComboBox<>();
        preferencesPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        customValueDelimiterTextField = new javax.swing.JTextField();
        valueDelimiterComboBox = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        dataPacketPanel = new javax.swing.JPanel();
        dataPacketScrollPane = new javax.swing.JScrollPane();
        dataPacketModelTable = new javax.swing.JTable();
        packetLengthLabel = new javax.swing.JLabel();
        processingTablePanel = new javax.swing.JPanel();
        tableHeaderPanel = new javax.swing.JPanel();
        toggleTableButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        dataProcessingTableLabel = new javax.swing.JLabel();
        tablePanel = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        processingTable = new javax.swing.JTable();
        editTablePanel = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        newVariableButton = new javax.swing.JButton();
        deleteVariableButton = new javax.swing.JButton();
        deleteVariableComboBox = new javax.swing.JComboBox<>();
        contentScrollPane = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();
        footerPanel = new javax.swing.JPanel();
        bottomAddGraphButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        fileMenuCloseItem = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        alwaysOnTopMenuItem = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Serial Graph Plotter");
        setIconImage(new ImageIcon(getClass().getResource("/line_graph.png")).getImage());
        setMinimumSize(new java.awt.Dimension(850, 500));
        setPreferredSize(new java.awt.Dimension(750, 680));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+2));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Add or remove graphs");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        topAddGraphButton.setText("Add graph");
        topAddGraphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topAddGraphButtonActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD, jLabel2.getFont().getSize()+2));
        jLabel2.setText("Time axis");

        jLabel4.setText("Unit");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "hours (hrs)", "minutes (min)", "seconds (s)", "miliseconds (ms)", "microseconds (us)", "nanoseconds (ns)" }));

        jLabel5.setText("Current time (T+):");

        jButton2.setText("Start");

        jButton3.setText("Restart");
        jButton3.setEnabled(false);

        removeGraphButton.setText("Remove");
        removeGraphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeGraphButtonActionPerformed(evt);
            }
        });

        removeGraphComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        removeGraphComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                removeGraphComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });

        javax.swing.GroupLayout toolsPanelLayout = new javax.swing.GroupLayout(toolsPanel);
        toolsPanel.setLayout(toolsPanelLayout);
        toolsPanelLayout.setHorizontalGroup(
            toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(toolsPanelLayout.createSequentialGroup()
                        .addComponent(topAddGraphButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeGraphButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(removeGraphComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(toolsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addComponent(jLabel5))
                .addContainerGap(155, Short.MAX_VALUE))
        );
        toolsPanelLayout.setVerticalGroup(
            toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(toolsPanelLayout.createSequentialGroup()
                        .addGroup(toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(toolsPanelLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel1)
                                .addGap(10, 10, 10)
                                .addGroup(toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(topAddGraphButton)
                                    .addComponent(removeGraphButton)
                                    .addComponent(removeGraphComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(toolsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton2)
                                    .addComponent(jButton3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        optionsPanel.addTab("Tools", toolsPanel);

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD, jLabel3.getFont().getSize()+2));
        jLabel3.setText("Setup incomming data packet");

        jLabel7.setText("Value delimiter (char)");

        customValueDelimiterTextField.setColumns(1);
        customValueDelimiterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                customValueDelimiterTextFieldKeyTyped(evt);
            }
        });

        valueDelimiterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Comma ','", "Tab '\\t'", "Semi-colin ';'", "Newline '\\n'", "Carrige-return '\\r'", "Other" }));
        valueDelimiterComboBox.setMinimumSize(new java.awt.Dimension(108, 20));
        valueDelimiterComboBox.setPreferredSize(new java.awt.Dimension(108, 20));
        valueDelimiterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueDelimiterComboBoxActionPerformed(evt);
            }
        });

        jLabel8.setText("Decode charset can be set on Serial Monitor.");

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel9.setFont(jLabel9.getFont().deriveFont(jLabel9.getFont().getStyle() | java.awt.Font.BOLD, jLabel9.getFont().getSize()+2));
        jLabel9.setText("Model of incomming data packet");

        jLabel10.setText("Position");

        jLabel11.setText("Size");

        dataPacketPanel.setLayout(new javax.swing.BoxLayout(dataPacketPanel, javax.swing.BoxLayout.Y_AXIS));

        dataPacketScrollPane.setAlignmentX(0.0F);
        dataPacketScrollPane.setMaximumSize(new java.awt.Dimension(75, 50));
        dataPacketScrollPane.setPreferredSize(new java.awt.Dimension(75, 50));

        dataPacketModelTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "1", "1"}
            },
            new String [] {
                "0", "1", "2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataPacketModelTable.setEnabled(false);
        dataPacketModelTable.setFocusable(false);
        dataPacketModelTable.getTableHeader().setResizingAllowed(false);
        dataPacketModelTable.getTableHeader().setReorderingAllowed(false);
        dataPacketScrollPane.setViewportView(dataPacketModelTable);

        dataPacketPanel.add(dataPacketScrollPane);

        packetLengthLabel.setText("Packet length: 3 bytes");

        javax.swing.GroupLayout preferencesPanelLayout = new javax.swing.GroupLayout(preferencesPanel);
        preferencesPanel.setLayout(preferencesPanelLayout);
        preferencesPanelLayout.setHorizontalGroup(
            preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(preferencesPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(valueDelimiterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(customValueDelimiterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(preferencesPanelLayout.createSequentialGroup()
                        .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataPacketPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                .addComponent(packetLengthLabel)
                .addContainerGap())
        );
        preferencesPanelLayout.setVerticalGroup(
            preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preferencesPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dataPacketPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(packetLengthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(preferencesPanelLayout.createSequentialGroup()
                        .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(preferencesPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, preferencesPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(customValueDelimiterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(valueDelimiterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        optionsPanel.addTab("Preferences", preferencesPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(optionsPanel, gridBagConstraints);

        processingTablePanel.setPreferredSize(new java.awt.Dimension(726, 186));
        processingTablePanel.setLayout(new java.awt.BorderLayout());

        toggleTableButton.setText("Hide");
        toggleTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleTableButtonActionPerformed(evt);
            }
        });

        dataProcessingTableLabel.setFont(dataProcessingTableLabel.getFont().deriveFont(dataProcessingTableLabel.getFont().getStyle() | java.awt.Font.BOLD, dataProcessingTableLabel.getFont().getSize()+2));
        dataProcessingTableLabel.setText("Data processing table");
        dataProcessingTableLabel.setToolTipText("Table for setting up variables used for graph axises");

        javax.swing.GroupLayout tableHeaderPanelLayout = new javax.swing.GroupLayout(tableHeaderPanel);
        tableHeaderPanel.setLayout(tableHeaderPanelLayout);
        tableHeaderPanelLayout.setHorizontalGroup(
            tableHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableHeaderPanelLayout.createSequentialGroup()
                .addComponent(toggleTableButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1))
            .addComponent(dataProcessingTableLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
        );
        tableHeaderPanelLayout.setVerticalGroup(
            tableHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableHeaderPanelLayout.createSequentialGroup()
                .addGroup(tableHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toggleTableButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataProcessingTableLabel)
                .addContainerGap())
        );

        processingTablePanel.add(tableHeaderPanel, java.awt.BorderLayout.PAGE_START);

        tablePanel.setMinimumSize(new java.awt.Dimension(31, 134));
        tablePanel.setPreferredSize(new java.awt.Dimension(606, 134));
        tablePanel.setLayout(new java.awt.GridBagLayout());

        tableScrollPane.setBorder(null);

        processingTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"var1",  new Integer(0),  new Integer(1), null,  new Double(0.0),  new Double(0.0)},
                {"var2",  new Integer(1),  new Integer(1), null,  new Double(0.0),  new Double(0.0)},
                {"var3",  new Integer(2),  new Integer(1), null,  new Double(0.0),  new Double(0.0)}
            },
            new String [] {
                "Variable name", "Position in packet", "Variable size (bytes)", "Preprocessing rule", "Raw value (example)", "Formatted value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        processingTable.setShowGrid(false);
        processingTable.setShowHorizontalLines(true);
        processingTable.setShowVerticalLines(true);
        processingTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                processingTablePropertyChange(evt);
            }
        });
        tableScrollPane.setViewportView(processingTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        tablePanel.add(tableScrollPane, gridBagConstraints);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getStyle() | java.awt.Font.BOLD, jLabel6.getFont().getSize()+2));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Edit table");

        newVariableButton.setText("ADD new variable");
        newVariableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVariableButtonActionPerformed(evt);
            }
        });

        deleteVariableButton.setText("DELETE");
        deleteVariableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteVariableButtonActionPerformed(evt);
            }
        });

        deleteVariableComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "var1", "var2" }));
        deleteVariableComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                deleteVariableComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });

        javax.swing.GroupLayout editTablePanelLayout = new javax.swing.GroupLayout(editTablePanel);
        editTablePanel.setLayout(editTablePanelLayout);
        editTablePanelLayout.setHorizontalGroup(
            editTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editTablePanelLayout.createSequentialGroup()
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newVariableButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(editTablePanelLayout.createSequentialGroup()
                        .addComponent(deleteVariableButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteVariableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        editTablePanelLayout.setVerticalGroup(
            editTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator4)
            .addGroup(editTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(newVariableButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(editTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteVariableButton)
                    .addComponent(deleteVariableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        tablePanel.add(editTablePanel, gridBagConstraints);

        processingTablePanel.add(tablePanel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(processingTablePanel, gridBagConstraints);

        contentScrollPane.setBorder(null);

        contentPanel.setLayout(new java.awt.GridBagLayout());
        contentScrollPane.setViewportView(contentPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(contentScrollPane, gridBagConstraints);

        bottomAddGraphButton.setText("Add graph");
        bottomAddGraphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bottomAddGraphButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addComponent(bottomAddGraphButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE))
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bottomAddGraphButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(footerPanel, gridBagConstraints);

        fileMenu.setText("File");

        fileMenuCloseItem.setText("Close");
        fileMenuCloseItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuCloseItemActionPerformed(evt);
            }
        });
        fileMenu.add(fileMenuCloseItem);

        menuBar.add(fileMenu);

        windowMenu.setText("Window");

        alwaysOnTopMenuItem.setText("Always on top");
        alwaysOnTopMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysOnTopMenuItemActionPerformed(evt);
            }
        });
        windowMenu.add(alwaysOnTopMenuItem);

        menuBar.add(windowMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void toggleTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleTableButtonActionPerformed
        // TODO add your handling code here:
        if (toggleTableButton.getText().equals("Show"))
        {
            // Show
            tablePanel.setVisible(true);
            toggleTableButton.setText("Hide");
        }
        else
        {
            // Hide
            tablePanel.setVisible(false);
            toggleTableButton.setText("Show");
        }
    }//GEN-LAST:event_toggleTableButtonActionPerformed

    private void newVariableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVariableButtonActionPerformed
        Object[] o = new Object[processingModel.getColumnCount()];
        o[VARIABLE_NAME_COL] = getNewProcessingVariableName();
        o[EXAMPLE_VALUE_COL] = 0;
        o[FORMATTED_VALUE_COL] = 0;
        o[VARIABLE_POSITION_COL] = 0;
        o[VARIABLE_SIZE_COL] = 1;
        processingModel.addRow(o);
    }//GEN-LAST:event_newVariableButtonActionPerformed

    private void topAddGraphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topAddGraphButtonActionPerformed
        // TODO add your handling code here:
        addGraph();
    }//GEN-LAST:event_topAddGraphButtonActionPerformed

    private void bottomAddGraphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bottomAddGraphButtonActionPerformed
        // TODO add your handling code here:
        addGraph();
    }//GEN-LAST:event_bottomAddGraphButtonActionPerformed

    private void removeGraphComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_removeGraphComboBoxPopupMenuWillBecomeVisible
        // TODO add your handling code here:
        refreshRemoveGraphComboBox();
    }//GEN-LAST:event_removeGraphComboBoxPopupMenuWillBecomeVisible

    private void removeGraphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeGraphButtonActionPerformed
        // TODO add your handling code here:
        // Delete item
        for (int i = 0; i < graphs.size(); i++)
        {
            if (graphs.get(i).getGraphName().equals((String)removeGraphComboBox.getSelectedItem()))
            {
                graphs.remove(i);
                break;
            }
        }
        
        // Refresh list and GUI
        contentPanel.removeAll();
        for (int i = 0; i < graphs.size(); i++)
        {
            graphs.get(i).updatePosition(i);
            contentPanel.add(graphs.get(i), graphs.get(i).constraints);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
        
        refreshRemoveGraphComboBox();
    }//GEN-LAST:event_removeGraphButtonActionPerformed

    private void deleteVariableComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_deleteVariableComboBoxPopupMenuWillBecomeVisible
        // TODO add your handling code here:
        updateDeleteVariableComboBox();
    }//GEN-LAST:event_deleteVariableComboBoxPopupMenuWillBecomeVisible

    private void deleteVariableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVariableButtonActionPerformed
        // TODO add your handling code here:
        if (processingTable.getRowCount() <= 1)
        {
            return;
        }
        
        for (int i = 0; i < processingTable.getRowCount(); i++)
        {
            if(((String) processingModel.getValueAt(i, VARIABLE_NAME_COL)).equalsIgnoreCase((String) deleteVariableComboBox.getSelectedItem()))
            {
                processingModel.removeRow(i);
            }
        }
        updateDeleteVariableComboBox();
        
        graphs.forEach((gp) -> {
            gp.updateVariableComboBoxes();
        });
    }//GEN-LAST:event_deleteVariableButtonActionPerformed

    private void processingTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_processingTablePropertyChange
        // TODO add your handling code here:
        if (processingModel == null)
        {
            return;
        }
        
        for (int i = 0; i < processingModel.getRowCount(); i++)
        {
            // Dont allow for variable name to be left empty
            if (processingModel.getValueAt(i, VARIABLE_NAME_COL).equals(""))
            {
                processingModel.setValueAt(getNewProcessingVariableName(), i, VARIABLE_NAME_COL);
            }
            
            // Compute formatted value when example value or processing rule changed
            if (processingModel.getValueAt(i, EXAMPLE_VALUE_COL) != null)
            {
                try
                {
                    processingModel.setValueAt(
                        formatValue(
                                (double) processingModel.getValueAt(i, EXAMPLE_VALUE_COL),
                                (String) processingModel.getValueAt(i, PROCESSING_RULE_COL)
                        ),
                        i,
                        FORMATTED_VALUE_COL
                    );
                }
                catch (ClassCastException e)
                {
                    processingModel.setValueAt(
                        formatValue(
                                (int) processingModel.getValueAt(i, EXAMPLE_VALUE_COL),
                                (String) processingModel.getValueAt(i, PROCESSING_RULE_COL)
                        ),
                        i,
                        FORMATTED_VALUE_COL
                    );
                }
                
            }
            else
            {
                processingModel.setValueAt(0, i, EXAMPLE_VALUE_COL);
            }
            
            // Don't allow position in packet to be empty or negative
            if (processingModel.getValueAt(i, VARIABLE_POSITION_COL) != null)
            {
                if ((int) processingModel.getValueAt(i, VARIABLE_POSITION_COL) < 0)
                {
                    processingModel.setValueAt(0, i, VARIABLE_POSITION_COL);
                }
            }
            else
            {
                processingModel.setValueAt(0, i, VARIABLE_POSITION_COL);
            }
            
            // Don't allow number size to be empty or smaller equal to 0
            if (processingModel.getValueAt(i, VARIABLE_SIZE_COL) != null)
            {
                if ((int) processingModel.getValueAt(i, VARIABLE_SIZE_COL) <= 0)
                {
                    processingModel.setValueAt(1, i, VARIABLE_SIZE_COL);
                }
            }
            else
            {
                processingModel.setValueAt(1, i, VARIABLE_SIZE_COL);
            }
        }
        
        // Update data packet model table
        // 1. Get largest position in packet -> num of cols in data packet model
        int largest = 0;
        for (int e = 0; e < processingModel.getRowCount(); e++)
        {
            if ((int) processingModel.getValueAt(e, VARIABLE_POSITION_COL) > largest)
            {
                largest = (int) processingModel.getValueAt(e, VARIABLE_POSITION_COL);
            }
        }

        // 2. Build matrix of position and size
        int[][] data = new int[largest+1][2];
        for (int e = 0; e < processingModel.getRowCount(); e++)
        {
            int lpos = (int) processingModel.getValueAt(e, VARIABLE_POSITION_COL);
            data[lpos][0] = lpos;
            
            // Keep largest variable size
            if (data[lpos][1] <(int) processingModel.getValueAt(e, VARIABLE_SIZE_COL))
            {
                data[lpos][1] = (int) processingModel.getValueAt(e, VARIABLE_SIZE_COL);
            }
        }

        // 2.1. Fill empty spaces of matrix
        for (int e = 0; e < data.length; e++)
        {
            if (data[e][1] == 0)
            {
                data[e][0] = e;
                data[e][1] = 1;
            }
        }

        // 3. Sort matrix
        for (int x = 0; x < data.length-1; x++)
        {
            for (int y = 0; y < data[0].length-1; y++)
            {
                if (data[y+1][0] < data[y][0])
                {
                    int pos = data[y+1][0];
                    int size = data[y+1][1];
                    data[y+1][0] = data[y][0];
                    data[y+1][1] = data[y][1];
                    data[y][0] = pos;
                    data[y][1] = size;
                }
            }
        }

        // 4.2. Add data to packet model
        String[][] sData = new String[1][data.length];
        String[] colNames = new String[data.length];

        for (int e = 0; e < data.length; e++)
        {
            sData[0][e] = Integer.toString(data[e][1]);
            colNames[e] = Integer.toString(data[e][0]);
        }

        dataPacketModel = new DefaultTableModel(sData, colNames);
        dataPacketModelTable.setModel(dataPacketModel);
        dataPacketScrollPane.setMaximumSize(new java.awt.Dimension(25*data.length, dataPacketScrollPane.getHeight()));
        
        // 4.3. Calculate packet length and display it
        int sum = 0;
        for (int[] row : data)
        {
            sum += row[1];
        }
        packetLengthLabel.setText("Packet length: "+sum+" bytes");
        
        // Update varible combo boxes in graphs
        graphs.forEach((gp) -> {
            gp.updateVariableComboBoxes();
        });
        
        updateDeleteVariableComboBox();
    }//GEN-LAST:event_processingTablePropertyChange

    private void valueDelimiterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueDelimiterComboBoxActionPerformed
        // TODO add your handling code here:
        if (((String)valueDelimiterComboBox.getSelectedItem()).equals("Other"))
        {
            customValueDelimiterTextField.setVisible(true);
        }
        else
        {
            customValueDelimiterTextField.setVisible(false);
        }
    }//GEN-LAST:event_valueDelimiterComboBoxActionPerformed

    private void customValueDelimiterTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customValueDelimiterTextFieldKeyTyped
        // TODO add your handling code here:
        if ((customValueDelimiterTextField.getText() + evt.getKeyChar()).length() > 1)
        {
            evt.consume();
        }
    }//GEN-LAST:event_customValueDelimiterTextFieldKeyTyped

    private void fileMenuCloseItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuCloseItemActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_fileMenuCloseItemActionPerformed

    private void alwaysOnTopMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alwaysOnTopMenuItemActionPerformed
        // TODO add your handling code here:
        this.setAlwaysOnTop(alwaysOnTopMenuItem.isSelected());
    }//GEN-LAST:event_alwaysOnTopMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem alwaysOnTopMenuItem;
    private javax.swing.JButton bottomAddGraphButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane contentScrollPane;
    private javax.swing.JTextField customValueDelimiterTextField;
    private javax.swing.JTable dataPacketModelTable;
    private javax.swing.JPanel dataPacketPanel;
    private javax.swing.JScrollPane dataPacketScrollPane;
    private javax.swing.JLabel dataProcessingTableLabel;
    private javax.swing.JButton deleteVariableButton;
    private javax.swing.JComboBox<String> deleteVariableComboBox;
    private javax.swing.JPanel editTablePanel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fileMenuCloseItem;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton newVariableButton;
    private javax.swing.JTabbedPane optionsPanel;
    private javax.swing.JLabel packetLengthLabel;
    private javax.swing.JPanel preferencesPanel;
    private javax.swing.JTable processingTable;
    private javax.swing.JPanel processingTablePanel;
    private javax.swing.JButton removeGraphButton;
    private javax.swing.JComboBox<String> removeGraphComboBox;
    private javax.swing.JPanel tableHeaderPanel;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JButton toggleTableButton;
    private javax.swing.JPanel toolsPanel;
    private javax.swing.JButton topAddGraphButton;
    private javax.swing.JComboBox<String> valueDelimiterComboBox;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables
}
