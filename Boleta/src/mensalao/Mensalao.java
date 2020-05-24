/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mensalao;

/**
 *
 * @author supervisor
 */
public class Mensalao {
    private String rgprp;
    private String nome;
    private String cpfcnpj;
    private float comercial;
    private float residencial;
    private float nresidencial;
    private float irrf;

    public String getrgprp() { return rgprp; }
    public void setrgprp(String Valor) { this.rgprp = Valor; }
    
    public String getnome() { return nome; }
    public void setnome(String Valor) { this.nome = Valor; }

    public String getcpfcnpj() { return cpfcnpj; }
    public void setcpfcnpj(String Valor) { this.cpfcnpj = Valor; }

    public float getcomercial() { return comercial; }
    public void setcomercial(float Valor) { this.comercial = Valor; }

    public float getresidencial() { return residencial; }
    public void setresidencial(float Valor) { this.residencial = Valor; }

    public float getnresidencial() { return nresidencial; }
    public void setnresidencial(float Valor) { this.nresidencial = Valor; }

    public float getirrfl() { return irrf; }
    public void setirrf(float Valor) { this.irrf = Valor; }
}
