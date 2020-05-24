/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento.BoletasCentral;

/**
 *
 * @author softelet
 */
public class PessoasBoleta {
    private String contrato;
    private String nome;
    private String vencimentoRec;
    private String vencimentoBol;
    private String tipoEnvio;
    private String rgprp;
    private String rgimv;
    private boolean tag;
    private String nnumero;
    
    public PessoasBoleta(String contrato, String nome, String vencimentoRec, String vencimentoBol, String tipoEnvio, String rgprp, String rgimv, boolean tag) {
        super();
        this.contrato = contrato;
        this.nome = nome;
        this.vencimentoRec = vencimentoRec;
        this.vencimentoBol = vencimentoBol;
        this.tipoEnvio = tipoEnvio;
        this.rgprp = rgprp;
        this.rgimv = rgimv;
        this.tag = tag;
    }
    
    public PessoasBoleta(String contrato, String nome, String vencimentoRec, String vencimentoBol, String tipoEnvio, String rgprp, String rgimv, boolean tag, String nnumero) {
        super();
        this.contrato = contrato;
        this.nome = nome;
        this.vencimentoRec = vencimentoRec;
        this.vencimentoBol = vencimentoBol;
        this.tipoEnvio = tipoEnvio;
        this.rgprp = rgprp;
        this.rgimv = rgimv;
        this.tag = tag;
        this.nnumero = nnumero;
    }

    public String getContrato() { return contrato; }
    public void setContrato(String contrato) { this.contrato = contrato; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getVencimentoRec() { return vencimentoRec; }
    public void setVencimentoRec(String vencimentoRec) { this.vencimentoRec = vencimentoRec; }
    
    public String getVencimentoBol() { return vencimentoBol; }
    public void setVencimentoBol(String vencimentoBol) { this.vencimentoBol = vencimentoBol; }
    
    public String getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(String tipoEnvio) { this.tipoEnvio = tipoEnvio; }
    
    public String getRgprp() { return rgprp; }
    public void setRgprp(String rgprp) { this.rgprp = rgprp; }
    
    public String getRgimv() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv = rgimv; }

    public boolean getTag() { return tag; }
    public void setTag(boolean tag) { this.tag = tag; }
    
    public String getNnumero() { return nnumero; }
    public void setNnumero(String nnumero) { this.nnumero = nnumero; }
}
