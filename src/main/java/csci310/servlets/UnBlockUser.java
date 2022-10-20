package csci310.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import csci310.UsernameHash;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "UnBlockUser", value = "/UnBlockUser")
public class UnBlockUser extends HttpServlet {
    public boolean throwException = false;
    public String exceptionThrown = "";
    public String query = "SELECT blockedBy FROM users WHERE username = ?";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        UsernameHash UH = new UsernameHash();
        username = UH.hashUsername(username);
        String userToUnBlock = request.getParameter("userToUnBlock");
        userToUnBlock = UH.hashUsername(userToUnBlock);
        System.out.println("username: " + username);
        System.out.println("userToUnBlock: " + userToUnBlock);
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
            ps.setString(1, userToUnBlock);
            rs = ps.executeQuery();
            if(rs.next()){
                String blockedByListString = rs.getString("blockedBy");
                JsonArray blockedByList = JsonParser.parseString(blockedByListString).getAsJsonArray();
                int i = 0;
                boolean found = false;
                for(JsonElement JE : blockedByList){
                    String blockedBy = JE.getAsString();
                    if(blockedBy.equals(username)){
                        blockedByList.remove(i);
                        found = true;
                        break;
                    }
                    i++;
                }

                blockedByListString = blockedByList.toString();
                String updateQuery = String.format("UPDATE Users set blockedBy=? WHERE username=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, blockedByListString);
                ps.setString(2, userToUnBlock);
                ps.executeUpdate();
                ps = null;
                ps = conn.prepareStatement("SELECT blocked FROM users WHERE username = ?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                rs.next();
                String blockedListString = rs.getString("blocked");
                JsonArray blockedList = JsonParser.parseString(blockedListString).getAsJsonArray();
                i = 0;
                for(JsonElement JE : blockedList){
                    String blocked = JE.getAsString();
                    if(blocked.equals(userToUnBlock)){
                        blockedList.remove(i);
                        found = true;
                        break;
                    }
                    i++;
                }
                blockedListString = blockedList.toString();
                ps = null;
                updateQuery = String.format("UPDATE Users set blocked=? WHERE username=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, blockedListString);
                ps.setString(2, username);
                ps.executeUpdate();
                if(!found){
                    out.println("Error unblocking user");
                    return;
                }
                out.println("UnBlocked User");
            }else{
                out.println("Error unblocking user");
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