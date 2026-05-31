package com.scambaiter.scambaiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
    public String reply(@RequestBody MessageRequest request) {
        ScamResult result = scamDetector.analyze(request.getMessage());
        if (!result.isScam()) {
            return "Not a scam, no reply needed.";
        }
        return geminiService.generateReply(request.getMessage(), result.getScamType());
    }
}