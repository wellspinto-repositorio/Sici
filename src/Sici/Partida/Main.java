/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Sici.Partida;

import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.Settings;
import Funcoes.VariaveisGlobais;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.UIManager;

/**
 *
 * @author supervisor
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Timer t = new Timer();
        t.schedule(new RemindTask(), 60 * 1000);
        
        try {
            UIManager.setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel");           
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(jLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    
        LerSettings();

        t.cancel();
        
        (new jLogin(null, true)).main(new String[] {""});        
    }
    
    private static class RemindTask extends TimerTask {
        public void run() {
            System.exit(0);
        }
    }

    private static void LerSettings() {
        // Settings
        new Settings();

        VariaveisGlobais.LerConf();
        
        String[] _host = null;
        if (!"".equals(VariaveisGlobais.unidade)) {
            _host = VariaveisGlobais.unidade.split(",");
            if (_host.length > 1) {
                VariaveisGlobais.unidade = _host[0];
                VariaveisGlobais.dbnome = _host[1];
                VariaveisGlobais.dbsenha = Boolean.valueOf(_host[2]);
            }
        }
        
        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ObjectsAdd(VariaveisGlobais.unidades, new Object[]{VariaveisGlobais.unidade,VariaveisGlobais.dbnome,VariaveisGlobais.dbsenha});
        for (int w=1;w<=99;w++) {
            VariaveisGlobais.remoto1 = System.getProperty("remoto" + LerValor.FormatPattern(String.valueOf(w), "#0"), "");
            
            String[] _host1 = null;
            if (!"".equals(VariaveisGlobais.remoto1)) {
                _host1 = VariaveisGlobais.remoto1.split(",");
                if (_host1.length > 1) {
                    VariaveisGlobais.remoto1  = _host1[0];
                    VariaveisGlobais.dbnome1  = _host1[1];
                    VariaveisGlobais.dbsenha1 = Boolean.valueOf(_host1[2]);
                }
            }
            if (!"".equals(VariaveisGlobais.remoto1)) VariaveisGlobais.unidades = FuncoesGlobais.ObjectsAdd(VariaveisGlobais.unidades, new Object[]{VariaveisGlobais.remoto1,VariaveisGlobais.dbnome1,VariaveisGlobais.dbsenha1});            
        }
    }
}
