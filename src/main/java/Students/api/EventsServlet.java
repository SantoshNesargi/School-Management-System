package Students.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import Students.util.EventBus;
import Students.util.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Server-Sent Events stream. One long-lived HTTP response, one EventSource
 * per browser tab. Subscribes to every topic on the {@link EventBus} and
 * forwards each event to the client. The response is sent with the
 * {@code text/event-stream} content type and the connection is kept open
 * until the client closes the tab or the server is shut down.
 */
@WebServlet("/api/events")
public class EventsServlet extends HttpServlet {

    private static final List<String> TOPICS = List.of(
            "students", "marks", "attendance", "timetable-class", "timetable-exam");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(401);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"ok\":false,\"error\":\"Login required\"}");
            return;
        }

        // SSE requires the response not to be buffered or compressed.
        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache, no-transform");
        resp.setHeader("Connection", "keep-alive");
        resp.setHeader("X-Accel-Buffering", "no");

        final PrintWriter out = resp.getWriter();
        out.flush();

        AutoCloseable[] subs = new AutoCloseable[TOPICS.size()];
        for (int i = 0; i < TOPICS.size(); i++) {
            final String topic = TOPICS.get(i);
            subs[i] = EventBus.get().subscribe(topic, payload -> {
                try {
                    String data = (payload instanceof String s) ? s : Json.stringify(payload);
                    out.write("event: " + topic + "\n");
                    out.write("data: " + data + "\n\n");
                    out.flush();
                } catch (IOException e) {
                    // Client disconnected — let EventBus swallow this.
                    throw new RuntimeException(e);
                }
            });
        }

        try {
            out.write(": connected\n\n");
            out.flush();
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(15_000);
                if (out.checkError()) {
                    break;
                }
                out.write(": ping\n\n");
                out.flush();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            for (AutoCloseable h : subs) {
                try { h.close(); } catch (Exception ignored) { /* nothing */ }
            }
        }
    }
}
