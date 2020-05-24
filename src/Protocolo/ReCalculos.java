/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Protocolo;

import Funcoes.*;
import java.sql.SQLException;
import java.util.Date;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author supervisor
 */
public class ReCalculos {
    DbMain conn = VariaveisGlobais.conexao;
    private String rgprp = "";
    private String rgimv = "";
    private String contrato = "";

    private float taxa_per = 0;
    private float taxa_vrm = 0;
    private String taxa_exp = "";

    private boolean tAlug = false;
    private boolean tTaxa = false;
    private boolean tSeguro = false;
    private boolean taxa_brlq = false;

    private float multa_res = 0;
    private float multa_com = 0;
    public float dia_mul = 0;
    private String tipo_multa = "";

    private boolean mAlug = false;
    private boolean mTaxa = false;
    private boolean mExp = false;

    public int dia_jur = 0;
    private String tipo_jur = "";
    private float per_jur = 0;

    private boolean jAlug = false;
    private boolean jTaxa = false;
    private boolean jExp  = false;
    private boolean jSeguro  = false;

    private float cor_tipo = 0;
    private float cor_dia = 0;
    private float cor_lim_a = 0;
    private float cor_per = 0;

    private boolean cAlug = false;
    private boolean cTaxa = false;
    private boolean cExp = false;
    private boolean cSeguro = false;

    private boolean tmulta = false;
    private boolean tjuros = false;
    private boolean tcorrecao = false;
    private boolean jmulta = false;
    private boolean jcorrecao = false;
    private boolean cmulta = false;
    private boolean cjuros = false;
    private boolean mjuros = false;
    private boolean mcorrecao = false;

    private float fComissao = 0;

    private Date CalcDate = new Date();
    
    public void setCalcDate(Date value) { CalcDate = value; }
    public Date getCalcDate() { return CalcDate; }
    
    public void Inicializa(String rgprp, String rgimv, String contrato) throws SQLException {
        this.rgprp = rgprp;
        this.rgimv = rgimv;
        this.contrato = contrato;

        this.taxa_per = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("te_per"), 3));
        this.taxa_vrm = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("te_vrm"), 2));
        this.taxa_exp = conn.LerParametros("txexp");

        this.tAlug = Boolean.valueOf(conn.LerParametros("talug"));
        this.tTaxa = Boolean.valueOf(conn.LerParametros("ttaxa"));
        this.tSeguro = Boolean.valueOf(conn.LerParametros("tseguro"));
        this.taxa_brlq = Boolean.valueOf(conn.LerParametros("tbrliq"));

        this.tipo_multa = conn.LerParametros("multa");
        this.multa_res = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("multa_res"), 3));
        this.multa_com = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("multa_com"), 3));
        this.dia_mul = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("dia_mul"), 0));

        this.mAlug = Boolean.valueOf(conn.LerParametros("malug"));
        this.mTaxa = Boolean.valueOf(conn.LerParametros("mtaxa"));
        this.mExp = Boolean.valueOf(conn.LerParametros("mexpe"));

        this.tipo_jur = conn.LerParametros("juros");
        this.dia_jur = Integer.valueOf(LerValor.FormatNumber(conn.LerParametros("dia_jur"), 0));
        this.per_jur = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("jur_per"), 3));

        this.jAlug = Boolean.valueOf(conn.LerParametros("jalug"));
        this.jTaxa = Boolean.valueOf(conn.LerParametros("jtaxa"));
        this.jExp = Boolean.valueOf(conn.LerParametros("jexpe"));
        this.jSeguro = Boolean.valueOf(conn.LerParametros("jseguro"));

        this.cor_tipo = LerValor.StringToFloat(conn.LerParametros("cor_tipo"));
        this.cor_lim_a = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("cor_lim_a"), 0));
        this.cor_per = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("cor_corr"), 3));
        this.cor_dia = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("dia_cor"), 0));

        this.cAlug = Boolean.valueOf(conn.LerParametros("calug"));
        this.cTaxa = Boolean.valueOf(conn.LerParametros("ctaxa"));
        this.cExp = Boolean.valueOf(conn.LerParametros("cexpe"));
        this.cSeguro = Boolean.valueOf(conn.LerParametros("cseguro"));

        this.tmulta = Boolean.valueOf(conn.LerParametros("tmulta"));
        this.tjuros = Boolean.valueOf(conn.LerParametros("tjuros"));
        this.tcorrecao = Boolean.valueOf(conn.LerParametros("tcorrecao"));
        this.jmulta = Boolean.valueOf(conn.LerParametros("jmulta"));
        this.jcorrecao = Boolean.valueOf(conn.LerParametros("jcorrecao"));
        this.cmulta = Boolean.valueOf(conn.LerParametros("cmulta"));
        this.cjuros = Boolean.valueOf(conn.LerParametros("cjuros"));
        this.mjuros = Boolean.valueOf(conn.LerParametros("mjuros"));
        this.mcorrecao = Boolean.valueOf(conn.LerParametros("mcorrecao"));

        this.fComissao = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("comissao"), 3));

        if (conn.LerCamposTabela(new String[] {"RGIMV"}, "MULTA", "RGIMV = '" + this.rgimv + "'") != null) {
            String[][] regFields = null;
            regFields = conn.LerCamposTabela(new String[] {"te_per", "te_vrm", "txexp", "tbrliq", "multa_res", "multa_com", "dia_mul", "dia_jur", "per_jur", "cor_tipo", "cor_lim_a", "cor_corr", "dia_cor", "comissao"}, "MULTA", "RGIMV = '" + this.rgimv + "'");
            this.taxa_per = LerValor.StringToFloat(LerValor.FormatNumber(regFields[0][3], 3));
            this.taxa_vrm = LerValor.StringToFloat(LerValor.FormatNumber(regFields[1][3], 2));
            this.taxa_exp = regFields[2][3];
            //'this.taxa_brlq = regFields[3][3]
            this.multa_res = LerValor.StringToFloat(LerValor.FormatNumber(regFields[4][3], 3));
            this.multa_com = LerValor.StringToFloat(LerValor.FormatNumber(regFields[5][3], 3));
            this.dia_mul = LerValor.StringToFloat(LerValor.FormatNumber(regFields[6][3], 0));
            this.dia_jur = Integer.valueOf(LerValor.FormatNumber(regFields[7][3], 0));
            this.per_jur = LerValor.StringToFloat(LerValor.FormatNumber(regFields[8][3], 3));
            this.cor_tipo = LerValor.StringToFloat(regFields[9][3]);
            this.cor_lim_a = LerValor.StringToFloat(regFields[10][3]);
            this.cor_per = LerValor.StringToFloat(LerValor.FormatNumber(regFields[11][3], 3));
            this.cor_dia = LerValor.StringToFloat(LerValor.FormatNumber(regFields[12][3], 0));
            this.fComissao = LerValor.StringToFloat(LerValor.FormatNumber(regFields[13][3], 3));
       } else {
        if (conn.LerCamposTabela(new String[] {"RGPRP"}, "MULTA", "RGPRP = '" + this.rgprp + "' AND IsNull(RGIMV)") != null) {
            String[][] regFields = null;
            regFields = conn.LerCamposTabela(new String[] {"te_per", "te_vrm", "txexp", "tbrliq", "multa_res", "multa_com", "dia_mul", "dia_jur", "per_jur", "cor_tipo", "cor_lim_a", "cor_corr", "dia_cor", "comissao"}, "MULTA", "RGPRP = '" + this.rgprp + "' AND IsNull(RGIMV)");
            this.taxa_per = LerValor.StringToFloat(LerValor.FormatNumber(regFields[0][3], 3));
            this.taxa_vrm = LerValor.StringToFloat(LerValor.FormatNumber(regFields[1][3], 2));
            this.taxa_exp = regFields[2][3];
            //'this.taxa_brlq = regFields[3][3]
            this.multa_res = LerValor.StringToFloat(LerValor.FormatNumber(regFields[4][3], 3));
            this.multa_com = LerValor.StringToFloat(LerValor.FormatNumber(regFields[5][3], 3));
            this.dia_mul = LerValor.StringToFloat(LerValor.FormatNumber(regFields[6][3], 0));
            this.dia_jur = Integer.valueOf(LerValor.FormatNumber(regFields[7][3], 0));
            this.per_jur = LerValor.StringToFloat(LerValor.FormatNumber(regFields[8][3], 3));
            this.cor_tipo = LerValor.StringToFloat(regFields[9][3]);
            this.cor_lim_a = LerValor.StringToFloat(regFields[10][3]);
            this.cor_per = LerValor.StringToFloat(LerValor.FormatNumber(regFields[11][3], 3));
            this.cor_dia = LerValor.StringToFloat(LerValor.FormatNumber(regFields[12][3], 0));
            this.fComissao = LerValor.StringToFloat(LerValor.FormatNumber(regFields[13][3], 3));
        }
      }
    }

    private int DiasParado(String vencto) {
        int retorno = 0;
        // Checa se é sabado ou domingo
        int sabdom = Dates.isSabadoOuDomingo(Dates.StringtoDate(vencto, "dd/MM/yyyy"));
        int difdata = Dates.DateDiff(Dates.DIA,  CalcDate, Dates.StringtoDate(vencto, "dd/MM/yyyy")) + 1;
        if (difdata >= -3 && difdata <= -1) {
            if (sabdom > 0) {
                Date nvecto = Dates.DateAdd(Dates.DIA, sabdom, Dates.StringtoDate(vencto, "dd/MM/yyyy"));
                vencto = Dates.DateFormata("dd/MM/yyyy", nvecto);
                retorno += sabdom;
            }
            String pdia = vencto.substring(0, 2);
            String pmes = vencto.substring(3, 5);
            String[][] aferiado = null;
            try {aferiado = conn.LerCamposTabela(new String[] {"descricao"}, "feriados",
                    FuncoesGlobais.Subst("(tipo = 'F' OR tipo = 'N') AND dia = '&1.' AND mes = '&2.'",
                    new String[] {pdia, pmes}));} catch (Exception ex) {}
            if (aferiado != null) {
                Date nvecto = Dates.DateAdd(Dates.DIA, 1, Dates.StringtoDate(vencto, "dd/MM/yyyy"));
                vencto = Dates.DateFormata("dd/MM/yyyy", nvecto);
                retorno += 1;
            }
        }
        
        return retorno;
    }
        
    public float Multa(String campos, String vecto, boolean padrao) throws SQLException {
        boolean bMulta = RetPar(campos, "MU");
        if (bMulta) { return RetVarPar(campos, "MU"); }

        int sabdomfer = DiasParado(vecto);
        
        Date mHoje = CalcDate;
        Date mVecto = Dates.DateAdd(Dates.DIA, (int)this.dia_mul + sabdomfer, Dates.StringtoDate(vecto, "dd/MM/yyyy"));

        if (Dates.DiffDate(mHoje, mVecto) <= 0) { return 0; }

        mVecto = Dates.StringtoDate(vecto, "dd/MM/yyyy");

        float tot_multa = 0;
        int mDias = 0;
        if ("SIMPLES".equals(this.tipo_multa)) {
            tot_multa = 1;
        } else {
            mDias = Dates.DiffDate(mHoje, mVecto);
            mDias = (mDias < 0 ? 0 : mDias);
            tot_multa = (mDias / 30) + ((mDias % 30) > 0 ? 1 : 0);
        }

        float multa_imovel = 0;
        if ("RESIDENCIAL".equals(TipoImovel())) {
            multa_imovel = (this.multa_res / 100);
        } else   multa_imovel = (this.multa_com / 100);

        String[] aCampos = campos.split(";");
        int i = 0; float alCampos = 0; float txCampos = 0; float dc_br_Campos = 0; float dc_lq_Campos = 0;
        float df_br_Campos = 0; float df_lq_Campos = 0; float sgCampos = 0; float exCampos = 0;

        for (i=0;i<=aCampos.length - 1;i++) {
            String[] dCampos = aCampos[i].split(":");

            if ("AL".equals(dCampos[4])) {
                alCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            } else if ("NT".equals(dCampos[4])) {
                txCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            } else if ("DC".equals(dCampos[4])) {
                if ("BR".equals(dCampos[5])) {
                    dc_br_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                } else {
                    dc_lq_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                }
            } else if ("DF".equals(dCampos[4])) {
                if ("BR".equals(dCampos[5])) {
                    df_br_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                } else {
                    df_lq_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                }
            } else if ("SG".equals(dCampos[4])) {
                sgCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            }
        }

        exCampos = TaxaExp(campos);

        float multa_total = 0;
        if (padrao) {
            return Math.round((alCampos - dc_lq_Campos) * (multa_imovel));
        } else {
            if (this.mAlug) { multa_total += alCampos; }
            if (this.mTaxa) { multa_total += txCampos; }
            if (this.mExp) { multa_total += exCampos; }
            multa_total += sgCampos + df_br_Campos - dc_lq_Campos;

            //return Math.round((multa_total) * (multa_imovel));
            return (multa_total) * (multa_imovel);
        }
    }

    public float TaxaExp(String campos) throws SQLException {
        boolean bTaxa = RetPar(campos, "EP");
        if (bTaxa) { return RetVarPar(campos, "EP"); }

        String[] aCampos = campos.split(";");
        int i = 0; float alCampos = 0; float txCampos = 0; float dc_br_Campos = 0; float dc_lq_Campos = 0;
        float df_br_Campos = 0; float df_lq_Campos = 0; float sgCampos = 0; float exCampos = 0;

        for (i=0;i<=aCampos.length - 1;i++) {
            String[] dCampos = aCampos[i].split(":");

            if ("AL".equals(dCampos[4])) {
                alCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            } else if ("NT".equals(dCampos[4])) {
                txCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            } else if ("DC".equals(dCampos[4])) {
                if ("BR".equals(dCampos[5])) {
                    dc_br_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                } else {
                    dc_lq_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                }
            } else if ("DF".equals(dCampos[4])) {
                if ("BR".equals(dCampos[5])) {
                    df_br_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                } else {
                    df_lq_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                }
            } else if ("SG".equals(dCampos[4])) {
                sgCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            }
        }

        //(converter) exCampos = TaxaExp(mCampo, rgimv, rgprp)

        float taxa_total = 0;
        if (this.tAlug) { taxa_total += alCampos; }
        if (this.tTaxa) { taxa_total += txCampos; }
        if (this.tSeguro) { taxa_total += sgCampos; }
        if (this.taxa_brlq) { taxa_total += df_br_Campos; } else taxa_total += df_br_Campos - dc_lq_Campos;

        float taxa_valor = 0;
        taxa_valor = taxa_total * (this.taxa_per / 100);
        if (taxa_valor < this.taxa_vrm) taxa_valor = this.taxa_vrm;

        //return Math.round(taxa_valor);
        return taxa_valor;
    }

    public float Juros(String campos, String vecto) throws SQLException {
        boolean bJuros = RetPar(campos, "JU");
        if (bJuros) { return RetVarPar(campos, "JU"); }

        int sabdomfer = DiasParado(vecto);
        
        Date mHoje = CalcDate;
        Date mVecto = Dates.DateAdd(Dates.DIA, (int)this.dia_jur + sabdomfer, Dates.StringtoDate(vecto, "dd/MM/yyyy"));

        if (Dates.DiffDate(mHoje, mVecto) <= 0) { return 0;}

        mVecto = Dates.StringtoDate(vecto, "dd/MM/yyyy");

        float tot_jur = 0;
        int mDias = 0;
        if ("SIMPLES".equals(this.tipo_jur)) {
            tot_jur = 1;
        } else {
            mDias = Dates.DiffDate(mHoje, mVecto);
            mDias = (mDias < 0 ? 0 : mDias);

            if (mDias >= this.dia_jur) {
                tot_jur = (mDias / 30);
                if ((mDias - ((mDias / 30) * 30)) >= this.dia_jur) { tot_jur += 1; }
            } else tot_jur = 0;
        }

        String[] aCampos = campos.split(";");
        int i = 0; float alCampos = 0; float txCampos = 0; float dc_br_Campos = 0; float dc_lq_Campos = 0;
        float df_br_Campos = 0; float df_lq_Campos = 0; float sgCampos = 0; float exCampos = 0;

        for (i=0;i<=aCampos.length - 1;i++) {
            String[] dCampos = aCampos[i].split(":");

            if ("AL".equals(dCampos[4])) {
                alCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            } else if ("NT".equals(dCampos[4])) {
                txCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            } else if ("DC".equals(dCampos[4])) {
                if ("BR".equals(dCampos[5])) {
                    dc_br_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                } else {
                    dc_lq_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                }
            } else if ("DF".equals(dCampos[4])) {
                if ("BR".equals(dCampos[5])) {
                    df_br_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                } else {
                    df_lq_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                }
            } else if ("SG".equals(dCampos[4])) {
                sgCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            }
        }

        exCampos = TaxaExp(campos);

        float jur_total = 0;
        if (this.jAlug) { jur_total += alCampos; }
        if (this.jTaxa) { jur_total += txCampos; }
        if (this.jExp) { jur_total += exCampos; }
        if (this.jSeguro) { jur_total += sgCampos; }

        jur_total += df_br_Campos - dc_lq_Campos;

        if (this.jmulta) { jur_total += Multa(campos, vecto, false); }
        if (this.jcorrecao) { jur_total += Correcao(campos, vecto); }

        return (jur_total) * ((per_jur / 100) * tot_jur);
    }

    public float Correcao(String campos, String vecto) throws SQLException {
        boolean bCor = RetPar(campos, "CO");
        if (bCor) { return RetVarPar(campos, "CO"); }

        int sabdomfer = DiasParado(vecto);

        Date mHoje = CalcDate;
        Date mVecto = Dates.DateAdd(Dates.DIA, (int)this.cor_dia + sabdomfer, Dates.StringtoDate(vecto, "dd/MM/yyyy"));

        if (Dates.DiffDate(mHoje, mVecto) <= 0) { return 0; }

        String[] aCampos = campos.split(";");
        int i = 0; float alCampos = 0; float txCampos = 0; float dc_br_Campos = 0; float dc_lq_Campos = 0;
        float df_br_Campos = 0; float df_lq_Campos = 0; float sgCampos = 0; float exCampos = 0;

        for (i=0;i<=aCampos.length - 1;i++) {
            String[] dCampos = aCampos[i].split(":");

            if ("AL".equals(dCampos[4])) {
                alCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            } else if ("NT".equals(dCampos[4])) {
                txCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            } else if ("DC".equals(dCampos[4])) {
                if ("BR".equals(dCampos[5])) {
                    dc_br_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                } else {
                    dc_lq_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                }
            } else if ("DF".equals(dCampos[4])) {
                if ("BR".equals(dCampos[5])) {
                    df_br_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                } else {
                    df_lq_Campos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
                }
            } else if ("SG".equals(dCampos[4])) {
                sgCampos += LerValor.StringToFloat(LerValor.FormatNumber(dCampos[2],2));
            }
        }

        exCampos = TaxaExp(campos);

        float cor_total = 0;
        if (this.cAlug) { cor_total += alCampos; }
        if (this.cTaxa) { cor_total += txCampos; }
        if (this.cExp) { cor_total += exCampos; }
        if (this.cSeguro) { cor_total += sgCampos; }

        cor_total += sgCampos + df_br_Campos - dc_lq_Campos;

        if (this.cmulta) { cor_total += Multa(campos, vecto, false); }
        if (this.cjuros) { cor_total += Juros(campos, vecto); }

        mVecto = Dates.StringtoDate(vecto, "dd/MM/yyyy");

        int mDias = 0;
        mDias = Dates.DiffDate(mHoje, mVecto);
        mDias = (mDias < 0 ? 0 : mDias);

        if ((int) cor_lim_a > 0) {
            if (mDias >= this.cor_lim_a) { mDias = (int) cor_lim_a; }
        }
        
        float cor_final = 0;
        if (this.cor_tipo == 0) {
            return cor_total * (((cor_per / 100) * mDias));
        } else {
            cor_final = cor_total;
            for (i=1;i<=mDias;i++) { cor_final += (cor_final * (this.cor_per / 100)); }
            return cor_final - cor_total;
        }
    }

    public static boolean RetPar(String campo, String oque) {
        return (campo.contains(oque));
    }

    public static float RetValorCampos(String campos) {
        String[] campo = campos.split(";");
        int i = 0;
        float fRet = 0;
        for (i=0;i<=campo.length-1;i++) {
            String[] scampo = campo[i].split(":");
            if (!"DC".equals(scampo[4])) {
                fRet += LerValor.StringToFloat(LerValor.FormatNumber(scampo[2], 2));
            } else {
                fRet -= LerValor.StringToFloat(LerValor.FormatNumber(scampo[2], 2));
            }
        }
        return fRet;
    }
    
    private float RetVarPar(String campos, String oque) {
        String mVrPar = "0000000000";
        int mIndex = campos.indexOf(oque,0);
        String mCpo = "";

        if (mIndex > -1) {
            mCpo = campos.substring(mIndex + 2, mIndex + 2 + 1);
            if (!":".equals(mCpo)) {
                mCpo = campos.substring(mIndex + 2, mIndex + 2 + 10);

                if (NumberUtils.isDigits(mCpo)) {
                    mVrPar = mCpo;
                }
            }
        }

        return LerValor.StringToFloat(LerValor.FormatNumber(mVrPar, 2));
    }
    
    public static String RetCpoPar(String campos, String oque) {
        int mIndex = campos.indexOf(oque,0);
        String mCpo = "";

        if (mIndex > 0) {
            mCpo = campos.substring(mIndex + 2, mIndex + 2 + 1);
            if (!":".equals(mCpo)) {
                mCpo = campos.substring(mIndex, mIndex + 2 + 10);
            } else {
                mCpo = campos.substring(mIndex, mIndex + 2);
            }
        }

        return mCpo;
    }    

    private String TipoImovel() throws SQLException {
        String tpImv = conn.LerCamposTabela(new String[] {"tpimovel"}, "imoveis", "rgimv = '" + this.rgimv + "'")[0][3];
        return tpImv.toUpperCase();
    }
    
    public String[] percADM(String mrgprp, String mrgimv) throws SQLException {
        String[][] regFields;
        String admmulta, admjuros, admcorrecao, admTxExp = "";

        admmulta = LerValor.FormatNumber(conn.LerParametros("PA_MULTA"), 3);
        admjuros = LerValor.FormatNumber(conn.LerParametros("PA_JUROS"), 3);
        admcorrecao = LerValor.FormatNumber(conn.LerParametros("PA_CORR"), 3);
        admTxExp = LerValor.FormatNumber(conn.LerParametros("PA_TXEXP"), 3);
        
        if (conn.LerCamposTabela(new String[] {"RGIMV"}, "MULTA", "RGIMV = '" + mrgimv + "'") != null) {
            regFields = conn.LerCamposTabela(new String[] {"pa_multa", "pa_jur", "pa_cor", "txexp"}, "MULTA", "RGIMV = '" + mrgimv + "'");
            admmulta = LerValor.FormatNumber(regFields[0][3], 3);
            admjuros = LerValor.FormatNumber(regFields[1][3], 3);
            admcorrecao = LerValor.FormatNumber(regFields[2][3], 3);
            admTxExp = LerValor.FormatNumber(regFields[3][3], 3);
        } else {
            if (conn.LerCamposTabela(new String[] {"RGPRP"}, "MULTA", "RGPRP = '" + mrgprp + "' AND IsNull(RGIMV)") != null) {
                regFields = conn.LerCamposTabela(new String[] {"pa_multa", "pa_jur", "pa_cor", "txexp"}, "MULTA", "RGPRP = '" + mrgprp + "' AND IsNull(RGIMV)");
                admmulta = LerValor.FormatNumber(regFields[0][3], 3);
                admjuros = LerValor.FormatNumber(regFields[1][3], 3);
                admcorrecao = LerValor.FormatNumber(regFields[2][3], 3);
                admTxExp = LerValor.FormatNumber(regFields[3][3], 3);
            }
        }
        
        String[] tmp ={admmulta, admjuros, admcorrecao, admTxExp};
        return tmp;
    }
    
    public float percComissao(String rgprp, String rgimv) throws SQLException {
        String[][] regFields;
        
        fComissao = Float.valueOf(LerValor.FormatNumber(conn.LerParametros("comissao"), 3).replace(",", "."));
        
        if (conn.LerCamposTabela(new String[] {"RGIMV"}, "MULTA", "RGIMV = '" + rgimv + "'") != null) {
            regFields = conn.LerCamposTabela(new String[] {"comissao"}, "MULTA", "RGIMV = '" + rgimv + "'");
            fComissao = Float.valueOf(LerValor.FormatNumber(regFields[0][3], 3).replace(",", "."));
        } else {
            if (conn.LerCamposTabela(new String[] {"RGPRP"}, "MULTA", "RGPRP = '" + rgprp + "' AND IsNull(RGIMV)") != null) {
                regFields = conn.LerCamposTabela(new String[] {"comissao"}, "MULTA", "RGPRP = '" + rgprp + "' AND IsNull(RGIMV)");
                fComissao = Float.valueOf(LerValor.FormatNumber(regFields[0][3], 3).replace(",", "."));
            }
        }
        return fComissao;
    }
}
