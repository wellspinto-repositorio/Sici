/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import static Funcoes.PrinterPOS.backlashReplace;

/**
 *
 * @author samic
 */
public class toPreview {
    public toPreview(String outFileName) {
        String defaultFileName = null;
        if (!System.getProperty("os.name").toUpperCase().trim().equals("LINUX")) {
            defaultFileName = backlashReplace(outFileName);
        } else defaultFileName = LinuxTags(outFileName);
        String cmdPrint = VariaveisGlobais.Preview;
        cmdPrint = cmdPrint.replace("[FILENAME]", defaultFileName);
        try {
            ComandoExterno.ComandoExterno(cmdPrint);
        } catch (Exception e) {}
        System.out.println(cmdPrint);
    }
    
    private String LinuxTags(String outFileName) {
        outFileName = outFileName.replace(" ", "\\ ");
        outFileName = outFileName.replace("(", "\\(");
        outFileName = outFileName.replace(")", "\\)");
        
        return outFileName;
    }
    
}
