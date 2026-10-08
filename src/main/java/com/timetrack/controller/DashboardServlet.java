package com.timetrack.controller;

import com.google.gson.Gson;
import com.timetrack.model.ProductivitySummary;
import com.timetrack.model.TimeLog;
import com.timetrack.model.User;
import com.timetrack.service.*;
import com.timetrack.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

/**
 * Controller providing role-based dashboard view and KPI metrics.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", ""})
public class DashboardServlet extends HttpServlet {

    private final TimeLogService timeLogService = new TimeLogServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final SettingsService settingsService = new SettingsService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            Date today = DateUtil.today();
            Date sevenDaysAgo = DateUtil.daysAgo(7);
            Date thirtyDaysAgo = DateUtil.daysAgo(30);

            String targetHoursStr = settingsService.getSetting("work_hours_per_day", "8");
            BigDecimal targetHours = new BigDecimal(targetHoursStr);
            req.setAttribute("dailyTargetHours", targetHours);

            if (currentUser.isAdmin()) {
                // Admin Dashboard KPIs
                int totalUsers = userService.getUserCount();
                List<TimeLog> recentLogs = timeLogService.getAllLogs();
                ProductivitySummary summary7d = timeLogService.getProductivitySummary(null, sevenDaysAgo, today);
                ProductivitySummary summary30d = timeLogService.getProductivitySummary(null, thirtyDaysAgo, today);

                // Sublist for recent 8 logs
                List<TimeLog> latestPreview = recentLogs.size() > 8 ? recentLogs.subList(0, 8) : recentLogs;

                req.setAttribute("totalUsers", totalUsers);
                req.setAttribute("totalHoursAllTime", summary30d.getTotalHours());
                req.setAttribute("totalEntriesAllTime", summary30d.getTotalEntries());
                req.setAttribute("recentLogs", latestPreview);
                req.setAttribute("summary", summary7d);
                req.setAttribute("dailyHoursJson", gson.toJson(summary7d.getDailyHours()));
                req.setAttribute("taskHoursJson", gson.toJson(summary7d.getTaskHours()));
                req.setAttribute("userHoursJson", gson.toJson(summary7d.getUserHours()));

                req.getRequestDispatcher("/WEB-INF/views/dashboard/admin_dashboard.jsp").forward(req, resp);
            } else {
                // User Dashboard KPIs
                List<TimeLog> userLogsToday = timeLogService.searchLogs(currentUser.getId(), null, today, today);
                BigDecimal todayHours = BigDecimal.ZERO;
                for (TimeLog log : userLogsToday) {
                    todayHours = todayHours.add(log.getDuration());
                }

                ProductivitySummary summary7d = timeLogService.getProductivitySummary(currentUser.getId(), sevenDaysAgo, today);
                List<TimeLog> recentUserLogs = timeLogService.getUserLogs(currentUser.getId());
                List<TimeLog> latestPreview = recentUserLogs.size() > 5 ? recentUserLogs.subList(0, 5) : recentUserLogs;

                req.setAttribute("todayDate", today);
                req.setAttribute("todayHours", todayHours);
                req.setAttribute("recentLogs", latestPreview);
                req.setAttribute("summary", summary7d);
                req.setAttribute("dailyHoursJson", gson.toJson(summary7d.getDailyHours()));
                req.setAttribute("taskHoursJson", gson.toJson(summary7d.getTaskHours()));

                // Calculate target progress percentage
                int progressPercent = targetHours.compareTo(BigDecimal.ZERO) > 0
                        ? Math.min(100, todayHours.multiply(new BigDecimal(100)).divide(targetHours, 0, RoundingMode.HALF_UP).intValue())
                        : 0;
                req.setAttribute("progressPercent", progressPercent);

                req.getRequestDispatcher("/WEB-INF/views/dashboard/user_dashboard.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
        }
    }
}
