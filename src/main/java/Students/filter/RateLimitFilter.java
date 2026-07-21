package Students.filter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * In-memory per-IP rate limit for the login endpoint. Five attempts per
 * fifteen minutes, with the limit enforced by a sliding window. The bucket
 * map is bounded — entries older than the window are removed lazily.
 * <p>
 * This is a defence-in-depth measure only; a real deployment would put a
 * proper rate limiter in front of Tomcat. The state is per-JVM so it does
 * not survive a restart and does not cluster.
 */
public class RateLimitFilter implements Filter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 15L * 60L * 1000L;

    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (req.getRequestURI() == null || !req.getRequestURI().endsWith("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String ip = remoteAddr(req);
        long now = System.currentTimeMillis();
        Deque<Long> attempts = hits.computeIfAbsent(ip, k -> new ArrayDeque<>());
        synchronized (attempts) {
            while (!attempts.isEmpty() && now - attempts.peekFirst() > WINDOW_MS) {
                attempts.pollFirst();
            }
            if (attempts.size() >= MAX_ATTEMPTS) {
                resp.setStatus(429);
                resp.setContentType("application/json");
                resp.setHeader("Retry-After", "900");
                resp.getWriter().write(
                        "{\"ok\":false,\"error\":\"Too many login attempts. Try again in 15 minutes.\"}");
                return;
            }
            attempts.addLast(now);
        }

        chain.doFilter(request, response);
    }

    private static String remoteAddr(HttpServletRequest req) {
        String fwd = req.getHeader("X-Forwarded-For");
        if (fwd != null && !fwd.isBlank()) {
            int comma = fwd.indexOf(',');
            return (comma < 0 ? fwd : fwd.substring(0, comma)).trim();
        }
        return req.getRemoteAddr() == null ? "unknown" : req.getRemoteAddr();
    }
}
