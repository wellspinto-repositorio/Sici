/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

/**
 *
 * @author supervisor
 */
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class DecoratedDesktopPane extends JPanel {

     private ImageIcon image;
     private MediaTracker tracker;

     public DecoratedDesktopPane(String caminhoImagem) {

         image = new ImageIcon(this.getClass().getResource(caminhoImagem));
         tracker = new MediaTracker(this);
         tracker.addImage(image.getImage(), 0);

         try {
             tracker.waitForID(0);
         } catch (InterruptedException exception) {
             exception.printStackTrace();
         }

     }

//     public void paintComponent(Graphics graphics) {
//
//         super.paintComponent(graphics);
//
//         // Desenha a imagem e a centraliza no componente.
////         graphics.drawImage(image.getImage(),
////                 this.getWidth()/2 - image.getImage().getWidth(this)/2,
////                 this.getHeight()/2 - image.getImage().getHeight(this)/2,
////                 this.getBackground(), this);
//
//         //BufferedImage background = resize((BufferedImage) image.getImage(), 100, 100);
//         graphics.drawImage(image.getImage(),
//                 0,
//                 0,
//                 this.getBackground(), this);
//
//     }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dDesktop = this.getSize();

        double width = dDesktop.getWidth();
        double height = dDesktop.getHeight();

        Image background = new ImageIcon(image.getImage().getScaledInstance(
                (int) width, (int) height, 1)).getImage();

        g.drawImage(background, 0, 0, this);
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
} 