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

@WebServlet(name = "RemoveUnavailableDates", value = "/RemoveUnavailableDates")
public class RemoveUnavailableDates extends HttpServlet {
    public boolean throwException = false;
    public String exceptionThrown = "";
    public String query = "SELECT unavailableDates FROM users WHERE username = ?";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        UsernameHash UH = new UsernameHash();
        username = UH.hashUsername(username);
        String dateToRemove = request.getParameter("unavailableDates");

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
                String unavailableDatesString = rs.getString("unavailableDates");
                JsonArray unavailableDates = JsonParser.parseString(unavailableDatesString).getAsJsonArray();
                int i = 0;
                boolean found = false;
                for(JsonElement JE : unavailableDates){
                    String unavailableDate = JE.getAsString();
                    System.out.println(unavailableDate);
                    if(unavailableDate.equals(dateToRemove)){
                        unavailableDates.remove(i);
                        found = true;
                        break;
                    }
                    i++;
                }
                if(found){
                    unavailableDatesString = unavailableDates.toString();
                    String updateQuery = String.format("UPDATE Users set unavailableDates=? WHERE username=?");
                    ps = conn.prepareStatement(updateQuery);
                    ps.setString(1, unavailableDatesString);
                    ps.setString(2, username);
                    ps.executeUpdate();
                    out.println("Removed Unavailabledates");
                }else{
                    out.println("Date is not set as an unavailabledate");
                }
            }else{
                out.println("Error removing Unavailabledate");
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
