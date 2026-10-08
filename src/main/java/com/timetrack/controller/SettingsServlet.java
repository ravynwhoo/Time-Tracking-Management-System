package com.timetrack.controller;

import com.timetrack.service.SettingsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for viewing and updating system settings (Admin only).
 */
@WebServlet(name = "SettingsServlet", urlPatterns = {"/settings"})
public class SettingsServlet extends HttpServlet {

    private final SettingsService settingsService = new SettingsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, String> settings = settingsService.getAllSettings();
            req.setAttribute("settings", settings);
            req.getRequestDispatcher("/WEB-INF/views/settings/settings.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading settings: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/settings/settings.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, String> newSettings = new HashMap<>();
            String[] keys = {
                    "work_hours_per_day",
                    "break_time_minutes",
                    "work_days_per_week",
                    "overtime_threshold",
                    "start_of_day",
                    "end_of_day"
            };

            for (String key : keys) {
                String val = req.getParameter(key);
                if (val != null) {
                    newSettings.put(key, val.trim());
                }
            }

            settingsService.updateSettings(newSettings);
            req.getSession().setAttribute("flashSuccess", "System settings successfully updated!");
            resp.sendRedirect(req.getContextPath() + "/settings");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to update settings: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/settings");
        }
    }
}
