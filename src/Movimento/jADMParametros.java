/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento;

import Funcoes.*;
import Protocolo.DepuraCampos;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author supervisor
 */
public class jADMParametros extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String tag = "UPDATE";
    boolean mCartVazio = false;
    String regras = "";

    /**
     * Creates new form jADMParametros
     */
    public jADMParametros() {
        initComponents();
        
        AtualizaNomes();
        LerParamCalculos();
        LerDadosAdm();
        LerContas();
        LerCampos();
        
        ListaContasBoletas();
        try {
            LerEmailSettings();
        } catch (SQLException ex) {
            Logger.getLogger(jADMParametros.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            LerMensagens();
        } catch (SQLException ex) {
            Logger.getLogger(jADMParametros.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        new SimpleThread().start();
    }

    class SimpleThread extends Thread {
        public SimpleThread() {
        }
        public void run() {
            ListaFeriados();
        }
    }

    private void ListaFeriados() {
        String sql = "SELECT dia, mes, descricao, tipo, dias FROM feriados ORDER BY dia, mes;";

        TableControl.Clear(jFeriados);
        // Seta Cabecario
        TableControl.header(jFeriados, new String[][] {{"Data", "Descrição",
                                      "Tipo", "dias"},{"80","300","100","30"}});

        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            jbarra.setValue(0);
            int eof = DbMain.RecordCount(rs); int pos = 1;
            rs.beforeFirst();
            while (rs.next()) {
                int br = (pos++ * 100) / eof;
                jbarra.setValue(br + 1);
                
                String pdia = rs.getString("dia");
                String pmes = rs.getString("mes");
                String pano = Dates.DateFormata("yyyy/MM/dd",new Date()).substring(0, 4);
                String pdata = FuncoesGlobais.StrZero(pdia, 2) + "-" + FuncoesGlobais.StrZero(pmes, 2) + "-" + FuncoesGlobais.StrZero(pano, 4);
                String pdesc = rs.getString("descricao");
                String ptipo = rs.getString("tipo");
                String pdias = rs.getString("dias");
                TableControl.add(jFeriados, new String[][]{{pdata, pdesc, ptipo, pdias},{"C","L","C","C"}}, true);
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        DbMain.FecharTabela(rs);
        jbarra.setValue(100);
    }
    
    private void LerCampos() {
        // Dados Reajuste
        try {
            jspReajNumero.setValue(Integer.parseInt(conn.LerParametros("REAJNUM")));
        } catch (Exception ex) {
            jspReajNumero.setValue(0);
        }
        try {
            jcbReajTipo.getModel().setSelectedItem(conn.LerParametros("REAJTIPO"));
        } catch (Exception ex) {
            jcbReajTipo.setSelectedIndex(0);
        }
        
        try {c_vrmaxch.setText(LerValor.FormatNumber(conn.LerParametros("MAXCH"),2));} catch (Exception ex) {c_vrmaxch.setText("0,00");}        
        try {extMax.setText(LerValor.FormatNumber(conn.LerParametros("EXTMAX"),2));} catch (Exception ex) {extMax.setText("0,00");}        
        try {regras = conn.LerParametros("REGRAS");} catch (Exception ex) {}
        VariaveisGlobais.ccampos = "";        
        
        try {
            if (InsereCampos()) {
                DesMontaTela();
                MontaTela();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private void AtualizaNomes() {
        jAbasCalculos.setTitleAt(0, VariaveisGlobais.dCliente.get("MU"));
        jAbasCalculos.setTitleAt(1, VariaveisGlobais.dCliente.get("JU"));
        jAbasCalculos.setTitleAt(2, VariaveisGlobais.dCliente.get("CO"));
        jAbasCalculos.setTitleAt(3, "Taxas");

        // Ajusta Nomes campos letras
        jMU.setText(VariaveisGlobais.dCliente.get("MU"));
        jJU.setText(VariaveisGlobais.dCliente.get("JU"));
        jCO.setText(VariaveisGlobais.dCliente.get("CO"));
        jEP.setText(VariaveisGlobais.dCliente.get("EP"));        
    }
    
    private void LerContas() {
        TableControl.header(jContas, new String[][] {{"codigo", "descrição"},{"60","600"}});
        
        a_btadc.setEnabled(true);
        a_btdel.setEnabled(false);
        a_btsave.setEnabled(false);
        
        jContas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {  
                a_sigla.setText(jContas.getValueAt(jContas.getSelectedRow(), 0).toString());
                a_desc.setText(jContas.getValueAt(jContas.getSelectedRow(), 1).toString());
                
                a_btdel.setEnabled(!(jContas.getSelectedRow() >= 0 && jContas.getSelectedRow() <= 7));
                tag = "UPDATE";
                a_btsave.setEnabled(true);
            }});

        FillContas();
    }
    
    private void FillContas() {
        String sSql = "SELECT CODIGO, DESCR FROM ADM ORDER BY AUTOID;";
        ResultSet rs = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String tcodigo = rs.getString("codigo");
                String tdescr = rs.getString("descr");
                
                TableControl.add(jContas, new String[][]{{tcodigo, tdescr}, {"C","L"}},true);
            }
        } catch (Exception ex) {ex.printStackTrace();}
        DbMain.FecharTabela(rs);
    }
    
    private void LerDadosAdm() {
        e_NOME.setText(VariaveisGlobais.dCliente.get("empresa"));
        e_CNPJ.setText(VariaveisGlobais.dCliente.get("cnpj"));
        e_INSC.setText(VariaveisGlobais.dCliente.get("inscricao"));        
        e_END.setText(VariaveisGlobais.dCliente.get("endereco"));
        e_NUM.setText(VariaveisGlobais.dCliente.get("numero"));
        e_COMPL.setText(VariaveisGlobais.dCliente.get("complemento"));
        e_BAIRRO.setText(VariaveisGlobais.dCliente.get("bairro"));
        e_CIDADE.setText(VariaveisGlobais.dCliente.get("cidade"));
        e_ESTADO.setText(VariaveisGlobais.dCliente.get("estado"));
        e_CEP.setText(VariaveisGlobais.dCliente.get("cep"));
        e_TELEFONE.setText(VariaveisGlobais.dCliente.get("telefone"));
        e_EMAIL.setText(VariaveisGlobais.dCliente.get("email"));
        e_HPAGE.setText(VariaveisGlobais.dCliente.get("hpage"));
        e_MARCA.setText(VariaveisGlobais.dCliente.get("marca"));
    }
    
    private void GravarDadosAdm() {
        try {
            String[][] param = {{"EMPRESA","TEXTO",e_NOME.getText()},
                {"ENDERECO","TEXTO",e_END.getText()},
                {"NUMERO","TEXTO",e_NUM.getText()},
                {"COMPLEMENTO","TEXTO",e_COMPL.getText()},
                {"BAIRRO","TEXTO",e_BAIRRO.getText()},
                {"CIDADE","TEXTO",e_CIDADE.getText()},
                {"ESTADO","TEXTO",e_ESTADO.getText()},
                {"CEP","TEXTO",e_CEP.getText()},
                {"CNPJ","TEXTO",e_CNPJ.getText()},
                {"INSCRICAO","TEXTO",e_INSC.getText()},
                {"TELEFONE","TEXTO",e_TELEFONE.getText()},
                {"HPAGE","TEXTO",e_HPAGE.getText()},
                {"EMAIL","TEXTO",e_EMAIL.getText()},
                {"MARCA","TEXTO",e_MARCA.getText()}};
            conn.GravarMultiParametros(param);
            
            // Atualiza Variaveis
            VariaveisGlobais.dCliente.set("empresa", e_NOME.getText());
            VariaveisGlobais.dCliente.set("endereco", e_END.getText());
            VariaveisGlobais.dCliente.set("numero", e_NUM.getText());
            VariaveisGlobais.dCliente.set("complemento", e_COMPL.getText());
            VariaveisGlobais.dCliente.set("bairro", e_BAIRRO.getText());
            VariaveisGlobais.dCliente.set("cidade", e_CIDADE.getText());
            VariaveisGlobais.dCliente.set("estado", e_ESTADO.getText());
            VariaveisGlobais.dCliente.set("cep", e_CEP.getText());
            VariaveisGlobais.dCliente.set("cnpj", e_CNPJ.getText());
            VariaveisGlobais.dCliente.set("inscricao", e_INSC.getText());
            VariaveisGlobais.dCliente.set("marca", e_MARCA.getText());
            VariaveisGlobais.dCliente.set("telefone", e_TELEFONE.getText());
            VariaveisGlobais.dCliente.set("hpage", e_HPAGE.getText());
            VariaveisGlobais.dCliente.set("email", e_EMAIL.getText());
            VariaveisGlobais.marca = VariaveisGlobais.dCliente.get("marca");

        } catch (Exception ex) {ex.printStackTrace();}
    }
    
    private void LerParamCalculos() {
        try {
            jTALUG.setSelected(Boolean.valueOf(conn.LerParametros("talug")));
            jTBRLIQ.setSelected(Boolean.valueOf(conn.LerParametros("tbrliq")));
            jTCORRECAO.setSelected(Boolean.valueOf(conn.LerParametros("tcorrecao")));
            jTJUROS.setSelected(Boolean.valueOf(conn.LerParametros("tjuros")));
            jTMULTA.setSelected(Boolean.valueOf(conn.LerParametros("tmulta")));
            jTSEGURO.setSelected(Boolean.valueOf(conn.LerParametros("tseguro")));
            jTTAXA.setSelected(Boolean.valueOf(conn.LerParametros("ttaxa")));

            jMALUG.setSelected(Boolean.valueOf(conn.LerParametros("malug")));
            jMCORRECAO.setSelected(Boolean.valueOf(conn.LerParametros("mcorrecao")));
            jMEXPE.setSelected(Boolean.valueOf(conn.LerParametros("mexpe")));
            jMJUROS.setSelected(Boolean.valueOf(conn.LerParametros("mjuros")));
            jMTAXA.setSelected(Boolean.valueOf(conn.LerParametros("mtaxa")));

            jJALUG.setSelected(Boolean.valueOf(conn.LerParametros("jalug")));
            jJCORRECAO.setSelected(Boolean.valueOf(conn.LerParametros("jcorrecao")));
            jJEXPE.setSelected(Boolean.valueOf(conn.LerParametros("jexpe")));
            jJMULTA.setSelected(Boolean.valueOf(conn.LerParametros("jmulta")));
            jJSEGURO.setSelected(Boolean.valueOf(conn.LerParametros("jseguro")));
            jJTAXA.setSelected(Boolean.valueOf(conn.LerParametros("jtaxa")));

            jCALUG.setSelected(Boolean.valueOf(conn.LerParametros("calug")));
            jCEXPE.setSelected(Boolean.valueOf(conn.LerParametros("cexpe")));
            jCJUROS.setSelected(Boolean.valueOf(conn.LerParametros("cjuros")));
            jCMULTA.setSelected(Boolean.valueOf(conn.LerParametros("cmulta")));
            jCSEGURO.setSelected(Boolean.valueOf(conn.LerParametros("cseguro")));
            jCTAXA.setSelected(Boolean.valueOf(conn.LerParametros("ctaxa")));
        } catch (Exception ex) {}
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel14 = new javax.swing.JPanel();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jAbasCalculos = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jMALUG = new javax.swing.JCheckBox();
        jMCORRECAO = new javax.swing.JCheckBox();
        jMEXPE = new javax.swing.JCheckBox();
        jMJUROS = new javax.swing.JCheckBox();
        jMTAXA = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jJALUG = new javax.swing.JCheckBox();
        jJCORRECAO = new javax.swing.JCheckBox();
        jJEXPE = new javax.swing.JCheckBox();
        jJMULTA = new javax.swing.JCheckBox();
        jJTAXA = new javax.swing.JCheckBox();
        jJSEGURO = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jCALUG = new javax.swing.JCheckBox();
        jCEXPE = new javax.swing.JCheckBox();
        jCJUROS = new javax.swing.JCheckBox();
        jCMULTA = new javax.swing.JCheckBox();
        jCSEGURO = new javax.swing.JCheckBox();
        jCTAXA = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jTALUG = new javax.swing.JCheckBox();
        jTBRLIQ = new javax.swing.JCheckBox();
        jTCORRECAO = new javax.swing.JCheckBox();
        jTJUROS = new javax.swing.JCheckBox();
        jTMULTA = new javax.swing.JCheckBox();
        jTSEGURO = new javax.swing.JCheckBox();
        jTTAXA = new javax.swing.JCheckBox();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        a_sigla = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        a_desc = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        a_btsave = new javax.swing.JButton();
        a_btadc = new javax.swing.JButton();
        a_btdel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jContas = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        e_NOME = new javax.swing.JTextField();
        e_TIPO = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        e_CNPJ = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        e_INSC = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        e_END = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        e_NUM = new javax.swing.JTextField();
        e_COMPL = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        e_BAIRRO = new javax.swing.JTextField();
        e_CIDADE = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        e_ESTADO = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        e_CEP = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        e_MARCA = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        e_LOGO = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        e_EMAIL = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        e_HPAGE = new javax.swing.JTextField();
        jbtGravarDadosAdm = new javax.swing.JButton();
        e_TELEFONE = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jScroll = new javax.swing.JScrollPane();
        jctCampo = new javax.swing.JPanel();
        jMU = new javax.swing.JLabel();
        jcbMU = new javax.swing.JCheckBox();
        jcbJU = new javax.swing.JCheckBox();
        jJU = new javax.swing.JLabel();
        jcbCO = new javax.swing.JCheckBox();
        jCO = new javax.swing.JLabel();
        jcbEP = new javax.swing.JCheckBox();
        jEP = new javax.swing.JLabel();
        jcbSG = new javax.swing.JCheckBox();
        jSG = new javax.swing.JLabel();
        jcbDC = new javax.swing.JCheckBox();
        jDC = new javax.swing.JLabel();
        jcbDF = new javax.swing.JCheckBox();
        jDF = new javax.swing.JLabel();
        jsmMU = new javax.swing.JCheckBox();
        jsmJU = new javax.swing.JCheckBox();
        jsmCO = new javax.swing.JCheckBox();
        jsmEP = new javax.swing.JCheckBox();
        jsmSG = new javax.swing.JCheckBox();
        jsmDC = new javax.swing.JCheckBox();
        jsmDF = new javax.swing.JCheckBox();
        e_btupdate = new javax.swing.JButton();
        e_btdel = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jAgencia = new javax.swing.JTextField();
        jConta = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jCtaDv = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jbanco = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jcarteira = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jmoeda = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jtarifa = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jNossoNumero = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtblBcos = new javax.swing.JTable();
        jbcoAdc = new javax.swing.JButton();
        jbcoDel = new javax.swing.JButton();
        jbancoDv = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        c_vrmaxch = new javax.swing.JFormattedTextField();
        c_btgravar = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jFeriados = new javax.swing.JTable();
        jDia = new javax.swing.JSpinner();
        jMes = new javax.swing.JSpinner();
        btAdc = new javax.swing.JButton();
        jbtDel = new javax.swing.JButton();
        jDesc = new javax.swing.JTextField();
        jTipo = new javax.swing.JSpinner();
        jDiasAnt = new javax.swing.JSpinner();
        jbarra = new javax.swing.JProgressBar();
        jPanel13 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jspReajNumero = new javax.swing.JSpinner();
        jcbReajTipo = new javax.swing.JComboBox();
        jbtReajuste = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        extMax = new javax.swing.JFormattedTextField();
        btextMax = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel21 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jEmailEmp = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jSenhaEmail = new javax.swing.JTextField();
        jPop = new javax.swing.JRadioButton();
        jImap = new javax.swing.JRadioButton();
        jAutentica = new javax.swing.JCheckBox();
        jPanel17 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jSmtp = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jPortSmtp = new javax.swing.JSpinner();
        jPanel18 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jEndPopImap = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jPortPopImap = new javax.swing.JSpinner();
        jPanel20 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jSSH_Conta = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jSSH_Usuario = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jSSH_Senha = new javax.swing.JTextField();
        jSSH_DbName = new javax.swing.JTextField();
        jPanel22 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLinha1 = new javax.swing.JTextField();
        jLinha2 = new javax.swing.JTextField();
        jLinha3 = new javax.swing.JTextField();
        jLinha4 = new javax.swing.JTextField();
        jLinha5 = new javax.swing.JTextField();
        jLinha6 = new javax.swing.JTextField();
        jLinha7 = new javax.swing.JTextField();
        jLinha8 = new javax.swing.JTextField();
        jLinha9 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLinha10 = new javax.swing.JTextField();
        jcbAniversario = new javax.swing.JCheckBox();
        jLabel48 = new javax.swing.JLabel();
        jLinhaMsgAniversário = new javax.swing.JTextField();
        jcbComemorativas = new javax.swing.JCheckBox();
        btSaveMsg = new javax.swing.JButton();
        jLabel49 = new javax.swing.JLabel();
        jCab1 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jCab2 = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jCabDoc = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        btSaveEmail = new javax.swing.JButton();
        jPanel23 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jFTP_Porta = new javax.swing.JSpinner();
        jFTP_Conta = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jFTP_Usuario = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jFTP_Senha = new javax.swing.JTextField();
        jbtGravarParametros = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Dados e Parametros da ADM ::.");
        setVisible(true);

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jMALUG.setText("Calcula multa incluindo os campos AL na soma. (MALUG)");

        jMCORRECAO.setText("Calcula multa incluindo a CORREÇÃO na soma. (MCORRECAO)");

        jMEXPE.setText("Calcula multa incluindo a Taxa de EXPEDIENTE na soma. (MEXPE)");

        jMJUROS.setText("Calcula multa incluindo o JUROS na soma. (MJUROS)");

        jMTAXA.setText("Calcula multa incluindo os campos NT na soma. (MTAXA)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jMALUG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMCORRECAO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMEXPE, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jMJUROS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMTAXA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jMALUG)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMCORRECAO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMEXPE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMJUROS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMTAXA)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jAbasCalculos.addTab("MU", jPanel2);

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jJALUG.setText("Calcula juros incluindo os campos AL na soma. (JALUG)");

        jJCORRECAO.setText("Calcula juros incluindo a CORREÇÃO na soma. (JCORRECAO)");

        jJEXPE.setText("Calcula juros incluindo a Taxa de EXPEDIENTE na soma. (JEXPE)");

        jJMULTA.setText("Calcula juros incluindo a MULTA na soma. (JMULTA)");

        jJTAXA.setText("Calcula juros incluindo as TAXAS=NT na soma. (JTAXA)");

        jJSEGURO.setText("Calcula juros incluindo o SEGURO na soma. (JSEGURO)");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jJALUG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jJCORRECAO, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jJEXPE, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jJMULTA, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jJTAXA, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jJSEGURO, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jJALUG)
                .addGap(2, 2, 2)
                .addComponent(jJCORRECAO)
                .addGap(2, 2, 2)
                .addComponent(jJEXPE)
                .addGap(2, 2, 2)
                .addComponent(jJMULTA)
                .addGap(2, 2, 2)
                .addComponent(jJSEGURO)
                .addGap(2, 2, 2)
                .addComponent(jJTAXA))
        );

        jAbasCalculos.addTab("JU", jPanel3);

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jCALUG.setText("Calcula correção sobre campos AL. (CALUG)");

        jCEXPE.setText("Calcula correção incluindo a taxa de expediente na soma. (CEXPE)");

        jCJUROS.setText("Calcula correção incluindo o JUROS na soma. (CJUROS)");

        jCMULTA.setText("Calcula correção incluindo a MULTA na soma. (CMULTA)");

        jCSEGURO.setText("Calcula correção incluindo o SEGURO na soma. (CSEGURO)");

        jCTAXA.setText("Calcula correção incluindo as TAXA=NT na soma. (CTAXA)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCALUG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCEXPE, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jCJUROS, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jCMULTA, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jCTAXA, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jCSEGURO, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCALUG)
                .addGap(2, 2, 2)
                .addComponent(jCEXPE)
                .addGap(2, 2, 2)
                .addComponent(jCJUROS)
                .addGap(2, 2, 2)
                .addComponent(jCMULTA)
                .addGap(2, 2, 2)
                .addComponent(jCSEGURO)
                .addGap(2, 2, 2)
                .addComponent(jCTAXA))
        );

        jAbasCalculos.addTab("CO", jPanel4);

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jTALUG.setText("Soma campos NT com campos AL na soma. (TALUG)");

        jTBRLIQ.setText("TRUE PARA calcular sobre o BRUTO e FALSE para calcular sobre o LIQUIDO. (TBRLIQ)");

        jTCORRECAO.setText("Soma os CAMPOS NT para calcular a CORREÇÃO. (TCORRECAO)");

        jTJUROS.setText("Soma os campos NT para calcular o JUROS. (TJUROS)");

        jTMULTA.setText("Soma os campos NT para calcular MULTAS. (TMULTA)");

        jTSEGURO.setText("oma os campos NT para calcular SEGURO. (TSEGURO)");

        jTTAXA.setText("Soma os campos NT para calcular a Taxa de EXPEDIENTE. (TTAXA)");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTALUG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTBRLIQ, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jTCORRECAO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTJUROS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTSEGURO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTMULTA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTTAXA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTALUG)
                .addGap(1, 1, 1)
                .addComponent(jTBRLIQ)
                .addGap(1, 1, 1)
                .addComponent(jTCORRECAO)
                .addGap(1, 1, 1)
                .addComponent(jTJUROS)
                .addGap(1, 1, 1)
                .addComponent(jTMULTA)
                .addGap(1, 1, 1)
                .addComponent(jTSEGURO)
                .addGap(1, 1, 1)
                .addComponent(jTTAXA)
                .addGap(2, 2, 2))
        );

        jAbasCalculos.addTab("Taxas", jPanel5);

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        a_sigla.setEditable(false);

        jLabel1.setText("Sigla");

        jLabel2.setText("Descrição");

        a_btsave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        a_btsave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a_btsaveActionPerformed(evt);
            }
        });

        a_btadc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/edit.png"))); // NOI18N
        a_btadc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a_btadcActionPerformed(evt);
            }
        });

        a_btdel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/stop.png"))); // NOI18N
        a_btdel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a_btdelActionPerformed(evt);
            }
        });

        jContas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jContas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jContas.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jContas);
        jContas.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(a_sigla, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(a_desc, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                                .addGap(1, 1, 1)
                                .addComponent(a_btsave, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(a_btadc, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)
                                .addComponent(a_btdel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(a_btdel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(a_btadc, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(a_sigla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(a_desc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(a_btsave, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Contas ADM", jPanel1);

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel3.setText("Razão Social:");

        jLabel4.setText("CNPJ/CPF:");

        jLabel5.setText("Inscrição:");

        jLabel6.setText("Endereço:");

        jLabel7.setText("Nº");

        jLabel8.setText("Bairro:");

        jLabel9.setText("Cidade:");

        jLabel10.setText("Estado:");

        jLabel11.setText("Cep:");

        try {
            e_CEP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel12.setText("Telefone:");

        jLabel13.setText("Marca:");

        jLabel14.setText("Logo:");

        e_LOGO.setEditable(false);
        e_LOGO.setEnabled(false);

        jLabel15.setText("Email:");

        jLabel16.setText("HPage:");

        jbtGravarDadosAdm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        jbtGravarDadosAdm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtGravarDadosAdmActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(4, 4, 4)
                        .addComponent(e_NOME)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(e_TIPO, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(e_CNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(e_INSC))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(e_END, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(e_NUM, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(e_COMPL, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(e_MARCA, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(e_LOGO)
                        .addGap(1, 1, 1)
                        .addComponent(jbtGravarDadosAdm, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(e_BAIRRO, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(e_CIDADE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(e_ESTADO, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(e_CEP, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(83, 83, 83)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(e_TELEFONE))
                            .addComponent(e_EMAIL)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(e_HPAGE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(e_NOME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(e_TIPO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(e_CNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(e_INSC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(e_END, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(e_NUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(e_COMPL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(e_BAIRRO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(e_CIDADE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(e_ESTADO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(e_CEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(e_TELEFONE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(e_EMAIL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(e_HPAGE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtGravarDadosAdm)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(e_MARCA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(e_LOGO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Dados ADM", jPanel6);

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jScroll.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("1ª Via"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScroll.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        jctCampo.setBackground(new java.awt.Color(254, 189, 124));
        jctCampo.setEnabled(false);
        jctCampo.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N

        javax.swing.GroupLayout jctCampoLayout = new javax.swing.GroupLayout(jctCampo);
        jctCampo.setLayout(jctCampoLayout);
        jctCampoLayout.setHorizontalGroup(
            jctCampoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 592, Short.MAX_VALUE)
        );
        jctCampoLayout.setVerticalGroup(
            jctCampoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 342, Short.MAX_VALUE)
        );

        jScroll.setViewportView(jctCampo);

        jMU.setText("Multa");
        jMU.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jJU.setText("Juros");
        jJU.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jCO.setText("Correcao");
        jCO.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jEP.setText("Expediente");
        jEP.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jSG.setText("Seguro");
        jSG.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jDC.setText("Desconto");
        jDC.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jDF.setText("Diferenca");
        jDF.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jsmMU.setText("Somar com o Aluguel");

        jsmJU.setText("Somar com o Aluguel");

        jsmCO.setText("Somar com o Aluguel");

        jsmEP.setText("Somar com o Aluguel");

        jsmSG.setText("Somar com o Aluguel");

        jsmDC.setText("Somar com o Aluguel");

        jsmDF.setText("Somar com o Aluguel");

        e_btupdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        e_btupdate.setText("Atualizar");
        e_btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                e_btupdateActionPerformed(evt);
            }
        });

        e_btdel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/stop.png"))); // NOI18N
        e_btdel.setText("Apagar");
        e_btdel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                e_btdelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDF, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDC, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSG, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jEP, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCO, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jJU, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMU, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbDF)
                            .addComponent(jcbDC)
                            .addComponent(jcbSG)
                            .addComponent(jcbEP)
                            .addComponent(jcbCO)
                            .addComponent(jcbJU)
                            .addComponent(jcbMU))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jsmDF, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                            .addComponent(jsmDC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jsmSG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jsmEP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jsmCO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jsmJU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jsmMU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(176, 176, 176)
                        .addComponent(e_btdel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(e_btupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(261, 261, 261)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbMU)
                            .addComponent(jsmMU)
                            .addComponent(jMU))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbJU)
                            .addComponent(jJU)
                            .addComponent(jsmJU))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbCO)
                            .addComponent(jCO)
                            .addComponent(jsmCO))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbEP)
                            .addComponent(jEP)
                            .addComponent(jsmEP))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbSG)
                            .addComponent(jSG)
                            .addComponent(jsmSG))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbDC)
                            .addComponent(jDC)
                            .addComponent(jsmDC))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jsmDF)
                            .addComponent(jcbDF)
                            .addComponent(jDF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(e_btupdate)
                            .addComponent(e_btdel))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Emissão Recibo", jPanel7);

        jLabel18.setText("Agencia:");

        jLabel19.setText("Conta:");

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Numero do Banco:");

        jLabel21.setText("Carteira:");

        jLabel23.setText("Moeda:");

        jLabel24.setText("Tarifa:");

        jtarifa.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtarifa.setText("0,00");

        jLabel25.setText("Nosso Numero:");

        jNossoNumero.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jNossoNumero.setText("0000000000000");

        jtblBcos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "Agencia", "Conta", "DV", "N.Banco", "Carteira", "Moeda", "Tarifa", "N.Numero"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtblBcos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtblBcos.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jtblBcos);
        if (jtblBcos.getColumnModel().getColumnCount() > 0) {
            jtblBcos.getColumnModel().getColumn(0).setResizable(false);
            jtblBcos.getColumnModel().getColumn(0).setPreferredWidth(15);
            jtblBcos.getColumnModel().getColumn(1).setResizable(false);
            jtblBcos.getColumnModel().getColumn(1).setPreferredWidth(30);
            jtblBcos.getColumnModel().getColumn(2).setResizable(false);
            jtblBcos.getColumnModel().getColumn(2).setPreferredWidth(80);
            jtblBcos.getColumnModel().getColumn(3).setResizable(false);
            jtblBcos.getColumnModel().getColumn(3).setPreferredWidth(20);
            jtblBcos.getColumnModel().getColumn(4).setResizable(false);
            jtblBcos.getColumnModel().getColumn(4).setPreferredWidth(20);
            jtblBcos.getColumnModel().getColumn(5).setResizable(false);
            jtblBcos.getColumnModel().getColumn(5).setPreferredWidth(30);
            jtblBcos.getColumnModel().getColumn(6).setPreferredWidth(30);
            jtblBcos.getColumnModel().getColumn(7).setResizable(false);
            jtblBcos.getColumnModel().getColumn(7).setPreferredWidth(50);
            jtblBcos.getColumnModel().getColumn(8).setResizable(false);
            jtblBcos.getColumnModel().getColumn(8).setPreferredWidth(100);
        }

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jbcoAdc.setText("Incluir");
        jbcoAdc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbcoAdcActionPerformed(evt);
            }
        });

        jbcoDel.setText("Excluir");
        jbcoDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbcoDelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAgencia, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jConta, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCtaDv, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbanco, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbancoDv, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcarteira, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jmoeda, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNossoNumero))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbcoAdc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbcoDel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jAgencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCtaDv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jbanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbancoDv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jcarteira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jmoeda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(jtarifa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jNossoNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jbcoAdc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbcoDel)
                        .addGap(0, 144, Short.MAX_VALUE))
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Banco", jPanel11);

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Emissão de Cheques"));

        jLabel17.setText("Valor Máximo por Cheque emitido:");

        c_vrmaxch.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        c_vrmaxch.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        c_vrmaxch.setText("0,00");

        c_btgravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        c_btgravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_btgravarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel17)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(c_vrmaxch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(c_btgravar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(c_btgravar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(c_vrmaxch))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Feriados e dias sem funcionamento"));

        jFeriados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jFeriados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jFeriados.getTableHeader().setReorderingAllowed(false);
        jFeriados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jFeriadosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jFeriados);
        jFeriados.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jDia.setModel(new javax.swing.SpinnerNumberModel(1, 1, 31, 1));
        jDia.setEditor(new javax.swing.JSpinner.NumberEditor(jDia, "00"));

        jMes.setModel(new javax.swing.SpinnerListModel(new String[] {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"}));

        btAdc.setText("+");
        btAdc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAdcActionPerformed(evt);
            }
        });

        jbtDel.setText("-");
        jbtDel.setEnabled(false);
        jbtDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtDelActionPerformed(evt);
            }
        });

        jTipo.setModel(new javax.swing.SpinnerListModel(new String[] {"Feriado", "Anuncio", "Não Abriu"}));

        jDiasAnt.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));
        jDiasAnt.setToolTipText("Dias anteriores no qual o anuncio começara a aparecer.");
        jDiasAnt.setEditor(new javax.swing.JSpinner.NumberEditor(jDiasAnt, "00"));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbarra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btAdc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtDel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jDia, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMes, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDiasAnt, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(btAdc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtDel)
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDiasAnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbarra, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Reajuste de Aluguel"));

        jLabel22.setText("Avisar no Recibo/Boleta com:");

        jcbReajTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Meses" }));

        jbtReajuste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        jbtReajuste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtReajusteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jspReajNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbReajTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtReajuste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtReajuste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jcbReajTipo)
                    .addComponent(jspReajNumero, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(6, 6, 6))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Extratos a Depositos"));

        jLabel26.setText("Valor Minimo:");

        extMax.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        extMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        extMax.setText("0,00");

        btextMax.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        btextMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btextMaxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(extMax)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btextMax, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(btextMax)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(extMax))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Demais parametros", jPanel8);

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(".:: E-MAIL ::."));

        jLabel27.setText("Conta de Email:");

        jEmailEmp.setText("jTextField1");

        jLabel28.setText("Senha:");

        jSenhaEmail.setText("jTextField2");

        buttonGroup2.add(jPop);
        jPop.setText("POP");

        buttonGroup2.add(jImap);
        jImap.setText("IMAP");

        jAutentica.setText("Autenticação");
        jAutentica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAutenticaActionPerformed(evt);
            }
        });

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Envio"));

        jLabel29.setText("SMTP:");

        jSmtp.setText("jTextField3");

        jLabel31.setText("Porta:");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSmtp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPortSmtp, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel29)
                .addComponent(jSmtp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel31)
                .addComponent(jPortSmtp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Recebimento"));

        jLabel30.setText("POP::");

        jEndPopImap.setText("jTextField3");

        jLabel32.setText("Porta:");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jEndPopImap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPortPopImap, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel30)
                .addComponent(jEndPopImap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel32)
                .addComponent(jPortPopImap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEmailEmp))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSenhaEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jImap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
                        .addComponent(jAutentica))
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(0, 8, Short.MAX_VALUE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jEmailEmp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jSenhaEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPop)
                    .addComponent(jImap)
                    .addComponent(jAutentica))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(".:: SSH DO SITE ::."));

        jLabel33.setText("Endereço:");

        jLabel34.setText("DbName:");

        jSSH_Conta.setText("jTextField5");

        jLabel35.setText("Usuário:");

        jSSH_Usuario.setText("jTextField6");

        jLabel36.setText("Senha:");

        jSSH_Senha.setText("jTextField7");

        jSSH_DbName.setText("jTextField6");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jSSH_Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel36))
                    .addComponent(jSSH_Conta))
                .addGap(6, 6, 6)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSSH_Senha, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSSH_DbName, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel34)
                    .addComponent(jSSH_Conta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSSH_DbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(jSSH_Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(jSSH_Senha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder("Mensagens do Boleto"));

        jLabel37.setText("Linha1:");

        jLabel38.setText("Linha2:");

        jLabel39.setText("Linha3:");

        jLabel40.setText("Linha4:");

        jLabel41.setText("Linha5:");

        jLabel42.setText("Linha6:");

        jLabel43.setText("Linha7:");

        jLabel44.setForeground(new java.awt.Color(255, 0, 51));
        jLabel44.setText("Linha8:");

        jLabel45.setForeground(new java.awt.Color(255, 0, 51));
        jLabel45.setText("Linha9:");

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("R E C I B O  D O  S A C A D O");
        jLabel46.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 5, true));

        jLabel47.setText("Mensagem:");

        jcbAniversario.setText("Aniversário Automático");
        jcbAniversario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbAniversarioActionPerformed(evt);
            }
        });

        jLabel48.setText("Msg Aniversário:");

        jLinhaMsgAniversário.setText("FELIZ ANIVERSÁRIO!");

        jcbComemorativas.setText("Datas Comemorativas");

        btSaveMsg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        btSaveMsg.setText("Salvar Mensagens");
        btSaveMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveMsgActionPerformed(evt);
            }
        });

        jLabel49.setText("Cab1:");

        jLabel50.setText("Cab2:");

        jLabel51.setText("Especie Documento:");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel44)
                            .addComponent(jLabel45)
                            .addComponent(jLabel43)
                            .addComponent(jLabel42)
                            .addComponent(jLabel41)
                            .addComponent(jLabel40)
                            .addComponent(jLabel39)
                            .addComponent(jLabel37)
                            .addComponent(jLabel38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLinha1)
                            .addComponent(jLinha2, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLinha3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLinha8, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLinha4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                                .addComponent(jLinha5, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLinha6, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLinha7, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLinha9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLinha10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                        .addComponent(jcbComemorativas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btSaveMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jcbAniversario)
                        .addGap(45, 45, 45)
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLinhaMsgAniversário))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCab1))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCab2))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCabDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel22Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLinha2, jLinha3, jLinha4});

        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jCab1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(jCab2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(jCabDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jLinha1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(jLinha2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLinha3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addGap(5, 5, 5)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(jLinha4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(jLinha5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(jLinha6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(jLinha7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(jLinha8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(jLinha9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(jLinha10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbAniversario)
                    .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLinhaMsgAniversário, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel48)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbComemorativas)
                    .addComponent(btSaveMsg))
                .addContainerGap())
        );

        btSaveEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        btSaveEmail.setText("Salvar Configurações");
        btSaveEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveEmailActionPerformed(evt);
            }
        });

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(".:: FTP DO SITE ::."));

        jLabel52.setText("Endereço:");

        jLabel53.setText("Porta:");

        jFTP_Conta.setText("jTextField5");

        jLabel54.setText("Usuário:");

        jFTP_Usuario.setText("jTextField6");

        jLabel55.setText("Senha:");

        jFTP_Senha.setText("jTextField7");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel52)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jFTP_Usuario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFTP_Senha, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jFTP_Conta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFTP_Porta, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jLabel53)
                    .addComponent(jFTP_Porta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFTP_Conta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(jFTP_Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55)
                    .addComponent(jFTP_Senha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btSaveEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btSaveEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane4.setViewportView(jPanel21);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Configurar Serviços de Correio e Transmissão", jPanel16);

        jbtGravarParametros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        jbtGravarParametros.setText("Gravar Parametros");
        jbtGravarParametros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtGravarParametrosActionPerformed(evt);
            }
        });

        jSeparator1.setForeground(new java.awt.Color(1, 1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtGravarParametros, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jAbasCalculos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 774, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 772, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAbasCalculos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtGravarParametros)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtGravarParametrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtGravarParametrosActionPerformed
        try {
            String[][] param = {{"TALUG","LOGICO",(jTALUG.isSelected() ? "TRUE" : "FALSE")},
                {"TBRLIQ","LOGICO",(jTBRLIQ.isSelected() ? "TRUE" : "FALSE")},
                {"TCORRECAO","LOGICO",(jTCORRECAO.isSelected() ? "TRUE" : "FALSE")},
                {"TJUROS","LOGICO",(jTJUROS.isSelected() ? "TRUE" : "FALSE")},
                {"TMULTA","LOGICO",(jTMULTA.isSelected() ? "TRUE" : "FALSE")},
                {"TSEGURO","LOGICO",(jTSEGURO.isSelected() ? "TRUE" : "FALSE")},
                {"TTAXA","LOGICO",(jTTAXA.isSelected() ? "TRUE" : "FALSE")},
                {"MALUG","LOGICO",(jMALUG.isSelected() ? "TRUE" : "FALSE")},
                {"MCORRECAO","LOGICO",(jMCORRECAO.isSelected() ? "TRUE" : "FALSE")},
                {"MEXPE","LOGICO",(jMEXPE.isSelected() ? "TRUE" : "FALSE")},
                {"MJUROS","LOGICO",(jMJUROS.isSelected() ? "TRUE" : "FALSE")},
                {"MTAXA","LOGICO",(jMTAXA.isSelected() ? "TRUE" : "FALSE")},
                {"JALUG","LOGICO",(jJALUG.isSelected() ? "TRUE" : "FALSE")},
                {"JCORRECAO","LOGICO",(jJCORRECAO.isSelected() ? "TRUE" : "FALSE")},
                {"JEXPE","LOGICO",(jJEXPE.isSelected() ? "TRUE" : "FALSE")},
                {"JMULTA","LOGICO",(jJMULTA.isSelected() ? "TRUE" : "FALSE")},
                {"JSEGURO","LOGICO",(jJSEGURO.isSelected() ? "TRUE" : "FALSE")},
                {"JTAXA","LOGICO",(jJTAXA.isSelected() ? "TRUE" : "FALSE")},
                {"CALUG","LOGICO",(jCALUG.isSelected() ? "TRUE" : "FALSE")},
                {"CEXPE","LOGICO",(jCEXPE.isSelected() ? "TRUE" : "FALSE")},
                {"CJUROS","LOGICO",(jCJUROS.isSelected() ? "TRUE" : "FALSE")},
                {"CMULTA","LOGICO",(jCMULTA.isSelected() ? "TRUE" : "FALSE")},
                {"CSEGURO","LOGICO",(jCSEGURO.isSelected() ? "TRUE" : "FALSE")},
                {"CTAXA","LOGICO",(jCTAXA.isSelected() ? "TRUE" : "FALSE")}};

            conn.GravarMultiParametros(param);
        } catch (Exception ex) {ex.printStackTrace();}
    }//GEN-LAST:event_jbtGravarParametrosActionPerformed

    private void jbtGravarDadosAdmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtGravarDadosAdmActionPerformed
        GravarDadosAdm();
    }//GEN-LAST:event_jbtGravarDadosAdmActionPerformed

    private void a_btdelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_a_btdelActionPerformed
            Object[] options = { "Sim", "Não" };
            int n = JOptionPane.showOptionDialog(null,
                "Deseja excluir esta Conta ??? ",
                "Atenção", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (n == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM ADM WHERE CODIGO = '&1.' AND DESCR = '&2.'";
                String tcodigo = jContas.getValueAt(jContas.getSelectedRow(), 0).toString();
                String tdescr  = jContas.getValueAt(jContas.getSelectedRow(), 1).toString();
                sql = FuncoesGlobais.Subst(sql, new String[] {tcodigo, tdescr});
                try { conn.ExecutarComando(sql); } catch (Exception e) {}
                
                a_btdel.setEnabled(false);
                
                // Deleta da tabela
                TableControl.del(jContas, jContas.getSelectedRow());
                // Atualiza collections
                VariaveisGlobais.dCliente.remove(tcodigo);
                
                // Refresca tela
                a_sigla.setText(""); a_desc.setText("");
                AtualizaNomes();
                
                jContas.repaint();
            }

    }//GEN-LAST:event_a_btdelActionPerformed

    private void a_btadcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_a_btadcActionPerformed
        tag = "INSERT";
        a_btsave.setEnabled(true);
        
        a_sigla.setText(CTACOD());
        a_desc.setText("");
        a_desc.requestFocus();
    }//GEN-LAST:event_a_btadcActionPerformed

    private void a_btsaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_a_btsaveActionPerformed
        if (tag.equalsIgnoreCase("UPDATE")) {
            String sql = "UPDATE ADM SET DESCR = '&1.' WHERE CODIGO = '&2.'";
            sql = FuncoesGlobais.Subst(sql, new String[] {a_desc.getText().trim(), a_sigla.getText().trim()});
            try { conn.ExecutarComando(sql); } catch (Exception ex) {ex.printStackTrace();}
            // Altera exibicão
            TableControl.alt(jContas, a_desc.getText().trim(), jContas.getSelectedRow(), 1);
            // Atualiza collections
            VariaveisGlobais.dCliente.set(a_sigla.getText().trim(),a_desc.getText().trim());
        } else {
            String sql = "INSERT INTO ADM (CODIGO, DESCR) VALUES ('&1.','&2.')";
            sql = FuncoesGlobais.Subst(sql, new String[] {a_sigla.getText().trim(), a_desc.getText().trim()});
            try { 
                conn.ExecutarComando(sql); 
                conn.GravarParametros(new String[] {"CONTAS",a_sigla.getText().trim(),"TEXTO"});

                // Inclui exibicão
                TableControl.add(jContas, new String[][] {{a_sigla.getText().trim(), a_desc.getText().trim()},{"C","L"}},true);
                // Atualiza collections
                VariaveisGlobais.dCliente.add(a_sigla.getText().trim(),a_desc.getText().trim());                
            } catch (Exception ex) {ex.printStackTrace();}
        }
        tag = "UPDATE";
        AtualizaNomes();
        jContas.repaint();
    }//GEN-LAST:event_a_btsaveActionPerformed

    private void e_btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_e_btupdateActionPerformed
        GravarCampos();
    }//GEN-LAST:event_e_btupdateActionPerformed

    private void e_btdelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_e_btdelActionPerformed
        ApagaRegra();
        LerCampos();
    }//GEN-LAST:event_e_btdelActionPerformed

    private void c_btgravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_btgravarActionPerformed
        try {
            String prvarch = FuncoesGlobais.GravaValor(c_vrmaxch.getText().trim());
            conn.GravarParametros(new String[] {"MAXCH",prvarch,"TEXTO"});
        } catch (Exception ex) {ex.printStackTrace();}
    }//GEN-LAST:event_c_btgravarActionPerformed

    private void jFeriadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jFeriadosMouseClicked
        int prows = jFeriados.getRowCount();
        jbtDel.setEnabled(prows > 0);
    }//GEN-LAST:event_jFeriadosMouseClicked

    private void btAdcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAdcActionPerformed
        String pdia = FuncoesGlobais.StrZero(jDia.getValue().toString(),2);
        String tmes = jMes.getValue().toString();
        String pmes = null;
        if (tmes.equalsIgnoreCase("janeiro")) {
            pmes = "01";
        } else if (tmes.equalsIgnoreCase("fevereiro")) {
            pmes = "02";
        } else if (tmes.equalsIgnoreCase("março")) {
            pmes = "03";
        } else if (tmes.equalsIgnoreCase("abril")) {
            pmes = "04";
        } else if (tmes.equalsIgnoreCase("maio")) {
            pmes = "05";
        } else if (tmes.equalsIgnoreCase("junho")) {
            pmes = "06";
        } else if (tmes.equalsIgnoreCase("julho")) {
            pmes = "07";
        } else if (tmes.equalsIgnoreCase("agosto")) {
            pmes = "08";
        } else if (tmes.equalsIgnoreCase("setembro")) {
            pmes = "09";
        } else if (tmes.equalsIgnoreCase("outubro")) {
            pmes = "10";
        } else if (tmes.equalsIgnoreCase("novembro")) {
            pmes = "11";
        } else if (tmes.equalsIgnoreCase("dezembro")) {
            pmes = "12";
        }
        
        String ttipo = jTipo.getValue().toString();
        String ptipo;
        if (ttipo.equalsIgnoreCase("feriado")) {
            ptipo = "F";
        } else if (ttipo.equalsIgnoreCase("anuncio")) {
            ptipo = "A";
        }  else {
            ptipo = "N";
        }
        
        String pdesc = jDesc.getText();
        String pdias = FuncoesGlobais.StrZero(jDiasAnt.getValue().toString(),2);
        String pano = Dates.DateFormata("yyyy/MM/dd",new Date()).substring(0, 4);
        
        if (TableControl.seek(jFeriados, 0, pdia + "-" + pmes + "-" + pano) < 0) {
            String sql = "INSERT INTO feriados (dia, mes, descricao, tipo, dias) VALUES ('&1.','&2.','&3.','&4.','&5.');";
            sql = FuncoesGlobais.Subst(sql, new String[] {pdia, pmes, pdesc, ptipo, pdias });
            try { conn.ExecutarComando(sql); } catch (Exception ex) {}
            
            String pdata = pdia + "-" + pmes + "-" + pano;
            TableControl.add(jFeriados, new String[][]{{pdata, pdesc, ptipo, pdias},{"C","L","C","C"}}, true);
        } else {
            JOptionPane.showMessageDialog(null, "Data já cadastrada!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }//GEN-LAST:event_btAdcActionPerformed

    private void jbtDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtDelActionPerformed
        String sql = "DELETE FROM feriados WHERE dia = '&1.' AND mes = '&2.'";
        int prow = jFeriados.getSelectedRow();
        String pdia = jFeriados.getValueAt(prow, 0).toString().substring(0,2);
        String pmes = jFeriados.getValueAt(prow, 0).toString().substring(3,5);
        sql = FuncoesGlobais.Subst(sql, new String[] {pdia, pmes});
        try { conn.ExecutarComando(sql); } catch (Exception ex) {}
        jbtDel.setEnabled(false);
        TableControl.del(jFeriados, prow);
    }//GEN-LAST:event_jbtDelActionPerformed

    private void jbcoAdcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbcoAdcActionPerformed
        bcoAdc();
    }//GEN-LAST:event_jbcoAdcActionPerformed

    private void jbcoDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbcoDelActionPerformed
        bcoDel();
    }//GEN-LAST:event_jbcoDelActionPerformed

    private void jbtReajusteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtReajusteActionPerformed
        try {
            String[][] param = {{"REAJNUM","NUMERICO",jspReajNumero.getValue().toString()},
                {"REAJTIPO","TEXTO",jcbReajTipo.getSelectedItem().toString().trim().toUpperCase()}};
            conn.GravarMultiParametros(param);
        } catch (Exception ex) {ex.printStackTrace();}        
    }//GEN-LAST:event_jbtReajusteActionPerformed

    private void btextMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btextMaxActionPerformed
        try {
            String prvarch = FuncoesGlobais.GravaValor(extMax.getText().trim());
            conn.GravarParametros(new String[] {"EXTMAX",prvarch,"TEXTO"});
        } catch (Exception ex) {ex.printStackTrace();}
    }//GEN-LAST:event_btextMaxActionPerformed

    private void jAutenticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAutenticaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jAutenticaActionPerformed

    private void btSaveEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveEmailActionPerformed
        try {
            GravarEmailSettings();
        } catch (SQLException ex) {}
    }//GEN-LAST:event_btSaveEmailActionPerformed

    private void jcbAniversarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAniversarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbAniversarioActionPerformed

    private void btSaveMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveMsgActionPerformed
        try {
            GravarMensagens();
        } catch (SQLException ex) {}
    }//GEN-LAST:event_btSaveMsgActionPerformed

    private void LerMensagens() throws SQLException {
        try {
            jCab1.setText(conn.LerParametros("MSGCABBOL1"));
            jCab2.setText(conn.LerParametros("MSGCABBOL2"));
            jCabDoc.setText(conn.LerParametros("MSGCABBOLDOC"));

            jLinha1.setText(conn.LerParametros("MSGBOL1"));
            jLinha2.setText(conn.LerParametros("MSGBOL2"));
            jLinha3.setText(conn.LerParametros("MSGBOL3"));
            jLinha4.setText(conn.LerParametros("MSGBOL4"));
            jLinha5.setText(conn.LerParametros("MSGBOL5"));
            jLinha6.setText(conn.LerParametros("MSGBOL6"));
            jLinha7.setText(conn.LerParametros("MSGBOL7"));
            jLinha8.setText(conn.LerParametros("MSGBOL8"));
            jLinha9.setText(conn.LerParametros("MSGBOL9"));

            jLinha10.setText(conn.LerParametros("MSGBOL10"));
            jLinhaMsgAniversário.setText(conn.LerParametros("MSGANIVERSARIO"));

            jcbAniversario.setSelected(("TRUE".equals(conn.LerParametros("ANIVERSARIO").toUpperCase()) ? true : false));
            jcbComemorativas.setSelected(("TRUE".equals(conn.LerParametros("FERIADOS").toUpperCase()) ? true : false));
        } catch (Exception ex) {}
    }

    private void GravarMensagens() throws SQLException {
        conn.GravarParametros(new String[] {"MSGCABBOL1", jCab1.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGCABBOL2", jCab2.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGCABBOLDOC", jCabDoc.getText(), "TEXTO"});

        conn.GravarParametros(new String[] {"MSGBOL1", jLinha1.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL2", jLinha2.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL3", jLinha3.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL4", jLinha4.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL5", jLinha5.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL6", jLinha6.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL7", jLinha7.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL8", jLinha8.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL9", jLinha9.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGBOL10", jLinha10.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"MSGANIVERSARIO", jLinhaMsgAniversário.getText(), "TEXTO"});
        conn.GravarParametros(new String[] {"ANIVERSARIO", (jcbAniversario.isSelected() ? "TRUE" : "FALSE"), "LOGICO"});
        conn.GravarParametros(new String[] {"FERIADOS", (jcbComemorativas.isSelected() ? "TRUE" : "FALSE"), "LOGICO"});
    }
    
    private void LerEmailSettings() throws SQLException {
        jEmailEmp.setText(conn.LerParametros("EMAIL"));
        jSenhaEmail.setText(conn.LerParametros("EMAILSENHA"));
        jPop.setSelected("TRUE".equals(conn.LerParametros("POP")) ? true : false);
        jAutentica.setSelected("TRUE".equals(conn.LerParametros("EMAILAUTENTICA")) ? true : false);
        jEndPopImap.setText(conn.LerParametros("POPIMAP"));
        jPortPopImap.setValue(Integer.valueOf(conn.LerParametros("POPIMAPPORT")));
        jSmtp.setText(conn.LerParametros("SMTP"));
        jPortSmtp.setValue(Integer.valueOf(conn.LerParametros("SMTPPORT")));

        jFTP_Conta.setText(conn.LerParametros("FTPCONTA"));
        
        String ftpPort = conn.LerParametros("FTPPORTA");
        int ftpport = 22;
        if (ftpPort == null) {
            ftpport = Integer.valueOf(ftpPort);
        }
        jFTP_Porta.setValue(ftpport);
        jFTP_Usuario.setText(conn.LerParametros("FTPUSUARIO"));
        jFTP_Senha.setText(conn.LerParametros("FTPSENHA"));
        
        jSSH_Conta.setText(conn.LerParametros("siteIP"));
        jSSH_Usuario.setText(conn.LerParametros("siteUser"));
        jSSH_Senha.setText(conn.LerParametros("sitePwd"));
        jSSH_DbName.setText(conn.LerParametros("siteDbName"));
    }
    
    private void GravarEmailSettings() throws SQLException {
        String[][] aPar = {{"EMAIL","TEXTO","" + jEmailEmp.getText()},
            {"EMAILSENHA","TEXTO","" + jSenhaEmail.getText()},
            {"POP","LOGICO",(jPop.isSelected() ? "TRUE" : "FALSE")},
            {"EMAILAUTENTICA","LOGICO",(jAutentica.isSelected() ? "TRUE" : "FALSE")},
            {"POPIMAP","TEXTO","" + jEndPopImap.getText()},
            {"POPIMAPPORT","NUMERICO","" + jPortPopImap.getValue().toString()},
            {"SMTP","TEXTO","" + jSmtp.getText()},
            {"SMTPPORT","NUMERICO","" + jPortSmtp.getValue().toString()},
            {"FTPCONTA", "TEXTO", "" + jFTP_Conta.getText()},
            {"FTPPORTA", "TEXTO", "" + jFTP_Porta.getValue().toString()},
            {"FTPUSUARIO", "TEXTO", "" + jFTP_Usuario.getText()},
            {"FTPSENHA", "TEXTO", "" + jFTP_Senha.getText()},
            {"siteIP", "TEXTO", "" + jSSH_Conta.getText()},
            {"siteUser", "TEXTO", "" + jSSH_Usuario.getText()},
            {"sitePwd", "TEXTO", "" + jSSH_Senha.getText()},
            {"siteDbName", "TEXTO", "" + jSSH_DbName.getText()}
        };

        if (!conn.GravarMultiParametros(aPar)) {
            JOptionPane.showMessageDialog(null, "Nào foi possível realizar a gravação!!!\nTente novamente...", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String CTACOD() {
        String sCod = "";
        char sPt1;
        char sPt2;
        try { sCod = conn.LerParametros("CONTAS"); } catch (Exception ex) {}
        if (sCod.trim().equals("")) {
            sCod = "A1";
        } else {
            sPt1 = sCod.charAt(0);
            sPt2 = sCod.charAt(1);
            
            if (sPt2 + 1 > 57) {
                sPt2 = '1';
                sPt1 = (char)((int)sPt1 + 1);
                if (sPt1 == 'Z') sPt1 = 'A';
            } else {
                sPt2 = (char)((int)sPt2 + 1);
            }
            
            sCod = "" + sPt1 + sPt2;
        }
        
        return sCod;
    }
    
    private boolean InsereCampos() throws SQLException {
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
                  "LANCART.CART_EXTRATO, LANCART.CART_IMPOSTO FROM LANCART " + sqlWhere;

        String cartCampos = null; String stCAMPOS = null; String mSql = null;
        cartCampos = CarregaCamposCarteira(sqlSelect);

        if (!"".equals(cartCampos.trim())) {
            stCAMPOS = VariaveisGlobais.ccampos + ";" + cartCampos;
            if (stCAMPOS.substring(0, 1).equals(";")) {
              stCAMPOS = stCAMPOS.substring(1);
            }

            VariaveisGlobais.ccampos = stCAMPOS;
            bRet = true;
        }

        return bRet;
    }
    
    public String CarregaCamposCarteira(String cSql) throws SQLException {
        ResultSet Data1 = conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
        String mCampos = "";

        while (Data1.next()) {
            mCampos = mCampos + Data1.getString("CART_CODIGO") + ":" +
                       Data1.getString("CART_ORDEM") + ":" +
                       "0000000000" + ":" +
                       "0000" + ":" +
                       ("1".equals(Data1.getString("CART_TAXA")) ? "NT" : "AL") + ":" +
                       ("1".equals(Data1.getString("CART_RETENCAO")) ? "RT" : "") + ":" +
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
    
    private void DesMontaTela() {
        jctCampo.removeAll();
        jcbMU.setSelected(false);
        jsmMU.setSelected(false);
        jcbJU.setSelected(false);
        jsmJU.setSelected(false);
        jcbCO.setSelected(false);
        jsmCO.setSelected(false);
        jcbEP.setSelected(false);
        jsmEP.setSelected(false);
        jcbSG.setSelected(false);
        jsmSG.setSelected(false);
        jcbDC.setSelected(false);
        jsmDC.setSelected(false);
        jcbDF.setSelected(false);
        jsmDF.setSelected(false);        
    }

    private void MontaTela() throws SQLException {
        String nCampos = VariaveisGlobais.ccampos;

        if (!"".equals(nCampos.trim())) {
            DepuraCampos a = new DepuraCampos(nCampos);

            a.SplitCampos();
            // Ordena Matriz
            Arrays.sort (a.aCampos, new Comparator()
            {
            private int pos1 = 0;
            private int pos2 = 2;
            public int compare(Object o1, Object o2) {
                String p1 = ((String)o1).substring(pos1, pos2);
                String p2 = ((String)o2).substring(pos1, pos2);
                return p1.compareTo(p2);
            }
            });

            int i = 0;
            for (i=0; i<= a.length() - 1; i++) {
                String[] Campo = a.Depurar_withcod(i);
                if (Campo.length > 0) {
                    MontaCampos(Campo, i);
                }
            }
            mCartVazio = false;
            
            if (regras != null) {
                String[] rgCampos = regras.split(";");
                // Aqui entra a segunad parte da atualização
                int npos = FuncoesGlobais.IndexOf(rgCampos, "MU" + ":");
                if ( npos > -1) {
                    jcbMU.setSelected(rgCampos[npos].split(":")[1].equals("1"));
                    jsmMU.setSelected(rgCampos[npos].split(":")[2].equals("1"));
                }

                npos = FuncoesGlobais.IndexOf(rgCampos, "JU" + ":");
                if ( npos > -1) {
                    jcbJU.setSelected(rgCampos[npos].split(":")[1].equals("1"));
                    jsmJU.setSelected(rgCampos[npos].split(":")[2].equals("1"));
                }

                npos = FuncoesGlobais.IndexOf(rgCampos, "CO" + ":");
                if ( npos > -1) {
                    jcbCO.setSelected(rgCampos[npos].split(":")[1].equals("1"));
                    jsmCO.setSelected(rgCampos[npos].split(":")[2].equals("1"));
                }

                npos = FuncoesGlobais.IndexOf(rgCampos, "EP" + ":");
                if ( npos > -1) {
                    jcbEP.setSelected(rgCampos[npos].split(":")[1].equals("1"));
                    jsmEP.setSelected(rgCampos[npos].split(":")[2].equals("1"));
                }

                npos = FuncoesGlobais.IndexOf(rgCampos, "SG" + ":");
                if ( npos > -1) {
                    jcbSG.setSelected(rgCampos[npos].split(":")[1].equals("1"));
                    jsmSG.setSelected(rgCampos[npos].split(":")[2].equals("1"));
                }

                npos = FuncoesGlobais.IndexOf(rgCampos, "DC" + ":");
                if ( npos > -1) {
                    jcbDC.setSelected(rgCampos[npos].split(":")[1].equals("1"));
                    jsmDC.setSelected(rgCampos[npos].split(":")[2].equals("1"));
                }

                npos = FuncoesGlobais.IndexOf(rgCampos, "DF" + ":");
                if ( npos > -1) {
                    jcbDF.setSelected(rgCampos[npos].split(":")[1].equals("1"));
                    jsmDF.setSelected(rgCampos[npos].split(":")[2].equals("1"));
                }
            }
        } else {
            VariaveisGlobais.ccampos = "";
            mCartVazio = false;
        }
    }

    private void MontaCampos(String[] aCampos, int i) {
        int at = 20; int llg = 100; int ltf = 80; int lcp = 60; int lcc = 180;

        JLabel lb = new JLabel();
        lb.setText(aCampos[1]);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(Color.BLACK);
        lb.setBounds(0, 0 + (at * i), llg, at);
        lb.setName("Label" + i);
        jctCampo.add(lb);

        JCheckBox cb = new JCheckBox();
        cb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        if (regras != null) {
            cb.setSelected((FuncoesGlobais.IndexOf(regras.split(";"), aCampos[0].trim() + ":") > -1));
        } else cb.setSelected(("0".equals(aCampos[3]) ? false : true));
        cb.setVisible(true);
        cb.setForeground(Color.BLACK);
        cb.setBounds(lb.getX() + llg + 5, 0 + (at * i), at, at);
        cb.setName("Check" + i);
        jctCampo.add(cb);
    }
    
    private void GravarCampos() {
        if ("".equals(VariaveisGlobais.ccampos.trim())) return;

        String cCampos[] = VariaveisGlobais.ccampos.split(";");
        Arrays.sort (cCampos, new Comparator()
        {
            private int pos1 = 0;
            private int pos2 = 2;
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
            } else if (nmCampo.contains("Cota")) {
                cCampos[j] = ChangeCampos(cCampos[j], 3, saida);
            } else if (nmCampo.contains("Barras")) {
                cCampos[j] = ChangeCampos(cCampos[j], -1, saida);
            }

            int mod = (i + 1) % 2;
            if (mod == 0) {
                j++;
            }
        }

        String[] campos_1via = {};
        for (int z=0;z<cCampos.length;z++) {
            int pos = FuncoesGlobais.IndexOf(cCampos[z].split(":"), "RT");
            if (pos > -1) campos_1via = FuncoesGlobais.ArrayAdd(campos_1via, cCampos[z].substring(0, 3) + "1:X" );
        }
        
        if (jcbMU.isSelected()) {
            campos_1via = FuncoesGlobais.ArrayAdd(campos_1via, "MU:" + "1:" + (jsmMU.isSelected() ? "1" : "0"));
        }
        if (jcbJU.isSelected()) {
            campos_1via = FuncoesGlobais.ArrayAdd(campos_1via, "JU:" + "1:" + (jsmJU.isSelected() ? "1" : "0"));
        }
        if (jcbCO.isSelected()) {
            campos_1via = FuncoesGlobais.ArrayAdd(campos_1via, "CO:" + "1:" + (jsmCO.isSelected() ? "1" : "0"));
        }
        if (jcbEP.isSelected()) {
            campos_1via = FuncoesGlobais.ArrayAdd(campos_1via, "EP:" + "1:" + (jsmEP.isSelected() ? "1" : "0"));
        }
        if (jcbSG.isSelected()) {
            campos_1via = FuncoesGlobais.ArrayAdd(campos_1via, "SG:" + "1:" + (jsmSG.isSelected() ? "1" : "0"));
        }
        if (jcbDC.isSelected()) {
            campos_1via = FuncoesGlobais.ArrayAdd(campos_1via, "DC:" + "1:" + (jsmDC.isSelected() ? "1" : "0"));
        }
        if (jcbDF.isSelected()) {
            campos_1via = FuncoesGlobais.ArrayAdd(campos_1via, "DF:" + "1:" + (jsmDF.isSelected() ? "1" : "0"));
        }
        
        String protocol = FuncoesGlobais.join(campos_1via, ";");
        if (!protocol.trim().equals("")) {
            try {
                conn.GravarParametros(new String[] {"REGRAS",protocol,"TEXTO"});
                regras = protocol;
            } catch (Exception ex) {ex.printStackTrace();}            
        } else ApagaRegra();
    }

    private void ApagaRegra() {
        try {
            conn.ExecutarComando("DELETE FROM PARAMETROS WHERE Lower(Trim(variavel)) = 'REGRAS';" );
        } catch (Exception ex) {ex.printStackTrace();}
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

    private void ListaContasBoletas() {
        String sql = "SELECT id, agencia, conta, conta_dv, nbanco, nbancodv, carteira, moeda, tarifa, nnumero FROM contas_boletas ORDER BY id;";

        TableControl.Clear(jtblBcos);
        // Seta Cabecario
        TableControl.header(jtblBcos, new String[][] {
            {"id", "Agencia", "Conta", "DV", "N.Banco", "DV", "Carteira", "moeda", "tarifa", "N.Numero"},
            {"15","80","100","30","80","20","80","60","80","100"}
        });

        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String pid = rs.getString("id");
                String pagencia = rs.getString("agencia");
                String pconta =  rs.getString("conta");
                String pcontadv =  rs.getString("conta_dv");
                String pnbanco =  rs.getString("nbanco");
                String pnbancodv = rs.getString("nbancodv");
                String pcarteira =  rs.getString("carteira");
                String pmoeda =  rs.getString("moeda");
                String ptarifa =  rs.getString("tarifa");
                String pnnumero =  rs.getString("nnumero");
                TableControl.add(jtblBcos, new String[][]{
                    {pid, pagencia, pconta, pcontadv, pnbanco, pnbancodv, pcarteira, pmoeda, ptarifa, pnnumero},
                    {"C","L","L","C","L","L","C","R","R"}
                }, true);
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        DbMain.FecharTabela(rs);
        jbarra.setValue(100);
    }
    
    private void bcoAdc() {
        String tagencia = jAgencia.getText();
        String tconta = jConta.getText();
        String tcontadv = jCtaDv.getText();
        String tnbanco = jbanco.getText();
        String tnbancodv = jbancoDv.getText();
        String tcarteira = jcarteira.getText();
        String tmoeda = jmoeda.getText();
        String ttarifa = jtarifa.getText();
        String tnnumero = jNossoNumero.getText();
        
        String sql = "INSERT INTO contas_boletas (agencia, conta, conta_dv, nbanco, nbancodv, carteira, moeda, tarifa, nnumero) VALUES ('" + tagencia + "','" +
                tconta + "','" + tcontadv + "','" + tnbanco + "','" + tnbancodv + "','" +
                tcarteira + "','" + tmoeda + "','" + ttarifa + "','" + tnnumero + "');";
        try {
            conn.ExecutarComando(sql);
        } catch (Exception e) {e.printStackTrace();}
        
        jAgencia.setText("");
        jConta.setText("");
        jCtaDv.setText("");
        jbanco.setText("");
        jbancoDv.setText("");
        jcarteira.setText("");
        jmoeda.setText("");
        jtarifa.setText("");
        jNossoNumero.setText("");
        
        ListaContasBoletas();
    }
    
    private void bcoDel() {
        int pos = jtblBcos.getSelectedRow();
        if (pos > -1) {
            String tid = jtblBcos.getModel().getValueAt(pos, 0).toString().trim();
            String sql = "DELETE FROM contas_boletas WHERE id = '" + tid + "';";
            try { conn.ExecutarComando(sql); } catch (Exception e) {e.printStackTrace();}
        }
        ListaContasBoletas();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton a_btadc;
    private javax.swing.JButton a_btdel;
    private javax.swing.JButton a_btsave;
    private javax.swing.JTextField a_desc;
    private javax.swing.JTextField a_sigla;
    private javax.swing.JButton btAdc;
    private javax.swing.JButton btSaveEmail;
    private javax.swing.JButton btSaveMsg;
    private javax.swing.JButton btextMax;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton c_btgravar;
    private javax.swing.JFormattedTextField c_vrmaxch;
    private javax.swing.JTextField e_BAIRRO;
    private javax.swing.JFormattedTextField e_CEP;
    private javax.swing.JTextField e_CIDADE;
    private javax.swing.JFormattedTextField e_CNPJ;
    private javax.swing.JTextField e_COMPL;
    private javax.swing.JTextField e_EMAIL;
    private javax.swing.JTextField e_END;
    private javax.swing.JTextField e_ESTADO;
    private javax.swing.JTextField e_HPAGE;
    private javax.swing.JTextField e_INSC;
    private javax.swing.JTextField e_LOGO;
    private javax.swing.JTextField e_MARCA;
    private javax.swing.JTextField e_NOME;
    private javax.swing.JTextField e_NUM;
    private javax.swing.JTextField e_TELEFONE;
    private javax.swing.JTextField e_TIPO;
    private javax.swing.JButton e_btdel;
    private javax.swing.JButton e_btupdate;
    private javax.swing.JFormattedTextField extMax;
    private javax.swing.JTabbedPane jAbasCalculos;
    private javax.swing.JTextField jAgencia;
    private javax.swing.JCheckBox jAutentica;
    private javax.swing.JCheckBox jCALUG;
    private javax.swing.JCheckBox jCEXPE;
    private javax.swing.JCheckBox jCJUROS;
    private javax.swing.JCheckBox jCMULTA;
    private javax.swing.JLabel jCO;
    private javax.swing.JCheckBox jCSEGURO;
    private javax.swing.JCheckBox jCTAXA;
    private javax.swing.JTextField jCab1;
    private javax.swing.JTextField jCab2;
    private javax.swing.JTextField jCabDoc;
    private javax.swing.JTextField jConta;
    private javax.swing.JTable jContas;
    private javax.swing.JTextField jCtaDv;
    private javax.swing.JLabel jDC;
    private javax.swing.JLabel jDF;
    private javax.swing.JTextField jDesc;
    private javax.swing.JSpinner jDia;
    private javax.swing.JSpinner jDiasAnt;
    private javax.swing.JLabel jEP;
    private javax.swing.JTextField jEmailEmp;
    private javax.swing.JTextField jEndPopImap;
    private javax.swing.JTextField jFTP_Conta;
    private javax.swing.JSpinner jFTP_Porta;
    private javax.swing.JTextField jFTP_Senha;
    private javax.swing.JTextField jFTP_Usuario;
    private javax.swing.JTable jFeriados;
    private javax.swing.JRadioButton jImap;
    private javax.swing.JCheckBox jJALUG;
    private javax.swing.JCheckBox jJCORRECAO;
    private javax.swing.JCheckBox jJEXPE;
    private javax.swing.JCheckBox jJMULTA;
    private javax.swing.JCheckBox jJSEGURO;
    private javax.swing.JCheckBox jJTAXA;
    private javax.swing.JLabel jJU;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField jLinha1;
    private javax.swing.JTextField jLinha10;
    private javax.swing.JTextField jLinha2;
    private javax.swing.JTextField jLinha3;
    private javax.swing.JTextField jLinha4;
    private javax.swing.JTextField jLinha5;
    private javax.swing.JTextField jLinha6;
    private javax.swing.JTextField jLinha7;
    private javax.swing.JTextField jLinha8;
    private javax.swing.JTextField jLinha9;
    private javax.swing.JTextField jLinhaMsgAniversário;
    private javax.swing.JCheckBox jMALUG;
    private javax.swing.JCheckBox jMCORRECAO;
    private javax.swing.JCheckBox jMEXPE;
    private javax.swing.JCheckBox jMJUROS;
    private javax.swing.JCheckBox jMTAXA;
    private javax.swing.JLabel jMU;
    private javax.swing.JSpinner jMes;
    private javax.swing.JTextField jNossoNumero;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jPop;
    private javax.swing.JSpinner jPortPopImap;
    private javax.swing.JSpinner jPortSmtp;
    private javax.swing.JLabel jSG;
    private javax.swing.JTextField jSSH_Conta;
    private javax.swing.JTextField jSSH_DbName;
    private javax.swing.JTextField jSSH_Senha;
    private javax.swing.JTextField jSSH_Usuario;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jSenhaEmail;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jSmtp;
    private javax.swing.JCheckBox jTALUG;
    private javax.swing.JCheckBox jTBRLIQ;
    private javax.swing.JCheckBox jTCORRECAO;
    private javax.swing.JCheckBox jTJUROS;
    private javax.swing.JCheckBox jTMULTA;
    private javax.swing.JCheckBox jTSEGURO;
    private javax.swing.JCheckBox jTTAXA;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JSpinner jTipo;
    private javax.swing.JTextField jbanco;
    private javax.swing.JTextField jbancoDv;
    private javax.swing.JProgressBar jbarra;
    private javax.swing.JButton jbcoAdc;
    private javax.swing.JButton jbcoDel;
    private javax.swing.JButton jbtDel;
    private javax.swing.JButton jbtGravarDadosAdm;
    private javax.swing.JButton jbtGravarParametros;
    private javax.swing.JButton jbtReajuste;
    private javax.swing.JTextField jcarteira;
    private javax.swing.JCheckBox jcbAniversario;
    private javax.swing.JCheckBox jcbCO;
    private javax.swing.JCheckBox jcbComemorativas;
    private javax.swing.JCheckBox jcbDC;
    private javax.swing.JCheckBox jcbDF;
    private javax.swing.JCheckBox jcbEP;
    private javax.swing.JCheckBox jcbJU;
    private javax.swing.JCheckBox jcbMU;
    private javax.swing.JComboBox jcbReajTipo;
    private javax.swing.JCheckBox jcbSG;
    private javax.swing.JPanel jctCampo;
    private javax.swing.JTextField jmoeda;
    private javax.swing.JCheckBox jsmCO;
    private javax.swing.JCheckBox jsmDC;
    private javax.swing.JCheckBox jsmDF;
    private javax.swing.JCheckBox jsmEP;
    private javax.swing.JCheckBox jsmJU;
    private javax.swing.JCheckBox jsmMU;
    private javax.swing.JCheckBox jsmSG;
    private javax.swing.JSpinner jspReajNumero;
    private javax.swing.JTextField jtarifa;
    private javax.swing.JTable jtblBcos;
    // End of variables declaration//GEN-END:variables
}
