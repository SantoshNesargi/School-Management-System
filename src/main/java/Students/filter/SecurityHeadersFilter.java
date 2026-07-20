package Students.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Adds browser-hardening response headers. Cookie flags ({@code HttpOnly},
 * {@code SameSite=Strict}) are configured in {@code web.xml} so Tomcat sets
 * them automatically on the JSESSIONID.
 */
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "same-origin");
        resp.setHeader("Content-Security-Policy",
                "default-src 'self'; "
                + "img-src 'self' data:; "
                + "style-src 'self' 'unsafe-inline'; "
                + "script-src 'self' 'unsafe-inline'; "
                + "connect-src 'self'; "
                + "frame-ancestors 'none'");

        chain.doFilter(request, response);
    }
}
