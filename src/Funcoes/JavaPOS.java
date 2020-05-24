/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

/**
 *
 * @author supervisor
 */
public class JavaPOS {
    public static final String HT   = "\t";                       // Horizontal Tab = \u0009
    public static final String LF = "\n";                         // Print and line feed \u000A
    public static final String FF   = "\f";                       // Print and return to standard mode (in page mode) \u000C
    public static final String CR   = "\r";                       // Return \u000D
    public static final String CAN  = "\u0018";                   // Cancel print data in page mode
    public static final String ESC  = "\u001B";
    public static final String ESC_FF = "\u001B" + "\f";          // Print data in page mode
    public static final String ESC_J = "\u001B" + "\u004A";       // Print and feed paper0
    public static final String ESC_K = "\u001B" + "\u004B";       // Print and reverse feed
    public static final String ESC_2 = "\u001B" + "\u0032";       // Select default line spacing
    public static final String ESC_ARROBA = "\u001B" + "\u0040";  // Inicialize Printer
    public static final String ESC_90_OFF = "\u001B" + "\u0056";
    public static final String ESC_90_ON = "\u001B" + "\u0056" + "\u0001";
    public static final String LATIN_AMERICA = "\u001B" + "\u0052" + "\u000C";
    public static final String CODE_PAGE_PROTUGUESE = "\u001B" + "\u0074" + "\u0002";
    public static final String NORMAL = "\u001D" + "\u0042" + "\u0000";
    public static final String REVERSO = "\u001D" + "\u0042" + "\u0001";
    public static final String FULLCUT = "\u001B" + "i";
    public static final String PARTCUT = "\u001B" + "m";

    public static final int FONT1 = 0;
    public static final int FONT2 = 1;
    public static final int ENFATIZADO = 8;
    public static final int DHEIGTH = 16;
    public static final int DWIDTH = 32;
    public static final int UNDERLINE = 128;

    public static final String Align_Rigth = ESC + "|rA";
    public static final String Align_Center = ESC + "|cA";
    public static final String Align_Left = ESC + "|lA";
    public static final String Bold = ESC + "|bC";
    public static final String Underline = ESC + "|uC";
    public static final String Double = ESC + "|4C";
    
    public static String DLE_EOT(int n, int a) {
        String DLEEOT = "\u0020";
        String H16 = "\u0010"; String H04 = "\u0004"; char Hnn = (char)n;
        if (a == -1) {
            if (n < 1 || n > 4) { return DLEEOT; }
        } else {
            if (n <7 || n > 8) { return DLEEOT; }
            if (n == 7) { if (a < 1 || a > 2) { return DLEEOT; } } else { if (a != 3) { return DLEEOT; } }
        }
        DLEEOT = H16 + H04 + Hnn;
        return DLEEOT;
    }
    
    public static String ESC_D(int n) {
        String ESCD = "";
        if (n < 0 || n > 255) { return ESCD; }
        ESCD = "\u001B" + "\u0064" + (char)n;
        return ESCD;
    }
    
    public static String ESC_E(int n) {
        String ESCE = "";
        if (n < 0 || n > 2) { return ESCE; }
        ESCE = "\u001B" + "\u0045" + (char)n;
        return ESCE;
    }
    
    public static String ESC_SP(int n) {
        String ESCSP = "";
        if (n < 0 || n > 255) { return ESCSP; }
        ESCSP = "\u001B" + "\u0020" + (char)n;
        return ESCSP;
    }
    
    public static String ESCLAMATION(int n) {
        String ESCEXC = "";
        if (n < 0 || n > 255) { return ESCEXC; }
        ESCEXC = "\u001B" + "\u0021" + (char)n;
        return ESCEXC;
    }
        
    /**
     * 
     * @param w  Tamanho
     *        0  Standard
     *        1  2 vezes
     *        2  3 vezes
     *        3  4 vezes
     *        4  5 vezes
     *        5  6 vezes
     *        6  7 vezes
     *        7  8 vezes
     * 
     * @param h  Tamanho
     *        0  Standard
     *       16  2 vezes
     *       32  3 vezes
     *       48  4 vezes
     *       64  5 vezes
     *       80  6 vezes
     *       96  7 vezes
     *      112  8 vezes
     * @return fontSize
     */
    public static String SetCharacterSize(int w, int h) {
        int n = 0; int _h = 0; int _w = 0;
        if (h >=0 || h <= 7) { _h = h; } else { _h = 0; }
        if (w >=0 && (w % 15) == 0 && w <= 112) { _w = w; } else { _w = 0; }
        
        n = _h + _w;
        return "\u001B" + "!" + (char)n;
    }
    
    /**
     * 
     * @return Initialization
     */
    public static String InitPrinter() { return ESC_ARROBA; }
    
    public static String ESC_DOLAR(int n, int n2) {
        String ESCDOL = "";
        if ((n < 0 || n > 255) && (n2 < 0 || n2 > 255)) { return ESCDOL; }
        ESCDOL = "\u001B" + "\u0024" + (char)n + (char)n2;
        return ESCDOL;
    }
    
    public static String ESC_3(int n) {
        String ESC3 = ""; // Default = 30
        if (n < 0 || n > 255) { return ESC3; }
        ESC3 = "\u001B" + "\u0033" + (char)n;
        return ESC3;
    }
    
    public static String ESC_A(int n) {
        String ESCA = "";
        if (n < 0 || n > 2) { return ESCA; }
        ESCA = "\u001B" + "\u0061" + (char)(48 + n);
        return ESCA;
    }
    
    public static String GS_EXC(int n) {
        // Width               Height
        //  0 - Normal          0 - Normal
        // 16 - Duplo           1 - Duplo
        // 32 - 3               2 - 3
        // 48 - 4               3 - 4
        // 64 - 5               4 - 5
        // 80 - 6               5 - 6
        // 96 - 7               6 - 7
        //112 - 8               7 - 8
        //
        // 16 + 1 = 17 - Duplo W e Duplo H
        String GS = "\u001D" + "\u0021";
        
        return GS + (char)n;
    }
    
    public static String ESC_G(int n) {
        String ESCG = "";
        if (n < 0 || n > 1) { return ESCG; }
        ESCG = "\u001B" + "\u0047" + (char)n;
        return ESCG;
    }
    
    public static String ESC_R(int n) {
        String ESCR = "";
        if (n < 0 || n > 1) { return ESCR; }
        ESCR = "\u001B" + "\u0072" + (char)n;
        return ESCR;
    }

    public static String EAN13(float nAut) {
      return "\u001d" + "h" + (char)80  + 
             "\u001d" + "k" + (char)2 + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 13) + "\u0000";
    }
    
    public static String CodBar39(String numero) {
        return "\u001d" + "h" + (char)80  + 
               "\u001D" + "k" + (char)4 +  numero;
    }
    
    public static String RemovePos(String texto) {
        if (texto == null) return "";
        if (texto.isEmpty()) return "";

        texto = texto.replaceAll(HT, "    ");
        texto = texto.replaceAll(CAN, "");
        texto = texto.replaceAll(ESC_FF, "");
        texto = texto.replaceAll(ESC_J, "");
        texto = texto.replaceAll(ESC_K, "");
        texto = texto.replaceAll(ESC_2, "");
        texto = texto.replaceAll(ESC_ARROBA, "");
        texto = texto.replaceAll(ESC_90_OFF, "");
        texto = texto.replaceAll(ESC_90_ON, "");
        texto = texto.replaceAll(LATIN_AMERICA, "");
        texto = texto.replaceAll(CODE_PAGE_PROTUGUESE, "");
        texto = texto.replaceAll(NORMAL, "");
        texto = texto.replaceAll(REVERSO, "");
        texto = texto.replaceAll(FULLCUT, "");
        texto = texto.replaceAll(PARTCUT, "");
        
        String letra = "";
        letra = getLetra(texto,"\u001B" + "\u0064");        
        texto = texto.replaceAll("\u001B" + "\u0064" + letra, "");
        
        letra = getLetra(texto,"\u001B" + "\u0045");        
        texto = texto.replaceAll("\u001B" + "\u0045" + letra, "");

        letra = getLetra(texto,"\u001B" + "\u0020");        
        texto = texto.replaceAll("\u001B" + "\u0020" + letra, "");

        letra = getLetra(texto,"\u001B" + "\u0021");        
        texto = texto.replaceAll("\u001B" + "\u0021" + letra, "");

        letra = getLetra(texto,"\u001B" + "!");        
        texto = texto.replaceAll("\u001B" + "!" + letra, "");

        letra = getLetra(texto,"\u001B" + "\u0033");        
        texto = texto.replaceAll("\u001B" + "\u0033" + letra, "");

        letra = getLetra(texto,"\u001B" + "\u0061");        
        texto = texto.replaceAll("\u001B" + "\u0061" + letra, "");

        letra = getLetra(texto,"\u001D" + "\u0021");        
        texto = texto.replaceAll("\u001D" + "\u0021" + letra, "");

        letra = getLetra(texto,"\u001B" + "\u0047");        
        texto = texto.replaceAll("\u001B" + "\u0047" + letra, "");

        letra = getLetra(texto,"\u001B" + "\u0072");        
        texto = texto.replaceAll("\u001B" + "\u0072" + letra, "");

        letra = getLetra(texto,"\u001D" + "\u0021");        
        texto = texto.replaceAll("\u001D" + "\u0021" + letra, "");

        return texto;
    }

    private static String getLetra(String text, String command) {
        String doCmd = "";
        int pos = text.indexOf(command);        
        if (pos > -1) doCmd = text.substring(pos + 2, pos + 3);
        return doCmd;
    }
}    