/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Movimento;

import Bancos.CEF;
import Bancos.Santander;
import Bancos.bancos;
import Bancos.bb;
import Bancos.bradesco;
import Bancos.itau;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.StreamFile;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import boleta.Boleta;
import Sici.Partida.Collections;
import java.awt.BorderLayout;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.view.JRViewer;

/**
 *
 * @author passeli
 */
public class jBoletasAvulsasOld extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    boolean bExecNome = false, bExecCodigo = false;
    Boleta Bean1 = null;
   
    /**
     * Creates new form jBoletasAvulsas
     */
    public jBoletasAvulsasOld() {
        initComponents();
        
        Clean();
        phistorico.setText(null);
        jvencto.setDate(new Date());
        jvalor.setText("0,00");
        
        FillOpcao();
        FillBancos();        
        
        LerMsgAv();
    }

    private void FillOpcao() {
        btLimpar.setVisible(false);
        jcodigo.setEnabled(true);
        jnome.setEnabled(true);
        
        int opc = -1;
        if (opcprop.isSelected()) {
            Enabled(false);
            opc = 0;
        } else if (opcloca.isSelected()) {
            Enabled(false);
            opc = 1;
        } else {
            opc = -1;
            btLimpar.setVisible(true);
            jcodigo.setEnabled(false);
            jnome.setEnabled(false);
            Enabled(true);
            Clean();
            pnome.requestFocus();
        }
        
        if (opc == 0 || opc == 1) {
            String sql = null;
            if (opc == 0) { 
                sql = "SELECT rgprp AS codigo, Upper(nome) AS nome FROM proprietarios ORDER BY nome;"; 
            } else {
                sql = "SELECT contrato AS codigo, Upper(nomerazao) AS nome FROM locatarios ORDER BY nomerazao;"; 
            }
            ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            jcodigo.removeAllItems();
            jnome.removeAllItems();
            try {
                while (rs.next()) {
                    jcodigo.addItem(rs.getString("codigo"));
                    jnome.addItem(rs.getString("nome"));
                }
            } catch (Exception e) {}
            DbMain.FecharTabela(rs);
            
//            // Autocompletação
//            AutoCompletion.enable(jcodigo);
//            AutoCompletion.enable(jnome);
            
            jcodigo.requestFocus();
        }
    }
    
    private void FillBancos() {
        //String sql = "SELECT c.nbanco, b.nome FROM contas_boletas c, bancos b WHERE c.nbanco = b.codigo;";
        String sql = "SELECT b.codigo AS nbanco, b.nome FROM bancos b ORDER BY b.codigo;";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            jbancos.removeAllItems();
            while (rs.next()) {
                jbancos.addItem(rs.getString("nbanco") + " - " + rs.getString("nome"));
            }
        } catch (Exception e) {e.printStackTrace();}
        DbMain.FecharTabela(rs);
    }
    
    private void Clean() {
        pnome.setText(null);
        pdocumento.setText(null);
        pendereco.setText(null);
        pnumero.setText(null);
        pcomplto.setText(null);
        pbairro.setText(null);
        pcidade.setText(null);
        pestado.setText(null);
        pcep.setText(null);
    }
    
    private void Enabled(boolean endi) {
        pnome.setEditable(endi);
        pdocumento.setEditable(endi);
        pendereco.setEditable(endi);
        pnumero.setEditable(endi);
        pcomplto.setEditable(endi);
        pbairro.setEditable(endi);
        pcidade.setEditable(endi);
        pestado.setEditable(endi);
        pcep.setEditable(endi);
    }
    
    private void Setar() {
        if (jcodigo.getSelectedItem() == null) return;
        
        String sql = null;
        if (opcprop.isSelected()) {
            sql = "SELECT nome, end, num, compl, bairro, cidade, estado, cep, cpfcnpj FROM proprietarios WHERE rgprp = '" + jcodigo.getSelectedItem().toString() + "';";
        } else if (opcloca.isSelected()) {
            sql = "SELECT l.nomerazao AS nome, i.end, i.num, i.compl, i.bairro, i.cidade, i.estado, i.cep, l.cpfcnpj FROM locatarios l, imoveis i WHERE (l.rgprp = i.rgprp AND l.rgimv = i.rgimv) AND l.contrato = '" + jcodigo.getSelectedItem().toString() + "';";
        }
        
        Clean();
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                pnome.setText(rs.getString("nome"));
                pdocumento.setText(rs.getString("cpfcnpj"));
                pendereco.setText(rs.getString("end"));
                pnumero.setText(rs.getString("num"));
                pcomplto.setText(rs.getString("compl"));
                pbairro.setText(rs.getString("bairro"));
                pcidade.setText(rs.getString("cidade"));
                pestado.setText(rs.getString("estado"));
                pcep.setText(rs.getString("cep"));
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(rs);
    }
    
    private void GeraRemessaAvulsa() {
        File diretorio = new File("remessa"); 
        if (!diretorio.exists()) {
           diretorio.mkdirs(); 
        }
        
        File arquivo = new File("remessa/" + jnomeArq.getText().trim() + "_" + jnrlote.getValue().toString().trim() + ".rem");
        if (arquivo.exists()) {
            JOptionPane.showMessageDialog(null, "Arquivo de remessa ja existe!!!\n\nTente novamente com outro nome.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            jnomeArq.requestFocus();
            return;
        }
        
        String LF = "\r\n";
        
        String _banco = jbancos.getSelectedItem().toString().trim().substring(0, 3);
        String nrlote = String.valueOf((Integer)jnrlote.getValue() + 1);
        String[] variaveis = {"LOTE_" + _banco, nrlote,"NUMERICO"};
        try { conn.GravarParametros(variaveis); } catch (Exception e) {}
        
        String nmEmp = new Pad(VariaveisGlobais.dCliente.get("empresa"),30).RPad();
        String icEmp = FuncoesGlobais.StrZero(rmvNumero(VariaveisGlobais.dCliente.get("cnpj")),15);
        int ctalinhas = 1;
        StreamFile filler = new StreamFile(new String[] {"remessa/" + jnomeArq.getText().trim() + "_" + jnrlote.getValue().toString().trim() + ".rem"});
        if (filler.Open()) {
            String codBaco = "033";
            String loteSer = "0000";
            String tipoSer = "0";
            String reserVd = FuncoesGlobais.Space(8);
            String tipoInc = "2";
            String inscEmp = icEmp;  // cnpj
            String codTran = "340500007926383";  // dados da conta
            String reseVad = FuncoesGlobais.Space(25); // 25 digitos
            String nomeEmp = nmEmp; // 30 digitos
            String nomeBan = "BANCO SANTANDER" + FuncoesGlobais.Space(15); // 30 digitos
            String resVado = FuncoesGlobais.Space(10); // 10 digitos
            String codRems = "1";
            String dtGerac = Dates.DateFormata("ddMMyyyy", new Date()); // data atual
            String rservDo = FuncoesGlobais.Space(6); // 6 barncos
            String numSequ = FuncoesGlobais.StrZero(jnrlote.getValue().toString().trim(), 6); // 6 digitos de 1 a 999999 <
            String Versaos = "040";
            String reSerVa = FuncoesGlobais.Space(74); // 74 digitos           
                    
            String output = codBaco + loteSer + tipoSer + reserVd + tipoInc +
                            inscEmp + codTran + reseVad + nomeEmp + nomeBan +
                            resVado + codRems + dtGerac + rservDo + numSequ +
                            Versaos + reSerVa;
            filler.Print(output + LF);

            ctalinhas += 1;
            
            String codBco = "033"; 
            String loteRe = "0001"; 
            String tpRems = "1";
            String tpOper = "R";
            String tpServ = "01";
            String reseVd = FuncoesGlobais.Space(2);
            String nversa = "030";
            String resedo = FuncoesGlobais.Space(1);
            String tpInsc = "2";
            String cnpjEp = icEmp;
            String resVdo = FuncoesGlobais.Space(20);
            String codTrE = "340500007926383";
            String rseVdo = FuncoesGlobais.Space(5);
            String nomeCd = nmEmp; // 30dig
            String mensN1 = FuncoesGlobais.Space(40); // 40dig
            String mensN2 = FuncoesGlobais.Space(40); // 40dig
            String numRRt = "00000000"; // 8dig
            String dtgrav = Dates.DateFormata("ddMMyyyy", new Date());;
            String reVado = FuncoesGlobais.Space(41);

            output = codBco + loteRe + tpRems + tpOper + tpServ + reseVd +
                     nversa + resedo + tpInsc + cnpjEp + resVdo + codTrE +
                     rseVdo + nomeCd + mensN1 + mensN2 + numRRt + dtgrav +
                     reVado;
            filler.Print(output + LF);
        }

        ctalinhas += 1;
        
        if (filler.Open()) {
            Boleta bean1 = new Boleta();                
            //try {
                bean1 = this.Bean1; // CreateBoleta();
            //} catch (SQLException ex) { ex.printStackTrace(); }

            // P
            String codBcC = "033";
            String nrReme = "0001";
            String tpRegi = "3";
            String nrSequ = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
            String cdSegR = "P";
            String rsvDos = FuncoesGlobais.Space(1);
            String cdMvRm = jtparq.getSelectedItem().toString().trim().substring(0, 2); // 01 - Entrada de título
            String agCedn = "3405";  // agencia do cedente
            String digAgc = "3"; // digito verificador
            String numCoC = "130005160"; // Conta Corrente
            String digCoC = "1"; // digito verificador
            String contCb = "130005160"; // Conta cobranca
            String digtCb = "1"; // digito
            String rservo = FuncoesGlobais.Space(2);
            String nnumer = new Pad(bean1.getbolDadosNnumero().substring(3),13).RPad().toUpperCase(); // nosso numero com 13 dig
            String tpoCob = "5"; // tipo de cobrança
            String formCd = "1"; // forma de cadastramento
            String tipoDc = "2"; // tipo de documento
            String rsvad1 = FuncoesGlobais.Space(1);
            String rsvad2 = FuncoesGlobais.Space(1);
            
            String _contrato = (opcprop.isSelected() || opcloca.isSelected() ? jcodigo.getSelectedItem().toString() : "0");
            String numDoc = new Pad(FuncoesGlobais.StrZero(_contrato,15),15).RPad(); //new Pad(bean1.getbolDadosNumdoc(),15).RPad(); // "000000000000000"; // numero do documento (livre)
            String dtavtt = Dates.StringtoString(bean1.getbolDadosVencimento(),"dd/MM/yyyy","ddMMyyyy"); // "ddmmaaaa"; // data de vencimento do titulo
            String vrnmtt = fmtNumero(bean1.getbolDadosVrdoc()); //"000000000123129"; // valor nominal do titulo
            String agencb = "0000"; // agencia encarregada
            String digaec = "0"; // digito
            String rsvado = FuncoesGlobais.Space(1);
            String esptit = "04"; /// 04 - dup de serviço, 17 - recibo/ especie de titulo // 17 - Recibo
            String idtitu = "N";
            String dtemti = Dates.StringtoString(bean1.getbolDadosDatadoc(),"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data emissao do titulo
            String cdjuti = "1"; // codigo juros do titulo
            String dtjrmo = Dates.StringtoString(bean1.getbolDadosVencimento(),"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data juros mora
            float valor = LerValor.StringToFloat(bean1.getbolDadosVrdoc()) * 0.00033f;
            String vrmtxm = fmtNumero(LerValor.FloatToString(valor)); //"000000000000041"; // valor ou taxa de mora (aluguel * 0,0333)
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

            String output = codBcC + nrReme + tpRegi + nrSequ + cdSegR + rsvDos +
                            cdMvRm + agCedn + digAgc + numCoC + digCoC + contCb +
                            digtCb + rservo + nnumer + tpoCob + formCd + tipoDc +
                            rsvad1 + rsvad2 + numDoc + dtavtt + vrnmtt + agencb +
                            digaec + rsvado + esptit + idtitu + dtemti + cdjuti +
                            dtjrmo + vrmtxm + cddesc + dtdesc + vrpecd + vriofr +
                            vrabti + idttep + cdprot + nrdpro + cdbxdv + revdao +
                            nrdibd + cdmoed + revado;
            filler.Print(output + LF);

            ctalinhas += 1;

            // Sqgmento Q
            String cdbcoc = "033";
            String nrltre = "0001";
            String tiporg = "3";

            String nrSeqq = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
            String cdregt = "Q";
            String bracos = FuncoesGlobais.Space(1);
            String cdmvrm = jtparq.getSelectedItem().toString().trim().substring(0, 2); // ou 02 - pedido de baixa
            String cpfCNPJ = rmvLetras(rmvNumero(bean1.getsacDadosCpfcnpj()));
            String tpinss = (cpfCNPJ.length() == 11 ? "1" : "2"); // tipo inscricao sacado
            String inscsc = FuncoesGlobais.StrZero(cpfCNPJ,15); //"000000000000000"; // CPF/CNPJ
            String nmesac = FuncoesGlobais.myLetra(new Pad(bean1.getsacDadosNome().toUpperCase(),40).RPad()); //"(40)"; // nome do sacado
            String endsac = FuncoesGlobais.myLetra(new Pad(bean1.getsacDadosEndereco() + ", " + bean1.getsacDadosNumero() + " " + bean1.getsacDadosCompl(),40).RPad().toUpperCase()); //"(40)"; // endereco 
            String baisac = FuncoesGlobais.myLetra(new Pad(bean1.getsacDadosBairro(),15).RPad().toUpperCase()); // "(15)"; // bairro
            String cepsac = FuncoesGlobais.myLetra(new Pad(bean1.getsacDadosCep().substring(0, 5),5).RPad().toUpperCase()); // "(5)";  // cep
            String cepsus = FuncoesGlobais.myLetra(new Pad(bean1.getsacDadosCep().substring(6, 9),3).RPad().toUpperCase()); // "(3)";  // sufixo cep
            String cidsac = FuncoesGlobais.myLetra(new Pad(bean1.getsacDadosCidade(),15).RPad().toUpperCase()); //"(15)"; // cidade
            String ufsaca = FuncoesGlobais.myLetra(new Pad(bean1.getsacDadosEstado(),2).RPad().toUpperCase()); //"RJ";   // UF
            String demais = "0000000000000000                                        000000000000                   ";

            output = cdbcoc + nrltre + tiporg + nrSeqq + cdregt + bracos +
                     cdmvrm + tpinss + inscsc + nmesac + endsac + baisac +
                     cepsac + cepsus + cidsac + ufsaca + demais;
            filler.Print(output + LF);

            ctalinhas += 1;

            // R
            String cbcodc = "033";
            String nrlotr = "0001";
            String tporeg = "3";

            String nrSeqr = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
            String cdgseg = "R";
            String spacob = FuncoesGlobais.Space(1);
            String cdomot = jtparq.getSelectedItem().toString().trim().substring(0, 2);  // ou 02 - baixa
            String cdgdes = "0"; // codigo desconto
            String dtdes2 = "00000000"; // data desconto 2
            String vrpccd = "000000000000000"; // valor perc desco
            String brac24 = FuncoesGlobais.Space(24);
            String cdmult = "2"; // codigo da multa (1 - fixo / 2 - perc)
            String dtamul = Dates.StringtoString(bean1.getbolDadosVencimento(),"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data multa
            String vrpcap = "000000000001000"; // vr/per multa
            String bran10 = FuncoesGlobais.Space(10);
            String msge03 = FuncoesGlobais.Space(40); // msg 3
            String msge04 = FuncoesGlobais.Space(40); // msg 4
            String branfn = FuncoesGlobais.Space(61);

            output = cbcodc + nrlotr + tporeg + nrSeqr + cdgseg + spacob +
                     cdomot + cdgdes + dtdes2 + vrpccd + brac24 + cdmult +
                     dtamul + vrpcap + bran10 + msge03 + msge04 + branfn;
            filler.Print(output + LF);

            ctalinhas += 1;

            String[] msg = {                
                (!(bean1.getlocaDescL01() == null) ? new Pad(bean1.getlocaDescL01(),100).RPad() : null),                

                (!bean1.getbolDadosMsg01().equalsIgnoreCase("") ? bean1.getbolDadosMsg01() : null),
                (!bean1.getbolDadosMsg02().equalsIgnoreCase("") ? bean1.getbolDadosMsg02() : null),
                (!bean1.getbolDadosMsg03().equalsIgnoreCase("") ? bean1.getbolDadosMsg03() : null),
                (!bean1.getbolDadosMsg04().equalsIgnoreCase("") ? bean1.getbolDadosMsg04() : null),
                (!bean1.getbolDadosMsg05().equalsIgnoreCase("") ? bean1.getbolDadosMsg05() : null),
                (!bean1.getbolDadosMsg06().equalsIgnoreCase("") ? bean1.getbolDadosMsg06() : null),
                (!bean1.getbolDadosMsg07().equalsIgnoreCase("") ? bean1.getbolDadosMsg07() : null),
                (!bean1.getbolDadosMsg08().equalsIgnoreCase("") ? bean1.getbolDadosMsg08() : null),
                (!bean1.getbolDadosMsg09().equalsIgnoreCase("") ? bean1.getbolDadosMsg09() : null),
                (bean1.getbolDadosMsg10() != null ? bean1.getbolDadosMsg10() : null)
            };

            // S
            int nrlin = 1;
            for (int z=0;z<msg.length;z++) {
                if (msg[z] != null) {
                    String codbcc = "033";
                    String nrorem = "0001";
                    String tppreg = "3";

                    String nrSeqs = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                    String cdoseg = "S";
                    String branrs = FuncoesGlobais.Space(1);
                    String cdgmvt = jtparq.getSelectedItem().toString().trim().substring(0, 2); // ou 02 - baixa

                    String idimpr = "1";
                    String nrlnip = FuncoesGlobais.StrZero(String.valueOf(nrlin++), 2); // nrlinha impressa 01 ate 22
                    String msgimp = "4"; 
                    String msgipr = FuncoesGlobais.myLetra(new Pad(msg[z],100).RPad().toUpperCase()); //"(100)"; // mensagem a imprimir
                    String brancs = FuncoesGlobais.Space(119);

                    output = codbcc + nrorem + tppreg + nrSeqs + 
                             cdoseg + branrs + cdgmvt + idimpr +
                             nrlnip + msgimp + msgipr + brancs;
                    filler.Print(output + LF);

                    ctalinhas += 1;
                }
            }
        }
        
        if (filler.Open()) {
            // trailer lote
            String cdgcom = "033";
            String nrores = "0001";
            String tporgt = "5";
            String brantl = FuncoesGlobais.Space(9);
            String qtdrlt = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 1), 6); //"000000"; // quantidade reg no lote
            String brcolt = FuncoesGlobais.Space(217);

            String output = cdgcom + nrores + tporgt + brantl + qtdrlt + brcolt;
            filler.Print(output + LF);

            ctalinhas += 1;
            
            // trailer arquivo remessa
            String cgdcop = "033";
            String nrolte = "9999";
            String tpregi = "9";
            String brcoat = FuncoesGlobais.Space(9);
            String qtdlaq = "000001"; // quantidade de lotes do arquivo
            String qtdrga = FuncoesGlobais.StrZero(String.valueOf(ctalinhas), 6); //"000000"; // quantidade reg do arquivo tipo=0+1+2+3+5+9
            String brcalt = FuncoesGlobais.Space(211);

            output = cgdcop + nrolte + tpregi + brcoat + qtdlaq + qtdrga + brcalt;
            filler.Print(output + LF);
        }        
        filler.Close();        
        
        JOptionPane.showMessageDialog(null, "Arquivo de remessa " + jnomeArq.getText().trim() + "_" + jnrlote.getValue().toString().trim() + ".rem" + " gerado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        jnrlote.setValue(Integer.valueOf(nrlote));
        jnomeArq.setText(null);
    }
    
    private Boleta CreateBoleta() throws SQLException {
        bancos.LerBancoAvulso(jbancos.getSelectedItem().toString().substring(0, 3));

        Collections gVar = VariaveisGlobais.dCliente;

        Boleta bean1 = new Boleta();
        bean1.setempNome(gVar.get("empresa").toUpperCase().trim());
        bean1.setempEndL1(gVar.get("endereco") + ", " + gVar.get("numero") + gVar.get("complemento") + " - " + gVar.get("bairro"));
        bean1.setempEndL2(gVar.get("cidade") + " - " + gVar.get("estado") + " - CEP " + gVar.get("cep"));
        bean1.setempEndL3("Tel/Fax.: " + gVar.get("telefone"));
        bean1.setempEndL4(gVar.get("hpage") + " / " + gVar.get("email"));

        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/boleta/" + VariaveisGlobais.icoBoleta);

        bean1.setlocaMsgL01("");
        bean1.setlocaMsgL02("");

        // Logo do Banco
        bean1.setlogoBanco(bancos.getLogo());
        bean1.setnumeroBanco(bancos.getBanco() + "-" + Santander.CalcDig11N(bancos.getBanco()));

        try {
            bean1.setlocaDescL01(phistorico.getText());
        } catch (Exception ex) {}

        String vencto = Dates.DateFormata("dd-MM-yyyy", jvencto.getDate());
        bean1.setbolDadosVencimento(vencto);

        String cValor = bancos.Valor4Boleta(jvalor.getText());  // valor da boleta
        bean1.setbolDadosVrdoc(jvalor.getText());

        String banco = "";
        if (bancos.getBanco().equalsIgnoreCase("104")) {
            banco = "cef";
        } else if (bancos.getBanco().equalsIgnoreCase("341")) {
            banco = "itau";
        } else if (bancos.getBanco().equalsIgnoreCase("033")) {
            banco = "santander";
        } else if (bancos.getBanco().equalsIgnoreCase("001")) {
            banco = "bb";
        } else if (bancos.getBanco().equalsIgnoreCase("237")) {
            banco = "bd";
        } else banco = "";
        
        String nNumero = "";
        nNumero = FuncoesGlobais.StrZero(bancos.getNnumero().trim(),11);

        // Atualizar Nosso Numero
        String npart1, npart2;
        npart1 = nNumero.substring(0, 6);
        npart2 = nNumero.substring(6, 11);

        int innumero = Integer.valueOf(npart2) + 1;
        String snnumero = String.valueOf(npart1 + FuncoesGlobais.StrZero(String.valueOf(innumero),5));
        bancos.GravarNnumero(bancos.getBanco(), snnumero);

        String showNossoNumero = ""; String showCarteira = "";
        if (banco.equalsIgnoreCase("itau")) {
            nNumero =  itau.NossoNumeroItau(nNumero, 9);                    
            showNossoNumero = bancos.getCarteira() + "/" + nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
            showCarteira = bancos.getCarteira();
        } else if (banco.equalsIgnoreCase("cef")) {
            nNumero =  CEF.NossoNumero(nNumero, 16);                                        
            showNossoNumero = bancos.getCarteira() +  nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
            showCarteira = "SR";
        } else if (banco.equalsIgnoreCase("santander")) {
            nNumero =  Santander.NossoNumero(nNumero, 13);
            showNossoNumero = nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
            showCarteira = bancos.getCarteira();
        } else if (banco.equalsIgnoreCase("bb")) {
            nNumero = bb.NossoNumeroBB(bancos.getConta(),nNumero);
            showNossoNumero = nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
            showCarteira = bancos.getCarteira();
        } else if (banco.equalsIgnoreCase("bd")) {
            nNumero = bradesco.NossoNumeroBradesco(nNumero.substring(0, 11),bancos.getCarteira());
            showCarteira = bancos.getCarteira();
            showNossoNumero = bancos.getCarteira() + "/" + nNumero.substring(0, 11) + "-" + nNumero.substring(11, 12);
        }

        String cdBarras = ""; String lnDig = "";
        if (banco.toLowerCase().equalsIgnoreCase("itau")) {
            cdBarras = itau.CodBar(vencto, cValor,nNumero);
            lnDig = itau.LinhaDigitavel(cdBarras);  
        } else if (banco.toLowerCase().equalsIgnoreCase("cef")) {
            cdBarras = CEF.CodBar(vencto,cValor,nNumero);
            lnDig = CEF.linhadigitavel(nNumero,vencto,cValor);
        } else if (banco.toLowerCase().equalsIgnoreCase("santander")) {
            cdBarras = Santander.CodBar(vencto, cValor, nNumero);
            lnDig = Santander.LinhaDigitavel(nNumero, cdBarras.substring(4, 5), vencto, cValor);
        } else if (banco.toLowerCase().equalsIgnoreCase("bb")) {
            cdBarras = bb.CodBar(vencto, cValor, nNumero.substring(0, 11));
            lnDig = bb.LinhaDigitavel(cdBarras, cdBarras.substring(4, 5), vencto, cValor);
        } else if (banco.equalsIgnoreCase("bd")) {
            cdBarras = bradesco.CodBar(vencto, cValor, nNumero.substring(0, 11));
            lnDig = bradesco.LinhaDigitavel(cdBarras, vencto, cValor);
        }

        if (banco.toLowerCase().equalsIgnoreCase("itau")) {
            bean1.setbolDadosAgcodced(bancos.getAgencia() + " / " + bancos.getConta() + "-" + bancos.getCtaDv());
        } else if (banco.toLowerCase().equalsIgnoreCase("cef")) {
            bean1.setbolDadosAgcodced(bancos.getAgencia() + " / " + bancos.getConta() + "-" + bancos.getCtaDv());
        } else if (banco.toLowerCase().equalsIgnoreCase("santander")) {
            bean1.setbolDadosAgcodced(bancos.getAgencia() + "-" + Santander.CalcDig11N(bancos.getAgencia()) + " / " + bancos.getConta());
        } else if (banco.toLowerCase().equalsIgnoreCase("bb")) {
            bean1.setbolDadosAgcodced(FuncoesGlobais.StrZero(bancos.getAgencia(),5) + "-" + bb.CalcDig11N(FuncoesGlobais.StrZero(bancos.getAgencia(),5)) + " / " + 
                                      FuncoesGlobais.StrZero(bancos.getCtaDv(),12) + "-" + bb.CalcDig11N(FuncoesGlobais.StrZero(bancos.getCtaDv(),12)));
        } else if (banco.toLowerCase().equalsIgnoreCase("bd")) {
            bean1.setbolDadosAgcodced(bancos.getAgencia() + 
                    "-" + 
                    bradesco.CalcDig11Bradesco(bancos.getAgencia()) + 
                    " / " + 
                    FuncoesGlobais.StrZero(bancos.getCtaDv(),7) + 
                    "-" + 
                    bradesco.CalcDig11Bradesco(bancos.getCtaDv()) 
            );
        }

        bean1.setbolDadosNnumero(showNossoNumero);
        bean1.setbolDadosNumdoc("AVULSA");

        bean1.setsacDadosNome(pnome.getText().toUpperCase());  // Nome do Sacado
        bean1.setsacDadosCpfcnpj("CNPJ/CPF: " + pdocumento.getText().trim());  // Cpf ou Cnpj do Sacado

        bean1.setsacDadosEndereco(pendereco.getText().toUpperCase());
        bean1.setsacDadosNumero(pnumero.getText().toUpperCase());
        bean1.setsacDadosCompl(pcomplto.getText().toUpperCase());
        bean1.setsacDadosBairro(pbairro.getText().toUpperCase());
        bean1.setsacDadosCidade(pcidade.getText().toUpperCase());
        bean1.setsacDadosEstado(pestado.getText().toUpperCase());
        bean1.setsacDadosCep(pcep.getText().toUpperCase());

        bean1.setcodDadosDigitavel(lnDig);
        bean1.setcodDadosBarras(cdBarras);


        String msgCabBol01 = conn.LerParametros("MSGCABBOL1"); if (msgCabBol01 == null) msgCabBol01 = "";
        String msgCabBol02 = conn.LerParametros("MSGCABBOL2"); if (msgCabBol02 == null) msgCabBol02 = "";
        String msgCabBolDoc = conn.LerParametros("MSGCABBOLDOC"); if (msgCabBolDoc == null) msgCabBolDoc = "";

        if (!msgCabBol01.isEmpty() && !msgCabBol02.isEmpty() && !msgCabBolDoc.isEmpty()) {
            bean1.setbcoMsgL01(msgCabBol01);
            bean1.setbcoMsgL02(msgCabBol02);
            bean1.setbolDadosEspeciedoc(msgCabBolDoc);
        } else {
            if (banco.toLowerCase().equalsIgnoreCase("itau")) {
                bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
                bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NO BANCO ITAU OU IMOBILIARIA.");
                bean1.setbolDadosEspeciedoc("RC");
            } else if (banco.toLowerCase().equalsIgnoreCase("cef")) {
                bean1.setbcoMsgL01("PAGAR PREFERENCIALMENTE NAS CASAS LOTERICAS ATE O VALOR LIMITE");
                bean1.setbcoMsgL02("");
                bean1.setbolDadosEspeciedoc("RC");
            } else if (banco.toLowerCase().equalsIgnoreCase("santander")) {
                bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
                bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NO BANCO SANTANDER.");
                bean1.setbolDadosEspeciedoc("DM");
            } else if (banco.toLowerCase().equalsIgnoreCase("bb")) {
                bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
                bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NO BANCO DO BRASIL.");
                bean1.setbolDadosEspeciedoc("RC");            
            } else if (banco.toLowerCase().equalsIgnoreCase("bd")) {
                    bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
                    bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NO BANCO BRADESCO S/A..");
                    bean1.setbolDadosEspeciedoc("RC");            
            }
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
 
        String msgBol01 = jAvMsg01.getText(); if (msgBol01 == null) msgBol01 = "";
        String msgBol02 = jAvMsg02.getText(); if (msgBol02 == null) msgBol02 = "";
        String msgBol03 = jAvMsg03.getText(); if (msgBol03 == null) msgBol03 = "";
        String msgBol04 = jAvMsg04.getText(); if (msgBol04 == null) msgBol04 = "";
        String msgBol05 = jAvMsg05.getText(); if (msgBol05 == null) msgBol05 = "";
        String msgBol06 = jAvMsg06.getText(); if (msgBol06 == null) msgBol06 = "";
        String msgBol07 = jAvMsg07.getText(); if (msgBol07 == null) msgBol07 = "";
        String msgBol08 = jAvMsg08.getText(); if (msgBol08 == null) msgBol08 = "";
        String msgBol09 = jAvMsg09.getText(); if (msgBol09 == null) msgBol09 = "";

        bean1.setbolDadosMsg01(msgBol01);
        bean1.setbolDadosMsg02(msgBol02);
        bean1.setbolDadosMsg03(msgBol03);
        bean1.setbolDadosMsg04(msgBol04);
        bean1.setbolDadosMsg05(msgBol05);
        bean1.setbolDadosMsg06(msgBol06);
        bean1.setbolDadosMsg07(msgBol07);

        Date tvecto = Dates.StringtoDate(vencto,"dd/MM/yyyy");
        String carVecto = Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, 5, tvecto));

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
            ln08 = ln08.replace("[MULTA]", "10%");
            ln08 = ln08.replace("[ENCARGOS]", "0,333%");
        }
        bean1.setbolDadosMsg08(ln08);
        
//        bean1.setbolDadosMsg08("".equals(msgBol08) ? "APÓS O DIA " + 
//            Dates.DateFormata("dd-MM-yyyy", jvencto.getDate()) + 
//            " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO." : "" + msgBol08);
        bean1.setbolDadosMsg09("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);

        bean1.setbolDadosDesconto("");
        bean1.setbolDadosMora("");
        bean1.setbolDadosVrcobrado("");

        return bean1;

    }
    
    private String fmtNumero(String value) {
        String saida = "";
        try {
            String numero = "000000000000000";
            value = value.substring(0, value.indexOf(",") + 3);
            saida = (numero + rmvNumero(value)).trim();
        } catch (Exception e) {}
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
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        opcprop = new javax.swing.JRadioButton();
        opcloca = new javax.swing.JRadioButton();
        opcavulsa = new javax.swing.JRadioButton();
        jnome = new javax.swing.JComboBox();
        jcodigo = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        pnome = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        pdocumento = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        pendereco = new javax.swing.JTextField();
        pnumero = new javax.swing.JTextField();
        pcomplto = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        pbairro = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        pcidade = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        phistorico = new javax.swing.JTextArea();
        btImprimir = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jbancos = new javax.swing.JComboBox();
        jvalor = new javax.swing.JFormattedTextField();
        jvencto = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        pcep = new javax.swing.JFormattedTextField();
        pestado = new javax.swing.JFormattedTextField();
        btLimpar = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jtparq = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        jnrlote = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        jnomeArq = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jAvMsg01 = new javax.swing.JTextField();
        jAvMsg02 = new javax.swing.JTextField();
        jAvMsg03 = new javax.swing.JTextField();
        jAvMsg04 = new javax.swing.JTextField();
        jAvMsg05 = new javax.swing.JTextField();
        jAvMsg06 = new javax.swing.JTextField();
        jAvMsg07 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jAvMsg08 = new javax.swing.JTextField();
        jAvMsg09 = new javax.swing.JTextField();
        jmsgGravar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Boleta Avulsa");

        jTabbedPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTabbedPane2MousePressed(evt);
            }
        });

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        buttonGroup1.add(opcprop);
        opcprop.setSelected(true);
        opcprop.setText("Proprietário");
        opcprop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcpropActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcloca);
        opcloca.setText("Locatarios");
        opcloca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opclocaActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcavulsa);
        opcavulsa.setText("Avulsa");
        opcavulsa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcavulsaActionPerformed(evt);
            }
        });

        jnome.setEditable(true);
        jnome.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jnome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jnomeActionPerformed(evt);
            }
        });

        jcodigo.setEditable(true);
        jcodigo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcodigoActionPerformed(evt);
            }
        });

        jLabel8.setText("Nome:");

        pnome.setText("jTextField1");

        jLabel9.setText("CPF/CNPJ:");

        pdocumento.setText("jTextField2");

        jLabel10.setText("Endereço:");

        pendereco.setText("jTextField3");

        pnumero.setText("jTextField4");

        pcomplto.setText("jTextField5");

        jLabel11.setText("Bairro:");

        pbairro.setText("jTextField6");

        jLabel12.setText("Cidade:");

        pcidade.setText("jTextField7");

        jLabel13.setText("Estado:");

        jLabel14.setText("Cep:");

        jLabel15.setText("Vencimento:");

        jLabel16.setText("Valor:");

        jLabel17.setText("Histórico");

        phistorico.setColumns(20);
        phistorico.setRows(5);
        jScrollPane7.setViewportView(phistorico);

        btImprimir.setText("Imprimir");
        btImprimir.setToolTipText("Esta boleta não tem efeito contábil no sistema, devendo a entrada do valor ser feita após o recebimento desta manualmente na conta apropriada.");
        btImprimir.setEnabled(false);
        btImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btImprimirActionPerformed(evt);
            }
        });

        jLabel1.setText("Banco:");

        jbancos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jbancos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbancosActionPerformed(evt);
            }
        });

        jvalor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jvalor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jvalor.setText("0,00");

        jvencto.setDate(new java.util.Date(-2208977612000L));

        try {
            pcep.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        pestado.setText("jFormattedTextField1");

        btLimpar.setText("Limpar");
        btLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLimparActionPerformed(evt);
            }
        });

        jLabel18.setText("Tipo de Arquivo:");

        jtparq.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01 - ENTRADA DE TITULOS", "02 - PEDIDO DE BAIXA", "06 - ALTERAÇÃO DE VENCIMENTO", "09 - PEDIDO DE PROTESTO", "18 - PEDIDO DE SUSTAÇÃO DE PROTESTO", "31 - ALTERAÇÃO DE OUTROS DADOS", "98 - NÃO PROTESTAR (ANTES DE INICIAR CICLO DE PROTESTO)" }));

        jLabel19.setText("Lote Nº.:");

        jnrlote.setModel(new javax.swing.SpinnerNumberModel());

        jLabel20.setText("Arquivo:");

        jnomeArq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jnomeArqKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jcodigo, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jnome, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(opcprop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(opcloca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(opcavulsa))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnome))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pendereco, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnumero, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pcomplto))
                    .addComponent(jScrollPane7)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(114, 114, 114)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbancos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(pestado, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pcep, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(pbairro, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pcidade))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btLimpar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jvencto, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jvalor, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtparq, 0, 1, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pdocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jnrlote, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jnomeArq, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btImprimir)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(opcprop)
                    .addComponent(opcloca)
                    .addComponent(opcavulsa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jnome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(pnome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(pdocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(pendereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pcomplto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(pbairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(pcidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(pcep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pestado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel1)
                    .addComponent(jbancos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btLimpar)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jvalor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jvencto, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jtparq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jnomeArq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jnrlote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btImprimir))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Boleta", jPanel2);

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setText("Menssagens Superiores:");

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel3.setText("Menssagens Inferiores:");

        jAvMsg08.setBackground(new java.awt.Color(160, 203, 254));
        jAvMsg08.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jAvMsg08.setForeground(new java.awt.Color(0, 17, 255));
        jAvMsg08.setToolTipText("APÓS O DIA [VENCIMENTO] MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO.");
        jAvMsg08.setSelectedTextColor(new java.awt.Color(0, 255, 114));

        jAvMsg09.setBackground(new java.awt.Color(160, 203, 254));
        jAvMsg09.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jAvMsg09.setForeground(new java.awt.Color(0, 17, 255));
        jAvMsg09.setToolTipText("NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO.");
        jAvMsg09.setSelectedTextColor(new java.awt.Color(0, 255, 114));

        jmsgGravar.setText("Gravar");
        jmsgGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmsgGravarActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 10)); // NOI18N
        jLabel4.setText("Legenda: (linha 08)");

        jLabel5.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jLabel5.setText("[VENCIMENTO] - Data de Vencimento do Boleto.");

        jLabel6.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jLabel6.setText("[CARENCIA] - Data de Vencimento acrescido de dias de carência.");

        jLabel7.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jLabel7.setText("[MULTA] - Multa padrão de 10%.");

        jLabel21.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jLabel21.setText("[ENCARGOS] - Encargos padrão de 0,333%.");

        jLabel22.setText("L01:");

        jLabel23.setText("L08:");

        jLabel24.setText("L09:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAvMsg02)
                    .addComponent(jAvMsg03)
                    .addComponent(jAvMsg04)
                    .addComponent(jAvMsg05)
                    .addComponent(jAvMsg06)
                    .addComponent(jAvMsg07)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAvMsg01))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jAvMsg08, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAvMsg09))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jmsgGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel21)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAvMsg01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg02, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg03, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg04, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg05, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg06, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg07, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAvMsg08, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(2, 2, 2)
                .addComponent(jLabel5)
                .addGap(0, 0, 0)
                .addComponent(jLabel6)
                .addGap(0, 0, 0)
                .addComponent(jLabel7)
                .addGap(0, 0, 0)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAvMsg09, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jmsgGravar)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Menssagem", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btImprimirActionPerformed
        List<Boleta> lista = new ArrayList<Boleta>();
        try {
            Bean1 = CreateBoleta();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        lista.add(Bean1);

        String rgprp = null, rgimv = null, contrato = null;
        if (opcprop.isSelected()) {
            rgprp = jcodigo.getSelectedItem().toString();
            rgimv = "";
            contrato = "";
        } else if (opcloca.isSelected()) {
            rgprp = "";
            rgimv = "";
            contrato = jcodigo.getSelectedItem().toString();
        } else {
            rgprp = "";
            rgimv = "";
            contrato = "";
        }
        // Gravar no arquivo Boletas
        String cSql = "INSERT INTO bloquetos (`rgprp`,`rgimv`,`contrato`," +
        "`nome`,`vencimento`,`valor`,`nnumero`) " +
        "VALUES (\"&1.\",\"&2.\",\"&3.\",\"&4.\",\"&5.\",\"&6.\",\"&7.\")";
        cSql = FuncoesGlobais.Subst(cSql, new String[] {
            rgprp,
            rgimv,
            contrato,
            Bean1.getsacDadosNome(),
            Dates.DateFormata("yyyy-MM-dd",jvencto.getDate()),
            Bean1.getbolDadosVrdoc(),
            Bean1.getbolDadosNnumero(),
        });
        try {
            conn.ExecutarComando(cSql);
        } catch (Exception e) {e.printStackTrace();}
        
        JRDataSource jrds = new JRBeanCollectionDataSource(lista);

        try {
            String fileName = "reports/Boletos_avulsas.jasper";
            JasperPrint print = JasperFillManager.fillReport(fileName, null, jrds);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            new jDirectory("reports/Boletas/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
            String pathName = "reports/Boletas/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
            
            // Configure the exporter (set output file name and print object)
            String outFileName;
            if (!opcavulsa.isSelected()) {
                outFileName = pathName + 
                    "BOLETA_AVULSA_" +
                    jcodigo.getSelectedItem().toString() + "_" + 
                    jnome.getSelectedItem().toString().trim() + "_" + 
                    Dates.DateFormata("ddMMyyyy", jvencto.getDate()) + "_" +
                    Bean1.getbolDadosNnumero().substring((bancos.getBanco().equalsIgnoreCase("341") ? 4 : 3)) + ".pdf";
            } else {
                outFileName = pathName + 
                    "BOLETA_AVULSA_" +
                    pnome.getText().toString().trim() + "_" + 
                    Dates.DateFormata("ddMMyyyy", jvencto.getDate()) + "_" +
                    Bean1.getbolDadosNnumero().substring((bancos.getBanco().equalsIgnoreCase("341") ? 4 : 3)) + ".pdf";
            }
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();

            //JasperViewer viewer = new JasperViewer(print, false);
            //viewer.show();
            viewReportFrame("Boleto Bancário", print);

        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        // Gerar remessa avulsa
        GeraRemessaAvulsa();
    }//GEN-LAST:event_btImprimirActionPerformed

    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLimparActionPerformed
        if (opcprop.isSelected() || opcloca.isSelected()) return;
        
        Clean();
        phistorico.setText(null);
        jvencto.setDate(new Date());
        jvalor.setText("0,00");
    }//GEN-LAST:event_btLimparActionPerformed

    private void jcodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcodigoActionPerformed
        if (!bExecNome) {
            int pos = jcodigo.getSelectedIndex();
            if (jnome.getItemCount() > 0) {bExecCodigo = true; jnome.setSelectedIndex(pos); bExecCodigo = false;}
        }
        Setar();
    }//GEN-LAST:event_jcodigoActionPerformed

    private void jnomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jnomeActionPerformed
        if (!bExecCodigo) {
            int pos = jnome.getSelectedIndex();
            if (jcodigo.getItemCount() > 0) {bExecNome = true; jcodigo.setSelectedIndex(pos); bExecNome = false;}
        }
        Setar();
    }//GEN-LAST:event_jnomeActionPerformed

    private void opcpropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcpropActionPerformed
        FillOpcao();
    }//GEN-LAST:event_opcpropActionPerformed

    private void opclocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opclocaActionPerformed
        FillOpcao();
    }//GEN-LAST:event_opclocaActionPerformed

    private void opcavulsaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcavulsaActionPerformed
        FillOpcao();
    }//GEN-LAST:event_opcavulsaActionPerformed

    private void jnomeArqKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jnomeArqKeyReleased
        btImprimir.setEnabled(jnomeArq.getText().trim().length() > 3);
    }//GEN-LAST:event_jnomeArqKeyReleased

    private void jbancosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbancosActionPerformed
        // Numerador de lote
        if (jbancos.getSelectedItem() != null) {
            String _banco = jbancos.getSelectedItem().toString().trim().substring(0, 3);
            int lotenr = 0; try { lotenr = Integer.valueOf(conn.LerParametros("LOTE_" + _banco)); } catch (Exception e) {}
            jnrlote.setValue(lotenr);
        }
    }//GEN-LAST:event_jbancosActionPerformed

    private void jTabbedPane2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane2MousePressed
        LerMsgAv();
    }//GEN-LAST:event_jTabbedPane2MousePressed

    private void LerMsgAv() {
        String avsMSG01 = ""; try { avsMSG01 = conn.LerParametros("avsMSG01"); } catch (Exception e) {}
        String avsMSG02 = ""; try { avsMSG02 = conn.LerParametros("avsMSG02"); } catch (Exception e) {}
        String avsMSG03 = ""; try { avsMSG03 = conn.LerParametros("avsMSG03"); } catch (Exception e) {}
        String avsMSG04 = ""; try { avsMSG04 = conn.LerParametros("avsMSG04"); } catch (Exception e) {}
        String avsMSG05 = ""; try { avsMSG05 = conn.LerParametros("avsMSG05"); } catch (Exception e) {}
        String avsMSG06 = ""; try { avsMSG06 = conn.LerParametros("avsMSG06"); } catch (Exception e) {}
        String avsMSG07 = ""; try { avsMSG07 = conn.LerParametros("avsMSG07"); } catch (Exception e) {}
        String avsMSG08 = ""; try { avsMSG08 = conn.LerParametros("avsMSG08"); } catch (Exception e) {}
        String avsMSG09 = ""; try { avsMSG09 = conn.LerParametros("avsMSG09"); } catch (Exception e) {}
        
        jAvMsg01.setText(avsMSG01); jAvMsg02.setText(avsMSG02);
        jAvMsg03.setText(avsMSG03); jAvMsg04.setText(avsMSG04);
        jAvMsg05.setText(avsMSG05); jAvMsg06.setText(avsMSG06);
        jAvMsg07.setText(avsMSG07); jAvMsg08.setText(avsMSG08);
        jAvMsg09.setText(avsMSG09);
    }
    
    private void jmsgGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmsgGravarActionPerformed
        String avsMSG01 = jAvMsg01.getText(); try { conn.GravarParametros(new String[] {"avsMSG01",avsMSG01,"TEXTO"}); } catch (Exception e) {}
        String avsMSG02 = jAvMsg02.getText(); try { conn.GravarParametros(new String[] {"avsMSG02",avsMSG02,"TEXTO"}); } catch (Exception e) {}
        String avsMSG03 = jAvMsg03.getText(); try { conn.GravarParametros(new String[] {"avsMSG03",avsMSG03,"TEXTO"}); } catch (Exception e) {}
        String avsMSG04 = jAvMsg04.getText(); try { conn.GravarParametros(new String[] {"avsMSG04",avsMSG04,"TEXTO"}); } catch (Exception e) {}
        String avsMSG05 = jAvMsg05.getText(); try { conn.GravarParametros(new String[] {"avsMSG05",avsMSG05,"TEXTO"}); } catch (Exception e) {}
        String avsMSG06 = jAvMsg06.getText(); try { conn.GravarParametros(new String[] {"avsMSG06",avsMSG06,"TEXTO"}); } catch (Exception e) {}
        String avsMSG07 = jAvMsg07.getText(); try { conn.GravarParametros(new String[] {"avsMSG07",avsMSG07,"TEXTO"}); } catch (Exception e) {}
        String avsMSG08 = jAvMsg08.getText(); try { conn.GravarParametros(new String[] {"avsMSG08",avsMSG08,"TEXTO"}); } catch (Exception e) {}
        String avsMSG09 = jAvMsg09.getText(); try { conn.GravarParametros(new String[] {"avsMSG09",avsMSG09,"TEXTO"}); } catch (Exception e) {}
    }//GEN-LAST:event_jmsgGravarActionPerformed

    private static void viewReportFrame( String titulo, JasperPrint print ) {

            /*
             * Cria um JRViewer para exibir o relatório.
             * Um JRViewer é uma JPanel.
             */
            JRViewer viewer = new JRViewer( print );

            // cria o JFrame
            JFrame frameRelatorio = new JFrame( titulo );

            // adiciona o JRViewer no JFrame
            frameRelatorio.add( viewer, BorderLayout.CENTER );

            // configura o tamanho padrão do JFrame
            frameRelatorio.setSize( 500, 500 );

            // maximiza o JFrame para ocupar a tela toda.
            frameRelatorio.setExtendedState( JFrame.MAXIMIZED_BOTH );

            // configura a operação padrão quando o JFrame for fechado.
            frameRelatorio.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

            // exibe o JFrame
            frameRelatorio.setVisible( true );

        }    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btImprimir;
    private javax.swing.JButton btLimpar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField jAvMsg01;
    private javax.swing.JTextField jAvMsg02;
    private javax.swing.JTextField jAvMsg03;
    private javax.swing.JTextField jAvMsg04;
    private javax.swing.JTextField jAvMsg05;
    private javax.swing.JTextField jAvMsg06;
    private javax.swing.JTextField jAvMsg07;
    private javax.swing.JTextField jAvMsg08;
    private javax.swing.JTextField jAvMsg09;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JComboBox jbancos;
    private javax.swing.JComboBox jcodigo;
    private javax.swing.JButton jmsgGravar;
    private javax.swing.JComboBox jnome;
    private javax.swing.JTextField jnomeArq;
    private javax.swing.JSpinner jnrlote;
    private javax.swing.JComboBox jtparq;
    private javax.swing.JFormattedTextField jvalor;
    private com.toedter.calendar.JDateChooser jvencto;
    private javax.swing.JRadioButton opcavulsa;
    private javax.swing.JRadioButton opcloca;
    private javax.swing.JRadioButton opcprop;
    private javax.swing.JTextField pbairro;
    private javax.swing.JFormattedTextField pcep;
    private javax.swing.JTextField pcidade;
    private javax.swing.JTextField pcomplto;
    private javax.swing.JTextField pdocumento;
    private javax.swing.JTextField pendereco;
    private javax.swing.JFormattedTextField pestado;
    private javax.swing.JTextArea phistorico;
    private javax.swing.JTextField pnome;
    private javax.swing.JTextField pnumero;
    // End of variables declaration//GEN-END:variables
}
