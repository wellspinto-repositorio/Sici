/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jMensalao.java
 *
 * Created on 14/10/2011, 11:44:55
 */
package Movimento;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.jTableControl;
import Funcoes.toPreview;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mensalao.Mensalao;
import net.sf.jasperreports.engine.JRDataSource;
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
public class jMensalao extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    jTableControl tabela = new jTableControl(true);
    
    /** Creates new form jMensalao */
    public jMensalao() {
        initComponents();
        
        FillProp();
    }

    private void FillProp() {
        String sql = "SELECT rgprp, nome FROM proprietarios ORDER BY nome;";
        sql = FuncoesGlobais.Subst(sql, new String[] {VariaveisGlobais.usuario.toLowerCase().trim()});

        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Integer[] tam = {40,350,20};
        String[] col = {"rgprp","Nome","tag"};
        Boolean[] edt = {false,false,true};
        String[] aln = {"L","L",""};
        Object[][] data = {};
        try {
            while (rs.next()) {
                String drgprp = rs.getString("rgprp");
                String dnome = rs.getString("nome");
                Boolean dTag = true;
                
                Object[] dado = {drgprp, dnome, dTag};
                data = tabela.insert(data, dado);
            }
        } catch (SQLException ex) {ex.printStackTrace();}

        DbMain.FecharTabela(rs);
        tabela.Show(jtbReten, data, tam, aln, col, edt);
    }
    
    private void Mensalao(String mes, String ano, String minimo) {
        float tres = 0, tcom = 0, tncom = 0, tirf = 0;
        //String Sql = "SELECT p.rgprp, p.nome, p.cpfcnpj, i.rgprp, i.rgimv, i.tpimovel, r.rgprp, r.rgimv, r.contrato, r.campo, r.dtvencimento FROM proprietarios p, imoveis i, extrato r where p.rgprp = i.rgprp AND i.rgimv = r.rgimv AND (Year(dtrecebimento) = &1. AND Month(dtrecebimento) = &2.) order by i.rgprp, i.rgimv, i.tpimovel;";
        String Sql = "SELECT p.rgprp, p.nome, p.cpfcnpj, i.rgprp, i.rgimv, i.tpimovel, r.rgprp, r.rgimv, r.contrato, r.campo, r.dtvencimento FROM proprietarios p, imoveis i, auxiliar r where (r.conta = 'REC') AND p.rgprp = i.rgprp AND i.rgimv = r.rgimv AND (Year(dtrecebimento) = &1. AND Month(dtrecebimento) = &2.) order by i.rgprp, i.rgimv, i.tpimovel;";
        Sql = FuncoesGlobais.Subst(Sql, new String[] {ano, mes});
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
        
        List<Mensalao> lista = new ArrayList<Mensalao>();
        Mensalao bean1 = new Mensalao();
        
        String sprop = "";
        String tprop = "";
        String ttipo = "";
        String tnome = "";
        String tcpf = "";            
        
        String tmpTexto = "";
        for (int i=0; i< jtbReten.getRowCount(); i++) {
            if ("true".equals(jtbReten.getModel().getValueAt(i, 2).toString().toLowerCase())) {        
                tmpTexto += jtbReten.getModel().getValueAt(i, 0).toString() + ";";
            }
        }
        
        try {
            while (rs.next()) {
                tprop = rs.getString("rgprp");
                if (FuncoesGlobais.OfIndex(tmpTexto.split(";"),tprop) > -1) {
                    String campo = rs.getString("campo");
                    float[] alrtCampos = PegaAluguel(campo);

                    if (!tprop.equals(sprop)) {
                        if (!"".equals(sprop.trim())) {
                            if (tcom + tres + tncom >= LerValor.StringToFloat(minimo)) {
                                bean1 = new Mensalao();
                                bean1.setrgprp(sprop);
                                bean1.setnome(tnome);
                                bean1.setcpfcnpj(tcpf);
                                bean1.setcomercial(tres);
                                bean1.setresidencial(tcom);
                                bean1.setnresidencial(tncom);
                                bean1.setirrf(tirf);
                                lista.add(bean1);

                            }
                        }
                        tres = 0; tcom = 0; tncom = 0; tirf = 0; sprop = rs.getString("rgprp");
                    }

                    try {tirf += alrtCampos[5];} catch (Exception e) {}

                    String tploca = conn.LerCamposTabela(new String[] {"tploca"}, "locatarios", "contrato = '" + rs.getString("contrato") + "'")[0][3].toUpperCase().trim();
//                    if ("RESIDENCIAL".equals(ttipo.toUpperCase().trim())) {
//                        tres += alrtCampos[0] + alrtCampos[2] - alrtCampos[1];
//                    } else if ("COMERCIAL".equals(ttipo.toUpperCase().trim())) {
//                        tcom += alrtCampos[0] + alrtCampos[2] - alrtCampos[1];
//                    } else {
//                        tncom += alrtCampos[0] + alrtCampos[2] - alrtCampos[1];
//                    }            
                    if ("F".equals(tploca)) {
                        tres += alrtCampos[0] - alrtCampos[4] + alrtCampos[2];
                    } else {
                        tcom += alrtCampos[0] - alrtCampos[4] + alrtCampos[2];
                    }            

                    ttipo = rs.getString("tpimovel");
                    tnome = rs.getString("nome");
                    tcpf = rs.getString("cpfcnpj");            
                }
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(rs);
        
        if (tcom + tres >= LerValor.StringToFloat(minimo)) {
            bean1 = new Mensalao();
            bean1.setrgprp(sprop);
            bean1.setnome(tnome);
            bean1.setcpfcnpj(tcpf);
            bean1.setcomercial(tres);
            bean1.setresidencial(tcom);
            bean1.setnresidencial(tncom);
            bean1.setirrf(tirf);
            lista.add(bean1);

        }

        Map parametros = new HashMap();
        parametros.put("parameter1", mes.trim() + "/" + ano.trim());
        parametros.put("parameter2", minimo);
        JRDataSource jrds = new JRBeanCollectionDataSource(lista);
        
        try {
            String fileName = "reports/rMensalao.jasper";
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrds);

            // Create a PDF exporter
            
              JRExporter exporter = new JRPdfExporter();

              new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
              String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

              // Configure the exporter (set output file name and print object)
              String outFileName = pathName + "Mensalao_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
              exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
              exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

              // Export the PDF file
              exporter.exportReport();
        
              //JasperViewer viewer = new JasperViewer(print, false);
              //viewer.show();
              new toPreview(outFileName);
//              ComandoExterno.ComandoExterno(VariaveisGlobais.reader + " " + outFileName);
              
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }                
    }
    
    private float[] PegaAluguel(String tmpCampo) throws SQLException {        
        if ("".equals(tmpCampo.trim())) return new float[] {0, 0, 0};
        
        String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);
        float fTotAL = 0; float fTotDC = 0; float fTotDF = 0; float fTotIR = 0; float ftdcal = 0; float ftdcir = 0;
        for (int j = 0; j<rCampos.length; j++) {
            if ("AL".equals(rCampos[j][4])) {
                if (LerValor.isNumeric(rCampos[j][0])) {
                    fTotAL += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                } else {
                    fTotAL += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                }
            } else if ("DC".equals(rCampos[j][4])) {
                if (rCampos[j][rCampos[j].length - 1].toUpperCase().contains("ALUG")) {
                    if (LerValor.isNumeric(rCampos[j][0])) {
                        ftdcal += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                    } else {
                        ftdcal += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                    }
                } else if (rCampos[j][rCampos[j].length - 1].toUpperCase().contains("IR")) {
                    if (LerValor.isNumeric(rCampos[j][0])) {
                        ftdcir += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                    } else {
                        ftdcir += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                    }
                }
            } else if ("DF".equals(rCampos[j][4]) && rCampos[j][rCampos[j].length - 1].toUpperCase().contains("ALUG")) {
                if (LerValor.isNumeric(rCampos[j][0])) {
                    fTotDF += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                } else {
                    fTotDF += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                }
            } 
        }
        return new float[] {fTotAL, fTotDC, fTotDF, fTotIR, ftdcal, ftdcir};
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
        jMesAno = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jVrMinimo = new javax.swing.JFormattedTextField();
        jListar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtbReten = new javax.swing.JTable();
        jcbMarcar = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Mensalão ::.");
        setFont(new java.awt.Font("Andale Mono", 0, 8)); // NOI18N
        setVisible(true);

        jLabel1.setText("Mês/Ano:");

        try {
            jMesAno.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel2.setText("Vr.Mínimo IR:");

        jVrMinimo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jVrMinimo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jVrMinimo.setText("0,00");

        jListar.setText("Listar");
        jListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jListarActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lista dos Proprietários", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 0, 153)));
        jScrollPane2.setForeground(new java.awt.Color(0, 0, 153));
        jScrollPane2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        jtbReten.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(jtbReten);

        jcbMarcar.setSelected(true);
        jcbMarcar.setText("Marcar/Desmarcar Todos");
        jcbMarcar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbMarcarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jcbMarcar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(4, 4, 4)
                        .addComponent(jMesAno, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jVrMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jListar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jMesAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jVrMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jListar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbMarcar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jListarActionPerformed
        Mensalao(jMesAno.getText().substring(0,2), jMesAno.getText().substring(3,7), jVrMinimo.getText());
    }//GEN-LAST:event_jListarActionPerformed

    private void jcbMarcarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbMarcarActionPerformed
        int nrow = jtbReten.getRowCount();
        for (int i=0;i<nrow;i++) {
            jtbReten.getModel().setValueAt(!(Boolean)jtbReten.getModel().getValueAt(i, 2), i, 2);
        }
    }//GEN-LAST:event_jcbMarcarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jListar;
    private javax.swing.JFormattedTextField jMesAno;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JFormattedTextField jVrMinimo;
    private javax.swing.JCheckBox jcbMarcar;
    private javax.swing.JTable jtbReten;
    // End of variables declaration//GEN-END:variables
}
