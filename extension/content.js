let lastMessage = "";
let isAutobaiting = false;
let selectedVoice = "grandma";
let currentScamType = "crypto";

console.log("ScamBaiter loaded!");

const observer = new MutationObserver(() => {
  const allContainers = document.querySelectorAll('[data-testid="msg-container"]');
  if (allContainers.length === 0) return;

  const incomingMessages = Array.from(allContainers).filter(el =>
    !el.parentElement.className.includes('xuk3077')
  );

  if (incomingMessages.length === 0) return;

  const latest = incomingMessages[incomingMessages.length - 1].innerText.split('\n')[0];
  console.log("Latest incoming:", latest);

  if (!latest || latest === lastMessage) return;

  if (latest.startsWith('http') || latest.startsWith('www')) {
    lastMessage = latest;
    return;
  }

  if (latest.length > 300) {
    lastMessage = latest;
    return;
  }

  lastMessage = latest;

  if (isAutobaiting) {
    console.log("Auto-baiting reply to:", latest);
    setTimeout(() => sendAutoReply(latest), randomDelay());
  } else {
    checkForScam(latest);
  }
});

observer.observe(document.body, { childList: true, subtree: true });

window.addEventListener('load', () => {
  setTimeout(() => {
    const allContainers = document.querySelectorAll('[data-testid="msg-container"]');
    const incomingMessages = Array.from(allContainers).filter(el =>
      !el.parentElement.className.includes('xuk3077')
    );
    if (incomingMessages.length > 0) {
      lastMessage = incomingMessages[incomingMessages.length - 1].innerText.split('\n')[0];
      console.log("ScamBaiter initialized, watching after:", lastMessage.substring(0, 40));
    }
  }, 3000);
});

function checkForScam(message) {
  console.log("Checking for scam:", message);
  chrome.runtime.sendMessage({ type: "ANALYZE", message: message }, (response) => {
    console.log("Full backend response:", JSON.stringify(response));
    if (response && response.success && response.data.scam) {
      currentScamType = response.data.scamType;
      showPopup(message, response.data.scamType);
    } else {
      console.log("No scam - data:", JSON.stringify(response?.data));
    }
  });
}

function showPopup(message, scamType) {
  const existing = document.getElementById("scambaiter-popup");
  if (existing) existing.remove();

  const popup = document.createElement("div");
  popup.id = "scambaiter-popup";
  popup.innerHTML = `
    <div class="sb-header">
      <span>🛡️</span>
      <span class="sb-title">Scam Detected</span>
      <span class="sb-badge">${scamType}</span>
    </div>
    <div class="sb-message">"${message.substring(0, 80)}..."</div>
    <div class="sb-voice-label">Choose a voice</div>
    <select id="voice-select">
      <option value="grandma">👵 Confused Grandma</option>
      <option value="child">👦 Excited Child</option>
      <option value="man">👨 Suspicious Man</option>
      <option value="woman">👩 Chatty Woman</option>
      <option value="nerd">🤓 Tech Nerd</option>
    </select>
    <div class="sb-buttons">
      <button id="btn-auto">🤖 Auto-bait</button>
      <button id="btn-once">✅ Once</button>
      <button id="btn-no">❌ Ignore</button>
    </div>
  `;
  document.body.appendChild(popup);

  document.getElementById("btn-auto").onclick = () => {
    selectedVoice = document.getElementById("voice-select").value;
    isAutobaiting = true;
    showAutoBaitingBadge();
    sendAutoReply(message);
    popup.remove();
  };

  document.getElementById("btn-once").onclick = () => {
    selectedVoice = document.getElementById("voice-select").value;
    sendAutoReply(message);
    popup.remove();
  };

  document.getElementById("btn-no").onclick = () => {
    popup.remove();
  };
}

function showAutoBaitingBadge() {
  const existing = document.getElementById("scambaiter-badge");
  if (existing) return;

  const badge = document.createElement("div");
  badge.id = "scambaiter-badge";
  badge.innerHTML = `
    <span>🤖 Auto-baiting as ${selectedVoice}</span>
    <button id="btn-stop">Stop</button>
  `;
  badge.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    background: linear-gradient(135deg, #25d366, #128c7e);
    color: white;
    padding: 10px 16px;
    border-radius: 25px;
    z-index: 99999;
    font-family: 'Segoe UI', Arial, sans-serif;
    font-size: 13px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 10px;
    box-shadow: 0 4px 15px rgba(37,211,102,0.4);
  `;
  document.body.appendChild(badge);

  document.getElementById("btn-stop").style.cssText = `
    background: rgba(255,255,255,0.2);
    color: white;
    border: 1px solid rgba(255,255,255,0.3);
    border-radius: 12px;
    padding: 4px 12px;
    cursor: pointer;
    font-size: 12px;
    font-weight: 600;
  `;

  document.getElementById("btn-stop").onclick = () => {
    isAutobaiting = false;
  
  // Save conversation log
    const chatName = document.querySelector('[data-testid="conversation-header"] span')?.innerText || "default";
    const sessionId = chatName.replace(/\s+/g, '_').toLowerCase();
  
    chrome.runtime.sendMessage({ 
      type: "STOP", 
      sessionId: sessionId 
    });
  
    badge.remove();
   };
 }

function sendAutoReply(message) {
  const chatName = document.querySelector('[data-testid="conversation-header"] span')?.innerText || "default";
  const sessionId = chatName.replace(/\s+/g, '_').toLowerCase();

  chrome.runtime.sendMessage({
    type: "AUTOREPLY",
    message: message,
    voice: selectedVoice,
    sessionId: sessionId
  }, (response) => {
    if (response && response.success) {
      typeReply(response.data);
    }
  });
}

function typeReply(text) {
  const input = document.querySelector('div[contenteditable="true"][data-tab="10"]');
  if (!input) {
    console.log("ScamBaiter: input box not found!");
    return;
  }
  input.focus();
  document.execCommand("insertText", false, text);
  setTimeout(() => {
    const sendBtn = document.querySelector('button[data-tab="11"]');
    if (sendBtn) sendBtn.click();
  }, 500);
}

function randomDelay() {
  return Math.floor(Math.random() * 12000) + 8000;
}