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

        ScamResult result = aiScamDetector.detect(request.getMessage());
        if (result == null) result = scamDetector.analyze(request.getMessage());
        String scamType = result.isScam() ? result.getScamType() : "general";

        return geminiService.generateReply(request.getMessage(), scamType, voice, sessionId);
    }

    @DeleteMapping("/session/{sessionId}")
    public String clearSession(@PathVariable String sessionId) {
        conversationMemory.clearSession(sessionId);
        return "Session cleared";
    }
}