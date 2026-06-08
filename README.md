# 🛡️ ScamBaiter

> A Chrome extension + Java backend that detects WhatsApp scam messages and automatically baits scammers back using AI-powered personas — so you never have to waste your own time.

---

## 📌 What It Does

When a scam message arrives on WhatsApp Web, ScamBaiter:
1. Detects it using **Gemini AI** (with keyword fallback)
2. Shows a popup asking **"Want me to handle this?"**
3. If you say yes — picks a persona and **auto-replies continuously**
4. Remembers the full conversation so replies never repeat
5. Saves the entire baiting conversation to a log file when you stop

All processing happens **locally on your machine**. Your WhatsApp messages never go to any external server except the Gemini AI API call.

---

## ✨ Features

- 🤖 **Auto-baiting mode** — replies automatically every time the scammer responds
- 🎭 **5 AI personas** — Confused Grandma, Excited Child, Suspicious Man, Chatty Woman, Tech Nerd
- 🧠 **Conversation memory** — AI remembers what was said and never repeats itself
- 🔍 **Smart scam detection** — Gemini AI classifies scam type with keyword fallback
- 📁 **Conversation logging** — saves every baiting session as a `.txt` file
- 🔒 **100% local** — no third-party servers see your chats
- ⏱️ **Human-like delays** — random 8-20 second reply timing

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.x |
| AI | Google Gemini API (gemini-2.5-flash) |
| Frontend | Chrome Extension (Manifest V3) |
| HTTP Client | Java HttpClient (built-in) |
| JSON Parsing | Gson |
| Build Tool | Maven |

---

## 📁 Project Structure

```
scambaiter/
├── src/main/java/com/scambaiter/scambaiter/
│   ├── ScambaiterApplication.java     # Spring Boot entry point
│   ├── HelloController.java           # REST API endpoints
│   ├── ScamDetector.java              # Keyword-based scam detection
│   ├── AiScamDetector.java            # Gemini AI scam detection
│   ├── GeminiService.java             # Gemini API integration
│   ├── PersonaEngine.java             # 5 AI voice personas
│   ├── ConversationMemory.java        # Per-session conversation history
│   ├── MessageRequest.java            # Request model
│   ├── ReplyRequest.java              # Reply request model
│   └── ScamResult.java                # Scam detection result model
├── extension/
│   ├── manifest.json                  # Chrome extension config
│   ├── content.js                     # WhatsApp Web DOM interaction
│   ├── background.js                  # Handles API calls from extension
│   └── popup.css                      # Extension UI styling
└── logs/                              # Saved baiting conversations
```

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Google Chrome
- Gemini API key (free at [aistudio.google.com](https://aistudio.google.com))

### Step 1 — Configure API key

Create `src/main/resources/application.properties.example`:
```properties
spring.application.name=scambaiter
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
```

### Step 2 — Start the Java backend
```bash
mvnw.cmd spring-boot:run
```
Server starts at `http://localhost:8080`

### Step 3 — Load the Chrome extension
1. Open Chrome → `chrome://extensions`
2. Enable **Developer mode**
3. Click **Load unpacked** → select the `extension/` folder

### Step 4 — Open WhatsApp Web
Go to [web.whatsapp.com](https://web.whatsapp.com) and log in.

### Step 5 — Test it
Ask someone to send a scam message. The popup will appear automatically!

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/hello` | Health check |
| POST | `/analyze` | Detect if a message is a scam |
| POST | `/autoreply` | Generate AI baiting reply |
| POST | `/stop` | Save and clear conversation session |

---

## 🎭 Personas

| Persona | Behaviour |
|---|---|
| 👵 Confused Grandma | Slow, trusting, mentions grandson, asks to repeat everything |
| 👦 Excited Child | Doesn't understand money, asks silly questions, mentions mom |
| 👨 Suspicious Man | Demands proof, negotiates everything, never trusts easily |
| 👩 Chatty Woman | Goes off topic constantly, tells personal stories |
| 🤓 Tech Nerd | Asks for whitepapers, smart contract audits, technical specs |

---

## 🔒 Security

- No WhatsApp messages sent to external servers
- All conversation logs stored locally in `logs/` folder

---

## ⚠️ Known Limitations

- WhatsApp Web DOM selectors may break if WhatsApp updates their frontend
- Gemini free tier has a daily request limit (20 requests/day on free tier)
- Java server must be running manually before using the extension
- Only monitors the currently open WhatsApp chat

---

## 🧑‍💻 Built By

**Bhumika Mandal**
Fresher | Java Developer
[GitHub](https://github.com/bmandal05)

---

## 📄 License

MIT License — feel free to use, modify, and share.
