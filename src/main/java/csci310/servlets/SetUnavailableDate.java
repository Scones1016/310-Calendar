package csci310.servlets;

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

@WebServlet(name = "SetUnavailableDate", value = "/SetUnavailableDate")
public class SetUnavailableDate extends HttpServlet {
    public boolean throwException = false;
    public String exceptionThrown = "";
    public String query = "SELECT * FROM Users WHERE username = ?";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");

        String username = request.getParameter("username");
        UsernameHash UH = new UsernameHash();
        username = UH.hashUsername(username);
        String unavailableDates = request.getParameter("unavailableDates");
        JsonArray requestUnavailableList = JsonParser.parseString(unavailableDates).getAsJsonArray();
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

            if(rs.next()){
                // update userToBlock's blockedBy list
                String unavailableDatesListString = rs.getString("unavailableDates");
                JsonArray unavailableDatesList = JsonParser.parseString(unavailableDatesListString).getAsJsonArray();
                // go through unavailableDates request and add each one to this list
                for(JsonElement JE : requestUnavailableList){
                    String date = JE.getAsString();
                    unavailableDatesList.add(date);
                }
                unavailableDatesListString= unavailableDatesList.toString();
                String updateQuery = String.format("UPDATE Users set unavailableDates=? WHERE username=?");
                ps = conn.prepareStatement(updateQuery);
                ps.setString(1, unavailableDatesListString);
                ps.setString(2, username);
                ps.executeUpdate();
                out.println("Updated unavailable dates");
            }else{
                out.println("Error Updating unavailable dates");
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
