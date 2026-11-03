package benutzerschnittstelle;

import java.awt.EventQueue;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import steuerung.Steuerung;

public class BenutzerschnittstelleKasse extends JFrame {
	
	private JPanel contentPane;
	private JPanel pnlPositionen;
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
		//lblSuchleiste.setForeground(new Color(150, 150, 150));
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
		
		
		
		pnlPositionen = new JPanel();
		pnlPositionen.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlPositionen.setBounds(150, 200, 500, 231);
		//contentPane.add(pnlPositionen);
		pnlPositionen.setLayout(null);
		
		dieSteuerung = new Steuerung(this);
		
		//btnPositionStornieren = new ArrayList<JButton>();
		
		String[] spalten = {
				"Produktnummer", 
				"Bezeichnung", 
				"Preis pro Einheit", 
				"Umst.",
				"Menge",
				"Betrag"
				};
		
		
        Object[][] daten = {
        		/*
            {"2901234500008", "Spülmittel Zitrone 500 ml", "9999 € pro KG", "17%", 9999.999, "9999.99€"},
            {"2901234500008", "Spülmittel Zitrone 500 ml", "2 € pro KG", "7%", 2.50, "5€"},
            {"2901234500008", "Spülmittel Zitrone 500 ml", "2 € pro KG", "7%", 2.50, "5€"},
            {"2901234500008", "Spülmittel Zitrone 500 ml", "9999 € pro KG", "17%", 9999.999, "9999.99€"},
            {"2901234500008", "Spülmittel Zitrone 500 ml", "9999 € pro KG", "17%", 9999.999, "9999.99€"},
            {"2901234500008", "Spülmittel Zitrone 500 ml", "9999 € pro KG", "17%", 9999.999, "9999.99€"},
            {"2901234500008", "Spülmittel Zitrone 500 ml", "9999 € pro KG", "17%", 9999.999, "9999.99€"},
            {"2901234500008", "Spülmittel Zitrone 500 ml", "9999 € pro KG", "17%", 9999.999, "9999.99€"},
            {"2901234500008", "Spülmittel Zitrone 500 ml", "9999 € pro KG", "17%", 9999.999, "9999.99€"},
            */
        };

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
        
        
        
        
        //tblPositionen.setBounds(10, 10, 400, 100);
        //tblPositionen.setLayout(null);

        scrollTblPositionen = new JScrollPane(tblPositionen);
        
        scrollTblPositionen.setBounds(60, 100, 600, 340);
        //scrollPane.setLayout(null);
        
        //pnlPositionen.add(tblPositionen);
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
        
        tblPositionen.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblPositionen.getSelectedRow();
                if (selectedRow != -1) {
                	btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(selectedRow));
                }
            }
        });
        
        schalteSichtbarkeitUm(false);
        
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
			zeigeMeldung(ex.getMessage());
		}
	}
	
	private void geklicktStornieren() {
		dieSteuerung.storniereGebuchtePosition(tblPositionen.getSelectedRow());
		zeigeGebuchtePositionen(-1);
		zeigeGesamtbetrag();
		zeigeGezahltUndRueckgeld();
	}
	
	private void geklicktMengeMinus() {
		try {
			dieSteuerung.verkleinereGebuchteMenge(tblPositionen.getSelectedRow());
			btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(tblPositionen.getSelectedRow()));
			zeigeGebuchtePositionen(tblPositionen.getSelectedRow());
			zeigeGesamtbetrag();
			zeigeGezahltUndRueckgeld();
		}
		catch (Exception ex)
		{
			zeigeMeldung(ex.getMessage());
		}
	}
	
	private void geklicktMengePlus() {
		try {
			dieSteuerung.vergroessereGebuchteMenge(tblPositionen.getSelectedRow());
			btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(tblPositionen.getSelectedRow()));
			zeigeGebuchtePositionen(tblPositionen.getSelectedRow());
			zeigeGesamtbetrag();
			zeigeGezahltUndRueckgeld();
		}
		catch (Exception ex)
		{
			zeigeMeldung(ex.getMessage());
		}
	}
	
	private void geklicktMengeBearbeiten() {
		try {
			String neueMenge = JOptionPane.showInputDialog("Geben Sie den gewünschten Betrag in STK ein." + "\n" + "Verwenden Sie eine gültige Zahl ohne Sonderzeichen.");
			int selectedRow = tblPositionen.getSelectedRow();
			if (neueMenge != null) {
				dieSteuerung.bearbeiteGebuchteMenge(selectedRow, neueMenge);
				btnPositionMenge.setText(dieSteuerung.ermittleGebuchteMengeAlsString(selectedRow));
				zeigeGebuchtePositionen(tblPositionen.getSelectedRow());
			}
			zeigeGesamtbetrag();
			zeigeGezahltUndRueckgeld();
		}
		catch (Exception ex)
		{
			zeigeMeldung(ex.getMessage());
		}
	}
	
	private void geklicktTransaktionAbschliessen() {
		dieSteuerung.schliesseTransaktionAb();
	}
	
	private void geklicktTransaktionZuruecksetzen() {
		dieSteuerung.setzeTransaktionZurueck();
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
			zeigeMeldung(ex.getMessage());
		}
	}
	
	private void geklicktGeldschublade() {
		
	}
	
	private void zeigeText(int pIndex) {
		
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
	
	private void zeigeMeldung(String pText)
	{
		JOptionPane.showMessageDialog(this, pText, "Kassensystem", JOptionPane.WARNING_MESSAGE);
	}
	
}
