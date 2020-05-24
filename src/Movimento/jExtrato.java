/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jExtrato.java
 *
 * Created on 10/05/2011, 16:11:51
 */

package Movimento;

import Funcoes.*;
import Transicao.jPagarExtrato;
import com.lowagie.text.Font;
import extrato.Extrato;
import Sici.Partida.Collections;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author supervisor
 */
public class jExtrato extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    
    float extMax = 0f; 
    String rgprp = ""; String rgimv = ""; String contrato = "";
    JRViewer visor;
    boolean bExecNome = false, bExecCodigo = false;

    String[] anexos = new String[0];

    jPagarExtrato tPag = new jPagarExtrato();
    JPanel pnlDigite = (JPanel) tPag.getComponent(ComponentSearch.ComponentSearch(tPag, "jpnDigite"));
    JButton btnLancar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtLancar"));
    JButton btnCancelar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtCancelar"));
    JPanel pnlBotoes = (JPanel) tPag.getComponent(ComponentSearch.ComponentSearch(tPag, "pnlBotoes"));
    JToggleButton btDN = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgDN"));
    JToggleButton btCH = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCH"));
    JToggleButton btCT = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCT"));
    JFormattedTextField jResto = (JFormattedTextField) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "JRESTO"));

    String jEmailEmp = ""; String jSenhaEmail = ""; boolean jPop = false; boolean jAutentica = false;
    String jEndPopImap = ""; String jPortPopImap = ""; String jSmtp = ""; String jPortSmtp = "";
    String jAssunto = ""; String jMsgEmail = ""; String jFTP_Conta = ""; String jFTP_Porta = "";
    String jFTP_Usuario = ""; String jFTP_Senha = ""; 
    
    private void InitjPagar() {
        tPag.setVisible(true);
        tPag.setEnabled(true);
        tPag.setBounds(0, 0, 314, 313);

        try {
            jpRecebe.add(tPag);
        } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace();}
        jpRecebe.repaint();
        jpRecebe.setSize(314,313);
        jpRecebe.setEnabled(true);

        btnLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tPag.Lancar();
                if (tPag.bprintdoc) {
                    ImprimirExtrato();
                    tPag.Clear();
                    tPag.Enable(false);
                    jResto.setText("0,00");
                    tPag.vrAREC = 0;
                    jView.removeAll();
                    jView.revalidate();
                    RefreshVisor();
                    jView.repaint();
                    tPag.LimpaTransicao();
                    CleanLastPagto();

                    jRgprp.setEnabled(true);
                    jNomeProp.setEnabled(true);
                    jRgprp.requestFocus();
                }
            }
        });

        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tPag.Cancelar()) {
                    jRgprp.setEnabled(true);
                    jNomeProp.setEnabled(true);
                    jRgprp.requestFocus();
                }
            }
        });

        btDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            }
        });

        btCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            }
        });

        btCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            }
        });
    }

    /** Creates new form jExtrato */
    public jExtrato() throws JRException {
        initComponents();

        int inset = 100;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - (inset * 2), screenSize.height - (inset * 2));
                
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setBounds(0, 80, (int)screenSize.getWidth() - 50, (int)screenSize.getHeight() - 50);
        this.setVisible(true);    
        //this.setMaximizable(true);  
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        // Valor Minimo Extrato depositários
        try {extMax = LerValor.StringToFloat(LerValor.FormatNumber(conn.LerParametros("EXTMAX"),2));} catch (Exception ex) {extMax = 0f;}
        
        InitjPagar();

        FillCombos(false);
        AutoCompletion.enable(jRgprp);
        AutoCompletion.enable(jNomeProp);

        jbtAdcRetencao.setEnabled(false);
        
        ComboBoxEditor edit1 = jRgprp.getEditor();
        Component comp1 = edit1.getEditorComponent();
        comp1.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                tPag.Clear();
                tPag.Enable(false);
                jResto.setText("0,00");
                tPag.vrAREC = 0;
                jView.removeAll();
                jView.revalidate();
                RefreshVisor();
                jView.repaint();
                tPag.LimpaTransicao();
                CleanLastPagto();
                jbtAdcRetencao.setEnabled(false);

                anexos = new String[0];
                btAnexo.setEnabled(false);
                btAnexo.setText("Anexos [00]");
                btLancTxBanc.setEnabled(false);
            }
        });

        ComboBoxEditor edit = jNomeProp.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                String sPrint = Imprimir(true);
                if (sPrint == null) return;
                tPag.vrAREC = LerValor.StringToFloat(sPrint);
                jResto.setValue(LerValor.StringToFloat(sPrint));
                
                FillBloqueados(jliberar, jRgprp.getSelectedItem().toString());

                // mostra data ult pagto e valor
                ShowLastPagto(jRgprp.getSelectedItem().toString());

                jRgprp.setEnabled(false);
                jNomeProp.setEnabled(false);
                
                tPag.rgimv = rgimv; tPag.rgprp = jRgprp.getSelectedItem().toString(); tPag.contrato = contrato; tPag.acao = "ET"; tPag.operacao = "DEB";
                rgprp = tPag.rgprp; rgimv = tPag.rgimv; contrato = tPag.contrato;
                btnLancar.setEnabled(false);
                tPag.LimpaTransicao();
                jbtAdcRetencao.setEnabled(true);

                tPag.Enable(true);
                if (Depositario()) {
                    if (LerValor.StringToFloat(sPrint) < extMax) {
                        JOptionPane.showMessageDialog(null, "Depósitos não podem ser inferior a " + extMax + " !!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);                        
                        tPag.Enable(false);
                        btnCancelar.setEnabled(true);
                    }
                } 
                btnLancar.setEnabled(false);
                
                anexos = new String[0];
                btAnexo.setEnabled(true);
                btAnexo.setText("Anexos [00]");
                btLancTxBanc.setEnabled(true);
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                tPag.Clear();
                tPag.Enable(false);
                jResto.setText("0,00");
                tPag.vrAREC = 0;
                jView.removeAll();
                jView.revalidate();
                jView.repaint();
                RefreshVisor();
                tPag.LimpaTransicao();
                jbtAdcRetencao.setEnabled(false);
                
                anexos = new String[0];
                btAnexo.setEnabled(false);
                btAnexo.setText("Anexos [00]");
                btLancTxBanc.setEnabled(false);
            }
        });

        CleanLastPagto();
        jRgprp.requestFocus();
    }

    private Boolean Depositario() {
        Boolean retorno = false;
        String sSql = "SELECT DISTINCT e.rgprp, p.nome AS nome FROM extrato e, proprietarios p WHERE (e.rgprp = '" + jRgprp.getSelectedItem().toString().trim() +"') and (Upper(p.status) = 'ATIVO') and p.rgprp = e.rgprp and e.tag <> 'X' and TRIM(p.conta) <> '' ORDER BY Lower(p.nome);";
        ResultSet rs = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                retorno = true;
                break;
            }
        } catch (Exception ex) {}
        DbMain.FecharTabela(rs);
        return retorno;
    }
    
    private void ShowLastPagto(String sProp) {
        String sData = ""; String sValor = "0,00"; String sEmail = ""; String sObscaixa = "";
        String[][] lastFields = null;
        try {
            lastFields = conn.LerCamposTabela(new String[] {"dtultpagto", "vrultpagto", "email", "obscaixa"}, "proprietarios", "rgprp = '" + sProp + "';");
        } catch (Exception ex) {}

        if (lastFields != null) {
            sData = Dates.DateFormata("dd/MM/yyyy",Dates.StringtoDate(lastFields[0][3],"yyyy-MM-dd"));
            sValor = LerValor.FloatToString(Float.valueOf(lastFields[1][3]));
            sEmail = lastFields[2][3];
            sObscaixa = lastFields[3][3];
        }
        
        jDtUltPagto.setText(sData);
        jVrUltPagto.setText(sValor);
        jObs.setText(sObscaixa);
        
        if (!"".equals(sEmail.trim())) {
            jEnviarEmail.setForeground(Color.green);
            jEnviarEmail.setEnabled(true);
        } else {
            jEnviarEmail.setForeground(Color.black);
            jEnviarEmail.setEnabled(false);
        }
    }

    private void CleanLastPagto() {
        String sData = "          "; String sValor = "0,00";
        jDtUltPagto.setText(sData);
        jVrUltPagto.setText(sValor);
        jObs.setText(null);
    }
    
    private void FillBloqueados(JTable table, String tProp) {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"contrato","inquilino","vencimento","recebimento","valor"},{"80","400","100","100","100"}});

        String sSql = "SELECT contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, tag FROM extrato WHERE rgprp = '&1.' AND (tag <> 'X' AND tag = 'B') ORDER BY dtvencimento;";
        sSql = FuncoesGlobais.Subst(sSql, new String[] {jRgprp.getSelectedItem().toString()});
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        float fTotCred = 0; float fTotDeb = 0; float fSaldoAnt = 0;
        String inq = "";
        try {
            while (imResult.next()) {
                String tmpCampo = imResult.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);
                fTotCred = 0; fTotDeb = 0; fSaldoAnt = 0;
                for (int j = 0; j<rCampos.length; j++) {
                    boolean bRetc = FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1;
                    if ("AL".equals(rCampos[j][4])) {
                        if (LerValor.isNumeric(rCampos[j][0])) {
                            inq = new Pad(conn.LerCamposTabela(new String[] {"nomerazao"}, "locatarios", "contrato = '" + imResult.getString("contrato") + "'")[0][3],18).RPad();
                                         //Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtvencimento")) + " - " + Dates.DateFormata("dd/MM/yyyy", hrs.getDate("dtrecebimento")) + "]";

                            fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                            if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        } else {
                            if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        }
                    }
                }

                String trgimv = String.valueOf(imResult.getInt("contrato"));
                String tinq = inq;
                String tvecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtvencimento"));
                String trecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtrecebimento"));
                String tvalor = LerValor.floatToCurrency(fTotCred - fTotDeb,2);
                TableControl.add(table, new String[][]{{trgimv, tinq, tvecto, trecto,tvalor},{"C","L","C","C","R"}}, true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);

        sSql = "SELECT registro, campo, autenticacao FROM avisos WHERE registro = '&1.' AND (tag <> 'X' AND tag = 'B') AND rid = '0' ORDER BY autenticacao;";
        sSql = FuncoesGlobais.Subst(sSql, new String[] {tProp});
        imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (imResult.next()) {
                String tmpCampo = "" + imResult.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);
                String sinq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" + rCampos[0][7].substring(2,4) + "/" + rCampos[0][7].substring(4);
                String tregistro = imResult.getString("registro");
                String tinq = sinq;
                String tvalor = LerValor.FormatNumber(rCampos[0][2],2);
                
                TableControl.add(table, new String[][]{{tregistro, tinq,"","", tvalor},{"C","L","C","C","R"}}, true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);        
    }
    
    private void FillCombos(boolean Depositos) {
        String sSql = "";
        if (!Depositos) {
            sSql = "SELECT distinct p.rgprp, p.nome FROM proprietarios p WHERE Upper(p.status) = 'ATIVO' ORDER BY p.nome;";
        } else {
            //sSql = "SELECT DISTINCT e.rgprp, p.nome AS nome FROM extrato e, proprietarios p WHERE e.rgprp = p.rgprp AND TRIM(p.conta) <> '' AND tag <> 'X' ORDER BY Lower(p.nome);";
            // 09/05/2012 para mostar somente asqueles que possuem saldo
            sSql = "SELECT DISTINCT e.rgprp, p.nome AS nome FROM extrato e, proprietarios p WHERE (Upper(p.status) = 'ATIVO') and p.rgprp = e.rgprp and e.tag <> 'X' and TRIM(p.conta) <> '' ORDER BY Lower(p.nome);";
        }
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        jRgprp.removeAllItems();
        jNomeProp.removeAllItems();
        try {
            while (imResult.next()) {
                jRgprp.addItem(String.valueOf(imResult.getInt("rgprp")));
                jNomeProp.addItem(imResult.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }

    private void ImprimirExtrato() {
        Imprimir(false);
    }

    private String Imprimir(boolean Preview) {
        Collections gVar = VariaveisGlobais.dCliente;
        List<Extrato> lista = new ArrayList<Extrato>();
        String[][] sCampos = {};
        float tpagar = 0;
        String dados_prop[][] = null;
        
        float fTotCred = 0; float fTotDeb = 0; float fSaldoAnt = 0;
        try {
            dados_prop = conn.LerCamposTabela(new String[] {"banco", "agencia", "conta", "favorecido","cpfcnpj","saldoant"}, "proprietarios", "rgprp = '" + jRgprp.getSelectedItem().toString() + "'");
            String sdant = dados_prop[5][3]; //conn.LerCamposTabela(new String[] {"saldoant"}, "proprietarios", "rgprp = '" + jRgprp.getSelectedItem().toString() + "'")[0][3];
            //fSaldoAnt = FuncoesGlobais.strCurrencyToFloat(sdant);
            fSaldoAnt = Float.valueOf(sdant.trim());
        } catch (SQLException ex) {
            Logger.getLogger(jExtrato.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (fSaldoAnt > 0) {
            fTotCred += fSaldoAnt;
            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Saldo Anterior","0;;black",LerValor.floatToCurrency(fSaldoAnt, 2) + " ",""});
            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"","0;;black","",""});
        }

        //String sql = "SELECT contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento FROM extrato WHERE rgprp = '&1.' AND (tag <> 'X' AND tag <> 'B') ORDER BY dtvencimento;";
        String bloqAD = "";
        if (VariaveisGlobais.bloqAdianta) bloqAD = " AND InStr(campo, '@') = 0 ";
        String sql = "SELECT contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut FROM extrato WHERE rgprp = '&1.' AND (tag <> 'X' AND tag <> 'B' " + bloqAD + " ) ORDER BY rgimv, dtrecebimento;";
               sql = FuncoesGlobais.Subst(sql, new String[] {jRgprp.getSelectedItem().toString().trim()});

        ResultSet hrs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (hrs.next()) {
                String tmpCampo = hrs.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);

                for (int j = 0; j<rCampos.length; j++) {
                    //String tpCampo = new Pad(rCampos[j][rCampos[j].length - 1], 25).RPad();
                    String tpCampo = rCampos[j][rCampos[j].length - 1];
                    if (VariaveisGlobais.bShowCotaParcelaExtrato) {
                        String spart1 = "", spart2 = "", scotaparc = "";
                        if (!"".equals(rCampos[j][3].trim())) {
                            spart1 = rCampos[j][3].trim().substring(0, 2);
                            spart2 = rCampos[j][3].trim().substring(2);
                        } else {
                            spart1 = "00"; spart2 = "0000";
                        }
                        if (!"00".equals(spart1) && "0000".equals(spart2)) {
                            spart1 = "00";
                        } else if ("00".equals(spart1) && !"0000".equals(spart2)) {
                            spart2 = "0000";
                        }
                        scotaparc = spart1 + spart2;
                        tpCampo += "  " + ("0000".equals(scotaparc) || "000000".equals(scotaparc) || "".equals(scotaparc) ? "       " : scotaparc.substring(0,2) + "/" + scotaparc.substring(2));
                    }
                    boolean bRetc = (FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1) || (FuncoesGlobais.IndexOf(rCampos[j], "AT") > -1);
                    if ("AL".equals(rCampos[j][4])) {
                        if (LerValor.isNumeric(rCampos[j][0])) {
                            String[][] hBusca = conn.LerCamposTabela(new String[] {"end", "num", "compl"}, "imoveis", "rgimv = '" + hrs.getString("rgimv") + "'");

                            java.awt.Font ft = new java.awt.Font("Arial",Font.NORMAL,8);
                            String imv = hrs.getString("rgimv").trim() + " - " + hBusca[0][3].trim() + ", " + hBusca[1][3].trim() + " " + hBusca[2][3].trim();
                            List aLinhas = StringUtils.wrap(imv, getFontMetrics(ft), 257);
                            for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { sCampos = FuncoesGlobais.ArraysAdd(sCampos,new String[] {StringManager.ConvStr((String) linha.next()).replace("ò", " "),"0;b;black","",""}); }

                            String loc = conn.LerCamposTabela(new String[] {"nomerazao"}, "locatarios", "contrato = '" + hrs.getString("contrato") + "'")[0][3];
                            aLinhas = null;
                            aLinhas = StringUtils.wrap(loc, getFontMetrics(ft), 257);
                            for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { sCampos = FuncoesGlobais.ArraysAdd(sCampos,new String[] {StringManager.ConvStr((String) linha.next()).replace("ò", " "),"0;;black","",""}); }
                            
                            String inq = "[" + Dates.DateFormata("dd/MM/yyyy", hrs.getDate("dtvencimento")) + (VariaveisGlobais.ShowRecebimentoExtrato ? " - " + Dates.DateFormata("dd/MM/yyyy", hrs.getDate("dtrecebimento")) : "          ") + "] - " + hrs.getString("rc_aut");
                            aLinhas = null;
                            aLinhas = StringUtils.wrap(inq, getFontMetrics(ft), 257);
                            for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { sCampos = FuncoesGlobais.ArraysAdd(sCampos,new String[] {StringManager.ConvStr((String) linha.next()).replace("ò", " "),"0;;black","",""}); }

                            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {tpCampo,"0;;black",LerValor.FormatNumber(rCampos[j][2],2) + " ",(bRetc ? LerValor.FormatNumber(rCampos[j][2],2) + " " : "")});

                            fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                            if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}

                            int nPos = FuncoesGlobais.IndexOf(rCampos[j], "CM");
                            if (nPos > -1) {
                                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("CM"),"0;;black","",LerValor.FormatNumber(rCampos[j][nPos].substring(2),2) + " "});
                                fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                            }

                            nPos = FuncoesGlobais.IndexOf(rCampos[j], "AD");
                            if (nPos > -1) {
                                if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(9),2)) > 0) {
                                    String wAD = rCampos[j][nPos].split("@")[1];
                                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Adiantamento","0;;black","", LerValor.FormatNumber(wAD,2) + " "});
                                    fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(wAD,2));
                                }
                            }
                            
                            String admAut = null; String admSql = null;
                            float adm_mu = 0, adm_ju = 0, adm_co = 0, adm_ep = 0;
                            try { admAut = hrs.getString("rc_aut"); } catch (SQLException e) {admAut = null;}
                            if (admAut != null) {
                                admSql = "SELECT * FROM auxiliar where conta = 'ADM' and rc_aut = " + admAut + " and dtvencimento = '" + hrs.getDate("dtvencimento") + "' LIMIT 1;";
                                ResultSet admRs = conn.AbrirTabela(admSql, ResultSet.CONCUR_READ_ONLY);
                                try {
                                    while (admRs.next()) {
                                        String mujuco = null; String[] amujuco = null;
                                        try {mujuco = admRs.getString("campo");} catch (SQLException e) {}
                                        if (mujuco != null) {
                                            amujuco = mujuco.split(":");
                                        }
                                        for (String cpo : amujuco) {
                                            if (cpo.substring(0, 2).equalsIgnoreCase("MU")) adm_mu += LerValor.StringToFloat(LerValor.FormatNumber(cpo.substring(2), 2));
                                            if (cpo.substring(0, 2).equalsIgnoreCase("JU")) adm_ju += LerValor.StringToFloat(LerValor.FormatNumber(cpo.substring(2), 2));
                                            if (cpo.substring(0, 2).equalsIgnoreCase("CO")) adm_co += LerValor.StringToFloat(LerValor.FormatNumber(cpo.substring(2), 2));
                                            if (cpo.substring(0, 2).equalsIgnoreCase("EP")) adm_ep += LerValor.StringToFloat(LerValor.FormatNumber(cpo.substring(2), 2));
                                        }
                                    }
                                } catch (SQLException e) {}
                                try {admRs.close();} catch (Exception e) {}
                            }
                            
                            nPos = FuncoesGlobais.IndexOf(rCampos[j], "MU");
                            if (nPos > -1) {
                                if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                    if (!VariaveisGlobais.extMU) {
                                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("MU"),"0;;black",LerValor.FormatNumber(rCampos[j][nPos].substring(2),2) + " ",""});
                                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                    } else {
                                        float tmpMU = LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                        tmpMU += adm_mu;
                                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("MU") + " / Comissão s/" + gVar.get("MU"),"0;;black", LerValor.floatToCurrency(tmpMU, 2) + " ",LerValor.floatToCurrency(adm_mu, 2) + " "});
                                        fTotCred += tmpMU; fTotDeb += adm_mu;
                                    }
                                }
                            }

                            nPos = FuncoesGlobais.IndexOf(rCampos[j], "JU");
                            if (nPos > -1) {
                                if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                    if (!VariaveisGlobais.extJU) {
                                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("JU"),"0;;black",LerValor.FormatNumber(rCampos[j][nPos].substring(2),2) + " ",""});
                                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                    } else {
                                        float tmpJU = LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                        tmpJU += adm_ju;
                                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("JU") + " / Comissão s/" + gVar.get("JU"),"0;;black", LerValor.floatToCurrency(tmpJU, 2) + " ",LerValor.floatToCurrency(adm_ju, 2) + " "});
                                        fTotCred += tmpJU; fTotDeb += adm_ju;
                                    }
                                }
                            }

                            nPos = FuncoesGlobais.IndexOf(rCampos[j], "CO");
                            if (nPos > -1) {
                                if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                    if (!VariaveisGlobais.extCO) {
                                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("CO"),"0;;black",LerValor.FormatNumber(rCampos[j][nPos].substring(2),2) + " ",""});
                                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                    } else {
                                        float tmpCO = LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                        tmpCO += adm_co;
                                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("CO") + " / Comissão s/" + gVar.get("CO"),"0;;black", LerValor.floatToCurrency(tmpCO, 2) + " ",LerValor.floatToCurrency(adm_co, 2) + " "});
                                        fTotCred += tmpCO; fTotDeb += adm_co;
                                    }
                                }
                            }

                            nPos = FuncoesGlobais.IndexOf(rCampos[j], "EP");
                            if (nPos > -1) {
                                if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                    if (!VariaveisGlobais.extEP) {
                                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("EP"),"0;;black",LerValor.FormatNumber(rCampos[j][nPos].substring(2),2) + " ",""});
                                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                    } else {
                                        float tmpEP = LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                        tmpEP += adm_ep;
                                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {gVar.get("EP") + " / Comissão s/" + gVar.get("EP"),"0;;black", LerValor.floatToCurrency(tmpEP, 2) + " ",LerValor.floatToCurrency(adm_ep, 2) + " "});
                                        fTotCred += tmpEP; fTotDeb += adm_ep;
                                    }
                                }
                            }
                        } else {
                            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {tpCampo,"0;;black",LerValor.FormatNumber(rCampos[j][2],2) + " ",(bRetc ? LerValor.FormatNumber(rCampos[j][2],2) + " " : "")});
                            if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        }
                    } else if (FuncoesGlobais.IndexOf(rCampos[j], "AD") > -1) {
                        int nPos = FuncoesGlobais.IndexOf(rCampos[j], "AD");
                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].split("@")[1],2)) > 0) {
                            String wAD = rCampos[j][nPos].split("@")[1];
                            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Adiantamento - " + rCampos[j][nPos].split("@")[0].substring(2),"0;;black","", LerValor.FormatNumber(wAD,2) + " "});
                            fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(wAD,2));
                        }                        
                    } else if ("DC".equals(rCampos[j][4])) {
                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Desc." + tpCampo.trim(),"0;;black",(bRetc ? LerValor.FormatNumber(rCampos[j][2],2) + " " : ""),LerValor.FormatNumber(rCampos[j][2],2) + " "});
                        fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                        if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                    } else if ("DF".equals(rCampos[j][4])) {
                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Dif." + tpCampo.trim(),"0;;black",LerValor.FormatNumber(rCampos[j][2],2) + " ",(bRetc ? LerValor.FormatNumber(rCampos[j][2],2) + " " : "")});
                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                        if (bRetc) {fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                    } else if ("SG".equals(rCampos[j][4])) {
                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {tpCampo,"0;;black",LerValor.FormatNumber(rCampos[j][2],2) + " ",(bRetc ? LerValor.FormatNumber(rCampos[j][2],2) + " " : "")});
                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                        if (bRetc) {fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                    } else {
                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {tpCampo,"0;;black",LerValor.FormatNumber(rCampos[j][2],2) + " ",(bRetc ? LerValor.FormatNumber(rCampos[j][2],2) + " " : "")});
                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                        if (bRetc) {fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                    }
                }
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"","0;;black","",""});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(hrs);

        sql = FuncoesGlobais.Subst("SELECT campo, autenticacao FROM avisos WHERE registro = '&1.' AND rid = '0' AND (tag <> 'X' AND tag <> 'B' OR ISNULL(tag)) ORDER BY autenticacao;", new String[] {jRgprp.getSelectedItem().toString()});
        hrs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (hrs.next()) {
                String tmpCampo = "" + hrs.getString("campo");
                String tmpAuten = "" + hrs.getString("autenticacao");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, false);
                String sinq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" + rCampos[0][7].substring(2,4) + "/" + rCampos[0][7].substring(4) + " - " + tmpAuten;
                if (!"".equals(sinq.trim())) {
                    java.awt.Font ft = new java.awt.Font("Arial",Font.NORMAL,9);
                    List aLinhas = StringUtils.wrap(sinq, getFontMetrics(ft), 257);
                    for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { sCampos = FuncoesGlobais.ArraysAdd(sCampos,new String[] {StringManager.ConvStr((String) linha.next()).replace("ò", " "),"0;;black","",""}); }
                    //String aLinhas[] = WordWrap.wrap(sinq, 237, getFontMetrics(new java.awt.Font("SansSerif",Font.NORMAL,8))).split("\n");
                    //for (int k=0;k<aLinhas.length;k++) { sCampos = FuncoesGlobais.ArraysAdd(sCampos,new String[] {StringManager.ConvStr(aLinhas[k]).replace("ò", " "),"0;;black","",""}); }
                    if ("CRE".equals(rCampos[0][8])) {
                        sCampos[sCampos.length - 1][2] = LerValor.FormatNumber(rCampos[0][2],2) + " ";
                        sCampos[sCampos.length - 1][3] = "";

                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2],2));
                    } else {
                        sCampos[sCampos.length - 1][2] = "";
                        sCampos[sCampos.length - 1][3] = LerValor.FormatNumber(rCampos[0][2],2) + " ";
                        fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2],2));
                    }
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"","0;;black","",""});
                }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(hrs);

        if (VariaveisGlobais.marca.trim().equalsIgnoreCase("artvida")) {
            // Colocado aqui para satisfazer o cliente em 04-08-2014
            // Creditos
            if (fTotCred >= 0) {
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Total de Creditos","0;b;black",LerValor.floatToCurrency(fTotCred, 2),""});
            } else {
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Total de Creditos","0;b;black","",LerValor.floatToCurrency(fTotCred, 2)});
            }
            
            // Debitos
            if (fTotDeb >= 0) {
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Total de Débitos","0;b;black","",LerValor.floatToCurrency(fTotDeb, 2)});
            } else {
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Total de Débitos","0;b;black",LerValor.floatToCurrency(fTotDeb, 2),""});
            }
            
            // Total
            String saldoext = LerValor.floatToCurrency(fTotCred - fTotDeb, 2);
            if (saldoext.trim().equalsIgnoreCase("-0,00")) saldoext = "0,00";
            if (fTotCred - fTotDeb >= 0) {
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Liquido a Receber","0;b;black",saldoext,""});
            } else {
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Liquido a Receber","0;b;black","",saldoext});
            }
        } else {       
            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {new Pad("Total de Créditos .... " + LerValor.floatToCurrency(fTotCred, 2),45).RPad() ,"0;b;black","",""});
            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {new Pad("Total de Débitos........" + LerValor.floatToCurrency(fTotDeb, 2),45).RPad(),"0;b;black","",""});
            String saldoext = LerValor.floatToCurrency(fTotCred - fTotDeb, 2);
            if (saldoext.trim().equalsIgnoreCase("-0,00")) saldoext = "0,00";
            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {new Pad("Liquido a Receber..........." + saldoext,45).RPad(),(fTotCred - fTotDeb < 0 ? "0;b;red" : "0;b;black"),"",""});
        }
        
        // 09/05/2012 Implementação dos dados do depósito
        if (dados_prop != null) {
            try {
                if (!dados_prop[0][3].trim().equals("")) {
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"","0;;black","",""});
                    if (dados_prop[3][3].trim().equals("")) sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Cpf/Cnpj: " + dados_prop[4][3],"0;b;red","",""});
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Banco: " + dados_prop[0][3],"0;b;red","",""});
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Agencia: " + dados_prop[1][3],"0;b;red","",""});
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Conta: " + dados_prop[2][3],"0;b;red","",""});
                    if (!dados_prop[3][3].trim().equals("")) {
                        String aLinhas[] = WordWrap.wrap("Favorecido: " + dados_prop[3][3], 210, getFontMetrics(new java.awt.Font("SansSerif",Font.NORMAL,8))).split("\n");
                        for (int k=0;k<aLinhas.length;k++) { sCampos = FuncoesGlobais.ArraysAdd(sCampos,new String[] {aLinhas[k],"0;b;red","",""}); }                        
                        //sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Favorecido: " + dados_prop[3][3],"0;b;red","",""});
                    }
                }
            } catch (Exception e) {}
        }
        
        String[][] aTrancicao = null;
        float nAut = 0;
        if (!Preview) {
            try {
                aTrancicao = tPag.Transicao("ET");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (aTrancicao.length <= 0 ) return LerValor.floatToCurrency(fTotCred - fTotDeb, 2);

            nAut = (float)Autenticacao.getAut();
            if (!Autenticacao.setAut((double)nAut, 1)) {
                JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            //try {nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));} catch (SQLException ex) {}

            // grava no caixa
            conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, aTrancicao,String.valueOf(nAut).replace(".0", ""));

            // pegar valor pago do extrato
            tpagar = 0;
            for (int t=0;t<aTrancicao.length;t++) { tpagar += Float.valueOf(aTrancicao[t][4]); }

            // grava no auxiliar
            String tmpTexto = "INSERT INTO auxiliar (conta, contrato, campo, dtvencimento, dtrecebimento, rc_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.');";
            String sVar = "ET:9:" + FuncoesGlobais.GravaValor(LerValor.floatToCurrency(tpagar, 2)) +
                          ":000000:ET:" + String.valueOf(nAut).replace(".0", "") +
                          ":" + Dates.DateFormata("ddMMyyyy", new Date()) + ":DEB:" +
                          FuncoesGlobais.CriptaNome("EXTRATO") + ":" + VariaveisGlobais.usuario;
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"EXT",rgprp,sVar,
                    Dates.DateFormata("yyyy/MM/dd", new Date()), Dates.DateFormata("yyyy/MM/dd", new Date()),
                    FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            // gravar no razao
            String extVar = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, tag) VALUES ('PR','AV:9:&1.:000000:AV:ET:&2._05PR:&3.:&4.:&5.:&6.:&7.','&8.','&9.','&10.')";
            extVar = FuncoesGlobais.Subst(extVar, new String[] {
                     FuncoesGlobais.GravaValor(LerValor.floatToCurrency(tpagar, 2)),
                     FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6),
                     Dates.DateFormata("ddMMyyyy", new Date()),
                     "DEB", aTrancicao[0][5], FuncoesGlobais.CriptaNome("CONTA PROPRIETARIO"),
                     VariaveisGlobais.usuario,
                     Dates.DateFormata("yyyy/MM/dd", new Date()),
                     Dates.DateFormata("yyyy/MM/dd", new Date()), " "}).replace("_", "");
            conn.ExecutarComando(extVar);

            extVar = "UPDATE proprietarios SET dtultpagto = '&1.', vrultpagto = '&2.', saldoant = '&3.' WHERE rgprp = '&4.'";
            extVar = FuncoesGlobais.Subst(extVar, new String[] {
                     Dates.DateFormata("yyyy/MM/dd", new Date()),
                     String.valueOf(tpagar),
                     String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(tPag.vrAREC - tpagar, 2))),
                     rgprp});
            conn.ExecutarComando(extVar);

            extVar = "UPDATE extrato SET TAG = 'X', ET_AUT = '&1.', PR_SDANT = '&2.' WHERE RGPRP = '&3.' AND (TAG <> 'X' AND TAG <> 'B') AND ET_AUT = 0;";
            extVar = FuncoesGlobais.Subst(extVar, new String[] {
                FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6),
                String.valueOf(fSaldoAnt), rgprp});
            conn.ExecutarComando(extVar);
            // reforço de gravação
            String tmpVar = "UPDATE extrato SET TAG = 'X' WHERE ET_AUT = '" + FuncoesGlobais.StrZero(LerValor.FloatToString((int)nAut).replace(",0", ""), 6) + "';";
            try {
                conn.ExecutarComando(tmpVar);
            } catch (Exception e) {e.printStackTrace(); }
            tmpVar = null;
            // -- fim reforço
            
            extVar = "UPDATE avisos SET tag = 'X', et_aut = '&1.' WHERE registro = '&2.' AND (tag <> 'X' AND tag <> 'B' OR ISNULL(tag)) AND et_aut = 0;";
            extVar = FuncoesGlobais.Subst(extVar, new String[] {
            FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6), rgprp});
            conn.ExecutarComando(extVar);

        }

        String sAut = FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6);

        Extrato bean1 = new Extrato();
        int n = 0;
        // Impressao do header
        // Logo da Imobiliaria
        bean1 = HeaderExtrato(bean1, Preview, sAut);

        // limpa linhas
        for (int i=1;i<=40;i++) {bean1.sethist_linhan(i, ""); bean1.sethist_linhan_cor(i,"0;;black");}

        for (int i=0;i<sCampos.length;i++) {
            if (n == 39) {
                lista.add(bean1);
                bean1 = new Extrato();
                bean1 = HeaderExtrato(bean1, Preview, sAut);
                n = 0;
            }
            bean1.sethist_linhan(n + 1, sCampos[i][0]);
            bean1.sethist_linhan_cor(n + 1, sCampos[i][1]);
            bean1.sethist_credn(n + 1, sCampos[i][2]);
            bean1.sethist_debn(n + 1, sCampos[i][3]);
            n++;
        }

        if (!Preview) {
          if (n == 39) {
              lista.add(bean1);
              bean1 = new Extrato();
              bean1 = HeaderExtrato(bean1, Preview, sAut);
              n = 0;
          }
          bean1.sethist_linhan(n + 1,"");
          bean1.sethist_linhan_cor(n + 1, "0;;black");
          n++;

          if (n == 39) {
              lista.add(bean1);
              bean1 = new Extrato();
              bean1 = HeaderExtrato(bean1, Preview, sAut);
              n = 0;
          }
          bean1.sethist_linhan(n + 1,"VALOR(ES) LANCADOS");
          bean1.sethist_linhan_cor(n + 1, "0;;blue");
          n++;

          if (n == 39) {
              lista.add(bean1);
              bean1 = new Extrato();
              bean1 = HeaderExtrato(bean1, Preview, sAut);
              n = 0;
          }
          bean1.sethist_linhan(n + 1,"--------------------------------------------------------");
          bean1.sethist_linhan_cor(n + 1, "0;;blue");
          n++;

          for (int i=0;i<aTrancicao.length;i++) {
             if (n == 39) {
                 lista.add(bean1);
                 bean1 = new Extrato();
                 bean1 = HeaderExtrato(bean1, Preview, sAut);
                 n = 0;
             }

              String bLinha = "";
              if (!"".equals(aTrancicao[i][1].trim())) {
                  bLinha = "BCO:" + new Pad(aTrancicao[i][1],3).RPad() +
                           " AG:" + new Pad(aTrancicao[i][2],4).RPad() +
                           " CH:" + new Pad(aTrancicao[i][3],8).RPad() +
                           " DT: " + new Pad(aTrancicao[i][0],10).CPad() +
                           " VR:" + new Pad(aTrancicao[i][4],10).LPad();
              } else {
                  bLinha = aTrancicao[i][5].trim().replaceAll("CT", "BC") + ":" + (aTrancicao[i][8].isEmpty() ? "" : aTrancicao[i][8]) + new Pad(aTrancicao[i][4],10).LPad();
              }
              bean1.sethist_linhan(n + 1,bLinha);
              bean1.sethist_linhan_cor(n + 1, "0;;red");
              n++;
          }

          //bean1.setautentica("PAL" + sAut);
          bean1.setautentica( VariaveisGlobais.dCliente.get("marca").trim() + "ET" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(LerValor.FloatToString(tpagar), 2) + VariaveisGlobais.usuario);
        } else bean1.setautentica("");

        lista.add(bean1);

        // 25-06-2013 - By wellspinto@gmail.com
        JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);

        new jDirectory("reports/Extratos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String pathName = "reports/Extratos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

        String FileNamePdf = pathName + jRgprp.getSelectedItem().toString().trim() + " - " + jNomeProp.getSelectedItem().toString().trim() + "_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 7) + ".pdf"; //Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + ".pdf";

        try {
            Map parametros = new HashMap();
            parametros.put("parameter1", VariaveisGlobais.ExtratoTotal);

            String fileName = "reports/" + (Preview ? VariaveisGlobais.extPreview : VariaveisGlobais.extPrint);
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrds);

            if (!Preview) {
                // Create a PDF exporter
                JRExporter exporter = new JRPdfExporter();

                // Configure the exporter (set output file name and print object)
                String outFileName = FileNamePdf;
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

                // Export the PDF file
                exporter.exportReport();
            }

            if (Preview) {
                jView.removeAll();
                visor = new JRViewer(print);
                visor.setBackground(Color.WHITE);
                visor.setOpaque(true);
                visor.setVisible(true);
                visor.setBounds(0, 0, jView.getWidth(), jView.getHeight());
                //visor.setFitWidthZoomRatio();
                visor.setFitPageZoomRatio();
                jView.add(visor);
                RefreshVisor();
            } else {
                new toPrint(FileNamePdf, VariaveisGlobais.Extrato.split(",")[0],VariaveisGlobais.Extrato.split(",")[1],VariaveisGlobais.Extrato.split(",")[2]);
                
//                //- Colocado no dia 07/07/2014
//                String docPrint = backlashReplace(FileNamePdf);
//                ComandoExterno ce = new ComandoExterno();
//                ce.ComandoExterno(VariaveisGlobais.extPrintCmd + " \"" + docPrint + "\"");

                //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

                if (jEnviarEmail.isSelected()) {
                    String[][] EmailLocaDados = conn.LerCamposTabela(new String[] {"nome","email"}, "proprietarios", "rgprp = '" + jRgprp.getSelectedItem().toString().trim() + "'");
                    String EmailLoca = EmailLocaDados[1][3].toLowerCase();
                    boolean emailvalido = (EmailLoca.indexOf("@") > 0) && (EmailLoca.indexOf("@")+1 < (EmailLoca.lastIndexOf(".")) && (EmailLoca.lastIndexOf(".") < EmailLoca.length()) );
                    if (emailvalido) {
                        try {
                            anexos = FuncoesGlobais.ArrayAdd(anexos, FileNamePdf);
                            if (!new JEmail().SendEmail(EmailLoca, anexos, "Extrato do Mês", "Documento em Anexo no formato pdf").equals("")) {
                                JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(jViewDoctos.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(jViewDoctos.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            }
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return LerValor.floatToCurrency(fTotCred - fTotDeb, 2);
    }

    private void RefreshVisor() {
        jView.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                visor.setBounds(0, 0, jView.getWidth(), jView.getHeight());
                visor.setFitPageZoomRatio();
                jView.revalidate();
            }
        });

        jView.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                try {
                visor.setBounds(0, 0, jView.getWidth(), jView.getHeight());
                jView.revalidate();
                visor.setFitWidthZoomRatio();
                } catch (Exception e) {}
            }
        });
    }
    
    private Extrato HeaderExtrato(Extrato bean1, boolean Preview, String barras) {
        Collections gVar = VariaveisGlobais.dCliente;

        // Impressao do header
        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/extrato/" + VariaveisGlobais.icoExtrato);
        bean1.setnomeProp(jRgprp.getSelectedItem().toString().trim() + " - " + jNomeProp.getSelectedItem().toString().trim());
        if (!Preview) bean1.setbarras(barras);

        try {
            if ("TRUE".equals(conn.LerParametros("ANIVERSARIO").toUpperCase())) {
                String msgNiver = conn.LerParametros("MSGANIVERSARIO");
                String DtNascProp = conn.LerCamposTabela(new String[] {"dtnasc"}, "proprietarios", "rgprp = '" + jRgprp.getSelectedItem().toString() + "'")[0][3];
                if (DtNascProp != null) {
                    DtNascProp = DtNascProp.substring(0, 10);
                    if (Dates.iMonth(new Date()) == Dates.iMonth(Dates.StringtoDate(DtNascProp, "yyyy-MM-dd"))) bean1.setmensagem(msgNiver);
                }
            }
        } catch (SQLException ex) {}

        return bean1;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jView = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jRgprp = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jNomeProp = new javax.swing.JComboBox();
        jDepositos = new javax.swing.JToggleButton();
        jDemais = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jliberar = new javax.swing.JTable();
        jEnviarSite = new javax.swing.JCheckBox();
        jbtAdcRetencao = new javax.swing.JButton();
        jEnviarEmail = new javax.swing.JCheckBox();
        btAnexo = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jDtUltPagto = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jVrUltPagto = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jObs = new javax.swing.JTextPane();
        jpRecebe = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btLancTxBanc = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(".:: Extrato de Proprietário ::.");
        setVisible(true);

        jView.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout jViewLayout = new javax.swing.GroupLayout(jView);
        jView.setLayout(jViewLayout);
        jViewLayout.setHorizontalGroup(
            jViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 516, Short.MAX_VALUE)
        );
        jViewLayout.setVerticalGroup(
            jViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N

        jLabel1.setText("Rgprp:");

        jRgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRgprpActionPerformed(evt);
            }
        });

        jLabel2.setText("Nome:");

        jNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomePropActionPerformed(evt);
            }
        });

        jDepositos.setText("Depósitos");
        jDepositos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDepositosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNomeProp, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDepositos)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDepositos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jNomeProp, jRgprp});

        jDemais.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jDemais.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jPanel2.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jliberar.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jliberar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jliberar.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jliberar);

        jEnviarSite.setText("Site");

        jbtAdcRetencao.setText("Lançar/Remover Retenção");
        jbtAdcRetencao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtAdcRetencaoActionPerformed(evt);
            }
        });

        jEnviarEmail.setText("Enviar via EMail");

        btAnexo.setText("Anexos [00]");
        btAnexo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAnexoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtAdcRetencao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jEnviarSite)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jEnviarEmail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btAnexo, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtAdcRetencao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jEnviarSite, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEnviarEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btAnexo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Pagto:");

        jDtUltPagto.setBackground(java.awt.Color.white);
        jDtUltPagto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jDtUltPagto.setText("00/00/0000");
        jDtUltPagto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jDtUltPagto.setOpaque(true);

        jLabel6.setText("Valor:");

        jVrUltPagto.setBackground(java.awt.Color.white);
        jVrUltPagto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jVrUltPagto.setText("0,00");
        jVrUltPagto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jVrUltPagto.setOpaque(true);

        jLabel3.setBackground(new java.awt.Color(153, 153, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("A LIBERAR");
        jLabel3.setOpaque(true);

        jObs.setEditable(false);
        jObs.setBorder(javax.swing.BorderFactory.createTitledBorder("Observações"));
        jScrollPane2.setViewportView(jObs);

        javax.swing.GroupLayout jDemaisLayout = new javax.swing.GroupLayout(jDemais);
        jDemais.setLayout(jDemaisLayout);
        jDemaisLayout.setHorizontalGroup(
            jDemaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDemaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDemaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDemaisLayout.createSequentialGroup()
                        .addGroup(jDemaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jDemaisLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDtUltPagto, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jVrUltPagto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jDemaisLayout.setVerticalGroup(
            jDemaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDemaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDemaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jDtUltPagto, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jVrUltPagto, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpRecebe.setPreferredSize(new java.awt.Dimension(314, 313));

        javax.swing.GroupLayout jpRecebeLayout = new javax.swing.GroupLayout(jpRecebe);
        jpRecebe.setLayout(jpRecebeLayout);
        jpRecebeLayout.setHorizontalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );
        jpRecebeLayout.setVerticalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N

        btLancTxBanc.setText("Lanc.Tx.Banc.");
        btLancTxBanc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLancTxBancActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btLancTxBanc, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btLancTxBanc)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jDemais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jpRecebe, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDemais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRgprpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRgprpActionPerformed
        if (!bExecNome) {
            int pos = jRgprp.getSelectedIndex();
            if (jNomeProp.getItemCount() > 0) {bExecCodigo = true; jNomeProp.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jRgprpActionPerformed

    private void jNomePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomePropActionPerformed
        if (!bExecCodigo) {
            int pos = jNomeProp.getSelectedIndex();
            if (jRgprp.getItemCount() > 0) {bExecNome = true; jRgprp.setSelectedIndex(pos); bExecNome = false; }
        }
    }//GEN-LAST:event_jNomePropActionPerformed

    private void jDepositosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDepositosActionPerformed
        FillCombos(jDepositos.isSelected());
    }//GEN-LAST:event_jDepositosActionPerformed

    private void jbtAdcRetencaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtAdcRetencaoActionPerformed
        VariaveisGlobais.lbr_rgprp = jRgprp.getSelectedItem().toString();
        jAdcReten oTela = new jAdcReten(null, closable);
        oTela.setVisible(true);
        jRgprp.setEnabled(true);
        jNomeProp.setEnabled(true);
        jRgprp.requestFocus();
        //String sPrint = Imprimir(true);
        //tPag.vrAREC = LerValor.StringToFloat(sPrint);
        //jResto.setValue(LerValor.StringToFloat(sPrint));
    }//GEN-LAST:event_jbtAdcRetencaoActionPerformed

    private void btAnexoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAnexoActionPerformed
        if(this.anexos.length > 0) {
            Object[] aFiles = new Object[]{"Sim", "Não"};
            int n = JOptionPane.showOptionDialog((Component)null, "Deseja excluir os anexos ? ", "Atenção", 0, 3, (Icon)null, aFiles, aFiles[0]);
            if(n == 0) {
                this.anexos = new String[0];
            }
        }

        String[] var7 = this.escolherArquivos();
        String[] var8 = var7;
        int var4 = var7.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String files = var8[var5];
            this.anexos = FuncoesGlobais.ArrayAdd(this.anexos, files);
        }

        btAnexo.setText("Anexos [" + FuncoesGlobais.StrZero(String.valueOf(this.anexos.length), 2) + "]");
    }//GEN-LAST:event_btAnexoActionPerformed

    private void btLancTxBancActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLancTxBancActionPerformed
        Object[] options = new Object[]{"Sim", "Não"};
        int n = JOptionPane.showOptionDialog((Component)null, "Lança Taxa Bancária para o Proprietário " + jRgprp.getSelectedItem().toString() + " ? ", "Atenção", 0, 3, (Icon)null, options, options[0]);
        if(n == 0) {
            options = new Object[]{"TED", "DOC", "Cancelar"};
            int op = JOptionPane.showOptionDialog((Component)null, "Selecione TED ou DOC", "Atenção", 1, 3, (Icon)null, options, options[0]);
            if(op != 2) {
                String vrTarifa = "0,00";

                try {
                    if(op == 0) {
                        vrTarifa = LerValor.FormatNumber(conn.LerParametros("TARIFATED"), 2);
                    } else {
                        vrTarifa = LerValor.FormatNumber(conn.LerParametros("TARIFADOC"), 2);
                    }
                } catch (Exception var18) {
                    ;
                }

                vrTarifa = vrTarifa.replace(".", "");
                vrTarifa = vrTarifa.replace(",", ".");
                String[][] aTrancicao = new String[][]{{"", "", "", "", vrTarifa, "CT", "DEB", "AV", "00"}};
                double nAut = 0.0D;
                nAut = Autenticacao.getAut();
                if(!Autenticacao.setAut(nAut, 1.0D)) {
                    JOptionPane.showMessageDialog((Component)null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", 1);
                    return;
                }

                String corpo = "Taxa Bancária";
                String idAviso = "AVISO DISPONIVEL - DÉBITO";
                (new StringBuilder()).append(jRgprp.getSelectedItem().toString().trim()).append(" - ").append(jNomeProp.getSelectedItem().toString().trim()).toString();
                String idConta = "00PR";
                String sCodigo = "";
                String scpAviso = CampoAviso(FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6) + idConta, "DEB:XX", Dates.DateFormata("ddMMyyyy", new Date()), FuncoesGlobais.GravaValor(vrTarifa.replace(".", ",")), FuncoesGlobais.CriptaNome(corpo.replaceAll("\r\n", " ")));
                String tmpTexto = "INSERT INTO avisos (rid, registro, campo, autenticacao) VALUES (\'&1.\',\'&2.\',\'&3.\',\'&4.\');";
                tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[]{"0", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut), 6)});
                conn.ExecutarComando(tmpTexto);
                tmpTexto = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES (\'PR\',\'&1.\',\'&2.\',\'&3.\',\'&4.\');";
                tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[]{scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()), Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6)});

                try {
                    conn.ExecutarComando(tmpTexto);
                } catch (Exception var17) {
                    ;
                }

                sCodigo = jRgprp.getSelectedItem().toString().trim();
                conn.LancarCaixa(new String[]{jRgprp.getSelectedItem().toString(), "", ""}, aTrancicao, String.valueOf(nAut).replace(".0", ""));
                tmpTexto = "INSERT INTO auxiliar (conta, contrato, campo, dtvencimento, dtrecebimento, rc_aut) VALUES (\'&1.\',\'&2.\',\'&3.\',\'&4.\',\'&5.\',\'&6.\');";
                tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[]{"AVI", sCodigo, "AV:" + FuncoesGlobais.StrZero("0", 2) + scpAviso.substring(4), Dates.DateFormata("yyyy/MM/dd", new Date()), Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6)});
                conn.ExecutarComando(tmpTexto);
            }
        }
    }//GEN-LAST:event_btLancTxBancActionPerformed

    private String[] escolherArquivos() {
        File[] arquivos = null;
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Escolha o(s) arquivo(s)...");
        fc.setDialogType(0);
        fc.setApproveButtonText("OK");
        fc.setFileSelectionMode(0);
        fc.setMultiSelectionEnabled(true);
        fc.showOpenDialog(fc);
        arquivos = fc.getSelectedFiles();
        String[] aArquivos = new String[0];
        File[] var4 = arquivos;
        int var5 = arquivos.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            File f = var4[var6];

            try {
                aArquivos = FuncoesGlobais.ArrayAdd(aArquivos, f.getCanonicalPath().substring(2));
            } catch (IOException var9) {
                var9.printStackTrace();
            }
        }

        return aArquivos;
    }

    private String CampoAviso(String mAutenticacao, String mTipo, String mData, String mValor, String mDesc) {
        String retorno = "AV:9:" + mValor + ":000000:AV:ET:" + mAutenticacao + ":" + mData + ":" + mTipo + ":" + mDesc + ":" + VariaveisGlobais.usuario;
        return retorno;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAnexo;
    private javax.swing.JButton btLancTxBanc;
    private javax.swing.JPanel jDemais;
    private javax.swing.JToggleButton jDepositos;
    private javax.swing.JLabel jDtUltPagto;
    private javax.swing.JCheckBox jEnviarEmail;
    private javax.swing.JCheckBox jEnviarSite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JTextPane jObs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox jRgprp;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jView;
    private javax.swing.JLabel jVrUltPagto;
    private javax.swing.JButton jbtAdcRetencao;
    private javax.swing.JTable jliberar;
    private javax.swing.JPanel jpRecebe;
    // End of variables declaration//GEN-END:variables

}
