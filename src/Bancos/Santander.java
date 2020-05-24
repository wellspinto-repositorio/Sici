/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Bancos;

import static Bancos.bancos.SoNumeroSemZerosAEsq;
import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import static Funcoes.FuncoesGlobais.StrZero;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.StreamFile;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;
import Protocolo.Calculos;
import Sici.Partida.Collections;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author supervisor
 */
public class Santander {
    static private String FatorVencimento(String fator, String vencimento) {
        String retorno = "0000";
        if (vencimento.length() < 8) {retorno = "0000";} else {
            retorno = String.valueOf(Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "MM/dd/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
        }
        
        return retorno;
    }
    
    static public String CalcDig11N(String cadeia) {
        int total= 0; int mult = 2;
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 9) mult = 2;
        }
        int soma = total; // * 10;
        int resto = (soma % 11);
        if (resto == 0 || resto == 1 || resto == 10) { 
            resto = 1; 
        } else if (resto > 10) {
            resto = 1; 
        } else {
            resto = 11 - resto;
        }
        return String.valueOf(resto);
    }
     
    static private String CalcDig11NBarrasSantander(String cadeia) {
        int total= 0; int mult = 2;
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 9) mult = 2;
        }
        int soma = total * 10;
        int resto = (soma % 11);
        if (resto == 0 || resto == 1 || resto == 10) { 
            resto = 1; 
        } else {
            resto = resto;
        }
        return String.valueOf(resto);
    }
    
    static public String CodBar(String vencimento,String valor,String nossonumero) {
        String part1; String part2; String dv;
        part1 = bancos.getBanco() + bancos.getMoeda();
        part2 = FatorVencimento("10/07/1997", vencimento) + bancos.Valor4Boleta(valor) + "9" + bancos.getCtaDv() + nossonumero + "0" + bancos.getCarteira();
        dv = CalcDig11N(part1 + part2);
        //dv = CalcDig11NBarrasSantander(part1 + part2);
        return part1 + dv + part2;
    }

    static public String LinhaDigitavel(String nossonumero, String codbardv, String vencimento, String valortitulo) {
        String campo1, campo2, campo3, campo4, campo5;
        
        campo1 = bancos.getBanco() + bancos.getMoeda() + "9" + StringManager.Left(bancos.getCtaDv(), 4);
        campo1 += bancos.CalcDig10(campo1);
        campo1 = campo1.substring(0, 5) + "." + campo1.substring(5,10);
        
        campo2 = StringManager.Right(bancos.getCtaDv(),3) + StringManager.Left(nossonumero, 7);
        campo2 += bancos.CalcDig10(campo2);
        campo2 = campo2.substring(0,5) + "." + campo2.substring(5, 11);
        
        campo3 = StringManager.Right(nossonumero, 6) + "0" + bancos.getCarteira();
        campo3 += bancos.CalcDig10(campo3);
        campo3 = campo3.substring(0, 5) + "." + campo3.substring(5, 11);
        
        
        String dvcb = bancos.getBanco() + bancos.getMoeda() + FatorVencimento("10/07/1997", vencimento) +
                       bancos.Valor4Boleta(valortitulo) + "9" + StringManager.Left(bancos.getCtaDv(), 7) +
                       nossonumero + "0" + bancos.getCarteira();
        
        campo4 = CalcDig11NBarrasSantander(dvcb);
        
        campo5 = FatorVencimento("10/07/1997", vencimento) + bancos.Valor4Boleta(valortitulo);
        
        return campo1 + "  " + campo2 + "  " + campo3 + "  " + campo4 + "  " + campo5;
    }    
    
    static public String NossoNumero(String value, int tam) {
        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
                        Integer.valueOf(value).toString().trim(),tam - 1);
        String valor2 = CalcDig11N(valor1);
        return valor1 + valor2;
    }
    
    static public String Remessa(String nrlote, String fileName, String movimento, String[][] lista, Object tipo) {
        if (lista.length == 0) return "Lista vazia!";
        
        bancos.LerBancoAvulso("033");
        
        File diretorio = new File("remessa"); if (!diretorio.exists()) { diretorio.mkdirs(); }
        
        String nroLote = nrlote;
        if (tipo == "A" || tipo == null) try {nroLote =  FuncoesGlobais.StrZero(VariaveisGlobais.conexao.LerParametros("LOTE_" + bancos.getBanco()),5);} catch (SQLException e) { nroLote = "00000"; }
        
        File arquivo = new File("remessa/" + bancos.getBanco() + fileName + nroLote + ".rem");
        if (arquivo.exists()) {
            JOptionPane.showMessageDialog(null, "Arquivo de remessa ja existe!!!\n\nTente novamente com outro nome.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return "Arquivo de remessa ja existe";
        }
        
        String LF = "\r\n";
        
        String _banco = bancos.getBanco();
        if (tipo == "A" || tipo == null) {
            String[] variaveis = {"LOTE_" + _banco, nrlote,"NUMERICO"};
            try { VariaveisGlobais.conexao.GravarParametros(variaveis); } catch (Exception e) {}
        }
        
        String nmEmp = new Pad(VariaveisGlobais.dCliente.get("empresa"),30).RPad();
        String ticEmp = bancos.rmvNumero(VariaveisGlobais.dCliente.get("cnpj")).trim();
        String icEmp = FuncoesGlobais.StrZero(ticEmp,15);
        int ctalinhas = 1;
        StreamFile filler = new StreamFile(new String[] {"remessa/" + bancos.getBanco() + fileName + nroLote + ".rem"});
        if (filler.Open()) {
            String codBaco = _banco;
            String loteSer = "0000";
            String tipoSer = "0";
            String reserVd = FuncoesGlobais.Space(8);
            String tipoInc = ticEmp.length() == 11 ? "1" : "2";
            String inscEmp = icEmp;  // cnpj
            String codTran = bancos.getAgencia() + FuncoesGlobais.StrZero(bancos.getCtaDv(), 11);   //"340500007926383";  // dados da conta 
            String reseVad = FuncoesGlobais.Space(25); // 25 digitos
            String nomeEmp = nmEmp; // 30 digitos
            String nomeBan = "BANCO SANTANDER" + FuncoesGlobais.Space(15); // 30 digitosg
            String resVado = FuncoesGlobais.Space(10); // 10 digitos
            String codRems = "1";
            String dtGerac = Dates.DateFormata("ddMMyyyy", new Date()); // data atual
            String rservDo = FuncoesGlobais.Space(6); // 6 barncos
            String numSequ = FuncoesGlobais.StrZero(nroLote, 6); // 6 digitos de 1 a 999999 <
            String Versaos = "040";
            String reSerVa = FuncoesGlobais.Space(74); // 74 digitos           
                    
            String output = codBaco + loteSer + tipoSer + reserVd + tipoInc +
                            inscEmp + codTran + reseVad + nomeEmp + nomeBan +
                            resVado + codRems + dtGerac + rservDo + numSequ +
                            Versaos + reSerVa;
            filler.Print(output + LF);

            ctalinhas += 1;
            
            String codBco = _banco; 
            String loteRe = "0001"; 
            String tpRems = "1";
            String tpOper = "R";
            String tpServ = "01";
            String reseVd = FuncoesGlobais.Space(2);
            String nversa = "030";
            String resedo = FuncoesGlobais.Space(1);
            String tpInsc = ticEmp.length() == 11 ? "1" : "2";
            String cnpjEp = icEmp;
            String resVdo = FuncoesGlobais.Space(20);
            String codTrE = bancos.getAgencia() + FuncoesGlobais.StrZero(bancos.getCtaDv(), 11); //"340500007926383";
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
        
        for (int i=0; i < lista.length; i++) {
            if (filler.Open()) {
                String _rgprp = null;
                String _rgimv = null;
                String _contrato = null;
                
                String _nome = null;
                String _cpfcnpj = null;
                
                String _ender = null;       // = vCampos[4][3].trim() + ", " + vCampos[5][3].trim() + " " + vCampos[6][3].trim();
                String _bairro = null;
                String _cidade = null;
                String _estado = null;
                String _cep = null;
                
                String _vencto = null;
                String _valor = null ;
                String _rnnumero = null;    // = lista[i][3].substring(0, 12);
                String _rnnumerodac = null; // = lista[i][3].substring(12, 13);
                
                String[][] vCampos = null;
                try {
                    if (tipo == "A" || tipo == null) {
                        vCampos = VariaveisGlobais.conexao.LerCamposTabela(new String[] {"l.contrato", "l.rgprp", "l.rgimv", "l.aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "'0' AS nnumero", "l.cpfcnpj", "l.nomerazao"}, "locatarios l, imoveis i, proprietarios p", "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp) AND l.contrato = '" + lista[i][0] + "'");
                    } else {
                        vCampos = VariaveisGlobais.conexao.LerCamposTabela(new String[] {"l.contrato", "l.rgprp", "l.rgimv", "l.aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "r.nnumero", "l.cpfcnpj", "l.nomerazao"}, "locatarios l, imoveis i, proprietarios p, RECIBO r", "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp AND r.contrato = l.contrato) AND l.contrato = '" + lista[i][0] + "'");
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }
                if (tipo == null) {
                    //
                    _rgprp = "";
                    _rgimv = "";
                    _contrato = "";

                    _nome = lista[i][4].trim();
                    _cpfcnpj = lista[i][5].trim();

                    _ender = lista[i][6].trim() + ", " + lista[i][7].trim() + " " + lista[i][8].trim();
                    _bairro = lista[i][9].trim();
                    _cidade = lista[i][10].trim();
                    _estado = lista[i][11].trim();
                    _cep = lista[i][12].trim();

                    _vencto = lista[i][1];
                    _valor = lista[i][2];
                    _rnnumero = lista[i][3];
                    _rnnumerodac = lista[i][3];
                } else {
                    if (vCampos == null) {
                        try {
                            if (tipo == "A") {
                                vCampos = VariaveisGlobais.conexao.LerCamposTabela(new String[] {"'' as contrato", "p.rgprp", "'0' as rgimv", "'' as aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "'0' AS nnumero", "p.cpfcnpj", "p.nome"}, "imoveis i, proprietarios p", "p.rgprp = '" + lista[i][0] + "'");
                            } else {
                                vCampos = VariaveisGlobais.conexao.LerCamposTabela(new String[] {"'' as contrato", "p.rgprp", "'0' as rgimv", "'' as aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "'0' as nnumero", "p.cpfcnpj", "p.nome"}, "imoveis i, proprietarios p", "p.rgprp = '" + lista[i][0] + "'");
                            }
                        } catch (SQLException ex) { ex.printStackTrace(); }
                       // "SELECT '0' as contrato, p.rgprp, '0' as rgimv, i.end, i.num, i.compl, i.bairro, i.cidade, i.estado, i.cep, p.nome, '0' AS nnumero, p.cpfcnpj, p.nome FROM imoveis i, proprietarios p WHERE p.rgprp = '1400'""
                    }
                    _rgprp = vCampos[1][3];
                    _rgimv = vCampos[2][3];
                    _contrato = vCampos[0][3];
                    if (_contrato.trim().equals("")) _contrato = vCampos[1][3];

                    _nome = vCampos[14][3].trim();
                    _cpfcnpj = vCampos[13][3].trim();

                    _ender = vCampos[4][3].trim() + ", " + vCampos[5][3].trim() + " " + vCampos[6][3].trim();
                    _bairro = vCampos[7][3].trim();
                    _cidade = vCampos[8][3].trim();
                    _estado = vCampos[9][3].trim();
                    _cep = vCampos[10][3].trim();

                    _vencto = lista[i][1];
                    _valor = lista[i][2];
                    _rnnumero = lista[i][3];
                    _rnnumerodac = lista[i][3];
                }
                
                // P
                String codBcC = _banco;  
                String nrReme = "0001";
                String tpRegi = "3";
                String nrSequ = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                String cdSegR = "P";
                String rsvDos = FuncoesGlobais.Space(1);
                String cdMvRm = movimento; // 01 - Entrada de título
                String agCedn = bancos.getAgencia(); //"3405";  // agencia do cedente
                String digAgc = CalcDig11N(bancos.getAgencia());  //"3"; // digito verificador
                String numCoC = bancos.getIdentificacao(); //"013000516"; // Conta Corrente 013000516
                String digCoC = bancos.getIdentDv(); //"0"; // digito verificador
                String contCb = bancos.getIdentificacao(); //"013000516"; // Conta cobranca 7926383
                String digtCb = bancos.getIdentDv();  // digito
                String rservo = FuncoesGlobais.Space(2);
                String nnumer = FuncoesGlobais.StrZero(_rnnumero,13); // nosso numero com 13 dig
                String tpoCob = "5"; // tipo de cobrança
                String formCd = "1"; // forma de cadastramento
                String tipoDc = "2"; // tipo de documento
                String rsvad1 = FuncoesGlobais.Space(1);
                String rsvad2 = FuncoesGlobais.Space(1);
                String numDoc = new Pad(_contrato,15).RPad();
                String dtavtt = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); // "ddmmaaaa"; // data de vencimento do titulo
                String vrnmtt = bancos.fmtNumero(_valor); //"000000000123129"; // valor nominal do titulo
                String agencb = "0000"; // agencia encarregada
                String digaec = "0"; // digito
                String rsvado = FuncoesGlobais.Space(1);
                String esptit = "04"; /// 04 - dup de serviço, 17 - recibo/ especie de titulo // 17 - Recibo
                String idtitu = "N";
                String dtemti = Dates.DateFormata("ddMMyyyy", new Date()); //"ddmmaaaa"; // data emissao do titulo
                String cdjuti = "1"; // codigo juros do titulo
                String dtjrmo = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data juros mora
                float valor = LerValor.StringToFloat(_valor) * 0.00033f;
                String vrmtxm = bancos.fmtNumero(LerValor.FloatToString(valor)); //"000000000000041"; // valor ou taxa de mora (aluguel * 0,0333)
                String cddesc = "0"; // codigo desconto
                String dtdesc = "00000000"; // data desconto
                String vrpecd = "000000000000000"; // valor ou percentual de desconto
                String vriofr = "000000000000000"; // iof a ser recolhido
                String vrabti = "000000000000000"; // valor abatimento
                String idttep = FuncoesGlobais.Space(25);
                String cdprot = "0"; // codigo para protesto
                String nrdpro = "00"; // numero de dias para protesto
                String cdbxdv = "1"; // codigo baixa devolucao (2)
                String revdao = "0";
                String nrdibd = "15"; // numero de dias baixa devolucao
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

                if (movimento.equalsIgnoreCase("01")) {
                    /*
                    Marca remessa = 'S' 
                    */
                    String wSql = "UPDATE recibo SET remessa = 'S' WHERE nnumero LIKE '%" + SoNumeroSemZerosAEsq(_rnnumero)  + "%';";
                    try {
                        VariaveisGlobais.conexao.ExecutarComando(wSql);
                    } catch (Exception e) {}


                    // Sqgmento Q
                    String cdbcoc = _banco; 
                    String nrltre = "0001";
                    String tiporg = "3";

                    String nrSeqq = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                    String cdregt = "Q";
                    String bracos = FuncoesGlobais.Space(1);
                    String cdmvrm = movimento; // ou 02 - pedido de baixa
                    String cpfCNPJ = bancos.rmvLetras(bancos.rmvNumero(_cpfcnpj));
                    String tpinss = (cpfCNPJ.length() == 11 ? "1" : "2"); // tipo inscricao sacado
                    String inscsc = FuncoesGlobais.StrZero(cpfCNPJ,15); //"000000000000000"; // CPF/CNPJ
                    String nmesac = FuncoesGlobais.myLetra(new Pad(_nome.toUpperCase(),40).RPad()); //"(40)"; // nome do sacado
                    String endsac = FuncoesGlobais.myLetra(new Pad(_ender,40).RPad().toUpperCase()); //"(40)"; // endereco 
                    String baisac = FuncoesGlobais.myLetra(new Pad(_bairro,15).RPad().toUpperCase()); // "(15)"; // bairro
                    String cepsac = FuncoesGlobais.myLetra(new Pad(_cep.substring(0, 5),5).RPad().toUpperCase()); // "(5)";  // cep
                    String cepsus = FuncoesGlobais.myLetra(new Pad(_cep.substring(6, 9),3).RPad().toUpperCase()); // "(3)";  // sufixo cep
                    String cidsac = FuncoesGlobais.myLetra(new Pad(_cidade,15).RPad().toUpperCase()); //"(15)"; // cidade
                    String ufsaca = FuncoesGlobais.myLetra(new Pad(_estado,2).RPad().toUpperCase()); //"RJ";   // UF
                    String demais = "0000000000000000                                        000000000000                   ";

                    output = cdbcoc + nrltre + tiporg + nrSeqq + cdregt + bracos +
                             cdmvrm + tpinss + inscsc + nmesac + endsac + baisac +
                             cepsac + cepsus + cidsac + ufsaca + demais;
                    filler.Print(output + LF);

                    ctalinhas += 1;

                    // R
                    String cbcodc = _banco;
                    String nrlotr = "0001";
                    String tporeg = "3";

                    String nrSeqr = StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                    String cdgseg = "R";
                    String spacob = FuncoesGlobais.Space(1);
                    String cdomot = movimento;  // ou 02 - baixa
                    String cdgdes = "0"; // codigo desconto
                    String dtdes2 = "00000000"; // data desconto 2
                    String vrpccd = "000000000000000"; // valor perc desco
                    String brac24 = FuncoesGlobais.Space(24);
                    String cdmult = "2"; // codigo da multa (1 - fixo / 2 - perc)
                    String dtamul = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data multa

                    Calculos rc = new Calculos();
                    try {
                        rc.Inicializa(_rgprp, _rgimv, _contrato);
                    } catch (SQLException ex) {}
                    String nvrpercmult = null;
                    try {nvrpercmult = StrZero(String.valueOf(rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", ""),13) + "00";} catch (Exception e) {}
                    String vrpcap = nvrpercmult; // vr/per multa
                    
                    //String vrpcap = "000000000001000"; // vr/per multa
                    String bran10 = FuncoesGlobais.Space(10);
                    String msge03 = FuncoesGlobais.Space(40); // msg 3
                    String msge04 = FuncoesGlobais.Space(40); // msg 4
                    String branfn = FuncoesGlobais.Space(61);

                    output = cbcodc + nrlotr + tporeg + nrSeqr + cdgseg + spacob +
                             cdomot + cdgdes + dtdes2 + vrpccd + brac24 + cdmult +
                             dtamul + vrpcap + bran10 + msge03 + msge04 + branfn;
                    filler.Print(output + LF);

                    ctalinhas += 1;

                    String msgBol01 = null; try { msgBol01 = VariaveisGlobais.conexao.LerParametros("MSGBOL1"); } catch (SQLException e) {} if (msgBol01 == null) msgBol01 = "";
                    String msgBol02 = null; try { msgBol02 = VariaveisGlobais.conexao.LerParametros("MSGBOL2"); } catch (SQLException e) {} if (msgBol02 == null) msgBol02 = "";
                    String msgBol03 = null; try { msgBol03 = VariaveisGlobais.conexao.LerParametros("MSGBOL3"); } catch (SQLException e) {} if (msgBol03 == null) msgBol03 = "";
                    String msgBol04 = null; try { msgBol04 = VariaveisGlobais.conexao.LerParametros("MSGBOL4"); } catch (SQLException e) {} if (msgBol04 == null) msgBol04 = "";
                    String msgBol05 = null; try { msgBol05 = VariaveisGlobais.conexao.LerParametros("MSGBOL5"); } catch (SQLException e) {} if (msgBol05 == null) msgBol05 = "";
                    String msgBol06 = null; try { msgBol06 = VariaveisGlobais.conexao.LerParametros("MSGBOL6"); } catch (SQLException e) {} if (msgBol06 == null) msgBol06 = "";
                    String msgBol07 = null; try { msgBol07 = VariaveisGlobais.conexao.LerParametros("MSGBOL7"); } catch (SQLException e) {} if (msgBol07 == null) msgBol07 = "";
                    String msgBol08 = null; try { msgBol08 = VariaveisGlobais.conexao.LerParametros("MSGBOL8"); } catch (SQLException e) {} if (msgBol08 == null) msgBol08 = "";
                    String msgBol09 = null; try { msgBol09 = VariaveisGlobais.conexao.LerParametros("MSGBOL9"); } catch (SQLException e) {} if (msgBol09 == null) msgBol09 = "";

                    //Calculos rc = new Calculos(); 
                    String _tipoimovel = null;
                    try {
                        //rc.Inicializa(_rgprp, _rgimv, _contrato);
                        if (!_rgprp.equalsIgnoreCase(_contrato)) {
                            _tipoimovel = rc.TipoImovel();
                        } 
                    } catch (SQLException ex) {}
                    Date tvecto = Dates.StringtoDate(_vencto,"dd/MM/yyyy");
                    String carVecto;
                    
                    if (_tipoimovel != null) {
                        carVecto = Dates.DateFormata("dd/MM/yyyy", 
                                    Dates.DateAdd(Dates.DIA, (int)rc.dia_mul, tvecto));
                    } else {
                        carVecto = Dates.DateFormata("dd/MM/yyyy", 
                                    Dates.DateAdd(Dates.DIA, 5, tvecto));
                    }
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
                        if (_tipoimovel != null) {
                            ln08 = ln08.replace("[MULTA]", String.valueOf(_tipoimovel.equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", "") + "%");
                        } else {
                            ln08 = ln08.replace("[MULTA]", "10%");
                        }
                        ln08 = ln08.replace("[ENCARGOS]", "0,333%");
                    }
                    msgBol08 = ln08;
                    msgBol09 = ("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);

                    Collections gVar = VariaveisGlobais.dCliente;
                    String[][] linhas = null; // = bancos.Recalcula(_rgprp, _rgimv, _contrato, _vencto);
                    float[] totais;
                    if (tipo == null && !(_rgprp.equals("") && _rgimv.equals("") && _contrato.equals(""))) {
                        linhas= bancos.Recalcula(_rgprp, _rgimv, _contrato, _vencto);
                        totais = bancos.CalcularRecibo(_rgprp, _rgimv, _contrato, _vencto);

                        // exp, mul, jur, cor
                        float expediente = 0, multa = 0, juros = 0, correcao = 0;

                        if (VariaveisGlobais.boletoEP || VariaveisGlobais.boletoSomaEP) expediente = totais[0];
                        if (VariaveisGlobais.boletoMU) { multa = totais[1]; } else { totais[4] -= totais[1]; }
                        if (VariaveisGlobais.boletoJU) { juros = totais[2]; } else { totais[4] -= totais[2]; }
                        if (VariaveisGlobais.boletoCO) { correcao = totais[3]; } else { totais[4] -= totais[3]; }
                        float tRecibo = totais[4];

                        DecimalFormat df = new DecimalFormat("#,##0.00");
                        df.format(multa);

                        if ((VariaveisGlobais.boletoEP && expediente > 0) && !VariaveisGlobais.boletoSomaEP) {
                            int pos = bancos.AchaVazio(linhas);
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
                            int pos = bancos.AchaVazio(linhas);
                            if (pos > -1) {
                                linhas[pos][0] = gVar.get("MU");
                                linhas[pos][1] = "-";
                                linhas[pos][2] = df.format(multa);
                            }
                        }

                        if (juros > 0) {
                            int pos = bancos.AchaVazio(linhas);
                            if (pos > -1) {
                                linhas[pos][0] = gVar.get("JU");
                                linhas[pos][1] = "-";
                                linhas[pos][2] = df.format(juros);
                            }
                        }

                        if (correcao > 0) {
                            int pos = bancos.AchaVazio(linhas);
                            if (pos > -1) {
                                linhas[pos][0] = gVar.get("CO");
                                linhas[pos][1] = "-";
                                linhas[pos][2] = df.format(correcao);
                            }
                        }
                    } else {
                        linhas = new String[][] {
                            {null},
                            {null},
                            {null},
                            {null},
                            {null},
                            {null},
                            {null},
                            {null},
                            {null},
                            {null}
                        };
                    }
                    
                    String[] msg = {                
                        (!(linhas[0][0] == null) ? new Pad(linhas[0][0],20).RPad() + " " + new Pad(linhas[0][1],10).RPad() + " R$" + new Pad(linhas[0][2],15).LPad() : null),                
                        (!(linhas[1][0] == null) ? new Pad(linhas[1][0],20).RPad() + " " + new Pad(linhas[1][1],10).RPad() + " R$" + new Pad(linhas[1][2],15).LPad() : null),                
                        (!(linhas[2][0] == null) ? new Pad(linhas[2][0],20).RPad() + " " + new Pad(linhas[2][1],10).RPad() + " R$" + new Pad(linhas[2][2],15).LPad() : null),                
                        (!(linhas[3][0] == null) ? new Pad(linhas[3][0],20).RPad() + " " + new Pad(linhas[3][1],10).RPad() + " R$" + new Pad(linhas[3][2],15).LPad() : null),                
                        (!(linhas[4][0] == null) ? new Pad(linhas[4][0],20).RPad() + " " + new Pad(linhas[4][1],10).RPad() + " R$" + new Pad(linhas[4][2],15).LPad() : null),                
                        (!(linhas[5][0] == null) ? new Pad(linhas[5][0],20).RPad() + " " + new Pad(linhas[5][1],10).RPad() + " R$" + new Pad(linhas[5][2],15).LPad() : null),                
                        (!(linhas[6][0] == null) ? new Pad(linhas[6][0],20).RPad() + " " + new Pad(linhas[5][1],10).RPad() + " R$" + new Pad(linhas[6][2],15).LPad() : null),                
                        (!(linhas[7][0] == null) ? new Pad(linhas[7][0],20).RPad() + " " + new Pad(linhas[7][1],10).RPad() + " R$" + new Pad(linhas[7][2],15).LPad() : null),                
                        (!(linhas[8][0] == null) ? new Pad(linhas[8][0],20).RPad() + " " + new Pad(linhas[8][1],10).RPad() + " R$" + new Pad(linhas[8][2],15).LPad() : null),                
                        (!(linhas[9][0] == null) ? new Pad(linhas[9][0],20).RPad() + " " + new Pad(linhas[9][1],10).RPad() + " R$" + new Pad(linhas[9][2],15).LPad() : null),                
                        (null),                
                        (null),                
                        (!msgBol01.equalsIgnoreCase("") ? msgBol01 : null),
                        (!msgBol02.equalsIgnoreCase("") ? msgBol02 : null),
                        (!msgBol03.equalsIgnoreCase("") ? msgBol03 : null),
                        (!msgBol04.equalsIgnoreCase("") ? msgBol04 : null),
                        (!msgBol05.equalsIgnoreCase("") ? msgBol05 : null),
                        (!msgBol06.equalsIgnoreCase("") ? msgBol06 : null),
                        (!msgBol07.equalsIgnoreCase("") ? msgBol07 : null),
                        (!msgBol08.equalsIgnoreCase("") ? msgBol08 : null),
                        (!msgBol09.equalsIgnoreCase("") ? msgBol09 : null),
                        (null)
                    };

                    // S
                    int nrlin = 1;
                    for (int z=0;z<msg.length;z++) {
                        if (msg[z] != null) {
                            String codbcc = _banco; 
                            String nrorem = "0001";
                            String tppreg = "3";

                            String nrSeqs = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                            String cdoseg = "S";
                            String branrs = FuncoesGlobais.Space(1);
                            String cdgmvt = movimento; // ou 02 - baixa

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
                } // Separa baixa
            }
        }

        if (filler.Open()) {
            // trailer lote
            String cdgcom = _banco; 
            String nrores = "0001";
            String tporgt = "5";
            String brantl = FuncoesGlobais.Space(9);
            String qtdrlt = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 1), 6); //"000000"; // quantidade reg no lote
            String brcolt = FuncoesGlobais.Space(217);

            String output = cdgcom + nrores + tporgt + brantl + qtdrlt + brcolt;
            filler.Print(output + LF);

            ctalinhas += 1;
            
            // trailer arquivo remessa
            String cgdcop = _banco; 
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
        
        if (tipo == "A" || tipo == null) JOptionPane.showMessageDialog(null, "Arquivo de remessa " + bancos.getBanco() + fileName + nroLote + ".rem" + " gerado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        return bancos.getBanco() + fileName + nroLote + ".rem";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package Bancos;
//
//import Funcoes.Dates;
//import Funcoes.FuncoesGlobais;
//import static Funcoes.FuncoesGlobais.StrZero;
//import Funcoes.LerValor;
//import Funcoes.Pad;
//import Funcoes.StreamFile;
//import Funcoes.StringManager;
//import Funcoes.VariaveisGlobais;
//import Protocolo.Calculos;
//import Sici.Partida.Collections;
//import java.io.File;
//import java.sql.SQLException;
//import java.text.DecimalFormat;
//import java.util.Date;
//import javax.swing.JOptionPane;

/**
 *
 * @author supervisor
 */
//public class Santander {
//    static private String FatorVencimento(String fator, String vencimento) {
//        String retorno = "0000";
//        if (vencimento.length() < 8) {retorno = "0000";} else {
//            retorno = String.valueOf(Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "MM/dd/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
//        }
//        
//        return retorno;
//    }
//    
//    static public String CalcDig11N(String cadeia) {
//        int total= 0; int mult = 2;
//        for (int i=1; i<=cadeia.length();i++) {
//            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
//            mult++;
//            if (mult > 9) mult = 2;
//        }
//        int soma = total; // * 10;
//        int resto = (soma % 11);
//        if (resto == 0 || resto == 1 || resto == 10) { 
//            resto = 1; 
//        } else if (resto > 10) {
//            resto = 1; 
//        } else {
//            resto = 11 - resto;
//        }
//        return String.valueOf(resto);
//    }
//     
//    static private String CalcDig11NBarrasSantander(String cadeia) {
//        int total= 0; int mult = 2;
//        for (int i=1; i<=cadeia.length();i++) {
//            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
//            mult++;
//            if (mult > 9) mult = 2;
//        }
//        int soma = total * 10;
//        int resto = (soma % 11);
//        if (resto == 0 || resto == 1 || resto == 10) { 
//            resto = 1; 
//        } else {
//            resto = resto;
//        }
//        return String.valueOf(resto);
//    }
//    
//    static public String CodBar(String vencimento,String valor,String nossonumero) {
//        String part1; String part2; String dv;
//        part1 = bancos.getBanco() + bancos.getMoeda();
//        part2 = FatorVencimento("10/07/1997", vencimento) + bancos.Valor4Boleta(valor) + "9" + bancos.getCtaDv() + nossonumero + "0" + bancos.getCarteira();
//        dv = CalcDig11N(part1 + part2);
//        //dv = CalcDig11NBarrasSantander(part1 + part2);
//        return part1 + dv + part2;
//    }
//
//    static public String LinhaDigitavel(String nossonumero, String codbardv, String vencimento, String valortitulo) {
//        String campo1, campo2, campo3, campo4, campo5;
//        
//        campo1 = bancos.getBanco() + bancos.getMoeda() + "9" + StringManager.Left(bancos.getCtaDv(), 4);
//        campo1 += bancos.CalcDig10(campo1);
//        campo1 = campo1.substring(0, 5) + "." + campo1.substring(5,10);
//        
//        campo2 = StringManager.Right(bancos.getCtaDv(),3) + StringManager.Left(nossonumero, 7);
//        campo2 += bancos.CalcDig10(campo2);
//        campo2 = campo2.substring(0,5) + "." + campo2.substring(5, 11);
//        
//        campo3 = StringManager.Right(nossonumero, 6) + "0" + bancos.getCarteira();
//        campo3 += bancos.CalcDig10(campo3);
//        campo3 = campo3.substring(0, 5) + "." + campo3.substring(5, 11);
//        
//        
//        String dvcb = bancos.getBanco() + bancos.getMoeda() + FatorVencimento("10/07/1997", vencimento) +
//                       bancos.Valor4Boleta(valortitulo) + "9" + StringManager.Left(bancos.getCtaDv(), 7) +
//                       nossonumero + "0" + bancos.getCarteira();
//        
//        campo4 = CalcDig11NBarrasSantander(dvcb);
//        
//        campo5 = FatorVencimento("10/07/1997", vencimento) + bancos.Valor4Boleta(valortitulo);
//        
//        return campo1 + "  " + campo2 + "  " + campo3 + "  " + campo4 + "  " + campo5;
//    }    
//    
//    static public String NossoNumero(String value, int tam) {
//        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
//                        Integer.valueOf(value).toString().trim(),tam - 1);
//        String valor2 = CalcDig11N(valor1);
//        return valor1 + valor2;
//    }
//    
//    static public void Remessa(String fileName, String movimento, String[][] lista) {
//        if (lista.length == 0) return;
//        
//        bancos.LerBancoAvulso("033");
//        
//        File diretorio = new File("remessa"); if (!diretorio.exists()) { diretorio.mkdirs(); }
//        
//        String nroLote = null;
//        try {nroLote =  FuncoesGlobais.StrZero(VariaveisGlobais.conexao.LerParametros("LOTE_" + bancos.getBanco()),5);} catch (SQLException e) { nroLote = "00000"; }
//        
//        File arquivo = new File("remessa/" + bancos.getBanco() + "_" + fileName + "_" + nroLote + ".rem");
//        if (arquivo.exists()) {
//            JOptionPane.showMessageDialog(null, "Arquivo de remessa ja existe!!!\n\nTente novamente com outro nome.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
//        
//        String LF = "\r\n";
//        
//        String _banco = bancos.getBanco();
//        String nrlote = nroLote;
//        String[] variaveis = {"LOTE_" + _banco, nrlote,"NUMERICO"};
//        try { VariaveisGlobais.conexao.GravarParametros(variaveis); } catch (Exception e) {}
//        
//        String nmEmp = new Pad(VariaveisGlobais.dCliente.get("empresa"),30).RPad();
//        String icEmp = FuncoesGlobais.StrZero(bancos.rmvNumero(VariaveisGlobais.dCliente.get("cnpj")),15);
//        int ctalinhas = 1;
//        StreamFile filler = new StreamFile(new String[] {"remessa/" + bancos.getBanco() + "_" + fileName + "_" + nroLote + ".rem"});
//        if (filler.Open()) {
//            String codBaco = _banco;
//            String loteSer = "0000";
//            String tipoSer = "0";
//            String reserVd = FuncoesGlobais.Space(8);
//            String tipoInc = "2";
//            String inscEmp = icEmp;  // cnpj
//            String codTran = bancos.getAgencia() + FuncoesGlobais.StrZero(bancos.getCtaDv(), 11);   //"340500007926383";  // dados da conta 
//            String reseVad = FuncoesGlobais.Space(25); // 25 digitos
//            String nomeEmp = nmEmp; // 30 digitos
//            String nomeBan = "BANCO SANTANDER" + FuncoesGlobais.Space(15); // 30 digitos
//            String resVado = FuncoesGlobais.Space(10); // 10 digitos
//            String codRems = "1";
//            String dtGerac = Dates.DateFormata("ddMMyyyy", new Date()); // data atual
//            String rservDo = FuncoesGlobais.Space(6); // 6 barncos
//            String numSequ = FuncoesGlobais.StrZero(nroLote, 6); // 6 digitos de 1 a 999999 <
//            String Versaos = "040";
//            String reSerVa = FuncoesGlobais.Space(74); // 74 digitos           
//                    
//            String output = codBaco + loteSer + tipoSer + reserVd + tipoInc +
//                            inscEmp + codTran + reseVad + nomeEmp + nomeBan +
//                            resVado + codRems + dtGerac + rservDo + numSequ +
//                            Versaos + reSerVa;
//            filler.Print(output + LF);
//
//            ctalinhas += 1;
//            
//            String codBco = _banco; 
//            String loteRe = "0001"; 
//            String tpRems = "1";
//            String tpOper = "R";
//            String tpServ = "01";
//            String reseVd = FuncoesGlobais.Space(2);
//            String nversa = "030";
//            String resedo = FuncoesGlobais.Space(1);
//            String tpInsc = "2";
//            String cnpjEp = icEmp;
//            String resVdo = FuncoesGlobais.Space(20);
//            String codTrE = bancos.getAgencia() + FuncoesGlobais.StrZero(bancos.getCtaDv(), 11); //"340500007926383";
//            String rseVdo = FuncoesGlobais.Space(5);
//            String nomeCd = nmEmp; // 30dig
//            String mensN1 = FuncoesGlobais.Space(40); // 40dig
//            String mensN2 = FuncoesGlobais.Space(40); // 40dig
//            String numRRt = "00000000"; // 8dig
//            String dtgrav = Dates.DateFormata("ddMMyyyy", new Date());;
//            String reVado = FuncoesGlobais.Space(41);
//
//            output = codBco + loteRe + tpRems + tpOper + tpServ + reseVd +
//                     nversa + resedo + tpInsc + cnpjEp + resVdo + codTrE +
//                     rseVdo + nomeCd + mensN1 + mensN2 + numRRt + dtgrav +
//                     reVado;
//            filler.Print(output + LF);
//        }
//
//        ctalinhas += 1;
//        
//        for (int i=0; i < lista.length; i++) {
//            if (filler.Open()) {
//                String[][] vCampos = null;
//                try {
//                    vCampos = VariaveisGlobais.conexao.LerCamposTabela(new String[] {"l.contrato", "l.rgprp", "l.rgimv", "l.aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "r.nnumero", "l.cpfcnpj", "l.nomerazao"}, "locatarios l, imoveis i, proprietarios p, RECIBO r", "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp AND r.contrato = l.contrato) AND l.contrato = '" + lista[0][0] + "'");
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//                String _rgprp = vCampos[1][3];
//                String _rgimv = vCampos[2][3];
//                String _contrato = vCampos[0][3];
//                
//                String _nome = vCampos[14][3].trim();
//                String _cpfcnpj = vCampos[13][3].trim();
//                
//                String _ender = vCampos[4][3].trim() + ", " + vCampos[5][3].trim() + " " + vCampos[6][3].trim();
//                String _bairro = vCampos[7][3].trim();
//                String _cidade = vCampos[8][3].trim();
//                String _estado = vCampos[9][3].trim();
//                String _cep = vCampos[10][3].trim();
//                
//                String _vencto = lista[i][1];
//                String _valor = lista[i][2];
//                String _rnnumero = lista[i][3];
//                
//                // P
//                String codBcC = _banco;  
//                String nrReme = "0001";
//                String tpRegi = "3";
//                String nrSequ = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
//                String cdSegR = "P";
//                String rsvDos = FuncoesGlobais.Space(1);
//                String cdMvRm = movimento; // 01 - Entrada de título
//                String agCedn = bancos.getAgencia(); //"3405";  // agencia do cedente
//                String digAgc = CalcDig11N(bancos.getAgencia());  //"3"; // digito verificador
//                String numCoC = bancos.getIdentificacao(); //"013000516"; // Conta Corrente 013000516
//                String digCoC = bancos.getIdentDv(); //"0"; // digito verificador
//                String contCb = bancos.getIdentificacao(); //"013000516"; // Conta cobranca 7926383
//                String digtCb = bancos.getIdentDv(); //"0"; // digito
//                String rservo = FuncoesGlobais.Space(2);
//                String nnumer = FuncoesGlobais.StrZero(_rnnumero,13); // nosso numero com 13 dig
//                String tpoCob = "5"; // tipo de cobrança
//                String formCd = "1"; // forma de cadastramento
//                String tipoDc = "2"; // tipo de documento
//                String rsvad1 = FuncoesGlobais.Space(1);
//                String rsvad2 = FuncoesGlobais.Space(1);
//                String numDoc = new Pad(_contrato,15).RPad();
//                String dtavtt = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); // "ddmmaaaa"; // data de vencimento do titulo
//                String vrnmtt = bancos.fmtNumero(_valor); //"000000000123129"; // valor nominal do titulo
//                String agencb = "0000"; // agencia encarregada
//                String digaec = "0"; // digito
//                String rsvado = FuncoesGlobais.Space(1);
//                String esptit = "04"; /// 04 - dup de serviço, 17 - recibo/ especie de titulo // 17 - Recibo
//                String idtitu = "N";
//                String dtemti = Dates.DateFormata("ddMMyyyy", new Date()); //"ddmmaaaa"; // data emissao do titulo
//                String cdjuti = "1"; // codigo juros do titulo
//                String dtjrmo = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data juros mora
//                float valor = LerValor.StringToFloat(_valor) * 0.00033f;
//                String vrmtxm = bancos.fmtNumero(LerValor.FloatToString(valor)); //"000000000000041"; // valor ou taxa de mora (aluguel * 0,0333)
//                String cddesc = "0"; // codigo desconto
//                String dtdesc = "00000000"; // data desconto
//                String vrpecd = "000000000000000"; // valor ou percentual de desconto
//                String vriofr = "000000000000000"; // iof a ser recolhido
//                String vrabti = "000000000000000"; // valor abatimento
//                String idttep = FuncoesGlobais.Space(25);
//                String cdprot = "0"; // codigo para protesto
//                String nrdpro = "00"; // numero de dias para protesto
//                String cdbxdv = "1"; // codigo baixa devolucao (2)
//                String revdao = "0";
//                String nrdibd = "15"; // numero de dias baixa devolucao
//                String cdmoed = "00"; // codigo moeda
//                String revado = FuncoesGlobais.Space(11);
//
//                String output = codBcC + nrReme + tpRegi + nrSequ + cdSegR + rsvDos +
//                                cdMvRm + agCedn + digAgc + numCoC + digCoC + contCb +
//                                digtCb + rservo + nnumer + tpoCob + formCd + tipoDc +
//                                rsvad1 + rsvad2 + numDoc + dtavtt + vrnmtt + agencb +
//                                digaec + rsvado + esptit + idtitu + dtemti + cdjuti +
//                                dtjrmo + vrmtxm + cddesc + dtdesc + vrpecd + vriofr +
//                                vrabti + idttep + cdprot + nrdpro + cdbxdv + revdao +
//                                nrdibd + cdmoed + revado;
//                filler.Print(output + LF);
//        
//                ctalinhas += 1;
//
//                if (movimento.equalsIgnoreCase("01")) {
//                    /*
//                    Marca remessa = 'S' 
//                    */
//                    String wSql = "UPDATE bloquetos SET remessa = 'S' WHERE nnumero LIKE '%" + _rnnumero  + "%';";
//                    try {
//                        VariaveisGlobais.conexao.ExecutarComando(wSql);
//                    } catch (Exception e) {}
//
//
//                    // Sqgmento Q
//                    String cdbcoc = _banco; 
//                    String nrltre = "0001";
//                    String tiporg = "3";
//
//                    String nrSeqq = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
//                    String cdregt = "Q";
//                    String bracos = FuncoesGlobais.Space(1);
//                    String cdmvrm = movimento; // ou 02 - pedido de baixa
//                    String cpfCNPJ = bancos.rmvLetras(bancos.rmvNumero(_cpfcnpj));
//                    String tpinss = (cpfCNPJ.length() == 11 ? "1" : "2"); // tipo inscricao sacado
//                    String inscsc = FuncoesGlobais.StrZero(cpfCNPJ,15); //"000000000000000"; // CPF/CNPJ
//                    String nmesac = FuncoesGlobais.myLetra(new Pad(_nome.toUpperCase(),40).RPad()); //"(40)"; // nome do sacado
//                    String endsac = FuncoesGlobais.myLetra(new Pad(_ender,40).RPad().toUpperCase()); //"(40)"; // endereco 
//                    String baisac = FuncoesGlobais.myLetra(new Pad(_bairro,15).RPad().toUpperCase()); // "(15)"; // bairro
//                    String cepsac = FuncoesGlobais.myLetra(new Pad(_cep.substring(0, 5),5).RPad().toUpperCase()); // "(5)";  // cep
//                    String cepsus = FuncoesGlobais.myLetra(new Pad(_cep.substring(6, 9),3).RPad().toUpperCase()); // "(3)";  // sufixo cep
//                    String cidsac = FuncoesGlobais.myLetra(new Pad(_cidade,15).RPad().toUpperCase()); //"(15)"; // cidade
//                    String ufsaca = FuncoesGlobais.myLetra(new Pad(_estado,2).RPad().toUpperCase()); //"RJ";   // UF
//                    String demais = "0000000000000000                                        000000000000                   ";
//
//                    output = cdbcoc + nrltre + tiporg + nrSeqq + cdregt + bracos +
//                             cdmvrm + tpinss + inscsc + nmesac + endsac + baisac +
//                             cepsac + cepsus + cidsac + ufsaca + demais;
//                    filler.Print(output + LF);
//
//                    ctalinhas += 1;
//
//                    // R
//                    String cbcodc = _banco;
//                    String nrlotr = "0001";
//                    String tporeg = "3";
//
//                    String nrSeqr = StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
//                    String cdgseg = "R";
//                    String spacob = FuncoesGlobais.Space(1);
//                    String cdomot = movimento;  // ou 02 - baixa
//                    String cdgdes = "0"; // codigo desconto
//                    String dtdes2 = "00000000"; // data desconto 2
//                    String vrpccd = "000000000000000"; // valor perc desco
//                    String brac24 = FuncoesGlobais.Space(24);
//                    String cdmult = "2"; // codigo da multa (1 - fixo / 2 - perc)
//                    String dtamul = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data multa
//
//                    /*
//                    Calculos rc = new Calculos();
//                    try {
//                        rc.Inicializa(_rgprp, _rgimv, _contrato);
//                    } catch (SQLException ex) {}
//                    String nvrpercmult = null;
//                    try {nvrpercmult = StrZero(String.valueOf(rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", ""),13) + "00";} catch (Exception e) {}
//                    String vrpcap = nvrpercmult; // vr/per multa
//                    */
//                    String vrpcap = "000000000001000"; // vr/per multa
//                    String bran10 = FuncoesGlobais.Space(10);
//                    String msge03 = FuncoesGlobais.Space(40); // msg 3
//                    String msge04 = FuncoesGlobais.Space(40); // msg 4
//                    String branfn = FuncoesGlobais.Space(61);
//
//                    output = cbcodc + nrlotr + tporeg + nrSeqr + cdgseg + spacob +
//                             cdomot + cdgdes + dtdes2 + vrpccd + brac24 + cdmult +
//                             dtamul + vrpcap + bran10 + msge03 + msge04 + branfn;
//                    filler.Print(output + LF);
//
//                    ctalinhas += 1;
//
//                    String msgBol01 = null; try { msgBol01 = VariaveisGlobais.conexao.LerParametros("MSGBOL1"); } catch (SQLException e) {} if (msgBol01 == null) msgBol01 = "";
//                    String msgBol02 = null; try { msgBol02 = VariaveisGlobais.conexao.LerParametros("MSGBOL2"); } catch (SQLException e) {} if (msgBol02 == null) msgBol02 = "";
//                    String msgBol03 = null; try { msgBol03 = VariaveisGlobais.conexao.LerParametros("MSGBOL3"); } catch (SQLException e) {} if (msgBol03 == null) msgBol03 = "";
//                    String msgBol04 = null; try { msgBol04 = VariaveisGlobais.conexao.LerParametros("MSGBOL4"); } catch (SQLException e) {} if (msgBol04 == null) msgBol04 = "";
//                    String msgBol05 = null; try { msgBol05 = VariaveisGlobais.conexao.LerParametros("MSGBOL5"); } catch (SQLException e) {} if (msgBol05 == null) msgBol05 = "";
//                    String msgBol06 = null; try { msgBol06 = VariaveisGlobais.conexao.LerParametros("MSGBOL6"); } catch (SQLException e) {} if (msgBol06 == null) msgBol06 = "";
//                    String msgBol07 = null; try { msgBol07 = VariaveisGlobais.conexao.LerParametros("MSGBOL7"); } catch (SQLException e) {} if (msgBol07 == null) msgBol07 = "";
//                    String msgBol08 = null; try { msgBol08 = VariaveisGlobais.conexao.LerParametros("MSGBOL8"); } catch (SQLException e) {} if (msgBol08 == null) msgBol08 = "";
//                    String msgBol09 = null; try { msgBol09 = VariaveisGlobais.conexao.LerParametros("MSGBOL9"); } catch (SQLException e) {} if (msgBol09 == null) msgBol09 = "";
//
//                    Calculos rc = new Calculos(); String _tipoimovel = null;
//                    try {
//                        rc.Inicializa(_rgprp, _rgimv, _contrato);
//                        _tipoimovel = rc.TipoImovel();
//                    } catch (SQLException ex) {}
//                    Date tvecto = Dates.StringtoDate(_vencto,"dd/MM/yyyy");
//                    String carVecto = Dates.DateFormata("dd/MM/yyyy", 
//                                    Dates.DateAdd(Dates.DIA, (int)rc.dia_mul, tvecto));
//
//                    String ln08 = "";
//                    if ("".equals(msgBol08)) {
//                        ln08 = "APÓS O DIA " + carVecto + " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO.";
//                    } else {
//                        // [VENCIMENTO] - Mostra Vencimento
//                        // [CARENCIA] - Mostra Vencimento + Carencia
//                        // [MULTA] - Mostra Juros
//                        // [ENCARGOS] - Mostra Encargos
//                        ln08 = msgBol08.replace("[VENCIMENTO]", Dates.DateFormata("dd/MM/yyyy", tvecto));
//                        ln08 = ln08.replace("[CARENCIA]", carVecto);
//                        ln08 = ln08.replace("[MULTA]", String.valueOf(_tipoimovel.equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", "") + "%");
//                        ln08 = ln08.replace("[ENCARGOS]", "0,333%");
//                    }
//                    msgBol08 = ln08;
//                    msgBol09 = ("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);
//
//                    Collections gVar = VariaveisGlobais.dCliente;
//                    String[][] linhas = bancos.Recalcula(_rgprp, _rgimv, _contrato, _vencto);
//                    float[] totais = bancos.CalcularRecibo(_rgprp, _rgimv, _contrato, _vencto);
//                    
//                    // exp, mul, jur, cor
//                    float expediente = 0, multa = 0, juros = 0, correcao = 0;
//
//                    if (VariaveisGlobais.boletoEP || VariaveisGlobais.boletoSomaEP) expediente = totais[0];
//                    if (VariaveisGlobais.boletoMU) { multa = totais[1]; } else { totais[4] -= totais[1]; }
//                    if (VariaveisGlobais.boletoJU) { juros = totais[2]; } else { totais[4] -= totais[2]; }
//                    if (VariaveisGlobais.boletoCO) { correcao = totais[3]; } else { totais[4] -= totais[3]; }
//                    float tRecibo = totais[4];
//
//                    DecimalFormat df = new DecimalFormat("#,##0.00");
//                    df.format(multa);
//
//                    if ((VariaveisGlobais.boletoEP && expediente > 0) && !VariaveisGlobais.boletoSomaEP) {
//                        int pos = bancos.AchaVazio(linhas);
//                        if (pos > -1) {
//                            linhas[pos][0] = gVar.get("EP");
//                            linhas[pos][1] = "-";
//                            linhas[pos][2] = df.format(expediente);
//                        }
//                    } else if (VariaveisGlobais.boletoEP && VariaveisGlobais.boletoSomaEP) {
//                        float alrec = LerValor.StringToFloat(linhas[0][2]);
//                        linhas[0][2] = LerValor.floatToCurrency(alrec + expediente, 2);
//                        expediente = 0;
//                    } else if (!VariaveisGlobais.boletoEP && !VariaveisGlobais.boletoSomaEP) {
//                        tRecibo -= totais[0];
//                        expediente = 0;
//                    }
//
//                    if (multa > 0) {
//                        int pos = bancos.AchaVazio(linhas);
//                        if (pos > -1) {
//                            linhas[pos][0] = gVar.get("MU");
//                            linhas[pos][1] = "-";
//                            linhas[pos][2] = df.format(multa);
//                        }
//                    }
//
//                    if (juros > 0) {
//                        int pos = bancos.AchaVazio(linhas);
//                        if (pos > -1) {
//                            linhas[pos][0] = gVar.get("JU");
//                            linhas[pos][1] = "-";
//                            linhas[pos][2] = df.format(juros);
//                        }
//                    }
//
//                    if (correcao > 0) {
//                        int pos = bancos.AchaVazio(linhas);
//                        if (pos > -1) {
//                            linhas[pos][0] = gVar.get("CO");
//                            linhas[pos][1] = "-";
//                            linhas[pos][2] = df.format(correcao);
//                        }
//                    }
//                    
//                    String[] msg = {                
//                        (!(linhas[0][0] == null) ? new Pad(linhas[0][0],20).RPad() + " " + new Pad(linhas[0][1],10).RPad() + " R$" + new Pad(linhas[0][2],15).LPad() : null),                
//                        (!(linhas[1][0] == null) ? new Pad(linhas[1][0],20).RPad() + " " + new Pad(linhas[1][1],10).RPad() + " R$" + new Pad(linhas[1][2],15).LPad() : null),                
//                        (!(linhas[2][0] == null) ? new Pad(linhas[2][0],20).RPad() + " " + new Pad(linhas[2][1],10).RPad() + " R$" + new Pad(linhas[2][2],15).LPad() : null),                
//                        (!(linhas[3][0] == null) ? new Pad(linhas[3][0],20).RPad() + " " + new Pad(linhas[3][1],10).RPad() + " R$" + new Pad(linhas[3][2],15).LPad() : null),                
//                        (!(linhas[4][0] == null) ? new Pad(linhas[4][0],20).RPad() + " " + new Pad(linhas[4][1],10).RPad() + " R$" + new Pad(linhas[4][2],15).LPad() : null),                
//                        (!(linhas[5][0] == null) ? new Pad(linhas[5][0],20).RPad() + " " + new Pad(linhas[5][1],10).RPad() + " R$" + new Pad(linhas[5][2],15).LPad() : null),                
//                        (!(linhas[6][0] == null) ? new Pad(linhas[6][0],20).RPad() + " " + new Pad(linhas[5][1],10).RPad() + " R$" + new Pad(linhas[6][2],15).LPad() : null),                
//                        (!(linhas[7][0] == null) ? new Pad(linhas[7][0],20).RPad() + " " + new Pad(linhas[7][1],10).RPad() + " R$" + new Pad(linhas[7][2],15).LPad() : null),                
//                        (!(linhas[8][0] == null) ? new Pad(linhas[8][0],20).RPad() + " " + new Pad(linhas[8][1],10).RPad() + " R$" + new Pad(linhas[8][2],15).LPad() : null),                
//                        (!(linhas[9][0] == null) ? new Pad(linhas[9][0],20).RPad() + " " + new Pad(linhas[9][1],10).RPad() + " R$" + new Pad(linhas[9][2],15).LPad() : null),                
//                        (null),                
//                        (null),                
//                        (!msgBol01.equalsIgnoreCase("") ? msgBol01 : null),
//                        (!msgBol02.equalsIgnoreCase("") ? msgBol02 : null),
//                        (!msgBol03.equalsIgnoreCase("") ? msgBol03 : null),
//                        (!msgBol04.equalsIgnoreCase("") ? msgBol04 : null),
//                        (!msgBol05.equalsIgnoreCase("") ? msgBol05 : null),
//                        (!msgBol06.equalsIgnoreCase("") ? msgBol06 : null),
//                        (!msgBol07.equalsIgnoreCase("") ? msgBol07 : null),
//                        (!msgBol08.equalsIgnoreCase("") ? msgBol08 : null),
//                        (!msgBol09.equalsIgnoreCase("") ? msgBol09 : null),
//                        (null)
//                    };
//
//                    // S
//                    int nrlin = 1;
//                    for (int z=0;z<msg.length;z++) {
//                        if (msg[z] != null) {
//                            String codbcc = _banco; 
//                            String nrorem = "0001";
//                            String tppreg = "3";
//
//                            String nrSeqs = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
//                            String cdoseg = "S";
//                            String branrs = FuncoesGlobais.Space(1);
//                            String cdgmvt = movimento; // ou 02 - baixa
//
//                            String idimpr = "1";
//                            String nrlnip = FuncoesGlobais.StrZero(String.valueOf(nrlin++), 2); // nrlinha impressa 01 ate 22
//                            String msgimp = "4"; 
//                            String msgipr = FuncoesGlobais.myLetra(new Pad(msg[z],100).RPad().toUpperCase()); //"(100)"; // mensagem a imprimir
//                            String brancs = FuncoesGlobais.Space(119);
//
//                            output = codbcc + nrorem + tppreg + nrSeqs + 
//                                     cdoseg + branrs + cdgmvt + idimpr +
//                                     nrlnip + msgimp + msgipr + brancs;
//                            filler.Print(output + LF);
//
//                            ctalinhas += 1;
//                        }
//                    }
//                } // Separa baixa
//            }
//        }
//
//        if (filler.Open()) {
//            // trailer lote
//            String cdgcom = _banco; 
//            String nrores = "0001";
//            String tporgt = "5";
//            String brantl = FuncoesGlobais.Space(9);
//            String qtdrlt = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 1), 6); //"000000"; // quantidade reg no lote
//            String brcolt = FuncoesGlobais.Space(217);
//
//            String output = cdgcom + nrores + tporgt + brantl + qtdrlt + brcolt;
//            filler.Print(output + LF);
//
//            ctalinhas += 1;
//            
//            // trailer arquivo remessa
//            String cgdcop = _banco; 
//            String nrolte = "9999";
//            String tpregi = "9";
//            String brcoat = FuncoesGlobais.Space(9);
//            String qtdlaq = "000001"; // quantidade de lotes do arquivo
//            String qtdrga = FuncoesGlobais.StrZero(String.valueOf(ctalinhas), 6); //"000000"; // quantidade reg do arquivo tipo=0+1+2+3+5+9
//            String brcalt = FuncoesGlobais.Space(211);
//
//            output = cgdcop + nrolte + tpregi + brcoat + qtdlaq + qtdrga + brcalt;
//            filler.Print(output + LF);
//        }        
//        filler.Close();        
//        
//        JOptionPane.showMessageDialog(null, "Arquivo de remessa " + bancos.getBanco() + "_" + fileName + "_" + nroLote + ".rem" + " gerado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
//    }
//}
