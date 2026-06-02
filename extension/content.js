let lastMessage = "";
let isAutobaiting = false;
let baitingInterval = null;

console.log("ScamBaiter loaded!");

const observer = new MutationObserver(() => {
  const containers = document.querySelectorAll('.message-in [data-testid="msg-container"]');
  if (containers.length === 0) return;

  const latest = containers[containers.length - 1].innerText.split('\n')[0];
  if (!latest || latest === lastMessage) return;

  lastMessage = latest;

  if (isAutobaiting) {
    // Already in auto mode — just reply automatically
    console.log("Auto-baiting reply to:", latest);
    setTimeout(() => sendReply(latest), randomDelay());
  } else {
    checkForScam(latest);
  }
});

observer.observe(document.body, { childList: true, subtree: true });

function checkForScam(message) {
  chrome.runtime.sendMessage({ type: "ANALYZE", message: message }, (response) => {
    console.log("Backend response:", response);
    if (response && response.success && response.data.scam) {
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
    <p>🚨 <b>Scam detected!</b> (${scamType})</p>
    <p style="font-size:12px;color:#aaa;">"${message.substring(0, 60)}..."</p>
    <button id="btn-auto">🤖 Auto-bait them!</button>
    <button id="btn-once">✅ Reply once</button>
    <button id="btn-no">❌ Ignore</button>
  `;
  document.body.appendChild(popup);

  document.getElementById("btn-auto").onclick = () => {
    isAutobaiting = true;
    showAutoBaitingBadge();
    sendReply(message);
    popup.remove();
  };

  document.getElementById("btn-once").onclick = () => {
    sendReply(message);
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
    <span>🤖 Auto-baiting active</span>
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
    console.log("Auto-baiting stopped.");
  };
}

function sendReply(message) {
  chrome.runtime.sendMessage({ type: "REPLY", message: message }, (response) => {
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

// Random delay between 8-20 seconds so it feels human
function randomDelay() {
  return Math.floor(Math.random() * 12000) + 8000;
}