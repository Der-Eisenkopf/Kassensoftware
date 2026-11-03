package fachkonzept;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bon {
	
	private String header; // wird statisch definiert, z.B. "Tante-Emma-Laden ... Balingen"
	private String datetime; // wird in Textform von der vorher aufgerufener Bibliothek gespeichert
	private String[] positionen; // gebuchte Positionen in Textform
	private String hauptbereich;
	// private String steueruebersicht // für die nächsten Sprints
	private String footer; // wird statisch definiert, z.B. "Alle Beträge sind in Euro ..."
	private String dateiname;
	
	public Bon() {
		
		header = "Tante-Emma-Laden";
		hauptbereich = "";
		footer = "Alle Beträge sind in Euro. \n Vielen Dank für Ihren Einkauf!";
		
	}
	
    public void drucke(Transaktion pTransaktion) {
		
		for (int i = 0; i < pTransaktion.ermittleGroesse(); i++) {
			
			
			
		}
		Path directory = Path.of("bons");
		String fileName = "example.txt";
        String contents = "Hello, World!\nThis is my text file.";

        try {
            // 3. Create the directory (including parent directories if needed)
            Files.createDirectories(directory);

            // 4. Create the file inside the directory and write its contents
            Path filePath = directory.resolve(fileName);
            Files.writeString(filePath, contents);

            System.out.println("File created at: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
    
    public String meldeErstellung() {
    	return "Bon wurde gespeichert unter ...";
    }
    
}
