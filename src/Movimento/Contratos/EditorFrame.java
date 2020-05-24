/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento.Contratos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
 
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.rtf.RTFEditorKit;
 
public class EditorFrame extends JFrame implements ActionListener {
 
 
    //private final JEditorPane editorPane = new JEditorPane();
    private final JTextPane editorPane = new JTextPane();
    private final JPanel toolBarPane = new JPanel();
    private final JToolBar sizeToolBar = new JToolBar();
    private final JToolBar styleToolBar = new JToolBar();
    private final JToolBar toolBar = new JToolBar();
   
    private final HashMap<String, Action> actionsMap = new HashMap<String, Action>();
   
    private final String[] fontSize = { "font-size-8", "font-size-14", "font-size-16", "font-size-18", "font-size-24", "font-size-36", "font-size-48"};
    private final String[] fontStyle = {"font-italic", "font-bold", "font-underline", "left-justify", "right-justify", "center-justify" };
    private final String[] tools = {"select-all", "cut-to-clipboard", "paste-from-clipboard" };
   
    private final JTextArea textArea = new JTextArea(20, 5);
   
    EditorFrame() {
        super("Editor");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
       
        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());
 
       
        //TextPane erstellen
        editorPane.setContentType("text/rtf");
        editorPane.setEditorKit(new RTFEditorKit());
       
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(250, 145));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));
 
        editorPane.setText("Hallo");        //<----------------- geht nicht
 
        // ToolBars erstellen
        createActionTable();
        fontSizeBox();
        fontStyleBox();
        toolBox();
           
        toolBarPane.setLayout(new GridLayout(4, 1, 0, 00));
 
        toolBarPane.add(myToolBox());
        toolBarPane.add(toolBar);
        toolBarPane.add(sizeToolBar);
        toolBarPane.add(styleToolBar);     
       
        contentPane.add(BorderLayout.NORTH, toolBarPane);  
        contentPane.add(BorderLayout.CENTER, editorScrollPane);
        contentPane.add(BorderLayout.SOUTH, textArea);     
       
        this.setVisible(true);
    }
   
    private void createActionTable() { 
        Action[] actionsArray = editorPane.getActions();
       
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actionsMap.put((String) a.getValue(Action.NAME), a);
        }
    }  
   
    private void fontSizeBox() {       
        for (int i = 0; i < fontSize.length; i++) {
            if(actionsMap.containsKey(fontSize[i])) {
                sizeToolBar.add(actionsMap.get(fontSize[i]));
            }
        }      
    }
   
    private void fontStyleBox() {      
        for (int i = 0; i < fontStyle.length; i++) {
            if(actionsMap.containsKey(fontStyle[i])) {
                styleToolBar.add(actionsMap.get(fontStyle[i]));
            }
        }      
    }  
   
    private void toolBox() {       
        for (int i = 0; i < tools.length; i++) {
            if(actionsMap.containsKey(tools[i])) {
                toolBar.add(actionsMap.get(tools[i]));
            }
        }      
    }  
   
    public JPanel myToolBox() {
        JPanel myToolPanel = new JPanel();
       
        JButton save = new JButton("save");
        save.setActionCommand("save");
        save.addActionListener(this);
       
        myToolPanel.add(save);     
       
        return myToolPanel;
    }
 
 
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("save")) {
            textArea.setText("Save: "+ editorPane.getText());   // <----------------- geht nicht
        }
       
    }

   public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new EditorFrame().setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
   }
    
}
 
