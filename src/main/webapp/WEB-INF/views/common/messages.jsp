<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Flash Success Message --%>
<c:if test="${not empty sessionScope.flashSuccess}">
    <div class="alert alert-success">
        <i class="fa-solid fa-circle-check"></i>
        <span><c:out value="${sessionScope.flashSuccess}"/></span>
    </div>
    <c:remove var="flashSuccess" scope="session"/>
</c:if>

<%-- Flash Error Message --%>
<c:if test="${not empty sessionScope.flashError}">
    <div class="alert alert-danger">
        <i class="fa-solid fa-triangle-exclamation"></i>
        <span><c:out value="${sessionScope.flashError}"/></span>
    </div>
    <c:remove var="flashError" scope="session"/>
</c:if>

<%-- Request-level Error Message --%>
<c:if test="${not empty requestScope.errorMessage}">
    <div class="alert alert-danger">
        <i class="fa-solid fa-circle-exclamation"></i>
        <span><c:out value="${requestScope.errorMessage}"/></span>
    </div>
</c:if>

<%-- Request-level Success Message --%>
<c:if test="${not empty requestScope.successMessage}">
    <div class="alert alert-success">
        <i class="fa-solid fa-circle-check"></i>
        <span><c:out value="${requestScope.successMessage}"/></span>
    </div>
</c:if>
