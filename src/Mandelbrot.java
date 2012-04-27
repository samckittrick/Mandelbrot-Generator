/****************************************************
//    Mandelbrot Generator
//    Scott McKittrick
//    http://www.scottmckittrick.com
//
//    Written as a JComponent for the Swing Library
//**************************************************/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.ProgressMonitor;

public class Mandelbrot extends JComponent// implements Runnable
{
   private int sideSize;
   private int maxIterations;
   private BufferedImage image;
   private ColorMap map;
   private double zoomFactor;
   private boolean autoUpdateIterations;
   
   //field of view: start point and dimension/
   private Point2D.Double FOVLow;
   private double FOVx, FOVy;
   
   public Mandelbrot()
   {
      //initialise variables
      sideSize = 500;
      maxIterations = 20;
      zoomFactor = 0.5;
      setDefaultZoom();
      map = new ColorMap();
      image = new BufferedImage(sideSize, sideSize, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = image.createGraphics();
      g2.setColor(Color.WHITE);
      g2.fillRect(0, 0, sideSize, sideSize);
      g2.dispose();
      autoUpdateIterations = true;

      //updateImage();
   }
   
   private Point2D.Double normalise(int Px, int Py)
   {
       double x0 = ((double)(Px*FOVx)/sideSize)+FOVLow.getX();
       double y0 = ((double)(Py*FOVy)/sideSize)+FOVLow.getY();
       return new Point2D.Double(x0,y0);
   }
   
   private int calcIt(Point2D.Double d)
   {
      int iterations = 0;
      int bailout = 4;
      
      //normalise the points to a cartesian 
      double x0 = d.getX();
      double y0 = d.getY();
      double distance = 0;
      double a = 0;
      double b = 0;
      while((distance < bailout)&&(iterations < maxIterations))
      {
         double temp = a*a - b*b + x0;
         b = 2*a*b + y0;
         a = temp;
         distance = a*a + b*b;
         iterations++;
      }    
      return iterations;
   }
   
   private Color mapToColor(int iterations)
   {
      return map.getColor(iterations, maxIterations);
   }
   
   public void updateImage()
   {
      Graphics2D g2 = image.createGraphics();
      //ProgressMonitor pm = new ProgressMonitor(this, "Generating Figure...", "", 0, sideSize*sideSize);
      for(int i = 0; i < sideSize; i++)
      {
         for(int j = 0; j < sideSize; j++)
         {
            Point2D.Double p = normalise(i, j);
            int iterations = calcIt(p);
            g2.setColor(mapToColor(iterations));
            g2.fillRect(i,j, 1, 1);
            //pm.setProgress((i)*j + j);
         }
      }
      //pm.close();
      g2.dispose();
      repaint();
   }
             
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.drawImage(image, 0, 0, null);
   }
   
   public Dimension getPreferredSize()
   {
      return new Dimension(sideSize, sideSize);
   }
   
   public Dimension getMinimumSize()
   {
      return getPreferredSize();
   }
   
   public Dimension getMaximumSize()
   {
      return getPreferredSize();
   }
   
   public void setIterations(int iterations)
   {
      maxIterations = iterations;
   }
   
   public void setColors(ColorMap c)
   {
       map = c;
   }
   
   public int getIterations()
   {
      return maxIterations;
   }
   
   public double getZoomFactor()
   {
       return zoomFactor;
   }
   
   public void setZoomFactor(double z)
   {
       zoomFactor = z;
   }
   
   public void zoomIn(int Px, int Py)
   {
       //normalise click point
       Point2D.Double zoomPoint = normalise(Px, Py);
       
       //calculate the new field of view size
       FOVx *= zoomFactor;
       FOVy *= zoomFactor;
       //calculate start point of fov
       
       double LprimeX = zoomPoint.getX() - (FOVx/2);
       double LprimeY = zoomPoint.getY() - (FOVy/2);
       FOVLow = new Point2D.Double(LprimeX, LprimeY);
       
       //maxIterations *= 1.25;
   }
   
   public void zoomOut(int Px, int Py)
   {
       //Normalise click point
       Point2D.Double zoomPoint = normalise(Px, Py);
       
       //calculate the new field of view size
       FOVx /= zoomFactor;
       FOVy /= zoomFactor;
       
       //calculate start point of fov
       double LprimeX = zoomPoint.getX() - (FOVx/2);
       double LprimeY = zoomPoint.getY() - (FOVy/2);
       FOVLow = new Point2D.Double(LprimeX, LprimeY);
   }
   
   public void setDefaultZoom()
   {
       FOVx = 3.5;
       FOVy = 3;
       FOVLow = new Point2D.Double(-2.25, -1.5);
   }
   
   public void setAutoUpdateIterations(boolean value)
   {
       autoUpdateIterations = value;
   }
   
   public boolean getAutoUpdateIterations()
   {
       return autoUpdateIterations;
   }
   
   public BufferedImage renderImage(int x, int y)
   {
       int imgSideSize = Math.min(x, y);
       double tFOVx, tFOVy;
       Point2D.Double tFOVLow;
       if(imgSideSize == x)
       {
           tFOVx = FOVx;
           tFOVy = FOVy * ((double)y/sideSize);
           tFOVLow = new Point2D.Double(FOVLow.getX(), FOVLow.getY()-(tFOVy - FOVy)/2);
       }
       else
       {
           tFOVx = FOVx * ((double)x/imgSideSize);
           System.out.println(tFOVx);
           tFOVLow = new Point2D.Double(FOVLow.getX() - (tFOVx - FOVx)/2, FOVLow.getY());
           tFOVy = FOVy;
       }
       BufferedImage image = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
       Graphics2D g2 = image.createGraphics();
       for(int i = 0; i < x; i++)
       {
           for(int j = 0; j < y; j++)
           {
                double x0 = ((double)(i*tFOVx)/x) + tFOVLow.getX();
                double y0 = ((double)(j*tFOVy)/y)+ tFOVLow.getY();
                int it = calcIt(new Point2D.Double(x0,y0));
                g2.setColor(mapToColor(it));
                g2.fillRect(i,j,1,1);
           }
       }
       g2.dispose();
       return image;
   }
   
   /*public void run()
   {
       
   }*/
}