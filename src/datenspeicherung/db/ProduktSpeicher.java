package datenspeicherung.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fachkonzept.Produkt;

public class ProduktSpeicher
{
	private static final String KONFIG_DATEI = "db.properties";

	private final String url;
	private final String benutzer;
	private final String passwort;

	public ProduktSpeicher()
	{
		url = "jdbc:mariadb://localhost:3306/kassensystemdb";
		benutzer = "root";
		passwort = "";
	}

	private Connection verbinden() throws SQLException
	{
		return DriverManager.getConnection(url, benutzer, passwort);
	}

	public Produkt laden(String pEAN) throws SQLException
	{
		if (pEAN == null || !pEAN.matches("\\d{13}"))
		{
			if (pEAN.matches("\\d{8}")) {
				pEAN = "00000" + pEAN;
			}
			else {
				throw new IllegalArgumentException("Die EAN muss aus genau 8 oder genau 13 Ziffern bestehen.");
			}
		}
		String sql = "SELECT EAN, bezeichnung, nettopreis, basiseinheit, umsatzsteuersatz FROM produkt WHERE EAN = ?";
		try (Connection con = verbinden(); PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setString(1, pEAN);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next()) {
				    return zeile(rs);
				} else {
				    throw new IllegalArgumentException("Produkt nicht gefunden.");
				}
			}
		}
	}

	private Produkt zeile(ResultSet rs) throws SQLException
	{
		return new Produkt(rs.getString("EAN"), rs.getString("bezeichnung"), rs.getString("nettopreis"),
				rs.getString("basiseinheit"), rs.getString("umsatzsteuersatz"));
	}
}
