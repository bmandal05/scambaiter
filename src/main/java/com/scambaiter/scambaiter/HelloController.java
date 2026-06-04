package com.scambaiter.scambaiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class HelloController {

    @Autowired
    private ScamDetector scamDetector;

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello! ScamBaiter is running.";
    }

    @PostMapping("/analyze")
    public ScamResult analyze(@RequestBody MessageRequest request) {
        return scamDetector.analyze(request.getMessage());
    }

    @PostMapping("/reply")
    public String reply(@RequestBody ReplyRequest request) {
        ScamResult result = scamDetector.analyze(request.getMessage());
        if (!result.isScam()) {
            return "Not a scam, no reply needed.";
        }
        return geminiService.generateReply(request.getMessage(), result.getScamType(), request.getVoice());
    }

    @PostMapping("/autoreply")
    public String autoReply(@RequestBody ReplyRequest request) {
        String voice = request.getVoice() != null ? request.getVoice() : "grandma";
        ScamResult result = scamDetector.analyze(request.getMessage());
        String scamType = result.isScam() ? result.getScamType() : "general";
        return geminiService.generateReply(request.getMessage(), scamType, voice);
    }
}