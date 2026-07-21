package Students;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import Students.HtmlUtil;


import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/server")
public class server extends HttpServlet {

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;

        String role = (String) session.getAttribute("role");
        return "admin".equals(role);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,IOException {

        response.setContentType("text/html");

        if (!isAdmin(request)) {
            response.getWriter().println("<h2>Access Denied: Admin Only</h2>");
            return;
        }

        String action = request.getParameter("action");

        try {
            Connection con = Jdbc.getConnection();

            // ================= SEARCH =================
            if ("search".equals(action)) {

                String idParam = request.getParameter("id");

                response.setContentType("text/html");
                PrintWriter out = response.getWriter();

                if (idParam == null || idParam.trim().isEmpty()) {
                    out.println("<h3>Please enter a Student ID</h3>");
                    out.println("<a href='admin.jsp'>Back</a>");
                    return;
                }

                int id = Integer.parseInt(idParam);

                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM student WHERE id=?");
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                out.println("<!DOCTYPE html>");
                out.println("<html><head><title>Student Details</title>");
                out.println("<style>");
                out.println("body{font-family:Arial;background:#f4f4f4;}");
                out.println("table{width:80%;margin:auto;border-collapse:collapse;background:white;}");
                out.println("th{background:#2563eb;color:white;padding:10px;}");
                out.println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                out.println("</style></head><body>");

                out.println("<h2 style='text-align:center;'>Student Details</h2>");

                if (rs.next()) {

                    out.println("<table>");
                    out.println("<tr><th>ID</th><th>Name</th><th>Class</th><th>Age</th></tr>");

                    out.println("<tr>");
                    out.println("<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("name")) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(rs.getString("class")) + "</td>");
                    out.println("<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("age"))) + "</td>");
                    out.println("</tr>");

                    out.println("</table>");

                } else {
                    out.println("<h3 style='text-align:center;color:red;'>No Student Found</h3>");
                }

                out.println("<br><center><a href='admin.jsp'>Back</a></center>");
                out.println("</body></html>");

                rs.close();
                ps.close();
            }
              
            // ================= VIEW STUDENTS (NEW PAGE) =================
            else if ("viewPage".equals(action)) {

                ResultSet rs = con.prepareStatement(
                        "SELECT * FROM student").executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><title>Students</title>");
                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;}");
                response.getWriter().println("table{width:80%;margin:auto;border-collapse:collapse;background:white;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:10px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("</style></head><body>");

                response.getWriter().println("<h2 style='text-align:center;'>Student Details</h2>");

                response.getWriter().println("<table>");
                response.getWriter().println("<tr><th>ID</th><th>Name</th><th>Class</th><th>Age</th></tr>");

                while (rs.next()) {
                    response.getWriter().println(
                        "<tr>" +
                        "<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(rs.getString("name")) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(rs.getString("class")) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("age"))) + "</td>" +
                        "</tr>"
                    );
                }

                response.getWriter().println("</table>");
                response.getWriter().println("<br><center><a href='admin.jsp'>Back</a></center>");
                response.getWriter().println("</body></html>");
            }
           //======================view by class ===================

            // (duplicate "search" block removed — it was unreachable because the
            //  earlier search branch already handles the action. class is VARCHAR
            //  in the schema, so it must be set with setString, not setInt.)

            // ================= VIEW CLASS TT (NEW PAGE) =================
            else if ("viewClassTTPage".equals(action)) {

                String cls = request.getParameter("class");

                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM class_timetable WHERE class=?");
                ps.setString(1, cls);

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;}");
                response.getWriter().println("table{width:80%;margin:auto;border-collapse:collapse;background:white;}");
                response.getWriter().println("th{background:#16a34a;color:white;padding:10px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("</style></head><body>");

                response.getWriter().println("<h2 style='text-align:center;'>Class Timetable - " + HtmlUtil.escapeHtml(cls) + "</h2>");

                response.getWriter().println("<table>");
                
                response.getWriter().println("<tr><th>ID</th><th>Day</th><th>Subject</th><th>Time</th></tr>");

                while (rs.next()) {
                    response.getWriter().println(
                        "<tr>" +
                        "<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(rs.getString("day")) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(rs.getString("subject")) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(rs.getString("time")) + "</td>" +
                        "</tr>"
                    );
                }
                

                response.getWriter().println("</table>");
                response.getWriter().println("<br><center><a href='admin.jsp'>Back</a></center>");
                response.getWriter().println("</body></html>");
            }

            // ================= VIEW EXAM TT (NEW PAGE) =================
            else if ("viewExamTTPage".equals(action)) {

                String cls = request.getParameter("class");

                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM exam_timetable WHERE class=?");
                ps.setString(1, cls);

                ResultSet rs = ps.executeQuery();

                response.setContentType("text/html");

                response.getWriter().println("<html><head><style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;}");
                response.getWriter().println("table{width:80%;margin:auto;border-collapse:collapse;background:white;}");
                response.getWriter().println("th{background:#dc2626;color:white;padding:10px;}");
                response.getWriter().println("td{text-align:center;padding:10px;border:1px solid #ddd;}");
                response.getWriter().println("</style></head><body>");

                response.getWriter().println("<h2 style='text-align:center;'>Exam Timetable - " + HtmlUtil.escapeHtml(cls) + "</h2>");

                response.getWriter().println("<table>");
                response.getWriter().println("<tr><th>ID</th><th>Subject</th><th>Date</th><th>Time</th></tr>");

                while (rs.next()) {
                    response.getWriter().println(
                        "<tr>" +
                        "<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(rs.getString("subject")) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(rs.getDate("exam_date").toString()) + "</td>" +
                        "<td>" + HtmlUtil.escapeHtml(rs.getString("time")) + "</td>" +
                        "</tr>"
                    );
                }

                response.getWriter().println("</table>");
                response.getWriter().println("<br><center><a href='admin.jsp'>Back</a></center>");
                response.getWriter().println("</body></html>");
            }

         // ================= VIEW BY CLASS =================
            else if ("viewByClass".equals(action)) {

                String cls = request.getParameter("class");

                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM student WHERE class=?");

                ps.setString(1, cls);

                ResultSet rs = ps.executeQuery();

                response.getWriter().println("<html>");
                response.getWriter().println("<head>");

                response.getWriter().println("<style>");
                response.getWriter().println("body{font-family:Arial;background:#f4f4f4;padding:30px;}");
                response.getWriter().println("table{width:80%;margin:auto;border-collapse:collapse;background:white;}");
                response.getWriter().println("th{background:#2563eb;color:white;padding:12px;}");
                response.getWriter().println("td{padding:10px;border:1px solid #ddd;text-align:center;}");
                response.getWriter().println("h2{text-align:center;color:#2563eb;}");
                response.getWriter().println("</style>");

                response.getWriter().println("</head>");
                response.getWriter().println("<body>");

                response.getWriter().println("<h2>Students of Class : " + cls + "</h2>");

                response.getWriter().println("<table>");
                response.getWriter().println(
                        "<tr>" +
                        "<th>ID</th>" +
                        "<th>Name</th>" +
                        "<th>Class</th>" +
                        "<th>Age</th>" +
                        "</tr>");

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    response.getWriter().println(
                            "<tr>" +
                            "<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("id"))) + "</td>" +
                            "<td>" + HtmlUtil.escapeHtml(rs.getString("name")) + "</td>" +
                            "<td>" + HtmlUtil.escapeHtml(rs.getString("class")) + "</td>" +
                            "<td>" + HtmlUtil.escapeHtml(String.valueOf(rs.getInt("age"))) + "</td>" +
                            "</tr>");
                }

                response.getWriter().println("</table>");

                if (!found) {
                    response.getWriter().println(
                            "<h3 style='text-align:center;color:red;'>No Students Found</h3>");
                }

                response.getWriter().println(
                        "<br><center><a href='admin.jsp'>Back</a></center>");

                response.getWriter().println("</body>");
                response.getWriter().println("</html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        response.setContentType("text/html");

        if (!isAdmin(request)) {
            response.getWriter().println("<h2>Access Denied: Admin Only</h2>");
            return;
        }

        String action = request.getParameter("action");

        try {
            Connection con = Jdbc.getConnection();

            // ================= ADD STUDENT =================
            if ("addStudent".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO student(name,class,age,id) VALUES(?,?,?,?)");

                ps.setString(1, request.getParameter("name"));
                ps.setString(2, request.getParameter("class"));
                ps.setInt(3, Integer.parseInt(request.getParameter("age")));
                 ps.setInt(4, Integer.parseInt(request.getParameter("id")));

                ps.executeUpdate();
                
                response.setContentType("text/html");
                response.getWriter().println(
                    "<html>" +
                    "<head><title>Update Status</title></head>" +
                    "<body style='font-family: Arial; text-align:center; margin-top:100px;' >" +
                    "<h2 style='color:green;'>✅ Student Updated Successfully</h2>" +
                  
                   "<br><center><a href='admin.jsp'>Back</a></center>"+
                    "</body>" +
                    "</html>"
                );
            }

            // ================= UPDATE STUDENT =================
            else if ("updateStudent".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE student SET name=?, class=?, age=? WHERE id=?");

                ps.setString(1, request.getParameter("name"));
                ps.setString(2, request.getParameter("class"));
                ps.setInt(3, Integer.parseInt(request.getParameter("age")));
                ps.setInt(4, Integer.parseInt(request.getParameter("id")));

                ps.executeUpdate();
               
                response.setContentType("text/html");
                response.getWriter().println(
                    "<html>" +
                    "<head><title>Update Status</title></head>" +
                    "<body style='font-family: Arial; text-align:center; margin-top:100px;' >" +
                    "<h2 style='color:yellow;'>✅ Student Updated Successfully</h2>" +
                  
                   "<br><center><a href='admin.jsp'>Back</a></center>"+
                    "</body>" +
                    "</html>"
                );
            }

            // ================= DELETE STUDENT =================
            else if ("delete".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM student WHERE id=?");

                ps.setInt(1, Integer.parseInt(request.getParameter("id")));

                ps.executeUpdate();
               
                response.setContentType("text/html");
                response.getWriter().println(
                    "<html>" +
                    "<head><title>Update Status</title></head>" +
                    "<body style='font-family: Arial; text-align:center; margin-top:100px;' >" +
                    "<h2 style='color:red;'>✅ Student Deleted Successfully</h2>" +
                  
                   "<br><center><a href='admin.jsp'>Back</a></center>"+
                    "</body>" +
                    "</html>"
                );
            }

            // ================= CLASS TT =================
            else if ("createTimetable".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO  class_timetable(class, subject, day, time) VALUES(?,?,?,?)"
                );

                ps.setString(1, request.getParameter("class"));
                ps.setString(2, request.getParameter("subject"));
                ps.setString(3, request.getParameter("day"));
                ps.setString(4, request.getParameter("time"));

                ps.executeUpdate();

                response.setContentType("text/html");
                response.getWriter().println(
                    "<html>" +
                    "<head>" +
                    "<title>Success</title>" +
                    "<style>" +
                    "body{font-family:Arial;text-align:center;margin-top:100px;background:#f4f4f4;}" +
                    ".box{background:white;padding:30px;border-radius:10px;width:400px;margin:auto;" +
                    "box-shadow:0 0 10px rgba(0,0,0,0.1);}" +
                    ".btn{display:inline-block;padding:10px 20px;background:#28a745;color:white;" +
                    "text-decoration:none;border-radius:5px;margin-top:15px;}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='box'>" +
                    "<h2 style='color:green;'>✅ Timetable Created Successfully</h2>" +
                    "<a href='admin.jsp' class='btn'>Back to Admin Panel</a>" +
                    "</div>" +
                    "</body>" +
                    "</html>"
                );
            }
            
            
            
            else if ("updateClassTT".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE class_timetable SET subject=?, day=?, time=? WHERE id=?");

                ps.setString(1, request.getParameter("subject"));
                ps.setString(2, request.getParameter("day"));
                ps.setString(3, request.getParameter("time"));
                ps.setInt(4, Integer.parseInt(request.getParameter("id")));

                ps.executeUpdate();
                 response.setContentType("text/html");
                response.getWriter().println(
                        "<html>" +
                        "<head><title>Update Status</title></head>" +
                        "<body style='font-family: Arial; text-align:center; margin-top:100px;' >" +
                        "<h2 style='color:green;'>✅ Updated Successfully</h2>" +
                      
                       "<br><center><a href='admin.jsp'>Back</a></center>"+
                        "</body>" +
                        "</html>"
                    );
            }

            else if ("deleteClassTT".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM class_timetable WHERE id=?");

                ps.setInt(1, Integer.parseInt(request.getParameter("id")));

                ps.executeUpdate();
                response.setContentType("text/html");
                response.getWriter().println(
                        "<html>" +
                        "<head><title>Update Status</title></head>" +
                        "<body style='font-family: Arial; text-align:center; margin-top:100px;' >" +
                        "<h2 style='color:green;'>✅ Deleted Successfully</h2>" +
                      
                       "<br><center><a href='admin.jsp'>Back</a></center>"+
                        "</body>" +
                        "</html>"
                    );
            }

            // ================= EXAM TT =================
            
            else if ("createExam".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO  exam_timetable(class, subject, exam_date, time) VALUES(?,?,?,?)"
                );

                ps.setString(1, request.getParameter("class"));
                ps.setString(2, request.getParameter("subject"));
                ps.setString(3, request.getParameter("exam_date"));
                ps.setString(4, request.getParameter("time"));

                ps.executeUpdate();

                response.setContentType("text/html");
                response.getWriter().println(
                    "<html>" +
                    "<head>" +
                    "<title>Success</title>" +
                    "<style>" +
                    "body{font-family:Arial;text-align:center;margin-top:100px;background:#f4f4f4;}" +
                    ".box{background:white;padding:30px;border-radius:10px;width:400px;margin:auto;" +
                    "box-shadow:0 0 10px rgba(0,0,0,0.1);}" +
                    ".btn{display:inline-block;padding:10px 20px;background:#28a745;color:white;" +
                    "text-decoration:none;border-radius:5px;margin-top:15px;}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='box'>" +
                    "<h2 style='color:green;'>✅ Timetable Created Successfully</h2>" +
                    "<a href='admin.jsp' class='btn'>Back to Admin Panel</a>" +
                    "</div>" +
                    "</body>" +
                    "</html>"
                );
            }
            else if ("updateExamTT".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE exam_timetable SET subject=?, exam_date=?, time=? WHERE id=?");

                ps.setString(1, request.getParameter("subject"));
                ps.setString(2, request.getParameter("date"));
                ps.setString(3, request.getParameter("time"));
                ps.setInt(4, Integer.parseInt(request.getParameter("id")));

                ps.executeUpdate();
                response.setContentType("text/html");
                response.getWriter().println(
                        "<html>" +
                        "<head><title>Update Status</title></head>" +
                        "<body style='font-family: Arial; text-align:center; margin-top:100px;' >" +
                        "<h2 style='color:green;'>✅ Created Successfully </h2>" +
                      
                       "<br><center><a href='admin.jsp'>Back</a></center>"+
                        "</body>" +
                        "</html>"
                    );
            }

            else if ("deleteExamTT".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM exam_timetable WHERE id=?");

                ps.setInt(1, Integer.parseInt(request.getParameter("id")));

                ps.executeUpdate();
                response.getWriter().println("Exam Deleted");
            }
            else if ("logout".equals(action)) {
                System.out.println("Logout called");

                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                response.sendRedirect("login.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}