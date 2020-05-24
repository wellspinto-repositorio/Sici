/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jBloqueio.java
 *
 * Created on 27/06/2011, 14:17:05
 */
package Movimento;

import Funcoes.*;
import Protocolo.Calculos;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ComboBoxEditor;
import javax.swing.JTable;

/**
 *
 * @author supervisor
 */
public class jBloqueio extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    
    /** Creates new form jBloqueio */
    public jBloqueio() {
        initComponents();
        
        FillCombos();
        AutoCompletion.enable(jCodigo);
        AutoCompletion.enable(jNomeProp);
        
        ComboBoxEditor edit = jNomeProp.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                FillBloqueados(jRecibos, jCodigo.getSelectedItem().toString());
                FillAviBloqueados(jAvisos, jCodigo.getSelectedItem().toString());
                jAvisos.requestFocus();
            }
            
            public void focusGained(java.awt.event.FocusEvent evt) {
            }
        });        
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
    
//    private void ListaBloq(JTable table, String tProp) {
//        // Seta Cabecario
//        TableControl.header(table, new String[][] {{"tag","rgprp","rgimv","contrato","inquilino","vencimento","recebimento","valor"},{"30","60","60","60","250","130","130","100"}});
//
//        String sSql = "SELECT contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, tag FROM extrato WHERE rgprp = '&1.' AND tag <> 'X' ORDER BY dtvencimento;";
//        sSql = FuncoesGlobais.Subst(sSql, new String[] {tProp});
//        
//        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
//
//        try {
//            while (imResult.next()) {
//                String ttag = imResult.getString("tag");
//                String trgprp = String.valueOf(imResult.getInt("rgprp"));
//                String trgimv = String.valueOf(imResult.getInt("rgimv"));
//                String tcontrato = String.valueOf(imResult.getInt("contrato"));
//                String tinq = conn.LerCamposTabela(new String[] {"nomerazao"}, "locatarios", "contrato = '" + tcontrato + "'")[0][3];
//                String tvecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtvencimento"));
//                String trecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtrecebimento"));
//                
//                float vrrecibo = CalcularRecibo(trgprp, trgimv, tcontrato, tvecto, true);
//                String tvalor = LerValor.floatToCurrency(vrrecibo, 2);
//                
//                TableControl.add(table, new String[][]{{ttag, trgprp, trgimv, tcontrato, tinq, tvecto, trecto, tvalor},{"C","C","C","C","L","C","C","R"}}, true);
//
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        DbMain.FecharTabela(imResult);
//    }
    
    private void FillBloqueados(JTable table, String tProp) {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"tag","rgprp","rgimv","contrato","inquilino","vencimento","recebimento","valor"},{"30","60","60","60","250","130","130","100"}});

        String sSql = "SELECT contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, tag FROM extrato WHERE rgprp = '&1.' AND tag <> 'X' ORDER BY rgimv;";
        sSql = FuncoesGlobais.Subst(sSql, new String[] {jCodigo.getSelectedItem().toString()});
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        float fTotCred = 0; float fTotDeb = 0; float fSaldoAnt = 0;
        String inq = "";
        try {
            while (imResult.next()) {
                String ttag = imResult.getString("tag");
                String tmpCampo = imResult.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);
                fTotCred = 0; fTotDeb = 0; fSaldoAnt = 0;
                for (int j = 0; j<rCampos.length; j++) {
                    boolean bRetc = FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1;
                    if ("AL".equals(rCampos[j][4])) {
                        if (LerValor.isNumeric(rCampos[j][0])) {
                            inq = new Pad(conn.LerCamposTabela(new String[] {"nomerazao"}, "locatarios", "contrato = '" + imResult.getString("contrato") + "'")[0][3],18).RPad();
                                         //Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtvencimento")) + " - " + Dates.DateFormata("dd/MM/yyyy", hrs.getDate("dtrecebimento")) + "]";
                            
                            fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));
                            if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        } else {
                            if (bRetc) {fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2));}
                        }
                    } 
                }
                
                String trgprp = String.valueOf(imResult.getInt("rgprp"));
                String trgimv = String.valueOf(imResult.getInt("rgimv"));
                String tcontrato = String.valueOf(imResult.getInt("contrato"));
                String tinq = inq;
                String tvecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtvencimento"));
                String trecto = Dates.DateFormata("dd/MM/yyyy", imResult.getDate("dtrecebimento"));
                String tvalor = LerValor.floatToCurrency(fTotCred - fTotDeb,2);
                TableControl.add(table, new String[][]{{ttag, trgprp, trgimv, tcontrato, tinq, tvecto, trecto, tvalor},{"C","C","C","C","L","C","C","R"}}, true);
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }
        
    private void FillAviBloqueados(JTable table, String tProp) {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"tag","rgprp","aviso","valor","tp", "aut"},{"20","60","350","80","20","0"}});

        String sSql = "SELECT tag, registro, campo, autenticacao FROM avisos WHERE registro = '&1.' AND (tag <> 'X' OR ISNULL(tag)) AND rid = '0' ORDER BY autenticacao;";
        sSql = FuncoesGlobais.Subst(sSql, new String[] {jCodigo.getSelectedItem().toString()});
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        float fTotCred = 0; float fTotDeb = 0;
        try {
            while (imResult.next()) {
                String tmpCampo = "" + imResult.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);
                String sinq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" + rCampos[0][7].substring(2,4) + "/" + rCampos[0][7].substring(4);
                fTotCred = 0; fTotDeb = 0;
                if ("CRE".equals(rCampos[0][8])) {
                    fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2],2));
                } else {
                    fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2],2));
                }
                String tregistro = imResult.getString("registro");
                String tinq = sinq;
                String tvalor = LerValor.floatToCurrency(fTotCred - fTotDeb,2);
                String ttipo = ("CRE".equals(rCampos[0][8]) ? "C" : "D");
                String taut = imResult.getString("autenticacao");
                String ttag = imResult.getString("tag");
                TableControl.add(table, new String[][]{{ttag, tregistro, tinq, tvalor, ttipo, taut},{"C","C","L","R","C","C"}}, true);
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }

    private float CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto, boolean soAluguel) {
        String rcampo = "";
        boolean executando = false; boolean mCartVazio = false;

        // campos de calculos
        float exp = 0;
        float multa = 0;
        float juros = 0;
        float correcao = 0;

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
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
            ex.printStackTrace();
        }
        DbMain.FecharTabela(pResult);

        try {
            exp = rc.TaxaExp(campo);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            multa = rc.Multa(campo, vecto, false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            juros = rc.Juros(campo, vecto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            correcao = rc.Correcao(campo, vecto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        return tRecibo + (soAluguel ? 0 : exp + multa + juros + correcao);
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
        jCodigo = new javax.swing.JComboBox();
        jNomeProp = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jAvisos = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jRecibos = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Bloqueia recebimento no Extrato do Proprietário ::.");
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

        jAvisos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jAvisos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jAvisos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jAvisosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jAvisos);

        jLabel2.setBackground(java.awt.Color.orange);
        jLabel2.setFont(new java.awt.Font("Ubuntu", 3, 15)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("R E C I B O S");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
        jLabel2.setOpaque(true);

        jLabel3.setBackground(java.awt.Color.orange);
        jLabel3.setFont(new java.awt.Font("Ubuntu", 3, 15)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("A V I S O S");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
        jLabel3.setOpaque(true);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNomeProp, 0, 437, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCodigoActionPerformed
        int pos = jCodigo.getSelectedIndex();
        if (jNomeProp.getItemCount() > 0) jNomeProp.setSelectedIndex(pos);
    }//GEN-LAST:event_jCodigoActionPerformed

    private void jNomePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomePropActionPerformed
        int pos = jNomeProp.getSelectedIndex();
        if (jCodigo.getItemCount() > 0) jCodigo.setSelectedIndex(pos);
    }//GEN-LAST:event_jNomePropActionPerformed

    private void jAvisosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jAvisosMouseClicked
        int col = jAvisos.getSelectedColumn();
        int row = jAvisos.getSelectedRow();
        
        if (col == 0) {
            String sTag;
            try {
                sTag = ("B".equals(jAvisos.getModel().getValueAt(row, 0).toString()) ? "" : "B");
            } catch (NullPointerException ex) { sTag = "B"; }
            
            String sql = "UPDATE avisos SET tag = '&1.' WHERE registro = '&2.' AND autenticacao = '&3.'";
            sql = FuncoesGlobais.Subst(sql, new String[] {
                    sTag,
                    jAvisos.getModel().getValueAt(row, 1).toString(),
                    jAvisos.getModel().getValueAt(row, 5).toString()});
            if (conn.ExecutarComando(sql) > 0) {
                jAvisos.getModel().setValueAt(sTag, row, 0);
            }
        }
    }//GEN-LAST:event_jAvisosMouseClicked

    private void jRecibosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRecibosMouseClicked
        int col = jRecibos.getSelectedColumn();
        int row = jRecibos.getSelectedRow();
        
        if (col == 0) {
            String sTag = ("B".equals(jRecibos.getModel().getValueAt(row, 0).toString()) ? "" : "B");
            String sql = "UPDATE extrato SET tag = '&1.' WHERE contrato = '&2.' AND dtvencimento = '&3.'";
            sql = FuncoesGlobais.Subst(sql, new String[] {
                    sTag,
                    jRecibos.getModel().getValueAt(row, 3).toString(),
                    Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(jRecibos.getModel().getValueAt(row, 5).toString(),"dd/MM/yyyy"))});
            if (conn.ExecutarComando(sql) > 0) {
                jRecibos.getModel().setValueAt(sTag, row, 0);
            }
        }
    }//GEN-LAST:event_jRecibosMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable jAvisos;
    private javax.swing.JComboBox jCodigo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JTable jRecibos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
