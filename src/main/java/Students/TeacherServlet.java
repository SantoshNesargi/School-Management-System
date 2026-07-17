package Students;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import Students.HtmlUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/teacher")
public class TeacherServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {

            // ================= LOGIN =================
            if ("login".equals(action)) {

                String user = request.getParameter("username");
                String pass = request.getParameter("password");

                if ("teacher".equals(user) && "123".equals(pass)) {
                    out.println("<h2>Teacher Login Success</h2>");
                    out.println("<a href='teacher.jsp'>Dashboard</a>");
                } else {
                    out.println("<h3>Invalid Login</h3>");
                }
            }

            // ================= VIEW STUDENTS =================
            else if ("viewStudents".equals(action)) {

                String className = request.getParameter("class");

                Connection con = Jdbc.getConnection();

                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM student WHERE class=?"
                );

                ps.setString(1, className);

                ResultSet rs = ps.executeQuery();


               
                response.setContentType("text/html");
                response.getWriter().println("<center><a href='teacher.jsp'>Back</a></center>");
                response.getWriter().println("<html><head><title>Students</title>");
                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;}");
                response.getWriter().println("table{width:80%;margin:auto;border-collapse:collapse;background:white;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:10px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("</style></head><body>");

                response.getWriter().println("<h2 style='text-align:center;'>Student Class Details " + HtmlUtil.escapeHtml(className) +"</h2>");
               

                response.getWriter().println("<table>");
                response.getWriter().println("<tr><th>ID</th><th>Name</th><th>Class</th><th>Age</th></tr>");
              

                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("name")) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("class")) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("age")) + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
                con.close();
            }

            // ================= VIEW EXAM TT =================
            else if ("viewExamTT".equals(action)) {

                String className = request.getParameter("class");

                Connection con = Jdbc.getConnection();

                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM exam_timetable WHERE class=?"
                );

                ps.setString(1, className);

                ResultSet rs = ps.executeQuery();

         
                
                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Students</title>");
                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;}");
                response.getWriter().println("table{width:80%;margin:auto;border-collapse:collapse;background:white;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:10px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("</style></head><body>");

                response.getWriter().println("<h2 style='text-align:center;'>Exam Timetable -  " + HtmlUtil.escapeHtml(className) +"</h2>");

                response.getWriter().println("<table>");
                response.getWriter().println("<tr><th>ID</th><th>Subject</th><th>Date</th><th>Time</th></tr>");


                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("subject")) + "</td>");
                    out.println("<td>" + rs.getDate("exam_date") + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("time")) + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
                con.close();
            }

            // ================= VIEW CLASS TT =================
            else if ("viewClassTT".equals(action)) {

                Connection con = Jdbc.getConnection();

                String className = request.getParameter("class");

                if (className == null || className.trim().isEmpty()) {
                    out.println("Please enter class name");
                    return;
                }

                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM class_timetable WHERE class=?"
                );

                ps.setString(1, className);   // ✅ IMPORTANT FIX

                ResultSet rs = ps.executeQuery();


                
                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Students</title>");
                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;}");
                response.getWriter().println("table{width:80%;margin:auto;border-collapse:collapse;background:white;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:10px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("</style></head><body>");

                response.getWriter().println("<h2 style='text-align:center;'>Class Timetable -  " + HtmlUtil.escapeHtml(className) +"</h2>");

                response.getWriter().println("<table>");
                response.getWriter().println("<tr><th>ID</th><th>Class</th><th>Subject</th><th>Day</th><th>Time</th></tr>");

                
                

            

                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("class")) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("subject")) + "</td>");
                    out.println("<td>" + rs.getString("day") + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("time")) + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
                con.close();
            }

            // ================= ADD MARKS =================
            else if ("addMarks".equals(action)) {

                try {
                    Connection con = Jdbc.getConnection();

                    int sid = Integer.parseInt(request.getParameter("student_id"));
                    String subject = request.getParameter("subject");
                    int marks = Integer.parseInt(request.getParameter("marks"));

                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO marks(student_id, subject, marks) VALUES (?, ?, ?)"
                    );

                    ps.setInt(1, sid);
                    ps.setString(2, subject);
                    ps.setInt(3, marks);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        out.println("<h3 style='color:green;'>Marks Added Successfully</h3>");
                    } else {
                        out.println("<h3 style='color:red;'>Insert Failed</h3>");
                    }

                    con.close();

                } catch (Exception e) {
                    out.println("<h3 style='color:red;'>Error: " + HtmlUtil.escapeHtml(e.getMessage()) + "</h3>");
                }
            }
            // ================= VIEW MARKS =================
            else if ("viewMarks".equals(action)) {

                Connection con = Jdbc.getConnection();

                PreparedStatement ps = con.prepareStatement(
                    "SELECT m.id, s.name, s.class, m.subject, m.marks " +
                    "FROM marks m " +
                    "JOIN student s ON m.student_id = s.id"
                );

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Marks Details</title>");

                out.println("<style>");
                out.println("body{font-family:Arial,sans-serif;background:#f4f6f9;margin:0;padding:20px;}");
                out.println(".container{width:90%;margin:auto;}");
                out.println(".card{background:white;padding:25px;border-radius:10px;box-shadow:0 2px 8px rgba(0,0,0,0.15);}");
                out.println("h2{text-align:center;color:#2563eb;margin-bottom:20px;}");
                out.println("table{width:100%;border-collapse:collapse;}");
                out.println("th{background:#2563eb;color:white;padding:12px;}");
                out.println("td{padding:10px;border:1px solid #ddd;text-align:center;}");
                out.println("tr:nth-child(even){background:#f9fafb;}");
                out.println("tr:hover{background:#eef2ff;}");
                out.println(".back{display:inline-block;margin-top:20px;padding:10px 15px;background:#16a34a;color:white;text-decoration:none;border-radius:5px;}");
                out.println("</style>");

                out.println("</head>");
                out.println("<body>");

                out.println("<div class='container'>");
                out.println("<div class='card'>");

                out.println("<h2>📝 Marks Details</h2>");

                out.println("<table>");

                out.println("<tr>");
                out.println("<th>ID</th>");
                out.println("<th>Name</th>");
                out.println("<th>Class</th>");
                out.println("<th>Subject</th>");
                out.println("<th>Marks</th>");
                out.println("</tr>");

                boolean hasData = false;

                while (rs.next()) {

                    hasData = true;

                    out.println("<tr>");

                    out.println("<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("name")) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("class")) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("subject")) + "</td>");
                    out.println("<td>" + rs.getInt("marks") + "</td>");

                    out.println("</tr>");
                }

                if (!hasData) {
                    out.println("<tr>");
                    out.println("<td colspan='5'>No Marks Found</td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                out.println("<div style='text-align:center;'>");
                out.println("<a href='teacher.jsp' class='back'>⬅ Back to Dashboard</a>");
                out.println("</div>");

                out.println("</div>");
                out.println("</div>");

                out.println("</body>");
                out.println("</html>");

                rs.close();
                ps.close();
                con.close();
            }
            // ================= UPDATE MARKS =================
            else if ("updateMarks".equals(action)) {

                Connection con = Jdbc.getConnection();

                int id = Integer.parseInt(request.getParameter("id"));
                int marks = Integer.parseInt(request.getParameter("marks"));

                PreparedStatement ps = con.prepareStatement(
                    "UPDATE marks SET marks=? WHERE id=?"
                );

                ps.setInt(1, marks);
                ps.setInt(2, id);

                ps.executeUpdate();

                out.println("Marks Updated");

                con.close();
            }

            // ================= DELETE MARKS =================
            else if ("deleteMarks".equals(action)) {

                Connection con = Jdbc.getConnection();

                int id = Integer.parseInt(request.getParameter("id"));

                PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM marks WHERE id=?"
                );

                ps.setInt(1, id);
                ps.executeUpdate();

                out.println("Marks Deleted");

                con.close();
            }

            // ================= ADD ATTENDANCE =================
            else if ("addAttendance".equals(action)) {

                Connection con = Jdbc.getConnection();

                int sid = Integer.parseInt(request.getParameter("student_id"));
                String className = request.getParameter("class_name");
                String date = request.getParameter("date");
                String status = request.getParameter("status");

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO attendance(student_id,class_name,attendance_date,status) VALUES(?,?,?,?)"
                );

                ps.setInt(1, sid);
                ps.setString(2, className);
                ps.setString(3, date);
                ps.setString(4, status);

                ps.executeUpdate();

                out.println("<h3 style='color:green;'>Attendance Added</h3>");

                con.close();
            }

       

            // ================= ATTENDANCE SUMMARY =================
            else if ("attendanceSummary".equals(action)) {

                Connection con = Jdbc.getConnection();

                int sid = Integer.parseInt(request.getParameter("student_id"));

                PreparedStatement ps = con.prepareStatement(
                    "SELECT " +
                    "SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) AS present_days, " +
                    "COUNT(*) AS total_days, " +
                    "(SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) AS percentage " +
                    "FROM attendance WHERE student_id=?"
                );

                ps.setInt(1, sid);

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Attendance Summary</title>");

                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;padding:20px;}");
                response.getWriter().println(".card{width:500px;margin:auto;background:white;padding:20px;border-radius:10px;box-shadow:0 2px 8px rgba(0,0,0,0.2);}");
                response.getWriter().println("h2{text-align:center;color:#2563eb;}");
                response.getWriter().println("table{width:100%;border-collapse:collapse;margin-top:20px;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:10px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("</style>");

                response.getWriter().println("</head><body>");

                response.getWriter().println("<div class='card'>");
                response.getWriter().println("<h2>📅 Attendance Summary</h2>");

                if (rs.next()) {

                    response.getWriter().println("<table>");
                    response.getWriter().println("<tr><th>Student ID</th><th>Present Days</th><th>Total Days</th><th>Percentage</th></tr>");

                    response.getWriter().println("<tr>");
                    response.getWriter().println("<td>" + HtmlUtil.escapeHtml(String.valueOf(sid)) + "</td>");
                    response.getWriter().println("<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("present_days"))) + "</td>");
                    response.getWriter().println("<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("total_days"))) + "</td>");
                    response.getWriter().println("<td>" + HtmlUtil.escapeHtml(String.format("%.2f", rs.getDouble("percentage"))) + "%</td>");
                    response.getWriter().println("</tr>");

                    response.getWriter().println("</table>");
                }

                response.getWriter().println("<br><div style='text-align:center;'>");
                response.getWriter().println("<a href='teacher.jsp'>⬅ Back to Dashboard</a>");
                response.getWriter().println("</div>");

                response.getWriter().println("</div>");
                response.getWriter().println("</body></html>");

                rs.close();
                ps.close();
                con.close();
            }
            
            // to get auto metic name ================
            else if ("getStudentName".equals(action)) {

                Connection con = Jdbc.getConnection();

                int sid = Integer.parseInt(request.getParameter("student_id"));

                PreparedStatement ps = con.prepareStatement(
                    "SELECT name FROM student WHERE id=?"
                );

                ps.setInt(1, sid);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    out.println(rs.getString("name"));
                } else {
                    out.println("Not Found");
                }

                con.close();
            }

        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}