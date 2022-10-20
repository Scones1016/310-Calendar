package csci310.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import csci310.UsernameHash;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "BlockUser", value = "/BlockUser")
public class BlockUser extends HttpServlet {
    public boolean throwException = false;
    public String exceptionThrown = "";
    public String query = "SELECT * FROM Users WHERE username = ?";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");

        String username = request.getParameter("username");
        String userToBlock = request.getParameter("userToBlock");
        UsernameHash UH = new UsernameHash();
        username = UH.hashUsername(username);
        userToBlock = UH.hashUsername(userToBlock);
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
            ps.setString(1, userToBlock);
            rs = ps.executeQuery();

            if(rs.next()){
                // update userToBlock's blockedBy list
                String blockedByListString = rs.getString("blockedBy");
                JsonArray blockedListBy = JsonParser.parseString(blockedByListString).getAsJsonArray();
                blockedListBy.add(username);
                blockedByListString= blockedListBy.toString();
                String updateQuery = String.format("UPDATE Users set blockedBy=? WHERE username=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, blockedByListString);
                ps.setString(2, userToBlock);
                ps.executeUpdate();
                // update username's blocked list
                ps = null;
                rs = null;
                ps = conn.prepareStatement("SELECT blocked FROM users WHERE username = ?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                rs.next();
                String blockedListString = rs.getString("blocked");
                ps = null;
                rs = null;
                JsonArray blockedList = JsonParser.parseString(blockedListString).getAsJsonArray();
                blockedList.add(userToBlock);
                blockedListString= blockedList.toString();
                updateQuery = String.format("UPDATE Users set blocked=? WHERE username=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, blockedListString);
                ps.setString(2, username);
                ps.executeUpdate();


                out.println("Blocked User");
            }else{
                out.println("Error blocking user");
            }
        }catch(SQLException sqle){
            exceptionThrown = "SQLException: " + sqle.getMessage();
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
                exceptionThrown = "SQLException: " + sqle.getMessage();
                System.out.println("sqle: " + sqle.getMessage());
            }
        }
    }
}
