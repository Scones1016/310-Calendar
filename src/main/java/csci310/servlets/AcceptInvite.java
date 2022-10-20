package csci310.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import csci310.UsernameHash;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "AcceptInvite", value = "/AcceptInvite")
public class AcceptInvite extends HttpServlet {
    public String query = "";
    public boolean throwException = false;
    public String exceptionThrown = "";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        String eventname = request.getParameter("eventname");
        String accept = request.getParameter("accept");
        String preferences = request.getParameter("preferences");
        String username = request.getParameter("username");
        UsernameHash UH = new UsernameHash();
        username = UH.hashUsername(username);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PrintWriter out = response.getWriter();
        // add username to declined list in event table
        try{
            if(throwException){
                throw new SQLException();
            }
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            query = "SELECT * FROM Events WHERE eventname = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, eventname);
            rs = ps.executeQuery();
            rs.next();
            if(accept.equals("no")){
                String declinedListString = rs.getString("declined");
                JsonArray declinedList = JsonParser.parseString(declinedListString).getAsJsonArray();
                declinedList.add(username);
                declinedListString= declinedList.toString();
                String updateQuery = String.format("UPDATE Events set declined=? WHERE eventname=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, declinedListString);
                ps.setString(2, eventname);
                ps.executeUpdate();
                out.println("Declined event invite");
            // add username to accepted list and add preferences to preferences list
            }else{
                String acceptedListString = rs.getString("accepted");
                JsonArray acceptedList = JsonParser.parseString(acceptedListString).getAsJsonArray();
                acceptedList.add(username);
                acceptedListString= acceptedList.toString();
                String updateQuery = String.format("UPDATE Events set accepted=? WHERE eventname=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, acceptedListString);
                ps.setString(2, eventname);
                ps.executeUpdate();

                String preferencesListString = rs.getString("preferences");
                JsonArray preferencesList = JsonParser.parseString(preferencesListString).getAsJsonArray();
                preferencesList.add(preferences);
                preferencesListString= preferencesList.toString();
                updateQuery = String.format("UPDATE Events set preferences=? WHERE eventname=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, preferencesListString);
                ps.setString(2, eventname);
                ps.executeUpdate();
                out.println("Accepted event invite");
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
