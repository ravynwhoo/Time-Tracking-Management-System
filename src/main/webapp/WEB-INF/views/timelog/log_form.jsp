<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="${action} Time Log" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body" style="max-width: 720px;">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <div class="card">
        <div class="card-header">
            <h1 class="card-title">
                <i class="fa-solid fa-clock" style="color: var(--primary);"></i>
                <span>${action} Time Log Entry</span>
            </h1>
            <a href="${pageContext.request.contextPath}/timelog" class="btn btn-secondary btn-sm">
                <i class="fa-solid fa-arrow-left"></i> Back to Logs
            </a>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/timelog/save" method="post" id="timeLogForm">
                <input type="hidden" name="id" value="${timeLog.id}">

                <%-- Admin can assign logs to any user --%>
                <c:if test="${sessionScope.currentUser.admin}">
                    <div class="form-group">
                        <label class="form-label" for="userId">Assign To User *</label>
                        <select name="userId" id="userId" class="form-control" required>
                            <c:forEach var="u" items="${userList}">
                                <option value="${u.id}" ${timeLog.userId == u.id ? 'selected' : ''}>
                                    <c:out value="${u.name}"/> (<c:out value="${u.email}"/>)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>

                <div class="form-group">
                    <label class="form-label" for="logDate">Date *</label>
                    <input type="date" name="logDate" id="logDate" class="form-control"
                           value="${param.date != null ? param.date : (timeLog.logDate != null ? timeLog.logDate : '')}" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="startTime">Start Time (HH:mm) *</label>
                        <input type="time" name="startTime" id="startTime" class="form-control"
                               value="${param.start != null ? param.start : (timeLog.startTime != null ? timeLog.startTime : '09:00')}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="endTime">End Time (HH:mm) *</label>
                        <input type="time" name="endTime" id="endTime" class="form-control"
                               value="${param.end != null ? param.end : (timeLog.endTime != null ? timeLog.endTime : '17:00')}" required>
                    </div>
                </div>

                <!-- Live Duration Calculator Indicator -->
                <div id="durationPreviewBox" style="background: #f1f5f9; border-radius: var(--radius-sm); padding: 0.75rem 1rem; margin-bottom: 1.25rem; display: flex; align-items: center; justify-content: space-between;">
                    <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">
                        <i class="fa-solid fa-calculator" style="color: var(--primary);"></i> Calculated Duration:
                    </span>
                    <span id="durationCalculatedText" class="badge badge-duration" style="font-size: 0.95rem;">
                        0.00 hrs
                    </span>
                </div>

                <div class="form-group">
                    <label class="form-label" for="task">Task / Activity Description *</label>
                    <input type="text" name="task" id="task" class="form-control"
                           placeholder="e.g. Backend Development, Code Review, Client Meeting"
                           value="<c:out value="${timeLog.task}"/>" maxlength="255" required>
                    
                    <!-- Quick Preset Task Chips -->
                    <div style="margin-top: 0.6rem; display: flex; gap: 0.4rem; flex-wrap: wrap;">
                        <span style="font-size: 0.75rem; color: var(--text-muted); align-self: center; margin-right: 0.2rem;">Quick picks:</span>
                        <button type="button" class="btn btn-secondary btn-sm" style="font-size: 0.75rem; padding: 0.2rem 0.5rem;" onclick="setTask('Frontend Development')">Frontend</button>
                        <button type="button" class="btn btn-secondary btn-sm" style="font-size: 0.75rem; padding: 0.2rem 0.5rem;" onclick="setTask('Backend Development')">Backend</button>
                        <button type="button" class="btn btn-secondary btn-sm" style="font-size: 0.75rem; padding: 0.2rem 0.5rem;" onclick="setTask('Bug Fixing')">Bug Fixing</button>
                        <button type="button" class="btn btn-secondary btn-sm" style="font-size: 0.75rem; padding: 0.2rem 0.5rem;" onclick="setTask('Code Review')">Code Review</button>
                        <button type="button" class="btn btn-secondary btn-sm" style="font-size: 0.75rem; padding: 0.2rem 0.5rem;" onclick="setTask('Team Meeting')">Team Meeting</button>
                        <button type="button" class="btn btn-secondary btn-sm" style="font-size: 0.75rem; padding: 0.2rem 0.5rem;" onclick="setTask('Testing & QA')">Testing</button>
                    </div>
                </div>

                <div style="display: flex; gap: 0.75rem; margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;">
                        <i class="fa-solid fa-floppy-disk"></i>
                        <span>Save Time Log</span>
                    </button>
                    <a href="${pageContext.request.contextPath}/timelog" class="btn btn-secondary">
                        Cancel
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function setTask(taskName) {
        document.getElementById('task').value = taskName;
    }

    function recalculateDuration() {
        const startVal = document.getElementById('startTime').value;
        const endVal = document.getElementById('endTime').value;
        const display = document.getElementById('durationCalculatedText');

        if (!startVal || !endVal) {
            display.textContent = '0.00 hrs';
            return;
        }

        const [startH, startM] = startVal.split(':').map(Number);
        const [endH, endM] = endVal.split(':').map(Number);

        let startMinutes = startH * 60 + startM;
        let endMinutes = endH * 60 + endM;

        let diff = endMinutes - startMinutes;
        if (diff < 0) {
            diff += 24 * 60;
        }

        const hours = (diff / 60).toFixed(2);
        display.textContent = hours + ' hrs';
    }

    document.getElementById('startTime').addEventListener('change', recalculateDuration);
    document.getElementById('endTime').addEventListener('change', recalculateDuration);

    // Initial calculation on load
    document.addEventListener('DOMContentLoaded', function() {
        recalculateDuration();
    });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
