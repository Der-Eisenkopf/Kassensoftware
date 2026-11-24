package fachkonzept;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class Bon {
	
	Transaktion dieTransaktion;
	private String header; 
	private LocalDateTime zeitpunkt; 
	private String hauptbereich;
	private String steueruebersicht;
	private String footer;
	private String dateiname;
	
	private static NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMANY);
	
	
    //0,7 wird zu 0,70)
	private static NumberFormat formatMitCent = NumberFormat.getNumberInstance(Locale.GERMANY);
	
	public Bon(Transaktion pTransaktion) {

		formatMitCent.setMinimumFractionDigits(2);
		formatMitCent.setMaximumFractionDigits(2);
		
		zeitpunkt = LocalDateTime.now();
		dieTransaktion = pTransaktion;
		header = "Blue Tomato\nHeinlenStrasse 2\n73044 Stuttgart\n" 
		         + zeitpunkt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
		
		String strich = "";
		for (int i = 0; i < 65; i++) {
			strich += "-";
		}
		hauptbereich = strich;
		for (int i = 0; i < dieTransaktion.ermittleGroesse(); i++) {
			
			String bezeichnung = dieTransaktion.ermittleGebuchteBezeichnung(i);
			
			String preisProMenge = format.format(dieTransaktion.ermittleGebuchteMenge(i)) 
			                       + " " + dieTransaktion.ermittleGebuchteEinheit(i)
			                       + " x " 
			                       + format.format(dieTransaktion.ermittleGebuchtenBruttoPreis(i))
			                       + " €";
			
			String betrag = format.format(dieTransaktion.ermittleGebuchtenBetrag(i)) + " €";
			
			hauptbereich += "\n" + String.format(
				    "%-30s%20s%15s",
				    bezeichnung,
				    preisProMenge,
				    betrag
				) + "\n";
			
		}
		hauptbereich += strich;
		
		hauptbereich += "\n\n" + "Zu zahlen: " + format.format(dieTransaktion.ermittleGesamtbetrag()) + " €";
		hauptbereich += "\n" + "Gegeben: " + format.format(dieTransaktion.ermittleGezahlt()) + " €";
		hauptbereich += "\n" + "Zurück: " + format.format(dieTransaktion.ermittleRueckgeld()) + " €";
		
		steueruebersicht = erzeugeSteueruebersicht(strich);
		
		footer = "Vielen Dank für Ihren Einkauf!";
		
		dateiname = "bon_" + zeitpunkt.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		
	}
	
	// Erzeugt die Steuerübersicht: für jeden Steuersatz Netto, Steuer und Brutto.
	private String erzeugeSteueruebersicht(String pStrich) {
		
		// Zwei Listen, die zusammengehören: An derselben Stelle steht
		// der Steuersatz (z.B. 7) und die Summe aller Bruttobeträge mit diesem Steuersatz.
		ArrayList<BigDecimal> steuersaetze = new ArrayList<BigDecimal>();
		ArrayList<BigDecimal> bruttoSummen = new ArrayList<BigDecimal>();
		
		//1: Alle Positionen durchgehen und nach Steuersatz sortieren
		for (int i = 0; i < dieTransaktion.ermittleGroesse(); i++) {
			BigDecimal steuersatz = dieTransaktion.ermittleGebuchteUmsatzsteuer(i);
			BigDecimal brutto = dieTransaktion.ermittleGebuchtenBetrag(i);
			
			int stelle = sucheSteuersatz(steuersaetze, steuersatz);
			if (stelle == -1) {
				// Steuersatz kam noch nicht vor: neu anlegen
				steuersaetze.add(steuersatz);
				bruttoSummen.add(brutto);
			}
			else {
				// Steuersatz gibt es schon: Brutto dazuzählen
				bruttoSummen.set(stelle, bruttoSummen.get(stelle).add(brutto));
			}
		}
		
		//2: Tabelle aufbauen
		String zeilenformat = "%-12s%17s%17s%17s";
		String text = String.format(zeilenformat, "Steuersatz", "Netto", "Steuer", "Brutto");
		text += "\n" + pStrich;
		
		BigDecimal summeNetto = BigDecimal.ZERO;
		BigDecimal summeSteuer = BigDecimal.ZERO;
		BigDecimal summeBrutto = BigDecimal.ZERO;
		
		for (int i = 0; i < steuersaetze.size(); i++) {
			BigDecimal steuersatz = steuersaetze.get(i);
			BigDecimal brutto = bruttoSummen.get(i);
			
			// Steuer = Brutto * Satz / (100 + Satz), z.B. 7,07 * 7 / 107
			BigDecimal steuer = brutto.multiply(steuersatz)
					.divide(steuersatz.add(new BigDecimal("100")), 2, RoundingMode.HALF_UP);
			// Netto = Brutto - Steuer (so passt die Rechnung immer auf den Cent)
			BigDecimal netto = brutto.subtract(steuer);
			
			summeNetto = summeNetto.add(netto);
			summeSteuer = summeSteuer.add(steuer);
			summeBrutto = summeBrutto.add(brutto);
			
			String satzText = format.format(steuersatz) + " %";
			text += "\n" + String.format(zeilenformat, satzText,
					formatMitCent.format(netto) + " €",
					formatMitCent.format(steuer) + " €",
					formatMitCent.format(brutto) + " €");
		}
		
		//3: Summenzeile
		text += "\n" + pStrich;
		text += "\n" + String.format(zeilenformat, "Summe",
				formatMitCent.format(summeNetto) + " €",
				formatMitCent.format(summeSteuer) + " €",
				formatMitCent.format(summeBrutto) + " €");
		
		return text;
	}
	
	// Sucht einen Steuersatz in der Liste. Gibt die Stelle zurück oder -1, wenn er nicht vorkommt.
	// (compareTo statt equals, weil 7 und 7.00 sonst als verschieden gelten würden)
	private int sucheSteuersatz(ArrayList<BigDecimal> pListe, BigDecimal pSteuersatz) {
		for (int i = 0; i < pListe.size(); i++) {
			if (pListe.get(i).compareTo(pSteuersatz) == 0) {
				return i;
			}
		}
		return -1;
	}
	
    public void drucke() {
		
		Path directory = Path.of("bons");
        String contents = header + "\n\n" + hauptbereich + "\n\n" + steueruebersicht + "\n\n" + footer;

        try {
            Files.createDirectories(directory);

            Path filePath = directory.resolve(dateiname);
            Files.writeString(filePath, contents);

            System.out.println("Bon gespeichert unter: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
    
}