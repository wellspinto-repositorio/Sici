package Movimento.Contratos;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeModelListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/**
 *
 * @author supervisor
 */
public class jContrato extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String [] javaWords = {"=teste","=se2"};        
    String[][] formulas = {{"teste","prop.sexo == 'M'","'O Proprietario'","'A Proprietaria'"}};
    Boolean newfml = false;
    List<Variaveis> Cpos = new ArrayList<Variaveis>();  
    
    private UndoManager undoManager = new UndoManager();
    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();

    /**
     * Creates new form jContrato
     */
    public jContrato() {
        initComponents();

        BarradeCampos();
        FillFml();

        Action boldAction = new BoldAction();
        boldAction.putValue(Action.NAME, "");
        Action italicAction = new ItalicAction();
        italicAction.putValue(Action.NAME, "");
        Action underlineAction = new UnderlineAction();
        underlineAction.putValue(Action.NAME, "");
        
        Action alignleftAction = new AlignLeftAction();
        Action alignrightAction = new AlignRightAction();
        Action aligncenterAction = new AlignCenterAction();
        Action alignjustifiedAction = new AlignJustifiedAction();

        Action fontsizeAction = new FontSizeAction();
        Action foregroundAction = new ForegroundAction();
        Action backgroundAction = new BackgroundAction();
        Action formatTextAction = new FontAndSizeAction();

        jEdit.add(undoAction);
        jEdit.add(redoAction);
                
        jEdit.add(foregroundAction);
        jEdit.add(formatTextAction);
        
        FontBold.setAction(boldAction);
        FontBold.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2532_text_bold.png")));
        FontItalic.setAction(italicAction);
        FontItalic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2533_text_italic.png")));
        FontUnderLine.setAction(underlineAction);
        FontUnderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2536_text_underline.png")));
        
        TextAlignLeft.setAction(alignleftAction);
        TextAlignLeft.setText(null);
        TextAlignLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_left.png")));
        TextAlignLeft.setToolTipText("Alinhamento a Esquerda");
        
        TextAlignRigth.setAction(alignrightAction);
        TextAlignRigth.setText(null);
        TextAlignRigth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_right.png")));
        TextAlignRigth.setToolTipText("Alinhamento a Direita");
        
        TextAlignCenter.setAction(aligncenterAction);
        TextAlignCenter.setText(null);
        TextAlignCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_center.png")));
        TextAlignCenter.setToolTipText("Alinhamento ao Centro");
        
        TextAlignJustified.setAction(alignjustifiedAction);
        TextAlignJustified.setText(null);
        TextAlignJustified.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_justify.png")));
        TextAlignJustified.setToolTipText("Alinhamento Justificado");
        
        ListarFontes();
        FontName.setAction(fontsizeAction);
        FontName.setToolTipText("Estilo da Fonte");
        FontSize.setAction(fontsizeAction);
        FontSize.setToolTipText("Tamanho da Fonte");
        
        FontColor.setAction(foregroundAction);
        FontColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_foregroundcolor.png")));
        FontColor.setToolTipText("Cor da Fonte");
        
        FontBackColor.setAction(backgroundAction);
        FontBackColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_backgroundcolor.png")));
        FontBackColor.setToolTipText("Cor do Fundo");

        JEditorPane _htmlPane = new JEditorPane();
        _htmlPane.setEditable(true);
        pScroll.setViewportView(_htmlPane);
        
        HTMLEditorKit htmlEditor = new HTMLEditorKit();
        _htmlPane.setEditorKit(htmlEditor);
        String htmlString="<html>\n" + "<body>\n" + "<h2>Sequence Check Details</h2>\n"+ "<p>The DNA sequence level details of a check are displayed in this space.</p>\n"+ "</body>\n";
        Document doc=htmlEditor.createDefaultDocument();
        _htmlPane.setDocument(doc);
        _htmlPane.setText(htmlString);
        
    }

    public void highLight(JTextComponent textComp, String[] pattern) {   
      // First remove all old highlights
         removeHighlights(textComp);

      try {

         Highlighter hilite = textComp.getHighlighter();                                
         Document doc = textComp.getDocument();      
         String text = doc.getText(0, doc.getLength());         
         for (int i = 0; i < pattern.length; i++) {                          
            int pos = 0;
             // Search for pattern
             while ((pos = text.indexOf(pattern[i], pos)) >= 0) {

                Highlighter.HighlightPainter color;
                if (pattern[i].contains("prop.")) {
                    color = prop;
                } else if (pattern[i].contains("imv.")) {
                    color = imv;
                } else if (pattern[i].contains("loca.")) {
                    color = loca;
                } else if (pattern[i].contains("fia.")) {
                    color = fia;
                } else if (pattern[i].contains("cart.")) {
                    color = cart;
                } else {
                    color = ese;
                }
                
                hilite.addHighlight(pos, pos + pattern[i].length(), color);             
                pos += pattern[i].length();

             }
         }
         } catch (BadLocationException e) {}

    }

    public void removeHighlights(JTextComponent textComp) {
       Highlighter hilite = textComp.getHighlighter();
       Highlighter.Highlight[] hilites = hilite.getHighlights();
       for (int i = 0; i < hilites.length; i++) {
          if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                  hilite.removeHighlight(hilites[i]);
          }
       }
    }

    Highlighter.HighlightPainter prop = new MyHighlightPainter(new Color(204,255,255));
    Highlighter.HighlightPainter imv = new MyHighlightPainter(new Color(204,204,255));
    Highlighter.HighlightPainter loca = new MyHighlightPainter(new Color(255,204,255));
    Highlighter.HighlightPainter fia = new MyHighlightPainter(new Color(255,204,204));
    Highlighter.HighlightPainter cart = new MyHighlightPainter(new Color(255,255,204));
    Highlighter.HighlightPainter ese = new MyHighlightPainter(new Color(204,255,204));

    private class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
       public MyHighlightPainter(Color color) {
               super(color);                
          }
    }

    class FontSizeAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 584531387732416339L;
        private String family;
        private float fontSize;
        public FontSizeAction() {
            super("Font and Size");
        }
        public String toString() {
            return "Font and Size";
        }
        public void actionPerformed(ActionEvent e) {
            JTextPane editor = (JTextPane) getEditor(e);
            family = (String) FontName.getSelectedItem();
            fontSize = Float.parseFloat(FontSize.getSelectedItem().toString());
            MutableAttributeSet attr = null;
            if (editor != null) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, family);
                StyleConstants.setFontSize(attr, (int) fontSize);
                setCharacterAttributes(editor, attr, false);
                editor.requestFocus();
            }
        }
    }

    class FontCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(
                list,value,index,isSelected,cellHasFocus);
            Font font = new Font((String)value, Font.PLAIN, 20);
            label.setFont(font);
            return label;
        }
    }

    private void ListarFontes() {
        GraphicsEnvironment ge = GraphicsEnvironment.
            getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();
        for (String f : fonts) {
            FontName.addItem(f);
        }
        FontName.setRenderer(new FontCellRenderer());
        //FontName.setSelectedItem(family);
    }
    
    class AlignJustifiedAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;
        public AlignJustifiedAction() {
            super("align-justified");
        }
        public String toString() {
            return "AlignJustified";
        }
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_JUSTIFIED);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignCenterAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;
        public AlignCenterAction() {
            super("align-center");
        }
        public String toString() {
            return "AlignCenter";
        }
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_CENTER);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignRightAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;
        public AlignRightAction() {
            super("align-right");
        }
        public String toString() {
            return "AlignRight";
        }
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_RIGHT);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignLeftAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;
        public AlignLeftAction() {
            super("align-left");
        }
        public String toString() {
            return "AlignLeft";
        }
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_LEFT);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class UnderlineAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1794670038684056758L;
        public UnderlineAction() {
            super("font-underline");
        }
        public String toString() {
            return "Underline";
        }
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean underline = (StyleConstants.isUnderline(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setUnderline(sas, underline);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class BoldAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 9174670038684056758L;
        public BoldAction() {
            super("font-bold");
        }
        public String toString() {
            return "Bold";
        }
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean bold = (StyleConstants.isBold(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setBold(sas, bold);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }
    
    class ItalicAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = -1428340091100055456L;
        public ItalicAction() {
            super("font-italic");
        }
        public String toString() {
            return "Italic";
        }
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean italic = (StyleConstants.isItalic(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setItalic(sas, italic);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }
    
    class ForegroundAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 6384632651737400352L;
        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = new JDialog();
        boolean noChange = false;
        boolean cancelled = false;
        public ForegroundAction() {
            super("");
        }
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JTextPane) getEditor(e);
            if (editor == null) {
                JOptionPane.showMessageDialog(null,
                    "You need to select the editor pane before you can change the color.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return;
            }
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            fg = StyleConstants.getForeground(as);
            if (fg == null) {
                fg = Color.BLACK;
            }
            colorChooser.setColor(fg);
            JButton accept = new JButton("OK");
            accept.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    fg = colorChooser.getColor();
                    dialog.dispose();
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                   cancelled = true;
                    dialog.dispose();
                }
            });
            JButton none = new JButton("None");
            none.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    noChange = true;
                    dialog.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(accept);
            buttons.add(none);
            buttons.add(cancel);
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(colorChooser, BorderLayout.CENTER);
            dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
            dialog.setModal(true);
            dialog.pack();
            dialog.setVisible(true);
            if (!cancelled) {
                MutableAttributeSet attr = null;
                if (editor != null) {
                    if (fg != null && !noChange) {
                        attr = new SimpleAttributeSet();
                        StyleConstants.setForeground(attr, fg);
                        setCharacterAttributes(editor, attr, false);
                    }
                }
            }// end if color != null
            noChange = false;
            cancelled = false;
        }
        private Color fg;
    }
    
    class FontAndSizeAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 584531387732416339L;
        private String family;
        private float fontSize;
        JDialog formatText;
        private boolean accept = false;
        JComboBox fontFamilyChooser;
        JComboBox fontSizeChooser;
        public FontAndSizeAction() {
            super("Font and Size");
        }
        public String toString() {
            return "Font and Size";
        }
        public void actionPerformed(ActionEvent e) {
            JTextPane editor = (JTextPane) getEditor(e);
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            family = StyleConstants.getFontFamily(as);
            fontSize = StyleConstants.getFontSize(as);
            formatText = new JDialog(new JFrame(), "Font and Size", true);
            formatText.getContentPane().setLayout(new BorderLayout());
            JPanel choosers = new JPanel();
            choosers.setLayout(new GridLayout(2, 1));
            JPanel fontFamilyPanel = new JPanel();
            fontFamilyPanel.add(new JLabel("Font"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();
            fontFamilyChooser = new JComboBox();
            for (int i = 0; i < fontNames.length; i++) {
                fontFamilyChooser.addItem(fontNames[i]);
            }
            fontFamilyChooser.setSelectedItem(family);
            fontFamilyPanel.add(fontFamilyChooser);
            choosers.add(fontFamilyPanel);
            JPanel fontSizePanel = new JPanel();
            fontSizePanel.add(new JLabel("Size"));
            fontSizeChooser = new JComboBox();
            fontSizeChooser.setEditable(true);
            fontSizeChooser.addItem(new Float(4));
            fontSizeChooser.addItem(new Float(8));
            fontSizeChooser.addItem(new Float(12));
            fontSizeChooser.addItem(new Float(16));
            fontSizeChooser.addItem(new Float(20));
            fontSizeChooser.addItem(new Float(24));
            fontSizeChooser.setSelectedItem(new Float(fontSize));
            fontSizePanel.add(fontSizeChooser);
            choosers.add(fontSizePanel);
            JButton ok = new JButton("OK");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    accept = true;
                    formatText.dispose();
                    family = (String) fontFamilyChooser.getSelectedItem();
                    fontSize = Float.parseFloat(fontSizeChooser.getSelectedItem().toString());
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    formatText.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(ok);
            buttons.add(cancel);
            formatText.getContentPane().add(choosers, BorderLayout.CENTER);
            formatText.getContentPane().add(buttons, BorderLayout.SOUTH);
            formatText.pack();
            formatText.setVisible(true);
            MutableAttributeSet attr = null;
            if (editor != null && accept) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, family);
                StyleConstants.setFontSize(attr, (int) fontSize);
                setCharacterAttributes(editor, attr, false);
            }
        }
    }

    class UndoListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    class UndoAction extends AbstractAction {
        public UndoAction() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled()) {
                undoManager.undo();
                undoAction.update();
                redoAction.update();
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(undoManager.canUndo());
        }
    }

    class RedoAction extends AbstractAction {
        public RedoAction() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled()) {
                undoManager.redo();
                undoAction.update();
                redoAction.update();
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(undoManager.canRedo());
        }
    }

    class BackgroundAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 3684632651737400352L;
        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = new JDialog();
        boolean noChange = false;
        boolean cancelled = false;
        public BackgroundAction() {
            super("");
        }
        public void actionPerformed(ActionEvent e) {
            JTextPane editor = (JTextPane) getEditor(e);
            if (editor == null) {
                JOptionPane.showMessageDialog(null,
                    "You need to select the editor pane before you can change the color.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return;
            }
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            fg = StyleConstants.getBackground(as);
            if (fg == null) {
                fg = Color.BLACK;
            }
            colorChooser.setColor(fg);
            JButton accept = new JButton("OK");
            accept.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    fg = colorChooser.getColor();
                    dialog.dispose();
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                   cancelled = true;
                    dialog.dispose();
                }
            });
            JButton none = new JButton("None");
            none.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    noChange = true;
                    dialog.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(accept);
            buttons.add(none);
            buttons.add(cancel);
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(colorChooser, BorderLayout.CENTER);
            dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
            dialog.setModal(true);
            dialog.pack();
            dialog.setVisible(true);
            if (!cancelled) {
                MutableAttributeSet attr = null;
                if (editor != null) {
                    if (fg != null && !noChange) {
                        attr = new SimpleAttributeSet();
                        StyleConstants.setBackground(attr, fg);
                        setCharacterAttributes(editor, attr, false);
                    }
                }
            }// end if color != null
            noChange = false;
            cancelled = false;
        }
        private Color fg;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton2 = new javax.swing.JToggleButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        FontName = new javax.swing.JComboBox();
        FontSize = new javax.swing.JComboBox();
        FontBold = new javax.swing.JToggleButton();
        FontItalic = new javax.swing.JToggleButton();
        FontUnderLine = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JSeparator();
        TextAlignLeft = new javax.swing.JToggleButton();
        TextAlignCenter = new javax.swing.JToggleButton();
        TextAlignRigth = new javax.swing.JToggleButton();
        TextAlignJustified = new javax.swing.JToggleButton();
        FontColor = new javax.swing.JButton();
        FontBackColor = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTreeCpos = new javax.swing.JTree();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ListFml = new javax.swing.JList();
        btadcfml = new javax.swing.JLabel();
        btdelfml = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nmcampo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        condicao = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        verdadeiro = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        falso = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        btsavefml = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jComboBox1 = new javax.swing.JComboBox();
        pnlFuncoes = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pScroll = new javax.swing.JScrollPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jEdit = new javax.swing.JMenu();

        jToggleButton2.setText("jToggleButton2");

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(".:: Editor de Contratos");

        jToolBar1.setRollover(true);

        FontName.setToolTipText("Tipo da Fonte");
        FontName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FontNameActionPerformed(evt);
            }
        });

        FontSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "6", "7", "8", "10", "12", "14", "16", "18", "20", "22", "24", "26", "32", "64", "72" }));
        FontSize.setToolTipText("Tamanho da Fonte");
        FontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FontSizeActionPerformed(evt);
            }
        });

        FontBold.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2532_text_bold.png"))); // NOI18N
        FontBold.setToolTipText("Negrito");
        FontBold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FontBoldActionPerformed(evt);
            }
        });

        FontItalic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2533_text_italic.png"))); // NOI18N
        FontItalic.setToolTipText("Itálico");
        FontItalic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FontItalicActionPerformed(evt);
            }
        });

        FontUnderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2536_text_underline.png"))); // NOI18N
        FontUnderLine.setToolTipText("Sublinhado");
        FontUnderLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FontUnderLineActionPerformed(evt);
            }
        });

        buttonGroup1.add(TextAlignLeft);
        TextAlignLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_left.png"))); // NOI18N
        TextAlignLeft.setSelected(true);
        TextAlignLeft.setToolTipText("Alinhamento a Esquerda");
        TextAlignLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextAlignLeftActionPerformed(evt);
            }
        });

        buttonGroup1.add(TextAlignCenter);
        TextAlignCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_center.png"))); // NOI18N
        TextAlignCenter.setToolTipText("Alinhamento ao Centro");
        TextAlignCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextAlignCenterActionPerformed(evt);
            }
        });

        buttonGroup1.add(TextAlignRigth);
        TextAlignRigth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_right.png"))); // NOI18N
        TextAlignRigth.setToolTipText("Alinhamento a Direita");
        TextAlignRigth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextAlignRigthActionPerformed(evt);
            }
        });

        buttonGroup1.add(TextAlignJustified);
        TextAlignJustified.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_justify.png"))); // NOI18N
        TextAlignJustified.setToolTipText("Alinhamento Justificado");
        TextAlignJustified.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextAlignJustifiedActionPerformed(evt);
            }
        });

        FontColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_foregroundcolor.png"))); // NOI18N
        FontColor.setToolTipText("Cor da Fonte");
        FontColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FontColorActionPerformed(evt);
            }
        });

        FontBackColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_backgroundcolor.png"))); // NOI18N
        FontBackColor.setToolTipText("Cor do Fundo");
        FontBackColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FontBackColorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(FontName, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(FontBold, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontItalic, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontUnderLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TextAlignLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignRigth, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignJustified, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(FontColor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontBackColor, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 474, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FontName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(FontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(FontBold)
                        .addComponent(FontItalic)
                        .addComponent(FontUnderLine))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TextAlignCenter)
                    .addComponent(TextAlignRigth)
                    .addComponent(TextAlignLeft)
                    .addComponent(TextAlignJustified)
                    .addComponent(FontBackColor)
                    .addComponent(FontColor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar1.add(jPanel1);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        jScrollPane2.setViewportView(jTreeCpos);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Tabelas & Campos", jPanel2);

        ListFml.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ListFmlMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(ListFml);

        btadcfml.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btadcfml.setText("+");
        btadcfml.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btadcfml.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btadcfmlMouseReleased(evt);
            }
        });

        btdelfml.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btdelfml.setText("-");
        btdelfml.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btdelfml.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btdelfmlMouseReleased(evt);
            }
        });

        jLabel3.setText("=SE(");

        jLabel5.setText("Nome:");

        condicao.setColumns(20);
        condicao.setRows(5);
        jScrollPane3.setViewportView(condicao);

        jLabel6.setText(";");

        jLabel7.setText("true");

        verdadeiro.setColumns(20);
        verdadeiro.setRows(5);
        jScrollPane4.setViewportView(verdadeiro);

        jLabel8.setText(";");

        jLabel9.setText("false");

        falso.setColumns(20);
        falso.setRows(5);
        jScrollPane5.setViewportView(falso);

        jLabel10.setText(")");

        btsavefml.setText("S");
        btsavefml.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btsavefml.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btsavefmlMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel5))
                .addGap(0, 0, 0)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(nmcampo))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10))
                        .addGap(22, 22, 22))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btsavefml)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btadcfml)
                .addGap(0, 0, 0)
                .addComponent(btdelfml, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadcfml)
                    .addComponent(btdelfml))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(nmcampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btsavefml))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Formulas", jPanel7);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane8.setViewportView(jList1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Extenço(@Var)", "DifData(@VarDate1, @VarDate1, @VarPatern)", "Data()", "Hora()", "Format(@Var, @VarPatern)", "NomeArq()" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFuncoesLayout = new javax.swing.GroupLayout(pnlFuncoes);
        pnlFuncoes.setLayout(pnlFuncoesLayout);
        pnlFuncoesLayout.setHorizontalGroup(
            pnlFuncoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlFuncoesLayout.setVerticalGroup(
            pnlFuncoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8)
            .addComponent(jComboBox1, 0, 285, Short.MAX_VALUE)
            .addComponent(pnlFuncoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFuncoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Funções", jPanel8);

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jProgressBar1.setValue(50);

        jLabel1.setText("jLabel1");
        jLabel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("jLabel2");
        jLabel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)))
        );

        jMenu1.setText("Arquivo");

        jMenuItem1.setText("Abrir Documento");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Salvar");
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Salvar Como ...");
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Fechar");
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("Sair");
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        jEdit.setText("Edit");
        jMenuBar1.add(jEdit);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pScroll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addComponent(pScroll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FontBoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FontBoldActionPerformed
//        textPane.requestFocus();
    }//GEN-LAST:event_FontBoldActionPerformed

    private void FontItalicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FontItalicActionPerformed
//        textPane.requestFocus();
    }//GEN-LAST:event_FontItalicActionPerformed

    private void FontUnderLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FontUnderLineActionPerformed
//        textPane.requestFocus();
    }//GEN-LAST:event_FontUnderLineActionPerformed

    private void TextAlignLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextAlignLeftActionPerformed
//        textPane.requestFocus();
    }//GEN-LAST:event_TextAlignLeftActionPerformed

    private void TextAlignRigthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextAlignRigthActionPerformed
//        textPane.requestFocus();
    }//GEN-LAST:event_TextAlignRigthActionPerformed

    private void TextAlignCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextAlignCenterActionPerformed
//        textPane.requestFocus();
    }//GEN-LAST:event_TextAlignCenterActionPerformed

    private void TextAlignJustifiedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextAlignJustifiedActionPerformed
//        textPane.requestFocus();
    }//GEN-LAST:event_TextAlignJustifiedActionPerformed

    private void FontNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FontNameActionPerformed
        //textPane.requestFocus();
    }//GEN-LAST:event_FontNameActionPerformed

    private void FontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FontSizeActionPerformed
        //textPane.requestFocus();
    }//GEN-LAST:event_FontSizeActionPerformed

    private void FontColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FontColorActionPerformed
        //textPane.requestFocus();
    }//GEN-LAST:event_FontColorActionPerformed

    private void FontBackColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FontBackColorActionPerformed
        //textPane.requestFocus();
    }//GEN-LAST:event_FontBackColorActionPerformed

    private void ListFmlMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ListFmlMouseReleased
        if (ListFml.getSelectedIndex() == -1) return;
        String oque = ListFml.getSelectedValue().toString();
        int pos = FuncoesGlobais.FindinArrays(formulas, 0, oque);
        if (pos > -1) {
            nmcampo.setText(formulas[pos][0]);
            condicao.setText(formulas[pos][1]);
            verdadeiro.setText(formulas[pos][2]);
            falso.setText(formulas[pos][3]);
        }
    }//GEN-LAST:event_ListFmlMouseReleased

    private void btdelfmlMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btdelfmlMouseReleased
        delfml(true);
    }//GEN-LAST:event_btdelfmlMouseReleased

    private void delfml(Boolean clean) {
        if (ListFml.getSelectedIndex() == -1) return;
        if (clean) {
            nmcampo.setText("");
            condicao.setText("");
            verdadeiro.setText("");
            falso.setText("");
        }        
        formulas = FuncoesGlobais.ArraysDel(formulas, ListFml.getSelectedIndex());
        int pos = FuncoesGlobais.IndexOf(javaWords, "=" + ListFml.getSelectedValue().toString());
        javaWords = FuncoesGlobais.ArrayDel(javaWords, pos);
        DefaultListModel model = (DefaultListModel) ListFml.getModel();
        model.remove(ListFml.getSelectedIndex());        
    }
    
    private void btadcfmlMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btadcfmlMouseReleased
        nmcampo.setText("");
        condicao.setText("");
        verdadeiro.setText("");
        falso.setText("");
        
        newfml = true;
        ListFml.setEnabled(false);
        nmcampo.requestFocus();
    }//GEN-LAST:event_btadcfmlMouseReleased

    private void btsavefmlMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btsavefmlMouseReleased
        if (!newfml) {
            delfml(false);
        }

        DefaultListModel lista = (DefaultListModel) ListFml.getModel();
        lista.addElement(nmcampo.getText());
        ListFml.setModel(lista);

        formulas = FuncoesGlobais.ArraysAdd(formulas, new String[] {nmcampo.getText(),condicao.getText(),verdadeiro.getText(),falso.getText()});
        javaWords = FuncoesGlobais.ArrayAdd(javaWords, "=" + nmcampo.getText());
        nmcampo.setText("");
        condicao.setText("");
        verdadeiro.setText("");
        falso.setText("");

        newfml = false;
        ListFml.setEnabled(true);
    }//GEN-LAST:event_btsavefmlMouseReleased

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        JPanel tRec = null;
        try {
            pnlFuncoes.removeAll();
        } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace(); }
        if (jComboBox1.getSelectedIndex() == 0) {
            tRec = new jExtenco();
        } else if (jComboBox1.getSelectedIndex() == 1) {
            tRec = new jDifData();
        }
        if (tRec != null) {
            try {
                tRec.setVisible(true);
                tRec.setEnabled(true);
                tRec.setBounds(0, 0, 285,500);
            } catch (Exception e) {}
            try {
                pnlFuncoes.add(tRec);
            } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace(); }
            pnlFuncoes.repaint();
            pnlFuncoes.setEnabled(true);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void FillFml() {
        DefaultListModel lista = new DefaultListModel();
        for (String[] out : formulas) {
            lista.addElement(out[0]);
        }
        ListFml.setModel(lista);
    }
    
    private void BarradeCampos() {
        String[][] prop = new String[][] {{"rgprp","Registro do Proprietário"},
            {"nome","Nome do Proprietátio"},
            {"tipoprop","Pessoa Física/Jurídica"},
            {"end","Endereço do Proprietário"},
            {"num","Número"},
            {"compl","Complemento do endereço"},
            {"bairro","Bairro"},
            {"cidade","Cidade"},
            {"estado","Estado"},
            {"cep","Cep"},
            {"tel","Telefone de contato"},
            {"profissao","Profissão do Proprietário"},
            {"representante","Nome do Representante do Proprietário"},
            {"naciona","Nacionalidade"},
            {"ecivil","Estado Civil"},
            {"sexo","Sexo"},
            {"dtnasc","Data de Nascimento"},
            {"rginsc","Registro Geral/Inscrição"},
            {"cpfcnpj","CPF/CNPJ"},
            {"conjugue","Conjugue"},
            {"conjdtnasc","Data de Nascimento do Conjugue"},
            {"banco","Banco"},
            {"agencia","Agencia"},
            {"conta","Conta Corrente"},
            {"favorecido","Favorecido da Conta"},
            {"email","E-Mail"},
            {"cor_end","Endereço de Correspondência"},
            {"cor_num","Numero"},
            {"cor_compl","Complemento"},
            {"cor_bairro","Bairro"},
            {"cor_cidade","Cidade"},
            {"cor_estado","Estado"},
            {"cor_cep","Cep"}};
        for (String[] out : prop) {
            javaWords = FuncoesGlobais.ArrayAdd(javaWords, "prop." + out[0]);
        }
        
        String[][] imoveis = new String[][] {{"tpimovel","Tipo de Imovel"},
            {"tpurbrural","Urbano/Rural"},
            {"end","Endereço do Imóvel"},
            {"num","Numero"},
            {"compl","Complemento"},
            {"bairro","Bairro"},
            {"cidade","Cidade"},
            {"estado","Estado"},
            {"cep","Cep"},
            {"especifica","Especificações do imovel"},
            {"situacao","Situação (Vazio/Ocupado/Ação..."}};
        for (String[] out : imoveis) {
            javaWords = FuncoesGlobais.ArrayAdd(javaWords, "imv." + out[0]);
        }

        String[][] locatarios = new String[][] {{"contrato","Numero do Contrato"},
            {"tploca","Tipo Locatario F/J"},
            {"cpfcnpj","CPF/CNPJ"},
            {"rginsc","Registro Geral/Inscrição"},
            {"dtnasc","Data de Nascimento"},
            {"nomerazao","Razão Social"},
            {"fantasia","Nome Fantasia da Empresa"},
            {"sexo","Sexo"},
            {"end","Endereço"},
            {"num","Número"},
            {"compl","Complemento"},
            {"bairro","Bairro"},
            {"cidade","Cidade"},
            {"estado","Estado"},
            {"cep","Cep"},
            {"tel","Telefone"},
            {"ramal","Ramal"},
            {"celular","Celular"},
            {"naciona","Nacionalidade"},
            {"ecivil","Estado Civil"},
            {"pai","Nome do pai"},
            {"mae","Nome da Mãe"},
            {"empresa","Empresa em que Trabalha"},
            {"cargo","Cargo que ocupa"},
            {"salario","Salário Mensal"},
            {"dtadmis","Data de Admissão"},
            {"email","E-Mail"},
            {"conjugue","Nome do Conjugue"},
            {"conjsexo","Sexo do Conjugue"},
            {"cjdtnasc","Data de Nascimento do Conjugue"},
            {"cjempresa","Nome Empresa do Conjugue"},
            {"cjtel","Telefone do Conjugue"},
            {"cjramal","Ramal do conjugue"},
            {"cjsalario","Salário de Conjugue"},
            {"cjrg","Identidade do Conjugue"},
            {"cjcpf","CPF do Conjugue"},
            {"cor_nome","Nome Para Correspondência"},
            {"cor_end","Endereço de Correspondência"},
            {"cor_num","Número de Correspondência"},
            {"cor_compl", "Complemento de Correspondência"},
            {"cor_bairro","Bairro de Correspondência"},
            {"cor_cidade","Cidade de Correspondência"},
            {"cor_estado","Estado de Correspondência"},
            {"cor_cep","Cep de Correspondência"},
            {"socionome1","Nome do Primeiro Sócio"},
            {"sociodtnasc1","Data de Nacimento"},
            {"sociocpf1","CPF"},
            {"sociorg1","Registro Geral (RG)"},
            {"socionac1","Nacionalidade"},
            {"socioecivil1","Estado Civil"},
            {"sociopai1","Nome do Pai"},
            {"sociomae1","Nome da Mãe"},
            {"sociosalario1","Prolabore Mensal"},
            {"sociocargo1","Cargo na Empresa"},
            {"socionome2","Nome do Segundo Sócio"},
            {"sociodtnasc2","Data de Nacimento"},
            {"sociocpf2","CPF"},
            {"sociorg2","Registro Geral (RG)"},
            {"socionac2","Nacionalidade"},
            {"socioecivil2","Estado Civil"},
            {"sociopai2","Nome do Pai"},
            {"sociomae2","Nome da Mãe"},
            {"sociosalario2","Prolabore Mensal"},
            {"sociocargo2","Cargo na Empresa"},
            {"socionome3","Nome do Terceiro Sócio"},
            {"sociodtnasc3","Data de Nacimento"},
            {"sociocpf3","CPF"},
            {"sociorg3","Registro Geral (RG)"},
            {"socionac3","Nacionalidade"},
            {"socioecivil3","Estado Civil"},
            {"sociopai3","Nome do Pai"},
            {"sociomae3","Nome da Mãe"},
            {"sociosalario3","Prolabore Mensal"},
            {"sociocargo3","Cargo na Empresa"},
            {"socionome4","Nome do Quarto Sócio"},
            {"sociodtnasc4","Data de Nacimento"},
            {"sociocpf4","CPF"},
            {"sociorg4","Registro Geral (RG)"},
            {"socionac4","Nacionalidade"},
            {"socioecivil4","Estado Civil"},
            {"sociopai4","Nome do Pai"},
            {"sociomae4","Nome da Mãe"},
            {"sociosalario4","Prolabore Mensal"},
            {"sociocargo4","Cargo na Empresa"}};
        for (String[] out : locatarios) {
            javaWords = FuncoesGlobais.ArrayAdd(javaWords, "loca." + out[0]);
        }

        String[][] fiadores = new String[][] {{"contrato","Numero do Contrato"},
            {"tploca","Tipo Fiador F/J"},
            {"cpfcnpj","CPF/CNPJ"},
            {"rginsc","Registro Geral/Inscrição"},
            {"dtnasc","Data de Nascimento"},
            {"nomerazao","Razão Social"},
            {"fantasia","Nome Fantasia da Empresa"},
            {"end","Endereço"},
            {"num","Número"},
            {"compl","Complemento"},
            {"bairro","Bairro"},
            {"cidade","Cidade"},
            {"estado","Estado"},
            {"cep","Cep"},
            {"tel","Telefone"},
            {"ramal","Ramal"},
            {"celular","Celular"},
            {"naciona","Nacionalidade"},
            {"ecivil","Estado Civil"},
            {"pai","Nome do pai"},
            {"mae","Nome da Mãe"},
            {"empresa","Empresa em que Trabalha"},
            {"cargo","Cargo que ocupa"},
            {"salario","Salário Mensal"},
            {"dtadmis","Data de Admissão"},
            {"email","E-Mail"},
            {"conjugue","Nome do Conjugue"},
            {"cjdtnasc","Data de Nascimento do Conjugue"},
            {"cjempresa","Nome Empresa do Conjugue"},
            {"cjtel","Telefone do Conjugue"},
            {"cjramal","Ramal do conjugue"},
            {"cjsalario","Salário de Conjugue"},
            {"cjrg","Identidade do Conjugue"},
            {"cjcpf","CPF do Conjugue"},
            {"cor_nome","Nome Para Correspondência"},
            {"cor_end","Endereço de Correspondência"},
            {"cor_num","Número de Correspondência"},
            {"cor_compl", "Complemento de Correspondência"},
            {"cor_bairro","Bairro de Correspondência"},
            {"cor_cidade","Cidade de Correspondência"},
            {"cor_estado","Estado de Correspondência"},
            {"cor_cep","Cep de Correspondência"},
            {"socionome1","Nome do Primeiro Sócio"},
            {"sociodtnasc1","Data de Nacimento"},
            {"sociocpf1","CPF"},
            {"sociorg1","Registro Geral (RG)"},
            {"socionac1","Nacionalidade"},
            {"socioecivil1","Estado Civil"},
            {"sociopai1","Nome do Pai"},
            {"sociomae1","Nome da Mãe"},
            {"sociosalario1","Prolabore Mensal"},
            {"sociocargo1","Cargo na Empresa"},
            {"socionome2","Nome do Segundo Sócio"},
            {"sociodtnasc2","Data de Nacimento"},
            {"sociocpf2","CPF"},
            {"sociorg2","Registro Geral (RG)"},
            {"socionac2","Nacionalidade"},
            {"socioecivil2","Estado Civil"},
            {"sociopai2","Nome do Pai"},
            {"sociomae2","Nome da Mãe"},
            {"sociosalario2","Prolabore Mensal"},
            {"sociocargo2","Cargo na Empresa"},
            {"socionome3","Nome do Terceiro Sócio"},
            {"sociodtnasc3","Data de Nacimento"},
            {"sociocpf3","CPF"},
            {"sociorg3","Registro Geral (RG)"},
            {"socionac3","Nacionalidade"},
            {"socioecivil3","Estado Civil"},
            {"sociopai3","Nome do Pai"},
            {"sociomae3","Nome da Mãe"},
            {"sociosalario3","Prolabore Mensal"},
            {"sociocargo3","Cargo na Empresa"},
            {"socionome4","Nome do Quarto Sócio"},
            {"sociodtnasc4","Data de Nacimento"},
            {"sociocpf4","CPF"},
            {"sociorg4","Registro Geral (RG)"},
            {"socionac4","Nacionalidade"},
            {"socioecivil4","Estado Civil"},
            {"sociopai4","Nome do Pai"},
            {"sociomae4","Nome da Mãe"},
            {"sociosalario4","Prolabore Mensal"},
            {"sociocargo4","Cargo na Empresa"}};
        for (String[] out : fiadores) {
            javaWords = FuncoesGlobais.ArrayAdd(javaWords, "fia." + out[0]);
        }

        String[][] carteira = new String[][] {{"DTINICIO","Data do Início de Contrato"},
            {"DTTERMINO","Data do Termino do Contrato"},
            {"DTVENCIMENTO","Data do Vencimento do Contrato"},
            {"DTADITO","Data de Aditamento do Contrato"}};
        for (String[] out : carteira) {
            javaWords = FuncoesGlobais.ArrayAdd(javaWords, "cart." + out[0]);
        }
        
        String[][] lancart = new String[][] {};
        ResultSet rs = conn.AbrirTabela("SELECT CART_DESCR FROM LANCART;", ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                lancart = FuncoesGlobais.ArraysAdd(lancart, new String[] {rs.getString("CART_DESCR"),rs.getString("CART_DESCR")});
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(rs);
        for (String[] out : lancart) {
            javaWords = FuncoesGlobais.ArrayAdd(javaWords, "cart." + out[0]);
        }

        //
        final Integer POSICAO = 0;
        Variaveis propri = new Variaveis("Proprietários");  
        for (String[] out : prop) {
            propri.addAutor(new Campos("prop." + out[POSICAO]));  
        }
        Cpos.add(propri);  

        Variaveis imv = new Variaveis("Imoveis");  
        for (String[] out : imoveis) {
            imv.addAutor(new Campos("imv." + out[POSICAO]));  
        }
        Cpos.add(imv);  
        
        Variaveis loca = new Variaveis("Locatários");  
        for (String[] out : locatarios) {
            loca.addAutor(new Campos("loca." + out[POSICAO]));  
        }
        Cpos.add(loca);  

        Variaveis fia = new Variaveis("Fiadores");  
        for (String[] out : fiadores) {
            fia.addAutor(new Campos("fia." + out[POSICAO]));  
        }
        Cpos.add(fia);  

        Variaveis cart = new Variaveis("Carteira");  
        for (String[] out : carteira) {
            cart.addAutor(new Campos("cart." + out[POSICAO]));  
        }
        for (String[] out : lancart) {
            cart.addAutor(new Campos("cart." + out[POSICAO]));  
        }
        Cpos.add(cart);  

        jTreeCpos.setModel(new LivroTreeModel(Cpos));
        MouseListener listener = new DragMouseAdapter();
        jTreeCpos.addMouseListener(listener);
        
    }
    
    public class DragMouseAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            JComponent c = (JComponent)e.getSource();
 
            if (c instanceof JTree) {
                JTree tr = (JTree) c;
                if (tr.getSelectionModel().getLeadSelectionPath().getPathCount() == 3) {
                    Object[] node = tr.getSelectionModel().getSelectionPath().getPath();
                    //if (node[1].toString().toUpperCase().equalsIgnoreCase("CARTEIRA")) {
                        TransferHandler handler = c.getTransferHandler();
                        handler.exportAsDrag(c, e, TransferHandler.COPY);
                    //}
                }
            }
            
        }
    }
    
    public class Variaveis {  
        private String nome;  
        private List<Campos> autores = new ArrayList<Campos>();  

        public Variaveis(String nome) {  
            this.nome = nome;  
        }  

        public String getNome() {  
            return nome;  
        }  

        @Override  
        public String toString() {  
            return getNome();  
        }  

        public void addAutor(Campos campos) {  
            autores.add(campos);  
        }  

        public List<Campos> getAutores() {  
            return Collections.unmodifiableList(autores);  
        }  
    }  

    public class Campos {  
        private String nome;  
      
        public Campos(String nome) {  
            this.nome = nome;  
        }  
      
        public String getNome() {  
            return nome;  
        }  
      
        @Override  
        public String toString() {  
            return getNome();  
        }  
    }  

    public class LivroTreeModel implements TreeModel {  
            private String raiz = "Campos";  
            private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();  
            private List<Variaveis> variaveis;  
            public LivroTreeModel(List<Variaveis> variaveis) {  
            this.variaveis = variaveis;  
    }  

    /** 
     * Com esse método, o Java quem é o objeto que está num determinado índice 
     * do pai. Cada nó de uma árvore pode ser encarado como uma lista. Sendo o 
     * pai a lista e o índice um dos filhos. 
     *  
     * @param parent 
     *            É o pai, que tem os filhos. No caso do Livro, o próprio livro. 
     * @param index 
     *            Índice do filho. No caso do livro, o índice corresponde aos 
     *            autores. 
     */  
    public Object getChild(Object parent, int index) {  
        if (parent == raiz) // É o nó principal?  
            return variaveis.get(index); // Pegamos da lista de livro  

        if (parent instanceof Variaveis) // O pai é um livro?  
        {  
            // Devolvemos um autor  
            return ((Variaveis) parent).getAutores().get(index);  
        }  

        // Se o pai não é nenhum desses. Melhor dar erro.  
        throw new IllegalArgumentException("Invalid parent class"  
                + parent.getClass().getSimpleName());  
    }  

    /** 
     * Retornamos quantos filhos um pai tem. No caso de um livro, é a contagem 
     * de autores. No caso da lista de livros, é a quantidade de livros. 
     */  
    public int getChildCount(Object parent) {  
        // Mesma lógica.  
        if (parent == raiz)  
            return variaveis.size();  

        if (parent instanceof Variaveis) // O pai é um livro?  
            return ((Variaveis) parent).getAutores().size();  

        // Se o pai não é nenhum desses. Melhor dar erro.  
        throw new IllegalArgumentException("Invalid parent class"  
                + parent.getClass().getSimpleName());  
    }  

    /** 
     * Dado um pai, indicamos qual é o índice do filho correspondente. 
     */  
    public int getIndexOfChild(Object parent, Object child) {  
        if (parent == raiz)  
            return variaveis.indexOf(child);  
        if (parent instanceof Variaveis)  
            return ((Variaveis) parent).getAutores().indexOf(child);  

        return 0;  
    }  

    /** 
     * Devemos retornar quem é o nó raiz da árvore. Afinal, a árvore tem que 
     * começar em algum lugar. 
     */  
    public Object getRoot() {  
        return raiz;  
    }  

    /** 
     * Indicamos se um nó é ou não uma folha. Isso é, se ele não tem filhos. No 
     * nosso caso, os autores são as folhas da árvore. 
     */  
    public boolean isLeaf(Object node) {  
        return node instanceof Campos;  
    }  

    public void valueForPathChanged(TreePath path, Object newValue) {  
        // Com esse método, a tree avisa que um objeto mudou.  
        // Editem se quiserem que um nó seja editável  
    }  

    // Esses dois métodos abaixo poderiam ir para classe abstrata  
    public void removeTreeModelListener(TreeModelListener l) {  
        listeners.remove(l);  
    }  

    public void addTreeModelListener(TreeModelListener l) {  
        listeners.add(l);  
    }  
}  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton FontBackColor;
    private javax.swing.JToggleButton FontBold;
    private javax.swing.JButton FontColor;
    private javax.swing.JToggleButton FontItalic;
    private javax.swing.JComboBox FontName;
    private javax.swing.JComboBox FontSize;
    private javax.swing.JToggleButton FontUnderLine;
    private javax.swing.JList ListFml;
    private javax.swing.JToggleButton TextAlignCenter;
    private javax.swing.JToggleButton TextAlignJustified;
    private javax.swing.JToggleButton TextAlignLeft;
    private javax.swing.JToggleButton TextAlignRigth;
    private javax.swing.JLabel btadcfml;
    private javax.swing.JLabel btdelfml;
    private javax.swing.JLabel btsavefml;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextArea condicao;
    private javax.swing.JTextArea falso;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JMenu jEdit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTree jTreeCpos;
    private javax.swing.JTextField nmcampo;
    private javax.swing.JScrollPane pScroll;
    private javax.swing.JPanel pnlFuncoes;
    private javax.swing.JTextArea verdadeiro;
    // End of variables declaration//GEN-END:variables
}

