package com.scambaiter.scambaiter;

import java.util.Arrays;
import java.util.List;

public class ScamDetector {

    private static final List<String> CRYPTO_KEYWORDS = Arrays.asList(
        "crypto", "bitcoin", "wallet", "usdt", "binance", 
        "investment", "double your money", "returns guaranteed"
    );

    private static final List<String> OTP_KEYWORDS = Arrays.asList(
        "otp", "verification code", "verify your account",
        "account suspended", "click this link", "urgent"
    );

    private static final List<String> JOB_KEYWORDS = Arrays.asList(
        "job offer", "work from home", "earn daily",
        "part time", "no experience needed", "whatsapp job"
    );

    private static final List<String> ROMANCE_KEYWORDS = Arrays.asList(
        "i love you", "send me money", "stuck abroad",
        "emergency", "western union", "gift card"
    );

    public ScamResult analyze(String message) {
        String lower = message.toLowerCase();

        if (containsKeyword(lower, CRYPTO_KEYWORDS)) {
            return new ScamResult(true, "crypto", "high");
        }
        if (containsKeyword(lower, OTP_KEYWORDS)) {
            return new ScamResult(true, "otp_phishing", "high");
        }
        if (containsKeyword(lower, JOB_KEYWORDS)) {
            return new ScamResult(true, "job_fraud", "medium");
        }
        if (containsKeyword(lower, ROMANCE_KEYWORDS)) {
            return new ScamResult(true, "romance", "medium");
        }

        return new ScamResult(false, "none", "low");
    }

    private boolean containsKeyword(String message, List<String> keywords) {
        return keywords.stream().anyMatch(message::contains);
    }
}