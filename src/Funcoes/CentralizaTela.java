/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import Sici.jProprietarios;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 *
 * @author supervisor
 */
public class CentralizaTela {
    private static Dimension ds = null;
    private static Dimension dx = null;

    /**
     * Centraliza JDialog
     * @param com
     * @param menosX
     * @param menosY
     */

   public static void setCentro ( JFrame com , int menosX , int menosY )
   {
        dx = Toolkit.getDefaultToolkit ( ).getScreenSize ( );
        ds = com.getSize ( );
        com.setLocation ( ( ( dx.width - ds.width ) / 2 ) - menosX , ( ( dx.height - ds.height ) / 2 ) - menosY );
   }

   /**
    * Centraliza JDialog
    * @param com
    * @param menosX
    * @param menosY
    */

   public static void setCentro ( JInternalFrame com , JDesktopPane desktop, int menosX , int menosY )
   {
       dx = desktop.getSize();
       ds = com.getSize ( );
       com.setLocation ( ( ( dx.width - ds.width ) / 2 ) - menosX , ( ( dx.height - ds.height ) / 2 ) - menosY );
   }
}
