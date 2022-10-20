package csci310.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import csci310.UsernameHash;
import org.openqa.selenium.json.Json;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "DeletePGD", value = "/DeletePGD")
public class DeletePGD extends HttpServlet {
    public boolean throwException = false;
    public String exceptionThrown = "";
    public String query = "SELECT * FROM Events WHERE eventname = ?";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        String eventToRemove = request.getParameter("eventToRemove");

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
            ps.setString(1, eventToRemove);
            rs = ps.executeQuery();
            rs.next();
            String invited = rs.getString("invitees");
            JsonArray invitedList = JsonParser.parseString(invited).getAsJsonArray();
            // remove the eventname from every invited user's accepted/invited columns
            for(JsonElement JE : invitedList){
                rs = null;
                String invitee = JE.getAsString();
                query = "SELECT * FROM Users WHERE username = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, invitee);
                rs = ps.executeQuery();
                rs.next();
                String invitedEvents = rs.getString("invited");
                JsonArray invitedEventlist = JsonParser.parseString(invitedEvents).getAsJsonArray();
                JsonArray newInvitedEventList = new JsonArray();
                int i = 0;
                System.out.println(1);
                for(JsonElement item : invitedEventlist){
                    String event = item.getAsString();
                    if(!event.equals(eventToRemove)){
                        newInvitedEventList.add(event);
                    }
                    i++;
                }
                String acceptedEvents = rs.getString("accepted");
                JsonArray acceptedEventlist = JsonParser.parseString(acceptedEvents).getAsJsonArray();
                JsonArray newAcceptedEventList = new JsonArray();
                i = 0;
                for(JsonElement item : acceptedEventlist){
                    String event = item.getAsString();
                    if(!event.equals(eventToRemove)){
                        newAcceptedEventList.add(event);
                    }
                    i++;
                }
                invitedEvents = newInvitedEventList.toString();
                acceptedEvents = newAcceptedEventList.toString();
                String updateQuery = String.format("UPDATE Users set invited=?, accepted=? WHERE username=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, invitedEvents);
                ps.setString(2, acceptedEvents);
                ps.setString(3, invitee);
                ps.executeUpdate();
            }
            String updateQuery = String.format("DELETE FROM Events WHERE eventname = ?");
            ps = conn.prepareStatement(updateQuery);
            ps.setString(1, eventToRemove);
            ps.executeUpdate();
            out.println("Successfully removed PGD");

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
