/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LerValor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

/**
 *
 * @author supervisor
 */
public class LerValor {

    static public String FormataCurrency(String cValor) {
        String Inteiro;
        String Decimal;
        Float Valor;

        try {
            cValor = ("".equals(cValor.trim()) || cValor == null ? "0000000000" : cValor);
        } catch (NullPointerException ex) { cValor = "0000000000"; }

        Inteiro = cValor.substring(0,8);
        Decimal = cValor.substring(8);

        Valor = Float.valueOf(Inteiro) + (Float.valueOf(Decimal) / 100);

        DecimalFormat v = new DecimalFormat();
        v.applyPattern("#,##0.00");

        return v.format(Valor);
    }

    static public String FormatZeros(String sValor) {
        String zeros = "00000000";
        //System.out.println(sValor.length());

        try {
            sValor = ("".equals(sValor.trim()) || sValor == null ? "0000000000" : sValor);
        } catch (NullPointerException ex) { sValor = "0000000000"; }

        String part1 = sValor.substring(0, 8).trim();
        String part2 = sValor.substring(8,10);

        String part3 = zeros + part1;
        part3 = part3.substring(part1.length() - 1, part3.length());

        zeros = "00";
        String part4 = zeros + part2;
        part4 = part4.substring(part2.length() - 1, part4.length());

        return part3 + part4;
    }

    static public String FormatNumber(String cValor, int decimal) {
        String Inteiro;
        String Decimal;
        Float Valor;

        if (cValor.trim().length() != 10) return "0";
        
        // Adaptado para numeros negativos que vem do caixa (17/08/2011)
        cValor = cValor.replace("-", "0");
        
        try {
            cValor = ("".equals(cValor.trim()) || cValor == null ? "0000000000" : cValor);
        } catch (NullPointerException ex) { cValor = "0000000000"; }

        Inteiro = cValor.substring(0,(10 - decimal));
        Decimal = cValor.substring(10 - decimal);

        DecimalFormat v = new DecimalFormat();
        if (decimal > 0) {
            Valor = Float.valueOf(Inteiro) + (Float.valueOf(Decimal) / (int)Math.pow((double)10,(double)decimal));
            v.applyPattern("#,##0." + StrZero("0", decimal));
        } else {
            Valor = Float.valueOf(Inteiro);
            v.applyPattern("##");
        }
        return v.format(Valor);
    }

    static public float StringToFloat(String Valor) {
        String retorno = "0.00";
        if (Valor != null) {
            if (!Valor.trim().equals("")) {
                String newNumber = Valor.replace(".", "");
                newNumber = newNumber.replace(",", ".");
                retorno = newNumber;
            }
        }
        
        return Float.valueOf(retorno);
    }

    static public String FloatToString(float Valor) {
        String newNumber = String.valueOf(Valor);
        newNumber = newNumber.replace(".", ",");
        
        return newNumber;
    }

    static public String floatToCurrency(float cValor, int decimal) {
        DecimalFormat v = new DecimalFormat();
        if (decimal > 0) {
            v.applyPattern("#,##0." + StrZero("0", decimal));
        } else {
            v.applyPattern("##");
        }
        return v.format(cValor);
    }
    
     public static boolean isNumeric (String s) {  
         try {  
             Long.parseLong (s);  
         } catch (NumberFormatException ex) {  
             return false;  
         }  
         return true;  
     }  
     
     public static float FloatNumber(String number, int decimal) {
         if (number.trim().length() != 10) return 0;
         
         String auxNumber = FormatNumber(number, decimal);
         return StringToFloat(auxNumber);
     }
     
     // Aqui estamos vendo se o valor é monetário (exemplo: 12.345,67)  
     // e em notação brasileira. A declaração de "brazilianCurrencyFormat"  
     // abaixo é um pouco complexa, mas é necessária para evitar problemas  
     // em servlets, JSPs e outros ambientes "multi-threaded".  
     private static ThreadLocal<NumberFormat> brazilianCurrencyFormat = new ThreadLocal<NumberFormat> () {  
         @Override protected NumberFormat initialValue() {  
             return new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));  
         }  
     };  

     public static boolean isCurrency (String s) {  
         s = s.trim();  
         ParsePosition pos = new ParsePosition (0);  
                     brazilianCurrencyFormat.get().parse(s, pos);  
         return pos.getIndex() == s.length();  
     }  

    public static String StrZero(String valor, int Tam) {
        String tmpValor = valor.replace(".0", "").replace(" ", "").replace(",", "");
        int i = 0; String zeros = Repete("0", Tam);
        String part1 = zeros + tmpValor;

        return part1.substring(part1.length() - Tam);
    }

    public static String Repete(String texto, int length) {
        StringBuffer retorno = new StringBuffer();
        for (int i=1; i<=length;i++) {
            retorno.append(texto);
        }
        return retorno.toString();
    }
    
}
