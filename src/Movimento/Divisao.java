/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Divisao.java
 *
 * Created on 16/02/2011, 15:20:07
 */

package Movimento;

import Funcoes.AutoCompletion;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.jTableControl;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import javax.swing.ComboBoxEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author supervisor
 */
public class Divisao extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    
    String rgprp = ""; String rgimv = ""; String contrato = "";
    boolean bExecNome = false, bExecCodigo = false;
    boolean bExecNomeb = false, bExecCodigob = false;

    jTableControl tabela = new jTableControl(true);
    jTableControl tabela2 = new jTableControl(true);
    
    /** Creates new form Divisao */
    public Divisao() {
        initComponents();
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);
        
        jLancar.setEnabled(false);
        
        FillCombos(false);
        AutoCompletion.enable(jRgprp);
        AutoCompletion.enable(jNomeProp);
        
        AutoCompletion.enable(bRgprp);
        AutoCompletion.enable(bNomeProp);

        ComboBoxEditor edit1 = jRgprp.getEditor();
        Component comp1 = edit1.getEditorComponent();
        comp1.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                // limpar
                TableControl.header(jtblImvPrinc, new String[][] {{"rgimv","endereco", "%","d","benefs","bnfs"},{"100","500","100","30","0","0"}});
                TableControl.header(jBenefs, new String[][] {{"rgprp","nome", "%"},{"100","500","100"}});
            }
        });

        ComboBoxEditor edit = jNomeProp.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rgprp = jRgprp.getSelectedItem().toString(); //rgimv = tPag.rgimv; contrato = tPag.contrato;
                FillImoveis(jtblImvPrinc, rgprp);
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                // Limpar
                TableControl.header(jtblImvPrinc, new String[][] {{"rgimv","endereco", "%","d","benefs","bnfs"},{"100","500","100","30","0","0"}});
                TableControl.header(jBenefs, new String[][] {{"rgprp","nome", "%"},{"100","500","100"}});
            }
        });
    }

    private void CleanTable(JTable table) {
        
    }
    
    private void FillCombos(boolean Depositos) {
        String sSql = "";
        if (!Depositos) {
            sSql = "SELECT distinct p.rgprp, p.nome FROM proprietarios p WHERE Upper(p.status) = 'ATIVO' ORDER BY p.nome;";
        } else {
            sSql = "SELECT DISTINCT e.rgprp, p.nome AS nome FROM extrato e, proprietarios p WHERE (Upper(p.status) = 'ATIVO') AND e.rgprp = p.rgprp AND TRIM(p.conta) <> '' AND tag <> 'X' ORDER BY Lower(p.nome);";
        }
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        jRgprp.removeAllItems();
        jNomeProp.removeAllItems();
        try {
            while (imResult.next()) {
                jRgprp.addItem(String.valueOf(imResult.getInt("rgprp")));
                bRgprp.addItem(String.valueOf(imResult.getInt("rgprp")));
                jNomeProp.addItem(imResult.getString("nome"));
                bNomeProp.addItem(imResult.getString("nome"));
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
        jRgprp = new javax.swing.JComboBox();
        jNomeProp = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblImvPrinc = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pDiv = new javax.swing.JFormattedTextField();
        jLancar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jBenefs = new javax.swing.JTable();
        bRgprp = new javax.swing.JComboBox();
        bNomeProp = new javax.swing.JComboBox();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Divisão de Conta Corrente ::.");
        setMaximumSize(new java.awt.Dimension(533, 357));
        setVisible(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Principal e seus imóveis", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 0, 12), new java.awt.Color(0, 51, 153))); // NOI18N

        jLabel1.setText("Codigo");

        jRgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRgprpActionPerformed(evt);
            }
        });

        jNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomePropActionPerformed(evt);
            }
        });

        jLabel2.setText("Proprietário");

        jtblImvPrinc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jtblImvPrinc.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtblImvPrinc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtblImvPrincMouseClicked(evt);
            }
        });
        jtblImvPrinc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtblImvPrincKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jtblImvPrinc);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jNomeProp, 0, 433, Short.MAX_VALUE)
                            .addComponent(jLabel2))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Beneficiários", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 0, 12), new java.awt.Color(0, 204, 0))); // NOI18N

        jLabel3.setText("Código");

        jLabel4.setText("Nome");

        pDiv.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        pDiv.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        pDiv.setText("0,00");
        pDiv.setToolTipText("");
        pDiv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                pDivFocusLost(evt);
            }
        });

        jLancar.setText("Lançar");
        jLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLancarActionPerformed(evt);
            }
        });

        jBenefs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jBenefs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBenefsKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jBenefs);

        bRgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRgprpActionPerformed(evt);
            }
        });

        bNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNomePropActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(bNomeProp, 0, 289, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pDiv, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLancar)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pDiv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLancar))
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void bRgprpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRgprpActionPerformed
        if (!bExecNomeb) {
            int pos = bRgprp.getSelectedIndex();
            if (bNomeProp.getItemCount() > 0) {bExecCodigob = true; bNomeProp.setSelectedIndex(pos); bExecCodigob = false;}
        }
    }//GEN-LAST:event_bRgprpActionPerformed

    private void bNomePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNomePropActionPerformed
        if (!bExecCodigob) {
            int pos = bNomeProp.getSelectedIndex();
            if (bRgprp.getItemCount() > 0) {bExecNomeb = true; bRgprp.setSelectedIndex(pos); bExecNomeb = false; }
        }
    }//GEN-LAST:event_bNomePropActionPerformed

    private void jtblImvPrincMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtblImvPrincMouseClicked
        if (jtblImvPrinc.columnAtPoint(evt.getPoint()) == 3) {
            String uSql = "UPDATE divisao set divtudo = '&1.' WHERE rgprp = '&2.' AND rgimv = '&3.'";
            String studo = ("true".equalsIgnoreCase(jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 3).toString().toLowerCase()) ? "1" : "0");
            uSql = FuncoesGlobais.Subst(uSql, new String[] {studo, rgprp, jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 0).toString()});
            try {conn.ExecutarComando(uSql);} catch (Exception e) {}
        }      
        
        FillBenefs();
    }//GEN-LAST:event_jtblImvPrincMouseClicked

    private void FillBenefs() {
        float tPerc = Float.valueOf(jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 2).toString().replace(",", "."));
        
        podelancar(tPerc > 0);
        
        String cSql = "select p.rgprp, p.nome, d.benefs from proprietarios p, divisao d " + (!jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 4).toString().trim().equals("") ? "Where (" + 
                jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 4).toString().trim() + ") AND d.rgimv = " +
                jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 0).toString() : " Where IsNUll(p.rgprp)");
        

        Integer[] tam = {100,500,100};
        String[] col = {"rgprp","nome", "%"};
        Boolean[] edt = {false,false,false};
        String[] aln = {"C","L","C"};
        Object[][] data = {};

        ResultSet hResult = conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (hResult.next()) {
                String trgprp = hResult.getString("rgprp");
                String tnome = hResult.getString("nome");
                String sPerc = "";
                
                String tbenefs = hResult.getString("benefs");
                
                int tPos1 = tbenefs.indexOf(trgprp.trim() + ":");
                int tPos2 = tbenefs.indexOf(";", tPos1);
                
                if (tPos2 > 0) {
                    tbenefs = tbenefs.substring(tPos1, tPos2 );
                    tPos1 = tbenefs.indexOf(":");
                    sPerc = tbenefs.substring(tPos1 + 1);
                } else {
                    if (tPos1 > 0) tbenefs = tbenefs.substring(tPos1);
                    tPos1 = tbenefs.indexOf(":");
                    sPerc = tbenefs.substring(tPos1 + 1);
                }

                Object[] dado = {trgprp, tnome, sPerc};
                data = tabela2.insert(data, dado);
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(hResult);
        tabela2.Show(jBenefs, data, tam, aln, col, edt);        
    }
    
    private void pDivFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pDivFocusLost
        float tPerc = Float.valueOf(jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 2).toString().replace(",", "."));
        float lPerc = Float.valueOf(pDiv.getText().trim().replace(",", ".")); 
        jLancar.setEnabled(lPerc <= tPerc && lPerc > 0);
        jLancar.requestFocus();
    }//GEN-LAST:event_pDivFocusLost

    private void jLancarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLancarActionPerformed
        String[][] aBenefs = {}; String cSql = "";
        try {
            aBenefs = conn.LerCamposTabela(new String[] {"benefs", "divtudo"}, "divisao", "rgprp = '" + rgprp + "' AND rgimv = '" +
                jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 0).toString().trim() + "'");
        } catch (Exception e) {}
        if (aBenefs != null) {
            if (!aBenefs[0][3].trim().equals("")) {
                cSql = "UPDATE divisao SET benefs = CONCAT(benefs,';','"  + bRgprp.getSelectedItem().toString().trim() +
                            ":" + pDiv.getText().trim() + "') WHERE rgprp = '" + rgprp + "' AND rgimv = '" + 
                            jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 0).toString().trim() + "'";
            } else {
                cSql = "INSERT INTO divisao (rgprp, rgimv, benefs) VALUES ('" + rgprp + "','" +
                        jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 0).toString().trim() +
                        "','" + bRgprp.getSelectedItem().toString().trim() + ":" + pDiv.getText().trim() + "')";
            }
        } else {
            cSql = "INSERT INTO divisao (rgprp, rgimv, benefs, divtudo) VALUES ('" + rgprp + "','" +
                    jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 0).toString().trim() +
                    "','" + bRgprp.getSelectedItem().toString().trim() + ":" + pDiv.getText().trim() + "'," +
                    "'" + ((Boolean)jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 3) ? "1" : "0") + "')";
        }
        
        if (!cSql.trim().equals("")) {
            try {
                conn.ExecutarComando(cSql);
            } catch (Exception e) {}
        }
        
        pDiv.setText("0,00");
        podelancar(false);
        //FillBenefs();
        jRgprp.requestFocus();
    }//GEN-LAST:event_jLancarActionPerformed

    private void jtblImvPrincKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtblImvPrincKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            rgimv = jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 0).toString().trim();
            Object[] options = { "Sim", "Não" };
            int n = JOptionPane.showOptionDialog(null,
                "Deseja excluir a divisão do imovel " + rgimv + " ? ",
                "Atenção", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (n == JOptionPane.YES_OPTION) {
                try { conn.ExecutarComando("DELETE FROM divisao Where rgimv = '" + rgimv +
                        "' AND rgprp = '" + rgprp + "'"); } catch (Exception e) {}
                jRgprp.requestFocus();
            }
        }
    }//GEN-LAST:event_jtblImvPrincKeyPressed

    private void jBenefsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBenefsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            rgimv = jtblImvPrinc.getModel().getValueAt(jtblImvPrinc.getSelectedRow(), 0).toString().trim();
            String bimv = jBenefs.getModel().getValueAt(jBenefs.getSelectedRow(), 0).toString().trim();
            String iperc = jBenefs.getModel().getValueAt(jBenefs.getSelectedRow(), 2).toString().trim();
            String rValue = bimv + ":" + iperc.trim();
            String cSQL = "UPDATE divisao SET benefs = REPLACE(benefs,'" + rValue + ";',''), benefs = REPLACE(benefs,'" + rValue + "','') WHERE rgprp = '" + rgprp + "' AND rgimv = '" + rgimv + "'";
            Object[] options = { "Sim", "Não" };
            int n = JOptionPane.showOptionDialog(null,
                "Deseja excluir beneficiario " + bimv + " da divisão ? ",
                "Atenção", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (n == JOptionPane.YES_OPTION) {
                try { 
                    conn.ExecutarComando(cSQL);
                    
                    // Exclui registro caso todas as divisoes tenham sido excluidas
                    if (IsNullBenef(rgprp, rgimv)) {
                        conn.ExecutarComando("DELETE FROM divisao Where rgimv = '" + rgimv +
                        "' AND rgprp = '" + rgprp + "'");
                    }
                } catch (Exception e) {}
                jRgprp.requestFocus();
            }
        }
    }//GEN-LAST:event_jBenefsKeyPressed

    private boolean IsNullBenef(String prop, String imov) {
        String[][] aBenefs = {}; boolean retorno = false;
        try {
            aBenefs = conn.LerCamposTabela(new String[] {"benefs", "divtudo"}, "divisao", "rgprp = '" + prop + "' AND rgimv = '" +
                imov + "'");
        } catch (Exception e) {}
        if (aBenefs != null) {
            if (aBenefs[0][3].trim().equals("")) {
                retorno = true;
            } else {
                retorno = false;
            }
        } else {
            retorno = true;
        }
        return retorno;
    }
    
    private void podelancar(boolean bpode) {
        jLancar.setEnabled(false);
        bRgprp.setEnabled(bpode);
        bNomeProp.setEnabled(bpode);
        pDiv.setEnabled(bpode);
        
        if (bpode) bRgprp.requestFocus();
    }
    
private void FillImoveis(JTable table, String rgprp) {
        Integer[] tam = {100,500,100,30,0,0};
        String[] col = {"rgimv","endereco", "%","d","benefs","bnfs"};
        Boolean[] edt = {false,false,false,true,false,false};
        String[] aln = {"C","L","C","","L","L"};
        Object[][] data = {};
        
        String sSql = "SELECT rgimv, CONCAT(Trim(end), ', ', Trim(num), ' ', Trim(compl)) AS endereco FROM imoveis WHERE rgprp = '" + rgprp + "' ORDER BY rgprp, rgimv;";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (imResult.next()) {
                String trgimv = String.valueOf(imResult.getInt("rgimv"));
                String tend = imResult.getString("endereco").trim();
                
                String[] sPerc = imvPerc(rgprp, trgimv);
                String tperc1 = sPerc[0];
                String tperc2 = sPerc[1];
                String tperc3 = sPerc[2];

                boolean ttudo = (sPerc[3].equals("0") ? false : true);
                
                Object[] dado = {trgimv, tend, tperc1, ttudo, tperc2, tperc3};
                data = tabela.insert(data, dado);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
        tabela.Show(table, data, tam, aln, col, edt);
    }

    private String[] imvPerc(String tRgprp, String tRgimv) {
        String tBenefs = ""; String tudo = "0";
        float r = 0; String b = "";
        
        try { 
            String[][] aBenefs = conn.LerCamposTabela(new String[] {"benefs", "divtudo"}, "divisao", "rgprp = '" + tRgprp + "' AND rgimv = '" + tRgimv + "'");
            tBenefs = aBenefs[0][3];
            tudo = aBenefs[1][3];
        } catch (Exception e) {}

        if (!tBenefs.trim().equals("")) {
            String[] tPt1 = tBenefs.split(";");
            for (int i=0;i<tPt1.length;i++) {
                if (!tPt1[i].trim().equals("")) {
                    r += Float.valueOf(tPt1[i].split(":")[1].replace(",", "."));
                    b += "p.rgprp = " + tPt1[i].split(":")[0] + " or ";
                }
            }
        }
        
        String rgbenef = "";
        try { rgbenef = b.substring(0, b.length() - 4); } catch (Exception e) {}
        
        return new String[] {LerValor.floatToCurrency((100 - r),2), rgbenef, tBenefs, tudo.trim()};
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox bNomeProp;
    private javax.swing.JComboBox bRgprp;
    private javax.swing.JTable jBenefs;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton jLancar;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox jRgprp;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jtblImvPrinc;
    private javax.swing.JFormattedTextField pDiv;
    // End of variables declaration//GEN-END:variables

}