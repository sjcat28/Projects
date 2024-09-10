import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


/*
This class is for Getting the data of the weather based on the location
Retrivting data using API for geolocation and weather
this will modify data in the GUI to change to what the current weather is
*/
public class WeatherAppData {
    /**
     * fetches the weather data from a given location
     * @param locationString
     * @return
     */
    public static JSONObject getWeatherData(String locationString){
        //get coordinates from the given location
        JSONArray locationArray = getLocationData(locationString);
        JSONObject locationData = (JSONObject) locationArray.getFirst();

        double longitude = (double) locationData.get("longitude");
        double latitude = (double) locationData.get("latitude");

        //creating URL for API request
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Pacific%2FAuckland";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if(conn.getResponseCode() != 200){
                //if the response code is an error in connection return null and say WHOOPSIES
                System.out.println("WHOOPSIES");
                return null;
            }

            //storing the things gotten from API
            StringBuilder jsonResult = new StringBuilder();
            Scanner sc = new Scanner(conn.getInputStream());

            //reading the results and storing them in our
            while(sc.hasNext()){
                jsonResult.append(sc.nextLine());
            }

            //closing scanner and connection
            sc.close();
            conn.disconnect();

            //parsing into a JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultObject = (JSONObject) parser.parse(String.valueOf(jsonResult));

            //getting the hourly data
            JSONObject hourlyData = (JSONObject) resultObject.get("hourly");

            //getting the index of the current time
            JSONArray hours = (JSONArray) hourlyData.get("time");
            int index = findIndexOfCurrentTime(hours);

            //getting temperature
            JSONArray tempsData = (JSONArray) hourlyData.get("temperature_2m");
            double currentTemp = (double) tempsData.get(index);

            //getting weatherCode
            JSONArray weatherCodes = (JSONArray) hourlyData.get("weather_code");
            Long weatherCode = (long) weatherCodes.get(index);
            String weatherCondition = convertWeatherCode(weatherCode);

            //getting humidity
            JSONArray humidityData = (JSONArray) hourlyData.get("relative_humidity_2m");
            long humidity = (long) humidityData.get(index);

            //getting wind speed
            JSONArray windSpeedData = (JSONArray) hourlyData.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);

            //creating JSON object containing all neccessary data
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", currentTemp);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("wind_speed", windSpeed);

            return weatherData;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * this method parses the location given and produces an JSONArray from the Api response
     * @param location
     * @return
     */
    public static JSONArray getLocationData(String location){
        //must change string to have + instead of blank space
        String searchLocation = location.replaceAll(" ", "+");

        //creating URL for api call
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="+
                searchLocation + "&count=10&language=en&format=json";
        try{
            HttpURLConnection conn = fetchApiResponse(urlString);
            if(conn.getResponseCode() != 200){
                //if the response code is an error in connection return null and say WHOOPSIES
                System.out.println("WHOOPSIES");
                return null;
            }
            //storing the things gotten from API
            StringBuilder jsonResult = new StringBuilder();
            Scanner sc = new Scanner(conn.getInputStream());

            //reading the results and storing them in our
            while(sc.hasNext()){
                jsonResult.append(sc.nextLine());
            }

            //closing scanner and connection
            sc.close();
            conn.disconnect();

            //parsing into a JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultObject = (JSONObject) parser.parse(String.valueOf(jsonResult));

            //getting the list of location data from the API request
            JSONArray locationData = (JSONArray) resultObject.get("results");
            return locationData;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * this method
     * @param urlString
     * @return
     */
    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //creating connection to the URL
            URI uri = new URI(urlString); //use new URL() is apperantly deprecated
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //setting the request method to get
            conn.setRequestMethod("GET");

            //connection
            conn.connect();
            return conn;
        } catch(Exception e){
            e.printStackTrace();
        }
        //if fails return null
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray hours){
        String currentTime = getCurrentTime();

        //iterate through the list til we find a match
        for(int i = 0; i < hours.size(); i++){
            String time = (String) hours.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return current index
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime(){
        //getting the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //formatting the date time to fit the API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedTime = currentDateTime.format(formatter);

        return formattedTime;
    }

    /**
     * takes weather code from API and interprets it to get the weather condition
     * @param weatherCode
     * @return
     */
    private static String convertWeatherCode(Long weatherCode){
        String weatherCondition = "";

        if(weatherCode == 0L){weatherCondition = "Clear Sky";}
        else if(weatherCode <= 3L && weatherCode >= 0L){weatherCondition = "Cloudy";}

        //grouping all the "rainy" weather codes together, so can either be light drizzle of heavy rain
        else if(weatherCode <= 67 && weatherCode >= 51 || weatherCode <= 99 && weatherCode >= 80){
            weatherCondition = "Rainy";
        }

        //grouping all "snowy" weather codes
        else if(weatherCode <= 77 && weatherCode >= 71){ weatherCondition = "Snowy";}

        return weatherCondition;
    }
}
