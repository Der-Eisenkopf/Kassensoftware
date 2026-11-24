package steuerung;
import java.io.FileInputStream;
import java.math.RoundingMode;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import fachkonzept.Transaktion;
import fachkonzept.Waage;
import fachkonzept.Geldschublade;
import fachkonzept.Produkt;
import datenspeicherung.db.ProduktSpeicher;


public class Steuerung {
	
	private static final String KASSEN_KONFIG = "kasse.properties";
	private static NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMANY);
	private Transaktion dieTransaktion;
	private ProduktSpeicher derProduktSpeicher;
	private final Geldschublade geldschublade;
	private static final String WAAGE_PFAD = "/uc3/getlifeweight";
	private final Waage waage; //
	
	public Steuerung() throws IOException
	{
		Properties konfig = ladeKonfig();
		dieTransaktion = new Transaktion();
		derProduktSpeicher = new ProduktSpeicher();
		geldschublade = erzeugeGeldschublade(konfig);
		waage = erzeugeWaage(konfig);
	}

	private static Geldschublade erzeugeGeldschublade(Properties props) throws IOException
	{
		String port = props.getProperty("schublade.com", "auto").trim();
		if (port.equalsIgnoreCase("aus"))
		{
			port = null;
		}
		else if (port.isEmpty() || port.equalsIgnoreCase("auto"))
		{
			port = ermittlePort();
		}

		try
		{
			int baud = Integer.parseInt(props.getProperty("schublade.baud", "9600").trim());
			return new Geldschublade(port, baud, Geldschublade.STANDARD_BEFEHL);
		}
		catch (IllegalArgumentException e)
		{
			throw new IOException("Fehler in " + KASSEN_KONFIG + ": " + e.getMessage(), e);
		}
	}

	private static String ermittlePort() throws IOException
	{
		List<String> ports = Geldschublade.findeSeriellePorts();
		if (ports.isEmpty())
		{
			return null; // kein Port -> Simulationsmodus
		}
		if (ports.size() == 1)
		{
			return ports.get(0);
		}
		throw new IOException("Mehrere serielle Ports gefunden " + ports
				+ ". Bitte schublade.com in " + KASSEN_KONFIG + " festlegen.");
	}
	
	private static Properties ladeKonfig() throws IOException
	{
		Properties props = new Properties();
		if (Files.exists(Paths.get(KASSEN_KONFIG)))
		{
			try (FileInputStream in = new FileInputStream(KASSEN_KONFIG))
			{
				props.load(in);
			}
		}
		return props;
	}

	// waage.adresse = IP; fehlt sie, gibt es keine Waage
	private static Waage erzeugeWaage(Properties pKonfig) throws IOException
	{
		String adresse = pKonfig.getProperty("waage.adresse", "").trim();
		if (adresse.isEmpty())
		{
			return null;
		}
		String port = pKonfig.getProperty("waage.port", "1122").trim();
		try
		{
			return new Waage("http://" + adresse + ":" + port + WAAGE_PFAD);
		}
		catch (IllegalArgumentException e)
		{
			throw new IOException("Fehler in " + KASSEN_KONFIG + ": ungültige Waagen-Adresse \"" + adresse + "\"", e);
		}
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
		    		 "Die eingegebene Menge \"" + pMenge + "\" ist ungültig. Bitte geben Sie eine gültige Zahl ein.", e
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

	public boolean istGewichtsartikel(String pEAN) throws SQLException 
	{
	    String einheit = derProduktSpeicher.laden(pEAN).getBasiseinheit();
	    return einheit != null && einheit.trim().equalsIgnoreCase("kg");
	}
	
	public boolean istGewichtsposition(int pIndex) 
	{
	    String einheit = dieTransaktion.ermittleGebuchteEinheit(pIndex);
	    return einheit != null && einheit.trim().equalsIgnoreCase("kg");
	}

	public BigDecimal leseGewicht() throws IOException, InterruptedException 
	{
	    double kg = waage.getGewicht();
	    if (waage == null)
	    {
	    	throw new IllegalStateException("Keine Waage eingerichtet (waage.adresse in " + KASSEN_KONFIG + " fehlt).");
	    }
	    if (kg <= 0) 
	    {
	        throw new IllegalStateException("Kein Gewicht auf der Waage.");
	    }
	    return BigDecimal.valueOf(kg).setScale(3, RoundingMode.HALF_UP);
	}

	public int buchePositionMitGewicht(String pEAN, BigDecimal pKg) throws SQLException 
	{
	    Produkt produkt = derProduktSpeicher.laden(pEAN);

	    for (int i = 0; i < dieTransaktion.ermittleGroesse(); i++) 
	    {
	        if (produkt.getEAN().equals(dieTransaktion.ermittleGebuchteProduktnummer(i))) 
	        {
	            BigDecimal neueMenge = dieTransaktion.ermittleGebuchteMenge(i).add(pKg);
	            dieTransaktion.bearbeiteGebuchteMenge(i, neueMenge);
	            return i;
	        }
	    }

	    dieTransaktion.buchePosition(produkt);
	    int index = dieTransaktion.ermittleGroesse() - 1;
	    dieTransaktion.bearbeiteGebuchteMenge(index, pKg);
	    return index;
	}

}
