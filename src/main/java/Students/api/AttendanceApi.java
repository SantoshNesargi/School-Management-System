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
 * {@code /api/attendance} — record attendance and read summaries.
 */
@WebServlet("/api/attendance/*")
public class AttendanceApi extends ApiServlet {

    @Override
    protected void doApiGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        if ("/summary".equals(path)) {
            handleSummary(req, resp);
        } else {
            handleList(req, resp);
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cls = req.getParameter("class");
        try (Connection con = Jdbc.getConnection()) {
            String sql = (cls != null && !cls.isBlank())
                    ? "SELECT a.id, a.student_id, s.name AS student_name, a.class_name, a.attendance_date, a.status "
                    + "FROM attendance a JOIN student s ON a.student_id = s.id "
                    + "WHERE a.class_name=? ORDER BY a.attendance_date DESC, a.id"
                    : "SELECT a.id, a.student_id, s.name AS student_name, a.class_name, a.attendance_date, a.status "
                    + "FROM attendance a JOIN student s ON a.student_id = s.id "
                    + "ORDER BY a.attendance_date DESC, a.id";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (cls != null && !cls.isBlank()) ps.setString(1, cls);
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
                                .put("class", rs.getString("class_name"))
                                .put("date", String.valueOf(rs.getDate("attendance_date")))
                                .put("status", rs.getString("status"))
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

    private void handleSummary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int sid = ValidationUtil.requireNonNegativeInt(req, "student_id");
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) AS present_days, "
                   + "COUNT(*) AS total_days "
                   + "FROM attendance WHERE student_id=?")) {
            ps.setInt(1, sid);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    writeError(resp, 404, "No attendance records for student");
                    return;
                }
                int present = rs.getInt("present_days");
                int total   = rs.getInt("total_days");
                double percent = total == 0 ? 0 : (present * 100.0 / total);
                writeOk(resp, Json.object()
                        .put("student_id", sid)
                        .put("present_days", present)
                        .put("total_days", total)
                        .put("absent_days", total - present)
                        .put("percentage", String.format("%.2f", percent))
                        .end().toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doApiPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int sid     = ValidationUtil.requireNonNegativeInt(req, "student_id");
        String cls  = ValidationUtil.requireString(req, "class", 20);
        String date = ValidationUtil.requireString(req, "date", 10);
        String status = ValidationUtil.requireString(req, "status", 10);
        if (!status.equalsIgnoreCase("Present") && !status.equalsIgnoreCase("Absent") && !status.equalsIgnoreCase("Late")) {
            throw new Students.util.BadRequestException("status must be Present, Absent or Late");
        }

        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO attendance(student_id, class_name, attendance_date, status) VALUES(?, ?, ?, ?)")) {
            ps.setInt(1, sid);
            ps.setString(2, cls);
            ps.setDate(3, java.sql.Date.valueOf(date));
            ps.setString(4, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("attendance", java.util.Map.of(
                "event", "created", "student_id", sid, "class", cls, "status", status));
        writeCreated(resp, Json.object().put("ok", true).end().toString());
    }
}
