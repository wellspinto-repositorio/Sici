/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jDescontos.java
 *
 * Created on 16/02/2011, 14:55:12
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
public class jDescontos extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    int pos = 0;
    boolean bExecNome = false, bExecCodigo = false;

    /** Creates new form jDescontos */
    public jDescontos() {
        initComponents();

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        FillCombos();

        AutoCompletion.enable(jContrato);
        AutoCompletion.enable(jNomeLoca);

        FillBuffer();
        //AutoCompletion.enable(jDescricao);

        //Obtem o Editor do seu JComboBox
        ComboBoxEditor editor = jDescricao.getEditor();
        //Obtem o componente de edicao do ComboBoxEditor
        Component component = editor.getEditorComponent();
        //Adiciona os ouvintes FocusAdapter, que serao responsaveis por escutar eventos de foco
        component.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                //Aqui você implementa seu codigo
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (jDescricao.getSelectedIndex() < 0 && jDescricao.getSelectedItem().toString().length() > 0) {
                    Object[] options = { "Sim", "Não" };
                    int n = JOptionPane.showOptionDialog(null,
                            "Deseja incluir este tópico para uso futuros ? ",
                            "Atenção", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (n == JOptionPane.YES_OPTION) {
                        AddBuffer(jDescricao.getSelectedItem().toString());
                    }
                }
                if (jDescricao.getSelectedItem().toString().toUpperCase().trim().contains("ALUGUEL")) {
                    jLiquido.setSelected(true);
                }                
            }
        });

        ComboBoxEditor edit = jNomeLoca.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                try {
                    FillDesc(tblDescontos);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void FillDesc(JTable table) throws SQLException {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"tp","nome","referencia","descrição","valor","campo","pos"},{"25","300","100","150","80","0","0"}});

        String mContrato; String mNome; String mCampo; String mVecto; String mDesc; String mValor;
        String sql = "SELECT r.contrato, l.nomerazao, r.campo, r.dtvencimento FROM RECIBO r, locatarios l WHERE (r.contrato = l.contrato) AND r.contrato = '" + jContrato.getSelectedItem().toString() + "' AND r.tag <> 'X' AND INSTR(r.campo,'DC:2:') > 0 ORDER BY r.dtvencimento;";
        ResultSet hRs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        while (hRs.next()) {
            mContrato = hRs.getString("r.contrato");
            mNome = hRs.getString("l.nomerazao");
            mCampo = hRs.getString("r.campo");
            mVecto = hRs.getString("r.dtvencimento");

            while (this.pos > -1) {
                String[] a = RetValorDesc(mCampo, "DC");
                if (a != null) {
                    mDesc = a[0];
                    mValor = a[1];

                    TableControl.add(table, new String[][]{{"R", mNome, mVecto.substring(5, 7) + "-" + mVecto.substring(0, 4), mDesc, mValor, mCampo, String.valueOf(this.pos - 1)},{"C","L","C","L","R","L","L"}}, true);
                }
            }
            this.pos = 0;
        }
        DbMain.FecharTabela(hRs);

        sql = "SELECT d.contrato, l.nomerazao, d.descricao, d.valor, d.referencia FROM Descontos d, locatarios l WHERE (d.contrato = l.contrato) AND d.contrato = '" + jContrato.getSelectedItem().toString() + "' ORDER BY d.contrato, d.referencia";
        hRs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        while (hRs.next()) {
            mContrato = hRs.getString("d.contrato");
            mNome = hRs.getString("l.nomerazao");
            mVecto = hRs.getString("d.referencia");
            mDesc = hRs.getString("d.descricao");
            mValor = LerValor.FormataCurrency(hRs.getString("d.valor"));

            TableControl.add(table, new String[][]{{"D", mNome, mVecto, mDesc, mValor},{"C","L","C","L","R"}}, true);
        }
        DbMain.FecharTabela(hRs);

    }

    private String[] RetValorDesc(String campo, String oQue) throws SQLException {
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

    private void FillBuffer() {
        String sSql = "SELECT autoid, buffer, local FROM COMBOBUFFER WHERE local = 'DESCON' ORDER BY local, lower(buffer);";
        ResultSet hBuf = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {

            while (hBuf.next()) {
                jDescricao.addItem(hBuf.getString("buffer"));
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(hBuf);
    }

    private void AddBuffer(String Desc) {
        String sSql = "INSERT INTO COMBOBUFFER (buffer, local) VALUES ('" + Desc.trim().toUpperCase() + "','DESCON');";
        if (this.conn.ExecutarComando(sSql) > 0) jDescricao.addItem(Desc);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dGrupo = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jContrato = new javax.swing.JComboBox();
        jNomeLoca = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jMes = new com.toedter.calendar.JMonthChooser();
        jAno = new com.toedter.calendar.JYearChooser();
        jValor = new javax.swing.JFormattedTextField();
        jQtde = new javax.swing.JSpinner();
        jBruto = new javax.swing.JRadioButton();
        jLiquido = new javax.swing.JRadioButton();
        jBrLiq = new javax.swing.JRadioButton();
        jLancar = new javax.swing.JButton();
        jSair = new javax.swing.JButton();
        jDescricao = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDescontos = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Descontos em Recibo ::.");
        setVisible(true);

        jLabel1.setText("Contrato");

        jLabel2.setText("Nome");

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

        jLabel3.setText("Descriminação");

        jLabel4.setText("Mês/Ano");

        jLabel5.setText("Valor");

        jLabel6.setText("Qtde");

        jValor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jValor.setText("0,00");

        jQtde.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jQtdeStateChanged(evt);
            }
        });

        dGrupo.add(jBruto);
        jBruto.setSelected(true);
        jBruto.setText("Bruto");

        dGrupo.add(jLiquido);
        jLiquido.setText("Líquido");

        dGrupo.add(jBrLiq);
        jBrLiq.setText("Bruto Imposto");
        jBrLiq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrLiqActionPerformed(evt);
            }
        });

        jLancar.setMnemonic('L');
        jLancar.setText("Lançar");
        jLancar.setEnabled(false);
        jLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLancarActionPerformed(evt);
            }
        });

        jSair.setMnemonic('S');
        jSair.setText("Sair");
        jSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSairActionPerformed(evt);
            }
        });

        jDescricao.setEditable(true);
        jDescricao.setName("jDescricao"); // NOI18N

        tblDescontos.setAutoCreateRowSorter(true);
        tblDescontos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblDescontos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDescontos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblDescontosKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblDescontos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(158, 158, 158)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jMes, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jBruto)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLiquido)
                                    .addGap(18, 18, 18)
                                    .addComponent(jBrLiq)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jAno, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jQtde, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLancar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jSair, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jQtde, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jAno, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jMes, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBruto)
                    .addComponent(jLiquido)
                    .addComponent(jBrLiq)
                    .addComponent(jSair, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLancar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jContratoActionPerformed
        if (!bExecNome) {
            int pos = jContrato.getSelectedIndex();
            if (jNomeLoca.getItemCount() > 0) {bExecCodigo = true; jNomeLoca.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jContratoActionPerformed

    private void jNomeLocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomeLocaActionPerformed
        if (!bExecCodigo) {
            int pos = jNomeLoca.getSelectedIndex();
            if (jContrato.getItemCount() > 0) {bExecNome = true; jContrato.setSelectedIndex(pos); bExecNome = false;}
        }
    }//GEN-LAST:event_jNomeLocaActionPerformed

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
        if (jBruto.isSelected()) { mTipo = "BR"; } else if (jLiquido.isSelected()) { mTipo = "AL:IP"; } else mTipo = "BR:IP";
        String mCpoDesc = ";DC:2:" + FuncoesGlobais.GravaValor(jValor.getText()) + ":0000:DC:" + mTipo + ":ET:DS" + FuncoesGlobais.CriptaNome(jDescricao.getSelectedItem().toString().trim().toUpperCase());

        String[] aMeses = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        String nmMes = ((JComboBox)jMes.getComboBox()).getSelectedItem().toString();
        int nrMes = FuncoesGlobais.IndexOf(aMeses, nmMes);
        
        String mMes = FuncoesGlobais.StrZero(String.valueOf(nrMes),2);
        String mAno = FuncoesGlobais.StrZero(String.valueOf(jAno.getYear()), 4);

        ResultSet hRs = this.conn.AbrirTabela("SELECT * FROM RECIBO WHERE contrato = '" + mContrato + "' AND dtvencimento >= CONCAT('" + mAno + "','-','" + mMes + "','-',DAY(dtvencimento)) ORDER BY dtvencimento;", ResultSet.CONCUR_READ_ONLY);

        try {
            while (hRs.next()) {
                String newCampo = hRs.getString("campo").trim().toUpperCase();
                newCampo += mCpoDesc;

                java.sql.Date venc = (java.sql.Date) hRs.getDate("dtvencimento");
                String gDtVecto = "0000-00-00";
                try {
                    gDtVecto = fmt.format(venc);
                } catch (Exception ex) { ex.printStackTrace(); }

                if (!"X".equals(hRs.getString("tag"))) {
                    this.conn.ExecutarComando("UPDATE RECIBO SET campo = '" + newCampo + "' WHERE contrato = '" + mContrato + "' AND dtvencimento = '" + gDtVecto + "';");
                    //System.out.println("UPDATE RECIBO SET campo = '" + newCampo + "' WHERE contrato = '" + mContrato + "' AND dtvencimento = '" + gDtVecto + "';");
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
            String mDesc = jDescricao.getSelectedItem().toString().trim().toUpperCase(); //FuncoesGlobais.CriptaNome(jDescricao.getSelectedItem().toString().trim().toUpperCase());
            for (;k<=j;k += 1) {
                ++iMes;
                if (iMes > 12) {
                    iAno++;
                    mAno = FuncoesGlobais.StrZero(String.valueOf(iAno), 4);
                    iMes = 1;
                }
                String mRef = FuncoesGlobais.StrZero(String.valueOf(iMes), 2) + "/" + mAno;
                String sql = "INSERT INTO Descontos (rgprp, rgimv, contrato, descricao, valor, referencia, sigla) VALUES ('" +
                        rgprp + "','" + rgimv + "','" + mContrato + "','" + mDesc + "','" + mValor + "','" + mRef + "','" + mTipo + ":ET" + "');";

                conn.ExecutarComando(sql);
            }
        }
        try {
            FillDesc(tblDescontos);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jLancarActionPerformed

    private void jQtdeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jQtdeStateChanged
        jLancar.setEnabled((jDescricao.getSelectedItem().toString().length() > 0 && (jValor.getText().length() > 0 && FuncoesGlobais.strCurrencyToFloat(jValor.getText()) > 0) && Integer.parseInt(String.valueOf(jQtde.getValue())) > 0));
    }//GEN-LAST:event_jQtdeStateChanged

    private void tblDescontosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblDescontosKeyPressed
        int SelRow = tblDescontos.getSelectedRow();
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (SelRow >  -1) {
                Object[] options = { "Sim", "Não" };
                int n = JOptionPane.showOptionDialog(null,
                        "Deseja excluir este lançamento ? ",
                        "Atenção", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    if ("D".equals(tblDescontos.getModel().getValueAt(SelRow, 0))) {
                        String mRef = (String) tblDescontos.getModel().getValueAt(SelRow, 2);
                        String mDesc = (String) tblDescontos.getModel().getValueAt(SelRow, 3);
                        String mValor = FuncoesGlobais.GravaValor((String) tblDescontos.getModel().getValueAt(SelRow, 4));
                        conn.ExecutarComando("DELETE FROM Descontos WHERE contrato = '" + jContrato.getSelectedItem().toString() + "' AND referencia = '" + mRef + "' AND UPPER(descricao) = '" + mDesc.toUpperCase() + "' AND valor = '" + mValor + "';");
                    } else {
                        String mCampo = (String) tblDescontos.getModel().getValueAt(SelRow, 5);
                        int mPosDesc =  Integer.parseInt((String) tblDescontos.getModel().getValueAt(SelRow, 6));
                        String mRef =  (String) tblDescontos.getModel().getValueAt(SelRow, 2);

                        String[] lcpos = mCampo.split(";");
                        lcpos = FuncoesGlobais.ArrayDel(lcpos, mPosDesc);
                        mCampo = FuncoesGlobais.join(lcpos, ";");
                        conn.ExecutarComando("UPDATE RECIBO SET campo = '" + mCampo + "' WHERE contrato = '" + jContrato.getSelectedItem().toString() + "' AND YEAR(dtvencimento) = " + mRef.substring(3, 7) + " AND MONTH(dtvencimento) = " + mRef.substring(0, 2) +";");
                    }
                    try {
                        FillDesc(tblDescontos);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }//GEN-LAST:event_tblDescontosKeyPressed

    private void jSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_jSairActionPerformed

    private void jBrLiqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrLiqActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBrLiqActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup dGrupo;
    private com.toedter.calendar.JYearChooser jAno;
    private javax.swing.JRadioButton jBrLiq;
    private javax.swing.JRadioButton jBruto;
    private javax.swing.JComboBox jContrato;
    private javax.swing.JComboBox jDescricao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton jLancar;
    private javax.swing.JRadioButton jLiquido;
    private com.toedter.calendar.JMonthChooser jMes;
    private javax.swing.JComboBox jNomeLoca;
    private javax.swing.JSpinner jQtde;
    private javax.swing.JButton jSair;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField jValor;
    private javax.swing.JTable tblDescontos;
    // End of variables declaration//GEN-END:variables

}
