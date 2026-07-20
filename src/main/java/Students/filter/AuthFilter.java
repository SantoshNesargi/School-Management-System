package Students.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Centralised role-based access control. Replaces the scattered
 * {@code isAdmin} checks that were inlined in every servlet.
 * <p>
 * URL prefixes are mapped to required roles:
 * <ul>
 *   <li>{@code /api/admin/*}  — admin only</li>
 *   <li>{@code /api/teacher/*} — admin or teacher</li>
 *   <li>{@code /api/student/*} — admin, teacher, or student</li>
 *   <li>{@code /api/auth/login}, {@code /api/auth/register} — public</li>
 *   <li>{@code /api/events} — any authenticated user</li>
 *   <li>everything else — passes through unchanged</li>
 * </ul>
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI() == null ? "" : req.getRequestURI();

        // Public endpoints.
        if (path.endsWith("/api/auth/login")
                || path.endsWith("/api/auth/register")
                || path.endsWith("/login.jsp")
                || path.endsWith("/register.jsp")
                || path.endsWith("/api/events")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        String role = session == null ? null : (String) session.getAttribute("role");

        boolean needsAdmin   = path.contains("/api/admin/");
        boolean needsTeacher = path.contains("/api/teacher/") || needsAdmin;
        boolean needsStudent = path.contains("/api/student/") || needsTeacher
                || path.endsWith("/admin.jsp")
                || path.endsWith("/teacher.jsp")
                || path.endsWith("/student.jsp")
                || path.endsWith("/dashboard.jsp");

        if (needsStudent) {
            if (role == null) {
                unauthorized(resp, "Login required");
                return;
            }
            if (needsTeacher && !"admin".equals(role) && !"teacher".equals(role)) {
                forbidden(resp, "Teacher or admin role required");
                return;
            }
            if (needsAdmin && !"admin".equals(role)) {
                forbidden(resp, "Admin role required");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private static void unauthorized(HttpServletResponse resp, String msg) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"ok\":false,\"error\":\"" + msg + "\"}");
    }

    private static void forbidden(HttpServletResponse resp, String msg) throws IOException {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"ok\":false,\"error\":\"" + msg + "\"}");
    }
}
