/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
/**
 *
 * @author softelet
 */
public class ManipuladorArquivo {
    public static String leitor(String path) throws IOException {
        BufferedReader buffRead = new BufferedReader(new FileReader(path));
        String retorno = "";
        String linha = "";
        while (true) {
            if (linha != null) {
                //System.out.println(linha);
                retorno += linha;
            } else
                break;
            linha = buffRead.readLine();
        }
        buffRead.close();
        
        return retorno;
    }
 
    public static void escritor(String path, String texto) throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(path));
        String linha = texto;
        //Scanner in = new Scanner(System.in);
        //System.out.println("Escreva algo: ");
        //linha = in.nextLine();
        buffWrite.append(linha);
        buffWrite.close();
    }    
}
