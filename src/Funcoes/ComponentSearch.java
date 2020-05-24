/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.awt.Component;
import javax.swing.JPanel;

/**
 *
 * @author supervisor
 */
public class ComponentSearch {
    
    public static int ComponentSearch(JPanel panel, String name) {
        Component[] components = panel.getComponents();
        Component component = null;
        int j = -1;
        for (int i = 0; i < components.length; i++)
        {
            component = components[i];
            if (component.getName() != null) {
                if (component.getName().trim().toLowerCase().equals(name.trim().toLowerCase())) {
                    j = i;
                    break;
                }
            }
        }     
        return j;
    }
}
