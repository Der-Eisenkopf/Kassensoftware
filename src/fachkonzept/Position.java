package fachkonzept;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Position {
	
	private Produkt dasProdukt;
	private BigDecimal menge;
	private BigDecimal betragNetto;
	private BigDecimal betragSteuer;
	private BigDecimal betragBrutto;
	
	public Position(Produkt pProdukt) {
		dasProdukt = pProdukt;
		menge = new BigDecimal("1"); 
		berechneBetraege();
	}
	
	public Produkt ermittleProdukt() {
		return dasProdukt;
	}
	
	private void berechneBetraege() {
		betragNetto = dasProdukt.getNettoPreis().multiply(menge).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
		betragSteuer = dasProdukt.getUmsatzsteuer().multiply(menge).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
		betragBrutto = dasProdukt.getBruttoPreis().multiply(menge).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
	}
	
	public void bearbeiteMenge(BigDecimal pMenge) {
		if (pMenge.stripTrailingZeros().scale() > 0 && dasProdukt.getBasiseinheit().equals("STK")) {
			throw new IllegalArgumentException("Bei der Basiseinheit STK sind ausschließlich ganzzahlige Mengen zulässig.");
		}
		if (pMenge.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Die Menge muss größer als null sein.");
		}
		menge = pMenge.setScale(3, RoundingMode.HALF_UP).stripTrailingZeros();
		berechneBetraege();
	}
	
	public void vergroessereMenge() {
		bearbeiteMenge(menge.add(BigDecimal.ONE));
	}
	
	public void verkleinereMenge() {
		bearbeiteMenge(menge.subtract(BigDecimal.ONE));
	}
	
	public BigDecimal ermittleMenge() {
		return menge;
	}
	
	public BigDecimal ermittleBetragNetto() {
		return betragNetto;
	}
	
	public BigDecimal ermittleBetragSteuer() {
		return betragSteuer;
	}
	
	public BigDecimal ermittleBetragBrutto() {
		return betragBrutto;
	}
	
}
