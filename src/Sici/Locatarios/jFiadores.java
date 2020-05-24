/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jFiadores.java
 *
 * Created on 12/12/2011, 08:13:16
 */

package Sici.Locatarios;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LimitedTextField;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author mariana
 */
public class jFiadores extends javax.swing.JDialog {
    DbMain conn = VariaveisGlobais.conexao;
    ResultSet pResult = conn.AbrirTabela("SELECT * FROM fiadores WHERE contrato = '" + VariaveisGlobais.fcontrato.trim() + "' AND UPPER(nomerazao) = '" + VariaveisGlobais.fnome.toUpperCase() + "' ORDER BY contrato;", ResultSet.CONCUR_UPDATABLE);
    boolean bNew = false;
    private boolean closable;
    
    /** Creates new form jFiadores */
    public jFiadores(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        //Centraliza a janela.
        Dimension dimension = this.getToolkit().getScreenSize();
        int x = (int) (dimension.getWidth() - this.getSize().getWidth() ) / 2;
        int y = (int) (dimension.getHeight() - this.getSize().getHeight()) / 2;
        this.setLocation(x,y);

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        if ("".equals(VariaveisGlobais.fcontrato.trim())) {
            mCpf.setEnabled(true);
            mCnpj.setEnabled(true);
            jDados.setEnabledAt(0, true);
            jDados.setEnabledAt(1, true);
            jDados.setSelectedIndex(0);
            jrbFisica.requestFocus();
        } else {
            try {if (DbMain.RecordCount(pResult) > 0) MoveToFiador("contrato", VariaveisGlobais.fcontrato);} catch (Exception ex) {}
        }
        
        if (VariaveisGlobais.isBloqueado) {
            btIncluir.setEnabled(false);
            btExcluir.setEnabled(false);
            btGravar.setEnabled(false);
        }
    }

    private boolean MoveToFiador(String campo, String seek) throws SQLException {
        boolean achei = false;
        pResult.first();
        while (pResult.next()) {
            if (pResult.getInt(campo) == Integer.parseInt(seek)) {
                achei = true;
                break;
            }
        }
        if (!achei) pResult.first();
        LerDados(false);
        return achei;
    }

    private void ExcluirSocio() throws SQLException {
        int selRow = jSocios.getSelectedRow();
        if (selRow > -1) {
            pResult.updateString("socionome" + (selRow + 1), "");
            pResult.updateString("sociodtnasc" + (selRow + 1), "0000-00-00");
            pResult.updateString("socionac" + (selRow + 1), "");
            pResult.updateString("socioecivil" + (selRow + 1), "");
            pResult.updateString("sociocpf" + (selRow + 1), null);
            pResult.updateString("sociorg" + (selRow + 1), "");
            pResult.updateString("sociosalario" + (selRow + 1), "");
            pResult.updateString("sociocargo" + (selRow + 1), "");
            pResult.updateString("sociomae" + (selRow + 1), "");
            pResult.updateString("sociopai" + (selRow + 1),"");

            pResult.updateRow();
        }
    }

    private void LerDados(boolean bFirst) throws SQLException {

        if (bFirst) pResult.first();
        if (pResult.getString("tploca").toUpperCase().contains("F")) {
            jrbFisica.setSelected(true);
            mCpf.setText(pResult.getString("cpfcnpj"));

            mCpf.setEnabled(true);
            mCnpj.setEnabled(false);
            jDados.setEnabledAt(0, true);
            jDados.setEnabledAt(1, false);
            jDados.setSelectedIndex(0);
            jrbFisica.setEnabled(true);
            jrbJuridica.setEnabled(false);
        } else {
            jrbJuridica.setSelected(true);
            mCnpj.setText(pResult.getString("cpfcnpj"));
            mCpf.setEnabled(false);
            mCnpj.setEnabled(true);
            jDados.setEnabledAt(0, false);
            jDados.setEnabledAt(1, true);
            jrbFisica.setEnabled(false);
            jrbJuridica.setEnabled(true);
        }
        mIdentidade.setText(pResult.getString("rginsc"));

        if (jrbFisica.isSelected()) {
            // Pessoa Física (limpar dados dos campos juridica)
            mfNome.setText(pResult.getString("nomerazao"));
            try {mfDtNasc.setDate(Dates.StringtoDate(pResult.getString("dtnasc"),"yyyy-MM-dd"));} catch (Exception ex) {}
            mfNacionalidade.setText(pResult.getString("naciona"));

            String pCivil = pResult.getString("ecivil").trim().toLowerCase();
            String sCivil[] = {"solteiro","solteira","casado","casada","separado","separada","divorciado","divorciada","viuvo","viuva"};
            Integer nPos = FuncoesGlobais.IndexOf2(sCivil,pCivil) ;
            if (nPos == -1) {
                nPos = 0;
            }
            else if (nPos == 0 || nPos == 1){
                nPos = 0;
            }
            else if (nPos == 2 || nPos == 3) {
                nPos = 1;
            }
            else if (nPos == 4 || nPos == 5) {
                nPos = 2;
            }
            else if (nPos == 6 || nPos == 7) {
                nPos = 3;
            }
            else if (nPos == 8 || nPos == 9 ) {
                nPos = 4;
            }
            else
                nPos = 0;

            mfEstCivil.setSelectedIndex(nPos);

            mfTel1.setText(pResult.getString("celular"));
            // tel2
            mfMae.setText(pResult.getString("mae"));
            mfPai.setText(pResult.getString("pai"));
            mfEmpresa.setText(pResult.getString("empresa"));
            try {mfDtAdmis.setDate(Dates.StringtoDate(pResult.getString("dtadmis"), "yyyy-MM-dd"));} catch (Exception ex) {mfDtAdmis.setDate(null);}
            mfEndereco.setText(pResult.getString("end"));
            mfNumero.setText(pResult.getString("num"));
            mfCplto.setText(pResult.getString("compl"));
            mfBairro.setText(pResult.getString("bairro"));
            mfCidade.setText(pResult.getString("cidade"));
            mfEstado.setText(pResult.getString("estado"));
            mfCep.setText(pResult.getString("cep"));
            mfTelEmpresa.setText(pResult.getString("tel"));
            mfRamalEmpresa.setText(pResult.getString("ramal"));
            mfCargo.setText(pResult.getString("cargo"));
            mfSalario.setText(pResult.getString("salario"));
            mfEmail.setText(pResult.getString("email"));
            mfConjugue.setText(pResult.getString("conjugue"));
            try {mfDtNascConj.setDate(Dates.StringtoDate(pResult.getString("cjdtnasc"), "yyyy-MM-dd"));} catch (Exception ex) {mfDtNascConj.setDate(null);}
            mfCpfConj.setText(pResult.getString("cjcpf"));
            mfIdentidadeConj.setText(pResult.getString("cjrg"));
            mfSalarioConj.setText(pResult.getString("cjsalario"));
            mfEmpresaConj.setText(pResult.getString("cjempresa"));
            mfEmpresaTelConj.setText(pResult.getString("cjtel"));
            mfEmpresaRamalConj.setText(pResult.getString("cjramal"));
        } else {
            // Pessoa Jurica (limpar dados dos campos física)
            mjRazao.setText(pResult.getString("nomerazao"));
            mjFantasia.setText(pResult.getString("fantasia"));
            mjEndereco.setText(pResult.getString("end"));
            mjNumero.setText(pResult.getString("num"));
            mjCplto.setText(pResult.getString("compl"));
            mjBairro.setText(pResult.getString("bairro"));
            mjCidade.setText(pResult.getString("cidade"));
            mjEstado.setText(pResult.getString("estado"));
            mjCep.setText(pResult.getString("cep"));
            mjTelefone.setText(pResult.getString("tel"));
            mjRamal.setText(pResult.getString("ramal"));
            mjCelular.setText(pResult.getString("celular"));
            try {mjDtContratoSocial.setDate(Dates.StringtoDate(pResult.getString("dtnasc"), "yyyy-MM-dd"));} catch (Exception ex) {mjDtContratoSocial.setDate(null);}
            mjEmail.setText(pResult.getString("email"));

            FillSocios(jSocios, pResult);
        }

        mEnderecoEnv.setText(pResult.getString("cor_end"));
        mNumeroEnv.setText(pResult.getString("cor_num"));
        mCpltoEnv.setText(pResult.getString("cor_compl"));
        mBairroEnv.setText(pResult.getString("cor_bairro"));
        mCidadeEnv.setText(pResult.getString("cor_cidade"));
        mEstadoEnv.setText(pResult.getString("cor_estado"));
        mCepEnv.setText(pResult.getString("cor_cep"));

    }

    private void LimpaDados() {
        jrbFisica.setSelected(true);
        mCpf.setText("");
        mCnpj.setText("");

        jDados.setEnabledAt(0, true);
        jDados.setEnabledAt(1, false);
        jDados.setSelectedIndex(0);

        jrbFisica.setEnabled(true);
        jrbJuridica.setEnabled(false);

        mIdentidade.setText("");

        // Pessoa Física (limpar dados dos campos juridica)
        mfNome.setText("");
        mfDtNasc.setDate(null);
        mfNacionalidade.setText("");
        mfEstCivil.setSelectedIndex(0);
        mfTel1.setText("");
        // tel2
        mfMae.setText("");
        mfPai.setText("");
        mfEmpresa.setText("");
        mfDtAdmis.setDate(null);
        mfEndereco.setText("");
        mfNumero.setText("");
        mfCplto.setText("");
        mfBairro.setText("");
        mfCidade.setText("");
        mfEstado.setText("");
        mfCep.setText("");
        mfTelEmpresa.setText("");
        mfRamalEmpresa.setText("");
        mfCargo.setText("");
        mfSalario.setText("");
        mfEmail.setText("");
        mfConjugue.setText("");
        mfDtNascConj.setDate(null);
        mfCpfConj.setText("");
        mfIdentidadeConj.setText("");
        mfSalarioConj.setText("");
        mfEmpresaConj.setText("");
        mfEmpresaTelConj.setText("");
        mfEmpresaRamalConj.setText("");

        // Pessoa Jurica (limpar dados dos campos física)
        mjRazao.setText("");
        mjFantasia.setText("");
        mjEndereco.setText("");
        mjNumero.setText("");
        mjCplto.setText("");
        mjBairro.setText("");
        mjCidade.setText("");
        mjEstado.setText("");
        mjCep.setText("");
        mjTelefone.setText("");
        mjRamal.setText("");
        mjCelular.setText("");
        mjDtContratoSocial.setDate(null);
        mjEmail.setText("");

        TableControl.header(jSocios, new String[][] {{"cpfcnpj","nomerazao","cargo"},{"150","500","150"}});

        mEnderecoEnv.setText("");
        mNumeroEnv.setText("");
        mCpltoEnv.setText("");
        mBairroEnv.setText("");
        mCidadeEnv.setText("");
        mEstadoEnv.setText("");
        mCepEnv.setText("");

    }

    private void GravarDados() throws SQLException {
        if (bNew) {
            pResult.moveToInsertRow();

            pResult.updateInt("rgprp", Integer.parseInt(VariaveisGlobais.frgprp.trim()));
            pResult.updateInt("rgimv", Integer.parseInt(VariaveisGlobais.frgimv.trim()));
            pResult.updateInt("contrato", Integer.parseInt(VariaveisGlobais.fcontrato));
            pResult.updateString("tploca", (jrbFisica.isSelected() ? "F" : "J"));
        }

        if (jrbFisica.isSelected()) {
            pResult.updateString("cpfcnpj", mCpf.getText());
            pResult.updateString("rginsc", mIdentidade.getText());

            pResult.updateString("nomerazao", mfNome.getText());
            try {pResult.updateString("dtnasc", Dates.DateFormata("yyyy-MM-dd",mfDtNasc.getDate()));} catch (Exception ex) {}
            pResult.updateString("naciona", mfNacionalidade.getText());
            pResult.updateString("ecivil", (mfEstCivil.getSelectedItem().toString() + "          ").substring(0, 10));
            pResult.updateString("celular", mfTel1.getText());
            pResult.updateString("mae", mfMae.getText());
            pResult.updateString("pai",mfPai.getText());
            pResult.updateString("empresa", mfEmpresa.getText());


            try {pResult.updateString("dtadmis",Dates.DateFormata("yyyy-MM-dd",mfDtAdmis.getDate()));} catch (Exception ex) {}
            pResult.updateString("end",mfEndereco.getText());
            pResult.updateString("num",mfNumero.getText());
            pResult.updateString("compl", mfCplto.getText());
            pResult.updateString("bairro", mfBairro.getText());
            pResult.updateString("cidade", mfCidade.getText());
            pResult.updateString("estado", mfEstado.getText());
            pResult.updateString("cep", mfCep.getText());
            pResult.updateString("tel", mfTelEmpresa.getText());
            pResult.updateString("ramal", mfRamalEmpresa.getText());
            pResult.updateString("cargo", mfCargo.getText());
            pResult.updateString("salario", mfSalario.getText());
            pResult.updateString("email", mfEmail.getText());
            pResult.updateString("conjugue", mfConjugue.getText());

            try {pResult.updateString("cjdtnasc", Dates.DateFormata("yyyy-MM-dd",mfDtNascConj.getDate()));} catch (Exception ex) {}
            pResult.updateString("cjcpf", mfCpfConj.getText());
            pResult.updateString("cjrg", mfIdentidadeConj.getText());
            pResult.updateString("cjsalario", mfSalarioConj.getText());
            pResult.updateString("cjempresa", mfEmpresaConj.getText());
            pResult.updateString("cjtel", mfEmpresaTelConj.getText());
            pResult.updateString("cjramal", mfEmpresaRamalConj.getText());
        } else {
            pResult.updateString("cpfcnpj", mCnpj.getText());
            pResult.updateString("rginsc", mIdentidade.getText());

            pResult.updateString("nomerazao", mjRazao.getText());
            pResult.updateString("fantasia", mjFantasia.getText());
            pResult.updateString("end", mjEndereco.getText());
            pResult.updateString("num", mjNumero.getText());
            pResult.updateString("compl", mjCplto.getText());
            pResult.updateString("bairro", mjBairro.getText());
            pResult.updateString("cidade", mjCidade.getText());
            pResult.updateString("estado", mjEstado.getText());
            pResult.updateString("cep", mjCep.getText());
            pResult.updateString("tel", mjTelefone.getText());
            pResult.updateString("ramal", mjRamal.getText());
            pResult.updateString("celular", mjCelular.getText());

            try {pResult.updateString("dtnasc", Dates.DateFormata("yyyy-MM-dd",mjDtContratoSocial.getDate()));} catch (Exception ex) {}

            pResult.updateString("email", mjEmail.getText());
        }

        pResult.updateString("cor_end",mEnderecoEnv.getText());
        pResult.updateString("cor_num", mNumeroEnv.getText());
        pResult.updateString("cor_compl",mCpltoEnv.getText());
        pResult.updateString("cor_bairro",mBairroEnv.getText());
        pResult.updateString("cor_cidade", mCidadeEnv.getText());
        pResult.updateString("cor_estado", mEstadoEnv.getText());
        pResult.updateString("cor_cep", mCepEnv.getText());

        if (bNew) {
            pResult.insertRow();
        } else {
            pResult.updateRow();
        }

    }

    private void FillSocios(JTable table, ResultSet sResult) {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"cpfcnpj","nomerazao","cargo"},{"150","500","150"}});

        int i = 0;
        for (i=1;i<=4;i++) {
            String sCpfCnpj = null;
            try {
                sCpfCnpj = sResult.getString("sociocpf" + Integer.toString(i));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String sNomeRazao = null;
            try {
                sNomeRazao = sResult.getString("socionome" + Integer.toString(i));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String sCargo = null;
            try {
                sCargo = sResult.getString("sociocargo" + Integer.toString(i));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (sCpfCnpj != null) {
                TableControl.add(table, new String[][]{{sCpfCnpj, sNomeRazao, sCargo},{"C","L","L"}}, true);
            }
        }

        return;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jDadosIniciais = new javax.swing.JPanel();
        jrbFisica = new javax.swing.JRadioButton();
        jrbJuridica = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        mIdentidade = new LimitedTextField(20);
        mCpf = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        mCnpj = new javax.swing.JFormattedTextField();
        jDados = new javax.swing.JTabbedPane();
        jFisica = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        mfNome = new LimitedTextField(60);
        jLabel9 = new javax.swing.JLabel();
        mfDtNasc = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        mfNacionalidade = new LimitedTextField(25);
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        mfEstCivil = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        mfTel2 = new javax.swing.JFormattedTextField();
        mfTel1 = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        mfMae = new LimitedTextField(60);
        mfPai = new LimitedTextField(60);
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        mfEmpresa = new LimitedTextField(60);
        jLabel16 = new javax.swing.JLabel();
        mfDtAdmis = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel17 = new javax.swing.JLabel();
        mfEndereco = new LimitedTextField(60);
        jLabel18 = new javax.swing.JLabel();
        mfNumero = new LimitedTextField(10);
        jLabel19 = new javax.swing.JLabel();
        mfCplto = new LimitedTextField(15);
        jLabel20 = new javax.swing.JLabel();
        mfBairro = new LimitedTextField(25);
        jLabel21 = new javax.swing.JLabel();
        mfCidade = new LimitedTextField(25);
        jLabel22 = new javax.swing.JLabel();
        mfEstado = new LimitedTextField(2);
        jLabel23 = new javax.swing.JLabel();
        mfCep = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        mfTelEmpresa = new javax.swing.JFormattedTextField();
        mfRamalEmpresa = new LimitedTextField(4);
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        mfCargo = new LimitedTextField(25);
        jLabel27 = new javax.swing.JLabel();
        mfSalario = new javax.swing.JFormattedTextField();
        jLabel28 = new javax.swing.JLabel();
        mfEmail = new LimitedTextField(60);
        jLabel29 = new javax.swing.JLabel();
        mfConjugue = new LimitedTextField(60);
        jLabel30 = new javax.swing.JLabel();
        mfDtNascConj = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel31 = new javax.swing.JLabel();
        mfCpfConj = new javax.swing.JFormattedTextField();
        jLabel32 = new javax.swing.JLabel();
        mfIdentidadeConj = new LimitedTextField(15);
        jLabel33 = new javax.swing.JLabel();
        mfSalarioConj = new javax.swing.JFormattedTextField();
        jLabel34 = new javax.swing.JLabel();
        mfEmpresaConj = new LimitedTextField(60);
        jLabel35 = new javax.swing.JLabel();
        mfEmpresaTelConj = new javax.swing.JFormattedTextField();
        jLabel36 = new javax.swing.JLabel();
        mfEmpresaRamalConj = new LimitedTextField(4);
        jJuridica = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jSocios = new javax.swing.JTable();
        btFicha = new javax.swing.JButton();
        btExcluirFicha = new javax.swing.JButton();
        mjRazao = new LimitedTextField(60);
        mjFantasia = new LimitedTextField(60);
        mjEndereco = new LimitedTextField(60);
        jLabel48 = new javax.swing.JLabel();
        mjNumero = new LimitedTextField(10);
        jLabel49 = new javax.swing.JLabel();
        mjCplto = new LimitedTextField(15);
        mjBairro = new LimitedTextField(25);
        jLabel51 = new javax.swing.JLabel();
        mjCidade = new LimitedTextField(25);
        jLabel52 = new javax.swing.JLabel();
        mjEstado = new LimitedTextField(2);
        jLabel53 = new javax.swing.JLabel();
        mjCep = new javax.swing.JFormattedTextField();
        jLabel46 = new javax.swing.JLabel();
        mjTelefone = new javax.swing.JFormattedTextField();
        jLabel55 = new javax.swing.JLabel();
        mjRamal = new LimitedTextField(4);
        jLabel56 = new javax.swing.JLabel();
        mjCelular = new javax.swing.JFormattedTextField();
        jLabel57 = new javax.swing.JLabel();
        mjDtContratoSocial = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        mjEmail = new LimitedTextField(60);
        jPanel4 = new javax.swing.JPanel();
        cbxEnviar = new javax.swing.JCheckBox();
        jLabel38 = new javax.swing.JLabel();
        mEnderecoEnv = new LimitedTextField(60);
        jLabel39 = new javax.swing.JLabel();
        mBairroEnv = new LimitedTextField(25);
        jLabel40 = new javax.swing.JLabel();
        mCidadeEnv = new LimitedTextField(25);
        mNumeroEnv = new LimitedTextField(10);
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        mEstadoEnv = new LimitedTextField(2);
        jLabel43 = new javax.swing.JLabel();
        mCpltoEnv = new LimitedTextField(15);
        jLabel44 = new javax.swing.JLabel();
        mCepEnv = new javax.swing.JFormattedTextField();
        jBotoes = new javax.swing.JPanel();
        btIncluir = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btGravar = new javax.swing.JButton();
        btRetornar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Cadastro de Fiadores");
        setAlwaysOnTop(true);
        setResizable(false);

        jDadosIniciais.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(jrbFisica);
        jrbFisica.setSelected(true);
        jrbFisica.setText("Física");
        jrbFisica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbFisicaActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbJuridica);
        jrbJuridica.setText("Jurídica");
        jrbJuridica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbJuridicaActionPerformed(evt);
            }
        });

        jLabel5.setText("Cnpj:");

        jLabel6.setText("RG:");

        try {
            mCpf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mCpf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mCpfFocusLost(evt);
            }
        });

        jLabel7.setText("Cpf:");

        try {
            mCnpj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##.###.###/####-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mCnpj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mCnpjFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jDadosIniciaisLayout = new javax.swing.GroupLayout(jDadosIniciais);
        jDadosIniciais.setLayout(jDadosIniciaisLayout);
        jDadosIniciaisLayout.setHorizontalGroup(
            jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jrbFisica, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jrbJuridica, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(8, 8, 8)
                .addComponent(mIdentidade, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDadosIniciaisLayout.setVerticalGroup(
            jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mIdentidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jrbFisica, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jrbJuridica, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jFisica.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jFisica.setEnabled(false);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Nome:");

        jLabel9.setText("Dt.Nasc:");

        mfDtNasc.setDate(new java.util.Date(-2208977612000L));

        mfNacionalidade.setName("mfNacionalidade"); // NOI18N

        jLabel15.setText("E.Civil:");

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Nacionalidade:");

        mfEstCivil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Solteriro(a)", "Casado(a)", "Separado(a)", "Divorciado(a)", "Viuvo(a)" }));
        mfEstCivil.setName("mfEstCivil"); // NOI18N

        jLabel10.setText("Telefones:");

        mfTel2.setEditable(false);
        mfTel2.setEnabled(false);

        try {
            mfTel1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Mãe:");

        jLabel12.setText("Pai:");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Empresa:");

        jLabel16.setText("Dt.Adm:");

        mfDtAdmis.setDate(new java.util.Date(-2208977612000L));

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Endereço:");

        mfEndereco.setName("mfEndereco"); // NOI18N

        jLabel18.setText("N°.:");

        mfNumero.setName("mfNumero"); // NOI18N

        jLabel19.setText("Cplto:");

        mfCplto.setName("mfCplto"); // NOI18N

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Bairro:");

        mfBairro.setName("mfBairro"); // NOI18N

        jLabel21.setText("Cidade:");

        mfCidade.setName("mfCidade"); // NOI18N

        jLabel22.setText("UF:");

        mfEstado.setName("mfEstado"); // NOI18N

        jLabel23.setText("Cep:");

        try {
            mfCep.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Telefone:");

        try {
            mfTelEmpresa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        mfRamalEmpresa.setName("mNumero"); // NOI18N

        jLabel25.setText("Ramal:");

        jLabel26.setText("Cargo:");

        mfCargo.setName("mCidade"); // NOI18N

        jLabel27.setText("Salário:");

        mfSalario.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mfSalario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mfSalario.setText("0,00");

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("E-Mail:");

        jLabel29.setText("Conjugue:");

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Dt.Nasc:");

        mfDtNascConj.setDate(new java.util.Date(-2208977612000L));

        jLabel31.setText("Cpfj:");

        try {
            mfCpfConj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel32.setText("RG:");

        jLabel33.setText("Salário:");

        mfSalarioConj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mfSalarioConj.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mfSalarioConj.setText("0,00");

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("Empresa:");

        jLabel35.setText("Telefones:");

        try {
            mfEmpresaTelConj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mfEmpresaTelConj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mfEmpresaTelConjActionPerformed(evt);
            }
        });

        jLabel36.setText("Ramal:");

        mfEmpresaRamalConj.setName("mNumero"); // NOI18N

        javax.swing.GroupLayout jFisicaLayout = new javax.swing.GroupLayout(jFisica);
        jFisica.setLayout(jFisicaLayout);
        jFisicaLayout.setHorizontalGroup(
            jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFisicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfNome, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfNacionalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfEstCivil, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfTel1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfMae, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfEmpresaConj, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfEmpresaTelConj, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfEmpresaRamalConj, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfDtNascConj, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfCpfConj, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfIdentidadeConj, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfSalarioConj, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfConjugue, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfTelEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfRamalEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfSalario, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfCep, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFisicaLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(mfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19))
                            .addGroup(jFisicaLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mfCplto, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jFisicaLayout.createSequentialGroup()
                            .addGap(465, 465, 465)
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                            .addComponent(mfDtAdmis, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jFisicaLayout.createSequentialGroup()
                            .addGap(387, 387, 387)
                            .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(mfDtNasc, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mfTel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mfPai, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jFisicaLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel13, jLabel14, jLabel17, jLabel20, jLabel24, jLabel28, jLabel30, jLabel34, jLabel8});

        jFisicaLayout.setVerticalGroup(
            jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFisicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mfNome, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mfDtNasc, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfNacionalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfEstCivil, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfTel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfTel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfMae, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfPai, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfCplto, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfCep, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfTelEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfRamalEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfConjugue, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mfCpfConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mfIdentidadeConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mfSalarioConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jFisicaLayout.createSequentialGroup()
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mfEmpresaConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mfEmpresaTelConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mfEmpresaRamalConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(mfDtNascConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(mfDtAdmis, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jFisicaLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel13, jLabel14, jLabel17, jLabel20, jLabel24, jLabel28, jLabel30, jLabel34, jLabel8});

        jDados.addTab("Física", jFisica);

        jJuridica.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel45.setText("Razão Social:");

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("Endereço:");

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel50.setText("Bairro:");

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel54.setText("Telefone:");

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel58.setText("E-Mail:");

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel1.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jSocios.setAutoCreateRowSorter(true);
        jSocios.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jSocios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jSocios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jSocios.getTableHeader().setResizingAllowed(false);
        jSocios.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jSocios);

        btFicha.setText("Ficha");
        btFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFichaActionPerformed(evt);
            }
        });

        btExcluirFicha.setText("Excluir");
        btExcluirFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirFichaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 609, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btExcluirFicha, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(btFicha, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(btFicha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btExcluirFicha)
                .addContainerGap())
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        mjEndereco.setName("mjEndereco"); // NOI18N

        jLabel48.setText("N°.:");

        mjNumero.setName("mjNumero"); // NOI18N

        jLabel49.setText("Cplto:");

        mjCplto.setName("mjCplto"); // NOI18N

        mjBairro.setName("mjBairro"); // NOI18N

        jLabel51.setText("Cidade:");

        mjCidade.setName("mjCidade"); // NOI18N
        mjCidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mjCidadeActionPerformed(evt);
            }
        });

        jLabel52.setText("UF:");

        mjEstado.setName("mjEstado"); // NOI18N

        jLabel53.setText("Cep:");

        try {
            mjCep.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("Nome Fantasia:");

        try {
            mjTelefone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel55.setText("Ramal:");

        jLabel56.setText("Celular:");

        try {
            mjCelular.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel57.setText("Dt.Cont Soc:");

        mjDtContratoSocial.setDate(new java.util.Date(-2208977612000L));

        javax.swing.GroupLayout jJuridicaLayout = new javax.swing.GroupLayout(jJuridica);
        jJuridica.setLayout(jJuridicaLayout);
        jJuridicaLayout.setHorizontalGroup(
            jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jJuridicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel58)
                    .addComponent(jLabel54)
                    .addComponent(jLabel50)
                    .addComponent(jLabel47)
                    .addComponent(jLabel46)
                    .addComponent(jLabel45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mjEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                    .addComponent(mjFantasia, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                    .addComponent(mjRazao, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jJuridicaLayout.createSequentialGroup()
                                .addComponent(mjTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel55)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjRamal, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel56)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel57)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                                .addComponent(mjDtContratoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jJuridicaLayout.createSequentialGroup()
                                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jJuridicaLayout.createSequentialGroup()
                                        .addComponent(mjEndereco, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel48)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mjNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jJuridicaLayout.createSequentialGroup()
                                        .addComponent(mjBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel51)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mjCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel52)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mjEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel53)
                                    .addComponent(jLabel49))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(mjCep)
                                    .addComponent(mjCplto, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(3, 3, 3)))
                .addContainerGap())
            .addGroup(jJuridicaLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jJuridicaLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel45, jLabel46, jLabel47, jLabel50, jLabel54, jLabel58});

        jJuridicaLayout.setVerticalGroup(
            jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jJuridicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjRazao, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjCplto, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjCep, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(mjCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(mjTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjRamal, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(mjDtContratoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(mjEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jJuridicaLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel45, jLabel46, jLabel47, jLabel50, jLabel54, jLabel58});

        jDados.addTab("Jurídica", jJuridica);

        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        cbxEnviar.setText("Enviar Correspondências para o endereço abaixo:");

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("Endereço:");

        mEnderecoEnv.setName("mEnderecoEnv"); // NOI18N

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("Bairro:");

        mBairroEnv.setName("mBairroEnv"); // NOI18N

        jLabel40.setText("Cidade:");

        mCidadeEnv.setName("mCidadeEnv"); // NOI18N

        mNumeroEnv.setName("mNumeroEnv"); // NOI18N

        jLabel41.setText("UF:");

        jLabel42.setText("N°.:");

        mEstadoEnv.setName("mEstadoEnv"); // NOI18N

        jLabel43.setText("Cep:");

        mCpltoEnv.setName("mCpltoEnv"); // NOI18N

        jLabel44.setText("Cplto:");

        try {
            mCepEnv.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mBairroEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mCidadeEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mEstadoEnv))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mEnderecoEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mNumeroEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mCpltoEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mCepEnv))))
                    .addComponent(cbxEnviar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel38, jLabel39});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbxEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mEnderecoEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mNumeroEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mCpltoEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mBairroEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mCidadeEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mEstadoEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mCepEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel38, jLabel39});

        mEnderecoEnv.getAccessibleContext().setAccessibleDescription("24");

        jBotoes.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jBotoes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        btIncluir.setText("Incluir");
        btIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIncluirActionPerformed(evt);
            }
        });

        btExcluir.setText("Excluir");
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });

        btGravar.setText("Gravar");
        btGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGravarActionPerformed(evt);
            }
        });

        btRetornar.setText("Retornar");
        btRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRetornarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jBotoesLayout = new javax.swing.GroupLayout(jBotoes);
        jBotoes.setLayout(jBotoesLayout);
        jBotoesLayout.setHorizontalGroup(
            jBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btIncluir, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(btExcluir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(btRetornar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btGravar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                .addContainerGap())
        );
        jBotoesLayout.setVerticalGroup(
            jBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btIncluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btExcluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 354, Short.MAX_VALUE)
                .addComponent(btGravar)
                .addGap(18, 18, 18)
                .addComponent(btRetornar)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jDadosIniciais, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jDados, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jDadosIniciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDados, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jrbFisicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbFisicaActionPerformed
        mCpf.setEnabled(true);
        mCnpj.setEnabled(false);
        jDados.setEnabledAt(0, true);
        jDados.setEnabledAt(1, false);
        jDados.setSelectedIndex(0);
}//GEN-LAST:event_jrbFisicaActionPerformed

    private void jrbJuridicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbJuridicaActionPerformed
        mCpf.setEnabled(false);
        mCnpj.setEnabled(true);
        jDados.setEnabledAt(0, false);
        jDados.setEnabledAt(1, true);
        jDados.setSelectedIndex(1);
}//GEN-LAST:event_jrbJuridicaActionPerformed

    private void mfEmpresaTelConjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mfEmpresaTelConjActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_mfEmpresaTelConjActionPerformed

    private void btFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFichaActionPerformed
        int selRow = jSocios.getSelectedRow();
        if (selRow > -1) {
            VariaveisGlobais.pResult = pResult;
            VariaveisGlobais.mQtdSoc = jSocios.getRowCount();
            VariaveisGlobais.mPosSoc = selRow + 1;
        } else {
            VariaveisGlobais.pResult = pResult;
            VariaveisGlobais.mQtdSoc = 0;
            VariaveisGlobais.mPosSoc = 0;
        }

        try {
            jFichaSocios oSoc = new jFichaSocios(null, closable);
            oSoc.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Atualiza table
        FillSocios(jSocios, pResult);
}//GEN-LAST:event_btFichaActionPerformed

    private void btExcluirFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirFichaActionPerformed
        Object[] options = { "Sim", "Não" };
        int i = JOptionPane.showOptionDialog(null,
                "Tem certeza que deseja Excluir este Sócio", "Excluir",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        if (i == JOptionPane.YES_OPTION) {
            try {
                ExcluirSocio();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        FillSocios(jSocios, pResult);
}//GEN-LAST:event_btExcluirFichaActionPerformed

    private void mjCidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mjCidadeActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_mjCidadeActionPerformed

    private void btIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIncluirActionPerformed
        bNew = true;

        // Bloqueio dos botões
        btIncluir.setEnabled(false);
        btGravar.setEnabled(true);
        btRetornar.setEnabled(true);
        btFicha.setEnabled(false);
        btExcluirFicha.setEnabled(false);

        LimpaDados();

        jrbFisica.setEnabled(true);
        jrbJuridica.setEnabled(true);
        jrbFisica.requestFocus();
}//GEN-LAST:event_btIncluirActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_btExcluirActionPerformed

    private void btGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGravarActionPerformed

        if (jrbFisica.isSelected() && mfNome.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'NOME' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mfNome.requestFocus();
            return;
        }

        if (jrbJuridica.isSelected() && mjRazao.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'RAZÃO' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mjRazao.requestFocus();
            return;
        }

        if (jrbFisica.isSelected() && mCpf.getText().replace(".", "").replace("-", "").trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'CPF' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mCpf.requestFocus();
            return;
        }

        if (jrbJuridica.isSelected() && mCnpj.getText().replace(".", "").replace("-", "").replace("/","").trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'CNPJ' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mCnpj.requestFocus();
            return;
        }

//        if (jrbFisica.isSelected() && mfDtNasc.getDate() == null) {
//            JOptionPane.showMessageDialog(null, "Campo 'DATA DE NASCIMENTO' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
//            mfDtNasc.requestFocus();
//            return;
//        }
//
//        if (jrbJuridica.isSelected() && mjDtContratoSocial.getDate() == null) {
//            JOptionPane.showMessageDialog(null, "Campo 'DATA DE INÍCIO' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
//            mjDtContratoSocial.requestFocus();
//            return;
//        }

        try {
            GravarDados();
        } catch (SQLException ex) { ex.printStackTrace();}

        bNew = false;

        // Bloqueio dos botões
        btIncluir.setEnabled(true);
        btGravar.setEnabled(true);
        btRetornar.setEnabled(true);
        btFicha.setEnabled(true);
        btExcluirFicha.setEnabled(true);
    }//GEN-LAST:event_btGravarActionPerformed

    private void btRetornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRetornarActionPerformed
        if (bNew) {
            try {
                LerDados(false);

                // Bloqueio dos botões
                btIncluir.setEnabled(true);
                btGravar.setEnabled(true);
                btRetornar.setEnabled(true);
                btFicha.setEnabled(true);
                btExcluirFicha.setEnabled(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            bNew = false;
        } else this.dispose();
}//GEN-LAST:event_btRetornarActionPerformed

    private void mCpfFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mCpfFocusLost
        if (!FuncoesGlobais.ValidarCPFCNPJ(mCpf.getText())) mCpf.requestFocus();
    }//GEN-LAST:event_mCpfFocusLost

    private void mCnpjFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mCnpjFocusLost
        if (!FuncoesGlobais.ValidarCPFCNPJ(mCnpj.getText())) mCnpj.requestFocus();
    }//GEN-LAST:event_mCnpjFocusLost

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jFiadores dialog = new jFiadores(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btExcluirFicha;
    private javax.swing.JButton btFicha;
    private javax.swing.JButton btGravar;
    private javax.swing.JButton btIncluir;
    private javax.swing.JButton btRetornar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbxEnviar;
    private javax.swing.JPanel jBotoes;
    private javax.swing.JTabbedPane jDados;
    private javax.swing.JPanel jDadosIniciais;
    private javax.swing.JPanel jFisica;
    private javax.swing.JPanel jJuridica;
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
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
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
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jSocios;
    private javax.swing.JRadioButton jrbFisica;
    private javax.swing.JRadioButton jrbJuridica;
    private javax.swing.JTextField mBairroEnv;
    private javax.swing.JFormattedTextField mCepEnv;
    private javax.swing.JTextField mCidadeEnv;
    private javax.swing.JFormattedTextField mCnpj;
    private javax.swing.JFormattedTextField mCpf;
    private javax.swing.JTextField mCpltoEnv;
    private javax.swing.JTextField mEnderecoEnv;
    private javax.swing.JTextField mEstadoEnv;
    private javax.swing.JTextField mIdentidade;
    private javax.swing.JTextField mNumeroEnv;
    private javax.swing.JTextField mfBairro;
    private javax.swing.JTextField mfCargo;
    private javax.swing.JFormattedTextField mfCep;
    private javax.swing.JTextField mfCidade;
    private javax.swing.JTextField mfConjugue;
    private javax.swing.JFormattedTextField mfCpfConj;
    private javax.swing.JTextField mfCplto;
    private com.toedter.calendar.JDateChooser mfDtAdmis;
    private com.toedter.calendar.JDateChooser mfDtNasc;
    private com.toedter.calendar.JDateChooser mfDtNascConj;
    private javax.swing.JTextField mfEmail;
    private javax.swing.JTextField mfEmpresa;
    private javax.swing.JTextField mfEmpresaConj;
    private javax.swing.JTextField mfEmpresaRamalConj;
    private javax.swing.JFormattedTextField mfEmpresaTelConj;
    private javax.swing.JTextField mfEndereco;
    private javax.swing.JComboBox mfEstCivil;
    private javax.swing.JTextField mfEstado;
    private javax.swing.JTextField mfIdentidadeConj;
    private javax.swing.JTextField mfMae;
    private javax.swing.JTextField mfNacionalidade;
    private javax.swing.JTextField mfNome;
    private javax.swing.JTextField mfNumero;
    private javax.swing.JTextField mfPai;
    private javax.swing.JTextField mfRamalEmpresa;
    private javax.swing.JFormattedTextField mfSalario;
    private javax.swing.JFormattedTextField mfSalarioConj;
    private javax.swing.JFormattedTextField mfTel1;
    private javax.swing.JFormattedTextField mfTel2;
    private javax.swing.JFormattedTextField mfTelEmpresa;
    private javax.swing.JTextField mjBairro;
    private javax.swing.JFormattedTextField mjCelular;
    private javax.swing.JFormattedTextField mjCep;
    private javax.swing.JTextField mjCidade;
    private javax.swing.JTextField mjCplto;
    private com.toedter.calendar.JDateChooser mjDtContratoSocial;
    private javax.swing.JTextField mjEmail;
    private javax.swing.JTextField mjEndereco;
    private javax.swing.JTextField mjEstado;
    private javax.swing.JTextField mjFantasia;
    private javax.swing.JTextField mjNumero;
    private javax.swing.JTextField mjRamal;
    private javax.swing.JTextField mjRazao;
    private javax.swing.JFormattedTextField mjTelefone;
    // End of variables declaration//GEN-END:variables

}
