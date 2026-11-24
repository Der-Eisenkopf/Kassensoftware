package fachkonzept;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Waage 
{
    private static final Pattern WEIGHT = Pattern.compile("\"weight\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");

    private final URI uri;
    private final HttpClient client;

    public Waage(String pUrl) 
    {
        uri = URI.create(pUrl);
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    }

    public double getGewicht() throws IOException, InterruptedException 
    {
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(2)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200)
        {
            throw new IOException("Waage antwortet mit HTTP " + response.statusCode());
        }
        
        Matcher mGewicht = WEIGHT.matcher(response.body());
        if (!mGewicht.find()) 
        {
            throw new IOException("Kein Gewicht in Antwort der Waage.");
        }
        
        return Double.parseDouble(mGewicht.group(1)) / 1000.0;
    }
}