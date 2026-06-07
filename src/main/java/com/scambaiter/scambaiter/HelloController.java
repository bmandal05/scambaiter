package com.scambaiter.scambaiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class HelloController {

    @Autowired
    private ScamDetector scamDetector;

    @Autowired
    private AiScamDetector aiScamDetector;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private ConversationMemory conversationMemory;

    @GetMapping("/hello")
    public String hello() {
        return "Hello! ScamBaiter is running.";
    }

    @PostMapping("/analyze")
    public ScamResult analyze(@RequestBody MessageRequest request) {
        ScamResult aiResult = aiScamDetector.detect(request.getMessage());
        if (aiResult == null) return scamDetector.analyze(request.getMessage());
        return aiResult;
    }

    @PostMapping("/autoreply")
    public String autoReply(@RequestBody ReplyRequest request) {
        String voice = request.getVoice() != null ? request.getVoice() : "grandma";
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default";

        System.out.println("=== AUTOREPLY ===");
        System.out.println("SessionId: " + sessionId);
        System.out.println("Voice: " + voice);
        System.out.println("Message: " + request.getMessage());
        System.out.println("Current history: " + conversationMemory.getHistory(sessionId));

        ScamResult result = aiScamDetector.detect(request.getMessage());
        if (result == null) result = scamDetector.analyze(request.getMessage());
        String scamType = result.isScam() ? result.getScamType() : "general";

        return geminiService.generateReply(request.getMessage(), scamType, voice, sessionId);
    }

    @PostMapping("/stop")
    public String stopSession(@RequestBody MessageRequest request) {
        String sessionId = request.getMessage();
        System.out.println("=== STOP called for sessionId: " + sessionId + " ===");
        System.out.println("History before save: " + conversationMemory.getHistory(sessionId));
        conversationMemory.clearSession(sessionId);
        return "Session saved and cleared";
    }

    @DeleteMapping("/session/{sessionId}")
    public String clearSession(@PathVariable String sessionId) {
        conversationMemory.clearSession(sessionId);
        return "Session cleared";
    }
}