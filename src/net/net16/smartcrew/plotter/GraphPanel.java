/*
 * Copyright (C) 2020 Robert Hutter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.net16.smartcrew.plotter;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import net.net16.smartcrew.GraphPlotter;

/**
 *
 * @author Robert Hutter
 */
public class GraphPanel extends JPanel implements ComponentListener
{
    String name;
    Graph g;
    JPanel headerPanel;
    JPanel contentPanel;
    JLabel title;
    JTabbedPane options;
    JPanel xPanel;
    JPanel yPanel;
    JPanel gSettingsPanel;
    JSeparator js;
    JButton hideShowButton;
    JComboBox xAxisComboBox;
    JTextField xVariableTextField;
    JTextField xUnitTextField;
    JCheckBox xUseTimeAxisCheckBox;
    JComboBox yAxisComboBox;
    JTextField yVariableTextField;
    JTextField yUnitTextField;
    JComboBox graphLayoutComboBox;
    JButton graphClearButton;
    JComboBox xAxisScaleComboBox;
    JComboBox yAxisScaleComboBox;
    public GridBagConstraints constraints;
    DefaultTableModel processingModel;
    GraphPlotter graphPlotter;
    
    final public static int GRAPH_WIDTH = 400;
    final public static int GRAPH_HEIGHT = 200;
    
    {
        super.addComponentListener(this);
    }
    
    public GraphPanel(GraphPlotter graphPlotter, int gridy)
    {
        super(new BorderLayout());
        
        this.graphPlotter = graphPlotter;
        
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        constraints.gridx = 0;
        constraints.gridy = gridy;
        constraints.insets = new Insets(0, 0, 10, 5);
        
        // Setup headerPanel
        headerPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints bC = new GridBagConstraints();
        GridBagConstraints jsC = new GridBagConstraints();
        GridBagConstraints titleC = new GridBagConstraints();
        
        hideShowButton = new JButton("Hide");
        hideShowButton.setMargin(new Insets(2, 14, 2, 14));
        hideShowButton.addActionListener((ActionEvent e) ->
        {
            if(hideShowButton.getText().equals("Show"))
            {
                // Show
                contentPanel.setVisible(true);
                hideShowButton.setText("Hide");
            }
            else
            {
                // Hide
                contentPanel.setVisible(false);
                hideShowButton.setText("Show");
            }   
        });
        
        bC.gridx = 0;
        bC.gridy = 0;
        bC.anchor = GridBagConstraints.LINE_START;
        
        js = new JSeparator(SwingConstants.HORIZONTAL);
        
        jsC.gridx = 1;
        jsC.gridy = 0;
        jsC.anchor = GridBagConstraints.CENTER;
        jsC.fill = GridBagConstraints.HORIZONTAL;
        jsC.weightx = 0.1;
        jsC.insets = new Insets(0, 6, 0, 0);
        
        title = new JLabel();
        name = "Graph #" + Integer.toString(gridy + 1);
        title.setText(name);
        title.setFont(title.getFont().deriveFont(title.getFont().getStyle() | java.awt.Font.BOLD, title.getFont().getSize()+2));
        
        titleC.gridx = 0;
        titleC.gridy = 1;
        titleC.gridheight = 1;
        titleC.gridwidth = 1;
        titleC.anchor = GridBagConstraints.FIRST_LINE_START;
        titleC.insets = new Insets(10, 0, 0, 0);
        
        headerPanel.add(hideShowButton, bC);
        headerPanel.add(js, jsC);
        headerPanel.add(title, titleC);
        super.add(headerPanel, BorderLayout.PAGE_START);
        
        // Setup contentPanel
        contentPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gC = new GridBagConstraints();
        GridBagConstraints optionsC = new GridBagConstraints();
        
        g = new Graph();
        options = new JTabbedPane();
        
        g.setPreferredSize(new Dimension(GRAPH_WIDTH, GRAPH_HEIGHT));
        gC.gridx = 0;
        gC.gridy = 0;
        gC.gridwidth = 1;
        gC.gridheight = 1;
        gC.fill = GridBagConstraints.HORIZONTAL;
        gC.weightx = 0.1;
        gC.anchor = GridBagConstraints.LINE_START;
        gC.insets = new Insets(10, 0, 0, 10);
        
        options.setPreferredSize(new Dimension(200,200));
        optionsC.gridx = 1;
        optionsC.gridy = 0;
        optionsC.gridwidth = 1;
        optionsC.gridheight = 1;
        optionsC.anchor = GridBagConstraints.LINE_START;
        
        // Setup tabbed panes
        xPanel = new JPanel();
        yPanel = new JPanel();
        gSettingsPanel = new JPanel();
        
        // xPanel
        JPanel xPaddingPanel = new JPanel();
        xPaddingPanel.setLayout(new GridBagLayout());
        
        JLabel xAxisOptionsLabel = new JLabel("X-Axis setup", SwingConstants.CENTER);
        xAxisOptionsLabel.setFont(xAxisOptionsLabel.getFont().deriveFont(xAxisOptionsLabel.getFont().getStyle() | java.awt.Font.BOLD, xAxisOptionsLabel.getFont().getSize()+2));
        GridBagConstraints xpgcl = new GridBagConstraints();
        xpgcl.fill = GridBagConstraints.HORIZONTAL;
        xpgcl.weightx = 0.1;
        xpgcl.insets = new Insets(0, 0, 10, 0);
        xPaddingPanel.add(xAxisOptionsLabel, xpgcl);
        
        GridBagConstraints xpgc = new GridBagConstraints();
        xpgc.gridy = 1;
        xpgc.fill = GridBagConstraints.HORIZONTAL;
        xpgc.weightx = 0.1;
        xPaddingPanel.add(xPanel, xpgc);
        
        xAxisComboBox = new JComboBox();
        xVariableTextField = new JTextField("x Axis");
        xUnitTextField = new JTextField("1");
        xUseTimeAxisCheckBox = new JCheckBox();
        xUseTimeAxisCheckBox.setText("Use time axis");
        
        xAxisComboBox.setPreferredSize(new Dimension(xAxisComboBox.getWidth(), 20));
        
        xAxisComboBox.addPopupMenuListener( new PopupMenuListener () {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                GraphPanel.this.updateXAxisComboBox();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        
        xVariableTextField.addActionListener((ActionEvent e) ->
        {
            g.setXAxisName(xVariableTextField.getText());
        });
        
        xUnitTextField.addActionListener((ActionEvent e) ->
        {
            g.setXUnitName(xUnitTextField.getText());
        });
        
        xUseTimeAxisCheckBox.addActionListener((ActionEvent e) ->
        {
            if (xUseTimeAxisCheckBox.isSelected())
            {
                xUnitTextField.setEnabled(false);
                xAxisComboBox.setEnabled(false);

                updateTimeAxisLabel();
            }
            else
            {
                xUnitTextField.setEnabled(true);
                xAxisComboBox.setEnabled(true);
            }
        });
        
        graphPlotter.timeAxisUnitComboBox.addActionListener((ActionEvent e) ->
        {
            updateTimeAxisLabel();
        });
        
        xAxisScaleComboBox = new JComboBox();
        xAxisScaleComboBox.addItem("1:1");
        xAxisScaleComboBox.addItem("1:10");
        xAxisScaleComboBox.addItem("1:100");
        xAxisScaleComboBox.addItem("1:1000");
        
        xAxisScaleComboBox.addActionListener((ActionEvent e) ->
        {
            switch ((String)xAxisScaleComboBox.getSelectedItem())
            {
                case "1:1":
                    g.setXScale(1);
                break;
                
                case "1:10":
                    g.setXScale(10);    
                break;
                
                case "1:100":
                    g.setXScale(100);
                break;
                
                case "1:1000":
                    g.setXScale(1000);
                break;
            }
        });
        
        JLabel xVariableLabel = new JLabel("Variable");
        JLabel xLabelLabel = new JLabel("Label");
        JLabel xUnitLabel = new JLabel("Unit");
        JLabel xAxisScaleLabel = new JLabel("Scale");
        
        GroupLayout xLayout = new GroupLayout(xPanel);
        xPanel.setLayout(xLayout);
        xLayout.setAutoCreateGaps(true);
        xLayout.setAutoCreateContainerGaps(true);
        
        xLayout.setHorizontalGroup(xLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(xLayout.createSequentialGroup()
                .addGroup(xLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(xVariableLabel)
                .addComponent(xAxisScaleLabel)
                .addComponent(xLabelLabel)
                .addComponent(xUnitLabel)
                )
                .addGroup(xLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(xAxisComboBox)
                .addComponent(xAxisScaleComboBox)
                .addComponent(xVariableTextField)
                .addComponent(xUnitTextField)
            ))
            .addComponent(xUseTimeAxisCheckBox)
        );
        
        xLayout.setVerticalGroup(xLayout.createSequentialGroup()
                    .addGroup(xLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xVariableLabel)
                    .addComponent(xAxisComboBox)
                    )
                    .addGroup(xLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xAxisScaleLabel)
                    .addComponent(xAxisScaleComboBox)
                    )
                    .addGroup(xLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xLabelLabel)
                    .addComponent(xVariableTextField)
                    )
                    .addGroup(xLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xUnitLabel)
                    .addComponent(xUnitTextField)
                    )
                    .addComponent(xUseTimeAxisCheckBox)
        );
        
        // yPanel
        JPanel yPaddingPanel = new JPanel();
        yPaddingPanel.setLayout(new GridBagLayout());
        
        JLabel yAxisOptionsLabel = new JLabel("Y-Axis setup", SwingConstants.CENTER);
        yAxisOptionsLabel.setFont(yAxisOptionsLabel.getFont().deriveFont(yAxisOptionsLabel.getFont().getStyle() | java.awt.Font.BOLD, yAxisOptionsLabel.getFont().getSize()+2));
        GridBagConstraints ypgcl = new GridBagConstraints();
        ypgcl.fill = GridBagConstraints.HORIZONTAL;
        ypgcl.weightx = 0.1;
        ypgcl.insets = new Insets(0, 0, 10, 0);
        yPaddingPanel.add(yAxisOptionsLabel, ypgcl);
        
        GridBagConstraints ypgc = new GridBagConstraints();
        ypgc.fill = GridBagConstraints.HORIZONTAL;
        ypgc.weightx = 0.1;
        ypgc.gridy = 1;
        yPaddingPanel.add(yPanel, ypgc);
        
        yAxisComboBox = new JComboBox();
        yVariableTextField = new JTextField("y Axis");
        yUnitTextField = new JTextField("1");
        
        yAxisComboBox.setPreferredSize(new Dimension(yAxisComboBox.getWidth(), 20));
        
        yAxisComboBox.addPopupMenuListener( new PopupMenuListener () {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                GraphPanel.this.updateYAxisComboBox();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        
        yVariableTextField.addActionListener((ActionEvent e) ->
        {
            g.setYAxisName(xVariableTextField.getText());
        });
        
        yUnitTextField.addActionListener((ActionEvent e) ->
        {
            g.setYUnitName(yUnitTextField.getText());
        });
        
        yAxisScaleComboBox = new JComboBox();
        yAxisScaleComboBox.addItem("auto");
        yAxisScaleComboBox.addItem("1:1");
        yAxisScaleComboBox.addItem("1:5");
        yAxisScaleComboBox.addItem("1:10");
        yAxisScaleComboBox.addItem("1:25");
        yAxisScaleComboBox.addItem("1:50");
        yAxisScaleComboBox.addItem("1:100");
        
        yAxisScaleComboBox.addActionListener((ActionEvent e) ->
        {
            switch ((String)yAxisScaleComboBox.getSelectedItem())
            {
                case "1:1":
                    g.setYScale(1);
                break;
                
                case "1:5":
                    g.setYScale(5);
                break;
                
                case "1:10":
                    g.setYScale(10);
                break;
                
                case "1:25":
                    g.setYScale(25);
                break;
                
                case "1:50":
                    g.setYScale(50);
                break;
                
                case "1:100":
                    g.setYScale(100);
                break;
                
                default:
                    g.setYScale(Graph.AUTO); // Auto
                break;
            }
        });
        
        JLabel yVariableLabel = new JLabel("Variable");
        JLabel yLabelLabel = new JLabel("Label");
        JLabel yUnitLabel = new JLabel("Unit");
        JLabel yAxisScaleLabel = new JLabel("Scale");
        
        GroupLayout yLayout = new GroupLayout(yPanel);
        yPanel.setLayout(yLayout);
        yLayout.setAutoCreateGaps(true);
        yLayout.setAutoCreateContainerGaps(true);
        
        yLayout.setHorizontalGroup(yLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(yLayout.createSequentialGroup()
                .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(yVariableLabel)
                .addComponent(yAxisScaleLabel)
                .addComponent(yLabelLabel)
                .addComponent(yUnitLabel)
                )
                .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(yAxisComboBox)
                .addComponent(yAxisScaleComboBox)
                .addComponent(yVariableTextField)
                .addComponent(yUnitTextField)
            ))
        );
        
        yLayout.setVerticalGroup(yLayout.createSequentialGroup()
                    .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(yVariableLabel)
                    .addComponent(yAxisComboBox)
                    )
                    .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(yAxisScaleLabel)
                    .addComponent(yAxisScaleComboBox)
                    )
                    .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(yLabelLabel)
                    .addComponent(yVariableTextField)
                    )
                    .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(yUnitLabel)
                    .addComponent(yUnitTextField)
                    )
        );
        
        // gSettings
        JPanel gSettingsPaddingPanel = new JPanel();
        gSettingsPaddingPanel.setLayout(new GridBagLayout());
        
        JLabel gSettingsLabel = new JLabel("Graph setup", SwingConstants.CENTER);
        gSettingsLabel.setFont(gSettingsLabel.getFont().deriveFont(yAxisOptionsLabel.getFont().getStyle() | java.awt.Font.BOLD, gSettingsLabel.getFont().getSize()+2));
        GridBagConstraints gspgcl = new GridBagConstraints();
        gspgcl.fill = GridBagConstraints.HORIZONTAL;
        gspgcl.weightx = 0.1;
        gspgcl.insets = new Insets(0, 0, 10, 0);
        gSettingsPaddingPanel.add(gSettingsLabel, gspgcl);
        
        GridBagConstraints gspgc = new GridBagConstraints();
        gspgc.fill = GridBagConstraints.HORIZONTAL;
        gspgc.weightx = 0.1;
        gspgc.gridy = 1;
        gSettingsPaddingPanel.add(gSettingsPanel, gspgc);
        
        graphLayoutComboBox = new JComboBox();
        graphLayoutComboBox.addItem("auto");
        graphLayoutComboBox.addItem("positive");
        graphLayoutComboBox.addItem("negative");
        graphLayoutComboBox.addItem("both");
        
        graphClearButton = new JButton("Clear");
        
        graphLayoutComboBox.addActionListener((ActionEvent e) ->
        {
            switch ((String)graphLayoutComboBox.getSelectedItem())
            {
                case "auto":
                    g.setGraphLayout(Graph.AUTO);
                break;
                
                case "positive":
                    g.setGraphLayout(Graph.POSITIVE);
                break;
                
                case "negative":
                    g.setGraphLayout(Graph.NEGATIVE);
                break;
                
                case "both":
                    g.setGraphLayout(Graph.BOTH);
                break;
            }
        });
        
        graphClearButton.addActionListener((ActionEvent e) ->
        {
            g.clear();
        });
        
        JLabel graphLayoutLabel = new JLabel("Layout");
        JLabel graphClearLabel = new JLabel("Clear graph");
        
        GroupLayout gLayout = new GroupLayout(gSettingsPanel);
        gSettingsPanel.setLayout(gLayout);
        gLayout.setAutoCreateGaps(true);
        gLayout.setAutoCreateContainerGaps(true);
        
        gLayout.setHorizontalGroup(gLayout.createSequentialGroup()
                .addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(graphLayoutLabel)
                        .addComponent(graphClearLabel)
                )
                .addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(graphLayoutComboBox)
                        .addComponent(graphClearButton)
                )
        );
        
        gLayout.setVerticalGroup(gLayout.createSequentialGroup()
                .addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(graphLayoutLabel)
                        .addComponent(graphLayoutComboBox)
                        
                )
                .addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(graphClearLabel)
                        .addComponent(graphClearButton)
                )
        );
        
        options.addTab("x-Axis", xPaddingPanel);
        options.addTab("y-Axis", yPaddingPanel);
        options.addTab("Settings", gSettingsPaddingPanel);
        
        contentPanel.add(g, gC);
        contentPanel.add(options, optionsC);
        
        this.processingModel = graphPlotter.processingModel;
        updateXAxisComboBox();
        updateYAxisComboBox();
        
        super.add(contentPanel, BorderLayout.PAGE_END);
    }
    
    /**
     * 
     */
    public void updateVariableComboBoxes()
    {
       updateXAxisComboBox();
       updateYAxisComboBox();
    }
    
    final void updateXAxisComboBox()
    {
        String selected = (String) xAxisComboBox.getSelectedItem();
        int selectedIndex = -1;
        
        xAxisComboBox.removeAllItems();
        for (int i = 0; i < processingModel.getRowCount(); i++)
        {
            xAxisComboBox.addItem((String) processingModel.getValueAt(i, GraphPlotter.VARIABLE_NAME_COL));
            if (((String)xAxisComboBox.getItemAt(i)).equals(selected))
            {
                selectedIndex = i;
            }
        }
        
        if (selectedIndex >= 0)
        {
            xAxisComboBox.setSelectedIndex(selectedIndex);
        }
    }
    
    final void updateYAxisComboBox()
    {
        String selected = (String) yAxisComboBox.getSelectedItem();
        int selectedIndex = -1;
        
        yAxisComboBox.removeAllItems();
        for (int i = 0; i < processingModel.getRowCount(); i++)
        {
            yAxisComboBox.addItem((String) processingModel.getValueAt(i, GraphPlotter.VARIABLE_NAME_COL));
            if (((String)yAxisComboBox.getItemAt(i)).equals(selected))
            {
                selectedIndex = i;
            }
        }
        
        
        if (selectedIndex >= 0)
        {
            yAxisComboBox.setSelectedIndex(selectedIndex);
        }
    }
    
    public void updateTimeAxisLabel()
    {
        if (xUseTimeAxisCheckBox.isSelected())
        {
            g.setXUnitName(graphPlotter.getClockUnit());
            this.xUnitTextField.setText(graphPlotter.getClockUnit());
        }
    }
    
    /**
     * 
     * @return 
     */
    public String getGraphName()
    {
        return this.name;
    }
    
    /**
     * 
     * @param name 
     */
    public void setGraphName(String name)
    {
        this.name = name;
        title.setText(name);
    }
    
    /**
     * 
     * @param i 
     */
    public void updatePosition(int i)
    {
        setGraphName("Graph #" + Integer.toString(i + 1));
        constraints.gridy = i;
    }
    
    /**
     *  Puts data on graph
     * 
     * @param x
     * @param y 
     */
    public synchronized void putData(int x, int y)
    {
        g.put(x, y);
    }
    
    /**
     * Puts data on graph. Y value taken from clock.
     * 
     * @param y 
     */
    public synchronized void putData(int y)
    {
        if (xUseTimeAxisCheckBox.isSelected())
        {
            g.put((int)graphPlotter.getTimeXValue(), y);
        }
    }
    
    /**
     * Clears data from graph.
     */
    public void clearData()
    {
        g.clear();
    }
    
    public synchronized void updateGraphics()
    {
        g.repaint();
    }
    
    public synchronized int getTimeAxisX()
    {
        return (int) graphPlotter.getTimeXValue();
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        this.updateGraphics();
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
        
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
        
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        
    }
}

class Graph extends JComponent implements ComponentListener
{
    Axis x;
    Axis y;
    
    int xOrigin;
    int yOrigin;
    
    int layout = Graph.AUTO;

    int xScale;
    int yScale;
    
    final static int PADDING = 20;
    final static BasicStroke axisStroke;
    final static BasicStroke gridStroke;
    final int DATA_HEIGHT; // Height of data display area, CONSTANT
    int data_width; // Width of data display area, DYNAMIC
    final static int GRID_DISTANCE = 10; // Distance between grid lines
    
    ArrayList<Integer> xdata;
    ArrayList<Integer> ydata;
    
    // Layout constants
    public final static int AUTO = 0;
    public final static int POSITIVE = 1;
    public final static int NEGATIVE = 2;
    public final static int BOTH = 3;
    
    static
    {
        axisStroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    }
    
    {
        super.addComponentListener(this);
        DATA_HEIGHT = GraphPanel.GRAPH_HEIGHT - 2 * PADDING - 2 ; // height - 2*padding - axis width
        data_width = GraphPanel.GRAPH_WIDTH - 2 * PADDING - 2; // width - 2*padding - axis width
        xdata = new ArrayList<>();
        ydata = new ArrayList<>();
    }
    
    /**
     * Constructor for class Graph. Builds a Graph out of two Axis.
     * Uses layout as specified. Uses scale as specified.
     * 
     * @param x
     * @param y
     * @param layout 
     * @param xScale
     * @param yScale
     */
    public Graph (Axis x, Axis y, int layout, int xScale, int yScale)
    {
        this.x = x;
        this.y = y;
        this.layout = layout;
        this.xScale = xScale;
        this.yScale = yScale;
    }
    
    /**
     * Constructor for class Graph. Builds a Graph out of two Axis.
     * Layout will be set to default: AUTO.
     * X Axis scale will be set to default: 1.
     * Y Axis scale will be set to default: AUTO.
     * 
     * @param x X Axis
     * @param y Y Axis
     */
    public Graph(Axis x, Axis y)
    {
        this(x, y, Graph.AUTO, 1, Graph.AUTO);
    }
    
    /**
     * Constructor for Graph. Axis will be named "x Axis" and "y Axis".
     * Unit will be set to default: "1".
     * Layout will be set to default: AUTO.
     * Scale will be set to default: 1.
     */
    public Graph()
    {
        this(new Axis("x Axis", "1", "1"), new Axis("y Axis", "1", "1"));
    }
    
    /**
     * Renders graph (axis, labels and data).
     * 
     * @param g 
     */
    @Override
    public synchronized void paintComponent(Graphics g)
    {
        int axisLayout = this.layout;
        
        // If layout auto, then check ydata
        if (layout == Graph.AUTO || (layout != Graph.POSITIVE && layout != Graph.NEGATIVE && layout != Graph.BOTH))
        {
            // Check if contains negative / is all positive
            boolean hasNegative = false;
            for (int i = 0; i < ydata.size(); i++)
            {
                if (ydata.get(i) < 0)
                {
                    hasNegative = true;
                    break;
                }
            }
            
            // Check to see if all negative
            if (hasNegative)
            {
                boolean hasPositive = false;
                for (int i = 0; i < ydata.size(); i++)
                {
                    if (ydata.get(i) > 0)
                    {
                        hasPositive = true;
                        break;
                    }
                }
                
                if (hasPositive)
                {
                    axisLayout = Graph.BOTH;
                }
                else
                {
                    axisLayout = Graph.NEGATIVE;
                }
            }
            else
            {
                axisLayout = Graph.POSITIVE;
            }
        }
        
        // If scale auto (scale == 0), find right scale
        float actualYScale = this.yScale;
        if (this.yScale == Graph.AUTO)
        {
            actualYScale = 1.0f;
            switch (axisLayout)
            {
                case Graph.POSITIVE:
                    for (int i = 0; i < ydata.size(); i++)
                    {
                        if (ydata.get(i) * (GRID_DISTANCE/actualYScale) > DATA_HEIGHT)
                        {
                            actualYScale++;
                            i--;
                        }
                    }
                break;

                case Graph.NEGATIVE:
                    for (int i = 0; i < ydata.size(); i++)
                    {
                        if (ydata.get(i) * (GRID_DISTANCE/actualYScale) < (-1*DATA_HEIGHT))
                        {
                            actualYScale++;
                            i--;
                        }
                    }
                break;

                case Graph.BOTH:
                    for (int i = 0; i < ydata.size(); i++)
                    {
                        if (ydata.get(i) * (GRID_DISTANCE/actualYScale) > DATA_HEIGHT/2 || ydata.get(i) * (GRID_DISTANCE/actualYScale) < (-1*DATA_HEIGHT/2))
                        {
                            actualYScale++;
                            i--;
                        }
                    }
                break;
            }
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        final AffineTransform normalTransform = g2d.getTransform();
        
        // Render grid
        g2d.setStroke(gridStroke);
        g2d.setColor(Color.LIGHT_GRAY);
        
        for (int i = PADDING; i <= this.getWidth()-PADDING; i = i + GRID_DISTANCE) // vertical
        {
            g2d.drawLine(i, PADDING, i, this.getHeight()-PADDING);
        }
        
        for (int j = PADDING; j <= this.getHeight()-PADDING; j = j + GRID_DISTANCE) // horizontal
        {
            g2d.drawLine(PADDING, j, this.getWidth()-PADDING, j);
        }
        
        // Render axis based on layout
        switch (axisLayout)
        {
            case Graph.POSITIVE:
                xOrigin = PADDING;
                yOrigin = this.getHeight()-PADDING;

                // Draw axis
                g2d.setColor(Color.black);
                g2d.setStroke(axisStroke);
                g2d.drawLine(xOrigin, yOrigin, xOrigin, PADDING); // yAxis
                g2d.drawLine(xOrigin, yOrigin, this.getWidth()-PADDING, yOrigin); // xAxis
                // xAxis cap
                g2d.drawLine(this.getWidth()-PADDING-8, yOrigin+6, this.getWidth()-PADDING, yOrigin);
                g2d.drawLine(this.getWidth()-PADDING-8, yOrigin-6, this.getWidth()-PADDING, yOrigin);
                // yAxis cap
                g2d.drawLine(xOrigin-6, PADDING+8, xOrigin, PADDING);
                g2d.drawLine(xOrigin, PADDING, xOrigin+6, PADDING+8);
                // xAxis label
                g2d.drawString(x.getName(), (this.getWidth()-2*PADDING)/2, (this.getHeight()-PADDING/2)+5);
                // xAxis unit
                g2d.drawString(x.getUnit(), (this.getWidth()-2*PADDING),(this.getHeight()-PADDING/2)+5);
                // yAxis unit
                g2d.rotate(Math.PI/2);
                g2d.drawString(y.getUnit(), (1.5f*PADDING), -(PADDING/2-5));
                //g2d.drawString(y.getUnit(), this.getWidth()/2, this.getHeight()/2);
                // yAxis label
                g2d.drawString(y.getName(), (this.getHeight()-2*PADDING)/2, -(PADDING/2-5));
                g2d.setTransform(normalTransform);
                
                // Render scale
                g2d.setStroke(gridStroke);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(Integer.toString((int)actualYScale), // y Axis
                    xOrigin - (10 + ((Integer.toString((int)actualYScale).length() >= 4 ? 3 : Integer.toString((int)actualYScale).length())-1)*5),
                    yOrigin - GRID_DISTANCE + 2.5f
                );
                g2d.drawString(Integer.toString((int)xScale), // x Axis
                    xOrigin + GRID_DISTANCE - 2.5f * Integer.toString((int)xScale).length(),
                    yOrigin + 12.5f
                );
            break;
            
            case Graph.NEGATIVE:
                xOrigin = PADDING;
                yOrigin = PADDING;

                // Draw axis
                g2d.setColor(Color.black);
                g2d.setStroke(axisStroke);
                g2d.drawLine(xOrigin, yOrigin, xOrigin, this.getHeight()-PADDING); // yAxis
                g2d.drawLine(xOrigin, yOrigin, this.getWidth()-PADDING, yOrigin); // xAxis
                // xAxis cap
                g2d.drawLine(this.getWidth()-PADDING-8, yOrigin+6, this.getWidth()-PADDING, yOrigin);
                g2d.drawLine(this.getWidth()-PADDING-8, yOrigin-6, this.getWidth()-PADDING, yOrigin);
                // yAxis cap
                g2d.drawLine(xOrigin-6, this.getHeight()-PADDING-8, xOrigin, this.getHeight()-PADDING);
                g2d.drawLine(xOrigin, this.getHeight()-PADDING, xOrigin+6, this.getHeight()-PADDING-8);
                // xAxis label
                g2d.drawString(x.getName(), (this.getWidth()-2*PADDING)/2, (PADDING/2)+5);
                // xAxis unit
                g2d.drawString(x.getUnit(), (this.getWidth()-2*PADDING),(PADDING/2)+5);
                // yAxis unit
                g2d.rotate(Math.PI/2);
                g2d.drawString(y.getUnit(), (this.getHeight() - 2.0f*PADDING), -(PADDING/2-5));
                //g2d.drawString(y.getUnit(), this.getWidth()/2, this.getHeight()/2);
                // yAxis label
                g2d.drawString(y.getName(), (this.getHeight()-2*PADDING)/2, -(PADDING/2-5));
                g2d.setTransform(normalTransform);
                
                // Render scale
                g2d.setStroke(gridStroke);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(Integer.toString((int) actualYScale), // y Axis
                    xOrigin - (10 + ((Integer.toString((int) actualYScale).length() >= 4 ? 3 : Integer.toString((int) actualYScale).length())-1)*5),
                    yOrigin + GRID_DISTANCE + 2.5f
                );
                g2d.drawString(Integer.toString((int) xScale), // x Axis
                    xOrigin + GRID_DISTANCE - 2.5f * Integer.toString((int) xScale).length(),
                    yOrigin - 4.0f
                );
            break;
            
            case Graph.BOTH:
                xOrigin = PADDING;
                yOrigin = this.getHeight()/2;

                // Draw axis
                g2d.setColor(Color.black);
                g2d.setStroke(axisStroke);
                g2d.drawLine(xOrigin, PADDING, xOrigin, this.getHeight()-PADDING); // yAxis
                g2d.drawLine(xOrigin, yOrigin, this.getWidth()-PADDING, yOrigin); // xAxis
                // xAxis cap
                g2d.drawLine(this.getWidth()-PADDING-8, yOrigin+6, this.getWidth()-PADDING, yOrigin);
                g2d.drawLine(this.getWidth()-PADDING-8, yOrigin-6, this.getWidth()-PADDING, yOrigin);
                // yAxis cap
                g2d.drawLine(xOrigin-6, PADDING+8, xOrigin, PADDING); // Top
                g2d.drawLine(xOrigin, PADDING, xOrigin+6, PADDING+8); 
                g2d.drawLine(xOrigin-6, this.getHeight()-PADDING-8, xOrigin, this.getHeight()-PADDING); // Bottom
                g2d.drawLine(xOrigin, this.getHeight()-PADDING, xOrigin+6, this.getHeight()-PADDING-8);
                // xAxis label
                g2d.drawString(x.getName(), (this.getWidth()-2*PADDING)/2, (this.getHeight()/2)+(PADDING/2)+5);
                // xAxis unit
                g2d.drawString(x.getUnit(), (this.getWidth()-2*PADDING),(this.getHeight()/2)+(PADDING/2)+5);
                // yAxis unit
                g2d.rotate(Math.PI/2);
                g2d.drawString(y.getUnit(), (this.getHeight() - 2.0f*PADDING), -(PADDING/2-5));
                //g2d.drawString(y.getUnit(), this.getWidth()/2, this.getHeight()/2);
                // yAxis label
                g2d.drawString(y.getName(), (this.getHeight()-2*PADDING)/2, -(PADDING/2-5));
                g2d.setTransform(normalTransform);
                
                // Render scale
                g2d.setStroke(gridStroke);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(Integer.toString((int) actualYScale), // y Axis
                    xOrigin - (10 + ((Integer.toString((int) actualYScale).length() >= 4 ? 3 : Integer.toString((int) actualYScale).length())-1)*5),
                    yOrigin - GRID_DISTANCE + 2.5f
                );
                g2d.drawString(Integer.toString((int) xScale), // x Axis
                    xOrigin + GRID_DISTANCE - 2.5f * Integer.toString((int) xScale).length(),
                    this.getHeight() - PADDING + 12.5f
                );
            break;
        }
        
        // Only render graphline if data available same for all layouts
        if (xdata.size() > 0)
        {
            // Check if all datapoints fit into the graph
            if ((xdata.get(xdata.size() - 1) - xdata.get(0)) * (GRID_DISTANCE/(float)xScale) > data_width) // True if they dont
            {
                for (int i = 0; i < xdata.size(); i++)
                {
                    if (((xdata.get(xdata.size() - 1) - xdata.get(i))) * (GRID_DISTANCE/(float)xScale) <= data_width)
                    {
                        break;
                    }
                    else
                    {
                        xdata.remove(0);
                        ydata.remove(0);
                        i--;
                    }
                }
            }

            // Convert x and y values to graphics coordinates
            int dxdata[] = new int[xdata.size()];
            int dydata[] = new int[ydata.size()];

            for (int i = 0; i < xdata.size(); i++)
            {
                dxdata[i] = (int) (xOrigin + (xdata.get(i) - xdata.get(0)) * (GRID_DISTANCE/(float)xScale));
                dydata[i] = (int) (yOrigin - ydata.get(i) * (GRID_DISTANCE/actualYScale));
                
                // Check y height
                if (dydata[i] < PADDING)
                {
                    dydata[i] = PADDING;
                }
                else if (dydata[i] > this.getHeight() - PADDING)
                {
                    dydata[i] = this.getHeight() - PADDING;
                }
            }

            g2d.setStroke(axisStroke);
            g2d.setColor(Color.blue);
            g2d.drawPolyline(dxdata, dydata, xdata.size());
        }
    }
    
    /**
     * Adds a point to the data buffer
     * 
     * @param x
     * @param y 
     */
    public synchronized void put(int x, int y)
    {
        xdata.add(x);
        ydata.add(y);
        if (layout == Graph.BOTH)
        {
            this.repaint();
        }
        else
        {
            this.repaint(PADDING, PADDING, data_width, DATA_HEIGHT);
        }
    }
    
    /**
     * Setter for x Axis name.
     * 
     * @param name Name of axis (to be displayed on graph).
     */
    public void setXAxisName(String name)
    {
        if (name.strip().equals(""))
        {
            name = "x Axis";
        }
        x.setName(name);
        this.repaint();
    }
    
    /**
     * Getter for x Axis name.
     * 
     * @return 
     */
    public String getXAxisName()
    {
        return x.getName();
    }
    
    /**
     * Setter for y Axis name.
     * 
     * @param name Name of Axis (to be displayed on graph).
     */
    public void setYAxisName(String name)
    {
        if (name.strip().equals(""))
        {
            name = "y Axis";
        }
        y.setName(name);
        this.repaint();
    }
    
    /**
     * Getter of y Axis name.
     * 
     * @return 
     */
    public String getYAxisName()
    {
        return y.getName();
    }
    
    /**
     * Setter of x Axis unit name.
     * 
     * @param name Unit name (to be displayed on graph).
     */
    public void setXUnitName(String name)
    {
        if (name.strip().equals(""))
        {
            name = "1";
        }
        x.setUnit(name);
        this.repaint();
    }
    
    /**
     * Setter of y Axis unit name.
     * 
     * @param name Unit name (to be displayed on graph).
     */
    public void setYUnitName(String name)
    {
        if (name.strip().equals(""))
        {
            name = "1";
        }
        y.setUnit(name);
        this.repaint();
    }
    
    /**
     * Getter of x Axis unit name.
     * 
     * @return 
     */
    public String getXAxisUnit()
    {
        return x.getUnit();
    }
    
    /**
     * Getter of y Axis unit name.
     * 
     * @return 
     */
    public String getYAxisUnit()
    {
        return y.getUnit();
    }
    
    /**
     * Setter of y axis scaling value.
     * Scaling value helps keep points visible (scales y value).
     * 
     * 0: auto scaling
     * 
     * @param scale
     * @return 
     */
    public void setYScale(int scale)
    {
        this.yScale = scale;
        this.repaint();
    }
    
    /**
     * Setter of x axis scaling value.
     * 
     * @param scale 
     */
    public void setXScale(int scale)
    {
        this.xScale = scale;
        this.repaint();
    }

    @Override
    public synchronized void componentResized(ComponentEvent e)
    {
        data_width = this.getWidth() - 2 * PADDING - 2; // width - 2*padding - axis width
        this.repaint(PADDING, PADDING, data_width, DATA_HEIGHT);
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
        this.repaint();
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
        
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
        
    }

    /**
     * Clears graph data
     */
    void clear()
    {
        xdata.clear();
        ydata.clear();
        this.repaint();
    }
    
    /**
     * Sets the layout of the graph.
     * 
     * Possible layouts:
     *    - Graph.AUTO Starts out in positive layout and switches based on data.
     *    - Graph.POSITIVE Constantly in positive layout. Negative numbers not displayed.
     *    - Graph.NEGATIVE Constantly in negative layout. Positive numbers not displayed.
     *    - Graph.BOTH Constantly in both layout, all numbers will be displayed (smaller area).
     * 
     * @param layout
     */
    void setGraphLayout(int layout)
    {
        this.layout = layout;
        this.repaint();
    }
}

class Axis
{
    String name;
    String variable;
    String unit;
    
    /**
     * Constructor of class Axis.
     * 
     * @param name Name of axis.
     * @param unit Unit of axis.
     * @param variable Processing variable name providing axis data.
     */
    public Axis(String name, String unit, String variable)
    {
        this.name = name;
        this.unit = unit;
        this.variable = variable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
}