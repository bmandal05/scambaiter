package com.scambaiter.scambaiter;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConversationMemory {

    // Stores history per phone number / chat
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

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    private List<String> getOrCreate(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }
}