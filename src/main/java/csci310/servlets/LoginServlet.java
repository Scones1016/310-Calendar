package csci310.servlets;

import csci310.PasswordHash;
import csci310.UsernameHash;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public String caughtException = "";
    public Boolean throwSQLException = false;
    public Boolean throwIOException = false;
    public Boolean throwClassNotFoundException = false;
    public Boolean throwNoSuchAlgorithmException = false;
    public Boolean throwNullPointerException = false;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        System.out.println("in login Servlet");

        Connection conn = null;
        Statement st = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            PrintWriter out = response.getWriter();
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            UsernameHash UH = new UsernameHash();
            PasswordHash h = new PasswordHash();
            String hash = h.getSHA(password);
            username = UH.hashUsername(username);
            st = conn.createStatement();
            String cmd = String.format("SELECT * FROM Users WHERE username=? AND password=?;");
            ps = conn.prepareStatement(cmd);
            ps.setString(1, username); // set first variable in prepared statement
            ps.setString(2, hash); // set first variable in prepared statement
            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("no such login credential: " + username);
                out.print("");
            } else {
                out.println("{");
                out.println("\"token\" : \"" + hash + "\"");
                out.println("}");
            }
            if (throwSQLException) {
                throw new SQLException();
            } else if (throwIOException) {
                throw new IOException();
            } else if (throwClassNotFoundException) {
                throw new ClassNotFoundException();
            } else if (throwNoSuchAlgorithmException) {
                throw new NoSuchAlgorithmException();
            }
        }
        catch (SQLException e) {
            caughtException = "SQLException";
            System.out.println("SQLException: " + e.getMessage());
        } catch (IOException e) {
            caughtException = "IOException";
            System.out.println("IOException: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            caughtException = "ClassNotFoundException";
            System.out.println("ClassNotFoundException: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            caughtException = "NoSuchAlgorithmException";
            System.out.println("NoSuchAlgorithmException: " + e.getMessage());
        } finally {
            try {
                rs.close();
                st.close();
                ps.close();
                conn.close();

                if (throwSQLException) {
                    throw new SQLException();
                } if (throwNullPointerException) {
                    throw new NullPointerException();
                }
            } catch (SQLException e) {
                caughtException = "SQLException";
                System.out.println("SQLException: " + e.getMessage());
            } catch (NullPointerException e) {
                caughtException = "NullPointerException";
                System.out.println("NullPointerException: " + e.getMessage());
            }
        }
    }
}
