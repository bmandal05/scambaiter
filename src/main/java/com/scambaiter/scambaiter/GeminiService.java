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
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final PersonaEngine personaEngine = new PersonaEngine();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String generateReply(String scammerMessage, String scamType, String voice) {
        try {
            String prompt = personaEngine.getPersonaPrompt(scamType, voice)
                + "\n\nThe scammer just sent you: \"" + scammerMessage + "\"\n\nReply naturally, stay in character. One short WhatsApp message only.";

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

            System.out.println("GEMINI RAW: " + response.body());

            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

            // Handle error response
            if (responseJson.has("error")) {
                String errorMsg = responseJson.getAsJsonObject("error").get("message").getAsString();
                return getFallbackReply(voice);
            }

            return responseJson
                .getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        } catch (Exception e) {
            System.out.println("Gemini error: " + e.getMessage());
            return getFallbackReply(voice);
        }
    }

    private String getFallbackReply(String voice) {
        switch (voice) {
            case "grandma": return "Oh beta sorry I got confused again! Can you explain once more slowly?";
            case "child": return "I don't understand 😅 can you ask my mom?";
            case "man": return "I need more proof before I trust this. Send me documents.";
            case "woman": return "Oh my god this reminds me of my cousin's story! Anyway what were you saying?";
            case "nerd": return "Can you provide the technical whitepaper and smart contract audit report first?";
            default: return "Sorry can you repeat that? I didn't understand.";
        }
    }
}