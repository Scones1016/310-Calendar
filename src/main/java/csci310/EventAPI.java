package csci310;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import com.google.gson.*;

public class EventAPI {
    public JsonObject getEventAPI(String keyword, String location, String startDate, String endDate){
        String urlString = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=df2ZhOwyQfgIkOau7zypZ25GTQfro8vC";
        String params = "&keyword=" + keyword + "&city=" + location + "&localStartDateTime =" + startDate + "T00:00:00Z" + "&localStartEndDateTime =" + endDate + "T00:00:00Z";
        String apiKey = "df2ZhOwyQfgIkOau7zypZ25GTQfro8vC";
        int count = 0;
        String output = "";
        String json = "";
        Vector<EventDetailsHelper> eventsList = new Vector<EventDetailsHelper>();
        JsonObject embedded = new JsonObject();
        try {
            URL url = new URL(urlString + params);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setAllowUserInteraction(false);
            connection.connect();

            int httpResult = connection.getResponseCode();
            if(httpResult == HttpURLConnection.HTTP_OK) {
                Gson g = new GsonBuilder().setPrettyPrinting().create();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                //Store price information for that company/date in API
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) !=null) {
                    sb.append(line);
                }
                output = sb.toString();
                br.close();
                //System.out.println("Output:\n" + output);

                JsonObject convertedObject = new Gson().fromJson(output, JsonObject.class);

                //System.out.println(convertedObject.get("_embedded"));

                embedded = (JsonObject) convertedObject.get("_embedded");
                return embedded;
                //JsonElement links = convertedObject.get("_links");
                /*System.out.println(embedded.size());
                JsonArray events = (JsonArray) embedded.get("events");
                //System.out.println(embedded);
                for(int i = 0; i < events.size(); i++){
                    count++;

                    JsonObject eventDetails = (JsonObject) events.get(i);

                    JsonObject dates = (JsonObject) eventDetails.get("dates");

                    JsonObject dateDetails = (JsonObject) dates.get("start");

                    JsonObject embedded2 = (JsonObject) eventDetails.get("_embedded");
                    JsonArray venues = (JsonArray) embedded2.get("venues");
                    JsonObject venueDetails = (JsonObject) venues.get(0);

                    String date = dateDetails.get("localDate").getAsString();
                    String name = eventDetails.get("name").getAsString();
                    String time = dateDetails.get("localTime").getAsString();
                    String venue = venueDetails.get("name").getAsString();*/

                    /*System.out.println(name);
                    System.out.println(date);
                    System.out.println(time);
                    System.out.println(venue);*/


                    /*EventDetailsHelper temp = new EventDetailsHelper(name, date, time, venue);
                    eventsList.add(temp);
                    if(eventsList.size() >= 5)
                    {
                        return eventsList;
                    }
                }

                for(EventDetailsHelper event : eventsList){
                    System.out.println(event.name + "\n" + event.venueName + "\n");
                }

                System.out.print("Count: " + count);*/
            }


        } catch(MalformedURLException m){
            System.out.println("API error");
        } catch (IOException ie){
            System.out.println("API Error");

        }
        return embedded;

    }


}
