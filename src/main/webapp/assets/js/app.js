/* ============================================================
   TimeTrack - Interactive Client JavaScript
   Stopwatch, Charts rendering, Filter management & Modals
   ============================================================ */

document.addEventListener('DOMContentLoaded', function () {
    initStopwatch();
    initFlashAlerts();
});

/* ─── Live Stopwatch Module ─────────────────────────────────── */
let timerInterval = null;
let timerSeconds = 0;
let isTimerRunning = false;
let sessionStartTime = null;

function initStopwatch() {
    const timerDisplay = document.getElementById('timer-digits');
    if (!timerDisplay) return;

    const startBtn = document.getElementById('btn-timer-start');
    const pauseBtn = document.getElementById('btn-timer-pause');
    const resetBtn = document.getElementById('btn-timer-reset');
    const logBtn = document.getElementById('btn-timer-log');

    // Retrieve any persisted timer from sessionStorage
    const savedSeconds = sessionStorage.getItem('timetrack_timer_seconds');
    const savedRunning = sessionStorage.getItem('timetrack_timer_running');
    const savedStart = sessionStorage.getItem('timetrack_timer_start');

    if (savedSeconds) {
        timerSeconds = parseInt(savedSeconds, 10);
        updateTimerDisplay(timerSeconds);
    }
    if (savedStart) {
        sessionStartTime = savedStart;
    }
    if (savedRunning === 'true') {
        startTimer();
    }

    if (startBtn) {
        startBtn.addEventListener('click', function () {
            startTimer();
        });
    }

    if (pauseBtn) {
        pauseBtn.addEventListener('click', function () {
            pauseTimer();
        });
    }

    if (resetBtn) {
        resetBtn.addEventListener('click', function () {
            resetTimer();
        });
    }

    if (logBtn) {
        logBtn.addEventListener('click', function () {
            openQuickLogModal();
        });
    }
}

function startTimer() {
    if (isTimerRunning) return;
    isTimerRunning = true;

    if (!sessionStartTime) {
        const now = new Date();
        sessionStartTime = formatTimeStr(now);
        sessionStorage.setItem('timetrack_timer_start', sessionStartTime);
    }

    const startBtn = document.getElementById('btn-timer-start');
    const pauseBtn = document.getElementById('btn-timer-pause');
    if (startBtn) startBtn.style.display = 'none';
    if (pauseBtn) pauseBtn.style.display = 'inline-flex';

    timerInterval = setInterval(function () {
        timerSeconds++;
        sessionStorage.setItem('timetrack_timer_seconds', timerSeconds);
        sessionStorage.setItem('timetrack_timer_running', 'true');
        updateTimerDisplay(timerSeconds);
    }, 1000);
}

function pauseTimer() {
    if (!isTimerRunning) return;
    isTimerRunning = false;
    clearInterval(timerInterval);
    sessionStorage.setItem('timetrack_timer_running', 'false');

    const startBtn = document.getElementById('btn-timer-start');
    const pauseBtn = document.getElementById('btn-timer-pause');
    if (startBtn) startBtn.style.display = 'inline-flex';
    if (pauseBtn) pauseBtn.style.display = 'none';
}

function resetTimer() {
    pauseTimer();
    timerSeconds = 0;
    sessionStartTime = null;
    sessionStorage.removeItem('timetrack_timer_seconds');
    sessionStorage.removeItem('timetrack_timer_running');
    sessionStorage.removeItem('timetrack_timer_start');
    updateTimerDisplay(0);
}

function updateTimerDisplay(totalSecs) {
    const display = document.getElementById('timer-digits');
    if (!display) return;

    const hrs = Math.floor(totalSecs / 3600);
    const mins = Math.floor((totalSecs % 3600) / 60);
    const secs = totalSecs % 60;

    const pad = (n) => (n < 10 ? '0' + n : n);
    display.textContent = pad(hrs) + ':' + pad(mins) + ':' + pad(secs);
}

function formatTimeStr(dateObj) {
    const pad = (n) => (n < 10 ? '0' + n : n);
    return pad(dateObj.getHours()) + ':' + pad(dateObj.getMinutes());
}

function openQuickLogModal() {
    if (timerSeconds < 60) {
        if (!confirm("Timer has recorded less than 1 minute. Would you like to log this session anyway?")) {
            return;
        }
    }

    const now = new Date();
    const endTime = formatTimeStr(now);
    const startTime = sessionStartTime || formatTimeStr(new Date(now.getTime() - timerSeconds * 1000));
    const today = now.toISOString().split('T')[0];

    // Populate modal or redirect to new log form with query params
    const contextPath = window.contextPath || '';
    const newLogUrl = contextPath + '/timelog/new?date=' + today + '&start=' + startTime + '&end=' + endTime;
    
    // Reset timer
    resetTimer();

    window.location.href = newLogUrl;
}

/* ─── Flash Alert Auto-Dismiss ───────────────────────────────── */
function initFlashAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(function () {
                if (alert.parentNode) alert.parentNode.removeChild(alert);
            }, 500);
        }, 5000);
    });
}

/* ─── Delete Confirmation ────────────────────────────────────── */
function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this item? This action cannot be undone.');
}

/* ─── Chart Rendering Helpers ────────────────────────────────── */
function renderDailyTrendChart(canvasId, dailyData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !dailyData) return;

    const labels = Object.keys(dailyData);
    const dataValues = Object.values(dailyData);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Hours Logged',
                data: dataValues,
                backgroundColor: 'rgba(79, 70, 229, 0.8)',
                borderColor: '#4f46e5',
                borderWidth: 1.5,
                borderRadius: 6,
                maxBarThickness: 36
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: function (ctx) {
                            return ctx.parsed.y + ' hrs';
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (val) { return val + 'h'; }
                    },
                    grid: {
                        color: '#f1f5f9'
                    }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });
}

function renderTaskDistributionChart(canvasId, taskData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !taskData) return;

    const labels = Object.keys(taskData);
    const dataValues = Object.values(taskData);

    if (labels.length === 0) return;

    const palette = [
        '#4f46e5', '#06b6d4', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6', '#64748b'
    ];

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: dataValues,
                backgroundColor: palette.slice(0, labels.length),
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        boxWidth: 12,
                        font: { size: 12 }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (ctx) {
                            return ' ' + ctx.label + ': ' + ctx.parsed + ' hrs';
                        }
                    }
                }
            },
            cutout: '65%'
        }
    });
}

function renderUserHoursChart(canvasId, userData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !userData) return;

    const labels = Object.keys(userData);
    const dataValues = Object.values(userData);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Total Hours',
                data: dataValues,
                backgroundColor: 'rgba(6, 182, 212, 0.85)',
                borderColor: '#0891b2',
                borderWidth: 1.5,
                borderRadius: 6,
                maxBarThickness: 32
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: function (ctx) {
                            return ctx.parsed.x + ' hrs';
                        }
                    }
                }
            },
            scales: {
                x: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (val) { return val + 'h'; }
                    },
                    grid: { color: '#f1f5f9' }
                },
                y: {
                    grid: { display: false }
                }
            }
        }
    });
}
