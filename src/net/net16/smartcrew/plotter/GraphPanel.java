/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.net16.smartcrew.plotter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Robert Hutter
 */
public class GraphPanel extends JPanel
{
    Graph g;
    JPanel headerPanel;
    JPanel contentPanel;
    JLabel title;
    JTabbedPane options;
    JPanel xPanel;
    JPanel yPanel;
    JSeparator js;
    JButton hideShowButton;
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
        constraints.insets = new Insets(0, 0, 10, 0);
        
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
        
        title = new JLabel("Graph #" + Integer.toString(gridy + 1));
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
        
        options.setPreferredSize(new Dimension(200,200));
        optionsC.gridx = 1;
        optionsC.gridy = 0;
        optionsC.gridwidth = 1;
        optionsC.gridheight = 1;
        optionsC.anchor = GridBagConstraints.LINE_START;
        
        // Setup tabbed pane
        
        xPanel = new JPanel();
        yPanel = new JPanel();
        
        options.addTab("x-Axis", xPanel);
        options.addTab("y-Axis", yPanel);
        
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