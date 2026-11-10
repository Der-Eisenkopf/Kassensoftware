package fachkonzept;

// wurde durch KI generiert, benötigt das Testen mit dem Gerät von Herrn Lorenz

// jSerialComm.SerialPort anbinden: 
//               Rechtsklick auf Referenced Libraries -> Build Path -> Configure Build Path
//               -> Java Build Path -> Module Path -> Add JARs -> lib/jSerialComm-2.11.4.jar

import com.fazecast.jSerialComm.SerialPort;

public final class Geldschublade {

    // ==========================================
    // INTERNE KONFIGURATION
    // ==========================================

    // Standardmäßig Simulation aktiviert.
    // Kann beim Programmstart deaktiviert werden.
    private static final boolean SIMULATION =
            Boolean.parseBoolean(
                    System.getProperty("kasse.simulation", "true")
            );

    // Serielle Kommunikation
    private static final int BAUDRATE = Integer.parseInt(
            System.getProperty("kasse.baudrate", "9600")
    );

    // Optionaler Portname:
    // Windows: COM3
    // Linux:   /dev/ttyUSB0
    // macOS:   /dev/cu.usbserial-XXXX
    private static final String PORT_NAME =
            System.getProperty("kasse.port", "");

    // ESC/POS-Befehl zum Öffnen der Geldschublade.
    // Kann später über die Systemeigenschaft angepasst werden.
    private static final String OPEN_COMMAND =
            System.getProperty(
                    "kasse.befehl",
                    "1B700019FA"
            );

    // Interne Variablen
    private SerialPort serialPort;
    private boolean geoeffnet = false;


    // ==========================================
    // KONSTRUKTOR
    // ==========================================

    public Geldschublade() {

        if (SIMULATION) {

            System.out.println(
                    "[Geldschublade] Simulationsmodus aktiviert."
            );

        } else {

            verbinde();
        }
    }


    // ==========================================
    // ÖFFENTLICHE METHODEN
    // ==========================================

    /**
     * Öffnet die Geldschublade.
     */
    public synchronized void oeffne() {

        if (geoeffnet) {
            return;
        }

        if (SIMULATION) {

            System.out.println(
                    "[Geldschublade] Simulation: "
                    + "Geldschublade geöffnet."
            );

        } else {

            sendeOeffnungsbefehl();
        }

        geoeffnet = true;
    }


    /**
     * Schließt die Geldschublade logisch.
     *
     * Die physische Geldschublade muss normalerweise
     * manuell zugeschoben werden.
     */
    public synchronized void schliesse() {

        if (!geoeffnet) {
            return;
        }

        if (SIMULATION) {

            System.out.println(
                    "[Geldschublade] Simulation: "
                    + "Geldschublade geschlossen."
            );

        } else {

            System.out.println(
                    "[Geldschublade] "
                    + "Schließen erfolgt manuell."
            );
        }

        geoeffnet = false;
    }


    // ==========================================
    // PRIVATE METHODEN
    // ==========================================

    /**
     * Stellt die Verbindung zum seriellen USB-Adapter her.
     */
    private void verbinde() {

        SerialPort[] ports = SerialPort.getCommPorts();

        if (ports.length == 0) {

            throw new IllegalStateException(
                    "Kein serieller Anschluss gefunden."
            );
        }

        // Falls ein konkreter Port angegeben wurde:
        if (!PORT_NAME.isEmpty()) {

            for (SerialPort port : ports) {

                if (port.getSystemPortName()
                        .equalsIgnoreCase(PORT_NAME)
                        || port.getDescriptivePortName()
                        .equalsIgnoreCase(PORT_NAME)) {

                    serialPort = port;
                    break;
                }
            }

            if (serialPort == null) {

                throw new IllegalStateException(
                        "Der angegebene Anschluss wurde "
                        + "nicht gefunden: " + PORT_NAME
                );
            }

        } else {

            // Automatische Erkennung:
            // Nur verwenden, wenn genau ein Port existiert.

            if (ports.length > 1) {

                StringBuilder verfuegbarePorts =
                        new StringBuilder();

                for (SerialPort port : ports) {

                    verfuegbarePorts.append(
                            port.getSystemPortName()
                    ).append(" ");
                }

                throw new IllegalStateException(
                        "Mehrere serielle Anschlüsse gefunden: "
                        + verfuegbarePorts
                        + ". Bitte kasse.port konfigurieren."
                );
            }

            serialPort = ports[0];
        }

        // Serielle Schnittstelle konfigurieren
        serialPort.setComPortParameters(
                BAUDRATE,
                8,
                SerialPort.ONE_STOP_BIT,
                SerialPort.NO_PARITY
        );

        serialPort.setComPortTimeouts(
                SerialPort.TIMEOUT_WRITE_BLOCKING,
                0,
                1000
        );

        // Verbindung herstellen
        if (!serialPort.openPort()) {

            throw new IllegalStateException(
                    "Verbindung zum seriellen Anschluss "
                    + "fehlgeschlagen: "
                    + serialPort.getSystemPortName()
            );
        }

        System.out.println(
                "[Geldschublade] Verbunden mit: "
                + serialPort.getSystemPortName()
        );
    }


    /**
     * Sendet den Öffnungsbefehl an den USB-Adapter.
     */
    private void sendeOeffnungsbefehl() {

        if (serialPort == null || !serialPort.isOpen()) {

            throw new IllegalStateException(
                    "Keine Verbindung zur Geldschublade."
            );
        }

        byte[] befehl = hexZuBytes(OPEN_COMMAND);

        int geschrieben = serialPort.writeBytes(
                befehl,
                befehl.length
        );

        if (geschrieben != befehl.length) {

            throw new IllegalStateException(
                    "Der Öffnungsbefehl konnte nicht "
                    + "vollständig gesendet werden."
            );
        }

        System.out.println(
                "[Geldschublade] Öffnungsbefehl gesendet."
        );
    }


    /**
     * Wandelt einen Hexadezimal-String in ein Byte-Array um.
     *
     * Beispiel:
     * "1B700019FA" -> ESC/POS-Befehl
     */
    private byte[] hexZuBytes(String hex) {

        if (hex == null || hex.isEmpty()
                || hex.length() % 2 != 0
                || !hex.matches("[0-9a-fA-F]+")) {

            throw new IllegalArgumentException(
                    "Ungültiger Hexadezimal-Befehl: " + hex
            );
        }

        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < bytes.length; i++) {

            int index = i * 2;

            bytes[i] = (byte) Integer.parseInt(
                    hex.substring(index, index + 2),
                    16
            );
        }

        return bytes;
    }

}
