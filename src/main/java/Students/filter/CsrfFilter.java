package Students.filter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Synchronizer-token CSRF protection.
 * <p>
 * On every request:
 * <ol>
 *   <li>If the session has no {@code csrfToken} attribute, generate a fresh
 *       32-byte random token, store it, and make it available to JSPs as
 *       a request attribute.</li>
 *   <li>For non-idempotent methods (POST/PUT/DELETE/PATCH), require the
 *       request to carry an {@code X-CSRF-Token} header that matches the
 *       session token. Mismatch → 403 JSON.</li>
 *   <li>The {@code /api/auth/login} endpoint is exempt because the user
 *       has no session yet; the same filter issues a token right after
 *       login.</li>
 * </ol>
 */
public class CsrfFilter implements Filter {

    public static final String SESSION_ATTR = "csrfToken";
    public static final String REQ_ATTR = "csrfToken";
    public static final String HEADER = "X-CSRF-Token";

    private static final SecureRandom RNG = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(true);
        String token = (String) session.getAttribute(SESSION_ATTR);
        if (token == null) {
            token = newToken();
            session.setAttribute(SESSION_ATTR, token);
        }
        req.setAttribute(REQ_ATTR, token);

        String method = req.getMethod();
        boolean mutating = "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method);

        // Public auth endpoints need to be reachable without a token.
        String path = req.getRequestURI();
        boolean isPublicAuth = path != null && path.endsWith("/api/auth/login")
                || path != null && path.endsWith("/api/auth/register");

        if (mutating && !isPublicAuth) {
            String supplied = req.getHeader(HEADER);
            if (supplied == null || !constantTimeEquals(supplied, token)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.setContentType("application/json");
                resp.getWriter().write(
                        "{\"ok\":false,\"error\":\"Missing or invalid CSRF token\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public static String newToken() {
        byte[] buf = new byte[32];
        RNG.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
