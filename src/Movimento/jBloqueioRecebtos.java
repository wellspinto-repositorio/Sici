/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento;

import Funcoes.*;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import javax.swing.ComboBoxEditor;

/**
 *
 * @author supervisor
 */
public class jBloqueioRecebtos extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    boolean bExecNome = false, bExecCodigo = false, bExiste = false; 

    /**
     * Creates new form jBloqueioRecebtos
     */
    public jBloqueioRecebtos() {
        initComponents();

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        ComboBoxEditor edit = jNomeLoca.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jMotivo.setEnabled(true);
                jGravar.setEnabled(true);
                jBloqDesbloq.setEnabled(true);
                LerBloqueio(jContrato.getSelectedItem().toString().trim());
                jMotivo.requestFocus();
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                jMotivo.setText("");
                jMotivo.setEnabled(false);
                jGravar.setEnabled(false);
                jBloqDesbloq.setEnabled(false);
                jBloqDesbloq.setText("Bloquear");
            }
        });

        ComboBoxEditor editContrato = jContrato.getEditor();
        Component compContrato = editContrato.getEditorComponent();
        compContrato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                jMotivo.setText("");
                jMotivo.setEnabled(false);
                jGravar.setEnabled(false);
                jBloqDesbloq.setEnabled(false);
                jBloqDesbloq.setText("Bloquear");
            }
        });
        
        FillCombos();
        AutoCompletion.enable(jContrato);
        AutoCompletion.enable(jNomeLoca);
        
        new SimpleThread().start();
    }

    private void LerBloqueio(String contrato) {
        String sql = "SELECT historico, status FROM LocaBloq WHERE contrato = '&1.'";
        sql = FuncoesGlobais.Subst(sql, new String[] {contrato});
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        
        jMotivo.setText("");
        bExiste = false;
        try {
            while (rs.next()) {
                String motivo = rs.getString("historico");
                jMotivo.setText(motivo);
                String btTxt = (rs.getBoolean("status") ? "Desbloquear" : "Bloquear");
                jBloqDesbloq.setText(btTxt);
                bExiste = true;
            }
        } catch (Exception ex) {}
        DbMain.FecharTabela(rs);
    }
    
    class SimpleThread extends Thread {
        public SimpleThread() {
        }
        public void run() {
            ListaBloqueados();
        }
    }
    
    private void ListaBloqueados() {
        String sql = "SELECT contrato, nome, data FROM LocaBloq ORDER BY Lower(nome);";

        TableControl.Clear(jBloqueados);
        // Seta Cabecario
        TableControl.header(jBloqueados, new String[][] {{"contrato", "nome locatario", "data"},{"80","500","100"}});

        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            jbarra.setValue(0);
            int eof = DbMain.RecordCount(rs); int pos = 1;
            rs.beforeFirst();
            while (rs.next()) {
                int br = (pos++ * 100) / eof;
                jbarra.setValue(br + 1);
                
                String pcontrato = rs.getString("contrato");
                String pnomel = rs.getString("nome");
                String pdata = rs.getString("data");
                TableControl.add(jBloqueados, new String[][]{{pcontrato, pnomel, pdata},{"L","L","C"}}, true);
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        DbMain.FecharTabela(rs);
    }
    
    private void FillCombos() {
        String sSql = "SELECT contrato as codigo, UPPER(nomerazao) as nome FROM locatarios WHERE fiador1uf <> 'X' OR IsNull(fiador1uf) ORDER BY UPPER(nomerazao);";

        jContrato.removeAllItems();
        jNomeLoca.removeAllItems();
        if (!sSql.isEmpty()) {
            ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

            try {
                while (imResult.next()) {
                    jContrato.addItem(String.valueOf(imResult.getInt("codigo")));
                    jNomeLoca.addItem(imResult.getString("nome"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            DbMain.FecharTabela(imResult);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jContrato = new javax.swing.JComboBox();
        jNomeLoca = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jMotivo = new javax.swing.JTextArea();
        jBloqDesbloq = new javax.swing.JButton();
        jbarra = new javax.swing.JProgressBar();
        jGravar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jBloqueados = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Bloqueio de Recebimentos ::>");
        setVisible(true);

        jContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jContratoActionPerformed(evt);
            }
        });

        jNomeLoca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomeLocaActionPerformed(evt);
            }
        });

        jLabel1.setText("Contrato:");

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Motivo do Bloqueio"));

        jMotivo.setColumns(20);
        jMotivo.setLineWrap(true);
        jMotivo.setRows(5);
        jMotivo.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jMotivo);

        jBloqDesbloq.setText("Bloquear");
        jBloqDesbloq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBloqDesbloqActionPerformed(evt);
            }
        });

        jGravar.setText("Gravar Motivos");
        jGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGravarActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Locatários Bloqueados"));

        jBloqueados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jBloqueados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jBloqueados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jBloqueadosMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jBloqueados);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNomeLoca, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addComponent(jbarra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBloqDesbloq, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jGravar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBloqDesbloq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jContratoActionPerformed
        if (!bExecNome) {
            int pos = jContrato.getSelectedIndex();
            if (jNomeLoca.getItemCount() > 0) {
                bExecCodigo = true;
                jNomeLoca.setSelectedIndex(pos);
                bExecCodigo = false;
            }
        }
    }//GEN-LAST:event_jContratoActionPerformed

    private void jNomeLocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomeLocaActionPerformed
        if (!bExecCodigo) {
            int pos = jNomeLoca.getSelectedIndex();
            if (jContrato.getItemCount() > 0) {
                bExecNome = true;
                jContrato.setSelectedIndex(pos);
                bExecNome = false;
            }
        }
    }//GEN-LAST:event_jNomeLocaActionPerformed

    private void jGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGravarActionPerformed
        String sql = "";
        if (bExiste) {
            sql = "UPDATE LocaBloq SET historico = '&1.' WHERE contrato = '&2.';";
            sql = FuncoesGlobais.Subst(sql, new String[] {jMotivo.getText().trim(),
                  jContrato.getSelectedItem().toString().trim()});
        } else {
            sql = "INSERT INTO LocaBloq (contrato, nome, historico, status, data) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            sql = FuncoesGlobais.Subst(sql, new String[] {jContrato.getSelectedItem().toString().trim(),
                  jNomeLoca.getSelectedItem().toString().trim(),
                  jMotivo.getText().trim(),
                  (jBloqDesbloq.getText().trim().toLowerCase().equalsIgnoreCase("BLOQUEAR") ? "1" : "0"),
                  Dates.DateFormata("dd/MM/yyyy", new Date())});
        }
        conn.ExecutarComando(sql);
        
        new SimpleThread().start();
        jContrato.requestFocus();
    }//GEN-LAST:event_jGravarActionPerformed

    private void jBloqDesbloqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBloqDesbloqActionPerformed
        String sql = "";
        if (bExiste) {
            if (jBloqDesbloq.getText().trim().toLowerCase().equalsIgnoreCase("BLOQUEAR")) {
                sql = "UPDATE LocaBloq SET status = '&1.', historico = '&2.' WHERE contrato = '&3.';";
                sql = FuncoesGlobais.Subst(sql, new String[] {"1",
                      jMotivo.getText().trim(),
                      jContrato.getSelectedItem().toString().trim()});
                jBloqDesbloq.setText("Desbloquear");
            } else {
                sql = "DELETE FROM LocaBloq WHERE contrato = '&1.';";
                sql = FuncoesGlobais.Subst(sql, new String[] {jContrato.getSelectedItem().toString().trim()});                            
                jBloqDesbloq.setText("Bloquear");
            }
        } else {
            sql = "INSERT INTO LocaBloq (contrato, nome, historico, status, data) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            sql = FuncoesGlobais.Subst(sql, new String[] {jContrato.getSelectedItem().toString().trim(),
                  jNomeLoca.getSelectedItem().toString().trim(),
                  jMotivo.getText().trim(),
                  "1", Dates.DateFormata("dd/MM/yyyy", new Date())});            
            jBloqDesbloq.setText("Desbloquear");
        }
        conn.ExecutarComando(sql);
        
        new SimpleThread().start();
        jContrato.requestFocus();
    }//GEN-LAST:event_jBloqDesbloqActionPerformed

    private void jBloqueadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBloqueadosMouseClicked
        int pos = jBloqueados.getSelectedRow();
        if (pos > -1) {
            String tctro = jBloqueados.getModel().getValueAt(jBloqueados.getSelectedRow(), 0).toString().trim();
            jContrato.getModel().setSelectedItem(tctro);
            jMotivo.setEnabled(true);
            jGravar.setEnabled(true);
            jBloqDesbloq.setEnabled(true);
            LerBloqueio(jContrato.getSelectedItem().toString().trim());
            jMotivo.requestFocus();
        }
    }//GEN-LAST:event_jBloqueadosMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBloqDesbloq;
    private javax.swing.JTable jBloqueados;
    private javax.swing.JComboBox jContrato;
    private javax.swing.JButton jGravar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea jMotivo;
    private javax.swing.JComboBox jNomeLoca;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JProgressBar jbarra;
    // End of variables declaration//GEN-END:variables
}
