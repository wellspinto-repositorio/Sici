/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jCarteira.java
 *
 * Created on 08/02/2011, 10:35:53
 */

package Sici.Locatarios;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Protocolo.DepuraCampos;
import Sici.Partida.jAutoriza;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author supervisor
 */
public final class jCarteira extends javax.swing.JDialog {
    DbMain conn = VariaveisGlobais.conexao;
    boolean mCartVazio = false;
    boolean bIncluir = false;
    String mContrato = "";
    
    /** Creates new form jCarteira */
    public jCarteira(java.awt.Frame parent, boolean modal) throws SQLException, ParseException {
        super(parent, modal);
        initComponents();

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        MontaTela();
        setLocationRelativeTo(null);   
        
        boolean bini = false;
        boolean bfim = false;
        boolean bvec = false;
        boolean bure = false;

        try {bini = jDtInicio.getDate().toString().trim().equalsIgnoreCase("") ? true : false;} catch (Exception e) {bini=true;}
        try {bfim = jDtTermino.getDate().toString().trim().equalsIgnoreCase("") ? true : false;} catch (Exception e) {bfim=true;}
        try {bvec = jDtVencto.getDate().toString().trim().equalsIgnoreCase("") ? true : false;} catch (Exception e) {bvec=true;}
        try {bure = jDtUltRec.getDate().toString().trim().equalsIgnoreCase("") ? true : false;} catch (Exception e) {bure=true;}

        boolean ecaixa = VariaveisGlobais.funcao.trim().toUpperCase().equalsIgnoreCase("CAIXA");
        if (ecaixa) {
            jDtInicio.setEnabled(bini);
            jDtTermino.setEnabled(bfim);
            jDtAdito.setEnabled(bfim);
            jDtVencto.setEnabled(bvec);
            jDtUltRec.setEnabled(bure);
            jAltData.setEnabled(true);
            jAltData.setVisible(true);
        } else {
            jDtInicio.setEnabled(true);
            jDtTermino.setEnabled(true);
            jDtAdito.setEnabled(true);
            jDtVencto.setEnabled(true);
            jDtUltRec.setEnabled(true);
            jAltData.setEnabled(false);
            jAltData.setVisible(true);
        }

    }

    public jCarteira(String lContrato, java.awt.Frame parent, boolean modal) throws SQLException, ParseException {
        super(parent, modal);
        initComponents();

        this.mContrato = lContrato;
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        MontaTela();
        setLocationRelativeTo(null);           

        boolean bini = false;
        boolean bfim = false;
        boolean bvec = false;
        boolean bure = false;

        try {bini = jDtInicio.getDate().toString().trim().equalsIgnoreCase("") ? true : false;} catch (Exception e) {bini=true;}
        try {bfim = jDtTermino.getDate().toString().trim().equalsIgnoreCase("") ? true : false;} catch (Exception e) {bfim=true;}
        try {bvec = jDtVencto.getDate().toString().trim().equalsIgnoreCase("") ? true : false;} catch (Exception e) {bvec=true;}
        try {bure = jDtUltRec.getDate().toString().trim().equalsIgnoreCase("") ? true : false;} catch (Exception e) {bure=true;}

        boolean ecaixa = VariaveisGlobais.funcao.trim().toUpperCase().equalsIgnoreCase("CAIXA");
        if (ecaixa) {
            jDtInicio.setEnabled(bini);
            jDtTermino.setEnabled(bfim);
            jDtAdito.setEnabled(bfim);
            jDtVencto.setEnabled(bvec);
            jDtUltRec.setEnabled(bure);
            jAltData.setEnabled(true);
        } else {
            jDtInicio.setEnabled(true);
            jDtTermino.setEnabled(true);
            jDtAdito.setEnabled(true);
            jDtVencto.setEnabled(true);
            jDtUltRec.setEnabled(true);
            jAltData.setEnabled(false);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPnlDados = new javax.swing.JPanel();
        jDtTermino = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel2 = new javax.swing.JLabel();
        jDtInicio = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jDtVencto = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel4 = new javax.swing.JLabel();
        jDtUltRec = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jDtAdito = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel5 = new javax.swing.JLabel();
        jScroll = new javax.swing.JScrollPane();
        jctCampo = new javax.swing.JPanel();
        jbtInserir = new javax.swing.JButton();
        jbtGravar = new javax.swing.JButton();
        jbtRetornar = new javax.swing.JButton();
        jAltData = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Carteira ::.");
        setName("jCarteira"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPnlDados.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPnlDados.setMaximumSize(new java.awt.Dimension(585, 72));
        jPnlDados.setMinimumSize(new java.awt.Dimension(585, 72));
        jPnlDados.setPreferredSize(new java.awt.Dimension(585, 72));

        jLabel2.setText("Termino:");

        jLabel1.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel1.setText("Início:");

        jLabel3.setText("Vencimento:");

        jLabel4.setText("Ultimo Recebimento:");

        jLabel5.setText("Aditamento:");

        javax.swing.GroupLayout jPnlDadosLayout = new javax.swing.GroupLayout(jPnlDados);
        jPnlDados.setLayout(jPnlDadosLayout);
        jPnlDadosLayout.setHorizontalGroup(
            jPnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPnlDadosLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(1, 1, 1)
                        .addComponent(jDtInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(4, 4, 4)
                        .addComponent(jDtTermino, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPnlDadosLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDtVencto, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(17, 17, 17)))
                .addGroup(jPnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jDtAdito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDtUltRec, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPnlDadosLayout.setVerticalGroup(
            jPnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPnlDadosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDtInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDtTermino, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDtAdito, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDtVencto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDtUltRec, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jScroll.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Campos"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScroll.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        jctCampo.setEnabled(false);
        jctCampo.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N

        javax.swing.GroupLayout jctCampoLayout = new javax.swing.GroupLayout(jctCampo);
        jctCampo.setLayout(jctCampoLayout);
        jctCampoLayout.setHorizontalGroup(
            jctCampoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 595, Short.MAX_VALUE)
        );
        jctCampoLayout.setVerticalGroup(
            jctCampoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 342, Short.MAX_VALUE)
        );

        jScroll.setViewportView(jctCampo);

        jbtInserir.setText("Inserir Campos");
        jbtInserir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtInserirActionPerformed(evt);
            }
        });

        jbtGravar.setText("Gravar Campos");
        jbtGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtGravarActionPerformed(evt);
            }
        });

        jbtRetornar.setText("Retornar");
        jbtRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRetornarActionPerformed(evt);
            }
        });

        jAltData.setText("Alterar Datas");
        jAltData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAltDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jbtInserir, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69)
                        .addComponent(jAltData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtRetornar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPnlDados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
                    .addComponent(jScroll, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jbtInserir)
                    .addComponent(jbtGravar)
                    .addComponent(jbtRetornar)
                    .addComponent(jAltData))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtInserirActionPerformed
        bIncluir = true;
        try {
            if (InsereCampos()) {
                DesMontaTela();
                MontaTela();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jbtInserirActionPerformed

    private void jbtGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtGravarActionPerformed
        if (GravarCampos()) this.dispose();
    }//GEN-LAST:event_jbtGravarActionPerformed

    private boolean  ChecaVecto() {
        String sqlVer = "SELECT * FROM RECIBO WHERE contrato = '" + mContrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", jDtVencto.getDate()) + "' ORDER BY dtvencimento DESC LIMIT 1;";
        ResultSet rsVer = conn.AbrirTabela(sqlVer, ResultSet.CONCUR_READ_ONLY);
        boolean bexist = DbMain.RecordCount(rsVer) > 0;
        DbMain.FecharTabela(rsVer);
        if (!bexist) return true;
        JOptionPane.showMessageDialog(this, "Este vencimento já foi gerado!!!");
        jDtVencto.requestFocus();
        return false;
    }
    
    private boolean GravarCampos() {
        if ("".equals(VariaveisGlobais.ccampos.trim())) return true;
        if (!ChecaVecto()) return false;
        
        String cCampos[] = VariaveisGlobais.ccampos.split(";");
        Arrays.sort (cCampos, new Comparator()
        {
            private int pos1 = 3;
            private int pos2 = 4;
            public int compare(Object o1, Object o2) {
                String p1 = ((String)o1).substring(pos1, pos2);
                String p2 = ((String)o2).substring(pos1, pos2);
                return p1.compareTo(p2);
            }
        });

        int i = 0; int maxcpos = jctCampo.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = "";
        for (i=0; i <= maxcpos - 1; i++) {
            if (jctCampo.getComponent(i) instanceof JLabel) {
                saida = ((JLabel) jctCampo.getComponent(i)).getText();
                nmCampo = ((JLabel) jctCampo.getComponent(i)).getName();
            } else if (jctCampo.getComponent(i) instanceof JFormattedTextField) {
                saida = ((JFormattedTextField) jctCampo.getComponent(i)).getText();
                nmCampo = ((JFormattedTextField) jctCampo.getComponent(i)).getName();
            } else if (jctCampo.getComponent(i) instanceof JTextField) {
                saida = ((JTextField) jctCampo.getComponent(i)).getText();
                nmCampo = ((JTextField) jctCampo.getComponent(i)).getName();
            } else if (jctCampo.getComponent(i) instanceof JCheckBox) {
                boolean simnao = ((JCheckBox) jctCampo.getComponent(i)).isSelected();
                saida = (simnao ? "TRUE" : "FALSE");
                nmCampo = ((JCheckBox) jctCampo.getComponent(i)).getName();
            } else saida = "";
            //System.out.println(saida + " -> " + jctCampo.getComponent(i).getName());

            if (nmCampo.contains("Field")) {
                cCampos[j] = ChangeCampos(cCampos[j],2,saida);
            } else if (nmCampo.contains("Check")) {
                cCampos[j] = ChangeCampos(cCampos[j], -2, saida);
            } else if (nmCampo.contains("Ant")) {
                cCampos[j] = ChangeCampos(cCampos[j], 4, saida);
            } else if (nmCampo.contains("Cota")) {
                cCampos[j] = ChangeCampos(cCampos[j], 3, saida);
            } else if (nmCampo.contains("Barras")) {
                cCampos[j] = ChangeCampos(cCampos[j], -1, saida);
            }

            int mod = (i + 1) % 6;
            if (mod == 0) {
                j++;
                //System.out.println(new Pad("-",80,"-").CPad());
            }
        }

        // Compacta
        while (true) {
            int pos = FuncoesGlobais.IndexOfPart(cCampos, ":0000000000:", 4,16);
            if (pos == -1) break;
            cCampos = FuncoesGlobais.ArrayDel(cCampos, pos);
        }

        SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
        String tDtVecto = "00-00-0000";
        try {
            tDtVecto = fm.format(jDtVencto.getDate()).toString();
        } catch (Exception ex) { ex.printStackTrace(); }

        int sDia = Integer.parseInt(tDtVecto.substring(0, 2));
        if (cCampos.length != 0) {
            int npos = cCampos[0].indexOf(":DV");
            if (npos > -1 && sDia > 28) {
                cCampos[0] = cCampos[0].substring(0, npos) + ":DV" + FuncoesGlobais.StrZero(String.valueOf(sDia), 2) + cCampos[0].substring(npos + 5);
            } else if (npos > -1 && sDia <= 28) {
                cCampos[0] = cCampos[0].substring(0, npos) + cCampos[0].substring(npos + 5);
            } else {
                int ipos = FuncoesGlobais.OcourCount(cCampos[0], ":", 4);
                if (ipos > -1) {
                    if (sDia > 28) cCampos[0] = cCampos[0].substring(0, ipos + 1) + "DV" + FuncoesGlobais.StrZero(String.valueOf(sDia), 2) + cCampos[0].substring(ipos);
                }
            }
        }

        String fCampos = "";
        if (cCampos.length != 0) fCampos = FuncoesGlobais.join(cCampos, ";"); else fCampos = "";

        // Gravar CARTEIRA
        SimpleDateFormat fm2 = new SimpleDateFormat("dd/MM/yyyy");
        String gDtVecto = "00-00-0000";
        try {
            gDtVecto = fm2.format(jDtVencto.getDate()).toString();
        } catch (Exception ex) {}
        String gDtInicio = "00-00-0000";
        try {
            gDtInicio = fm2.format(jDtInicio.getDate()).toString();
        } catch (Exception ex) {}
        String gDtTermino = "00-00-0000";
        try {
            gDtTermino = fm2.format(jDtTermino.getDate()).toString();
        } catch (Exception ex) {}

        String gDtAdito = "";
        try {
            gDtAdito = Dates.DateFormata("dd-MM-yyyy", jDtAdito.getDate());
        } catch(Exception e) {}

        String gDtUltRec = "";
        try {
            gDtUltRec = Dates.DateFormata("dd-MM-yyyy", jDtUltRec.getDate());
        } catch(Exception e) {}
        
        boolean retorno = false;
        String smsg = FuncoesGlobais.ValidaProtocol(fCampos);
        if ("".equals(smsg)) { 
            /* String SQLtxt = "UPDATE CARTEIRA SET DTINICIO = '" + gDtInicio + "', CAMPO = '" + fCampos + "', DTTERMINO = '" +
                 gDtTermino + "', DTVENCIMENTO = '" + gDtVecto + "', DTULTRECEBIMENTO = '" + gDtUltRec + "' WHERE CONTRATO = '" + VariaveisGlobais.ccontrato + "';"; */
            String SQLtxt = "UPDATE CARTEIRA SET DTINICIO = '" + gDtInicio + "', CAMPO = '" + fCampos + "', DTTERMINO = '" +
                 gDtTermino + "', DTADITO = '" + gDtAdito + "', DTVENCIMENTO = '" + gDtVecto + "' WHERE CONTRATO = '" + this.mContrato + "';";
            retorno = (conn.ExecutarComando(SQLtxt) > 0);
        } else {
            JOptionPane.showMessageDialog(null, smsg , "Error", JOptionPane.ERROR_MESSAGE);            
        }
        
        jDtInicio.setEnabled(true);
        jDtTermino.setEnabled(true);
        jDtAdito.setEnabled(true);
        jDtVencto.setEnabled(true);
        jDtUltRec.setEnabled(true);
        
        return retorno;
    }

    private void jbtRetornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRetornarActionPerformed
        if (bIncluir) {
            if (GravarCampos()) this.dispose();
        } else {
            this.dispose();
        }
    }//GEN-LAST:event_jbtRetornarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (bIncluir) {
            if (GravarCampos()) this.dispose();
        } else {
            this.dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void jAltDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAltDataActionPerformed
        jAutoriza uAut = new jAutoriza(null, true);
        boolean ecaixa = VariaveisGlobais.funcao.trim().toUpperCase().equalsIgnoreCase("CAIXA");
        boolean pode = false;
        if (ecaixa) {
            uAut.setVisible(true);
            pode = uAut.pode;

            if (pode) {
                jDtInicio.setEnabled(true);
                jDtTermino.setEnabled(true);
                jDtVencto.setEnabled(true);
                jDtUltRec.setEnabled(true);
            }
        }
        if (pode || !ecaixa) {
            jDtInicio.setEnabled(true);
            jDtTermino.setEnabled(true);
            jDtVencto.setEnabled(true);
            jDtUltRec.setEnabled(true);
        }
        uAut = null;
        jAltData.setVisible(false);
    }//GEN-LAST:event_jAltDataActionPerformed

    public String ChangeCampos(String campo, int pos, String valor) {
        String[] aCampo = campo.split(":");

        if (pos == 2) {
            // Altera valor no protocolo
            String newValor = FuncoesGlobais.GravaValor(valor);
            aCampo[pos] = newValor;
        } else if (pos == 3) {
            // Altera Cota/Parcela
            aCampo[pos] = valor.replace("/", "");
        } else if (pos == -1) {
            int npos = FuncoesGlobais.IndexOf(aCampo, "CB");
            if (npos > -1) {

                if (!"".equals(valor)) {
                    aCampo[npos] = "CB" + valor;
                } else aCampo[npos] = "X";

            } else {
                if (!"".equals(valor)) {
                    aCampo = FuncoesGlobais.ArrayAdd(aCampo, "CB"+valor);
                }
            }
        } else if (pos == -2) {
            int npos = FuncoesGlobais.IndexOf(aCampo, "RT");
            if (npos > -1) {
                if (!"".equals(valor)) {
                    aCampo[npos] = ("TRUE".equals(valor) ? "RT" : "X");
                } else aCampo[npos] = "X";
            } else {
                if (!"".equals(valor)) {
                    if ("TRUE".equals(valor)) {
                        aCampo = FuncoesGlobais.ArrayAdd(aCampo, "RT");
                    }
                }
            }
        } else if (pos == 4) {
            int npos = FuncoesGlobais.IndexOf(aCampo, "AT");
            if (npos > -1) {
                if (!"".equals(valor)) {
                    aCampo[npos] = ("TRUE".equals(valor) ? "AT" : "X");
                } else aCampo[npos] = "X";
            } else {
                if (!"".equals(valor)) {
                    if ("TRUE".equals(valor)) {
                        aCampo = FuncoesGlobais.ArrayAdd(aCampo, "AT");
                    }
                }
            }
        }

        String mCampo = FuncoesGlobais.join(aCampo, ":");
        mCampo = mCampo.replaceAll("X:", "");
        mCampo = mCampo.replaceAll("X", "");
        if (":".equals(mCampo.substring(mCampo.length() - 1, mCampo.length()))) {
            mCampo = mCampo.substring(0, mCampo.length() - 1);
        }
        
        mCampo = mCampo.replaceAll("::", ":");
        return mCampo;
    }

    public boolean InsereCampos() throws SQLException {
        String[] gCampos = VariaveisGlobais.ccampos.split(";");
        int j = 0; boolean bRet = false;
        String sqlWhere = ""; String sqlSelect = "";
        if (!"".equals(VariaveisGlobais.ccampos.trim())) {
            for (j = 0; j <= gCampos.length - 1; j++) {
              sqlWhere = sqlWhere + "CART_CODIGO <> '" + gCampos[j].substring(0,2) + "' AND ";
            }
        }

        if (!"".equals(sqlWhere.trim())) {
          sqlWhere = "WHERE " + sqlWhere.substring(0, sqlWhere.length() - 5);
        }
        sqlSelect = "SELECT LANCART.CART_CODIGO, " +
                  "LANCART.CART_DESCR, LANCART.CART_ORDEM, LANCART.CART_CONTEUDO, " +
                  "LANCART.CART_COTPAR, LANCART.CART_TAXA, LANCART.CART_FEDERAL, LANCART.CART_MODIFICA, " +
                  "LANCART.CART_FIXO, LANCART.CART_RETENCAO, LANCART.CART_RECIBO, " +
                  "LANCART.CART_EXTRATO, LANCART.CART_IMPOSTO, LANCART.CART_ANTECIPA FROM LANCART " + sqlWhere;

        String cartCampos = null; String stCAMPOS = null; String mSql = null;
        cartCampos = CarregaCamposCarteira(sqlSelect);

        if (!"".equals(cartCampos.trim())) {
            stCAMPOS = VariaveisGlobais.ccampos + ";" + cartCampos;
            if (stCAMPOS.substring(0, 1).equals(";")) {
              stCAMPOS = stCAMPOS.substring(1);
            }

            if (!mCartVazio)
                //mSql = "UPDATE CARTEIRA SET CAMPO = '" + stCAMPOS + "' WHERE CONTRATO = '" + VariaveisGlobais.ccontrato + "';";
                mSql = "UPDATE CARTEIRA SET CAMPO = '" + stCAMPOS + "' WHERE CONTRATO = '" + this.mContrato + "';";

            else {
                //String[][] vCampos = conn.LerCamposTabela(new String[] {"rgprp", "rgimv", "contrato"}, "locatarios", "contrato = '" + VariaveisGlobais.ccontrato + "'");
                String[][] vCampos = conn.LerCamposTabela(new String[] {"rgprp", "rgimv", "contrato"}, "locatarios", "contrato = '" + this.mContrato + "'");
                String rgprp = vCampos[0][3];
                String rgimv = vCampos[1][3];
                String contrato = vCampos[2][3];

                /* mSql = "INSERT INTO CARTEIRA (rgprp,rgimv,contrato,campo) " +
                   "VALUES ('" + rgprp + "','" + rgimv + "','" + VariaveisGlobais.ccontrato +
                   "','" + stCAMPOS + "');"; */
                mSql = "INSERT INTO CARTEIRA (rgprp,rgimv,contrato,campo) " +
                   "VALUES ('" + rgprp + "','" + rgimv + "','" + this.mContrato +
                   "','" + stCAMPOS + "');";
            }
            if (conn.ExecutarComando(mSql) > 0 ) {
                bRet = true;
            }
        }

        return bRet;
    }

    public String CarregaCamposCarteira(String cSql) throws SQLException {
        ResultSet Data1 = conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
        String mCampos = "";

        while (Data1.next()) {
//            mCampos = mCampos + Data1.getString("CART_CODIGO") + ":" +
//                       Data1.getString("CART_ORDEM") + ":" +
//                       "0000000000" + ":" +
//                       "0000" + ":" +
//                       ("1".equals(Data1.getString("CART_TAXA")) ? "NT" : "AL") + ":" +
//                       ("1".equals(Data1.getString("CART_RETENCAO")) ? "RT" : "") + ":" +
//                       ("1".equals(Data1.getString("CART_RECIBO")) ? "RZ" : "") + ":" +
//                       ("1".equals(Data1.getString("CART_EXTRATO")) ? "ET" : "") + ":" +
//                       ("1".equals(Data1.getString("CART_IMPOSTO")) ?  "IP" : "") + ";";

            mCampos = mCampos + Data1.getString("CART_CODIGO") + ":" +
                       Data1.getString("CART_ORDEM") + ":" +
                       "0000000000" + ":" +
                       "0000" + ":" +
                       ("1".equals(Data1.getString("CART_TAXA")) ? "NT" : "AL") + ":" +
                       ("1".equals(Data1.getString("CART_RETENCAO")) ? ("1".equals(Data1.getString("CART_MODIFICA")) ? "RT" : "") : "") + ":" +
                       ("1".equals(Data1.getString("CART_ANTECIPA")) ? ("1".equals(Data1.getString("CART_MODIFICA")) ? "AT" : "") : "") + ":" +
                       ("1".equals(Data1.getString("CART_RECIBO")) ? "RZ" : "") + ":" +
                       ("1".equals(Data1.getString("CART_EXTRATO")) ? "ET" : "") + ":" +
                       ("1".equals(Data1.getString("CART_IMPOSTO")) ?  "IP" : "") + ";";
                       //(!"P".equals(Data1.getString("CART_COTPAR")) ? "0000" : "000000") + ":" +
        }
        
        DbMain.FecharTabela(Data1);

        if (!"".equals(mCampos.trim())) {
            mCampos = mCampos.replaceAll("::", ":");
            mCampos = mCampos.replaceAll(":;", ";");

            mCampos = mCampos.substring(0, mCampos.length() - 1);
        }
        return mCampos;
    }

    public void DesMontaTela() {
        jctCampo.removeAll();
    }

    public void MontaTela() throws SQLException {
        VariaveisGlobais.ccampos = "";
        //String sql = "SELECT * FROM CARTEIRA WHERE contrato = '" + VariaveisGlobais.ccontrato + "';";
        String sql = "SELECT * FROM CARTEIRA WHERE contrato = '" + this.mContrato + "';";
        DbMain conn = VariaveisGlobais.conexao;
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (pResult.first()) {
            // Dados basicos
            //java.sql.Date tDtInicio = null;
            Date tDtInicio = null;
            try {
                //tDtInicio = new java.sql.Date(formatter.parse(pResult.getString("dtinicio")).getTime());
                tDtInicio = Dates.StringtoDate(pResult.getString("dtinicio"), "dd-MM-yyyy");
            } catch (SQLException ex) { ex.printStackTrace(); } catch (Exception ex) {tDtInicio = null; ex.printStackTrace();}
            jDtInicio.setDate(tDtInicio);

            //java.sql.Date tDtTermino = null;
            Date tDtTermino = null;
            try {
                //tDtTermino = new java.sql.Date(formatter.parse(pResult.getString("dttermino")).getTime());
                tDtTermino = Dates.StringtoDate(pResult.getString("dttermino"), "dd-MM-yyyy");
            } catch (SQLException ex) { ex.printStackTrace(); } catch (Exception ex) {tDtTermino = null; ex.printStackTrace();}
            jDtTermino.setDate(tDtTermino);

            Date tDtAdito = null;
            try {
                tDtAdito = Dates.StringtoDate(pResult.getString("dtadito"), "dd-MM-yyyy");
            } catch (SQLException ex) { ex.printStackTrace(); } catch (Exception ex) {tDtAdito = null; ex.printStackTrace();}
            jDtAdito.setDate(tDtAdito);

            //java.sql.Date tDtVencimento = null;
            Date tDtVencimento = null;
            try {
                //tDtVencimento = new java.sql.Date(formatter.parse(pResult.getString("dtvencimento")).getTime());
                tDtVencimento = Dates.StringtoDate(pResult.getString("dtvencimento"), "dd-MM-yyyy");
            } catch (SQLException ex) { ex.printStackTrace(); } catch (Exception ex) {tDtVencimento = null; ex.printStackTrace();}
            jDtVencto.setDate(tDtVencimento);

            try {
                jDtUltRec.setDate(Dates.StringtoDate(pResult.getString("dtultrecebimento"),"dd/MM/yyyy"));
            } catch (Exception e) { e.printStackTrace(); }
            
            String nCampos = pResult.getString("campo");

            if (!"".equals(nCampos.trim())) {
                DepuraCampos a = new DepuraCampos(nCampos);
                VariaveisGlobais.ccampos = pResult.getString("campo");

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

                int i = 0;
                for (i=0; i<= a.length() - 1; i++) {
                    String[] Campo = a.Depurar(i);
                    if (Campo.length > 0) {
                        MontaCampos(Campo, i);
                    }
                }
                mCartVazio = false;
            } else {
                VariaveisGlobais.ccampos = "";
                mCartVazio = false;
            }
        } else {
            VariaveisGlobais.ccampos = "";
            mCartVazio = true;
        }

        DbMain.FecharTabela(pResult);
        ///////////////////////conn.FecharConexao();
    }

    private void MontaCampos(String[] aCampos, int i) {
        int at = 20; int llg = 100; int ltf = 80; int lcp = 60; int lcc = 180;

        JLabel lb = new JLabel();
        lb.setText(aCampos[0]);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(Color.BLACK);
        lb.setBounds(0, 0 + (at * i), llg, at);
        lb.setName("Label" + i);
        jctCampo.add(lb);

        JFormattedTextField tf = new JFormattedTextField();
        tf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        tf.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        tf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tf.setText(aCampos[1]);
        tf.setVisible(true);
        tf.setForeground(Color.BLACK);
        tf.setBounds(lb.getX() + llg, 0 + (at * i), ltf, at);
        tf.setName("Field" + i);
        jctCampo.add(tf);

        JCheckBox cb = new JCheckBox();
        cb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        //tf.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        
        String modifica[][] = null;
        try {modifica = conn.LerCamposTabela(new String[] {"CART_MODIFICA"}, "LANCART", "CART_DESCR = '" + aCampos[0] + "'");} catch (Exception e) {}        
        if (modifica == null) {
            if ("A".equalsIgnoreCase(aCampos[7])) {
                cb.setSelected(false);
                cb.setEnabled(false);
            } else {
                cb.setSelected(("0".equals(aCampos[2]) ? false : true));
                cb.setEnabled(true);
            }
        } else if (modifica[0][2].equals("0")) {
            if ("A".equalsIgnoreCase(aCampos[7])) {
                cb.setSelected(false);
                cb.setEnabled(false);
            } else {
                cb.setSelected(false);
                cb.setEnabled(true);
            }
        } else {
            if ("A".equalsIgnoreCase(aCampos[7])) {
                cb.setSelected(false);
                cb.setEnabled(false);
            } else {
                cb.setSelected(("0".equals(aCampos[2]) ? false : true));
                cb.setEnabled(true);
            }
        }
        cb.setVisible(true);
        cb.setForeground(Color.BLACK);
        cb.setBounds(tf.getX() + ltf + 5, 0 + (at * i), at, at);
        cb.setName("Check" + i);
        jctCampo.add(cb);
        
        JCheckBox tt = new JCheckBox();
        tt.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        try {modifica = conn.LerCamposTabela(new String[] {"CART_ANTECIPA"}, "LANCART", "CART_DESCR = '" + aCampos[0] + "'");} catch (Exception e) {}        
        if (modifica == null) {
            tt.setSelected(("0".equals(aCampos[6]) ? false : true));
        } else if (modifica[0][2].equals("0")) {
            tt.setSelected(false);
        } else {
            tt.setSelected(("0".equals(aCampos[6]) ? false : true));
        }
        tt.setVisible(true);
        tt.setForeground(Color.BLACK);
        tt.setBounds(cb.getX() + at + 10, 0 + (at * i), lcp, at);
        tt.setName("Ant" + i);
//        tt.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                (JCheckBox)"Check" + evt.getClass().getName().substring(3)
//            }
//        });
        jctCampo.add(tt);

        JFormattedTextField cp = new JFormattedTextField();
        try {
            //cp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter(("C".equals(aCampos[5]) ? "##/####" : "##/##"))));
            cp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##")));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        cp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        cp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cp.setText(aCampos[3]);
        cp.setVisible(true);
        cp.setForeground(Color.BLACK);
        cp.setBounds(tt.getX() + at + 10, 0 + (at * i), lcp, at);
        cp.setName("Cota" + i);
        cp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                String scpo = ((JFormattedTextField)evt.getComponent()).getText().trim();
                DefaultFormatterFactory dff = (DefaultFormatterFactory) ((JFormattedTextField)evt.getComponent()).getFormatterFactory();
                String sMask = ((MaskFormatter)dff.getDefaultFormatter()).getMask();
               
                if ("/".equals(scpo) || scpo.length() < sMask.length()) {
                    ((JFormattedTextField)evt.getComponent()).setText("00/" + (sMask.length() == 5 ? "00" : "0000"));
                } 
                
                if (VariaveisGlobais.scroll) {
                    java.awt.Rectangle rect = ((JFormattedTextField)evt.getComponent()).getBounds();
                    jScroll.getViewport().setViewPosition(new Point(0,(int)rect.getY() + 20));
                }
            }
        });
        jctCampo.add(cp);

        JTextField cc = new JTextField();
        cc.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        cc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cc.setText(aCampos[4]);
        cc.setVisible(true);
        cc.setForeground(Color.BLACK);
        cc.setBounds(cp.getX() + lcp + 15, 0 + (at * i), lcc, at);
        cc.setName("Barras" + i);
        jctCampo.add(cc);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jCarteira dialog = null;
                try {
                    try {
                        dialog = new jCarteira(new javax.swing.JFrame(), true);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAltData;
    private com.toedter.calendar.JDateChooser jDtAdito;
    private com.toedter.calendar.JDateChooser jDtInicio;
    private com.toedter.calendar.JDateChooser jDtTermino;
    private com.toedter.calendar.JDateChooser jDtUltRec;
    private com.toedter.calendar.JDateChooser jDtVencto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPnlDados;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JButton jbtGravar;
    private javax.swing.JButton jbtInserir;
    private javax.swing.JButton jbtRetornar;
    private javax.swing.JPanel jctCampo;
    // End of variables declaration//GEN-END:variables

}
