<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Access Forbidden" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body" style="text-align: center; padding: 5rem 1rem;">
    <div style="font-size: 5rem; font-weight: 800; color: var(--danger); line-height: 1;">403</div>
    <h1 style="font-size: 1.75rem; font-weight: 700; margin: 1rem 0;">Access Denied</h1>
    <p style="color: var(--text-muted); max-width: 480px; margin: 0 auto 2rem;">
        You do not possess the required administrator credentials to view this protected resource.
    </p>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
        <i class="fa-solid fa-house"></i> Return to Dashboard
    </a>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
