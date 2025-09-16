(function() {
  const LS_SCORE = "ghcScore";
  const LS_ACHV = "ghcAchv";
  const LS_STATS = "ghcStats";

  function readScore() {
    return parseInt(localStorage.getItem(LS_SCORE) || "0", 10);
  }

  function writeScore(v) {
    localStorage.setItem(LS_SCORE, String(Math.max(0, v)));
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

  function readStats() {
    try {
      return JSON.parse(localStorage.getItem(LS_STATS) || "{}");
    } catch (e) {
      return {};
    }
  }

  function writeStats(stats) {
    localStorage.setItem(LS_STATS, JSON.stringify(stats));
  }

  function ensureInit() {
    if (localStorage.getItem(LS_SCORE) == null) {
      writeScore(1337);
    }
    if (localStorage.getItem(LS_ACHV) == null) {
      writeAchv([]);
    }
    if (localStorage.getItem(LS_STATS) == null) {
      writeStats({
        totalCommitsLost: 42,
        totalConflicts: 999,
        totalResets: 13,
        sanityLevel: 404,
        lastPlayed: new Date().toISOString()
      });
    }
  }

  function createParticles() {
    const container = document.getElementById('particles');
    if (!container) return;
    
    container.innerHTML = '';
    
    const colors = ['#58a6ff', '#00ff41', '#ff6b6b', '#f9ca24', '#ff9ff3'];
    const particleCount = window.innerWidth < 768 ? 30 : 60;
    
    for (let i = 0; i < particleCount; i++) {
      const particle = document.createElement('div');
      particle.className = 'particle';
      particle.style.left = Math.random() * 100 + '%';
      particle.style.animationDelay = Math.random() * 10 + 's';
      particle.style.animationDuration = (Math.random() * 6 + 4) + 's';
      particle.style.background = colors[Math.floor(Math.random() * colors.length)];
      
      const size = Math.random() * 3 + 1;
      particle.style.width = size + 'px';
      particle.style.height = size + 'px';
      
      container.appendChild(particle);
    }
  }

  function mountHUD(opts = {}) {
    ensureInit();
    
    const el = document.getElementById("hud");
    if (!el) return;

    const score = readScore();
    const achv = readAchv();
    const stats = readStats();
    
    const achievementsList = achv.slice(-4).map(a => 
      `<div class="achv ${a.type || 'good'}" title="${a.label}">🏅 ${a.label}</div>`
    ).join("");
    
    el.innerHTML = `
      <div class="hud-left">
        ${opts.showHome !== false ? `<a class="hud-home" href="/">← 로비</a>` : ""}
        <div class="logo neon">GHC</div>
        <div class="badge">
          SCORE&nbsp;<span id="hud-score">${score.toLocaleString()}</span>
          <span class="sublabel">pts</span>
        </div>
      </div>
      <div class="hud-right" id="hud-achv">
        ${achievementsList || '<div class="achv">🎮 게임 시작!</div>'}
      </div>
    `;

    updateStatsDisplay(stats);
  }

  function updateStatsDisplay(stats) {
    const scoreEl = document.getElementById("hud-score");
    if (scoreEl) {
      const currentScore = readScore();
      animateNumber(scoreEl, parseInt(scoreEl.textContent.replace(/,/g, '')), currentScore);
    }
  }

  function animateNumber(element, start, end, duration = 1000) {
    const range = end - start;
    const increment = range / (duration / 16);
    let current = start;
    
    const timer = setInterval(() => {
      current += increment;
      if ((increment > 0 && current >= end) || (increment < 0 && current <= end)) {
        current = end;
        clearInterval(timer);
      }
      element.textContent = Math.floor(current).toLocaleString();
    }, 16);
  }

  function addScore(delta) {
    const currentScore = readScore();
    const newScore = Math.max(0, currentScore + Number(delta || 0));
    writeScore(newScore);
    
    const scoreEl = document.getElementById("hud-score");
    if (scoreEl) {
      animateNumber(scoreEl, currentScore, newScore, 500);
      
      if (delta > 0) {
        showFloatingScore(delta, 'positive');
      } else if (delta < 0) {
        showFloatingScore(delta, 'negative');
      }
    }
    
    checkScoreAchievements(newScore);
    return newScore;
  }

  function showFloatingScore(delta, type) {
    const scoreEl = document.getElementById("hud-score");
    if (!scoreEl) return;

    const floating = document.createElement('div');
    floating.textContent = `${delta > 0 ? '+' : ''}${delta}`;
    floating.style.cssText = `
      position: absolute;
      top: 50px;
      left: 50%;
      transform: translateX(-50%);
      color: ${type === 'positive' ? '#00ff41' : '#ff6b6b'};
      font-weight: bold;
      font-size: 1.2rem;
      pointer-events: none;
      z-index: 1000;
      text-shadow: 0 0 10px currentColor;
      animation: float-score 2s ease-out forwards;
    `;

    const style = document.createElement('style');
    style.textContent = `
      @keyframes float-score {
        0% {
          opacity: 1;
          transform: translateX(-50%) translateY(0);
        }
        100% {
          opacity: 0;
          transform: translateX(-50%) translateY(-50px);
        }
      }
    `;
    document.head.appendChild(style);

    scoreEl.parentElement.style.position = 'relative';
    scoreEl.parentElement.appendChild(floating);

    setTimeout(() => {
      floating.remove();
      style.remove();
    }, 2000);
  }

  function checkScoreAchievements(score) {
    const milestones = [
      { score: 1000, id: 'score1k', label: '첫 천점', type: 'good' },
      { score: 5000, id: 'score5k', label: '오천점 달성', type: 'good' },
      { score: 10000, id: 'score10k', label: '만점왕', type: 'good' },
      { score: 50000, id: 'score50k', label: '전설의 개발자', type: 'good' },
      { score: 100000, id: 'score100k', label: 'Git 마스터', type: 'good' }
    ];

    milestones.forEach(milestone => {
      if (score >= milestone.score) {
        unlock(milestone.id, milestone.label, milestone.type);
      }
    });
  }

  function unlock(id, label, type = 'good') {
    const list = readAchv();
    
    if (list.find(x => x.id === id)) {
      return false;
    }

    const item = {
      id,
      label,
      type,
      ts: Date.now()
    };
    
    list.push(item);
    writeAchv(list);
    
    showAchievementNotification(item);
    updateAchievementDisplay(item);
    
    return true;
  }

  function showAchievementNotification(achievement) {
    const notification = document.createElement('div');
    notification.innerHTML = `
      <div style="
        position: fixed;
        top: 20px;
        right: 20px;
        background: linear-gradient(145deg, #1a1a1a, #2a2a2a);
        border: 2px solid ${achievement.type === 'good' ? '#00ff41' : '#ff6b6b'};
        border-radius: 15px;
        padding: 15px 20px;
        color: white;
        z-index: 10000;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
        animation: achievement-slide 3s ease-in-out forwards;
        backdrop-filter: blur(10px);
      ">
        <div style="font-size: 1.2rem; font-weight: bold; margin-bottom: 5px;">
          🏆 업적 달성!
        </div>
        <div style="color: ${achievement.type === 'good' ? '#00ff41' : '#ff6b6b'};">
          ${achievement.label}
        </div>
      </div>
    `;

    const style = document.createElement('style');
    style.textContent = `
      @keyframes achievement-slide {
        0% {
          transform: translateX(100%);
          opacity: 0;
        }
        15%, 85% {
          transform: translateX(0);
          opacity: 1;
        }
        100% {
          transform: translateX(100%);
          opacity: 0;
        }
      }
    `;
    document.head.appendChild(style);

    document.body.appendChild(notification);

    setTimeout(() => {
      notification.remove();
      style.remove();
    }, 3000);
  }

  function updateAchievementDisplay(newAchievement) {
    const hudAchv = document.getElementById("hud-achv");
    if (!hudAchv) return;

    const achievementEl = document.createElement('div');
    achievementEl.className = `achv ${newAchievement.type}`;
    achievementEl.innerHTML = `🏅 ${newAchievement.label}`;
    achievementEl.title = newAchievement.label;
    
    hudAchv.insertBefore(achievementEl, hudAchv.firstChild);
    
    while (hudAchv.children.length > 4) {
      hudAchv.removeChild(hudAchv.lastChild);
    }
    
    achievementEl.style.animation = 'achv-appear 0.5s ease-out';
    
    const style = document.createElement('style');
    style.textContent = `
      @keyframes achv-appear {
        0% {
          transform: scale(0) rotate(180deg);
          opacity: 0;
        }
        100% {
          transform: scale(1) rotate(0deg);
          opacity: 1;
        }
      }
    `;
    document.head.appendChild(style);
  }

  function resetProgress() {
    const confirmed = confirm('정말로 모든 진행상황을 초기화하시겠습니까?\n(점수, 업적, 통계가 모두 삭제됩니다)');
    
    if (!confirmed) return;

    writeScore(0);
    writeAchv([]);
    writeStats({
      totalCommitsLost: 0,
      totalConflicts: 0,
      totalResets: 0,
      sanityLevel: 100,
      lastPlayed: new Date().toISOString()
    });
    
    const scoreEl = document.getElementById("hud-score");
    if (scoreEl) {
      scoreEl.textContent = "0";
    }
    
    const hudAchv = document.getElementById("hud-achv");
    if (hudAchv) {
      hudAchv.innerHTML = '<div class="achv">🎮 새로운 시작!</div>';
    }

    showResetNotification();
  }

  function showResetNotification() {
    const notification = document.createElement('div');
    notification.innerHTML = `
      <div style="
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: linear-gradient(145deg, #1a1a1a, #2a2a2a);
        border: 2px solid #ff6b6b;
        border-radius: 20px;
        padding: 30px;
        color: white;
        z-index: 10000;
        text-align: center;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.7);
        backdrop-filter: blur(15px);
        animation: reset-notification 2s ease-in-out forwards;
      ">
        <div style="font-size: 3rem; margin-bottom: 10px;">💀</div>
        <div style="font-size: 1.5rem; font-weight: bold; margin-bottom: 10px;">
          SYSTEM RESET
        </div>
        <div style="color: #ff6b6b;">
          모든 데이터가 초기화되었습니다
        </div>
      </div>
    `;

    const style = document.createElement('style');
    style.textContent = `
      @keyframes reset-notification {
        0% {
          transform: translate(-50%, -50%) scale(0);
          opacity: 0;
        }
        20% {
          transform: translate(-50%, -50%) scale(1.1);
          opacity: 1;
        }
        80% {
          transform: translate(-50%, -50%) scale(1);
          opacity: 1;
        }
        100% {
          transform: translate(-50%, -50%) scale(0);
          opacity: 0;
        }
      }
    `;
    document.head.appendChild(style);

    document.body.appendChild(notification);

    setTimeout(() => {
      notification.remove();
      style.remove();
    }, 2000);
  }

  function updateStats(updates) {
    const stats = readStats();
    const newStats = { ...stats, ...updates };
    writeStats(newStats);
    return newStats;
  }

  function addInteractiveEffects() {
    const cabinets = document.querySelectorAll('.cabinet');
    
    cabinets.forEach((cabinet, index) => {
      cabinet.addEventListener('mouseenter', () => {
        const screen = cabinet.querySelector('.cabinet-screen');
        const emoji = screen.querySelector('span');
        
        screen.style.transform = 'scale(1.05)';
        screen.style.filter = 'brightness(1.3) saturate(1.4)';
        
        if (emoji) {
          emoji.style.transform = 'scale(1.2) rotate(5deg)';
          emoji.style.textShadow = '0 0 20px currentColor';
        }

        cabinet.style.zIndex = '10';
      });
      
      cabinet.addEventListener('mouseleave', () => {
        const screen = cabinet.querySelector('.cabinet-screen');
        const emoji = screen.querySelector('span');
        
        screen.style.transform = '';
        screen.style.filter = '';
        
        if (emoji) {
          emoji.style.transform = '';
          emoji.style.textShadow = '';
        }

        cabinet.style.zIndex = '';
      });
      
      cabinet.addEventListener('click', (e) => {
        if (e.target.classList.contains('btn-play')) {
          e.target.style.transform = 'scale(0.95)';
          
          const ripple = document.createElement('div');
          ripple.style.cssText = `
            position: absolute;
            background: rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            width: 10px;
            height: 10px;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            animation: ripple 0.6s ease-out;
            pointer-events: none;
          `;
          
          const rippleStyle = document.createElement('style');
          rippleStyle.textContent = `
            @keyframes ripple {
              0% {
                transform: translate(-50%, -50%) scale(1);
                opacity: 1;
              }
              100% {
                transform: translate(-50%, -50%) scale(20);
                opacity: 0;
              }
            }
          `;
          document.head.appendChild(rippleStyle);
          
          e.target.style.position = 'relative';
          e.target.appendChild(ripple);
          
          setTimeout(() => {
            e.target.style.transform = '';
            ripple.remove();
            rippleStyle.remove();
          }, 200);
        }
      });

      cabinet.addEventListener('mousemove', (e) => {
        const rect = cabinet.getBoundingClientRect();
        const x = e.clientX - rect.left - rect.width / 2;
        const y = e.clientY - rect.top - rect.height / 2;
        
        const rotateX = (y / rect.height) * 10;
        const rotateY = -(x / rect.width) * 10;
        
        cabinet.style.transform = `translateY(-20px) scale(1.05) perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
      });

      cabinet.addEventListener('mouseleave', () => {
        cabinet.style.transform = '';
      });
    });
  }

  function initKonamiCode() {
    let sequence = '';
    const konamiCode = 'ArrowUpArrowUpArrowDownArrowDownArrowLeftArrowRightArrowLeftArrowRightKeyBKeyA';
    
    document.addEventListener('keydown', (e) => {
      sequence += e.code;
      
      if (sequence.length > konamiCode.length) {
        sequence = sequence.slice(-konamiCode.length);
      }
      
      if (sequence === konamiCode) {
        activateKonamiMode();
        unlock('konami', '코나미 코드', 'good');
        addScore(1000);
      }
    });
  }

  function activateKonamiMode() {
    const body = document.body;
    body.classList.add('matrix-mode');
    
    const notification = document.createElement('div');
    notification.innerHTML = `
      <div style="
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        background: linear-gradient(45deg, #000, #003300);
        border: 2px solid #00ff41;
        border-radius: 15px;
        padding: 20px;
        color: #00ff41;
        z-index: 10000;
        font-family: 'JetBrains Mono', monospace;
        text-align: center;
        animation: matrix-notification 3s ease-in-out forwards;
        box-shadow: 0 0 30px #00ff41;
      ">
        <div style="font-size: 1.5rem; margin-bottom: 10px;">💊 KONAMI MODE ACTIVATED</div>
        <div>Welcome to the Matrix...</div>
      </div>
    `;

    const style = document.createElement('style');
    style.textContent = `
      @keyframes matrix-notification {
        0% { transform: translateX(-50%) scale(0); opacity: 0; }
        20% { transform: translateX(-50%) scale(1); opacity: 1; }
        80% { opacity: 1; }
        100% { transform: translateX(-50%) scale(0); opacity: 0; }
      }
    `;
    document.head.appendChild(style);

    document.body.appendChild(notification);
    
    setTimeout(() => {
      body.classList.remove('matrix-mode');
      notification.remove();
      style.remove();
    }, 3000);
  }

  function typewriterEffect(element, text, speed = 80) {
    return new Promise((resolve) => {
      let i = 0;
      element.textContent = '';
      
      const timer = setInterval(() => {
        if (i < text.length) {
          element.textContent += text.charAt(i);
          i++;
        } else {
          clearInterval(timer);
          resolve();
        }
      }, speed);
    });
  }

  function initRandomEvents() {
    const events = [
      { 
        chance: 0.1, 
        message: "🚨 긴급: 운영 서버 폭발! 즉시 핫픽스 필요!", 
        type: "error",
        action: () => addScore(-100)
      },
      { 
        chance: 0.05, 
        message: "🎉 보너스: 팀장님이 코드리뷰 승인!", 
        type: "success",
        action: () => addScore(200)
      },
      { 
        chance: 0.08, 
        message: "😱 충격: 3개월 전 코드에서 버그 발견!", 
        type: "warning",
        action: () => unlock('archaeologist', '고고학자', 'bad')
      }
    ];

    setInterval(() => {
      const event = events.find(e => Math.random() < e.chance);
      if (event) {
        showRandomEvent(event);
      }
    }, 30000);
  }

  function showRandomEvent(event) {
    const notification = document.createElement('div');
    const colors = {
      error: '#ff6b6b',
      success: '#00ff41',
      warning: '#f9ca24'
    };

    notification.innerHTML = `
      <div style="
        position: fixed;
        bottom: 20px;
        left: 20px;
        background: linear-gradient(145deg, #1a1a1a, #2a2a2a);
        border: 2px solid ${colors[event.type]};
        border-radius: 15px;
        padding: 15px 20px;
        color: white;
        z-index: 10000;
        max-width: 300px;
        animation: random-event 4s ease-in-out forwards;
        backdrop-filter: blur(10px);
      ">
        <div style="color: ${colors[event.type]}; font-weight: bold; margin-bottom: 5px;">
          랜덤 이벤트!
        </div>
        <div>${event.message}</div>
      </div>
    `;

    const style = document.createElement('style');
    style.textContent = `
      @keyframes random-event {
        0% { transform: translateX(-100%); opacity: 0; }
        15%, 85% { transform: translateX(0); opacity: 1; }
        100% { transform: translateX(-100%); opacity: 0; }
      }
    `;
    document.head.appendChild(style);

    document.body.appendChild(notification);
    
    if (event.action) {
      event.action();
    }

    setTimeout(() => {
      notification.remove();
      style.remove();
    }, 4000);
  }

  window.ghcMountHUD = mountHUD;
  window.ghcAddScore = addScore;
  window.ghcUnlock = unlock;
  window.ghcResetProgress = resetProgress;
  window.ghcUpdateStats = updateStats;
  window.ghcCreateParticles = createParticles;
  window.ghcAddInteractiveEffects = addInteractiveEffects;
  window.ghcTypewriterEffect = typewriterEffect;

  document.addEventListener('DOMContentLoaded', () => {
    createParticles();
    initKonamiCode();
    initRandomEvents();
    
    window.addEventListener('resize', () => {
      createParticles();
    });
  });

  if (typeof window !== 'undefined') {
    window.addEventListener('beforeunload', () => {
      updateStats({ lastPlayed: new Date().toISOString() });
    });
  }
})();