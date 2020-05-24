/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Protocolo;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author supervisor
 */
public class DepuraCampos {
    private String tCampos;
    public String aCampos[];
    private int length = 0;

    public DepuraCampos(String setCampos) {
        tCampos = setCampos;
    }

    public void SplitCampos() {
        aCampos = tCampos.split(";");
        length = aCampos.length;
    }

    public String[] Depurar(int n) throws SQLException {
        int nConta = 0;
        String aCampo[];
        aCampo = aCampos[n].split(":");
        Sici.Partida.Collections gVar = VariaveisGlobais.dCliente;

        String mCod = aCampo[0];
        String mValor = aCampo[2];

        DbMain conn = VariaveisGlobais.conexao;
        ResultSet hR = conn.AbrirTabela("SELECT CART_CODIGO, CART_DESCR, CART_ORDEM, CART_COTPAR, CART_TAXA FROM LANCART WHERE CART_CODIGO = '" + mCod + "';", ResultSet.CONCUR_READ_ONLY);

        String m_Descr = null;
        String m_Valor;
        String m_CP;
        String mCB;
        String m_CB;
        String m_Retencao;
        String m_Antecipa;
        String mCOTPAR;
        String mDataBoleta;
        String m_Tipo;
        
        if (hR.first()) {
          m_Descr = hR.getString("CART_DESCR");
          m_Valor = LerValor.FormataCurrency(mValor);
          m_CP = aCampo[3].substring(0,2) + "/" + aCampo[3].substring(2);
          m_Retencao = (BuscaCod(aCampo, "RT") == -1 ? "0" : "1");
          m_Antecipa = (BuscaCod(aCampo, "AT") == -1 ? "0" : "1");
          if (BuscaCod(aCampo, "CB") > -1) {
              mCB = aCampo[BuscaCod(aCampo, "CB")].substring(2);
          } else {
              mCB = "";
          }
          m_CB = mCB;

          mCOTPAR = ("C".equals(hR.getString("CART_COTPAR")) ? "C" : "P");

          m_Tipo = hR.getInt("CART_TAXA") == 0 ? "A" : "T";
        } else {
            if ("DC".equals(mCod)) {
                int pos = FuncoesGlobais.IndexOf(aCampo, "DS");
                if (pos > -1) {
                    m_Descr = gVar.get("DC") + FuncoesGlobais.DecriptaNome(aCampo[pos].substring(2));
                } else {
                    m_Descr = gVar.get("DC");
                }
            } else if ("DF".equals(mCod)) {
                int pos = FuncoesGlobais.IndexOf(aCampo, "DS");
                if (pos > -1) {
                    m_Descr = gVar.get("DF") + FuncoesGlobais.DecriptaNome(aCampo[pos].substring(2));
                } else {
                    m_Descr = gVar.get("DF");
                }
            } else if ("SG".equals(mCod)) {
                m_Descr = VariaveisGlobais.dCliente.get("SG");  //"Seguro";  // VariaveisGlobais.dCliente["SG"]
            }

            m_Valor = LerValor.FormataCurrency(mValor);
            m_CP =  aCampo[3].substring(0,2) + "/" + aCampo[3].substring(2); //"00/00";
            m_Retencao = (BuscaCod(aCampo, "RT") == -1 ? "0" : "1"); //m_Retencao = "0";
            m_Antecipa = (BuscaCod(aCampo, "AT") == -1 ? "0" : "1"); 
            mCOTPAR = "C";
            m_CB = "";
            m_Tipo = "T";
           // mDataBoleta = BuscaCod(aCampo, "DB") = -1 ? "" : 
        }

      DbMain.FecharTabela(hR);
      ////////////////conn.FecharConexao();

      String[] cCampos = {m_Descr, m_Valor, m_Retencao, m_CP, m_CB, mCOTPAR, m_Antecipa, m_Tipo};
      if (BuscaCod(aCampo, "*") > -1) {
          cCampos = FuncoesGlobais.ArrayAdd(cCampos, "*");
      }

      return cCampos;
    }

    public String[] Depurar_withcod(int n) throws SQLException {
        int nConta = 0;
        String aCampo[];
        aCampo = aCampos[n].split(":");
        Sici.Partida.Collections gVar = VariaveisGlobais.dCliente;

        String mCod = aCampo[0];
        String mValor = aCampo[2];

        DbMain conn = VariaveisGlobais.conexao;
        ResultSet hR = conn.AbrirTabela("SELECT CART_CODIGO, CART_DESCR, CART_ORDEM, CART_COTPAR, CART_TAXA FROM LANCART WHERE CART_CODIGO = '" + mCod + "';", ResultSet.CONCUR_READ_ONLY);

        String m_Descr = null;
        String m_Valor;
        String m_CP;
        String mCB;
        String m_CB;
        String m_Retencao;
        String mCOTPAR;
        String m_Tipo;
        
        if (hR.first()) {
          m_Descr = hR.getString("CART_DESCR");
          m_Valor = LerValor.FormataCurrency(mValor);
          m_CP = aCampo[3].substring(0,2) + "/" + aCampo[3].substring(2);
          m_Retencao = (BuscaCod(aCampo, "RT") == -1 ? "0" : "1");
          if (BuscaCod(aCampo, "CB") > -1) {
              mCB = aCampo[BuscaCod(aCampo, "CB")].substring(2);
          } else {
              mCB = "";
          }
          m_CB = mCB;

          mCOTPAR = ("C".equals(hR.getString("CART_COTPAR")) ? "C" : "P");
          m_Tipo = hR.getInt("CART_TAXA") == 0 ? "A" : "T";
        } else {
            if ("DC".equals(mCod)) {
                int pos = FuncoesGlobais.IndexOf(aCampo, "DS");
                if (pos > -1) {
                    m_Descr = gVar.get("DC") + FuncoesGlobais.DecriptaNome(aCampo[pos].substring(2));
                } else {
                    m_Descr = gVar.get("DC");
                }
            } else if ("DF".equals(mCod)) {
                int pos = FuncoesGlobais.IndexOf(aCampo, "DS");
                if (pos > -1) {
                    m_Descr = gVar.get("DF") + FuncoesGlobais.DecriptaNome(aCampo[pos].substring(2));
                } else {
                    m_Descr = gVar.get("DF");
                }
            } else if ("SG".equals(mCod)) {
                m_Descr = VariaveisGlobais.dCliente.get("SG");  //"Seguro";  // VariaveisGlobais.dCliente["SG"]
            }

            m_Valor = LerValor.FormataCurrency(mValor);
            m_CP =  "00/00";
            m_Retencao = "0";
            mCOTPAR = "C";
            m_CB = "";
            m_Tipo = "T";
        }

      DbMain.FecharTabela(hR);

      String[] cCampos = {mCod, m_Descr, m_Valor, m_Retencao, m_CP, m_CB, mCOTPAR, m_Tipo};
      if (BuscaCod(aCampo, "*") > -1) {
          cCampos = FuncoesGlobais.ArrayAdd(cCampos, "*");
      }

      return cCampos;
    }

    public int BuscaCod(String[] Campo, String oQue) {
        int nConta;
        boolean bAchei = false;
        int Ret = -1;

        for (nConta = 0; nConta <= Campo.length - 1; nConta++) {
            if (StringManager.Left(Campo[nConta],oQue.length()).equals(oQue)) {
                bAchei = true;
                break;
            }
        }

        if (bAchei) Ret = nConta;
        return Ret;
    }

    public int length() {
        return length;
    }
}
