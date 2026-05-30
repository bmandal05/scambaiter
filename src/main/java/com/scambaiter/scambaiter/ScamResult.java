package com.scambaiter.scambaiter;

public class ScamResult {
    private boolean isScam;
    private String scamType;
    private String confidence;

    public ScamResult(boolean isScam, String scamType, String confidence) {
        this.isScam = isScam;
        this.scamType = scamType;
        this.confidence = confidence;
    }

    public boolean isScam() { return isScam; }
    public String getScamType() { return scamType; }
    public String getConfidence() { return confidence; }
}