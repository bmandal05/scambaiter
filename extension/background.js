chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.type === "ANALYZE") {
    fetch("http://localhost:8080/analyze", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ message: request.message })
    })
    .then(res => res.json())
    .then(data => sendResponse({ success: true, data }))
    .catch(err => sendResponse({ success: false, error: err.message }));
    return true;
  }

  if (request.type === "AUTOREPLY") {
    fetch("http://localhost:8080/autoreply", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        message: request.message,
        voice: request.voice,
        sessionId: request.sessionId
      })
    })
    .then(res => res.text())
    .then(data => sendResponse({ success: true, data }))
    .catch(err => sendResponse({ success: false, error: err.message }));
    return true;
  }
  if (request.type === "STOP") {
    fetch("http://localhost:8080/stop", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ message: request.sessionId })
    })
    .then(res => res.text())
    .then(data => console.log("Session saved:", data))
    .catch(err => console.log("Stop error:", err));
    return true;
  }
});