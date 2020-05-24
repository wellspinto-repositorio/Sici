/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jIRF.java
 *
 * Created on 28/01/2012, 14:27:18
 */
package Movimento;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import Protocolo.DepuraCampos;
import java.text.ParseException;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

/**
 *
 * @author supervisor
 */
public class jIRF extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    String sql = "SELECT * FROM extrato where Year(dtrecebimento) = '2011' AND Month(dtrecebimento) = '12' AND InStr(campo,'01:1:') ORDER BY rgprp, rgimv, contrato;";
    ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
    
    /** Creates new form jIRF */
    public jIRF() {
        initComponents();
        
        try {
            while (rs.next()) { Ler(); }
        } catch (SQLException e) {}
    }

    private void Ler() {
        try {
            String jrgprp = rs.getString("rgprp"); jRGPRP.setText(jrgprp);
            String jrgimv = rs.getString("rgimv"); jRGIMV.setText(jrgimv);
            String jcontrato = rs.getString("contrato"); jCONTRATO.setText(jcontrato);
            jVENCIMENTO.setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("dtvencimento")));
            
            // Proprietario
            String[][] prop = conn.LerCamposTabela(new String[] {"nome","cpfcnpj"}, "proprietarios", "rgprp = '" + jrgprp + "'");
            jNOMEPROP.setText(prop[0][3]);
            jDOCPROP.setText(prop[1][3]);
            
            // Locatario
            String[][] loca = conn.LerCamposTabela(new String[] {"nomerazao","cpfcnpj"}, "locatarios", "contrato = '" + jcontrato + "'");
            jNOMELOCA.setText(loca[0][3]);
            jDOCLOCA.setText(loca[1][3]);
            
            // Imovel
            String[][] imv = conn.LerCamposTabela(new String[] {"tpimovel","end","num","compl","bairro","cidade","estado"}, "imoveis", "rgimv = '" + jrgimv + "'");
            jTIPOIMV.setText(imv[0][3]);
            jEND.setText(imv[1][3].trim() + "," + imv[2][3] + " " + imv[3][3]);
            jBAIRRO.setText(imv[4][3]); jCIDADE.setText(imv[5][3]); jESTADO.setText(imv[6][3]);
            
            String campo = rs.getString("campo");
            //MontaTela(campo);
            
            String[][] matrix = FuncoesGlobais.treeArray(campo, true);
            
            float aluguel = 0; float desconto = 0; float diferenca = 0; float imposto = 0;
            for (int i=0;i<matrix.length;i++) {
                // Aluguel
                if (matrix[i][matrix[i].length - 1].trim().toUpperCase().equalsIgnoreCase("ALUGUEL")) {
                    aluguel += LerValor.StringToFloat(LerValor.FormataCurrency(matrix[i][2]));
                }

                // desconto de aluguel
                if (matrix[i][matrix[i].length - 1].trim().toUpperCase().contains("DESC") && matrix[i][matrix[i].length - 1].trim().toUpperCase().contains("ALUG")) {
                    desconto += LerValor.StringToFloat(LerValor.FormataCurrency(matrix[i][2]));
                }
                
                // diferenca de aluguel
                if (matrix[i][matrix[i].length - 1].trim().toUpperCase().contains("DIF") && matrix[i][matrix[i].length - 1].trim().toUpperCase().contains("ALUG")) {
                    diferenca += LerValor.StringToFloat(LerValor.FormataCurrency(matrix[i][2]));
                }
                
                // desconto de ir
                if (matrix[i][matrix[i].length - 1].trim().toUpperCase().contains("DESC") && matrix[i][matrix[i].length - 1].trim().toUpperCase().contains("IR")) {
                    imposto += LerValor.StringToFloat(LerValor.FormataCurrency(matrix[i][2]));
                }   
            }

            String ins = "INSERT INTO irf (rgprp, nomeprop, docprop, rgimv, contrato, nomeloca, docloca, tipoimv, endimv, baiimv, cidimv, estimv, aluguel, desconto, diferenca, imposto) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.','&9.','&10.','&11.','&12.','&13.','&14.','&15.','&16.');";
            ins = FuncoesGlobais.Subst(ins, new String[] {jrgprp, prop[0][3], prop[1][3], jrgimv, jcontrato,
                                       loca[0][3], loca[1][3], 
                                       imv[0][3],
                                       imv[1][3].trim() + "," + imv[2][3] + " " + imv[3][3],
                                       imv[4][3], imv[5][3], imv[6][3], String.valueOf(aluguel),
                                       String.valueOf(desconto), 
                                       String.valueOf(diferenca),
                                       String.valueOf(imposto)});
            conn.ExecutarComando(ins);
            
        } catch (Exception e) {}
    }
    
    public void MontaTela(String campo) throws SQLException, ParseException {
        // Limpa campos
        jctCampos.removeAll();
        jctCampos.repaint();

        DepuraCampos a = new DepuraCampos(campo);
        VariaveisGlobais.ccampos = campo;

        a.SplitCampos();
        // Ordena Matriz
        Arrays.sort (a.aCampos, new Comparator()
        {
        private int pos1 = 3;
        private int pos2 = 4;
        public int compare(Object o1, Object o2) {
            String p1 = ((String)o1).substring(pos1, pos2);
            String p2 = ((String)o2).substring(pos1, pos2);
            return p1.compareTo(p2);
        }
        });

        // Monta campos
        int i = 0;
        for (i=0; i<= a.length() - 1; i++) {
            String[] Campo = a.Depurar(i);
            if (Campo.length > 0) {
                MontaCampos(Campo, i);
            }
        }
    }

    private void MontaCampos(String[] aCampos, int i) {
        int at = 20; int llg = 180 - (VariaveisGlobais.bShowCotaParcela ? 50 : 0); int ltf = 120; int lcp = 60; int lcc = 278;
        int top = 5; int left = 5;

        JLabel lb = new JLabel();
        lb.setText(aCampos[0]);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(Color.BLACK);
        lb.setBounds(0 + left, 0 + (at * i) + top, llg, at);
        lb.setName("Label" + i);
        jctCampos.add(lb);

        JFormattedTextField cp = null;
        if (VariaveisGlobais.bShowCotaParcela) {
            cp = new JFormattedTextField();
            try {
                cp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter((!"C".equals(aCampos[5]) ? "##/####" : "##/##"))));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            cp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            cp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            cp.setText(("".equals(aCampos[3]) ? "00/00" + (!"C".equals(aCampos[5]) ? "00" : "") : aCampos[3]));
            cp.setVisible(true);
            cp.setForeground(new Color(0,128,0));
            cp.setDisabledTextColor(new Color(0,128,0));
            cp.setBounds(lb.getX() + llg, 0 + (at * i) + top, lcp, at);
            cp.setName("Cota" + i);
            cp.setEditable(false);
            jctCampos.add(cp);
        }

        JFormattedTextField tf = new JFormattedTextField();
        tf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        tf.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        tf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tf.setText(aCampos[1]);
        tf.setVisible(true);
        tf.setDisabledTextColor(new Color(0,128,0));
        tf.setBounds((VariaveisGlobais.bShowCotaParcela ? cp.getX() + lcp : lb.getX() + llg), 0 + (at * i) + top, ltf, at);
        tf.setName("Field" + i);
        tf.setEditable(false);
        jctCampos.add(tf);

        jctCampos.repaint();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScroll = new javax.swing.JScrollPane();
        jctCampos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jRGPRP = new javax.swing.JTextField();
        jRGIMV = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jCONTRATO = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jFirst = new javax.swing.JButton();
        jPrevious = new javax.swing.JButton();
        jNext = new javax.swing.JButton();
        jLast = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jVENCIMENTO = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jNOMEPROP = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jDOCPROP = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jNOMELOCA = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jDOCLOCA = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTIPOIMV = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jEND = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jBAIRRO = new javax.swing.JTextField();
        jCIDADE = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jESTADO = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();

        setClosable(true);
        setTitle(".:: Lista Campos");
        setVisible(true);

        jScroll.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Recibo"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScroll.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jctCampos.setEnabled(false);

        javax.swing.GroupLayout jctCamposLayout = new javax.swing.GroupLayout(jctCampos);
        jctCampos.setLayout(jctCamposLayout);
        jctCamposLayout.setHorizontalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 860, Short.MAX_VALUE)
        );
        jctCamposLayout.setVerticalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );

        jScroll.setViewportView(jctCampos);

        jLabel1.setText("Rgprp:");

        jRGPRP.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jRGIMV.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel2.setText("RgImv:");

        jCONTRATO.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel3.setText("Contrato:");

        jFirst.setText("<<");
        jFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFirstActionPerformed(evt);
            }
        });

        jPrevious.setText("<");
        jPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPreviousActionPerformed(evt);
            }
        });

        jNext.setText(">");
        jNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextActionPerformed(evt);
            }
        });

        jLast.setText(">>");
        jLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLastActionPerformed(evt);
            }
        });

        jLabel4.setText("Vencimento:");

        jVENCIMENTO.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jVENCIMENTO.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("Nome:");

        jLabel6.setText("CPF/CNPJ:");

        jLabel7.setBackground(new java.awt.Color(204, 255, 204));
        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("DADOS DO PROPRIETARIO");
        jLabel7.setOpaque(true);

        jLabel8.setBackground(new java.awt.Color(204, 255, 204));
        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("DADOS DO IMOVEL");
        jLabel8.setOpaque(true);

        jLabel9.setText("Locatario:");

        jLabel10.setText("CPF/CNPJ:");

        jLabel11.setText("TIPO:");

        jLabel12.setText("Endereço:");

        jLabel13.setText("Bairro:");

        jLabel14.setText("Cidade:");

        jLabel15.setText("UF:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jNOMEPROP, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                            .addComponent(jDOCPROP, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jDOCLOCA, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTIPOIMV, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                            .addComponent(jNOMELOCA, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jEND, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jBAIRRO, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addGap(16, 16, 16)
                                .addComponent(jCIDADE, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jESTADO, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jNOMEPROP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jDOCPROP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jNOMELOCA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jDOCLOCA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jTIPOIMV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jEND, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBAIRRO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jCIDADE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jESTADO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addContainerGap(174, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jVENCIMENTO)
                            .addComponent(jCONTRATO)
                            .addComponent(jRGIMV)
                            .addComponent(jRGPRP, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPrevious)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jNext))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jFirst)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLast)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFirst, jLast, jNext, jPrevious});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jRGPRP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFirst)
                            .addComponent(jLast))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jRGIMV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jCONTRATO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPrevious)
                            .addComponent(jNext))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jVENCIMENTO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPreviousActionPerformed
        try {
            boolean previous = rs.previous();
            if (previous) Ler();
        } catch (SQLException ex) {}
    }//GEN-LAST:event_jPreviousActionPerformed

    private void jNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextActionPerformed
        try {
            boolean next = rs.next();
            if (next) Ler();
        } catch (SQLException ex) {}
    }//GEN-LAST:event_jNextActionPerformed

    private void jFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFirstActionPerformed
        try {
            boolean first = rs.first();
            if (first) Ler();
        } catch (SQLException e) {}
    }//GEN-LAST:event_jFirstActionPerformed

    private void jLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLastActionPerformed
        try {
            boolean last = rs.last();
            if (last) Ler();
        } catch (SQLException e) {}
    }//GEN-LAST:event_jLastActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jBAIRRO;
    private javax.swing.JTextField jCIDADE;
    private javax.swing.JTextField jCONTRATO;
    private javax.swing.JTextField jDOCLOCA;
    private javax.swing.JTextField jDOCPROP;
    private javax.swing.JTextField jEND;
    private javax.swing.JTextField jESTADO;
    private javax.swing.JButton jFirst;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jLast;
    private javax.swing.JTextField jNOMELOCA;
    private javax.swing.JTextField jNOMEPROP;
    private javax.swing.JButton jNext;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jPrevious;
    private javax.swing.JTextField jRGIMV;
    private javax.swing.JTextField jRGPRP;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JTextField jTIPOIMV;
    private javax.swing.JTextField jVENCIMENTO;
    private javax.swing.JPanel jctCampos;
    // End of variables declaration//GEN-END:variables
}
