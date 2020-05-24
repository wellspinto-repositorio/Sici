/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jDespesas.java
 *
 * Created on 01/06/2011, 14:09:31
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
import Funcoes.TableControl;
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
import Sici.Partida.jAutoriza;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;

/**
 *
 * @author supervisor
 */
public class jDespesas extends javax.swing.JInternalFrame {
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
        //tRec.setBounds(5, 5, 365, 229);
        tRec.setBounds(0, 0, 481, 250);
        
        try {
            jpRecebe.add(tRec);
        } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace(); }
        jpRecebe.repaint();
        jpRecebe.setEnabled(true);
        tRec.acao = "DP"; tRec.operacao = "DEB"; tRec.LimpaTransicao();

        btnLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tRec.Lancar();
                if (tRec.bprintdoc) {
                    try {
                        Imprimir();
                        tRec.Cancelar();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tRec.Cancelar()) {
                    txbValor.setEnabled(true);
                    tRec.btEnabled(false);
                    btnCancelar.setEnabled(false);
                    btnLancar.setEnabled(false);                    
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

    /** Creates new form jDespesas */
    public jDespesas() {
        initComponents();
        
        InitjReceber();

        tRec.btEnabled(false);
        btnCancelar.setEnabled(false);
        btnLancar.setEnabled(false);
        
        FillBuffer(tvbBuffer);
    }

    private void FillBuffer(JTable table) {
        // Seta Cabecario
        TableControl.header(tvbBuffer, new String[][] {{"id","Buffer"},{"0","500"}});    

        String sSql = "SELECT autoid, buffer FROM COMBOBUFFER WHERE UPPER(TRIM(local)) = 'DESPES' ORDER BY Lower(buffer);";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (imResult.next()) {
                String tid = String.valueOf(imResult.getInt("autoid"));
                String tbuffer = imResult.getString("buffer").toUpperCase();

                TableControl.add(table, new String[][]{{tid, tbuffer},{"C","L"}}, true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txbGrupo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tvbBuffer = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        txbValor = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jVias = new javax.swing.JCheckBox();
        jpRecebe = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txbDesc = new javax.swing.JTextArea();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Despesas ::.");
        setFont(new java.awt.Font("DejaVu Serif", 0, 10)); // NOI18N
        setVisible(true);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/stop.png"))); // NOI18N
        jLabel1.setText("Grupo:");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });

        txbGrupo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txbGrupoFocusLost(evt);
            }
        });
        txbGrupo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txbGrupoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbGrupoKeyReleased(evt);
            }
        });

        tvbBuffer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tvbBuffer.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tvbBuffer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tvbBufferMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tvbBuffer);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txbGrupo)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(txbGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        txbValor.setForeground(new java.awt.Color(153, 0, 51));
        txbValor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txbValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txbValor.setText("0,00");
        txbValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txbValorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txbValorFocusLost(evt);
            }
        });

        jLabel2.setText("Valor:");

        jVias.setText("2as Vias");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jVias)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 211, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txbValor, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txbValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jVias))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpRecebe.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpRecebe.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
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

        jScrollPane2.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        txbDesc.setColumns(20);
        txbDesc.setLineWrap(true);
        txbDesc.setRows(5);
        txbDesc.setWrapStyleWord(true);
        txbDesc.setBorder(javax.swing.BorderFactory.createTitledBorder("Descrição"));
        jScrollPane2.setViewportView(txbDesc);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane2)))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txbValorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txbValorFocusGained
        tRec.acao = "DP"; tRec.operacao = "DEB"; tRec.LimpaTransicao();
        jResto.setValue(0); tRec.vrAREC = 0;
        tRec.btEnabled(false); btnLancar.setEnabled(false); btnCancelar.setEnabled(false);        
    }//GEN-LAST:event_txbValorFocusGained

    private void txbValorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txbValorFocusLost
        float iValor = LerValor.StringToFloat(txbValor.getText());
        jResto.setValue(iValor);
        tRec.vrAREC = LerValor.StringToFloat(jResto.getText());

        tRec.rgimv = ""; tRec.rgprp = ""; tRec.contrato = ""; tRec.acao = "DP"; tRec.operacao = "DEB";
        txbValor.setEnabled(false);
        tRec.btEnabled(true);
        btnCancelar.setEnabled(true);
    }//GEN-LAST:event_txbValorFocusLost

    private void txbGrupoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbGrupoKeyPressed

    }//GEN-LAST:event_txbGrupoKeyPressed

    private void txbGrupoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbGrupoKeyReleased
        int Pos = TableControl.seek(tvbBuffer, 1, txbGrupo.getText().toUpperCase().trim());
        if (Pos > -1) {tvbBuffer.addRowSelectionInterval(Pos,Pos);} else tvbBuffer.removeRowSelectionInterval(0, tvbBuffer.getRowCount() - 1);
    }//GEN-LAST:event_txbGrupoKeyReleased

    private void txbGrupoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txbGrupoFocusLost
        if (tvbBuffer.getSelectedRow() > -1) {
            txbGrupo.setText(tvbBuffer.getValueAt(tvbBuffer.getSelectedRow(), 1).toString().trim());
        } else {
            jAutoriza uAut = new jAutoriza(null, true);
            boolean ecaixa = VariaveisGlobais.funcao.trim().toUpperCase().equalsIgnoreCase("CAIXA");
            boolean pode = false;
            boolean jatem = TableControl.seek(tvbBuffer, 1, txbGrupo.getText().trim().toUpperCase()) > -1 ? true : false;
            if (ecaixa) {
                uAut.setVisible(true);
                pode = uAut.pode;

                if (pode && !jatem) {
                    if (!"".equals(txbGrupo.getText().trim())) {
                        String sql = "INSERT INTO COMBOBUFFER (buffer, local) VALUES ('&1.','&2.')";
                        sql = FuncoesGlobais.Subst(sql, new String[] {txbGrupo.getText().trim().toUpperCase(),"DESPES"});
                        conn.ExecutarComando(sql);
                        FillBuffer(tvbBuffer);
                        tvbBuffer.addRowSelectionInterval(tvbBuffer.getRowCount() - 1,tvbBuffer.getRowCount() - 1);
                        jatem = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Selecione um grupo!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        txbGrupo.setText("");
                        txbGrupo.requestFocus();
                    }
                } else {
                    if (!"".equals(txbGrupo.getText().trim())) {
                        JOptionPane.showMessageDialog(null, "Usuário não pode criar grupo!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        txbGrupo.setText("");
                        txbGrupo.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(null, "Selecione um grupo!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        txbGrupo.setText("");
                        txbGrupo.requestFocus();
                    }                    
                }
            }
            if (!jatem && (pode || !ecaixa) ) {
                if (!"".equals(txbGrupo.getText().trim())) {
                    String sql = "INSERT INTO COMBOBUFFER (buffer, local) VALUES ('&1.','&2.')";
                    sql = FuncoesGlobais.Subst(sql, new String[] {txbGrupo.getText().trim().toUpperCase(),"DESPES"});
                    conn.ExecutarComando(sql);
                    FillBuffer(tvbBuffer);
                    tvbBuffer.addRowSelectionInterval(tvbBuffer.getRowCount() - 1,tvbBuffer.getRowCount() - 1);
                    jatem = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um grupo!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    txbGrupo.setText("");
                    txbGrupo.requestFocus();
                }
            }
            uAut = null;
            
//            if ("master".equals(VariaveisGlobais.funcao)) {
//                if (!"".equals(txbGrupo.getText().trim())) {
//                    String sql = "INSERT INTO COMBOBUFFER (buffer, local) VALUES ('&1.','&2.')";
//                    sql = FuncoesGlobais.Subst(sql, new String[] {txbGrupo.getText().trim().toUpperCase(),"DESPES"});
//                    conn.ExecutarComando(sql);
//                    FillBuffer(tvbBuffer);
//                    tvbBuffer.addRowSelectionInterval(tvbBuffer.getRowCount() - 1,tvbBuffer.getRowCount() - 1);
//                } else {
//                    JOptionPane.showMessageDialog(null, "Selecione um grupo!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
//                    txbGrupo.setText("");
//                    txbGrupo.requestFocus();
//                    return;                    
//                }
//            } else {
//                if (!"".equals(txbGrupo.getText().trim())) {
//                    JOptionPane.showMessageDialog(null, "Usuário não pode criar grupo!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
//                    txbGrupo.setText("");
//                    txbGrupo.requestFocus();
//                    return;
//                } else {
//                    JOptionPane.showMessageDialog(null, "Selecione um grupo!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
//                    txbGrupo.setText("");
//                    txbGrupo.requestFocus();
//                    return;                    
//                }
//            }
        }
        
        txbDesc.setText("");
        txbDesc.requestFocus();
    }//GEN-LAST:event_txbGrupoFocusLost

    private void tvbBufferMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tvbBufferMouseClicked
        txbGrupo.setText(tvbBuffer.getValueAt(tvbBuffer.getSelectedRow(), 1).toString().trim().toUpperCase());
    }//GEN-LAST:event_tvbBufferMouseClicked

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseReleased
        if (tvbBuffer.getSelectedRow() > -1) {
            String dDesp = tvbBuffer.getValueAt(tvbBuffer.getSelectedRow(), 1).toString().trim();
            String sql = "DELETE FROM COMBOBUFFER WHERE Upper(local) = 'DESPES' AND Upper(buffer) = '" + dDesp + "';";
            conn.ExecutarComando(sql);
            TableControl.del(tvbBuffer, tvbBuffer.getSelectedRow());
        }
    }//GEN-LAST:event_jLabel1MouseReleased

    public void Imprimir() throws SQLException {
        float nAut = 0;
        float vias = (jVias.isSelected() ? 2 : 1);
        
        String[][] aTrancicao = tRec.Transicao("DP");
        if (aTrancicao.length <= 0 ) return;
        
        // Autenticacao
        nAut = (float)Autenticacao.getAut();
        if (!Autenticacao.setAut((double)nAut, 1)) {
            JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        //nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));
        //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

        for (int i=1;i<=vias;i++) {
            if (VariaveisGlobais.Despesas.split(",")[1].equalsIgnoreCase("TXT")) {
                ImprimeDespesas(nAut, aTrancicao, txbDesc.getText(), txbGrupo.getText(), txbValor.getText(), "F");
            } else {
                //ImprimeDespesasThermica(nAut, aTrancicao, txbDesc.getText(), txbGrupo.getText(), txbValor.getText(), "F");
                ImprimeDespesasPDF(nAut, aTrancicao, txbDesc.getText(), txbGrupo.getText(), txbValor.getText(), "F");
            }
        }
        
        // Gravacao
        String sql = "INSERT INTO DESPESAS (grupo, item, valor, data, autentica) VALUES ('&1.','&2.','&3.','&4.','&5.')";
        sql = FuncoesGlobais.Subst(sql, 
                new String[] {tvbBuffer.getValueAt(tvbBuffer.getSelectedRow(), 0).toString().trim(),
                txbDesc.getText().trim(), String.valueOf(txbValor.getValue()), Dates.DateFormata("yyyy-MM-dd", new Date()),
                FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
        conn.ExecutarComando(sql);
        
        // grava no caixa
        conn.LancarCaixa(new String[] {tvbBuffer.getValueAt(tvbBuffer.getSelectedRow(), 0).toString().trim(),
                                       "", ""}, aTrancicao,String.valueOf((int)nAut).replace(",0", ""));
        
        String sVar = "DP:9:" + FuncoesGlobais.GravaValor(txbValor.getText()) + ":000000:DP:" +
                FuncoesGlobais.StrZero(String.valueOf(nAut),6) + ":" + Dates.DateFormata("yyyy-MM-dd", new Date()) +
                ":DEB:" + FuncoesGlobais.CriptaNome(txbDesc.getText()) + ":" + VariaveisGlobais.usuario;
        String tmpTexto = "INSERT INTO auxiliar (conta, contrato, campo, dtvencimento, dtrecebimento, rc_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.');";
        tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"DES",
                    tvbBuffer.getValueAt(tvbBuffer.getSelectedRow(), 0).toString().trim(),
        sVar, Dates.DateFormata("yyyy-MM-dd", new Date()), Dates.DateFormata("yyyy-MM-dd", new Date()),
        FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
        conn.ExecutarComando(tmpTexto);
        
        tRec.LimpaTransicao();
        jResto.setValue(0);
        tRec.vrAREC = 0;
        txbValor.setValue(0);
        txbValor.setEnabled(true);
        
        tRec.Enable(false);
        tRec.btEnabled(false);
        btnLancar.setEnabled(false);
        btnCancelar.setEnabled(false);                

        txbDesc.setText("");
        txbGrupo.setText("");
        txbGrupo.requestFocus();
    }
    
    public void ImprimeDespesas(float nAut, String[][] Valores, String texto, String nmDespesa, String ValorRec, String cutPaper) {
      int i = 0;
      Collections gVar = VariaveisGlobais.dCliente;

        String docID = "_" + Dates.DateFormata("dd-MM-yyyy", new Date()) + ".smc";
        String docName = "reports/Recibos/" + "DP_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;      
        PrinterPOS printer = new PrinterPOS(docName);
        printer.Print(JavaPOS.ESC_ARROBA + "", 1);
        //printer.setDocName(docName);

        // Imprime logomarca
        ////////////////printer.PrintBitMap("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif",0);
        //////////////printer.Print("", 1);

        // cabeçário
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_E(1) + gVar.get("empresa"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "Tel/Fax:" + gVar.get("telefone") + JavaPOS.ESC_E(0), 1);
        printer.Print("", 1);

        printer.Print(JavaPOS.ESCLAMATION(70) + JavaPOS.ESC_A(1) + JavaPOS.REVERSO + "  D E S P E S A S  " + JavaPOS.NORMAL, 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "", 1);

        // Dados do aviso
        printer.Print(JavaPOS.ESCLAMATION(65) + "CAIXA: " + VariaveisGlobais.usuario + "                 Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()) ,1);

        printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);
        printer.Print(JavaPOS.ESCLAMATION(65) + nmDespesa,1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "",1);

        // imprime aviso aqui
        String[] aLinhas = WordWrap.wrap(texto, 270, getFontMetrics(new java.awt.Font("SansSerif",Font.NORMAL,10))).split("\n");
        for (int k=0;k<aLinhas.length;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + aLinhas[k], 1); }

        printer.Print("",1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(2) + "Total da Despesa....................  ",0);
        printer.Print(JavaPOS.ESCLAMATION(65) + ValorRec,1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "",1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);

        if (nAut > 0) {
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) + "VALOR(ES) LANCADOS" + JavaPOS.NORMAL, 1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) +  "--------------------------------------------------------" + JavaPOS.NORMAL,1);

            for (i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

                printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(2) + bLinha, 1);
            }

            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);

            // Imprimir Autenticação
            printer.Print(JavaPOS.ESC_A(1) + JavaPOS.ESCLAMATION(65) + JavaPOS.REVERSO + VariaveisGlobais.dCliente.get("marca").trim() + JavaPOS.NORMAL + "DP" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,1);
            // Codigo de barra
            //////////////printer.PrintBarCode(FuncoesGlobais.StrZero(String.valueOf((int)nAut), 12));
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + "", 1); }

        if ("P".equals(cutPaper.trim().toUpperCase())) { printer.Print(JavaPOS.PARTCUT, 0); } else { printer.Print(JavaPOS.FULLCUT, 0); }

        //printer.Close();
        new toPrint(docName, VariaveisGlobais.Despesas.split(",")[0],VariaveisGlobais.Despesas.split(",")[1],VariaveisGlobais.Despesas.split(",")[2]);
        printer.setDocName("");
    }   

    public void ImprimeDespesasPDF(float nAut, String[][] Valores, String texto, String nmDespesa, String ValorRec, String cutPaper) {
        float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();

        String docID = "_" + Dates.DateFormata("dd-MM-yyyy", new Date()) + ".pdf";
        String docName = "DP_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
        String pathName = "reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
        pdf.setPathName(pathName);
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
        p = pdf.print("D E S P E S A S", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
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
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
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
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + "DS" +
                    FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + 
                    Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + 
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
        new toPrint(pathName + docName, VariaveisGlobais.Despesas.split(",")[0],VariaveisGlobais.Despesas.split(",")[1],VariaveisGlobais.Despesas.split(",")[2]);
        pdf.setPathName("");
        pdf.setDocName("");
    }
 
    public void ImprimeDespesasThermica(float nAut, String[][] Valores, String texto, String nmDespesa, String ValorRec, String cutPaper) {
        Object[][] linhas = {};
        jThermica Printer = new jThermica(linhas);
        int i = 0;
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
                                {"D E S P E S A S",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,10), Color.WHITE, Color.BLACK}
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
                            {"Total da Despesa",100,1,new java.awt.Font("Times New Roman",java.awt.Font.BOLD,7), Color.BLACK, Color.WHITE},
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

            for (i=0;i<Valores.length;i++) {
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

//            tmp = new Object[][] {
//                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
//                                };
//            linhas =  Printer.adcLine(linhas, tmp);

            // Imprimir Autenticação
            tmp = new Object[][] {
                                    {VariaveisGlobais.dCliente.get("marca").trim() + "DS" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,null,0,new java.awt.Font("Arial",java.awt.Font.BOLD,7), Color.BLACK, Color.WHITE}
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

        String docID = "_" + Dates.DateFormata("dd-MM-yyyy", new Date()) + ".smc";
        String docName = "reports/Recibos/" + "DP_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;

        jThermica np = new jThermica(linhas);
        np.setDocName(docName);
        np.toSave();
//        np.toPrint();
        np.setDocName("");
    }   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox jVias;
    private javax.swing.JPanel jpRecebe;
    private javax.swing.JTable tvbBuffer;
    private javax.swing.JTextArea txbDesc;
    private javax.swing.JTextField txbGrupo;
    private javax.swing.JFormattedTextField txbValor;
    // End of variables declaration//GEN-END:variables
}
