/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package retencao;

import java.util.Date;

/**
 *
 * @author supervisor
 */
public class Retencoes {
    private String rgprp;
    private String rgimv;
    private String contrato;
    private String marca;
    private String endi;
    private String descricao;
    private float valor;
    private Date vencimento;
    private Date dtrecebimento;
    private String rc_aut;
    private String rt_aut;
    
    public String getrgprp() { return rgprp; }
    public void setrgprp(String Valor) { this.rgprp = Valor; }
    
    public String getrgimv() { return rgimv; }
    public void setrgimv(String Valor) { this.rgimv = Valor; }
    
    public String getcontrato() { return contrato; }
    public void setcontrato(String Valor) { this.contrato = Valor; }
    
    public String getmarca() { return marca; }
    public void setmarca(String Valor) { this.marca = Valor; }
    
    public String getendi() { return endi; }
    public void setendi(String Valor) { this.endi = Valor; }
    
    public String getdescricao() { return descricao; }
    public void setdescricao(String Valor) { this.descricao = Valor; }
    
    public float getvalor() { return valor; }
    public void setvalor(float Valor) { this.valor = Valor; }
    
    public Date getvencimento() { return vencimento; }
    public void setvencimento(Date Valor) { this.vencimento = Valor; }
    
    public Date getrecebimento() { return dtrecebimento; }
    public void setrecebimento(Date Valor) { this.dtrecebimento = Valor; }
    
    public String getrcaut() { return rc_aut; }
    public void setrcaut(String Valor) { this.rc_aut = Valor; }
    
    public String getrtaut() { return rt_aut; }
    public void setrtaut(String Valor) { this.rt_aut = Valor; }
    
}
