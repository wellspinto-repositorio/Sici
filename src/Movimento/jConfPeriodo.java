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

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.toPreview;
import conferencia.Periodo;
import Sici.Partida.Collections;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.swing.JRViewer;
import org.apache.commons.lang.NumberUtils;

/**
 *
 * @author supervisor
 */
public class jConfPeriodo extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    JRViewer visor;

    /** Creates new form jExtrato */
    public jConfPeriodo() throws JRException {
        initComponents();
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);
    }

    public static boolean RetPar(String campo, String oque) {
        return (campo.contains(oque));
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
    
    private float RetSGVarPar(String campos, String oque) {
        String mVrPar = "0000000000";
        int mIndex = campos.indexOf(oque,0);
        String mCpo = "";

        if (mIndex > -1) {
            mCpo = campos.substring(mIndex + 5, mIndex + 5 + 10);

            if (NumberUtils.isDigits(mCpo)) {
                mVrPar = mCpo;
            }
        }

        return LerValor.StringToFloat(LerValor.FormatNumber(mVrPar, 2));
    }

    private void Imprimir(String diai, String diaf) {
        Collections gVar = VariaveisGlobais.dCliente;
        List<Periodo> lista = new ArrayList<Periodo>();
        String[][] sCampos = {};
        
        String sSql = "SELECT rgprp, rgimv, contrato, campo, dtvencimento, dtrecebimento, rc_aut FROM auxiliar WHERE dtrecebimento >= '&1.' AND dtrecebimento <= '&2.' AND CONTA = 'REC' ORDER BY dtrecebimento,contrato;";
        sSql = FuncoesGlobais.Subst(sSql, new String[] {Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(diai, "dd/MM/yyyy")), Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(diaf, "dd/MM/yyyy"))});

        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        float tfalug = 0, tfmul = 0, tfjur = 0, tfcor = 0, tfexp = 0, tfseg = 0, tfcom = 0;
        
        try {
            while (imResult.next()) {
                Periodo bean1 = new Periodo();
        
                String campo = imResult.getString("campo");
                
                String trgprp = String.valueOf(imResult.getInt("rgprp"));
                String trgimv = String.valueOf(imResult.getInt("rgimv"));
                String tcontrato = String.valueOf(imResult.getInt("contrato"));
                String tinq = conn.LerCamposTabela(new String[] {"nomerazao"}, "locatarios", "contrato = '" + tcontrato + "'")[0][3];
                String tvecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtvencimento"));
                String tvecto2 = Dates.DateFormata("yyyy-MM-dd", imResult.getDate("dtvencimento"));
                String trecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtrecebimento"));
                
                float vrrecibo = PegaAluguel(campo);
                String tvalor = LerValor.floatToCurrency(vrrecibo, 2);

                float fmul = 0, fjur = 0, fcor = 0, fexp = 0, fseg = 0, fcom = 0;
                if (!"".equals(campo.trim())) {
                    // Multa
                    boolean bMulta = RetPar(campo, "MU");
                    if (bMulta) { fmul = RetVarPar(campo, "MU"); }

                    // Juros
                    boolean bJuros = RetPar(campo, "JU");
                    if (bJuros) { fjur = RetVarPar(campo, "JU"); }

                    // Correcao
                    boolean bCorrecao = RetPar(campo, "CO");
                    if (bCorrecao) { fcor = RetVarPar(campo, "CO"); }

                    // Expediente
                    boolean bExp = RetPar(campo, "EP");
                    if (bExp) { fexp = RetVarPar(campo, "EP"); }

                    // Seguro
                    boolean bSeg = RetPar(campo, "SG");
                    if (bSeg) { 
                        fseg = RetSGVarPar(campo, "SG"); 
                    }

                    // Comissao
                    boolean bCom = RetPar(campo, "CM");
                    if (bCom) { fcom = RetVarPar(campo, "CM"); }
                }
                
                //
                try {
                    campo = conn.LerCamposTabela(new String[] {"campo"}, "auxiliar", "Upper(conta) LIKE 'ADM%' AND rgprp = '" + trgprp + "' AND contrato = '" + tcontrato + "' AND rc_aut = '" + imResult.getString("rc_aut") + "' AND dtvencimento = '" + tvecto2 + "'")[0][3];
                } catch (Exception ex) {campo = "";}
                
                if (!"".equals(campo.trim())) {
                    // Multa
                    boolean bMulta = RetPar(campo, "MU");
                    if (bMulta) { fmul = RetVarPar(campo, "MU"); }

                    // Juros
                    boolean bJuros = RetPar(campo, "JU");
                    if (bJuros) { fjur = RetVarPar(campo, "JU"); }

                    // Correcao
                    boolean bCorrecao = RetPar(campo, "CO");
                    if (bCorrecao) { fcor = RetVarPar(campo, "CO"); }

                    // Expediente
                    boolean bExp = RetPar(campo, "EP");
                    if (bExp) { fexp = RetVarPar(campo, "EP"); }
                    
                    // Seguro
                    boolean bSeg = RetPar(campo, "SG");
                    if (bSeg) { 
                        fseg = RetSGVarPar(campo, "SG"); 
                    }

                    // Comissao
                    boolean bCom = RetPar(campo, "CM");
                    if (bCom) { fcom = RetVarPar(campo, "CM"); }
                }
                
                String texp = LerValor.floatToCurrency(fexp, 2);
                String tmul = LerValor.floatToCurrency(fmul, 2);
                String tjur = LerValor.floatToCurrency(fjur, 2);
                String tcor = LerValor.floatToCurrency(fcor, 2);
                String tseg = LerValor.floatToCurrency(fseg, 2);
                
                float perc = percComissao(trgprp, trgimv);
                String tcop = String.valueOf(perc);
                String tcom = LerValor.floatToCurrency(fcom, 2);
                        //LerValor.floatToCurrency(vrrecibo * (perc / 100), 2);
                
                bean1.setdata(trecto);
                bean1.setprop(trgprp);
                bean1.setcontrato(tcontrato);
                bean1.setinquilino(tinq);
                bean1.setVecto(tvecto);
                bean1.setcomissaopct(tcop);
                bean1.setcomissao(tcom);
                bean1.setaluguel(tvalor);
                bean1.setexpediente(texp);
                bean1.setmulta(tmul);
                bean1.setjuros(tjur);
                bean1.setcorrecao(tcor);
                bean1.setseguro(tseg);
                
                lista.add(bean1);
                
                tfalug += vrrecibo; tfexp += fexp; tfmul += fmul; tfjur += fjur; tfcor += fcor; tfseg += fseg; tfcom += fcom;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
                
        String talu = LerValor.floatToCurrency(tfalug, 2);
        String tcom = LerValor.floatToCurrency(tfcom, 2);
        String texp = LerValor.floatToCurrency(tfexp, 2);
        String tmul = LerValor.floatToCurrency(tfmul, 2);
        String tjur = LerValor.floatToCurrency(tfjur, 2);
        String tcor = LerValor.floatToCurrency(tfcor, 2);
        String tseg = LerValor.floatToCurrency(tfseg, 2);

        Periodo bean1 = new Periodo();
        bean1.setdata("");
        bean1.setprop("");
        bean1.setcontrato("");
        bean1.setinquilino("");
        bean1.setVecto("");
        bean1.setcomissaopct("");
        bean1.setcomissao("");
        bean1.setaluguel("");
        bean1.setexpediente("");
        bean1.setmulta("");
        bean1.setjuros("");
        bean1.setcorrecao("");
        bean1.setseguro("");
        lista.add(bean1);

        bean1 = new Periodo();
        bean1.setdata("");
        bean1.setprop("");
        bean1.setcontrato("");
        bean1.setinquilino("T O T A I S ===>");
        bean1.setVecto("");
        bean1.setcomissaopct("");
        bean1.setcomissao(tcom);
        bean1.setaluguel(talu);
        bean1.setexpediente(texp);
        bean1.setmulta(tmul);
        bean1.setjuros(tjur);
        bean1.setcorrecao(tcor);
        bean1.setseguro(tseg);
        lista.add(bean1);

        JRDataSource jrds = new JRBeanCollectionDataSource(lista);
        
        try {
            Map parametros = new HashMap();
            parametros.put("data", Dates.DateFormata("dd/MM/yyyy",jData.getDate()));
            parametros.put("data1", Dates.DateFormata("dd/MM/yyyy",jData1.getDate()));

            String fileName = "reports/rPeriodo.jasper";
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrds);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
            String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
            
            
            // Configure the exporter (set output file name and print object)
            String outFileName = pathName + "Periodo_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
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
                
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }        
    }
            
    private float PegaAluguel(String tmpCampo) throws SQLException {        
        String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);
        float fTotCred = 0; 
        for (int j = 0; j<rCampos.length; j++) {
            boolean bRetc = FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1;
            if ("AL".equals(rCampos[j][4])) {
                if (LerValor.isNumeric(rCampos[j][0])) {
                    fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                    if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                } else {
                    if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                }
            } 
        }
        return fTotCred;
    }
    
    public float percComissao(String rgprp, String rgimv) throws SQLException {
        String[][] regFields;
        
        float fComissao = Float.valueOf(LerValor.FormatNumber(conn.LerParametros("comissao"), 3).replace(",", "."));
        
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
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jGerar = new javax.swing.JButton();
        jData = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel2 = new javax.swing.JLabel();
        jData1 = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Relatório por Período de Conferências ::.");
        setMaximumSize(new java.awt.Dimension(448, 81));
        setMinimumSize(new java.awt.Dimension(448, 81));
        setPreferredSize(new java.awt.Dimension(448, 81));
        setVisible(true);

        jLabel1.setText("Data:");

        jGerar.setText("Gerar");
        jGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGerarActionPerformed(evt);
            }
        });

        jLabel2.setText("Até");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jData, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jData1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jGerar)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jData1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jData, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGerarActionPerformed
        Imprimir(Dates.DateFormata("dd/MM/yyyy",jData.getDate()), Dates.DateFormata("dd/MM/yyyy",jData1.getDate()));
    }//GEN-LAST:event_jGerarActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser jData;
    private com.toedter.calendar.JDateChooser jData1;
    private javax.swing.JButton jGerar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables

}
