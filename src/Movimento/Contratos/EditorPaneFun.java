/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento.Contratos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class EditorPaneFun extends JPanel {
   private static final Integer[] ITEMS = { 9, 10, 11, 12, 14, 16, 18, 20, 24,
         32 };
   private static final String[] LETRAS = {"San Serife", "Arial Balck"};
   private JEditorPane editorPane = new JEditorPane();
   private JComboBox<Integer> fontBox = new JComboBox<Integer>(ITEMS);
   private JComboBox<String> fontLeter = new JComboBox<String>(LETRAS);
   private StyledDocument doc = new DefaultStyledDocument();
   private StyledEditorKit styledEditorKit = new StyledEditorKit();

   public EditorPaneFun() {
      editorPane.setDocument(doc);
      editorPane.setEditorKit(styledEditorKit);
      JScrollPane scrollpane = new JScrollPane(editorPane);
      scrollpane.setPreferredSize(new Dimension(500, 400));
      JPanel comboPanel = new JPanel();
      comboPanel.add(fontBox);
      comboPanel.add(fontLeter);
      
      setLayout(new BorderLayout());
      add(scrollpane, BorderLayout.CENTER);
      add(comboPanel, BorderLayout.SOUTH);

      Document doc = editorPane.getDocument();
      for (int i = 0; i < 20; i++) {
         int offset = doc.getLength();
         String str = "This is line number: " + i + "\n";
         try {
            doc.insertString(offset, str, null);
         } catch (BadLocationException e) {
            e.printStackTrace();
         }
      }

      fontBox.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            int size = (Integer) fontBox.getSelectedItem();
            Action fontAction = new StyledEditorKit.FontSizeAction(String
                  .valueOf(size), size);
            fontAction.actionPerformed(e);
         }
      });
      
      fontLeter.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
              String letra = fontLeter.getSelectedItem().toString();
              Action fontAction = new StyledEditorKit.FontFamilyAction(letra, letra);
              fontAction.actionPerformed(e);
          }
      });
   }

   private static void createAndShowGui() {
      JFrame frame = new JFrame("EditorPaneFun");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(new EditorPaneFun());
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGui();
         }
      });
   }
}