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
import Funcoes.jPDF;
import Funcoes.jTableControl;
import Funcoes.jThermica;
import Funcoes.newTable;
import Funcoes.toPrint;
import Protocolo.DepuraCampos;
import Transicao.jReceber;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeInter25;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.lowagie.text.Element;
import Sici.Partida.Collections;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.PatternSyntaxException;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


/**
 *
 * @author supervisor
 */
public class jAviso extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String rgprp = ""; String rgimv = ""; String contrato = ""; String rcampo = "";
    boolean executando = false; boolean mCartVazio = false;
    jTableControl tabela = new jTableControl(true);
    boolean bExecNome = false, bExecCodigo = false;
    TableRowSorter<TableModel> sorter;
    TableRowSorter<TableModel> sorterA;
    
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
        //tRec.setBounds(5, 5, 374, 229);
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
                            
                            // 08-09-2011
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
                    // 20/08/2013
                    tRec.LimpaCampos();
                    
                    // 08-09-2011
                    jbtTipo.setEnabled(true);

                    jValor.setText("0,00");
                    jValor.setEnabled(true);
                    
                    //jValor.requestFocus();
                    
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
    public jAviso() {
        initComponents();

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

        // Retenção
        //FillRetencao();
        new SimpleThread().start();            

        // Antecipados
        new SimpleThreadAntecip().start();
        
        tRec.btEnabled(false);
        btnCancelar.setEnabled(false);
        btnLancar.setEnabled(false);
        
        jtbReten.addMouseMotionListener(new MouseMotionAdapter(){
           public void mouseMoved(MouseEvent e){
                Point p = e.getPoint(); 
                int row = jtbReten.rowAtPoint(p);
                int modelRow = jtbReten.convertRowIndexToModel(row);
                int column = jtbReten.columnAtPoint(p);
                jtbReten.setToolTipText(String.valueOf(jtbReten.getModel().getValueAt(modelRow,column)));
            }
        });
        
        jtbAntecip.addMouseMotionListener(new MouseMotionAdapter(){
           public void mouseMoved(MouseEvent e){
                Point p = e.getPoint(); 
                int row = jtbAntecip.rowAtPoint(p);
                int modelRow = jtbAntecip.convertRowIndexToModel(row);
                int column = jtbAntecip.columnAtPoint(p);
                jtbAntecip.setToolTipText(String.valueOf(jtbAntecip.getModel().getValueAt(modelRow,column)));
            }
        });

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
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtbReten = new javax.swing.JTable();
        jSelAllChCM = new javax.swing.JCheckBox();
        jbtListarRetencoes = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jBuscaPasta = new javax.swing.JTextField();
        jbtTotalizar = new javax.swing.JButton();
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
        btprePrint = new javax.swing.JToggleButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblLanca = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        mDesc = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jtbAntecip = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jBuscaAntecip = new javax.swing.JTextField();
        jbtTotalizar1 = new javax.swing.JButton();
        jbtListarAntecip = new javax.swing.JButton();
        jSelAllAntecip = new javax.swing.JCheckBox();
        jbtTipo = new javax.swing.JToggleButton();
        jPanel9 = new javax.swing.JPanel();
        jVias = new javax.swing.JCheckBox();
        jValor = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jpRecebe = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Avisos ::.");
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAbas.addTab("Disponível", jPanel3);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Discriminação das Retenções", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane2.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N

        jtbReten.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jtbReten.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtbReten.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbRetenMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtbReten);

        jSelAllChCM.setText("Selecionar todas as Retenções");
        jSelAllChCM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSelAllChCMActionPerformed(evt);
            }
        });

        jbtListarRetencoes.setText("Listar Retenções");
        jbtListarRetencoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtListarRetencoesActionPerformed(evt);
            }
        });

        jLabel6.setText("Filtrar:");

        jBuscaPasta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jBuscaPastaKeyReleased(evt);
            }
        });

        jbtTotalizar.setText("Totalizar");
        jbtTotalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtTotalizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jSelAllChCM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
                        .addComponent(jbtListarRetencoes)
                        .addGap(41, 41, 41))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBuscaPasta, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtTotalizar, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jBuscaPasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtTotalizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSelAllChCM)
                    .addComponent(jbtListarRetencoes))
                .addContainerGap())
        );

        jAbas.addTab("Retenção", jPanel4);

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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
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
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
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
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAbas.addTab("Contas", jPanel1);

        btprePrint.setText("Imprimir");
        btprePrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprePrintActionPerformed(evt);
            }
        });

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
        jScrollPane7.setViewportView(tblLanca);

        jLabel8.setText("Descrição:");

        mDesc.setColumns(20);
        mDesc.setRows(5);
        jScrollPane8.setViewportView(mDesc);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btprePrint, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane8))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprePrint)
                .addContainerGap())
        );

        jAbas.addTab("Pré-Avisos", jPanel2);

        jScrollPane9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Discriminação das Antecipações", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane9.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N

        jtbAntecip.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jtbAntecip.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtbAntecip.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbAntecipMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(jtbAntecip);

        jLabel9.setText("Filtrar:");

        jBuscaAntecip.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jBuscaAntecipKeyReleased(evt);
            }
        });

        jbtTotalizar1.setText("Totalizar");
        jbtTotalizar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtTotalizar1ActionPerformed(evt);
            }
        });

        jbtListarAntecip.setText("Listar Antecipados");
        jbtListarAntecip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtListarAntecipActionPerformed(evt);
            }
        });

        jSelAllAntecip.setText("Selecionar todas as Retenções");
        jSelAllAntecip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSelAllAntecipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jSelAllAntecip)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtListarAntecip))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBuscaAntecip, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtTotalizar1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jBuscaAntecip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtTotalizar1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSelAllAntecip)
                    .addComponent(jbtListarAntecip))
                .addContainerGap())
        );

        jAbas.addTab("Antecipados", jPanel5);

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

    private void jContrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jContrActionPerformed
        if (!bExecNome) {
            int pos = jContr.getSelectedIndex();
            if (jNmLoc.getItemCount() > 0) {bExecCodigo = true; jNmLoc.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jContrActionPerformed

    private void jNmLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNmLocActionPerformed
        if (!bExecCodigo) {
            int pos = jNmLoc.getSelectedIndex();
            if (jContr.getItemCount() > 0) {bExecNome = true; jContr.setSelectedIndex(pos); bExecNome = false;}
        }
    }//GEN-LAST:event_jNmLocActionPerformed

    private void jbtTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtTipoActionPerformed
        if (jbtTipo.isSelected()) { 
//            jAutoriza uAut = new jAutoriza(null, true);
//            boolean ecaixa = VariaveisGlobais.funcao.trim().toUpperCase().equalsIgnoreCase("CAIXA");
//            boolean pode = false;
//            if (ecaixa) {
//                uAut.setVisible(true);
//                pode = uAut.pode;
//
//                if (pode) {
//                    jbtTipo.setForeground(new java.awt.Color(204, 51, 0)); 
//                    jbtTipo.setText("DÉBITO");
//                }
//            }
//            if (pode || !ecaixa) {
//                jbtTipo.setForeground(new java.awt.Color(204, 51, 0)); 
//                jbtTipo.setText("DÉBITO");
//            }
//            uAut = null;
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

    class SimpleThread extends Thread {
        public SimpleThread() {
        }
        public void run() {
            FillRetencao();
        }
    }
    
    class SimpleThreadAntecip extends Thread {
        public SimpleThreadAntecip() {}
        public void run() { FillAntecip(); }
    }
    
    
    private void jAbasStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jAbasStateChanged
        jbtTipo.enable(true);
        jValor.enable(true);
        jVias.enable(true);
        
        if (jAbas.getSelectedIndex() == 0) {
            // disponível
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

            jtxtDisp.setText("");
            jtxtDisp.requestFocus();

        } else if (jAbas.getSelectedIndex() == 1) {
            // retenção
            jbtTipo.setSelected(true);
            jbtTipo.setEnabled(false);
            jbtTipoStatusUpg(true);

        } else if (jAbas.getSelectedIndex() == 2) {
            // Adm
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            // Retirar quando for BRUNO
            //jbtTipo.setEnabled(VariaveisGlobais.funcao.equalsIgnoreCase("master"));
            jbtTipoStatusUpg(false);

            
            jtxtAdm.setText("");
            jtxtAdm.requestFocus();

        } else if (jAbas.getSelectedIndex() == 3) {
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

//            FillSocios();
//            AutoCompletion.enable(jSocios);

            jtxtSoc.setText("");
            jtxtSoc.requestFocus();

        } else if (jAbas.getSelectedIndex() == 4) {
            // Locatario
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

//            FillLoca();
//            AutoCompletion.enable(jContr);
//            AutoCompletion.enable(jNmLoc);

            jtxtLoca.setText("");
            jtxtLoca.requestFocus();

        } else if (jAbas.getSelectedIndex() == 5) {
            // Contas Controle
            jbtTipo.setSelected(false);
            jbtTipo.setEnabled(true);
            jbtTipoStatusUpg(false);

            jtxtConta.setText("");
            jtxtConta.requestFocus();            
        } else if (jAbas.getSelectedIndex() == 6) {
            jbtTipo.enable(false);
            jValor.enable(false);
            jValor.repaint();
            jVias.enable(false);
            FillLanca();
            return;
        } else if (jAbas.getSelectedIndex() == 7) {
            // retenção
            jbtTipo.setSelected(true);
            jbtTipo.setEnabled(false);
            jbtTipoStatusUpg(true);
        }
        
        tRec.LimpaTransicao();
        jValor.setEnabled(true);
        jValor.setValue(0);


    }//GEN-LAST:event_jAbasStateChanged

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
            rgimv = ""; rgprp = ""; contrato = "";
        } else if (jAbas.getSelectedIndex() == 2) {
            rgimv = ""; rgprp = jAdm.getSelectedItem().toString().trim().substring(0, 2); contrato = "";
        } else if (jAbas.getSelectedIndex() == 3) {
            rgimv = ""; rgprp = jSocios.getSelectedItem().toString().trim().substring(0, 2); contrato = "";
        } else if (jAbas.getSelectedIndex() == 4) {
            rgimv = ""; rgprp = ""; contrato = jContr.getSelectedItem().toString().trim();
        } else if (jAbas.getSelectedIndex() == 5) {
            rgimv = ""; rgprp = jIdConta.getSelectedItem().toString().trim(); contrato = "";
        } else if (jAbas.getSelectedIndex() == 7) {
            rgimv = ""; rgprp = ""; contrato = "";
        }

        tRec.rgimv = rgimv; tRec.rgprp = rgprp; tRec.contrato = contrato; tRec.acao = "AV"; tRec.operacao = (jbtTipo.isSelected() ? "DEB" : "CRE");
        jValor.setEnabled(true);
        tRec.btEnabled(true);
        
        // 08-09-2011
        jbtTipo.setEnabled(false);
        jValor.setEnabled(false);
        
        btnCancelar.setEnabled(true);
    }//GEN-LAST:event_jValorFocusLost

    private void jSelAllChCMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSelAllChCMActionPerformed
        SelectAllCHComum();
        Totaliza();
}//GEN-LAST:event_jSelAllChCMActionPerformed

    private void jtbRetenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbRetenMouseClicked
        //Totaliza();
    }//GEN-LAST:event_jtbRetenMouseClicked

    private void jbtListarRetencoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtListarRetencoesActionPerformed
//        if (VariaveisGlobais.DefaultPrinterMode.equalsIgnoreCase("NORMAL")) {
        if (VariaveisGlobais.Aviso.split(",")[1].equalsIgnoreCase("TXT")) {
            ImprimeRetencao(0, new String[][] {}, jValor.getText(), "F");
        } else {
            //ImprimeRetencao_new(0, new String[][] {}, jValor.getText(), "F");
            ImprimeRetencaoPDF(0, new String[][] {}, jValor.getText(), "F");
        }        
    }//GEN-LAST:event_jbtListarRetencoesActionPerformed

    private void jIdContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jIdContaActionPerformed
        if (!bExecNome) {
            int pos = jIdConta.getSelectedIndex();
            if (jnmConta.getItemCount() > 0) {bExecCodigo = true; jnmConta.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jIdContaActionPerformed

    private void jnmContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jnmContaActionPerformed
        if (!bExecCodigo) {
            int pos = jnmConta.getSelectedIndex();
            if (jIdConta.getItemCount() > 0) {bExecNome = true; jIdConta.setSelectedIndex(pos); bExecNome = false;}
        }
    }//GEN-LAST:event_jnmContaActionPerformed

    private void jBuscaPastaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBuscaPastaKeyReleased
        if ("".equals(jBuscaPasta.getText().trim())) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter(jBuscaPasta.getText().trim()));
            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern");
            }
        }
    }//GEN-LAST:event_jBuscaPastaKeyReleased

    private void jbtTotalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtTotalizarActionPerformed
        jBuscaPasta.setText("");
        sorter.setRowFilter(null);
        Totaliza();
        jValor.requestFocus();
    }//GEN-LAST:event_jbtTotalizarActionPerformed

    private void tblLancaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLancaKeyReleased
        ShowDetail();
    }//GEN-LAST:event_tblLancaKeyReleased

    private void tblLancaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLancaMouseReleased
        ShowDetail();
    }//GEN-LAST:event_tblLancaMouseReleased

    private void btprePrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprePrintActionPerformed
        PrintPreAviso();
    }//GEN-LAST:event_btprePrintActionPerformed

    private void jtbAntecipMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbAntecipMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jtbAntecipMouseClicked

    private void jBuscaAntecipKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBuscaAntecipKeyReleased
        if ("".equals(jBuscaAntecip.getText().trim())) {
            sorterA.setRowFilter(null);
        } else {
            try {
                sorterA.setRowFilter(RowFilter.regexFilter(jBuscaAntecip.getText().trim()));
            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern");
            }
        }
    }//GEN-LAST:event_jBuscaAntecipKeyReleased

    private void jbtTotalizar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtTotalizar1ActionPerformed
        jBuscaAntecip.setText("");
        sorterA.setRowFilter(null);
        TotalizaAntecip();
        jValor.requestFocus();
    }//GEN-LAST:event_jbtTotalizar1ActionPerformed

    private void jbtListarAntecipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtListarAntecipActionPerformed
        ImprimeAntecipaPDF(0, new String[][] {}, jValor.getText(), "F");
    }//GEN-LAST:event_jbtListarAntecipActionPerformed

    private void jSelAllAntecipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSelAllAntecipActionPerformed
        SelectAllAntecip();
        TotalizaAntecip();
    }//GEN-LAST:event_jSelAllAntecipActionPerformed

    private void PrintPreAviso() {
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(null,
            "Deseja imprimir este lançamento ? ",
            "Atenção", JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) {
            int selRow = tblLanca.getSelectedRow();
            if (selRow > -1) {
                int modelRow = tblLanca.convertRowIndexToModel(selRow);
                String taut = tblLanca.getValueAt(modelRow, 0).toString().trim();
                String corpo = tblLanca.getValueAt(modelRow, 4).toString().trim();
                String valor = tblLanca.getValueAt(modelRow, 5).toString().trim();
                String dtpre = tblLanca.getValueAt(modelRow, 9).toString().trim();
                float rid  = Float.valueOf(tblLanca.getValueAt(modelRow, 1).toString().trim());
                String tipo = tblLanca.getValueAt(modelRow, 6).toString().trim();
                String registro = tblLanca.getValueAt(modelRow, 2).toString().trim();
                String digitador = tblLanca.getValueAt(modelRow, 10).toString().trim();
                
                String sCRDB = null;
                if (tipo.equalsIgnoreCase("CRE")) {
                    sCRDB = "CREDITO";
                } else {
                    sCRDB = "DEBITO";
                }
                
                String[][] lcto = new String[][] {{"","","","",valor,dtpre,"",valor}};
                String idAviso = null, idNome = null;
                if (rid == 0) {
                    idAviso = "AVISO DISPONIVEL - " + sCRDB;
                    String[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"nome"}, "proprietarios", "rgprp = '" + registro + "'");
                    } catch (Exception e) {}
                    idNome = registro.trim() + " - " + dadosprop[0][3].trim();
                } else if (rid == 2) {
                    idAviso = "AVISO ADM - " + sCRDB;
                    idNome = registro.trim();
                } else if (rid == 3) {
                    idAviso = "AVISO SOCIO - " + sCRDB;
                    idNome = registro.trim();
                } else if (rid == 4) {
                    idAviso = "AVISO LOCATARIO - " + sCRDB;
                    String[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"nomerazao"}, "locatarios", "contrato = '" + registro + "'");
                    } catch (Exception e) {}
                    idNome = registro.trim() + " - " + dadosprop[0][3].trim();
                } else if (rid == 5) {
                    idAviso = "AVISO CONTAS CONTROLE - " + sCRDB;
                    String[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"descr"}, "ADM", "codigo = '" + registro + "'");
                    } catch (Exception e) {}
                    idNome = registro.trim() + " - " + dadosprop[0][3].trim();
                }
                
                for (int a=1; a<=2; a++) {ImprimeAvisoPre(LerValor.StringToFloat(taut), digitador, lcto, corpo, valor, idNome, idAviso, "F");}
                
                try {
                       // Inserções
                    String sql = "INSERT INTO avisos (SELECT * FROM avisostmp WHERE autenticacao = '" + taut + "')";
                    conn.ExecutarComando(sql);

                    sql = "INSERT INTO razao (SELECT * FROM razaotmp WHERE av_aut = '" + taut + "')";
                    conn.ExecutarComando(sql);

                    sql = "INSERT INTO auxiliar (SELECT * FROM auxiliartmp WHERE rc_aut = '" + taut + "')";
                    conn.ExecutarComando(sql);

                    sql = "INSERT INTO caixa (SELECT * FROM caixatmp WHERE cx_aut = '" + taut + "')";
                    conn.ExecutarComando(sql);

                    sql = "INSERT INTO Cheques (SELECT * FROM Chequestmp WHERE ch_autenticacao = '" + taut + "')";
                    conn.ExecutarComando(sql);

                    sql = "INSERT INTO ExtBanco (SELECT * FROM ExtBancotmp WHERE ch_autenticacao = '" + taut + "')";
                    conn.ExecutarComando(sql);

                    // Deleções
                    sql = "DELETE FROM avisostmp WHERE autenticacao = '" + taut + "'";
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
                    "	(SELECT c.CX_DATA FROM jgeral.caixatmp c WHERE c.CX_AUT = a.autenticacao AND c.CX_TIPOPG = 'CH') AS cx_chdt, " +
                    "   RetAvUserRid2(a.campo) AS logado " +
                    "FROM " +
                    "	avisostmp a;";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        
        String[] headers = new String[] {"autenticacao", "rid", "registro", "data", "descricao", "valor", "tipo", "dn", "ch", "dtpre", "logado"};
        int[] widths = new int[] {0,0,80,80,0,100,80,100,100,100,200};
        String[] aligns = new String[] {"C","C","C","C","L","R","C","R","R","C","L"};
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
                                  dtpre,
                                  rs.getString("logado")
                                 };
                newTable.add(tblLanca, linha);
            }
        } catch (Exception e) {e.printStackTrace();}
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
        jAbas.setEnabledAt(5, i > 0);
    }

    private void FillCombos() {
        //String sSql = "SELECT distinct p.rgprp, p.nome FROM proprietarios p, imoveis i WHERE p.rgprp = i.rgprp and Lower(Trim(i.situacao)) = 'ocupado' ORDER BY Lower(Trim(p.nome));";
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
        
        //nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));
        //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

        String corpo = ""; String idNome = ""; String idAviso = ""; String idConta = "";
        String sCRDB = (jbtTipo.isSelected() ? "DEBITO" : "CREDITO");
        if (jAbas.getSelectedIndex() == 0) {
            corpo = jtxtDisp.getText();
            idAviso = "AVISO DISPONIVEL - " + sCRDB;
            idNome = jRgprp.getSelectedItem().toString().trim() + " - " + jNomeProp.getSelectedItem().toString().trim();
            idConta = "00PR";
        } else if (jAbas.getSelectedIndex() == 1) {
            corpo = "";
            idAviso = "AVISO RETENCAO - DEBITO";
            idNome = ""; idConta = "00RT";
        } else if (jAbas.getSelectedIndex() == 2) {
            corpo = jtxtAdm.getText();
            idAviso = "AVISO ADM - " + sCRDB;
            idNome = jAdm.getSelectedItem().toString().trim();
            idConta = idNome.substring(0, 2);
        } else if (jAbas.getSelectedIndex() == 3) {
            corpo = jtxtSoc.getText();
            idAviso = "AVISO SOCIO - " + sCRDB;
            idNome = jSocios.getSelectedItem().toString().trim();
            idConta = idNome.substring(0, 2);
        } else if (jAbas.getSelectedIndex() == 4) {
            corpo = jtxtLoca.getText();
            idAviso = "AVISO LOCATARIO - " + sCRDB;
            idNome = jContr.getSelectedItem().toString().trim() + " - " + jNmLoc.getSelectedItem().toString().trim();
            idConta = "04LC";
        } else if (jAbas.getSelectedIndex() == 5) {
            corpo = jtxtConta.getText();
            idAviso = "AVISO CONTAS CONTROLE - " + sCRDB;
            idNome = jIdConta.getSelectedItem().toString().trim() + " - " + jnmConta.getSelectedItem().toString().trim();
            idConta = "05CT";
        } else if (jAbas.getSelectedIndex() == 7) {
            corpo = "";
            idAviso = "AVISO ANTECIPAÇÃO - DEBITO";
            idNome = ""; 
            idConta = "07AT";
        }
        
        if (jAbas.getSelectedIndex() == 1) {
            if (VariaveisGlobais.Aviso.split(",")[1].equalsIgnoreCase("TXT")) {
                ImprimeRetencao((float)nAut, aTrancicao, jValor.getText(), "F");
            } else {
                ImprimeRetencaoPDF((float)nAut, aTrancicao, jValor.getText(), "F");
            }
        } else if (jAbas.getSelectedIndex() == 7) {
            ImprimeAntecipaPDF((float)nAut, aTrancicao, jValor.getText(), "F");
        } else {
            for (int a=1; a<=vias; a++) {
                if (VariaveisGlobais.Aviso.split(",")[1].equalsIgnoreCase("TXT")) {
                    ImprimeAviso((float)nAut, aTrancicao, corpo, jValor.getText(), idNome, idAviso, "F");
                } else {
                    ImprimeAvisoPDF((float)nAut, aTrancicao, corpo, jValor.getText(), idNome, idAviso, "F");
                }
            }
        }

        String scpAviso = CampoAviso(FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6) +
                (jAbas.getSelectedIndex() ==2 || jAbas.getSelectedIndex() == 3 ?
                FuncoesGlobais.StrZero(String.valueOf(jAbas.getSelectedIndex()), 2) + idConta :
                idConta), (!jbtTipo.isSelected() ? "CRE:" : "DEB:") + "XX",
                Dates.DateFormata("ddMMyyyy", new Date()),
                FuncoesGlobais.GravaValor(jValor.getText()),
                FuncoesGlobais.CriptaNome(corpo.replaceAll("\r\n", " ")));

        String sCodigo = "";
        if (jAbas.getSelectedIndex() == 0) {
            String tmpTexto = "INSERT INTO avisos (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"0", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('PR','&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            try {conn.ExecutarComando(tmpTexto);} catch (Exception e) {}

            sCodigo = jRgprp.getSelectedItem().toString().trim();
        } else if (jAbas.getSelectedIndex() == 1) {
            String tmpTexto = null;
            for (int i=0; i< jtbReten.getRowCount(); i++) {
                int modelRow = jtbReten.convertRowIndexToModel(i);
                if ("true".equals(jtbReten.getModel().getValueAt(modelRow, 7).toString().toLowerCase())) {
                    tmpTexto = "UPDATE retencao SET marca = 'X', rt_aut = '&1.' WHERE contrato = '&2.' AND rgprp = '&3.' AND rgimv = '&4.' AND campo = '&5.' AND vencimento = '&6.';";
                    tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {
                               FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6),
                               jtbReten.getModel().getValueAt(modelRow, 0).toString(),
                               jtbReten.getModel().getValueAt(modelRow, 1).toString(),
                               jtbReten.getModel().getValueAt(modelRow, 2).toString(),
                               jtbReten.getModel().getValueAt(modelRow, 8).toString(),
                               Dates.DateFormata("yyyy-MM-dd",Dates.StringtoDate(jtbReten.getModel().getValueAt(modelRow, 6).toString(), "dd/MM/yyyy"))});
                    conn.ExecutarComando(tmpTexto);
                }
            }

            try {
                tmpTexto = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('&1.','&2.','&3.','&4.','&5.');";
                tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {rgprp, scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
                Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
                conn.ExecutarComando(tmpTexto);
            } catch (Exception e) {}
            
            sCodigo = "";
        } else if (jAbas.getSelectedIndex() == 7) {
            String tmpTexto = null;
            for (int i=0; i< jtbAntecip.getRowCount(); i++) {
                int modelRow = jtbAntecip.convertRowIndexToModel(i);
                if ("true".equals(jtbAntecip.getModel().getValueAt(modelRow, 7).toString().toLowerCase())) {
                    tmpTexto = "UPDATE ANTECIPADOS SET dtpagamento = '" + Dates.DateFormata("yyyy-MM-dd", new Date())+ "', at_aut = '&1.' WHERE contrato = '&2.' AND rgprp = '&3.' AND rgimv = '&4.' AND campo = '&5.' AND dtvencimento = '&6.';";
                    tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {
                               FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""), 6),
                               jtbAntecip.getModel().getValueAt(modelRow, 0).toString(),
                               jtbAntecip.getModel().getValueAt(modelRow, 1).toString(),
                               jtbAntecip.getModel().getValueAt(modelRow, 2).toString(),
                               jtbAntecip.getModel().getValueAt(modelRow, 8).toString(),
                               Dates.DateFormata("yyyy-MM-dd",Dates.StringtoDate(jtbAntecip.getModel().getValueAt(modelRow, 6).toString(), "dd/MM/yyyy"))});
                    conn.ExecutarComando(tmpTexto);
                }
            }

            try {
                tmpTexto = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('&1.','&2.','&3.','&4.','&5.');";
                tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {rgprp, scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
                Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
                conn.ExecutarComando(tmpTexto);
            } catch (Exception e) {}
            
            sCodigo = "";
        } else if (jAbas.getSelectedIndex() == 2) {
            String tmpTexto = "INSERT INTO avisos (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"2", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {rgprp, scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            sCodigo = jAdm.getSelectedItem().toString().trim().substring(0, 2);
        } else if (jAbas.getSelectedIndex() == 3) {
            String tmpTexto = "INSERT INTO avisos (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"3", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {rgprp, scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            sCodigo = jSocios.getSelectedItem().toString().trim().substring(0, 2);
        } else if (jAbas.getSelectedIndex() == 4) {
            String tmpTexto = "INSERT INTO avisos (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"4", contrato, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {contrato, scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            sCodigo = jContr.getSelectedItem().toString().trim();
        } else if (jAbas.getSelectedIndex() == 5) {
            String tmpTexto = "INSERT INTO avisos (rid, registro, campo, autenticacao) VALUES ('&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"5", rgprp, scpAviso, FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            tmpTexto = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, av_aut) VALUES ('CT','&1.','&2.','&3.','&4.');";
            tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {scpAviso, Dates.DateFormata("yyyy/MM/dd", new Date()),
            Dates.DateFormata("yyyy/MM/dd", new Date()), FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
            conn.ExecutarComando(tmpTexto);

            sCodigo = jIdConta.getSelectedItem().toString().trim();
        }

        // grava no caixa
        conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, aTrancicao,String.valueOf(nAut).replace(".0", ""));

        // grava no auxiliar
        String tmpTexto = "INSERT INTO auxiliar (conta, contrato, campo, dtvencimento, dtrecebimento, rc_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.');";
        tmpTexto = FuncoesGlobais.Subst(tmpTexto, new String[] {"AVI", sCodigo, "AV:" +
                FuncoesGlobais.StrZero(String.valueOf(jAbas.getSelectedIndex()), 2) + scpAviso.substring(4),
                Dates.DateFormata("yyyy/MM/dd", new Date()), Dates.DateFormata("yyyy/MM/dd", new Date()),
                FuncoesGlobais.StrZero(String.valueOf(nAut).replace(".0", ""),6)});
        conn.ExecutarComando(tmpTexto);

        if (jAbas.getSelectedIndex() == 0) {
            jtxtDisp.setText("");
            jRgprp.requestFocus();
        } else if (jAbas.getSelectedIndex() == 1) {
            jtbReten.requestFocus();
        } else if (jAbas.getSelectedIndex() == 2) {
            jtxtAdm.setText("");
            jAdm.requestFocus();
        } else if (jAbas.getSelectedIndex() == 3) {
            jtxtSoc.setText("");
            jSocios.requestFocus();
        } else if (jAbas.getSelectedIndex() == 4) {
            jtxtLoca.setText("");
            jContr.requestFocus();
        } else if (jAbas.getSelectedIndex() == 5) {
            jtxtConta.setText("");
            jIdConta.requestFocus();
        } else if (jAbas.getSelectedIndex() == 7) {
            jtbAntecip.requestFocus();
        }

        tRec.LimpaTransicao();
        jResto.setValue(0);
        tRec.vrAREC = 0;
        jValor.setValue(0);
        tRec.Enable(false);
        tRec.btEnabled(false);
        btnLancar.setEnabled(false);
        btnCancelar.setEnabled(false);

        if (jAbas.getSelectedIndex() == 1) FillRetencao();
        if (jAbas.getSelectedIndex() == 7) FillAntecip();
    }

    private String CampoAviso(String mAutenticacao, String mTipo, String mData, String mValor, String mDesc) {
        String retorno;

        retorno = "AV:9:" + mValor + ":000000:AV:ET:" + mAutenticacao + ":" + mData + ":" +
             mTipo + ":" + mDesc + ":" + VariaveisGlobais.usuario;

        return retorno;
    }

    private void Totaliza() {
        float trt = 0;
        for (int i=0; i< jtbReten.getRowCount(); i++) {
            int modelRow = jtbReten.convertRowIndexToModel(i);
            //if ("true".equals(jtbReten.getModel().getValueAt(modelRow, 7).toString().toLowerCase())) {
            if ((Boolean)jtbReten.getModel().getValueAt(modelRow, 7)) {
                trt += LerValor.StringToFloat(jtbReten.getModel().getValueAt(modelRow, 5).toString());
            }
        }

        jValor.setText(LerValor.floatToCurrency(trt, 2));
    }

    private void SelectAllCHComum() {
        for (int i=0; i< jtbReten.getRowCount(); i++) {
            int modelRow = jtbReten.convertRowIndexToModel(i);
            jtbReten.getModel().setValueAt(new Boolean((jSelAllChCM.isSelected() ? true : false)), modelRow, 7);
        }
    }
    
    private void TotalizaAntecip() {
        float trt = 0;
        for (int i=0; i< jtbAntecip.getRowCount(); i++) {
            int modelRow = jtbAntecip.convertRowIndexToModel(i);
            //if ("true".equals(jtbReten.getModel().getValueAt(modelRow, 7).toString().toLowerCase())) {
            if ((Boolean)jtbAntecip.getModel().getValueAt(modelRow, 7)) {
                trt += LerValor.StringToFloat(jtbAntecip.getModel().getValueAt(modelRow, 5).toString());
            }
        }

        jValor.setText(LerValor.floatToCurrency(trt, 2));
    }

    private void SelectAllAntecip() {
        for (int i=0; i< jtbAntecip.getRowCount(); i++) {
            int modelRow = jtbAntecip.convertRowIndexToModel(i);
            jtbAntecip.getModel().setValueAt(new Boolean((jSelAllAntecip.isSelected() ? true : false)), modelRow, 7);
        }
    }
    
    private void FillAntecip() {
        String sql = "SELECT r.contrato, r.rgprp, r.rgimv, r.campo, r.dtvencimento, CONCAT(i.end,',',i.num,' ',i.compl) as endereco FROM ANTECIPADOS r, imoveis i WHERE (r.rgimv = i.rgimv) AND r.dtpagamento Is NULL ORDER BY r.rgimv, r.campo, r.dtvencimento;";
        sql = FuncoesGlobais.Subst(sql, new String[] {VariaveisGlobais.usuario.toLowerCase().trim()});

        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Integer[] tam = {0,0,90,240,110,100,130,20,0};
        String[] col = {"contrato","rgprp","rgimv","endereco","taxa","valor","vencto","tag","campo"};
        Boolean[] edt = {false,false,false,false,false,false,false,true,false};
        String[] aln = {"C","C","C","L","L","R","C","","L"};
        Object[][] data = {};
        try {
            while (rs.next()) {
                String dcontrato = rs.getString("contrato");
                String drgprp = rs.getString("rgprp");
                String drgimv = rs.getString("rgimv");

                DepuraCampos a = new DepuraCampos(rs.getString("campo"));
                VariaveisGlobais.ccampos = rs.getString("campo");

                a.SplitCampos();
                // Ordena Matriz
                Arrays.sort(a.aCampos, new Comparator()
                {
                private int pos1 = 3;
                private int pos2 = 4;
                public int compare(Object o1, Object o2) {
                    String p1 = ((String)o1).substring(pos1, pos2);
                    String p2 = ((String)o2).substring(pos1, pos2);
                    return p1.compareTo(p2);
                }
                });

                String[] Campo = a.Depurar(0);
                String dtaxa = Campo[0];
                String dValor = Campo[1];

                String dDtLanc = Dates.DateFormata("dd/MM/yyyy", rs.getDate("dtvencimento"));
                Boolean dTag = false;
                String dcampo = rs.getString("campo");

                String dend = rs.getString("endereco");
                
                Object[] dado = {dcontrato, drgprp, drgimv, dend, dtaxa, dValor, dDtLanc, dTag, dcampo};
                data = tabela.insert(data, dado);
            }
        } catch (SQLException ex) {ex.printStackTrace();}

        DbMain.FecharTabela(rs);
        tabela.Show(jtbAntecip, data, tam, aln, col, edt);
        
        sorterA = new TableRowSorter<TableModel>(jtbAntecip.getModel());
        jtbAntecip.setRowSorter(sorterA);

    }
    
    private void FillRetencao() {
        //String sql = "SELECT contrato, rgprp, rgimv, campo, vencimento, tag FROM retencao WHERE marca <> 'X' ORDER BY rgimv, campo, vencimento;";
        String sql = "SELECT r.contrato, r.rgprp, r.rgimv, r.campo, r.vencimento, r.tag, CONCAT(i.end,',',i.num,' ',i.compl) as endereco FROM retencao r, imoveis i WHERE (r.rgimv = i.rgimv) AND r.marca <> 'X' ORDER BY r.rgimv, r.campo, r.vencimento;";
        sql = FuncoesGlobais.Subst(sql, new String[] {VariaveisGlobais.usuario.toLowerCase().trim()});

        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Integer[] tam = {0,0,90,240,110,100,130,20,0};
        String[] col = {"contrato","rgprp","rgimv","endereco","taxa","valor","recebto","tag","campo"};
        Boolean[] edt = {false,false,false,false,false,false,false,true,false};
        String[] aln = {"C","C","C","L","L","R","C","","L"};
        Object[][] data = {};
        try {
            while (rs.next()) {
                String dcontrato = rs.getString("contrato");
                String drgprp = rs.getString("rgprp");
                String drgimv = rs.getString("rgimv");

                DepuraCampos a = new DepuraCampos(rs.getString("campo"));
                VariaveisGlobais.ccampos = rs.getString("campo");

                a.SplitCampos();
                // Ordena Matriz
                Arrays.sort(a.aCampos, new Comparator()
                {
                private int pos1 = 3;
                private int pos2 = 4;
                public int compare(Object o1, Object o2) {
                    String p1 = ((String)o1).substring(pos1, pos2);
                    String p2 = ((String)o2).substring(pos1, pos2);
                    return p1.compareTo(p2);
                }
                });

                String[] Campo = a.Depurar(0);
                String dtaxa = Campo[0];
                String dValor = Campo[1];

                String dDtLanc = Dates.DateFormata("dd/MM/yyyy", rs.getDate("vencimento"));
                Boolean dTag = false;
                String dcampo = rs.getString("campo");

                String dend = rs.getString("endereco");
                
                Object[] dado = {dcontrato, drgprp, drgimv, dend, dtaxa, dValor, dDtLanc, dTag, dcampo};
                data = tabela.insert(data, dado);
            }
        } catch (SQLException ex) {ex.printStackTrace();}

        DbMain.FecharTabela(rs);
        tabela.Show(jtbReten, data, tam, aln, col, edt);
        
        sorter = new TableRowSorter<TableModel>(jtbReten.getModel());
        jtbReten.setRowSorter(sorter);

    }

    public void ImprimeAviso(float nAut, String[][] Valores, String texto, String ValorRec, String idNome, String idAviso, String cutPaper) {
      int i = 0;
      Collections gVar = VariaveisGlobais.dCliente;

      // hResult = FuncoesGlobais.OpenTransicao("RC")

        String docID = "";
        if (!idNome.trim().equals("")) {
            docID = "_" + idNome.substring(0, idNome.indexOf("-") -1).trim();
        }
        String docName = "reports/Recibos/" + "AV_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
        PrinterPOS printer = new PrinterPOS(docName);
        printer.Print(JavaPOS.ESC_ARROBA + "", 1);
        //printer.setDocName(docName);

        // Imprime logomarca
        /////////////////printer.PrintBitMap("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif",0);
        //////////////////printer.Print("", 1);

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
        printer.Print(JavaPOS.ESCLAMATION(65) + "CAIXA: " + new Pad(VariaveisGlobais.usuario.trim(),15).RPad() + "       Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()) ,1);

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
                } else {
                    bLinha = Valores[i][5].trim() + ":" + new Pad(Valores[i][4],10).LPad();
                }

                printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(2) + bLinha, 1);
            }

            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);

            printer.Print(JavaPOS.ESCLAMATION(65) + "--------------------------------------------------------",1);

            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);

            // Imprimir Autenticação
            printer.Print(JavaPOS.ESC_A(1) + JavaPOS.ESCLAMATION(65) + JavaPOS.REVERSO + VariaveisGlobais.dCliente.get("marca").trim() + JavaPOS.NORMAL + "AV" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,1);
            // Codigo de barra
            ////////////////////printer.PrintBarCode(FuncoesGlobais.StrZero(String.valueOf((int)nAut), 12));
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + "", 1); }

        if ("P".equals(cutPaper.trim().toUpperCase())) { printer.Print(JavaPOS.PARTCUT, 0); } else { printer.Print(JavaPOS.FULLCUT, 0); }
        //printer.Close();
        new toPrint(docName, VariaveisGlobais.Aviso.split(",")[0],VariaveisGlobais.Aviso.split(",")[1],VariaveisGlobais.Aviso.split(",")[2]);
        printer.setDocName("");
    }

    public void ImprimeAvisoPre(float nAut, String pre, String[][] Valores, String texto, String ValorRec, String idNome, String idAviso, String cutPaper) {
      int i = 0;
      Collections gVar = VariaveisGlobais.dCliente;

      // hResult = FuncoesGlobais.OpenTransicao("RC")

        String docID = "";
        if (!idNome.trim().equals("")) {
            docID = "_" + idNome.substring(0, idNome.indexOf("-") -1).trim();
        }
        String pathName = "reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
        String docName = pathName + "AV_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
                //"reports/Recibos/" + "AV_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
        PrinterPOS printer = new PrinterPOS(docName);
        printer.Print(JavaPOS.ESC_ARROBA + "", 1);
        //printer.setDocName(docName);

        // Imprime logomarca
        /////////////////printer.PrintBitMap("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif",0);
        //////////////////printer.Print("", 1);

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
        printer.Print(JavaPOS.ESCLAMATION(65) + "CAIXA: " + new Pad(VariaveisGlobais.usuario.trim() + "/" + pre.trim(),15).RPad() + "       Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()) ,1);

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
            // Codigo de barra
            ////////////////////printer.PrintBarCode(FuncoesGlobais.StrZero(String.valueOf((int)nAut), 12));
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + "", 1); }

        if ("P".equals(cutPaper.trim().toUpperCase())) { printer.Print(JavaPOS.PARTCUT, 0); } else { printer.Print(JavaPOS.FULLCUT, 0); }
        //printer.Close();
        new toPrint(docName, VariaveisGlobais.AvisoPre.split(",")[0],VariaveisGlobais.AvisoPre.split(",")[1],VariaveisGlobais.AvisoPre.split(",")[2]);
        printer.setDocName("");
    }

    public void ImprimeRetencao(float nAut, String[][] Valores, String ValorRec, String cutPaper) {
      System.out.println("ImprimeRetencao");
      int i = 0;
      Collections gVar = VariaveisGlobais.dCliente;

      // hResult = FuncoesGlobais.OpenTransicao("RC")

        String docName = "reports/Recibos/" + "RT_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + ".ps";
        PrinterPOS printer = new PrinterPOS(docName);
        printer.Print(JavaPOS.ESC_ARROBA + "", 1);
        //printer.setDocName(docName);

        // Imprime logomarca
        //printer.PrintBitMap("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif",0);
        //printer.Print("", 1);

        // cabeçário
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_E(1) + gVar.get("empresa"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "Tel/Fax:" + gVar.get("telefone") + JavaPOS.ESC_E(0), 1);
        printer.Print("", 1);

        printer.Print(JavaPOS.ESCLAMATION(70) + JavaPOS.ESC_A(1) + JavaPOS.REVERSO + "AVISO RETENCAO - DEBITO" + JavaPOS.NORMAL, 1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "", 1);

        printer.Print(JavaPOS.ESCLAMATION(65) + "CAIXA: " + new Pad(VariaveisGlobais.usuario,15).RPad() + "       Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()) ,1);
        printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);

        // Lista depósitos
        for (int c=0; c< jtbReten.getRowCount(); c++) {
            int modelRow = jtbReten.convertRowIndexToModel(c);
            if ("true".equals(jtbReten.getModel().getValueAt(modelRow, 7).toString().toLowerCase())) {
                printer.Print(JavaPOS.ESCLAMATION(65) + new Pad(jtbReten.getModel().getValueAt(modelRow, 6).toString(),10).RPad() + " ", 0);
                printer.Print(JavaPOS.ESCLAMATION(65) + new Pad(jtbReten.getModel().getValueAt(modelRow, 2).toString(),6).RPad() + " ", 0);
                printer.Print(JavaPOS.ESCLAMATION(65) + new Pad(jtbReten.getModel().getValueAt(modelRow, 4).toString(),22).RPad() + " ", 0);
                printer.Print(JavaPOS.ESCLAMATION(65) + new Pad(jtbReten.getModel().getValueAt(modelRow, 5).toString(),15).LPad(), 1);
                printer.Print(JavaPOS.ESCLAMATION(65) + new Pad(jtbReten.getModel().getValueAt(modelRow, 3).toString(),53).RPad(), 1);
            }
        }

        printer.Print("",1);
        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(2) + "Total Geral de Retencoes................ ",0);
        printer.Print(JavaPOS.ESCLAMATION(65) + new Pad(ValorRec,15).LPad(),1);

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

            // Imprimir Autenticação
            printer.Print(JavaPOS.ESC_A(1) + JavaPOS.ESCLAMATION(65) + JavaPOS.REVERSO + VariaveisGlobais.dCliente.get("marca").trim() + JavaPOS.NORMAL + "AV" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,1);
            // Codigo de barra
            ////////////////////printer.PrintBarCode(FuncoesGlobais.StrZero(String.valueOf((int)nAut), 12));
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + "", 1); }

        if ("P".equals(cutPaper.trim().toUpperCase())) { printer.Print(JavaPOS.PARTCUT, 0); } else { printer.Print(JavaPOS.FULLCUT, 0); }
        //printer.Close();
        new toPrint(docName, VariaveisGlobais.Aviso.split(",")[0],VariaveisGlobais.Aviso.split(",")[1],VariaveisGlobais.Aviso.split(",")[2]);
        printer.setDocName("");
    }

   public void ImprimeRetencaoPDF(float nAut, String[][] Valores, String ValorRec, String cutPaper) {
        float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();

        PdfPCell cell1, cell2, cell3, cell4;        
        
        String docName = "RT_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + ".pdf";
        String pathName = "reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
        pdf.setPathName(pathName);
        pdf.setDocName(docName);
        
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);

        pdf.open();
        
        // Logo
        com.itextpdf.text.Image img;
        try {
            img = com.itextpdf.text.Image.getInstance("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif");
            img.setAlignment(Element.ALIGN_LEFT);        
            pdf.doc_add(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Paragraph p;
        
        p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
            p = pdf.print(gVar.get("tipodoc"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT,pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Tel/Fax:" + gVar.get("telefone"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("AVISO RETENCAO - DEBITO", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        
        cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Lista Retenções
        columnWidths = new float[] {30,20,25,25};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        for (int c=0; c< jtbReten.getRowCount(); c++) {
            int modelRow = jtbReten.convertRowIndexToModel(c);
            if ("true".equals(jtbReten.getModel().getValueAt(modelRow, 7).toString().toLowerCase())) {
                font.setColor(BaseColor.BLACK);
                cell1 = new PdfPCell(new Phrase(jtbReten.getModel().getValueAt(modelRow, 6).toString(),font));
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell1);
                cell2 = new PdfPCell(new Phrase(jtbReten.getModel().getValueAt(modelRow, 2).toString(),font));
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell2.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell2);
                cell3 = new PdfPCell(new Phrase(jtbReten.getModel().getValueAt(modelRow, 4).toString(),font));
                cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell3.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell3);
                cell4 = new PdfPCell(new Phrase(jtbReten.getModel().getValueAt(modelRow, 5).toString(),font));
                cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell4.setBorder(Rectangle.NO_BORDER);
                cell4.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell4);

                cell1 = new PdfPCell(new Phrase(jtbReten.getModel().getValueAt(modelRow, 3).toString(),font));
                cell1.setColspan(4);
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell1);
            }
        }
        table.completeRow();
        pdf.doc_add(table);
        
        columnWidths = new float[] {70, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("==========", font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(ValorRec, font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        if (nAut > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

                p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                pdf.doc_add(p);
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + "AV" +
                    FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + 
                    Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + 
                    FuncoesGlobais.GravaValores(ValorRec, 2) + 
                    VariaveisGlobais.usuario, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);
            
            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)nAut),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            com.itextpdf.text.Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);            
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        
        pdf.close();
        //pdf.print();
        new toPrint(pathName + docName, VariaveisGlobais.Aviso.split(",")[0],VariaveisGlobais.Aviso.split(",")[1],VariaveisGlobais.Aviso.split(",")[2]);
        pdf.setPathName("");
        pdf.setDocName("");
    }
 
    public void ImprimeRetencaoThermica2(float nAut, String[][] Valores, String ValorRec, String cutPaper) {
        Object[][] linhas = {};
        jThermica Printer = new jThermica(linhas);
        int i = 0;
        Collections gVar = VariaveisGlobais.dCliente;

        // cabeçário
        Object[][] tmp = {
                                {gVar.get("empresa"),null,0,new java.awt.Font("Arial",java.awt.Font.BOLD + java.awt.Font.ITALIC,9), Color.BLACK, Color.WHITE}
                            };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"),null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                               };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"),null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"Tel/Fax:" + gVar.get("telefone"),null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"AVISO RETENCAO - DEBITO",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,10), Color.WHITE, Color.BLACK}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);
      
        tmp = new Object[][] {
                            {"CAIXA: " + VariaveisGlobais.usuario,80,1,new Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE},
                            {"Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),null,2,new Font("Times New Roman",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {null,null,2,null, Color.BLACK, Color.WHITE},
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        // Lista depósitos
        for (int c=0; c< jtbReten.getRowCount(); c++) {
            int modelRow = jtbReten.convertRowIndexToModel(c);
            if ("true".equals(jtbReten.getModel().getValueAt(modelRow, 7).toString().toLowerCase())) {
                  tmp = new Object[][] {
                                      {jtbReten.getModel().getValueAt(modelRow, 6).toString(),45,1,new Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE},
                                      {jtbReten.getModel().getValueAt(modelRow, 2).toString(),30,1,new Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE},
                                      {jtbReten.getModel().getValueAt(modelRow, 4).toString(),80,1,new Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE},
                                      {jtbReten.getModel().getValueAt(modelRow, 5).toString(),null,2,new Font("Times New Roman",Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                   };
                  linhas =  Printer.adcLine(linhas, tmp);
                  tmp = new Object[][] {
                                      {jtbReten.getModel().getValueAt(modelRow, 3).toString(),null,1,new Font("Times New Roman",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                                   };
                  linhas =  Printer.adcLine(linhas, tmp);
            }
        }

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Times New Roman",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"============",null,2,new Font("Times New Roman",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"Total Geral de Retenções",100,1,new Font("Times New Roman",Font.BOLD,7), Color.BLACK, Color.WHITE},
                            {ValorRec,null,2,new Font("Times New Roman",Font.BOLD,8), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {null,null,2,null, Color.BLACK, Color.WHITE},
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        if (nAut > 0) {
              tmp = new Object[][] {
                                      {"VALOR(ES) LANCADOS",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                   };
              linhas =  Printer.adcLine(linhas, tmp);

              tmp = new Object[][] {
                                  {null,null,2,null, Color.BLACK, Color.WHITE},
                               };
              linhas =  Printer.adcLine(linhas, tmp);

            for (i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

               tmp = new Object[][] {
                                      {bLinha,null,2,new java.awt.Font("Arial",java.awt.Font.PLAIN,6), Color.BLACK, Color.WHITE}
                                   };
               linhas =  Printer.adcLine(linhas, tmp);
            }

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                 };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                {null,null,2,null, Color.BLACK, Color.WHITE},
                             };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                 };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            // Imprimir Autenticação
            tmp = new Object[][] {
                                    {VariaveisGlobais.dCliente.get("marca").trim() + "AV" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,6), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);
        }
        
//        //if (linhas.length >= 70) {
//            Object[][] nlinhas = {};
//            int pag = 0;
//            for (int w = 0; w<linhas.length; w++) {
//                nlinhas = FuncoesGlobais.ObjectsAdd(nlinhas, linhas[w]);
//                
//                if (w % 70 == 0) {
//                    jThermica np = new jThermica(nlinhas);
//                    np.setDocName("reports/Recibos/" + "RT_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "_" + FuncoesGlobais.StrZero(String.valueOf(++pag), 2) + ".smc");
//                    np.toSave();
//                    np.toPrint();
//                    np.setDocName("");
//                    
//                    nlinhas = new Object[][] {};
//                }
//            }
//            if (nlinhas.length > 0) {
//                jThermica np = new jThermica(nlinhas);
//                np.setDocName("reports/Recibos/" + "RT_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "_" + FuncoesGlobais.StrZero(String.valueOf(++pag), 2) + ".smc");
//                //np.setDocName("reports/Recibos/" + "RT_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + ".smc");
//                np.toSave();
//                np.toPrint();
//                np.setDocName("");
//            }
//        //}
        jThermica np = new jThermica(linhas);
        np.setDocName("reports/Recibos/" + "RT_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + ".ps");
        np.toSave();
//        np.toPrint();
        np.setDocName("");
    }
    
   public void ImprimeAvisoPDF(float nAut, String[][] Valores, String texto, String ValorRec, String idNome, String idAviso, String cutPaper) {
        float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();

        String docID = "";
        if (!idAviso.trim().equals("")) {
            docID = "_" + idAviso.substring(0, idAviso.indexOf("-") -1).trim() + ".pdf";
        }
        String docName = "AV_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut).replace(".0", ""), 7) + docID;
        String pathName = "reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
        pdf.setPathName(pathName);
        pdf.setDocName(docName);
        
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);

        pdf.open();
        
        // Logo
        com.itextpdf.text.Image img;
        try {
            img = com.itextpdf.text.Image.getInstance("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif");
            img.setAlignment(Element.ALIGN_LEFT);        
            pdf.doc_add(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Paragraph p;
        
        p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
            p = pdf.print(gVar.get("tipodoc"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT,pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Tel/Fax:" + gVar.get("telefone"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(idAviso, pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        p = pdf.print(idNome, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        
        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        
        PdfPCell cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        columnWidths = new float[] {100};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        // Dados do aviso
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase(texto,font));
        cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        table.completeRow();
        pdf.doc_add(table);
        
        columnWidths = new float[] {70, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("==========", font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(ValorRec, font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        if (nAut > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

                p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                pdf.doc_add(p);
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + "AV" +
                    FuncoesGlobais.StrZero(String.valueOf((int)nAut).replace(".0", ""), 7) + "-1" + 
                    Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + 
                    FuncoesGlobais.GravaValores(ValorRec, 2) + 
                    VariaveisGlobais.usuario, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);
            
            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)nAut).replace(".0", ""),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            com.itextpdf.text.Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);            
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        
        pdf.close();
        //pdf.print();
        new toPrint(pathName + docName, VariaveisGlobais.Aviso.split(",")[0],VariaveisGlobais.Aviso.split(",")[1],VariaveisGlobais.Aviso.split(",")[2]);
        pdf.setPathName("");
        pdf.setDocName("");
   }
 
    public void ImprimeAvisoThermica(float nAut, String[][] Valores, String texto, String ValorRec, String idNome, String idAviso, String cutPaper) {
        Object[][] linhas = {};
        jThermica Printer = new jThermica(linhas);
        int i = 0;
        Collections gVar = VariaveisGlobais.dCliente;

        // cabeçário
        Image img1 = Toolkit.getDefaultToolkit().getImage("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif");
        Object[][] tmp = {
                            {img1,null,0,null,null,null}
                         };
        linhas =  Printer.adcLine(linhas, tmp);            

        tmp = new Object[][] {
                                {gVar.get("empresa"),null,1,new java.awt.Font("Arial",java.awt.Font.BOLD + java.awt.Font.ITALIC,9), Color.BLACK, Color.WHITE}
                            };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"),null,1,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                               };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"),null,1,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"Tel/Fax:" + gVar.get("telefone"),null,1,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {idAviso,null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,10), (idAviso.indexOf("DEBITO") > -1 ? Color.WHITE : Color.BLACK), (idAviso.indexOf("DEBITO") > -1 ? Color.BLACK : Color.WHITE)}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        // Dados do aviso
        tmp = new Object[][] {
                                {idNome,null,1,new java.awt.Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"CAIXA: " + VariaveisGlobais.usuario,80,1,new Font("Times New Roman",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE},
                            {"Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),null,2,new Font("Times New Roman",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {null,null,2,null, Color.BLACK, Color.WHITE},
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        // imprime aviso aqui
        String[] aLinhas = WordWrap.wrap(texto, 204, getFontMetrics(new java.awt.Font("SansSerif",java.awt.Font.PLAIN,8))).split("\n");
        for (int k=0;k<aLinhas.length;k++) { 
              tmp = new Object[][] {
                                      {aLinhas[k],null,1,new java.awt.Font("SansSerif",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                   };
              linhas =  Printer.adcLine(linhas, tmp);
        }

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Times New Roman",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"============",null,2,new Font("Times New Roman",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"Total do Aviso",100,1,new Font("Times New Roman",Font.BOLD,7), Color.BLACK, Color.WHITE},
                            {ValorRec,null,2,new Font("Times New Roman",Font.BOLD,8), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {null,null,2,null, Color.BLACK, Color.WHITE},
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        if (nAut > 0) {
              tmp = new Object[][] {
                                      {"VALOR(ES) LANCADOS",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                   };
              linhas =  Printer.adcLine(linhas, tmp);

              tmp = new Object[][] {
                                  {null,null,2,null, Color.BLACK, Color.WHITE},
                               };
              linhas =  Printer.adcLine(linhas, tmp);

            for (i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

               tmp = new Object[][] {
                                      {bLinha,null,2,new java.awt.Font("Arial",java.awt.Font.PLAIN,6), Color.BLACK, Color.WHITE}
                                   };
               linhas =  Printer.adcLine(linhas, tmp);
            }

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                 };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                {null,null,2,null, Color.BLACK, Color.WHITE},
                             };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                 };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            // Imprimir Autenticação
            tmp = new Object[][] {
                                    {VariaveisGlobais.dCliente.get("marca").trim() + "AV" + FuncoesGlobais.StrZero(String.valueOf((int)nAut).replace(".0", ""), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,null,0,new java.awt.Font("Arial",java.awt.Font.BOLD,7), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);

//            // Codigo de barra
//            tmp = new Object[][] {
//                                    {String.valueOf((int)nAut),null,0,new java.awt.Font("3 of 9 Barcode",java.awt.Font.PLAIN,32), Color.BLACK, Color.WHITE}
//                                };
//            linhas =  Printer.adcLine(linhas, tmp);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            tmp = new Object[][] {
                                    {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                };
            linhas =  Printer.adcLine(linhas, tmp);
        }

        String docID = "";
        if (!idAviso.trim().equals("")) {
            docID = "_" + idAviso.substring(0, idAviso.indexOf("-") -1).trim() + ".ps";
        }
        String docName = "reports/Recibos/" + "AV_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
                //"reports/Recibos/" + "AV_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;

        jThermica np = new jThermica(linhas);
        np.setDocName(docName);
        np.toSave();
//        np.toPrint();
        np.setDocName("");
    }

   public void ImprimeAntecipaPDF(float nAut, String[][] Valores, String ValorRec, String cutPaper) {
        float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();

        PdfPCell cell1, cell2, cell3, cell4;        
        
        String docName = "AT_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut).replace(".0", ""), 7) + ".pdf";
        String pathName = "reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
        pdf.setPathName(pathName);
        pdf.setDocName(docName);
        
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);

        pdf.open();
        
        // Logo
        com.itextpdf.text.Image img;
        try {
            img = com.itextpdf.text.Image.getInstance("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif");
            img.setAlignment(Element.ALIGN_LEFT);        
            pdf.doc_add(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Paragraph p;
        
        p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
            p = pdf.print(gVar.get("tipodoc"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT,pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Tel/Fax:" + gVar.get("telefone"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("AVISO ANTECIPAÇÕES - DEBITO", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        
        cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Lista Retenções
        columnWidths = new float[] {30,20,25,25};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        for (int c=0; c< jtbAntecip.getRowCount(); c++) {
            int modelRow = jtbAntecip.convertRowIndexToModel(c);
            if ("true".equals(jtbAntecip.getModel().getValueAt(modelRow, 7).toString().toLowerCase())) {
                font.setColor(BaseColor.BLACK);
                cell1 = new PdfPCell(new Phrase(jtbAntecip.getModel().getValueAt(modelRow, 6).toString(),font));
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell1);
                cell2 = new PdfPCell(new Phrase(jtbAntecip.getModel().getValueAt(modelRow, 2).toString(),font));
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell2.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell2);
                cell3 = new PdfPCell(new Phrase(jtbAntecip.getModel().getValueAt(modelRow, 4).toString(),font));
                cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell3.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell3);
                cell4 = new PdfPCell(new Phrase(jtbAntecip.getModel().getValueAt(modelRow, 5).toString(),font));
                cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell4.setBorder(Rectangle.NO_BORDER);
                cell4.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell4);

                cell1 = new PdfPCell(new Phrase(jtbAntecip.getModel().getValueAt(modelRow, 3).toString(),font));
                cell1.setColspan(4);
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell1);
            }
        }
        table.completeRow();
        pdf.doc_add(table);
        
        columnWidths = new float[] {70, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("==========", font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(ValorRec, font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        if (nAut > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();

                p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                pdf.doc_add(p);
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + "AT" +
                    FuncoesGlobais.StrZero(String.valueOf((int)nAut).replace(".0", ""), 7) + "-1" + 
                    Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + 
                    FuncoesGlobais.GravaValores(ValorRec, 2) + 
                    VariaveisGlobais.usuario, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);
            
            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)nAut).replace(".0", ""),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            com.itextpdf.text.Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);            
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        
        pdf.close();
        //pdf.print();
        new toPrint(pathName + docName, VariaveisGlobais.Aviso.split(",")[0],VariaveisGlobais.Aviso.split(",")[1],VariaveisGlobais.Aviso.split(",")[2]);
        pdf.setPathName("");
        pdf.setDocName("");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btprePrint;
    private javax.swing.JTabbedPane jAbas;
    private javax.swing.JComboBox jAdm;
    private javax.swing.JTextField jBuscaAntecip;
    private javax.swing.JTextField jBuscaPasta;
    private javax.swing.JComboBox jContr;
    private javax.swing.JComboBox jIdConta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox jNmLoc;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
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
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JCheckBox jSelAllAntecip;
    private javax.swing.JCheckBox jSelAllChCM;
    private javax.swing.JComboBox jSocios;
    private javax.swing.JFormattedTextField jValor;
    private javax.swing.JCheckBox jVias;
    private javax.swing.JButton jbtListarAntecip;
    private javax.swing.JButton jbtListarRetencoes;
    private javax.swing.JToggleButton jbtTipo;
    private javax.swing.JButton jbtTotalizar;
    private javax.swing.JButton jbtTotalizar1;
    private javax.swing.JComboBox jnmConta;
    private javax.swing.JPanel jpRecebe;
    private javax.swing.JTable jtbAntecip;
    private javax.swing.JTable jtbReten;
    private javax.swing.JTextArea jtxtAdm;
    private javax.swing.JTextArea jtxtConta;
    private javax.swing.JTextArea jtxtDisp;
    private javax.swing.JTextArea jtxtLoca;
    private javax.swing.JTextArea jtxtSoc;
    private javax.swing.JTextArea mDesc;
    private javax.swing.JTable tblLanca;
    // End of variables declaration//GEN-END:variables
}
