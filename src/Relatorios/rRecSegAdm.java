/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * rSeguros.java
 *
 * Created on 28/02/2012, 15:35:45
 */
package Relatorios;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.toPreview;
import Sici.Partida.Collections;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 *
 * @author supervisor
 */
public class rRecSegAdm extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;

    /** Creates new form rSeguros */
    public rRecSegAdm() {
        initComponents();

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

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
        jbtnPreview = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jDtInic = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jDtFim = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel2 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Relatório de Seguros ADM ... ::.");
        setVisible(true);

        jbtnPreview.setText("Preview");
        jbtnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPreviewActionPerformed(evt);
            }
        });

        jLabel1.setText("Data Inicial:");

        jLabel2.setText("Data Final:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDtInic, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDtFim, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jbtnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDtFim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jDtInic, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        jbtnPreview.setEnabled(false);
        String dini = Dates.DateFormata("yyyy-MM-dd", jDtInic.getDate());
        String dfim = Dates.DateFormata("yyyy-MM-dd", jDtFim.getDate());
        RelRecAtr(dini, dfim);
        jbtnPreview.setEnabled(true);
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void RelRecAtr(String dtInic, String dtFinal) {
        Collections gVar = VariaveisGlobais.dCliente;
        
        Map parametros = new HashMap();
        parametros.put("dataini", dtInic);
        parametros.put("datafim", dtFinal);
        
        try {
            String fileName = "reports/rRelSegADM.jasper";
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, conn.conn);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
            String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
            
            // Configure the exporter (set output file name and print object)
            String outFileName = pathName + "RelSegADM" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser jDtFim;
    private com.toedter.calendar.JDateChooser jDtInic;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jbtnPreview;
    // End of variables declaration//GEN-END:variables
}
// SELECT IF(INSTR(campo,'CRE'),'CRE','DEB') AS tipo, REPLACE(`DecriptaNome`(Mid(campo,INSTR(campo,'XX:')+3,InStr(Mid(campo,INSTR(campo,'XX:')+3),':')-1)),'\t',' ') as descricao, StrVal(Mid(campo,7,10)) AS valor, dtrecebimento, rc_aut FROM auxiliar WHERE conta = 'AVI' and contrato = 'SG' ORDER BY dtrecebimento;