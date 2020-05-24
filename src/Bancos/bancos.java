/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Bancos;

import Funcoes.*;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JOptionPane;

/**
 *
 * @author supervisor
 */
public class bancos {    
    private static String Agencia = "3402";
    private static String ContaCed = "563350";
    private static String CtaDv = "8";
    private static String Banco = "033";
    private static String BancoDv = "7";
    private static String Cart = "101";
    private static String Moeda = "9";
    private static String Tarifa = "6,00";
    private static String Nnumero = "0000000000";
    private static String Identificacao = "013000516";
    private static String IdentDv = "3";
    private static String Logo = "resources/logoBancos/033.jpg";

    static public String CalcDig10(String cadeia) {
        int mult; int total; int res; int pos;
        mult = (cadeia.length() % 2);
        mult += 1; total = 0;
        for (pos=0;pos<=cadeia.length()-1;pos++) {
            res = Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
            if (res > 9) { res = (res / 10) + (res % 10); }
            total += res;
            if (mult == 2) { mult =1; } else mult = 2;
        }
        total = ((10 - (total % 10)) % 10);
        return  String.valueOf(total);
    }
    
//    static public String CalcDig11N(String cadeia) {
//        int total= 0; int mult = 2;
//        for (int i=1; i<=cadeia.length();i++) {
//            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
//            mult++;
//            if (mult > 9) mult = 2;
//        }
//        int soma = total; // * 10;
//        int resto = (soma % 11);
//        if (resto == 0 || resto == 1) { 
//            resto = 0; 
//        } else if (resto >= 10) {
//            resto = 1; 
//        } else {
//            resto = 11 - resto;
//        }
//        return String.valueOf(resto);
//    }
//     
//    static public String CalcDig11(String cadeia, int limitesup, int lflag) {
//
//        int mult; int total; int nresto; int ndig; int pos;
//        String retorno = "";
//        
//        mult = 1 + (cadeia.length() % (limitesup - 1));
//        if (mult == 1) { mult = limitesup; }
//        
//        total = 0;
//        for (pos=0;pos<=cadeia.length()-1;pos++) {
//            total += Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
//            mult -= 1;
//            if (mult == 1) mult = limitesup;
//        }
//        
//        nresto = (total % 11);
//        if (lflag == 1) { retorno = String.valueOf(nresto); } else {
//            if (nresto == 0 || nresto == 1) {
//                ndig = 0; 
//            } else if (nresto > 9) { 
//                ndig = 1; 
//            } else ndig = 11 - nresto;
//            retorno = String.valueOf(ndig);
//        }
//        return retorno;
//    }

//    static public String CalcDig11Bradesco(String cadeia, int limitesup, int lflag) {
//
//        int mult; int total; int nresto; int ndig; int pos;
//        String retorno = "";
//        
//        mult = 1 + (cadeia.length() % (limitesup - 1));
//        if (mult == 1) { mult = limitesup; }
//        
//        total = 0;
//        for (pos=0;pos<=cadeia.length()-1;pos++) {
//            total += Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
//            mult -= 1;
//            if (mult == 1) mult = limitesup;
//        }
//        
//        nresto = (total % 11);
//        if (lflag == 1) { 
//            if ((11 - nresto) == 0 || (11 - nresto) == 1 || (11 - nresto) > 9) {
//                ndig = 1;
//            } else ndig = 11 - nresto;
//            retorno = String.valueOf(ndig);
//        } else {
//            if (nresto == 0) {
//                ndig = 0;
//            } else if (nresto == 1) {
//                ndig = -1; 
//            } else ndig = 11 - nresto;
//            retorno = String.valueOf(ndig);
//        }
//        return retorno;
//    }
    
    static public String Valor4Boleta(String valor) {
        String valor1 = "0000000000" + valor.replace(" ", "").replace(",", "").replace(".", "").replace("-", "");
        return valor1.substring(valor1.length() - 10, valor1.length());
    }    

//    static public String FatorVencimento(String fator, String vencimento) {
//        String retorno = "0000";
//        // 07/03/2000 - CEF
//        // 07/10/1997 - Itau, Santander
//        
//        if (vencimento.length() < 8) {retorno = "0000";} else {
//            int fat1 = (!"033".equals(Banco) && !"001".equals(Banco) ? 1000 : 0);
//            retorno = String.valueOf(fat1 + Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "MM/dd/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
//        }
//        
//        return retorno;
//    }

//    static public String NossoNumero(String value, int tam) {
//        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
//                        Integer.valueOf(value).toString().trim(),tam - 1);
//        //String valor2 = bancos.CalcDig11(valor1,9,2);
//        String valor2 = bancos.CalcDig11N(valor1);
//        return valor1 + valor2;
//    }

//    static public String NossoNumeroItau(String value, int tam) {
//        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
//                        Integer.valueOf(value).toString().trim(),tam - 1);
//        String valor3 = bancos.getAgencia() + bancos.getConta() + bancos.getCarteira() + valor1;
//        String valor2 = bancos.CalcDig10(valor3);
//        return valor1 + valor2;
//    }
    
//    static public String NossoNumeroBB(String conta, String value) {
//        String valor1 = new Pad(conta,6).RPad() + new Pad(value,5).RPad();
//        String valor2 = bancos.CalcDig11N(valor1); //,9,2);
//        return valor1 + valor2;
//    }

//    static public String NossoNumeroBradesco(String value, String cart) {
//        String valor1 = cart + StringManager.Right(FuncoesGlobais.StrZero("0", 12) +
//                        Integer.valueOf(value).toString().trim(),12);
//        String valor2 = bancos.CalcDig11Bradesco(valor1,7,2);
//        return valor1.substring(2) + valor2;
//    }

    static public void setAgencia(String value) {
        Agencia = value;
    }
    static public String getAgencia() {
        return Agencia;
    }

    static public void setConta(String value) {
        ContaCed = value;
    }
    static public String getConta() {
        return ContaCed;
    }

    static public void setCtaDv(String value) {
        CtaDv = value;
    }
    static public String getCtaDv() {
        return CtaDv;
    }

    static public void setBanco(String value) {
        Banco = value;
    }
    static public String getBanco() {
        return Banco;
    }

    static public void setBancoDv(String value) {
        BancoDv = value;
    }
    static public String getBancoDv() {
        return BancoDv;
    }

    static public void setCarteira(String value) {
        Cart = value;
    }
    static public String getCarteira() {
        return Cart;
    }

    static public void setMoeda(String value) {
        Moeda = value;
    }
    static public String getMoeda() {
        return Moeda;
    }

    static public void setTarifa(String value) {
        Tarifa = value;
    }
    static public String getTarifa() {
        return Tarifa;
    }

    static public void setNnumero(String value) {
        Nnumero = value;
    }
    static public String getNnumero() {
        return Nnumero;
    }

    static public String getIdentificacao() {
        return Identificacao;
    }
    static public void setIdentificacao(String value) {
        Identificacao = value;
    }
    
    static public String getIdentDv() {
        return IdentDv;
    }
    static public void setIdentDv(String value) {
        IdentDv = value;
    }

    static public void setLogo(String value) {
        Logo = "resources/logoBancos/" + value + ".jpg";
    }    
    static public String getLogo() {
        return Logo;
    }    
    
    static public void ReadBanco() {
        DbMain conn = VariaveisGlobais.conexao;

        
        try {setAgencia(conn.LerParametros("AGENCIA").trim());} catch (Exception err) {err.printStackTrace();}
        try {setConta(conn.LerParametros("CONTACED").trim());} catch (Exception err) {err.printStackTrace();}
        try {setCtaDv(conn.LerParametros("CTADV").trim());} catch (Exception err) {err.printStackTrace();}
        try {setBanco(conn.LerParametros("BANCO").trim());} catch (Exception err) {err.printStackTrace();}
        try {setCarteira(conn.LerParametros("CARTEIRA").trim());} catch (Exception err) {err.printStackTrace();}
        try {setMoeda(conn.LerParametros("MOEDA").trim());} catch (Exception err) {err.printStackTrace();}
        try {setTarifa(conn.LerParametros("TARIFA").trim());} catch (Exception err) {err.printStackTrace();}
        try {setLogo(conn.LerParametros("BANCO").trim());} catch (Exception err) {err.printStackTrace();}
                
    }
    
    static public void SaveBanco() {
        DbMain conn = VariaveisGlobais.conexao;
        
        try {conn.GravarParametros(new String[] {"AGENCIA", getAgencia(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.GravarParametros(new String[] {"CONTACED", getConta(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.GravarParametros(new String[] {"CTADV", getCtaDv(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.GravarParametros(new String[] {"BANCO", getBanco(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.GravarParametros(new String[] {"CARTEIRA", getCarteira(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.GravarParametros(new String[] {"MOEDA", getMoeda(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.GravarParametros(new String[] {"TARIFA", getTarifa(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        
    }
    
    static public void LerBanco(String contrato) {
        DbMain conn = VariaveisGlobais.conexao;
        String[][] bancobol = null;
        try {
            bancobol = conn.LerCamposTabela(new String[] {"bcobol"}, "locatarios", "contrato = '" + contrato + "'");
        } catch (Exception e) {e.printStackTrace();}
        if (bancobol != null) {
            String dadosBol[][] = null;
            try {
                dadosBol = conn.LerCamposTabela(
                        new String[] {"agencia","conta","conta_dv","nbanco","nbancodv","carteira","moeda","tarifa","nnumero","identificacao", "identdv"}, 
                        "contas_boletas", 
                        "Trim(nbanco) = '" + bancobol[0][3].trim() + "'"
                        );
            } catch (Exception e) {e.printStackTrace();}
            
            if (dadosBol != null) {
                try {setAgencia(dadosBol[0][3].trim());} catch (Exception err) {}
                try {setConta(dadosBol[1][3].trim());} catch (Exception err) {}
                try {setCtaDv(dadosBol[2][3].trim());} catch (Exception err) {}
                try {setBanco(dadosBol[3][3].trim());} catch (Exception err) {}
                try {setBancoDv(dadosBol[4][3].trim());} catch (Exception err) {}
                try {setCarteira(dadosBol[5][3].trim());} catch (Exception err) {}
                try {setMoeda(dadosBol[6][3].trim());} catch (Exception err) {}
                try {setTarifa(dadosBol[7][3].trim());} catch (Exception err) {}
                try {setLogo(dadosBol[3][3].trim());} catch (Exception err) {}
                
                try {if (dadosBol[9][3] != null) setIdentificacao(dadosBol[9][3].trim());} catch (Exception err) {}
                try {if (dadosBol[10][3] != null) setIdentDv(dadosBol[10][3].trim());} catch (Exception err) {}
                
                try {setNnumero(dadosBol[8][3].trim());} catch (Exception err) {}
            }
        }
    }
    
    static public void GravarNnumero(String nbanco, String Value) {
        DbMain conn = VariaveisGlobais.conexao;
        
        String dadosBol[][] = null; double oldnnumero = -1;
        try { dadosBol = conn.LerCamposTabela( new String[] {"nnumero"}, "contas_boletas", "Trim(nbanco) = '" + nbanco.trim() + "'"); } catch (Exception e) {e.printStackTrace();}
        if (dadosBol != null) {
            oldnnumero = Double.valueOf(dadosBol[0][3]);
        } else {
            JOptionPane.showMessageDialog(null, "Houve um problema ao ler nnumero!!!\nContacte o administrador do sistema.\nTel.:(21)2701-0261 / 98552-1405");
            System.exit(1);
        }
        
        if (Double.valueOf(Value) > oldnnumero) {
            String sql = "UPDATE contas_boletas SET nnumero = '" + Value + "' WHERE Trim(nbanco) = '" + nbanco + "';";
            try { conn.ExecutarComando(sql);} catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Não consegui gravar Nnumero!!!\nContacte o administrador do sistema.\nTel.:(21)2701-0261 / 98552-1405");
                System.exit(1);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nnumero não coerente!!!\nContacte o administrador do sistema.\nTel.:(21)2701-0261 / 98552-1405");
            System.exit(1);
        }
    }

    static public void LerBancoAvulso(String banco) {
        DbMain conn = VariaveisGlobais.conexao;
        String dadosBol[][] = null;
        try {
            dadosBol = conn.LerCamposTabela(
                    new String[] {"agencia","conta","conta_dv","nbanco","carteira","moeda","tarifa","nnumero","identificacao","identdv"}, 
                    "contas_boletas", 
                    "Trim(nbanco) = '" + banco.trim() + "'"
                    );
        } catch (Exception e) {e.printStackTrace();}

        if (dadosBol != null) {
            try {setAgencia(dadosBol[0][3].trim());} catch (Exception err) {}
            try {setConta(dadosBol[1][3].trim());} catch (Exception err) {}
            try {setCtaDv(dadosBol[2][3].trim());} catch (Exception err) {}
            try {setBanco(dadosBol[3][3].trim());} catch (Exception err) {}
            try {setCarteira(dadosBol[4][3].trim());} catch (Exception err) {}
            try {setMoeda(dadosBol[5][3].trim());} catch (Exception err) {}
            try {setTarifa(dadosBol[6][3].trim());} catch (Exception err) {}
            try {setLogo(dadosBol[3][3].trim());} catch (Exception err) {}
            
            try {if (dadosBol[8][3] != null) setIdentificacao(dadosBol[8][3].trim());} catch (Exception err) {}
            try {if (dadosBol[9][3] != null) setIdentDv(dadosBol[9][3].trim());} catch (Exception err) {}
            
            try {setNnumero(dadosBol[7][3].trim());} catch (Exception err) {}
        }
    }
    
    static public String fmtNumero(String value) {
        String numero = "000000000000000";
        value = value.substring(0, value.indexOf(",") + 3);
        String saida = (numero + rmvNumero(value)).trim();
        return saida.substring(saida.length() - 15);
    }
    
    static public String rmvNumero(String value) {
        String ret = "";
        for (int i=0;i<value.length();i++) {
            if (value.substring(i, i + 1).equalsIgnoreCase(".") || value.substring(i, i + 1).equalsIgnoreCase("/") || value.substring(i, i + 1).equalsIgnoreCase("-") || value.substring(i, i + 1).equalsIgnoreCase(",") || value.substring(i, i + 1).equalsIgnoreCase(" ")){
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }

    static public String rmvLetras(String value) {
        String ret = "";
        for (int i=0; i<value.length();i++) {
            char letra = value.charAt(i);
            if (value.substring(i, i + 1).equalsIgnoreCase(":")) {
                //
            } else if ((int)letra < 48 || (int)letra > 57) {                  
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }

    static public String[][] Recalcula(String rgprp, String rgimv, String contrato, String vencimento) {
        String[][] linhas = null;
        try {
            linhas = MontaTela2(rgprp, rgimv, contrato, vencimento);
        } catch (Exception ex) {} 

        return linhas;
    }
    
    static public String[][] MontaTela2(String rgprp, String rgimv, String contrato, String vecto) throws SQLException, bsh.ParseException {

        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = VariaveisGlobais.conexao.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);

        String[][] linhas = null;
        if (pResult.first()) {
            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
            VariaveisGlobais.ccampos = pResult.getString("campo");
            linhas = new String[14][3];

            a.SplitCampos();
            // Ordena Matriz
            Arrays.sort(a.aCampos, new Comparator()
            {
            private int pos1 = 3;
            private int pos2 = 4;
            @Override
            public int compare(Object o1, Object o2) {
                String p1 = ((String)o1).substring(pos1, pos2);
                String p2 = ((String)o2).substring(pos1, pos2);
                return p1.compareTo(p2);
            }
            });

            // Monta campos
            for (int i=0; i<= a.length() - 1; i++) {
                String[] Campo = a.Depurar(i);
                if (Campo.length > 0) {
                    //MontaCampos(Campo, i);
                    linhas[i][0] = Campo[0];
                    linhas[i][1] = ("00/00".equals(Campo[3]) || "00/0000".equals(Campo[3]) || "".equals(Campo[3]) ? "-" : Campo[3]) ;
                    linhas[i][2] = Campo[1];
                }
            }
        }

        DbMain.FecharTabela(pResult);

        return linhas;
    }
    
    static public float[] CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto) {
        if ("".equals(vecto.trim())) { return null; }

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}

        String campo = ""; String rcampo = ""; boolean mCartVazio = false;
        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = VariaveisGlobais.conexao.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) { rcampo = ""; }
        DbMain.FecharTabela(pResult);

        float exp = 0, multa = 0, juros = 0, correcao = 0;

        try { exp = rc.TaxaExp(campo); } catch (SQLException ex) {}
        try { multa = rc.Multa(campo, vecto, false); } catch (SQLException ex) {}
        try { juros = rc.Juros(campo, vecto); } catch (SQLException ex) {}
        try { correcao = rc.Correcao(campo, vecto); } catch (SQLException ex) {}

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        tRecibo += exp + multa + juros + correcao;

        float[] retorno = new float[5];
        retorno[0] = exp; retorno[1] = multa; retorno[2] = juros; retorno[3] = correcao; retorno[4] = tRecibo;
        return retorno;
    }
    
    static public int AchaVazio(String[][] value) {
        int r = -1;

        for (int i=0;i<value.length;i++) {
            if (value[i][0] == null || "".equals(value[i][0])) {r = i; break;}
        }

        return r;
    }

    public static String SoNumeroSemZerosAEsq(String numero) {
        String Retorno = "";
        for (int i=0;i<numero.length();i++) {
            if (!numero.substring(i,i+1).equalsIgnoreCase("0")) {
                Retorno += numero.substring(i);
                break;
            }
        }
        return Retorno;
    }
}

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package Bancos;
//
//import Funcoes.*;
//import Protocolo.Calculos;
//import Protocolo.DepuraCampos;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Arrays;
//import java.util.Comparator;
//
///**
// *
// * @author supervisor
// */
//public class bancos {    
//    private static String Agencia = "3402";
//    private static String ContaCed = "563350";
//    private static String CtaDv = "8";
//    private static String Banco = "033";
//    private static String BancoDv = "7";
//    private static String Cart = "101";
//    private static String Moeda = "9";
//    private static String Tarifa = "6,00";
//    private static String Nnumero = "0000000000";
//    private static String Identificacao = "013000516";
//    private static String IdentDv = "0";
//    private static String Logo = "resources/logoBancos/033.jpg";
//
//    static public String CalcDig10(String cadeia) {
//        int mult; int total; int res; int pos;
//        mult = (cadeia.length() % 2);
//        mult += 1; total = 0;
//        for (pos=0;pos<=cadeia.length()-1;pos++) {
//            res = Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
//            if (res > 9) { res = (res / 10) + (res % 10); }
//            total += res;
//            if (mult == 2) { mult =1; } else mult = 2;
//        }
//        total = ((10 - (total % 10)) % 10);
//        return  String.valueOf(total);
//    }
//    
////    static public String CalcDig11N(String cadeia) {
////        int total= 0; int mult = 2;
////        for (int i=1; i<=cadeia.length();i++) {
////            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
////            mult++;
////            if (mult > 9) mult = 2;
////        }
////        int soma = total; // * 10;
////        int resto = (soma % 11);
////        if (resto == 0 || resto == 1) { 
////            resto = 0; 
////        } else if (resto >= 10) {
////            resto = 1; 
////        } else {
////            resto = 11 - resto;
////        }
////        return String.valueOf(resto);
////    }
////     
////    static public String CalcDig11(String cadeia, int limitesup, int lflag) {
////
////        int mult; int total; int nresto; int ndig; int pos;
////        String retorno = "";
////        
////        mult = 1 + (cadeia.length() % (limitesup - 1));
////        if (mult == 1) { mult = limitesup; }
////        
////        total = 0;
////        for (pos=0;pos<=cadeia.length()-1;pos++) {
////            total += Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
////            mult -= 1;
////            if (mult == 1) mult = limitesup;
////        }
////        
////        nresto = (total % 11);
////        if (lflag == 1) { retorno = String.valueOf(nresto); } else {
////            if (nresto == 0 || nresto == 1) {
////                ndig = 0; 
////            } else if (nresto > 9) { 
////                ndig = 1; 
////            } else ndig = 11 - nresto;
////            retorno = String.valueOf(ndig);
////        }
////        return retorno;
////    }
//
////    static public String CalcDig11Bradesco(String cadeia, int limitesup, int lflag) {
////
////        int mult; int total; int nresto; int ndig; int pos;
////        String retorno = "";
////        
////        mult = 1 + (cadeia.length() % (limitesup - 1));
////        if (mult == 1) { mult = limitesup; }
////        
////        total = 0;
////        for (pos=0;pos<=cadeia.length()-1;pos++) {
////            total += Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
////            mult -= 1;
////            if (mult == 1) mult = limitesup;
////        }
////        
////        nresto = (total % 11);
////        if (lflag == 1) { 
////            if ((11 - nresto) == 0 || (11 - nresto) == 1 || (11 - nresto) > 9) {
////                ndig = 1;
////            } else ndig = 11 - nresto;
////            retorno = String.valueOf(ndig);
////        } else {
////            if (nresto == 0) {
////                ndig = 0;
////            } else if (nresto == 1) {
////                ndig = -1; 
////            } else ndig = 11 - nresto;
////            retorno = String.valueOf(ndig);
////        }
////        return retorno;
////    }
//    
//    static public String Valor4Boleta(String valor) {
//        String valor1 = "0000000000" + valor.replace(" ", "").replace(",", "").replace(".", "").replace("-", "");
//        return valor1.substring(valor1.length() - 10, valor1.length());
//    }    
//
////    static public String FatorVencimento(String fator, String vencimento) {
////        String retorno = "0000";
////        // 07/03/2000 - CEF
////        // 07/10/1997 - Itau, Santander
////        
////        if (vencimento.length() < 8) {retorno = "0000";} else {
////            int fat1 = (!"033".equals(Banco) && !"001".equals(Banco) ? 1000 : 0);
////            retorno = String.valueOf(fat1 + Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "MM/dd/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
////        }
////        
////        return retorno;
////    }
//
////    static public String NossoNumero(String value, int tam) {
////        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
////                        Integer.valueOf(value).toString().trim(),tam - 1);
////        //String valor2 = bancos.CalcDig11(valor1,9,2);
////        String valor2 = bancos.CalcDig11N(valor1);
////        return valor1 + valor2;
////    }
//
////    static public String NossoNumeroItau(String value, int tam) {
////        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
////                        Integer.valueOf(value).toString().trim(),tam - 1);
////        String valor3 = bancos.getAgencia() + bancos.getConta() + bancos.getCarteira() + valor1;
////        String valor2 = bancos.CalcDig10(valor3);
////        return valor1 + valor2;
////    }
//    
////    static public String NossoNumeroBB(String conta, String value) {
////        String valor1 = new Pad(conta,6).RPad() + new Pad(value,5).RPad();
////        String valor2 = bancos.CalcDig11N(valor1); //,9,2);
////        return valor1 + valor2;
////    }
//
////    static public String NossoNumeroBradesco(String value, String cart) {
////        String valor1 = cart + StringManager.Right(FuncoesGlobais.StrZero("0", 12) +
////                        Integer.valueOf(value).toString().trim(),12);
////        String valor2 = bancos.CalcDig11Bradesco(valor1,7,2);
////        return valor1.substring(2) + valor2;
////    }
//
//    static public void setAgencia(String value) {
//        Agencia = value;
//    }
//    static public String getAgencia() {
//        return Agencia;
//    }
//
//    static public void setConta(String value) {
//        ContaCed = value;
//    }
//    static public String getConta() {
//        return ContaCed;
//    }
//
//    static public void setCtaDv(String value) {
//        CtaDv = value;
//    }
//    static public String getCtaDv() {
//        return CtaDv;
//    }
//
//    static public void setBanco(String value) {
//        Banco = value;
//    }
//    static public String getBanco() {
//        return Banco;
//    }
//
//    static public void setBancoDv(String value) {
//        BancoDv = value;
//    }
//    static public String getBancoDv() {
//        return BancoDv;
//    }
//
//    static public void setCarteira(String value) {
//        Cart = value;
//    }
//    static public String getCarteira() {
//        return Cart;
//    }
//
//    static public void setMoeda(String value) {
//        Moeda = value;
//    }
//    static public String getMoeda() {
//        return Moeda;
//    }
//
//    static public void setTarifa(String value) {
//        Tarifa = value;
//    }
//    static public String getTarifa() {
//        return Tarifa;
//    }
//
//    static public void setNnumero(String value) {
//        Nnumero = value;
//    }
//    static public String getNnumero() {
//        return Nnumero;
//    }
//
//    static public String getIdentificacao() {
//        return Identificacao;
//    }
//    static public void setIdentificacao(String value) {
//        Identificacao = value;
//    }
//    
//    static public String getIdentDv() {
//        return IdentDv;
//    }
//    static public void setIdentDv(String value) {
//        IdentDv = value;
//    }
//
//    static public void setLogo(String value) {
//        Logo = "resources/logoBancos/" + value + ".jpg";
//    }    
//    static public String getLogo() {
//        return Logo;
//    }    
//    
//    static public void ReadBanco() {
//        DbMain conn = VariaveisGlobais.conexao;
//
//        
//        try {setAgencia(conn.LerParametros("AGENCIA").trim());} catch (Exception err) {err.printStackTrace();}
//        try {setConta(conn.LerParametros("CONTACED").trim());} catch (Exception err) {err.printStackTrace();}
//        try {setCtaDv(conn.LerParametros("CTADV").trim());} catch (Exception err) {err.printStackTrace();}
//        try {setBanco(conn.LerParametros("BANCO").trim());} catch (Exception err) {err.printStackTrace();}
//        try {setCarteira(conn.LerParametros("CARTEIRA").trim());} catch (Exception err) {err.printStackTrace();}
//        try {setMoeda(conn.LerParametros("MOEDA").trim());} catch (Exception err) {err.printStackTrace();}
//        try {setTarifa(conn.LerParametros("TARIFA").trim());} catch (Exception err) {err.printStackTrace();}
//        try {setLogo(conn.LerParametros("BANCO").trim());} catch (Exception err) {err.printStackTrace();}
//                
//    }
//    
//    static public void SaveBanco() {
//        DbMain conn = VariaveisGlobais.conexao;
//        
//        try {conn.GravarParametros(new String[] {"AGENCIA", getAgencia(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
//        try {conn.GravarParametros(new String[] {"CONTACED", getConta(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
//        try {conn.GravarParametros(new String[] {"CTADV", getCtaDv(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
//        try {conn.GravarParametros(new String[] {"BANCO", getBanco(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
//        try {conn.GravarParametros(new String[] {"CARTEIRA", getCarteira(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
//        try {conn.GravarParametros(new String[] {"MOEDA", getMoeda(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
//        try {conn.GravarParametros(new String[] {"TARIFA", getTarifa(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
//        
//    }
//    
//    static public void LerBanco(String contrato) {
//        DbMain conn = VariaveisGlobais.conexao;
//        String[][] bancobol = null;
//        try {
//            bancobol = conn.LerCamposTabela(new String[] {"bcobol"}, "locatarios", "contrato = '" + contrato + "'");
//        } catch (Exception e) {e.printStackTrace();}
//        if (bancobol != null) {
//            String dadosBol[][] = null;
//            try {
//                dadosBol = conn.LerCamposTabela(
//                        new String[] {"agencia","conta","conta_dv","nbanco","nbancodv","carteira","moeda","tarifa","nnumero"}, 
//                        "contas_boletas", 
//                        "Trim(nbanco) = '" + bancobol[0][3].trim() + "'"
//                        );
//            } catch (Exception e) {e.printStackTrace();}
//            
//            if (dadosBol != null) {
//                try {setAgencia(dadosBol[0][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setConta(dadosBol[1][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setCtaDv(dadosBol[2][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setBanco(dadosBol[3][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setBancoDv(dadosBol[4][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setCarteira(dadosBol[5][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setMoeda(dadosBol[6][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setTarifa(dadosBol[7][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setLogo(dadosBol[3][3].trim());} catch (Exception err) {err.printStackTrace();}
//                try {setNnumero(dadosBol[8][3].trim());} catch (Exception err) {err.printStackTrace();}
//            }
//        }
//    }
//    
//    static public void GravarNnumero(String nbanco, String Value) {
//        DbMain conn = VariaveisGlobais.conexao;
//        String sql = "UPDATE contas_boletas SET nnumero = '" + Value + "' WHERE Trim(nbanco) = '" + nbanco + "';";
//        try { conn.ExecutarComando(sql);} catch (Exception e) {e.printStackTrace();}
//    }
//
//    static public void LerBancoAvulso(String banco) {
//        DbMain conn = VariaveisGlobais.conexao;
//        String dadosBol[][] = null;
//        try {
//            dadosBol = conn.LerCamposTabela(
//                    new String[] {"agencia","conta","conta_dv","nbanco","carteira","moeda","tarifa","nnumero"}, 
//                    "contas_boletas", 
//                    "Trim(nbanco) = '" + banco.trim() + "'"
//                    );
//        } catch (Exception e) {e.printStackTrace();}
//
//        if (dadosBol != null) {
//            try {setAgencia(dadosBol[0][3].trim());} catch (Exception err) {err.printStackTrace();}
//            try {setConta(dadosBol[1][3].trim());} catch (Exception err) {err.printStackTrace();}
//            try {setCtaDv(dadosBol[2][3].trim());} catch (Exception err) {err.printStackTrace();}
//            try {setBanco(dadosBol[3][3].trim());} catch (Exception err) {err.printStackTrace();}
//            try {setCarteira(dadosBol[4][3].trim());} catch (Exception err) {err.printStackTrace();}
//            try {setMoeda(dadosBol[5][3].trim());} catch (Exception err) {err.printStackTrace();}
//            try {setTarifa(dadosBol[6][3].trim());} catch (Exception err) {err.printStackTrace();}
//            try {setLogo(dadosBol[3][3].trim());} catch (Exception err) {err.printStackTrace();}
//            try {setNnumero(dadosBol[7][3].trim());} catch (Exception err) {err.printStackTrace();}
//            
//        }
//    }
//    
//    static public String fmtNumero(String value) {
//        String numero = "000000000000000";
//        value = value.substring(0, value.indexOf(",") + 3);
//        String saida = (numero + rmvNumero(value)).trim();
//        return saida.substring(saida.length() - 15);
//    }
//    
//    static public String rmvNumero(String value) {
//        String ret = "";
//        for (int i=0;i<value.length();i++) {
//            if (value.substring(i, i + 1).equalsIgnoreCase(".") || value.substring(i, i + 1).equalsIgnoreCase("/") || value.substring(i, i + 1).equalsIgnoreCase("-") || value.substring(i, i + 1).equalsIgnoreCase(",") || value.substring(i, i + 1).equalsIgnoreCase(" ")){
//                //
//            } else {
//                ret += value.substring(i, i + 1);
//            }
//        }
//        return ret;
//    }
//
//    static public String rmvLetras(String value) {
//        String ret = "";
//        for (int i=0; i<value.length();i++) {
//            char letra = value.charAt(i);
//            if (value.substring(i, i + 1).equalsIgnoreCase(":")) {
//                //
//            } else if ((int)letra < 48 || (int)letra > 57) {                  
//                //
//            } else {
//                ret += value.substring(i, i + 1);
//            }
//        }
//        return ret;
//    }
//
//    static public String[][] Recalcula(String rgprp, String rgimv, String contrato, String vencimento) {
//        String[][] linhas = null;
//        try {
//            linhas = MontaTela2(rgprp, rgimv, contrato, vencimento);
//        } catch (Exception ex) {} 
//
//        return linhas;
//    }
//    
//    static public String[][] MontaTela2(String rgprp, String rgimv, String contrato, String vecto) throws SQLException, bsh.ParseException {
//
//        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
//        ResultSet pResult = VariaveisGlobais.conexao.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
//
//        String[][] linhas = null;
//        if (pResult.first()) {
//            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
//            VariaveisGlobais.ccampos = pResult.getString("campo");
//            linhas = new String[14][3];
//
//            a.SplitCampos();
//            // Ordena Matriz
//            Arrays.sort(a.aCampos, new Comparator()
//            {
//            private int pos1 = 3;
//            private int pos2 = 4;
//            @Override
//            public int compare(Object o1, Object o2) {
//                String p1 = ((String)o1).substring(pos1, pos2);
//                String p2 = ((String)o2).substring(pos1, pos2);
//                return p1.compareTo(p2);
//            }
//            });
//
//            // Monta campos
//            for (int i=0; i<= a.length() - 1; i++) {
//                String[] Campo = a.Depurar(i);
//                if (Campo.length > 0) {
//                    //MontaCampos(Campo, i);
//                    linhas[i][0] = Campo[0];
//                    linhas[i][1] = ("00/00".equals(Campo[3]) || "00/0000".equals(Campo[3]) || "".equals(Campo[3]) ? "-" : Campo[3]) ;
//                    linhas[i][2] = Campo[1];
//                }
//            }
//        }
//
//        DbMain.FecharTabela(pResult);
//
//        return linhas;
//    }
//    
//    static public float[] CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto) {
//        if ("".equals(vecto.trim())) { return null; }
//
//        Calculos rc = new Calculos();
//        try {
//            rc.Inicializa(rgprp, rgimv, contrato);
//        } catch (SQLException ex) {}
//
//        String campo = ""; String rcampo = ""; boolean mCartVazio = false;
//        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
//        ResultSet pResult = VariaveisGlobais.conexao.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
//        try {
//            if (pResult.first()) {
//                campo = pResult.getString("campo");
//                rcampo = campo;
//                mCartVazio = ("".equals(rcampo.trim()));
//            }
//        } catch (SQLException ex) { rcampo = ""; }
//        DbMain.FecharTabela(pResult);
//
//        float exp = 0, multa = 0, juros = 0, correcao = 0;
//
//        try { exp = rc.TaxaExp(campo); } catch (SQLException ex) {}
//        try { multa = rc.Multa(campo, vecto, false); } catch (SQLException ex) {}
//        try { juros = rc.Juros(campo, vecto); } catch (SQLException ex) {}
//        try { correcao = rc.Correcao(campo, vecto); } catch (SQLException ex) {}
//
//        float tRecibo = 0;
//        tRecibo = Calculos.RetValorCampos(campo);
//        tRecibo += exp + multa + juros + correcao;
//
//        float[] retorno = new float[5];
//        retorno[0] = exp; retorno[1] = multa; retorno[2] = juros; retorno[3] = correcao; retorno[4] = tRecibo;
//        return retorno;
//    }
//    
//    static public int AchaVazio(String[][] value) {
//        int r = -1;
//
//        for (int i=0;i<value.length;i++) {
//            if (value[i][0] == null || "".equals(value[i][0])) {r = i; break;}
//        }
//
//        return r;
//    }
//
//}
//// CREATE  TABLE `jgeral`.`contas_boletas` (
////  `id` INT NOT NULL AUTO_INCREMENT ,
////  `agencia` VARCHAR(45) NULL ,
////  `conta` VARCHAR(45) NULL ,
////  `conta_dv` VARCHAR(45) NULL ,
////  `nbanco` VARCHAR(45) NULL ,
////  `nbancodv VARCHAR(1) NULL ,
////  `carteira` VARCHAR(45) NULL ,
////  `moeda` VARCHAR(45) NULL DEFAULT '9' ,
////  `tarifa` VARCHAR(45) NULL DEFAULT '0000000000' ,
////  `nnumero` VARCHAR(45) NULL ,
////  PRIMARY KEY (`id`) );
