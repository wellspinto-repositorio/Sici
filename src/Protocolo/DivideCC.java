/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Protocolo;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author supervisor
 */
public class DivideCC {
    static DbMain conn = VariaveisGlobais.conexao;
    
    static public ArrayList<String[]> Dividir(String rgimv) throws SQLException {

        String[][] regFields;
        ArrayList<String[]> r = new ArrayList<String[]>();

        String[] c, d;
        int i; float t = 0;

        regFields = conn.LerCamposTabela(new String[] {"rgprp", "rgimv", "benefs"}, "divisao", "rgimv = '" + rgimv + "'");
        if (regFields != null) {
            if (regFields.length > 0) {
                c = regFields[2][3].split(";");
                for (i=0; i <= c.length - 1; i++) {
                    d = c[i].split(":");

                    String[] tmp = {d[0], d[1]};
                    r.add(tmp);

                    t += LerValor.StringToFloat(d[1]);
                }
            }
            if (100 - t > 0) {
                String[] tmp = {regFields[0][3], String.valueOf(100 - t)};
                r.add(0, tmp);
            }
        } else {
            regFields = conn.LerCamposTabela(new String[] {"rgprp"}, "imoveis", "rgimv = '" + rgimv + "'");
            String[] tmp = {regFields[0][3], String.valueOf(100)};
            r.add(0, tmp);
        }

        return r;
    }

    static public String[][] Divisao(String rgimv) throws SQLException {
        String[][] regFields;
        String[][] r = {};
        String[] c, d;
        int i; float t = 0;

        regFields = conn.LerCamposTabela(new String[] {"rgprp", "rgimv", "benefs", "divtudo"}, "divisao", "rgimv = '" + rgimv + "'");
        String divtudo = "FALSE";
        if (regFields != null) {
            divtudo = (regFields[3][3].trim().equals("1") ? "TRUE" : "FALSE");
            if (100 - t > 0) {
                String[] tmp = {regFields[0][3], String.valueOf(0), divtudo};
                r = FuncoesGlobais.ArraysAdd(r, tmp);  
            }
            if (regFields.length > 0) {
                c = regFields[2][3].split(";");
                for (i=0; i <= c.length - 1; i++) {
                    d = c[i].split(":");

                    String[] tmp = {d[0], d[1], divtudo};
                    r = FuncoesGlobais.ArraysAdd(r, tmp);

                    t += LerValor.StringToFloat(d[1]);
                }
            }
            if (100 - t > 0) {
                r[0][1] = LerValor.FloatToString(100 - t);
            }

        } else {
            regFields = conn.LerCamposTabela(new String[] {"rgprp"}, "imoveis", "rgimv = '" + rgimv + "'");
            String[] tmp = {regFields[0][3], String.valueOf(100), divtudo};
            r = FuncoesGlobais.ArraysAdd(r, tmp);
        }

        return r;
    }
}
