/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jSeguro.java
 *
 * Created on 25/02/2011, 14:02:06
 */

package Movimento;

import Funcoes.AutoCompletion;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Protocolo.DepuraCampos;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author supervisor
 */
public class jSeguro extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    int pos = 0;

    /** Creates new form jSeguro */
    public jSeguro() {
        initComponents();

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        FillCombos();

        AutoCompletion.enable(jContrato);
        AutoCompletion.enable(jNomeLoca);

        ComboBoxEditor edit = jNomeLoca.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                try {
                    FillSeg(tblSeguros);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void FillSeg(JTable table) throws SQLException {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"tp","nome","referencia","descrição","valor","campo","pos"},{"25","300","100","150","80","0","0"}});

        String mContrato; String mNome; String mCampo; String mVecto; String mDesc; String mValor;
        String sql = "SELECT r.contrato, l.nomerazao, r.campo, r.dtvencimento FROM RECIBO r, locatarios l WHERE (r.contrato = l.contrato) AND r.contrato = '" + jContrato.getSelectedItem().toString() + "' AND r.tag <> 'X' AND INSTR(r.campo,'SG:3:') > 0 ORDER BY r.dtvencimento;";
        ResultSet hRs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        while (hRs.next()) {
            mContrato = hRs.getString("r.contrato");
            mNome = hRs.getString("l.nomerazao");
            mCampo = hRs.getString("r.campo");
            mVecto = hRs.getString("r.dtvencimento");

            while (this.pos > -1) {
                String[] a = RetValorDif(mCampo, "SG");
                if (a != null) {
                    mDesc = a[0];
                    mValor = a[1];

                    TableControl.add(table, new String[][]{{"R", mNome, mVecto.substring(5, 7) + "/" + mVecto.substring(0, 4), "SEGURO", mValor, mCampo, String.valueOf(this.pos - 1)},{"C","L","C","L","R","L","L"}}, true);
                }
            }
            this.pos = 0;
        }
        DbMain.FecharTabela(hRs);

        sql = "SELECT d.contrato, l.nomerazao, d.valor, d.referencia FROM Seguros d, locatarios l WHERE (d.contrato = l.contrato) AND d.contrato = '" + jContrato.getSelectedItem().toString() + "' ORDER BY d.contrato, d.referencia";
        hRs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        while (hRs.next()) {
            mContrato = hRs.getString("d.contrato");
            mNome = hRs.getString("l.nomerazao");
            mVecto = hRs.getString("d.referencia");
            mDesc = "SEGURO";
            mValor = LerValor.FormataCurrency(hRs.getString("d.valor"));

            TableControl.add(table, new String[][]{{"D", mNome, mVecto, mDesc, mValor},{"C","L","C","L","R"}}, true);
        }
        DbMain.FecharTabela(hRs);

    }

    private String[] RetValorDif(String campo, String oQue) throws SQLException {
        DepuraCampos a = new DepuraCampos(campo);
        a.SplitCampos();
        // Ordena Matriz
        Arrays.sort (a.aCampos, new Comparator()
        {
        private int pos1 = 3;
        private int pos2 = 4;
        public int compare(Object o1, Object o2) {
            String p1 = ((String)o1).substring(pos1, pos2);
            String p2 = ((String)o2).substring(pos1, pos2);
            return p1.compareTo(p2);
        }
        });

        this.pos = FuncoesGlobais.IndexOfn(a.aCampos, oQue.trim().toUpperCase(), this.pos);
        String[] b = null;
        if (this.pos > -1) {
            b = a.Depurar(this.pos);
            this.pos += 1;
        }
        return b;
    }

    private void FillCombos() {
        String sSql = "SELECT contrato AS Conta, nomerazao AS nome FROM locatarios WHERE fiador1uf <> 'X' OR IsNull(fiador1uf) ORDER BY Lower(nomerazao);";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (imResult.next()) {
                jContrato.addItem(String.valueOf(imResult.getInt("Conta")));
                jNomeLoca.addItem(imResult.getString("nome"));
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

        jQtde = new javax.swing.JSpinner();
        jAno = new com.toedter.calendar.JYearChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSeguros = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLancar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jMes = new com.toedter.calendar.JMonthChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSair = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jNomeLoca = new javax.swing.JComboBox();
        jValor = new javax.swing.JFormattedTextField();
        jContrato = new javax.swing.JComboBox();
        jExtrato = new javax.swing.JCheckBox();
        jRetencao = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Lançamento de Seguros ::.");
        setVisible(true);

        jQtde.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jQtdeStateChanged(evt);
            }
        });

        jScrollPane1.setFont(new java.awt.Font("Tahoma", 0, 8));

        tblSeguros.setAutoCreateRowSorter(true);
        tblSeguros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblSeguros.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblSegurosKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblSeguros);

        jLabel1.setText("Contrato");

        jLancar.setMnemonic('L');
        jLancar.setText("Lançar");
        jLancar.setEnabled(false);
        jLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLancarActionPerformed(evt);
            }
        });

        jLabel2.setText("Nome");

        jLabel5.setText("Valor");

        jLabel6.setText("Qtde");

        jSair.setMnemonic('S');
        jSair.setText("Sair");
        jSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSairActionPerformed(evt);
            }
        });

        jLabel4.setText("Mês/Ano");

        jNomeLoca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomeLocaActionPerformed(evt);
            }
        });

        jValor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jValor.setText("0,00");

        jContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jContratoActionPerformed(evt);
            }
        });

        jExtrato.setSelected(true);
        jExtrato.setText("Enviar para o Extrato");

        jRetencao.setText("Enviar para Retenção");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(75, 75, 75))
                            .addComponent(jMes, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(jContrato, 0, 138, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(76, 76, 76)))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jAno, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jValor, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(219, 219, 219)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jQtde, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(80, 80, 80))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(357, 357, 357))
                            .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jExtrato)
                        .addComponent(jRetencao, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                        .addGap(35, 35, 35)
                        .addComponent(jLancar, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSair, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jContrato, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNomeLoca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jMes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jValor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jQtde, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jExtrato)
                        .addComponent(jRetencao))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLancar)
                        .addComponent(jSair)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jQtdeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jQtdeStateChanged
        jLancar.setEnabled(((jValor.getText().length() > 0 && FuncoesGlobais.strCurrencyToFloat(jValor.getText()) > 0) && Integer.parseInt(String.valueOf(jQtde.getValue())) > 0));
}//GEN-LAST:event_jQtdeStateChanged

    private void tblSegurosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSegurosKeyPressed
        int SelRow = tblSeguros.getSelectedRow();
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (SelRow >  -1) {
                Object[] options = { "Sim", "Não" };
                int n = JOptionPane.showOptionDialog(null,
                        "Deseja excluir este lançamento ? ",
                        "Atenção", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    if ("D".equals(tblSeguros.getModel().getValueAt(SelRow, 0))) {
                        String mRef = (String) tblSeguros.getModel().getValueAt(SelRow, 2);
                        String mValor = FuncoesGlobais.GravaValor((String) tblSeguros.getModel().getValueAt(SelRow, 4));
                        conn.ExecutarComando("DELETE FROM Seguros WHERE contrato = '" + jContrato.getSelectedItem().toString() + "' AND referencia = '" + mRef + "' AND valor = '" + mValor + "';");
                    } else {
                        String mCampo = (String) tblSeguros.getModel().getValueAt(SelRow, 5);
                        int mPosDesc =  Integer.parseInt((String) tblSeguros.getModel().getValueAt(SelRow, 6));
                        String mRef =  (String) tblSeguros.getModel().getValueAt(SelRow, 2);

                        String[] lcpos = mCampo.split(";");
                        lcpos = FuncoesGlobais.ArrayDel(lcpos, mPosDesc);
                        mCampo = FuncoesGlobais.join(lcpos, ";");
                        conn.ExecutarComando("UPDATE RECIBO SET campo = '" + mCampo + "' WHERE contrato = '" + jContrato.getSelectedItem().toString() + "' AND YEAR(dtvencimento) = " + mRef.substring(3, 7) + " AND MONTH(dtvencimento) = " + mRef.substring(0, 2) +";");
                    }
                    try {
                        FillSeg(tblSeguros);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
}//GEN-LAST:event_tblSegurosKeyPressed

    private void jLancarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLancarActionPerformed
        String mContrato = jContrato.getSelectedItem().toString();
        String[][] aDados = null;
        try {
            aDados = this.conn.LerCamposTabela(new String[] {"rgprp", "rgimv"}, "locatarios", "contrato = '" + mContrato + "'");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        String rgprp = "";
        String rgimv = "";
        if (aDados != null) {
            rgprp = aDados[0][3];
            rgimv = aDados[1][3];
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        int i=0;

        int qtde = Integer.parseInt(String.valueOf(jQtde.getValue()));

        String mTipo = "";
        mTipo = (jExtrato.isSelected() ? ":ET" : "") + (jRetencao.isSelected() ? ":RT" : "");
        if (mTipo.length() > 0) {if (":".equals(mTipo.substring(0, 1))) mTipo = mTipo.substring(1);}
        String mCpoDesc = "";

        String[] aMeses = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        String nmMes = ((JComboBox)jMes.getComboBox()).getSelectedItem().toString();
        int nrMes = FuncoesGlobais.IndexOf(aMeses, nmMes);
        
        String mMes = FuncoesGlobais.StrZero(String.valueOf(nrMes),2);
        String mAno = FuncoesGlobais.StrZero(String.valueOf(jAno.getYear()), 4);

        ResultSet hRs = this.conn.AbrirTabela("SELECT * FROM RECIBO WHERE contrato = '" + mContrato + "' AND dtvencimento >= CONCAT('" + mAno + "','-','" + FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(mMes) + 1),2) + "','-',DAY(dtvencimento)) ORDER BY dtvencimento;", ResultSet.CONCUR_READ_ONLY);

        try {
            while (hRs.next()) {
                String newCampo = hRs.getString("campo").trim().toUpperCase();
                mCpoDesc = ";SG:3:" + FuncoesGlobais.GravaValor(jValor.getText()) + ":" + FuncoesGlobais.StrZero(String.valueOf(i + 1), 2) + FuncoesGlobais.StrZero(String.valueOf(qtde), 2) + ":SG:RZ:" + mTipo + ":DS" + FuncoesGlobais.CriptaNome("SEGURO");
                newCampo += mCpoDesc;

                java.sql.Date venc = (java.sql.Date) hRs.getDate("dtvencimento");
                String gDtVecto = "0000-00-00";
                try {
                    gDtVecto = fmt.format(venc);
                } catch (Exception ex) { ex.printStackTrace(); }

                if (!"X".equals(hRs.getString("tag"))) {
                    this.conn.ExecutarComando("UPDATE RECIBO SET campo = '" + newCampo + "' WHERE contrato = '" + mContrato + "' AND dtvencimento = '" + gDtVecto + "';");
                    i += 1;
                }
                mMes = hRs.getString("dtvencimento").substring(5, 7);
                mAno = hRs.getString("dtvencimento").substring(0, 4);
                if (i == qtde) break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(hRs);

        if (i < qtde) {
            int j = qtde;
            int k = (i <=0 ? 1 : i + 1);
            int iMes = Integer.parseInt(mMes);
            int iAno = Integer.parseInt(mAno);
            String mValor = FuncoesGlobais.GravaValor(jValor.getText());
            String mDesc = "SEGURO";
            for (;k<=j; k += 1) {
                ++iMes;
                if (iMes > 12) {
                    iAno++;
                    mAno = FuncoesGlobais.StrZero(String.valueOf(iAno), 4);
                    iMes = 1;
                }
                String mRef = FuncoesGlobais.StrZero(String.valueOf(iMes), 2) + "/" + mAno;
                String sql = "INSERT INTO Seguros (rgprp, rgimv, contrato, valor, referencia, sigla) VALUES ('" +
                        rgprp + "','" + rgimv + "','" + mContrato + "','" + mValor + "','" + mRef + "','" + mTipo + "');";

                conn.ExecutarComando(sql);
            }
        }
        try {
            FillSeg(tblSeguros);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
}//GEN-LAST:event_jLancarActionPerformed

    private void jSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSairActionPerformed
        this.dispose();
}//GEN-LAST:event_jSairActionPerformed

    private void jContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jContratoActionPerformed
        int pos = jContrato.getSelectedIndex();
        if (jNomeLoca.getItemCount() > 0) {jNomeLoca.setSelectedIndex(pos);}
    }//GEN-LAST:event_jContratoActionPerformed

    private void jNomeLocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomeLocaActionPerformed
        int pos = jNomeLoca.getSelectedIndex();
        if (jContrato.getItemCount() > 0) {jContrato.setSelectedIndex(pos);}
    }//GEN-LAST:event_jNomeLocaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JYearChooser jAno;
    private javax.swing.JComboBox jContrato;
    private javax.swing.JCheckBox jExtrato;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton jLancar;
    private com.toedter.calendar.JMonthChooser jMes;
    private javax.swing.JComboBox jNomeLoca;
    private javax.swing.JSpinner jQtde;
    private javax.swing.JCheckBox jRetencao;
    private javax.swing.JButton jSair;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField jValor;
    private javax.swing.JTable tblSeguros;
    // End of variables declaration//GEN-END:variables

}
