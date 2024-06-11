import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiClima {

    private static final String API_KEY = "7LNHXZ8VQVDLSSPPYCFLTJLCF";
    private static final String BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";

    public static void main(String[] args) {
        String location = "Maceio";
        String encodedLocation = URI.create(BASE_URL + location).toASCIIString().substring(BASE_URL.length());
        String url = BASE_URL + encodedLocation + "?key=" + API_KEY;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                analisarClima(responseBody);
            } else {
                System.out.println("Erro: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void analisarClima(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);

            String endereco = rootNode.path("address").asText();
            JsonNode currentConditions = rootNode.path("currentConditions");
            double temperatureFahrenheit = currentConditions.path("temp").asDouble();
            String conditions = currentConditions.path("conditions").asText();

            double temperaturaCelsius = (temperatureFahrenheit - 32) * 5 / 9;

            Map<String, String> conditionTranslations = createConditionTranslations();
            String condicoesemPortugues = conditionTranslations.getOrDefault(conditions, conditions);

            System.out.println("Endereço: " + endereco);
            System.out.printf("Temperatura Atual: %.1f°C\n", temperaturaCelsius);
            System.out.println("Condições: " + condicoesemPortugues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> createConditionTranslations() {
        Map<String, String> translations = new HashMap<>();
        translations.put("Clear", "Claro");
        translations.put("Partially cloudy", "Parcialmente nublado");
        translations.put("Overcast", "Nublado");
        translations.put("Rain", "Chuva");
        translations.put("Thunderstorm", "Tempestade");

        return translations;
    }
}
