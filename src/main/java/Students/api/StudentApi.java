package Students.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Students.Jdbc;
import Students.util.BadRequestException;
import Students.util.EventBus;
import Students.util.Json;
import Students.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CRUD for the {@code student} table. The {@code class} column is a SQL
 * reserved word, so every query that references it is backtick-quoted.
 * <ul>
 *   <li>{@code GET    /api/students}            — list</li>
 *   <li>{@code GET    /api/students?id=N}       — by id</li>
 *   <li>{@code GET    /api/students?class=10A}  — by class</li>
 *   <li>{@code POST   /api/students}            — add</li>
 *   <li>{@code PUT    /api/students}            — update (id required)</li>
 *   <li>{@code DELETE /api/students?id=N}       — delete</li>
 * </ul>
 */
@WebServlet("/api/students/*")
public class StudentApi extends ApiServlet {

    @Override
    protected void doApiGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        String cls = req.getParameter("class");

        try (Connection con = Jdbc.getConnection()) {
            if (idParam != null && !idParam.isBlank()) {
                int id = ValidationUtil.requireNonNegativeInt(req, "id");
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id, name, `class`, age FROM student WHERE id=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            writeError(resp, 404, "Student not found");
                            return;
                        }
                        writeOk(resp, studentJson(rs).toString());
                    }
                }
                return;
            }

            String sql = (cls != null && !cls.isBlank())
                    ? "SELECT id, name, `class`, age FROM student WHERE `class`=? ORDER BY id"
                    : "SELECT id, name, `class`, age FROM student ORDER BY id";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (cls != null && !cls.isBlank()) ps.setString(1, cls);
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder out = new StringBuilder("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) out.append(',');
                        first = false;
                        out.append(studentJson(rs).toString());
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
        String name  = ValidationUtil.requireString(req, "name", 100);
        String cls   = ValidationUtil.requireString(req, "class", 20);
        int    age   = ValidationUtil.requireNonNegativeInt(req, "age");
        int    id    = ValidationUtil.requireNonNegativeInt(req, "id");

        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO student(id, name, `class`, age) VALUES(?, ?, ?, ?)")) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, cls);
            ps.setInt(4, age);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new BadRequestException("A student with that id already exists");
            }
            throw new RuntimeException(e);
        }
        EventBus.get().publish("students", java.util.Map.of("event", "created", "id", id));
        writeCreated(resp, Json.object()
                .put("ok", true)
                .put("id", id)
                .end().toString());
    }

    @Override
    protected void doApiPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = ValidationUtil.requireNonNegativeInt(req, "id");
        String name = ValidationUtil.requireString(req, "name", 100);
        String cls  = ValidationUtil.requireString(req, "class", 20);
        int    age  = ValidationUtil.requireNonNegativeInt(req, "age");

        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE student SET name=?, `class`=?, age=? WHERE id=?")) {
            ps.setString(1, name);
            ps.setString(2, cls);
            ps.setInt(3, age);
            ps.setInt(4, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                writeError(resp, 404, "Student not found");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("students", java.util.Map.of("event", "updated", "id", id));
        writeOk(resp, Json.object().put("ok", true).put("id", id).end().toString());
    }

    @Override
    protected void doApiDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = ValidationUtil.requireNonNegativeInt(req, "id");
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM student WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                writeError(resp, 404, "Student not found");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("students", java.util.Map.of("event", "deleted", "id", id));
        writeOk(resp, Json.object().put("ok", true).put("id", id).end().toString());
    }

    private static Json studentJson(ResultSet rs) throws SQLException {
        return Json.object()
                .put("id", rs.getInt("id"))
                .put("name", rs.getString("name"))
                .put("class", rs.getString("class"))
                .put("age", rs.getInt("age"));
    }
}
