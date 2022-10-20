package csci310.servlets;

import com.google.gson.JsonObject;
import csci310.EventAPI;
import csci310.EventDetailsHelper;

/*import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;*/
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import javax.servlet.ServletException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

@WebServlet(name = "GetEvents", value = "/GetEvents")
public class GetEvents extends HttpServlet {
    public String caughtException = "";
    public Boolean throwException = false;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        String eventKeyword = request.getParameter("eventInput");
        String eventLocation = request.getParameter("eventLocation");
        String eventStart = request.getParameter("startDate");
        String eventEnd = request.getParameter("endDate");
        PrintWriter out = response.getWriter();
        EventAPI eventAPI = new EventAPI();

        JsonObject eventList = new JsonObject();
        eventList = eventAPI.getEventAPI(eventKeyword, eventLocation, eventStart, eventEnd);
        out.println(eventList);
    }
}
