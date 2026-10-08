<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="User Management" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
        <div>
            <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.25rem;">
                <span class="badge badge-admin"><i class="fa-solid fa-users-gear"></i> Administration</span>
            </div>
            <h1 style="font-size: 1.6rem; font-weight: 800; color: var(--text-main);">
                User Management
            </h1>
            <p style="color: var(--text-muted); font-size: 0.9rem;">
                Create, edit, or delete user accounts and manage permissions & roles.
            </p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/users/new" class="btn btn-primary">
                <i class="fa-solid fa-user-plus"></i>
                <span>Add New User</span>
            </a>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                <i class="fa-solid fa-users" style="color: var(--primary);"></i>
                Registered Accounts (<c:out value="${users.size()}"/> users)
            </h2>
        </div>
        <div class="table-responsive">
            <table class="table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email Address</th>
                    <th>Role</th>
                    <th>Registration Date</th>
                    <th style="text-align: right;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td style="color: var(--text-muted);">#<c:out value="${u.id}"/></td>
                        <td>
                            <div style="display: flex; align-items: center; gap: 0.6rem;">
                                <div class="user-avatar" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                    <c:out value="${u.name.substring(0, 1).toUpperCase()}"/>
                                </div>
                                <span style="font-weight: 700;"><c:out value="${u.name}"/></span>
                            </div>
                        </td>
                        <td><c:out value="${u.email}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${u.admin}">
                                    <span class="badge badge-admin"><i class="fa-solid fa-shield"></i> Admin</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-user"><i class="fa-solid fa-user"></i> Standard User</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td style="color: var(--text-muted); font-size: 0.85rem;">
                            <c:out value="${u.createdAt}"/>
                        </td>
                        <td style="text-align: right;">
                            <a href="${pageContext.request.contextPath}/users/edit?id=${u.id}"
                               class="btn btn-secondary btn-sm" title="Edit User">
                                <i class="fa-solid fa-pen-to-square"></i>
                            </a>
                            <c:if test="${sessionScope.currentUser.id != u.id}">
                                <a href="${pageContext.request.contextPath}/users/delete?id=${u.id}"
                                   class="btn btn-secondary btn-sm"
                                   onclick="return confirmDelete('Are you sure you want to delete user \'${u.name}\'? All associated logs will also be deleted.');"
                                   title="Delete User" style="color: var(--danger);">
                                    <i class="fa-solid fa-trash-can"></i>
                                </a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
