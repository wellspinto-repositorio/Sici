/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author supervisor
 */
public class TableControl {

    public TableControl() {}

    /**
     * Cria e define cabecario em um jTable
     * @param table
     * @param aheader
     */
    public static void header(JTable table, String aheader[][]) {
        DefaultTableModel tableModel = new DefaultTableModel(new Object [][] { }, aheader[0]) {
            @Override
            public Class getColumnClass(int column) {
               Class returnValue = Object.class;
               try {
               if ((column >= 0) && (column < getColumnCount())) {
                 returnValue = getValueAt(0, column).getClass();
               } else {
                 returnValue = Object.class;
               }
               } catch (Exception ex) {}
               return returnValue;
             }
             @Override
             public boolean isCellEditable(int row, int column) {
               //all cells false
               return false;
             }
        };

        //table.setModel(new javax.swing.table.DefaultTableModel(new Object [][] { }, aheader[0]){});
        table.setModel(tableModel);
        for (int i=0;i <= (aheader[1].length - 1); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(Integer.parseInt(aheader[1][i]));

            if (Integer.parseInt(aheader[1][i]) == 0) {
                TableColumn tableColumn = table.getColumn(aheader[0][i]);
                tableColumn.setPreferredWidth(0);
                tableColumn.setMinWidth(0);
                tableColumn.setMaxWidth(0);

                table.getTableHeader().setReorderingAllowed(false);
                table.getTableHeader().setResizingAllowed(false);
                //((JTextArea)table.getEditorComponent()).setEditable(false);
            }
        }

    }

    public static void add(JTable table, String itens[][], boolean zebra){
        javax.swing.table.DefaultTableModel dtm =
        (javax.swing.table.DefaultTableModel)table.getModel();
        //lembre-se um "" para cada coluna na tabela
        dtm.addRow(itens[0]);

        if (zebra) {
            UIManager.put("Table.alternateRowColor", new Color(204,204,255));
            UIManager.put("Table.foreground", new Color(0, 0, 0));  //new Color(255, 255, 255)
            //UIManager.put("Table.selectionBackground", Color.YELLOW);
            //UIManager.put("Table.selectionForeground", Color.RED);
        }

        if (itens[1].length > 0) {
            DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
            DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
            DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

            esquerda.setHorizontalAlignment(SwingConstants.LEFT);
            centralizado.setHorizontalAlignment(SwingConstants.CENTER);
            direita.setHorizontalAlignment(SwingConstants.RIGHT);
           //DefaultTableCellRenderer alinha = new DefaultTableCellRenderer();
           for (int i=0; i <= itens[1].length - 1; i++) {
               if (itens[1][i].equals("L")) {
                   table.getColumnModel().getColumn(i).setCellRenderer(esquerda);
               } else if (itens[1][i].equals("C")) {
                   table.getColumnModel().getColumn(i).setCellRenderer(centralizado);
               } else if (itens[1][i].equals("R")) {
                   table.getColumnModel().getColumn(i).setCellRenderer(direita);
               }               
           }
        }
    }

    public static void insert(JTable table, Object itens[][], boolean zebra){
        javax.swing.table.DefaultTableModel dtm =
        (javax.swing.table.DefaultTableModel)table.getModel();
        //lembre-se um "" para cada coluna na tabela
        dtm.addRow(itens);

        if (zebra) {
            UIManager.put("Table.alternateRowColor", new Color(204,204,255));
            UIManager.put("Table.foreground", new Color(0, 0, 0));
            //UIManager.put("Table.selectionBackground", Color.YELLOW);
            //UIManager.put("Table.selectionForeground", Color.RED);
        }

//        if (itens[1].length > 0) {
//            DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
//            DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
//            DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
//
//            esquerda.setHorizontalAlignment(SwingConstants.LEFT);
//            centralizado.setHorizontalAlignment(SwingConstants.CENTER);
//            direita.setHorizontalAlignment(SwingConstants.RIGHT);
//           //DefaultTableCellRenderer alinha = new DefaultTableCellRenderer();
//           for (int i=0; i <= itens[1].length - 1; i++) {
//               if (itens[1][i].equals("L")) {
//                   table.getColumnModel().getColumn(i).setCellRenderer(esquerda);
//               } else if (itens[1][i].equals("C")) {
//                   table.getColumnModel().getColumn(i).setCellRenderer(centralizado);
//               } else if (itens[1][i].equals("R")) {
//                   table.getColumnModel().getColumn(i).setCellRenderer(direita);
//               }
//           }
//        }
    }

    public static int seek(JTable table, int coluna, String buscar) {
        int retorno = -1;
        for (int i=0;i<table.getRowCount();i++) {
            String vrTable = ((String) table.getValueAt(i, coluna));
            if (vrTable.contains(buscar)) {
                retorno = i;
                break;
            }
        }
        return retorno;
    }

    public static boolean  del(JTable table, int row) {
        boolean retorno = false;
        if (row > table.getRowCount()) retorno = false; else retorno = true;
        try {((DefaultTableModel)table.getModel()).removeRow(row);} catch (Exception ex) {retorno = false;}
        return retorno;
    }

    public static boolean  delall(JTable table) {
        boolean retorno = false;
        for (int z=0;z<table.getRowCount();z++) {if (!del(table, z)) {retorno = false; break;} else retorno = true; }
        return retorno;
    }

    public static boolean  alt(JTable table, Object value, int row, int col) {
        boolean retorno = false;
        if (row > table.getRowCount()) retorno = false; else retorno = true;
        if (col > table.getColumnCount()) retorno = false; else retorno = true;
        try { table.setValueAt(value, row, col); } catch (Exception ex) {retorno = false;}
        return retorno;
    }
    // Aqui entra classe que remove item, altera conteudo de uma celula em uma linha, procura item

    public static void Clear(JTable table) {
        DefaultTableModel mdl = (DefaultTableModel) table.getModel();
        mdl.setRowCount(0);
        table.updateUI();
    }
}
