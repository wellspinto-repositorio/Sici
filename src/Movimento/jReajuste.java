/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import java.sql.ResultSet;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Color;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author supervisor
 */
public class jReajuste extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    
    /**
     * Creates new form jReajuste
     */
    public jReajuste() {
        initComponents();
        
    }

    private void ListaImvReaj() {
        TableControl.Clear(jtbImoveis);
        String sql = "SELECT c.contrato, c.rgimv, l.nomerazao AS nome, c.dtinicio, c.dttermino, c.dtvencimento, c.dtadito, " +
                "StrVal(Mid(c.campo,6,10)) AS valor, ((Month(StrDate(c.dttermino)) = &1. AND Year(StrDate(c.dttermino)) = &2.)) AS pinta, " + 
                "c.dtseguro FROM jgeral.CARTEIRA c, jgeral.locatarios l WHERE (c.contrato = l.contrato) AND ((Month(StrDate(c.dtinicio)) = &1. AND " + 
                "Year(StrDate(c.dttermino)) >= &2.) OR (Month(StrDate(c.dttermino)) = &1. AND Year(StrDate(c.dttermino)) = &2.)) " + 
                "ORDER BY Upper(l.nomerazao);";
        javax.swing.table.DefaultTableModel dtm = (javax.swing.table.DefaultTableModel)jtbImoveis.getModel();

        ResultSet rs = conn.AbrirTabela(FuncoesGlobais.Subst(sql,new String[] {String.valueOf(jMes.getSelectedIndex() + 1), String.valueOf(jAno.getValue())}), ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String tcontrato = rs.getString("contrato");
                String trgimv = rs.getString("rgimv");
                String tnome = rs.getString("nome");
                String tvalor = LerValor.floatToCurrency(rs.getFloat("valor"),2);
                String tdtini = rs.getString("dtinicio");
                String tdtter = rs.getString("dttermino");
                String tdtadito = rs.getString("dtadito");
                boolean tpinta = rs.getBoolean("pinta");
                String tmesanor = rs.getString("dtseguro"); if (tmesanor == null) tmesanor = "";
                
                Date _inic = Dates.StringtoDate(tdtini,"dd/MM/yyyy");
                Date _comp = new Date();
                if (_inic.getYear() != _comp.getYear()) {   
                    if (tmesanor.isEmpty()) {
                        dtm.addRow(new Object[] {tcontrato,trgimv,tnome,tvalor,tdtini,tdtter,tdtadito,false, tpinta});
                    } else {
                        boolean reaj = Integer.valueOf(tmesanor.substring(3,7)) >= Dates.iYear(new Date());
                        if (!reaj) {
                            dtm.addRow(new Object[] {tcontrato,trgimv,tnome,tvalor,tdtini,tdtter,tdtadito,false, tpinta});
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        DbMain.FecharTabela(rs);
        
        // Alinhamentos
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        
        jtbImoveis.getColumnModel().getColumn(3).setCellRenderer(direita);
        jtbImoveis.getColumnModel().getColumn(4).setCellRenderer(centralizado);
        jtbImoveis.getColumnModel().getColumn(5).setCellRenderer(centralizado);
        jtbImoveis.getColumnModel().getColumn(6).setCellRenderer(centralizado);
    
        // Tamanhos
        jtbImoveis.getColumnModel().getColumn(0).setPreferredWidth(30);
        jtbImoveis.getColumnModel().getColumn(1).setPreferredWidth(30);
        jtbImoveis.getColumnModel().getColumn(2).setPreferredWidth(250);
        jtbImoveis.getColumnModel().getColumn(7).setPreferredWidth(20);
        jtbImoveis.getColumnModel().getColumn(8).setPreferredWidth(20);
        // ocultar
        //jtbImoveis.getColumnModel().getColumn(8).setPreferredWidth(0);
        //jtbImoveis.getColumnModel().getColumn(8).setMinWidth(0);
        //jtbImoveis.getColumnModel().getColumn(8).setMaxWidth(0);
        
        // Zebra
        UIManager.put("Table.alternateRowColor", new Color(204,204,255));
        UIManager.put("Table.foreground", new Color(0, 0, 0));
        //UIManager.put("Table.selectionBackground", Color.YELLOW);
        //UIManager.put("Table.selectionForeground", Color.RED);
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtbImoveis = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jMes = new javax.swing.JComboBox();
        jAno = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jbtListar = new javax.swing.JButton();
        jbtProcessa = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jfmIndice = new javax.swing.JFormattedTextField();

        setClosable(true);
        setIconifiable(true);
        setTitle("Reajuste de Aluguel");

        jtbImoveis.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Contrato", "RgImv", "Nome", "Valor", "DtInicio", "DtTermino", "DtAdito", "Reajustar", "Fim"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jtbImoveis);
        jtbImoveis.getColumnModel().getColumn(4).setCellEditor(null);
        jtbImoveis.getColumnModel().getColumn(5).setCellEditor(null);
        jtbImoveis.getColumnModel().getColumn(6).setCellEditor(null);

        jLabel1.setText("Mês para Reajuste:");

        jMes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" }));

        jAno.setModel(new javax.swing.SpinnerNumberModel(2013, 2010, 2030, 1));

        jLabel2.setText("Ano:");

        jbtListar.setText("Listar");
        jbtListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtListarActionPerformed(evt);
            }
        });

        jbtProcessa.setText("Processar Aumento");
        jbtProcessa.setEnabled(true);
        jbtProcessa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtProcessaActionPerformed(evt);
            }
        });

        jLabel3.setText("Indice de Reajuste no Mês:");

        jfmIndice.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        jfmIndice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jfmIndice.setText("0,000");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMes, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(10, 10, 10)
                        .addComponent(jAno, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtListar, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jfmIndice, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                    .addComponent(jbtProcessa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jbtListar)
                    .addComponent(jLabel3)
                    .addComponent(jfmIndice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtProcessa)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtListarActionPerformed
        ListaImvReaj();
    }//GEN-LAST:event_jbtListarActionPerformed

    private void jbtProcessaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtProcessaActionPerformed
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(null,
            "Deseja REAJUSTAR os alugueis dos locatários ? ",
            "Atenção", JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.NO_OPTION) return;
        
        for (int i=0;i<jtbImoveis.getRowCount();i++) {
            String contrato = (String) jtbImoveis.getModel().getValueAt(i, 0);
            float valor = LerValor.StringToFloat((String) jtbImoveis.getModel().getValueAt(i, 3));
            String dtadito = (String) jtbImoveis.getModel().getValueAt(i, 6);
            boolean reaj = (Boolean) jtbImoveis.getModel().getValueAt(i, 7);
            float indice = LerValor.StringToFloat(jfmIndice.getText());
            
            if (dtadito == null) dtadito = "";
            
            if ((Boolean) jtbImoveis.getModel().getValueAt(i, 7)) {
                String _reaj = LerValor.floatToCurrency(valor * (1 + indice / 100),2);
                String _cpo  = FuncoesGlobais.GravaValor(_reaj);
                String _adito = ", dtadito = '" + dtadito + "'";
                conn.ExecutarComando("UPDATE CARTEIRA SET CAMPO = CONCAT(MID(CAMPO,1,5),'" + _cpo + "',MID(CAMPO,16))" + (!dtadito.trim().equals("") ? _adito : "") + 
                        ", DTSEGURO = '" + Dates.DateFormata("MM/yyyy", new Date()) + "' WHERE contrato = '" + contrato + "';");
            }
        }
        ListaImvReaj();
    }//GEN-LAST:event_jbtProcessaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner jAno;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox jMes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtListar;
    private javax.swing.JButton jbtProcessa;
    private javax.swing.JFormattedTextField jfmIndice;
    private javax.swing.JTable jtbImoveis;
    // End of variables declaration//GEN-END:variables
}

/*
 * Colorindo jTable.java
 * Pintas as tabelas alterando as linhas
 * Destaca uma linha se o checkbox da linha for selecionado
 * Author : Mario Cezzare Angelicola Chiodi
 * mcezzare@gmail.com
 */
 
 
