/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import javax.swing.JTextField;
import java.awt.event.*;

public class LimitedTextField extends JTextField{
    private byte maxLength=0;

    public LimitedTextField(int maxLength){
        super();
        this.maxLength= (byte)maxLength;
        this.addKeyListener(new LimitedKeyListener());
        this.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                setText(StringManager.ConvStr(getText()));
            }
        });
    }

    public void setMaxLength(int maxLength){
        this.maxLength= (byte)maxLength;
        update();
    }

    private void update(){
        if (getText().length()>maxLength){
            setText(StringManager.ConvStr(getText().substring(0,maxLength)));
            setCaretPosition(maxLength);
        }
    }

    public void setText(String arg0){
        super.setText(StringManager.ConvStr(arg0));
        update();
    }

    public void paste(){
        super.paste();
        update();
    }

    //Classes Internas
    private class LimitedKeyListener extends KeyAdapter{
        private boolean backspace= false;

        public void keyPressed(KeyEvent e){
            backspace=(e.getKeyCode()==8);
        }

        public void keyTyped(KeyEvent e){
            if (    !backspace  &&
                    getText().length()>maxLength-1){
                e.consume();
            }
        }
    }
}
