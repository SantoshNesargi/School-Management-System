package Students.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import Students.Jdbc;
import Students.util.BadRequestException;
import Students.util.Json;
import Students.util.PasswordUtil2;
import Students.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * JSON auth endpoints: {@code /api/auth/login}, {@code /api/auth/logout},
 * {@code /api/auth/register}, {@code /api/auth/me}.
 */
@WebServlet("/api/auth/*")
public class AuthApi extends ApiServlet {

    @Override
    protected void doApiPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        switch (path) {
            case "/login":    handleLogin(req, resp); break;
            case "/register": handleRegister(req, resp); break;
            case "/logout":   handleLogout(req, resp); break;
            default: writeError(resp, 404, "Unknown auth endpoint: " + path);
        }
    }

    @Override
    protected void doApiGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/me".equals(req.getPathInfo())) {
            handleMe(req, resp);
            return;
        }
        writeError(resp, 404, "Unknown auth endpoint");
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = ValidationUtil.requireString(req, "username", 50);
        String password = ValidationUtil.requireString(req, "password", 200);

        try (Connection con = Jdbc.getConnection()) {
            String findSql = "SELECT id, password, role, password_algo FROM user WHERE username=?";
            try (PreparedStatement ps = con.prepareStatement(findSql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        // Same error for missing user and bad password so the
                        // endpoint does not leak which usernames exist.
                        writeError(resp, 401, "Invalid username or password");
                        return;
                    }
                    int id = rs.getInt("id");
                    String stored = rs.getString("password");
                    String algo = rs.getString("password_algo");
                    String role = rs.getString("role");

                    if (!PasswordUtil2.verify(password, stored, algo)) {
                        writeError(resp, 401, "Invalid username or password");
                        return;
                    }

                    // Prevent session fixation: rotate the session ID on login.
                    HttpSession old = req.getSession(false);
                    if (old != null) old.invalidate();
                    HttpSession session = req.getSession(true);
                    session.setAttribute("user", username);
                    session.setAttribute("role", role == null ? "student" : role.toLowerCase());
                    session.setAttribute("user_id", id);
                    session.setMaxInactiveInterval(60 * 60);

                    // If the password was stored with the legacy SHA-256 algo,
                    // upgrade it in the same transaction.
                    String upgraded = PasswordUtil2.upgradeFromLegacyIfMatched(password, stored, algo);
                    if (upgraded != null) {
                        try (PreparedStatement up = con.prepareStatement(
                                "UPDATE user SET password=?, password_algo='PBKDF2' WHERE id=?")) {
                            up.setString(1, upgraded);
                            up.setInt(2, id);
                            up.executeUpdate();
                        }
                    }

                    writeOk(resp, Json.object()
                            .put("ok", true)
                            .put("username", username)
                            .put("role", session.getAttribute("role"))
                            .end().toString());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = ValidationUtil.requireString(req, "username", 50);
        String password = ValidationUtil.requireString(req, "password", 200);
        if (password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }
        // SECURITY: the public registration endpoint may only create student
        // accounts. Admin/teacher accounts must be promoted by an existing
        // admin through the admin API.
        String role = "student";

        String hash = PasswordUtil2.hash(password);
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO user(username, password, role, password_algo) VALUES(?, ?, ?, 'PBKDF2')")) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new BadRequestException("Username already taken");
            }
            throw new RuntimeException(e);
        }
        writeCreated(resp, Json.object()
                .put("ok", true)
                .put("username", username)
                .put("role", role)
                .end().toString());
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        writeOk(resp, Json.object().put("ok", true).end().toString());
    }

    private void handleMe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            writeError(resp, 401, "Not logged in");
            return;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("ok", true);
        body.put("username", session.getAttribute("user"));
        body.put("role", session.getAttribute("role"));
        writeOk(resp, Json.object()
                .put("ok", true)
                .put("username", String.valueOf(session.getAttribute("user")))
                .put("role", String.valueOf(session.getAttribute("role")))
                .end().toString());
    }
}
