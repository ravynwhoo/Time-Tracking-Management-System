package com.timetrack.controller;

import com.timetrack.exception.ValidationException;
import com.timetrack.model.User;
import com.timetrack.service.UserService;
import com.timetrack.service.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller for Admin User Management (CRUD operations on users).
 */
@WebServlet(name = "UserServlet", urlPatterns = {
        "/users",
        "/users/new",
        "/users/edit",
        "/users/delete",
        "/users/save"
})
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        try {
            switch (path) {
                case "/users/new":
                    showNewForm(req, resp);
                    break;
                case "/users/edit":
                    showEditForm(req, resp);
                    break;
                case "/users/delete":
                    handleDelete(req, resp);
                    break;
                case "/users":
                default:
                    showUserList(req, resp);
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            showUserListQuietly(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleSave(req, resp);
    }

    private void showUserList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<User> users = userService.getAllUsers();
            req.setAttribute("users", users);
            req.getRequestDispatcher("/WEB-INF/views/user/user_list.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Failed to retrieve users: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/user/user_list.jsp").forward(req, resp);
        }
    }

    private void showUserListQuietly(HttpServletRequest req, HttpServletResponse resp) {
        try {
            showUserList(req, resp);
        } catch (Exception ignored) {}
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = new User();
        user.setRole(User.ROLE_USER);
        req.setAttribute("user", user);
        req.setAttribute("action", "Create");
        req.getRequestDispatcher("/WEB-INF/views/user/user_form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/users");
                return;
            }
            int id = Integer.parseInt(idStr);
            User user = userService.getUserById(id);
            req.setAttribute("user", user);
            req.setAttribute("action", "Edit");
            req.getRequestDispatcher("/WEB-INF/views/user/user_form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "User not found: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/users");
        }
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                int id = Integer.parseInt(idStr.trim());
                user.setId(id);
                userService.updateUser(user);
                req.getSession().setAttribute("flashSuccess", "User '" + name + "' updated successfully!");
            } else {
                userService.register(user);
                req.getSession().setAttribute("flashSuccess", "User '" + name + "' created successfully!");
            }
            resp.sendRedirect(req.getContextPath() + "/users");
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("user", user);
            req.setAttribute("action", (idStr != null && !idStr.isEmpty()) ? "Edit" : "Create");
            req.getRequestDispatcher("/WEB-INF/views/user/user_form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to save user: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/users");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        try {
            String idStr = req.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);

                // Prevent admin from deleting their own account
                if (currentUser != null && currentUser.getId().equals(id)) {
                    req.getSession().setAttribute("flashError", "You cannot delete your own administrative account.");
                } else {
                    userService.deleteUser(id);
                    req.getSession().setAttribute("flashSuccess", "User deleted successfully.");
                }
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to delete user: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/users");
    }
}
