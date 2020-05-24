/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Bancos;

import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import Funcoes.Pad;

/**
 *
 * @author supervisor
 */
public class bb {
    
    static public String CodBar(String vencimento,String valor,String nossonumero) {
        String part1; String part2; String dv;
        part1 = bancos.getBanco() + bancos.getMoeda();
        part2 = FatorVencimento("10/07/1997", vencimento) + bancos.Valor4Boleta(valor) + nossonumero.substring(0, 11) + 
                bancos.getAgencia() + FuncoesGlobais.StrZero(bancos.getCtaDv(),8) + bancos.getCarteira();
        dv = CalcDig11N(part1 + part2); 
        return part1 + dv + part2;
    }

    static public String LinhaDigitavel(String codbar, String codbardv, String vencimento, String valortitulo) {
        String campo1, campo2, campo3, campo4, campo5;
        
        campo1 = bancos.getBanco() + bancos.getMoeda() + codbar.substring(19, 24);
        campo1 += bancos.CalcDig10(campo1);
        campo1 = campo1.substring(0, 5) + "." + campo1.substring(5,10);
        
        campo2 = codbar.substring(24, 34);
        campo2 += bancos.CalcDig10(campo2);
        campo2 = campo2.substring(0,5) + "." + campo2.substring(5, 11);
        
        campo3 = codbar.substring(34, 44);
        campo3 += bancos.CalcDig10(campo3);
        campo3 = campo3.substring(0, 5) + "." + campo3.substring(5, 11);
        
        campo4 = codbardv;
        
        campo5 = FatorVencimento("10/07/1997", vencimento) + bancos.Valor4Boleta(valortitulo);
        
        return campo1 + "  " + campo2 + "  " + campo3 + "  " + campo4 + "  " + campo5;
    }    
    
    static public String NossoNumeroBB(String conta, String value) {
        String valor1 = new Pad(conta,6).RPad() + new Pad(value,5).RPad();
        String valor2 = CalcDig11N(valor1);
        return valor1 + valor2;
    }
    
    static public String FatorVencimento(String fator, String vencimento) {
        String retorno = "0000";
        if (vencimento.length() < 8) {retorno = "0000";} else {
            retorno = String.valueOf(Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "MM/dd/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
        }
        
        return retorno;
    }

    static public String CalcDig11N(String cadeia) {
        int total= 0; int mult = 2;
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 9) mult = 2;
        }
        int soma = total; // * 10;
        int resto = (soma % 11);
        if (resto == 0 || resto == 1) { 
            resto = 0; 
        } else if (resto >= 10) {
            resto = 1; 
        } else {
            resto = 11 - resto;
        }
        return String.valueOf(resto);
    }
    
}

