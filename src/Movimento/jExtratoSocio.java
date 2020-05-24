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

import Funcoes.Autenticacao;
import Funcoes.AutoCompletion;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.VariaveisGlobais;
import Funcoes.WordWrap;
import Funcoes.toPrint;
import com.lowagie.text.Font;
import extrato.Extrato;
import Sici.Partida.Collections;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.swing.ComboBoxEditor;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author supervisor
 */
public class jExtratoSocio extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String rgprp = ""; String rgimv = ""; String contrato = ""; 
    JRViewer visor;
    boolean bExecNome = false, bExecCodigo = false;

    /** Creates new form jExtrato */
    public jExtratoSocio() throws JRException {
        initComponents();
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        FillCombos();
        AutoCompletion.enable(jRgprp);
        AutoCompletion.enable(jNomeProp);

        ComboBoxEditor edit = jNomeProp.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                String sPrint = Imprimir(true,true);
            }
            
            public void focusGained(java.awt.event.FocusEvent evt) {
            }
        });
     
        String sPrint = Imprimir(true,false);
    }

    private void FillCombos() {
        String sSql = "SELECT autoid, nome FROM SOCIOS ORDER BY autoid;";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        jRgprp.removeAllItems();
        jNomeProp.removeAllItems();
        try {
            while (imResult.next()) {
                jRgprp.addItem(FuncoesGlobais.StrZero(imResult.getString("autoid"), 2));
                jNomeProp.addItem(imResult.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }

    private String Imprimir(boolean Preview, boolean SelData) {
        String wDtInic = ""; String wDtFinal = "";
        if (SelData) {
            //wDtFinal = new JOptionPane
        }
        
        Collections gVar = VariaveisGlobais.dCliente;
        List<Extrato> lista = new ArrayList<Extrato>();
        String[][] sCampos = {};
        
        float fTotCred = 0; float fTotDeb = 0; float fSaldoAnt = 0;
        try {
            String sSaldoAnt = conn.LerCamposTabela(new String[] {"saldoant"}, "SOCIOS", "autoid = '" + jRgprp.getSelectedItem().toString() + "'")[0][3];            
            fSaldoAnt = Float.valueOf(sSaldoAnt.trim());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        //--------------------
        if (fSaldoAnt >= 0) {
            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Saldo Anterior","0;;black",LerValor.floatToCurrency(fSaldoAnt, 2) + " ",""});
            fTotCred += fSaldoAnt;
        }
        if (fSaldoAnt < 0) {
            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"Saldo Anterior","0;;black","",LerValor.floatToCurrency(fSaldoAnt * -1, 2) + " "});
            fTotDeb += (fSaldoAnt * -1);
        }
        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {"","0;;black","",""});
        //------------------
        
        String sql;
        if (SelData) {
            sql = FuncoesGlobais.Subst("SELECT campo FROM avisos WHERE registro = '&1.' AND rid = '3' AND (tag <> 'X' OR ISNULL(tag)) ORDER BY RetAvDataRid2(campo);", new String[] {jRgprp.getSelectedItem().toString()});
        } else {
            sql = FuncoesGlobais.Subst("SELECT campo FROM avisos WHERE registro = '&1.' AND rid = '3' AND (tag <> 'X' OR ISNULL(tag)) ORDER BY RetAvDataRid2(campo);", new String[] {jRgprp.getSelectedItem().toString()});
        }
        ResultSet hrs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        
        try {
            while (hrs.next()) {
                String tmpCampo = "" + hrs.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, false);
                String sinq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" + rCampos[0][7].substring(2,4) + "/" + rCampos[0][7].substring(4);
                if (!"".equals(sinq.trim())) {
                    String aLinhas[] = WordWrap.wrap(sinq, 237, getFontMetrics(new java.awt.Font("SansSerif",Font.NORMAL,8))).split("\n");
                    for (int k=0;k<aLinhas.length;k++) { sCampos = FuncoesGlobais.ArraysAdd(sCampos,new String[] {aLinhas[k].replace("ò", " "),"0;;black","",""}); }
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
        
        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {new Pad("Total de Créditos..........." + LerValor.floatToCurrency(fTotCred, 2),45).RPad(),"0;b;black","",""});
        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {new Pad("Total de Débitos............" + LerValor.floatToCurrency(fTotDeb, 2),45).RPad(),"0;b;black","",""});
        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {new Pad("Liquido a Receber..........." + LerValor.floatToCurrency(fTotCred - fTotDeb, 2),45).RPad(),(fTotCred - fTotDeb < 0 ? "0;b;red" : "0;b;black"),"",""});

        float nAut = 0;
        if (!Preview) {
            //if (aTrancicao.length <= 0 ) return LerValor.floatToCurrency(fTotCred - fTotDeb, 2);
            
            nAut = (float)Autenticacao.getAut();
            if (!Autenticacao.setAut((double)nAut, 1)) {
                JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            //try {nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));} catch (SQLException ex) {}
        }
        
        String sAut = FuncoesGlobais.StrZero(String.valueOf(nAut),6);
        
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
          
          bean1.setautentica("PAL" + sAut);
        } else bean1.setautentica("");
        
        lista.add(bean1);
        
        JRDataSource jrds = new JRBeanCollectionDataSource(lista);
        
        String outFileName = "";
        try {
            String fileName = "reports/" + (Preview ? "ExtratoPreview.jasper" : "Extrato.jasper");
            JasperPrint print = JasperFillManager.fillReport(fileName, null, jrds);

            if (!Preview) {
                // Create a PDF exporter
                JRExporter exporter = new JRPdfExporter();

                // Configure the exporter (set output file name and print object)
                outFileName = "reports/Extratos/" + jRgprp.getSelectedItem().toString().trim() + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
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
                visor.setFitWidthZoomRatio();
                jView.add(visor);

                jView.addContainerListener(new java.awt.event.ContainerAdapter() {
                    public void componentAdded(java.awt.event.ContainerEvent evt) {
                        visor.setBounds(0, 0, jView.getWidth(), jView.getHeight());
                        visor.setFitWidthZoomRatio();
                        jView.revalidate();
                    }
                });

                jView.addComponentListener(new java.awt.event.ComponentAdapter() {
                    public void componentResized(java.awt.event.ComponentEvent evt) {
                        visor.setBounds(0, 0, jView.getWidth(), jView.getHeight());
                        jView.revalidate();
                        visor.setFitWidthZoomRatio();
                    }
                });
            } else {
                new toPrint(outFileName, VariaveisGlobais.ExtratoSocio.split(",")[0],VariaveisGlobais.ExtratoSocio.split(",")[1],VariaveisGlobais.ExtratoSocio.split(",")[2]);
//                if (VariaveisGlobais.extPrintCmd.trim().equals("JasperPrintManager")) {
//                    JasperPrintManager.printPage(print, 0, false);
//                } else {
//                    //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});
//                    ComandoExterno ce = new ComandoExterno();
//                    ce.ComandoExterno(VariaveisGlobais.extPrintCmd + " " + outFileName);
//                }
//                //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});
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
                if (Dates.iMonth(new Date()) == Dates.iMonth(Dates.StringtoDate(DtNascProp, "dd/MM/yyyy"))) bean1.setmensagem(msgNiver);
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
        jPrintExtr = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(".:: Extrato de Sócios ::.");
        setVisible(true);

        jView.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jView.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        javax.swing.GroupLayout jViewLayout = new javax.swing.GroupLayout(jView);
        jView.setLayout(jViewLayout);
        jViewLayout.setHorizontalGroup(
            jViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 499, Short.MAX_VALUE)
        );
        jViewLayout.setVerticalGroup(
            jViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 8));
        jLabel1.setText("Sócio:");

        jRgprp.setFont(new java.awt.Font("Dialog", 1, 8));
        jRgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRgprpActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 8));
        jLabel2.setText("Nome:");

        jNomeProp.setFont(new java.awt.Font("Dialog", 1, 8));
        jNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomePropActionPerformed(evt);
            }
        });

        jPrintExtr.setFont(new java.awt.Font("Dialog", 0, 8));
        jPrintExtr.setText("Imprimir");
        jPrintExtr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPrintExtrActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNomeProp, 0, 268, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPrintExtr)
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
                    .addComponent(jPrintExtr))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void jPrintExtrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPrintExtrActionPerformed
        String sValor = Imprimir(false,true);
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(null,
                "Deseja fechar extrato do Sócio ? ",
                "Atenção", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) {
            String sql = "UPDATE avisos SET tag = 'X' WHERE rid = 3 AND registro = '" + jRgprp.getSelectedItem().toString().trim() + "';";
            conn.ExecutarComando(sql);
            sql = "UPDATE SOCIOS SET saldoant = '" + LerValor.StringToFloat(sValor) + "' WHERE autoid = '" + jRgprp.getSelectedItem().toString().trim() + "';";
            conn.ExecutarComando(sql);
        }
        sValor = Imprimir(true, true);
        jRgprp.requestFocus();
    }//GEN-LAST:event_jPrintExtrActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jPrintExtr;
    private javax.swing.JComboBox jRgprp;
    private javax.swing.JPanel jView;
    // End of variables declaration//GEN-END:variables

}
