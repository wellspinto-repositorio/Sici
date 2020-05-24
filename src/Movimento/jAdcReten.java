/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jAdcReten.java
 *
 * Created on 01/02/2012, 12:21:55
 */
package Movimento;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import Funcoes.jTableControl;
import Sici.Partida.Collections;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author supervisor
 */
public class jAdcReten extends javax.swing.JDialog {
    DbMain conn = VariaveisGlobais.conexao;
    boolean bExecNome = false, bExecCodigo = false;
    jTableControl tabela = new jTableControl(true);
    
    /** Creates new form jAdcReten */
    public jAdcReten(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        //Centraliza a janela.
        Dimension dimension = this.getToolkit().getScreenSize();
        int x = (int) (dimension.getWidth() - this.getSize().getWidth() ) / 2;
        int y = (int) (dimension.getHeight() - this.getSize().getHeight()) / 2;
        this.setLocation(x,y);
        
        FillRetencao();
        
    }

 private void FillRetencao() {
        Integer[] tam = {120,130,300,120,20,0,0,0,0};
        String[] col = {"RgImv","Vencimento", "Taxa","Crédito","Retido","campo","aut","rgprp","contrato"};
        Boolean[] edt = {false,false,false,false,true,false,false,false,false};
        String[] aln = {"C","C","L","R","","L","L","L","L"};
        Object[][] data = {};
        String[][] sPrint = Imprimir();        
        
        for (int i=0;i<sPrint.length;i++) {
            Object[] dado = {sPrint[i][0], sPrint[i][1], sPrint[i][2], sPrint[i][3], (sPrint[i][4].equalsIgnoreCase("TRUE") ? true : false), sPrint[i][5], sPrint[i][6], sPrint[i][7], sPrint[i][8]};
            data = tabela.insert(data, dado);
        }
        tabela.Show(jLista, data, tam, aln, col, edt);
    }
 
    private String[][] Imprimir() {
        Collections gVar = VariaveisGlobais.dCliente;
        String[][] sCampos = {};

        String sql = "SELECT contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut FROM extrato WHERE rgprp = '&1.' AND (tag <> 'X' AND tag <> 'B') ORDER BY rgimv, dtrecebimento;";
               sql = FuncoesGlobais.Subst(sql, new String[] {VariaveisGlobais.lbr_rgprp.trim()});

        ResultSet hrs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (hrs.next()) {
                String srgimv = hrs.getString("rgimv").trim();
                String srgprp = hrs.getString("rgprp").trim();
                String scontrato = hrs.getString("contrato").trim();
                
                String svencto = Dates.DateFormata("dd/MM/yyyy", hrs.getDate("dtvencimento"));
                String saut = hrs.getString("rc_aut");
                
                String tmpCampo = hrs.getString("campo");
                String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);
                String[][] rCampos2 = FuncoesGlobais.treeArray(tmpCampo, false);

                for (int j = 0; j<rCampos.length; j++) {
                    String tpCampo = rCampos[j][rCampos[j].length - 1];
                    if (VariaveisGlobais.bShowCotaParcelaExtrato) {
                        String spart1 = "", spart2 = "", scotaparc = "";
                        if (!"".equals(rCampos[j][3].trim())) {
                            spart1 = rCampos[j][3].trim().substring(0, 2);
                            spart2 = rCampos[j][3].trim().substring(2);
                        } else {
                            spart1 = "00"; spart2 = "0000";
                        }
                        if (!"00".equals(spart1) && "0000".equals(spart2)) {
                            spart1 = "00";
                        } else if ("00".equals(spart1) && !"0000".equals(spart2)) {
                            spart2 = "0000";
                        }
                        scotaparc = spart1 + spart2;
                        tpCampo += "  " + ("0000".equals(scotaparc) || "000000".equals(scotaparc) || "".equals(scotaparc) ? "       " : scotaparc.substring(0,2) + "/" + scotaparc.substring(2));
                    }
                    boolean bRetc = FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1;
                    if ("AL".equals(rCampos[j][4])) {
                        if (LerValor.isNumeric(rCampos[j][0])) {
                            //
                        } else {
                            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {srgimv, svencto, tpCampo, LerValor.FormatNumber(rCampos[j][2],2),(bRetc ? "TRUE" : "FALSE"), FuncoesGlobais.join(rCampos2[j], ":"), saut,srgprp, scontrato});
                        }
                    } else if ("SG".equals(rCampos[j][4])) {
                        sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {srgimv, svencto, tpCampo, LerValor.FormatNumber(rCampos[j][2],2),(bRetc ? "TRUE" : "FALSE"),FuncoesGlobais.join(rCampos2[j], ":"), saut,srgprp, scontrato});
                    } else if (!"DC".equals(rCampos[j][4]) && !"DF".equals(rCampos[j][4])) {
                        if (FuncoesGlobais.IndexOf(rCampos[j], "AT") <= -1) {
                            sCampos = FuncoesGlobais.ArraysAdd(sCampos, new String[] {srgimv, svencto, tpCampo, LerValor.FormatNumber(rCampos[j][2],2),(bRetc ? "TRUE" : "FALSE"), FuncoesGlobais.join(rCampos2[j], ":"), saut,srgprp, scontrato});
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(hrs);

        return sCampos;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jLista = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Adicionar taxa em Retenção");
        setAlwaysOnTop(true);
        setModal(true);
        setResizable(false);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Discriminação das Retenções", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 0, 12), new java.awt.Color(0, 0, 153))); // NOI18N
        jScrollPane2.setForeground(new java.awt.Color(0, 0, 153));
        jScrollPane2.setFont(new java.awt.Font("Dialog", 0, 8));

        jLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jLista);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListaMouseClicked
        if (jLista.columnAtPoint(evt.getPoint()) == 4) {
            ProcessaRetencao();
        }
    }//GEN-LAST:event_jListaMouseClicked

    private void ProcessaRetencao() {
        String[][] tmpCampo = {}; String[] tmpCampo2 = {}; String oldCampo = ""; String nAut = "";
        String rgimv = ""; String rgprp = ""; String contrato = ""; String vecto = "";
        boolean insert = false;
        try {
            oldCampo = jLista.getModel().getValueAt(jLista.getSelectedRow(), 5).toString();
            nAut = jLista.getModel().getValueAt(jLista.getSelectedRow(), 6).toString();
            rgprp = jLista.getModel().getValueAt(jLista.getSelectedRow(), 7).toString();
            rgimv = jLista.getModel().getValueAt(jLista.getSelectedRow(), 0).toString();
            contrato = jLista.getModel().getValueAt(jLista.getSelectedRow(), 8).toString();
            vecto = jLista.getModel().getValueAt(jLista.getSelectedRow(), 1).toString();
            
            tmpCampo = FuncoesGlobais.treeArray(oldCampo, false);
            tmpCampo2 = tmpCampo[0];
        } catch (SQLException ex) {
            Logger.getLogger(jAdcReten.class.getName()).log(Level.SEVERE, null, ex);
        }

        if ("true".equalsIgnoreCase(jLista.getModel().getValueAt(jLista.getSelectedRow(), 4).toString().toLowerCase())) {
            tmpCampo2 = FuncoesGlobais.ArrayAdd(tmpCampo2, "RT");
            insert = true;
        } else {
            int pos = FuncoesGlobais.IndexOfn(tmpCampo2, "RT", 0);
            while ( pos > -1) {
                tmpCampo2 = FuncoesGlobais.ArrayDel(tmpCampo2, pos);
                pos = FuncoesGlobais.IndexOfn(tmpCampo2, "RT", pos);
            }
            insert = false;
        }
        // Gravações
        // Descobre se a retenção existe na tabela
        String tbl[][] = {};
        try {
            tbl = conn.LerCamposTabela(new String[] {"rc_aut", "marca"}, "retencao", "rc_aut = '" + nAut + "' AND campo = '" + oldCampo + "'");
        } catch (SQLException ex) {
            Logger.getLogger(jAdcReten.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        boolean podelancar = false;
        try {
            podelancar = tbl[0][3].trim().equalsIgnoreCase(nAut) && !tbl[1][3].trim().equalsIgnoreCase("X");
        } catch (NullPointerException e) { podelancar = true; }
        
        if (podelancar) {
            // Deleta autenticacao do arquivo de retencoes
            String sSql = "";
            sSql = "DELETE FROM retencao WHERE rc_aut = '" + nAut + "' AND marca <> 'X' AND campo = '" + oldCampo + "'";
            conn.ExecutarComando(sSql);

            if (insert) {
                sSql = "INSERT INTO retencao (contrato, rgprp, rgimv, campo, vencimento, rc_aut, tag, gat, rt_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.','&9.');";
                String[] par1 = {contrato, rgprp, rgimv, FuncoesGlobais.join(tmpCampo2, ":"),
                                 Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")),
                                 nAut,"0"," ","0"};
                sSql = FuncoesGlobais.Subst(sSql, par1);
                conn.ExecutarComando(sSql);
            }

            sSql = "UPDATE extrato set campo = Replace(campo,'&1.','&2.') WHERE rc_aut = '&3.';";
            sSql = FuncoesGlobais.Subst(sSql, new String[] {oldCampo,FuncoesGlobais.join(tmpCampo2, ":"),nAut});
            conn.ExecutarComando(sSql);

            sSql = "UPDATE auxiliar set campo = Replace(campo,'&1.','&2.') WHERE conta = 'REC' AND rc_aut = '&3.';";
            sSql = FuncoesGlobais.Subst(sSql, new String[] {oldCampo,FuncoesGlobais.join(tmpCampo2, ":"),nAut});
            conn.ExecutarComando(sSql);
            
            // Atualiza lista
            tmpCampo[0] = tmpCampo2;
            jLista.getModel().setValueAt(FuncoesGlobais.join(tmpCampo2, ":"), jLista.getSelectedRow(), 5);
        } else {
            JOptionPane.showMessageDialog(null, "Esta retenção já foi paga!!!\nNão é possivel remove-la...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            jLista.getModel().setValueAt(new Boolean(true),jLista.getSelectedRow(), 4);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(jAdcReten.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jAdcReten.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jAdcReten.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jAdcReten.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                jAdcReten dialog = new jAdcReten(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable jLista;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
