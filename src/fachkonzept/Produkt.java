package fachkonzept;

import java.math.BigDecimal;

public class Produkt
{
	private String EAN;
	private String bezeichnung;
	private BigDecimal nettopreis;
	private String basiseinheit;
	private BigDecimal umsatzsteuersatz;

	public Produkt(String pEAN, String pBezeichnung, String pNettopreis, String pBasiseinheit, String pUmsatzsteuersatz)
	{
		/*
		if (pEAN == null || !pEAN.matches("\\d{13}"))
		{
			throw new IllegalArgumentException("Die EAN muss aus genau 13 Ziffern bestehen.");
		}
		*/
		// pEAN kann nicht geprüft werden, wenn das Objekt null (= in SQL nicht gefunden) ist; 
	    // wird deshalb stattdessen in ProduktSpeicher validiert
		EAN = pEAN;
		bezeichnung = pBezeichnung;
		nettopreis = new BigDecimal(pNettopreis);
		basiseinheit = pBasiseinheit;
		umsatzsteuersatz = new BigDecimal(pUmsatzsteuersatz);
	}

	public String getEAN()
	{
		return EAN;
	}

	public String getBezeichnung()
	{
		return bezeichnung;
	}

	public BigDecimal getNettoPreis()
	{
		return nettopreis;
	}

	public String getBasiseinheit()
	{
		return basiseinheit;
	}

	public BigDecimal getUmsatzsteuersatz()
	{
		return umsatzsteuersatz;
	}

}
