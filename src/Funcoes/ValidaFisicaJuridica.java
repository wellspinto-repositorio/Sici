/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author supervisor
 */
  public class ValidaFisicaJuridica extends InputVerifier {

       private JTextField textfield;
       private JFormattedTextField mCpf;
       private JFormattedTextField mCnpj;
       private boolean result = true;

       public ValidaFisicaJuridica(JTextField textfield, JFormattedTextField mCpf, JFormattedTextField mCnpj) {
           this.textfield = textfield;
           this.mCpf = mCpf;
           this.mCnpj = mCnpj;
           
       }

       @Override
       public boolean verify(JComponent arg0) {
           System.out.println(this.textfield.getText());

           result = true;
           String mFJ = this.textfield.getText().trim().toUpperCase();
           boolean badFormat = false;

           badFormat = !(mFJ.contains("F") || mFJ.contains("J"));
           if ( badFormat ) {
               JOptionPane.showMessageDialog(null,
                   "Campo só pode ser 'F' ou 'J'", "Erro",
                   JOptionPane.ERROR_MESSAGE);
               result = false;

           } else {
               result = true;

               if (mFJ.contains("F")) {
                   this.mCpf.setVisible(true);
                   this.mCnpj.setVisible(false);
               } else {
                   this.mCpf.setVisible(false);
                   this.mCnpj.setVisible(true);
               }
//                MaskFormatter defaultMask = null;
//                MaskFormatter displayMask = null;
//                MaskFormatter editMask = null;
//                if (mFJ.contains("F")) {
//                    try {
//                        defaultMask = new MaskFormatter("###.###.###-##");
//                        displayMask = new MaskFormatter("###.###.###-##");
//                        editMask = new MaskFormatter("###.###.###-##");
//                    } catch (ParseException ex) {
//                        ex.printStackTrace();
//                    }
//                } else {
//                    try {
//                        defaultMask = new MaskFormatter("##.###.###/####-##");
//                        displayMask = new MaskFormatter("##.###.###/####-##");
//                        editMask = new MaskFormatter("##.###.####/###-##");
//                    } catch (ParseException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                defaultMask.setValueClass(String.class);
//                displayMask.setValueClass(String.class);
//                editMask.setValueClass(String.class);
//
//               DefaultFormatterFactory mcpfCnpjMask =
//                   new DefaultFormatterFactory(defaultMask,displayMask,editMask);
//               this.formattedfield.setFormatterFactory(mcpfCnpjMask);

           }

           return result;

       }
   }