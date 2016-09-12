package rs.reviewer.services;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Danijela on 9/8/2016.
 */
public class GooglePlacesAPIService {

    public static HashMap<String, String> locationIds;

    public static ArrayList<String> autocomplete(String input) {
        locationIds = new HashMap<String, String>();
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(APIKey.GOOGLE_PLACES_API_BASE + APIKey.TYPE_AUTOCOMPLETE + APIKey.OUT_JSON);
            sb.append("?key=" + APIKey.GOOGLE_API_KEY);
            //sb.append("&components=country:gr");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            System.out.println(sb);

            URL url = new URL(sb.toString());

            try{
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            Log.e("GooglePlacesAutocomple", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("GooglePlacesAutocomple", "Error connecting to Places API", e);
            return resultList;
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            System.out.println(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                locationIds.put(predsJsonArray.getJSONObject(i).getString("description"), predsJsonArray.getJSONObject(i).getString("place_id"));
            }
        } catch (JSONException e) {
            Log.e("GooglePlacesAutocomple", "Cannot process JSON results", e);
        }

        return resultList;
    }

    public static LatLng getLatLng(String placeId){

        String location ="";
        LatLng retVal = new LatLng(0,0);
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(APIKey.GOOGLE_PLACE_GEOMETRY + APIKey.OUT_JSON);
            sb.append("?placeid=" + placeId);
            sb.append("&key=" + APIKey.GOOGLE_API_KEY);
            System.out.println(sb);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("GooglePlacesAutocomple", "Error processing Places API URL", e);
            return retVal;
        } catch (IOException e) {
            Log.e("GooglePlacesAutocomple", "Error connecting to Places API", e);
            return retVal;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            System.out.println(jsonResults.toString());
            JSONObject geometryJsonObject = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
            String latitude = geometryJsonObject.getString("lat");

            String longitude = geometryJsonObject.getString("lng");

            System.out.println("latitude "+latitude);
            System.out.println("longitude "+longitude);


            location = latitude+","+longitude;
            retVal = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));


        } catch (JSONException e) {
            Log.e("GooglePlacesAutocomple", "Cannot process JSON results", e);
        }

        return retVal;
    }
}
