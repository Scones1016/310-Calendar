package csci310.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import csci310.UsernameHash;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "GetBlockedUsers", value = "/GetBlockedUsers")
public class GetBlockedUsers extends HttpServlet {
    public String caughtException = "";
    public Boolean throwException = false;
    public String query = "SELECT * from Users WHERE username = ?";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        String username = request.getParameter("username");
        UsernameHash UH = new UsernameHash();
        username = UH.hashUsername(username);
        String choice = request.getParameter("blockChoice");

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
            if (rs.next()){
                String stringBlockedByList = "";
                if(choice.equals("blockedBy")){
                    stringBlockedByList = rs.getString("blockedBy");
                }else{
                    stringBlockedByList = rs.getString("blocked");
                }
                JsonArray blockedList = JsonParser.parseString(stringBlockedByList).getAsJsonArray();
                JsonArray unHashedBlockedList = new JsonArray();
                for(JsonElement JE : blockedList){
                    String name = JE.getAsString();
                    unHashedBlockedList.add(UH.unHashUsername(name));
                }
                out.println(unHashedBlockedList.toString());
            }else{
                out.println("[\"invalid\"]");
            }
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
