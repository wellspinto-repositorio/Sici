/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DecriptaNome;

/**
 *
 * @author supervisor
 */
public class DecriptaNome {
    
    static public String Decriptar(String cNome) {
        String cRetorno = "";
        String auxVar;
        int nConta;

        auxVar = cNome.trim().toUpperCase();
        if (auxVar.length() > 0) {
            for (nConta = 1; nConta <= auxVar.length(); nConta += 2) {
                cRetorno += (char) Integer.parseInt(auxVar.substring(nConta - 1, (nConta - 1) + 2));
            }

            cRetorno = cRetorno.substring(0,1).toUpperCase() + cRetorno.substring(1).toLowerCase();
        }

        return ("".equals(cRetorno.trim()) ? null : cRetorno);
    }
}

