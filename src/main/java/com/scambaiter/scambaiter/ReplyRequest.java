package com.scambaiter.scambaiter;

public class ReplyRequest {
    private String message;
    private String voice;
    private String sessionId;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getVoice() { return voice; }
    public void setVoice(String voice) { this.voice = voice; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}