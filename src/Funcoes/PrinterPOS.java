/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 *
 * @author supervisor
 */
public class PrinterPOS {
//    String defaultPort = VariaveisGlobais.DefaultThermalPort;    
    //String defaultPort = "/home/supervisor/jpos/jpos.xml";
    String docName;
    
    jpos.POSPrinter printer;
    String ESC    = ((char) 0x1b) + "";
    String LF     = ((char) 0x0a) + "";
    String SPACES = "                                                                      ";
    
    public PrinterPOS(String docName) {
        String fileName = docName;
        File f = new File(fileName);
        if (f.exists()) f.delete();
        setDocName(fileName);
    }
    public PrinterPOS() {
        //
    }
    
    public void Print(String output, int newLine) {
        if (newLine == 1) { output += LF; }

        try {
            if (docName != null) {
                if (!"".equals(docName.trim())) {
                    StreamFile filler = new StreamFile(new String[] {docName});
                    if (filler.Open()) {
                        filler.Print(output);
                    }
                    filler.Close();
                }
            }
        } catch (Exception e) {}
    }
    
    public void PrintBitMap(String bitmap, int align) {
        try {
            if (docName != null) {
                if (!"".equals(docName.trim())) {
                    StreamFile filler = new StreamFile(new String[] {docName});
                    if (filler.Open()) {
                        filler.Print("imagem;" + align + ";" + bitmap);
                    }
                    filler.Close();
                }
            }
        } catch (Exception e) {}
    }
    
    public void PrintBarCode(String barcode) {
        try {
            if (docName != null) {
                if (!"".equals(docName.trim())) {
                    StreamFile filler = new StreamFile(new String[] {docName});
                    if (filler.Open()) {
                        filler.Print("barcode;null;" + barcode);
                    }
                    filler.Close();
                }
            }
        } catch (Exception e) {}
    }
    
    public void CutPaper() {
        try {
            if (docName != null) {
                if (!"".equals(docName.trim())) {
                    StreamFile filler = new StreamFile(new String[] {docName});
                    if (filler.Open()) {
                        filler.Print(ESC + "|100fP");
                    }
                    filler.Close();
                }
            }
        } catch (Exception e) {}
    }
    
//    public void Close() {
//        // Verifica se Impressola esta habilitada
//        if (!VariaveisGlobais.statPrinter) return;
//        
//        String cmd = null;
//        if (System.getProperty("os.name").toUpperCase().trim().equals("LINUX")) {
//            cmd = "lp -d " + defaultPort + " " + docName.replace(" ", "\\ ");
//            ComandoExterno.ComandoExterno(cmd);
//        } else {
//            String docPrint = backlashReplace(docName);
//            try {
//                cmd = "C:\\windows\\system32\\cmd.exe /c copy " + docPrint + " \\\\" + VariaveisGlobais.DefaultPrinterPort + "\\"  + defaultPort;
//                ComandoExterno.ComandoExterno(cmd);
//            } catch (Exception e) { e.printStackTrace(); }
//        }
//        System.out.println(cmd);
//    }
    
    public static String backlashReplace(String myStr){
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(myStr);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){

          if (character == '/') {
             result.append("\\");
          }
           else {
            result.append(character);
          }


          character = iterator.next();
        }
        return result.toString();
    }

    
    public void setDocName(String Value) { docName = Value; }
    public String getDocName() { return docName; }
    
}
