/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  Note: In this object there is a list of colors. There will always be a color
 *        located at 0, and 100. 
 * @author Scott
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.GradientPaint;
import java.awt.Insets;

public class ColorMap extends javax.swing.JPanel {
    
    ColorUnit[] list;
    Color noEscape;
    Dimension minSize;

    /**
     * Creates new form ColorMap
     */
    public ColorMap() {
        list = new ColorUnit[2];
        //list[0] = new ColorUnit(new Color(255,0,0), 0);
        list[1] = new ColorUnit(new Color(66, 37, 250), 100);
        list[0] = new ColorUnit(new Color(255, 255, 255), 0);

        noEscape = new Color(0,0,0);
        minSize = new Dimension(300,50);
        initComponents();
    }
        
    private class ColorUnit
    {
        //A class to hold color information
        //location is a placement on a 100 unit scale.
        public Color c;
        public int location;
        
        public ColorUnit(Color cin, int l)
        {
            c = cin;
            location = l;
        }
    }
    
    public Color getColor(int value, int max)
    {
        //normalise the value to a 100 unit scale
        int normalised = (value*100)/max;
        
        if(normalised == 100)
            return new Color(noEscape.getRGB());
        
        ColorUnit ColorH = list[1], ColorL = list[0];
        //for every color entry except the very last one
        for(int i = list.length - 1; i >= 0; i--)
        {
            //if the value is on a colorunit, return that color
            if(normalised == list[i].location)
                return new Color(list[i].c.getRGB());
            
            //otherwise find out which two units it falls between
            if(normalised > list[i].location)
            {
                ColorL = list[i];
                ColorH = list[i+1];
                break;
            }
        }        
                    
        //adjust value to position between those two colorunits
        double percent = (double)(normalised - ColorL.location)/(ColorH.location-ColorL.location);
        //System.out.println(ColorL.c.toString() + ": :" + ColorH.c.toString());
        double r = ColorL.c.getRed()*percent + ColorH.c.getRed()*(1-percent);
        double g = ColorL.c.getGreen()*percent + ColorH.c.getGreen()*(1-percent);
        double b = ColorL.c.getBlue()*percent + ColorH.c.getBlue()*(1-percent);
        return new Color((int)r, (int)g, (int)b);
    }
    
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        //determine paintable area
        Dimension d = getSize();
        Insets inset = getInsets();
        d.setSize(d.width - inset.right - inset.left, d.height - inset.top - inset.bottom);        
        
        //for every color entry except the last one
        for(int i = 0; i < list.length-1; i++)
        {
            int startX = (list[i].location*d.width)/100 + inset.left;
            int endX = (list[i+1].location*d.width)/100;
            if(i == list.length-2)
                endX = endX - inset.right;
            
            GradientPaint gradient = new GradientPaint(startX, inset.top, list[i].c, endX, inset.top, list[i+1].c);
            g2.setPaint(gradient);
            g2.fill(new Rectangle2D.Double(startX, inset.top, endX, d.height));
         //   System.out.println("( " + startX + ", " + endX + ")");
        }
        
        //draw a vertical line for all points except the first and last
        for(int i = 1; i < list.length - 1; i++)
        {
            int location = (list[i].location*d.width)/100 + inset.left;
            g2.setColor(Color.BLACK);
            g2.draw(new Line2D.Double(location, inset.top, location, d.height));
        }
    } 
    
    public Dimension getMinimumSize()
    {
        return new Dimension(minSize);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
