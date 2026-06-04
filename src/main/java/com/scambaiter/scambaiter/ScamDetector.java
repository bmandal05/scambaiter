package com.scambaiter.scambaiter;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ScamDetector {

    private static final List<String> CRYPTO_KEYWORDS = Arrays.asList(
            "crypto",
            "bitcoin",
            "wallet",
            "usdt",
            "binance",
            "investment",
            "double your money",
            "guaranteed return",
            "guaranteed returns",
            "profit daily",
            "trading signal",
            "passive income"
    );

    private static final List<String> OTP_KEYWORDS = Arrays.asList(
            "otp",
            "verification code",
            "verify your account",
            "verify immediately",
            "account suspended",
            "account locked",
            "bank account",
            "security alert",
            "click this link",
            "confirm identity",
            "reset password",
            "urgent action required"
    );

    private static final List<String> JOB_KEYWORDS = Arrays.asList(
            "job offer",
            "work from home",
            "earn daily",
            "earn money",
            "part time",
            "part-time",
            "remote job",
            "whatsapp job",
            "telegram job",
            "no experience needed",
            "easy income",
            "easy money"
    );

    private static final List<String> ROMANCE_KEYWORDS = Arrays.asList(
            "i love you",
            "send me money",
            "send money",
            "stuck abroad",
            "emergency",
            "western union",
            "gift card",
            "my dear",
            "need help urgently",
            "soulmate"
    );

    private static final List<String> LOTTERY_KEYWORDS = Arrays.asList(
            "you won",
            "winner",
            "lucky draw",
            "prize money",
            "claim your reward",
            "congratulations",
            "cash prize",
            "lottery"
    );

    public ScamResult analyze(String message) {

        if (message == null || message.trim().isEmpty()) {
            return new ScamResult(false, "none", "low");
        }

        String lower = message.toLowerCase();

        System.out.println("Analyzing message: " + message);

        if (containsKeyword(lower, CRYPTO_KEYWORDS)) {
            System.out.println("Detected: Crypto Scam");
            return new ScamResult(true, "crypto", "high");
        }

        if (containsKeyword(lower, OTP_KEYWORDS)) {
            System.out.println("Detected: OTP Phishing");
            return new ScamResult(true, "otp_phishing", "high");
        }

        if (containsKeyword(lower, JOB_KEYWORDS)) {
            System.out.println("Detected: Job Fraud");
            return new ScamResult(true, "job_fraud", "medium");
        }

        if (containsKeyword(lower, ROMANCE_KEYWORDS)) {
            System.out.println("Detected: Romance Scam");
            return new ScamResult(true, "romance", "medium");
        }

        if (containsKeyword(lower, LOTTERY_KEYWORDS)) {
            System.out.println("Detected: Lottery Scam");
            return new ScamResult(true, "lottery", "high");
        }

        System.out.println("No Scam Detected");
        return new ScamResult(false, "none", "low");
    }

    private boolean containsKeyword(String message, List<String> keywords) {
        return keywords.stream()
                .anyMatch(message::contains);
    }
}