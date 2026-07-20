package Students.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Strict input-validation helpers. Every read of {@code request.getParameter}
 * should go through one of these so a single missing or out-of-range value
 * produces a clean 400 JSON response instead of a 500 stack trace.
 */
public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static String requireString(HttpServletRequest req, String name, int maxLen) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty()) {
            throw new BadRequestException("Missing required field: " + name);
        }
        if (v.length() > maxLen) {
            throw new BadRequestException("Field too long: " + name);
        }
        return v.trim();
    }

    public static String optionalString(HttpServletRequest req, String name, int maxLen) {
        String v = req.getParameter(name);
        if (v == null) return null;
        if (v.length() > maxLen) {
            throw new BadRequestException("Field too long: " + name);
        }
        return v.trim();
    }

    public static int requireInt(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty()) {
            throw new BadRequestException("Missing required field: " + name);
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Field must be an integer: " + name);
        }
    }

    public static int requireNonNegativeInt(HttpServletRequest req, String name) {
        int n = requireInt(req, name);
        if (n < 0) {
            throw new BadRequestException("Field must be non-negative: " + name);
        }
        return n;
    }

    /** Restrict a string role to a known set. */
    public static String requireRole(HttpServletRequest req, String allowedCsv) {
        String role = requireString(req, "role", 20);
        for (String allowed : allowedCsv.split(",")) {
            if (allowed.trim().equalsIgnoreCase(role)) {
                return role.toLowerCase();
            }
        }
        throw new BadRequestException("Role not allowed: " + role);
    }
}
