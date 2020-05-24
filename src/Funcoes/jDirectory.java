/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.io.File;

/**
 *
 * @author samic
 */
public class jDirectory {
    public jDirectory(String pathName) {
        // Cria o diretorio se necessário
        if (!new File(pathName).exists()) {
            boolean sucess = new File(pathName).mkdirs();
            if (!sucess) { System.out.println("Não consegui criar " + pathName); System.exit(1); }
        }
}
}
