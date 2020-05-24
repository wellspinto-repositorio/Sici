/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author supervisor
 */
public class newTable {
    public newTable() {}
    
    public static void InitTable(JTable table, String[] headers, int[] widths, String[] align, boolean zebra) {
        final DefaultTableModel aModel = new DefaultTableModel(headers, 0) {  
            @Override  
            public Class getColumnClass(int column) {  
                Class returnValue;  
                if ((column >= 0) && (column < getColumnCount())) {  
                    if (getValueAt(0, column) != null) {  
                        returnValue = getValueAt(0, column).getClass();  
                    } else {  
                        returnValue = Object.class;  
                    }  
                } else {  
                    returnValue = Object.class;  
                }  
                return returnValue;  
            }  
            
            @Override  
            public boolean isCellEditable(int row, int col) {  
                return false;  
            }  

        };
        
        table.setModel(aModel);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        
        // setar tamanho e alinhamento
        for (int i=0;i<align.length;i++) {
            if (widths[i] > -1) {
                table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);  
                if (widths[i] == 0) {
                    TableColumn tableColumn = table.getColumn(headers[i]);
                    tableColumn.setPreferredWidth(0);
                    tableColumn.setMinWidth(0);
                    tableColumn.setMaxWidth(0);

                    table.getTableHeader().setReorderingAllowed(false);
                    table.getTableHeader().setResizingAllowed(false);                
                }

                DefaultTableCellRenderer edc = new DefaultTableCellRenderer();
                if (align[i].equalsIgnoreCase("L")) {
                    edc.setHorizontalAlignment(SwingConstants.LEFT);
                }
                if (align[i].equalsIgnoreCase("R")) {
                    edc.setHorizontalAlignment(SwingConstants.RIGHT);
                }
                if (align[i].equalsIgnoreCase("C")) {
                    edc.setHorizontalAlignment(SwingConstants.CENTER);
                }
                table.getColumnModel().getColumn(i).setCellRenderer(edc);
            }
        }
        
        if (zebra) {
            UIManager.put("Table.alternateRowColor", new Color(204,204,255));
            UIManager.put("Table.foreground", new Color(0, 0, 0));
            UIManager.put("Table.selectionBackground", Color.YELLOW);
            UIManager.put("Table.selectionForeground", Color.RED);
        }
        
        TableColumnModel columnModel = table.getColumnModel();
        JTableRenderer renderer;
        renderer = new JTableRenderer();
        columnModel.getColumn(0).setCellRenderer(renderer);        

    }

    public static void InitTable2(JTable table, String[] headers, int[] widths, int heigths, String[] align, boolean zebra) {
        final DefaultTableModel aModel = new DefaultTableModel(headers, 0) {  
            @Override  
            public Class getColumnClass(int column) {  
                Class returnValue;  
                if ((column >= 0) && (column < getColumnCount())) {  
                    if (getValueAt(0, column) != null) {  
                        returnValue = getValueAt(0, column).getClass();  
                    } else {  
                        returnValue = Object.class;  
                    }  
                } else {  
                    returnValue = Object.class;  
                }  
                return returnValue;  
            }  
            
            @Override  
            public boolean isCellEditable(int row, int col) {  
                return false;  
            }  
        };
        
        table.setModel(aModel);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        
        // setar tamanho e alinhamento
        for (int i=0;i<align.length;i++) {
            if (widths[i] > -1) {
                table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);  
                if (widths[i] == 0) {
                    TableColumn tableColumn = table.getColumn(headers[i]);
                    tableColumn.setPreferredWidth(0);
                    tableColumn.setMinWidth(0);
                    tableColumn.setMaxWidth(0);

                    table.getTableHeader().setReorderingAllowed(false);
                    table.getTableHeader().setResizingAllowed(false);                
                }

                DefaultTableCellRenderer edc = new DefaultTableCellRenderer();
                if (align[i].equalsIgnoreCase("L")) {
                    edc.setHorizontalAlignment(SwingConstants.LEFT);
                }
                if (align[i].equalsIgnoreCase("R")) {
                    edc.setHorizontalAlignment(SwingConstants.RIGHT);
                }
                if (align[i].equalsIgnoreCase("C")) {
                    edc.setHorizontalAlignment(SwingConstants.CENTER);
                }
                table.getColumnModel().getColumn(i).setCellRenderer(edc);
            }
        }
        
        if (zebra) {
            UIManager.put("Table.alternateRowColor", new Color(204,204,255));
            UIManager.put("Table.foreground", new Color(0, 0, 0));
            UIManager.put("Table.selectionBackground", Color.YELLOW);
            UIManager.put("Table.selectionForeground", Color.RED);
        }
        
        if (heigths != -1) {
            table.setRowHeight(heigths);                    
        }        
    }

    private static class JTableRenderer extends DefaultTableCellRenderer {
        protected void setValue(Object value) {
            if (value instanceof ImageIcon) {
                if (value != null) {
                    ImageIcon d = (ImageIcon) value;
                    setIcon(d);
                } else {
                    setText("");
                    setIcon(null);
                }
            } else {
                super.setValue(value);
            }
        }
    }
    
    public static void add(JTable table, Object[] itens){
        javax.swing.table.DefaultTableModel dtm =
        (javax.swing.table.DefaultTableModel)table.getModel();

        for (int i = 0; i< itens.length; i++) {
            if (itens[i] instanceof ImageIcon) {
                TableColumnModel columnModel = table.getColumnModel();
                JTableRenderer renderer;
                renderer = new JTableRenderer();
                columnModel.getColumn(i).setCellRenderer(renderer);                        
            }
        }

        dtm.addRow(itens);

    }
    
}
