<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Admin Command Center" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <!-- Welcome & Admin Actions -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
        <div>
            <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.25rem;">
                <span class="badge badge-admin"><i class="fa-solid fa-shield-halved"></i> Administrator Mode</span>
            </div>
            <h1 style="font-size: 1.6rem; font-weight: 800; color: var(--text-main);">
                System Command Center
            </h1>
            <p style="color: var(--text-muted); font-size: 0.9rem;">
                Monitor organization work hours, manage user accounts & analyze productivity trends.
            </p>
        </div>
        <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
            <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">
                <i class="fa-solid fa-users"></i>
                <span>Manage Users</span>
            </a>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">
                <i class="fa-solid fa-chart-line"></i>
                <span>Full Reports</span>
            </a>
            <a href="${pageContext.request.contextPath}/timelog/new" class="btn btn-primary">
                <i class="fa-solid fa-plus"></i>
                <span>Add Time Log</span>
            </a>
        </div>
    </div>

    <!-- Admin KPI Grid -->
    <div class="kpi-grid">
        <div class="kpi-card">
            <div class="kpi-icon indigo">
                <i class="fa-solid fa-users"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Active Users</span>
                <span class="kpi-value"><c:out value="${totalUsers}"/></span>
                <span class="kpi-sub">Registered accounts</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon emerald">
                <i class="fa-solid fa-business-time"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Total Logged (30d)</span>
                <span class="kpi-value"><c:out value="${totalHoursAllTime}"/> hrs</span>
                <span class="kpi-sub"><c:out value="${totalEntriesAllTime}"/> total time entries</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon blue">
                <i class="fa-solid fa-clock"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">7-Day Team Total</span>
                <span class="kpi-value"><c:out value="${summary.totalHours}"/> hrs</span>
                <span class="kpi-sub">Across <c:out value="${summary.uniqueActiveDays}"/> active days</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon amber">
                <i class="fa-solid fa-award"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Top Organization Task</span>
                <span class="kpi-value" style="font-size: 1.25rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 170px;">
                    <c:out value="${summary.topTask}"/>
                </span>
                <span class="kpi-sub"><c:out value="${summary.topTaskHours}"/> hrs logged</span>
            </div>
        </div>
    </div>

    <!-- Admin Charts Grid -->
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(360px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">
        <div class="card" style="margin-bottom: 0;">
            <div class="card-header">
                <h2 class="card-title"><i class="fa-solid fa-chart-column" style="color: var(--primary);"></i> Team Hours (Past 7 Days)</h2>
            </div>
            <div class="card-body">
                <div style="height: 250px;">
                    <canvas id="adminDailyChart"></canvas>
                </div>
            </div>
        </div>

        <div class="card" style="margin-bottom: 0;">
            <div class="card-header">
                <h2 class="card-title"><i class="fa-solid fa-chart-pie" style="color: var(--secondary);"></i> Task Distribution</h2>
            </div>
            <div class="card-body">
                <div style="height: 250px;">
                    <canvas id="adminTaskChart"></canvas>
                </div>
            </div>
        </div>

        <div class="card" style="margin-bottom: 0;">
            <div class="card-header">
                <h2 class="card-title"><i class="fa-solid fa-user-group" style="color: #06b6d4;"></i> User Contributions</h2>
            </div>
            <div class="card-body">
                <div style="height: 250px;">
                    <canvas id="adminUserChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Team Logs Table -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                <i class="fa-solid fa-list-check" style="color: var(--primary);"></i>
                Latest Organization Time Logs
            </h2>
            <a href="${pageContext.request.contextPath}/timelog" class="btn btn-secondary btn-sm">
                View All Logs <i class="fa-solid fa-arrow-right"></i>
            </a>
        </div>
        <div class="table-responsive">
            <table class="table">
                <thead>
                <tr>
                    <th>User</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Task</th>
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
                                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                                        <div class="user-avatar" style="width: 26px; height: 26px; font-size: 0.75rem;">
                                            <c:out value="${log.userName.substring(0, 1).toUpperCase()}"/>
                                        </div>
                                        <span style="font-weight: 600;"><c:out value="${log.userName}"/></span>
                                    </div>
                                </td>
                                <td><c:out value="${log.logDate}"/></td>
                                <td style="color: var(--text-muted); font-size: 0.85rem;">
                                    <c:out value="${log.startTime}"/> - <c:out value="${log.endTime}"/>
                                </td>
                                <td><c:out value="${log.task}"/></td>
                                <td>
                                    <span class="badge badge-duration">
                                        <c:out value="${log.duration}"/> hrs
                                    </span>
                                </td>
                                <td style="text-align: right;">
                                    <a href="${pageContext.request.contextPath}/timelog/edit?id=${log.id}"
                                       class="btn btn-secondary btn-sm" title="Edit Log">
                                        <i class="fa-solid fa-pen-to-square"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/timelog/delete?id=${log.id}"
                                       class="btn btn-secondary btn-sm"
                                       onclick="return confirmDelete('Are you sure you want to delete this log?');"
                                       title="Delete Log" style="color: var(--danger);">
                                        <i class="fa-solid fa-trash-can"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-muted);">
                                No time logs registered yet.
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
        const userData = ${userHoursJson != null ? userHoursJson : '{}'};

        renderDailyTrendChart('adminDailyChart', dailyData);
        renderTaskDistributionChart('adminTaskChart', taskData);
        renderUserHoursChart('adminUserChart', userData);
    });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
