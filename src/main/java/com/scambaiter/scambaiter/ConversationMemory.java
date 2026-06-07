package com.scambaiter.scambaiter;

import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConversationMemory {

    private final Map<String, List<String>> sessions = new HashMap<>();

    public void addScammerMessage(String sessionId, String message) {
        getOrCreate(sessionId).add("Scammer: " + message);
    }

    public void addOurReply(String sessionId, String reply) {
        getOrCreate(sessionId).add("Me: " + reply);
    }

    public String getHistory(String sessionId) {
        List<String> history = sessions.get(sessionId);
        if (history == null || history.isEmpty()) return "No previous messages.";
        return String.join("\n", history);
    }

    public boolean hasSession(String sessionId) {
        return sessions.containsKey(sessionId) && !sessions.get(sessionId).isEmpty();
    }

    public void saveToFile(String sessionId) {
        try {
            String history = getHistory(sessionId);
            new File("logs").mkdirs();
            String filename = "logs/" + sessionId + "_" + System.currentTimeMillis() + ".txt";
            Files.writeString(Path.of(filename), history);
            System.out.println("Conversation saved to: " + filename);
        } catch (Exception e) {
            System.out.println("Could not save log: " + e.getMessage());
        }
    }

    public void clearSession(String sessionId) {
        saveToFile(sessionId); // save before clearing
        sessions.remove(sessionId);
    }

    private List<String> getOrCreate(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }
}