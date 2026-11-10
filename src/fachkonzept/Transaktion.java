package fachkonzept;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList; 

public class Transaktion {
	
	private ArrayList<Position> PositionenGebucht;
	
	private BigDecimal gesamtbetrag;
	
	private BigDecimal gezahlt;
	
	private BigDecimal rueckgeld;
	
	public Transaktion() {
		setzeZurueck();
	}
	
	public void setzeZurueck() {
		gesamtbetrag = new BigDecimal("0");
		gezahlt = new BigDecimal("0");
		rueckgeld = new BigDecimal("0");
		PositionenGebucht = new ArrayList<Position>();
	}
	
	public void schliesseAb() {
		if (!genugGezahlt()) {
			throw new IllegalArgumentException(
			        "Diebstahl wird hier nicht toleriert."
			    );
		}
		Bon derBon = new Bon(this);
		derBon.drucke();
		setzeZurueck();
	}
	
	public void buchePosition(Produkt pProdukt) {
		int bereitsGebuchtePosition = -1;
		for (int i = 0; i < ermittleGroesse(); i++) {
			if (ermittleGebuchteProduktnummer(i).equals(pProdukt.getEAN())) {
				bereitsGebuchtePosition = i;
				break;
			}
		}
		if (bereitsGebuchtePosition >= 0) {
			PositionenGebucht.get(bereitsGebuchtePosition).vergroessereMenge();
		}
		else {
			PositionenGebucht.add(new Position(pProdukt));
		}
		
		berechneGesamtbetrag();
	}
	
	public void storniereGebuchtePosition(int pIndex) {
		PositionenGebucht.remove(pIndex);
		if (ermittleGroesse() == 0) {
			setzeZurueck();
		}
		else {
			berechneGesamtbetrag();
		}
	}
	
	public void vergroessereGebuchteMenge(int pIndex) {
		PositionenGebucht.get(pIndex).vergroessereMenge();
		berechneGesamtbetrag();
	}
	
	public void verkleinereGebuchteMenge(int pIndex) {
		PositionenGebucht.get(pIndex).verkleinereMenge();
		berechneGesamtbetrag();
	}
	
	public void bearbeiteGebuchteMenge(int pIndex, BigDecimal pMenge) {
		PositionenGebucht.get(pIndex).bearbeiteMenge(pMenge);
		berechneGesamtbetrag();
	}
	
	public String ermittleGebuchteProduktnummer(int pIndex) {
		return PositionenGebucht.get(pIndex).ermittleProdukt().getEAN();
	}
	public String ermittleGebuchteBezeichnung(int pIndex) {
		return PositionenGebucht.get(pIndex).ermittleProdukt().getBezeichnung();
	}
	public BigDecimal ermittleGebuchtenPreis(int pIndex) {
		return PositionenGebucht.get(pIndex).ermittleProdukt().getNettoPreis();
	}
	public String ermittleGebuchteEinheit(int pIndex) {
		return PositionenGebucht.get(pIndex).ermittleProdukt().getBasiseinheit();
	}
	public BigDecimal ermittleGebuchteUmsatzsteuer(int pIndex) {
		return PositionenGebucht.get(pIndex).ermittleProdukt().getUmsatzsteuersatz();
	}
	public BigDecimal ermittleGebuchteMenge(int pIndex) {
		return PositionenGebucht.get(pIndex).ermittleMenge();
	}
	public BigDecimal ermittleGebuchtenBetrag(int pIndex) {
		return PositionenGebucht.get(pIndex).ermittleBetragBrutto();
	}
	
	public BigDecimal ermittleGebuchtenBruttoPreis(int pIndex) {
		return PositionenGebucht.get(pIndex).ermittleProdukt().getBruttoPreis();
	}
	
	private void berechneGesamtbetrag() {
		gesamtbetrag = new BigDecimal("0");
		for (Position Position : PositionenGebucht) {
			  gesamtbetrag = gesamtbetrag.add(Position.ermittleBetragBrutto());
		}
		berechneRueckgeld();
	}
	
	private void berechneRueckgeld() {
		rueckgeld = gezahlt.subtract(gesamtbetrag);
	}
	
	public void erfasseZahlung(BigDecimal pBetrag) {
		if (pBetrag.compareTo(BigDecimal.ZERO) < 0) {
		    throw new IllegalArgumentException(
		        "Haste wohl die Kasse mit der Sparkasse verwechselt, das ist was anderes."
		    );
		}
		if (pBetrag.compareTo(BigDecimal.ZERO) == 0) {
		    throw new IllegalArgumentException(
		        "Wow, dankeschön, das war recht großzügig von dir..."
		    );
		}
		gezahlt = pBetrag.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
		
		berechneRueckgeld();
	}
	
	public BigDecimal ermittleGesamtbetrag() {
		return gesamtbetrag;
	}
	
	public BigDecimal ermittleGezahlt() {
		return gezahlt;
	}
	
	public BigDecimal ermittleRueckgeld() {
		return rueckgeld;
	}
	
	public int ermittleGroesse() {
		return PositionenGebucht.size();
	}
	
	public boolean genugGezahlt() {
		return gezahlt.compareTo(gesamtbetrag) >= 0;
	}
	
}
