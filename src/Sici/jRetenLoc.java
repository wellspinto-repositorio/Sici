/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jRetenLoc.java
 *
 * Created on 21/11/2011, 12:17:43
 */
package Sici;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.WordWrap;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTable;

/**
 *
 * @author user
 */
public class jRetenLoc extends javax.swing.JDialog {
    DbMain conn = VariaveisGlobais.conexao;
    
    /** Creates new form jRetenLoc */
    public jRetenLoc(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        FillAvisoLoca(jtbLcReten, VariaveisGlobais.rcontrato);

        setLocationRelativeTo(null);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtbLcReten = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Retenção Locatarios ::.");
        setAlwaysOnTop(true);
        setModal(true);
        setResizable(false);

        jtbLcReten.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jtbLcReten);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                jRetenLoc dialog = new jRetenLoc(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    private void FillAvisoLoca(JTable table, String sLoca) {
        float fTotCred = 0, fTotDeb = 0;

        // Seta Cabecario
        TableControl.header(table, new String[][] {{"desc","valor"},{"380","90"}});

        String sSql = FuncoesGlobais.Subst("SELECT campo FROM avisos WHERE registro = '&1.' AND rid = '4';", new String[] {sLoca});
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (imResult.next()) {
                String tmpCampo = imResult.getString("campo");
                String rCampos[][] = FuncoesGlobais.treeArray(tmpCampo, false);

                String sInq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" +
                              rCampos[0][7].substring(2, 4) + "/" + rCampos[0][7].substring(4, 8);

                if (!"".equals(sInq.trim())) {
                    String aLinhas[] = WordWrap.wrap(sInq, 38).split("\n");

                    for (int l =0;l<aLinhas.length;l++) {
                        String desc = ""; String valor = "";

                        if (l == aLinhas.length - 1) {
                            desc = aLinhas[l];
                            valor = ("CRE".equals(rCampos[0][8]) ? "" : "-") + LerValor.FormatNumber(rCampos[0][2], 2);
                        } else {
                            desc = aLinhas[l];
                            valor = "";
                        }

                        TableControl.add(table, new String[][]{{desc, valor},{"L","R"}}, true);
                    }
                }

                if ("CRE".equals(rCampos[0][8])) {
                    fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2], 2));
                } else {
                    fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2], 2));
                }
            }
            TableControl.add(table, new String[][]{{"Total Geral =>", LerValor.floatToCurrency(fTotCred - fTotDeb, 2)},{"R","R"}}, true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);

    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtbLcReten;
    // End of variables declaration//GEN-END:variables
}