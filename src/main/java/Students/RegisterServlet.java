package Students;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        // Hash the password before storing
        String hashedPassword = PasswordUtil.hashPassword(password);

        try {

            Connection con = Jdbc.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO user(username, password, role) VALUES(?, ?, ?)"
            );

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, role);

            int i = ps.executeUpdate();

            if (i > 0) {
                response.sendRedirect("login.jsp");
            } else {
                response.getWriter().println("Registration Failed");
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h2>Registration failed. Please try again.</h2>");
        }
    }
}