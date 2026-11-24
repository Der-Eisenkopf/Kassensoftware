package fachkonzept;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Geldschublade
{
	public static final byte[] STANDARD_BEFEHL = { 0x1B, 0x70, 0x00, 0x19, (byte) 0xFA };

	private final String comPort;
	private final int baudrate;
	private final byte[] befehl;

	// Ohne COM-Port laeuft die Schublade im Simulationsmodus (es wird nichts an Hardware gesendet)
	public Geldschublade(String pComPort, int pBaudrate, byte[] pBefehl)
	{
		comPort = (pComPort == null || pComPort.trim().isEmpty()) ? null : pComPort.trim().toUpperCase();
		if (comPort != null && !comPort.matches("COM\\d{1,3}"))
		{
			throw new IllegalArgumentException("Ungueltiger COM-Port: " + pComPort + " (erwartet z. B. COM3)");
		}
		baudrate = pBaudrate;
		befehl = pBefehl.clone();
	}

	public static List<String> findeSeriellePorts() throws IOException
	{
		Process prozess = new ProcessBuilder("reg", "query", "HKLM\\HARDWARE\\DEVICEMAP\\SERIALCOMM")
				.redirectErrorStream(true).start();
		String ausgabe;
		try (InputStream in = prozess.getInputStream())
		{
			ausgabe = new String(in.readAllBytes());
		}
		List<String> ports = new ArrayList<>();
		Matcher m = Pattern.compile("REG_SZ\\s+(COM\\d+)").matcher(ausgabe);
		while (m.find())
		{
			ports.add(m.group(1).toUpperCase());
		}
		return ports;
	}

	public void oeffne() throws IOException
	{
		if (comPort == null)
		{
			return;
		}
		stelleBaudrateEin();
		try (OutputStream out = new FileOutputStream("\\\\.\\" + comPort))
		{
			out.write(befehl);
		}
		catch (IOException e)
		{
			throw new IOException("COM-Port " + comPort + " ist nicht erreichbar (Schublade angesteckt? Anderes Programm belegt den Port?): "
					+ e.getMessage(), e);
		}
	}

	private void stelleBaudrateEin() throws IOException
	{
		Process prozess = new ProcessBuilder("cmd", "/c", "mode", comPort, "BAUD=" + baudrate, "PARITY=n", "DATA=8", "STOP=1")
				.redirectErrorStream(true).start();
		try
		{
			if (!prozess.waitFor(5, TimeUnit.SECONDS) || prozess.exitValue() != 0)
			{
				prozess.destroyForcibly();
				throw new IOException("COM-Port " + comPort + " konnte nicht eingestellt werden (Port vorhanden?).");
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			throw new IOException("Einstellung von " + comPort + " wurde unterbrochen.", e);
		}
	}
}