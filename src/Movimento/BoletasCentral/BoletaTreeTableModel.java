/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento.BoletasCentral;

import java.awt.Color;
import java.util.List;
import javax.swing.UIManager;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;


/**
 *
 * @author softelet
 */
public class BoletaTreeTableModel extends AbstractTreeTableModel {
    private final static String[] COLUMN_NAMES = {"Banco", "Nome", "Vencimento", "Boleta", "Tipo", "Rgprp", "Rgimv", "Tag"};
    
    private List<BancosBoleta> bancoBoleta;
    
    public BoletaTreeTableModel(List<BancosBoleta> bancoBoleta) {
        super(new Object());
        this.bancoBoleta = bancoBoleta;
        
        UIManager.put("Table.alternateRowColor", new Color(204,204,255));
        UIManager.put("Table.foreground", new Color(0, 0, 0));  //new Color(255, 255, 255)

    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
      Class clazz = String.class;
      switch (column) {
        case 7:
          clazz = Boolean.class;
          break;
      }
      return clazz;
    }
    
    @Override
    public boolean isCellEditable(Object node, int column) {
        if (node instanceof BancosBoleta) {
            if (column != 7) return false;
        }
        if (node instanceof PessoasBoleta) {
            if (column != 3 && column != 7) return false;
        }
        return true;
    }

    private void CheckUnCheck(Object value, Object node, int column) {
        if (node instanceof BancosBoleta) {
            if (column == 7) {
                BancosBoleta bancosBoleta = (BancosBoleta) node;
                for (PessoasBoleta p : bancosBoleta.getPessoasBoleta()) {
                    p.setTag((Boolean)value);
                }
            }
        }
    }
    
    @Override
    public boolean isLeaf(Object node) {
        return node instanceof PessoasBoleta;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof BancosBoleta) {
            BancosBoleta bancosBoleta = (BancosBoleta) parent;
            return bancosBoleta.getPessoasBoleta().size();
        }
        return bancoBoleta.size();
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof BancosBoleta) {
            BancosBoleta bancosBoleta = (BancosBoleta) parent;
            return bancosBoleta.getPessoasBoleta().get(index);
        }
        return bancoBoleta.get(index);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        BancosBoleta bancosBoleta = (BancosBoleta) parent;
        PessoasBoleta pessoasBoleta = (PessoasBoleta) child;
        return bancosBoleta.getPessoasBoleta().indexOf(pessoasBoleta);
    }
    
@Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof BancosBoleta) {
            BancosBoleta bancosBoleta = (BancosBoleta) node;
            switch (column) {
                case 0:
                    return bancosBoleta.getBanco();
                case 1:
                    return bancosBoleta.getNomeBanco();
                case 7:
                    return bancosBoleta.getTag();
            }
        } else if (node instanceof PessoasBoleta) {
            PessoasBoleta pessoasBoleta = (PessoasBoleta) node;
            switch (column) {
                case 0:
                    return pessoasBoleta.getContrato();
                case 1:
                    return pessoasBoleta.getNome();
                case 2:
                    return pessoasBoleta.getVencimentoRec();
                case 3:
                    return pessoasBoleta.getVencimentoBol();
                case 4:
                    return pessoasBoleta.getTipoEnvio();
                case 5:
                    return pessoasBoleta.getRgprp();
                case 6:
                    return pessoasBoleta.getRgimv();
                case 7:
                    return pessoasBoleta.getTag();
            }
        }
        return null;
    }    
    
    @Override
    public void setValueAt(Object value, Object node, int column) {
        if (node instanceof BancosBoleta) {
            BancosBoleta bancosBoleta = (BancosBoleta) node;
            switch (column) {
                case 0:
                    bancosBoleta.setBanco((String)value);
                    break;
                case 1:
                    bancosBoleta.setNomeBanco((String)value);
                    break;
                case 7:
                    bancosBoleta.setTag((Boolean)value);
                    CheckUnCheck(value, node, column);
            }
        } else if (node instanceof PessoasBoleta) {
            PessoasBoleta pessoasBoleta = (PessoasBoleta) node;
            switch (column) {
                case 0:
                    pessoasBoleta.setContrato((String)value);
                    break;
                case 1:
                    pessoasBoleta.setNome((String)value);
                    break;
                case 2:
                    pessoasBoleta.setVencimentoRec((String)value);
                    break;
                case 3:
                    pessoasBoleta.setVencimentoBol((String)value);
                    break;
                case 4:
                    pessoasBoleta.setTipoEnvio((String)value);
                    break;
                case 5:
                    pessoasBoleta.setRgprp((String)value);
                    break;
                case 6:
                    pessoasBoleta.setRgimv((String)value);
                    break;
                case 7:
                    pessoasBoleta.setTag((Boolean)value);
                    break;
            }
        }
    }
}
