<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="${action} User" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body" style="max-width: 650px;">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <div class="card">
        <div class="card-header">
            <h1 class="card-title">
                <i class="fa-solid fa-user-gear" style="color: var(--primary);"></i>
                <span>${action} User Account</span>
            </h1>
            <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary btn-sm">
                <i class="fa-solid fa-arrow-left"></i> Back to Users
            </a>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/users/save" method="post">
                <input type="hidden" name="id" value="${user.id}">

                <div class="form-group">
                    <label class="form-label" for="name">Full Name *</label>
                    <input type="text" name="name" id="name" class="form-control"
                           placeholder="e.g. Robert Martin" value="<c:out value="${user.name}"/>" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="email">Email Address *</label>
                    <input type="email" name="email" id="email" class="form-control"
                           placeholder="e.g. robert@company.com" value="<c:out value="${user.email}"/>" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="role">System Role *</label>
                    <select name="role" id="role" class="form-control" required>
                        <option value="user" ${user.role == 'user' ? 'selected' : ''}>Standard User (Log hours & view reports)</option>
                        <option value="admin" ${user.role == 'admin' ? 'selected' : ''}>Administrator (Full system control)</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">
                        Password ${action == 'Edit' ? '(Leave blank to keep existing password)' : '*'}
                    </label>
                    <input type="password" name="password" id="password" class="form-control"
                           placeholder="${action == 'Edit' ? '••••••••' : 'Minimum 6 characters'}"
                           ${action == 'Create' ? 'required' : ''}>
                </div>

                <div style="display: flex; gap: 0.75rem; margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;">
                        <i class="fa-solid fa-check"></i>
                        <span>Save User</span>
                    </button>
                    <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">
                        Cancel
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
