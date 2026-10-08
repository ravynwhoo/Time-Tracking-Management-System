<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="top-navbar">
    <a href="${pageContext.request.contextPath}/dashboard" class="nav-brand">
        <div class="nav-brand-icon">
            <i class="fa-solid fa-stopwatch-20"></i>
        </div>
        <span>TimeTrack</span>
    </a>

    <c:if test="${not empty sessionScope.currentUser}">
        <ul class="nav-links">
            <li>
                <a href="${pageContext.request.contextPath}/dashboard"
                   class="nav-link ${pageContext.request.requestURI.contains('/dashboard') ? 'active' : ''}">
                    <i class="fa-solid fa-chart-pie"></i>
                    <span>Dashboard</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/timelog"
                   class="nav-link ${pageContext.request.requestURI.contains('/timelog') ? 'active' : ''}">
                    <i class="fa-solid fa-clock-rotate-left"></i>
                    <span>Time Logs</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/reports"
                   class="nav-link ${pageContext.request.requestURI.contains('/reports') ? 'active' : ''}">
                    <i class="fa-solid fa-chart-line"></i>
                    <span>Reports</span>
                </a>
            </li>

            <!-- Admin Only Navigation Links -->
            <c:if test="${sessionScope.currentUser.admin}">
                <li>
                    <a href="${pageContext.request.contextPath}/users"
                       class="nav-link ${pageContext.request.requestURI.contains('/users') ? 'active' : ''}">
                        <i class="fa-solid fa-users-gear"></i>
                        <span>Users</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/settings"
                       class="nav-link ${pageContext.request.requestURI.contains('/settings') ? 'active' : ''}">
                        <i class="fa-solid fa-sliders"></i>
                        <span>Settings</span>
                    </a>
                </li>
            </c:if>
        </ul>

        <div class="nav-user">
            <div class="user-badge">
                <div class="user-avatar">
                    <c:out value="${sessionScope.currentUser.name.substring(0, 1).toUpperCase()}"/>
                </div>
                <div class="user-info">
                    <span class="user-name"><c:out value="${sessionScope.currentUser.name}"/></span>
                    <span class="user-role"><c:out value="${sessionScope.currentUser.role}"/></span>
                </div>
            </div>

            <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm" title="Log Out">
                <i class="fa-solid fa-right-from-bracket"></i>
                <span>Logout</span>
            </a>
        </div>
    </c:if>
</nav>
