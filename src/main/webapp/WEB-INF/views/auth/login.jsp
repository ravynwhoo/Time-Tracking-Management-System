<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - TimeTrack</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-page">

<div class="auth-card">
    <div class="auth-header">
        <div class="auth-logo">
            <i class="fa-solid fa-stopwatch-20"></i>
        </div>
        <h1 class="auth-title">Welcome to TimeTrack</h1>
        <p class="auth-subtitle">Log work hours, track productivity & generate reports</p>
    </div>

    <%-- Success/Error Messages --%>
    <c:if test="${param.msg == 'registered_success'}">
        <div class="alert alert-success">
            <i class="fa-solid fa-circle-check"></i>
            <span>Account created successfully! Please sign in.</span>
        </div>
    </c:if>
    <c:if test="${param.msg == 'logged_out'}">
        <div class="alert alert-success">
            <i class="fa-solid fa-circle-check"></i>
            <span>You have been safely logged out.</span>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">
            <i class="fa-solid fa-triangle-exclamation"></i>
            <span><c:out value="${errorMessage}"/></span>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label class="form-label" for="email">Email Address</label>
            <div style="position: relative;">
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="name@company.com" value="${email}" required autofocus>
            </div>
        </div>

        <div class="form-group">
            <label class="form-label" for="password">Password</label>
            <input type="password" id="password" name="password" class="form-control"
                   placeholder="Enter your password" required>
        </div>

        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem;">
            <i class="fa-solid fa-arrow-right-to-bracket"></i>
            <span>Sign In</span>
        </button>
    </form>

    <!-- Quick Demo Logins Helper -->
    <div style="margin-top: 1.5rem; padding: 1rem; background: #f8fafc; border-radius: var(--radius-sm); border: 1px dashed var(--border); font-size: 0.8rem;">
        <div style="font-weight: 700; color: var(--text-muted); margin-bottom: 0.5rem; display: flex; align-items: center; gap: 0.4rem;">
            <i class="fa-solid fa-wand-magic-sparkles" style="color: var(--primary);"></i>
            <span>Demo Test Credentials:</span>
        </div>
        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
            <button type="button" class="btn btn-secondary btn-sm"
                    onclick="fillCreds('admin@timetrack.com', 'admin123')">
                <i class="fa-solid fa-shield-halved"></i> Admin
            </button>
            <button type="button" class="btn btn-secondary btn-sm"
                    onclick="fillCreds('alice@company.com', 'password123')">
                <i class="fa-solid fa-user"></i> Alice (User)
            </button>
            <button type="button" class="btn btn-secondary btn-sm"
                    onclick="fillCreds('bob@company.com', 'password123')">
                <i class="fa-solid fa-user"></i> Bob (User)
            </button>
        </div>
    </div>

    <div class="auth-footer">
        Don't have an account?
        <a href="${pageContext.request.contextPath}/register" style="font-weight: 600;">Sign up</a>
    </div>
</div>

<script>
    function fillCreds(email, password) {
        document.getElementById('email').value = email;
        document.getElementById('password').value = password;
    }
</script>
</body>
</html>
