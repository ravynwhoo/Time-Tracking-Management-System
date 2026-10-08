package com.timetrack.controller;

import com.google.gson.Gson;
import com.timetrack.model.ProductivitySummary;
import com.timetrack.model.TimeLog;
import com.timetrack.model.User;
import com.timetrack.service.TimeLogService;
import com.timetrack.service.TimeLogServiceImpl;
import com.timetrack.service.UserService;
import com.timetrack.service.UserServiceImpl;
import com.timetrack.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

/**
 * Controller for generating comprehensive productivity reports, trend analytics, and CSV exports.
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/reports", "/reports/export"})
public class ReportServlet extends HttpServlet {

    private final TimeLogService timeLogService = new TimeLogServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String userIdParam = req.getParameter("userId");

            Date startDate = (startDateStr != null && !startDateStr.isEmpty())
                    ? DateUtil.parseDate(startDateStr)
                    : DateUtil.daysAgo(30);

            Date endDate = (endDateStr != null && !endDateStr.isEmpty())
                    ? DateUtil.parseDate(endDateStr)
                    : DateUtil.today();

            Integer targetUserId;
            if (currentUser.isAdmin()) {
                targetUserId = (userIdParam != null && !userIdParam.isEmpty()) ? Integer.parseInt(userIdParam) : null;
                req.setAttribute("userList", userService.getAllUsers());
            } else {
                targetUserId = currentUser.getId();
            }

            if ("/reports/export".equals(path)) {
                exportCsv(resp, targetUserId, startDate, endDate);
                return;
            }

            ProductivitySummary summary = timeLogService.getProductivitySummary(targetUserId, startDate, endDate);
            List<TimeLog> logs = timeLogService.searchLogs(targetUserId, null, startDate, endDate);

            req.setAttribute("summary", summary);
            req.setAttribute("logs", logs);
            req.setAttribute("startDate", DateUtil.formatDate(startDate));
            req.setAttribute("endDate", DateUtil.formatDate(endDate));
            req.setAttribute("selectedUserId", targetUserId);

            req.setAttribute("dailyHoursJson", gson.toJson(summary.getDailyHours()));
            req.setAttribute("taskHoursJson", gson.toJson(summary.getTaskHours()));
            if (currentUser.isAdmin()) {
                req.setAttribute("userHoursJson", gson.toJson(summary.getUserHours()));
            }

            req.getRequestDispatcher("/WEB-INF/views/report/reports.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Failed to generate report: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/report/reports.jsp").forward(req, resp);
        }
    }

    private void exportCsv(HttpServletResponse resp, Integer userId, Date startDate, Date endDate) throws Exception {
        List<TimeLog> logs = timeLogService.searchLogs(userId, null, startDate, endDate);

        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"timetrack_report_" + startDate + "_to_" + endDate + ".csv\"");

        try (PrintWriter writer = resp.getWriter()) {
            writer.println("Log ID,User Name,Date,Start Time,End Time,Task Description,Duration (Hours)");
            for (TimeLog log : logs) {
                writer.printf("%d,\"%s\",%s,%s,%s,\"%s\",%.2f%n",
                        log.getId(),
                        log.getUserName() != null ? log.getUserName().replace("\"", "\"\"") : "",
                        log.getLogDate(),
                        log.getStartTime(),
                        log.getEndTime(),
                        log.getTask() != null ? log.getTask().replace("\"", "\"\"") : "",
                        log.getDuration().doubleValue());
            }
        }
    }
}
