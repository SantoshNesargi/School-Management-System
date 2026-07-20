package Students.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Students.Jdbc;
import Students.util.EventBus;
import Students.util.Json;
import Students.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CRUD for the {@code marks} table.
 * <ul>
 *   <li>{@code GET    /api/marks}              — list (optionally {@code ?student_id=N})</li>
 *   <li>{@code POST   /api/marks}              — add</li>
 *   <li>{@code PUT    /api/marks}              — update by id</li>
 *   <li>{@code DELETE /api/marks?id=N}         — delete</li>
 * </ul>
 */
@WebServlet("/api/marks/*")
public class MarksApi extends ApiServlet {

    @Override
    protected void doApiGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sidParam = req.getParameter("student_id");
        try (Connection con = Jdbc.getConnection()) {
            String sql = (sidParam != null && !sidParam.isBlank())
                    ? "SELECT m.id, m.student_id, s.name AS student_name, s.`class`, m.subject, m.marks "
                    + "FROM marks m JOIN student s ON m.student_id = s.id "
                    + "WHERE m.student_id=? ORDER BY m.id"
                    : "SELECT m.id, m.student_id, s.name AS student_name, s.`class`, m.subject, m.marks "
                    + "FROM marks m JOIN student s ON m.student_id = s.id ORDER BY m.id";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (sidParam != null && !sidParam.isBlank()) {
                    ps.setInt(1, Integer.parseInt(sidParam));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder out = new StringBuilder("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) out.append(',');
                        first = false;
                        out.append(Json.object()
                                .put("id", rs.getInt("id"))
                                .put("student_id", rs.getInt("student_id"))
                                .put("student_name", rs.getString("student_name"))
                                .put("class", rs.getString("class"))
                                .put("subject", rs.getString("subject"))
                                .put("marks", rs.getInt("marks"))
                                .toString());
                    }
                    out.append(']');
                    writeOk(resp, out.toString());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doApiPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int sid     = ValidationUtil.requireNonNegativeInt(req, "student_id");
        String subj = ValidationUtil.requireString(req, "subject", 50);
        int marks   = ValidationUtil.requireNonNegativeInt(req, "marks");
        if (marks > 100) throw new Students.util.BadRequestException("Marks must be <= 100");

        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO marks(student_id, subject, marks) VALUES(?, ?, ?)")) {
            ps.setInt(1, sid);
            ps.setString(2, subj);
            ps.setInt(3, marks);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("marks", java.util.Map.of(
                "event", "created", "student_id", sid, "subject", subj, "marks", marks));
        writeCreated(resp, Json.object().put("ok", true).end().toString());
    }

    @Override
    protected void doApiPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id    = ValidationUtil.requireNonNegativeInt(req, "id");
        int marks = ValidationUtil.requireNonNegativeInt(req, "marks");
        if (marks > 100) throw new Students.util.BadRequestException("Marks must be <= 100");
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE marks SET marks=? WHERE id=?")) {
            ps.setInt(1, marks);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                writeError(resp, 404, "Mark not found");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("marks", java.util.Map.of("event", "updated", "id", id, "marks", marks));
        writeOk(resp, Json.object().put("ok", true).end().toString());
    }

    @Override
    protected void doApiDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = ValidationUtil.requireNonNegativeInt(req, "id");
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM marks WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                writeError(resp, 404, "Mark not found");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("marks", java.util.Map.of("event", "deleted", "id", id));
        writeOk(resp, Json.object().put("ok", true).end().toString());
    }
}
