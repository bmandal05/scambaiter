let lastMessage = "";
let isAutobaiting = false;
let selectedVoice = "grandma";
let currentScamType = "crypto";

console.log("ScamBaiter loaded!");

const observer = new MutationObserver(() => {
  const containers = document.querySelectorAll('.message-in [data-testid="msg-container"]');
  if (containers.length === 0) return;

  const latest = containers[containers.length - 1].innerText.split('\n')[0];
  if (!latest || latest === lastMessage) return;

  lastMessage = latest;

  if (isAutobaiting) {
    console.log("Auto-baiting reply to:", latest);
    setTimeout(() => sendAutoReply(latest), randomDelay());
  } else {
    checkForScam(latest);
  }
});

observer.observe(document.body, { childList: true, subtree: true });

function checkForScam(message) {
  chrome.runtime.sendMessage({ type: "ANALYZE", message: message }, (response) => {
    if (response && response.success && response.data.scam) {
      currentScamType = response.data.scamType;
      showPopup(message, response.data.scamType);
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
    background: #25d366;
    color: white;
    padding: 8px 14px;
    border-radius: 20px;
    z-index: 99999;
    font-family: Arial, sans-serif;
    font-size: 13px;
    font-weight: bold;
    display: flex;
    align-items: center;
    gap: 10px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.3);
  `;
  document.body.appendChild(badge);

  document.getElementById("btn-stop").style.cssText = `
    background: white;
    color: #25d366;
    border: none;
    border-radius: 10px;
    padding: 3px 10px;
    cursor: pointer;
    font-weight: bold;
  `;

  document.getElementById("btn-stop").onclick = () => {
    isAutobaiting = false;
    badge.remove();
  };
}

function sendAutoReply(message) {
  chrome.runtime.sendMessage({
    type: "AUTOREPLY",
    message: message,
    voice: selectedVoice
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