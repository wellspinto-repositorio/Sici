/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento.BoletasCentral;

import java.util.List;

/**
 *
 * @author softelet
 */
public class BancosBoleta {
    private String banco;
    private String nomeBanco;
    private List<PessoasBoleta> pessoasBoleta;
    private boolean tag;
    
    public BancosBoleta(String banco, String nomeBanco, List<PessoasBoleta> pessoasBoleta) {
        super();
        this.banco = banco;
        this.nomeBanco = nomeBanco;
        this.pessoasBoleta = pessoasBoleta;
    }
    
    public BancosBoleta(String banco, String nomeBanco, List<PessoasBoleta> pessoasBoleta, boolean tag) {
        super();
        this.banco = banco;
        this.nomeBanco = nomeBanco;
        this.pessoasBoleta = pessoasBoleta;
        this.tag = tag;
    }
    
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    
    public String getNomeBanco() { return nomeBanco; }
    public void setNomeBanco(String banco) { this.nomeBanco = nomeBanco; }
    
    public List<PessoasBoleta> getPessoasBoleta() { return pessoasBoleta; }
    public void setPessoasBoleta(List<PessoasBoleta> pessoasBoleta) { this.pessoasBoleta = pessoasBoleta; }
    
    public boolean getTag() { return tag; }
    public void setTag(boolean tag) { this.tag = tag; }
}
