(function() {
  const LS_SCORE = "ghcScore";
  const LS_ACHV = "ghcAchv";

  function readScore() {
    return parseInt(localStorage.getItem(LS_SCORE) || "0", 10);
  }

  function writeScore(v) {
    localStorage.setItem(LS_SCORE, String(v));
  }

  function readAchv() {
    try {
      return JSON.parse(localStorage.getItem(LS_ACHV) || "[]");
    } catch (e) {
      return [];
    }
  }

  function writeAchv(a) {
    localStorage.setItem(LS_ACHV, JSON.stringify(a));
  }

  function ensureInit() {
    if (localStorage.getItem(LS_SCORE) == null) {
      writeScore(0);
    }
    if (localStorage.getItem(LS_ACHV) == null) {
      writeAchv([]);
    }
  }

  function mountHUD(opts) {
    ensureInit();
    
    const el = document.getElementById("hud");
    if (!el) return;

    const score = readScore();
    const achv = readAchv();
    
    el.innerHTML = `
      <div class="hud-left">
        ${opts && opts.showHome !== false ? `<a class="hud-home" href="/">← 로비</a>` : ""}
        <div class="logo">GHC</div>
        <div class="badge">SCORE&nbsp;<span id="hud-score">${score}</span></div>
      </div>
      <div class="hud-right" id="hud-achv">
        ${achv.slice(-4).map(a => `<div class="achv ${a.type || ''}">🏅 ${a.label}</div>`).join("")}
      </div>
    `;
  }

  function addScore(delta) {
    const v = Math.max(0, readScore() + Number(delta || 0));
    writeScore(v);
    
    const s = document.getElementById("hud-score");
    if (s) {
      s.textContent = v;
    }
    
    return v;
  }

  function unlock(id, label, type) {
    const list = readAchv();
    
    if (!list.find(x => x.id === id)) {
      const item = {
        id,
        label,
        type: type || "good",
        ts: Date.now()
      };
      
      list.push(item);
      writeAchv(list);
      
      const box = document.getElementById("hud-achv");
      if (box) {
        const tag = document.createElement("div");
        tag.className = `achv ${item.type}`;
        tag.textContent = `🏅 ${item.label}`;
        box.prepend(tag);
        
        while (box.children.length > 6) {
          box.lastChild.remove();
        }
      }
    }
  }

  function resetProgress() {
    writeScore(0);
    writeAchv([]);
    
    const s = document.getElementById("hud-score");
    if (s) {
      s.textContent = "0";
    }
    
    const box = document.getElementById("hud-achv");
    if (box) {
      box.innerHTML = "";
    }
  }

  window.ghcMountHUD = mountHUD;
  window.ghcAddScore = addScore;
  window.ghcUnlock = unlock;
  window.ghcResetProgress = resetProgress;
})();