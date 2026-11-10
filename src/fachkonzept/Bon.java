package fachkonzept;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Bon {
	
	Transaktion dieTransaktion;
	private String header; // wird statisch definiert, z.B. "Tante-Emma-Laden ... Balingen"
	private LocalDateTime zeitpunkt; 
	private String hauptbereich;
	// private String steueruebersicht // für die nächsten Sprints
	private String footer; // wird statisch definiert, z.B. "Alle Beträge sind in Euro ..."
	private String dateiname;
	
	private static NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMANY);
	// damit 1234.56 zum 1.234,56 wird
	
	public Bon(Transaktion pTransaktion) {

		zeitpunkt = LocalDateTime.now();
		dieTransaktion = pTransaktion;
		header = "Tante-Emma-Laden\nAn der Ecke 2\n72336 Balingen\n" 
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
		
		hauptbereich += "\n\n" + "Za zahlen: " + format.format(dieTransaktion.ermittleGesamtbetrag()) + " €";
		hauptbereich += "\n" + "Gegeben: " + format.format(dieTransaktion.ermittleGezahlt()) + " €";
		hauptbereich += "\n" + "Zurück: " + format.format(dieTransaktion.ermittleRueckgeld()) + " €";
		footer = "Vielen Dank für Ihren Einkauf!";
		
		dateiname = "bon_" + zeitpunkt.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		
	}
	
    public void drucke() {
		
		Path directory = Path.of("bons");
        String contents = header + "\n\n" + hauptbereich + "\n\n" + footer;

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
