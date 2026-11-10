package benutzerschnittstelle;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import steuerung.Steuerung;

public class BenutzerschnittstelleKasse extends JFrame {
	
	private JPanel contentPane;
	//private JPanel pnlPositionen;
	private JTextField txtSuchleiste;
	private JLabel lblSuchleiste;
	private JButton btnBuchen;
	
	private JLabel lblEinleitung;
	
	private JTable tblPositionen;
	private DefaultTableModel mdlPositionen;
	private JScrollPane scrollTblPositionen;
	
	private JLabel lblPositionKontrolle;
	private JButton btnPositionMengeMinus;
	private JButton btnPositionMenge;
	private JButton btnPositionMengePlus;
	private JButton btnPositionStornieren;
	
	private JLabel lblGesamtbetrag;
	private JLabel lblGezahlt;
	private JLabel lblRueckgeld;
	
	private JButton btnTransaktionZuruecksetzen;
	private JButton btnZahlungErfassen;
	private JButton btnTransaktionAbschliessen;
	
	private JButton btnGeldschublade;
	
	private InputMap InputMap;
	private ActionMap ActionMap;
	
	private Steuerung dieSteuerung;
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					BenutzerschnittstelleKasse frame = new BenutzerschnittstelleKasse();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	public BenutzerschnittstelleKasse() {
		
		setTitle("Kassensystem");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 400, 1000, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblSuchleiste = new JLabel("Produktnummer:");
		lblSuchleiste.setBounds(60, 55, 300, 20);
		contentPane.add(lblSuchleiste);
		lblSuchleiste.setLayout(null);
		
		txtSuchleiste = new JTextField();
		txtSuchleiste.setBounds(170, 50, 120, 30);
		contentPane.add(txtSuchleiste);
		txtSuchleiste.setLayout(null);
		txtSuchleiste.addActionListener(e -> geklicktBuchen());
		
		btnBuchen = new JButton("Buchen");
		btnBuchen.setBounds(300, 50, 100, 30);
		contentPane.add(btnBuchen);
		btnBuchen.setLayout(null);
		btnBuchen.addActionListener(e -> geklicktBuchen());
		
		lblPositionKontrolle = new JLabel("Ausgewählte Position:");
		lblPositionKontrolle.setBounds(690, 100, 500, 20);
		contentPane.add(lblPositionKontrolle);
		lblPositionKontrolle.setLayout(null);
		
		btnPositionMengeMinus = new JButton("-");
		btnPositionMengeMinus.setBounds(685, 120, 40, 30);
		contentPane.add(btnPositionMengeMinus);
		btnPositionMengeMinus.setLayout(null);
		btnPositionMengeMinus.addActionListener(e -> geklicktMengeMinus());
		
		btnPositionMenge = new JButton("9999.999");
		btnPositionMenge.setBounds(725, 120, 120, 30);
		contentPane.add(btnPositionMenge);
		btnPositionMenge.setLayout(null);
		btnPositionMenge.addActionListener(e -> geklicktMengeBearbeiten());
		
		btnPositionMengePlus = new JButton("+");
		btnPositionMengePlus.setBounds(845, 120, 40, 30);
		contentPane.add(btnPositionMengePlus);
		btnPositionMengePlus.setLayout(null);
		btnPositionMengePlus.addActionListener(e -> geklicktMengePlus());
		
		btnPositionStornieren = new JButton("Stornieren");
		btnPositionStornieren.setBounds(685, 150, 200, 30);
		contentPane.add(btnPositionStornieren);
		btnPositionStornieren.setLayout(null);
		btnPositionStornieren.addActionListener(e -> geklicktStornieren());
		
		dieSteuerung = new Steuerung();
		
		String[] spalten = {
				"Produktnummer", 
				"Bezeichnung", 
				"Preis pro Einheit", 
				"Umst.",
				"Menge",
				"Betrag"
				};
		
        Object[][] daten = {};

        mdlPositionen = new DefaultTableModel(daten, spalten) {
        	@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPositionen = new JTable(mdlPositionen);
        tblPositionen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPositionen.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblPositionen.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblPositionen.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblPositionen.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblPositionen.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblPositionen.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        lblGesamtbetrag = new JLabel("Gesamtbetrag: 9999,99 €");
        lblGesamtbetrag.setBounds(690, 190, 260, 30);
        contentPane.add(lblGesamtbetrag);
        
        lblGezahlt = new JLabel("Gezahlt: 9999,99 €");
        lblGezahlt.setBounds(690, 320, 260, 30);
        contentPane.add(lblGezahlt);
        
        lblRueckgeld = new JLabel("Rückgeld: 9999,99 €");
        lblRueckgeld.setBounds(690, 340, 260, 30);
        contentPane.add(lblRueckgeld);

        scrollTblPositionen = new JScrollPane(tblPositionen);
        
        scrollTblPositionen.setBounds(60, 100, 600, 340);
        
        contentPane.add(scrollTblPositionen);
        
        lblEinleitung = new JLabel("Scannen Sie ein Produkt, um mit der Kassiervorgang loszulegen.");
        lblEinleitung.setBounds(60, 100, 600, 20);
		contentPane.add(lblEinleitung);
        
        btnTransaktionZuruecksetzen = new JButton("Transaktion zurücksetzen");
        btnTransaktionZuruecksetzen.setBounds(685, 220, 200, 30);
        contentPane.add(btnTransaktionZuruecksetzen);
        btnTransaktionZuruecksetzen.addActionListener(e -> geklicktTransaktionZuruecksetzen());
        
        btnZahlungErfassen = new JButton("Zahlung erfassen");
        btnZahlungErfassen.setBounds(685, 295, 200, 30);
        contentPane.add(btnZahlungErfassen);
        btnZahlungErfassen.addActionListener(e -> geklicktZahlungErfasssen());
        
        btnTransaktionAbschliessen = new JButton("Transaktion abschließen");
        btnTransaktionAbschliessen.setBounds(685, 410, 200, 35);
        contentPane.add(btnTransaktionAbschliessen);
        btnTransaktionAbschliessen.addActionListener(e -> geklicktTransaktionAbschliessen());
        
        btnGeldschublade = new JButton("Geldschublade öffnen");
        btnGeldschublade.setBounds(685, 365, 200, 30);
        contentPane.add(btnGeldschublade);
        btnGeldschublade.addActionListener(e -> geklicktGeldschublade());
        
        tblPositionen.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblPositionen.getSelectedRow();
                if (selectedRow != -1) {
                	btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(selectedRow));
                }
            }
            txtSuchleiste.requestFocusInWindow();
        });
        
        schalteSichtbarkeitUm(false);
        
        InputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    	ActionMap = contentPane.getActionMap();
        
        InputMap.put(KeyStroke.getKeyStroke("released UP"), "upReleased");
        ActionMap.put("upReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	wechsleZeile(true);
            }
        });
        
        InputMap.put(KeyStroke.getKeyStroke("released DOWN"), "downReleased");
        ActionMap.put("downReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	wechsleZeile(false);
            }
        });
        
        InputMap.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        ActionMap.put("leftReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	geklicktMengeMinus();
            }
        });
        
        InputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        ActionMap.put("rightReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	geklicktMengePlus();
            }
        });
        
	}
	
	private void geklicktBuchen() {
		String ean = txtSuchleiste.getText().trim();
		if (ean.isEmpty())
		{
			return;
		}
		try
		{
			dieSteuerung.buchePosition(ean);
			txtSuchleiste.setText("");
			zeigeGebuchtePositionen(dieSteuerung.ermittleTransaktionGroesse()-1);
			btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(dieSteuerung.ermittleTransaktionGroesse()-1));
			zeigeGesamtbetrag();
			zeigeGezahltUndRueckgeld();
			
		}
		catch (Exception ex)
		{
			zeigeWarnmeldung(ex.getMessage());
		}
	}
	
	private void geklicktStornieren() {
		dieSteuerung.storniereGebuchtePosition(tblPositionen.getSelectedRow());
		zeigeGebuchtePositionen(-1);
		zeigeGesamtbetrag();
		zeigeGezahltUndRueckgeld();
		wechsleZeile(false);
	}
	
	private void geklicktMengeMinus() {
		try {
			int selectedRow = tblPositionen.getSelectedRow();
			if (selectedRow >= 0) {
				dieSteuerung.verkleinereGebuchteMenge(selectedRow);
				btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(selectedRow));
				zeigeGebuchtePositionen(selectedRow);
				zeigeGesamtbetrag();
				zeigeGezahltUndRueckgeld();
			}
		}
		catch (Exception ex)
		{
			zeigeWarnmeldung(ex.getMessage());
		}
	}
	
	private void geklicktMengePlus() {
		try {
			int selectedRow = tblPositionen.getSelectedRow();
			if (selectedRow >= 0) {
				dieSteuerung.vergroessereGebuchteMenge(selectedRow);
				btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(selectedRow));
				zeigeGebuchtePositionen(selectedRow);
				zeigeGesamtbetrag();
				zeigeGezahltUndRueckgeld();
			}
		}
		catch (Exception ex)
		{
			zeigeWarnmeldung(ex.getMessage());
		}
	}
	
	private void geklicktMengeBearbeiten() {
		try {
			String neueMenge = JOptionPane.showInputDialog("Geben Sie den gewünschten Betrag in STK ein." + "\n" + "Verwenden Sie eine gültige Zahl ohne Sonderzeichen.");
			int selectedRow = tblPositionen.getSelectedRow();
			if (neueMenge != null && !neueMenge.equals("")) {
				dieSteuerung.bearbeiteGebuchteMenge(selectedRow, neueMenge);
				btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(selectedRow));
				zeigeGebuchtePositionen(tblPositionen.getSelectedRow());
			}
			zeigeGesamtbetrag();
			zeigeGezahltUndRueckgeld();
		}
		catch (Exception ex)
		{
			zeigeWarnmeldung(ex.getMessage());
		}
		txtSuchleiste.requestFocusInWindow();
	}
	
	private void geklicktTransaktionAbschliessen() {
		try {
			dieSteuerung.schliesseTransaktionAb();
			zeigeGebuchtePositionen(-1);
			zeigeMeldung("Die Transaktion erfolgreich abgeschlossen.");
		}
		catch (Exception ex) {
			zeigeWarnmeldung("Nur noch " + dieSteuerung.ermittleRueckgeldAlsStringOhneMinus() + " fällig.");
		}
	}
	
	private void geklicktTransaktionZuruecksetzen() {
		dieSteuerung.setzeTransaktionZurueck();
		zeigeGebuchtePositionen(-1);
	}
	
	private void geklicktZahlungErfasssen() {
		try {
			String neuerBetrag = JOptionPane.showInputDialog("Geben Sie den gewünschten Betrag in € ein." + "\n" + "Verwenden Sie eine gültige Zahl ohne Sonderzeichen.");
			if (neuerBetrag != null && !neuerBetrag.equals("")) {
				dieSteuerung.erfasseZahlung(neuerBetrag);
				zeigeGezahltUndRueckgeld();
			}
		}
		catch (Exception ex)
		{
			zeigeWarnmeldung(ex.getMessage());
		}
		txtSuchleiste.requestFocusInWindow();
	}
	
	private void geklicktGeldschublade() {
		dieSteuerung.oeffneGeldschublade();
		txtSuchleiste.requestFocusInWindow();
	}
	
	private void zeigeGesamtbetrag() {
		lblGesamtbetrag.setText("Gesamtbetrag: " + dieSteuerung.ermittleGesamtbetragAlsString());
	}
	
	private void zeigeGezahltUndRueckgeld() {
		lblGezahlt.setText("Gezahlt: " + dieSteuerung.ermittleGezahltAlsString());
		
		String rueckgeldLabel = "Rückgeld: ";
		if (!dieSteuerung.genugGezahlt()) {
			rueckgeldLabel = "Fällig: ";
		}
		String rueckgeld = rueckgeldLabel + dieSteuerung.ermittleRueckgeldAlsStringOhneMinus();
		lblRueckgeld.setText(rueckgeld);
	}
	
	private void zeigeGebuchtePositionen(int pZeileZumAuswaehlen) {
		mdlPositionen.setRowCount(0);
		if (dieSteuerung.ermittleTransaktionGroesse() > 0) {
			schalteSichtbarkeitUm(true);
			for (int i = 0; i < dieSteuerung.ermittleTransaktionGroesse(); i++) {
				mdlPositionen.addRow(dieSteuerung.ermittleGebuchtePositiondatenAlsTabellenzeile(i));
			}
			if (pZeileZumAuswaehlen >= 0) {
				tblPositionen.setRowSelectionInterval(pZeileZumAuswaehlen, pZeileZumAuswaehlen);
			}
		}
		else  {
			schalteSichtbarkeitUm(false);
		}
		txtSuchleiste.requestFocusInWindow();
	}
	
	private void schalteSichtbarkeitUm(boolean angezeigt) {
		lblEinleitung.setVisible(!angezeigt);
		scrollTblPositionen.setVisible(angezeigt);
		btnPositionMengeMinus.setVisible(angezeigt);
		btnPositionMenge.setVisible(angezeigt);
		btnPositionMengePlus.setVisible(angezeigt);
		btnPositionStornieren.setVisible(angezeigt);
		lblPositionKontrolle.setVisible(angezeigt);
		lblGesamtbetrag.setVisible(angezeigt);
		lblGezahlt.setVisible(angezeigt);
		lblRueckgeld.setVisible(angezeigt);
		btnTransaktionZuruecksetzen.setVisible(angezeigt);
		btnZahlungErfassen.setVisible(angezeigt);
		btnTransaktionAbschliessen.setVisible(angezeigt);
		btnGeldschublade.setVisible(angezeigt);
	}
	
	private void wechsleZeile(boolean pNachOben) {
		int selectedRow = tblPositionen.getSelectedRow();
		int newSelectedRow;
		int tblPositionenSize = tblPositionen.getRowCount();
		if (tblPositionenSize > 0) {
			if (pNachOben) {
				newSelectedRow = selectedRow - 1;
				if (newSelectedRow < 0) {
					newSelectedRow = tblPositionenSize - 1;
				}
			}
			else {
				newSelectedRow = selectedRow + 1;
				if (newSelectedRow >= tblPositionenSize) {
					newSelectedRow = 0;
				}
			}
			tblPositionen.setRowSelectionInterval(newSelectedRow, newSelectedRow);
		}
	}
	
	private void zeigeWarnmeldung(String pText)
	{
		JOptionPane.showMessageDialog(this, pText, "Kassensystem", JOptionPane.WARNING_MESSAGE);
	}
	
	private void zeigeMeldung(String pText)
	{
		JOptionPane.showMessageDialog(this, pText, "Kassensystem", JOptionPane.INFORMATION_MESSAGE);
	}
	
}
