package rs.reviewer.sync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import model.Artist;
import model.Concert;
import model.Track;
import model.Venue;
import rs.reviewer.MainActivity;
import rs.reviewer.R;
import rs.reviewer.database.DBContentProvider;
import rs.reviewer.database.ReviewerSQLiteHelper;
import rs.reviewer.services.APIKey;
import rs.reviewer.services.GooglePlacesAPIService;
import rs.reviewer.tools.ReviewerTools;
import rs.reviewer.tools.Util;



/**
 * Created by milossimic on 4/6/16.
 */
public class SyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;





    private String radius;
    private String location;
    private String placeId;
    private String latLng;
    private ProgressBar pb;

   // LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);



    public static String RESULT_CODE = "RESULT_CODE";

    public SyncTask(Context context, ProgressBar pb)
    {
        this.context = context;
        this.pb = pb;


    }

    @Override
    protected void onPreExecute() {
        if(pb!=null) {
            pb.setVisibility(View.VISIBLE);
        }else{

//                        Toast.makeText(context, "Sync started",
//                    Toast.LENGTH_LONG).show();

        }
    }


       // LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        //postaviti parametre, pre pokretanja zadatka ako je potrebno





    public static String replaceSpa(String str){

        StringBuffer strBuffer = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {

            if (str.charAt(i) == ' ') {

                strBuffer.append("+");

            }else {

                strBuffer.append(str.charAt(i));

            }

        }

        return strBuffer.toString();

    }


    @Override
    protected Boolean doInBackground(Void... params) {

        //simulacija posla koji se obavlja u pozadini i traje duze vreme
        ArrayList<String> responseArray = new ArrayList<String>();
        try {
            //URL url = new URL(API_URL + "email=" + email + "&apiKey=" + API_KEY);
            //  URL url = new URL("https://graph.facebook.com/search?q=lancaster&type=event");
            //  URL url = new URL("http://api.bandsintown.com/artists//events.json?api_version=2.0&app_id=NSLive");


         //   latitude = String.valueOf(51.500152);

          //  longitude = String.valueOf(-0.126236);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            System.out.println("Radijus "+ sp.getString("pref_radius", "5"));
            System.out.println("Lokacija "+ sp.getString("location", "45.2671352,19.8335496"));

            radius = sp.getString("pref_radius", "5");
            location = sp.getString("location", "45.2671352,19.8335496");
            latLng = sp.getString("getLatLong", "false");


            //get location from place Id
            if(latLng.equals("true") && location != null) {
                if(GooglePlacesAPIService.locationIds != null) {

                    System.out.println("Lookacija iz sync taska-a"+location);

                    placeId = GooglePlacesAPIService.locationIds.get(location);

                    location = getLatLng(placeId);
                }else{

                    location = "45.2671352,19.8335496";
                }
            }







            URL urlArtist = new URL("http://api.bandsintown.com/artists/"+"MuseTribute"+"/events/search.json?api_version=2.0&app_id=NSLive&location=London,United+Kingdom&radius=10");
            URL url = new URL("http://api.bandsintown.com/events/search.json?api_version=2.0&app_id=NSLive&location="+location+"&radius="+radius);

            System.out.println(url);

            //  URL url = new URL("http://api.bandsintown.com/events/search.json?location=Boston,MA&page=2&app_id=NSLive");
            //  http://api.bandsintown.com/artists/Skrillex/events/search.json?api_version=2.0&app_id=NSLive&location=San+Diego,CA&radius=10
            ArrayList<String> artists = new ArrayList<String>();
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String response = stringBuilder.toString();

               // ArrayList<Artist> artists = new ArrayList<Artist>();


                JSONArray concerts = (JSONArray) new JSONTokener(response).nextValue();

                artists = getUrlParams(concerts);

               // URL urlArtist = new URL("http://api.bandsintown.com/artists/"+"MuseTribute"+"/events/search.json?api_version=2.0&app_id=NSLive&location=London,United+Kingdom&radius=150");


            }
            finally{
                urlConnection.disconnect();
            }

            responseArray = new ArrayList<String>();
            for(String urlParam : artists){

                URL urlArt = new URL("http://api.bandsintown.com/artists/"+urlParam+"/events/search.json?api_version=2.0&app_id=NSLive&location="+location+"&radius="+radius);
                System.out.println(urlArt);
                urlConnection = (HttpURLConnection) urlArt.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String response = stringBuilder.toString();

                    responseArray.add(response);

                  //  JSONArray concerts = (JSONArray) new JSONTokener(response).nextValue();





                }
                finally{
                    urlConnection.disconnect();
                }




            }


            if(responseArray != null) {
                if (responseArray.size() > 0) {

                    updateDB(context, responseArray);
                    return true;
                }
                else{
                    return false;
                }
            }else{



                return false;
            }




        } catch (FileNotFoundException e2){

//            Toast.makeText(context, "No concerts found for that location and radius, change configuration",
//                    Toast.LENGTH_LONG).show();

            return null;

        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }



    }

    @Override
    protected void onPostExecute(Boolean result) {

        if(pb!=null) {
            pb.setVisibility(View.GONE);
        }

//        try {
//          //  JSONArray object = (JSONArray) new JSONTokener(result).nextValue();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        if(result != null){
            if(!result) {
                Toast.makeText(context, "No concerts found for that location and radius, change configuration",
                        Toast.LENGTH_LONG).show();

            }else{
                if(pb==null){
//                    Toast.makeText(context, "Concert list is updated",
//                            Toast.LENGTH_LONG).show();
                }
            }
        }else{

            Toast.makeText(context, "No concerts found for that location and radius, change configuration",
                    Toast.LENGTH_LONG).show();

        }





    }

    public static  ArrayList<String> getUrlParams( JSONArray concerts){
        ArrayList<String> artists = new ArrayList<String>();
        //iteriranje kroz sve koncerte
        try {
        for(int i=0 ; i<concerts.length(); i++) {

            JSONArray artistsJSON = concerts.getJSONObject(i).getJSONArray("artists");
            //iteriranje kroz sve izvodjace jednog koncerta
            for (int j = 0; j < artistsJSON.length(); j++) {
                if (artistsJSON.getJSONObject(j).has("url")) {

                    String artistUrl = null;

                    artistUrl = artistsJSON.getJSONObject(j).getString("url");


                  //  Log.i("artist_url", artistUrl);

                    String[] artistArray = artistUrl.split("/");

                    if(!artistArray[artistArray.length - 1].contains("%")) {
                        artists.add(artistArray[artistArray.length - 1]);
                    //    Log.i("artist_param", artistArray[artistArray.length - 1]);
                    }


                }

            }
        }

        } catch (JSONException e) {
                e.printStackTrace();
            }
        return artists;

    }


    public  void updateDB(Context context, ArrayList<String> results) {
        JSONArray objects = null;


        try {

            ReviewerSQLiteHelper dbHelper = new ReviewerSQLiteHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

          //  dbHelper.onUpgrade(db, db.getVersion(), db.getVersion()+1);
                dbHelper.onSync(db);



            {
                int i = 1;
                for(String result : results) {
                   // Log.i("UTIL_result", result);

                    objects = (JSONArray) new JSONTokener(result).nextValue();

                    JSONArray jsonObj = new JSONArray(result);

                  //  Log.i("UTIL_objects", objects.toString());

                    ArrayList<Concert> concerts = processJSON(jsonObj);


                    for (Concert c : concerts) {

                        if(!c.getApiID().equals(-1)) {
                            ContentValues entry = new ContentValues();
                            entry.put(ReviewerSQLiteHelper.COLUMN_NAME, c.getTitle());
                            entry.put(ReviewerSQLiteHelper.COLUMN_DESCRIPTION, c.getDescription());
                            entry.put(ReviewerSQLiteHelper.COLUMN_DATETIME, c.getDatetime());
                            entry.put(ReviewerSQLiteHelper.COLUMN_FORMATTED_DATETIME, c.getFormatted_datetime());
                            entry.put(ReviewerSQLiteHelper.COLUMN_FORMATTED_LOCATION, c.getFormatted_location());
                            entry.put(ReviewerSQLiteHelper.COLUMN_VENUE_DISPLAY_NAME, c.getVenue_display_name());
                            entry.put(ReviewerSQLiteHelper.COLUMN_API_ID, (Integer)c.getApiID());
                            entry.put(ReviewerSQLiteHelper.COLUMN_AVATAR, c.getAvatar());
                            entry.put(ReviewerSQLiteHelper.COLUMN_LATITUDE, (Double) c.getLatitude());
                            entry.put(ReviewerSQLiteHelper.COLUMN_LONGITUDE, (Double) c.getLongitude());

                            context.getContentResolver().insert(DBContentProvider.CONTENT_URI_CONCERT, entry);


                            for (Artist a : c.getArtists()) {
                                if(!a.getConcert_id().equals(-1)) {
                                   // System.out.println(a);

                                    ContentValues entryArtist = new ContentValues();
                                    entryArtist.put(ReviewerSQLiteHelper.COLUMN_ARTIST_NAME, a.getName());
                                    entryArtist.put(ReviewerSQLiteHelper.COLUMN_CONCERT_ID, (Integer) a.getConcert_id());
                                    entryArtist.put(ReviewerSQLiteHelper.COLUMN_IMAGE_URL, a.getImage_url());


                                    context.getContentResolver().insert(DBContentProvider.CONTENT_URI_ARTIST, entryArtist);

                                  //  System.out.println("III jee "+i);
                                    if(i<5) {

                                       // System.out.println("tu sam");
                                       // System.out.println("a.getName "+a.getName());
                                        ArrayList<Track> tracks = getTracks(a.getName());

                                        for (Track t : tracks) {
                                            ContentValues entryTrack = new ContentValues();
                                           // System.out.println(t);
                                            entryTrack.put(ReviewerSQLiteHelper.COLUMN_TRACK_TITLE, t.getTitle());
                                            entryTrack.put(ReviewerSQLiteHelper.COLUMN_TRACK_YTID, t.getYtid());
                                            entryTrack.put(ReviewerSQLiteHelper.COLUMN_TRACK_ARTIST, (Integer) a.getConcert_id());

                                            context.getContentResolver().insert(DBContentProvider.CONTENT_URI_TRACK, entryTrack);
                                        }

                                    }
//                                    for(int i=0; i<2; i++){
//                                        ContentValues entryTrack = new ContentValues();
//
//                                       entryTrack.put(ReviewerSQLiteHelper.COLUMN_TRACK_TITLE, "pjesma");
//                                        entryTrack.put(ReviewerSQLiteHelper.COLUMN_TRACK_YTID, "W1IVP8R8jTs");
//
//                                        entryTrack.put(ReviewerSQLiteHelper.COLUMN_TRACK_ARTIST, (Integer) a.getConcert_id());
//
//                                        context.getContentResolver().insert(DBContentProvider.CONTENT_URI_TRACK, entryTrack);
//                                    }




                                }
                                i=i+1;

                            }


                        }

                    }


                }

                Intent ints = new Intent(MainActivity.SYNC_DATA);
                int status = ReviewerTools.getConnectivityStatus(context);
                ints.putExtra(RESULT_CODE, status);
                context.sendBroadcast(ints);


            }

            db.close();



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public ArrayList<Track> getTracks(String artistName){

            ArrayList<Track> tracks = new ArrayList<Track>();

            String name = replaceSpa(artistName.trim());
            String response = "";

        try {
            URL urlAlbums = new URL("http://api.musicgraph.com/api/v2/album/search?api_key=b4cb74c7eb70f4228eb605b52006df13&artist_name=Adele");
            URL urlTracks = new URL("http://api.musicgraph.com/api/v2/track/search?api_key=bc5ffce5b5b0433709c2e4933e9138fe&artist_name="+name);
            HttpURLConnection urlConnection = null;
//            try {
//                urlConnection = (HttpURLConnection) urlAlbums.openConnection();
//
//                try {
//
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                    StringBuilder stringBuilder = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        stringBuilder.append(line).append("\n");
//                    }
//                    bufferedReader.close();
//                    responses[0] = stringBuilder.toString();
//
//
//                }
//                finally{
//                    urlConnection.disconnect();
//                }

                urlConnection = (HttpURLConnection) urlTracks.openConnection();

                try {

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();

                    if(!response.equals("")){
                        JSONObject object = (JSONObject)new JSONTokener(response).nextValue();


                    JSONArray data = object.getJSONArray("data");

                    for(int i=0 ; i<data.length(); i++) {
                        if(data.getJSONObject(i).has("track_youtube_id")) {
                            String title = data.getJSONObject(i).getString("title");
                            String track_youtube_id = data.getJSONObject(i).getString("track_youtube_id");

                            Track track = new Track();

                            track.setTitle(title);
                            track.setYtid(track_youtube_id);
                            tracks.add(track);

                        }
                    }



                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                } finally{
                    urlConnection.disconnect();
                }



            } catch (IOException e) {
                e.printStackTrace();
            }

        return tracks;
    }


    public ArrayList<Concert> processJSON(JSONArray objects){
        ArrayList<Concert> concertList = new ArrayList<Concert>();
        try {

           // Log.i("OBJECTS_LENGTH", String.valueOf(objects.length()));


            for(int i=0 ; i<objects.length(); i++) {
                //Log.i("i", String.valueOf(i));
                Concert concert = new Concert();
                ArrayList<Artist> artists = new ArrayList<Artist>();
             //   Venue venue = new Venue();

                JSONArray artistsJSON = objects.getJSONObject(i).getJSONArray("artists");
              //  Log.i("artists",artistsJSON.toString());
               // Log.i("artists_num", String.valueOf(artistsJSON.length()));


                JSONObject venueJSON = objects.getJSONObject(i).getJSONObject("venue");



                if( venueJSON.has("name") && venueJSON.getString("name") != null
                        && !venueJSON.getString("name").equals("null") ) {
                    concert.setVenue_display_name(venueJSON.getString("name"));
                }else{
                   concert.setVenue_display_name("No name");
                }

                if( venueJSON.has("place") && venueJSON.getString("place") != null
                        && !venueJSON.getString("place").equals("place") ) {
                    concert.setVenue_name(venueJSON.getString("place"));
                }else{
                    concert.setVenue_name("No name");
                }

                if( venueJSON.has("city") && venueJSON.getString("city") != null
                        && !venueJSON.getString("city").equals("null") ) {
                    concert.setCity(venueJSON.getString("city"));
                }else{
                    concert.setCity("No available city");
                }


                if( venueJSON.has("region") && venueJSON.getString("region") != null
                        && !venueJSON.getString("region").equals("region") ) {
                    concert.setRegion(venueJSON.getString("region"));
                }else{
                    concert.setRegion("No available region");
                }

                if( venueJSON.has("country") && venueJSON.getString("country") != null
                        && !venueJSON.getString("country").equals("country") ) {
                    concert.setCountry(venueJSON.getString("country"));
                }else{
                    concert.setCountry("No available country");
                }

                if( venueJSON.has("latitude")  ) {
                    concert.setLatitude(venueJSON.getDouble("latitude"));
                }else{
                    concert.setLatitude(null);
                }

                if( venueJSON.has("longitude")  ) {
                    concert.setLongitude(venueJSON.getDouble("longitude"));
                }else{
                    concert.setLongitude(null);
                }




                for(int j=0 ; j<artistsJSON.length(); j++) {

//                    Log.i("artist",artistsJSON.getJSONObject(j).toString());
//                    Log.i("artist_num", String.valueOf(j));

                    Artist artist = new Artist();
                    if( artistsJSON.getJSONObject(j).has("name") && artistsJSON.getJSONObject(j).getString("name") != null
                            && !artistsJSON.getJSONObject(j).getString("name").equals("null") ) {
                        artist.setName(artistsJSON.getJSONObject(j).getString("name"));
                    }else{
                        artist.setName("No name");
                    }

                    if( artistsJSON.getJSONObject(j).has("image_url") && artistsJSON.getJSONObject(j).getString("image_url") != null
                            && !artistsJSON.getJSONObject(j).getString("image_url").equals("null") ) {
                        artist.setImage_url(artistsJSON.getJSONObject(j).getString("image_url"));
                    }else{
                        artist.setImage_url("-1");
                    }



                    if(objects.getJSONObject(i).has("id")) {

                        artist.setConcert_id(objects.getJSONObject(i).getInt("id"));

                    }else {
                        artist.setConcert_id(-1);
                    }

                    artists.add(artist);
                }

                concert.setAvatar(artistsJSON.getJSONObject(0).getString("image_url"));

                concert.setArtists(artists);


                if(objects.getJSONObject(i).has("formatted_location")) {
                    if (objects.getJSONObject(i).getString("formatted_location") != null && !(objects.getJSONObject(i).getString("formatted_location").equals("null"))) {
                        concert.setFormatted_location(objects.getJSONObject(i).getString("formatted_location"));
                    } else {
                        concert.setFormatted_location("null");
                    }
                }else {
                    concert.setFormatted_location("null");
                }


                if(objects.getJSONObject(i).has("title")) {
                    if(objects.getJSONObject(i).getString("title")!=null && !(objects.getJSONObject(i).getString("title").equals("null"))) {
                        concert.setTitle(objects.getJSONObject(i).getString("title"));
                    }else{
                        concert.setTitle("No title");
                    }
                }else{
                    concert.setTitle("No title");
                }

                if(objects.getJSONObject(i).has("description")) {
                    if (objects.getJSONObject(i).getString("description") != null && !(objects.getJSONObject(i).getString("description").equals("null"))) {
                        concert.setDescription(objects.getJSONObject(i).getString("description"));
                    } else {
                        concert.setDescription("No description");
                    }
                }else {
                    concert.setDescription("No description");
                }

                if(objects.getJSONObject(i).has("datetime")) {
                    if (objects.getJSONObject(i).getString("datetime") != null && !(objects.getJSONObject(i).getString("datetime").equals("null"))) {
                        concert.setDatetime(objects.getJSONObject(i).getString("datetime"));
                    } else {
                        concert.setDatetime("No available date and time");
                    }
                }else {
                    concert.setDatetime("No available date and time");
                }

                if(objects.getJSONObject(i).has("formatted_datetime")) {
                    if (objects.getJSONObject(i).getString("formatted_datetime") != null && !(objects.getJSONObject(i).getString("formatted_datetime").equals("null"))) {
                        concert.setFormatted_datetime(objects.getJSONObject(i).getString("formatted_datetime"));
                    } else {
                        concert.setFormatted_datetime("null");
                    }
                }else {
                    concert.setFormatted_datetime("null");
                }

                if(objects.getJSONObject(i).has("formatted_location")) {
                    if (objects.getJSONObject(i).getString("formatted_location") != null && !(objects.getJSONObject(i).getString("formatted_location").equals("null"))) {
                        concert.setFormatted_location(objects.getJSONObject(i).getString("formatted_location"));
                    } else {
                        concert.setFormatted_location("null");
                    }
                }else {
                    concert.setFormatted_location("null");
                }

                if(objects.getJSONObject(i).has("id")) {

                    concert.setApiID(objects.getJSONObject(i).getInt("id"));

                }else {
                    concert.setApiID(-1);
                }





//                Log.i("concert", String.valueOf(concert));
                concertList.add(concert);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return concertList;
    }


//    public String getLocation(){
//
//    }


    public String getLatLng(String placeId){

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
            return location;
        } catch (IOException e) {
            Log.e("GooglePlacesAutocomple", "Error connecting to Places API", e);
            return location;
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

          //  System.out.println("latitude "+latitude);
          //  System.out.println("longitude "+longitude);


            location = latitude+","+longitude;
            retVal = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));


        } catch (JSONException e) {
            Log.e("GooglePlacesAutocomple", "Cannot process JSON results", e);
        }

        return location;
    }



}
