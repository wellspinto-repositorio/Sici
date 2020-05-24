/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Bancos;

import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import Funcoes.StringManager;

/**
 *
 * @author supervisor
 */
public class bradesco {
    static public String CodBar(String vencimento,String valor,String nossonumero) {
        String part1; String part2; String part3; String dv;
        part1 = bancos.getBanco() + bancos.getMoeda();
        part2 = FatorVencimento("10-07-1997", vencimento) + bancos.Valor4Boleta(valor);
        part3 = bancos.getAgencia() + bancos.getCarteira() + nossonumero.substring(0, 11) + 
                FuncoesGlobais.StrZero(bancos.getCtaDv(),7) + "0";
        
        String CalcDv = part1 + part2 + part3;
        dv = CalcDig11Bradesco(CalcDv);
        return part1 + dv + part2 + part3;
    }

    static public String LinhaDigitavel(String codbar, String vencimento, String valortitulo) {
        String campo1, campo2, campo3, campo4, campo5;
        String livre = codbar.substring(19);
        
        campo1 = bancos.getBanco() + bancos.getMoeda() + livre.substring(0, 5);
        campo1 += bancos.CalcDig10(campo1);
        campo1 = campo1.substring(0, 5) + "." + campo1.substring(5,10);
        
        campo2 = livre.substring(5, 15);
        campo2 += bancos.CalcDig10(campo2);
        campo2 = campo2.substring(0,5) + "." + campo2.substring(5, 11);
        
        campo3 = livre.substring(15, 25);
        campo3 += bancos.CalcDig10(campo3);
        campo3 = campo3.substring(0, 5) + "." + campo3.substring(5, 11);
        
        campo4 = codbar.substring(4, 5);
        
        campo5 = FatorVencimento("10-07-1997", vencimento) + bancos.Valor4Boleta(valortitulo);
        
        return campo1 + "  " + campo2 + "  " + campo3 + "  " + campo4 + "  " + campo5;
    }        
    
    static private String FatorVencimento(String fator, String vencimento) {
        String retorno = "0000";
        if (vencimento.length() < 8) {retorno = "0000";} else {
            retorno = String.valueOf(Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "MM/dd/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
        }
        
        return retorno;
    }
    
    static public String NossoNumeroBradesco(String value, String cart) {
        String valor1 = cart + StringManager.Right(FuncoesGlobais.StrZero("0", 11) +
                        value,11);
        String valor2 = CalcDig11NNumero(valor1); 
        return valor1.substring(2) + valor2;
    }

    static public String CalcDig11Bradesco(String cadeia) {
        int total= 0; int mult = 2; String ndig = "";
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 9) mult = 2;
        }
        
        int resto = 11 - (total % 11);
        if (resto == 0 || resto == 1 || resto > 9) {
            ndig = "1"; 
        } else ndig = String.valueOf(resto);
        return String.valueOf(ndig);
    }
    
    static public String CalcDig11NNumero(String cadeia) {
        int total= 0; int mult = 2; String ndig = "";
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 7) mult = 2;
        }
        
        int resto = (total % 11);
        if (resto == 0) {
            ndig = "0"; 
        } else if ((11 - resto) == 10) {
            ndig = "1"; //P
        } else ndig = String.valueOf(11 - resto);
        return String.valueOf(resto);
    }
}
