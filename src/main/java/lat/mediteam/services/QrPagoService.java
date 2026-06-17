package lat.mediteam.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lat.mediteam.core.Config;


public class QrPagoService {
    private static final String LOGIN_URL = "https://masterqr.pagofacil.com.bo/api/services/v2/login";
    private static final String GENERATE_QR_URL = "https://masterqr.pagofacil.com.bo/api/services/v2/generate-qr";
    private static final String QUERY_TX_URL = "https://masterqr.pagofacil.com.bo/api/services/v2/query-transaction";

    private static final String TOKEN_SERVICE = Config.TOKEN_SERVICE;
    private static final String TOKEN_SECRET = Config.TOKEN_SECRET;
    private static final String COMMERCE_ID = Config.COMMERCE_ID;
    private static final String CALLBACK_URL = Config.CALLBACK_URL;

    private static String cachedToken = null;
    private static long tokenExpirationTime = 0;
    private static final HttpClient client = HttpClient.newHttpClient();

    private static String extractJsonString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static Long extractJsonLong(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9]+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private static synchronized String login() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            return cachedToken;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .header("tcTokenService", TOKEN_SERVICE)
                    .header("tcTokenSecret", TOKEN_SECRET)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String accessToken = extractJsonString(response.body(), "accessToken");
                if (accessToken != null) {
                    cachedToken = accessToken;
                    tokenExpirationTime = System.currentTimeMillis() + (50 * 60 * 1000); // 50 min
                    return cachedToken;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> generateQR(String clientName, String clientCI, String phoneNumber, String clientEmail,
                                                 String paymentNumber, double amount, String clientCode, String service) {
        String token = login();
        if (token == null) return null;

        clientName = clientName.replaceAll("\"", "\\\"");

        String jsonBody = "{"
                + "\"paymentMethod\": 34,"
                + "\"clientName\": \"" + clientName + "\","
                + "\"documentType\": 1,"
                + "\"documentId\": \"" + clientCI + "\","
                + "\"phoneNumber\": \"" + phoneNumber + "\","
                + "\"email\": \"" + clientEmail + "\","
                + "\"paymentNumber\": \"" + paymentNumber + "\","
                + "\"amount\": " + amount + ","
                + "\"currency\": 2,"
                + "\"clientCode\": \"" + clientCode + "\","
                + "\"callbackUrl\": \"" + CALLBACK_URL + "\","
                + "\"orderDetail\": [{"
                + "\"serial\": 1,"
                + "\"product\": \"" + service + "\","
                + "\"quantity\": 1,"
                + "\"price\": " + amount + ","
                + "\"discount\": 0,"
                + "\"total\": " + amount
                + "}]"
                + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GENERATE_QR_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                Long transactionId = extractJsonLong(body, "transactionId");
                String qrImage = extractJsonString(body, "qrImage");
                if (qrImage == null) {
                    qrImage = extractJsonString(body, "qrBase64");
                }

                if (transactionId != null && qrImage != null) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("transactionId", transactionId);
                    result.put("qrImage", qrImage);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean queryTransaction(long transactionId) {
        String token = login();
        if (token == null) return false;

        String jsonBody = "{\"pagofacilTransactionId\": " + transactionId + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(QUERY_TX_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                
                // Cortar a partir de "values" para evitar el "status" raíz
                String valuesPart = body;
                int valuesIdx = body.indexOf("\"values\"");
                if (valuesIdx != -1) {
                    valuesPart = body.substring(valuesIdx);
                }

                String paymentStatus = "";
                String paymentStatusDesc = extractJsonString(valuesPart, "paymentStatusDescription");
                if (paymentStatusDesc == null) paymentStatusDesc = "";

                Long paymentStatusLong = extractJsonLong(valuesPart, "paymentStatus");
                if (paymentStatusLong != null) paymentStatus = String.valueOf(paymentStatusLong);

                String paymentDate = extractJsonString(valuesPart, "paymentDate");
                String status = paymentStatusDesc.toLowerCase();
                
                boolean tieneFechaPago = paymentDate != null && !paymentDate.equalsIgnoreCase("null") && !paymentDate.trim().isEmpty();

                boolean esEstadoExito = status.contains("comple") || status.contains("paga") || status.contains("succ") 
                        || status.contains("entregado") || status.contains("revis")
                        || paymentStatus.equals("2") || paymentStatus.equals("1") || paymentStatus.equals("5");

                return tieneFechaPago || esEstadoExito;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File saveBase64AsPng(String base64Str, String filename) {
        try {
            if (base64Str.startsWith("data:image")) {
                base64Str = base64Str.split(",")[1];
            }
            // Limpieza de escapes JSON \/ -> /
            base64Str = base64Str.replace("\\/", "/");
            base64Str = base64Str.replaceAll("[^a-zA-Z0-9+/=]", "");

            byte[] data = Base64.getDecoder().decode(base64Str.trim());
            File file = new File(filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
