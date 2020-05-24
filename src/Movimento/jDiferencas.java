/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jDiferencas.java
 *
 * Created on 16/02/2011, 15:07:37
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
public class jDiferencas extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    int pos = 0;
    boolean bExecNome = false, bExecCodigo = false;

    /** Creates new form jDiferencas */
    public jDiferencas() {
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
                    jComissao.setSelected(true);
                    jExtrato.setSelected(true);
                    jImposto.setSelected(true);
                }                                
            }
        });

        ComboBoxEditor edit = jNomeLoca.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                try {
                    FillDif(tblDescontos);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void FillDif(JTable table) throws SQLException {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"tp","nome","referencia","descrição","valor","campo","pos"},{"25","300","100","150","80","0","0"}});

        String mContrato; String mNome; String mCampo; String mVecto; String mDesc; String mValor;
        String sql = "SELECT r.contrato, l.nomerazao, r.campo, r.dtvencimento FROM RECIBO r, locatarios l WHERE (r.contrato = l.contrato) AND r.contrato = '" + jContrato.getSelectedItem().toString() + "' AND r.tag <> 'X' AND INSTR(r.campo,'DF:2:') > 0 ORDER BY r.dtvencimento;";
        ResultSet hRs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        while (hRs.next()) {
            mContrato = hRs.getString("r.contrato");
            mNome = hRs.getString("l.nomerazao");
            mCampo = hRs.getString("r.campo");
            mVecto = hRs.getString("r.dtvencimento");

            while (this.pos > -1) {
                String[] a = RetValorDif(mCampo, "DF");
                if (a != null) {
                    mDesc = a[0];
                    mValor = a[1];

                    TableControl.add(table, new String[][]{{"R", mNome, mVecto.substring(5, 7) + "-" + mVecto.substring(0, 4), mDesc, mValor, mCampo, String.valueOf(this.pos - 1)},{"C","L","C","L","R","L","L"}}, true);
                }
            }
            this.pos = 0;
        }
        DbMain.FecharTabela(hRs);

        sql = "SELECT d.contrato, l.nomerazao, d.descricao, d.valor, d.referencia FROM Diferenca d, locatarios l WHERE (d.contrato = l.contrato) AND d.contrato = '" + jContrato.getSelectedItem().toString() + "' ORDER BY d.contrato, d.referencia";
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

    private void FillBuffer() {
        String sSql = "SELECT autoid, buffer, local FROM COMBOBUFFER WHERE local = 'DIFERE' ORDER BY local, lower(buffer);";
        ResultSet hBuf = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {

            while (hBuf.next()) {
                jDescricao.addItem(hBuf.getString("buffer"));
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(hBuf);
    }

    private void AddBuffer(String Desc) {
        String sSql = "INSERT INTO COMBOBUFFER (buffer, local) VALUES ('" + Desc.trim().toUpperCase() + "','DIFERE');";
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
        jMes = new com.toedter.calendar.JMonthChooser();
        jLabel2 = new javax.swing.JLabel();
        jLancar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jAno = new com.toedter.calendar.JYearChooser();
        jQtde = new javax.swing.JSpinner();
        jValor = new javax.swing.JFormattedTextField();
        jContrato = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jNomeLoca = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jSair = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jImposto = new javax.swing.JCheckBox();
        jNormal = new javax.swing.JRadioButton();
        jComissao = new javax.swing.JRadioButton();
        jRetencao = new javax.swing.JRadioButton();
        jExtrato = new javax.swing.JCheckBox();
        jDescricao = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDescontos = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Lançamento de Diferenças ::.");
        setFont(new java.awt.Font("Agency FB", 0, 8)); // NOI18N
        setVisible(true);

        jLabel2.setText("Nome");

        jLancar.setMnemonic('L');
        jLancar.setText("Lançar");
        jLancar.setEnabled(false);
        jLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLancarActionPerformed(evt);
            }
        });

        jLabel1.setText("Contrato");

        jQtde.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jQtdeStateChanged(evt);
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

        jLabel3.setText("Descriminação");

        jNomeLoca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomeLocaActionPerformed(evt);
            }
        });

        jLabel4.setText("Mês/Ano");

        jSair.setMnemonic('S');
        jSair.setText("Sair");
        jSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSairActionPerformed(evt);
            }
        });

        jLabel6.setText("Qtde");

        jLabel5.setText("Valor");

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jImposto.setText("Vai para Imposto");

        dGrupo.add(jNormal);
        jNormal.setSelected(true);
        jNormal.setText("Normal");
        jNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNormalActionPerformed(evt);
            }
        });

        dGrupo.add(jComissao);
        jComissao.setText("Comissão");

        dGrupo.add(jRetencao);
        jRetencao.setText("Retenção");

        jExtrato.setSelected(true);
        jExtrato.setText("Vai para Extrato");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRetencao)
                        .addGap(32, 32, 32)
                        .addComponent(jComissao))
                    .addComponent(jExtrato))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jNormal)
                    .addComponent(jImposto))
                .addGap(63, 63, 63))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRetencao)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComissao)
                        .addComponent(jNormal)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jExtrato)
                    .addComponent(jImposto))
                .addContainerGap())
        );

        jDescricao.setEditable(true);

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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jNomeLoca, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(158, 158, 158)
                            .addComponent(jLabel4))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jMes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jAno, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jQtde, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(jSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLancar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jAno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDescricao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jQtde, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jValor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLancar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSair, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
                        conn.ExecutarComando("DELETE FROM Diferenca WHERE contrato = '" + jContrato.getSelectedItem().toString() + "' AND referencia = '" + mRef + "' AND UPPER(descricao) = '" + mDesc.toUpperCase() + "' AND valor = '" + mValor + "';");
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
                        FillDif(tblDescontos);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
}//GEN-LAST:event_tblDescontosKeyPressed

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
        if (jRetencao.isSelected()) { mTipo = "RT"; }
        if (jComissao.isSelected()) { mTipo = "AL:LQ"; }
        mTipo += (jExtrato.isSelected() ? ":ET" : "") + (jImposto.isSelected() ? ":IP" : "");
        try {if (":".equals(mTipo.substring(0, 1))) mTipo = mTipo.substring(1);} catch (Exception e) {}
        String mCpoDesc = ";DF:2:" + FuncoesGlobais.GravaValor(jValor.getText()) + ":0000:DF:RZ:" + mTipo + ":DS" + FuncoesGlobais.CriptaNome(jDescricao.getSelectedItem().toString().trim().toUpperCase());
        
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
            for (;k<=j; k += 1) {
                ++iMes;
                if (iMes > 12) {
                    iAno++;
                    mAno = FuncoesGlobais.StrZero(String.valueOf(iAno), 4);
                    iMes = 1;
                }
                String mRef = FuncoesGlobais.StrZero(String.valueOf(iMes), 2) + "/" + mAno;
                String sql = "INSERT INTO Diferenca (rgprp, rgimv, contrato, descricao, valor, referencia, sigla) VALUES ('" +
                        rgprp + "','" + rgimv + "','" + mContrato + "','" + mDesc + "','" + mValor + "','" + mRef + "','" + mTipo + "');";

                conn.ExecutarComando(sql);
            }
        }
        try {
            FillDif(tblDescontos);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jLancarActionPerformed

    private void jQtdeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jQtdeStateChanged
        jLancar.setEnabled((jDescricao.getSelectedItem().toString().length() > 0 && (jValor.getText().length() > 0 && FuncoesGlobais.strCurrencyToFloat(jValor.getText()) > 0) && Integer.parseInt(String.valueOf(jQtde.getValue())) > 0));
    }//GEN-LAST:event_jQtdeStateChanged

    private void jSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_jSairActionPerformed

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

    private void jNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNormalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jNormalActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup dGrupo;
    private com.toedter.calendar.JYearChooser jAno;
    private javax.swing.JRadioButton jComissao;
    private javax.swing.JComboBox jContrato;
    private javax.swing.JComboBox jDescricao;
    private javax.swing.JCheckBox jExtrato;
    private javax.swing.JCheckBox jImposto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton jLancar;
    private com.toedter.calendar.JMonthChooser jMes;
    private javax.swing.JComboBox jNomeLoca;
    private javax.swing.JRadioButton jNormal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner jQtde;
    private javax.swing.JRadioButton jRetencao;
    private javax.swing.JButton jSair;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField jValor;
    private javax.swing.JTable tblDescontos;
    // End of variables declaration//GEN-END:variables

}
