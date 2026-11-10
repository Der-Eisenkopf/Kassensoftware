package steuerung;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;

import fachkonzept.Transaktion;
import fachkonzept.Geldschublade;
import datenspeicherung.db.ProduktSpeicher;


public class Steuerung {
	
	private static final String KASSEN_KONFIG = "kasse.properties";
	
	private static NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMANY);
	// damit 1234.56 zum 1.234,56 wird
	
	private Transaktion dieTransaktion;
	
	private ProduktSpeicher derProduktSpeicher; // Mögliche Idee: db4free.net statt lokal
	
	private final Geldschublade geldschublade;
	
	public Steuerung() throws IOException
	{
		dieTransaktion = new Transaktion();
		derProduktSpeicher = new ProduktSpeicher();
		geldschublade = erzeugeGeldschublade();
	}
	// Die Datei ist optional; ohne sie laeuft die Geldschublade im Simulationsmodus
		private static Geldschublade erzeugeGeldschublade() throws IOException
		{
			if (!Files.exists(Paths.get(KASSEN_KONFIG)))
			{
				return new Geldschublade("COM6", 9600, Geldschublade.STANDARD_BEFEHL);
			}
			Properties props = new Properties();
			try (FileInputStream in = new FileInputStream(KASSEN_KONFIG))
			{
				props.load(in);
			}
			try
			{
				int baud = Integer.parseInt(props.getProperty("schublade.baud", "9600").trim());
				return new Geldschublade(props.getProperty("schublade.com"), baud,
						leseBefehl(props.getProperty("schublade.befehl")));
			}
			catch (IllegalArgumentException e)
			{
				throw new IOException("Fehler in " + KASSEN_KONFIG + ": " + e.getMessage(), e);
			}
		}
		
		// Befehl als Hex-Bytes, z. B. "1B 70 00 19 FA"; leer = ESC/POS-Standardbefehl
		private static byte[] leseBefehl(String hex)
		{
			if (hex == null || hex.trim().isEmpty())
			{
				return Geldschublade.STANDARD_BEFEHL;
			}
			String[] teile = hex.trim().split("[\\s,]+");
			byte[] befehl = new byte[teile.length];
			for (int i = 0; i < teile.length; i++)
			{
				befehl[i] = (byte) Integer.parseInt(teile[i], 16);
			}
			return befehl;
		}
		
	public void buchePosition(String pEAN) throws SQLException {
		dieTransaktion.buchePosition(derProduktSpeicher.laden(pEAN));
	}
	
	public void storniereGebuchtePosition(int pIndex) {
		dieTransaktion.storniereGebuchtePosition(pIndex);
	}
	
	public void verkleinereGebuchteMenge(int pIndex) {
		dieTransaktion.verkleinereGebuchteMenge(pIndex);
	}
	
	public void bearbeiteGebuchteMenge(int pIndex, String pMenge) {
		 try {
			 dieTransaktion.bearbeiteGebuchteMenge(pIndex, new BigDecimal(pMenge.replace(',', '.')));
		 } 
		 catch (NumberFormatException e) {
		     throw new IllegalArgumentException(
		    		 pMenge + " - so viel kannst du dir zu Weihnachten gratis vom Santa wünschen.", e
		        );
		 }
	}
	
	public void vergroessereGebuchteMenge(int pIndex) {
		dieTransaktion.vergroessereGebuchteMenge(pIndex);
	}
	
	public void erfasseZahlung(String pBetrag) throws IOException {
		BigDecimal betrag;
		try {
			betrag = new BigDecimal(pBetrag.trim().replace(',', '.'));
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Bitte einen gültigen Betrag eingeben.", e
			);
		}
		if (betrag.compareTo(dieTransaktion.ermittleGesamtbetrag()) < 0) {
			throw new IllegalArgumentException(
					"Zu wenig: " + format.format(betrag) + " € gegeben, " + ermittleGesamtbetragAlsString()
					+ " fällig. Bitte einen neuen Betrag eingeben."
			);
		}
		dieTransaktion.erfasseZahlung(betrag);
		geldschublade.oeffne();
	}
	
	public void setzeTransaktionZurueck() {
		dieTransaktion.setzeZurueck();
	}
	
	public void schliesseTransaktionAb() {
		dieTransaktion.schliesseAb();
	}
    
    public String ermittleGesamtbetragAlsString() {
    	return format.format(dieTransaktion.ermittleGesamtbetrag()) + " €";
    }
    
    public String ermittleGezahltAlsString() {
    	return format.format(dieTransaktion.ermittleGezahlt()) + " €";
    }
    
    public String ermittleRueckgeldAlsStringOhneMinus() {
    	return format.format(dieTransaktion.ermittleRueckgeld().abs()) + " €";
    }
    
    public boolean genugGezahlt() {
    	return dieTransaktion.genugGezahlt();
    }
    
    public String ermittleGebuchteMengeAlsString(int pIndex) {
    	return format.format(dieTransaktion.ermittleGebuchteMenge(pIndex)) + " " + dieTransaktion.ermittleGebuchteEinheit(pIndex);
    }
	
	public Object[] ermittleGebuchtePositiondatenAlsTabellenzeile(int pIndex) {
		return new Object[] {
			dieTransaktion.ermittleGebuchteProduktnummer(pIndex),
			dieTransaktion.ermittleGebuchteBezeichnung(pIndex),
			format.format(dieTransaktion.ermittleGebuchtenPreis(pIndex)) + " € pro " + dieTransaktion.ermittleGebuchteEinheit(pIndex),
			dieTransaktion.ermittleGebuchteUmsatzsteuer(pIndex) + " %",
			ermittleGebuchteMengeAlsString(pIndex),
			format.format(dieTransaktion.ermittleGebuchtenBetrag(pIndex)) + " €"
		};
	}
	
	public int ermittleTransaktionGroesse() {
		return dieTransaktion.ermittleGroesse();
	}
}
