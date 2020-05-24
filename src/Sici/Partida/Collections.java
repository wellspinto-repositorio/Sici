/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Sici.Partida;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author supervisor
 */
public class Collections {
    private List<String[]> lista = new ArrayList<String[]>();

    public void add(String chave, String conteudo) {
        String[] b = {chave,conteudo};
        lista.add(b);
    }

    public void remove(String chave) {
        int pos = indexOf(chave);
        if (pos > -1) lista.remove(pos);
    }

    public String get(String chave) {
        String Retorno = "";
        Iterator<String[]> iLista = lista.iterator();
        synchronized (iLista) {
            while (iLista.hasNext()) {
                String[] items = iLista.next();
                if (items[0].equals(chave)) { Retorno = items[1]; break; }
                //for (String item: items) {
                //    System.out.println(item);
                //}
            }
        }
        return Retorno;
    }

    public int indexOf(String chave) {
        int Retorno = -1; int i = 0;
        Iterator<String[]> iLista = lista.iterator();
        synchronized (iLista) {
            while (iLista.hasNext()) {
                String[] items = iLista.next();
                if (items[0].equals(chave)) { Retorno = i; break; }
                i++;
            }
        }
        return Retorno;
    }

    public String key(int pos) {
        String Retorno = ""; int i = 0;
        Iterator<String[]> iLista = lista.iterator();
        synchronized (iLista) {
            while (iLista.hasNext()) {
                String[] items = iLista.next();
                if (i == pos) {Retorno = items[0]; break; }
                i++;
            }
        }
        return Retorno;
    }
    
    public void set(String skey, String value) {
        int keypos = indexOf(skey);
        if (keypos > -1) {
            lista.set(keypos, new String[] {skey, value});
        }
    }
}
