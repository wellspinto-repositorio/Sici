/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jRelSdProp.java
 *
 * Created on 20/12/2011, 14:11:18
 */
package Movimento;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.toPreview;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import saldo.SaldoProp;

/**
 *
 * @author supervisor
 */
public class jRelSdProp extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String rgprp = ""; String rgimv = ""; String contrato = "";

    /** Creates new form jRelSdProp */
    public jRelSdProp() {
        initComponents();
        
    }

    private void Saldos() {
        List<SaldoProp> lista = new ArrayList<SaldoProp>();        
        SaldoProp Bean1 = new SaldoProp();
        
        String sql = "SELECT * FROM proprietarios WHERE " + (jProp.isSelected() ? "NOT" : "") + " Upper(nome) LIKE 'COND%' ORDER BY Lower(nome);";
        ResultSet rst = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rst.next()) {                
                String rgprp = rst.getString("rgprp");
                String saldo =  Imprimir(rgprp);
                String nmProp = rst.getString("nome").toUpperCase();
                
                Bean1 = new SaldoProp();
                Bean1.setrgprp(rgprp);
                Bean1.setnmPro(nmProp);
                Bean1.setsaldo(LerValor.StringToFloat(saldo));
                
                lista.add(Bean1);
            }
        } catch (SQLException ex) {}
        
        JRDataSource jrds = new JRBeanCollectionDataSource(lista);

        try {
            String fileName = "reports/rSaldoProp.jasper";
            JasperPrint print = JasperFillManager.fillReport(fileName, null, jrds);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
            String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
            
            // Configure the exporter (set output file name and print object)
            String FileNamePdf = pathName + "SaldoProp" + Dates.DateFormata("dd-MM-yyyy_HH_mm", new Date()) + ".pdf";
            String outFileName = FileNamePdf;

            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();

            new toPreview(outFileName);
//            if (!"jasper".equals(VariaveisGlobais.reader)) {
//                ComandoExterno ce = new ComandoExterno();
//                ComandoExterno.ComandoExterno(VariaveisGlobais.reader + " " + outFileName);
//                //ce.ComandoExterno("lp " + FileNamePdf);
//            } else {
//                JasperViewer viewer = new JasperViewer(print, false);
//                viewer.show();
//            }
            
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }        
        
        DbMain.FecharTabela(rst);
    }
    
    private String Imprimir(String jRgprp) {
        float fTotCred = 0; float fTotDeb = 0; float fTotAdi = 0; float fSaldoAnt = 0;
        try {
            String sdant = conn.LerCamposTabela(new String[] {"saldoant"}, "proprietarios", "rgprp = '" + jRgprp + "'")[0][3];
            fSaldoAnt = Float.valueOf(sdant.trim());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (fSaldoAnt > 0) {
            fTotCred += fSaldoAnt;
        }

        String sql = "SELECT contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento FROM extrato WHERE rgprp = '&1.' AND (tag <> 'X' AND tag <> 'B') ORDER BY dtvencimento;";
               sql = FuncoesGlobais.Subst(sql, new String[] {jRgprp});

        ResultSet hrs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (hrs.next()) {
                String tmpCampo = hrs.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);

                for (int j = 0; j<rCampos.length; j++) {
                    String tpCampo = new Pad(rCampos[j][rCampos[j].length - 1], 15).RPad();
                    if (VariaveisGlobais.bShowCotaParcelaExtrato) {
                        String spart1 = "", spart2 = "", scotaparc = "";
                        try {
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
                        } catch (Exception e) {spart1 = "00"; spart2 = "0000"; }
                        scotaparc = spart1 + spart2;
                        tpCampo += "  " + ("0000".equals(scotaparc) || "000000".equals(scotaparc) || "".equals(scotaparc) ? "       " : scotaparc.substring(0,2) + "/" + scotaparc.substring(2));
                    }
                    boolean bRetc = FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1;
                    try {
                        if ("AL".equals(rCampos[j][4])) {
                            if (LerValor.isNumeric(rCampos[j][0])) {
                                fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                                if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}

                                int nPos = FuncoesGlobais.IndexOf(rCampos[j], "CM");
                                if (nPos > -1) {
                                    // "CM"
                                    fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                }

                                nPos = FuncoesGlobais.IndexOf(rCampos[j], "MU");
                                if (nPos > -1) {
                                    if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                        // "MU"
                                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                    }
                                }

                                nPos = FuncoesGlobais.IndexOf(rCampos[j], "JU");
                                if (nPos > -1) {
                                    if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                        // "JU"
                                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                    }
                                }

                                nPos = FuncoesGlobais.IndexOf(rCampos[j], "AD");
                                if (nPos > -1) {
                                    if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(9),2)) > 0) {
                                        String wAD = rCampos[j][nPos].split("@")[1];
                                        fTotAdi += LerValor.StringToFloat(LerValor.FormatNumber(wAD,2));
                                    }
                                }
                                
                                nPos = FuncoesGlobais.IndexOf(rCampos[j], "CO");
                                if (nPos > -1) {
                                    if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                        // "CO"
                                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                    }
                                }

                                nPos = FuncoesGlobais.IndexOf(rCampos[j], "EP");
                                if (nPos > -1) {
                                    if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                        // "EP"
                                        fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2));
                                    }
                                }
                            } else {
                                if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                            }
                        } else if (FuncoesGlobais.IndexOf(rCampos[j], "AD") > -1) {
                            int nPos = FuncoesGlobais.IndexOf(rCampos[j], "AD");
                            if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].split("@")[1],2)) > 0) {
                                String wAD = rCampos[j][nPos].split("@")[1];
                                fTotAdi += LerValor.StringToFloat(LerValor.FormatNumber(wAD,2));
                            }                        
                        } else if ("DC".equals(rCampos[j][4])) {
                            fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                            if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        } else if ("DF".equals(rCampos[j][4])) {
                            fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                            if (bRetc) {fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        } else if ("SG".equals(rCampos[j][4])) {
                            fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                            if (bRetc) {fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        } else {
                            fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                            if (bRetc) {fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        }
                    } catch (Exception e) {}
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(hrs);

        sql = FuncoesGlobais.Subst("SELECT campo FROM avisos WHERE registro = '&1.' AND rid = '0' AND (tag <> 'X' OR ISNULL(tag));", new String[] {jRgprp});
        hrs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (hrs.next()) {
                String tmpCampo = "" + hrs.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, false);
                if ("CRE".equals(rCampos[0][8])) {
                    fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2],2));
                } else {
                    fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2],2));
                }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(hrs);

        return LerValor.floatToCurrency(fTotCred - fTotDeb - fTotAdi, 2);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPrint = new javax.swing.JButton();
        jProp = new javax.swing.JToggleButton();
        jCond = new javax.swing.JToggleButton();
        jbtSldLoca = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setClosable(true);
        setTitle(".:: Relatório de Saldos");
        setVisible(true);

        jPrint.setText("Imprimir Relatório de Saldos Proprietarios");
        jPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPrintActionPerformed(evt);
            }
        });

        buttonGroup1.add(jProp);
        jProp.setSelected(true);
        jProp.setText("Proprietários");

        buttonGroup1.add(jCond);
        jCond.setText("Condomínios");

        jbtSldLoca.setText("Imprimir Relatório de Saldos Locatários");
        jbtSldLoca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSldLocaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPrint, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jProp, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCond, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtSldLoca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jProp)
                    .addComponent(jCond))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtSldLoca)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPrintActionPerformed
        jPrint.setEnabled(false);
        Saldos();
        jPrint.setEnabled(true);
    }//GEN-LAST:event_jPrintActionPerformed

    private void jbtSldLocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSldLocaActionPerformed
        jbtSldLoca.setEnabled(false);
        GerarPrint();
        jbtSldLoca.setEnabled(true);
    }//GEN-LAST:event_jbtSldLocaActionPerformed

    private void GerarPrint() {
        
        conn.CriarMySqlProcedures("PLUSVAL", "");
        String query = "SELECT a.contrato, l.nomerazao, Sum(plusVal(mid(a.campo,7,10),IF(instr(a.campo,'CRE'),'CRE','DEB'))) AS saldo FROM auxiliar a, locatarios l where (a.contrato = l.contrato) and a.conta = 'AVI' and mid(a.campo,4,2) = '04' group by a.contrato;";
        ResultSet rs = conn.AbrirTabela(query, ResultSet.CONCUR_READ_ONLY);

        //implementação da interface JRDataSource para DataSource ResultSet
        JRResultSetDataSource jrRS = new JRResultSetDataSource( rs );

        new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

        String outFileName = pathName + "SaldosLoca_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
        String fileName = "reports/rSaldoLoca.jasper";

        //executa o relatório
        try {
            /* Preenche o relatório com os dados. Gera o arquivo BibliotecaPessoal.jrprint    */
            JasperPrint print = JasperFillManager.fillReport(fileName, null, jrRS);

            JRExporter exporter = new JRPdfExporter();

            // Configure the exporter (set output file name and print object)
            
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();
        
            new toPreview(outFileName);
//            if (!"jasper".equals(VariaveisGlobais.reader)) {
//                ComandoExterno ce = new ComandoExterno();
//                ComandoExterno.ComandoExterno(VariaveisGlobais.reader + " " + outFileName);
//            } else {
//                JasperViewer viewer = new JasperViewer(print, false);
//                viewer.show();
//            }

        } catch (Exception ex) {}
        DbMain.FecharTabela(rs);
        
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JToggleButton jCond;
    private javax.swing.JButton jPrint;
    private javax.swing.JToggleButton jProp;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jbtSldLoca;
    // End of variables declaration//GEN-END:variables
}

