/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.io.File;
import javax.swing.ImageIcon;

/**
 *
 * @author supervisor
 */
public class ResizeImageIcon {
    private ImageIcon img = null;
    
    public ResizeImageIcon(String IE, String path, int width, int height) {
        ImageIcon img = null;
        
        if (IE.equalsIgnoreCase("E")) {
            if (new File(path).exists()) {
                img = BlobFields.BufferedImage2ImageIcon(BlobFields.resize(
                                BlobFields.ImageIcon2BufferedImage(
                                new ImageIcon(path)), width, height));
            } else {
                System.out.println("Arquivo " + path + " não existe!!!");
                System.exit(1);
            }
        } else {
            img = BlobFields.BufferedImage2ImageIcon(BlobFields.resize(
                            BlobFields.ImageIcon2BufferedImage(
                            new ImageIcon(getClass().getResource("/Figuras/" + path))), width, height));            
        }
        this.img = img;
    }
    
    public ImageIcon getImg() { return this.img; }
    
}
