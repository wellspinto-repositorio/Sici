/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.AttributedString;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;


/**
 *
 * @author supervisor
 */
public class jThermica implements Printable {
    private Object[][] linhas;
//    String defaultPort = VariaveisGlobais.DefaultThermalPort;    
    String docName;

    public void setDocName(String Value) { docName = Value; }
    public String getDocName() { return docName; }    public void jThermica() {};

    public jThermica(Object[][] linhas) {
        this.linhas = linhas;
    }
    
    public void toSave() {
        String psMimeType = "application/postscript";
        FileOutputStream outstream; 
        StreamPrintService psPrinter = null; 
        
        PrinterJob job = PrinterJob.getPrinterJob();
        StreamPrintServiceFactory[] printers = PrinterJob.lookupStreamPrintServices(psMimeType);
        if (printers.length > 0) { 
                try { 
                        File outFile = new File(docName);
                        outstream = new FileOutputStream(outFile); 
                        psPrinter = printers[0].getPrintService(outstream); 
                } catch (FileNotFoundException e) { } 
        }         

        PageFormat pf = job.defaultPage();
        pf.setOrientation(PageFormat.PORTRAIT);
        
        // Tamanho do papel
        //Paper pp = new Paper();
        Paper pp = pf.getPaper();
        pp.setImageableArea(1.0, 1.0, pp.getWidth() - 2.0, pp.getHeight() - 2.0);
        pf.setPaper(pp);
       
        //job.setCopies(2);
        job.setPrintable(this, pf);
        try {
            job.setPrintService(psPrinter);
        } catch (PrinterException e1) {
            System.out.println("Erro acessando impressora: " + e1.getMessage());
        } 
        
        Book pBook = new Book();
        pBook.append(this, pf);
        job.setPageable(pBook);
        try {
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }
    
//    public void toPrint() {
//        // Verifica se Impressola esta habilitada
//        if (!VariaveisGlobais.statPrinter) return;
//        
//        PrintService service = null;
//        DocFlavor df = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
//        HashDocAttributeSet attributes = new HashDocAttributeSet();
//     
//        PrintService[] printers = PrintServiceLookup.lookupPrintServices(df, attributes);
//        
//        // Setar impressora thermica padrão
//        for (int p=0;p<printers.length;p++) {
//            if (printers[p].getName().toUpperCase().trim().equalsIgnoreCase(defaultPort)) {
//                service = printers[p];
//                break;
//            }
//        }
//        
//        PrinterJob job = PrinterJob.getPrinterJob(); 
//        PageFormat pf = job.defaultPage();
//        pf.setOrientation(PageFormat.PORTRAIT);
//        
//        Paper pp = pf.getPaper();
//        pp.setImageableArea(1.0, 1.0, pp.getWidth() - 2.0, pp.getHeight() - 2.0);
//        //Paper pp = new Paper();
//        //pp.setSize(204, 842);
//        //pp.setImageableArea(0, 0, 204, 842); 
//        pf.setPaper(pp);
//        
//        //job.setCopies(2);
//        job.setPrintable(this, pf);
//        try {
//            job.setPrintService(service);
//        } catch (PrinterException e1) {
//            System.out.println("Erro acessando impressora: " + e1.getMessage());
//        } 
//        
//        //job.pageDialog(pf);
//        Book pBook = new Book();
//        pBook.append(this, pf);
//        job.setPageable(pBook);
//        try {
//            job.print();
//        } catch (PrinterException e) {
//            e.printStackTrace();
//        }
//    }
    
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex != 0) {return NO_SUCH_PAGE;}
        
        int y = 10; int fheigth = 10;
        for (Object[] s_linhas : this.linhas) {
            int x = 0, ax = 0;
            int pg_width = 226 - 26; //(int) pf.getWidth() - 26;
            int len_slinhas = s_linhas.length;
            for (Object su_linhas : s_linhas) {
                Object[] ss_linhas = (Object[]) su_linhas;

                boolean elinha = false; String texto = ""; Image figura = null; Boolean efigura = false;
                if (ss_linhas[0] instanceof String) {texto = (String) ss_linhas[0]; efigura = false;}
                if (ss_linhas[0] instanceof Image) {figura = (Image) ss_linhas[0]; efigura = true;}
                if (ss_linhas[0] == null) elinha = true;

                if (!efigura) {
                    // Setar Font
                    Font lfont = new Font("Arial",Font.PLAIN,8);
                    if (ss_linhas[3] != null) lfont = (Font) ss_linhas[3];

                    // Setar ForeColor
                    Color lfcolor = Color.BLACK;
                    if (ss_linhas[4] != null) lfcolor = (Color) ss_linhas[4];

                    // Setar BackColor
                    Color lbcolor = Color.WHITE;
                    if (ss_linhas[5] != null) lbcolor = (Color) ss_linhas[5];

                    // Alinhamento
                    int lalign = 1; // 0 - Centro, 1 - Esquerdo, 2 - Direito, 3 - Justificado
                    if ((Integer) ss_linhas[2] != null) lalign = (Integer) ss_linhas[2];

                    Rectangle2D rsize = g.getFontMetrics(lfont).getStringBounds(texto, g);
                    int size = (int) rsize.getWidth();
                    int stringLen = size;
                    fheigth = (int) rsize.getHeight();

                    // Width
                    int lwidth = pg_width;
                    if ((Integer) ss_linhas[1] != null) lwidth = (Integer) ss_linhas[1];
                    if (stringLen > lwidth) lwidth = stringLen; //stringLen = lwidth;
                    if (ax + lwidth > pg_width) lwidth = pg_width - ax;

                    if (texto.length() > 0 && !elinha) {
                        if (lalign == 0) {
                            // Centro
                            x = 12 + ax + ((lwidth - stringLen) / 2);
                        } else if (lalign == 1) {
                            // Esquerdo
                            x = 12 + ax;
                        } else {
                            // Direito
                            x = 12 + ax + (lwidth - stringLen);
                        }
                        ax += lwidth;

                        AttributedString attributedString = new AttributedString(texto);
                        attributedString.addAttribute(TextAttribute.FONT, lfont); 
                        attributedString.addAttribute(TextAttribute.FOREGROUND, lfcolor);
                        attributedString.addAttribute(TextAttribute.BACKGROUND, lbcolor);

                        g.drawString(attributedString.getIterator(), x, y);
                    } else if (texto.length() <= 0 && !elinha) {
                        g.drawString(texto, x, y);
                    }  else if (elinha) {
                        // Impressão de linha-separadora
                        if (lalign == 0) {
                            // Centro
                            x = (pg_width - lwidth) / 2;
                        } else if (lalign == 1) {
                            // Esquerdo
                            x = 0;
                        } else {
                            // Direito
                            x = pg_width - lwidth;
                        }
                        g.drawLine(x, y, x + 12 + lwidth, y);
                    }
                } else {
                    g.drawImage(figura, 12, 0, null);
                    y += figura.getHeight(null);
                }
            }
            
            y += 10; x = 0;
          
            g.dispose();      
        }    
        
        return Printable.PAGE_EXISTS;
    }
    
    public Object[][] adcLine(Object[][] lines, Object[][] line) {
        int newSize = lines.length + 1;  
        Object[][] newItens = new Object[newSize][]; 
        
        for(int i=0;i<lines.length; i++) {
            newItens[i] = lines[i];
        }
        newItens[newSize - 1] = line;  
        
        return newItens;
    }
    
}
