/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package boleta;

import java.util.Date;

/**
 *
 * @author supervisor
 */
public class Baixa {
    private String rgprp;
    private String nomep;
    private String contrato;
    private String nomei;
    private String nnumero;
    private Date vencimento;
    private Date pagamento;
    private float vrboleta;
    private float vrrecebido;
    private float diferenca;
    
    public String getrgprp() { return rgprp; }
    public void setrgprp(String Valor) { this.rgprp = Valor; }
    
    public String getnomep() { return nomep; }
    public void setnomep(String Valor) { this.nomep = Valor; }
    
    public String getcontrato() { return contrato; }
    public void setcontrato(String Valor) { this.contrato = Valor; }
    
    public String getnomei() { return nomei; }
    public void setnomei(String Valor) { this.nomei = Valor; }
    
    public String getnnumero() { return nnumero; }
    public void setnnumero(String Valor) { this.nnumero = Valor; }
    
    public Date getvencimento() { return vencimento; }
    public void setvencimento(Date Valor) { this.vencimento = Valor; }
    
    public Date getpagamento() { return pagamento; }
    public void setpagamento(Date Valor) { this.pagamento = Valor; }
    
    public float getvrboleta() { return  vrboleta; }
    public void setvrboleta(float Valor) { this.vrboleta = Valor; }
    
    public float getvrrecebido() { return  vrrecebido; }
    public void setvrrecebido(float Valor) { this.vrrecebido = Valor; }
    
    public float getdiferenca() { return  diferenca; }
    public void setdiferenca(float Valor) { this.diferenca = Valor; }
}
