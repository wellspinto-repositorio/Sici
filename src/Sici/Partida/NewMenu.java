/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jSuperMenu.java
 *
 * Created on 25/01/2011, 16:08:28
 */

package Sici.Partida;
import Funcoes.BackGroundDeskTopPane;
import Funcoes.CentralizaTela;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Funcoes.ResizeImageIcon;
import static com.jtattoo.plaf.noire.NoireLookAndFeel.setCurrentTheme;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Properties;
import javax.swing.UIManager;
import mondrian.rolap.Test;
/**
 *
 * @author supervisor
 */
public final class NewMenu extends javax.swing.JFrame {
    String regras = null;
    DbMain conn = VariaveisGlobais.conexao;
    jBuscaGlobalizada jBuscaGlobal;
    
    /** Creates new form jSuperMenu */
    public NewMenu() throws SQLException {
        initComponents();

        // Busca Globalizada
        VariaveisGlobais.jBuscar = jBuscar;
        
        try {
            // setup the look and feel properties
            Properties props = new Properties();

            props.put("logoString", "SoftElet"); 
            props.put("licenseKey", "INSERT YOUR LICENSE KEY HERE");
            //props.put("backgroundPattern","255, 255, 255");
            props.put("selectionForegroundColor","255, 0, 0");
            props.put("selectionBackgroundColor","255, 0, 255");

            // set your theme
            setCurrentTheme(props);
            // select the Look and Feel
            //UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
            UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
        } catch (Exception e) {}
        
        InicializaVariaveis();

        // Setar menu
        super.setJMenuBar(montaMenu()); 
        
        /**
         * maximiza a janela
         */
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        /**
         * Icone do modulo
         */
        URL url = this.getClass().getResource("/Figuras/imobilis.png");
        Image imagemTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(imagemTitulo);
        
        //Centraliza a janela.
        Dimension dimension = this.getToolkit().getScreenSize();
        int x = (int) (dimension.getWidth() - this.getSize().getWidth() ) / 2;
        int y = (int) (dimension.getHeight() - this.getSize().getHeight()) / 2;
        this.setLocation(x,y);

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        VariaveisGlobais.jPanePrin = jDesktopPane1;

        ID();
        
        jDesktopPane1.requestFocus();

        InitBuscaGlobal();
    }

    private void InitBuscaGlobal() {
        jBuscaGlobal = new jBuscaGlobalizada();
        jBuscaGlobal.setVisible(false);
        jBuscaGlobal.setClosable(true);
        jBuscaGlobal.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jBuscaGlobal.setTitle(".:: Busca Globalizada");
        jBuscaGlobal.setVisible(true);
        jBuscaGlobal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jBuscaGlobal.setVisible(false);
            }
        });        

        jDesktopPane1.add(jBuscaGlobal);
        jBuscaGlobal.setBounds(380, 40, 570, 250);
        jBuscaGlobal.setVisible(false);
    }
    
    private JMenuBar montaMenu() {
        //Cria a barra  
        JMenuBar barraMenu = new JMenuBar();  
        barraMenu.setBackground(new java.awt.Color(153, 153, 255));
        barraMenu.setMaximumSize(new java.awt.Dimension(146, 146));

        SetarMenu(barraMenu);
        
        //Cria o menu  
        JMenu menuSair = new JMenu("Sair");  
        
        // Aqui vão os itens
        JMenuItem itemSair = new javax.swing.JMenuItem();
        itemSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        itemSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/stop.png"))); // NOI18N
        itemSair.setText("Logout");
        itemSair.setToolTipText("Logout do sistema...");
        itemSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
                try {
                    (new Main()).main(new String[]{""});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        menuSair.add(itemSair);

        // Aqui vão os itens
        JMenuItem itemFechar = new javax.swing.JMenuItem();
        itemFechar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        itemFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        itemFechar.setText("Encerrar o Sistema");
        itemFechar.setToolTipText("Sai completamente do programa...");
        itemFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.exit(0);
            }
        });
        menuSair.add(itemFechar);

        if (VariaveisGlobais.usuario.equalsIgnoreCase("SAMIC")) {
            // Menu de Calculos
            JMenuItem itemCalc = new javax.swing.JMenuItem();
            itemCalc.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
            itemCalc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
            itemCalc.setText("Calcular digito");
            itemCalc.setToolTipText("Calculos...");
            itemCalc.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JInternalFrame otela = new jCalDig();
                    jDesktopPane1.add(otela);
                    CentralizaTela.setCentro(otela, jDesktopPane1, 0, 0);

                    jDesktopPane1.getDesktopManager().activateFrame(otela);
                    otela.requestFocus();
                    //otela.setSelected(true);
                    otela.setVisible(true);                }
            });
            menuSair.add(itemCalc);
        }
        
        //Adiciona o menu na barra  
        barraMenu.add(menuSair);

        return barraMenu;
    }

    private boolean Setar(String[] _nodes, String _chave) {
        if (VariaveisGlobais.protocolomenu.trim().equalsIgnoreCase("")) return true;
        
        for (int z=0;z<_nodes.length;z++) {
            int pos = FuncoesGlobais.OcourCount(_nodes[z], ":", 2);
            String _node = _nodes[z].substring(0, pos);
            boolean _setar = (_chave.trim().equalsIgnoreCase(_node.trim()));
            if (_setar) {
                String[] _acesso = _nodes[z].split(":");
                if (_acesso[2].equalsIgnoreCase("1")) return true;
            }        
        }
        return false;
    }
    private void SetarMenu(JMenuBar barraMenu) {
        String protocolomenu = VariaveisGlobais.protocolomenu;
        String _tree = protocolomenu.replaceAll(";", ",");
        String[] _nodes = _tree.split(",");
        
        ResultSet mnu = conn.AbrirTabela("SELECT * FROM menu ORDER BY tipo,cod;", ResultSet.CONCUR_READ_ONLY);
        try {
            JMenu menuItem = null; JMenuItem item = null;
            while (mnu.next()) {
                int cod = mnu.getInt("cod");
                int tip = mnu.getInt("tipo");
                String nome = mnu.getString("nome");
                final String rotina = mnu.getString("rotina");
                String icone = mnu.getString("icone");
                final int _senha = mnu.getInt("senha");
                
                String _chave = tip + ":" + cod;
                
                if (cod == 0) {
                    //Cria o menu  
                    menuItem = new JMenu(nome);  
                    menuItem.setVisible(Setar(_nodes, _chave));
                } else {
                    // Adiciona o item ao menu
                    barraMenu.add(menuItem);
                    
                    // Aqui vão os itens
                    item = new javax.swing.JMenuItem();
                    //item.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
                    // Setar Icones
                    //ImageIcon ico = new ResizeImageIcon("I", "loca.png", 16, 16).getImg();
                    try{
                        item.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/" + icone ))); // NOI18N
                    } catch (Exception e) {}
                    item.setText(nome);
                    //item.setToolTipText("Sai completamente do programa...");
                    item.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            Class classe = null;  
                            try {  
                                boolean pode = true;
                                if (_senha > 0) {
                                    jAutoriza oAut = new jAutoriza(null, true);
                                    oAut.setVisible(true);
                                    pode = oAut.pode;
                                }
                                
                                if (pode) {
                                    classe = Class.forName(rotina);  
                                    JInternalFrame frame = (JInternalFrame) classe.newInstance();  
                                    jDesktopPane1.add(frame);
                                    CentralizaTela.setCentro(frame, jDesktopPane1, 0, 0);

                                    jDesktopPane1.getDesktopManager().activateFrame(frame);
                                    frame.requestFocus();
                                    frame.setSelected(true);
                                    frame.setVisible(true);  
                                }
                            } catch (Exception ex) {  
                                ex.printStackTrace();  
                            }  
                        }
                    });
                    
                    item.setVisible(Setar(_nodes,_chave));
                    menuItem.add(item);
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        DbMain.FecharTabela(mnu);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jDesktopPane1 = new BackGroundDeskTopPane("/Figuras/fundoimobilis.png");
        jStatus = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jBuscar = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jUsuario = new javax.swing.JLabel();
        jInicio = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jOS = new javax.swing.JLabel();
        jLocal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jBaseDados = new javax.swing.JLabel();
        jtermica = new javax.swing.JLabel();
        jRelogio = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jIMG = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(".:: SICI - Sistema Integrado para Controle de Imobiliárias");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jDesktopPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jStatus.setBackground(new java.awt.Color(254, 254, 254));
        jStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/search.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        jBuscar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel3.setText("Utilizador:");

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel4.setText("Data Login:");

        jUsuario.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jUsuario.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jInicio.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jInicio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("S.O.:");

        jLabel6.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel6.setText("Local:");

        jOS.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jOS.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLocal.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jLocal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel7.setText("Impressoras:");

        jLabel8.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel8.setText("Licensa para:");

        jBaseDados.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jBaseDados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jtermica.setBackground(new java.awt.Color(255, 255, 255));
        jtermica.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jtermica.setForeground(new java.awt.Color(0, 153, 0));
        jtermica.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jtermica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtermicaMouseReleased(evt);
            }
        });

        jRelogio.setFont(new java.awt.Font("Ubuntu", 1, 24)); // NOI18N
        jRelogio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRelogio.setText("00:00.00");
        jRelogio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jStatusLayout = new javax.swing.GroupLayout(jStatus);
        jStatus.setLayout(jStatusLayout);
        jStatusLayout.setHorizontalGroup(
            jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jStatusLayout.createSequentialGroup()
                .addComponent(jIMG, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel6))
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jOS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jBaseDados, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addComponent(jtermica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jRelogio)
                .addGap(0, 0, 0)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                .addContainerGap())
        );
        jStatusLayout.setVerticalGroup(
            jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jStatusLayout.createSequentialGroup()
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jUsuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jOS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtermica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                    .addComponent(jBaseDados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLocal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jSeparator8)
            .addComponent(jIMG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jStatusLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jBuscar)
            .addComponent(jSeparator9, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jRelogio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jDesktopPane1.add(jStatus);
        jStatus.setBounds(0, 0, 958, 38);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 958, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 869, Short.MAX_VALUE)
                .addGap(0, 3, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    private void ID() {
        ImageIcon img = new ResizeImageIcon("E", "resources/logos/boleta/" + VariaveisGlobais.marca.toLowerCase().trim() + ".gif", jIMG.getWidth(), jIMG.getHeight()).getImg();
        jIMG.setIcon(img);
        
        jUsuario.setText(VariaveisGlobais.usuario);
        if (VariaveisGlobais.local) {
            jUsuario.setForeground(Color.BLACK);
        } else {
            jUsuario.setForeground(Color.red);
        }
        
        jInicio.setText(new Date().toString());

        // 12-04-2012
        jOS.setText(System.getProperty("os.name") + " - " + System.getProperty("os.version"));
        jLocal.setText(VariaveisGlobais.unidade);
        jBaseDados.setText("Erica Santos"); //VariaveisGlobais.dbnome);
        //jtermica.setText(VariaveisGlobais.DefaultThermalPort);
        jtermica.setText(VariaveisGlobais.statPrinter ? "Ligada" : "Desligada");
        
        ActionListener action = new ActionListener() {
        public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
            jRelogio.setText(Dates.DateFormata("HH:mm.ss", new Date()));
            }
        };
        Timer t = new Timer(1000, action);
        t.start();        

    }
    
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jStatus.setBounds(0, 0, jDesktopPane1.getWidth(), 38);
    }//GEN-LAST:event_formComponentResized

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void jtermicaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtermicaMouseReleased
        //jtermica.setEnabled(!jtermica.isEnabled());
        jtermica.setForeground(!VariaveisGlobais.statPrinter ? new java.awt.Color(0, 153, 0) : Color.RED);
        VariaveisGlobais.statPrinter = !VariaveisGlobais.statPrinter;
        jtermica.setText(VariaveisGlobais.statPrinter ? "Ligada" : "Desligada");
    }//GEN-LAST:event_jtermicaMouseReleased

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        Test.main(new String[] {});
    }//GEN-LAST:event_jLabel1MouseClicked
    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new NewMenu().setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void InicializaVariaveis() throws SQLException {
        DbMain conn = VariaveisGlobais.conexao;
        VariaveisGlobais.dCliente.add("empresa", conn.LerParametros("EMPRESA"));
        VariaveisGlobais.dCliente.add("endereco", conn.LerParametros("ENDERECO"));
        VariaveisGlobais.dCliente.add("numero", conn.LerParametros("NUMERO"));
        VariaveisGlobais.dCliente.add("complemento", conn.LerParametros("COMPLEMENTO"));
        VariaveisGlobais.dCliente.add("bairro", conn.LerParametros("BAIRRO"));
        VariaveisGlobais.dCliente.add("cidade", conn.LerParametros("CIDADE"));
        VariaveisGlobais.dCliente.add("estado", conn.LerParametros("ESTADO"));
        VariaveisGlobais.dCliente.add("cep", conn.LerParametros("CEP"));
        VariaveisGlobais.dCliente.add("cnpj", conn.LerParametros("CNPJ"));
        VariaveisGlobais.dCliente.add("tipodoc", conn.LerParametros("TIPODOC"));
        VariaveisGlobais.dCliente.add("inscricao", conn.LerParametros("INSCRICAO"));
        VariaveisGlobais.dCliente.add("tipoinsc", conn.LerParametros("TIPOINSC"));
        VariaveisGlobais.dCliente.add("marca", conn.LerParametros("MARCA"));
        VariaveisGlobais.dCliente.add("telefone", conn.LerParametros("TELEFONE"));
        VariaveisGlobais.dCliente.add("hpage", conn.LerParametros("HPAGE"));
        VariaveisGlobais.dCliente.add("email", conn.LerParametros("EMAIL"));
        VariaveisGlobais.dCliente.add("recibo", conn.LerParametros("RECIBO"));

        VariaveisGlobais.cContas.add("PR", "00");  // Proprietario
        VariaveisGlobais.cContas.add("LC", "01");  // Locatarios
        VariaveisGlobais.cContas.add("PC", "02");  // Passagem Caixa
        VariaveisGlobais.cContas.add("CX", "03");  // Fechamento Caixa
        VariaveisGlobais.cContas.add("NT", "04");  // Taxas
        VariaveisGlobais.cContas.add("AL", "05");  // Alugueres
        VariaveisGlobais.cContas.add("EN", "06");  // Encargos
        VariaveisGlobais.cContas.add("CM", "07");  // Comissão
        VariaveisGlobais.cContas.add("SO", "08");  // Sócio
        VariaveisGlobais.cContas.add("RT", "09");  // Retenção
        VariaveisGlobais.cContas.add("SD", "10");  // Saldo
        VariaveisGlobais.cContas.add("EP", "12");  // Expediente
        VariaveisGlobais.cContas.add("SG", "13");  // Seguro
        VariaveisGlobais.cContas.add("DC", "14");  // Desconto
        VariaveisGlobais.cContas.add("DF", "15");  // Diferença
        VariaveisGlobais.cContas.add("GG", "16");  // Adm Valores
        VariaveisGlobais.cContas.add("CA", "17");  // Contas da Adn
        VariaveisGlobais.cContas.add("AT", "18");  // Antecipações

        VariaveisGlobais.marca = conn.LerParametros("MARCA").toLowerCase().trim();
        // logomarcas
        VariaveisGlobais.icoBoleta = System.getProperty("icoBoleta", VariaveisGlobais.marca + ".gif");
        VariaveisGlobais.icoExtrato = System.getProperty("icoExtrato", VariaveisGlobais.marca + ".gif");

        // Nomes das contas do sistema
        String sSql = "SELECT CODIGO, DESCR FROM ADM;";
        ResultSet hresult = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        while (hresult.next()) {
            String campo = hresult.getString("DESCR");
            String chave =  hresult.getString("CODIGO");
            VariaveisGlobais.dCliente.add(chave,campo);
        }
        DbMain.FecharTabela(hresult);
        
        try {regras = conn.LerParametros("REGRAS");} catch (Exception ex) {}

        try {VariaveisGlobais.bcobol = conn.LerParametros("BCOBOL");} catch (Exception ex) {VariaveisGlobais.bcobol = "itau";}
        
        // site
        try {VariaveisGlobais.siteIP = conn.LerParametros("siteIP");} catch (Exception e) {VariaveisGlobais.siteIP = "";}
        try {VariaveisGlobais.siteUser = conn.LerParametros("siteUser");} catch (Exception e) {VariaveisGlobais.siteUser = "";}
        try {VariaveisGlobais.sitePwd = conn.LerParametros("sitePwd");} catch (Exception e) {VariaveisGlobais.sitePwd = "";}
        try {VariaveisGlobais.siteDbName = conn.LerParametros("siteDbName");} catch (Exception e) {VariaveisGlobais.siteDbName = "";}
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jBaseDados;
    private javax.swing.JTextField jBuscar;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jIMG;
    private javax.swing.JLabel jInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLocal;
    private javax.swing.JLabel jOS;
    private javax.swing.JLabel jRelogio;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JPanel jStatus;
    private javax.swing.JLabel jUsuario;
    private javax.swing.JLabel jtermica;
    // End of variables declaration//GEN-END:variables

}
