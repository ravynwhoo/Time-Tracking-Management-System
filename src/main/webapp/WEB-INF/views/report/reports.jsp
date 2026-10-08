<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Productivity Reports" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <!-- Header & Action -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
        <div>
            <h1 style="font-size: 1.6rem; font-weight: 800; color: var(--text-main);">
                Productivity & Time Reports
            </h1>
            <p style="color: var(--text-muted); font-size: 0.9rem;">
                Analyze trends, calculate total work duration, and export verified records.
            </p>
        </div>
        <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
            <a href="${pageContext.request.contextPath}/reports/export?startDate=${startDate}&endDate=${endDate}&userId=${selectedUserId}"
               class="btn btn-primary" title="Download CSV">
                <i class="fa-solid fa-file-csv"></i>
                <span>Export CSV</span>
            </a>
            <button type="button" onclick="window.print()" class="btn btn-secondary" title="Print Report">
                <i class="fa-solid fa-print"></i>
                <span>Print</span>
            </button>
        </div>
    </div>

    <!-- Filter Bar -->
    <form action="${pageContext.request.contextPath}/reports" method="get" class="filter-bar">
        <c:if test="${sessionScope.currentUser.admin}">
            <div class="filter-item">
                <label class="form-label" style="font-size: 0.8rem;">Select User</label>
                <select name="userId" class="form-control">
                    <option value="">-- All Organization Users --</option>
                    <c:forEach var="u" items="${userList}">
                        <option value="${u.id}" ${selectedUserId == u.id ? 'selected' : ''}>
                            <c:out value="${u.name}"/> (${u.role})
                        </option>
                    </c:forEach>
                </select>
            </div>
        </c:if>

        <div class="filter-item">
            <label class="form-label" style="font-size: 0.8rem;">Start Date</label>
            <input type="date" name="startDate" class="form-control" value="<c:out value="${startDate}"/>">
        </div>

        <div class="filter-item">
            <label class="form-label" style="font-size: 0.8rem;">End Date</label>
            <input type="date" name="endDate" class="form-control" value="<c:out value="${endDate}"/>">
        </div>

        <div class="filter-actions">
            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-chart-simple"></i> Generate Report
            </button>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">
                <i class="fa-solid fa-arrow-rotate-right"></i> Reset
            </a>
        </div>
    </form>

    <!-- KPI Summary Grid -->
    <div class="kpi-grid">
        <div class="kpi-card">
            <div class="kpi-icon indigo">
                <i class="fa-solid fa-clock"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Total Duration</span>
                <span class="kpi-value"><c:out value="${summary.totalHours}"/> hrs</span>
                <span class="kpi-sub"><c:out value="${summary.totalEntries}"/> recorded sessions</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon emerald">
                <i class="fa-solid fa-calendar-day"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Active Work Days</span>
                <span class="kpi-value"><c:out value="${summary.uniqueActiveDays}"/></span>
                <span class="kpi-sub">Within selected period</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon blue">
                <i class="fa-solid fa-gauge"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Average Output</span>
                <span class="kpi-value"><c:out value="${summary.averageHoursPerDay}"/> hrs</span>
                <span class="kpi-sub">Per active working day</span>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon amber">
                <i class="fa-solid fa-star"></i>
            </div>
            <div class="kpi-meta">
                <span class="kpi-label">Top Category</span>
                <span class="kpi-value" style="font-size: 1.25rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 170px;">
                    <c:out value="${summary.topTask}"/>
                </span>
                <span class="kpi-sub"><c:out value="${summary.topTaskHours}"/> hrs logged</span>
            </div>
        </div>
    </div>

    <!-- Charts Section -->
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(360px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">
        <div class="card" style="margin-bottom: 0;">
            <div class="card-header">
                <h2 class="card-title"><i class="fa-solid fa-chart-column" style="color: var(--primary);"></i> Daily Hours Timeline</h2>
            </div>
            <div class="card-body">
                <div style="height: 260px;">
                    <canvas id="reportDailyChart"></canvas>
                </div>
            </div>
        </div>

        <div class="card" style="margin-bottom: 0;">
            <div class="card-header">
                <h2 class="card-title"><i class="fa-solid fa-chart-pie" style="color: var(--secondary);"></i> Task Allocation Breakdown</h2>
            </div>
            <div class="card-body">
                <div style="height: 260px;">
                    <canvas id="reportTaskChart"></canvas>
                </div>
            </div>
        </div>

        <c:if test="${sessionScope.currentUser.admin && userHoursJson != null}">
            <div class="card" style="margin-bottom: 0;">
                <div class="card-header">
                    <h2 class="card-title"><i class="fa-solid fa-users" style="color: #06b6d4;"></i> Team Member Hours</h2>
                </div>
                <div class="card-body">
                    <div style="height: 260px;">
                        <canvas id="reportUserChart"></canvas>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <!-- Detailed Time Log Records in Report -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                <i class="fa-solid fa-table-list" style="color: var(--primary);"></i>
                Detailed Log Entries for Period (<c:out value="${logs.size()}"/> entries)
            </h2>
        </div>
        <div class="table-responsive">
            <table class="table">
                <thead>
                <tr>
                    <c:if test="${sessionScope.currentUser.admin}">
                        <th>User</th>
                    </c:if>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Task Description</th>
                    <th>Duration</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty logs}">
                        <c:forEach var="log" items="${logs}">
                            <tr>
                                <c:if test="${sessionScope.currentUser.admin}">
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 0.5rem;">
                                            <div class="user-avatar" style="width: 24px; height: 24px; font-size: 0.7rem;">
                                                <c:out value="${log.userName.substring(0, 1).toUpperCase()}"/>
                                            </div>
                                            <strong><c:out value="${log.userName}"/></strong>
                                        </div>
                                    </td>
                                </c:if>
                                <td><c:out value="${log.logDate}"/></td>
                                <td><c:out value="${log.startTime}"/></td>
                                <td><c:out value="${log.endTime}"/></td>
                                <td><c:out value="${log.task}"/></td>
                                <td>
                                    <span class="badge badge-duration">
                                        <c:out value="${log.duration}"/> hrs
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="${sessionScope.currentUser.admin ? 6 : 5}" style="text-align: center; padding: 2rem; color: var(--text-muted);">
                                No records found for the selected time range.
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

        renderDailyTrendChart('reportDailyChart', dailyData);
        renderTaskDistributionChart('reportTaskChart', taskData);

        <c:if test="${sessionScope.currentUser.admin && userHoursJson != null}">
        const userData = ${userHoursJson != null ? userHoursJson : '{}'};
        renderUserHoursChart('reportUserChart', userData);
        </c:if>
    });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
