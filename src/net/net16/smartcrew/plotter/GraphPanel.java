/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.net16.smartcrew.plotter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
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
        
        g.setPreferredSize(new Dimension(400, 200));
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
        xVariableTextField = new JTextField();
        xUnitTextField = new JTextField();
        xUseTimeAxisCheckBox = new JCheckBox();
        xUseTimeAxisCheckBox.setText("Use time axis");
        
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
        yVariableTextField = new JTextField();
        yUnitTextField = new JTextField();
        yUseTimeAxisCheckBox = new JCheckBox();
        yUseTimeAxisCheckBox.setText("Use time axis");
        
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
        
        options.addTab("x-Axis", xPaddingPanel);
        options.addTab("y-Axis", yPaddingPanel);
        
        contentPanel.add(g, gC);
        contentPanel.add(options, optionsC);
        
        super.add(contentPanel, BorderLayout.PAGE_END);
    }
    
    void updateGraph()
    {
        g.update();
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
    
    private void xUseTimeAxisCheckBoxActionPerformed(ActionEvent evt)
    {
        if (xUseTimeAxisCheckBox.isSelected())
        {
            xUnitTextField.setEnabled(false);
        }
        else
        {
            xUnitTextField.setEnabled(true);
        }
    }
    
    private void yUseTimeAxisCheckBoxActionPerformed(ActionEvent evt)
    {
        if (yUseTimeAxisCheckBox.isSelected())
        {
            yUnitTextField.setEnabled(false);
        }
        else
        {
            yUnitTextField.setEnabled(true);
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
}

class Graph extends JComponent implements Runnable
{
    Axis x;
    Axis y;
    
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
        this(new Axis("x Axis", null, null), new Axis("y Axis", null, null));
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    
    @Override
    public void run()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void update()
    {
        
    }
    
    public String getXAxisName()
    {
        return x.getName();
    }
    
    public String getYAxisName()
    {
        return y.getName();
    }
    
    public String getXAxisUnit()
    {
        return x.getUnit();
    }
    
    public String getYAxisUnit()
    {
        return y.getUnit();
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