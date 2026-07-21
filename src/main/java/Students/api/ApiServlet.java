package Students.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import Students.Jdbc;
import Students.util.BadRequestException;
import Students.util.Json;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Base class for the JSON REST API. Provides:
 * <ul>
 *   <li>Uniform JSON content type.</li>
 *   <li>Helpers for writing success and error responses.</li>
 *   <li>Centralised exception handling so a thrown {@link BadRequestException}
 *       or {@link SQLException} becomes a clean 400/500 JSON, not a stack
 *       trace in the browser.</li>
 * </ul>
 */
public abstract class ApiServlet extends HttpServlet {

    /** Override to handle GET. Default: 405. */
    protected void doApiGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        methodNotAllowed(resp);
    }

    /** Override to handle POST. Default: 405. */
    protected void doApiPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        methodNotAllowed(resp);
    }

    /** Override to handle PUT. Default: 405. */
    protected void doApiPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        methodNotAllowed(resp);
    }

    /** Override to handle DELETE. Default: 405. */
    protected void doApiDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        methodNotAllowed(resp);
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepare(req, resp);
        try {
            doApiGet(req, resp);
        } catch (RuntimeException | IOException ex) {
            handle(req, resp, ex);
        }
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepare(req, resp);
        try {
            doApiPost(req, resp);
        } catch (RuntimeException | IOException ex) {
            handle(req, resp, ex);
        }
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepare(req, resp);
        try {
            doApiPut(req, resp);
        } catch (RuntimeException | IOException ex) {
            handle(req, resp, ex);
        }
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepare(req, resp);
        try {
            doApiDelete(req, resp);
        } catch (RuntimeException | IOException ex) {
            handle(req, resp, ex);
        }
    }

    private void prepare(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp, Throwable ex) throws IOException {
        if (ex instanceof BadRequestException bre) {
            writeError(resp, bre.getStatus(), bre.getMessage());
            return;
        }
        if (ex instanceof SQLException sqle) {
            // Don't leak SQL state details to the client.
            ex.printStackTrace();
            writeError(resp, 500, "Database error: " + sqle.getMessage());
            return;
        }
        ex.printStackTrace();
        writeError(resp, 500, "Internal error: " + ex.getMessage());
    }

    protected static void writeOk(HttpServletResponse resp, String jsonBody) throws IOException {
        resp.setStatus(200);
        resp.getWriter().write(jsonBody);
    }

    protected static void writeCreated(HttpServletResponse resp, String jsonBody) throws IOException {
        resp.setStatus(201);
        resp.getWriter().write(jsonBody);
    }

    protected static void writeError(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(
                Json.object()
                   .put("ok", false)
                   .put("error", msg == null ? "" : msg)
                   .end().toString());
    }

    private static void methodNotAllowed(HttpServletResponse resp) throws IOException {
        writeError(resp, 405, "Method not allowed");
    }

    /** Convenience: open a connection that is auto-closed when the lambda returns. */
    protected static <T> T withConn(java.util.function.Function<java.sql.Connection, T> fn) throws SQLException {
        try (var con = Jdbc.getConnection()) {
            return fn.apply(con);
        }
    }
}
