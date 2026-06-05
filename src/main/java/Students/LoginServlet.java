package Students;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Connection con = Jdbc.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM user WHERE username=? AND password=?"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String dbRole = rs.getString("role");

                HttpSession session = request.getSession();
                session.setAttribute("user", username);
                session.setAttribute("role", dbRole);
          

                if ("ADMIN".equalsIgnoreCase(dbRole)) {
                    response.sendRedirect("admin.jsp");
                }
                else if ("TEACHER".equalsIgnoreCase(dbRole)) {
                    response.sendRedirect("teacher.jsp");
                }
                else if ("STUDENT".equalsIgnoreCase(dbRole)) {
                    response.sendRedirect("student.jsp");
                }
                else {
                    response.getWriter().println("ROLE NOT FOUND IN DB: " + dbRole);
                }

            } else {
                response.getWriter().println("Invalid username or password");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}