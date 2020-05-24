/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.net.MalformedURLException;
import java.sql.SQLException;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author supervisor
 */
public class JEmail {
    DbMain conn = VariaveisGlobais.conexao;
    String jEmailEmp = ""; String jSenhaEmail = ""; boolean jPop = false; boolean jAutentica = false;
    String jEndPopImap = ""; String jPortPopImap = ""; String jSmtp = ""; String jPortSmtp = "";
    String jAssunto = ""; String jMsgEmail = ""; String jFTP_Conta = ""; String jFTP_Porta = "";
    String jFTP_Usuario = ""; String jFTP_Senha = ""; 

    public String SendEmail(String contrato, String[] anexo, Object SubJect, Object MSG) throws MalformedURLException, SQLException {
        try {
            this.LerEmailSettings();
        } catch (SQLException var16) {
            //Logger.getLogger(jViewDoctos.class.getName()).log(Level.SEVERE, (String)null, var16);
        }

        String[][] EmailLocaDados = (String[][])null;
        String EmailLoca = null;
        String LocaNome = null;
        if(LerValor.isNumeric(contrato)) {
            EmailLocaDados = this.conn.LerCamposTabela(new String[]{"nomerazao", "email"}, "locatarios", "contrato = \'" + contrato + "\'");
            EmailLoca = EmailLocaDados[1][3];
            LocaNome = EmailLocaDados[0][3];
        } else {
            EmailLoca = contrato;
            LocaNome = contrato;
        }

        String retorno = "";

        try {
            HtmlEmail ex = new HtmlEmail();
            //MultiPartEmail ex = new MultiPartEmail();
            ex.setHostName(this.jSmtp.trim());
            ex.setSmtpPort(Integer.valueOf(this.jPortSmtp).intValue());
            ex.setAuthenticator(new DefaultAuthenticator(this.jEmailEmp.trim(), this.jSenhaEmail.trim()));
            ex.setTLS(true);
            ex.setSSL(false);
            ex.setFrom(this.jEmailEmp.trim(), this.jEmailEmp.trim());
            if(SubJect != null) {
                ex.setSubject((String)SubJect);
            } else {
                ex.setSubject("Boleto " + anexo);
            }

            //ex.setContent();
            if(MSG != null) {
                ex.setHtmlMsg((String)MSG);
            } else {
                ex.setHtmlMsg(this.jAssunto.trim());
            }

            ex.setDebug(true);
            ex.getMailSession().getProperties().put("mail.smtp.auth", "true");
            ex.getMailSession().getProperties().put("mail.debug", "true");
            ex.getMailSession().getProperties().put("mail.smtp.port", this.jPortSmtp);
            ex.getMailSession().getProperties().put("mail.smtp.socketFactory.port", this.jPortSmtp);
            ex.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
            String[] aEmailLoca = EmailLoca.split(",");
            String[] var11 = aEmailLoca;
            int var12 = aEmailLoca.length;

            int var13;
            String anx;
            for(var13 = 0; var13 < var12; ++var13) {
                anx = var11[var13];
                ex.addTo(anx.trim(), LocaNome);
            }

            var11 = anexo;
            var12 = anexo.length;

            for(var13 = 0; var13 < var12; ++var13) {
                anx = var11[var13];
                EmailAttachment attachment = new EmailAttachment();
                attachment.setPath(anx);
                attachment.setDisposition("attachment");
                attachment.setDescription(anx);
                attachment.setName(anx);
                ex.attach(attachment);
            }

            ex.send();
        } catch (Exception var17) {
            //var17.printStackTrace();
            retorno = var17.getMessage();
        }

        return retorno;
    }

    private void LerEmailSettings() throws SQLException {
        jEmailEmp = conn.LerParametros("EMAIL");
        jSenhaEmail = conn.LerParametros("EMAILSENHA");
        jPop = ("TRUE".equals(conn.LerParametros("POP")) ? true : false);
        jAutentica = ("TRUE".equals(conn.LerParametros("EMAILAUTENTICA")) ? true : false);
        jEndPopImap = conn.LerParametros("POPIMAP");
        jPortPopImap = conn.LerParametros("POPIMAPPORT");
        jSmtp = conn.LerParametros("SMTP");
        jPortSmtp = conn.LerParametros("SMTPPORT");

        jFTP_Conta = conn.LerParametros("FTPCONTA");
        jFTP_Porta = conn.LerParametros("FTPPORTA");
        jFTP_Usuario = conn.LerParametros("FTPUSUARIO");
        jFTP_Senha = conn.LerParametros("FTPSENHA");
    }
    
}
