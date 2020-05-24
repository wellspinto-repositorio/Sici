/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import Movimento.jRecebtos;
import Movimento.jRecebtos_fake;
import Sici.Locatarios.jLocatarios;
import Sici.Partida.Collections;
import Sici.jProprietarios;
import java.sql.ResultSet;
import javax.swing.JDesktopPane;

/**
 *
 * @author supervisor
 */
public class VariaveisGlobais {
    static public boolean dbsenha = false;
    // variaveis do supermenu
    static public JDesktopPane jPanePrin = null;
    static public jProprietarios oProp = null;
    static public jLocatarios oLoca = null;

    // Variaveis de conexão
    static public String unidadeGlobal = "";
    static public String unidade = "";
    static public String usuario = "";
    static public String funcao = "";
    static public String senha = "";
    static public String dbnome = "";

    static public String  remoto1  = "";
    static public String  dbnome1  = "";
    static public boolean dbsenha1 = false;
    
    static public String  remoto2  = "";
    static public String  dbnome2  = "";
    static public boolean dbsenha2 = false;

    static public String  remoto3  = "";
    static public String  dbnome3  = "";
    static public boolean dbsenha3 = false;

    static public String  remoto4  = "";
    static public String  dbnome4  = "";
    static public boolean dbsenha4 = false;

    static public String  remoto5  = "";
    static public String  dbnome5  = "";
    static public boolean dbsenha5 = false;

    static public Object[][] unidades = {};

    // Variaveis para tela de Imoveis
    static public String rgprp = "";
    static public String rgimv = "";

    // Variaveis da Baixa de Imoveis
    static public String situacao = "";
    static public String historico = "";

    // Variaveis para tela de Sócios
    static public String mContrato = "";
    static public int mQtdSoc = 0;
    static public int mPosSoc = 0;
    static public ResultSet pResult = null;

    // Variaveis fiadores
    static public String frgprp = "";
    static public String frgimv = "";
    static public String fcontrato = "";
    static public String fnome = "";
    static public boolean isBloqueado = false;
    
    // Variaveis para carteira
    static public String ccontrato = "";
    static public String crgprp = "";
    static public String crgimv = "";
    static public String ccampos = "";

    static public Collections dCliente = new Collections();
    static public Collections cContas = new Collections();

    // para a rotina pag/rec
    static public jRecebtos rTela = null;
    static public jRecebtos_fake rTela_fake = null;
    
    // Printer settings
    //static public String DefaultThermalPort = "";
    //static public String DefaultPrinterPort = "";
    //static public String DefaultPrinterMode = "";
    
    // file path
    static public String DefaultFilePath = "./";

    // Ordenacao de Matriz
    static public int Inicio = 1;
    static public int Final = 1;

    // parametros fixo
    static public boolean impPropDiv = true;
    static public boolean bShowCotaParcela = true;
    static public boolean bShowCotaParcelaExtrato = true;
    static public boolean ShowRecebimentoExtrato = true;
    static public int nviasRecibo = 2;
    
    // tela de liberacao retençoes
    static public String lbr_rgprp = "";
    
    static public String marca = "";
    static public String icoBoleta = "";
    static public String icoExtrato = "";
    static public String extPreview = "ExtratoPreview.jasper";
    static public String extPrint = "Extrato.jasper";
    
    // Impressão de boleto
    static public boolean boletoMU = false;
    static public boolean boletoJU = false;
    static public boolean boletoCO = false;
    static public boolean boletoEP = false;
    static public boolean boletoSomaEP = false;
    
    // Impressão no Extrato
    static public boolean extADM = false;
    
    // Impressão no extrato das comissoes sobre MU/JU/CO/EP
    static public boolean extMU = false;
    static public boolean extJU = false;
    static public boolean extCO = false;
    static public boolean extEP = false;
    
    // variavel para tela de recebimento
    static public String rrgprp = "";
    static public String rrgimv = "";
    static public String rcontrato = "";

    static public String  versao = null;
    static public boolean miscelaneas = false;
    static public boolean showrecvalores = false;

    static public String protocolomenu = "";
    //static public String reader = "";

    static public boolean local;
    
    // Conexao compartilhada
    static public DbMain conexao = null;
    
    // geracao
    static public boolean gerMulSelect = false;
    
    //Inativos
    static public boolean Iloca = false;
    static public boolean IProp = false;
    
    // Impressão 
    // lp   - para linux/unix
    // copy - para windows
    //static public String extPrintCmd = "lp ";
    static public String bcobol = "itau";
    static public boolean scroll = false;
    public static String myLogo = "";
    
    public static boolean ShowDocBoleta = true;
    public static Boolean ExtratoTotal = true;
    
    public static float[] bobinaSize = {215f, 730f, 12, 2, -2, 2};
    //public static String externalcmd = null;
    
    // Printers
    public static String PrinterMode = "NORMAL";  // NORMAL | EXTERNA
    public static String Thermica = null;
    public static String ThermicaMode = "NORMAL";
    public static String Printer = null;
    public static String Preview = null;
    public static String Externo = null;
    public static String Externo2 = null;
    public static String PrinterType = "PDF";
    public static Boolean statPrinter = true;
    
    public static Boolean bloqAdianta = false;
    public static Boolean dimob = false;
    
    // Controle de impressão
    public static String AdiantAviso  = "";
    public static String Adiantamento = "";
    public static String Aviso        = "";
    public static String AvisoPre     = "";
    public static String Caixa        = "";
    public static String Deposito     = "";
    public static String Despesas     = "";
    public static String Extrato      = "";
    public static String ExtratoSocio = "";
    public static String Boleta       = "";
    public static String PassCaixa    = "";
    public static String Recibo       = "";
    
    // site informaçoes
    public static String siteIP = "";
    public static String siteUser = "";
    public static String sitePwd = "";
    public static String siteDbName = "";

    // Busca Globalizada
    public static javax.swing.JTextField jBuscar;
    
    public static void LerConf() {
        VariaveisGlobais.myLogo = System.getProperty("myLogo", "resources/login.jpg");
        
        // Printers
        VariaveisGlobais.Thermica = System.getProperty("Thermica", null);
        VariaveisGlobais.Printer = System.getProperty("Printer", null);
        VariaveisGlobais.Preview = System.getProperty("Preview", null);
        VariaveisGlobais.Externo = System.getProperty("Externo", null);
        VariaveisGlobais.Externo2 = System.getProperty("Externo2", null);
        
        // Controle de Impressão
        VariaveisGlobais.AdiantAviso  = System.getProperty("AdiantAviso", "NORMAL,PS/PDF,EXTERNA");
        VariaveisGlobais.Adiantamento = System.getProperty("Adiantamento", "NORMAL,PS/PDF,EXTERNA");
        VariaveisGlobais.Aviso        = System.getProperty("Aviso", "THERMICA,PS/PDF,EXTERNA");
        VariaveisGlobais.AvisoPre     = System.getProperty("AvisoPre", "THERMICA,PS/PDF,EXTERNA");
        VariaveisGlobais.Caixa        = System.getProperty("Caixa", "THERMICA,TXT,EXTERNA");
        VariaveisGlobais.Deposito     = System.getProperty("Deposito", "THERMICA,PS/PDF,EXTERNA");
        VariaveisGlobais.Despesas     = System.getProperty("Despesas", "THERMICA,PS/PDF,EXTERNA");
        VariaveisGlobais.Extrato      = System.getProperty("Extrato", "NORMAL,PS/PDF,EXTERNA");
        VariaveisGlobais.ExtratoSocio = System.getProperty("ExtratoSocio", "NORMAL,PS/PDF,EXTERNA");
        VariaveisGlobais.Boleta       = System.getProperty("Boleta", "NORMAL,PS/PDF,EXTERNA");
        VariaveisGlobais.PassCaixa    = System.getProperty("PassCaixa", "THERMICA,PS/PDF,EXTERNA");
        VariaveisGlobais.Recibo       = System.getProperty("Recibo", "THERMICA,PS/PDF,EXTERNA");
                
        VariaveisGlobais.extPreview = System.getProperty("extPreview", "ExtratoPreview.jasper");
        VariaveisGlobais.extPrint = System.getProperty("extPrint", "Extrato.jasper");
                
        VariaveisGlobais.bShowCotaParcela = Boolean.valueOf(System.getProperty("bShowCotaParcela", "true"));
        VariaveisGlobais.bShowCotaParcelaExtrato = Boolean.valueOf(System.getProperty("bShowCotaParcelaExtrato", "true"));
        VariaveisGlobais.ShowRecebimentoExtrato = Boolean.valueOf(System.getProperty("ShowRecebimentoExtrato", "true"));
        VariaveisGlobais.miscelaneas = Boolean.valueOf(System.getProperty("Micelaneas", "false"));
        VariaveisGlobais.showrecvalores = Boolean.valueOf(System.getProperty("ShowRecValores", "false"));
        VariaveisGlobais.nviasRecibo = Integer.valueOf(System.getProperty("nviasRecibo", "2"));
        VariaveisGlobais.gerMulSelect = Boolean.valueOf(System.getProperty("gerMulSelect", "false"));
        VariaveisGlobais.scroll = Boolean.valueOf(System.getProperty("scroll", "false"));
        VariaveisGlobais.ShowDocBoleta = Boolean.valueOf(System.getProperty("ShowDocBoleta", "true"));
        VariaveisGlobais.ExtratoTotal = Boolean.valueOf(System.getProperty("ExtratoTotal", "true"));
        
        String BobSize[] = System.getProperty("bobinaSize", "215, 730, 12, 10, 0, 2").split(",");
        VariaveisGlobais.bobinaSize = new float[] {Float.valueOf(BobSize[0]),Float.valueOf(BobSize[1]),Float.valueOf(BobSize[2]),
                                                   Float.valueOf(BobSize[3]),Float.valueOf(BobSize[4]),Float.valueOf(BobSize[5])};
        // boletos
        VariaveisGlobais.boletoMU = Boolean.valueOf(System.getProperty("boletoMU", "false"));
        VariaveisGlobais.boletoJU = Boolean.valueOf(System.getProperty("boletoJU", "false"));
        VariaveisGlobais.boletoCO = Boolean.valueOf(System.getProperty("boletoCO", "false"));
        VariaveisGlobais.boletoEP = Boolean.valueOf(System.getProperty("boletoEP", "false"));        
        VariaveisGlobais.boletoSomaEP = Boolean.valueOf(System.getProperty("boletoSomaEP", "false"));        
        
        // extrato
        VariaveisGlobais.extADM = Boolean.valueOf(System.getProperty("extADM", "false"));        

        // extrato MU/JU/CO/EP comissão
        VariaveisGlobais.extMU = Boolean.valueOf(System.getProperty("extMU", "false"));        
        VariaveisGlobais.extJU = Boolean.valueOf(System.getProperty("extJU", "false"));        
        VariaveisGlobais.extCO = Boolean.valueOf(System.getProperty("extCO", "false"));        
        VariaveisGlobais.extEP = Boolean.valueOf(System.getProperty("extEP", "false"));        

        VariaveisGlobais.dbsenha = Boolean.valueOf(System.getProperty("dbSenha", "false"));
        VariaveisGlobais.dbnome  = System.getProperty("dbNome", "jgeral");
        VariaveisGlobais.unidade = System.getProperty("Unidade", "127.0.0.1");
        
        VariaveisGlobais.bloqAdianta = Boolean.valueOf(System.getProperty("bloqAdianta", "false"));
        
        VariaveisGlobais.dimob = Boolean.valueOf(System.getProperty("Dimob", "false"));
    }
}
