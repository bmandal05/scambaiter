package com.scambaiter.scambaiter;

import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final PersonaEngine personaEngine = new PersonaEngine();

    public String generateReply(String scammerMessage, String scamType) {
        switch (scamType) {
            case "crypto":
                return "Oh my goodness! Double money? My grandson told me about bitcoin! How much do I need to start beta? I only have FD in SBI, will that work?";
            case "otp_phishing":
                return "OTP? What is OTP? My phone always shows some numbers... which one you want? Why you need it? Is this from bank?";
            case "job_fraud":
                return "Yes yes I need job urgently! What is the work exactly? Is there lunch break? What is the salary? Can I work from Panvel?";
            case "romance":
                return "Oh you seem very nice! But how do I know you are real? Can you send photo with today's newspaper? I am little scared only.";
            default:
                return "Sorry I don't understand. Can you explain again slowly? My English is not so good. What exactly you want from me?";
        }
    }
}