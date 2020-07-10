package Funcoes;

import com.mysql.jdbc.CommunicationsException;
import java.sql.*;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author wellspinto@gmail.com
 *
 * Rotinas de manipulação de Banco de Dados mySQL
 */
public class DbMain {

    public Connection conn = null;
    private String hostName = "127.0.0.1";
    private String userName = "root";
    private String password = VariaveisGlobais.passwd;
    private String url = null;
    private String jdbcDriver = null;
    private String dataBaseName = null;
    private String dataBasePrefix = null;
    private String dabaBasePort = null;

    private String mdbConect = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
    private String mdbODBC = "sun.jdbc.odbc.JdbcOdbcDriver";
    
    public DbMain(String host, String user, String passwd, String databasename) {        
        jdbcDriver = "com.mysql.jdbc.Driver";
        hostName = host;
        userName = user;
        password = passwd;
        
        dataBaseName = databasename;
        dataBasePrefix = "jdbc:mysql://";
        dabaBasePort = "3306";

        if ("".equals(host.trim()) && "".equals(user.trim()) && "".equals(passwd.trim()) && !"".equals(databasename.trim())) {
            jdbcDriver = mdbODBC;
            url = mdbConect + databasename.trim();
            userName = "";
            password = "";
        } else {
            url = dataBasePrefix + hostName + ":"+dabaBasePort+"/" + dataBaseName +
                  "?useSSL=false&useUnicode=true&characterEncoding=utf8&autoReconnect=true&useTimezone=true&serverTimezone=UTC";
        }

        System.out.println(url);
        AbrirConexao();
    }
    
    /* Abrir Banco de Dados
     * wellspinto@gmail.com
     * 12/01/2011
     */
    public Connection AbrirConexao(){
        try {
            if (conn == null) {
                Class.forName(jdbcDriver);
                conn = DriverManager.getConnection(url, userName, password);
            } else if (conn.isClosed()) {
                conn = null;
                return AbrirConexao();
            }
        } catch (CommunicationsException e2) {
            JOptionPane.showMessageDialog(null, "Unidade OffLine!!!\nTente novamente...", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            System.exit(0);
        } catch (SQLException e3) {
            JOptionPane.showMessageDialog(null, "Unidade OffLine!!!\nTente novamente...", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
            e3.printStackTrace();
            System.exit(0);
        }
        return conn;
    }

    /**
    * Fecha a conexão com BD.
    *
    */
    public void FecharConexao() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Abrir Tabela de dados
     */
    public ResultSet AbrirTabela(String sqlString, int iTipo) {
        ResultSet hResult = null;
        Connection connectionSQL = this.conn;
        Statement stm = null;

        try {
            stm = connectionSQL.prepareStatement(sqlString, ResultSet.TYPE_SCROLL_INSENSITIVE, iTipo);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            hResult = stm.executeQuery(sqlString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hResult;
    }

    /**
     *
     * @param hResult
     */
    public static void FecharTabela(ResultSet hResult) {
        try {
            hResult.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Abrir Tabela de dados
     */
    public int ExecutarComando(String sqlString) {
        int hRetorno = 0;
        Connection connectionSQL = this.conn;
        Statement stm = null;
        try {
            stm = connectionSQL.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            hRetorno = stm.executeUpdate(sqlString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hRetorno;
    }

    /**
     * Criar String Sql
     */
    public static String CreateSqlText(String aFiels[][], String cTableName, String cWhere, String cTipo) {
        int i = 0;
        String cRet = "";
        String auxCpo = "";

        if (cTipo.equals("INSERT")) {
            cRet = "INSERT INTO " + cTableName + " (";
            auxCpo = "VALUES (";

            for (i=0;i <= aFiels.length - 1; i++) {
                cRet += aFiels[i][0] + ",";

                if (aFiels[i][2] == "C" || aFiels[i][2] == "D") {
                    auxCpo += "'" + aFiels[i][1] + "',";
                } else if (aFiels[i][2] == "N") auxCpo += aFiels[i][1] + ",";
            }

            cRet = cRet.substring(0,cRet.length() -1) + ") "
                 + auxCpo.substring(0,auxCpo.length() - 1) + ")";

        } else if (cTipo.equals("UPDATE")) {
            cRet = "UPDATE " + cTableName + " SET ";

            for (i=0; i <= aFiels.length - 1; i++) {
                cRet += aFiels[i][0] + "=";

                if (aFiels[i][2] == "C" || aFiels[i][2] == "D") {
                    cRet += "'" + aFiels[i][1] + "',";
                } else if (aFiels[i][2] == "N") {
                    cRet += aFiels[i][1] + ",";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + " WHERE " + cWhere;

        } else if (cTipo.equals("SELECT")) {
            cRet = "SELECT";

            for (i=0; i <= aFiels.length - 1; i++) {
                cRet += ((aFiels.equals("")) ? aFiels[i][0] : aFiels[i][1] + ", ");
            }

            cRet = cRet.substring(0, cRet.length() - 2) + " FROM " + cTableName
                 + ((cWhere.equals("")) ? " WHERE " + cWhere : ";");

        }

        return cRet;
    }

    /**
     * LerParametros
     */
    public String LerParametros(String cVar) throws SQLException {
        String rVar = null;

        ResultSet hResult = AbrirTabela("SELECT variavel, conteudo, tipo FROM PARAMETROS WHERE LOWER(TRIM(variavel)) = '" + cVar.toLowerCase().trim() + "';", ResultSet.CONCUR_READ_ONLY);

        if (hResult.first()) {
            rVar = hResult.getString("conteudo");
        }

        return rVar;
    }

    /**
     * GravarParametros
     */
    public boolean GravarParametros(String cVar[]) throws SQLException {
        boolean rVar = false;
        boolean bInsert = false;
        String sql = "";

        bInsert = (LerParametros(cVar[0]) == null);
        if (bInsert) {
            sql = "INSERT INTO PARAMETROS (variavel, conteudo, tipo) VALUES ('" + cVar[0] + "','" + cVar[1] + "','" + cVar[2] + "')";
        } else {
            sql = "UPDATE PARAMETROS SET CONTEUDO = '" + cVar[1] + "' WHERE VARIAVEL = '" + cVar[0] + "';";
        }

        rVar = (ExecutarComando(sql)) > 0;
        return rVar;
    }

    public boolean GravarMultiParametros(String cVar[][]) throws SQLException {
        boolean bInsert = false;
        int i = 0; int nVar = 0;

        for (i=0;i<=cVar.length - 1;i++) {
            String sql = "";

            if (!"".equals(cVar[i][0])) {
                bInsert = (LerParametros(cVar[i][0]) == null);
                if (bInsert) {
                    sql = "INSERT INTO PARAMETROS (variavel, tipo, conteudo) VALUES ('" + cVar[i][0] + "','" + cVar[i][1] + "','" + cVar[i][2] + "')";
                } else {
                    sql = "UPDATE PARAMETROS SET CONTEUDO = '" + cVar[i][2] + "' WHERE VARIAVEL = '" + cVar[i][0] + "';";
                }

                nVar += ExecutarComando(sql);
            }
        }
        return (nVar > 0);
    }

    public String[][] LerCamposTabela(String[] aCampos, String tbNome, String sWhere) throws SQLException {
        String sCampos = FuncoesGlobais.join(aCampos,", ");
        String sSql = "SELECT " + sCampos + " FROM " + tbNome + " WHERE " + sWhere;
        ResultSet tmpResult = AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        ResultSetMetaData md = tmpResult.getMetaData();
        String[][] vRetorno = new String[aCampos.length][4];
        int i = 0;

        if(tmpResult.first()) {
            for (i=0; i<= aCampos.length - 1; i++) {
                vRetorno[i][0] = md.getColumnName(i + 1);
                vRetorno[i][1] =  md.getColumnTypeName(i + 1);
                
                // Trabala field name
                String variavel = aCampos[i].trim();
                if (variavel.toLowerCase().contains(" as ")) {
                    variavel = variavel.substring(variavel.toLowerCase().indexOf(" as") + 3).trim();
                } 
                try {
                    //vRetorno[i][2] =  String.valueOf(tmpResult.getString(aCampos[i]).length());
                    vRetorno[i][2] =  String.valueOf(tmpResult.getString(variavel).length());
                } catch (NullPointerException ex) { vRetorno[i][2] = "0"; }
                try {
                    //vRetorno[i][3] = tmpResult.getString(aCampos[i]);
                    vRetorno[i][3] = tmpResult.getString(variavel);
                } catch (NullPointerException ex) { vRetorno[i][3] = ""; }
            }
        } else {
            vRetorno = null;
        }

        FecharTabela(tmpResult);

        return vRetorno;
    }

    public static int RecordCount(ResultSet hrs) {
        
        int retorno = 0;
        try {
            int pos = hrs.getRow();
            hrs.last();
            retorno = hrs.getRow();
            hrs.beforeFirst();
            if (pos > 0) hrs.absolute(pos);
        } catch (SQLException e) {retorno = 0;}
        return retorno;
    }    
    
    public static String CreateSqlText2(String aFiels[][], String cTableName, String cWhere, String cTipo) {
        int i = 0;
        String cRet = "";
        String auxCpo = "";

        if (cTipo.equals("INSERT")) {
            cRet = "INSERT INTO " + cTableName + " (";
            auxCpo = "VALUES (";

            for (i=0;i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += aFiels[i][0] + ",";
                    auxCpo += "'" + aFiels[i][2] + "',";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + ") "
                 + auxCpo.substring(0,auxCpo.length() - 1) + ")";

        } else if (cTipo.equals("UPDATE")) {
            cRet = "UPDATE " + cTableName + " SET ";

            for (i=0; i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += aFiels[i][0] + "=";
                    cRet += "'" + aFiels[i][2] + "',";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + " WHERE " + cWhere;

        } else if (cTipo.equals("SELECT")) {
            cRet = "SELECT ";

            for (i=0; i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += (!"".equals(aFiels[i][2]) ? aFiels[i][0] : aFiels[i][2]) + ", ";
                }
            }

            cRet = cRet.substring(0, cRet.length() - 2) + " FROM " + cTableName
                 + (!"".equals(cWhere.trim()) ? " WHERE " + cWhere : "") + ";";
        }

        return cRet;
    }

    public void CreateAuxiliartmp() throws SQLException {
        String sString = "";
        
        sString = "CREATE TABLE `auxiliartmp` ( " +
                "  `AUTOID` float NOT NULL AUTO_INCREMENT, " +
                "  `CONTA` varchar(3) DEFAULT NULL, " +
                "  `RGPRP` varchar(6) NOT NULL DEFAULT '', " +
                "  `RGIMV` varchar(6) NOT NULL DEFAULT '', " +
                "  `CONTRATO` varchar(7) NOT NULL DEFAULT '', " +
                "  `CAMPO` text, " +
                "  `DTVENCIMENTO` date NOT NULL DEFAULT '0000-00-00', " +
                "  `DTRECEBIMENTO` date NOT NULL DEFAULT '0000-00-00', " +
                "  `RC_AUT` double NOT NULL DEFAULT '0', " +
                "  PRIMARY KEY (`AUTOID`) " +
                ") ENGINE=MyISAM AUTO_INCREMENT=35267 DEFAULT CHARSET=latin1";
                
        if (!ExistTable("auxiliartmp")) ExecutarComando(sString);
    }

    public void CreateExtBancotmp() throws SQLException {
        String sString = "CREATE TABLE `ExtBancotmp` ( " +
                        "  `CH_DATA` date DEFAULT NULL, " +
                        "  `CH_DATA2` datetime DEFAULT NULL, " +
                        "  `CH_BANCO` varchar(5) DEFAULT NULL, " +
                        "  `CH_AGENCIA` varchar(4) DEFAULT NULL, " +
                        "  `CH_NCHEQUE` varchar(8) DEFAULT NULL, " +
                        "  `CH_VALOR` decimal(19,4) DEFAULT NULL, " +
                        "  `CH_ETDA` varchar(3) DEFAULT NULL, " +
                        "  `CH_DESC` varchar(20) DEFAULT NULL, " +
                        "  `CH_DTDEPOS` date DEFAULT NULL, " +
                        "  `CH_BCOOR` varchar(5) DEFAULT NULL, " +
                        "  `CH_AUTENTICACAO` varchar(20) DEFAULT NULL, " +
                        "  `CH_AUTENT` varchar(6) DEFAULT NULL, " +
                        "  `CH_NCAIXA` varchar(30) DEFAULT NULL " +
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
        
        if (!ExistTable("ExtBancotmp")) ExecutarComando(sString);
    }
    
    public void CreateChequestmp() throws SQLException {
        String sString = "CREATE TABLE `Chequestmp` ( " +
                        "  `CH_DATA` date DEFAULT NULL, " +
                        "  `CH_DATA2` date DEFAULT NULL, " +
                        "  `CH_BANCO` varchar(5) DEFAULT NULL, " +
                        "  `CH_AGENCIA` varchar(4) DEFAULT NULL, " +
                        "  `CH_NCHEQUE` varchar(8) DEFAULT NULL, " +
                        "  `CH_VALOR` decimal(19,4) DEFAULT NULL, " +
                        "  `CH_NCAIXA` varchar(25) DEFAULT NULL, " +
                        "  `CH_AUTENTICACAO` varchar(20) DEFAULT NULL, " +
                        "  `CH_AUTENT` varchar(6) DEFAULT NULL " +
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
        
        if (!ExistTable("Chequestmp")) ExecutarComando(sString);
    }
    
    public void CreateCaixatmp() throws SQLException {
        String sString = "";
        
        sString = "CREATE TABLE `caixatmp` ( " +
                "  `CX_AUT` double DEFAULT NULL COMMENT 'Numero da Autenticação', " +
                "  `CX_DATA` datetime DEFAULT NULL COMMENT 'Data Autenticação', " +
                "  `CX_HORA` varchar(8) DEFAULT NULL COMMENT 'Hora Autenticação', " +
                "  `CX_LOGADO` varchar(30) DEFAULT NULL COMMENT 'Caixa Logado', " +
                "  `CX_CONTRATO` varchar(6) DEFAULT NULL COMMENT 'Numero do Contrato', " +
                "  `CX_RGPRP` varchar(6) DEFAULT NULL COMMENT 'Numero do Proprietário', " +
                "  `CX_RGIMV` varchar(6) DEFAULT NULL COMMENT 'Numero do Imóvel', " +
                "  `CX_OPER` varchar(3) DEFAULT NULL COMMENT 'Tipo Operação (CRE ou DEB)', " +
                "  `CX_VRDN` decimal(19,4) unsigned DEFAULT NULL COMMENT 'Valor em Dinheiro', " +
                "  `CX_VRCH` decimal(19,4) unsigned DEFAULT NULL COMMENT 'Valor em Cheque', " +
                "  `CX_CHREL` varchar(255) DEFAULT NULL COMMENT 'Relação dos cheques', " +
                "  `CX_TIPOPG` varchar(2) DEFAULT NULL COMMENT 'Tipo pag/rec (DN,CH,CP,CT,CD,DP)', " +
                "  `CX_DOC` varchar(30) DEFAULT NULL COMMENT 'Documento Emitido', " +
                "  `CX_NDOCS` int(11) DEFAULT '0' " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
                
        if (!ExistTable("caixatmp")) ExecutarComando(sString);
    }
    
    public void CreateRazaotmp() throws SQLException {
        String sString = "";
        
        sString = "CREATE TABLE `razaotmp` ( " +
                "  `RGPRP` varchar(6) NOT NULL DEFAULT '', " +
                "  `RGIMV` varchar(6) NOT NULL DEFAULT '', " +
                "  `CONTRATO` varchar(7) NOT NULL DEFAULT '', " +
                "  `CAMPO` text, " +
                "  `DTVENCIMENTO` date NOT NULL DEFAULT '0000-00-00', " +
                "  `DTRECEBIMENTO` date NOT NULL DEFAULT '0000-00-00', " +
                "  `TAG` varchar(1) DEFAULT ' ', " +
                "  `RC_AUT` double NOT NULL DEFAULT '0', " +
                "  `RZ_AUT` double NOT NULL DEFAULT '0', " +
                "  `AV_AUT` double NOT NULL DEFAULT '0' " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
        
        if (!ExistTable("razaotmp")) ExecutarComando(sString);
    }
    
    public void CreateAvisotmp() throws SQLException {
        String sString = "";
        
        sString = "CREATE TABLE `avisostmp` (" +
                "  `autoid` bigint(20) unsigned NOT NULL AUTO_INCREMENT, " +
                "  `RID` varchar(1) DEFAULT NULL, " +
                "  `registro` varchar(6) DEFAULT NULL, " +
                "  `campo` text, " +
                "  `tag` varchar(1) DEFAULT ' ', " +
                "  `autenticacao` int(11) NOT NULL, " +
                "  `et_aut` double NOT NULL DEFAULT '0', " +
                "  PRIMARY KEY (`autoid`) " +
                ") ENGINE=MyISAM AUTO_INCREMENT=12156 DEFAULT CHARSET=latin1";
        if (!ExistTable("avisostmp")) ExecutarComando(sString);
    }
    
    public void CreateArqAux() throws SQLException {
        String sString = "";

        sString = "CREATE TABLE  `" + dataBaseName + "`.`auxiliar` (";
        sString += "  `AUTOID` FLOAT  NOT NULL AUTO_INCREMENT,";
        sString += "  `CONTA` VARCHAR(3),";
        sString += "  `RGPRP` varchar(6) NOT NULL default '',";
        sString += "  `RGIMV` varchar(6) NOT NULL default '',";
        sString += "  `CONTRATO` varchar(7) NOT NULL default '',";
        sString += "  `CAMPO` text,";
        sString += "  `DTVENCIMENTO` date NOT NULL default '0000-00-00',";
        sString += "  `DTRECEBIMENTO` date NOT NULL default '0000-00-00',";
        sString += "  `RC_AUT` double NOT NULL default '0',";
        sString += "   PRIMARY KEY (`AUTOID`)";
        sString += ") ENGINE=MyISAM DEFAULT CHARSET=latin1";

        if (!ExistTable("auxiliar")) ExecutarComando(sString);        
    }
        
    public void CreateVisitas() throws SQLException {
        String sString = "";
        sString = "CREATE TABLE `" + dataBaseName + "`.`visitas` (";
        sString += "`ord` int(11) NOT NULL AUTO_INCREMENT,";
        sString += "`rgimv` varchar(6) NOT NULL,";
        sString += "`end` varchar(100) NOT NULL,";
        sString += "`dv_nome` varchar(60) DEFAULT NULL,";
        sString += "`dv_docto` varchar(60) DEFAULT NULL,";
        sString += "`dv_telefone` varchar(15) DEFAULT NULL,";
        sString += "`dv_dthrETD` datetime DEFAULT NULL,";
        sString += "`dv_dthrETA` datetime DEFAULT NULL,";
        sString += "`dv_historico` text,";
        sString += "PRIMARY KEY (`ord`)";
        sString += ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='arquivo contendo visitas a imóvel vazio com opnião dos visitantes'";
        if (!ExistTable("visitas")) ExecutarComando(sString);        
    }
    
    public boolean ExistTable(String TableName) throws SQLException {
        ResultSet tbl = AbrirTabela("SHOW TABLES LIKE '" + TableName + "';", ResultSet.CONCUR_READ_ONLY);
        tbl.last();
        boolean retorno = tbl.getRow() > 0;
        tbl.beforeFirst();
        FecharTabela(tbl);
        return retorno;
    }
    
    public void LancarCheques(String[][] aTrancicao, String nAut) {
        for (int i=0;i<aTrancicao.length;i++) {
            if (!"".equals(aTrancicao[i][1].trim()) && "CRE".equals(aTrancicao[i][6])) {
                String cSql = "INSERT INTO Cheques (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_ncaixa, ch_autenticacao) " +
                   "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.')";

                cSql = FuncoesGlobais.Subst(cSql, new String[] {
                    Dates.DateFormata("yyyy-MM-dd", new Date()),
                    ("".equals(aTrancicao[i][0].trim()) ? "0000-00-00" : Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(aTrancicao[i][0],"dd/MM/yyyy"))), 
                    aTrancicao[i][1], 
                    aTrancicao[i][2], 
                    aTrancicao[i][3], 
                    aTrancicao[i][4],
                    VariaveisGlobais.usuario, 
                    nAut});
            
                ExecutarComando(cSql);
            }
            
            if (!"DN".equals(aTrancicao[i][5]) || !"CT".equals(aTrancicao[i][5])) {
                // ExtBancario
                String cSql = "INSERT INTO ExtBanco (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_etda, ch_ncaixa, ch_autenticacao) " +
                   "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.')";

                cSql = FuncoesGlobais.Subst(cSql, new String[] {
                    Dates.DateFormata("yyyy-MM-dd", new Date()),
                    ("".equals(aTrancicao[i][0].trim()) ? "0000-00-00" : Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(aTrancicao[i][0],"dd/MM/yyyy"))), 
                    aTrancicao[i][1], 
                    aTrancicao[i][2], 
                    aTrancicao[i][3], 
                    aTrancicao[i][4],
                    aTrancicao[i][5],
                    VariaveisGlobais.usuario, 
                    nAut});

                ExecutarComando(cSql);
            }
        }        
    }
    
    public void LancarCaixa(String[] oper, String[][] aTranscicao, String nAut) {
        Date now = new Date();
        
        for (int i=0; i<aTranscicao.length; i++) {
            String Sql = "INSERT INTO caixa (cx_aut, cx_data, cx_hora, cx_logado, cx_contrato, cx_rgprp, " + 
            "cx_rgimv, cx_oper, cx_vrdn, cx_vrch, cx_chrel, cx_tipopg, cx_doc, cx_ndocs) " + 
            "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.', '&10.', '&11.', '&12.', '&13.', '&14.')";
            String rel = "";
            String valor = String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4]),2)));
            if (aTranscicao[i][5].equals("DN") || aTranscicao[i][5].equals("CT")) {
                rel = (!"".equals(aTranscicao[i][8]) ? "CN:" + aTranscicao[i][8] + " " : "") + aTranscicao[i][5] + ":" + LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4].trim()),2);
                Sql = FuncoesGlobais.Subst(Sql, new String[] {
                    nAut, Dates.DateFormata("yyyy-MM-dd", now), Dates.DateFormata("HH:mm:ss", now),
                    VariaveisGlobais.usuario, oper[2], oper[0], oper[1], aTranscicao[i][6], 
                    valor,"0",rel,aTranscicao[i][5],aTranscicao[i][7],"1"});
            } else if (aTranscicao[i][5].equals("CH") || aTranscicao[i][5].equals("CP")) {
                rel = (!"".equals(aTranscicao[i][0]) ? "DT:" + aTranscicao[i][0].trim() + " " : "") + 
                        "BC:" + aTranscicao[i][1].trim() + " AG:" + aTranscicao[i][2].trim() +
                        " CH:" + aTranscicao[i][3].trim() + "             " + " VR:" + LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4].trim()),2);
                Sql = FuncoesGlobais.Subst(Sql, new String[] {
                    nAut, Dates.DateFormata("yyyy-MM-dd", now), Dates.DateFormata("HH:mm:ss", now),
                    VariaveisGlobais.usuario, oper[2], oper[0], oper[1], aTranscicao[i][6], 
                    "0",valor, rel ,aTranscicao[i][5],aTranscicao[i][7],"1"});            
            }
            ExecutarComando(Sql);
        }
        
        if (!"VARIOS_RECIBOS".equals(aTranscicao[aTranscicao.length -1][8].trim().toUpperCase())) {
            LancarCheques(aTranscicao, nAut);
        }
    }

    public void LancarChequestmp(String[][] aTrancicao, String nAut) {
        for (int i=0;i<aTrancicao.length;i++) {
            if (!"".equals(aTrancicao[i][1].trim()) && "CRE".equals(aTrancicao[i][6])) {
                String cSql = "INSERT INTO Chequestmp (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_ncaixa, ch_autenticacao) " +
                   "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.')";

                cSql = FuncoesGlobais.Subst(cSql, new String[] {
                    Dates.DateFormata("yyyy-MM-dd", new Date()),
                    ("".equals(aTrancicao[i][0].trim()) ? "0000-00-00" : Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(aTrancicao[i][0],"dd/MM/yyyy"))), 
                    aTrancicao[i][1], 
                    aTrancicao[i][2], 
                    aTrancicao[i][3], 
                    aTrancicao[i][4],
                    VariaveisGlobais.usuario, 
                    nAut});
            
                try {
                    CreateChequestmp();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                ExecutarComando(cSql);
            }
            
            if (!"DN".equals(aTrancicao[i][5]) || !"CT".equals(aTrancicao[i][5])) {
                // ExtBancario
                String cSql = "INSERT INTO ExtBancotmp (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_etda, ch_ncaixa, ch_autenticacao) " +
                   "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.')";

                cSql = FuncoesGlobais.Subst(cSql, new String[] {
                    Dates.DateFormata("yyyy-MM-dd", new Date()),
                    ("".equals(aTrancicao[i][0].trim()) ? "0000-00-00" : Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(aTrancicao[i][0],"dd/MM/yyyy"))), 
                    aTrancicao[i][1], 
                    aTrancicao[i][2], 
                    aTrancicao[i][3], 
                    aTrancicao[i][4],
                    aTrancicao[i][5],
                    VariaveisGlobais.usuario, 
                    nAut});

                try {
                    CreateExtBancotmp();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                ExecutarComando(cSql);
            }
        }        
    }
    
    public void LancarCaixatmp(String[] oper, String[][] aTranscicao, String nAut) {
        Date now = new Date();
        
        for (int i=0; i<aTranscicao.length; i++) {
            String Sql = "INSERT INTO caixatmp (cx_aut, cx_data, cx_hora, cx_logado, cx_contrato, cx_rgprp, " + 
            "cx_rgimv, cx_oper, cx_vrdn, cx_vrch, cx_chrel, cx_tipopg, cx_doc, cx_ndocs) " + 
            "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.', '&10.', '&11.', '&12.', '&13.', '&14.')";
            String rel = "";
            String valor = String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4]),2)));
            if (aTranscicao[i][5].equals("DN") || aTranscicao[i][5].equals("CT")) {
                rel = (!"".equals(aTranscicao[i][8]) ? "CN:" + aTranscicao[i][8] + " " : "") + aTranscicao[i][5] + ":" + LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4].trim()),2);
                Sql = FuncoesGlobais.Subst(Sql, new String[] {
                    nAut, Dates.DateFormata("yyyy-MM-dd", now), Dates.DateFormata("HH:mm:ss", now),
                    VariaveisGlobais.usuario, oper[2], oper[0], oper[1], aTranscicao[i][6], 
                    valor,"0",rel,aTranscicao[i][5],aTranscicao[i][7],"1"});
            } else if (aTranscicao[i][5].equals("CH") || aTranscicao[i][5].equals("CP")) {
                rel = (!"".equals(aTranscicao[i][0]) ? "DT:" + aTranscicao[i][0].trim() + " " : "") + 
                        "BC:" + aTranscicao[i][1].trim() + " AG:" + aTranscicao[i][2].trim() +
                        " CH:" + aTranscicao[i][3].trim() + "             " + " VR:" + LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4].trim()),2);
                Sql = FuncoesGlobais.Subst(Sql, new String[] {
                    nAut, Dates.DateFormata("yyyy-MM-dd", now), Dates.DateFormata("HH:mm:ss", now),
                    VariaveisGlobais.usuario, oper[2], oper[0], oper[1], aTranscicao[i][6], 
                    "0",valor, rel ,aTranscicao[i][5],aTranscicao[i][7],"1"});            
            }
            
            try {
                CreateCaixatmp();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            ExecutarComando(Sql);
        }
        
        if (!"VARIOS_RECIBOS".equals(aTranscicao[aTranscicao.length -1][8].trim().toUpperCase())) {
            LancarChequestmp(aTranscicao, nAut);
        }
    }

    public void CriarMySqlProcedures(String qual, String cParam) {
        String sql;
        if ("ALL".equals(qual.toUpperCase()) || "GERAMOVTO".equals(qual.toUpperCase())) {
            sql = "DROP PROCEDURE IF EXISTS `" + dataBaseName + "`.`GeraMovto`";
            ExecutarComando(sql);
            
            sql = "CREATE DEFINER=`root`@`localhost` PROCEDURE  `" + dataBaseName + "`.`GeraMovto`() BEGIN " +
                 "DECLARE eBol Int DEFAULT 0; " +
                 "DECLARE sRgp VARCHAR(6); " + 
                 "DECLARE sRgi VARCHAR(6); " +
                 "DECLARE sCtr VARCHAR(6); " +
                 "DECLARE sVecto VARCHAR(10); " + 
                 "DECLARE sCampo LONGTEXT; " + 
                 "DECLARE sBusca LONGTEXT; " + 
                 "DECLARE sUpCpo LONGTEXT; " + 
                 "DECLARE iPos Int; " + 
                 "DECLARE sDesc LONGTEXT; " + 
                 "DECLARE sDife LONGTEXT; " + 
                 "DECLARE sSegu LONGTEXT; " + 
                 "DECLARE done BOOLEAN; " + 
                 "DECLARE cur1 CURSOR FOR SELECT l.boleta, c.rgprp, c.rgimv, c.contrato, c.dtvencimento, c.campo FROM CARTEIRA c, locatarios l WHERE c.contrato = l.contrato" + ("".equals(cParam.trim()) ? ";" : " AND (" + cParam + ");") + " " +
                 //"DECLARE cur1 CURSOR FOR SELECT l.boleta, c.rgprp, c.rgimv, c.contrato, c.dtvencimento, c.campo FROM CARTEIRA c, locatarios l WHERE c.contrato = l.contrato AND (c.rgimv LIKE '63802'); " + 
                 "DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; " +
                 "OPEN cur1; " + 
                 "MLOOP: LOOP   " + 
                 "    FETCH cur1 INTO eBol, SRgp, sRgi, sCtr, sVecto, sCampo;" + 
                 "    IF done THEN LEAVE MLOOP; END IF;" + 
                 "    set sDesc = gDesconto(sCtr, Right(sVecto, 7));" + 
                 "    set sDife = gDiferenca(sCtr, Right(sVecto, 7));" + 
                 "    set sSegu = gSeguro(sCtr, Right(sVecto, 7));" + 
                 "    set sBusca = sCampo;" + 
                 "    WHILE InStr(sBusca, ':*') > 0 DO" + 
                 "        set sBusca = Replace(sBusca, ':*','');" + 
                 "    END WHILE ;" + 
                 "    set sUpCpo = sBusca;" + 
                 "    set iPos = InStr(sBusca, ':DV');" + 
                 "    IF iPos > 0 THEN" + 
                 "        set sBusca = CONCAT(Left(sBusca, iPos - 1), Mid(sBusca, iPos + 5));" + 
                 "    END IF ;" + 
                 "    set sBusca = Replace(CONCAT(sBusca, ';', sDesc, ';', sDife, ';', sSegu), '::',':');" + 
                 "    WHILE InStr(sBusca, '::') > 0 DO" + 
                 "        set sBusca = Replace(sBusca, '::',':');" + 
                 "    END WHILE ;" + 
                 "    WHILE InStr(sBusca, ';;') > 0 DO" + 
                 "        set sBusca = Replace(sBusca, ';;',';');" + 
                 "    END WHILE ;" + 
                 "    IF Left(sBusca, 1) = ';' THEN set sBusca = MID(sBusca,2); END IF;" + 
                 "    IF Right(sBusca, 1) = ';' THEN set sBusca = MID(sBusca,1,LENGTH(sBusca) - 1); END IF;" + 
                 "    INSERT INTO TMPRECIBO (rgprp, rgimv, contrato, campo, dtvencimento, tag) VALUES(sRgp, sRgi, sCtr, sBusca, CONCAT(Right(sVecto, 4), '-',MID(sVecto,4,2),'-',LEFT(sVecto,2)), ' ');" + 
                 "    UPDATE CARTEIRA SET dtvencimento = ProxVecto(sUpCpo, sVecto), campo = AtRemove(CotaParc(campo)) WHERE contrato = sCtr;" + 
                 "END LOOP MLOOP;" + 
                 "CLOSE cur1;" + 
                 "INSERT INTO RECIBO (RGPRP,RGIMV,CONTRATO,CAMPO,DTVENCIMENTO,TAG) SELECT RGPRP,RGIMV,CONTRATO,CAMPO,DTVENCIMENTO,TAG FROM TMPRECIBO; " +
                 "DELETE FROM TMPRECIBO;" + 
                 "END";   

                 //"INSERT INTO RECIBO SELECT * FROM TMPRECIBO;" + 
                ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "COTAPARC".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`CotaParc`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`CotaParc`(sCampo LONGTEXT) RETURNS longtext CHARSET latin1 " +
                  "BEGIN " + 
                  "DECLARE iPos Int; " + 
                  "DECLARE sCp LONGTEXT; " + 
                  "DECLARE sCpos LONGTEXT; " + 
                  "DECLARE sAux LONGTEXT; " + 
                  "DECLARE sPart1 VARCHAR(2); " + 
                  "DECLARE sPart2 VARCHAR(4); " + 
                  "set sPart1 = '00'; " + 
                  "set sPart2 = '0000'; " + 
                  "set sCpos = ''; " + 
                  "set sCampo = CONCAT(sCampo,';'); " + 
                  "set iPos = InStr(sCampo, ';'); " + 
                  "WHILE iPos > 0 DO" + 
                  "   set sAux = Mid(sCampo, 1, iPos - 1);" + 
                  "   set sCampo = Mid(sCampo, iPos + 1);" + 
                  "   set sCp = Mid(sAux, 17, 6);" + 
                  "   IF Mid(sCp, 5, 1) = ':' THEN set sCp = LEFT(sCp,4); END IF;" + 
                  "   IF CHAR_LENGTH(sCp) = 4 THEN" + 
                  "      set sPart1 = LEFT(TRIM(sCp),2);" + 
                  "      set sPart2 = RIGHT(TRIM(sCp),2);" + 
                  "      IF sPart1 = sPart2 AND sPart1 <> '00' AND sPart2 <> '00' THEN" + 
                  "          set sPart1 = '00';" + 
                  "          set sPart2 = '00';" + 
                  "      ELSEIF CAST(sPart1 AS UNSIGNED INTEGER) < CAST(sPart2 AS UNSIGNED INTEGER) THEN" + 
                  "          set sPart1 = RIGHT(CONCAT('00',CAST(sPart1 AS UNSIGNED INTEGER) + 1),2);" + 
                  "      END IF;" + 
                  "      set sAux = REPLACE(sAux,sCp,CONCAT(sPart1,sPart2));" + 
                  "      ELSE" + 
                  "          set sPart1 = LEFT(TRIM(sCp),2);" + 
                  "          set sPart2 = RIGHT(TRIM(sCp),4);" + 
                  "          IF sPart1 = '12' AND sPart1 <> '00' AND sPart2 <> '0000' THEN" + 
                  "              set sPart1 = '00';" + 
                  "              set sPart2 = '0000';" + 
                  "          ELSEIF CAST(sPart1 AS UNSIGNED INTEGER) < 12 THEN" + 
                  "              set sPart1 = RIGHT(CONCAT('00',CAST(sPart1 AS UNSIGNED INTEGER) + 1),2);" + 
                  "          END IF;" + 
                  "          set sAux = REPLACE(sAux,sCp,CONCAT(sPart1,sPart2));" + 
                  "      END IF;" + 
                  "      set sCpos = CONCAT(sCpos, ';', sAux);" + 
                  "      set iPos = InStr(sCampo, ';'); " + 
                  "END WHILE ;" + 
                  "RETURN MID(sCpos,2);" + 
                  "END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "COUNTSTR".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`CountStr`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`CountStr`(cCampo LongText, patern VarChar(30)) RETURNS int(11) " +
                  "BEGIN declare i Int; declare nStr Int; declare nRet Int; set i = 1; set nStr = LENGTH(cCampo); set nRet = 0; WHILE i <= nStr DO IF Mid(cCampo, i, 1) = Trim(patern) THEN set nRet = nRet + 1; END IF; set i = i + 1; END WHILE; RETURN nRet; END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "CRIPTANOME".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`CriptaNome`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`CriptaNome`(sValue text) RETURNS text CHARSET latin1\n" +
                    "BEGIN 	\n" +
                    "	declare i Int; 	\n" +
                    "    declare iLen Int; 	\n" +
                    "    declare sRet text; 	\n" +
                    "    declare sChar CHAR(2); 	\n" +
                    "    set sRet = ''; 	\n" +
                    "    set i = 1; 	\n" +
                    "    set iLen = LENGTH(UCase(Trim(sValue))); 	\n" +
                    "    IF iLen > 0 THEN 		\n" +
                    "		WHILE i <= iLen DO 			\n" +
                    "			set sChar = CAST(ASCII(Mid(UCase(Trim(sValue)), i, 1)) AS CHAR(2));\n" +
                    "			set sRet = CONCAT(sRet, sChar);\n" +
                    "			set i = i + 1;\n" +
                    " 		END WHILE;\n" +
                    "	END IF;\n" +
                    " 	RETURN sRet;\n" +
                    "END;";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "DECRIPTANOME".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`DecriptaNome`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`DecriptaNome`(sValue text) RETURNS text CHARSET latin1\n" +
                    "BEGIN	\n" +
                    "	declare i Int;  	\n" +
                    "    declare iLen Int; 	\n" +
                    "    declare sRet text; 	\n" +
                    "    declare Letra CHAR(2);\n" +
                    "    declare passo Int;\n" +
                    "    set i = 1; 	set passo = 2;\n" +
                    "    set iLen = LENGTH(UCase(Trim(sValue))); 	\n" +
                    "    IF iLen > 0 THEN 		\n" +
                    "		WHILE i <= iLen DO			\n" +
                    "			set Letra = SUBSTRING(sValue, i, passo);\n" +
                    "			set sRet = CONCAT_WS('', sRet, Char(Letra));\n" +
                    "			set i = i + passo;\n" +
                    "		END WHILE; 	\n" +
                    "	END IF; 	\n" +
                    "	RETURN sRet;\n" +
                    "END ;";
            ExecutarComando(sql);
        }

        if ("ALL".equals(qual.toUpperCase()) || "GDESCONTO".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`gDesconto`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`gDesconto`(sContrato CHAR(6), sReferencia CHAR(7)) RETURNS longtext CHARSET latin1 " +
                  "BEGIN DECLARE a LONGTEXT; DECLARE i Int; DECLARE b LONGTEXT; DECLARE done BOOLEAN DEFAULT FALSE; DECLARE cur1 CURSOR FOR SELECT CONCAT( 'DC:2:',valor,':','0000',':DC:',sigla,':DS',CriptaNome(remove_accents(descricao))) AS campo FROM Descontos WHERE contrato = sContrato and referencia = sReferencia; DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; set i = 0; OPEN cur1; REPEAT FETCH cur1 INTO a; set b = CONCAT_WS( ';',b,a); set i = i + 1; IF i >= ( SELECT COUNT(referencia) FROM Descontos WHERE contrato = sContrato AND referencia = sReferencia) THEN set done = TRUE; END IF ; UNTIL done END REPEAT; CLOSE cur1; RETURN Trim(b); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "GDIFERENCA".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`gDiferenca`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `"+ dataBaseName + "`.`gDiferenca`(sContrato CHAR(6), sReferencia CHAR(7)) RETURNS longtext CHARSET latin1 " +
                  "BEGIN DECLARE a LONGTEXT; DECLARE i Int; DECLARE b LONGTEXT; DECLARE done BOOLEAN DEFAULT FALSE; DECLARE cur1 CURSOR FOR SELECT CONCAT( 'DF:2:',valor,':','0000',':DF:',sigla,':DS',CriptaNome(remove_accents(descricao))) AS campo FROM Diferenca WHERE contrato = sContrato and referencia = sReferencia; DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; set i = 0; OPEN cur1; REPEAT FETCH cur1 INTO a; set b = CONCAT_WS( ';',b,a); set i = i + 1; IF i >= ( SELECT COUNT(referencia) FROM Diferenca WHERE contrato = sContrato AND referencia = sReferencia) THEN set done = TRUE; END IF ; UNTIL done END REPEAT; CLOSE cur1; RETURN Trim(b); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "GSEGURO".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`gSeguro`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`gSeguro`(sContrato CHAR(6), sReferencia CHAR(7)) RETURNS longtext CHARSET latin1 " + 
                  "BEGIN DECLARE a LONGTEXT; DECLARE i Int; DECLARE b LONGTEXT; DECLARE done BOOLEAN DEFAULT FALSE; DECLARE cur1 CURSOR FOR SELECT CONCAT( 'SG:3:',valor,':','0000',':SG:',sigla) AS campo FROM Seguros WHERE contrato = sContrato and referencia = sReferencia; DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; set i = 0; OPEN cur1; REPEAT FETCH cur1 INTO a; set b = CONCAT_WS( ';',b,a); set i = i + 1; IF i >= ( SELECT COUNT(referencia) FROM Seguros WHERE contrato = sContrato AND referencia = sReferencia) THEN set done = TRUE; END IF ; UNTIL done END REPEAT; CLOSE cur1; RETURN Trim(b); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "PROXVECTO".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`ProxVecto`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`ProxVecto`(sCampo LongText, sVecto Char(10)) RETURNS char(10) CHARSET latin1 " + 
                  "BEGIN declare ultDiaMes Int; declare iPos Int; declare iDay Int; declare rDay Int; declare sMes Date; declare iMes Int; declare iAno Int; set iMes = CAST(Mid(sVecto, 4, 2) AS UNSIGNED Int); set iANO = CAST(Mid(sVecto, 7, 4) AS UNSIGNED Int); set iMes = iMes + 1; IF iMes > 12 THEN set iMes = 1; set iAno = iAno + 1; END IF; set sMes = CAST(CONCAT(Right(CONCAT('0000',TRIM(CAST(iAno AS CHAR(4)))),4),'-',RIGHT(CONCAT('00',TRIM(CAST(iMes AS CHAR(2)))),2),'-01') AS DATE); set ultDiaMes = Day(LAST_DAY(sMes)); set iPos = InStr(Trim(sCampo), 'DV'); set iDay = CAST(Mid(Trim(sCampo), iPos + 2, 2) AS UNSIGNED Int); IF iPos > 0 THEN IF iDay > ultDiaMes THEN set rDay = ultDiaMes; ELSE set rDay = iDay; END IF; ELSE set iDay = CAST(LEFT(sVecto,2) AS UNSIGNED Int); IF iDay > ultDiaMes THEN set rDay = ultDiaMes; ELSE set rDay = iDay; END IF; END IF; RETURN CONCAT(Right(CONCAT('00',TRIM(CAST(rDay AS CHAR(2)))),2),'/',RIGHT(CONCAT('00',TRIM(CAST(iMes AS CHAR(2)))),2),'/',RIGHT(CONCAT('0000',TRIM(CAST(iAno AS CHAR(4)))),4)); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "PROXVECTO2".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`ProxVecto2`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`ProxVecto2`(sCampo LongText, sVecto Char(10), sVecto2 Char(10)) RETURNS char(10) CHARSET latin1\n" +
                "BEGIN\n" +
                "	declare ultDiaMes Int; \n" +
                "	declare iPos Int; \n" +
                "	declare iDay Int; \n" +
                "	declare rDay Int; \n" +
                "	declare sMes Date; \n" +
                "	declare iMes Int; \n" +
                "	declare iAno Int; \n" +
                "\n" +
                "	IF CAST(Mid(sVecto,7,4) AS UNSIGNED Int) > 0 THEN\n" +
                "		set sVecto = ConCat(Mid(sVecto,7,4), \"-\", Mid(sVecto,4,2), \"-\", Mid(sVecto,1,2));\n" +
                "	END IF;\n" +
                "    \n" +
                "	set iMes = CAST(Mid(sVecto, 6, 2) AS UNSIGNED Int);\n" +
                "	set iANO = CAST(Mid(sVecto, 1, 4) AS UNSIGNED Int); \n" +
                "	set iMes = iMes + 1; \n" +
                "	IF iMes > 12 THEN \n" +
                "		set iMes = 1; \n" +
                "		set iAno = iAno + 1; \n" +
                "	END IF; \n" +
                "	set sMes = CAST(CONCAT(Right(CONCAT('0000',TRIM(CAST(iAno AS CHAR(4)))),4),'-',RIGHT(CONCAT('00',TRIM(CAST(iMes AS CHAR(2)))),2),'-01') AS DATE);\n" +
                "	set ultDiaMes = Day(LAST_DAY(sMes)); \n" +
                "	set iPos = InStr(Trim(sCampo), 'DV'); \n" +
                "	IF iPos > 0 THEN \n" +
                "		set iDay = CAST(Mid(Trim(sCampo), iPos + 2, 2) AS UNSIGNED Int); \n" +
                "		IF iDay > ultDiaMes THEN \n" +
                "			set rDay = ultDiaMes; \n" +
                "		ELSE \n" +
                "			set rDay = iDay; \n" +
                "		END IF; \n" +
                "	ELSE \n" +
                "		set iDay = CAST(LEFT(sVecto2,2) AS UNSIGNED Int); \n" +
                "		IF iDay > ultDiaMes THEN \n" +
                "			set rDay = ultDiaMes; \n" +
                "			ELSE set rDay = iDay; \n" +
                "		END IF; \n" +
                "	END IF; \n" +
                "	\n" +
                "	RETURN CONCAT(RIGHT(CONCAT('0000',TRIM(CAST(iAno AS CHAR(4)))),4), '-', RIGHT(CONCAT('00',TRIM(CAST(iMes AS CHAR(2)))),2), '-', Right(CONCAT('00',TRIM(CAST(rDay AS CHAR(2)))),2));\n" +
                "END";
            ExecutarComando(sql);
        }

        if ("ALL".equals(qual.toUpperCase()) || "RETAVDATARID2".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`RetAvDataRid2`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`RetAvDataRid2`(sValue Text) RETURNS date " +
                  "BEGIN declare i Int; declare iLen Int; declare iPto Int; declare lRet CHAR(8); declare bLog Boolean; set iLen = LENGTH(UCase(Trim(sValue))); set i = 1; set iPto = 0; set bLog = TRUE; IF iLen > 0 THEN   WHILE bLog DO     IF i >= iLen THEN       set bLog = FALSE;     END IF;     IF iPto = 7 THEN       set bLog = FALSE;     END IF;     IF MID(sValue, i, 1) = ':' THEN       set iPto = iPto + 1;     END IF;     set i = i + 1;   END WHILE;   set lRet = MID(sValue,i - 1,8); END IF; RETURN CAST(CONCAT(MID(lRet, 5, 4), '-', MID(lRet, 3, 2), '-', MID(lRet, 1, 2)) AS DATE); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "RETAVDESCRID2".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`RetAvDescRid2`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`RetAvDescRid2`(sValue Text) RETURNS text CHARSET latin1 " + 
                  "BEGIN declare i Int; declare iLen Int; declare iPto Int; declare lRet Text; declare bLog Boolean; set iLen = LENGTH(UCase(Trim(sValue))); set i = 1; set iPto = 0; set bLog = TRUE; IF iLen > 0 THEN   WHILE bLog DO     IF i >= iLen THEN       set bLog = FALSE;     END IF;     IF iPto = 10 THEN       set bLog = FALSE;     END IF;     IF MID(sValue, i, 1) = ':' THEN       set iPto = iPto + 1;     END IF;     set i = i + 1;   END WHILE;   set lRet = MID(sValue,i - 1); END IF; RETURN MID(lret,1,InStr(lRet,':') - 1); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "RETAVTIPORID2".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`RetAvTipoRid2`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`RetAvTipoRid2`(sValue Text) RETURNS char(3) CHARSET latin1 " + 
                  "BEGIN declare i Int; declare iLen Int; declare iPto Int; declare lRet CHAR(3); declare bLog Boolean; set iLen = LENGTH(UCase(Trim(sValue))); set i = 1; set iPto = 0; set bLog = TRUE; IF iLen > 0 THEN   WHILE bLog DO     IF i >= iLen THEN       set bLog = FALSE;     END IF;     IF iPto = 8 THEN       set bLog = FALSE;     END IF;     IF MID(sValue, i, 1) = ':' THEN       set iPto = iPto + 1;     END IF;     set i = i + 1;   END WHILE;   set lRet = MID(sValue,i - 1,3); END IF; RETURN lRet; END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "RETAVVALORRID2".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`RetAvValorRid2`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`RetAvValorRid2`(sValue Text) RETURNS decimal(10,2) " +
                  "BEGIN declare i Int; declare iLen Int; declare iPto Int; declare lRet CHAR(10); declare bLog Boolean; set iLen = LENGTH(UCase(Trim(sValue))); set i = 1; set iPto = 0; set bLog = TRUE; IF iLen > 0 THEN   WHILE bLog DO     IF i >= iLen THEN       set bLog = FALSE;     END IF;     IF iPto = 2 THEN       set bLog = FALSE;     END IF;     IF MID(sValue, i, 1) = ':' THEN       set iPto = iPto + 1;     END IF;     set i = i + 1;   END WHILE;   set lRet = MID(sValue,i - 1,10); END IF; RETURN CAST(CAST(MID(lRet, 1, 8) AS UNSIGNED INT) + (CAST(MID(lRet, 9, 2) AS UNSIGNED INT) / 100) AS DECIMAL(10,2)); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "STRVAL".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`StrVal`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`StrVal`(sValue Char(10)) RETURNS decimal(10,2) " +
                  "BEGIN declare nRet DECIMAL(10, 2); set nRet = CAST(CONCAT(CAST(Mid(sValue, 1, 8) AS UNSIGNED Int), '.',RIGHT(sValue,2)) AS DECIMAL(10,2)); RETURN nRet; END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "VALSTR".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`ValStr`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION  `" + dataBaseName + "`.`ValStr`(iValue DECIMAL(10,2)) RETURNS char(10) CHARSET latin1 " +
                  "BEGIN declare iValor CHAR(20); declare iCento CHAR(4); set iValor = Right(CONCAT('00000000',CAST(TRUNCATE(iValue,0) AS CHAR(10))),8); set iCento = CAST(((iValue - TRUNCATE(iValue, 0)) * 100) AS CHAR); RETURN CONCAT(iValor, iCento); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "STRDATE".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`StrDate`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`StrDate`(value VARCHAR(10)) RETURNS date " +
                    "BEGIN declare dia CHAR(2); declare mes CHAR(2); declare ano CHAR(4); set dia = MID(value, 1, 2); set mes = MID(value, 4, 2); " +
                    "set ano = MID(value, 7); RETURN CAST(CONCAT(ano,'-',mes,'-',dia) AS DATE); END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "RETAVUSERRID2".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`RetAvUserRid2`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`RetAvUserRid2` (sValue Text) RETURNS text CHARSET latin1 " +
                    "BEGIN " +
                    "RETURN RIGHT(sValue,INSTR(REVERSE(sValue),':') -1); " +
                    "END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toUpperCase()) || "PLUSVAL".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`PLUSVAL`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`plusVal`(sValue Char(10), sTipo Char(3)) RETURNS decimal(10,2) " +
                    "BEGIN " +
                    "	declare nRet DECIMAL(10, 2); " +
                    "	set nRet = StrVal(sValue); " +
                    "	if sTipo = 'DEB' THEN set nRet = nRet * -1; END IF; " +
                    "	RETURN nRet; " +
                    "END";
            ExecutarComando(sql);
        }

        if ("ALL".equals(qual.toUpperCase()) || "ATREMOVE".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`AtRemove`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`AtRemove`(sCampo LONGTEXT) RETURNS longtext CHARSET latin1 " +
                    "BEGIN " +
                    "	DECLARE iPos Int; " +
                    "	DECLARE sCp LONGTEXT; " +
                    "	DECLARE sCpos LONGTEXT; " +
                    "	DECLARE sAux LONGTEXT; " +
                    "	set sCpos = ''; " +
                    "	set sCampo = CONCAT(sCampo,';'); " +
                    "	set iPos = InStr(sCampo, ';'); " +
                    "	WHILE iPos > 0 DO   " +
                    "		set sAux = Mid(sCampo, 1, iPos - 1);   " +
                    "		set sCampo = Mid(sCampo, iPos + 1);   " +
                    "		set sCp = Mid(sAux, 22, 8);   " +
                    "		IF AscII(Mid(sCp, 1, 1)) >= 65 AND AscII(Mid(sCp,1,1)) <= 90 THEN set sCp = ''; END IF;   " +
                    "		if sCp != '' AND InStr(sAux,':AT') > 0 THEN " +
                    "			set sAux = REPLACE(sAux,CONCAT(':',sCp),''); " +
                    "		End If; " +
                    "		set sCpos = CONCAT(sCpos, ';', sAux);   " +
                    "		set iPos = InStr(sCampo, ';'); " +
                    "	END WHILE ; " +
                    "	RETURN MID(sCpos,2); " +
                    "END";
            ExecutarComando(sql);
        }

        if ("ALL".equals(qual.toUpperCase()) || "ATUNUPGRADE".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`AtUnUpgrade`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`AtUnUpgrade`(sCampo LONGTEXT) RETURNS tinyint(1) " +
                    "BEGIN " +
                    "	DECLARE iPos Int; " +
                    "	DECLARE sCp LONGTEXT; " +
                    "	DECLARE sAux LONGTEXT; " +
                    "	DECLARE bRet boolean; " +
                    "	set bRet = false; " +
                    "	set sCampo = CONCAT(sCampo,';'); " +
                    "	set iPos = InStr(sCampo, ';'); " +
                    "	WHILE iPos > 0 DO   " +
                    "		set sAux = Mid(sCampo, 1, iPos - 1);   " +
                    "		set sCampo = Mid(sCampo, iPos + 1);   " +
                    "		set sCp = Mid(sAux, 22, 8);   " +
                    "		IF AscII(Mid(sCp, 1, 1)) >= 65 AND AscII(Mid(sCp,1,1)) <= 90 THEN set sCp = ''; END IF;   " +
                    "		if sCp = '' AND InStr(sAux,':AT') > 0 THEN " +
                    "			set bRet = true; " +
                    "		End If; " +
                    "		set iPos = InStr(sCampo, ';'); " +
                    "	END WHILE ; " +
                    "	RETURN bRet; " +
                    "END";
            ExecutarComando(sql);
        }
        
        if ("ALL".equals(qual.toLowerCase()) || "remove_accents".equals(qual.toUpperCase())) {
            sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`remove_accents`";
            ExecutarComando(sql);
            sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`remove_accents`(textvalue varchar(20000)) RETURNS varchar(20000) CHARSET utf8\n" +
                "BEGIN\n" +
                "	set @textvalue = textvalue;\n" +
                "\n" +
                "	-- ACCENTS\n" +
                "	set @withaccents = 'ŠšŽžÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÑÒÓÔÕÖØÙÚÛÜÝŸÞàáâãäåæçèéêëìíîïñòóôõöøùúûüýÿþƒ';\n" +
                "	set @withoutaccents = 'SsZzAAAAAAACEEEEIIIINOOOOOOUUUUYYBaaaaaaaceeeeiiiinoooooouuuuyybf';\n" +
                "	set @count = length(@withaccents);\n" +
                "\n" +
                "	while @count > 0 do\n" +
                "		set @textvalue = replace(@textvalue, substring(@withaccents, @count, 1), substring(@withoutaccents, @count, 1));\n" +
                "		set @count = @count - 1;\n" +
                "	end while;\n" +
                "\n" +
                "	-- SPECIAL CHARS\n" +
                "	set @special = '!@#$%¨&*()_+=§¹²³£¢¬\"`´{[^~}]<,>.:;?/°ºª+*|\\\\''';\n" +
                "	set @count = length(@special);\n" +
                "	while @count > 0 do\n" +
                "		set @textvalue = replace(@textvalue, substring(@special, @count, 1), '');\n" +
                "		set @count = @count - 1;\n" +
                "	end while;\n" +
                "\n" +
                "	return @textvalue;\n" +
                "END";
            ExecutarComando(sql);
        }

    
    }

    public void Auditor(String cVelho, String cNovo) {
        if (!ExisteTabelaAuditor()) return;
        
        try {
            ExecutarComando("INSERT INTO auditor (usuario, datahora, origem, maquina, velho, novo) VALUES ('" +
            VariaveisGlobais.usuario + "','" + Dates.DateFormata("yyyy-MM-dd hh:mm:ss", new java.util.Date()) +
            "','" + VariaveisGlobais.marca + "','" + VariaveisGlobais.unidade + "','" +
            cVelho.toUpperCase() + "','" + cNovo.toUpperCase() + "')");
        } catch (Exception err) {}        
    }
    
    private boolean ExisteTabelaAuditor() {
        boolean ret = true;
        String sql = "CREATE TABLE `auditor` ( " +
                     "`usuario` varchar(25) DEFAULT NULL, " +
                     "`datahora` datetime DEFAULT NULL, " +
                     "`velho` varchar(255) DEFAULT NULL, " +
                     "`novo` varchar(255) DEFAULT NULL, " +
                     "`origem` varchar(30) DEFAULT NULL, " +
                     "`maquina` varchar(60) DEFAULT NULL " +
                     ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

        try {
            if (!ExistTable("auditor")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaFichas() {
        boolean ret = true;
        String sql = "CREATE TABLE  `FICHAS` (\n" +
        "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
        "  `contrato` varchar(6) DEFAULT NULL,\n" +
        "  `dtvencimento` varchar(10) DEFAULT NULL,\n" +
        "  `anotacoes` longtext,\n" +
        "  PRIMARY KEY (`id`)\n" +
        ") ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='fichas amarelas'";
        
        try {
            if (!ExistTable("FICHAS")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaBloquetos() {
        boolean ret = true;
        String sql = "CREATE TABLE `bloquetos` (\n" +
                    "  `rgprp` varchar(6) DEFAULT NULL,\n" +
                    "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                    "  `contrato` varchar(6) DEFAULT NULL,\n" +
                    "  `nome` varchar(70) DEFAULT NULL,\n" +
                    "  `vencimento` datetime DEFAULT NULL,\n" +
                    "  `valor` varchar(15) DEFAULT NULL,\n" +
                    "  `nnumero` varchar(20) DEFAULT NULL,\n" +
                    "  `remessa` varchar(1) DEFAULT 'N',\n" +
                    ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
        
        try {
            if (!ExistTable("bloquetos")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public boolean ExisteTabelaConcessionarias() {
        boolean ret = true;
        String sql = "CREATE  TABLE `concessionarias` (\n" +
                    "  `codigo` VARCHAR(2) NULL ,\n" +
                    "  `idconta` VARCHAR(3) NULL ,\n" +
                    "  `matricula` VARCHAR(4) NULL ,\n" +
                    "  `valor` VARCHAR(4) NULL ,\n" +
                    "  `vencimento` VARCHAR(4) NULL ,\n" +
                    "  `vctoformato` VARCHAR(4) NULL );";
        
        try {
            if (!ExistTable("Concessionarias")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaataxas() {
        boolean ret = true;
        String sql = "CREATE TABLE `ataxas` (\n" +
                    "  `idataxas` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `tipo` varchar(1) DEFAULT NULL,\n" +
                    "  `matricula` varchar(20) DEFAULT NULL,\n" +
                    "  `vencimento` date DEFAULT NULL,\n" +
                    "  `valor` decimal(10,2) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`idataxas`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("ataxas")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaAntecip() {
        boolean ret = true;
        String sql = "CREATE TABLE `ANTECIPADOS` (\n" +
                     "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                     "  `rgprp` varchar(6) DEFAULT NULL,\n" +
                     "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                     "  `contrato` varchar(6) DEFAULT NULL,\n" +
                     "  `campo` text,\n" +
                     "  `dtvencimento` date DEFAULT NULL,\n" +
                     "  `dtpagamento` date DEFAULT NULL,\n" +
                     "  `at_aut` double DEFAULT NULL,\n" +
                     "  `dtrecebimento` date DEFAULT NULL,\n" +
                     "  `rc_aut` double DEFAULT NULL,\n" +
                     "  PRIMARY KEY (`id`)\n" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("ANTECIPADOS")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaAdAviso() {
        boolean ret = true;
        String sql = "CREATE TABLE `ADAVISOS` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `rgprp` varchar(6) DEFAULT NULL,\n" +
                    "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                    "  `contrato` varchar(6) DEFAULT NULL,\n" +
                    "  `texto` text,\n" +
                    "  `valor` float DEFAULT NULL,\n" +
                    "  `tipo` varchar(1) DEFAULT NULL,\n" +
                    "  `vencimento` date DEFAULT NULL,\n" +
                    "  `data` date DEFAULT NULL,\n" +
                    "  `logado` varchar(20) DEFAULT NULL,\n" +
                    "  `ad_aut` double DEFAULT '0',\n" +
                    "  `et_aut` double DEFAULT '0',\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("ADAVISOS")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public boolean ExisteTabelaiptu() {
        boolean ret = true;
        String sql = "CREATE TABLE `iptu` (\n" +
                    "  `ano` varchar(4) DEFAULT NULL,\n" +
                    "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                    "  `matricula` varchar(20) DEFAULT NULL,\n" +
                    "  `jan` varchar(100) DEFAULT NULL,\n" +
                    "  `fev` varchar(100) DEFAULT NULL,\n" +
                    "  `mar` varchar(100) DEFAULT NULL,\n" +
                    "  `abr` varchar(100) DEFAULT NULL,\n" +
                    "  `mai` varchar(100) DEFAULT NULL,\n" +
                    "  `jun` varchar(100) DEFAULT NULL,\n" +
                    "  `jul` varchar(100) DEFAULT NULL,\n" +
                    "  `ago` varchar(100) DEFAULT NULL,\n" +
                    "  `set` varchar(100) DEFAULT NULL,\n" +
                    "  `out` varchar(100) DEFAULT NULL,\n" +
                    "  `nov` varchar(100) DEFAULT NULL,\n" +
                    "  `dez` varchar(100) DEFAULT NULL,\n" +
                    "  `ord` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  PRIMARY KEY (`ord`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("iptu")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public boolean ExisteTabelaDimob() {
        boolean ret = true;
        String sql = "CREATE TABLE `dimob` (\n" +
                    "  `autonum` double NOT NULL AUTO_INCREMENT,\n" +
                    "  `rgprp` varchar(6) NOT NULL, \n" +
                    "  `rgimv` varchar(6) NOT NULL, \n" +
                    "  `contrato` varchar(6) NOT NULL, \n" +
                    "  `cpfcnpjlocador` varchar(14) NOT NULL,\n" +
                    "  `nomelocador` varchar(60) NOT NULL,\n" +
                    "  `cpfcnpjlocatario` varchar(14) NOT NULL,\n" +
                    "  `nomelocatario` varchar(60) NOT NULL,\n" +
                    "  `numerocontrato` varchar(6) NOT NULL,\n" +
                    "  `datacontrato` varchar(8) NOT NULL,\n" +
                    "  `valorjan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaojan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostojan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfjan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcjan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mujan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jujan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `cojan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tejan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorfev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaofev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostofev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dffev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcfev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mufev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jufev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `cofev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tefev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valormar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaomar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostomar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfmar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcmar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mumar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jumar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `temar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaoabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostoabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `muabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `juabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `coabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `teabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valormai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaomai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostomai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfmai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcmai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mumai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jumai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `temai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorjun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaojun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostojun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfjun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcjun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mujun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jujun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `cojun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tejun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorjul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaojul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostojul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfjul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcjul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mujul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jujul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `cojul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tejul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaoago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostoago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `muago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `juago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `coago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `teago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaoset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostoset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `muset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `juset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `coset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `teset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaoout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostoout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `muout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `juout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `coout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `teout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valornov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaonov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostonov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfnov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcnov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `munov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `junov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `conov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tenov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valordez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaodez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostodez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfdez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcdez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mudez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `judez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `codez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tedez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tipoimovel` varchar(1) NOT NULL DEFAULT 'U',\n" +
                    "  `endimovel` varchar(60) NOT NULL,\n" +
                    "  `cepimovel` varchar(8) NOT NULL,\n" +
                    "  `codmunimovel` varchar(4) NOT NULL,\n" +
                    "  `ufimovel` varchar(2) NOT NULL,\n" +
                    "  PRIMARY KEY (`autonum`)\n" +
                    ") ENGINE=MyISAM AUTO_INCREMENT=9742 DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("dimob")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public boolean ExisteFuncRMVADIANTA() {
        boolean ret = true;
        String sql;
        sql = "DROP FUNCTION IF EXISTS `" + dataBaseName + "`.`RMVADIANTA`";
        ExecutarComando(sql);

        sql = "CREATE DEFINER=`root`@`%` FUNCTION `" + dataBaseName + "`.`rmvAdianta`(sCampo LONGTEXT) RETURNS longtext CHARSET latin1\n" +
              "BEGIN\n" +
              "	declare iPos int;\n" +
              "	declare tCampos LONGTEXT;\n" +
              "	set iPos = InStr(sCampo,':AD');\n" +
              "	set tCampos = MID(sCampo, iPos, 20);\n" +
              "RETURN Replace(sCampo,tCampos,'');\n" +
              "END;";
        try {
            ExecutarComando(sql);
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
}
