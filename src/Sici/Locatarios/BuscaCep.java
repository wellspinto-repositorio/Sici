/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Sici.Locatarios;

import Funcoes.DbMain;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.newTable;
import java.sql.ResultSet;

/**
 *
 * @author supervisor
 */
public class BuscaCep extends javax.swing.JDialog {
    int tam = 0, n = 0;
    DbMain conn = VariaveisGlobais.conexao;
    public Object[] dados = null;
    
    /**
     * Creates new form BuscaCep
     */
    public BuscaCep(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        String[] aheaders = {"Logradouro","Nomeslog","Endereço","Bairro","Cidade","Uf","Cep"};
        int[] widths = {0,0,500,250,250,100,150};
        String[] aligns = {"L","L","L","L","L","L","L"};
        newTable.InitTable(tblCEPS, aheaders, widths, aligns, true);
        
        setLocationRelativeTo(null);
    }

    private void SeekCep(String ctexto) {
        TableControl.Clear(tblCEPS);
        
        String[] cbusca = ctexto.split(",");
        String tsql = "";
        if (cbusca.length == 1) {
            tsql = "select e.logradouro, e.nomeslog, e.nomeclog, b.nome as bairro, c.nome as cidade, b.uf, e.cep  from cep_enderecos e, cep_bairros b, cep_cidades c where (e.bairro_id = b.id) and (e.cidade_id = c.id) and b.uf = 'RJ' and UPPER(nomeslog) like '%" + cbusca[0].toUpperCase().trim() + "%' ORDER BY Upper(c.nome), Upper(e.nomeslog);";
        } else {
            tsql = "select e.logradouro, e.nomeslog, e.nomeclog, b.nome as bairro, c.nome as cidade, b.uf, e.cep  from cep_enderecos e, cep_bairros b, cep_cidades c where (e.bairro_id = b.id) and (e.cidade_id = c.id) and b.uf = 'RJ' and UPPER(nomeslog) like '%" + cbusca[0].toUpperCase().trim() + "%' and Upper(c.nome) like '%" + cbusca[1].trim().toUpperCase() + "%' ORDER BY Upper(c.nome), Upper(e.nomeslog);";
        }
        //"select e.logradouro, e.nomeslog, e.nomeclog, b.nome as bairro, c.nome as cidade, b.uf, e.cep  from cep_enderecos e, cep_bairros b, cep_cidades c where (e.bairro_id = b.id) and (e.cidade_id = c.id) and b.uf = 'RJ' and UPPER(nomeslog) like '%" + ctexto.toUpperCase().trim() + "%' ORDER BY Upper(c.nome), Upper(e.nomeslog);";
        ResultSet tbcep = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY);
        try {
            tam = conn.RecordCount(tbcep);
            n = 1;
            jbarra.setValue(0);
            jbarra.setVisible(true);
            
            while (tbcep.next()) {
                new Thread() {
                    public void run() {
                        int pos = (n * 100) / tam;
                        try {sleep(100);} catch (Exception ex) {}
                        jbarra.setValue(pos);
                        jbarra.repaint();
                    }
                }.start();                
                                
                String slogra = tbcep.getString("logradouro");
                String sender = tbcep.getString("nomeslog");
                String sendbc = tbcep.getString("nomeclog");
                String sbairr = tbcep.getString("bairro");
                String scidad = tbcep.getString("cidade");
                String suf    = tbcep.getString("uf");
                String scep   = tbcep.getString("cep");
                
                Object[] linha = {slogra, sender, sendbc, sbairr, scidad, suf, scep};
                newTable.add(tblCEPS, linha);
                
                this.n++;
            }
        } catch (Exception err) {}
        DbMain.FecharTabela(tbcep);
        
        //jbarra.setVisible(false);
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
        tblCEPS = new javax.swing.JTable();
        jbarra = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        jbtBuscar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Busca de Cep por endereço...");
        setAlwaysOnTop(true);
        setModal(true);
        setResizable(false);

        tblCEPS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblCEPS.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCEPS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCEPSMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCEPS);

        jLabel1.setText("Texto:");

        jbtBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        jbtBuscar.setMnemonic('B');
        jbtBuscar.setText("Buscar");
        jbtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbarra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 846, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBuscarActionPerformed
        new Thread() {public void run() {SeekCep(txtBuscar.getText());}}.start();
    }//GEN-LAST:event_jbtBuscarActionPerformed

    private void tblCEPSMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCEPSMouseClicked
        int row = tblCEPS.getSelectedRow(); 
        if (row < 0) {
            dados = null;
        } else {
            if (evt.getClickCount() == 2) {
                String slogra = tblCEPS.getModel().getValueAt(row, 0).toString();
                String sender = tblCEPS.getModel().getValueAt(row, 1).toString();
                String sbairr = tblCEPS.getModel().getValueAt(row, 3).toString();
                String scidad = tblCEPS.getModel().getValueAt(row, 4).toString();
                String sestad = tblCEPS.getModel().getValueAt(row, 5).toString();
                String scep   = tblCEPS.getModel().getValueAt(row, 6).toString();
                dados = new Object[] {slogra, sender, sbairr, scidad, sestad, scep};
                
                this.dispose();
            }
        }
    }//GEN-LAST:event_tblCEPSMouseClicked

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
            java.util.logging.Logger.getLogger(BuscaCep.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscaCep.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscaCep.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscaCep.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BuscaCep dialog = new BuscaCep(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar jbarra;
    private javax.swing.JButton jbtBuscar;
    private javax.swing.JTable tblCEPS;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
