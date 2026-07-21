package Students.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import Students.Jdbc;
import Students.util.BadRequestException;
import Students.util.EventBus;
import Students.util.Json;
import Students.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CRUD for the two timetable tables. The {@code /api/timetable/class}
 * sub-path handles the class timetable and {@code /api/timetable/exam}
 * handles the exam timetable. The path-info is the discriminator; the
 * rest of the request shape is identical.
 */
@WebServlet("/api/timetable/*")
public class TimetableApi extends ApiServlet {

    @Override
    protected void doApiGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        String topic;
        String table;
        if ("/class".equals(path)) { topic = "timetable-class"; table = "class_timetable"; }
        else if ("/exam".equals(path)) { topic = "timetable-exam"; table = "exam_timetable"; }
        else { writeError(resp, 404, "Unknown timetable endpoint"); return; }

        String cls = req.getParameter("class");
        try (Connection con = Jdbc.getConnection()) {
            String sql = (cls != null && !cls.isBlank())
                    ? "SELECT id, `class`, subject, day, time, exam_date FROM " + table
                      + " WHERE `class`=? ORDER BY id"
                    : "SELECT id, `class`, subject, day, time, exam_date FROM " + table + " ORDER BY id";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (cls != null && !cls.isBlank()) ps.setString(1, cls);
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder out = new StringBuilder("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) out.append(',');
                        first = false;
                        Json j = Json.object()
                                .put("id", rs.getInt("id"))
                                .put("class", rs.getString("class"))
                                .put("subject", rs.getString("subject"));
                        if ("class_timetable".equals(table)) {
                            j.put("day", rs.getString("day"));
                            j.put("time", rs.getString("time"));
                        } else {
                            java.sql.Date d = rs.getDate("exam_date");
                            j.put("date", d == null ? "" : d.toString());
                            j.put("time", rs.getString("time"));
                        }
                        out.append(j.toString());
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
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        if ("/class".equals(path)) {
            handleClassCreate(req, resp);
        } else if ("/exam".equals(path)) {
            handleExamCreate(req, resp);
        } else {
            writeError(resp, 404, "Unknown timetable endpoint");
        }
    }

    @Override
    protected void doApiPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        if ("/class".equals(path))      handleClassUpdate(req, resp);
        else if ("/exam".equals(path))  handleExamUpdate(req, resp);
        else writeError(resp, 404, "Unknown timetable endpoint");
    }

    @Override
    protected void doApiDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        String table;
        String topic;
        if ("/class".equals(path))      { table = "class_timetable"; topic = "timetable-class"; }
        else if ("/exam".equals(path))  { table = "exam_timetable";  topic = "timetable-exam"; }
        else { writeError(resp, 404, "Unknown timetable endpoint"); return; }

        int id = ValidationUtil.requireNonNegativeInt(req, "id");
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM " + table + " WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) { writeError(resp, 404, "Row not found"); return; }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish(topic, java.util.Map.of("event", "deleted", "id", id));
        writeOk(resp, Json.object().put("ok", true).end().toString());
    }

    private void handleClassCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cls    = ValidationUtil.requireString(req, "class", 20);
        String subj   = ValidationUtil.requireString(req, "subject", 50);
        String day    = ValidationUtil.requireString(req, "day", 20);
        String time   = ValidationUtil.requireString(req, "time", 20);
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO class_timetable(`class`, subject, day, time) VALUES(?, ?, ?, ?)")) {
            ps.setString(1, cls);
            ps.setString(2, subj);
            ps.setString(3, day);
            ps.setString(4, time);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("timetable-class", java.util.Map.of("event", "created", "class", cls));
        writeCreated(resp, Json.object().put("ok", true).end().toString());
    }

    private void handleExamCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cls  = ValidationUtil.requireString(req, "class", 20);
        String subj = ValidationUtil.requireString(req, "subject", 50);
        String dateStr = ValidationUtil.requireString(req, "exam_date", 10);
        String time = ValidationUtil.requireString(req, "time", 20);
        Date sqlDate;
        try {
            sqlDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("exam_date must be yyyy-MM-dd");
        }
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO exam_timetable(`class`, subject, exam_date, time) VALUES(?, ?, ?, ?)")) {
            ps.setString(1, cls);
            ps.setString(2, subj);
            ps.setDate(3, sqlDate);
            ps.setString(4, time);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("timetable-exam", java.util.Map.of("event", "created", "class", cls));
        writeCreated(resp, Json.object().put("ok", true).end().toString());
    }

    private void handleClassUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id    = ValidationUtil.requireNonNegativeInt(req, "id");
        String subj = ValidationUtil.requireString(req, "subject", 50);
        String day  = ValidationUtil.requireString(req, "day", 20);
        String time = ValidationUtil.requireString(req, "time", 20);
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE class_timetable SET subject=?, day=?, time=? WHERE id=?")) {
            ps.setString(1, subj);
            ps.setString(2, day);
            ps.setString(3, time);
            ps.setInt(4, id);
            int rows = ps.executeUpdate();
            if (rows == 0) { writeError(resp, 404, "Row not found"); return; }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("timetable-class", java.util.Map.of("event", "updated", "id", id));
        writeOk(resp, Json.object().put("ok", true).end().toString());
    }

    private void handleExamUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id    = ValidationUtil.requireNonNegativeInt(req, "id");
        String subj = ValidationUtil.requireString(req, "subject", 50);
        String dateStr = ValidationUtil.requireString(req, "exam_date", 10);
        String time = ValidationUtil.requireString(req, "time", 20);
        Date sqlDate;
        try {
            sqlDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("exam_date must be yyyy-MM-dd");
        }
        try (Connection con = Jdbc.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE exam_timetable SET subject=?, exam_date=?, time=? WHERE id=?")) {
            ps.setString(1, subj);
            ps.setDate(2, sqlDate);
            ps.setString(3, time);
            ps.setInt(4, id);
            int rows = ps.executeUpdate();
            if (rows == 0) { writeError(resp, 404, "Row not found"); return; }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        EventBus.get().publish("timetable-exam", java.util.Map.of("event", "updated", "id", id));
        writeOk(resp, Json.object().put("ok", true).end().toString());
    }
}
