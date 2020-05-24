/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author supervisor
 */
public class FuncoesGlobais {

    public FuncoesGlobais() {
    }

    public static int IndexOf(String aString[], String sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (aString[i].contains(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int OfIndex(String aString[], String sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (aString[i].contentEquals(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int IndexOf2(String aString[], String sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (sOque.contains(aString[i])) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int IndexOfPart(String aString[], String sOque, int pos1, int pos2) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (aString[i].substring(pos1, pos2).equals(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int IndexOfn(String aString[], String sOque, int pos) {
        int i =  (pos <=0 ? 0 : pos);
        boolean achei = false;
        int retorno = -1;

        for (; i <= aString.length - 1; i++) {
            if (aString[i].contains(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int FindinArrays(String[][] marray, int coluna, String oque) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            if (marray[i][coluna].contains(oque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static String DecriptaNome(String cNome) {
        String cRetorno = "";
        String auxVar;
        int nConta;

        auxVar = cNome.trim().toUpperCase();
        if (auxVar.length() > 0) {
            for (nConta = 1; nConta <= auxVar.length(); nConta += 2) {
                String _letra = auxVar.substring(nConta - 1, (nConta - 1) + 2);
                String _numer[] = "99;93;01;05;11;18;92;00;04;10;17;95;98;96;13;60;94;02;06;12;19;96;03;07;14;20".split(";");
                String _acent[] = "ç;á;é;í;ó;ú;à;è;ì;ò;ù;ã;ẽ;ĩ;õ;ũ;â;ê;î;ô;û;ä;ë;ï;ö;ü".split(";");
                int pos = IndexOf(_numer, _letra);
                if (pos < 0) {
                    cRetorno += (char) Integer.parseInt(_letra);
                } else {
                    cRetorno += _acent[pos].toUpperCase();
                }
            }

            cRetorno = cRetorno.substring(0,1).toUpperCase() + cRetorno.substring(1).toLowerCase();
        }

        return ("".equals(cRetorno.trim()) ? null : cRetorno);
    }

    public static String CriptaNome(String cNome) {
        String cRetorno = ""; int i = 0;
        for (i=0;i<=cNome.length() - 1; i++) {
            char c = cNome.toUpperCase().charAt(i);
            int asc = (int) c;
            cRetorno += StrZero(String.valueOf(asc), 2);
        }
        return cRetorno;
    }

    public static String[] ArrayAdd(String[] mArray, String value) {
        String[] temp = new String[mArray.length+1];

        System.arraycopy(mArray,0,temp,0,mArray.length);

        temp[mArray.length] = value;

        return temp;
    }

    public static String[] ArrayAddPos(String[] mArray, String value, int pos) {
        String[] temp = new String[mArray.length+1];

        int w = 0;
        for (int i=0;i<temp.length;i++) {
            if (i != pos) {
                temp[i] = mArray[w];
                w += 1;
            }
        }
        temp[pos] = value;

        return temp;
    }

    public static String[] ArrayDel(String[] array, int index) {
            ArrayList list = CreateStringList(array);
            list.remove(index);
            return ConvertToStringArray(list);
    }

    public static String[][] ArraysAdd(String[][] mArray, String[] value) {
        String[][] temp = new String[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i];
        }
        return temp;
    }

    public static String[][] ArraysDel(String[][] array, int index) {
        if (array.length == 0) return null;

        String[][] temp = {};
        for (int i=0; i <= array.length - 1; i++) {
            if (i != index) {
                temp = ArraysAdd(temp, array[i]);
            }
        }

        return temp;
    }

    public static String[][] ArraysDelSub(String[][] array, int index, int pos) {
        if (array.length == 0) return null;

        String[][] temp = {};
        for (int i=0; i <= array.length - 1; i++) {
            if (i != index) {
                temp = ArraysAdd(temp, array[i]);
            } else {
                temp = ArraysAdd(temp, ArrayDel(array[i], pos));
            }
        }

        return temp;
    }

    public static Object[][] ObjectsAdd(Object[][] mArray, Object[] value) {
        Object[][] temp = new Object[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i];
        }
        return temp;
    }

    public static int FindinObjects(Object[][] marray, int coluna, String oque) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            String aonde = String.valueOf(marray[i][coluna]);
            if (aonde.contains(oque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }
    
    public static int FindNinObjects(Object[][] marray, int[] coluna, String[] oque) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            boolean aonde1 = true;
            for (int n=0;n<coluna.length;n++) {
                aonde1 =  aonde1 && marray[i][coluna[n]].equals(oque[n]) ;
            }
            
            if (aonde1) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static String[][] ArraySuperAdd(String[][] mArray, String[][] value) {
        String[][] temp = new String[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i][0];
        }
        return temp;
    }

    public static ArrayList<String> CreateStringList(String ... values)
    {
        ArrayList<String> results = new ArrayList<String>();
        Collections.addAll(results, values);
        return results;
    }

    public static String[] ConvertToStringArray(ArrayList list)
    {
        return (String[])list.toArray(new String[0]);
    }

    public static String join(String[] s, String delimiter) {
        if (s.length <= 0) return "";
        int i = 0;
        String sRet = "";
        for (i=0;i<=s.length - 1;i++) {
            sRet += s[i] + delimiter;
        }
        return sRet.substring(0, sRet.length() - delimiter.length());
    }

    public static String GravaValor(String valor) {
        String tmpValor = valor.replace(".", "");
        tmpValor = tmpValor.replace(" ", "");

        int ponto = tmpValor.indexOf(",");
        String part1 = "00000000" + tmpValor.substring(0, ponto);
        String part2 = tmpValor.substring(ponto + 1) + "0";

        return part1.substring(part1.length() - 8, part1.length()) + part2.substring(0,2);
    }

    public static int OcourCount(String valor, String oQue, int nQtd) {
        int i = 0; int j = 0; boolean achei = false;
        for (i=0; i <= valor.length() - 1; i++) {
            if (valor.substring(i, i + oQue.length()).equals(oQue)) j++;
            if (j == nQtd) {achei = true; break;}
        }
        return (achei ? i : -1);
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

    public static float strCurrencyToFloat(String Value)
    {
        if (Value.length() == 0)
            return 0;
        else
            return Float.parseFloat(Value.replace(" ", "").replace(".", "").replace(",", "."));
    }

    public static String GravaValores(String valor, int decimal) {
        String tmpValor = valor.replace(".", "");
        tmpValor = tmpValor.replace(" ", "");

        int ponto = tmpValor.indexOf(",");
        String part1 = StrZero("0", 10 - decimal) + (decimal > 0 ? tmpValor.substring(0, ponto) : valor);
        String part2 = "";
        if (decimal > 0) {
            part2 = tmpValor.substring(ponto + 1);
            if (part2.length() < decimal) part2 += Repete("0", decimal - part2.length());
        } else {
            part2 = "";
        }

        return part1.substring(part1.length() - (10 - decimal), part1.length()) + ("".equals(part2) ? "" : part2.substring(0,decimal));
    }

    public static String[] OrdenaMatriz(String[] Vetor, int Inicio, int Final, boolean eNumero) {
        VariaveisGlobais.Inicio = Inicio; VariaveisGlobais.Final = Final;
        Arrays.sort(Vetor, new Comparator()
        {
        private int pos1 = VariaveisGlobais.Inicio;
        private int pos2 = VariaveisGlobais.Final;
        public int compare(Object o1, Object o2) {
            String p1 = ((String)o1).substring(pos1, pos1 + pos2);
            String p2 = ((String)o2).substring(pos1, pos1 + pos2);
            return p1.compareTo(p2);
        }
        });

        return Vetor;
    }

    public static String[] ComboLista(JComboBox oBox) {
        String[] lista = {};
        for (int i=0; i< oBox.getItemCount(); i++) {
            lista = ArrayAdd(lista, oBox.getItemAt(i).toString());
        }
        return lista;
    }

    public static String[][] treeArray(String cVariavel, boolean bWithNameField) throws SQLException {
        if (cVariavel == null) return null;
        Sici.Partida.Collections gVar = VariaveisGlobais.dCliente;
        String[][] tArray = {}; String[] _2Array;

        String[] _1Array = cVariavel.split(";");
        if (_1Array.length > 0) {
            for (int i=0; i<_1Array.length; i++) {
                _2Array = _1Array[i].split(":");
                if (bWithNameField) {
                    String sSql = "SELECT CART_CODIGO, CART_DESCR, CART_ORDEM, CART_COTPAR " + "FROM LANCART WHERE CART_CODIGO = '" + _2Array[0] + "';";
                    DbMain conn = VariaveisGlobais.conexao;
                    ResultSet hResult = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
                    if (hResult.next()) {
                        _2Array = FuncoesGlobais.ArrayAdd(_2Array, hResult.getString("CART_DESCR"));
                    } else {
                        if ("DC".equals(_2Array[0])) {
                            if (IndexOf(_2Array, "DS") > -1) {
                                String tvar = gVar.get("DC") + DecriptaNome(_2Array[IndexOf(_2Array, "DS")].substring(2));
                                _2Array = ArrayAdd(_2Array, tvar);
                            } else _2Array = ArrayAdd(_2Array, gVar.get("DC"));
                        } else if ("DF".equals(_2Array[0])) {
                            if (IndexOf(_2Array, "DS") > -1) {
                                String tvar = gVar.get("DF") + DecriptaNome(_2Array[IndexOf(_2Array, "DS")].substring(2));
                                _2Array = ArrayAdd(_2Array, tvar);
                            } else _2Array = ArrayAdd(_2Array, gVar.get("DF"));
                        } else if ("SG".equals(_2Array[0])) {
                            _2Array = ArrayAdd(_2Array, gVar.get("SG"));
                        } else if ("AV".equals(_2Array[0])) {
                            //_2Array = ArrayAdd(_2Array, _2Array[6].substring(_2Array.length - 2, _2Array.length) + _2Array[8]);
                            _2Array = ArrayAdd(_2Array, _2Array[6].substring(_2Array[6].length() - 2, _2Array[6].length()) + _2Array[8]);
                        } else if ("SD".equals(_2Array[0])) {
                            _2Array = ArrayAdd(_2Array, "SD" + _2Array[8] + _2Array[6].substring(_2Array.length - 2, _2Array.length));
                        } else if ("GG".equals(_2Array[0])) {
                            _2Array = ArrayAdd(_2Array, "GG" + _2Array[8] + _2Array[6].substring(_2Array.length - 2, _2Array.length));
                        }
                    }
                    DbMain.FecharTabela(hResult);
                    ///////conn.FecharConexao();
                }
                tArray = ArraysAdd(tArray, _2Array);
            }
        } else tArray = null;

        return tArray;
    }

    public static String[] treeSeekArray(String[][] aValor, String sBuscar, int nStart) {
        String espCampos = "MU;JU;CO;EP;CM";
        boolean bEsp = espCampos.indexOf(sBuscar) > -1;
        String[] r = {"-1","-1",null,null};
        if (aValor.length <= 0) return r;
        int nPos = -1;
        for (int i=nStart;i<aValor.length;i++) {
            if (bEsp) {
                nPos = -1;
                for (int f=0; f<aValor[i].length;f++) {
                    if (aValor[i][f].indexOf(sBuscar) > -1) {
                        nPos = f;
                        break;
                    }
                }
            } else nPos = IndexOf(aValor[i], sBuscar);
            if (nPos >= 0) {
                r = new String[] {String.valueOf(i), String.valueOf(nPos)};
                if (bEsp) {
                    r = ArrayAdd(r, aValor[i][nPos].substring(0, 2));
                    if (aValor[i][nPos].length() >= 12) {
                        r = ArrayAdd(r, LerValor.FormatNumber(aValor[i][nPos].substring(aValor[i][nPos].length() - 10), 2));
                    } else {
                        r = ArrayAdd(r, LerValor.FormatNumber("0,00", 2));
                    }
                } else {
                    r = ArrayAdd(r, aValor[i][4]);
                    r = ArrayAdd(r, LerValor.FormatNumber(aValor[i][2],2));
                }
                if (!bEsp) {
                    boolean isALfield = FuncoesGlobais.IndexOf(aValor[i],"AL") > -1;
                    r = ArrayAdd(r, (!isALfield ? "BR" : "LQ"));
                    //r = ArrayAdd(r, (!"AL".equals(aValor[i][5]) ? "BR" : "LQ"));
                }
                break;
            }
        }
        return r;
    }

    public static String[] treeSeekArray2(String[][] aValor, String sBuscar, int nStart, int nStop) {
        String espCampos = "MU;JU;CO;EP;CM";
        boolean bEsp = espCampos.indexOf(sBuscar) > -1;
        String[] r = {"-1","-1",null,null};
        if (aValor.length <= 0) return r;
        int nPos = -1;
        for (int i=nStart;i<=nStop;i++) {
            if (bEsp) {
                nPos = -1;
                for (int f=0; f<aValor[i].length;f++) {
                    if (aValor[i][f].indexOf(sBuscar) > -1) {
                        nPos = f;
                        break;
                    }
                }
            } else nPos = IndexOf(aValor[i], sBuscar);
            if (nPos >= 0) {
                r = new String[] {String.valueOf(i), String.valueOf(nPos)};
                if (bEsp) {
                    r = ArrayAdd(r, aValor[i][nPos].substring(0, 2));
                    if (aValor[i][nPos].length() >= 12) {
                        r = ArrayAdd(r, LerValor.FormatNumber(aValor[i][nPos].substring(aValor[i][nPos].length() - 10), 2));
                    } else {
                        r = ArrayAdd(r, LerValor.FormatNumber("0,00", 2));
                    }
                } else {
                    r = ArrayAdd(r, aValor[i][4]);
                    r = ArrayAdd(r, LerValor.FormatNumber(aValor[i][2],2));
                }
                if (!bEsp) {
                    boolean isALfield = FuncoesGlobais.IndexOf(aValor[i],"AL") > -1;
                    r = ArrayAdd(r, (!isALfield ? "BR" : "LQ"));
                    //r = ArrayAdd(r, (!"AL".equals(aValor[i][5]) ? "BR" : "LQ"));
                }
                break;
            }
        }
        return r;
    }

    public static String SuperJoin(String[][] aVariavel, boolean bPrincipal) {
        String[] t = {};
        String r = "";

        for (int i=0; i<aVariavel.length;i++) {
            if (bPrincipal) {
                t = ArrayAdd(t, join(aVariavel[i], ":"));
            } else {
                if (i == 0) {
                    t = ArrayAdd(t, join(aVariavel[i], ":"));
                }
                if (IndexOf(aVariavel[i], "AL") > -1 && i > 0) {
                    t = ArrayAdd(t, join(aVariavel[i], ":"));
                }
            }
        }

        r = join(t, ";");
        return r;
    }

    public static String Subst(String Variavel, String[] Conteudos) {
        String retorno = Variavel;
        if (Conteudos.length > 0) {
            for (int i=0;i<Conteudos.length;i++) {
                retorno = retorno.replace("&" + String.valueOf(i + 1).trim() + ".", Conteudos[i]);
            }
        }

        return retorno;
    }

    public static String Choose(int pos, String[] Itens) {
        return Itens[pos];
    }

    public static String ValidaProtocol(String value) {
        String retorno = "";
        int pos = value.indexOf("AL");
        int pos2 = value.indexOf("AL", pos + 1);
        if (pos > -1 && pos2 > -1) { retorno = "Erro!!!\nDois campos principais na mesma sentença!!!"; }
        
        return retorno;
    }
    
    public static void SelectAllInFormattedTextField(java.awt.event.FocusEvent evt) {
        try{
            if (evt.getSource() instanceof JFormattedTextField){
                        final JFormattedTextField jftf =
                                (JFormattedTextField) evt.getSource();
                           SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                jftf.selectAll();
                            }});
                    } else if (evt.getSource() instanceof JTextField){
                        JTextField jtf = (JTextField) evt.getSource();
                        jtf.selectAll();
                    }

                } catch( Exception ex) { }

    }
    
    public static String[][] treeRegra(String cVariavel, boolean bWithNameField) throws SQLException {
        DbMain conn = VariaveisGlobais.conexao;
        if (cVariavel == null) return null;
        Sici.Partida.Collections gVar = VariaveisGlobais.dCliente;
        String[][] tArray = {}; String[] _2Array;

        String[] _1Array = cVariavel.split(";");
        if (_1Array.length > 0) {
            for (int i=0; i<_1Array.length; i++) {
                _2Array = _1Array[i].split(":");
                if (bWithNameField) {
                    String sSql = "SELECT CART_CODIGO, CART_DESCR, CART_ORDEM, CART_COTPAR " + "FROM LANCART WHERE CART_CODIGO = '" + _2Array[0] + "';";
                    ResultSet hResult = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
                    if (hResult.next()) {
                        _2Array = FuncoesGlobais.ArrayAdd(_2Array, hResult.getString("CART_DESCR"));
                    } else {
                        if        ("DC".equals(_2Array[0])) {
                             _2Array = ArrayAdd(_2Array, "Desconto");
                        } else if ("DF".equals(_2Array[0])) {
                             _2Array = ArrayAdd(_2Array, "Diferenca");
                        } else if ("SG".equals(_2Array[0])) {
                            _2Array = ArrayAdd(_2Array, gVar.get("SG"));
                        } else if ("MU".equals(_2Array[0])) {
                            _2Array = ArrayAdd(_2Array, gVar.get("MU"));
                        } else if ("JU".equals(_2Array[0])) {
                            _2Array = ArrayAdd(_2Array, gVar.get("JU"));
                        } else if ("CO".equals(_2Array[0])) {
                            _2Array = ArrayAdd(_2Array, gVar.get("CO"));
                        } else if ("EP".equals(_2Array[0])) {
                            _2Array = ArrayAdd(_2Array, gVar.get("EP"));
                        }
                    }
                    DbMain.FecharTabela(hResult);
                }
                tArray = ArraysAdd(tArray, _2Array);
            }
        } else tArray = null;

        return tArray;
    }    

    public static int FindInRegras(String[][] marray, int coluna, String oque, int inicio, int fim) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false; int nfim = fim;
        for (i=0;i<marray.length;i++) {
            if (nfim > marray[i][coluna].length()) { nfim = marray[i][coluna].length(); } else {nfim = fim;}
            if (marray[i][coluna].substring(inicio, nfim).contains(oque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int RAt(String value, String oque) {
        String newValue = value.trim();
        int i = newValue.trim().length();
        int z = i; int retorno = -1;
        for (;z > 0; z--) {
            if (newValue.substring(z - 1, z).equalsIgnoreCase(oque)) {retorno = z; break;}
        }
        return retorno;
    }
    
    public static String Space(int value) {
        String espaco = "";
        for (int i=1;i<=value;i++) {
            espaco += " ";
        }
        return espaco;
    }
    
    public static String myLetra(String cword) {
        String iLetras = "à;è;ì;ò;ù;ã;õ;â;ê;î;ô;û;á;é;í;ó;ú;ä;ë;ï;ö;ü;ç;À;È;Ì;Ò;Ù;Ã;Õ;Â;Ê;Î;Ô;Û;Á;É;Í;Ó;Ú;Ä;Ë;Ï;Ö;Ü;Ç;.;:;-;,;ª;º;°;²;³;§";
        String oLetras = "a;e;i;o;u;a;o;a;e;i;o;u;a;e;i;o;u;a;e;i;o;u;c;A;E;I;O;U;A;O;A;E;I;O;U;A;E;I;O;U;A;E;I;O;U;C; ; ; ; ;a;o;o;2;3;S";
        
        String[] aiLetras = iLetras.split(";"); String[] aoLetras = oLetras.split(";");
        for (int i=0;i<aiLetras.length;i++) {
            cword = cword.replace(aiLetras[i], aoLetras[i]);
        }
        
        return cword;
    }   
    
    public static String rmvNumero(String value) {
        if (value == null) return "";
        String ret = "";
        for (int i=0;i<value.length();i++) {
            if (value.substring(i, i + 1).equalsIgnoreCase(".") || 
                value.substring(i, i + 1).equalsIgnoreCase("/") || 
                value.substring(i, i + 1).equalsIgnoreCase("-") || 
                value.substring(i, i + 1).equalsIgnoreCase(",") || 
                value.substring(i, i + 1).equalsIgnoreCase(" ") ||
                value.substring(i, i + 1).equalsIgnoreCase(")") || 
                value.substring(i, i + 1).equalsIgnoreCase("(")) {
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }
    
    public static boolean ValidarCPFCNPJ(String nDOC) {
        nDOC = rmvNumero(nDOC);
        if (nDOC.isEmpty()) return false;
        if (nDOC.equalsIgnoreCase("11111111111") || nDOC.equalsIgnoreCase("11111111111111") ||
            nDOC.equalsIgnoreCase("22222222222") || nDOC.equalsIgnoreCase("22222222222222") ||
            nDOC.equalsIgnoreCase("33333333333") || nDOC.equalsIgnoreCase("33333333333333") ||
            nDOC.equalsIgnoreCase("44444444444") || nDOC.equalsIgnoreCase("44444444444444") ||
            nDOC.equalsIgnoreCase("55555555555") || nDOC.equalsIgnoreCase("55555555555555") ||
            nDOC.equalsIgnoreCase("66666666666") || nDOC.equalsIgnoreCase("66666666666666") ||
            nDOC.equalsIgnoreCase("77777777777") || nDOC.equalsIgnoreCase("77777777777777") ||
            nDOC.equalsIgnoreCase("88888888888") || nDOC.equalsIgnoreCase("88888888888888") ||
            nDOC.equalsIgnoreCase("99999999999") || nDOC.equalsIgnoreCase("99999999999999")) return false;
         
        int tamanho;
        String numero;
        int tammax;
        boolean result = false;
        
        numero = nDOC.trim();
        tammax = ((numero.trim().length() <= 11) ? 11 : 14);
        tamanho = numero.trim().length();
        boolean badFormat = false;
        result = true;
         
        if (tamanho == 0) {
            badFormat = true;
        }

        if (tamanho != tammax) {
            badFormat = true;
        }

        String dig1 = Modulo11.Verifica(numero.substring(0,tamanho - 2));
        String dig1v = numero.substring(tamanho - 2, tamanho - 1);
        String dig2 = Modulo11.Verifica(numero.substring(0,tamanho - 1));
        String dig2v = numero.substring(tamanho - 1, tamanho);
        if (!dig1.equals(dig1v)) {
            badFormat = true;
        }

        // verifica 2o. digito
        if (!dig2.equals(dig2v)) {
            badFormat = true;
        }

        if (badFormat) {
            result = false;
            JOptionPane.showMessageDialog(null, "Valor inválido!!!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else result = true;

        return result;
    }
}

