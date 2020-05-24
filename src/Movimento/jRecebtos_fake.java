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

import Funcoes.Autenticacao;
import Funcoes.AutoCompletion;
import Funcoes.ComponentSearch;
import Funcoes.Convert;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.JavaPOS;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.PrinterPOS;
import Funcoes.StringManager;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.WordWrap;
import Funcoes.jPDF;
import Funcoes.jThermica;
import Funcoes.toPrint;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import Protocolo.DivideCC;
import Transicao.jReceber;
import com.ibm.icu.math.BigDecimal;
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
import Sici.jRetenLoc;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIDefaults;

/**
 *
 * @author supervisor
 */
public class jRecebtos_fake extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String rgprp = ""; String rgimv = ""; String contrato = ""; String rcampo = "";
    boolean executando = false; boolean mCartVazio = false;
    boolean bExecNome = false, bExecCodigo = false, eBloq = false;
    String regras = "";
    
    jReceber tRec = new jReceber();
    JPanel pnlDigite = (JPanel) tRec.getComponent(ComponentSearch.ComponentSearch(tRec, "jpnDigite"));
    JButton btnLancar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtLancar"));
    JButton btnCancelar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtCancelar"));
    JPanel pnlBotoes = (JPanel) tRec.getComponent(ComponentSearch.ComponentSearch(tRec, "pnlBotoes"));
    JToggleButton btDN = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgDN"));
    JToggleButton btCH = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCH"));
    JToggleButton btCT = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCT"));
    JFormattedTextField jResto = (JFormattedTextField) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "JRESTO"));
    //JFormattedTextField jTroco = (JFormattedTextField) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "JTROCO"));

    // campos de calculos
    float exp = 0;
    float multa = 0;
    float juros = 0;
    float correcao = 0;

    public Object[][] linhas = {};
    
    private void InitjReceber() {
        tRec.setVisible(true);
        tRec.setEnabled(true);
        tRec.setBounds(0, 0, 470, 290);
        try {
            jpRecebe.add(tRec);
        } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace(); }
        jpRecebe.repaint();
        jpRecebe.setEnabled(true);

        btnLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tRec.Lancar();
                if (tRec.bprintdoc) {
                    try {
                        try {
                            Imprimir();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                        jContrato.setEnabled(true);
                        jNomeLoca.setEnabled(true);
                        jContrato.requestFocus();
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
                    jContrato.setEnabled(true);
                    jNomeLoca.setEnabled(true);
                    jContrato.requestFocus();
                }
                jVencimentos.setEnabled(true);
                jQtde.setEnabled(true);
                /////////////////jtgbAvulso.setEnabled(true);
            }
        });

        btDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentos.setEnabled(false);
                jQtde.setEnabled(false);
                ///////////////////jtgbAvulso.setEnabled(false);
            }
        });

        btCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentos.setEnabled(false);
                jQtde.setEnabled(false);
                ////////////////////jtgbAvulso.setEnabled(false);
            }
        });

        btCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentos.setEnabled(false);
                jQtde.setEnabled(false);
                //////////////////jtgbAvulso.setEnabled(false);
            }
        });

    }

    private void VerificaBloq(String tcontrato) {
        eBloq = false;
        String[][] aBloq = null;
        try {aBloq = conn.LerCamposTabela(new String[] {"historico"}, "LocaBloq", FuncoesGlobais.Subst("contrato = '&1.'",new String[] {tcontrato}));} catch (Exception ex) {}
        if (aBloq != null) {
            jMsgBloqLoca jBloqLoc = new jMsgBloqLoca(null, rootPaneCheckingEnabled);
            jBloqLoc.SetarMotivo(aBloq[0][3]);
            jBloqLoc.setVisible(true);      
            eBloq = true;
        } 
    }
    
    /** Creates new form jAltRecibos */
    public jRecebtos_fake() {
        initComponents();

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        try {regras = conn.LerParametros("REGRAS");} catch (SQLException ex) {}
        
        InitjReceber();

        FillCombos();
        AutoCompletion.enable(jContrato);
        AutoCompletion.enable(jNomeLoca);

        ComboBoxEditor edit = jNomeLoca.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Verifica bloqueio
                VerificaBloq(jContrato.getSelectedItem().toString());
                if (eBloq) {
                    jContrato.setEnabled(true);
                    jNomeLoca.setEnabled(true);
                    jContrato.requestFocus();
                    jVencimentos.setEnabled(true);
                    jQtde.setEnabled(true);                    
                    return;
                }

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
                    SpinnerModel sm = new SpinnerNumberModel(300, 1, 300, 1);
                    jQtde.setModel(sm);
                    ListaVectos();
                    executando = false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                boolean bbloq = true;
                if (jVencimentos.getItemCount() > 0) {
                    //jQtde.setValue(jVencimentos.getItemCount());
                    SpinnerModel sm = new SpinnerNumberModel(jVencimentos.getItemCount(), 1, jVencimentos.getItemCount(), 1);
                    jQtde.setModel(sm);

                    Recalcula();
                } else {
                    JOptionPane.showMessageDialog(null, "Não existe recibo(s) para este contrato!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    jVencimentos.setEnabled(true);
                    jQtde.setEnabled(true);
                    lblQtde.setVisible(true);
                    ///////////////////jtgbAvulso.setEnabled(true);
                    jNomeLoca.setEnabled(true);
                    jContrato.setEnabled(true);
                    jContrato.requestFocus();
                    bbloq = false;
                }

                if (bbloq) {
                    jContrato.setEnabled(false);
                    jNomeLoca.setEnabled(false);
                }

                tRec.Enable(true);
                tRec.rgimv = rgimv; tRec.rgprp = rgprp; tRec.contrato = contrato; tRec.acao = "RC"; tRec.operacao = "CRE";
                btnLancar.setEnabled(false);
                tRec.LimpaTransicao();

                // 20/10/2011 FillAvisoLoca(jAvisos, jContrato.getSelectedItem().toString().trim());
                jvrRetencao.setText(VrAvisoLoca(contrato));
                
                // Aviso Sobre termino de Contrato
                String msg = ChecaTermino(contrato);
                if (!msg.trim().equals("")) {
                    // Mensagem
                    JOptionPane.showMessageDialog(null, msg, "Atenção", JOptionPane.INFORMATION_MESSAGE);
                }
                
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
                ///////////////jtgbAvulso.setSelected(false);
                ///////////////jtgbAvulso.setEnabled(true);
                lblQtde.setVisible(true);
                jQtde.setVisible(true);
                tRec.Enable(false);
                tRec.LimpaTransicao();
            }
        });

        ComboBoxEditor editContrato = jContrato.getEditor();
        Component compContrato = editContrato.getEditorComponent();
        compContrato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tRec.Enable(false);
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
                ////////////////jtgbAvulso.setSelected(false);
                ////////////////jtgbAvulso.setEnabled(true);
                lblQtde.setVisible(true);
                jQtde.setVisible(true);
                tRec.Enable(false);
                //jTroco.setText("0,00");
            }
        });

//       ListCellRenderer renderer = jVencimentos.getRenderer();
//       jVencimentos.setRenderer(new FixDisabledCBRenderer(jVencimentos, renderer));
//       jContrato.setRenderer(new MyRenderer());
//       jNomeLoca.setRenderer(new MyRenderer());
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

    private String ChecaTermino(String contrato) {
        String msg = "";        
        String[][] campos = null;
        try {
            campos = conn.LerCamposTabela(new String[] {"dtinicio","dttermino","dtadito","dtseguro",
                    FuncoesGlobais.Subst("((Month(StrDate(dttermino)) = &1. AND Year(StrDate(dttermino)) = &2.)) AS pinta",
                    new String[] {String.valueOf(Dates.iMonth(new Date())), String.valueOf(Dates.iYear(new Date()))})}, "CARTEIRA", 
                    FuncoesGlobais.Subst("contrato = '" + contrato + "' AND ((Month(StrDate(dtinicio)) >= &1. AND " + 
                    "Year(StrDate(dttermino)) >= &2.) OR (Month(StrDate(dttermino)) >= &1. AND Year(StrDate(dttermino)) = &2.))",
                    new String[] {String.valueOf(Dates.iMonth(new Date())), String.valueOf(Dates.iYear(new Date()))}));
        } catch (Exception e) {}
        if (campos != null) {
            String tmesanor = campos[3][3]; if (tmesanor == null) tmesanor = "";
            String tdtini = campos[0][3];
            Date _inic = Dates.StringtoDate(tdtini,"dd/MM/yyyy");
            Date _comp = new Date();
            if (_inic.getYear() != _comp.getYear()) {   
                if (tmesanor.isEmpty()) {
                    String meses = null;
                    try {meses = conn.LerParametros("REAJNUM");} catch (Exception e) {meses = "1";}
                    if (_inic.getMonth() - 1 >= _comp.getMonth() - 1) {
                        if (((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) <= Integer.valueOf(meses)) {
                            if (((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) > 0) {
                                msg = "Faltam " + ((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) + " Mes(es) para o reajuste!!!";
                            } else msg = "";
                        }
                    } else if (_inic.getMonth() == _comp.getMonth()) {
                        msg = "Este é o mês do reajuste!!!";
                        if (campos[4][3].equals("1")) msg += "    Termino de contrato!!!";
                    }
                } else {
                    boolean reaj = Integer.valueOf(tmesanor.substring(3,7)) >= Dates.iYear(new Date());
                    if (!reaj) {
                        if (_inic.getMonth() - 1 > _comp.getMonth() - 1) {
                            String meses = null;
                            try {meses = conn.LerParametros("REAJNUM");} catch (Exception e) {meses = "1";}
                            msg = "Faltam " + meses + " Mes(es) para o reajuste!!!";
                        } else if (_inic.getMonth() == _comp.getMonth()) {
                            msg = "Este é o mês do reajuste!!!";
                            if (campos[4][3].equals("1")) msg += "    Termino de contrato!!!";
                        }
                    }
                }
            }

        }
        return msg;
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
                
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
            //ex.printStackTrace();
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

//        jResto.setValue(tRecibo + exp + multa + juros + correcao);
//        tRec.vrAREC = LerValor.StringToFloat(jResto.getText());

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

        executando = true;
        jVencimentos.removeAllItems();
        executando = false;

        jVrRecibo.setValue(0);
        jResto.setValue(0);
        jQtde.setValue(0);
        jTotalRecibos.setValue(0);

        // 20/10/2011 TableControl.header(jAvisos, new String[][] {{"desc","valor"},{"380","90"}});
    }

    private void ListaVectos() throws SQLException {
        String sql = "SELECT * FROM RECIBO WHERE tag <> 'X' AND contrato = '" + contrato + "' ORDER BY dtvencimento LIMIT &1.;";
        sql = FuncoesGlobais.Subst(sql, new String[] {String.valueOf(jQtde.getValue())});
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
        jLabel17 = new javax.swing.JLabel();
        jObs = new javax.swing.JLabel();
        jScroll = new javax.swing.JScrollPane();
        jctCampos = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jVencimentos = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jVrRecibo = new javax.swing.JFormattedTextField();
        jTotalRecibos = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        lblQtde = new javax.swing.JLabel();
        jQtde = new javax.swing.JSpinner();
        jtgbAvulso = new javax.swing.JToggleButton();
        jImprimir = new javax.swing.JButton();
        jchb1Via = new javax.swing.JCheckBox();
        jpRecebe = new javax.swing.JPanel();
        jbtretencão = new javax.swing.JButton();
        jvrRetencao = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Recebimentos (fk)::.");
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
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
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 711, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        jLabel17.setBackground(new java.awt.Color(191, 191, 191));
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jObs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNomeLoca, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jObs, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
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

        lblQtde.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblQtde.setText("Qtde:");

        jQtde.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jQtdeStateChanged(evt);
            }
        });

        jtgbAvulso.setText("Avulso");
        jtgbAvulso.setEnabled(false);
        jtgbAvulso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtgbAvulsoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addComponent(jVencimentos, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtgbAvulso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblQtde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jQtde, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jVrRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTotalRecibos)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtgbAvulso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jVencimentos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jQtde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblQtde, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jVrRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTotalRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jImprimir.setText("Imprimir");
        jImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jImprimirActionPerformed(evt);
            }
        });

        jchb1Via.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        jchb1Via.setText("Imprime apenas a 1° Via.");

        jpRecebe.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpRecebe.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jpRecebe.setPreferredSize(new java.awt.Dimension(481, 250));

        javax.swing.GroupLayout jpRecebeLayout = new javax.swing.GroupLayout(jpRecebe);
        jpRecebe.setLayout(jpRecebeLayout);
        jpRecebeLayout.setHorizontalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 481, Short.MAX_VALUE)
        );
        jpRecebeLayout.setVerticalGroup(
            jpRecebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );

        jbtretencão.setText("Valores retidos do Locatário");
        jbtretencão.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtretencãoActionPerformed(evt);
            }
        });

        jvrRetencao.setBackground(new java.awt.Color(254, 254, 254));
        jvrRetencao.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jvrRetencao.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jvrRetencao.setText("0,00");
        jvrRetencao.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jvrRetencao.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jchb1Via)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jbtretencão, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jvrRetencao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jchb1Via)
                            .addComponent(jImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtretencão, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jvrRetencao))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FillCombos() {
        String sSql = "SELECT contrato as codigo, UPPER(nomerazao) as nome FROM locatarios WHERE fiador1uf <> 'X' OR IsNull(fiador1uf) ORDER BY UPPER(nomerazao);";

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

    private void jImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jImprimirActionPerformed
        if (VariaveisGlobais.Recibo.split(",")[1].equalsIgnoreCase("TXT")) {
            ImprimeRecibo(-1, new String[][] {},jVrRecibo.getText(), "F",2);
        } else {
            //ImprimeReciboThermica(-1, new String[][] {},jVrRecibo.getText(), "F",2);
            ImprimeReciboPDF(-1, new String[][] {},jVrRecibo.getText(), "F",2);
        }
    }//GEN-LAST:event_jImprimirActionPerformed

    private void jQtdeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jQtdeStateChanged
        try {
            executando = true;
            ListaVectos();
            executando = false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Recalcula();
        tRec.Enable(true);
        tRec.rgimv = rgimv; tRec.rgprp = rgprp; tRec.contrato = contrato; tRec.acao = "RC"; tRec.operacao = "CRE";
        btnLancar.setEnabled(false);
        tRec.LimpaTransicao();
    }//GEN-LAST:event_jQtdeStateChanged

    private void jtgbAvulsoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtgbAvulsoActionPerformed
        lblQtde.setVisible(!jtgbAvulso.isSelected());
        jQtde.setVisible(!jtgbAvulso.isSelected());
        Recalcula();
    }//GEN-LAST:event_jtgbAvulsoActionPerformed

    private void jbtretencãoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtretencãoActionPerformed
        // chama tela de retençao de locatarios
        VariaveisGlobais.rcontrato = jContrato.getSelectedItem().toString();
        jRetenLoc oImv = null;
        oImv = new jRetenLoc(null, closable);
        oImv.setVisible(true);
        
    }//GEN-LAST:event_jbtretencãoActionPerformed

    private String VrAvisoLoca(String sLoca) {
        float fTotCred = 0, fTotDeb = 0;
        String retorno = "0,00";
        
        String sSql = FuncoesGlobais.Subst("SELECT campo FROM avisos WHERE registro = '&1.' AND rid = '4';", new String[] {sLoca});
        ResultSet imResult = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

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
                            valor = LerValor.FormatNumber(rCampos[0][2], 2);
                        } else {
                            desc = aLinhas[l];
                            valor = "";
                        }
                    }
                }

                if ("CRE".equals(rCampos[0][8])) {
                    fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2], 2));
                } else {
                    fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2], 2));
                }
            }
            retorno = LerValor.floatToCurrency(fTotCred - fTotDeb, 2);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
        return retorno;
    }

    private boolean Distribuicao(String nAut, String vencto) throws SQLException {
        DepuraCampos zCampos; String[] zCampo;

        String[][] aCC = DivideCC.Divisao(rgimv);
        String[][] jCampo;
        String[] gmpCampo, difCampo;
        String[][] tmpCampo;
        String[][] divaCampo = {};
        String[] admValores = {};
        String[][] gCampo = {};

        String[] admPer = null;
        admPer = new Calculos().percADM(rgprp, rgimv);

        String[][] dvCampo;

        float fDC = 0, fDF = 0;

        float[] aComissao = new Calculos().percComissao(rgprp, rgimv);
        float fComissao = aComissao[0]; float rComissao = aComissao[1];
        String sComissao = FuncoesGlobais.GravaValores(String.valueOf(fComissao).replace(".", ","), 3);

        String[] sVenctos = {vencto};

        int i = 0;
        //for (int i=0; i<sVenctos.length; i++) {
            String tCampo = CriticaCampo(contrato, sVenctos[i]);

            String[] aCampo = tCampo.split(";");

            // Captura descontos/diferencas
            jCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"), false);
            gmpCampo = null; fDC = 0; 
            difCampo = null; fDF = 0;
            for (int w=0;w<jCampo.length;w++) {
                gmpCampo = null;
                gmpCampo = FuncoesGlobais.treeSeekArray2(jCampo, "DC", w, w);
                if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                    fDC += ("LQ".equals(gmpCampo[4]) ? LerValor.StringToFloat(gmpCampo[3]) : 0);
                } else fDC += 0;

                difCampo = null;
                difCampo = FuncoesGlobais.treeSeekArray2(jCampo, "DF", w, w);                        
                if (Integer.valueOf(difCampo[0]) > -1 && Integer.valueOf(difCampo[1]) > -1) {
                    fDF += ("LQ".equals(difCampo[4]) ? LerValor.StringToFloat(difCampo[3]) : 0);
                } else fDF += 0;
            }
            // ------------- FIM Descontos/Diferenças
            
            for (int j=0; j < aCampo.length; j++) {
                int k = aCampo[j].indexOf("AL");
                if (k > -1) {
                    //zCampos = new DepuraCampos(aCampo[j]);
                    zCampos = new DepuraCampos(tCampo);
                    VariaveisGlobais.ccampos = aCampo[j];
                    zCampos.SplitCampos();
                    zCampo = zCampos.Depurar(j); 

                    if ("AL".equals(aCampo[j].split(":")[4])) {
                        float calc1 = LerValor.StringToFloat(zCampo[1]) + fDF - fDC;
                        float calc2 = fComissao / 100;
                        float reslt = calc1 * calc2;

                        // Nova modelo Comissao
                        String valor = "";
                        if (fComissao != 0 && rComissao == 0) {
                            valor = String.valueOf(reslt).replace(".", ",");
                        } else if (fComissao == 0 && rComissao != 0) {
                            valor = String.valueOf(rComissao).replace(".", ",");
                        } else {
                            valor = String.valueOf(reslt).replace(".", ",");
                        }
                        aCampo[j] += ":CM" + FuncoesGlobais.GravaValores(valor, 2);
                    }
//                }
                    tmpCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"),false);

                    // ja foi adiantado
                    boolean bad = (FuncoesGlobais.treeSeekArray(tmpCampo, "AD", 0)[0].equalsIgnoreCase("-1") ? false : true);
                    
                    if (!"DC".equals(aCampo[j].split(":")[4]) || !"DF".equals(aCampo[j].split(":")[4]) || !"SG".equals(aCampo[j].split(":")[4])) {
                        admValores = new String[] {};
                        // Separa valores da ADM
                        // Separa Multa
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "MU", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[0])) / 100));
                            String valor = LerValor.FormatNumber(FuncoesGlobais.GravaValor(LerValor.FloatToString(valor1)),2); 
                            valor1 = LerValor.StringToFloat(valor);
                            if (valor1 > 0) {
                                tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "MU" + FuncoesGlobais.GravaValores(valor, 2);
                            } else {
                                tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                            }
                            float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[0])) / 100));  //                    
                            String valor_mu = String.valueOf(valor3).replace(".", ",");
                            admValores = FuncoesGlobais.ArrayAdd(admValores, valor_mu);
                        } else {
                            admValores = FuncoesGlobais.ArrayAdd(admValores, "0");
                        }

                        // Juros
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "JU", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[1])) / 100));
                            String valor = LerValor.FormatNumber(FuncoesGlobais.GravaValor(LerValor.FloatToString(valor1)),2); 
                            valor1 = LerValor.StringToFloat(valor);
                            if (valor1 > 0) {
                                tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "JU" + FuncoesGlobais.GravaValores(valor, 2);
                            } else {
                                tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                            }
                            float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[1])) / 100));
                            String valor_ju = String.valueOf(valor3).replace(".", ",");
                            admValores = FuncoesGlobais.ArrayAdd(admValores, valor_ju);
                        } else {
                            admValores = FuncoesGlobais.ArrayAdd(admValores, "0");
                        }

                        // Correção
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "CO", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[2])) / 100));
                            String valor = LerValor.FormatNumber(FuncoesGlobais.GravaValor(LerValor.FloatToString(valor1)),2); 
                            valor1 = LerValor.StringToFloat(valor);
                            if (valor1 > 0) {
                                tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CO" + FuncoesGlobais.GravaValores(valor, 2);
                            } else {
                                tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                            }
                            float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[2])) / 100));
                            String valor_co = String.valueOf(valor3).replace(".", ",");
                            admValores = FuncoesGlobais.ArrayAdd(admValores, valor_co);
                        } else {
                            admValores = FuncoesGlobais.ArrayAdd(admValores, "0");
                        }

                        // Expediente
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "EP", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[3])) / 100));
                            String valor = LerValor.FormatNumber(FuncoesGlobais.GravaValor(LerValor.FloatToString(valor1)),2); 
                            valor1 = LerValor.StringToFloat(valor);
                            if (valor1 > 0) {
                                tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "EP" + FuncoesGlobais.GravaValores(valor, 2);
                            } else {
                                tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                            }
                            float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[3])) / 100));
                            String valor_ep = String.valueOf(valor3).replace(".", ",");
                            admValores = FuncoesGlobais.ArrayAdd(admValores, valor_ep);
                        } else {
                            admValores = FuncoesGlobais.ArrayAdd(admValores, "0");
                        }

                        // Separa comissão
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "CM", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            admValores = FuncoesGlobais.ArrayAdd(admValores, gmpCampo[3]);
                        } else admValores = FuncoesGlobais.ArrayAdd(admValores, "0");

                        aCampo[j] = FuncoesGlobais.join(tmpCampo[j], ":");

                        // Envio dos Valores da ADM
                        if (LerValor.StringToFloat(admValores[0]) + LerValor.StringToFloat(admValores[1]) + LerValor.StringToFloat(admValores[2]) + LerValor.StringToFloat(admValores[3]) > 0) {
                            gCampo = FuncoesGlobais.ArraysAdd(gCampo, new String[] {admValores[0], admValores[1], admValores[2], admValores[3], sVenctos[i]});
                        }
                    }
                }
            }

            // Clona Variavel
            String[] oCampo = aCampo;

            for (int l=0;l<aCC.length;l++) {
                dvCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"), false);
                for (int m=0;m<dvCampo.length;m++){
                    //';
                    float part1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                    float part2 = LerValor.StringToFloat(aCC[l][1].replace(".", ",")) / 100;
                    
                    // 15-12-2016 12h30m
                    if (l==0) {
                        if (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0) part2 = 1;
                    } else {
                        if (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0) {
                            part2 = 0;
                        } else part2 = 1;
                    }
                    
                    String vrfinal = LerValor.FloatToString(part1 * part2);
                    
                    //dvCampo[m][2] = FuncoesGlobais.GravaValores(LerValor.FloatToString(LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2],2)) * (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0 ? 1 : (LerValor.StringToFloat(aCC[l][1]) / 100))),2);
                    
                    if (aCC[l][2].trim().toUpperCase().equalsIgnoreCase("TRUE")) {
                        //'; divide tudo
                        float fValor1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                        float fValor2 = Float.valueOf(aCC[l][1].replace(",", ".")) / 100;
                        dvCampo[m][2] = FuncoesGlobais.GravaValores(LerValor.FloatToString(fValor1 * fValor2),2);
                    } else {
                        dvCampo[m][2] = FuncoesGlobais.GravaValores(vrfinal, 2);                    
                    }

                    if ("AL".equals(dvCampo[m][4])) {
                        // Separa Multa
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "MU", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "MU" + FuncoesGlobais.GravaValores(valor, 2);
                        }


                        // Separa juros
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "JU", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "JU" + FuncoesGlobais.GravaValores(valor, 2);
                        }

                        // Separa Correção
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "CO", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CO" + FuncoesGlobais.GravaValores(valor, 2);
                        }

                        // Separa Expediente
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "EP", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "EP" + FuncoesGlobais.GravaValores(valor, 2);
                        }

                        // Separa Comissao
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "CM", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1].replace(".", ",")) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CM" + FuncoesGlobais.GravaValores(valor, 2);
                        }
                    }
                }

                //String[] tpCampo = {contrato.trim(), aCC[l][0], rgimv, FuncoesGlobais.SuperJoin(dvCampo, l == 0)};
                String[] tpCampo = {contrato.trim(), aCC[l][0], rgimv, FuncoesGlobais.SuperJoin(dvCampo, true)};
                divaCampo = FuncoesGlobais.ArraysAdd(divaCampo, tpCampo);
            }

            for (int l=0; l<aCC.length; l++) {
                aCampo = null; aCampo = divaCampo[l][3].split(";");

                String[] rCampo = {};
                String[] eCampo = {};
                String[] iCampo = {};

                String[] lCampo = {};
                String[] xCampo = {};
                String[] pCampo = {};
                String[] sCampo = {};
                String[] hCampo = {};

                for (int j=0; j<aCampo.length; j++) {
                    String[] wCampo = aCampo[j].split(":");

                    int k = aCampo[j].indexOf("AL");
                    if (k > -1) lCampo = FuncoesGlobais.ArrayAdd(lCampo, aCampo[j]);

                    // Retenção
                    k = aCampo[j].indexOf("RT");
                    if (k > -1) sCampo = FuncoesGlobais.ArrayAdd(sCampo, aCampo[j]);

                    // Antecipados
                    k = aCampo[j].indexOf("AT");
                    if (k > -1) hCampo = FuncoesGlobais.ArrayAdd(hCampo, aCampo[j]);

                    // Expediente
                    k = FuncoesGlobais.IndexOf(wCampo, "EP");
                    if (k > -1) pCampo = FuncoesGlobais.ArrayAdd(pCampo, "EP" + wCampo[k].substring(wCampo[k].length() - 10, wCampo[k].length()) + ":0000:EP");

                    // Taxas
                    k = aCampo[j].indexOf("AL");
                    if (k < 0) xCampo = FuncoesGlobais.ArrayAdd(xCampo, aCampo[j]);

                    // Campos Razao
                    k = aCampo[j].indexOf("RZ");
                    if (k > -1) rCampo = FuncoesGlobais.ArrayAdd(rCampo, aCampo[j]);

                    k = aCampo[j].indexOf("ET");
                    if (k > -1) eCampo = FuncoesGlobais.ArrayAdd(eCampo, aCampo[j]);

                    k = aCampo[j].indexOf("IP");
                    if (k > -1) iCampo = FuncoesGlobais.ArrayAdd(iCampo, aCampo[j]);
                }

                //String[] tpCampo = {lCampo, pCampo, xCampo, rCampo, eCampo, iCampo, sCampo};
                String slCampo = "", spCampo = "", sxCampo = "", srCampo = "", seCampo = "", siCampo = "", ssCampo = "", waCampo = "";
                try {slCampo = FuncoesGlobais.join(lCampo, ";");} catch (Exception ex) {slCampo = "";}
                try {spCampo = FuncoesGlobais.join(pCampo, ";");} catch (Exception ex) {spCampo = "";}
                try {sxCampo = FuncoesGlobais.join(rCampo, ";");} catch (Exception ex) {sxCampo = "";}
                try {srCampo = FuncoesGlobais.join(rCampo, ";");} catch (Exception ex) {srCampo = "";}
                try {seCampo = FuncoesGlobais.join(eCampo, ";");} catch (Exception ex) {seCampo = "";}
                try {siCampo = FuncoesGlobais.join(iCampo, ";");} catch (Exception ex) {siCampo = "";}
                try {ssCampo = FuncoesGlobais.join(sCampo, ";");} catch (Exception ex) {ssCampo = "";}
                
                // 29/09/2014 11h
                try {waCampo = FuncoesGlobais.join(hCampo, ";");} catch (Exception ex) {waCampo = "";}
                String finalCampos = slCampo + "," + spCampo + "," + sxCampo + "," + srCampo + "," + seCampo + "," + siCampo + "," + ssCampo;
                divaCampo[l] = FuncoesGlobais.ArrayAdd(divaCampo[l], finalCampos);
            }

            boolean sucesso = true;
            for (int l=0;l<gCampo.length;l++) {
                String sADMCPOS = "";
                for (int p=0;p<gCampo[l].length - 1;p++) {
                    if (LerValor.StringToFloat(gCampo[l][p]) != 0) {
                        String sSql = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.',' ')";

                        String par1 = "GG";
                        String par2 = "GG:9:" +
                                      FuncoesGlobais.GravaValores(gCampo[l][p],2) +
                                      ":000000:GG:ET:" +
                                      FuncoesGlobais.StrZero(nAut.trim(), 6) +
                                      VariaveisGlobais.cContas.get("GG") +
                                      FuncoesGlobais.Choose(p + 1,new String[] {"", "MU", "JU", "CO", "EP"}) +
                                      ":" + Dates.DateFormata("yyyyMMdd", new Date()) +
                                      ":CRE:DN:656877:" +
                                      VariaveisGlobais.usuario;
                        String par3 = Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy"));
                        String par4 = Dates.DateFormata("yyyy-MM-dd", new Date());
                        String par5 = nAut;
                        sSql = FuncoesGlobais.Subst(sSql, new String[] {par1, par2, par3, par4, par5});
                        
                        try {
                            // Grava no Razao
                            conn.ExecutarComando(sSql);
                        } catch (Exception e) { sucesso = false; }
                        
                        sADMCPOS += FuncoesGlobais.Choose(p + 1, new String[] {"", "MU", "JU", "CO", "EP"}) + FuncoesGlobais.GravaValores(gCampo[l][p].replace(".", ","),2) + ":";
                    }
                }
                if (!"".equals(sADMCPOS)) {
                    sADMCPOS = sADMCPOS.substring(0, sADMCPOS.length() - 1);
                    conn.CreateArqAux();
                    String sSql = "INSERT INTO auxiliar (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, conta) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.')";
                    String[] variavel = new String[] {contrato, rgprp, rgimv, sADMCPOS,
                           Dates.DateFormata("yyyy-MM-dd",Dates.StringtoDate(gCampo[l][gCampo[l].length - 1],"dd/MM/yyyy")),
                           Dates.DateFormata("yyyy-MM-dd", new Date()),
                           nAut,
                           "ADM"};
                    sSql = FuncoesGlobais.Subst(sSql, variavel);
                    
                    try {
                        conn.ExecutarComando(sSql);
                    } catch (Exception e) { sucesso = false; }
                }
            }

            for (int l=0;l<divaCampo.length;l++) {
                // Splitar Itens do DivaCampos Elemento (4)
                String[] divaSplit = divaCampo[l][4].split(",");

                // gravar no arquivo auxiliar
                conn.CreateArqAux();
                String sSql = "INSERT INTO auxiliar (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, conta) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.')";
                sSql = FuncoesGlobais.Subst(sSql, new String[] {
                       divaCampo[l][0],
                       divaCampo[l][1],
                       divaCampo[l][2],
                       divaCampo[l][3],
                       Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                       Dates.DateFormata("yyyy-MM-dd", new Date()), nAut, "REC"});
                
                try {
                    conn.ExecutarComando(sSql);
                } catch (Exception e) { sucesso = false; }

                // gravar gravar razao na tabela
                if (!"".equals(divaSplit[3].trim())) {
                    sSql = "INSERT INTO razao (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.', ' ')";
                    String[] par1 = {divaCampo[l][0],
                                     divaCampo[l][1],
                                     divaCampo[l][2],
                                     divaSplit[3],
                                     Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                     Dates.DateFormata("yyyy-MM-dd", new Date()),
                                     nAut};
                    sSql = FuncoesGlobais.Subst(sSql, par1);
                    
                    try {
                        conn.ExecutarComando(sSql);
                    } catch (Exception e) { sucesso = false; }
                }

                // gravar extrato na tabela
                if (!"".equals(divaSplit[4].trim())) {
                    sSql = "INSERT INTO extrato (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.', ' ')";
                    String[] par1 = {divaCampo[l][0],
                                     divaCampo[l][1],
                                     divaCampo[l][2],
                                     divaSplit[4],
                                     Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                     Dates.DateFormata("yyyy-MM-dd", new Date()),
                                     nAut};
                    sSql = FuncoesGlobais.Subst(sSql, par1);
                    
                    try {
                        conn.ExecutarComando(sSql);
                    } catch (Exception e) { sucesso = false; }
                }

                // Grava imposto na tabela
                if (!"".equals(divaSplit[5].trim())) {
                    sSql = "INSERT INTO imposto (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.',' ')";
                    String[] par1 = {divaCampo[l][0],
                                     divaCampo[l][1],
                                     divaCampo[l][2],
                                     divaSplit[5],
                                     Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                     Dates.DateFormata("yyyy-MM-dd", new Date()),
                                     nAut};
                    sSql = FuncoesGlobais.Subst(sSql, par1);
                    
                    try {
                        conn.ExecutarComando(sSql);
                    } catch (Exception e) { sucesso = false; }
                }

                // Grava Retenções na tabela
                if (divaSplit.length > 6) {
                    String[] retencaoSplit = divaSplit[6].split(";");
                    for (int n=0;n<retencaoSplit.length;n++) {
                        sSql = "INSERT INTO retencao (contrato, rgprp, rgimv, campo, vencimento, rc_aut, tag, gat, rt_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.','&9.')";
                        String[] par1 = {divaCampo[l][0],
                                         divaCampo[l][1],
                                         divaCampo[l][2],
                                         retencaoSplit[n],
                                         Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                         nAut,"0"," ","0"};
                        sSql = FuncoesGlobais.Subst(sSql, par1);
                        
                        try {
                            conn.ExecutarComando(sSql);
                        } catch (Exception e) { sucesso = false; }
                    }
                }
            }
            
            // Atualiza Antecipados 29/09/2014
            for (int h=0;h<jCampo.length;h++) {
                if (jCampo[h][jCampo[h].length - 1].equalsIgnoreCase("AT")) {
                    if (Dates.isDateValid(jCampo[h][4], "ddMMyyyy")) {
                        String sSql = "UPDATE ANTECIPADOS SET dtrecebimento = '&1.', rc_aut = '&2.' WHERE contrato = '&3.' AND dtvencimento = '&4.' AND Mid(campo,1,2) = '&5.';";
                        sSql = FuncoesGlobais.Subst(sSql, new String[] {
                                Dates.DateFormata("yyyy-MM-dd", new Date()),
                            nAut, contrato, Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(jCampo[h][4], "ddMMyyyy")), jCampo[h][0]});
                        
                        System.out.println(sSql);
                        try {
                            conn.ExecutarComando(sSql);
                        } catch (Exception e) { sucesso = false; }
                    }
                }
            }
            
        //}
        return sucesso;
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
                        //rCampos[iMulta] = "MU" + FuncoesGlobais.GravaValores(String.valueOf(tbMulta).replace(".", ","), 2);
                        rCampos[iMulta] = "MU" + FuncoesGlobais.GravaValores(LerValor.FloatToString(BigDecimal.valueOf(tbMulta).setScale(2, java.math.BigDecimal.ROUND_HALF_DOWN ).floatValue()), 2);
                    } else {
                        //rCampos = FuncoesGlobais.ArrayAdd(rCampos, "MU" + FuncoesGlobais.GravaValores(String.valueOf(tbMulta).replace(".", ","), 2));
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "MU" + FuncoesGlobais.GravaValores(LerValor.FloatToString(BigDecimal.valueOf(tbMulta).setScale(2, java.math.BigDecimal.ROUND_HALF_DOWN ).floatValue()), 2));
                    }

                    int iJuros = FuncoesGlobais.IndexOf(rCampos,"JU");
                    if (iJuros > -1) {
                        //rCampos[iJuros] = "JU" + FuncoesGlobais.GravaValores(String.valueOf(tbJurosxt).replace(".", ","), 2);
                        rCampos[iJuros] = "JU" + FuncoesGlobais.GravaValores(LerValor.FloatToString(BigDecimal.valueOf(tbJurosxt).setScale(2, java.math.BigDecimal.ROUND_HALF_DOWN ).floatValue()), 2);
                    } else {
                        //rCampos = FuncoesGlobais.ArrayAdd(rCampos, "JU" + FuncoesGlobais.GravaValores(String.valueOf(tbJurosxt).replace(".", ","), 2));
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "JU" + FuncoesGlobais.GravaValores(LerValor.FloatToString(BigDecimal.valueOf(tbJurosxt).setScale(2, java.math.BigDecimal.ROUND_HALF_DOWN ).floatValue()), 2));
                    }

                    int iCorrecao = FuncoesGlobais.IndexOf(rCampos,"CO");
                    if (iCorrecao > -1) {
                        //rCampos[iCorrecao] = "CO" + FuncoesGlobais.GravaValores(String.valueOf(tbCorrecao).replace(".", ","), 2);
                        rCampos[iCorrecao] = "CO" + FuncoesGlobais.GravaValores(LerValor.FloatToString(BigDecimal.valueOf(tbCorrecao).setScale(2, java.math.BigDecimal.ROUND_HALF_DOWN ).floatValue()), 2);
                    } else {
                        //rCampos = FuncoesGlobais.ArrayAdd(rCampos, "CO" + FuncoesGlobais.GravaValores(String.valueOf(tbCorrecao).replace(".", ","), 2));
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "CO" + FuncoesGlobais.GravaValores(LerValor.FloatToString(BigDecimal.valueOf(tbCorrecao).setScale(2, java.math.BigDecimal.ROUND_HALF_DOWN ).floatValue()), 2));
                    }

                    int iExp = FuncoesGlobais.IndexOf(rCampos,"EP");
                    if (iExp > -1) {
                        //rCampos[iExp] = "EP" + FuncoesGlobais.GravaValores(String.valueOf(tbTaxa).replace(".", ","), 2);
                        rCampos[iExp] = "EP" + FuncoesGlobais.GravaValores(LerValor.FloatToString(BigDecimal.valueOf(tbTaxa).setScale(2, java.math.BigDecimal.ROUND_HALF_DOWN ).floatValue()), 2);
                    } else {
                        //rCampos = FuncoesGlobais.ArrayAdd(rCampos, "EP" + FuncoesGlobais.GravaValores(String.valueOf(tbTaxa).replace(".", ","), 2));
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "EP" + FuncoesGlobais.GravaValores(LerValor.FloatToString(BigDecimal.valueOf(tbTaxa).setScale(2, java.math.BigDecimal.ROUND_HALF_DOWN ).floatValue()), 2));
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
                if (!jtgbAvulso.isSelected()) {
                    jTotalRecibos.setValue(totRecibos);
                    jResto.setValue(totRecibos);
                } else {
                    jTotalRecibos.setValue(jVrRecibo.getValue());
                    jResto.setValue(jVrRecibo.getValue());
                }
                tRec.vrAREC = LerValor.StringToFloat(jResto.getText());

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
            String tcampo = IPTU(vecto, pResult.getString("campo"));
            DepuraCampos a = new DepuraCampos(tcampo);
            VariaveisGlobais.ccampos = tcampo;

//            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
//            VariaveisGlobais.ccampos = pResult.getString("campo");

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
//        int at = 18; int llg = 150 - (VariaveisGlobais.bShowCotaParcela ? 50 : 0); int ltf = 90; int lcp = 60; int lcc = 218;
//        int top = 5; int left = 5;

        JLabel lb = new JLabel();
        lb.setText(aCampos[0]);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(defaults.getColor("Label.Foreground")); //Color.BLACK);
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
            cp.setForeground(new Color(0,128,0));
            cp.setDisabledTextColor(defaults.getColor("FormattedTextField.Foreground")); //new Color(0,128,0));
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
//        int at = 18; int llg = 150 + (VariaveisGlobais.bShowCotaParcela ? 10 : 0); int ltf = 90; int lcp = 60; int lcc = 278;
//        int top = 5; int left = 5;

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

    private void ExtornaRecibo(String nAut) {
        try {
            String sql = "";
            sql = FuncoesGlobais.Subst("DELETE FROM Cheques WHERE ch_autenticacao = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM razao WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM retencao WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM imposto WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM extrato WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("UPDATE RECIBO SET TAG = ' ', AUTENTICACAO = '0' WHERE AUTENTICACAO = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);        
        } catch (Exception e) {}
    }
    
    public void Imprimir() throws FileNotFoundException, IOException, SQLException {
        float nAut = 0;
        float vias = LerValor.StringToFloat(jQtde.getValue().toString());
        float nvias;
        boolean sucesso = true;
        
        String[][] aTrancicao = tRec.Transicao("RC");
        if (aTrancicao.length <= 0 ) return;

        if (jchb1Via.isSelected()) {nvias = 1;} else {nvias = VariaveisGlobais.nviasRecibo;} // pegar no parametros quantidade de vias que a imobiliaria imprime
        if (!jtgbAvulso.isSelected()) {
            //nAut = SubRecibo(vias,nvias,aTrancicao,nAut);
            nAut = SubRecibo2(vias,nvias,aTrancicao,nAut);
        } else {
            // Autenticacao
            nAut = (float)Autenticacao.getAut();
            if (!Autenticacao.setAut((double)nAut, 1)) {
                JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            //nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));
            //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

            float rc = LerValor.StringToFloat(jVrRecibo.getText());

            // imprime recibo
            for (int v=1;v<=nvias;v++) {
                if (VariaveisGlobais.Recibo.split(",")[1].equalsIgnoreCase("TXT")) {
                    ImprimeRecibo(nAut, aTrancicao, LerValor.floatToCurrency(rc, 2), "F",v);
                } else {
                    ImprimeReciboPDF(nAut, aTrancicao, LerValor.floatToCurrency(rc, 2), "F",v);
                }
            }

            try {
                conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, aTrancicao, LerValor.FloatToString((int)nAut).replace(",0", ""));
            } catch (Exception e) { sucesso = false; }

            if (sucesso) {
                sucesso = Distribuicao(LerValor.FloatToString((int)nAut).replace(",0", ""), jVencimentos.getSelectedItem().toString());
            }
            if (!sucesso) {
                ExtornaRecibo(LerValor.FloatToString((int)nAut).replace(",0", ""));
            }
        }

        if (nAut > -1 && sucesso) {
            // Elimina os recibos
            if (!jtgbAvulso.isSelected()) {
                for (int j=0;j<vias;j++) {
                    jVencimentos.setSelectedIndex(j);
                    String sql = "UPDATE RECIBO SET tag = 'X', AUTENTICACAO = '" + LerValor.FloatToString((int)nAut).replace(",0", "") + "' WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(), "dd/MM/yyyy")) + "';";
                    
                    try {
                        conn.ExecutarComando(sql);
                    } catch (Exception e) { sucesso = false; }
                }
            } else {
                String sql = "UPDATE RECIBO SET tag = 'X', AUTENTICACAO = '" + LerValor.FloatToString((int)nAut).replace(",0", "") + "' WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(), "dd/MM/yyyy")) + "';";
                
                try {
                    conn.ExecutarComando(sql);
                } catch (Exception e) { sucesso = false; }
            }

            if (!sucesso) { ExtornaRecibo(LerValor.FloatToString((int)nAut).replace(",0", "")); } 
            
            try {
                String sql = "UPDATE CARTEIRA SET dtultrecebimento = '" + Dates.DateFormata("dd-MM-yyyy",Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd-MM-yyyy")) + "' WHERE contrato = '" + contrato + "';";
                conn.ExecutarComando(sql);
            } catch (Exception e) { 
                sucesso = false;
                e.printStackTrace(); 
            }
            
            
        } else if (!sucesso) {
            JOptionPane.showMessageDialog(null, "Banco de dados ocupado!!!\nOperação sem sucesso...\nInultilize o Recibo e tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
        }
    }

    private float SubRecibo2(float vias, float nvias, String[][] aTrancicao, float nAut) {
        float nret = -1;
        try {
            nAut = (float)Autenticacao.getAut();
            //nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));
            for (int i=0;i<vias;i++) {
                jVencimentos.setSelectedIndex(i);
                
                float rc = LerValor.StringToFloat(jVrRecibo.getText());
                String[][] lcto =  null;
                lcto = aTrancicao;
                
                if (i != vias - 1) {
                    //lcto = new String[][] {{"","","","",String.valueOf(rc),"CT","CRE","RC","VARIOS_RECIBOS"}};
                    lcto[lcto.length - 1][8] = "VARIOS_RECIBOS";
                } else { lcto[lcto.length - 1][8] = ""; }
                //new String[][] {{"","","","",String.valueOf(rc),aTrancicao[0][5],aTrancicao[0][6],aTrancicao[0][7],aTrancicao[0][8]}};
                
                for (int v=1;v<=nvias;v++) {
                    if (VariaveisGlobais.Recibo.split(",")[1].equalsIgnoreCase("TXT")) {
                        ImprimeRecibo(nAut, lcto,LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"),v);
                    } else {
                        ImprimeReciboPDF(nAut, lcto,LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"),v);
                    }
                }
                
                if (i == vias - 1) {
                    conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, lcto,LerValor.FloatToString((int)nAut).replace(",0", ""));
                    if (!Autenticacao.setAut((double)nAut, 1)) {
                        JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        return -1;
                    }
                    //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});
                }
                boolean sucesso = Distribuicao(LerValor.FloatToString((int)nAut).replace(",0", ""),jVencimentos.getSelectedItem().toString());
                if (!sucesso) { 
                    ExtornaRecibo(LerValor.FloatToString((int)nAut).replace(",0", ""));
                    JOptionPane.showMessageDialog(null, "Banco de dados ocupado!!!\nOperação sem sucesso...\nInultilize o Recibo e tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                }
            }
            nret = nAut;
        } catch (Exception e) {e.printStackTrace();}
        return nret;
    }
    
    private float SubRecibo(float vias, float nvias, String[][] aTrancicao, float nAut) {
        try {
            for (int i=0;i<vias;i++) {
                jVencimentos.setSelectedIndex(i);

                float dn = 0; int c = 0; int inc = 0;
                if ("".equals(aTrancicao[0][1].trim())) {dn = Float.valueOf(aTrancicao[0][4]); c=0;} else {dn=0; c=-1;}
                float rc = LerValor.StringToFloat(jVrRecibo.getText());
                float ch = LerValor.StringToFloat(LerValor.floatToCurrency(0,2));

                inc = c;
                String[][] especies = {};

                if (LerValor.StringToFloat(LerValor.floatToCurrency(rc,2)) <= LerValor.StringToFloat(LerValor.floatToCurrency(dn,2))) {
                    dn = LerValor.StringToFloat(LerValor.floatToCurrency(dn - rc,2));
                    aTrancicao[0][4] = String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(dn, 2)));

                    // imprime recibo
                    nAut = (float)Autenticacao.getAut();
                    //nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));
                    String[][] lcto = new String[][] {{"","","","",String.valueOf(rc),aTrancicao[0][5],aTrancicao[0][6],aTrancicao[0][7],aTrancicao[0][8]}};
                    for (int v=1;v<=nvias;v++) {
                        if (VariaveisGlobais.Recibo.split(",")[1].equalsIgnoreCase("TXT")) {
                            ImprimeRecibo(nAut, lcto,LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"),v);
                        } else {
                            ImprimeReciboPDF(nAut, lcto,LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"),v);
                        }
                    }
                    conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, lcto,LerValor.FloatToString((int)nAut).replace(",0", ""));
                    if (!Autenticacao.setAut((double)nAut, 1)) {
                        JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        return -1;
                    }
                    //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});
                    
                    boolean sucesso = Distribuicao(LerValor.FloatToString((int)nAut).replace(",0", ""),jVencimentos.getSelectedItem().toString());
                    if (!sucesso) { 
                        ExtornaRecibo(LerValor.FloatToString((int)nAut).replace(",0", ""));
                        JOptionPane.showMessageDialog(null, "Banco de dados ocupado!!!\nOperação sem sucesso...\nInultilize o Recibo e tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                    }
                   
                } else {
                    // Autenticacao
                    nAut = (float)Autenticacao.getAut();
                    if (!Autenticacao.setAut((double)nAut, 1)) {
                        JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        return -1;
                    }
                    //nAut = LerValor.StringToFloat(conn.LerParametros("AUTENTICACAO"));
                    //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

                    if (LerValor.StringToFloat(LerValor.floatToCurrency(dn,2)) > 0) { especies = FuncoesGlobais.ArraysAdd(especies, new String[] {"","","","",String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(dn, 2))),aTrancicao[0][5],aTrancicao[0][6],aTrancicao[0][7],aTrancicao[0][8]});}

                    while (c<aTrancicao.length) {
                        c++;
                        ch += Float.valueOf(aTrancicao[c][4]);

                        if (LerValor.StringToFloat(LerValor.floatToCurrency(rc,2)) <= LerValor.StringToFloat(LerValor.floatToCurrency(dn + ch,2))) {
                            float vrch = LerValor.StringToFloat(LerValor.floatToCurrency(0,2));
                            if (LerValor.StringToFloat(LerValor.floatToCurrency(dn,2)) > 0) {
                                vrch = LerValor.StringToFloat(LerValor.floatToCurrency(rc - dn,2));
                            } else {
                                vrch = Float.valueOf(aTrancicao[c][4]) - (LerValor.StringToFloat(LerValor.floatToCurrency(ch - rc,2)));
                            }
                            //especies = FuncoesGlobais.ArraysAdd(especies, new String[] {aTrancicao[c][0],aTrancicao[c][1],aTrancicao[c][2],aTrancicao[c][3],LerValor.floatToCurrency(vrch, 2),aTrancicao[c][5],aTrancicao[c][6],aTrancicao[c][7],aTrancicao[c][8]});
                            if (LerValor.StringToFloat(LerValor.floatToCurrency(vrch + dn,2)) >= rc) {
                                especies = FuncoesGlobais.ArraysAdd(especies, new String[] {aTrancicao[c][0],aTrancicao[c][1],aTrancicao[c][2],aTrancicao[c][3],aTrancicao[c][4],aTrancicao[c][5],aTrancicao[c][6],aTrancicao[c][7],aTrancicao[c][8]});
                            } else {
                                especies = FuncoesGlobais.ArraysAdd(especies, new String[] {aTrancicao[c][0],aTrancicao[c][1],aTrancicao[c][2],aTrancicao[c][3],String.valueOf(vrch),aTrancicao[c][5],aTrancicao[c][6],aTrancicao[c][7],aTrancicao[c][8]});
                            }

                            // imprime dn,ch
                            for (int v=1;v<=nvias;v++) {
                                if (VariaveisGlobais.Recibo.split(",")[1].equalsIgnoreCase("TXT")) {
                                    ImprimeRecibo(nAut, especies, LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"), v);
                                } else {
                                    ImprimeReciboPDF(nAut, especies, LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"), v);
                                }
                            }
                                
                            conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, especies,LerValor.FloatToString((int)nAut).replace(",0", ""));
                            
                            boolean sucesso = Distribuicao(LerValor.FloatToString((int)nAut).replace(",0", ""),jVencimentos.getSelectedItem().toString());
                            if (!sucesso) { 
                                ExtornaRecibo(LerValor.FloatToString((int)nAut).replace(",0", "")); 
                                JOptionPane.showMessageDialog(null, "Banco de dados ocupado!!!\nOperação sem sucesso...\nInultilize o Recibo e tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                            }
                            
                            ch = LerValor.StringToFloat(LerValor.floatToCurrency(ch,2)) - (LerValor.StringToFloat(LerValor.floatToCurrency(rc - dn,2)));

                            // zera dinheiro
                            if (dn > 0) aTrancicao[0][4] = "0.00";

                            if (ch > 0) {
                                aTrancicao[c][4] = String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(ch, 2)));
                            } else {
                                aTrancicao = FuncoesGlobais.ArraysDel(aTrancicao, c);
                            }
                            break;
                        } else {
                            especies = FuncoesGlobais.ArraysAdd(especies, new String[] {aTrancicao[c][0],aTrancicao[c][1],aTrancicao[c][2],aTrancicao[c][3],aTrancicao[c][4],aTrancicao[c][5],aTrancicao[c][6],aTrancicao[c][7],aTrancicao[c][8]});
                            aTrancicao = FuncoesGlobais.ArraysDel(aTrancicao, c); c=inc;
                        }
                    }

                }
             }        
        } catch (Exception e) {}
        return nAut;
    }
    
    private float CamposSomadosAoAluguel() {
        float totasomar = 0; 
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

        // 22-05-2012 -  via gambiarra - somente na 1a via
        String aregras[][] = {};
        try { aregras = FuncoesGlobais.treeRegra(regras, true); } catch (Exception ex) {}
        
        int i = 0; int maxcpos = jctCampos.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = ""; int newLine = 0;
        String posAnt = ""; String saida_final = ""; 
        if (maxcpos > 1) {
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
                            //alinha = new Pad("       ", 7).LPad();
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

                saida_final += saida;
                if (newLine == 1) {
                    boolean achei = false; 
                    int pos = FuncoesGlobais.FindInRegras(aregras, 3, "Desc",0,4);
                    if (saida_final.contains("Desc") && pos > -1 && aregras[pos][1].equals("1") && aregras[pos][2].equals("1")) {
                        achei = true; 
                    }

                    pos = FuncoesGlobais.FindInRegras(aregras, 3, "Dife",0,4);
                    if (saida_final.contains("Dife") && pos > -1 && aregras[pos][1].equals("1") && aregras[pos][2].equals("1")) {
                        achei = true;
                    }

                    if (!achei) {
                        for (int z=0;z<aregras.length;z++) {
                            if (saida_final.trim().toLowerCase().contains(aregras[z][3].trim().toLowerCase())) {
                                if (aregras[z][1].equals("1") && aregras[z][2].equals("1")) {
                                    achei = true; break;
                                }
                            }
                        }
                    }
                    if (achei) {
                        int xpos = FuncoesGlobais.RAt(saida_final," ");
                        String mvalor = saida_final.substring(xpos, saida_final.length());
                        totasomar += LerValor.StringToFloat(mvalor);
                    }
                    saida_final = "";
                }
            }        
        }
        
        return totasomar;
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
                            valor = LerValor.FormatNumber(rCampos[0][2], 2);
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

    @SuppressWarnings("empty-statement")
    public void ImprimeRecibo(float nAut, String[][] Valores, String ValorRec, String cutPaper, int via) {
      int i = 0;
      ResultSet hResult;
      Collections gVar = VariaveisGlobais.dCliente;

      // hResult = FuncoesGlobais.OpenTransicao("RC")
      String docID = "_" + contrato + "_VIA-" + FuncoesGlobais.StrZero(String.valueOf(via).trim(), 2) + ".ps";
      String docName = "reports/Recibos/" + "RC_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;
      PrinterPOS printer = new PrinterPOS(docName);
      //printer.setDocName(docName);

      // Imprime logomarca          
      printer.Print(JavaPOS.ESC_ARROBA + "", 1);
      /////////////////////printer.PrintBitMap("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif",0);
      ///////////////printer.Print("", 1);

      // cabeçário
      printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_E(1) + gVar.get("empresa"), 1);

      if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
          if (!gVar.get("tipodoc").equalsIgnoreCase("") || gVar.get("tipodoc") != null) {
              printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_E(1) + gVar.get("tipodoc") + ": " + gVar.get("cnpj"), 1);
          }
      }
      
      printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), 1);
      printer.Print(JavaPOS.ESCLAMATION(65) + gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), 1);
      printer.Print(JavaPOS.ESCLAMATION(65) + "Tel/Fax:" + gVar.get("telefone") + JavaPOS.ESC_E(0), 1);
      printer.Print("", 1);

      printer.Print(JavaPOS.ESCLAMATION(70) + JavaPOS.ESC_A(1) + (nAut >0 ? gVar.get("recibo") : "DEMONSTRATIVO DE ALUGUEL") + JavaPOS.NORMAL, 1);
      printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "", 1);

      // Dados do locatario
      printer.Print(JavaPOS.ESCLAMATION(65) + "Locatario: ", 0);
      printer.Print(JavaPOS.ESCLAMATION(65) + contrato + "  ",0);
      printer.Print(JavaPOS.ESCLAMATION(65) + jNomeLoca.getSelectedItem().toString(),1);
      printer.Print("",1);

      // Dados do Imóvel
      printer.Print(JavaPOS.ESCLAMATION(65) + "Imovel: ",0);
      printer.Print(JavaPOS.ESCLAMATION(65) + jRgimv.getText(),0);
      printer.Print(JavaPOS.ESCLAMATION(65) + "  Vencimento: ",0);
      printer.Print(JavaPOS.ESCLAMATION(65) + jVencimentos.getSelectedItem().toString(),1);
      printer.Print(JavaPOS.ESCLAMATION(65) + jEndereco.getText(),1);
      printer.Print(JavaPOS.ESCLAMATION(65) + jBairro.getText() + " - " + jCidade.getText(),1);
      printer.Print(JavaPOS.ESCLAMATION(65) + jEstado.getText(), 0);
      printer.Print(JavaPOS.ESCLAMATION(65) + " - Cep: " + jCep.getText(),1);
      printer.Print("",1);

      printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "DESCRIMINACAO                                      VALOR",1);
      printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);

      // 22-05-2012
      float vrrecibo = ListaCamposRecibo(printer, (nAut == -1 ? 1 : via));
      if (via == 1) ValorRec = LerValor.floatToCurrency(vrrecibo, 2);
      printer.Print("",1);
      printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(2) + "Total do Recibo......................  ",0);
      if (via > 1) {
        printer.Print(JavaPOS.ESCLAMATION(65) + jVrRecibo.getText(),1);
      } else {
        printer.Print(JavaPOS.ESCLAMATION(65) + LerValor.floatToCurrency(vrrecibo, 2), 1);
      }

      printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + "",1);
      printer.Print(JavaPOS.ESCLAMATION(65) + "Prop: " + rgprp + "  ",0);
      printer.Print(JavaPOS.ESCLAMATION(65) + jNomeProp.getText(),1);
      if (VariaveisGlobais.impPropDiv) {
          String[][] div = {};
          try {div = conn.LerCamposTabela(new String[] {"benefs"}, "divisao", "rgprp = '" + rgprp + "' AND rgimv = '" + rgimv + "'");} catch (SQLException ex) {}
          if (div != null) {
              if (div.length > 0) {
                  for (int q=0;q<div.length;q++) {
                      String[] cpos = div[q][3].split(";");
                      for (int z=0; z<cpos.length;z++) {
                          String[] scpos = cpos[z].split(":");
                          if (scpos.length > 0) {
                            printer.Print(JavaPOS.ESCLAMATION(65) + "Prop: " + scpos[0] + "  ",0);
                            String[][] nmProDiv = {};
                            try {nmProDiv = conn.LerCamposTabela(new String[] {"nome"}, "proprietarios", "rgprp = '" + scpos[0] + "'");} catch (SQLException ex) {}
                            if (nmProDiv.length > 0) printer.Print(nmProDiv[0][3],1);
                          }
                      }
                  }
              }
          }
      }

      // Mensagem do Recibo
      String msgem = "";
      try {
           try { msgem = conn.LerCamposTabela(new String[] {"msgboleta"}, "locatarios", "contrato = '" + contrato + "'")[0][3];} catch (SQLException ex) {msgem = "";}
           if (!msgem.trim().equalsIgnoreCase("")) {
               printer.Print("",1);
               printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) + "=== MENSAGEM ===" + JavaPOS.NORMAL, 1);
               printer.Print(JavaPOS.ESCLAMATION(65) + msgem,1);
               printer.Print("",1);
               
               String msg = ChecaTermino(contrato);
               if (!msg.trim().equals("")) {
                   printer.Print(JavaPOS.ESCLAMATION(65) + msg,1);
               }   
           }
      } catch (Exception e) {}

        printer.Print(JavaPOS.ESCLAMATION(65) + "========================================================",1);

        if (nAut > 0) {
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) + "VALOR(ES) LANCADOS" + JavaPOS.NORMAL, 1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) +  "--------------------------------------------------------" + JavaPOS.NORMAL,1);

            for (i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    if (VariaveisGlobais.showrecvalores) {
                      bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(LerValor.floatToCurrency(Float.valueOf(Valores[i][4]),2),10).LPad();
                    } else {
                      bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad();
                    }
                } else {
                    if (VariaveisGlobais.showrecvalores) {
                      bLinha = Valores[i][5].trim() + ":" + new Pad(LerValor.floatToCurrency(Float.valueOf(Valores[i][4]),2),10).LPad();
                    } else {
                      bLinha = Valores[i][5].trim() + ":";
                    }
                }

                printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(2) + bLinha, 1);
            }

            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.GS_EXC(0) + JavaPOS.ESC_A(0) + "",1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) + "Este recibo nao quita debitos anteriores." + JavaPOS.NORMAL, 1);
            printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(1) + "",1);

            // Imprimir Autenticação
            printer.Print(JavaPOS.ESC_A(1) + JavaPOS.ESCLAMATION(65) + JavaPOS.REVERSO + VariaveisGlobais.dCliente.get("marca").trim() + JavaPOS.NORMAL + "RC" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + " " + JavaPOS.ESC_E(1) + FuncoesGlobais.GravaValores(ValorRec, 2) + JavaPOS.NORMAL + " " + VariaveisGlobais.usuario,1);
            // Codigo de barra
            //////////////////////////printer.PrintBarCode(FuncoesGlobais.StrZero(String.valueOf((int)nAut), 12));
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { printer.Print(JavaPOS.ESCLAMATION(65) + "", 1); }

        if ("P".equals(cutPaper.trim().toUpperCase())) { printer.Print(JavaPOS.PARTCUT, 0); } else { printer.Print(JavaPOS.FULLCUT, 0); }
      
        //printer.Close();
        new toPrint(docName, VariaveisGlobais.Recibo.split(",")[0],VariaveisGlobais.Recibo.split(",")[1],VariaveisGlobais.Recibo.split(",")[2]);
        printer.setDocName("");
    }

    private float ListaCamposRecibo(PrinterPOS printer, int via) {
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

        // 22-05-2012 -  via gambiarra - somente na 1a via
        String aregras[][] = {};
        try { aregras = FuncoesGlobais.treeRegra(regras, true); } catch (Exception ex) {}
        
        int i = 0; int maxcpos = jctCampos.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = ""; int newLine = 0;
        String posAnt = ""; String saida_final = ""; int linha = 1;
        float totalrecibo = 0; float vdesconto = 0;
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
                        //alinha = new Pad("       ", 7).LPad();
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
            } else { saida = ""; newLine = 0;}

            saida_final += saida;
            if (newLine == 1) {
                if (via > 1) {
                    printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + saida_final, newLine);
                } else {
                    boolean achei = false; boolean soma = true;
                    int pos = FuncoesGlobais.FindInRegras(aregras, 3, "Desc",0,4);
                    if (saida_final.contains("Desc") && pos > -1 && aregras[pos][1].equals("1")) {
                        if (!aregras[pos][2].equals("1")) achei = true;
                        int xpos = FuncoesGlobais.RAt(saida_final," ");
                        vdesconto += LerValor.StringToFloat(saida_final.substring(xpos, saida_final.length()));
                        soma = false; 
                    }
                    
                    pos = FuncoesGlobais.FindInRegras(aregras, 3, "Dife",0,4);
                    if (saida_final.contains("Dife") && pos > -1 && aregras[pos][1].equals("1")) {
                        if (!aregras[pos][2].equals("1")) achei = true;
                    }

                    if (!achei) {
                        for (int z=0;z<aregras.length;z++) {
                            if (saida_final.trim().toLowerCase().contains(aregras[z][3].trim().toLowerCase())) {
                                if (aregras[z][1].equals("1")) {
                                    if (!aregras[z][2].equals("1")) {achei = true; break;}
                                }
                            }
                        }
                    }
                    if (!achei) newLine = -1;
                    if (newLine > -1) {
                        if (linha == 1) {
                            // aqui processa o acumulo na linha 1
                            float somar = CamposSomadosAoAluguel();
                            int xpos = FuncoesGlobais.RAt(saida_final," ");
                            String mvalor = saida_final.substring(xpos, saida_final.length());
                            float fvalor = LerValor.StringToFloat(mvalor) + somar;
                            String svalor = LerValor.floatToCurrency(fvalor,2).trim();                            
                            int newpos = saida_final.length() - svalor.length();
                            saida_final = saida_final.substring(0, newpos) + svalor;
                        }
                        printer.Print(JavaPOS.ESCLAMATION(65) + JavaPOS.ESC_A(0) + saida_final, newLine);
                        linha += 1;
                        
                        // gera novo total do recibo para 1a via
                        int xpos = FuncoesGlobais.RAt(saida_final," ");
                        String mvalor = saida_final.substring(xpos, saida_final.length());
                        float fvalor = LerValor.StringToFloat(mvalor);
                        if (soma) totalrecibo += fvalor; 
                    }
                }
                saida_final = "";
            }
        }
        
        return totalrecibo - vdesconto;
    }    

   public void ImprimeReciboPDF(float nAut, String[][] Valores, String ValorRec, String cutPaper, int via) {
        float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();

        String docID = "_" + contrato + ".pdf";
        String docName = "RC_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + (nAut == -1 ? "" : "-" + via) + docID;
        pdf.setPathName("reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
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
        p = pdf.print((nAut >0 ? gVar.get("recibo") : "D E M O N S T R A T I V O"), pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
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

        // Dados do locatario
        columnWidths = new float[] {35, 65 };
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        cell1 = new PdfPCell(new Phrase("Locatário: " + contrato,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(jNomeLoca.getSelectedItem().toString()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        cell1 = new PdfPCell(new Phrase("Imóvel: " + jRgimv.getText(),font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("Vencimento: " + jVencimentos.getSelectedItem().toString(),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);
        
        p = pdf.print(StringManager.ConvStr(jEndereco.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(StringManager.ConvStr(jBairro.getText() + " - " + jCidade.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(StringManager.ConvStr(jEstado.getText() + " - Cep: " + jCep.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Cabeçario do recibo
        columnWidths = new float[] {50, 20, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("C/P",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);
        PdfPCell cell3 = new PdfPCell(new Phrase("VALOR", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        columnWidths = new float[] {50, 20, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        Object[] retorno = ListaCamposRecibo_pdf((nAut == -1 ? 1 : via));
        float vrrecibo = Convert.toFloat(retorno[1]); 
        if (via == 1) ValorRec = LerValor.floatToCurrency(vrrecibo, 2);
        
        // Recalculando total do Recibo corretamente
        if (via > 1) {
            Object[][] rLista = (Object[][])retorno[0];
            float rtot = 0;
            for (int i=0; i < rLista.length; i++) {
                if (((String) rLista[i][0]).contains("Desc")) {
                    rtot -= LerValor.StringToFloat((String)rLista[i][2]);
                } else rtot += LerValor.StringToFloat((String)rLista[i][2]);
            }
            ValorRec = LerValor.floatToCurrency(rtot,2);
        }
        
        Object[][] linhas = (Object[][]) retorno[0];
        for (int i=0; i<linhas.length;i++) {
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase((String) linhas[i][0],font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);
            cell2 = new PdfPCell(new Phrase((String) linhas[i][1],font));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);
            cell3 = new PdfPCell(new Phrase((String) linhas[i][2], font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);
        }
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase("==========", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase((via > 1 ? ValorRec : LerValor.floatToCurrency(vrrecibo, 2)), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {35, 65};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Propriet(s): " + rgprp,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.TOP);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(jNomeProp.getText()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.TOP);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        if (VariaveisGlobais.impPropDiv) {
            String[][] div = {};
            try {div = conn.LerCamposTabela(new String[] {"benefs"}, "divisao", "rgprp = '" + rgprp + "' AND rgimv = '" + rgimv + "'");} catch (SQLException ex) {}
            if (div != null) {
                if (div.length > 0) {
                    for (int q=0;q<div.length;q++) {
                        String[] cpos = div[q][3].split(";");
                        for (int z=0; z<cpos.length;z++) {
                            String[] scpos = cpos[z].split(":");
                            if (scpos.length > 0) {
                                String[][] nmProDiv = {};
                                try {nmProDiv = conn.LerCamposTabela(new String[] {"nome"}, "proprietarios", "rgprp = '" + scpos[0] + "'");} catch (SQLException ex) {}
                                try {
                                    font.setColor(BaseColor.BLACK);
                                    cell1 = new PdfPCell(new Phrase("Propriet(s): " + scpos[0],font));
                                    cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cell1.setBorder(Rectangle.NO_BORDER);
                                    cell1.setBackgroundColor(BaseColor.WHITE);
                                    table.addCell(cell1);
                                    cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(nmProDiv[0][3]),font));
                                    cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cell2.setBorder(Rectangle.NO_BORDER);
                                    cell2.setBackgroundColor(BaseColor.WHITE);
                                    table.addCell(cell2);
                                } catch (Exception e) {}
                            }
                        }
                    }
                }
            }
        }
        table.completeRow();
        pdf.doc_add(table);
        
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        // Mensagem do Recibo
        String msgem = "";
        try {
            try { msgem = conn.LerCamposTabela(new String[] {"msgboleta"}, "locatarios", "contrato = '" + contrato + "'")[0][3];} catch (SQLException ex) {msgem = "";}
            if (!msgem.trim().equalsIgnoreCase("")) {
                p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("__________ MENSAGEM __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print(msgem, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
            }
        } catch (Exception e) {}

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

            p = pdf.print("Este recibo não quita qualquer débito anterior.", pdf.HELVETICA, 6, pdf.BOLDITALIC, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + "RC" + 
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
        new toPrint(pdf.getPathName() + docName, VariaveisGlobais.Recibo.split(",")[0],VariaveisGlobais.Recibo.split(",")[1],VariaveisGlobais.Recibo.split(",")[2]);
        pdf.setPathName("");
        pdf.setDocName("");
   }
 
    private Object[] ListaCamposRecibo_pdf(int via) {
        Object[][] linhas = {};
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

        // 22-05-2012 -  via gambiarra - somente na 1a via
        String aregras[][] = {};
        try { aregras = FuncoesGlobais.treeRegra(regras, true); } catch (Exception ex) {}
        
        int i = 0; int maxcpos = jctCampos.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = ""; int newLine = 0;
        String posAnt = ""; String saida_final = ""; int linha = 1;
        float totalrecibo = 0; float vdesconto = 0;
        String pitem = "", pcota = "", pvalor = "";
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
                
                // Nome do item
                pitem = saida.trim();                
            } else if (jctCampos.getComponent(i) instanceof JFormattedTextField) {
                saida = ((JFormattedTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JFormattedTextField) jctCampos.getComponent(i)).getName();
                newLine = 1;

                String alinha = "";
                if (VariaveisGlobais.bShowCotaParcela) {
                    if (nmCampo.toLowerCase().trim().indexOf("cota") > -1) {
                        alinha = new Pad(("00/00".equals(saida.trim()) || "00/0000".equals(saida.trim()) ? "       " : saida), 7).LPad();
                        //alinha = new Pad("       ", 7).LPad();
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
                // Valor do Item
                pvalor = saida.trim();                
            } else if (jctCampos.getComponent(i) instanceof JTextField) {
                saida = ((JTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JTextField) jctCampos.getComponent(i)).getName();
                newLine = 0;
                // Cota/Parcela do Item
                pcota = saida.trim();                
            } else if (jctCampos.getComponent(i) instanceof JCheckBox) {
                boolean simnao = ((JCheckBox) jctCampos.getComponent(i)).isSelected();
                saida = "";
                nmCampo = ((JCheckBox) jctCampos.getComponent(i)).getName();
                newLine = 0;
            } else { saida = ""; newLine = 0; pitem = ""; pcota = ""; pvalor = "";}

            saida_final += saida;
            if (newLine == 1) {
                if (via > 1) {
                    Object[] tmp =  {pitem,pcota,pvalor};
                    linhas =  FuncoesGlobais.ObjectsAdd(linhas, tmp);
                    pitem = ""; pcota = ""; pvalor = "";
                } else {
                    boolean achei = false; boolean soma = true;
                    int pos = FuncoesGlobais.FindInRegras(aregras, 3, "Desc",0,4);
                    if (saida_final.contains("Desc") && pos > -1 && aregras[pos][1].equals("1")) {
                        if (!aregras[pos][2].equals("1")) achei = true;
                        int xpos = FuncoesGlobais.RAt(saida_final," ");
                        vdesconto += LerValor.StringToFloat(saida_final.substring(xpos, saida_final.length()));
                        soma = false; 
                    }
                    
                    pos = FuncoesGlobais.FindInRegras(aregras, 3, "Dife",0,4);
                    if (saida_final.contains("Dife") && pos > -1 && aregras[pos][1].equals("1")) {
                        if (!aregras[pos][2].equals("1")) achei = true;
                    }

                    if (!achei) {
                        for (int z=0;z<aregras.length;z++) {
                            if (saida_final.trim().toLowerCase().contains(aregras[z][3].trim().toLowerCase())) {
                                if (aregras[z][1].equals("1")) {
                                    if (!aregras[z][2].equals("1")) {achei = true; break;}
                                }
                            }
                        }
                    }
                    if (!achei) newLine = -1;
                    if (newLine > -1) {
                        if (linha == 1) {
                            // aqui processa o acumulo na linha 1
                            float somar = CamposSomadosAoAluguel();
                            int xpos = FuncoesGlobais.RAt(saida_final," ");
                            String mvalor = saida_final.substring(xpos, saida_final.length());
                            float fvalor = LerValor.StringToFloat(mvalor) + somar;
                            String svalor = LerValor.floatToCurrency(fvalor,2).trim(); 
                            pvalor = svalor;
                            int newpos = saida_final.length() - svalor.length();
                            saida_final = saida_final.substring(0, newpos) + svalor;
                        }
                        
                        Object[] tmp =  {pitem,pcota,pvalor};
                        linhas =  FuncoesGlobais.ObjectsAdd(linhas, tmp);
                        pitem = ""; pcota = ""; pvalor = "";

                        linha += 1;
                        
                        // gera novo total do recibo para 1a via
                        int xpos = FuncoesGlobais.RAt(saida_final," ");
                        String mvalor = saida_final.substring(xpos, saida_final.length());
                        float fvalor = LerValor.StringToFloat(mvalor);
                        if (soma) totalrecibo += fvalor; 
                    }
                }
                saida_final = "";
            }
        }
        
        Object tgeral = Convert.toObject(totalrecibo - vdesconto);
        return new Object[] {linhas, tgeral};
    }
   
  public void ImprimeReciboThermica(float nAut, String[][] Valores, String ValorRec, String cutPaper, int via) {
        jThermica Printer = new jThermica(linhas);
        int i = 0;
        ResultSet hResult;
        Collections gVar = VariaveisGlobais.dCliente;

        linhas = new Object[][] {};
      
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

        if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
            tmp = new Object[][] {
                                    {gVar.get("tipodoc") + ": " + gVar.get("cnpj"),null,1,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                   };
            linhas =  Printer.adcLine(linhas, tmp);
        }
        
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
                                {(nAut >0 ? gVar.get("recibo") : "D E M O N S T R A T I V O"),null,0,new java.awt.Font("Arial",java.awt.Font.ITALIC + java.awt.Font.BOLD,10), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"CAIXA: " + VariaveisGlobais.usuario,50,1,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE},
                            {"Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),null,2,new java.awt.Font("Arial",java.awt.Font.PLAIN,8), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {null,null,2,null, Color.BLACK, Color.WHITE},
                         };
        linhas =  Printer.adcLine(linhas, tmp);
        
        // Dados do locatario
        tmp = new Object[][] {
                                 {"Locatário: " + contrato,70,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE},
                                 {jNomeLoca.getSelectedItem().toString(),null,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        // Dados do Imóvel
        tmp = new Object[][] {
                              {"Imóvel: " + jRgimv.getText(),70,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE},
                              {"Vencimento: " + jVencimentos.getSelectedItem().toString(),null,2,new Font("Arial",Font.PLAIN,8), Color.BLACK, Color.WHITE}
                           };
        linhas =  Printer.adcLine(linhas, tmp);
        
        tmp = new Object[][] {
                              {jEndereco.getText(),null,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                              {jBairro.getText() + " - " + jCidade.getText(),null,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                              {jEstado.getText() + " - Cep: " + jCep.getText(),null,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                              {"",null,1,new Font("Arial",Font.PLAIN,8), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                              {"DESCRIMINAÇÃO                                                                          ",70,1,new Font("Arial",Font.PLAIN,7), Color.WHITE, Color.BLACK},
                              {"VALOR",null,2,new Font("Arial",Font.PLAIN,7), Color.WHITE, Color.BLACK}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        float vrrecibo = ListaCamposReciboThermica(linhas, (nAut == -1 ? 1 : via), Printer);
        if (via == 1) ValorRec = LerValor.floatToCurrency(vrrecibo, 2);
        
        tmp = new Object[][] {
                            {"============",null,2,new Font("Times New Roman",Font.PLAIN,9), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {"Total do Recibo",100,1,new Font("Times New Roman",Font.BOLD,9), Color.BLACK, Color.WHITE},
                            {(via > 1 ? jVrRecibo.getText() : LerValor.floatToCurrency(vrrecibo, 2)),null,2,new Font("Times New Roman",Font.BOLD,9), Color.BLACK, Color.WHITE}
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                            {null,null,2,null, Color.BLACK, Color.WHITE},
                         };
        linhas =  Printer.adcLine(linhas, tmp);

        tmp = new Object[][] {
                                 {"Propriet(s): " + rgprp,70,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE},
                                 {jNomeProp.getText(),null,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                             };
        linhas =  Printer.adcLine(linhas, tmp);

        if (VariaveisGlobais.impPropDiv) {
            String[][] div = {};
            try {div = conn.LerCamposTabela(new String[] {"benefs"}, "divisao", "rgprp = '" + rgprp + "' AND rgimv = '" + rgimv + "'");} catch (SQLException ex) {}
            if (div != null) {
                if (div.length > 0) {
                    for (int q=0;q<div.length;q++) {
                        String[] cpos = div[q][3].split(";");
                        for (int z=0; z<cpos.length;z++) {
                            String[] scpos = cpos[z].split(":");
                            if (scpos.length > 0) {
                              //printer.Print(JavaPOS.ESCLAMATION(65) + "Prop: " + scpos[0] + "  ",0);
                              String[][] nmProDiv = {};
                              try {nmProDiv = conn.LerCamposTabela(new String[] {"nome"}, "proprietarios", "rgprp = '" + scpos[0] + "'");} catch (SQLException ex) {}
                              //if (nmProDiv.length > 0) printer.Print(nmProDiv[0][3],1);        
                              try {
                                tmp = new Object[][] {
                                                         {"Propriet(s): " + scpos[0],70,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE},
                                                         {nmProDiv[0][3],null,1,new Font("Arial",Font.PLAIN,7), Color.BLACK, Color.WHITE}
                                                     };
                                linhas =  Printer.adcLine(linhas, tmp);
                              } catch (Exception e) {}
                            }
                        }
                    }
                }
            }
        }
          
        // Mensagem do Recibo
        String msgem = "";
        try {
             try { msgem = conn.LerCamposTabela(new String[] {"msgboleta"}, "locatarios", "contrato = '" + contrato + "'")[0][3];} catch (SQLException ex) {msgem = "";}
             if (!msgem.trim().equalsIgnoreCase("")) {
                  tmp = new Object[][] {
                                          {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE}
                                       };
                  linhas =  Printer.adcLine(linhas, tmp);
  
                  tmp = new Object[][] {
                                          {"__________ MENSAGEM __________",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE}
                                       };
                  linhas =  Printer.adcLine(linhas, tmp);
  
                  tmp = new Object[][] {
                                          {msgem,null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE}
                                       };
                  linhas =  Printer.adcLine(linhas, tmp);

                  tmp = new Object[][] {
                                        {"",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE}
                                     };
                  linhas =  Printer.adcLine(linhas, tmp);
             }
        } catch (Exception e) {}

        if (nAut > 0) {
              tmp = new Object[][] {
                                      {"__________ VALOR(ES) LANCADOS __________",null,0,new java.awt.Font("Arial",java.awt.Font.PLAIN,7), Color.BLACK, Color.WHITE}
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
                                    {"Este recibo não quita qualquer débito anterior.",null,0,new java.awt.Font("Arial",java.awt.Font.ITALIC + java.awt.Font.BOLD,7), Color.BLACK, Color.WHITE}
                                 };
            linhas =  Printer.adcLine(linhas, tmp);

            tmp = new Object[][] {
                                {null,null,2,null, Color.BLACK, Color.WHITE},
                             };
            linhas =  Printer.adcLine(linhas, tmp);

            // Imprimir Autenticação
            tmp = new Object[][] {
                                    {VariaveisGlobais.dCliente.get("marca").trim() + "RC" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-1" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario,null,0,new java.awt.Font("Arial",java.awt.Font.BOLD,7), Color.BLACK, Color.WHITE}
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
        
        String docID = "_" + contrato + ".ps";
        String docName = "reports/Recibos/" + "RC_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + docID;

        jThermica np = new jThermica(linhas);
        np.setDocName(docName);
        if (nAut > -1) {
            np.toSave();
//            np.toPrint();
        } //else np.toPrint();
        np.setDocName("");
                
    }
    
    private float ListaCamposReciboThermica(Object[][] linhas, int via, jThermica Printer) {
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

        // 22-05-2012 -  via gambiarra - somente na 1a via
        String aregras[][] = {};
        try { aregras = FuncoesGlobais.treeRegra(regras, true); } catch (Exception ex) {}
        
        int i = 0; int maxcpos = jctCampos.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = ""; int newLine = 0;
        String posAnt = ""; String saida_final = ""; int linha = 1;
        float totalrecibo = 0; float vdesconto = 0;
        String pitem = "", pcota = "", pvalor = "";
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
                
                // Nome do item
                pitem = saida.trim();                
            } else if (jctCampos.getComponent(i) instanceof JFormattedTextField) {
                saida = ((JFormattedTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JFormattedTextField) jctCampos.getComponent(i)).getName();
                newLine = 1;

                String alinha = "";
                if (VariaveisGlobais.bShowCotaParcela) {
                    if (nmCampo.toLowerCase().trim().indexOf("cota") > -1) {
                        alinha = new Pad(("00/00".equals(saida.trim()) || "00/0000".equals(saida.trim()) ? "       " : saida), 7).LPad();
                        //alinha = new Pad("       ", 7).LPad();
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
                // Valor do Item
                pvalor = saida.trim();                
            } else if (jctCampos.getComponent(i) instanceof JTextField) {
                saida = ((JTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JTextField) jctCampos.getComponent(i)).getName();
                newLine = 0;
                // Cota/Parcela do Item
                pcota = saida.trim();                
            } else if (jctCampos.getComponent(i) instanceof JCheckBox) {
                boolean simnao = ((JCheckBox) jctCampos.getComponent(i)).isSelected();
                saida = "";
                nmCampo = ((JCheckBox) jctCampos.getComponent(i)).getName();
                newLine = 0;
            } else { saida = ""; newLine = 0; pitem = ""; pcota = ""; pvalor = "";}

            saida_final += saida;
            if (newLine == 1) {
                if (via > 1) {
                    Object[][] tmp =  {
                                          {pitem,60,1,new Font("Times New Roman",Font.PLAIN,8), Color.BLACK, Color.WHITE},
                                          {pcota,20,0,new Font("Times New Roman",Font.PLAIN,8), Color.BLACK, Color.WHITE},
                                          {pvalor,null,2,new Font("Times New Roman",Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                      };
                    this.linhas =  Printer.adcLine(this.linhas, tmp);
                } else {
                    boolean achei = false; boolean soma = true;
                    int pos = FuncoesGlobais.FindInRegras(aregras, 3, "Desc",0,4);
                    if (saida_final.contains("Desc") && pos > -1 && aregras[pos][1].equals("1")) {
                        if (!aregras[pos][2].equals("1")) achei = true;
                        int xpos = FuncoesGlobais.RAt(saida_final," ");
                        vdesconto += LerValor.StringToFloat(saida_final.substring(xpos, saida_final.length()));
                        soma = false; 
                    }
                    
                    pos = FuncoesGlobais.FindInRegras(aregras, 3, "Dife",0,4);
                    if (saida_final.contains("Dife") && pos > -1 && aregras[pos][1].equals("1")) {
                        if (!aregras[pos][2].equals("1")) achei = true;
                    }

                    if (!achei) {
                        for (int z=0;z<aregras.length;z++) {
                            if (saida_final.trim().toLowerCase().contains(aregras[z][3].trim().toLowerCase())) {
                                if (aregras[z][1].equals("1")) {
                                    if (!aregras[z][2].equals("1")) {achei = true; break;}
                                }
                            }
                        }
                    }
                    if (!achei) newLine = -1;
                    if (newLine > -1) {
                        if (linha == 1) {
                            // aqui processa o acumulo na linha 1
                            float somar = CamposSomadosAoAluguel();
                            int xpos = FuncoesGlobais.RAt(saida_final," ");
                            String mvalor = saida_final.substring(xpos, saida_final.length());
                            float fvalor = LerValor.StringToFloat(mvalor) + somar;
                            String svalor = LerValor.floatToCurrency(fvalor,2).trim(); 
                            pvalor = svalor;
                            int newpos = saida_final.length() - svalor.length();
                            saida_final = saida_final.substring(0, newpos) + svalor;
                        }
                        
                        Object[][] tmp =  {
                                              {pitem,60,1,new Font("Times New Roman",Font.PLAIN,8), Color.BLACK, Color.WHITE},
                                              {pcota,20,0,new Font("Times New Roman",Font.PLAIN,8), Color.BLACK, Color.WHITE},
                                              {pvalor,null,2,new Font("Times New Roman",Font.PLAIN,8), Color.BLACK, Color.WHITE}
                                          };
                        this.linhas =  Printer.adcLine(this.linhas, tmp);
                        linha += 1;
                        
                        // gera novo total do recibo para 1a via
                        int xpos = FuncoesGlobais.RAt(saida_final," ");
                        String mvalor = saida_final.substring(xpos, saida_final.length());
                        float fvalor = LerValor.StringToFloat(mvalor);
                        if (soma) totalrecibo += fvalor; 
                    }
                }
                saida_final = "";
            }
        }
        
        return totalrecibo - vdesconto;
    }

  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jBairro;
    private javax.swing.JLabel jCep;
    private javax.swing.JLabel jCidade;
    private javax.swing.JComboBox jContrato;
    private javax.swing.JLabel jEndereco;
    private javax.swing.JLabel jEstado;
    private javax.swing.JButton jImprimir;
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox jNomeLoca;
    private javax.swing.JLabel jNomeProp;
    private javax.swing.JLabel jObs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner jQtde;
    private javax.swing.JLabel jRgimv;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JFormattedTextField jTotalRecibos;
    private javax.swing.JComboBox jVencimentos;
    private javax.swing.JFormattedTextField jVrRecibo;
    private javax.swing.JButton jbtretencão;
    private javax.swing.JCheckBox jchb1Via;
    private javax.swing.JPanel jctCampos;
    private javax.swing.JPanel jpRecebe;
    private javax.swing.JToggleButton jtgbAvulso;
    private javax.swing.JLabel jvrRetencao;
    private javax.swing.JLabel lblQtde;
    // End of variables declaration//GEN-END:variables

}
