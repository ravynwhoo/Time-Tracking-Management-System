package com.timetrack.controller;

import com.timetrack.exception.ResourceNotFoundException;
import com.timetrack.exception.ValidationException;
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
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Controller managing Time Log entries: listing, filtering, creation, update, and deletion.
 */
@WebServlet(name = "TimeLogServlet", urlPatterns = {
        "/timelog",
        "/timelog/new",
        "/timelog/edit",
        "/timelog/delete",
        "/timelog/save"
})
public class TimeLogServlet extends HttpServlet {

    private final TimeLogService timeLogService = new TimeLogServiceImpl();
    private final UserService userService = new UserServiceImpl();

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
            switch (path) {
                case "/timelog/new":
                    showNewForm(req, resp, currentUser);
                    break;
                case "/timelog/edit":
                    showEditForm(req, resp, currentUser);
                    break;
                case "/timelog/delete":
                    handleDelete(req, resp, currentUser);
                    break;
                case "/timelog":
                default:
                    showLogList(req, resp, currentUser);
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            showLogListQuietly(req, resp, currentUser);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        handleSave(req, resp, currentUser);
    }

    private void showLogList(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        try {
            String keyword = req.getParameter("keyword");
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String userIdParam = req.getParameter("userId");

            Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? DateUtil.parseDate(startDateStr) : null;
            Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? DateUtil.parseDate(endDateStr) : null;

            Integer targetUserId;
            if (currentUser.isAdmin()) {
                targetUserId = (userIdParam != null && !userIdParam.isEmpty()) ? Integer.parseInt(userIdParam) : null;
                req.setAttribute("userList", userService.getAllUsers());
            } else {
                targetUserId = currentUser.getId();
            }

            List<TimeLog> logs = timeLogService.searchLogs(targetUserId, keyword, startDate, endDate);

            // Compute total hours in current view
            BigDecimal totalViewHours = BigDecimal.ZERO;
            for (TimeLog l : logs) {
                totalViewHours = totalViewHours.add(l.getDuration());
            }

            req.setAttribute("logs", logs);
            req.setAttribute("totalViewHours", totalViewHours);
            req.setAttribute("selectedUserId", targetUserId);
            req.setAttribute("keyword", keyword);
            req.setAttribute("startDate", startDateStr);
            req.setAttribute("endDate", endDateStr);

            req.getRequestDispatcher("/WEB-INF/views/timelog/log_list.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Failed to retrieve logs: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/timelog/log_list.jsp").forward(req, resp);
        }
    }

    private void showLogListQuietly(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        try {
            showLogList(req, resp, currentUser);
        } catch (Exception ignored) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        try {
            TimeLog log = new TimeLog();
            log.setUserId(currentUser.getId());
            log.setLogDate(DateUtil.today());
            req.setAttribute("timeLog", log);
            req.setAttribute("action", "Create");

            if (currentUser.isAdmin()) {
                req.setAttribute("userList", userService.getAllUsers());
            }
            req.getRequestDispatcher("/WEB-INF/views/timelog/log_form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            showLogListQuietly(req, resp, currentUser);
        }
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        try {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/timelog");
                return;
            }
            int id = Integer.parseInt(idStr);
            TimeLog log = timeLogService.getLogById(id);

            // Access control check
            if (!currentUser.isAdmin() && !log.getUserId().equals(currentUser.getId())) {
                req.getSession().setAttribute("flashError", "You are not authorized to edit this log.");
                resp.sendRedirect(req.getContextPath() + "/timelog");
                return;
            }

            req.setAttribute("timeLog", log);
            req.setAttribute("action", "Edit");
            if (currentUser.isAdmin()) {
                req.setAttribute("userList", userService.getAllUsers());
            }
            req.getRequestDispatcher("/WEB-INF/views/timelog/log_form.jsp").forward(req, resp);
        } catch (ResourceNotFoundException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/timelog");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to load log: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/timelog");
        }
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String userIdStr = req.getParameter("userId");
        String logDateStr = req.getParameter("logDate");
        String startTimeStr = req.getParameter("startTime");
        String endTimeStr = req.getParameter("endTime");
        String task = req.getParameter("task");

        try {
            Date logDate = DateUtil.parseDate(logDateStr);
            Time startTime = DateUtil.parseTime(startTimeStr);
            Time endTime = DateUtil.parseTime(endTimeStr);

            Integer assignedUserId = currentUser.getId();
            if (currentUser.isAdmin() && userIdStr != null && !userIdStr.isEmpty()) {
                assignedUserId = Integer.parseInt(userIdStr);
            }

            TimeLog log = new TimeLog();
            log.setUserId(assignedUserId);
            log.setLogDate(logDate);
            log.setStartTime(startTime);
            log.setEndTime(endTime);
            log.setTask(task);

            if (idStr != null && !idStr.trim().isEmpty()) {
                log.setId(Integer.parseInt(idStr.trim()));
                timeLogService.updateLog(log, currentUser);
                req.getSession().setAttribute("flashSuccess", "Time log updated successfully!");
            } else {
                timeLogService.createLog(log);
                req.getSession().setAttribute("flashSuccess", "Time log added successfully!");
            }

            resp.sendRedirect(req.getContextPath() + "/timelog");
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            TimeLog draft = new TimeLog();
            if (idStr != null && !idStr.isEmpty()) draft.setId(Integer.parseInt(idStr));
            draft.setTask(task);
            req.setAttribute("timeLog", draft);
            req.setAttribute("action", (idStr != null && !idStr.isEmpty()) ? "Edit" : "Create");
            try {
                if (currentUser.isAdmin()) req.setAttribute("userList", userService.getAllUsers());
            } catch (Exception ignored) {}
            req.getRequestDispatcher("/WEB-INF/views/timelog/log_form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/timelog");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws IOException {
        try {
            String idStr = req.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                timeLogService.deleteLog(id, currentUser);
                req.getSession().setAttribute("flashSuccess", "Time log deleted successfully.");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to delete log: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/timelog");
    }
}
