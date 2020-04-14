/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.net16.smartcrew.plotter;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Robert Hutter
 */
public class GraphPanel extends JPanel
{
    Graph g;
    JLabel title;
    JTabbedPane options;
    JPanel xPanel;
    JPanel yPanel;
    
    public GraphPanel()
    {
        super(new GridBagLayout());
        GridBagConstraints gC = new GridBagConstraints();
        GridBagConstraints titleC = new GridBagConstraints();
        GridBagConstraints optionsC = new GridBagConstraints();
        
        title = new JLabel("Graph #n");
        g = new Graph();
        options = new JTabbedPane();
        
        titleC.gridx = 0;
        titleC.gridy = 0;
        titleC.gridheight = 1;
        titleC.gridwidth = 1;
        titleC.anchor = GridBagConstraints.FIRST_LINE_START;
        
        g.setPreferredSize(new Dimension(400, 200));
        gC.gridx = 0;
        gC.gridy = 1;
        gC.gridwidth = 1;
        gC.gridheight = 1;
        gC.fill = GridBagConstraints.HORIZONTAL;
        gC.weightx = 0.1;
        gC.anchor = GridBagConstraints.LINE_START;
        
        options.setPreferredSize(new Dimension(200,200));
        optionsC.gridx = 1;
        optionsC.gridy = 1;
        optionsC.gridwidth = 1;
        optionsC.gridheight = 1;
        optionsC.anchor = GridBagConstraints.LINE_START;
        
        super.add(title, titleC);
        super.add(g, gC);
        super.add(options, optionsC);
        
        xPanel = new JPanel();
        yPanel = new JPanel();
        
        options.addTab("x-Axis", xPanel);
        options.addTab("y-Axis", yPanel);
    }
    
    void updateGraph()
    {
        g.update();
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