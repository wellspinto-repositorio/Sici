/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

/**
 * Classe de tratamento de String
 * @author supervisor
 */
public class Pad {
    private String texto;
    private int tamanho;
    private String full;

    /**
     *
     */
    public Pad() {
        OptionalVar("",80," ");
    }

    /**
     *
     * @param txt - String contendo texto a ser formatado
     */
    public Pad(String txt) {
        OptionalVar(txt, 80, " ");
    }

    /**
     *
     * @param txt - String contendo texto a ser formatado
     * @param tam - Tamanho do restorno da String
     */
    public Pad(String txt, int tam) {
        OptionalVar(txt, tam, " ");
    }

    /**
     *
     * @param txt - String contendo texto a ser formatado
     * @param tam - Tamanho do restorno da String
     * @param tfull - String de preenchimento
     */
    public Pad(String txt, int tam, String tfull) {
        OptionalVar(txt, tam, tfull);
    }

    /**
     *
     * @param txt - String contendo texto a ser formatado
     * @param tam - Tamanho do restorno da String
     * @param tfull - String de preenchimento
     */
    public void OptionalVar(String txt, int tam, String tfull) {
        texto = ((txt.trim() != "") ? txt : " " );
        tamanho = ((tam > 0) ? tam : 1);
        full = ((tfull.trim() != "") ? tfull : "");
    }

    /**
     * Retorna uma String com tamanho n e preenchida com char a direita
     * @return
     */
    public String RPad() {
        String retorno = texto.trim().concat(repete(full,tamanho));

        return retorno.substring(0, tamanho);
    }

    /**
     * Retorna uma String com tamanho n e preenchida com char a esquerda
     * @return
     */
    public String LPad() {
        String retorno = repete(full,tamanho) + texto.trim();
        int rlength = retorno.length();
        int tlength = tamanho;
        return retorno.substring(rlength - tlength, rlength);
    }

    /**
     * Retorna uma String com tamanho n com char a direita e esquerda centralizada
     * @return
     */
    public String CPad() {
        int tfull = tamanho - texto.trim().length();
        int tfullmod = tfull % 2;
        int lfull = (tfull / 2) + tfullmod;
        int rfull = (tfull / 2);
        String rtexto = repete(full,lfull) + texto.trim() + repete(full,rfull);
        return rtexto.substring(0, tamanho);
    }

    private String repete(String texto, int length) {
        StringBuffer retorno = new StringBuffer();
        for (int i=1; i<=length;i++) {
            retorno.append(texto);
        }
        return retorno.toString();
    }
}

/**
 * teste de pad
 * import Funcoes.Pad;

        Pad txt = new Pad("Wellington",80,"*");
        System.out.println(txt.RPad());
        System.out.println(new Pad("pinto",80,"$").RPad());

 */
