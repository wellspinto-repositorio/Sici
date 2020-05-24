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

import Funcoes.*;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import Sici.Partida.Collections;
import Sici.Partida.jAutoriza;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
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
public class jAltRecibos extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String rgprp = ""; String rgimv = ""; String contrato = ""; String rcampo = "";
    boolean executando = false; boolean mCartVazio = false;
    boolean bMulta = false; boolean bJuros = false; boolean bCorrecao = false; boolean bTaxa = false;
    boolean bExecNome = false, bExecCodigo = false;
    // campos de calculos
    float exp = 0;
    float multa = 0;
    float juros = 0;
    float correcao = 0;

    /** Creates new form jAltRecibos */
    public jAltRecibos() {
        initComponents();

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        Ajusta();
        FillCombos();
        AutoCompletion.enable(jContrato);
        AutoCompletion.enable(jNomeLoca);

        // ajustes 11/12/2011
        jInsDesconto.setVisible(false);
        jInsSeguro.setVisible(false);
        jInsDiferenca.setVisible(false);
                
        ComboBoxEditor edit = jNomeLoca.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                String[][] vCampos = null;
                try {
                    vCampos = conn.LerCamposTabela(new String[] {"l.contrato", "l.rgprp", "l.rgimv", "l.aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome"}, "locatarios l, imoveis i, proprietarios p", "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp) AND l.contrato = '" + jContrato.getSelectedItem().toString() + "'");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                rgprp = vCampos[1][3];
                rgimv = vCampos[2][3];
                contrato = vCampos[0][3];

                SetaCampos(vCampos);
                try {
                    executando = true;
                    ListaVectos();
                    executando = false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                
                Recalcula();
                
                jbtLiberar.setEnabled(true);
                jbtAlterar.setEnabled(true);
                jbtExcluir.setEnabled(rcampo.indexOf("AD") == -1);
                
            }
            
            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
            }
        });

        ComboBoxEditor editContrato = jContrato.getEditor();
        Component compContrato = editContrato.getEditorComponent();
        compContrato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
            }
        });
    }

    private String IPTU(String vecto, String campo) {
        String pIptu = ""; Integer pant = 0;
        try { pIptu = conn.LerParametros("IPTU"); } catch (Exception e) {}
        try { pant = Integer.valueOf(conn.LerParametros("IPTUANT"));  } catch (Exception e) {}
        if (pIptu == null || pIptu.equalsIgnoreCase("")) return campo;
        
        Boolean eiptu = campo.contains(pIptu + ":");
        if (eiptu) return campo;
        
        String[] meses = {"","jan","fev","mar","abr","mai","jun","jul","ago","set","out","nov","dez"};
        String ddia = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(0, 2);
        String dmes = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(3, 5);
        String dano  = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(6, 10);
        String umes = Dates.DateFormata("dd-MM-yyyy", new Date()).substring(3, 5);
        String uano = Dates.DateFormata("dd-MM-yyyy", new Date()).substring(6, 10);
        
        String wmes = ""; String wValor = "0000000000";
        
        String wSql = "SELECT p.* FROM iptu p, imoveis i WHERE InStr(i.matriculas,p.matricula) > 0 AND p.ano = '" + dano + "' AND i.rgimv = '" + this.rgimv + "';";
        ResultSet ws = conn.AbrirTabela(wSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (ws.next()) {
                wmes = ws.getString(meses[Integer.valueOf(dmes)]);
                String[] avar = wmes.split(";");
                if (avar.length > 0) {
                    for (int i=0;i<avar.length;i++) {
                        String[] rvar = avar[i].split(",");
                        if (!rvar[0].trim().equalsIgnoreCase("")) {
                            if (Dates.DateDiff(Dates.DIA, new Date(), Dates.DateAdd(Dates.DIA, (pant * -1), Dates.StringtoDate(rvar[0], "dd-MM-yyyy"))) > 0) {
                                wValor = rvar[1];
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return campo + ";" + (!wValor.equalsIgnoreCase("0000000000") ? pIptu + ":2:" + wValor + ":0000:NT:RZ:ET:IP" : "");
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
                
                // Aqui recebe o IPTU
                campo = IPTU(vecto, campo);
                
                // elimina problens
                campo = LimpaCalculos(campo);
                
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

        jMulta.setValue(multa);
        jCorrecao.setValue(correcao);
        jJuros.setValue(juros);
        jTaxa.setValue(exp);

        jcbMulta.setSelected(Calculos.RetPar(campo, "MU"));
        jcbCorrecao.setSelected(Calculos.RetPar(campo, "CO"));
        jcbJuros.setSelected(Calculos.RetPar(campo, "JU"));
        jcbTaxa.setSelected(Calculos.RetPar(campo, "EP"));
        
        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        jVrRecibo.setValue(tRecibo + exp + multa + juros + correcao);
        
        bMulta = Calculos.RetPar(campo, "MU");
        bCorrecao = Calculos.RetPar(campo, "CO");
        bJuros = Calculos.RetPar(campo, "JU");
        bTaxa = Calculos.RetPar(campo, "EP");
        
        jbtExcluir.setEnabled(rcampo.indexOf("AD") == -1);
    }

    private void ReCalcularRecibo(String vecto, String qual) {
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
                
                // Aqui IPTU
                campo = IPTU(vecto, campo);
                
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
            ex.printStackTrace();
        }
        DbMain.FecharTabela(pResult);

        if ("MU".equals(qual)) {
            try {
                multa = rc.Multa(campo, vecto, false);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            jMulta.setValue(multa);        
        } else if ("CO".equals(qual)) {
            try {
                correcao = rc.Correcao(campo, vecto);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            jCorrecao.setValue(correcao);
        } else if ("JU".equals(qual)) {
            try {
                juros = rc.Juros(campo, vecto);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            jJuros.setValue(juros);
        } else if ("EP".equals(qual)) {
            try {
                exp = rc.TaxaExp(campo);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            jTaxa.setValue(exp);
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
        jObs.setText(vCampos[3][3]);
    }

    private void LimpaTela() {
        jRgimv.setText("");
        jNomeProp.setText("");
        jEndereco.setText("");
        jBairro.setText("");
        jCidade.setText("");
        jEstado.setText("");
        jCep.setText("");
        jObs.setText("");        
        
        jctCampos.removeAll();
        jctCampos.repaint();
        
        jMulta.setValue(0);
        jCorrecao.setValue(0);
        jJuros.setValue(0);
        jTaxa.setValue(0);

        jcbMulta.setSelected(false);
        jcbCorrecao.setSelected(false);
        jcbJuros.setSelected(false);
        jcbTaxa.setSelected(false);
        
        Liberacoes(false);
        
        executando = true;
        jVencimentos.removeAllItems();
        executando = false;
        
        jVrRecibo.setValue(0);
        
        jbtLiberar.setEnabled(false);
        jbtAlterar.setEnabled(false);
        jbtExcluir.setEnabled(false);
    }
    
    private void ListaVectos() throws SQLException {
        String sql = "SELECT dtvencimento FROM RECIBO WHERE tag <> 'X' AND contrato = '" + contrato + "' ORDER BY dtvencimento;";
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

    private void Ajusta() {
        Collections gVar = VariaveisGlobais.dCliente;

        jlblMulta.setText(gVar.get("MU"));
        jlblJuros.setText(gVar.get("JU"));
        jlblCorrecao.setText(gVar.get("CO"));
        jlblTaxa.setText(gVar.get("EP"));

        Liberacoes(false);
        jContrato.requestFocus();
    }

    private void Liberacoes(boolean libera) {
        jLiberacoes.setEnabled(libera);
        if (libera) { jLiberacoes.setBackground(new Color(191,255,191)); } else { jLiberacoes.setBackground(this.getBackground()); }
        
        jMulta.setEnabled(false);
        jJuros.setEnabled(false);
        jCorrecao.setEnabled(false);
        jTaxa.setEnabled(false);

        Color cor_enable = new Color(0,128,0); Color cor_disable = Color.RED;
        jMulta.setDisabledTextColor(cor_disable);
        jMulta.setForeground(cor_enable);
        jJuros.setDisabledTextColor(cor_disable);
        jJuros.setForeground(cor_enable);
        jCorrecao.setDisabledTextColor(cor_disable);
        jCorrecao.setForeground(cor_enable);
        jTaxa.setDisabledTextColor(cor_disable);
        jTaxa.setForeground(cor_enable);

        jcbMulta.setEnabled(libera);
        jcbJuros.setEnabled(libera);
        jcbCorrecao.setEnabled(libera);
        jcbTaxa.setEnabled(libera);
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
        jLabel17 = new javax.swing.JLabel();
        jObs = new javax.swing.JLabel();
        jLiberacoes = new javax.swing.JPanel();
        jlblMulta = new javax.swing.JLabel();
        jlblCorrecao = new javax.swing.JLabel();
        jlblJuros = new javax.swing.JLabel();
        jlblTaxa = new javax.swing.JLabel();
        jMulta = new javax.swing.JFormattedTextField();
        jCorrecao = new javax.swing.JFormattedTextField();
        jJuros = new javax.swing.JFormattedTextField();
        jTaxa = new javax.swing.JFormattedTextField();
        jcbCorrecao = new javax.swing.JCheckBox();
        jcbMulta = new javax.swing.JCheckBox();
        jcbJuros = new javax.swing.JCheckBox();
        jcbTaxa = new javax.swing.JCheckBox();
        jbtLiberar = new javax.swing.JButton();
        jbtAlterar = new javax.swing.JButton();
        jbtExcluir = new javax.swing.JButton();
        jScroll = new javax.swing.JScrollPane();
        jctCampos = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jVencimentos = new javax.swing.JComboBox();
        jVrRecibo = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jInsSeguro = new javax.swing.JButton();
        jInsDesconto = new javax.swing.JButton();
        jInsDiferenca = new javax.swing.JButton();
        jbtApagarNNumero = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Alteração de Recibos ::.");
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
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

        jContrato.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jContratoActionPerformed(evt);
            }
        });

        jNomeLoca.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jNomeLoca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomeLocaActionPerformed(evt);
            }
        });

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setText("RgImv:");
        jLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jRgimv.setBackground(new java.awt.Color(254, 254, 254));
        jRgimv.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jRgimv.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jRgimv.setOpaque(true);

        jLabel5.setText("Proprietário:");
        jLabel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jNomeProp.setBackground(new java.awt.Color(254, 254, 254));
        jNomeProp.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jNomeProp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jNomeProp.setOpaque(true);

        jLabel7.setText("End.:");
        jLabel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jEndereco.setBackground(new java.awt.Color(254, 254, 254));
        jEndereco.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jEndereco.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jEndereco.setOpaque(true);

        jLabel9.setText("Bairro:");
        jLabel9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jBairro.setBackground(new java.awt.Color(254, 254, 254));
        jBairro.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jBairro.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jBairro.setOpaque(true);

        jCidade.setBackground(new java.awt.Color(254, 254, 254));
        jCidade.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jCidade.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jCidade.setOpaque(true);

        jLabel12.setText("Cidade:");
        jLabel12.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jEstado.setBackground(new java.awt.Color(254, 254, 254));
        jEstado.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jEstado.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jEstado.setOpaque(true);

        jLabel14.setText("Estado:");
        jLabel14.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jCep.setBackground(new java.awt.Color(254, 254, 254));
        jCep.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jCep.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jCep.setOpaque(true);

        jLabel16.setText("Cep:");
        jLabel16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCidade, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                            .addComponent(jEndereco, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCep, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBairro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jRgimv, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCep, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jCep, jCidade, jEstado, jLabel12, jLabel14, jLabel16});

        jLabel17.setBackground(new java.awt.Color(191, 191, 191));
        jLabel17.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel17.setText("Comunicado Importânte");
        jLabel17.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel17.setOpaque(true);

        jObs.setBackground(new java.awt.Color(254, 254, 254));
        jObs.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jObs.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jObs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jContrato, 0, 116, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 711, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jObs, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLiberacoes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jLiberacoes.setEnabled(false);

        jlblMulta.setBackground(new java.awt.Color(153, 153, 153));
        jlblMulta.setText("Multa:");
        jlblMulta.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jlblMulta.setOpaque(true);

        jlblCorrecao.setBackground(new java.awt.Color(153, 153, 153));
        jlblCorrecao.setText("Correção:");
        jlblCorrecao.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jlblCorrecao.setOpaque(true);

        jlblJuros.setBackground(new java.awt.Color(153, 153, 153));
        jlblJuros.setText("Juros:");
        jlblJuros.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jlblJuros.setOpaque(true);

        jlblTaxa.setBackground(new java.awt.Color(153, 153, 153));
        jlblTaxa.setText("Taxa:");
        jlblTaxa.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jlblTaxa.setOpaque(true);

        jMulta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jMulta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jMulta.setText("0,00");
        jMulta.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jMulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMultaActionPerformed(evt);
            }
        });

        jCorrecao.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jCorrecao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jCorrecao.setText("0,00");
        jCorrecao.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jJuros.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jJuros.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jJuros.setText("0,00");
        jJuros.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jTaxa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jTaxa.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTaxa.setText("0,00");
        jTaxa.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jcbCorrecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbCorrecaoActionPerformed(evt);
            }
        });

        jcbMulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbMultaActionPerformed(evt);
            }
        });

        jcbJuros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbJurosActionPerformed(evt);
            }
        });

        jcbTaxa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTaxaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jLiberacoesLayout = new javax.swing.GroupLayout(jLiberacoes);
        jLiberacoes.setLayout(jLiberacoesLayout);
        jLiberacoesLayout.setHorizontalGroup(
            jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLiberacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbMulta)
                    .addComponent(jcbTaxa)
                    .addComponent(jcbJuros)
                    .addComponent(jcbCorrecao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlblJuros, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jlblCorrecao, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jlblTaxa, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jlblMulta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jJuros, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(jTaxa, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(jCorrecao, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(jMulta, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                .addContainerGap())
        );
        jLiberacoesLayout.setVerticalGroup(
            jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLiberacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbMulta)
                    .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlblMulta)
                        .addComponent(jMulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbCorrecao)
                    .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlblCorrecao)
                        .addComponent(jCorrecao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbJuros)
                    .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlblJuros)
                        .addComponent(jJuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbTaxa)
                    .addGroup(jLiberacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlblTaxa)
                        .addComponent(jTaxa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jbtLiberar.setText("Liberar");
        jbtLiberar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtLiberarActionPerformed(evt);
            }
        });

        jbtAlterar.setText("Alterar");
        jbtAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtAlterarActionPerformed(evt);
            }
        });

        jbtExcluir.setText("Excluir Recibo");
        jbtExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtExcluirActionPerformed(evt);
            }
        });

        jScroll.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Campos"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScroll.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        jctCampos.setEnabled(false);
        jctCampos.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        javax.swing.GroupLayout jctCamposLayout = new javax.swing.GroupLayout(jctCampos);
        jctCampos.setLayout(jctCamposLayout);
        jctCamposLayout.setHorizontalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );
        jctCamposLayout.setVerticalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 342, Short.MAX_VALUE)
        );

        jScroll.setViewportView(jctCampos);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jVencimentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentosActionPerformed(evt);
            }
        });

        jVrRecibo.setEditable(false);
        jVrRecibo.setForeground(new java.awt.Color(255, 51, 51));
        jVrRecibo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jVrRecibo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jVrRecibo.setText("0,00");
        jVrRecibo.setDisabledTextColor(new java.awt.Color(255, 51, 51));
        jVrRecibo.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Venctos:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Vr.Recibo:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jVencimentos, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jVrRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jVencimentos, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jVrRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jInsSeguro.setText("SG");
        jInsSeguro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInsSeguroActionPerformed(evt);
            }
        });

        jInsDesconto.setText("DC");
        jInsDesconto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInsDescontoActionPerformed(evt);
            }
        });

        jInsDiferenca.setText("DF");
        jInsDiferenca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInsDiferencaActionPerformed(evt);
            }
        });

        jbtApagarNNumero.setText("Apagar NNumero");
        jbtApagarNNumero.setEnabled(false);
        jbtApagarNNumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtApagarNNumeroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jbtExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtApagarNNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jInsSeguro)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jInsDesconto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jInsDiferenca)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLiberacoes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jbtLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                .addComponent(jbtAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLiberacoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScroll, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jInsSeguro)
                    .addComponent(jInsDesconto)
                    .addComponent(jInsDiferenca)
                    .addComponent(jbtApagarNNumero))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtLiberarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLiberarActionPerformed
        if (!jLiberacoes.isEnabled()) {
            Liberacoes(true);
            jcbMulta.requestFocus();
            jbtLiberar.setEnabled(true);
            jbtAlterar.setEnabled(false);
            jbtExcluir.setEnabled(false);
        } else {
            AlteraMUJUCOEP();
            Recalcula();
        }
    }//GEN-LAST:event_jbtLiberarActionPerformed

    private void AlteraMUJUCOEP() {
        Liberacoes(false);
        jbtLiberar.setEnabled(true);
        jbtAlterar.setEnabled(true);
        jbtExcluir.setEnabled(rcampo.indexOf("AD") == -1);

        String auxCpo = rcampo;
        
        String vMulta = ""; String vJuros = ""; String vCorrecao = ""; String vTaxa = "";
        
        // Multa
        if (jcbMulta.isSelected()) {
            if (LerValor.StringToFloat(jMulta.getText()) > 0) {
                vMulta = "MU" + FuncoesGlobais.GravaValor(jMulta.getText());
            } else {
                vMulta = "MU";
            }
        } else {
            vMulta = "";
        }
        
        // Juros
        if (jcbJuros.isSelected()) {
            if (LerValor.StringToFloat(jJuros.getText()) > 0) {
                vJuros = "JU" + FuncoesGlobais.GravaValor(jJuros.getText());
            } else {
                vJuros = "JU";
            }
        } else {
            vJuros = "";
        }
        
        // Correção
        if (jcbCorrecao.isSelected()) {
            if (LerValor.StringToFloat(jCorrecao.getText()) > 0) {
                vCorrecao = "CO" + FuncoesGlobais.GravaValor(jCorrecao.getText());
            } else {
                vCorrecao = "CO";
            }
        } else {
            vCorrecao = "";
        }
        
        // Taxa
        if (jcbTaxa.isSelected()) {
            if (LerValor.StringToFloat(jTaxa.getText()) > 0) {
                vTaxa = "EP" + FuncoesGlobais.GravaValor(jTaxa.getText());
            } else {
                vTaxa = "EP";
            }
        } else {
            vTaxa = "";
        }
        
        // Limpar Ocorrencias
        String oldMU = BuscaXX(auxCpo, "MU");
        String oldJU = BuscaXX(auxCpo, "JU");
        String oldCO = BuscaXX(auxCpo, "CO");
        String oldEP = BuscaXX(auxCpo, "EP");
        
        auxCpo = auxCpo.replace(oldMU, "");
        auxCpo = auxCpo.replace(oldJU, "");
        auxCpo = auxCpo.replace(oldCO, "");
        auxCpo = auxCpo.replace(oldEP, "");
        
        // Grava Novas Ocorrencias
        String mujucoep = "";
        if (!"".equals(vMulta)) {
            mujucoep += vMulta + ":";
        }
        if (!"".equals(vJuros)) {
            mujucoep += vJuros + ":";
        }
        if (!"".equals(vCorrecao)) {
            mujucoep += vCorrecao + ":";
        }
        if (!"".equals(vTaxa)) {
            mujucoep += vTaxa + ":";
        }
        
        int pos = auxCpo.indexOf("AL:");
        auxCpo = auxCpo.substring(0, pos + 3) + mujucoep + auxCpo.substring(pos + 3);
        
        // 01-03-2012
        //auxCpo = auxCpo.replace("AL:", "AL:" + mujucoep);
        
        String SQLtxt = "UPDATE RECIBO SET CAMPO = '" + auxCpo + "' WHERE CONTRATO = '" + this.contrato +
                        "' AND DTVENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "';";

        conn.ExecutarComando(SQLtxt);

        this.rcampo = auxCpo;
        bMulta = jcbMulta.isSelected();
        bJuros = jcbJuros.isSelected();
        bCorrecao = jcbCorrecao.isSelected();
        bTaxa = jcbTaxa.isSelected();
}
    
    private String BuscaXX(String zcampo, String oque) {
        String zRet = "";
        int i = zcampo.indexOf(oque);
        if (i >= 0) {
            // Achou
            String auxCpo = zcampo.substring(i + 2);
            if (auxCpo.length() > 0) {
                if (":".equals(auxCpo.substring(0,1))) {
                    zRet = oque + ":";
                } else {
                    zRet = oque + auxCpo.substring(0,10) + ":";
                }
            } else zRet = oque + ":";
        }
        
        return zRet;
    }
    
    private void FillCombos() {
        String sSql = "SELECT contrato as codigo, UPPER(nomerazao) as nome FROM locatarios WHERE fiador1uf <> 'X' OR IsNull(fiador1uf) ORDER BY UPPER(nomerazao)";

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

    private void EnabledFields(boolean enable) {
        int i = 0; int maxcpos = jctCampos.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = "";
        for (i=0; i <= maxcpos - 1; i++) {
            if (jctCampos.getComponent(i) instanceof JLabel) {
                //((JLabel) jctCampos.getComponent(i)).setEnabled(enable);
            } else if (jctCampos.getComponent(i) instanceof JFormattedTextField) {
                ((JFormattedTextField) jctCampos.getComponent(i)).setEnabled(enable);
            } else if (jctCampos.getComponent(i) instanceof JTextField) {
                ((JTextField) jctCampos.getComponent(i)).setEnabled(enable);
            } else if (jctCampos.getComponent(i) instanceof JCheckBox) {
                ((JCheckBox) jctCampos.getComponent(i)).setEnabled(enable);
            }
        }
        jctCampos.setEnabled(enable);
    }

    private void SetFocusFields() {
        int i = 0; int maxcpos = jctCampos.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = "";
        for (i=0; i <= maxcpos - 1; i++) {
            if (jctCampos.getComponent(i) instanceof JLabel) {
                //((JLabel) jctCampos.getComponent(i)).setEnabled(enable);
            } else if (jctCampos.getComponent(i) instanceof JFormattedTextField) {
                ((JFormattedTextField) jctCampos.getComponent(i)).requestFocus();
                break;
            } else if (jctCampos.getComponent(i) instanceof JTextField) {
                //((JTextField) jctCampos.getComponent(i)).setEnabled(enable);
            } else if (jctCampos.getComponent(i) instanceof JCheckBox) {
                //((JCheckBox) jctCampos.getComponent(i)).setEnabled(enable);
            }
        }
    }

    private void jbtAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtAlterarActionPerformed
        if (!jctCampos.isEnabled()) {
            try {
                if (InsereCampos()) {
                    
                    // ajustes 11/12/2011
                    jInsDesconto.setVisible(true);
                    jInsSeguro.setVisible(true);
                    jInsDiferenca.setVisible(true);

                    try {
                        MontaTela(jVencimentos.getSelectedItem().toString());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            EnabledFields(!jctCampos.isEnabled());
            SetFocusFields();
        } else {
            // ajustes 11/12/2011
            jInsDesconto.setVisible(false);
            jInsSeguro.setVisible(false);
            jInsDiferenca.setVisible(false);

            GravarCampos();
            try {
                MontaTela(jVencimentos.getSelectedItem().toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            EnabledFields(!jctCampos.isEnabled());
            Recalcula();
            
            // move scrool para o inicio
            jScroll.getViewport().setViewPosition(new Point(0,0));
        }
        if (jctCampos.isEnabled()) { 
            jctCampos.setBackground(new Color(191,255,191)); 
            jbtLiberar.setEnabled(false);
            jbtAlterar.setEnabled(true);
            jbtExcluir.setEnabled(false);
        } else {
            jctCampos.setBackground(this.getBackground()); 
            jbtLiberar.setEnabled(true);
            jbtAlterar.setEnabled(true);
            jbtExcluir.setEnabled(rcampo.indexOf("AD") == -1);
        }
    }//GEN-LAST:event_jbtAlterarActionPerformed

    private void jbtExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtExcluirActionPerformed
        try {  
            boolean pode = true;
            jAutoriza oAut = new jAutoriza(null, true);
            oAut.setVisible(true);
            pode = oAut.pode;

            if (pode) {
                int resp = JOptionPane.showConfirmDialog(null, "Exclui o recibo deste contrato!!!", "Excluir", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    String Sql = "DELETE FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "';";
                    if (conn.ExecutarComando(Sql) > 0) { 
                        conn.Auditor("EXCLUSAO: RECIBO", contrato + " "+ Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")));
                        jContrato.requestFocus(); 
                    }
                }
            }
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  

    }//GEN-LAST:event_jbtExcluirActionPerformed

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

    private void jcbMultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbMultaActionPerformed
        if (jLiberacoes.isEnabled()) {
            if (jcbMulta.isSelected()) {
                jMulta.setValue(0);
                multa = 0;
                jMulta.setEnabled(true);
                jMulta.requestFocus();

                // Retotaliza
                float tRecibo = 0;
                tRecibo = Calculos.RetValorCampos(rcampo);
                jVrRecibo.setValue(tRecibo + exp + multa + juros + correcao);
            } else {
                ReCalcularRecibo(jVencimentos.getSelectedItem().toString(), "MU");
                jMulta.setEnabled(false);
            }
            
        }
        System.out.println(Calculos.RetCpoPar(rcampo, "MU"));
    }//GEN-LAST:event_jcbMultaActionPerformed

    private void jVencimentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jVencimentosActionPerformed
        Recalcula();
}//GEN-LAST:event_jVencimentosActionPerformed

    private void jcbCorrecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbCorrecaoActionPerformed
        if (jLiberacoes.isEnabled()) {
            if (jcbCorrecao.isSelected()) {
                jCorrecao.setValue(0);
                correcao = 0;
                jCorrecao.setEnabled(true);
                jCorrecao.requestFocus();

                // Retotaliza
                float tRecibo = 0;
                tRecibo = Calculos.RetValorCampos(rcampo);
                jVrRecibo.setValue(tRecibo + exp + multa + juros + correcao);
            } else {
                ReCalcularRecibo(jVencimentos.getSelectedItem().toString(), "CO");
                jCorrecao.setEnabled(false);                
            }

        }
    }//GEN-LAST:event_jcbCorrecaoActionPerformed

    private void jcbJurosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbJurosActionPerformed
        if (jLiberacoes.isEnabled()) {
            if (jcbJuros.isSelected()) {
                jJuros.setValue(0);
                juros = 0;
                jJuros.setEnabled(true);
                jJuros.requestFocus();

                // Retotaliza
                float tRecibo = 0;
                tRecibo = Calculos.RetValorCampos(rcampo);
                jVrRecibo.setValue(tRecibo + exp + multa + juros + correcao);
            } else {
                ReCalcularRecibo(jVencimentos.getSelectedItem().toString(), "JU");
                jJuros.setEnabled(false);
            }

        }
    }//GEN-LAST:event_jcbJurosActionPerformed

    private void jcbTaxaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTaxaActionPerformed
        if (jLiberacoes.isEnabled()) {
            if (jcbTaxa.isSelected()) {
                jTaxa.setValue(0);
                exp = 0;
                jTaxa.setEnabled(true);
                jTaxa.requestFocus();

                // Retotaliza
                float tRecibo = 0;
                tRecibo = Calculos.RetValorCampos(rcampo);
                jVrRecibo.setValue(tRecibo + exp + multa + juros + correcao);
            } else {
                ReCalcularRecibo(jVencimentos.getSelectedItem().toString(), "EP");
                jTaxa.setEnabled(false);
            }

            // Retotaliza
            float tRecibo = 0;
            tRecibo = Calculos.RetValorCampos(rcampo);
            jVrRecibo.setValue(tRecibo + exp + multa + juros + correcao);
            
        }
    }//GEN-LAST:event_jcbTaxaActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
       try  {
            this.requestFocus();
            this.setSelected(true);  
       } catch (java.beans.PropertyVetoException e)  { e.printStackTrace(); }     
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        if (jLiberacoes.isEnabled()) { AlteraMUJUCOEP(); }
        if (jctCampos.isEnabled()) {
            GravarCampos();
            try {
                MontaTela(jVencimentos.getSelectedItem().toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            EnabledFields(!jctCampos.isEnabled());
            Recalcula();            
        }
    }//GEN-LAST:event_formInternalFrameClosing

    private void jMultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMultaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMultaActionPerformed

    private void jInsDescontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInsDescontoActionPerformed
        String str = JOptionPane.showInputDialog(null, "Entre a Descrição : ", "Desconto", 1);
        String mTipo = "";
        if (str.trim().toUpperCase().contains("ALUGUEL")) {
            mTipo = "AL:IP";
        } else {
            mTipo = "BR";
        }

        String newCampo = rcampo;
        String mCpoDesc = ";DC:2:" + FuncoesGlobais.GravaValor("0,00") + ":0000:DC:" + mTipo + ":ET:DS" + FuncoesGlobais.CriptaNome(str.trim().toUpperCase()); 
        newCampo += mCpoDesc;
        rcampo = LimpaCalculos(newCampo);        
        
        try {
            if (InsereCampos()) {
                try {
                    MontaTela(jVencimentos.getSelectedItem().toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        EnabledFields(true);
        SetFocusFields();                
    }//GEN-LAST:event_jInsDescontoActionPerformed

    private void jInsDiferencaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInsDiferencaActionPerformed
        String str = JOptionPane.showInputDialog(null, "Entre a Descrição : ", "Diferença", 1);
            
        String mTipo = "";
        //if (!str.trim().toUpperCase().contains("SEGURO")) {
            if (str.trim().toUpperCase().contains("ALUGUEL")) {
                mTipo = ":AL:LQ:ET";
            } else {
                Object[] options = { "Sim", "Não" };
                int n = JOptionPane.showOptionDialog(null,
                        "Enviar para extrato do proprietário ? ",
                        "Atenção", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                String auxExt = "";
                if (n == JOptionPane.YES_OPTION) auxExt = ":ET";
                mTipo = auxExt;
            }
        //} else {
        //    mTipo = "";
        //}
        
        String newCampo = rcampo;
        String mCpoDesc = ";DF:2:" + FuncoesGlobais.GravaValor("0,00") + ":0000:DF:RZ" + mTipo + ":DS" + FuncoesGlobais.CriptaNome(str.trim().toUpperCase());
        newCampo += mCpoDesc;
        rcampo = LimpaCalculos(newCampo);        
        
        try {
            if (InsereCampos()) {
                try {
                    MontaTela(jVencimentos.getSelectedItem().toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        EnabledFields(true);
        SetFocusFields();                
    }//GEN-LAST:event_jInsDiferencaActionPerformed

    private void jInsSeguroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInsSeguroActionPerformed
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(null,
                "Enviar para extrato do proprietário ? ",
                "Atenção", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        String auxExt = "";
        if (n == JOptionPane.YES_OPTION) auxExt = ":ET";

        String newCampo = rcampo;
        String mCpoDesc = ";SG:3:" + FuncoesGlobais.GravaValor("0,00") + ":0000:SG:RZ" + auxExt + ":DS" + FuncoesGlobais.CriptaNome("SEGURO");
        newCampo += mCpoDesc;
        rcampo = LimpaCalculos(newCampo);
        
        try {
            if (InsereCampos()) {
                try {
                    MontaTela(jVencimentos.getSelectedItem().toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        EnabledFields(true);
        SetFocusFields();        
    }//GEN-LAST:event_jInsSeguroActionPerformed

    private void jbtApagarNNumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtApagarNNumeroActionPerformed
        String delnnumero = PegaNNumero();
        try {  
            boolean pode = true;
            jAutoriza oAut = new jAutoriza(null, true);
            oAut.setVisible(true);
            pode = oAut.pode;

            if (pode) {
                String SQLtxt = null;
                Object[] options = { "Sim", "Não" };
                int n = JOptionPane.showOptionDialog(null,
                    "Deseja apagar o NNUMERO ? ",
                    "Atenção", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (n == JOptionPane.YES_OPTION) {
                    // Pegar o path e nome do arquivo a excluir
                    String[][] aPath = null; String varBoleta = "";
                    try {
                        aPath = conn.LerCamposTabela(new String[] {"boletapath"}, "recibo", "CONTRATO = '" + this.contrato +
                                "' AND DTVENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "'");                        
                    } catch (SQLException e) {}
                    if (aPath != null) {
                        if (aPath[0][3] != null) varBoleta = aPath[0][3]; 
                    }
                
                    SQLtxt = "UPDATE RECIBO SET nnumero = null, remessa = 'N', boletapath = null WHERE CONTRATO = '" + this.contrato +
                                "' AND DTVENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "';";
                    if (conn.ExecutarComando(SQLtxt) > 0) {
                        conn.Auditor("APAGAR/RECIBO: NNUMERO", this.contrato + " "+ Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")));
                        
                        // Apaga no bloquetos
                        SQLtxt = "DELETE FROM bloquetos WHERE CONTRATO = '" + this.contrato +
                                "' AND VENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "';";
                        conn.ExecutarComando(SQLtxt);
                        
                        if (varBoleta.isEmpty()) {
                            // Apaga boleta em pdf
                            String aMeses[] = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
                            int nMes = Integer.valueOf(jVencimentos.getSelectedItem().toString().substring(3, 5));
                            int nAno = Integer.valueOf(jVencimentos.getSelectedItem().toString().substring(6));
                            String pathName = "reports/Boletas/" + nAno + "/" + aMeses[nMes - 1] + "/";
                            String outFileName = pathName + this.contrato + "_*" + delnnumero + ".pdf";
                            try {
                                File file = new File(pathName);
                                File[] arquivos = file.listFiles();    
                                for(File arquivo : arquivos) {
                                    if(arquivo.getName().endsWith(delnnumero + ".pdf")) {
                                        if(arquivo.delete()) {
                                            System.out.println(file.getName() + " is deleted!");
                                        }else{
                                            System.out.println("Delete operation is failed.");
                                        }
                                    }
                                }
                            } catch (Exception e) {}
                            System.out.println(outFileName);

                            // Deleta o mes seguinte
                            if (aMeses[nMes - 1].equalsIgnoreCase("Dezembro")) {
                                pathName = "reports/Boletas/" + (nAno + 1) + "/" + aMeses[0] + "/";
                                outFileName = pathName + this.contrato + "_*" + delnnumero + ".pdf";
                            } else {
                                pathName = "reports/Boletas/" + nAno + "/" + aMeses[nMes] + "/";
                                outFileName = pathName + this.contrato + "_*" + delnnumero + ".pdf";
                            }
                            try {
                                File file = new File(pathName);
                                File[] arquivos = file.listFiles();    
                                for(File arquivo : arquivos) {
                                    if(arquivo.getName().endsWith(delnnumero + ".pdf")) {
                                        if(arquivo.delete()) {
                                            System.out.println(file.getName() + " is deleted!");
                                        }else{
                                            System.out.println("Delete operation is failed.");
                                        }
                                    }
                                }
                            } catch (Exception e) {}
                            System.out.println(outFileName);
                        } else {
                            File file = new File(varBoleta);
                            if (file.delete()) {
                                System.out.println(file.getName() + " foi deletado!");
                            } else {
                                System.out.println("A operação de delação falhou.");
                            }
                        }        
                        EnaDisButton();
                    }
                } 
            }
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
    }//GEN-LAST:event_jbtApagarNNumeroActionPerformed

    private String PegaNNumero() {
        String Retorno = "";
        String[][] pNnumero = null;
        try {
            pNnumero = conn.LerCamposTabela(new String[] {"nnumero"}, "recibo", "CONTRATO = '" + this.contrato + "' AND DTVENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "'");
        } catch (SQLException e) {}
        if (pNnumero != null) {
            if (pNnumero[0][3] != null) {
                String tmpNnumero = pNnumero[0][3].substring(4);
                String tmpNumeroNosso = "";
                for (int i=0;i<=tmpNnumero.length()-1;i++) {
                    if (!tmpNnumero.substring(i, i + 1).equals("0")) tmpNumeroNosso += tmpNnumero.substring(i, i + 1);
                }
                Retorno = tmpNumeroNosso.substring(0,tmpNumeroNosso.length() -1) + "-" + tmpNumeroNosso.substring(tmpNumeroNosso.length()-1);
            } else Retorno = null;
        } else Retorno = null;
        
        return Retorno;
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
                EnabledFields(false);

                CalcularRecibo(jVencimentos.getSelectedItem().toString());
                EnaDisButton();
            } else { 
                JOptionPane.showMessageDialog(null, "Não existe recibo(s) gerado(s) para este contrato!!!", "Erro", JOptionPane.ERROR_MESSAGE);
                LimpaTela();
            } 
        }        
    }

    private void EnaDisButton() {
        // Ligar/Desligar botao apagar nnumero
        String tmpDtVecto = jVencimentos.getSelectedItem().toString();
        String tmpContrato = jContrato.getSelectedItem().toString();
        String tmpRemessa = "N";
        try {
            tmpRemessa = conn.LerCamposTabela(new String[] {"remessa"}, "recibo", "contrato = '" + tmpContrato + "' AND dtvencimento = '" + Dates.StringtoString(tmpDtVecto, "dd/MM/yyyy", "yyyy/MM/dd") + "'")[0][3];
        } catch (Exception e) {}
        jbtApagarNNumero.setEnabled(tmpRemessa.equalsIgnoreCase("S"));

    }

    public void MontaTela(String vecto) throws SQLException, ParseException {
        if ("".equals(vecto.trim())) { return; }

        // Limpa campos
        jctCampos.removeAll();
        jctCampos.repaint();

        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);

        if (pResult.first()) {
            String joCampo = pResult.getString("campo");
            while (joCampo.contains(";;")) {joCampo = joCampo.replaceAll(";;", ";");}
            if (joCampo.substring(joCampo.length() - 1, joCampo.length()).equalsIgnoreCase(";")) joCampo = joCampo.substring(0, joCampo.length() - 1);
            String tcampo = IPTU(vecto, joCampo);
            DepuraCampos a = new DepuraCampos(tcampo);
            VariaveisGlobais.ccampos = tcampo;

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

        int at = 20; int llg = 180; int ltf = 120; int lcp = 60; int lcc = 278;
        int top = 5; int left = 5;

        JLabel lb = new JLabel();
        lb.setText(aCampos[0]);
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
        tf.setText(aCampos[1]);
        tf.setVisible(true);
        tf.setForeground(new Color(0,128,0));
        tf.setDisabledTextColor(Color.RED);
        tf.setBounds(lb.getX() + llg, 0 + (at * i) + top, ltf, at);
        tf.setName("Field" + i);
        jctCampos.add(tf);

        JCheckBox cb = new JCheckBox();
        cb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        //cb.setSelected(("0".equals(aCampos[2]) ? false : true));
        cb.setVisible(true);
        cb.setForeground(defaults.getColor("CheckBox.Foreground")); //Color.BLACK);
        cb.setBounds(tf.getX() + ltf + 5, 0 + (at * i) + top, at, at);
        cb.setName("Check" + i);
        if ("A".equalsIgnoreCase(aCampos[7])) {
            cb.setSelected(false);
            cb.setEnabled(false);
        } else {
            cb.setSelected(("0".equals(aCampos[2]) ? false : true));
            cb.setEnabled(true);
        }
        jctCampos.add(cb);

        JFormattedTextField cp = new JFormattedTextField();
        try {
            //cp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter((!"C".equals(aCampos[5]) ? "##/####" : "##/##"))));
            cp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##")));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        cp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        cp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cp.setText(("".equals(aCampos[3]) ? "00/00" + (!"C".equals(aCampos[5]) ? "00" : "") : aCampos[3]));
        cp.setVisible(true);
        cp.setForeground(new Color(0,128,0));
        cp.setDisabledTextColor(Color.RED);
        cp.setBounds(cb.getX() + at + 10, 0 + (at * i) + top, lcp, at);
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
        jctCampos.add(cp);
        

        JTextField cc = new JTextField();
        cc.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        cc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cc.setText(aCampos[4]);
        cc.setVisible(false);
        cc.setForeground(new Color(0,128,0));
        cc.setDisabledTextColor(Color.RED);
        cc.setBounds(cp.getX() + lcp + 15, 0 + (at * i), lcc, at);
        cc.setName("Barras" + i);
        jctCampos.add(cc);

        jctCampos.repaint();
    }

    private boolean GravarCampos() {
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
        String saida = ""; String nmCampo = "";
        for (i=0; i <= maxcpos - 1; i++) {
            if (jctCampos.getComponent(i) instanceof JLabel) {
                saida = ((JLabel) jctCampos.getComponent(i)).getText();
                nmCampo = ((JLabel) jctCampos.getComponent(i)).getName();
            } else if (jctCampos.getComponent(i) instanceof JFormattedTextField) {
                saida = ((JFormattedTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JFormattedTextField) jctCampos.getComponent(i)).getName();
            } else if (jctCampos.getComponent(i) instanceof JTextField) {
                saida = ((JTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JTextField) jctCampos.getComponent(i)).getName();
            } else if (jctCampos.getComponent(i) instanceof JCheckBox) {
                boolean simnao = ((JCheckBox) jctCampos.getComponent(i)).isSelected();
                saida = (simnao ? "TRUE" : "FALSE");
                nmCampo = ((JCheckBox) jctCampos.getComponent(i)).getName();
            } else saida = "";
            //System.out.println(saida + " -> " + jctCampo.getComponent(i).getName());

            if (nmCampo.contains("Field")) {
                cCampos[j] = ChangeCampos(cCampos[j],2,saida);
            } else if (nmCampo.contains("Check")) {
                cCampos[j] = ChangeCampos(cCampos[j], -2, saida);
            } else if (nmCampo.contains("Cota")) {
                cCampos[j] = ChangeCampos(cCampos[j], 3, saida);
            } else if (nmCampo.contains("Barras")) {
                cCampos[j] = ChangeCampos(cCampos[j], -1, saida);
            }

            int mod = (i + 1) % 5;
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

        String fCampos = "";
        if (cCampos.length != 0) fCampos = FuncoesGlobais.join(cCampos, ";"); else fCampos = "";

        String SQLtxt; String tboleta = null;
        try {
            tboleta = conn.LerCamposTabela(new String[] {"boleta"}, "locatarios", "contrato = '" + this.contrato + "'")[0][3];
        } catch (Exception e) {}
        
        if (tboleta.equalsIgnoreCase("-1")) {
            SQLtxt = "UPDATE RECIBO SET CAMPO = '" + fCampos + "' WHERE CONTRATO = '" + this.contrato +
                        "' AND DTVENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "';";
        } else {
                SQLtxt = "UPDATE RECIBO SET CAMPO = '" + fCampos + "', NNUMERO = null WHERE CONTRATO = '" + this.contrato +
                            "' AND DTVENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "';";
        }   
        
        boolean sucesso = false;
        
        // 05-12-2011 (removido para analise futura)
        //String smsg = FuncoesGlobais.ValidaProtocol(fCampos);
        //if ("".equals(smsg)) { sucesso = (conn.ExecutarComando(SQLtxt) > 0); } else {JOptionPane.showMessageDialog(null, smsg , "Error", JOptionPane.ERROR_MESSAGE); }
        
        sucesso = (conn.ExecutarComando(SQLtxt) > 0);
        if (sucesso) { this.rcampo = fCampos; }
        return sucesso;
    }
    
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
        }

        String mCampo = FuncoesGlobais.join(aCampo, ":");
        mCampo = mCampo.replaceAll("X:", "");
        mCampo = mCampo.replaceAll("X", "");
        if (":".equals(mCampo.substring(mCampo.length() - 1, mCampo.length()))) {
            mCampo = mCampo.substring(0, mCampo.length() - 1);
        }
        return mCampo;
    }
    
    public boolean InsereCampos() throws SQLException {
        
        String[] gCampos = this.rcampo.split(";");
        int j = 0; boolean bRet = false;
        String sqlWhere = ""; String sqlSelect = "";
        for (j = 0; j <= gCampos.length - 1; j++) {
          sqlWhere = sqlWhere + "CART_CODIGO <> '" + gCampos[j].substring(0,2) + "' AND ";
        }

        if (!"".equals(sqlWhere.trim())) {
          sqlWhere = "WHERE " + sqlWhere.substring(0, sqlWhere.length() - 5);
        }
        sqlSelect = "SELECT LANCART.CART_CODIGO, " +
                  "LANCART.CART_DESCR, LANCART.CART_ORDEM, LANCART.CART_CONTEUDO, " +
                  "LANCART.CART_COTPAR, LANCART.CART_TAXA, LANCART.CART_FEDERAL, LANCART.CART_MODIFICA, " +
                  "LANCART.CART_FIXO, LANCART.CART_RETENCAO, LANCART.CART_RECIBO, " +
                  "LANCART.CART_EXTRATO, LANCART.CART_IMPOSTO FROM LANCART " + sqlWhere;

        String cartCampos = null; String stCAMPOS = null; String mSql = null;
        cartCampos = CarregaCamposCarteira(sqlSelect);

        if (!"".equals(cartCampos.trim())) {
            stCAMPOS = this.rcampo + ";" + cartCampos;
            if (stCAMPOS.substring(0, 1).equals(";")) {
              stCAMPOS = stCAMPOS.substring(1);
            }

            if (!this.mCartVazio)
                mSql = "UPDATE RECIBO SET CAMPO = '" + stCAMPOS + "' WHERE CONTRATO = '" + this.contrato + 
                       "' AND DTVENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy")) + "';"; 
            else {
                String[][] vCampos = conn.LerCamposTabela(new String[] {"rgprp", "rgimv", "contrato"}, "locatarios", "contrato = '" + this.contrato + "'");
                String rgprp = vCampos[0][3];
                String rgimv = vCampos[1][3];
                String contrato = vCampos[2][3];

                mSql = "INSERT INTO RECIBO (rgprp,rgimv,contrato,campo) " +
                   "VALUES ('" + this.rgprp + "','" + this.rgimv + "','" + this.contrato +
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

        if (Data1.first()) {
            while (Data1.next()) {
                mCampos = mCampos + Data1.getString("CART_CODIGO") + ":" +
                           Data1.getString("CART_ORDEM") + ":" +
                           "0000000000" + ":" +
                           (!"P".equals(Data1.getString("CART_COTPAR")) ? "0000" : "000000") + ":" +
                           ("1".equals(Data1.getString("CART_TAXA")) ? "NT" : "AL") + ":" +
                           ("1".equals(Data1.getString("CART_RETENCAO")) ? "RT" : "") + ":" +
                           ("1".equals(Data1.getString("CART_RECIBO")) ? "RZ" : "") + ":" +
                           ("1".equals(Data1.getString("CART_EXTRATO")) ? "ET" : "") + ":" +
                           ("1".equals(Data1.getString("CART_IMPOSTO")) ?  "IP" : "") + ";";
            }
        }
        DbMain.FecharTabela(Data1);

        if (!"".equals(mCampos.trim())) {
            mCampos = mCampos.replaceAll("::", ":");
            mCampos = mCampos.replaceAll(":;", ";");

            mCampos = mCampos.substring(0, mCampos.length() - 1);
        }
        return mCampos;
    }
        
    private String LimpaCalculos(String campos) {
        String retorno = campos;
        retorno = retorno.replaceAll("::", ":");
        retorno = retorno.replaceAll(":;", ";");
        retorno = retorno.replaceAll(";;", ";");
        
        return retorno;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jBairro;
    private javax.swing.JLabel jCep;
    private javax.swing.JLabel jCidade;
    private javax.swing.JComboBox jContrato;
    private javax.swing.JFormattedTextField jCorrecao;
    private javax.swing.JLabel jEndereco;
    private javax.swing.JLabel jEstado;
    private javax.swing.JButton jInsDesconto;
    private javax.swing.JButton jInsDiferenca;
    private javax.swing.JButton jInsSeguro;
    private javax.swing.JFormattedTextField jJuros;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jLiberacoes;
    private javax.swing.JFormattedTextField jMulta;
    private javax.swing.JComboBox jNomeLoca;
    private javax.swing.JLabel jNomeProp;
    private javax.swing.JLabel jObs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jRgimv;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JFormattedTextField jTaxa;
    private javax.swing.JComboBox jVencimentos;
    private javax.swing.JFormattedTextField jVrRecibo;
    private javax.swing.JButton jbtAlterar;
    private javax.swing.JButton jbtApagarNNumero;
    private javax.swing.JButton jbtExcluir;
    private javax.swing.JButton jbtLiberar;
    private javax.swing.JCheckBox jcbCorrecao;
    private javax.swing.JCheckBox jcbJuros;
    private javax.swing.JCheckBox jcbMulta;
    private javax.swing.JCheckBox jcbTaxa;
    private javax.swing.JPanel jctCampos;
    private javax.swing.JLabel jlblCorrecao;
    private javax.swing.JLabel jlblJuros;
    private javax.swing.JLabel jlblMulta;
    private javax.swing.JLabel jlblTaxa;
    // End of variables declaration//GEN-END:variables

}
/*
DELETE FROM `jgeral_excluidos`.`auxiliar`;
DELETE FROM `jgeral_excluidos`.`avisos`;
DELETE FROM `jgeral_excluidos`.`caixabck`;
DELETE FROM `jgeral_excluidos`.`carteira`;
DELETE FROM `jgeral_excluidos`.`descontos`;
DELETE FROM `jgeral_excluidos`.`diferenca`;
DELETE FROM `jgeral_excluidos`.`divisao`;
DELETE FROM `jgeral_excluidos`.`extrato`;
DELETE FROM `jgeral_excluidos`.`fiadores`;
DELETE FROM `jgeral_excluidos`.`imoveis`;
DELETE FROM `jgeral_excluidos`.`locabloq`;
DELETE FROM `jgeral_excluidos`.`locatarios`;
DELETE FROM `jgeral_excluidos`.`multa`;
DELETE FROM `jgeral_excluidos`.`parametros`;
DELETE FROM `jgeral_excluidos`.`proprietarios`;
DELETE FROM `jgeral_excluidos`.`recibo`;
DELETE FROM `jgeral_excluidos`.`retencao`;
DELETE FROM `jgeral_excluidos`.`seguros`;

INSERT INTO `olimar`.`menu` (`tipo`, `cod`, `atalho`, `nome`, `rotina`, `icone`, `senha`) VALUES ('4', '14', ' ', 'Retorna Excluido', 'Exclusao.RetornaExcluido', '', '0');

*/