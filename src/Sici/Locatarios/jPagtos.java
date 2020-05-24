/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jPagtos.java
 *
 * Created on 08/08/2011, 10:53:24
 */
package Sici.Locatarios;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import Sici.Partida.Collections;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author supervisor
 */
public class jPagtos extends javax.swing.JDialog {
    DbMain conn = VariaveisGlobais.conexao;
    String contrato = ""; String rgprp = "";
    boolean achou = false; String oldInfo = "";

    /** Creates new form jPagtos */
    public jPagtos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        setLocationRelativeTo(null);
        
        conn.ExisteTabelaFichas();
        TableControl.header(jLista, new String[][] {{"Vencimento","Pagamento","Valor", "Caixa", "Campo"},{"100","100","0","100","0"}});
    }

    public void MontaLista(String contrato, String rgprp) {
        TableControl.header(jLista, new String[][] {{"Vencimento","Pagamento","Valor", "Caixa", "Campo"},{"100","100","0","100","0"}});
        String sql = "SELECT r.rgprp, r.rgimv, r.contrato, r.dtvencimento, r.autenticacao, r.campo, " +
                     "a.dtrecebimento, a.rc_aut FROM RECIBO r, auxiliar a WHERE a.rgprp = r.rgprp " +
                     "AND a.contrato = r.contrato AND a.rc_aut = r.autenticacao " +
                     "AND a.conta = 'REC' AND a.dtvencimento = r.dtvencimento AND r.rgprp = '" + rgprp + "' AND r.contrato = '" + contrato + "' " +
                     "AND r.tag = 'X' ORDER BY r.dtvencimento;";
        //"SELECT rgprp, rgimv, contrato, dtvencimento, dtrecebimento, rc_aut, campo FROM auxiliar WHERE conta = 'REC' AND rgprp = '" + rgprp + "' AND contrato = '" + contrato + "' ORDER BY dtvencimento;";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String svecto = rs.getString("dtvencimento");
                String spagto = rs.getString("dtrecebimento");
                String scampo = rs.getString("campo");
                String trgprp = rs.getString("rgprp");
                float svrpago = CalcularRecibo(trgprp,
                        rs.getString("rgimv"),
                        rs.getString("contrato"),
                        Dates.DateFormata("dd/MM/yyyy",Dates.StringtoDate(rs.getString("dtvencimento"),"yyyy/MM/dd")),
                        true);
                
                String pSql = ""; String pWhere = FuncoesGlobais.Subst("cx_aut='&1.'", new String[] {rs.getString("rc_aut")});
                if (spagto.equals(Dates.DateFormata("yyyy-MM-dd", new Date()))) {
                    pSql = "caixa";
                } else {
                    pSql = "caixabck";
                }
                String[][] dCaixa = conn.LerCamposTabela(new String[] {"cx_hora", "cx_logado", "cx_tipopg"}, pSql, pWhere);
                String cxSaida;
                if (dCaixa == null) {cxSaida = "";} else { cxSaida = dCaixa[0][3] + "[" + dCaixa[2][3] + " - " + dCaixa[1][3] + "]"; }
                TableControl.add(jLista, new String[][] {{Dates.DateFormata("dd/MM/yyyy",Dates.StringtoDate(svecto,"yyyy/MM/dd")), 
                                 Dates.DateFormata("dd/MM/yyyy",Dates.StringtoDate(spagto,"yyyy/MM/dd")),
                                 LerValor.floatToCurrency(svrpago, 2),
                                 cxSaida,scampo},{"C","C","R","L","L"}}, true);
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);
        this.contrato = contrato;
        this.rgprp = rgprp;
    }
    
    private float CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto, boolean soAluguel) {
        String rcampo = "";
        boolean executando = false; boolean mCartVazio = false;

        // campos de calculos
        float exp = 0;
        float multa = 0;
        float juros = 0;
        float correcao = 0;

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String campo = "";
        String sql = "SELECT r.rgprp, r.rgimv, r.contrato, r.dtvencimento, r.autenticacao, r.campo, " +
                     "a.dtrecebimento, a.rc_aut FROM RECIBO r, auxiliar a WHERE a.rgprp = r.rgprp " +
                     "AND a.contrato = r.contrato AND a.rc_aut = r.autenticacao " +
                     "AND a.conta = 'REC' AND r.rgprp = '" + rgprp + "' AND r.contrato = '" + contrato + "' " +
                     "AND r.tag = 'X' AND r.dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "' ORDER BY r.dtvencimento;";
                //"SELECT * FROM auxiliar WHERE conta = 'REC' AND rgprp = '" + rgprp + "' AND contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
            ex.printStackTrace();
        }
        DbMain.FecharTabela(pResult);

        try {
            exp = rc.TaxaExp(campo);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            multa = rc.Multa(campo, vecto, false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            juros = rc.Juros(campo, vecto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            correcao = rc.Correcao(campo, vecto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        return tRecibo + (soAluguel ? 0 : exp + multa + juros + correcao);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jLista = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jInfo = new javax.swing.JTextArea();
        jAlterar = new javax.swing.JButton();
        jGravar = new javax.swing.JButton();
        jCancelar = new javax.swing.JButton();
        jScroll = new javax.swing.JScrollPane();
        jctCampos = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Pagamentos do Locatário ::.");
        setAlwaysOnTop(true);
        setModal(true);
        setResizable(false);

        jLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jLista);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));

        jInfo.setColumns(20);
        jInfo.setEditable(false);
        jInfo.setRows(5);
        jScrollPane2.setViewportView(jInfo);

        jAlterar.setMnemonic('A');
        jAlterar.setText("Alterar");
        jAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAlterarActionPerformed(evt);
            }
        });

        jGravar.setMnemonic('G');
        jGravar.setText("Gravar");
        jGravar.setEnabled(false);
        jGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGravarActionPerformed(evt);
            }
        });

        jCancelar.setMnemonic('C');
        jCancelar.setText("Cancelar");
        jCancelar.setEnabled(false);
        jCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCancelarActionPerformed(evt);
            }
        });

        jScroll.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Visualização do Recibo"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScroll.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jctCampos.setEnabled(false);

        javax.swing.GroupLayout jctCamposLayout = new javax.swing.GroupLayout(jctCampos);
        jctCampos.setLayout(jctCamposLayout);
        jctCamposLayout.setHorizontalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );
        jctCamposLayout.setVerticalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );

        jScroll.setViewportView(jctCampos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(jGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jGravar)
                    .addComponent(jAlterar)
                    .addComponent(jCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListaMouseReleased
        // CREATE TABLE `WinGerGambasAlc`.`FICHAS` (`id` INTEGER  NOT NULL AUTO_INCREMENT, `contrato` VARCHAR(6) , `dtvencimento` VARCHAR(10) , `anotacoes` LONGTEXT , PRIMARY KEY (`id`) ) ENGINE = MyISAM COMMENT = 'fichas amarelas';
        int selRow = jLista.getSelectedRow();
        if (selRow > -1) {
            String tcontrato = this.contrato;
            String tvencimento = (String) jLista.getModel().getValueAt(selRow, 0);
            MostraRecibo(tvencimento, rgprp);

            jInfo.setText("");
            String pWhere = FuncoesGlobais.Subst("contrato='&1.' AND dtvencimento='&2.'", new String[] {tcontrato, tvencimento});
            String[][] dbInfo = null;
            try {
                dbInfo = conn.LerCamposTabela(new String[]{"anotacoes"}, "FICHAS", pWhere);
            } catch (SQLException ex) {
                Logger.getLogger(jPagtos.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (dbInfo != null) {
                oldInfo = dbInfo[0][3];
                jInfo.setText(oldInfo);
                achou = true;
            } else { achou = false; oldInfo = ""; }
        }
    }//GEN-LAST:event_jListaMouseReleased

    private void MostraRecibo(String vecto, String rgprp) {
        try {
            MontaTela(vecto, rgprp);
        } catch (SQLException ex) {
            Logger.getLogger(jPagtos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(jPagtos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void MontaTela(String vecto, String rgprp) throws SQLException, ParseException {
        if ("".equals(vecto.trim())) { return; }

        // Limpa campos
        jctCampos.removeAll();
        jctCampos.repaint();

        String sql = "SELECT r.rgprp, r.rgimv, r.contrato, r.dtvencimento, r.autenticacao, r.campo, " +
                     "a.dtrecebimento, a.rc_aut FROM RECIBO r, auxiliar a WHERE a.rgprp = r.rgprp " +
                     "AND a.contrato = r.contrato AND a.rc_aut = r.autenticacao " +
                     "AND a.conta = 'REC' AND r.rgprp = '" + rgprp + "' AND r.contrato = '" + contrato + "' " +
                     "AND r.tag = 'X' AND r.dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "' ORDER BY r.dtvencimento;";
                //"SELECT * FROM auxiliar WHERE conta = 'REC' AND rgprp = '" + rgprp + "' AND contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);

        String rc_aut = "";
        if (pResult.first()) {
            rc_aut = pResult.getString("rc_aut");
            
            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
            VariaveisGlobais.ccampos = pResult.getString("campo");

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

        DbMain.FecharTabela(pResult);
        
        
        // Mostra MU/JU/CO/EP ADM
        sql = "SELECT campo FROM auxiliar where conta = 'ADM' AND rc_aut = '" + rc_aut + "'";
        pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        
        String expCampos = null;
        if (pResult.first()) {
            expCampos = pResult.getString("campo").trim();
        }
        DbMain.FecharTabela(pResult);
        
        float adm_exp = 0; float rec_exp = 0;
        float adm_multa = 0; float rec_multa = 0;
        float adm_juros = 0; float rec_juros = 0;
        float adm_correcao = 0; float rec_correcao = 0;
        if (expCampos != null) {
            boolean bMulta = RetPar(expCampos, "MU");
            if (bMulta) { adm_multa = RetVarPar(expCampos, "MU"); }

            boolean bJuros = RetPar(expCampos, "JU");
            if (bJuros) { adm_juros = RetVarPar(expCampos, "JU"); }
            
            boolean bCorr = RetPar(expCampos, "CO");
            if (bCorr) { adm_correcao = RetVarPar(expCampos, "CO"); }

            boolean bExp = RetPar(expCampos, "EP");
            if (bExp) { adm_exp = RetVarPar(expCampos, "EP"); }
        }
        
        // Mostra MU/JU/CO/EP RECIBO
        sql = "SELECT campo FROM auxiliar where conta = 'REC' AND rc_aut = '" + rc_aut + "'";
        pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        
        String recCampos = null;
        if (pResult.first()) {
            recCampos = pResult.getString("campo").trim();
        }
        DbMain.FecharTabela(pResult);
        
        if (recCampos != null) {
            boolean bMulta = RetPar(recCampos, "MU");
            if (bMulta) { rec_multa = RetVarPar(recCampos, "MU"); }

            boolean bJuros = RetPar(recCampos, "JU");
            if (bJuros) { rec_juros = RetVarPar(recCampos, "JU"); }
            
            boolean bCorr = RetPar(recCampos, "CO");
            if (bCorr) { rec_correcao = RetVarPar(recCampos, "CO"); }

            boolean bExp = RetPar(recCampos, "EP");
            if (bExp) { rec_exp = RetVarPar(recCampos, "EP"); }
        }

        int i = VariaveisGlobais.ccampos.split(";").length;
        Collections gVar = VariaveisGlobais.dCliente;

        if (adm_multa + rec_multa > 0) MontaCampos2(gVar.get("MU"), adm_multa + rec_multa, ++i);
        if (adm_juros + rec_juros > 0) MontaCampos2(gVar.get("JU"), adm_juros + rec_juros, ++i);
        if (adm_correcao + rec_correcao > 0) MontaCampos2(gVar.get("CO"), adm_correcao + rec_correcao, ++i);
        if (adm_exp + rec_exp > 0) MontaCampos2(gVar.get("EP"), adm_exp + rec_exp, ++i);
                
    }

    public static boolean RetPar(String campo, String oque) {
        return (campo.contains(oque));
    }

    private float RetVarPar(String campos, String oque) {
        String mVrPar = "0000000000";
        int mIndex = campos.indexOf(oque,0);
        String mCpo = "";

        if (mIndex > -1) {
            mCpo = campos.substring(mIndex + 2, mIndex + 2 + 1);
            if (!":".equals(mCpo)) {
                mCpo = campos.substring(mIndex + 2, mIndex + 2 + 10);

                if (NumberUtils.isDigits(mCpo)) {
                    mVrPar = mCpo;
                }
            }
        }

        return LerValor.StringToFloat(LerValor.FormatNumber(mVrPar, 2));
    }
    
    private void MontaCampos2(String Label, float Valor, int i) {
        int at = 20; int llg = 180 + (VariaveisGlobais.bShowCotaParcela ? 10 : 0); int ltf = 120; int lcp = 60; int lcc = 278;
        int top = 5; int left = 5;
//        int at = 18; int llg = 150 + (VariaveisGlobais.bShowCotaParcela ? 10 : 0); int ltf = 90; int lcp = 60; int lcc = 278;
//        int top = 5; int left = 5;

        JLabel lb = new JLabel();
        lb.setText(Label);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(Color.BLACK);
        lb.setBounds(0 + left, 0 + (at * i) + top, llg, at);
        lb.setName("Label" + i);
        jctCampos.add(lb);

        JFormattedTextField tf = new JFormattedTextField();
        tf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        tf.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        tf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tf.setValue(Valor);
        tf.setVisible(true);
        tf.setDisabledTextColor(new Color(0,128,0));
        tf.setBounds(lb.getX() + llg, 0 + (at * i) + top, ltf, at);
        tf.setName("Field" + i);
        tf.setEditable(false);
        jctCampos.add(tf);

        jctCampos.repaint();
    }
    
    private void MontaCampos(String[] aCampos, int i) {
        int at = 20; int llg = 180 - (VariaveisGlobais.bShowCotaParcela ? 50 : 0); int ltf = 120; int lcp = 60; int lcc = 278;
        int top = 5; int left = 5;
//        int at = 18; int llg = 150 - (VariaveisGlobais.bShowCotaParcela ? 50 : 0); int ltf = 90; int lcp = 60; int lcc = 218;
//        int top = 5; int left = 5;

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
    
    private void jAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAlterarActionPerformed
        jAlterar.setEnabled(false);
        jCancelar.setEnabled(true);
        jGravar.setEnabled(true);
        jInfo.setEditable(true);
        jInfo.requestFocus();
    }//GEN-LAST:event_jAlterarActionPerformed

    private void jCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCancelarActionPerformed
        jAlterar.setEnabled(true);
        jCancelar.setEnabled(false);
        jGravar.setEnabled(false);
        jInfo.setText(oldInfo);
        jInfo.setEditable(false);
    }//GEN-LAST:event_jCancelarActionPerformed

    private void jGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGravarActionPerformed
        int selRow = jLista.getSelectedRow();
        if (selRow > -1) {
            String tcontrato = this.contrato;
            String tvencimento = (String) jLista.getModel().getValueAt(selRow, 0);

            String sql;
            if (achou) {
                sql = "UPDATE FICHAS SET anotacoes='" + jInfo.getText().trim() + "' WHERE contrato='" + tcontrato + "' AND dtvencimento='" + tvencimento + "';";
            } else {
                sql = "INSERT INTO FICHAS (contrato,dtvencimento,anotacoes) VALUES ('" + tcontrato + "','" + tvencimento + "','" + jInfo.getText().trim() + "');";
            }
            if (!"".equals(sql)) {
                conn.ExecutarComando(sql);
            }
        }

        jAlterar.setEnabled(true);
        jCancelar.setEnabled(false);
        jGravar.setEnabled(false);
        oldInfo = jInfo.getText();
        jInfo.setEditable(false);
    }//GEN-LAST:event_jGravarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                jPagtos dialog = new jPagtos(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAlterar;
    private javax.swing.JButton jCancelar;
    private javax.swing.JButton jGravar;
    private javax.swing.JTextArea jInfo;
    private javax.swing.JTable jLista;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jctCampos;
    // End of variables declaration//GEN-END:variables
}
