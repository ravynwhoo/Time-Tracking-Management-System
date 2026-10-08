package com.timetrack.filter;

import com.timetrack.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter that verifies whether the user is authenticated for protected URLs.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    // Public URI prefixes that do not require authentication
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login",
            "/register",
            "/auth",
            "/assets",
            "/css",
            "/js",
            "/images",
            "/favicon.ico"
    );

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Allow root page or public paths
        if (path.isEmpty() || "/".equals(path) || isPublic(path)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            // Not authenticated: save redirect target and redirect to login
            String query = request.getQueryString();
            String fullUrl = path + (query != null ? "?" + query : "");
            request.getSession(true).setAttribute("redirectAfterLogin", fullUrl);
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isPublic(String path) {
        for (String pub : PUBLIC_PATHS) {
            if (path.startsWith(pub)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
