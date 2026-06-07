package com.scambaiter.scambaiter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AiScamDetector {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public ScamResult detect(String message) {
        try {
            String prompt = "You are a scam detection expert. Analyze this WhatsApp message and reply in this exact JSON format only, nothing else:\n" +
                "{\"isScam\": true/false, \"scamType\": \"crypto/otp_phishing/job_fraud/romance/none\", \"confidence\": \"high/medium/low\"}\n\n" +
                "Message: \"" + message + "\"";

            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", prompt);

            JsonArray parts = new JsonArray();
            parts.add(textPart);

            JsonObject content = new JsonObject();
            content.add("parts", parts);

            JsonArray contents = new JsonArray();
            contents.add(content);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contents);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

            // If Gemini returns error fall back to keyword detection
            if (responseJson.has("error")) {
                System.out.println("AI detection failed, falling back to keywords");
                return null;
            }

            String rawText = responseJson
                .getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString()
                .trim()
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

            System.out.println("AI detection result: " + rawText);

            JsonObject result = JsonParser.parseString(rawText).getAsJsonObject();
            boolean isScam = result.get("isScam").getAsBoolean();
            String scamType = result.get("scamType").getAsString();
            String confidence = result.get("confidence").getAsString();

            return new ScamResult(isScam, scamType, confidence);

        } catch (Exception e) {
            System.out.println("AI detection error: " + e.getMessage());
            return null;
        }
    }
}