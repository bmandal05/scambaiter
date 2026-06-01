console.log("CONTENT SCRIPT LOADED");

chrome.runtime.sendMessage(
  { type: "TEST" },
  (response) => {
    console.log("Response:", response);
  }
);
let lastMessage = "";

const observer = new MutationObserver(() => {
  const messages = document.querySelectorAll('div[class*="message-in"] span.selectable-text');
  if (messages.length === 0) return;

  const latest = messages[messages.length - 1].innerText;
  if (!latest || latest === lastMessage) return;

  lastMessage = latest;
  checkForScam(latest);
});

observer.observe(document.body, { childList: true, subtree: true });

function checkForScam(message) {
  chrome.runtime.sendMessage({ type: "ANALYZE", message: message }, (response) => {
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
    <button id="btn-yes">✅ Yes, bait them!</button>
    <button id="btn-no">❌ No, ignore</button>
  `;
  document.body.appendChild(popup);

  document.getElementById("btn-yes").onclick = () => {
    sendReply(message);
    popup.remove();
  };

  document.getElementById("btn-no").onclick = () => {
    popup.remove();
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
    alert("ScamBaiter: Could not find WhatsApp input box!");
    return;
  }

  input.focus();
  document.execCommand("insertText", false, text);

  setTimeout(() => {
    const sendBtn = document.querySelector('button[data-tab="11"]');
    if (sendBtn) sendBtn.click();
  }, 500);
}