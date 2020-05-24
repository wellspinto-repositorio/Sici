/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

/**
 *
 * @author supervisor
 */
import static Funcoes.PrinterPOS.backlashReplace;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.sql.SQLException;
import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class MyFTP {
    public static void main( String[] args ) {
        DbMain conn = null;
        String jFTP_Conta = ""; String jFTP_Port = "";
        String jFTP_Usuario = ""; String jFTP_Senha = "";
        try {
            conn = new DbMain(VariaveisGlobais.unidade,"root",(VariaveisGlobais.dbsenha ? "7kf51b" : ""),VariaveisGlobais.dbnome);

            jFTP_Conta = conn.LerParametros("FTPCONTA");
            jFTP_Port = conn.LerParametros("FTPPORTA");
            jFTP_Usuario = conn.LerParametros("FTPUSUARIO");
            jFTP_Senha = conn.LerParametros("FTPSENHA");
        } catch (SQLException e) {e.printStackTrace();}
        conn.FecharConexao();
        
        String nomeArquivo = null;
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect( jFTP_Conta );
           
            //verifica se conectou com sucesso!
            if( FTPReply.isPositiveCompletion( ftp.getReplyCode() ) ) {
                ftp.login( jFTP_Usuario, jFTP_Senha );
            } else {
                //erro ao se conectar
                ftp.disconnect();
                
                System.out.println("Conexão recusada");
                System.exit(1);
            }
           
            //para cada arquivo informado...
            for( int i=0; i<args.length; i++ ) {
                //pega apenas o nome do arquivo
                int idx = args[i].lastIndexOf(File.separator);
                if( idx < 0 ) idx = 0;
                else idx++;
                nomeArquivo = args[i].substring( idx, args[i].length() );
               
                //abre um stream com o arquivo a ser enviado
                InputStream is = new FileInputStream( args[i] );

                nomeArquivo = nomeArquivo.replaceAll(" ", "_");

                //ajusta o tipo do arquivo a ser enviado
                if( args[i].endsWith(".txt") ) {
                    ftp.setFileType( FTPClient.ASCII_FILE_TYPE );
                } else if( args[i].endsWith(".jpg") ) {
                    ftp.setFileType( FTPClient.BINARY_FILE_TYPE );
                } else if( args[i].endsWith(".pdf") ) {
                    ftp.setFileType( FTPClient.BINARY_FILE_TYPE );
                } else {
                    ftp.setFileType( FTPClient.ASCII_FILE_TYPE );
                }
                System.out.println("Enviando arquivo " + nomeArquivo + " ...");
                
                //faz o envio do arquivo
                ftp.storeFile(nomeArquivo, is );

                System.out.println("Arquivo " + nomeArquivo + " enviado com sucesso!");
                JOptionPane.showMessageDialog(null, "Arquivo " + nomeArquivo + " enviado com sucesso!", "Atenção", JOptionPane.INFORMATION_MESSAGE);               
            }
           
            ftp.disconnect();
            System.out.println("Fim. Tchau!");
        } catch( Exception e ) {
            System.out.println("Ocorreu um erro: "+e);
            JOptionPane.showMessageDialog(null, "Ocorreu um erro: " + e + "\n\nAvise ao programador.", "Atenção", JOptionPane.INFORMATION_MESSAGE);               
            
            System.exit(1);
        }
    }
    
    public static boolean FTPDel( String[] args  ) {
        boolean sucesso = false;
        DbMain conn = null;
        String jFTP_Conta = ""; String jFTP_Port = "";
        String jFTP_Usuario = ""; String jFTP_Senha = "";
        try {
            conn = new DbMain(VariaveisGlobais.unidade,"root",(VariaveisGlobais.dbsenha ? "7kf51b" : ""),VariaveisGlobais.dbnome);

            jFTP_Conta = conn.LerParametros("FTPCONTA");
            jFTP_Port = conn.LerParametros("FTPPORTA");
            jFTP_Usuario = conn.LerParametros("FTPUSUARIO");
            jFTP_Senha = conn.LerParametros("FTPSENHA");
        } catch (SQLException e) {e.printStackTrace();}
        conn.FecharConexao();

        String nomeArquivo = null;
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect( jFTP_Conta );
           
            //verifica se conectou com sucesso!
            if( FTPReply.isPositiveCompletion( ftp.getReplyCode() ) ) {
                ftp.login( jFTP_Usuario, jFTP_Senha );
            } else {
                //erro ao se conectar
                ftp.disconnect();
                System.out.println("Conexão recusada");
                sucesso = false;
            }
           
            if (args.length != 0) {
                //para cada arquivo informado...
                for( int i=0; i<args.length; i++ ) {
                    nomeArquivo = args[i];
                    System.out.println("Removendo arquivo "+nomeArquivo+"...");

                    //faz a exclusao do arquivo
                    ftp.deleteFile( nomeArquivo );
                    System.out.println("Arquivo "+nomeArquivo+" removido com sucesso!");
                }
            } else {
                String ftpUsuario = ""; int ftpUsuarioPos = -1;
                ftpUsuarioPos = jFTP_Usuario.indexOf("@");
                if (ftpUsuarioPos < 0) {
                    ftpUsuario = jFTP_Usuario;
                } else {
                    ftpUsuario = jFTP_Usuario.substring(0, ftpUsuarioPos - 1);
                }
                ftp.dele(ftpUsuario);
            }            
           
            ftp.disconnect();
            System.out.println("Fim. Tchau!");
            sucesso = true;
        } catch( Exception e ) {
            System.out.println("Ocorreu um erro: "+e);
            sucesso = false;
        }        
        
        return sucesso;
    }

    public static boolean FTPRetrive( String[] args  ) {
        boolean sucesso = false;
        DbMain conn = null;
        String jFTP_Conta = ""; String jFTP_Port = "";
        String jFTP_Usuario = ""; String jFTP_Senha = "";
        try {
            conn = new DbMain(VariaveisGlobais.unidade,"root",(VariaveisGlobais.dbsenha ? "7kf51b" : ""),VariaveisGlobais.dbnome);

            jFTP_Conta = conn.LerParametros("FTPCONTA");
            jFTP_Port = conn.LerParametros("FTPPORTA");
            jFTP_Usuario = conn.LerParametros("FTPUSUARIO");
            jFTP_Senha = conn.LerParametros("FTPSENHA");
        } catch (SQLException e) {e.printStackTrace();}
        conn.FecharConexao();

        String nomeArquivo = null;
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect( jFTP_Conta );

            //verifica se conectou com sucesso!
            if( FTPReply.isPositiveCompletion( ftp.getReplyCode() ) ) {
                ftp.login( jFTP_Usuario, jFTP_Senha );
            } else {
                //erro ao se conectar
                ftp.disconnect();
                System.out.println("Conexão recusada");
                sucesso = false;
            }

            //para cada arquivo informado...
            for( int i=0; i<args.length; i++ ) {
                nomeArquivo = args[i];
                System.out.println("Baixando arquivo "+nomeArquivo+"...");

                //baixa arquivo
                ftp.setFileType( FTPClient.BINARY_FILE_TYPE );
                OutputStream os = new FileOutputStream(nomeArquivo);
                ftp.retrieveFile(nomeArquivo, os );
                System.out.println("Arquivo "+nomeArquivo+" baixado com sucesso!");
            }

            ftp.disconnect();
            System.out.println("Fim. Tchau!");
            sucesso = true;
        } catch( Exception e ) {
            System.out.println("Ocorreu um erro: "+e);
            sucesso = false;
        }

        return sucesso;
    }

    public static String FTPList( String[] args  ) {
        String sucesso = "";
        DbMain conn = null;
        String jFTP_Conta = ""; String jFTP_Port = "";
        String jFTP_Usuario = ""; String jFTP_Senha = "";
        try {
            conn = new DbMain(VariaveisGlobais.unidade,"root",(VariaveisGlobais.dbsenha ? "7kf51b" : ""),VariaveisGlobais.dbnome);

            jFTP_Conta = conn.LerParametros("FTPCONTA");
            jFTP_Port = conn.LerParametros("FTPPORTA");
            jFTP_Usuario = conn.LerParametros("FTPUSUARIO");
            jFTP_Senha = conn.LerParametros("FTPSENHA");
        } catch (SQLException e) {e.printStackTrace();}
        conn.FecharConexao();

        String nomeArquivo = null;
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect( jFTP_Conta );

            //verifica se conectou com sucesso!
            if( FTPReply.isPositiveCompletion( ftp.getReplyCode() ) ) {
                System.out.println("Conectando...");
                ftp.login( jFTP_Usuario, jFTP_Senha );
            } else {
                //erro ao se conectar
                ftp.disconnect();
                System.out.println("Conexão recusada");
                sucesso = "";
            }

            //para cada arquivo informado...
            for( int i=0; i<args.length; i++ ) {
                nomeArquivo = args[i];
                System.out.println("Listando arquivo " + nomeArquivo + "...");

                String ftpUsuario = ""; int ftpUsuarioPos = -1;
                ftpUsuarioPos = jFTP_Usuario.indexOf("@");
                if (ftpUsuarioPos < 0) {
                    ftpUsuario = jFTP_Usuario;
                } else {
                    ftpUsuario = jFTP_Usuario.substring(0, ftpUsuarioPos - 1);
                }
                ftp.changeWorkingDirectory(ftpUsuario);
                FTPFile[] files = ftp.listFiles();
                for( i=0; i<files.length; i++ ) {
                    if (files[i].getName().contains(nomeArquivo)) {
                        sucesso =  files[i].getTimestamp().getTime().toString();
                        break;
                    }
                }

                if (!"".equals(sucesso.trim())) {System.out.println("Arquivo "+nomeArquivo+" listado com sucesso!");} else {System.out.println("Arquivo "+nomeArquivo+" não encontrado!");}
            }

            ftp.disconnect();
            System.out.println("Fim. Tchau!");
        } catch( Exception e ) {
            System.out.println("Ocorreu um erro: "+e);
            sucesso = "";
        }

        return sucesso;
    }

    public static String FTPExist( String[] args  ) {
        String sucesso = "";
        DbMain conn = null;
        String jFTP_Conta = ""; String jFTP_Port = "";
        String jFTP_Usuario = ""; String jFTP_Senha = "";
        try {
            conn = new DbMain(VariaveisGlobais.unidade,"root",(VariaveisGlobais.dbsenha ? "7kf51b" : ""),VariaveisGlobais.dbnome);

            jFTP_Conta = conn.LerParametros("FTPCONTA");
            jFTP_Port = conn.LerParametros("FTPPORTA");
            jFTP_Usuario = conn.LerParametros("FTPUSUARIO");
            jFTP_Senha = conn.LerParametros("FTPSENHA");
        } catch (SQLException e) {e.printStackTrace();}
        conn.FecharConexao();

        String nomeArquivo = null;
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect( jFTP_Conta );

            //verifica se conectou com sucesso!
            if( FTPReply.isPositiveCompletion( ftp.getReplyCode() ) ) {
                System.out.println("Conectando...");
                ftp.login( jFTP_Usuario, jFTP_Senha );
            } else {
                //erro ao se conectar
                ftp.disconnect();
                System.out.println("Conexão recusada");
                sucesso = "";
            }

            //para cada arquivo informado...
            for( int i=0; i<args.length; i++ ) {
                nomeArquivo = args[i];
                System.out.println("Listando arquivo " + nomeArquivo + "...");

                String ftpUsuario = ""; int ftpUsuarioPos = -1;
                ftpUsuarioPos = jFTP_Usuario.indexOf("@");
                if (ftpUsuarioPos < 0) {
                    ftpUsuario = jFTP_Usuario;
                } else {
                    ftpUsuario = jFTP_Usuario.substring(0, ftpUsuarioPos - 1);
                }
                ftp.changeWorkingDirectory(ftpUsuario);
                FTPFile[] files = ftp.listFiles();
                for( i=0; i<files.length; i++ ) {
                    if (files[i].getName().contains(nomeArquivo)) {
                        sucesso =  files[i].getName();
                        break;
                    }
                }

                if (!"".equals(sucesso.trim())) {System.out.println("Arquivo "+nomeArquivo+" listado com sucesso!");} else {System.out.println("Arquivo "+nomeArquivo+" não encontrado!");}
            }

            ftp.disconnect();
            System.out.println("Fim. Tchau!");
        } catch( Exception e ) {
            System.out.println("Ocorreu um erro: "+e);
            sucesso = "";
        }

        return sucesso;
    }
}