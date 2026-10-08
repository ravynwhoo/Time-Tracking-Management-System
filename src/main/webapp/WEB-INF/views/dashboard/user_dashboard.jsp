<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="User Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <!-- Welcome & Action Header -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
        <div>
            <h1 style="font-size: 1.6rem; font-weight: 800; color: var(--text-main);">
                Welcome back, <c:out value="${sessionScope.currentUser.name}"/>! 👋
            </h1>
            <p style="color: var(--text-muted); font-size: 0.9rem;">
                Here is your productivity overview for today (<c:out value="${todayDate}"/>).
            </p>
        </div>
        <div style="display: flex; gap: 0.75rem;">
            <a href="${pageContext.request.contextPath}/timelog/new" class="btn btn-primary">
                <i class="fa-solid fa-plus"></i>
                <span>Log Hours</span>
            </a>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">
                <i class="fa-solid fa-chart-line"></i>
                <span>Analytics</span>
            </a>
        </div>
    </div>

    <!-- Live Stopwatch Widget -->
    <div class="timer-widget">
        <div class="timer-display-box">
            <span class="timer-label"><i class="fa-solid fa-stopwatch"></i> Active Work Session Timer</span>
            <div id="timer-digits" class="timer-digits">00:00:00</div>
        </div>
        <div class="timer-controls">
            <button type="button" id="btn-timer-start" class="btn btn-timer btn-timer-start">
                <i class="fa-solid fa-play"></i> Start Timer
            </button>
            <button type="button" id="btn-timer-pause" class="btn btn-timer" style="display: none;">
                <i class="fa-solid fa-pause"></i> Pause
            </button>
            <button type="button" id="btn-timer-reset" class="btn btn-timer">
                <i class="fa-solid fa-rotate-left"></i> Reset
            </button>
            <button type="button" id="btn-timer-log" class="btn btn-primary">
                <i class="fa-solid fa-check"></i> Log Current Session
            </button>
        </div>
    </div>

    <!-- KPI Grid -->
    <div class="kpi-grid">
        <div class="kpi-card">
            <div class="kpi-icon indigo">
                <i class="fa-solid fa-hourglass-half"></i>
            </div>
            <div class="kpi-meta" style="flex: 1;">
                <span class="kpi-label">Today's Hours</span>
                <span class="kpi-value"><c:out value="${todayHours}"/> hrs</span>
                <div class="progress-bar-container">
                    <div class="progress-bar-fill" style="width: ${progressPercent}%;"></div>
                </div>
                <span class="kpi-sub">${progressPercent}% of ${dailyTargetHours}h daily goal</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon emerald">
                <i class="fa-solid fa-calendar-check"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">7-Day Total</span>
                <span class="kpi-value"><c:out value="${summary.totalHours}"/> hrs</span>
                <span class="kpi-sub">Across <c:out value="${summary.uniqueActiveDays}"/> active days</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon blue">
                <i class="fa-solid fa-gauge-high"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Avg. Daily Output</span>
                <span class="kpi-value"><c:out value="${summary.averageHoursPerDay}"/> hrs</span>
                <span class="kpi-sub">Over active logged days</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon amber">
                <i class="fa-solid fa-fire"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Top Focus Task</span>
                <span class="kpi-value" style="font-size: 1.25rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 170px;">
                    <c:out value="${summary.topTask}"/>
                </span>
                <span class="kpi-sub"><c:out value="${summary.topTaskHours}"/> hrs dedicated</span>
            </div>
        </div>
    </div>

    <!-- Charts Section -->
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(360px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">
        <div class="card" style="margin-bottom: 0;">
            <div class="card-header">
                <h2 class="card-title"><i class="fa-solid fa-chart-column" style="color: var(--primary);"></i> Daily Hours (Past 7 Days)</h2>
            </div>
            <div class="card-body">
                <div style="height: 250px;">
                    <canvas id="userDailyChart"></canvas>
                </div>
            </div>
        </div>

        <div class="card" style="margin-bottom: 0;">
            <div class="card-header">
                <h2 class="card-title"><i class="fa-solid fa-chart-pie" style="color: var(--secondary);"></i> Task Distribution</h2>
            </div>
            <div class="card-body">
                <div style="height: 250px;">
                    <canvas id="userTaskChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Logs Table -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                <i class="fa-solid fa-list-check" style="color: var(--primary);"></i>
                Recent Time Entries
            </h2>
            <a href="${pageContext.request.contextPath}/timelog" class="btn btn-secondary btn-sm">
                View All Logs <i class="fa-solid fa-arrow-right"></i>
            </a>
        </div>
        <div class="table-responsive">
            <table class="table">
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Time Interval</th>
                    <th>Task Description</th>
                    <th>Duration</th>
                    <th style="text-align: right;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty recentLogs}">
                        <c:forEach var="log" items="${recentLogs}">
                            <tr>
                                <td>
                                    <strong><c:out value="${log.logDate}"/></strong>
                                </td>
                                <td style="color: var(--text-muted); font-size: 0.85rem;">
                                    <i class="fa-regular fa-clock"></i>
                                    <c:out value="${log.startTime}"/> - <c:out value="${log.endTime}"/>
                                </td>
                                <td>
                                    <span style="font-weight: 600;"><c:out value="${log.task}"/></span>
                                </td>
                                <td>
                                    <span class="badge badge-duration">
                                        <i class="fa-solid fa-stopwatch"></i> <c:out value="${log.duration}"/> hrs
                                    </span>
                                </td>
                                <td style="text-align: right;">
                                    <a href="${pageContext.request.contextPath}/timelog/edit?id=${log.id}"
                                       class="btn btn-secondary btn-sm" title="Edit">
                                        <i class="fa-solid fa-pen-to-square"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/timelog/delete?id=${log.id}"
                                       class="btn btn-secondary btn-sm"
                                       onclick="return confirmDelete('Are you sure you want to delete this time entry?');"
                                       title="Delete" style="color: var(--danger);">
                                        <i class="fa-solid fa-trash-can"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="5" style="text-align: center; padding: 3rem 1rem; color: var(--text-muted);">
                                <i class="fa-solid fa-clock-rotate-left" style="font-size: 2.5rem; color: var(--border); margin-bottom: 0.75rem; display: block;"></i>
                                No time logs found yet. Start the stopwatch or click <strong>Log Hours</strong> to record your first session!
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const dailyData = ${dailyHoursJson != null ? dailyHoursJson : '{}'};
        const taskData = ${taskHoursJson != null ? taskHoursJson : '{}'};

        renderDailyTrendChart('userDailyChart', dailyData);
        renderTaskDistributionChart('userTaskChart', taskData);
    });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
