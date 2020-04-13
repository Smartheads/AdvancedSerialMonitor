/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.net16.smartcrew.plotter;

/**
 *
 * @author Robert Hutter
 */
public class Graph implements Runnable
{
    public Graph()
    {
        
    }
    
    @Override
    public void run()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}