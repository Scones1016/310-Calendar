package csci310.servlets;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import csci310.UsernameHash;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "GetInvited", value = "/GetInvited")
public class GetInvited extends HttpServlet {
    public String query = "SELECT invited FROM users WHERE username = ?";
    public String caughtException = "";
    public Boolean throwException = false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        String username = request.getParameter("username");
        UsernameHash UH = new UsernameHash();
        username = UH.hashUsername(username);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PrintWriter out = response.getWriter();

        try{
            if(throwException){
                throw new SQLException();
            }
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            String invitedList = rs.getString("invited");

            response.setContentType("application/json");
//            String invitedJson = new Gson().toJson(invitedList);
            out.println(invitedList);

        }catch(SQLException sqle){
            caughtException = "SQLException: " + sqle.getMessage();
            System.out.println("SQLException: " + sqle.getMessage());
        }finally {
            try {
                if(rs != null){
                    rs.close();
                }
                if(ps != null){
                    ps.close();
                }
                if(conn != null){
                    conn.close();
                }
                if(throwException){
                    throw new SQLException();
                }
            } catch (SQLException sqle) {
                caughtException = "SQLException: " + sqle.getMessage();
                System.out.println("sqle: " + sqle.getMessage());
            }
        }
    }
}
