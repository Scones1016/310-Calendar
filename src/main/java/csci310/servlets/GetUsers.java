package csci310.servlets;

import com.google.gson.Gson;
//import com.sun.org.apache.xpath.internal.operations.Bool;
import csci310.UsernameHash;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "getUsersServlet", value = "/getUsersServlet")
public class GetUsers extends HttpServlet {
    public String query = "SELECT username FROM users";
    public String caughtException = "";
    public Boolean throwException = false;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PrintWriter out = response.getWriter();

        try{
            if(throwException){
                throw new SQLException();
            }
            ArrayList usernameList = new ArrayList<>();
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            UsernameHash UH = new UsernameHash();
            while (rs.next()){
                String username = rs.getString("username");
                usernameList.add(UH.unHashUsername(username));
            }
            response.setContentType("application/json");
            String usernamesJson = new Gson().toJson(usernameList);
            out.println(usernamesJson);

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
                caughtException = "sqle: null";
                System.out.println("sqle: " + sqle.getMessage());
            }
        }
    }
}
