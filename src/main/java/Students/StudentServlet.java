package Students;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/student")
public class StudentServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        try {

            String action = request.getParameter("action");

            Connection con = Jdbc.getConnection();

            // Save student id in session

            if ("setStudent".equals(action)) {

                int sid =
                        Integer.parseInt(
                                request.getParameter("student_id"));

                request.getSession()
                       .setAttribute("student_id", sid);

                response.sendRedirect("student.jsp");

                return;
            }

            Integer sid =
                    (Integer) request.getSession()
                                     .getAttribute("student_id");

            if (sid == null) {

                out.println("Please Enter Student ID First");

                return;
            }

            // PROFILE

            if ("profile".equals(action)) {

                PreparedStatement ps =
                        con.prepareStatement(
                                "SELECT * FROM student WHERE id=?");

                ps.setInt(1, sid);

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Student Profile</title>");

                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:white;padding:20px;}");
                response.getWriter().println(".card{width:500px;margin:auto;background:white;padding:25px;border-radius:10px;box-shadow:0 2px 8px rgba(0,0,0,0.2);}");
                response.getWriter().println("h2{text-align:center;color:#2563eb;}");
                response.getWriter().println("table{width:100%;border-collapse:collapse;margin-top:20px;}");
                response.getWriter().println("th{background:white;color:black;padding:10px;width:40%; border:1px solid #ddd;}");
                response.getWriter().println("td{padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("a{display:inline-block;margin-top:20px;text-decoration:none;color:white;background:#2563eb;padding:10px 15px;border-radius:5px;}");
                response.getWriter().println("</style>");

                response.getWriter().println("</head><body>");

                if (rs.next()) {

                    response.getWriter().println("<div class='card'>");

                    response.getWriter().println("<h2>👤 Student Profile</h2>");

                    response.getWriter().println("<table>");

                    response.getWriter().println("<tr><th>ID</th><td>"
                            + rs.getInt("id") + "</td></tr>");

                    response.getWriter().println("<tr><th>Name</th><td>"
                            + rs.getString("name") + "</td></tr>");

                    response.getWriter().println("<tr><th>Class</th><td>"
                            + rs.getString("class") + "</td></tr>");

                    response.getWriter().println("<tr><th>Age</th><td>"
                            + rs.getString("age") + "</td></tr>");

                    response.getWriter().println("</table>");

                    response.getWriter().println(
                            "<div style='text-align:center;'>"
                          + "<a href='student.jsp'>⬅ Back to Dashboard</a>"
                          + "</div>");

                    response.getWriter().println("</div>");

                } else {

                    response.getWriter().println(
                            "<h2 style='text-align:center;color:red;'>Student Not Found</h2>");
                }

                response.getWriter().println("</body></html>");

                rs.close();
                ps.close();
            }
            // MARKS

            else if ("marks".equals(action)) {

                PreparedStatement ps =
                        con.prepareStatement(
                                "SELECT * FROM marks WHERE student_id=?");

                ps.setInt(1, sid);

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Student Marks</title>");

                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;padding:20px;}");
                response.getWriter().println(".card{width:700px;margin:auto;background:white;padding:25px;border-radius:10px;box-shadow:0 2px 8px rgba(0,0,0,0.2);}");
                response.getWriter().println("h2{text-align:center;color:#2563eb;}");
                response.getWriter().println("table{width:100%;border-collapse:collapse;margin-top:20px;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:12px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("tr:nth-child(even){background:#f9f9f9;}");
                response.getWriter().println("a{display:inline-block;margin-top:20px;text-decoration:none;color:white;background:#2563eb;padding:10px 15px;border-radius:5px;}");
                response.getWriter().println("</style>");

                response.getWriter().println("</head><body>");

                response.getWriter().println("<div class='card'>");

                response.getWriter().println("<h2>📝 Student Marks</h2>");

                response.getWriter().println("<table>");
                response.getWriter().println("<tr><th>Subject</th><th>Marks</th></tr>");

                boolean hasData = false;

                while (rs.next()) {

                    hasData = true;

                    response.getWriter().println("<tr>");

                    response.getWriter().println("<td>"
                            + rs.getString("subject")
                            + "</td>");

                    response.getWriter().println("<td>"
                            + rs.getInt("marks")
                            + "</td>");

                    response.getWriter().println("</tr>");
                }

                if (!hasData) {
                    response.getWriter().println(
                            "<tr><td colspan='2'>No Marks Found</td></tr>");
                }

                response.getWriter().println("</table>");

                response.getWriter().println(
                        "<div style='text-align:center;'>"
                      + "<a href='student.jsp'>⬅ Back to Dashboard</a>"
                      + "</div>");

                response.getWriter().println("</div>");

                response.getWriter().println("</body></html>");

                rs.close();
                ps.close();
            }
            // ATTENDANCE

            else if ("attendance".equals(action)) {

                PreparedStatement ps =
                        con.prepareStatement(
                                "SELECT COUNT(*) total, "
                              + "SUM(CASE WHEN status='Present' "
                              + "THEN 1 ELSE 0 END) present "
                              + "FROM attendance "
                              + "WHERE student_id=?");

                ps.setInt(1, sid);

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Attendance Summary</title>");

                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;padding:20px;}");
                response.getWriter().println(".card{width:500px;margin:auto;background:white;padding:25px;border-radius:10px;box-shadow:0 2px 8px rgba(0,0,0,0.2);}");
                response.getWriter().println("h2{text-align:center;color:#2563eb;}");
                response.getWriter().println("table{width:100%;border-collapse:collapse;margin-top:20px;}");
                response.getWriter().println("th{background:white;color:black;padding:12px;width:50%;border:1px solid #ddd;text-align:center;}");
                response.getWriter().println("td{padding:12px;border:1px solid #ddd;text-align:center;}");
                response.getWriter().println(".percent{font-size:24px;font-weight:bold;color:#16a34a;text-align:center;margin-top:20px;}");
                response.getWriter().println("a{display:inline-block;margin-top:20px;text-decoration:none;color:white;background:#2563eb;padding:10px 15px;border-radius:5px;}");
                response.getWriter().println("</style>");

                response.getWriter().println("</head><body>");

                if (rs.next()) {

                    int total = rs.getInt("total");
                    int present = rs.getInt("present");

                    double percent =
                            total == 0 ? 0
                            : (present * 100.0 / total);

                    response.getWriter().println("<div class='card'>");

                    response.getWriter().println("<h2>📅 Attendance Summary</h2>");

                    response.getWriter().println("<table>");

                    response.getWriter().println(
                            "<tr><th>Total Days</th><td>"
                            + total + "</td></tr>");

                    response.getWriter().println(
                            "<tr><th>Present Days</th><td>"
                            + present + "</td></tr>");

                    response.getWriter().println(
                            "<tr><th>Absent Days</th><td>"
                            + (total - present) + "</td></tr>");

                    response.getWriter().println("</table>");

                    response.getWriter().println(
                            "<div class='percent'>"
                            + String.format("%.2f", percent)
                            + "% Attendance</div>");

                    response.getWriter().println(
                            "<div style='text-align:center;'>"
                          + "<a href='student.jsp'>⬅ Back to Dashboard</a>"
                          + "</div>");

                    response.getWriter().println("</div>");
                }

                response.getWriter().println("</body></html>");

                rs.close();
                ps.close();
            }

            // EXAM TIMETABLE

            else if ("examTT".equals(action)) {

                PreparedStatement ps =
                        con.prepareStatement(
                                "SELECT e.* "
                              + "FROM exam_timetable e "
                              + "JOIN student s "
                              + "ON e.class=s.class "
                              + "WHERE s.id=?");

                ps.setInt(1, sid);

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Exam Timetable</title>");

                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;padding:20px;}");
                response.getWriter().println(".card{width:800px;margin:auto;background:white;padding:25px;border-radius:10px;box-shadow:0 2px 8px rgba(0,0,0,0.2);}");
                response.getWriter().println("h2{text-align:center;color:#2563eb;}");
                response.getWriter().println("table{width:100%;border-collapse:collapse;margin-top:20px;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:12px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("tr:nth-child(even){background:#f9f9f9;}");
                response.getWriter().println("a{display:inline-block;margin-top:20px;text-decoration:none;color:white;background:#2563eb;padding:10px 15px;border-radius:5px;}");
                response.getWriter().println("</style>");

                response.getWriter().println("</head><body>");

                response.getWriter().println("<div class='card'>");

                response.getWriter().println("<h2>📖 Exam Timetable</h2>");

                response.getWriter().println("<table>");
                response.getWriter().println(
                        "<tr>"
                      + "<th>Subject</th>"
                      + "<th>Exam Date</th>"
                      + "<th>Time</th>"
                      + "</tr>");

                boolean hasData = false;

                while (rs.next()) {

                    hasData = true;

                    response.getWriter().println("<tr>");

                    response.getWriter().println(
                            "<td>" + rs.getString("subject") + "</td>");

                    response.getWriter().println(
                            "<td>" + rs.getString("exam_date") + "</td>");

                    response.getWriter().println(
                            "<td>" + rs.getString("time") + "</td>");

                    response.getWriter().println("</tr>");
                }

                if (!hasData) {
                    response.getWriter().println(
                            "<tr><td colspan='3'>No Exam Timetable Found</td></tr>");
                }

                response.getWriter().println("</table>");

                response.getWriter().println(
                        "<div style='text-align:center;'>"
                      + "<a href='student.jsp'>⬅ Back to Dashboard</a>"
                      + "</div>");

                response.getWriter().println("</div>");

                response.getWriter().println("</body></html>");

                rs.close();
                ps.close();
            }

            // CLASS TIMETABLE

            else if ("classTT".equals(action)) {

                PreparedStatement ps1 =
                        con.prepareStatement(
                                "SELECT class FROM student WHERE id=?");

                ps1.setInt(1, sid);

                ResultSet rs1 = ps1.executeQuery();

                String className = "";

                if (rs1.next()) {
                    className = rs1.getString("class");
                }

                PreparedStatement ps =
                        con.prepareStatement(
                                "SELECT * FROM class_timetable WHERE class=?");

                ps.setString(1, className);

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Class Timetable</title>");

                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;padding:20px;}");
                response.getWriter().println(".card{width:900px;margin:auto;background:white;padding:25px;border-radius:10px;box-shadow:0 2px 8px rgba(0,0,0,0.2);}");
                response.getWriter().println("h2{text-align:center;color:#2563eb;}");
                response.getWriter().println("table{width:100%;border-collapse:collapse;margin-top:20px;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:12px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("tr:nth-child(even){background:#f9f9f9;}");
                response.getWriter().println(".className{text-align:center;font-size:18px;font-weight:bold;color:#16a34a;margin-bottom:15px;}");
                response.getWriter().println("a{display:inline-block;margin-top:20px;text-decoration:none;color:white;background:#2563eb;padding:10px 15px;border-radius:5px;}");
                response.getWriter().println("</style>");

                response.getWriter().println("</head><body>");

                response.getWriter().println("<div class='card'>");

                response.getWriter().println("<h2>🏫 Class Timetable</h2>");

                response.getWriter().println(
                        "<div class='className'>Class : " + className + "</div>");

                response.getWriter().println("<table>");

                response.getWriter().println(
                        "<tr>"
                      + "<th>Subject</th>"
                      + "<th>Day</th>"
                      + "<th>Time</th>"
                      + "<th>Class</th>"
                      + "</tr>");

                boolean hasData = false;

                while (rs.next()) {

                    hasData = true;

                    response.getWriter().println("<tr>");

                    response.getWriter().println(
                            "<td>" + rs.getString("subject") + "</td>");

                    response.getWriter().println(
                            "<td>" + rs.getString("day") + "</td>");

                    response.getWriter().println(
                            "<td>" + rs.getString("time") + "</td>");

                    response.getWriter().println(
                            "<td>" + rs.getString("class") + "</td>");

                    response.getWriter().println("</tr>");
                }

                if (!hasData) {
                    response.getWriter().println(
                            "<tr><td colspan='4'>No Class Timetable Found</td></tr>");
                }

                response.getWriter().println("</table>");

                response.getWriter().println(
                        "<div style='text-align:center;'>"
                      + "<a href='student.jsp'>⬅ Back to Dashboard</a>"
                      + "</div>");

                response.getWriter().println("</div>");

                response.getWriter().println("</body></html>");

                rs.close();
                rs1.close();
                ps.close();
                ps1.close();
            }
            }
        catch (Exception e) {

            out.println("Error : " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }
}