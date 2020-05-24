/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jAviso.java
 *
 * Created on 23/05/2011, 12:17:44
 */
package Movimento;

import Funcoes.Autenticacao;
import Funcoes.AutoCompletion;
import Funcoes.ComponentSearch;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.JavaPOS;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.PrinterPOS;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.WordWrap;
import Funcoes.jTableControl;
import Funcoes.newTable;
import Funcoes.toPrint;
import Transicao.jReceber;
import Sici.Partida.Collections;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


/**
 *
 * @author supervisor
 */
public class jAvisoPre extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String rgprp = ""; String rgimv = ""; String contrato = ""; String rcampo = "";
    boolean executando = false; boolean mCartVazio = false;
    jTableControl tabela = new jTableControl(true);
    boolean bExecNome = false, bExecCodigo = false;
    TableRowSorter<TableModel> sorter;
    
    jReceber tRec = new jReceber();
    JPanel pnlDigite = (JPanel) tRec.getComponent(ComponentSearch.ComponentSearch(tRec, "jpnDigite"));
    JButton btnLancar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtLancar"));
    JButton btnCancelar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtCancelar"));
    JPanel pnlBotoes = (JPanel) tRec.getComponent(ComponentSearch.ComponentSearch(tRec, "pnlBotoes"));
    JToggleButton btDN = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgDN"));
    JToggleButton btCH = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCH"));
    JToggleButton btCT = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCT"));
    JFormattedTextField jResto = (JFormattedTextField) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "JRESTO"));

    private void InitjReceber() {
        tRec.setVisible(true);
        tRec.setEnabled(true);
        tRec.setBounds(0, 0, 480, 250);
        try {
            jpRecebe.add(tRec);
        } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace(); }
        jpRecebe.repaint();
        jpRecebe.setEnabled(true);
        tRec.acao = "AV";

        btnLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tRec.Lancar();
                if (tRec.bprintdoc) {
                    try {
                        try {
                            Imprimir();
                            
                            jbtTipo.setEnabled(true);

                            jValor.setText("0,00");
                            jValor.setEnabled(true);
                            tRec.Cancelar();
                            
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tRec.Cancelar()) {
                    tRec.LimpaCampos();
                    jbtTipo.setEnabled(true);

                    jValor.setText("0,00");
                    jValor.setEnabled(true);
                    
                    jbtTipo.requestFocus();
                } else tRec.LimpaCampos();
            } 
        });

        btDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValor.setEnabled(false);
            }
        });

        btCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValor.setEnabled(false);
            }
        });

        btCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValor.setEnabled(false);
            }
        });

    }

    /** Creates new form jAviso */
    public jAvisoPre() {
        initComponents();

        conn.CriarMySqlProcedures("RETAVUSERRID2", "");
        
        InitjReceber();

        // Disponivel
        FillCombos();
        AutoCompletion.enable(jRgprp);
        AutoCompletion.enable(jNomeProp);

        // Sócios
        FillSocios();
        AutoCompletion.enable(jSocios);

        // Locatários
        FillLoca();
        AutoCompletion.enable(jContr);
        AutoCompletion.enable(jNmLoc);

        // Adm
        FillAdm();
        AutoCompletion.enable(jAdm);

        tRec.btEnabled(false);
        btnCancelar.setEnabled(false);
        btnLancar.setEnabled(false);
        
        // Fill Contas Controle
        FillContas();
        AutoCompletion.enable(jIdConta);
        AutoCompletion.enable(jnmConta);
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jAbas = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jNomeProp = new javax.swing.JComboBox();
        jRgprp = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtxtDisp = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jAdm = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtxtAdm = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSocios = new javax.swing.JComboBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtxtSoc = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jContr = new javax.swing.JComboBox();
        jNmLoc = new javax.swing.JComboBox();
        jScrollPane5 = new javax.swing.JScrollPane();
        jtxtLoca = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jtxtConta = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jIdConta = new javax.swing.JComboBox();
        jnmConta = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLanca = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        mDesc = new javax.swing.JTextArea();
        jbtTipo = new javax.swing.JToggleButton();
        jPanel9 = new javax.swing.JPanel();
        jVias = new javax.swing.JCheckBox();
        jValor = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jpRecebe = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Pré - Avisos ::.");
        setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        setVisible(true);

        jAbas.setForeground(new java.awt.Color(0, 0, 204));
        jAbas.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        jAbas.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jAbasStateChanged(evt);
            }
        });

        jNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomePropActionPerformed(evt);
            }
        });

        jRgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRgprpActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel1.setText("Rgprp:");

        jtxtDisp.setColumns(20);
        jtxtDisp.setLineWrap(true);
        jtxtDisp.setRows(5);
        jtxtDisp.setWrapStyleWord(true);
        jtxtDisp.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Especificação do Aviso", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane1.setViewportView(jtxtDisp);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNomeProp, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAbas.addTab("Disponível", jPanel3);

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setText("Cta.Controle:");

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Discriminação para Conta", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane3.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jtxtAdm.setColumns(20);
        jtxtAdm.setLineWrap(true);
        jtxtAdm.setRows(5);
        jtxtAdm.setWrapStyleWord(true);
        jScrollPane3.setViewportView(jtxtAdm);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAdm, 0, 324, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jAdm, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAbas.addTab("ADM", jPanel6);

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel3.setText("Sócio:");

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Discriminação do Aviso", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jtxtSoc.setColumns(20);
        jtxtSoc.setLineWrap(true);
        jtxtSoc.setRows(5);
        jtxtSoc.setWrapStyleWord(true);
        jScrollPane4.setViewportView(jtxtSoc);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSocios, 0, 378, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jSocios, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAbas.addTab("Sócios", jPanel7);

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel4.setText("Contrato:");

        jContr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jContrActionPerformed(evt);
            }
        });

        jNmLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNmLocActionPerformed(evt);
            }
        });

        jScrollPane5.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jtxtLoca.setColumns(20);
        jtxtLoca.setLineWrap(true);
        jtxtLoca.setRows(5);
        jtxtLoca.setWrapStyleWord(true);
        jtxtLoca.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Especificação do Saldo", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane5.setViewportView(jtxtLoca);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jContr, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNmLoc, 0, 248, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jContr, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNmLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAbas.addTab("Locatários", jPanel8);

        jScrollPane6.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jtxtConta.setColumns(20);
        jtxtConta.setLineWrap(true);
        jtxtConta.setRows(5);
        jtxtConta.setWrapStyleWord(true);
        jtxtConta.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Especificação do Saldo", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane6.setViewportView(jtxtConta);

        jLabel7.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel7.setText("Contas:");

        jIdConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jIdContaActionPerformed(evt);
            }
        });

        jnmConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jnmContaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jIdConta, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jnmConta, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jIdConta, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jnmConta, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAbas.addTab("Contas", jPanel1);

        tblLanca.setAutoCreateRowSorter(true);
        tblLanca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblLanca.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblLanca.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLanca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblLancaMouseReleased(evt);
            }
        });
        tblLanca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLancaKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblLanca);

        jLabel10.setText("Descrição:");

        mDesc.setColumns(20);
        mDesc.setRows(5);
        jScrollPane7.setViewportView(mDesc);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                    .addComponent(jScrollPane7)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAbas.addTab("Lançamentos", jPanel2);

        jbtTipo.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jbtTipo.setForeground(new java.awt.Color(0, 153, 0));
        jbtTipo.setText("CRÉDITO");
        jbtTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtTipoActionPerformed(evt);
            }
        });

        jPanel9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jVias.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        jVias.setText("2as Vias");

        jValor.setForeground(new java.awt.Color(51, 102, 0));
        jValor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jValor.setText("0,00");
        jValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jValorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jValorFocusLost(evt);
            }
        });

        jLabel5.setText("Valor:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jVias, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jVias)
                .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel5))
        );

        jpRecebe.setPreferredSize(new java.awt.Dimension(482, 253));

        javax.swing.GroupLayout jpRecebeLayout = new javax.swing.GroupLayout(jpRecebe);
        jpRecebe.setLayout(jpRecebeLayout);
        jpRecebeLayout.setHorizontalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 482, Short.MAX_VALUE)
        );
        jpRecebeLayout.setVerticalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jAbas, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jbtTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtTipo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jAbas, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtTipoActionPerformed
        if (jbtTipo.isSelected()) { 
            jbtTipo.setForeground(new java.awt.Color(204, 51, 0)); 
            jbtTipo.setText("DÉBITO");
        } else { 
            jbtTipo.setForeground(new java.awt.Color(0, 153, 0)); 
            jbtTipo.setText("CRÉDITO"); 
        }
    }//GEN-LAST:event_jbtTipoActionPerformed

    private void jbtTipoStatusUpg(boolean isSelected) {
        if (isSelected) { jbtTipo.setForeground(new java.awt.Color(204, 51, 0)); jbtTipo.setText("DÉBITO");
        } else { jbtTipo.setForeground(new java.awt.Color(0, 153, 0)); jbtTipo.setText("CRÉDITO"); }
    }

    private void jValorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jValorFocusGained
        tRec.LimpaTransicao();
        jResto.setValue(0);
        tRec.vrAREC = LerValor.StringToFloat(jResto.getText());
        tRec.btEnabled(false);
        btnLancar.setEnabled(false);
        btnCancelar.setEnabled(false);
        
        jValor.selectAll();
    }//GEN-LAST:event_jValorFocusGained

    private void jValorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jValorFocusLost
        float iValor = LerValor.StringToFloat(jValor.getText());
        jResto.setValue(iValor);
        tRec.vrAREC = LerValor.StringToFloat(jResto.getText());

        if (jAbas.getSelectedIndex() == 0) {
            rgimv = ""; rgprp = jRgprp.getSelectedItem().toString().trim(); contrato = "";
        } else if (jAbas.getSelectedIndex() == 1) {
            rgimv = ""; rgprp = jAdm.getSelectedItem().toString().trim().substring(0, 2); contrato = "";
        } else if (jAbas.getSelectedIndex() == 2) {
            rgimv = ""; rgprp = jSocios.getSelectedItem().toString().trim().substring(0, 2); contrato = "";
        } else if (jAbas.getSelectedIndex() == 3) {
            rgimv = ""; rgprp = ""; contrato = jContr.getSelectedItem().toString().trim();
        } else if (jAbas.getSelectedIndex() == 4) {
            rgimv = ""; rgprp = jIdConta.getSelectedItem().toString().trim(); contrato = "";
        }

        tRec.rgimv = rgimv; tRec.rgprp = rgprp; tRec.contrato = contrato; tRec.acao = "AV"; tRec.operacao = (jbtTipo.isSelected() ? "DEB" : "CRE");
        jValor.setEnabled(true);
        tRec.btEnabled(true);
        
        jbtTipo.setEnabled(false);
        jValor.setEnabled(false);
        
        btnCancelar.setEnabled(true);
    }//GEN-LAST:event_jValorFocusLost

    private void jAbasStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jAbasStateChanged
        if (jAbas.getSelectedIndex() == 0) {
            // disponível
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

            jtxtDisp.setText("");
            jtxtDisp.requestFocus();

        } else if (jAbas.getSelectedIndex() == 1) {
            // Adm
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

            jtxtAdm.setText("");
            jtxtAdm.requestFocus();

        } else if (jAbas.getSelectedIndex() == 2) {
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

            jtxtSoc.setText("");
            jtxtSoc.requestFocus();

        } else if (jAbas.getSelectedIndex() == 3) {
            // Locatario
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

            jtxtLoca.setText("");
            jtxtLoca.requestFocus();

        } else if (jAbas.getSelectedIndex() == 4) {
            // Contas Controle
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

            jtxtConta.setText("");
            jtxtConta.requestFocus();
        } else if (jAbas.getSelectedIndex() == 5) {
            //tRec.Enable(false);
            FillLanca();
        }
        
        tRec.LimpaTransicao();
        jValor.setEnabled(true);
        jValor.setValue(0);

    }//GEN-LAST:event_jAbasStateChanged

    private void FillLanca() {
        String sql = "SELECT " +
                    "   a.autenticacao, " +
                    "	a.rid, " +
                    "	a.registro, " +
                    "	RetAvDataRid2(a.campo) AS data, " +
                    "	RetAvDescRid2(a.campo) AS descricao, " +
                    "	RetAvValorRid2(a.campo) AS valor, " +
                    "	RetAvTipoRid2(a.campo) AS tipo, " +
                    "	(SELECT c.CX_VRDN FROM jgeral.caixatmp c WHERE c.CX_AUT = a.autenticacao AND c.CX_TIPOPG = 'DN') AS cx_vrdn, " +
                    "	(SELECT c.CX_VRCH FROM jgeral.caixatmp c WHERE c.CX_AUT = a.autenticacao AND c.CX_TIPOPG = 'CH') AS cx_vrch, " +
                    "	(SELECT c.CX_DATA FROM jgeral.caixatmp c WHERE c.CX_AUT = a.autenticacao AND c.CX_TIPOPG = 'CH') AS cx_chdt " +
                    "FROM " +
                    "	avisostmp a " +
                    "WHERE " +
                    "	RetAvUserRid2(a.campo) = '" + VariaveisGlobais.usuario + "';";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        
        String[] headers = new String[] {"autenticacao", "rid", "registro", "data", "descricao", "valor", "tipo", "dn", "ch", "dtpre"};
        int[] widths = new int[] {0,0,80,80,0,100,80,100,100,100};
        String[] aligns = new String[] {"C","C","C","C","L","R","C","R","R","C"};
        newTable.InitTable2(tblLanca, headers, widths, 20, aligns, true);

        try {
            while (rs.next()) {
                String dtpre = "";
                try {
                    dtpre = Dates.DateFormata("dd-MM-yyyy", rs.getDate("cx_chdt"));
                } catch (Exception e) {dtpre = "";}
                
                Object[] linha = {rs.getString("autenticacao"), 
                                  rs.getString("rid"),
                                  rs.getString("registro"),
                                  Dates.DateFormata("dd-MM-yyyy", rs.getDate("data")),
                                  FuncoesGlobais.DecriptaNome(rs.getString("descricao")),
                                  LerValor.floatToCurrency(rs.getFloat("valor"),2),
                                  rs.getString("tipo"),
                                  LerValor.floatToCurrency(rs.getFloat("cx_vrdn"),2),
                                  LerValor.floatToCurrency(rs.getFloat("cx_vrch"),2),
                                  dtpre
                                 };
                newTable.add(tblLanca, linha);
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    
    private void jnmContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jnmContaActionPerformed
        if (!bExecCodigo) {
            int pos = jnmConta.getSelectedIndex();
            if (jIdConta.getItemCount() > 0) {bExecNome = true; jIdConta.setSelectedIndex(pos); bExecNome = false;}
        }
    }//GEN-LAST:event_jnmContaActionPerformed

    private void jIdContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jIdContaActionPerformed
        if (!bExecNome) {
            int pos = jIdConta.getSelectedIndex();
            if (jnmConta.getItemCount() > 0) {bExecCodigo = true; jnmConta.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jIdContaActionPerformed

    private void jNmLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNmLocActionPerformed
        if (!bExecCodigo) {
            int pos = jNmLoc.getSelectedIndex();
            if (jContr.getItemCount() > 0) {bExecNome = true; jContr.setSelectedIndex(pos); bExecNome = false;}
        }
    }//GEN-LAST:event_jNmLocActionPerformed

    private void jContrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jContrActionPerformed
        if (!bExecNome) {
            int pos = jContr.getSelectedIndex();
            if (jNmLoc.getItemCount() > 0) {bExecCodigo = true; jNmLoc.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jContrActionPerformed

    private void jRgprpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRgprpActionPerformed
        if (!bExecNome) {
            int pos = jRgprp.getSelectedIndex();
            if (jNomeProp.getItemCount() > 0) {bExecCodigo = true; jNomeProp.setSelectedIndex(pos); bExecCodigo = false; }
        }
    }//GEN-LAST:event_jRgprpActionPerformed

    private void jNomePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomePropActionPerformed
        if (!bExecCodigo) {
            int pos = jNomeProp.getSelectedIndex();
            if (jRgprp.getItemCount() > 0) {bExecNome = true; jRgprp.setSelectedIndex(pos); bExecNome = false; }
        }
    }//GEN-LAST:event_jNomePropActionPerformed

    private void tblLancaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLancaKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            DelPreAviso();
        } else {
            ShowDetail();
        }
    }//GEN-LAST:event_tblLancaKeyReleased

    private void tblLancaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLancaMouseReleased
        ShowDetail();
    }//GEN-LAST:event_tblLancaMouseReleased

    private void DelPreAviso() {
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(null,
            "Deseja excluir este lançamento ? ",
            "Atenção", JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) {
            int selRow = tblLanca.getSelectedRow();
            if (selRow > -1) {
                int modelRow = tblLanca.convertRowIndexToModel(selRow);
                String taut = tblLanca.getValueAt(modelRow, 0).toString().trim();

                try {
                    String sql = "DELETE FROM avisostmp WHERE autenticacao = '" + taut + "'";
                    conn.ExecutarComando(sql);

                    sql = "DELETE FROM razaotmp WHERE av_aut = '" + taut + "'";
                    conn.ExecutarComando(sql);

                    sql = "DELETE FROM auxiliartmp WHERE rc_aut = '" + taut + "'";
                    conn.ExecutarComando(sql);

                    sql = "DELETE FROM caixatmp WHERE cx_aut = '" + taut + "'";
                    conn.ExecutarComando(sql);

                    sql = "DELETE FROM Chequestmp WHERE ch_autenticacao = '" + taut + "'";
                    conn.ExecutarComando(sql);

                    sql = "DELETE FROM ExtBancotmp WHERE ch_autenticacao = '" + taut + "'";
                    conn.ExecutarComando(sql);

                    TableControl.del(tblLanca, modelRow);
                } catch (Exception e) {e.printStackTrace();}
            }
        }
    }
    
    private void ShowDetail() {
        int selRow = tblLanca.getSelectedRow();
        if (selRow > -1) {
            int modelRow = tblLanca.convertRowIndexToModel(selRow);
            String tDesc = tblLanca.getValueAt(modelRow, 4).toString().trim();
            
            mDesc.setText(tDesc);
        } else {
            mDesc.setText("");
        }
    }
    
    private void FillContas() {
        String sSql = "SELECT autoid, buffer FROM COMBOBUFFER WHERE UPPER(TRIM(local)) = 'CONTAS' ORDER BY Lower(buffer);";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        jIdConta.removeAllItems();
        jnmConta.removeAllItems();
        
        int i = 0;
        try {
            while (imResult.next()) {
                jIdConta.addItem(String.valueOf(imResult.getInt("autoid")));
                jnmConta.addItem(imResult.getString("buffer").toUpperCase());
                i += 1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
        jAbas.setEnabledAt(4, i > 0);
    }

    private void FillCombos() {
        String sSql = "SELECT distinct p.rgprp, p.nome FROM proprietarios p WHERE Upper(p.status) = 'ATIVO' ORDER BY p.rgprp;";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        jRgprp.removeAllItems();
        jNomeProp.removeAllItems();
        try {
            while (imResult.next()) {
                jRgprp.addItem(String.valueOf(imResult.getInt("rgprp")));
                jNomeProp.addItem(imResult.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }

    private void FillLoca() {
        String sSql = "SELECT contrato as registro, nomerazao as nome FROM locatarios WHERE fiador1uf <> 'X' OR IsNull(fiador1uf) ORDER BY lower(nomerazao);";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        jContr.removeAllItems();
        jNmLoc.removeAllItems();
        try {
            while (imResult.next()) {
                jContr.addItem(String.valueOf(imResult.getInt("registro")));
                jNmLoc.addItem(imResult.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }

    private void FillSocios() {
        String sSql = "SELECT autoid, nome FROM SOCIOS ORDER BY autoid;";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        jSocios.removeAllItems();
        try {
            while (imResult.next()) {
                jSocios.addItem(FuncoesGlobais.StrZero(imResult.getString("autoid"), 2) + " - " + imResult.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }

    private void FillAdm() {
        String sSql = "SELECT CODIGO, DESCR FROM ADM ORDER BY AUTOID;";
        ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        jAdm.removeAllItems();
        try {
            while (imResult.next()) {
                jAdm.addItem(FuncoesGlobais.StrZero(imResult.getString("CODIGO"), 2) + " - " + imResult.getString("DESCR"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
    }

    public void Imprimir() throws FileNotFoundException, IOException, SQLException {
        conn.CreateAvisotmp(); 
        conn.CreateRazaotmp();
        conn.CreateAuxiliartmp();

        double nAut = 0;
        float vias = (jVias.isSelected() ? 2 : 1);

        String[][] aTrancicao = tRec.Transicao("AV");
        if (aTrancicao.length <= 0 ) return;

        // Autenticacao
        nAut = Autenticacao.getAut();
        if (!Autenticacao.setAut(nAut, 1)) {
            JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String corpo = ""; String idNome = ""; String idAviso = ""; String idConta = "";
        String sCRDB = (jbtTipo.isSelected() ? "DEBITO" : "CREDITO");
        if (jAbas.getSelectedIndex() == 0) {
            corpo = jtxtDisp.getText();
            idAviso = "AVISO DISPONIVEL - " + sCRDB;
            idNome = jRgprp.getSelectedItem().toString().trim() + " - " + jNomeProp.getSelectedItem().toString().trim();
            idConta = "00PR";
        } else if (jAbas.getSelectedIndex() == 1) {
            corpo = jtxtAdm.getText();
            idAviso = "AVISO ADM - " + sCRDB;
            idNome = jAdm.getSelectedItem().toString().trim();
            idConta = idNome.substring(0, 2);
        } else if (jAbas.getSelectedIndex() == 2) {
            corpo = jtxtSoc.getText();
            idAviso = "AVISO SOCIO - " + sCRDB;
            idNome = jSocios.getSelectedItem().toString().trim();
            idConta = idNome.substring(0, 2);
        } else if (jAbas.getSelectedIndex() == 3) {
            corpo = jtxtLoca.getText();
            idAviso = "AVISO LOCATARIO - " + sCRDB;
            idNome = jContr.getSelectedItem().toString().trim() + " - " + jNmLoc.getSelectedItem().toString().trim();
            idConta = "04LC";
        } else if (jAbas.getSelectedIndex() == 4) {
            corpo = jtxtConta.getText();
            idAviso = "AVISO CONTAS CONTROLE - " + sCRDB;
            idNome = jIdConta.getSelectedItem().toString().trim() + " - " + jnmConta.getSelectedItem().toString().trim();
            idConta = "05CT";
        }
        
        //for (int a=1; a<=vias; a++) {ImprimeAviso((float)nAut, aTrancicao, corpo, jValor.getText(), idNome, idAviso, "F");}

        float abas = -1;
        if (jAbas.getSelectedIndex() == 0) {
            abas = 0;
        } else if (jAbas.getSelectedIndex() == 1) {
            abas = 2;
        } else if (jAbas.getSelectedIndex() == 2) {
            abas = 3;
        } else  if (jAbas.getSelectedIndex() == 3) {
            abas = 4;
        } else if (jAbas.getSelectedIndex() == 4) {
            abas = 5;
        }
        
        String scpAviso = CampoAviso(FuncoesGlobais.StrZero(String.valueOf(nAut), 6) +
                (jAbas.getSelectedIndex() == 1 || jAbas.getSelectedIndex() == 2 ?
                FuncoesGlobais.StrZero(String.valueOf(abas), 2) + idConta :
                idConta), (!jbtTipo.isSelected() ? "CRE:" : "DEB:") + "XX",
                Dates.DateFormata("ddMMyyyy", new Date()),
                FuncoesGlobais.GravaValor(jValor.getText()),
                FuncoesGlobais.CriptaNome(corpo.replaceAll("\r\n", " ")));

        String sCodigo = "";
        if (jAbas.getSelectedIndex() == 0) {
            String tmpTexto = "INSERT INTO avisostmp (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"0", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razaotmp (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('PR','&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            try {conn.ExecutarComando(tmpTexto);} catch (Exception e) {}

            sCodigo = jRgprp.getSelectedItem().toString().trim();
        } else if (jAbas.getSelectedIndex() == 1) {
            String tmpTexto = "INSERT INTO avisostmp (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"2", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razaotmp (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {rgprp, scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            sCodigo = jAdm.getSelectedItem().toString().trim().substring(0, 2);
        } else if (jAbas.getSelectedIndex() == 2) {
            String tmpTexto = "INSERT INTO avisostmp (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"3", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razaotmp (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {rgprp, scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            sCodigo = jSocios.getSelectedItem().toString().trim().substring(0, 2);
        } else if (jAbas.getSelectedIndex() == 3) {
            String tmpTexto = "INSERT INTO avisostmp (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"4", contrato, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razaotmp (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {contrato, scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            sCodigo = jContr.getSelectedItem().toString().trim();
        } else if (jAbas.getSelectedIndex() == 4) {
            String tmpTexto = "INSERT INTO avisostmp (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"5", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razaotmp (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('CT','&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            sCodigo = jIdConta.getSelectedItem().toString().trim();
        }

        // grava no caixa
        conn.LancarCaixatmp(new String[] {rgprp, rgimv, contrato}, aTrancicao,LerValor.FloatToString((int)nAut).replace(",0", ""));

        // grava no auxiliar
        String tmpTexto = "INSERT INTO auxiliartmp (conta, contrato, campo, dtvencimento, dtrecebimento, rc_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.');";
        tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"AVI", sCodigo, "AV:" +
                FuncoesGlobais.StrZero(String.valueOf(abas), 2) + scpAviso.substring(4),
                Dates.DateFormata("yyyy/MM/dd", new Date()), Dates.DateFormata("yyyy/MM/dd", new Date()),
                FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
        conn.ExecutarComando(tmpTexto);

        if (jAbas.getSelectedIndex() == 0) {
            jtxtDisp.setText("");
            jRgprp.requestFocus();
        } else if (jAbas.getSelectedIndex() == 1) {
            jtxtAdm.setText("");
            jAdm.requestFocus();
        } else if (jAbas.getSelectedIndex() == 2) {
            jtxtSoc.setText("");
            jSocios.requestFocus();
        } else if (jAbas.getSelectedIndex() == 3) {
            jtxtLoca.setText("");
            jContr.requestFocus();
        } else if (jAbas.getSelectedIndex() == 4) {
            jtxtConta.setText("");
            jIdConta.requestFocus();
        }

        tRec.LimpaTransicao();
        jResto.setValue(0);
        tRec.vrAREC = 0;
        jValor.setValue(0);
        tRec.Enable(false);
        tRec.btEnabled(false);
        btnLancar.setEnabled(false);
        btnCancelar.setEnabled(false);
    }

    private String CampoAviso(String mAutenticacao, String mTipo, String mData, String mValor, String mDesc) {
        String retorno;

        retorno = "AV:9:" + mValor + ":000000:AV:ET:" + mAutenticacao + ":" + mData + ":" +
             mTipo + ":" + mDesc + ":" + VariaveisGlobais.usuario;

        return retorno;
    }

    public void ImprimeAviso(float nAut, String[][] Valores, String texto, String ValorRec, String idNome, String idAviso, String cutPaper) {
      int i = 0;
      Collections gVar = VariaveisGlobais.dCliente;

        String docID = "";
        if (!idNome.trim().equals("")) {
            docID = "_" + idNome.substring(0, idNome.indexOf("-") -1).trim() + ".smc";
        }
        String docName = "reports/Recibos/" + "AV_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
        PrinterPOS printer = new PrinterPOS(docName);
        printer.Print(JavaPOS.ESC_ARROBA + "", 1);

        // cabeçário
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_E(1) + gVar.get("empresa"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "Tel/Fax:" + gVar.get("telefone") + JavaPOS.ESC_E(0), 1);
        printer.Print("", 1);

        printer.Print(JavaPOS.ESCLAMATION(70) + JavaPOS.ESC_A(1) + (idAviso.indexOf("DEBITO") > -1 ? JavaPOS.REVERSO : "") + idAviso + JavaPOS.NORMAL, 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "", 1);

        // Dados do aviso
        printer.Print(JavaPOS.ESCLAMATION(65) + idNome,1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "CAIXA: " + new Pad(VariaveisGlobais.usuario,15).RPad() + "       Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()) ,1);

        printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);

        // imprime aviso aqui
        String[] aLinhas = WordWrap.wrap(texto, 270, getFontMetrics(new java.awt.Font("SansSerif",java.awt.Font.PLAIN,10))).split("\n");
        for (int k=0;k<aLinhas.length;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + aLinhas[k], 1); }

        printer.Print("",1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(2) + "Total do Aviso......................  ",0);
        printer.Print(JavaPOS.ESCLAMATION(65) + ValorRec,1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "",1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);

        if (nAut > 0) {
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) + "VALOR(ES) LANCADOS" + JavaPOS.NORMAL, 1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) +  "--------------------------------------------------------" + JavaPOS.NORMAL,1);

            for (i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

                printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(2) + bLinha, 1);
            }

            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);

            printer.Print(JavaPOS.ESCLAMATION(65) + "--------------------------------------------------------",1);

            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);

            // Imprimir Autenticação
            printer.Print(JavaPOS.ESC_A(1) + JavaPOS.ESCLAMATION(65) + JavaPOS.REVERSO + VariaveisGlobais.dCliente.get("marca").trim() + JavaPOS.NORMAL + "AV" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,1);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + "", 1); }

        if ("P".equals(cutPaper.trim().toUpperCase())) { printer.Print(JavaPOS.PARTCUT, 0); } else { printer.Print(JavaPOS.FULLCUT, 0); }
        //printer.Close();
        
        
        new toPrint(docName, VariaveisGlobais.AvisoPre.split(",")[0],VariaveisGlobais.AvisoPre.split(",")[1],VariaveisGlobais.AvisoPre.split(",")[2]);
        printer.setDocName("");
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jAbas;
    private javax.swing.JComboBox jAdm;
    private javax.swing.JComboBox jContr;
    private javax.swing.JComboBox jIdConta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JComboBox jNmLoc;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JComboBox jRgprp;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JComboBox jSocios;
    private javax.swing.JFormattedTextField jValor;
    private javax.swing.JCheckBox jVias;
    private javax.swing.JToggleButton jbtTipo;
    private javax.swing.JComboBox jnmConta;
    private javax.swing.JPanel jpRecebe;
    private javax.swing.JTextArea jtxtAdm;
    private javax.swing.JTextArea jtxtConta;
    private javax.swing.JTextArea jtxtDisp;
    private javax.swing.JTextArea jtxtLoca;
    private javax.swing.JTextArea jtxtSoc;
    private javax.swing.JTextArea mDesc;
    private javax.swing.JTable tblLanca;
    // End of variables declaration//GEN-END:variables
}
