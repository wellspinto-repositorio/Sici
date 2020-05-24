/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento;

import Funcoes.*;
import Protocolo.Calculos;
import Transicao.jPagar;
import com.lowagie.text.Font;
import extrato.Extrato;
import Sici.Partida.Collections;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;


/**
 *
 * @author supervisor
 */
public class jAdiantAviso extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;

    jPagar tRec = new jPagar();
    JPanel pnlDigite = (JPanel) tRec.getComponent(ComponentSearch.ComponentSearch(tRec, "jpnDigite"));
    JButton btnLancar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtLancar"));
    JButton btnCancelar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtCancelar"));
    JPanel pnlBotoes = (JPanel) tRec.getComponent(ComponentSearch.ComponentSearch(tRec, "pnlBotoes"));
    JToggleButton btDN = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgDN"));
    JToggleButton btCH = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCH"));
    JToggleButton btCT = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCT"));
    JFormattedTextField jResto = (JFormattedTextField) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "JRESTO"));

    private void InitjReceber() {
        tRec.setVisible(true);
        tRec.setEnabled(true);
        tRec.setBounds(0, 0, 456, 280);
        try {
            jpRecebe.add(tRec);
        } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace(); }
        jpRecebe.repaint();
        jpRecebe.setEnabled(true);
        tRec.acao = "AD";

        btnLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tRec.Lancar();
                if (tRec.bprintdoc) {
                    try {
                        try {
                            String corpo = PrintAd();
                            String naut = Imprimir(corpo);
                            if (naut != null) SaveAd(naut);
                            
                            jValor.setText("0,00");
                            jValor.setEnabled(true);
                            
                            dispose();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tRec.Cancelar()) {
                    jValor.setText("0,00");
                    jValor.setEnabled(true);
                }
            }
        });

        btDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValor.setEnabled(false);
            }
        });

        btCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValor.setEnabled(false);
            }
        });

        btCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValor.setEnabled(false);
            }
        });

        // Verifica a existencia da tabela ADAVISOS
        if (!conn.ExisteTabelaAdAviso()) {
            JOptionPane.showInternalMessageDialog(this,"Tabela ADAVISOS não criada !!!", "atenção",JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    private Extrato HeaderExtrato(Extrato bean1, boolean Preview, String barras) {
        Collections gVar = VariaveisGlobais.dCliente;

        // Impressao do header
        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/extrato/" + VariaveisGlobais.icoExtrato);
        bean1.setnomeProp(jCodigo.getSelectedItem().toString().trim() + " - " + jNomeProp.getSelectedItem().toString().trim());
        //if (!Preview) 
        bean1.setbarras(barras);

        try {
            if ("TRUE".equals(conn.LerParametros("ANIVERSARIO").toUpperCase())) {
                String msgNiver = conn.LerParametros("MSGANIVERSARIO");
                String DtNascProp = conn.LerCamposTabela(new String[] {"dtnasc"}, "proprietarios", "rgprp = '" + jCodigo.getSelectedItem().toString() + "'")[0][3];
                if (DtNascProp != null) {
                    DtNascProp = DtNascProp.substring(0, 10);
                    if (Dates.iMonth(new Date()) == Dates.iMonth(Dates.StringtoDate(DtNascProp, "yyyy-MM-dd"))) bean1.setmensagem(msgNiver);
                }
            }
        } catch (SQLException ex) {}

        return bean1;
    }
    
    public void ImprimeAdiantamentoPDF(float nAut, String[][] Valores, String texto, String ValorRec, String idNome, String idAviso, String cutPaper) throws SQLException {
        List<Extrato> lista = new ArrayList<Extrato>();
        String sAut = FuncoesGlobais.StrZero(String.valueOf(nAut),6);
        Boolean Preview = false;
        Extrato bean1 = new Extrato();
        int n = 0;
        String[][] sCampos = {};
        String[][] aTrancicao = null;
        try {
            aTrancicao = tRec.Transicao("AD");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Impressao do header
        // Logo da Imobiliaria
        bean1 = HeaderExtrato(bean1, Preview, sAut);

        // limpa linhas
        for (int i=1;i<=40;i++) {bean1.sethist_linhan(i, ""); bean1.sethist_linhan_cor(i,"0;;black");}

        for (int i=0;i<jRecibos.getRowCount();i++) {
            String tag = jRecibos.getModel().getValueAt(i, 0).toString().trim();
            if (tag.equalsIgnoreCase("X")) {
                String tcontrato = jRecibos.getModel().getValueAt(i, 2).toString();
                String trgimv = jRecibos.getModel().getValueAt(i, 1).toString();
                String tnome  = jRecibos.getModel().getValueAt(i, 3).toString();
                String tvecto  = jRecibos.getModel().getValueAt(i, 4).toString();
                String tvalor  = jRecibos.getModel().getValueAt(i, 5).toString();
                String tender  = jRecibos.getModel().getValueAt(i, 6).toString();
                String talug  = jRecibos.getModel().getValueAt(i, 7).toString();
                String tdesc  = jRecibos.getModel().getValueAt(i, 8).toString();
                String tdife  = jRecibos.getModel().getValueAt(i, 9).toString();
                String tcomi  = jRecibos.getModel().getValueAt(i, 10).toString();
                String todes  = jRecibos.getModel().getValueAt(i, 11).toString();
                String todif = jRecibos.getModel().getValueAt(i, 12).toString();
                String tadav = jRecibos.getModel().getValueAt(i, 13).toString();
                
                String[][] hBusca = conn.LerCamposTabela(new String[] {"end", "num", "compl"}, "imoveis", "rgimv = '" + trgimv + "'");

                String imv = trgimv + " - " + hBusca[0][3].trim() + ", " + hBusca[1][3].trim() + " " + hBusca[2][3].trim();
                String aLinhas[] = WordWrap.wrap(imv, 237, getFontMetrics(new java.awt.Font("SansSerif",Font.NORMAL,8))).split("\n");
                for (int k=0;k<aLinhas.length;k++) { sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {aLinhas[k],"0;;black","",""}); }

                String inq = "[" + new Pad(conn.LerCamposTabela(new String[] {"nomerazao"}, "locatarios", "contrato = '" + tcontrato + "'")[0][3],18).RPad() + "  " +
                             tvecto + " - " +  "          " + "] - ";
                aLinhas = WordWrap.wrap(inq, 237, getFontMetrics(new java.awt.Font("SansSerif",Font.NORMAL,8))).split("\n");
                for (int k=0;k<aLinhas.length;k++) { sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {aLinhas[k],"0;;black","",""}); }
                
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Aluguel","0;;black",talug + " ",""});
                if (!tdesc.trim().equalsIgnoreCase("0,00")) {
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Desc. Aluguel","0;;black","",tdesc + " "});                
                }
                if (!tdife.trim().equalsIgnoreCase("0,00")) {
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Dif. Aluguel","0;;black",tdife + " ",""});                
                }
                if (!todes.trim().equalsIgnoreCase("0,00")) {
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Desc. Outros","0;;black","",todes + " "});                
                }
                if (!todif.trim().equalsIgnoreCase("0,00")) {
                    sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Dif. Outros","0;;black",todif + " ",""});                
                }
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {VariaveisGlobais.dCliente.get("CM"),"0;;black","",tcomi + " "});                
                
                // Adiantamento Avisos 16/10/2014 10h15m
                String adSql = "SELECT texto, valor, tipo FROM ADAVISOS WHERE contrato = '" + tcontrato + "' AND vencimento = '" + Dates.StringtoString(tvecto,"dd-MM-yyyy","yyyy-MM-dd") + "' AND ad_aut = 0 AND et_aut = 0;";
                ResultSet adAviso = conn.AbrirTabela(adSql, ResultSet.CONCUR_READ_ONLY);
                try {
                    while (adAviso.next()) {
                        aLinhas = WordWrap.wrap(adAviso.getString("texto"), 237, getFontMetrics(new java.awt.Font("SansSerif",Font.NORMAL,8))).split("\n");
                        if (adAviso.getString("tipo").equalsIgnoreCase("C")) {
                            for (int k=0;k<aLinhas.length;k++) { sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {aLinhas[k],"0;;black",(k == aLinhas.length - 1 ? LerValor.floatToCurrency(adAviso.getFloat("valor"), 2) : ""),""}); }
                        } else {
                            for (int k=0;k<aLinhas.length;k++) { sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {aLinhas[k],"0;;black","",(k == aLinhas.length - 1 ? LerValor.floatToCurrency(adAviso.getFloat("valor"), 2) : "")}); }
                        }
                    }
                } catch (Exception e) {}
                DbMain.FecharTabela(adAviso);
                
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Liquido","0;;black",tvalor + " ",""});
                sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"","0;;black","",""});
            }
        }
        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Total do(s) Adiantamento(s)","0;b;black",ValorRec + " ",""});
        
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
          if (aTrancicao.length > 0) {
            bean1.setautentica(VariaveisGlobais.dCliente.get("marca").trim() + "AD" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario);
          }
        } else bean1.setautentica("");

        lista.add(bean1);

        // 25-06-2013 - By wellspinto@gmail.com
        JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);

        new jDirectory("reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String pathName = "reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

        String FileNamePdf = pathName + jCodigo.getSelectedItem().toString().trim() + " - " + jNomeProp.getSelectedItem().toString().trim() + "_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + ".pdf"; //Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + ".pdf";

        try {
            Map parametros = new HashMap();
            parametros.put("parameter1", VariaveisGlobais.ExtratoTotal);

            String fileName = (nAut == -1 ? "reports/rAdiantamento2.jasper" : "reports/rAdiantamento.jasper");
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
//                jView.removeAll();
//                visor = new JRViewer(print);
//                visor.setBackground(Color.WHITE);
//                visor.setOpaque(true);
//                visor.setVisible(true);
//                visor.setBounds(0, 0, jView.getWidth(), jView.getHeight());
//                //visor.setFitWidthZoomRatio();
//                visor.setFitPageZoomRatio();
//                jView.add(visor);
//                RefreshVisor();
            } else {
                new toPrint(FileNamePdf, VariaveisGlobais.AdiantAviso.split(",")[0], VariaveisGlobais.AdiantAviso.split(",")[1], VariaveisGlobais.AdiantAviso.split(",")[2]);
//                if (VariaveisGlobais.extPrintCmd.trim().equals("JasperPrintManager")) {
//                    JasperPrintManager.printPage(print, 0, false);
//                } else {
//                    //- Colocado no dia 07/07/2014
//                    String docPrint = backlashReplace(FileNamePdf);
//                    ComandoExterno ce = new ComandoExterno();
//                    ce.ComandoExterno(VariaveisGlobais.extPrintCmd + " \"" + docPrint + "\"");
//                }
            }
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private String PrintAd() {
        String corpo = "";
        for (int i=0;i<jRecibos.getRowCount();i++) {
            String tag = jRecibos.getModel().getValueAt(i, 0).toString().trim();
            if (tag.equalsIgnoreCase("X")) {
                String tcontrato = jRecibos.getModel().getValueAt(i, 2).toString();
                String trgimv = jRecibos.getModel().getValueAt(i, 1).toString();
                String tnome  = jRecibos.getModel().getValueAt(i, 3).toString();
                String tvecto  = jRecibos.getModel().getValueAt(i, 4).toString();
                String tvalor  = jRecibos.getModel().getValueAt(i, 5).toString();
                String tender  = jRecibos.getModel().getValueAt(i, 6).toString();
                String talug  = jRecibos.getModel().getValueAt(i, 7).toString();
                String tdesc  = jRecibos.getModel().getValueAt(i, 8).toString();
                String tdife  = jRecibos.getModel().getValueAt(i, 9).toString();
                String tcomi  = jRecibos.getModel().getValueAt(i, 10).toString();
                String todes  = jRecibos.getModel().getValueAt(i, 11).toString();
                String todif = jRecibos.getModel().getValueAt(i, 12).toString();
                
                corpo += tcontrato + " " + tnome + "\n" +
                         trgimv + " " + tender + "\n" +
                        "Vecto: " + tvecto + "\n" +
                        new Pad("Aluguel",15).RPad() + "R$ " + new Pad(talug,12).LPad() + "(+)\n" +
                        (!tdesc.trim().equalsIgnoreCase("0,00") ? new Pad("Desc. Aluguel",15).RPad() + "R$ " + new Pad(tdesc,12).LPad() + "(-)\n" : "") +
                        (!tdife.trim().equalsIgnoreCase("0,00") ? new Pad("Dif. Aluguel",15).RPad() + "R$ " + new Pad(tdife).LPad() + "(+)\n" : "") +

                        (!todes.trim().equalsIgnoreCase("0,00") ? new Pad("Desc. Outros",15).RPad() + "R$ " + new Pad(todes,12).LPad() + "(-)\n" : "") +
                        (!todif.trim().equalsIgnoreCase("0,00") ? new Pad("Dif. Outros",15).RPad() + "R$ " + new Pad(todif).LPad() + "(+)\n" : "") +
                        
                        new Pad(VariaveisGlobais.dCliente.get("CM"),15).RPad() + "R$ " + new Pad(tcomi,12).LPad() + "(-)\n" +
                        new Pad("Liquido",15).RPad() + "R$ " + new Pad(tvalor,12).LPad() + "(=)\n\n";
            }
        }
        return corpo;
    }
    
    private void SaveAd(String aut) {
        for (int i=0;i<jRecibos.getRowCount();i++) {
            String tag = jRecibos.getModel().getValueAt(i, 0).toString().trim();
            if (tag.equalsIgnoreCase("X")) {
                String tcontrato = jRecibos.getModel().getValueAt(i, 2).toString();
                String trgimv = jRecibos.getModel().getValueAt(i, 1).toString();
                String tnome  = jRecibos.getModel().getValueAt(i, 3).toString();
                String tvecto  = jRecibos.getModel().getValueAt(i, 4).toString();
                String tvalor  = jRecibos.getModel().getValueAt(i, 5).toString();
                String tender  = jRecibos.getModel().getValueAt(i, 6).toString();
                String talug  = jRecibos.getModel().getValueAt(i, 7).toString();
                String tdesc  = jRecibos.getModel().getValueAt(i, 8).toString();
                String tdife  = jRecibos.getModel().getValueAt(i, 9).toString();
                String tcomi  = jRecibos.getModel().getValueAt(i, 10).toString();
                
                String oldCampo = null;
                try {oldCampo = conn.LerCamposTabela(new String[] {"campo"}, "RECIBO", "rgprp = '" + jCodigo.getSelectedItem().toString().trim() + 
                       "' and rgimv = '" + trgimv + "' and contrato = '" + tcontrato + "' and tag <> 'X'" +
                       " and dtvencimento = '" + Dates.StringtoString(tvecto, "dd-MM-yyyy", "yyyy-MM-dd") + "'")[0][3];} catch (Exception e) {}
                if (oldCampo != null) {
                    String aoldCampo[] = oldCampo.split(";");
                    aoldCampo[0] = aoldCampo[0] + ":AD" + aut + "@" + FuncoesGlobais.GravaValor(tvalor);
                    oldCampo = FuncoesGlobais.join(aoldCampo, ";");
                }
                
                //String sql = "UPDATE RECIBO SET campo = CONCAT(campo, ':AD" + aut + "@" + 
                //       FuncoesGlobais.GravaValor(tvalor) + "') " + 
                //       "WHERE rgprp = '" + jCodigo.getSelectedItem().toString().trim() + 
                //       "' and rgimv = '" + trgimv + "' and contrato = '" + tcontrato + "' and tag <> 'X'" +
                //       " and dtvencimento = '" + Dates.StringtoString(tvecto, "dd-MM-yyyy", "yyyy-MM-dd") + "'";
                String sql = "UPDATE RECIBO SET campo = '" + oldCampo + "' " + 
                       "WHERE rgprp = '" + jCodigo.getSelectedItem().toString().trim() + 
                       "' and rgimv = '" + trgimv + "' and contrato = '" + tcontrato + "' and tag <> 'X'" +
                       " and dtvencimento = '" + Dates.StringtoString(tvecto, "dd-MM-yyyy", "yyyy-MM-dd") + "'";
                try {
                    conn.ExecutarComando(sql);
                } catch (Exception e) {}
                
            }
        }
    }

    public String Imprimir(String corpo) throws FileNotFoundException, IOException, SQLException {
        double nAut = 0;
        float vias = 2;

        String[][] aTrancicao = tRec.Transicao("AD");
        if (aTrancicao.length <= 0 ) return null;

        // Autenticacao
        nAut = Autenticacao.getAut();
        if (!Autenticacao.setAut(nAut, 1)) {
            JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        //nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));
        //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

        String idNome = ""; String idAviso = ""; String idConta = ""; String rgprp = jCodigo.getSelectedItem().toString();
        idAviso = "ADIANTAMENTO - ";
        idNome = jCodigo.getSelectedItem().toString() + " - " + jNomeProp.getSelectedItem().toString().trim();
        
        for (int a=1; a<=vias; a++) {
            ImprimeAdiantamentoPDF((float)nAut, aTrancicao, corpo, jValor.getText(), idNome, idAviso, "F");
        }

        // Adiantamento Avisos upgrade
        upgAdAviso((float)nAut);
        
        // grava no caixa
        conn.LancarCaixa(new String[] {rgprp, "", ""}, aTrancicao,LerValor.FloatToString((int)nAut).replace(",0", ""));

        // grava no auxiliar
        String tmpTexto = "INSERT INTO auxiliar (conta, rgprp, campo, dtvencimento, dtrecebimento, rc_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.');";
        tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"ADI", rgprp, "AD:" +
                FuncoesGlobais.GravaValor(jValor.getText()),
                Dates.DateFormata("yyyy/MM/dd", new Date()), Dates.DateFormata("yyyy/MM/dd", new Date()),
                FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
        conn.ExecutarComando(tmpTexto);

        
        tRec.LimpaTransicao();
        jResto.setValue(0);
        tRec.vrAREC = 0;
        jValor.setValue(0);
        tRec.Enable(false);
        tRec.btEnabled(false);
        btnLancar.setEnabled(false);
        btnCancelar.setEnabled(false);
        
        return FuncoesGlobais.StrZero(String.valueOf(nAut),6);
    }
    
    private void TotalAdianta() {
        float tad = 0;
        for (int i=0; i<jRecibos.getRowCount(); i++) {
            if (jRecibos.getModel().getValueAt(i, 0).toString().equalsIgnoreCase("X")) {
                tad += LerValor.StringToFloat(jRecibos.getModel().getValueAt(i, 5).toString());
            }
        }
        
        if (tad <= 0) {
            tRec.LimpaTransicao();
            jResto.setValue(0);
            tRec.vrAREC = LerValor.StringToFloat(jResto.getText());
            tRec.btEnabled(false);
            btnLancar.setEnabled(false);
            btnCancelar.setEnabled(false);
        } else {
            float iValor = tad;
            jResto.setValue(iValor);
            tRec.vrAREC = LerValor.StringToFloat(jResto.getText());

            tRec.rgimv = ""; tRec.rgprp = ""; tRec.contrato = ""; tRec.acao = "AD"; tRec.operacao = "DEB";
            jValor.setEnabled(true);
            tRec.btEnabled(true);

            jValor.setEnabled(false);

            btnCancelar.setEnabled(true);
        }        
        
        jValor.setText(LerValor.floatToCurrency(tad, 2));
    }
    
    /**
     * Creates new form jAdiantamento
     */
    public jAdiantAviso() {
        initComponents();
        
        InitjReceber();

        FillCombos();
        AutoCompletion.enable(jCodigo);
        AutoCompletion.enable(jNomeProp);
        
        ComboBoxEditor edit = jNomeProp.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                FillBloqueados(jRecibos, jCodigo.getSelectedItem().toString());
            }
            
            public void focusGained(java.awt.event.FocusEvent evt) {
                TableControl.Clear(jAdAv);
                adcAvAd.setEnabled(false);
                delAvAd.setEnabled(false);
            }
        });        
        
        TotalAdianta();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jCodigo = new javax.swing.JComboBox();
        jNomeProp = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jRecibos = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jpRecebe = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jValor = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        adcAvAd = new javax.swing.JButton();
        delAvAd = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jAdAv = new javax.swing.JTable();
        jbtPrintDem = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Adiantamento de Alugueres ::.");
        setVisible(true);

        jLabel1.setText("Proprietário:");

        jCodigo.setEditable(true);
        jCodigo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCodigoActionPerformed(evt);
            }
        });

        jNomeProp.setEditable(true);
        jNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomePropActionPerformed(evt);
            }
        });

        jRecibos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jRecibos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jRecibos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRecibosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jRecibos);

        jLabel2.setBackground(java.awt.Color.orange);
        jLabel2.setFont(new java.awt.Font("Ubuntu", 3, 15)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("I M Ó V E I S");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
        jLabel2.setOpaque(true);

        jpRecebe.setPreferredSize(new java.awt.Dimension(314, 313));

        javax.swing.GroupLayout jpRecebeLayout = new javax.swing.GroupLayout(jpRecebe);
        jpRecebe.setLayout(jpRecebeLayout);
        jpRecebeLayout.setHorizontalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 469, Short.MAX_VALUE)
        );
        jpRecebeLayout.setVerticalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
        );

        jLabel4.setText("Valor a Pagar:");

        jValor.setEditable(false);
        jValor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jValor.setText("0,00");
        jValor.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        adcAvAd.setText("Lançar");
        adcAvAd.setEnabled(false);
        adcAvAd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adcAvAdActionPerformed(evt);
            }
        });

        delAvAd.setText("Excluir");
        delAvAd.setEnabled(false);
        delAvAd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delAvAdActionPerformed(evt);
            }
        });

        jAdAv.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jAdAv.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(jAdAv);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(adcAvAd, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delAvAd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(adcAvAd)
                    .addComponent(delAvAd))
                .addContainerGap())
        );

        jbtPrintDem.setText("Imprimir Demonstrativo");
        jbtPrintDem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtPrintDemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtPrintDem, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                            .addContainerGap()))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtPrintDem)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private float[] CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto) {
        String rcampo = "";

        float[] aComissao = null;
        try {aComissao = new Calculos().percComissao(rgprp, rgimv);} catch (Exception e) {}
        float fComissao = aComissao[0]; float rComissao = aComissao[1];
        
        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String campo = "";
        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");
                rcampo = campo;
            }
        } catch (SQLException ex) {
            rcampo = "";
            ex.printStackTrace();
        }
        DbMain.FecharTabela(pResult);

        float tRecibo = 0;
        float[] ttRecibo = Calculos.RetAluguel(campo);
        tRecibo = ttRecibo[0] - ttRecibo[1] + ttRecibo[2];
        
        float valug = ttRecibo[0];
        float vdesc = ttRecibo[1];
        float vdife = ttRecibo[2];
        float vdesm = ttRecibo[3];
        float vdifm = ttRecibo[4];
        
        float vcomi = 0;
        if (fComissao != 0 && rComissao == 0) {
            vcomi = tRecibo * (fComissao / 100);
        } else if (fComissao == 0 && rComissao != 0) {
            vcomi = rComissao;
        } else {
            vcomi = tRecibo * (fComissao / 100);
        }
        
        return new float[] {valug,vdesc,vdife,vcomi,vdesm,vdifm};
    }
    
    private void FillCombos() {
        String sSql = "SELECT distinct p.rgprp, p.nome FROM proprietarios p WHERE Upper(p.status) = 'ATIVO' ORDER BY Lower(Trim(p.nome));";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (imResult.next()) {
                jCodigo.addItem(String.valueOf(imResult.getInt("rgprp")));
                jNomeProp.addItem(imResult.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }
        
    private void FillBloqueados(JTable table, String tProp) {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"tag","rgimv","contrato","inquilino","vencimento","valor","endereco","aluguel","desconto","diferenca","comissao","odesc","odif","adav"},{"30","60","60","250","130","100","0","0","0","0","0","0","0","0"}});

        String sSql = "SELECT contrato, rgprp, rgimv, campo, dtvencimento FROM RECIBO WHERE rgprp = '&1.' AND tag <> 'X' AND NOT InStr(campo, 'AD') ORDER BY rgimv;";
        sSql = FuncoesGlobais.Subst(sSql, new String[] {jCodigo.getSelectedItem().toString()});
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        float fTotCred[] = null; 
        String inq = "";
        try {
            while (imResult.next()) {
                String ttag = "";
                String tmpCampo = imResult.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);
                fTotCred = null;
                for (int j = 0; j<rCampos.length; j++) {
                    boolean bRetc = FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1;
                    if ("AL".equals(rCampos[j][4])) {
                        if (LerValor.isNumeric(rCampos[j][0])) {
                            inq = new Pad(conn.LerCamposTabela(new String[] {"nomerazao"}, "locatarios", "contrato = '" + imResult.getString("contrato") + "'")[0][3],18).RPad();
                        }
                    } 
                }
                
                String trgprp = String.valueOf(imResult.getInt("rgprp"));
                String trgimv = String.valueOf(imResult.getInt("rgimv"));
                String tcontrato = String.valueOf(imResult.getInt("contrato"));
                String tinq = inq;
                String tvecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtvencimento"));
                
                String[][] aend = conn.LerCamposTabela(new String[] {"end", "num", "compl"}, "imoveis", "rgimv = '" + trgimv + "'");
                String tender = "";
                if (aend != null) {
                    tender = aend[0][3].trim() + ", " + aend[1][3].trim() + " " + aend[2][3].trim();
                }
                
                float tadAviso = TotAdAviso(tcontrato, imResult.getDate("dtvencimento"));
                fTotCred = CalcularRecibo(trgprp, trgimv, tcontrato, tvecto);
                float vrreceber = fTotCred[0] - fTotCred[1] + fTotCred[2] - fTotCred[3] - fTotCred[4] + fTotCred[5] + tadAviso;
                String tvalor = LerValor.floatToCurrency(vrreceber,2);

                String talug = LerValor.floatToCurrency(fTotCred[0],2);
                String tdesc = LerValor.floatToCurrency(fTotCred[1],2);
                String tdife = LerValor.floatToCurrency(fTotCred[2],2);
                String tcomi = LerValor.floatToCurrency(fTotCred[3],2);
                String todes = LerValor.floatToCurrency(fTotCred[4],2);
                String todif = LerValor.floatToCurrency(fTotCred[5],2);
                String tadav = LerValor.floatToCurrency(tadAviso,2);
                
                if (conn.LerCamposTabela(new String[] {"rgprp"}, "divisao", "rgprp = '" + trgprp + "' AND rgimv = '" + trgimv + "'") == null) {
                    TableControl.add(table, new String[][]{{ttag, trgimv, tcontrato, tinq, tvecto, tvalor, tender,talug,tdesc,tdife,tcomi,todes,todif,tadav},
                        {"C","C","C","L","C","R","L","R","R","R","R","R","R","R"}}, true);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
        
    }
    
    private float TotAdAviso(String contrato, Date dtVencto) {
        float adAvisovr = 0f;
        // Aviso de Adiantamento - 14/10/2014 12h51m
        String adSql = "SELECT valor, tipo FROM ADAVISOS WHERE contrato = '" + contrato + "' AND vencimento = '" + Dates.DateFormata("yyyy-MM-dd", dtVencto) + "' AND ad_aut = 0 AND et_aut = 0;";
        ResultSet adAviso = conn.AbrirTabela(adSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (adAviso.next()) {
                if (adAviso.getString("tipo").equalsIgnoreCase("C")) {
                    adAvisovr += adAviso.getFloat("valor");
                } else adAvisovr -= adAviso.getFloat("valor");
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(adAviso);
        // - Fim Aviso Adiantamento
        return adAvisovr;
    }
    
    private void jCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCodigoActionPerformed
        int pos = jCodigo.getSelectedIndex();
        if (jNomeProp.getItemCount() > 0) {
            jNomeProp.setSelectedIndex(pos);
        }
    }//GEN-LAST:event_jCodigoActionPerformed

    private void jNomePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomePropActionPerformed
        int pos = jNomeProp.getSelectedIndex();
        if (jCodigo.getItemCount() > 0) {
            jCodigo.setSelectedIndex(pos);
        }
    }//GEN-LAST:event_jNomePropActionPerformed

    private void jRecibosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRecibosMouseClicked
        int col = jRecibos.getSelectedColumn();
        int row = jRecibos.getSelectedRow();
        if (row < 0) return;
        
        if (col == 0) {
            String sTag = ("X".equals(jRecibos.getModel().getValueAt(row, 0).toString()) ? "" : "X");
            jRecibos.getModel().setValueAt(sTag, row, 0);
            
            TotalAdianta();
        }
        upgAvAd(row);
        
        adcAvAd.setEnabled(true);
        delAvAd.setEnabled(true);
    }//GEN-LAST:event_jRecibosMouseClicked

    private void upgAvAd(int row) {
        // Lista jAdAv 
        String tcontrato = jRecibos.getModel().getValueAt(row, 2).toString();
        String tvencto = jRecibos.getModel().getValueAt(row, 4).toString();
        
        TableControl.header(jAdAv, new String[][] {{"id","Hist","Valor","T"},{"0","100","80","10"}});
        String tSql = "SELECT id, texto, valor, tipo FROM ADAVISOS WHERE contrato = '" + tcontrato + "' AND vencimento = '" + Dates.StringtoString(tvencto,"dd-MM-yyyy","yyyy-MM-dd") + "' AND ad_aut = 0 AND et_aut = 0;";;
        ResultSet trs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (trs.next()) {
                String tid = trs.getString("id");
                String thist = trs.getString("texto");
                String tvalor = LerValor.floatToCurrency(trs.getFloat("valor"),2);
                String ttipo = trs.getString("tipo");
                
                TableControl.add(jAdAv, new String[][]{{tid, thist, tvalor, ttipo},{"","L","R","C"}}, true);
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(trs);        
    }
    
    private void adcAvAdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adcAvAdActionPerformed
        jLancaAdAviso oTela = new jLancaAdAviso(null, true);
        oTela.setVisible(true);
        Object[] dados = oTela.Dados();
        if (dados != null) {
            int row = jRecibos.getSelectedRow();
            String trgprp = jCodigo.getSelectedItem().toString();
            String trgimv = jRecibos.getModel().getValueAt(row, 1).toString();
            String tcontrato = jRecibos.getModel().getValueAt(row, 2).toString();
            String tvecto = jRecibos.getModel().getValueAt(row, 4).toString();
            
            String Sql = "INSERT INTO ADAVISOS(rgprp, rgimv, contrato, texto, valor, tipo, vencimento) VALUES ('" + 
                    trgprp + "','" + trgimv + "','" + tcontrato + "','" + dados[0] + "'," + 
                    LerValor.StringToFloat((String) dados[1]) + ",'" + dados[2] + "','" + Dates.StringtoString(tvecto, "dd-MM-yyyy", "yyyy-MM-dd") + "');";
            try { conn.ExecutarComando(Sql); } catch (Exception e) {e.printStackTrace(); }
            upgAvAd(row);

            float tvalor = LerValor.StringToFloat((String) dados[1]);
            
            // Acerta os valores
            float rvalor = LerValor.StringToFloat(jRecibos.getModel().getValueAt(row, 5).toString());
            float tadav =  LerValor.StringToFloat(jRecibos.getModel().getValueAt(row, 13).toString());
            if (!dados[2].toString().equalsIgnoreCase("D")) {
                rvalor += tvalor;
                tadav += tvalor;
            } else {
                rvalor -= tvalor;
                tadav -= tvalor;
            }
            jRecibos.getModel().setValueAt(LerValor.floatToCurrency(rvalor, 2), row, 5);
            jRecibos.getModel().setValueAt(LerValor.floatToCurrency(tadav, 2), row, 13);
        }
        oTela = null;
    }//GEN-LAST:event_adcAvAdActionPerformed

    private void delAvAdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delAvAdActionPerformed
        if (jAdAv.getRowCount() < 0) return;
        
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(null,
            "Deseja excluir este lançamento ? ",
            "Atenção", JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) {
            int row = jAdAv.getSelectedRow();
            if (row < 0) return;
            String tid = jAdAv.getModel().getValueAt(row, 0).toString();
            float tvalor = LerValor.StringToFloat(jAdAv.getModel().getValueAt(row, 2).toString());
            String ttipo = jAdAv.getModel().getValueAt(row, 3).toString();
            
            String dSql = "DELETE FROM ADAVISOS WHERE id = '" + tid + "';";
            try {
                conn.ExecutarComando(dSql);
            } catch (Exception e) {return;}
            
            TableControl.del(jAdAv, row);
            
            // Acerta os valores
            float rvalor = LerValor.StringToFloat(jRecibos.getModel().getValueAt(row, 5).toString());
            float tadav =  LerValor.StringToFloat(jRecibos.getModel().getValueAt(row, 13).toString());
            if (ttipo.equalsIgnoreCase("D")) {
                rvalor += tvalor;
                tadav += tvalor;
            } else {
                rvalor -= tvalor;
                tadav -= tvalor;
            }
            jRecibos.getModel().setValueAt(LerValor.floatToCurrency(rvalor, 2), row, 5);
            jRecibos.getModel().setValueAt(LerValor.floatToCurrency(tadav, 2), row, 13);
        }
    }//GEN-LAST:event_delAvAdActionPerformed

    private void jbtPrintDemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtPrintDemActionPerformed
        int row = jRecibos.getSelectedRow();
        if (row < 0) return;
        
        String corpo = PrintAd();
        String idNome = ""; String idAviso = ""; String idConta = ""; String rgprp = jCodigo.getSelectedItem().toString();
        idAviso = "ADIANTAMENTO - ";
        idNome = jCodigo.getSelectedItem().toString() + " - " + jNomeProp.getSelectedItem().toString().trim();
        
        try {ImprimeAdiantamentoPDF(-1, new String[][] {}, corpo, jValor.getText(), idNome, idAviso, "F");} catch (Exception e) {e.printStackTrace();}
    }//GEN-LAST:event_jbtPrintDemActionPerformed

    // Adiantamento Avisos 16/10/2014 10h15m
    public void upgAdAviso(float aut) {
        for (int i=0;i<jRecibos.getRowCount();i++) {
            String tag = jRecibos.getModel().getValueAt(i, 0).toString().trim();
            if (tag.equalsIgnoreCase("X")) {
                String tcontrato = jRecibos.getModel().getValueAt(i, 2).toString();
                String tvecto  = jRecibos.getModel().getValueAt(i, 4).toString();
                String adSql = "UPDATE ADAVISOS SET data = '" + Dates.DateFormata("yyyy-MM-dd", new Date()) + "', logado = '" + VariaveisGlobais.usuario + "', ad_aut = '" + aut + "' WHERE contrato = '" + tcontrato + "' AND vencimento = '" + Dates.StringtoString(tvecto,"dd-MM-yyyy","yyyy-MM-dd") + "' AND ad_aut = 0 AND et_aut = 0;";
                try { conn.ExecutarComando(adSql); } catch (Exception e) {}
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton adcAvAd;
    private javax.swing.JButton delAvAd;
    private javax.swing.JTable jAdAv;
    private javax.swing.JComboBox jCodigo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable jRecibos;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JFormattedTextField jValor;
    private javax.swing.JButton jbtPrintDem;
    private javax.swing.JPanel jpRecebe;
    // End of variables declaration//GEN-END:variables
}
