/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author supervisor
 */
public class jTableControl {
    JTable tabela;
    Object[][] dados;
    Integer[] Size;
    String[] colunas;
    Boolean[] edit;
    String[] align;

    public jTableControl(Boolean zebra) {
        if (zebra) {
            //UIManager.put("Table.alternateRowColor", new Color(204,204,255));
            //UIManager.put("Table.foreground", new Color(255, 255, 255));
            UIManager.put("Table.alternateRowColor", new Color(204,204,255));
            UIManager.put("Table.foreground", new Color(0, 0,0));
            UIManager.put("Table.selectionBackground", Color.YELLOW);
            UIManager.put("Table.selectionForeground", Color.RED);
        }
    }

    public void Show(JTable table, Object[][] data, Integer[] tam, String[] algn, String[] col, Boolean[] edt) {
       tabela = table; dados = data; colunas = col; edit = edt; Size = tam; align = algn;

       MyTableModel tableModel = new MyTableModel();
       tabela.setModel(tableModel);

        for (int i=0;i < Size.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(Size[i]);

            if (Size[i] == 0) {
                TableColumn tableColumn = tabela.getColumn(colunas[i]);
                tableColumn.setPreferredWidth(0);
                tableColumn.setMinWidth(0);
                tableColumn.setMaxWidth(0);

                tabela.getTableHeader().setReorderingAllowed(false);
                tabela.getTableHeader().setResizingAllowed(false);
            }
        }

        Alinha();
    }

    private void Alinha() {
        if (align.length > 0) {
            DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
            DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
            DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

            esquerda.setHorizontalAlignment(SwingConstants.LEFT);
            centralizado.setHorizontalAlignment(SwingConstants.CENTER);
            direita.setHorizontalAlignment(SwingConstants.RIGHT);
           //DefaultTableCellRenderer alinha = new DefaultTableCellRenderer();
           for (int i=0; i < align.length; i++) {
               if (align[i].equals("L")) {
                   tabela.getColumnModel().getColumn(i).setCellRenderer(esquerda);
               } else if (align[i].equals("C")) {
                   tabela.getColumnModel().getColumn(i).setCellRenderer(centralizado);
               } else if (align[i].equals("R")) {
                   tabela.getColumnModel().getColumn(i).setCellRenderer(direita);
               }
           }
        }
    }

    public void Repaint() {
       MyTableModel tableModel = new MyTableModel();
       tabela.setModel(tableModel);

        for (int i=0;i < Size.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(Size[i]);

            if (Size[i] == 0) {
                TableColumn tableColumn = tabela.getColumn(colunas[i]);
                tableColumn.setPreferredWidth(0);
                tableColumn.setMinWidth(0);
                tableColumn.setMaxWidth(0);

                tabela.getTableHeader().setReorderingAllowed(false);
                tabela.getTableHeader().setResizingAllowed(false);
            }
        }
    }

    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = colunas;
        private Object[][] data = dados;

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (edit.length > 0) {return edit[col];} else return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    public void Add(Object[] value) {
        Object[][] temp = new Object[dados.length + 1][value.length];
        System.arraycopy(dados, 0, temp, 0, dados.length);
        System.arraycopy(value, 0, temp[dados.length], 0, value.length);
        dados = temp;
        Repaint();
    }

    public void Del(int index) {
        if (dados.length == 0) return;
        Object[][] temp = {};
        for (int i=0; i<dados.length; i++) {
            if (i != index) {
                temp = insert(temp, dados[i]);
            }
        }
        dados = temp;
        Repaint();
    }

    public Object[][] insert(Object[][] Variavel, Object[] value) {
        Object[][] temp = new Object[Variavel.length + 1][value.length];
        System.arraycopy(Variavel, 0, temp, 0, Variavel.length);

        for (int i=0; i<value.length;i++) {
            temp[Variavel.length][i] = value[i];
        }
        return temp;
    }

    public void Clear(JTable table) {
        DefaultTableModel mdl = (DefaultTableModel) table.getModel();
        mdl.setRowCount(0);
        tabela.updateUI();
    }

}
