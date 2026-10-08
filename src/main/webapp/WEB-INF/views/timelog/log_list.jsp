<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Time Logs" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <!-- Header & Action -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
        <div>
            <h1 style="font-size: 1.6rem; font-weight: 800; color: var(--text-main);">
                Time Log Entries
            </h1>
            <p style="color: var(--text-muted); font-size: 0.9rem;">
                <c:choose>
                    <c:when test="${sessionScope.currentUser.admin}">
                        Oversee and manage work hour records across the entire organization.
                    </c:when>
                    <c:otherwise>
                        Manage your recorded work sessions, view durations, and track completed tasks.
                    </c:otherwise>
                </c:choose>
            </p>
        </div>
        <div style="display: flex; gap: 0.75rem;">
            <a href="${pageContext.request.contextPath}/timelog/new" class="btn btn-primary">
                <i class="fa-solid fa-plus"></i>
                <span>Add Time Log</span>
            </a>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">
                <i class="fa-solid fa-file-export"></i>
                <span>Export / Report</span>
            </a>
        </div>
    </div>

    <!-- Filter Bar -->
    <form action="${pageContext.request.contextPath}/timelog" method="get" class="filter-bar">
        <div class="filter-item">
            <label class="form-label" style="font-size: 0.8rem;">Task Keyword</label>
            <input type="text" name="keyword" class="form-control" placeholder="Search tasks..."
                   value="<c:out value="${keyword}"/>">
        </div>

        <c:if test="${sessionScope.currentUser.admin}">
            <div class="filter-item">
                <label class="form-label" style="font-size: 0.8rem;">Filter User</label>
                <select name="userId" class="form-control">
                    <option value="">-- All Users --</option>
                    <c:forEach var="u" items="${userList}">
                        <option value="${u.id}" ${selectedUserId == u.id ? 'selected' : ''}>
                            <c:out value="${u.name}"/> (${u.role})
                        </option>
                    </c:forEach>
                </select>
            </div>
        </c:if>

        <div class="filter-item" style="max-width: 170px;">
            <label class="form-label" style="font-size: 0.8rem;">From Date</label>
            <input type="date" name="startDate" class="form-control" value="<c:out value="${startDate}"/>">
        </div>

        <div class="filter-item" style="max-width: 170px;">
            <label class="form-label" style="font-size: 0.8rem;">To Date</label>
            <input type="date" name="endDate" class="form-control" value="<c:out value="${endDate}"/>">
        </div>

        <div class="filter-actions">
            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-filter"></i> Filter
            </button>
            <a href="${pageContext.request.contextPath}/timelog" class="btn btn-secondary" title="Reset Filters">
                <i class="fa-solid fa-arrow-rotate-right"></i>
            </a>
        </div>
    </form>

    <!-- Table Card -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                <i class="fa-solid fa-clock-rotate-left" style="color: var(--primary);"></i>
                Logged Sessions (<c:out value="${logs.size()}"/> entries)
            </h2>
            <div style="font-size: 0.9rem; font-weight: 700; color: var(--primary);">
                Total Filtered Duration: <span class="badge badge-duration" style="font-size: 0.9rem;"><c:out value="${totalViewHours}"/> hrs</span>
            </div>
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
                    <th style="text-align: right;">Actions</th>
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
                                            <div class="user-avatar" style="width: 26px; height: 26px; font-size: 0.75rem;">
                                                <c:out value="${log.userName.substring(0, 1).toUpperCase()}"/>
                                            </div>
                                            <span style="font-weight: 600;"><c:out value="${log.userName}"/></span>
                                        </div>
                                    </td>
                                </c:if>
                                <td>
                                    <strong><c:out value="${log.logDate}"/></strong>
                                </td>
                                <td><c:out value="${log.startTime}"/></td>
                                <td><c:out value="${log.endTime}"/></td>
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
                                       onclick="return confirmDelete('Delete this time entry?');"
                                       title="Delete" style="color: var(--danger);">
                                        <i class="fa-solid fa-trash-can"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="${sessionScope.currentUser.admin ? 7 : 6}" style="text-align: center; padding: 3rem 1rem; color: var(--text-muted);">
                                <i class="fa-solid fa-magnifying-glass" style="font-size: 2.5rem; color: var(--border); margin-bottom: 0.75rem; display: block;"></i>
                                No matching time logs found. Try adjusting your filters or record a new log.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
