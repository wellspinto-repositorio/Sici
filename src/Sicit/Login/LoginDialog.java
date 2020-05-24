package Sicit.Login;

import Funcoes.VariaveisGlobais;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.sun.jna.examples.WindowUtils;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog implements ActionListener{

	public static void main(String[] args){
		LoginDialog login = new LoginDialog();
		System.out.println(login.showDialog());
		System.out.println(login.getUsername() + ':' + new String(login.getPassword()));
	}
	public static final int LOGOSIZE = 260;
	public static final int LOGINDIALOGWIDTH = 310;
	public static final int LOGINDIALOGHEIGHT = 200 + LOGOSIZE;

	public static final String LOGINCMD = "LOGIN";
	public static final String REGISTERCMD = "REGISTER";
	public static final String CANCELCMD = "CANCEL";

	/**
	 * 操作系统底部任务栏高度
	 */
	public static final int DOCKHEIGHT = 30;

	//JLabel l_logo = new JLabel();
	/**
	 * 输入面板
	 */
	JPanel p_input = new JPanel();
	/**
	 * 用户名JPanel
	 */
	JPanel p_name = new JPanel();
	/**
	 * 密码JPanel
	 */
	JPanel p_pwd = new JPanel();
        /**
         *  JPanel
         */
        JPanel p_unidade = new JPanel();
        /**
         *  JPanel
         *
         */
        JPanel p_versao = new JPanel();
	/**
	 * 用户名JLabel
	 */
	JLabel l_name = new JLabel("Usuário:");
	/**
	 * 密码JLabel
	 */
	JLabel l_pwd = new JLabel("Senha:");
        JLabel l_unidade = new JLabel("Unidade:");
        JLabel l_versao = new JLabel("Versão: " + Versao());

        JComboBox t_unidade = new JComboBox(new javax.swing.DefaultComboBoxModel(new String[] {VariaveisGlobais.unidade}));

        /**
	 * 用户名JTextField
	 */
	final JTextField t_name = new JTextField(18);
	/**
	 * 密码JPasswordField
	 */
	final JPasswordField t_pwd = new JPasswordField("", 18);

	JPanel p_buttons = new JPanel();
	final JButton b_login = new JButton("Entrar");
	final JButton b_cancel = new JButton("Fechar");

	/*String name = "";
	char[] password;*/

	/**
	 * 用来获取图片
	 */
	private Toolkit tk;
	private Image background;
	private JImgPanel rootpane;

	private Object returnVal = null;

    @SuppressWarnings({"LeakingThisInConstructor", "OverridableMethodCallInConstructor"})
	public LoginDialog() {
                // Colocando enter para pular de campo
                HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
                conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
                this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

		// This statement is important, only modal dialogs can return values
                this.setModal(true);

		tk = Toolkit.getDefaultToolkit();
		background = tk.getImage("resources/senha.png");
		this.prepareImage(background, rootpane);

		//Setar icone da tela
		//this.setIconImage(img_logo);

		rootpane = new JImgPanel(background);

		this.setUndecorated(true);
		l_name.setForeground(Color.WHITE);
		l_pwd.setForeground(Color.WHITE);
		l_unidade.setForeground(Color.WHITE);
                l_versao.setForeground(Color.WHITE);

		t_name.setToolTipText("Entre com seu login");
		t_pwd.setToolTipText("Entre com sua senha");
		t_pwd.setEchoChar('*');

                // Unidades de acesso remoto
                if (!"".equals(VariaveisGlobais.remoto1)) {
                    t_unidade.addItem(VariaveisGlobais.remoto1);
                }
                if (!"".equals(VariaveisGlobais.remoto2)) {
                    t_unidade.addItem(VariaveisGlobais.remoto2);
                }
                if (!"".equals(VariaveisGlobais.remoto3)) {
                    t_unidade.addItem(VariaveisGlobais.remoto3);
                }
                if (!"".equals(VariaveisGlobais.remoto4)) {
                    t_unidade.addItem(VariaveisGlobais.remoto4);
                }
                if (!"".equals(VariaveisGlobais.remoto5)) {
                    t_unidade.addItem(VariaveisGlobais.remoto5);
                }
                t_unidade.addItem("");
                t_unidade.setEditable(true);

		p_unidade.add(l_unidade);
                p_unidade.add(t_unidade);

                p_name.add(l_name);
		p_name.add(t_name);
		p_pwd.add(l_pwd);
		p_pwd.add(t_pwd);

                p_versao.add(l_versao);
                p_versao.setOpaque(false);

                p_unidade.setOpaque(false);
		p_name.setOpaque(false);
		p_pwd.setOpaque(false);

		p_input.setLayout(new BoxLayout(p_input, BoxLayout.Y_AXIS));
		//			p_input.setBounds(10,10,20,500);
                p_input.add(p_versao);
                p_input.add(p_unidade);
		p_input.add(p_name);
		p_input.add(p_pwd);
		p_input.setOpaque(false);

                b_login.setMnemonic('E');
		b_cancel.setMnemonic('F');

		p_buttons.add(b_login);
		//p_buttons.add(b_register);
		p_buttons.add(b_cancel);
		p_buttons.setOpaque(false);

		b_login.addActionListener(this);

		b_cancel.addActionListener(this);

		//rootpane.add(l_logo, BorderLayout.NORTH);
		//rootpane.add(Box.createVerticalStrut(LOGOSIZE), BorderLayout.NORTH);
		rootpane.setLayout(new BoxLayout(rootpane, BoxLayout.Y_AXIS));
		rootpane.add(Box.createVerticalStrut(LOGOSIZE));
		rootpane.add(p_input);
		rootpane.add(p_buttons);
		rootpane.add(Box.createVerticalStrut(15));

                // Seta botao login como default
		//getRootPane().setDefaultButton(b_login);
		this.setContentPane(rootpane);

		 Dimension screenDim = tk.getScreenSize();
		 int ScrWidth = screenDim.width;
		 int ScrHeight = screenDim.height - DOCKHEIGHT;

		 this.setBounds((ScrWidth-LOGINDIALOGWIDTH)/2, (ScrHeight-LOGINDIALOGHEIGHT)/2,
				 LOGINDIALOGWIDTH, LOGINDIALOGHEIGHT);
		 this.setSize(new Dimension(LOGINDIALOGWIDTH, LOGINDIALOGHEIGHT));
		 this.setPreferredSize(new Dimension(LOGINDIALOGWIDTH, LOGINDIALOGHEIGHT));
		 this.setMaximumSize(new Dimension(LOGINDIALOGWIDTH, LOGINDIALOGHEIGHT));
		 this.setMinimumSize(new Dimension(LOGINDIALOGWIDTH, LOGINDIALOGHEIGHT));

		 this.setAlwaysOnTop(true);
                 if (VariaveisGlobais.miscelaneas) {
                     this.setaOpacity(0.8f);
                     this.setRoundRecangle();
                 }
                 this.pack();
	}

    /** *//**
     * change statements here to get other return values
     * @return
     */
    public Object showDialog(){
        this.pack();
        this.setVisible(true);
        return this.returnVal;
    }

    private static String Versao() {
        File[] program = listarArquivos(".", "upg");
        String aplicativo = null;
        if (program.length > 0) {
            aplicativo = program[0].getName();
            aplicativo = aplicativo.substring(0, aplicativo.indexOf("."));
        }
        return aplicativo;
    }

   // lista os arquivos apartide de determinada extensão
   private static File[] listarArquivos(String caminhoDiretorio, final String extensao){
      File F = new File(caminhoDiretorio);


      File[] files = F.listFiles(new FileFilter() {

         public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(extensao);
         }
      });

      return files;
   }

    /**
	 * 设置不透明度
	 *
	 * @return 是否设置成功
	 */
	public boolean setaOpacity(float alpha) {
		if (WindowUtils.isWindowAlphaSupported()) {
			WindowUtils.setWindowAlpha(this, alpha);
			return true;
		} else {
			System.out.println("Sorry, WindowAlpha is not Supported");
			return false;
		}
	}
	public boolean setRoundRecangle(){
		RoundRectangle2D.Float mask =
			new RoundRectangle2D.Float(0, 0,
					LOGINDIALOGWIDTH, LOGINDIALOGHEIGHT , 60, 60);
	    WindowUtils.setWindowMask(this, mask);
	    return false;
	}

    public String getUsername(){
    	return t_name.getText();
    }

    public char[] getPassword(){
    	return t_pwd.getPassword();
    }

    public String getUnidade() {
        return t_unidade.getSelectedItem().toString();
    }

    public void showUnidadeInexistentBalloon(){
    	final String BALLOON_TEXT = "<html><center>"
            + "Unidade fora do ar.<br>"
            + "Por favor, tente novamente..<br>"
            + "(Click para desaparecer este balloon)</center></html>";
		JNABalloon balloon = new JNABalloon(BALLOON_TEXT, t_unidade, 10, 5);
		balloon.showBalloon();
    }

    public void showUsernameInexistentBalloon(){
    	final String BALLOON_TEXT = "<html><center>"
            + "O usuário não existe..<br>"
            + "Por favor, adicione o usuário e tente novamente..<br>"
            + "(Click para desaparecer este balloon)</center></html>";
		JNABalloon balloon = new JNABalloon(BALLOON_TEXT, t_name, 10, 5);
		balloon.showBalloon();
    }

    public void showPasswordIncorrectBalloon(){
    	final String BALLOON_TEXT = "<html><center>"
            + "Senha incorreta!<br>"
            + "(Click para desaparecer este balloon)</center></html>";
		JNABalloon balloon = new JNABalloon(BALLOON_TEXT, t_pwd, 10, 5);
		balloon.showBalloon();
    }
	public void actionPerformed(ActionEvent ae) {
    	if(ae.getSource() == b_login)
    		returnVal = LoginDialog.LOGINCMD;
    	else
    		returnVal = LoginDialog.CANCELCMD;
    	this.dispose();
	}
}