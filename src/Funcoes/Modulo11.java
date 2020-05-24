/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

/**
 *
 * @author supervisor
 */
public class Modulo11 {
    public static String Verifica(String numero) {
        int multiplicador = 2;
        int produto = 0;
        int digito = 0;
        int i = 0;
        int tam = numero.trim().length() - 1;

        for (i=tam; i >= 0; i--) {
            produto += Integer.parseInt(numero.substring(i,i + 1)) * multiplicador;
            multiplicador = ((numero.trim().length() > 11 && multiplicador == 9) ? 2 : multiplicador + 1);
        }

        digito = 11 - (produto % 11);
        digito = ((digito > 9) ? 0 : digito);

        return Integer.toString(digito).trim();
    }

}
