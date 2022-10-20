package csci310.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import csci310.UsernameHash;
import org.json.simple.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "CreateEvent", value = "/CreateEvent")
public class CreateEvent extends HttpServlet {
    public boolean throwException = false;
    public String exceptionThrown = "";
    public String eventSelectQuery = "SELECT * FROM Events WHERE eventname = ? AND hostname = ?";
    public String createEventQuery = "INSERT INTO Events('eventname', 'hostname', 'date', 'invitees', 'accepted', 'stage') VALUES (?, ?, ?, ?, ?, ?)";
    public String userSelectQuery = "SELECT * FROM Users WHERE username = ?";
    public String userUpdateQuery = "UPDATE Users SET invited = ? WHERE username = ?";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        String eventname = request.getParameter("eventname");
        String hostname = request.getParameter("hostname");
        UsernameHash UH = new UsernameHash();
        hostname = UH.hashUsername(hostname);
        String date = request.getParameter("date");
        String invitees = request.getParameter("invitees");
//        JsonArray inviteesArray = JsonParser.parseString(invitees).getAsJsonArray();
//            System.out.println(inviteesArray.size());
        //1. create event in events table
        //2. each user invited, update their column "invited" List in Users Table
        //3. send invites to invitees
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PrintWriter out = response.getWriter();

        try{
            if(throwException){
                throw new SQLException();
            }
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");

            ps = conn.prepareStatement(eventSelectQuery);
            ps.setString(1, eventname);
            ps.setString(2, hostname);
            rs = ps.executeQuery();
            if(rs.next()){
                out.println("Event Exists");
                return;
            }else{
                //Query to Events Table
                System.out.println("CREATING NEW EVENT");
                ps = conn.prepareStatement(createEventQuery);
                ps.setString(1, eventname);
                ps.setString(2, hostname);
                ps.setString(3, date);
                ps.setString(4, invitees);
                ps.setString(5, "[]");
                ps.setString(6, "PENDING");
                ps.executeUpdate();
            }


            //Query to the Users Table
            JsonArray inviteesArray = JsonParser.parseString(invitees).getAsJsonArray();

            for(int i = 0; i < inviteesArray.size(); i++){
                String username = inviteesArray.get(i).getAsString();
                UH = new UsernameHash();
                username = UH.hashUsername(username);
                ps = conn.prepareStatement(userSelectQuery);
                ps.setString(1, username);
                rs = ps.executeQuery();
                rs.next();
                JsonArray invitedArrayForUser =JsonParser.parseString(rs.getString("invited")).getAsJsonArray();
                invitedArrayForUser.add(eventname);
                ps = conn.prepareStatement(userUpdateQuery);
                ps.setString(1, invitedArrayForUser.toString());
                ps.setString(2, username);
                ps.executeUpdate();
//
//                    JSONObject o = JsonParser.parseString(invitedArrayForUser.get(0));
//                    System.out.println("TESTING: " + o);

            }

            out.println("Sent Invites");
            
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
