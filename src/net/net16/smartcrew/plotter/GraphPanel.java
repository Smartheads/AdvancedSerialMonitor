/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class GraphPanel extends JPanel
{
    String name;
    Graph g;
    JPanel headerPanel;
    JPanel contentPanel;
    JLabel title;
    JTabbedPane options;
    JPanel xPanel;
    JPanel yPanel;
    JPanel gSettings;
    JSeparator js;
    JButton hideShowButton;
    JComboBox xAxisComboBox;
    JTextField xVariableTextField;
    JTextField xUnitTextField;
    JCheckBox xUseTimeAxisCheckBox;
    JComboBox yAxisComboBox;
    JTextField yVariableTextField;
    JTextField yUnitTextField;
    JCheckBox yUseTimeAxisCheckBox;
    public GridBagConstraints constraints;
    DefaultTableModel processingModel;
    
    final public static int GRAPH_WIDTH = 400;
    final public static int GRAPH_HEIGHT = 200;
    
    public GraphPanel(int gridy)
    {
        super(new BorderLayout());
        
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.1;
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
        hideShowButton.addActionListener((ActionEvent e) -> {
            hideShowButtonActionPerformed(e);    
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
        gSettings = new JPanel();
        
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
                GraphPanel.this.xAxisComboBoxPopupMenuEventPerformed(e);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        
        xVariableTextField.addActionListener((ActionEvent e) -> {
            xVariableTextFieldActionPerformed(e);
        });
        
        xUnitTextField.addActionListener((ActionEvent e) -> {
            xUnitTextFieldActionPerformed(e);
        });
        
        xUseTimeAxisCheckBox.addActionListener((ActionEvent e) -> {
            xUseTimeAxisCheckBoxActionPerformed(e);
        });
        
        JLabel xVariableLabel = new JLabel("Variable");
        JLabel xLabelLabel = new JLabel("Label");
        JLabel xUnitLabel = new JLabel("Unit");
        
        GroupLayout xLayout = new GroupLayout(xPanel);
        xPanel.setLayout(xLayout);
        xLayout.setAutoCreateGaps(true);
        xLayout.setAutoCreateContainerGaps(true);
        
        xLayout.setHorizontalGroup(xLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(xLayout.createSequentialGroup()
                .addGroup(xLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(xVariableLabel)
                .addComponent(xLabelLabel)
                .addComponent(xUnitLabel)
                )
                .addGroup(xLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(xAxisComboBox)
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
        yUseTimeAxisCheckBox = new JCheckBox();
        yUseTimeAxisCheckBox.setText("Use time axis");
        
        yAxisComboBox.setPreferredSize(new Dimension(yAxisComboBox.getWidth(), 20));
        
        yAxisComboBox.addPopupMenuListener( new PopupMenuListener () {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                GraphPanel.this.yAxisComboBoxPopupMenuEventPerformed(e);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        
        yVariableTextField.addActionListener((ActionEvent e) -> {
            yVariableTextFieldActionPerformed(e);
        });
        
        yUnitTextField.addActionListener((ActionEvent e) -> {
            yUnitTextFieldActionPerformed(e);
        });
        
        yUseTimeAxisCheckBox.addActionListener((ActionEvent e) -> {
            yUseTimeAxisCheckBoxActionPerformed(e);
        });
        
        JLabel yVariableLabel = new JLabel("Variable");
        JLabel yLabelLabel = new JLabel("Label");
        JLabel yUnitLabel = new JLabel("Unit");
        
        GroupLayout yLayout = new GroupLayout(yPanel);
        yPanel.setLayout(yLayout);
        yLayout.setAutoCreateGaps(true);
        yLayout.setAutoCreateContainerGaps(true);
        
        yLayout.setHorizontalGroup(yLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(yLayout.createSequentialGroup()
                .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(yVariableLabel)
                .addComponent(yLabelLabel)
                .addComponent(yUnitLabel)
                )
                .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(yAxisComboBox)
                .addComponent(yVariableTextField)
                .addComponent(yUnitTextField)
            ))
            .addComponent(yUseTimeAxisCheckBox)
        );
        
        yLayout.setVerticalGroup(yLayout.createSequentialGroup()
                    .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(yVariableLabel)
                    .addComponent(yAxisComboBox)
                    )
                    .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(yLabelLabel)
                    .addComponent(yVariableTextField)
                    )
                    .addGroup(yLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(yUnitLabel)
                    .addComponent(yUnitTextField)
                    )
                    .addComponent(yUseTimeAxisCheckBox)
        );
        
        // gSettings
        
        
        
        options.addTab("x-Axis", xPaddingPanel);
        options.addTab("y-Axis", yPaddingPanel);
        //options.addTab("Settings", gSettings); NOT READY YET
        
        contentPanel.add(g, gC);
        contentPanel.add(options, optionsC);
        
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
    
    void updateXAxisComboBox()
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
    
    void updateYAxisComboBox()
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
    
    private void hideShowButtonActionPerformed(ActionEvent evt)
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
    }
    
    private void xAxisComboBoxPopupMenuEventPerformed(PopupMenuEvent e)
    {
        updateXAxisComboBox();
    }
    
    private void yAxisComboBoxPopupMenuEventPerformed(PopupMenuEvent e)
    {
        updateYAxisComboBox();
    }
    
    private void xVariableTextFieldActionPerformed(ActionEvent e)
    {
        g.setXAxisName(xVariableTextField.getText());
    }
    
    private void yVariableTextFieldActionPerformed(ActionEvent e)
    {
        g.setYAxisName(xVariableTextField.getText());
    }
    
    private void xUnitTextFieldActionPerformed(ActionEvent e)
    {
        g.setXUnitName(xUnitTextField.getText());
    }

    private void yUnitTextFieldActionPerformed(ActionEvent e)
    {
        g.setYUnitName(yUnitTextField.getText());
    }
    
    private void xUseTimeAxisCheckBoxActionPerformed(ActionEvent evt)
    {
        if (xUseTimeAxisCheckBox.isSelected())
        {
            xUnitTextField.setEnabled(false);
            xAxisComboBox.setEnabled(false);
        }
        else
        {
            xUnitTextField.setEnabled(true);
            xAxisComboBox.setEnabled(true);
        }
    }
    
    private void yUseTimeAxisCheckBoxActionPerformed(ActionEvent evt)
    {
        if (yUseTimeAxisCheckBox.isSelected())
        {
            yUnitTextField.setEnabled(false);
            yAxisComboBox.setEnabled(false);
        }
        else
        {
            yUnitTextField.setEnabled(true);
            yAxisComboBox.setEnabled(true);
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
     * 
     * @param m 
     */
    public void attachProcessingTableModel(DefaultTableModel m)
    {
        this.processingModel = m;
        updateXAxisComboBox();
        updateYAxisComboBox();
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
    
    public synchronized void updateGraphics()
    {
        g.repaint();
    }
}

class Graph extends JComponent implements ComponentListener
{
    Axis x;
    Axis y;
    
    int xOrigin;
    int yOrigin;
    
    final static int PADDING = 20;
    final static BasicStroke axisStroke;
    final int DATA_HEIGHT; // Height of data display area, CONSTANT
    int data_width; // Width of data display area, DYNAMIC
    
    ArrayList<Integer> xdata;
    ArrayList<Integer> ydata;
    
    static
    {
        axisStroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    }
    
    {
        super.addComponentListener(this);
        DATA_HEIGHT = GraphPanel.GRAPH_HEIGHT - 2 * PADDING - 2 ; // height - 2*padding - axis width
        data_width = GraphPanel.GRAPH_WIDTH - 2 * PADDING - 2; // width - 2*padding - axis width
        xdata = new ArrayList<>();
        ydata = new ArrayList<>();
    }
    
    /**
     * 
     * @param x
     * @param y 
     */
    public Graph(Axis x, Axis y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * 
     */
    public Graph()
    {
        this(new Axis("x Axis", "1", "1"), new Axis("y Axis", "1", "1"));
    }
    
    @Override
    public synchronized void paintComponent(Graphics g)
    {
        xOrigin = PADDING;
        yOrigin = this.getHeight()-PADDING;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        final AffineTransform normalTransform = g2d.getTransform();
        
        // Draw axis
        g2d.setColor(Color.black);
        g2d.setStroke(axisStroke);
        g2d.drawLine(xOrigin, yOrigin, PADDING, PADDING); // yAxis
        g2d.drawLine(xOrigin, yOrigin, this.getWidth()-PADDING, yOrigin); // xAxis
        // xAxis cap
        g2d.drawLine(xOrigin-6, PADDING+8, xOrigin, PADDING);
        g2d.drawLine(xOrigin, PADDING, xOrigin+6, PADDING+8);
        // yAxis cap
        g2d.drawLine(this.getWidth()-PADDING-8, yOrigin+6, this.getWidth()-PADDING, yOrigin);
        g2d.drawLine(this.getWidth()-PADDING-8, yOrigin-6, this.getWidth()-PADDING, yOrigin);
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
        
        g2d.setColor(Color.red);
        g2d.fillRect(PADDING + 2, PADDING, data_width, DATA_HEIGHT);
        g2d.setColor(Color.blue);
        
        // Check if all datapoints fit into the graph
        if (xdata.get(xdata.size() - 1) - xdata.get(0) > data_width) // True if they dont
        {
            for (int i = 0; i < xdata.size(); i++)
            {
                if (xdata.get(xdata.size() - 1) - xdata.get(i) <= data_width)
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
        
        // Convert x values to graphics coordinates
        int dxdata[] = new int[xdata.size()];
        int dydata[] = new int[ydata.size()];
        
        for (int i = 0; i < xdata.size(); i++)
        {
            dxdata[i] = xOrigin + xdata.get(i) - xdata.get(0);
            dydata[i] = yOrigin - ydata.get(i);
        }
        
        g2d.drawPolyline(dxdata, dydata, xdata.size());
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
        this.repaint(PADDING, PADDING, data_width, DATA_HEIGHT);
    }
    
    public void setXAxisName(String name)
    {
        if (name.strip().equals(""))
        {
            name = "x Axis";
        }
        x.setName(name);
        this.repaint();
    }
    
    public String getXAxisName()
    {
        return x.getName();
    }
    
    public void setYAxisName(String name)
    {
        if (name.strip().equals(""))
        {
            name = "y Axis";
        }
        y.setName(name);
        this.repaint();
    }
    
    public String getYAxisName()
    {
        return y.getName();
    }
    
    public void setXUnitName(String name)
    {
        if (name.strip().equals(""))
        {
            name = "1";
        }
        x.setUnit(name);
        this.repaint();
    }
    
    public void setYUnitName(String name)
    {
        if (name.strip().equals(""))
        {
            name = "1";
        }
        y.setUnit(name);
        this.repaint();
    }
    
    public String getXAxisUnit()
    {
        return x.getUnit();
    }
    
    public String getYAxisUnit()
    {
        return y.getUnit();
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
        
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
        
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
        
    }
}

class Axis
{
    String name;
    String variable;
    String unit;
    
    /**
     * 
     * @param name
     * @param unit
     * @param variable 
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