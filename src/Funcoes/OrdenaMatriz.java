/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.util.*;

/**
 *
 * @author supervisor
 */
public class OrdenaMatriz {
    String[][] tab = null;
    String[] tob = null;
    int coluna;
    int pos1, pos2;

    public OrdenaMatriz(String[][] tab, int coluna) {
        this.tab = tab;
        this.coluna = coluna;
    }

    public OrdenaMatriz(String[][] tab, int coluna, int pos1, int pos2) {
        this.tab = tab;
        this.coluna = coluna;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public OrdenaMatriz(String[] tab) {
        this.tob = tab;
    }

    public OrdenaMatriz(String[] tab, int pos1, int pos2) {
        this.tob = tab;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public static String[][] ColunaSimples(String[][] tab, int coluna) {
        String[][] ntab = tab;
        Arrays.sort (ntab, new Comparator<String[]>()
            {
            private int coluna = this.coluna;
                public int compare (String[] linha1, String[] linha2) {
                    return linha1[coluna].compareTo (linha2[coluna]);
                }
            });
        return ntab;
    }

    public static String[][] ColunaSubs(String[][] tab, int coluna, int pos1, int pos2) {
        String[][] ntab = tab;
        Arrays.sort (ntab, new Comparator<String[]>()
            {
            private int coluna = this.coluna;
            private int pos1 = this.pos1;
            private int pos2 = this.pos2;
                public int compare (String[] linha1, String[] linha2) {
                    return linha1[coluna].substring(pos1, pos2).compareTo (linha2[coluna].substring(pos1, pos2));
                }
            });
        return ntab;
    }

    public static String[] LinhaSimples(String[] tab) {
        String[] ntab = tab;
        Arrays.sort (ntab, new Comparator()
            {
            public int compare(Object o1, Object o2) {
                String p1 = (String)o1;
                String p2 = (String)o2;
                return p1.compareTo(p2);
            }
            });
        return ntab;
    }

    public static String[] LinhaSubs(String[] tab, int coluna) {
        String[] ntab = tab;
        Arrays.sort (ntab, new Comparator()
            {
            private int pos1 = this.pos1;
            private int pos2 = this.pos2;
            public int compare(Object o1, Object o2) {
                String p1 = ((String)o1).substring(this.pos1, this.pos2);
                String p2 = ((String)o2).substring(this.pos1, this.pos2);
                return p1.compareTo(p2);
            }
            });
        return ntab;
    }

}
