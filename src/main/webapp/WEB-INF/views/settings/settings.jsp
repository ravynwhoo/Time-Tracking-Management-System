<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="System Settings" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="content-body" style="max-width: 760px;">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <div class="card">
        <div class="card-header">
            <h1 class="card-title">
                <i class="fa-solid fa-sliders" style="color: var(--primary);"></i>
                <span>System Configuration Settings</span>
            </h1>
        </div>
        <div class="card-body">
            <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;">
                Configure organization-wide work parameters, default shift timings, and productivity tracking goals.
            </p>

            <form action="${pageContext.request.contextPath}/settings" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="work_hours_per_day">Daily Target Work Hours</label>
                        <input type="number" step="0.5" min="1" max="24" id="work_hours_per_day"
                               name="work_hours_per_day" class="form-control"
                               value="${settings['work_hours_per_day'] != null ? settings['work_hours_per_day'] : '8'}" required>
                        <small style="color: var(--text-muted);">Standard work hours expected per employee daily.</small>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="work_days_per_week">Work Days Per Week</label>
                        <input type="number" min="1" max="7" id="work_days_per_week"
                               name="work_days_per_week" class="form-control"
                               value="${settings['work_days_per_week'] != null ? settings['work_days_per_week'] : '5'}" required>
                        <small style="color: var(--text-muted);">Number of working days in a business week.</small>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="break_time_minutes">Standard Break Duration (Minutes)</label>
                        <input type="number" min="0" max="180" id="break_time_minutes"
                               name="break_time_minutes" class="form-control"
                               value="${settings['break_time_minutes'] != null ? settings['break_time_minutes'] : '60'}" required>
                        <small style="color: var(--text-muted);">Allocated lunch and intermediate breaks.</small>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="overtime_threshold">Overtime Threshold (Hours)</label>
                        <input type="number" step="0.5" min="1" max="24" id="overtime_threshold"
                               name="overtime_threshold" class="form-control"
                               value="${settings['overtime_threshold'] != null ? settings['overtime_threshold'] : '8'}" required>
                        <small style="color: var(--text-muted);">Hours exceeding this mark count as overtime.</small>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="start_of_day">Default Shift Start</label>
                        <input type="time" id="start_of_day" name="start_of_day" class="form-control"
                               value="${settings['start_of_day'] != null ? settings['start_of_day'] : '09:00'}" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="end_of_day">Default Shift End</label>
                        <input type="time" id="end_of_day" name="end_of_day" class="form-control"
                               value="${settings['end_of_day'] != null ? settings['end_of_day'] : '18:00'}" required>
                    </div>
                </div>

                <div style="margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary" style="width: 100%;">
                        <i class="fa-solid fa-floppy-disk"></i>
                        <span>Save System Settings</span>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
