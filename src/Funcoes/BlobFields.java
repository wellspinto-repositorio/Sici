/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author rick
 */
public class BlobFields {
    byte[] fotografia = null;
    BufferedImage img = null;

    public BlobFields(byte[] field) {
        this.fotografia = field;
    }

    public Icon getPicture() {
        try {this.img = ImageIO.read(new ByteArrayInputStream(this.fotografia));} catch (Exception ex) { ex.printStackTrace(); }
        return (Icon)this.img;
    }

    public BufferedImage getBuffered() {
        try {this.img = ImageIO.read(new ByteArrayInputStream(this.fotografia));} catch (Exception ex) { ex.printStackTrace(); }
        return this.img;
    }

    static public BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    static public ImageIcon BufferedImage2ImageIcon(BufferedImage bufimg) {
        ImageIcon icon = new ImageIcon(bufimg.getScaledInstance(bufimg.getWidth(), bufimg.getHeight(), 10000));
        return icon;
    }

    static public BufferedImage ImageIcon2BufferedImage(ImageIcon imgicn) {
        BufferedImage bufimg = new BufferedImage(imgicn.getIconWidth(), imgicn.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufimg.createGraphics();
        g.drawImage(imgicn.getImage(), 0, 0, null);
        g.dispose();
        return bufimg;
    }

//    public static byte[] bufferedImageToByteArray(BufferedImage img) throws ImageFormatException, IOException{
//                    ByteArrayOutputStream os = new ByteArrayOutputStream();
//                    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
//                    encoder.encode(img);
//                    return os.toByteArray();
//    }

//    public static Blob ByteArrayToBlob(byte[] buffer) {
//        Blob blob = null;
//        //blob = new SerialBlob(buffer);
//
//        return blob;
//    }

    public static InputStream ByteArrayToImputStream(byte[] bytes) {
        // Use BinaryStream para salvar no DB
        InputStream input = new ByteArrayInputStream(bytes);
        return input;
    }
}
