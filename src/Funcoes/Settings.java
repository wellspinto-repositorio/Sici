/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

/**
 *
 * @author supervisor
 */
public class Settings {
    static private String GlobalPropert = System.getProperty("user.dir") + "/Sici.conf";
    static private String mFile = "";
    private Properties p;
    FileInputStream propFile;
    
    public Settings() {
        String osname = System.getProperty("os.name").toUpperCase().trim();
        String localPropert = "";
        if (osname.equalsIgnoreCase("LINUX") || osname.equalsIgnoreCase("MAC")) {
            localPropert = System.getProperty("user.home") + "/Sici.conf";
        } else {
            localPropert = "C://Sici.conf";
        }
    
        boolean exists = (new File(localPropert)).exists();
        VariaveisGlobais.local = exists;
        
        if (exists) {
            mFile = localPropert;
        } else {
            mFile = GlobalPropert;
        }
        
        try {
            propFile = new FileInputStream(mFile);
            p = new Properties(System.getProperties()){
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            p.load(propFile);

            System.setProperties(p);
        } catch (Exception ex) {ex.printStackTrace();}
        
        System.out.println(localPropert);
    }
    
    public void Save(String propriedade, String Valor) {
        try {
            System.setProperty(propriedade, Valor);
            p.setProperty(propriedade, Valor);
            FileOutputStream outFile = new FileOutputStream(mFile);
            
            p.store(outFile,"Sici.propriedades");
            outFile.close();
        } catch (Exception ex) {ex.printStackTrace();}
    }
    
}
