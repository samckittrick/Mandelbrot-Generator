/****************************************************
//    Mandelbrot Generator
//    Scott McKittrick
//    http://www.scottmckittrick.com
//
//    Written as a JComponent for the Swing Library
//**************************************************/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  Note: In this object there is a list of colors. There will always be a color
 *        located at 0, and 100. 
 * @author Scott McKittrick
 */
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.Border;
import java.io.Serializable;

public class ColorMap extends javax.swing.JPanel implements Serializable {
    
    ColorUnit[] list;
    ColorUnit noEscape;
    Dimension minSize;
    JLabel label;

    /**
     * Creates new form ColorMap
     */
    public ColorMap() {
        list = new ColorUnit[2];
        list[0] = new ColorUnit(new Color(51,255,255), 0);
        //list[1] = new ColorUnit(new Color(66, 37, 250), 50);
        list[1] = new ColorUnit(new Color(0, 51, 153), 100);

        noEscape = new ColorUnit(new Color(0,0,0), -1);
        minSize = new Dimension(300,90);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        JButton leftMost = createSwatch(list[0].c);
        leftMost.addActionListener(new SwatchActionListener(list[0]));
        topPanel.add(leftMost);
        topPanel.add(Box.createHorizontalGlue());
        JButton rightMost = createSwatch(list[list.length-1].c);
        rightMost.addActionListener(new SwatchActionListener(list[list.length-1]));
        topPanel.add(rightMost);
        add(topPanel);       
        
        JPanel midPanel = new JPanel();
        label = new JLabel(new ImageIcon(drawGradient()));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        midPanel.add(label);
        add(midPanel);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        bottomPanel.add(new JLabel("0 Iterations"));
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(new JLabel("Max Iterations"));
        add(bottomPanel);
        
        JPanel extraBottomPanel = new JPanel();
        extraBottomPanel.setLayout(new BoxLayout(extraBottomPanel, BoxLayout.LINE_AXIS));
        extraBottomPanel.add(Box.createHorizontalGlue());
        extraBottomPanel.add(new JLabel("No-Escape Color"));
        extraBottomPanel.add(Box.createRigidArea(new Dimension(10,15)));
        JButton noEscapeButton = createSwatch(noEscape.c);
        noEscapeButton.addActionListener(new SwatchActionListener(noEscape));
        extraBottomPanel.add(noEscapeButton);
        add(extraBottomPanel);   
    }
    
    private void initComponents()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        JButton leftMost = createSwatch(list[0].c);
        leftMost.addActionListener(new SwatchActionListener(list[0]));
        topPanel.add(leftMost);
        topPanel.add(Box.createHorizontalGlue());
        JButton rightMost = createSwatch(list[list.length-1].c);
        rightMost.addActionListener(new SwatchActionListener(list[list.length-1]));
        topPanel.add(rightMost);
        add(topPanel);       
        
        JPanel midPanel = new JPanel();
        label = new JLabel(new ImageIcon(drawGradient()));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        midPanel.add(label);
        add(midPanel);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        bottomPanel.add(new JLabel("0 Iterations"));
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(new JLabel("Max Iterations"));
        add(bottomPanel);
        
        JPanel extraBottomPanel = new JPanel();
        extraBottomPanel.setLayout(new BoxLayout(extraBottomPanel, BoxLayout.LINE_AXIS));
        extraBottomPanel.add(Box.createHorizontalGlue());
        extraBottomPanel.add(new JLabel("No-Escape Color"));
        extraBottomPanel.add(Box.createRigidArea(new Dimension(10,15)));
        JButton noEscapeButton = createSwatch(noEscape.c);
        noEscapeButton.addActionListener(new SwatchActionListener(noEscape));
        extraBottomPanel.add(noEscapeButton);
        add(extraBottomPanel);   
    }
    
    public void importColors(ColorMap in)
    {
        list = new ColorUnit[in.list.length];
        System.arraycopy(in.list, 0, list, 0, list.length);
        noEscape = in.noEscape;
        minSize = in.minSize;
        removeAll();
        initComponents();
        validate();
    }
        
    private class ColorUnit implements Serializable
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
        
        public void setColor(Color cin)
        {
            c = cin;
        }
    }
    
    public Color getColor(int value, int max)
    {
        //normalise the value to a 100 unit scale
        int normalised = (value*100)/max;
        
        if(normalised == 100)
            return new Color(noEscape.c.getRGB());
        
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
    
    private BufferedImage drawGradient()
    {
                
        BufferedImage img = new BufferedImage(300, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        //determine paintable area
        Dimension d = new Dimension(minSize.width, minSize.height - 40);
        Insets inset = getInsets();      
        
        //for every color entry except the last one
        for(int i = 0; i < list.length-1; i++)
        {
            int startX = (list[i].location*d.width)/100;
            int endX = (list[i+1].location*d.width)/100;
            if(i == 0)
                startX += inset.left;
            
            if(i == list.length-2)
                endX = endX - inset.right;
            
            GradientPaint gradient = new GradientPaint(startX, inset.top, list[i].c, endX, inset.top, list[i+1].c);
            g2.setPaint(gradient);
            g2.fill(new Rectangle2D.Double(startX, inset.top, endX, d.height));
            //System.out.println("( " + startX + ", " + endX + ")");
            
        }
        
        //draw a vertical line for all points except the first and last
        for(int i = 1; i < list.length - 1; i++)
        {
            int location = (list[i].location*d.width)/100 + inset.left;
            g2.setColor(Color.BLACK);
            g2.draw(new Line2D.Double(location, inset.top, location, d.height));
        }
        g2.dispose();
        
        return img;
    }
    
    public void updateGradient()
    {
        label.setIcon(new ImageIcon(drawGradient()));
        label.repaint();
    }
    
    public Dimension getMinimumSize()
    {
        return new Dimension(minSize);
    }
    
    public Dimension getMaximumSize()
    {
        return new Dimension(minSize);
    }
    
    private JButton createSwatch(Color c)
    {
        JButton button = new JButton(createSwatchIcon(c));
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        button.addMouseListener(new SwatchML());
        
        return button;
    }
    
    private ImageIcon createSwatchIcon(Color c)
    {
        BufferedImage b = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = b.createGraphics();
        g2.setColor(c);
        g2.fillRect(0,0,10,10);
        g2.dispose();
        return new ImageIcon(b);
    }
    
    private class SwatchML implements MouseListener
    {
        private Border b1;
        private Border b2;
        
        public SwatchML()
        {
            b1 = BorderFactory.createRaisedBevelBorder();
            b2 = BorderFactory.createLoweredBevelBorder();            
        }
        public void mouseClicked(MouseEvent me) {}
        public void mouseEntered(MouseEvent me) {}
        public void mouseExited(MouseEvent me) {}
        public void mousePressed(MouseEvent me)
        {
            JButton b = (JButton)me.getComponent();
            b.setBorder(BorderFactory.createCompoundBorder(b2,b1));
        }
        
        public void mouseReleased(MouseEvent me)
        {
            JButton b = (JButton)me.getComponent();
            b.setBorder(BorderFactory.createCompoundBorder(b1,b2));
        }
    }
    
    private class SwatchActionListener implements ActionListener
    {
        private ColorUnit cu;
        
        public SwatchActionListener(ColorUnit color)
        {
            cu = color;
        }
        
        public void actionPerformed(ActionEvent ae)
        {
            Color newColor = JColorChooser.showDialog(null, "Choose Color", cu.c);
            if(newColor != null)
            {
                cu.setColor(newColor);
                JButton b = (JButton)ae.getSource();
                b.setIcon(createSwatchIcon(newColor));
                b.repaint();
                updateGradient();
            }           
        }
    }
}
