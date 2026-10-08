<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Internal Server Error" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body" style="text-align: center; padding: 5rem 1rem;">
    <div style="font-size: 5rem; font-weight: 800; color: #f59e0b; line-height: 1;">500</div>
    <h1 style="font-size: 1.75rem; font-weight: 700; margin: 1rem 0;">Server Error</h1>
    <p style="color: var(--text-muted); max-width: 520px; margin: 0 auto 2rem;">
        An unexpected server error occurred while processing your request.
        <c:if test="${not empty errorMessage}">
            <br><br><code style="background: #f1f5f9; padding: 0.5rem 1rem; border-radius: 4px; display: inline-block;"><c:out value="${errorMessage}"/></code>
        </c:if>
    </p>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
        <i class="fa-solid fa-house"></i> Return to Dashboard
    </a>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
