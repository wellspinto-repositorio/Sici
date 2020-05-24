/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.io.File;

/**
 *
 * @author supervisor
 */
public class ApplicationPath {

    public String ApplicationPath() {
        //return (new File(".")).getAbsolutePath();
        return System.getProperty("user.dir") + System.getProperty("file.separator");
    }
}
