/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jAltRecibos.java
 *
 * Created on 25/02/2011, 16:28:24
 */

package Movimento;

import Bancos.Santander;
import Bancos.bancos;
import Bancos.itau;
//import Bancos.itau;
import Funcoes.AutoCompletion;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.JavaPOS;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.PrinterPOS;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.newTable;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import boleta.Boleta;
import Sici.Partida.Collections;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Year;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import javax.swing.*;

/**
 *
 * @author supervisor
 */
public class jRemessa extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String rgprp = ""; String rgimv = ""; String contrato = ""; String rcampo = "";
    boolean executando = false; boolean mCartVazio = false;
    boolean bExecNome = false, bExecCodigo = false, eBloq = false;

    // Colecao para dados Bancários
    private Collections dadosBanco = new Collections();
    
    // campos de calculos
    float exp = 0;
    float multa = 0;
    float juros = 0;
    float correcao = 0;

    /** Creates new form jAltRecibos */
    public jRemessa() {
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
                String[][] vCampos = null;
                try {
                    vCampos = conn.LerCamposTabela(new String[] {"l.contrato", "l.rgprp", "l.rgimv", "l.aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "r.nnumero"}, "locatarios l, imoveis i, proprietarios p, RECIBO r", "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp AND r.contrato = l.contrato) AND l.contrato = '" + jContrato.getSelectedItem().toString() + "' AND NOT ISNULL(r.nnumero)");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                if (vCampos == null) {
                    JOptionPane.showMessageDialog(null, "Contrato não encontrado!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                rgprp = vCampos[1][3];
                rgimv = vCampos[2][3];
                contrato = vCampos[0][3];

                if (vCampos[12][3] == null) {
                    JOptionPane.showMessageDialog(null, "Este recibo não é uma boleta!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    jVencimentos.setEnabled(true);
                    jNomeLoca.setEnabled(true);
                    jContrato.setEnabled(true);
                    jContrato.requestFocus();            
                    return;
                } 
                
                SetaCampos(vCampos);
                try {
                    executando = true;
                    SpinnerModel sm = new SpinnerNumberModel(300, 1, 300, 1);
                    ListaVectos();
                    executando = false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                boolean bbloq = true;
                if (jVencimentos.getItemCount() > 0) {
                    Recalcula();
                } else {
                    JOptionPane.showMessageDialog(null, "Não existe recibo(s) para este contrato!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    jVencimentos.setEnabled(true);
                    jNomeLoca.setEnabled(true);
                    jContrato.setEnabled(true);
                    jContrato.requestFocus();
                } 
                btLancar.setEnabled(true);
                
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
            }
        });

        ComboBoxEditor editContrato = jContrato.getEditor();
        Component compContrato = editContrato.getEditorComponent();
        compContrato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                //
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
            }
        });

        String[][] cab = new String[][] {{"contrato","nome","vencimento","valor","nnumero","bolvecto"},
                                         {"80","200","80","80","100","80"}};
        TableControl.header(atable, cab);
    
        // Numerador de lote
        String _banco = jbanco.getSelectedItem().toString().trim().substring(0, 3);
        int lotenr = 0; try { lotenr = Integer.valueOf(conn.LerParametros("LOTE_" + _banco)); } catch (Exception e) {}
        jnrlote.setValue(lotenr);
    }

    class MyRenderer extends JLabel implements ListCellRenderer {
        public MyRenderer() {
                setOpaque(true);
                setHorizontalAlignment(LEFT);
                setVerticalAlignment(CENTER);
        }
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (isSelected) {
                    setBackground(Color.WHITE); //the background color of the selected item
                    setForeground(Color.RED); //the textcolor of the selected item
                } else {
                    setBackground(Color.WHITE); //the background color of the other items
                    setForeground(Color.RED); //the textcolor of the other items
                }
                setText(value.toString());
                return this;
        }
    }

    class FixDisabledCBRenderer implements ListCellRenderer {
        private JComboBox comboBox;
        private ListCellRenderer renderer;
        public FixDisabledCBRenderer(JComboBox combo, ListCellRenderer renderer) {
            comboBox = combo;
            this.renderer = renderer;
        }
        public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
            Component return_value = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (return_value instanceof JComponent) ((JComponent)return_value).setOpaque(comboBox.isEnabled());
            comboBox.setEditable(true);
            //comboBox.getEditor().getEditorComponent().setEnabled(false);
            if (isSelected) {
                comboBox.getEditor().getEditorComponent().setBackground(Color.WHITE); //the background color of the selected item
                comboBox.getEditor().getEditorComponent().setForeground(Color.RED); //the textcolor of the selected item
            } else {
                comboBox.getEditor().getEditorComponent().setBackground(Color.WHITE); //the background color of the other items
                comboBox.getEditor().getEditorComponent().setForeground(Color.RED); //the textcolor of the other items
            }
            //comboBox.setEditable(true);
            return return_value;
        }
    }

    private void CalcularRecibo(String vecto) {
        if ("".equals(vecto.trim())) { return; }

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(this.rgprp, this.rgimv, this.contrato);
        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        String campo = "";
        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
            ex.printStackTrace();
        }
        DbMain.FecharTabela(pResult);

        try {
            exp = rc.TaxaExp(campo);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            multa = rc.Multa(campo, vecto, false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            juros = rc.Juros(campo, vecto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            correcao = rc.Correcao(campo, vecto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        jVrRecibo.setValue(tRecibo + exp + multa + juros + correcao);
    }

    private void SetaCampos(String[][] vCampos) {
        jRgimv.setText(vCampos[2][3]);
        jNomeProp.setText(vCampos[11][3]);
        jEndereco.setText(vCampos[4][3].trim() + ", " + vCampos[5][3].trim() + " " + vCampos[6][3]);
        jBairro.setText(vCampos[7][3]);
        jCidade.setText(vCampos[8][3]);
        jEstado.setText(vCampos[9][3]);
        jCep.setText(vCampos[10][3]);
        jNNumero.setText(vCampos[12][3]);
    }

    private void LimpaTela() {
        jRgimv.setText("");
        jNomeProp.setText("");
        jEndereco.setText("");
        jBairro.setText("");
        jCidade.setText("");
        jEstado.setText("");
        jCep.setText("");
        jNNumero.setText("");
        
        jctCampos.removeAll();
        jctCampos.repaint();

        executando = true;
        jVencimentos.removeAllItems();
        executando = false;

        jVrRecibo.setValue(0);
        jTotalRecibos.setValue(0);
        
        btLancar.setEnabled(false);
    }

    private void ListaVectos() throws SQLException {
        String sql = "SELECT * FROM RECIBO WHERE NOT ISNULL(nnumero) AND contrato = '" + contrato + "' AND tag <> 'X' ORDER BY dtvencimento;";
        ResultSet hRs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        try {
            jVencimentos.removeAllItems();
        } catch (Exception ex) { ex.printStackTrace(); }

        while (hRs.next()) {
            Date jVecto = null;
            try {
                jVecto = (java.util.Date) hRs.getDate("dtvencimento");
            } catch (SQLException ex) { ex.printStackTrace(); }
            jVencimentos.addItem(Dates.DateFormata("dd/MM/yyyy", jVecto));
        }
        DbMain.FecharTabela(hRs);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jContrato = new javax.swing.JComboBox();
        jNomeLoca = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jRgimv = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jNomeProp = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jEndereco = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jBairro = new javax.swing.JLabel();
        jCidade = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jEstado = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jCep = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jbtEnvios = new javax.swing.JToggleButton();
        btnAuto = new javax.swing.JButton();
        jScroll = new javax.swing.JScrollPane();
        jctCampos = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jVencimentos = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jVrRecibo = new javax.swing.JFormattedTextField();
        jTotalRecibos = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        btLancar = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jNNumero = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        atable = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jTotBoletas = new javax.swing.JFormattedTextField();
        btExcluir = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jbanco = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jnomeArq = new javax.swing.JTextField();
        btGerar = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jnrlote = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        jtparq = new javax.swing.JComboBox();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Remessa ::.");
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel1.setBackground(new java.awt.Color(191, 191, 191));
        jLabel1.setText("Contrato");
        jLabel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel1.setOpaque(true);

        jLabel2.setBackground(new java.awt.Color(191, 191, 191));
        jLabel2.setText("Nome");
        jLabel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel2.setOpaque(true);

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

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        jLabel3.setText("RgImv:");
        jLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jRgimv.setBackground(new java.awt.Color(254, 254, 254));
        jRgimv.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jRgimv.setOpaque(true);

        jLabel5.setText("Proprietário:");
        jLabel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jNomeProp.setBackground(new java.awt.Color(254, 254, 254));
        jNomeProp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jNomeProp.setOpaque(true);

        jLabel7.setText("End.:");
        jLabel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jEndereco.setBackground(new java.awt.Color(254, 254, 254));
        jEndereco.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jEndereco.setOpaque(true);

        jLabel9.setText("Bairro:");
        jLabel9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jBairro.setBackground(new java.awt.Color(254, 254, 254));
        jBairro.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jBairro.setOpaque(true);

        jCidade.setBackground(new java.awt.Color(254, 254, 254));
        jCidade.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jCidade.setOpaque(true);

        jLabel12.setText("Cidade:");
        jLabel12.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jEstado.setBackground(new java.awt.Color(254, 254, 254));
        jEstado.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jEstado.setOpaque(true);

        jLabel14.setText("Estado:");
        jLabel14.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jCep.setBackground(new java.awt.Color(254, 254, 254));
        jCep.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jCep.setOpaque(true);

        jLabel16.setText("Cep:");
        jLabel16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setText("Endereço:");
        jLabel8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCep, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNomeProp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEndereco, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCep, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel3});

        jbtEnvios.setText("A Enviar");
        jbtEnvios.setToolTipText("");
        jbtEnvios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtEnviosActionPerformed(evt);
            }
        });

        btnAuto.setText("Automático");
        btnAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtEnvios, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAuto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbtEnvios)
                        .addComponent(btnAuto)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jContrato, jLabel1, jLabel2, jNomeLoca});

        jScroll.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Recibo"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScroll.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jctCampos.setEnabled(false);

        javax.swing.GroupLayout jctCamposLayout = new javax.swing.GroupLayout(jctCampos);
        jctCampos.setLayout(jctCamposLayout);
        jctCamposLayout.setHorizontalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );
        jctCamposLayout.setVerticalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );

        jScroll.setViewportView(jctCampos);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Venctos:");

        jVencimentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentosActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Vr.Recibo:");

        jVrRecibo.setEditable(false);
        jVrRecibo.setForeground(new java.awt.Color(255, 51, 51));
        jVrRecibo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jVrRecibo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jVrRecibo.setText("0,00");
        jVrRecibo.setDisabledTextColor(new java.awt.Color(255, 51, 51));
        jVrRecibo.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jTotalRecibos.setEditable(false);
        jTotalRecibos.setForeground(new java.awt.Color(255, 51, 51));
        jTotalRecibos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jTotalRecibos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTotalRecibos.setText("0,00");
        jTotalRecibos.setDisabledTextColor(new java.awt.Color(255, 51, 51));
        jTotalRecibos.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Tot.Recibos:");

        btLancar.setText("Lancar");
        btLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLancarActionPerformed(evt);
            }
        });

        jLabel15.setText("N.Numero:");

        jNNumero.setEditable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jNNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jVencimentos, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btLancar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jVrRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTotalRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jVencimentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btLancar)
                    .addComponent(jLabel15)
                    .addComponent(jNNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jVrRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTotalRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        atable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        atable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(atable);

        jLabel10.setText("Total Boletas:");

        jTotBoletas.setEditable(false);
        jTotBoletas.setBackground(new java.awt.Color(254, 254, 254));
        jTotBoletas.setForeground(new java.awt.Color(37, 181, 1));
        jTotBoletas.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jTotBoletas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTotBoletas.setText("0,00");
        jTotBoletas.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N

        btExcluir.setText("Excluir Selecionado");
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });

        jLabel11.setText("Banco:");

        jbanco.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "033 - Banco Santander S.A.", "341 - Banco Itaú S.A." }));
        jbanco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbancoActionPerformed(evt);
            }
        });

        jLabel13.setText("Nome Arquivo:");

        jnomeArq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jnomeArqKeyReleased(evt);
            }
        });

        btGerar.setText("Gerar");
        btGerar.setEnabled(false);
        btGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGerarActionPerformed(evt);
            }
        });

        jLabel17.setText("Lote Nº.:");

        jnrlote.setModel(new javax.swing.SpinnerNumberModel());

        jLabel18.setText("Tipo de Arquivo:");

        jtparq.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01 - ENTRADA DE TITULOS", "02 - PEDIDO DE BAIXA", "06 - ALTERAÇÃO DE VENCIMENTO", "09 - PEDIDO DE PROTESTO", "18 - PEDIDO DE SUSTAÇÃO DE PROTESTO", "31 - ALTERAÇÃO DE OUTROS DADOS", "98 - NÃO PROTESTAR (ANTES DE INICIAR CICLO DE PROTESTO)" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtparq, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jnrlote, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jnomeArq, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbanco, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTotBoletas))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTotBoletas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(btExcluir)
                    .addComponent(jLabel11)
                    .addComponent(jbanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jnomeArq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btGerar)
                    .addComponent(jLabel17)
                    .addComponent(jnrlote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jtparq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FillCombos() {
        String sSql = "SELECT DISTINCT r.contrato AS codigo, Upper(l.nomerazao) AS nome, r.nnumero FROM RECIBO r, locatarios l WHERE (r.contrato = l.contrato) AND r.tag != 'X' AND (r.nnumero != '' OR NOT ISNULL(r.nnumero)) AND remessa = '" + (jbtEnvios.isSelected() ? "S" : "N") + "' ORDER BY Upper(l.nomerazao);";
        jContrato.removeAllItems();
        jNomeLoca.removeAllItems();
        if (!sSql.isEmpty()) {
            ResultSet imResult = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

            try {
                while (imResult.next()) {
                    jContrato.addItem(String.valueOf(imResult.getInt("codigo")));
                    jNomeLoca.addItem(imResult.getString("nome"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            DbMain.FecharTabela(imResult);
        }
    }

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

    private void jVencimentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jVencimentosActionPerformed
        Recalcula();
}//GEN-LAST:event_jVencimentosActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
       try  {
            this.requestFocus();
            this.setSelected(true);
       } catch (java.beans.PropertyVetoException e)  { e.printStackTrace(); }
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
    }//GEN-LAST:event_formInternalFrameClosing

    private void btLancarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLancarActionPerformed
        if (TableControl.seek(atable, 4, jNNumero.getText()) > -1) {
            return;
        }
        
        String _banco = jbanco.getSelectedItem().toString().trim().substring(0, 3);
        String _selbc = jNNumero.getText().trim().substring(0, 3);
        if (!_selbc.equalsIgnoreCase(_banco)) {
            JOptionPane.showMessageDialog(null, "Este recibo não pertence ao banco selecionado!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);            
            return;
        }
        
        String tDataBol = null;
        Object[] options = { "Sim", "Não" };
        if (JOptionPane.showOptionDialog(null, "Esta boleta teve seu vencimento alterado?","Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]) == JOptionPane.YES_OPTION) {
            tDataBol = (String) JOptionPane.showInputDialog(null, "Entre com o Vencimento do Boleto (dd/MM/yyyy");
            if (tDataBol == null || !Dates.isDateValid(tDataBol, "dd/MM/yyyy")) {
                JOptionPane.showMessageDialog(null, "Data Vazia ou inválida!!\n Boleta segue sem alterações...");
                tDataBol = null;
            }
        }
        
        Object[] aItens = {jContrato.getSelectedItem().toString(), 
                           jNomeLoca.getSelectedItem().toString(), 
                           jVencimentos.getSelectedItem().toString(),
                           jVrRecibo.getText(),
                           jNNumero.getText(),
                           tDataBol
                          };
        newTable.add(atable, aItens);
        
        float totbol = TotalBoletos();
        jTotBoletas.setValue(totbol);
        
        jContrato.requestFocus();
    }//GEN-LAST:event_btLancarActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        int pos = atable.getSelectedRow();
        if (pos > -1) {
            TableControl.del(atable, pos);
            float totbol = TotalBoletos();
            jTotBoletas.setValue(totbol);
        }
    }//GEN-LAST:event_btExcluirActionPerformed

    private void btGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGerarActionPerformed
        if (atable.getRowCount() == 0) return;
        
        String _banco = jbanco.getSelectedItem().toString().trim().substring(0, 3);
        String nrlote = String.valueOf((Integer)jnrlote.getValue() + 1);
        
        try {conn.GravarParametros(new String[] {});} catch (Exception ex) {}
        
        String[][] relacao = {};
        for (int i=0; i<atable.getRowCount();i++) {
            String[][] vCampos = null;
            try {
                vCampos = conn.LerCamposTabela(new String[] {"l.contrato", "l.rgprp", "l.rgimv", "l.aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "r.nnumero"}, "locatarios l, imoveis i, proprietarios p, RECIBO r", "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp AND r.contrato = l.contrato) AND l.contrato = '" + atable.getModel().getValueAt(i, 0).toString() + "'");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String _rgprp = vCampos[1][3];
            String _rgimv = vCampos[2][3];
            String _contrato = vCampos[0][3];
            String _vencto = atable.getModel().getValueAt(i, 2).toString();
            String _rnnumero = atable.getModel().getValueAt(i, 4).toString();
            String _dataBol = null;
            try { _dataBol =atable.getModel().getValueAt(i, 5).toString(); } catch (Exception e) {_dataBol = null;}
            
            Boleta bean1 = new Boleta();                
            try {
                bean1 = CreateBoleta(_rgprp, _rgimv, _contrato, _vencto, _rnnumero, _dataBol);
            } catch (SQLException ex) { ex.printStackTrace(); }

            // Colocado aqui para testar 07-12-2016 12h41m
            relacao = FuncoesGlobais.ArraysAdd(relacao, new String[] {_contrato, bean1.getbolDadosVencimento(), bean1.getbolDadosVrdoc(), FuncoesGlobais.StrZero(bean1.getbolDadosNnumero().substring(3).trim(),13)});
        }                
        
        String operacao = jtparq.getSelectedItem().toString().substring(0, 2);
        if (_banco.equalsIgnoreCase("033")) {
            // Colocado aqui para testar 07-12-2016 12h41m
            Santander.Remessa(nrlote, jnomeArq.getText().trim().toLowerCase(), operacao, relacao, ""); // era null
        } else if (_banco.equalsIgnoreCase("341")) {
            itau.Remessa(nrlote, jnomeArq.getText().trim().toLowerCase(), operacao, relacao, "");
        }
        
        jnrlote.setValue(Integer.valueOf(nrlote));
        jnomeArq.setText(null);
        TableControl.Clear(atable);
        //TableControl.delall(atable);
        jContrato.requestFocus();
    }//GEN-LAST:event_btGerarActionPerformed

    private void jnomeArqKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jnomeArqKeyReleased
        btGerar.setEnabled(jnomeArq.getText().trim().length() > 3);
    }//GEN-LAST:event_jnomeArqKeyReleased

    private void jbancoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbancoActionPerformed
        // Numerador de lote
        String _banco = jbanco.getSelectedItem().toString().trim().substring(0, 3);
        int lotenr = 0; try { lotenr = Integer.valueOf(conn.LerParametros("LOTE_" + _banco)); } catch (Exception e) {}
        jnrlote.setValue(lotenr);
    }//GEN-LAST:event_jbancoActionPerformed

    private void jbtEnviosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtEnviosActionPerformed
        if (jbtEnvios.isSelected()) {
            jbtEnvios.setText("Enviados");
        } else {
            jbtEnvios.setText("A Enviar");
        }
        FillCombos();
    }//GEN-LAST:event_jbtEnviosActionPerformed

    private void btnAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoActionPerformed
        String aSql = "SELECT rgprp, rgimv, contrato, dtvencimento, nnumero FROM recibo where tag <> 'X' and not isnull(nnumero) and remessa = 'N' and year(DTVENCIMENTO) = " + Dates.DateFormata("yyyy", new Date()) + " ORDER BY dtvencimento;";
        ResultSet brs = conn.AbrirTabela(aSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (brs.next()) {
                // Coleta de dados
                String _rgprp = brs.getString("rgprp");
                String _rgimv = brs.getString("rgimv");
                String _contrato = brs.getString("contrato");
                String _vencto = Dates.DateFormata("dd/MM/yyyy", brs.getDate("dtvencimento"));
                String _rnnumero = brs.getString("nnumero");
                String _dataBol = null;
                
                // Calculos
                Boleta bean1 = new Boleta();                
                try {
                    bean1 = CreateBoleta(_rgprp, _rgimv, _contrato, _vencto, _rnnumero, _dataBol);  
                } catch (SQLException ex) { ex.printStackTrace(); }            
                
                Object[] aItens = {_contrato, 
                                   bean1.getsacDadosNome(), 
                                   _vencto,
                                   bean1.getbolDadosVrdoc(),
                                   _rnnumero,
                                   null
                                  };
                newTable.add(atable, aItens);

                float totbol = TotalBoletos();
                jTotBoletas.setValue(totbol);
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(brs); } catch (Exception e) {}
        
        jContrato.requestFocus();
    }//GEN-LAST:event_btnAutoActionPerformed

    private String fmtNumero(String value) {
        String numero = "000000000000000";
        value = value.substring(0, value.indexOf(",") + 3);
        String saida = (numero + rmvNumero(value)).trim();
        return saida.substring(saida.length() - 15);
    }
    
    private String rmvNumero(String value) {
        String ret = "";
        for (int i=0;i<value.length();i++) {
            if (value.substring(i, i + 1).equalsIgnoreCase(".") || value.substring(i, i + 1).equalsIgnoreCase("/") || value.substring(i, i + 1).equalsIgnoreCase("-") || value.substring(i, i + 1).equalsIgnoreCase(",") || value.substring(i, i + 1).equalsIgnoreCase(" ")){
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }

    private String rmvLetras(String value) {
        String ret = "";
        for (int i=0; i<value.length();i++) {
            char letra = value.charAt(i);
            if (value.substring(i, i + 1).equalsIgnoreCase(":")) {
                //
            } else if ((int)letra < 48 || (int)letra > 57) {                  
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }
    
    private void transasao(String rgprp, String rgimv, String contrato, String nrcontrole) {
        String codBaco = "033";
        String loteSer = "0000";
        String tipoSer = "0";
        String reserVd = FuncoesGlobais.Space(8);
        String tipoInsc = "2";
        String inscEmp = "014277429000135";  // cnpj
        String codTran = "340200005633508";  // dados da conta
        String reseVad = FuncoesGlobais.Space(25); // 25 digitos
        String nomeEmp = "ES ADMINISTRACAO DE IMOVEIS LT"; // 30 digitos
        String nomeBan = "BANCO SANTANDER" + FuncoesGlobais.Space(15); // 30 digitos
        String resVado = FuncoesGlobais.Space(10); // 10 digitos
        String codRems = "1";
        String dtGerac = Dates.DateFormata("ddMMyyyy", new Date()); // data atual
        String rservDo = FuncoesGlobais.Space(6); // 6 barncos
        String numSequ = FuncoesGlobais.StrZero(nrcontrole, 6); // 6 digitos de 1 a 999999
        String Versaos = "040";
        String reSerVa = FuncoesGlobais.Space(74); // 74 digitos

        String codBco = "033"; 
        String loteRe = "0001"; 
        String tpRems = "1";
        String tpOper = "R";
        String tpServ = "01";
        String reseVd = FuncoesGlobais.Space(2);
        String nversa = "030";
        String resedo = FuncoesGlobais.Space(1);
        String tpInsc = "2";
        String cnpjEp = "014277429000135";
        String resVdo = FuncoesGlobais.Space(20);
        String codTrE = "340200005633508";
        String rseVdo = FuncoesGlobais.Space(5);
        String nomeCd = "ES ADMINISTRACAO DE IMOVEIS LT"; // 30dig
        String mensN1 = FuncoesGlobais.Space(40); // 40dig
        String mensN2 = FuncoesGlobais.Space(40); // 40dig
        String numRRt = "00000000"; // 8dig
        String dtgrav = Dates.DateFormata("ddMMyyyy", new Date());;
        String reVado = FuncoesGlobais.Space(41);

        
        // P
        int nrSeqL = 1;
        String codBcC = "033";
        String nrReme = "0001";
        String tpRegi = "3";
        String nrSequ = FuncoesGlobais.StrZero(String.valueOf(nrSeqL), 5); //numero de seq do lote
        String cdSegR = "P";
        String rsvDos = FuncoesGlobais.Space(1);
        String cdMvRm = "01"; // 01 - Entrada de título
        String agCedn = "3402";  // agencia do cedente
        String digAgc = "9"; // digito verificador
        String numCoC = "013003361"; // Conta Corrente
        String digCoC = "8"; // digito verificador
        String contCb = "013003361"; // Conta cobranca
        String digtCb = "8"; // digito
        String rservo = FuncoesGlobais.Space(2);
        String nnumer = ""; // nosso numero com 13 dig
        String tpoCob = "5"; // tipo de cobrança
        String formCd = "1"; // forma de cadastramento
        String tipoDc = "2"; // tipo de documento
        String rsvad1 = FuncoesGlobais.Space(1);
        String rsvad2 = FuncoesGlobais.Space(2);
        String numDoc = "000000000000000"; // numero do documento (livre)
        String dtavtt = "ddmmaaaa"; // data de vencimento do titulo
        String vrnmtt = "000000000123129"; // valor nominal do titulo
        String agencb = "0000"; // agencia encarregada
        String digaec = "0"; // digito
        String rsvado = FuncoesGlobais.Space(1);
        String esptit = "04"; // especie de titulo
        String idtitu = "N";
        String dtemti = "ddmmaaaa"; // data emissao do titulo
        String cdjuti = "1"; // codigo juros do titulo
        String dtjrmo = "ddmmaaaa"; // data juros mora
        String vrmtxm = "000000000000041"; // valor ou taxa de mora (aluguel * 0,0333)
        String cddesc = "0"; // codigo desconto
        String dtdesc = "00000000"; // data desconto
        String vrpecd = "000000000000000"; // valor ou percentual de desconto
        String vriofr = "000000000000000"; // iof a ser recolhido
        String vrabti = "000000000000000"; // valor abatimento
        String idttep = FuncoesGlobais.Space(25);
        String cdprot = "0"; // codigo para protesto
        String nrdpro = "00"; // numero de dias para protesto
        String cdbxdv = "2"; // codigo baixa devolucao
        String revdao = "0";
        String nrdibd = "00"; // numero de dias baixa devolucao
        String cdmoed = "00"; // codigo moeda
        String revado = FuncoesGlobais.Space(11);
        
        // Sqgmento Q
        String cdbcoc = "033";
        String nrltre = "0001";
        String tiporg = "3";
        nrSeqL += 1;
        String nrSeqq = FuncoesGlobais.StrZero(String.valueOf(nrSeqL), 5); //numero de seq do lote
        String cdregt = "Q";
        String bracos = FuncoesGlobais.Space(1);
        String cdmvrm = "01";
        String tpinss = "1"; // tipo inscricao sacado
        String inscsc = "000000000000000"; // CPF/CNPJ
        String nmesac = "(40)"; // nome do sacado
        String endsac = "(40)"; // endereco 
        String baisac = "(15)"; // bairro
        String cepsac = "(5)";  // cep
        String cepsus = "(3)";  // sufixo cep
        String cidsac = "(15)"; // cidade
        String ufsaca = "RJ";   // UF
        String demais = "0000000000000000                                        000000000000                   ";
        
        // R
        String cbcodc = "033";
        String nrlotr = "0001";
        String tporeg = "3";
        nrSeqL += 1;
        String nrSeqr = FuncoesGlobais.StrZero(String.valueOf(nrSeqL), 5); //numero de seq do lote
        String cdgseg = "R";
        String spacob = FuncoesGlobais.Space(1);
        String cdomot = "01";
        String cdgdes = "0"; // codigo desconto
        String dtdes2 = "00000000"; // data desconto 2
        String vrpccd = "000000000000000"; // valor perc desco
        String brac24 = FuncoesGlobais.Space(24);
        String cdmult = "2"; // codigo da multa (1 - fixo / 2 - perc)
        String dtamul = "ddmmaaaa"; // data multa
        String vrpcap = "000000000001000"; // vr/per multa
        String bran10 = FuncoesGlobais.Space(10);
        String msge03 = FuncoesGlobais.Space(40); // msg 3
        String msge04 = FuncoesGlobais.Space(40); // msg 4
        String branfn = FuncoesGlobais.Space(61);
        
        
        // S
        String codbcc = "033";
        String nrorem = "0001";
        String tppreg = "3";
        nrSeqL += 1;
        String nrSeqs = FuncoesGlobais.StrZero(String.valueOf(nrSeqL), 5); //numero de seq do lote
        String cdoseg = "S";
        String branrs = FuncoesGlobais.Space(1);
        String cdgmvt = "01";
        
        String idimpr = "1";
        int nrlin = 1;
        String nrlnip = FuncoesGlobais.StrZero(String.valueOf(nrlin), 2); // nrlinha impressa 01 ate 22
        String msgimp = "4"; 
        String msgipr = "(100)"; // mensagem a imprimir
        String brancs = FuncoesGlobais.Space(119);
        
        
        // trailer lote
        String cdgcom = "033";
        String nrores = "0001";
        String tporgt = "5";
        String brantl = FuncoesGlobais.Space(9);
        String qtdrlt = "000000"; // quantidade reg no lote
        String brcolt = FuncoesGlobais.Space(217);
        
        // trailer arquivo remessa
        String cgdcop = "033";
        String nrlote = "9999";
        String tpregi = "9";
        String brcoat = FuncoesGlobais.Space(9);
        String qtdlaq = "000001"; // quantidade de lotes do arquivo
        String qtdrga = "000000"; // quantidade reg do arquivo tipo=0+1+2+3+5+9
        String brcalt = FuncoesGlobais.Space(211);

    }
    
    private float TotalBoletos() {
        float tbol = 0;
        for (int i=0; i<atable.getRowCount(); i++) {
            tbol += LerValor.StringToFloat(atable.getModel().getValueAt(i, 3).toString());
        }
        return tbol;
    }
    
    private String CriticaCampo(String nContrato, String dVecto) throws SQLException {
        String[] aCampo; int tCampo = 0; String ctCampo = "";
        String cSELECT = "SELECT * FROM RECIBO WHERE contrato = '" + nContrato + "' AND Cast(dtVencimento as CHAR(10)) = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(dVecto,"dd-MM-yyyy")) + "';";
        ResultSet Data1 = conn.AbrirTabela(cSELECT, ResultSet.CONCUR_READ_ONLY);

        boolean cbMulta, cbJuros, cbCorrecao, cbTaxa = false;
        float tbTaxa, tbMulta, tbJurosxt, tbCorrecao = 0;

        String tmpCampo = "";
        if (Data1.next()) {
            tmpCampo = "" + Data1.getString("CAMPO");

            aCampo = tmpCampo.split(";");
            aCampo = FuncoesGlobais.OrdenaMatriz(aCampo, 3, 1, true);
            tCampo = aCampo.length - 1;

            cbMulta = tmpCampo.contains("MU");
            cbJuros = tmpCampo.contains("JU");
            cbCorrecao = tmpCampo.contains("CO");
            cbTaxa = tmpCampo.contains("EP");

            Calculos rc = new Calculos();
            rc.Inicializa(this.rgprp, this.rgimv, this.contrato);
            tbTaxa = rc.TaxaExp(tmpCampo);
            tbMulta = rc.Multa(tmpCampo, dVecto, false);
            tbJurosxt = rc.Juros(tmpCampo, dVecto);
            tbCorrecao = rc.Correcao(tmpCampo, dVecto);

            for (int nConta=0; nConta <= tCampo; nConta++) {
                ctCampo = aCampo[nConta];
                String[] rCampos = ctCampo.split(":");

                if ("AL".equals(rCampos[4])) {
                    int iMulta = FuncoesGlobais.IndexOf(rCampos,"MU");
                    if (iMulta > -1) {
                        rCampos[iMulta] = "MU" + FuncoesGlobais.GravaValores(String.valueOf(tbMulta).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "MU" + FuncoesGlobais.GravaValores(String.valueOf(tbMulta).replace(".", ","), 2));
                    }

                    int iJuros = FuncoesGlobais.IndexOf(rCampos,"JU");
                    if (iJuros > -1) {
                        rCampos[iJuros] = "JU" + FuncoesGlobais.GravaValores(String.valueOf(tbJurosxt).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "JU" + FuncoesGlobais.GravaValores(String.valueOf(tbJurosxt).replace(".", ","), 2));
                    }

                    int iCorrecao = FuncoesGlobais.IndexOf(rCampos,"CO");
                    if (iCorrecao > -1) {
                        rCampos[iCorrecao] = "CO" + FuncoesGlobais.GravaValores(String.valueOf(tbCorrecao).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "CO" + FuncoesGlobais.GravaValores(String.valueOf(tbCorrecao).replace(".", ","), 2));
                    }

                    int iExp = FuncoesGlobais.IndexOf(rCampos,"EP");
                    if (iExp > -1) {
                        rCampos[iExp] = "EP" + FuncoesGlobais.GravaValores(String.valueOf(tbTaxa).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "EP" + FuncoesGlobais.GravaValores(String.valueOf(tbTaxa).replace(".", ","), 2));
                    }
                }
                aCampo[nConta] = FuncoesGlobais.join(rCampos, ":");
            }
            tmpCampo = FuncoesGlobais.join(aCampo, ";");
        }
        DbMain.FecharTabela(Data1);

        return tmpCampo;
    }

    public void Recalcula() {
        if (!executando) {
            if (jVencimentos.getItemCount() > 0) {
                try {
                    MontaTela(jVencimentos.getSelectedItem().toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                CalcularRecibo(jVencimentos.getSelectedItem().toString());

                //
                int i = rcampo.split(";").length;
                Collections gVar = VariaveisGlobais.dCliente;

                if (multa > 0) MontaCampos2(gVar.get("MU"), multa, ++i);
                if (juros > 0) MontaCampos2(gVar.get("JU"), juros, ++i);
                if (correcao > 0) MontaCampos2(gVar.get("CO"), correcao, ++i);
                if (exp > 0) MontaCampos2(gVar.get("EP"), exp, ++i);

                float totRecibos = Totaliza();
                jTotalRecibos.setValue(totRecibos);

                try {
                    String[][] dados = conn.LerCamposTabela(new String[] {"nnumero"}, "RECIBO", 
                            "contrato = '" + jContrato.getSelectedItem().toString().trim() + "' AND tag != 'X' AND dtvencimento = '" + Dates.StringtoString(jVencimentos.getSelectedItem().toString().trim(),"dd-MM-yyyy","yyyy-MM-dd") + "'");
                    jNNumero.setText(dados[0][3]);
                } catch (Exception e) {}
            } else {
                LimpaTela();
            }
        }
    }

    public float Totaliza() {
        int j = 0; int k = jVencimentos.getItemCount() - 1;
        float tRecibo = 0;

        for (j=0;j<=k;j++) {
            Calculos rc = new Calculos();
            try {
                rc.Inicializa(this.rgprp, this.rgimv, this.contrato);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            String vecto = jVencimentos.getItemAt(j).toString();
            String campo = "";
            String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
            ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
            try {
                if (pResult.first()) {
                    campo = pResult.getString("campo");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            DbMain.FecharTabela(pResult);

            float texp = 0;
            float tmulta = 0;
            float tjuros = 0;
            float tcorrecao = 0;

            try {
                texp = rc.TaxaExp(campo);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            try {
                tmulta = rc.Multa(campo, vecto, false);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            try {
                tjuros = rc.Juros(campo, vecto);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            try {
                tcorrecao = rc.Correcao(campo, vecto);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            float stRecibo = 0;
            stRecibo = Calculos.RetValorCampos(campo);
            tRecibo += LerValor.StringToFloat(LerValor.floatToCurrency(stRecibo + texp + tmulta + tjuros + tcorrecao,2));

        }

        return tRecibo;
    }

    public void MontaTela(String vecto) throws SQLException, ParseException {
        if ("".equals(vecto.trim())) { return; }

        // Limpa campos
        jctCampos.removeAll();
        jctCampos.repaint();

        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);

        if (pResult.first()) {
            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
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

            // Monta campos
            int i = 0;
            for (i=0; i<= a.length() - 1; i++) {
                String[] Campo = a.Depurar(i);
                if (Campo.length > 0) {
                    MontaCampos(Campo, i);
                }
            }
        }

        DbMain.FecharTabela(pResult);
    }

    private void MontaCampos(String[] aCampos, int i) {
        UIDefaults defaults = javax.swing.UIManager.getDefaults();

        int at = 20; int llg = 180 - (VariaveisGlobais.bShowCotaParcela ? 50 : 0); int ltf = 120; int lcp = 60; int lcc = 278;
        int top = 5; int left = 5;

        JLabel lb = new JLabel();
        lb.setText(aCampos[0]);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(defaults.getColor("Label.Foreground"));
        lb.setBounds(0 + left, 0 + (at * i) + top, llg, at);
        lb.setName("Label" + i);
        jctCampos.add(lb);

        JFormattedTextField cp = null;
        if (VariaveisGlobais.bShowCotaParcela) {
            cp = new JFormattedTextField();
            try {
                cp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter((!"C".equals(aCampos[5]) ? "##/####" : "##/##"))));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            cp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            cp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            cp.setText(("".equals(aCampos[3]) ? "00/00" + (!"C".equals(aCampos[5]) ? "00" : "") : aCampos[3]));
            cp.setVisible(true);
            cp.setForeground(defaults.getColor("FormattedTextField.Foreground")); //new Color(0,128,0));
            cp.setDisabledTextColor(new Color(0,128,0));
            cp.setBounds(lb.getX() + llg, 0 + (at * i) + top, lcp, at);
            cp.setName("Cota" + i);
            cp.setEditable(false);
            jctCampos.add(cp);
        }

        JFormattedTextField tf = new JFormattedTextField();
        tf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        tf.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        tf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tf.setText(aCampos[1]);
        tf.setVisible(true);
        tf.setDisabledTextColor(defaults.getColor("FormattedTextField.Foreground")); //new Color(0,128,0));
        tf.setBounds((VariaveisGlobais.bShowCotaParcela ? cp.getX() + lcp : lb.getX() + llg), 0 + (at * i) + top, ltf, at);
        tf.setName("Field" + i);
        tf.setEditable(false);
        jctCampos.add(tf);

        jctCampos.repaint();
    }

    private void MontaCampos2(String Label, float Valor, int i) {
        UIDefaults defaults = javax.swing.UIManager.getDefaults();

        int at = 20; int llg = 180 + (VariaveisGlobais.bShowCotaParcela ? 10 : 0); int ltf = 120; int lcp = 60; int lcc = 278;
        int top = 5; int left = 5;

        JLabel lb = new JLabel();
        lb.setText(Label);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(defaults.getColor("Label.Foreground")); //Color.BLACK);
        lb.setBounds(0 + left, 0 + (at * i) + top, llg, at);
        lb.setName("Label" + i);
        jctCampos.add(lb);

        JFormattedTextField tf = new JFormattedTextField();
        tf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        tf.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        tf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tf.setValue(Valor);
        tf.setVisible(true);
        tf.setDisabledTextColor(defaults.getColor("FormattedTextField.Foreground")); //new Color(0,128,0));
        tf.setBounds(lb.getX() + llg, 0 + (at * i) + top, ltf, at);
        tf.setName("Field" + i);
        tf.setEditable(false);
        jctCampos.add(tf);

        jctCampos.repaint();
    }

    private void ListaCamposRecibo(PrinterPOS printer) {
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

        int i = 0; int maxcpos = jctCampos.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = ""; int newLine = 0;
        String posAnt = "";
        for (i=0; i <= maxcpos - 1; i++) {
            if (jctCampos.getComponent(i) instanceof JLabel) {
                saida = ((JLabel) jctCampos.getComponent(i)).getText();
                nmCampo = ((JLabel) jctCampos.getComponent(i)).getName();
                newLine = 0;

                String alinha = "";
                if (!VariaveisGlobais.bShowCotaParcela) {
                    alinha = new Pad(saida, 25).RPad();
                } else {
                    alinha = new Pad(saida, 20).RPad();
                }
                saida = alinha;
                posAnt = "1";
            } else if (jctCampos.getComponent(i) instanceof JFormattedTextField) {
                saida = ((JFormattedTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JFormattedTextField) jctCampos.getComponent(i)).getName();
                newLine = 1;

                String alinha = "";
                if (VariaveisGlobais.bShowCotaParcela) {
                    if (nmCampo.toLowerCase().trim().indexOf("cota") > -1) {
                        alinha = new Pad(("00/00".equals(saida.trim()) || "00/0000".equals(saida.trim()) ? "       " : saida), 7).LPad();
                        newLine = 0;
                        posAnt = "2";
                    } else {
                        if ("2".equals(posAnt)) {
                            alinha = new Pad(saida, 29).LPad();
                        } else {
                            alinha = new Pad(saida, 29 + 7).LPad();
                        }
                        newLine = 1;
                    }
                    saida = alinha;
                } else {
                    alinha = new Pad(saida, 31).LPad();
                    saida = alinha;
                }
            } else if (jctCampos.getComponent(i) instanceof JTextField) {
                saida = ((JTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JTextField) jctCampos.getComponent(i)).getName();
                newLine = 0;
            } else if (jctCampos.getComponent(i) instanceof JCheckBox) {
                boolean simnao = ((JCheckBox) jctCampos.getComponent(i)).isSelected();
                saida = "";
                nmCampo = ((JCheckBox) jctCampos.getComponent(i)).getName();
                newLine = 0;
            } else { saida = ""; newLine = 0; }

            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + saida, newLine);
        }
    }

    public String[][] Recalcula(String rgprp, String rgimv, String contrato, String vencimento) {
        String[][] linhas = null;
        try {
            linhas = MontaTela2(rgprp, rgimv, contrato, vencimento);
        } catch (Exception ex) {} 

        return linhas;
    }
    
    public String[][] MontaTela2(String rgprp, String rgimv, String contrato, String vecto) throws SQLException, bsh.ParseException {

        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);

        String[][] linhas = null;
        if (pResult.first()) {
            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
            VariaveisGlobais.ccampos = pResult.getString("campo");
            linhas = new String[14][3];

            a.SplitCampos();
            // Ordena Matriz
            Arrays.sort(a.aCampos, new Comparator()
            {
            private int pos1 = 3;
            private int pos2 = 4;
            @Override
            public int compare(Object o1, Object o2) {
                String p1 = ((String)o1).substring(pos1, pos2);
                String p2 = ((String)o2).substring(pos1, pos2);
                return p1.compareTo(p2);
            }
            });

            // Monta campos
            for (int i=0; i<= a.length() - 1; i++) {
                String[] Campo = a.Depurar(i);
                if (Campo.length > 0) {
                    //MontaCampos(Campo, i);
                    linhas[i][0] = Campo[0];
                    linhas[i][1] = ("00/00".equals(Campo[3]) || "00/0000".equals(Campo[3]) || "".equals(Campo[3]) ? "-" : Campo[3]) ;
                    linhas[i][2] = Campo[1];
                }
            }
        }

        DbMain.FecharTabela(pResult);

        return linhas;
    }

    private float[] CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto) {
        if ("".equals(vecto.trim())) { return null; }

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}

        String campo = ""; String rcampo = ""; boolean mCartVazio = false;
        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
        }
        DbMain.FecharTabela(pResult);

        float exp = 0, multa = 0, juros = 0, correcao = 0;

        try {
            exp = rc.TaxaExp(campo);
        } catch (SQLException ex) {}

        try {
            multa = rc.Multa(campo, vecto, false);
        } catch (SQLException ex) {}

        try {
            juros = rc.Juros(campo, vecto);
        } catch (SQLException ex) {}

        try {
            correcao = rc.Correcao(campo, vecto);
        } catch (SQLException ex) {}

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        tRecibo += exp + multa + juros + correcao;

        float[] retorno = new float[5];
        retorno[0] = exp; retorno[1] = multa; retorno[2] = juros; retorno[3] = correcao; retorno[4] = tRecibo;
        return retorno;
    }
    
    private Boleta CreateBoleta(String rgprp, String rgimv, String contrato, String vencto, String rnnumero, String dataBol) throws SQLException {
        bancos.LerBanco(contrato);

        Collections gVar = VariaveisGlobais.dCliente;

        Boleta bean1 = new Boleta();
        bean1.setempNome(gVar.get("empresa").toUpperCase().trim());
        bean1.setempEndL1(gVar.get("endereco") + ", " + gVar.get("numero") + gVar.get("complemento") + " - " + gVar.get("bairro"));
        bean1.setempEndL2(gVar.get("cidade") + " - " + gVar.get("estado") + " - CEP " + gVar.get("cep"));
        bean1.setempEndL3("Tel/Fax.: " + gVar.get("telefone"));
        bean1.setempEndL4(gVar.get("hpage") + " / " + gVar.get("email"));

        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/boleta/" + VariaveisGlobais.icoBoleta);

        // 28-02-2018 - Reimpressão com nova data de boleta
        if (dataBol != null) {
            bean1.setlocaMsgL02("Calculos feitos em referencia a data original de Vencimento " + vencto);
        }

        String[][] linhas = Recalcula(rgprp, rgimv, contrato, vencto);
        float[] totais = CalcularRecibo(rgprp, rgimv, contrato, vencto);

        // exp, mul, jur, cor
        //float expediente = totais[0], multa = totais[1], juros = totais[2], correcao = totais[3];
        float expediente = 0, multa = 0, juros = 0, correcao = 0;
        
        if (VariaveisGlobais.boletoEP || VariaveisGlobais.boletoSomaEP) expediente = totais[0];
        if (VariaveisGlobais.boletoMU) { multa = totais[1]; } else { totais[4] -= totais[1]; }
        if (VariaveisGlobais.boletoJU) { juros = totais[2]; } else { totais[4] -= totais[2]; }
        if (VariaveisGlobais.boletoCO) { correcao = totais[3]; } else { totais[4] -= totais[3]; }
        float tRecibo = totais[4];
        
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.format(multa);

        if ((VariaveisGlobais.boletoEP && expediente > 0) && !VariaveisGlobais.boletoSomaEP) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("EP");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(expediente);
            }
        } else if (VariaveisGlobais.boletoEP && VariaveisGlobais.boletoSomaEP) {
            float alrec = LerValor.StringToFloat(linhas[0][2]);
            linhas[0][2] = LerValor.floatToCurrency(alrec + expediente, 2);
            expediente = 0;
        } else if (!VariaveisGlobais.boletoEP && !VariaveisGlobais.boletoSomaEP) {
            tRecibo -= totais[0];
            expediente = 0;
        }

        if (multa > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("MU");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(multa);
            }
        }

        if (juros > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("JU");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(juros);
            }
        }

        if (correcao > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("CO");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(correcao);
            }
        }

        try {
            bean1.setlocaDescL01(linhas[0][0]);
            bean1.setlocaCpL01(linhas[0][1]);
            bean1.setlocaVrL01(linhas[0][2]);
            bean1.setlocaDescL02(linhas[1][0]); 
            bean1.setlocaCpL02(linhas[1][1]);
            bean1.setlocaVrL02(linhas[1][2]);
            bean1.setlocaDescL03(linhas[2][0]);
            bean1.setlocaCpL03(linhas[2][1]);
            bean1.setlocaVrL03(linhas[2][2]);
            bean1.setlocaDescL04(linhas[3][0]);
            bean1.setlocaCpL04(linhas[3][1]);
            bean1.setlocaVrL04(linhas[3][2]);
            bean1.setlocaDescL05(linhas[4][0]);
            bean1.setlocaCpL05(linhas[4][1]);
            bean1.setlocaVrL05(linhas[4][2]);
            bean1.setlocaDescL06(linhas[5][0]);
            bean1.setlocaCpL06(linhas[5][1]);
            bean1.setlocaVrL06(linhas[5][2]);
            bean1.setlocaDescL07(linhas[6][0]);
            bean1.setlocaCpL07(linhas[6][1]);
            bean1.setlocaVrL07(linhas[6][2]);
            bean1.setlocaDescL08(linhas[7][0]);
            bean1.setlocaCpL08(linhas[7][1]);
            bean1.setlocaVrL08(linhas[7][2]);
            bean1.setlocaDescL09(linhas[8][0]);
            bean1.setlocaCpL09(linhas[8][1]);
            bean1.setlocaVrL09(linhas[8][2]);
            bean1.setlocaDescL10(linhas[9][0]);
            bean1.setlocaCpL10(linhas[9][1]);
            bean1.setlocaVrL10(linhas[9][2]);
        } catch (Exception ex) {}

        // 13-11-2017 - Reimpressão com outro vencimento
        if (dataBol != null) {
            bean1.setbolDadosVencimento(dataBol);
        } else {
            bean1.setbolDadosVencimento(vencto);
        }

        String cValor = bancos.Valor4Boleta(LerValor.floatToCurrency(tRecibo,2));  // valor da boleta
        bean1.setbolDadosVrdoc(df.format(tRecibo));

        String banco = (bancos.getBanco().equalsIgnoreCase("104") ? "cef" : (bancos.getBanco().equalsIgnoreCase("341") ? "itau" : "santander"));
        String nNumero = "";
        
//        nNumero = bancos.getNnumero().trim(); //conn.LerParametros(banco.toUpperCase()).trim();
        
        String showNossoNumero = ""; String showCarteira = "";
//        if (banco.equalsIgnoreCase("itau")) {
//            nNumero =  bancos.NossoNumeroItau(nNumero, 9);                    
//            showNossoNumero = bancos.getCarteira() + "/" + nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
//            showCarteira = bancos.getCarteira();
//        } else if (banco.equalsIgnoreCase("cef")) {
//            nNumero =  bancos.NossoNumero(nNumero, 16);                                        
//            showNossoNumero = bancos.getCarteira() +  nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
//            showCarteira = "SR";
//        } else if (banco.equalsIgnoreCase("santander")) {
//            nNumero =  bancos.NossoNumero(nNumero, 13);
//            showNossoNumero = nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
//            showCarteira = bancos.getCarteira();
//        }

        bean1.setbolDadosNnumero(rnnumero); //showNossoNumero);
        bean1.setbolDadosNumdoc(rgprp + "/" + contrato);

        String[][] dadosSac = conn.LerCamposTabela(new String[] {"nomerazao", "cpfcnpj"}, "locatarios", "contrato = '" + contrato + "'");

        bean1.setsacDadosNome(dadosSac[0][3]);  // Nome do Sacado
        bean1.setsacDadosCpfcnpj("CNPJ/CPF: " + dadosSac[1][3]);  // Cpf ou Cnpj do Sacado

        String[][] endSac = conn.LerCamposTabela(new String[] {"end", "num", "compl", "bairro", "cidade", "estado", "cep"}, "imoveis", "rgimv = '" + rgimv + "'");
        bean1.setsacDadosEndereco(endSac[0][3]);
        bean1.setsacDadosNumero(endSac[1][3]);
        bean1.setsacDadosCompl(endSac[2][3]);
        bean1.setsacDadosBairro(endSac[3][3]);
        bean1.setsacDadosCidade(endSac[4][3]);
        bean1.setsacDadosEstado(endSac[5][3]);
        bean1.setsacDadosCep(endSac[6][3]);

        if (banco.toLowerCase().equalsIgnoreCase("itau")) {
            bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
            bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NA IMOBILIARIA.");
            bean1.setbolDadosEspeciedoc("RC");
        } else if (banco.toLowerCase().equalsIgnoreCase("cef")) {
            bean1.setbcoMsgL01("PAGAR PREFERENCIALMENTE NAS CASAS LOTERICAS ATE O VALOR LIMITE");
            bean1.setbcoMsgL02("");
            bean1.setbolDadosEspeciedoc("RC");
        } else if (banco.toLowerCase().equalsIgnoreCase("santander")) {
            bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
            bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NA IMOBILIARIA.");
            bean1.setbolDadosEspeciedoc("DM");
        }

        bean1.setbolDadosCedente(gVar.get("empresa").toUpperCase() + "   - CNPJ: " + gVar.get("cnpj"));
        bean1.setbolDadosDatadoc(Dates.DatetoString(new Date()));
        bean1.setbolDadosAceite("N");
        bean1.setbolDadosDtproc(Dates.DatetoString(new Date()));
        bean1.setbolDadosUsobco("");
        bean1.setbolDadosCarteira(showCarteira);
        bean1.setbolDadosEspecie("R$");
        bean1.setbolDadosQtde("");
        bean1.setbolDadosValor("");
 
        String msgBol01 = conn.LerParametros("MSGBOL1"); if (msgBol01 == null) msgBol01 = "";
        String msgBol02 = conn.LerParametros("MSGBOL2"); if (msgBol02 == null) msgBol02 = "";
        String msgBol03 = conn.LerParametros("MSGBOL3"); if (msgBol03 == null) msgBol03 = "";
        String msgBol04 = conn.LerParametros("MSGBOL4"); if (msgBol04 == null) msgBol04 = "";
        String msgBol05 = conn.LerParametros("MSGBOL5"); if (msgBol05 == null) msgBol05 = "";
        String msgBol06 = conn.LerParametros("MSGBOL6"); if (msgBol06 == null) msgBol06 = "";
        String msgBol07 = conn.LerParametros("MSGBOL7"); if (msgBol07 == null) msgBol07 = "";
        String msgBol08 = conn.LerParametros("MSGBOL8"); if (msgBol08 == null) msgBol08 = "";
        String msgBol09 = conn.LerParametros("MSGBOL9"); if (msgBol09 == null) msgBol09 = "";

        bean1.setbolDadosMsg01(msgBol01);
        bean1.setbolDadosMsg02(msgBol02);
        bean1.setbolDadosMsg03(msgBol03);
        bean1.setbolDadosMsg04(msgBol04);
        bean1.setbolDadosMsg05(msgBol05);
        bean1.setbolDadosMsg06(msgBol06);
        bean1.setbolDadosMsg07(msgBol07);

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}
        Date tvecto = Dates.StringtoDate(dataBol != null ? dataBol : vencto,"dd/MM/yyyy");
        String carVecto = Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, (int)rc.dia_mul, tvecto));

        String ln08 = "";
        if ("".equals(msgBol08)) {
            ln08 = "APÓS O DIA " + carVecto + " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO.";
        } else {
            // [VENCIMENTO] - Mostra Vencimento
            // [CARENCIA] - Mostra Vencimento + Carencia
            // [MULTA] - Mostra Juros
            // [ENCARGOS] - Mostra Encargos
            ln08 = msgBol08.replace("[VENCIMENTO]", Dates.DateFormata("dd/MM/yyyy", tvecto));
            ln08 = ln08.replace("[CARENCIA]", carVecto);
            ln08 = ln08.replace("[MULTA]", String.valueOf(rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", "") + "%");
            ln08 = ln08.replace("[ENCARGOS]", "0,333%");
        }
        bean1.setbolDadosMsg08(ln08);
        //bean1.setbolDadosMsg08("".equals(msgBol08) ? "APÓS O DIA " + carVecto + 
        //    " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO." : "" + msgBol08);
        
        bean1.setbolDadosMsg09("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);

        return bean1;

    }
   
    private int AchaVazio(String[][] value) {
        int r = -1;

        for (int i=0;i<value.length;i++) {
            if (value[i][0] == null || "".equals(value[i][0])) {r = i; break;}
        }

        return r;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable atable;
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btGerar;
    private javax.swing.JButton btLancar;
    private javax.swing.JButton btnAuto;
    private javax.swing.JLabel jBairro;
    private javax.swing.JLabel jCep;
    private javax.swing.JLabel jCidade;
    private javax.swing.JComboBox jContrato;
    private javax.swing.JLabel jEndereco;
    private javax.swing.JLabel jEstado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField jNNumero;
    private javax.swing.JComboBox jNomeLoca;
    private javax.swing.JLabel jNomeProp;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jRgimv;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField jTotBoletas;
    private javax.swing.JFormattedTextField jTotalRecibos;
    private javax.swing.JComboBox jVencimentos;
    private javax.swing.JFormattedTextField jVrRecibo;
    private javax.swing.JComboBox jbanco;
    private javax.swing.JToggleButton jbtEnvios;
    private javax.swing.JPanel jctCampos;
    private javax.swing.JTextField jnomeArq;
    private javax.swing.JSpinner jnrlote;
    private javax.swing.JComboBox jtparq;
    // End of variables declaration//GEN-END:variables

}