/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jPassCaixa.java
 *
 * Created on 23/06/2011, 14:16:04
 */
package Movimento;

import Funcoes.Autenticacao;
import Funcoes.ComponentSearch;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.JavaPOS;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.PrinterPOS;
import Funcoes.VariaveisGlobais;
import Funcoes.WordWrap;
import Funcoes.jPDF;
import Funcoes.jThermica;
import Funcoes.toPrint;
import Transicao.jReceber;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeInter25;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import Sici.Partida.Collections;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 *
 * @author supervisor
 */
public class jPassCaixa extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    jReceber tRec = new jReceber();
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
        
        jbtOK.setVisible(false);
        jbtOK.setEnabled(false);
        
        //tRec.setBounds(5, 5, 374, 229);
        tRec.setBounds(0, 0, 480, 250);
        
        try {
            jpRecebe.add(tRec);
        } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace(); }
        jpRecebe.repaint();
        jpRecebe.setEnabled(true);
        tRec.acao = "PC";

        btnLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tRec.Lancar();
                if (tRec.bprintdoc) {
                    try {
                        try {
                            Imprimir();
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

    /** Creates new form jPassCaixa */
    public jPassCaixa() {
        initComponents();
        
        InitjReceber();
        tRec.Enable(false);
        jrdbCH.setEnabled(FillCheques());
        jrdbCH.setVisible(false);
        jcbxCHS.setVisible(false);
        jrdbDN.setVisible(false);
        jValor.setEnabled(true);
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
        jtgbCRDB = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtxaTexto = new javax.swing.JTextArea();
        jcbxCHS = new javax.swing.JComboBox();
        jrdbCH = new javax.swing.JRadioButton();
        jrdbDN = new javax.swing.JRadioButton();
        jValor = new javax.swing.JFormattedTextField();
        jpRecebe = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jbtOK = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Passagem de Caixa ::.");
        setFont(new java.awt.Font("Agency FB", 0, 8));
        setVisible(true);

        jtgbCRDB.setForeground(new java.awt.Color(0, 102, 0));
        jtgbCRDB.setText("CRÉDITO");
        jtgbCRDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtgbCRDBActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Texto"));
        jScrollPane1.setFont(new java.awt.Font("Tahoma", 0, 8));

        jtxaTexto.setColumns(20);
        jtxaTexto.setLineWrap(true);
        jtxaTexto.setRows(5);
        jtxaTexto.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jtxaTexto);

        jcbxCHS.setEnabled(false);
        jcbxCHS.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jcbxCHSFocusLost(evt);
            }
        });

        buttonGroup1.add(jrdbCH);
        jrdbCH.setText("Cheque");
        jrdbCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdbCHActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrdbDN);
        jrdbDN.setText("Dinheiro");
        jrdbDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdbDNActionPerformed(evt);
            }
        });

        jValor.setForeground(new java.awt.Color(0, 0, 204));
        jValor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jValor.setText("0,00");
        jValor.setEnabled(false);
        jValor.setFocusLostBehavior(javax.swing.JFormattedTextField.COMMIT);
        jValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jValorFocusLost(evt);
            }
        });

        jpRecebe.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpRecebe.setPreferredSize(new java.awt.Dimension(482, 253));

        javax.swing.GroupLayout jpRecebeLayout = new javax.swing.GroupLayout(jpRecebe);
        jpRecebe.setLayout(jpRecebeLayout);
        jpRecebeLayout.setHorizontalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 476, Short.MAX_VALUE)
        );
        jpRecebeLayout.setVerticalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );

        jLabel1.setText("Valor Total:");

        jbtOK.setText("OK");
        jbtOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtOKActionPerformed(evt);
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
                        .addComponent(jrdbDN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jtgbCRDB, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jrdbCH)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbxCHS, 0, 267, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jtgbCRDB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jrdbCH)
                            .addComponent(jcbxCHS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jrdbDN)
                            .addComponent(jLabel1)
                            .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtOK))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtgbCRDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtgbCRDBActionPerformed
        jtgbCRDB.setText((!jtgbCRDB.isSelected() ? "CRÉDITO" : "DÉBITO"));
        if (!jtgbCRDB.isSelected()) {
            // CREDITO
            this.setResizable(true);
            this.setSize(876, 307);
            jpRecebe.setVisible(true);
            tRec.setVisible(true);
            this.setResizable(false);

            jtgbCRDB.setForeground(new Color(0,102,0));
            tRec.Enable(false);
            jrdbCH.setVisible(false);
            jcbxCHS.setVisible(false);
            jrdbDN.setVisible(false);
            jValor.setEnabled(true);
            jValor.setText("0,00");

            jbtOK.setVisible(false);
            jbtOK.setEnabled(false);
            
        } else {
            // DEBITO
            this.setResizable(true);
            this.setSize(553, 303);
            jpRecebe.setVisible(false);
            tRec.setVisible(false);
            this.setResizable(false);
            
            jtgbCRDB.setForeground(new Color(204,0,0));
            tRec.Enable(false);
            jrdbCH.setVisible(true);
            jcbxCHS.setVisible(true);
            jrdbDN.setVisible(true);
            jValor.setEnabled(false);
            jValor.setText("0,00");

            jbtOK.setVisible(true);
            jbtOK.setEnabled(false);
        }
        jtxaTexto.setText("");
        jtxaTexto.requestFocus();
    }//GEN-LAST:event_jtgbCRDBActionPerformed

    private void jrdbCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdbCHActionPerformed
        jValor.setText("0,00");
        jValor.setEnabled(false);
        jcbxCHS.setEnabled(true);
        jcbxCHS.setSelectedIndex(0);
        jcbxCHS.requestFocus();
    }//GEN-LAST:event_jrdbCHActionPerformed

    private void jrdbDNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdbDNActionPerformed
        if (jrdbCH.isEnabled()) {
            jcbxCHS.setEnabled(false);
            jcbxCHS.setSelectedIndex(0);
        }
        jValor.setText("0,00");
        jValor.setEnabled(true);
        jValor.selectAll();
        jValor.requestFocus();        
    }//GEN-LAST:event_jrdbDNActionPerformed

    private void jcbxCHSFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jcbxCHSFocusLost
        jValor.setEnabled(true);
        jValor.setText(jcbxCHS.getSelectedItem().toString().substring(57));
        jValor.selectAll();
        jValor.requestFocus();
    }//GEN-LAST:event_jcbxCHSFocusLost

    private void jValorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jValorFocusLost
        try {jValor.commitEdit();} catch (Exception ex) {}
        float njValor =  LerValor.StringToFloat(jValor.getText());
                //((Number)jValor.getValue()).floatValue();
        if (njValor > 0) {
            if (!jtgbCRDB.isSelected()) {
                jbtOK.setEnabled(false);
                // CREDITO
                float iValor = njValor;
                jResto.setValue(iValor);
                tRec.vrAREC = LerValor.StringToFloat(jResto.getText());         

                tRec.rgimv = ""; tRec.rgprp = ""; tRec.contrato = ""; tRec.acao = "PC"; tRec.operacao = "CRE";
                tRec.btEnabled(true);
                btnCancelar.setEnabled(true);
            } else {
                // DEBITO
                jbtOK.setEnabled(true);
                jbtOK.requestFocus();
            }
        }
    }//GEN-LAST:event_jValorFocusLost

    private void jbtOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtOKActionPerformed
        tRec.rgimv = ""; tRec.rgprp = ""; tRec.contrato = ""; tRec.acao = "PC"; tRec.operacao = "DEB";
        tRec.btEnabled(false);
        btnCancelar.setEnabled(false);
        try {Imprimir();} catch (Exception ex) {ex.printStackTrace();}
    }//GEN-LAST:event_jbtOKActionPerformed

    private boolean FillCheques() {
        boolean ret = false;
        
        jcbxCHS.removeAllItems();
        String sSql = "SELECT ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, SUM(ch_valor) as ch_valor, ch_autenticacao FROM Cheques WHERE Lower(ch_ncaixa) = '" + VariaveisGlobais.usuario.toLowerCase() + "' group by ch_banco, ch_agencia, ch_ncheque ORDER BY ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor;";

        ResultSet hResult = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (hResult.next()) {
                String dData1 = "          ";
                try { dData1 = Dates.DateFormata("dd/MM/yyyy",hResult.getDate("ch_data")); } catch (Exception ex) {}
                String dData2 = "          ";
                try { dData2 = Dates.DateFormata("dd/MM/yyyy",hResult.getDate("ch_data2")); } catch (Exception ex) {}
                
                jcbxCHS.addItem(dData1 + "  " + dData2 + "  " + 
                        new Pad(hResult.getString("ch_banco"),3).RPad() + "  " +
                        new Pad(hResult.getString("ch_agencia"),10).RPad() + " " + 
                        new Pad(hResult.getString("ch_ncheque"),15).RPad() + "  " +
                        new Pad(LerValor.floatToCurrency(hResult.getFloat("ch_valor"),2),15).LPad());
            
                    ret = true;
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        DbMain.FecharTabela(hResult);
        
        return ret;
    }
    
    public void Imprimir() throws FileNotFoundException, IOException, SQLException {
        float nAut = 0;
        float vias = 1;
        String sDta = ""; String sPre = ""; String sBco = ""; String sAge = ""; String sNch = ""; String sVrl = ""; String sTp = "";

        try {jValor.commitEdit();} catch (Exception ex) {}
        float njValor =  LerValor.StringToFloat(jValor.getText());
        //((Number)jValor.getValue()).floatValue();

        String[][] aTrancicao = null;
        if (!jtgbCRDB.isSelected()) {
            // CREDITO
            aTrancicao = tRec.Transicao("PC");
            if (aTrancicao.length <= 0 ) return;
        } else {
            // DEBITO
            if (jrdbCH.isSelected()) {
                // "23/06/2011  dd/mm/yyyy  321  7654XXXXXX XXXXXXXXXXXXXX1  000000000987,65"
                //  012345678901234567890123456789012345678901234567890123456789012345678901
                //  0         1         2         3         4         5         6         7  
                //  123456789012345678901234567890123456789012345678901234567890123456789012
                //  0        1         2         3         4         5         6         7
                String sCH = jcbxCHS.getSelectedItem().toString().trim();
                sDta = sCH.substring(0, 10);
                sPre = sCH.substring(12, 22).trim();
                sBco = new Pad(sCH.substring(24, 27).trim(),3).RPad();
                sAge = new Pad(sCH.substring(29, 39).trim(),5).RPad();
                sNch = new Pad(sCH.substring(40, 55).trim(),10).RPad();
                sVrl = String.valueOf(njValor);
                sTp = ("".equals(sPre) ? "CH" : "CP");
            } else {
                sTp = "DN";
                sVrl = String.valueOf(njValor);
            }
            aTrancicao = new String[][] {{sPre, sBco, sAge, sNch, sVrl, sTp, "DEB","PC",""}};
        }
        
        // Autenticacao
        nAut = (float)Autenticacao.getAut();
        if (!Autenticacao.setAut((double)nAut, 1)) {
            JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        //nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));
        //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

        String corpo = jtxaTexto.getText();
        
        String idNome = (!jtgbCRDB.isSelected() ? "PASSAGEM DE CAIXA - CREDITO" : "PASSAGEM DE CAIXA - DEBITO");
        
//        if (VariaveisGlobais.DefaultPrinterMode.equalsIgnoreCase("NORMAL")) {
        if (VariaveisGlobais.PassCaixa.split(",")[1].equalsIgnoreCase("TXT")) {
            ImprimePassCaixa(nAut, aTrancicao, corpo, LerValor.floatToCurrency(njValor,2), idNome, "F");
        } else {
            //ImprimePassCaixaThermica(nAut, aTrancicao, corpo, LerValor.floatToCurrency(njValor,2), idNome, "F");
            ImprimePassCaixaPDF(nAut, aTrancicao, corpo, LerValor.floatToCurrency(njValor,2), idNome, "F");
        }
        
        if (jtgbCRDB.isSelected()) {
            // DEBITO/CH
            if (jrdbCH.isSelected()) {
                String sql = "DELETE FROM Cheques WHERE  Lower(ch_ncaixa) = '" + VariaveisGlobais.usuario.toLowerCase() + "' AND ch_data = '&1.' AND trim(ch_banco) = '&2.' AND trim(ch_agencia) = '&3.' AND trim(ch_ncheque) = '&4.'";
                sql = FuncoesGlobais.Subst(sql, new String[] {Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sDta, "dd-MM-yyyy")), sBco, sAge, sNch});
                conn.ExecutarComando(sql);
                
                jrdbCH.setEnabled(FillCheques());
            }
        }
        
        // grava no caixa
        conn.LancarCaixa(new String[] {tRec.rgprp, tRec.rgimv, tRec.contrato}, aTrancicao,LerValor.FloatToString((int)nAut).replace(",0", ""));
        
        // Manda para razao
        String sql = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento) VALUES ('PC','AV:9:&1.:000000:AV:ET:&2._05PC:&3.:&4.:&5.:&6.:&7.','&8.','&9.')";
        sql = FuncoesGlobais.Subst(sql, new String[] {
            String.valueOf(njValor),
            FuncoesGlobais.StrZero(String.valueOf(nAut),6),
            Dates.DateFormata("ddMMyyyy", new Date()),
            tRec.operacao, sTp, FuncoesGlobais.CriptaNome("PASSAGEM DE CAIXA"), VariaveisGlobais.usuario,
            Dates.DateFormata("yyyy-MM-dd", new Date()), Dates.DateFormata("yyyy-MM-dd", new Date())});
        sql = sql.replace("_", "") ;
        conn.ExecutarComando(sql);
        
        // grava no auxiliar
        String sVar = "PC:9:" + FuncoesGlobais.GravaValor(LerValor.floatToCurrency(njValor, 2)) + 
                      ":000000:PC:" + FuncoesGlobais.StrZero(String.valueOf(nAut),10) + ":" + 
                      Dates.DateFormata("ddMMyyyy", new Date()) + ":" + tRec.operacao + ":" + 
                      FuncoesGlobais.CriptaNome("PASSAGEM DE CAIXA") + ":" + VariaveisGlobais.usuario;
        sql = "INSERT INTO auxiliar (conta, contrato, campo, dtvencimento, dtrecebimento, rc_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.');";
        sql = FuncoesGlobais.Subst(sql, new String[] {
                "PCX", "", sVar, Dates.DateFormata("yyyy-MM-dd", new Date()),
                Dates.DateFormata("yyyy-MM-dd", new Date()), String.valueOf(nAut)});
        conn.ExecutarComando(sql);
        
        tRec.LimpaTransicao();
        tRec.Cancelar(); tRec.Cancelar();
        jResto.setValue(0);
        tRec.vrAREC = 0;
        jtxaTexto.setText("");
        jValor.setText("0,00");
        jValor.setValue(0);
        tRec.Enable(false);
        tRec.btEnabled(false);
        btnLancar.setEnabled(false);
        btnCancelar.setEnabled(false);        
    }

    private String CampoAviso(String mAutenticacao, String mTipo, String mData, String mValor, String mDesc) {
        String retorno;
        
        retorno = "AV:9:" + mValor + ":000000:AV:ET:" + mAutenticacao + ":" + mData + ":" +
             mTipo + ":" + mDesc + ":" + VariaveisGlobais.usuario;
                
        return retorno;
    }
    
    public void ImprimePassCaixa(float nAut, String[][] Valores, String texto, String ValorRec, String idAviso, String cutPaper) {
        Collections gVar = VariaveisGlobais.dCliente;

        String docID = ".ps";
        String docName = "reports/Recibos/" + "PC_" + Dates.DateFormata("dd-MM-yyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
        PrinterPOS printer = new PrinterPOS(docName);
        //printer.setDocName(docName);

        // inicializa impressora
        printer.Print(JavaPOS.ESC_ARROBA + "", 1);

        // Imprime logomarca
        //////////printer.PrintBitMap("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif",0);
        //////////printer.Print("", 1);

        // cabeçário
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_E(1) + gVar.get("empresa"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "Tel/Fax:" + gVar.get("telefone") + JavaPOS.ESC_E(0), 1);
        printer.Print("", 1);

        printer.Print(JavaPOS.ESCLAMATION(70) + JavaPOS.ESC_A(1) + (idAviso.indexOf("DEBITO") > -1 ? JavaPOS.REVERSO : "") + idAviso + JavaPOS.NORMAL, 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "", 1);

        printer.Print(JavaPOS.ESCLAMATION(65) + "CAIXA: " + new Pad(VariaveisGlobais.usuario,15).RPad() + "       Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()) ,1);

        printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);

        // imprime aviso aqui
        String[] aLinhas = WordWrap.wrap(texto, 270, getFontMetrics(new java.awt.Font("SansSerif",Font.NORMAL,10))).split("\n");
        for (int k=0;k<aLinhas.length;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + aLinhas[k], 1); }

        printer.Print("",1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(2) + "Total da Passagem de Caixa..........  ",0);
        printer.Print(JavaPOS.ESCLAMATION(65) + ValorRec,1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "",1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);

        if (nAut > 0) {
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) + "VALOR(ES) LANCADOS" + JavaPOS.NORMAL, 1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) +  "--------------------------------------------------------" + JavaPOS.NORMAL,1);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(LerValor.floatToCurrency(Float.valueOf(Valores[i][4]),2),10).LPad();
                } else bLinha = "DN:" + new Pad(LerValor.floatToCurrency(Float.valueOf(Valores[i][4]),2),10).LPad();

                printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(2) + bLinha, 1);
            }

            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);

            // Imprimir Autenticação
            printer.Print(JavaPOS.ESC_A(1) + JavaPOS.ESCLAMATION(65) + JavaPOS.REVERSO + VariaveisGlobais.dCliente.get("marca").trim() + JavaPOS.NORMAL + "AV" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,1);
            //////////////printer.PrintBarCode(FuncoesGlobais.StrZero(String.valueOf((int)nAut), 12));
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + "", 1); }

        if ("P".equals(cutPaper.trim().toUpperCase())) { printer.Print(JavaPOS.PARTCUT, 0); } else { printer.Print(JavaPOS.FULLCUT, 0); }

        //printer.Close();
        new toPrint(docName, VariaveisGlobais.PassCaixa.split(",")[0],VariaveisGlobais.PassCaixa.split(",")[1],VariaveisGlobais.PassCaixa.split(",")[2]);
        printer.setDocName("");
    }   

    public void ImprimePassCaixaPDF(float nAut, String[][] Valores, String texto, String ValorRec, String idAviso, String cutPaper) {
        float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();

        String docID = ".pdf";
        String pathName = "reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
        pdf.setPathName(pathName);
        String docName = "PC_" + Dates.DateFormata("dd-MM-yyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
        pdf.setDocName(docName);
        
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, 9, java.awt.Font.PLAIN);

        pdf.open();
        
        // Logo
        com.itextpdf.text.Image img;
        try {
            img = com.itextpdf.text.Image.getInstance("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif");
            img.setAlignment(Element.ALIGN_LEFT);        
            pdf.doc_add(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Paragraph p;
        
        p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
            p = pdf.print(gVar.get("tipodoc"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT,pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Tel/Fax:" + gVar.get("telefone"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(idAviso, pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 9, java.awt.Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        
        PdfPCell cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        columnWidths = new float[] {100};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        // Dados do aviso
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase(texto,font));
        cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        table.completeRow();
        pdf.doc_add(table);
        
        columnWidths = new float[] {70, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("==========", font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total da Passagem de Caixa",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(ValorRec, font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new com.itextpdf.text.Font(bf, 8, java.awt.Font.PLAIN);
        if (nAut > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

                p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                pdf.doc_add(p);
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + 
                    "PC" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + 
                    "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + 
                    FuncoesGlobais.GravaValores(ValorRec, 2) + 
                    VariaveisGlobais.usuario, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);
            
            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)nAut),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            com.itextpdf.text.Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);            
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        
        pdf.close();
        //pdf.print();
        new toPrint(pathName + docName, VariaveisGlobais.PassCaixa.split(",")[0],VariaveisGlobais.PassCaixa.split(",")[1],VariaveisGlobais.PassCaixa.split(",")[2]);
        pdf.setPathName("");
        pdf.setDocName("");
    }
 
    public void ImprimePassCaixaThermica(float nAut, String[][] Valores, String texto, String ValorRec, String idAviso, String cutPaper) {
        Object[][] linhas = {};
        jThermica Printer = new jThermica(linhas);
        Collections gVar = VariaveisGlobais.dCliente;

        // cabeçário
        Image img1 = Toolkit.getDefaultToolkit().getImage("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif");
        Object[][] tmp = {
                            {img1,null,0,null,null,null}
                         };
        linhas =  Printer.adcLine(linhas, tmp);            

        tmp = new Object[][] {
                                {gVar.get("empresa"),null,1,new java.awt.Font("Arial",java.awt.Font.BOLD + java.awt.Font.ITALIC,9), Color.BLACK, Color.WHITE}
                            };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"),null,1,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                               };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"),null,1,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"Tel/Fax:" + gVar.get("telefone"),null,1,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {idAviso,null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,10), (idAviso.indexOf("DEBITO") > -1 ? Color.WHITE : Color.BLACK), (idAviso.indexOf("DEBITO") > -1 ? Color.BLACK : Color.WHITE)}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"CAIXA: " + VariaveisGlobais.usuario,80,1,new java.awt.Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE},
                            {"Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),null,2,new java.awt.Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {null,null,2,null, Color.BLACK, Color.WHITE},
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        // imprime aviso aqui
        String[] aLinhas = WordWrap.wrap(texto, 204, getFontMetrics(new java.awt.Font("SansSerif",java.awt.Font.PLAIN,8))).split("\n");
        for (int k=0;k<aLinhas.length;k++) { 
              tmp = new Object[][] {
                                      {aLinhas[k],null,1,new java.awt.Font("SansSerif",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                   };
              linhas =  Printer.adcLine(linhas, tmp);
        }

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Times New Roman",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"============",null,2,new java.awt.Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"Total da Passagem de Caixa",100,1,new java.awt.Font("Times New Roman",java.awt.Font.BOLD,7), Color.BLACK, Color.WHITE},
                            {ValorRec,null,2,new java.awt.Font("Times New Roman",java.awt.Font.BOLD,8), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {null,null,2,null, Color.BLACK, Color.WHITE},
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        if (nAut > 0) {
              tmp = new Object[][] {
                                      {"VALOR(ES) LANCADOS",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                   };
              linhas =  Printer.adcLine(linhas, tmp);

              tmp = new Object[][] {
                                  {null,null,2,null, Color.BLACK, Color.WHITE},
                               };
              linhas =  Printer.adcLine(linhas, tmp);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

               tmp = new Object[][] {
                                      {bLinha,null,2,new java.awt.Font("Arial",java.awt.Font.PLAIN,6), Color.BLACK, Color.WHITE}
                                   };
               linhas =  Printer.adcLine(linhas, tmp);
            }

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                 };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                {null,null,2,null, Color.BLACK, Color.WHITE},
                             };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                 };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            // Imprimir Autenticação
            tmp = new Object[][] {
                                    {VariaveisGlobais.dCliente.get("marca").trim() + "AV" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,null,0,new java.awt.Font("Arial",java.awt.Font.BOLD,7), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            // Codigo de barra
            //printer.PrintBarCode(FuncoesGlobais.StrZero(String.valueOf((int)nAut), 12));
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);
        }
        String docID = ".ps";
        String docName = "reports/Recibos/" + "PC_" + Dates.DateFormata("dd-MM-yyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
                //"reports/Recibos/" + "PC_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;

        jThermica np = new jThermica(linhas);
        np.setDocName(docName);
        np.toSave();
//        np.toPrint();
        np.setDocName("");
    }   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField jValor;
    private javax.swing.JButton jbtOK;
    private javax.swing.JComboBox jcbxCHS;
    private javax.swing.JPanel jpRecebe;
    private javax.swing.JRadioButton jrdbCH;
    private javax.swing.JRadioButton jrdbDN;
    private javax.swing.JToggleButton jtgbCRDB;
    private javax.swing.JTextArea jtxaTexto;
    // End of variables declaration//GEN-END:variables
}
