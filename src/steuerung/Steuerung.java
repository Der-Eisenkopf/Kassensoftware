package steuerung;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import fachkonzept.Transaktion;
import fachkonzept.Geldschublade;
import datenspeicherung.db.ProduktSpeicher;


public class Steuerung {
	
	private static NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMANY);
	// damit 1234.56 zum 1.234,56 wird
	
	private Transaktion dieTransaktion;
	
	private ProduktSpeicher derProduktSpeicher; // Mögliche Idee: db4free.net statt lokal
	
	private Geldschublade dieGeldschublade;
	
	public Steuerung()
	{
		dieTransaktion = new Transaktion();
		derProduktSpeicher = new ProduktSpeicher();
		dieGeldschublade = new Geldschublade();
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
	
	public void erfasseZahlung(String pBetrag) {
		try {
			dieTransaktion.erfasseZahlung(new BigDecimal(pBetrag.replace(',', '.')));
		 } 
		 catch (NumberFormatException e) {
		     throw new IllegalArgumentException(
		    		 "Der aktuelle Wechselkurs von " + pBetrag + " zu Euro ist gerade leider nicht verfügbar.", e
		        );
		 }
	}
	
	public void setzeTransaktionZurueck() {
		dieTransaktion.setzeZurueck();
	}
	
	public void schliesseTransaktionAb() {
		dieTransaktion.schliesseAb();
	}
	
	public void oeffneGeldschublade() {
		dieGeldschublade.oeffne();
	}
	
    public void schliesseGeldschublade() {
    	// dieGeldschublade.schliesse();
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
