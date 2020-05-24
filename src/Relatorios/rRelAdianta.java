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
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.toPreview;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
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
public class rRelAdianta extends javax.swing.JInternalFrame {

    DbMain conn = VariaveisGlobais.conexao;

    /**
     * Creates new form rSeguros
     */
    public rRelAdianta() {
        initComponents();

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        setTitle(".:: Relatório de Adiantamentos ... ::.");
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
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jDtInic, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        jbtnPreview.setEnabled(false);
        String dini = Dates.DateFormata("yyyy-MM-dd", jDtInic.getDate());
        String dfim = Dates.DateFormata("yyyy-MM-dd", jDtFim.getDate());
        RelAdianta(dini, dfim);
        jbtnPreview.setEnabled(true);
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void RelAdianta(String dtInic, String dtFinal) {
        FillAdTemp();
        
        Map parametros = new HashMap();
        parametros.put("parameter1", dtInic);
        parametros.put("parameter2", dtFinal);

        try {
            String fileName = "reports/rAdiantamentos.jasper";
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, conn.conn);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
            String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
            
            // Configure the exporter (set output file name and print object)
            String outFileName = pathName + "Adiantamentos_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
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
    
    private void FillAdTemp() {
        String dSql = "DROP TABLE tADIANTA;";
        try {conn.ExecutarComando(dSql);} catch (Exception e) {}
        String cSql = "CREATE TABLE tADIANTA (`rgprp` varchar(6),`prop` varchar(60),`rgimv` varchar(6),`contrato` varchar(6),`loca` varchar(60),`dtvecto` DATE,`dtrecbto` DATE,`dtadianta` DATE,`vradianta` float ) ENGINE = InnoDB;";
        try {conn.ExecutarComando(cSql);} catch (Exception e) {}
        String tSql = "SELECT a.rgprp, p.nome, a.rgimv, a.contrato, l.nomerazao, a.dtvencimento, a.autenticacao, " +
                      "a.campo FROM RECIBO a, proprietarios p, locatarios l where (a.rgprp = p.rgprp) AND " +
                      "(a.contrato = l.contrato) AND instr(a.campo,'@') ORDER BY lower(p.nome);";
        ResultSet rs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String trgprp = rs.getString("rgprp");
                String tprop = rs.getString("nome");
                String trgimv = rs.getString("rgimv");
                String tcontrato = rs.getString("contrato");
                String tloca = rs.getString("nomerazao");
                Date tvecto = rs.getDate("dtvencimento");
                float tvradia = 0;
                String tcampo = rs.getString("campo");
                int pos = tcampo.indexOf("@");
                if (pos > -1) {
                    String svradia = tcampo.substring(pos + 1, pos + 11);
                    tvradia = LerValor.FloatNumber(svradia,2);
                }
                String trcaut = rs.getString("autenticacao");
                
                int apos = tcampo.indexOf("AD");
                String tadaut = "";
                if (apos > -1) {
                    tadaut = tcampo.substring(apos + 2, apos + 8);
                }
                //StrVal(Mid(a.campo,InStr(a.campo,'@')+1)) AS vradiantado, Mid(a.campo,inStr(a.campo,'AD')+2,6) As adaut 
                        
                // Pega data do recebimento
                Date tdtrec = null;
                String cwhere = "rgprp = '&1.' AND rgimv = '&2.' AND contrato = '&3.' AND rc_aut = '&4.' AND conta = 'REC'";
                cwhere = FuncoesGlobais.Subst(cwhere, new String[] {trgprp, trgimv, tcontrato, trcaut});
                String[][] recData = conn.LerCamposTabela(new String[] {"dtrecebimento"}, "auxiliar", cwhere);
                if (recData != null) {
                    tdtrec = Dates.StringtoDate(recData[0][3], "yyyy/MM/dd");
                }
                
                // Pega data adiantamento
                // CAST((SELECT t.dtrecebimento FROM auxiliar t WHERE t.rgprp = a.rgprp AND Mid(a.campo, inStr(a.campo,'AD')+2,6) = t.rc_aut AND t.conta = 'ADI') AS DATE) as dtadiantamento
                Date tdtadi = null;
                String awhere = "rgprp = '&1.' AND rc_aut = '&2.' AND conta = 'ADI'";
                awhere = FuncoesGlobais.Subst(awhere, new String[] {trgprp, tadaut});
                String[][] adiData = conn.LerCamposTabela(new String[] {"dtrecebimento"}, "auxiliar", awhere);
                if (adiData != null) {
                    tdtadi = Dates.StringtoDate(adiData[0][3], "yyyy/MM/dd");
                }
                
                String iSql = "INSERT INTO tADIANTA (rgprp, prop, rgimv, contrato, loca, dtvecto, dtrecbto, dtadianta, vradianta) " +
                              "VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.','&9.')";
                iSql = FuncoesGlobais.Subst(iSql, new String[] {
                   trgprp,
                   tprop,
                   trgimv,
                   tcontrato,
                   tloca,
                   tvecto != null ? Dates.DateFormata("yyyy/MM/dd", tvecto) : "",
                   tdtrec != null ? Dates.DateFormata("yyyy/MM/dd", tdtrec) : "",
                   tdtadi != null ? Dates.DateFormata("yyyy/MM/dd", tdtadi) : "",
                   String.valueOf(tvradia).replace(",", ".")
                });
                iSql = iSql.replace("''", "null");
                try {conn.ExecutarComando(iSql);} catch (Exception e) {e.printStackTrace();}
            }
            DbMain.FecharTabela(rs);
        } catch (Exception e) {e.printStackTrace();}
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