/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import static Funcoes.PrinterPOS.backlashReplace;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author supervisor
 */
public class toPrint2 {
    public int IMP_THERMICA = 0;
    public int IMP_NORMAL = 1;
    public int TP_PSTXT = 2;
    public int TP_PDF = 3;
    
    public toPrint2(String outFileName, int prn) {
        if (!VariaveisGlobais.statPrinter) return;
        
        String defaultIpPrinter = null;
        String defaultNamePrinter = null;
        String defaultNamePrinterShare = null;
        String defaultFileName = null;
        
        if (prn == IMP_THERMICA || prn == TP_PSTXT) {
            String[] printers = VariaveisGlobais.Thermica.split(",");
            for (String print : printers) {
                String tprint[] = print.split(":");
                if (tprint.length == 4) {
                    defaultIpPrinter = tprint[0];
                    defaultNamePrinter = tprint[1];
                    defaultNamePrinterShare = tprint[2];
                }
            }
        } else {
            String[] printers = VariaveisGlobais.Printer.split(",");
            for (String print : printers) {
                String tprint[] = print.split(":");
                if (tprint.length == 4) {
                    defaultIpPrinter = tprint[0];
                    defaultNamePrinter = tprint[1];
                    defaultNamePrinterShare = tprint[2];
                }
            }
        }
        
        if (!System.getProperty("os.name").toUpperCase().trim().equals("LINUX")) {
            defaultFileName = backlashReplace(outFileName);
        } else defaultFileName = LinuxTags(outFileName);
        
        String cmdPrint = null;
        if (prn == TP_PSTXT) {
            if (VariaveisGlobais.Externo2 == null) {
                cmdPrint = VariaveisGlobais.Externo;
            } else cmdPrint = VariaveisGlobais.Externo2;
        } else cmdPrint = VariaveisGlobais.Externo;
        
        //System.out.println(cmdPrint);
        
        cmdPrint = cmdPrint.replace("[IP]", defaultIpPrinter);
        cmdPrint = cmdPrint.replace("[PRINTER]", defaultNamePrinter);
        Boolean eshare = cmdPrint.contains("[PRINTERSHARE]");
        cmdPrint = cmdPrint.replace("[PRINTERSHARE]", defaultNamePrinterShare);
        cmdPrint = cmdPrint.replace("[FILENAME]", defaultFileName);
        
        //System.out.println(cmdPrint);

        if (VariaveisGlobais.PrinterMode.equalsIgnoreCase("EXTERNA") || eshare) {
            try {
                ComandoExterno.ComandoExterno(cmdPrint);
            } catch (Exception e) {}
            System.out.println(cmdPrint);
        } else {
            //System.out.println("Foi para PDF");
            
            try {
                PrintService impressora = null;
                PrintService[] pservices = PrinterJob.lookupPrintServices();

                if (pservices.length > 0) {
                    for (PrintService ps : pservices) {
                        System.out.println("Impressora Encontrada: " + ps.getName());

                        if (ps.getName().trim().contains(defaultNamePrinter.trim())) {
                            System.out.println("Impressora Selecionada: " + ps.getName());
                            impressora = ps;
                            break;
                        }
                    }
                }
                if (impressora != null) {
                    PrinterJob pjob = null;
                    pjob = PrinterJob.getPrinterJob();
                    pjob.setPrintService(impressora);

                    PDDocument pdf=PDDocument.load(defaultFileName.replace("\\ ", " "));
                    pdf.silentPrint(pjob);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }    
    
    private String LinuxTags(String outFileName) {
        outFileName = outFileName.replace(" ", "\\ ");
        outFileName = outFileName.replace("(", "\\(");
        outFileName = outFileName.replace(")", "\\)");
        
        return outFileName;
    }
    
    public String[] ListarImp() {
        String[] ret = {};
        PrintService[] pservices = PrinterJob.lookupPrintServices();

        if (pservices.length > 0) {
            for (PrintService ps : pservices) {
                ret = FuncoesGlobais.ArrayAdd(ret, ps.getName());
            }
        }
        
        return ret;
    }
    
}
