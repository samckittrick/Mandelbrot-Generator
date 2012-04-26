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
 *
 * @author Scott McKittrick
 */
import java.io.File;
import javax.swing.filechooser.FileFilter;
public class ImageFilter extends FileFilter {
    
    public boolean accept(File f)
    {
        if(f.isDirectory())
            return true;
        String fileName = f.getName();
        
        if(fileName.toLowerCase().endsWith(".jpg")|fileName.toLowerCase().endsWith(".jpeg"))
            return true;
        else
            return false;
    }
    
    public String getDescription()
    {
        return "JPEG images (.jpg, .jpeg";
    }
    
}
