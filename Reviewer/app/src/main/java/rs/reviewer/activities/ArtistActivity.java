package rs.reviewer.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import model.Artist;
import rs.reviewer.R;
import rs.reviewer.database.DBContentProvider;
import rs.reviewer.database.ReviewerSQLiteHelper;
import rs.reviewer.tools.ReviewerTools;

public class ArtistActivity extends AppCompatActivity {
    private Uri todoUri;
    private SimpleCursorAdapter dataAdapter;
    private String[] albums;
    private String[] tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();

        todoUri = extras.getParcelable("id");
        fillData(todoUri);
    }


    private void fillData(Uri todoUri) {
        String[] allColumns = {ReviewerSQLiteHelper.COLUMN_ARTIST_ID, ReviewerSQLiteHelper.COLUMN_ARTIST_NAME, ReviewerSQLiteHelper.COLUMN_IMAGE_URL, ReviewerSQLiteHelper.COLUMN_CONCERT_ID};
        Cursor cursor = getContentResolver().query(todoUri, allColumns, null, null,
                null);


        cursor.moveToFirst();

        Artist artist = createArtist(cursor);


        String[] projections = {"_id", "title", "ytid", "concert"};

//        Cursor cursor = db.query(domainClass.getSimpleName(), projections,
//                null, null, null, null, null);

        ////tracks

        Uri todoUri2 = Uri.parse(DBContentProvider.CONTENT_URI_TRACK_JOIN + "/" +todoUri.getLastPathSegment());
   //     Cursor cursor2 = getApplicationContext().getContentResolver().query(DBContentProvider.CONTENT_URI_TRACK, projections, null, null, null);
        Cursor cursor2 = getContentResolver().query(todoUri2,null, null, null, null);





        cursor2.moveToFirst();

        String[] columns = new String[] {
                ReviewerSQLiteHelper.COLUMN_TRACK_TITLE
        };

        int[] to = new int[] {
                R.id.trackTitle
        };


        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.tracks_layout,
                cursor2,
                columns,
                to,
                0);


        ListView listView = (ListView) findViewById(R.id.listViewTracks);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        setListViewHeightBasedOnItems(listView);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {


                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String ytid =
                        cursor.getString(2);



//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse("https://www.youtube.com/watch?v="+ytid));
//                startActivity(i);

                watchYoutubeVideo(ytid);


            }
        });



        TextView tvName = (TextView) findViewById(R.id.tvNameArtist);
        ImageView artistImage = (ImageView) findViewById(R.id.artist_image);

        int status = ReviewerTools.getConnectivityStatus(getApplicationContext());

        if (status == ReviewerTools.TYPE_NOT_CONNECTED) {
            // artistImage.setImageDrawable();
            artistImage.setImageResource(R.drawable.artist_large);
        } else {
            Picasso.with(getApplicationContext()).load(artist.getImage_url()).into(artistImage);
        }


        tvName.setText(artist.getName());

        cursor.close();

    }


    public void watchYoutubeVideo(String id){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    public static Artist createArtist(Cursor cursor) {

        Artist artist = new Artist();
        artist.setId(cursor.getLong(0));
        artist.setName(cursor.getString(1));
        artist.setImage_url(cursor.getString(2));
        artist.setConcert_id(cursor.getInt(3));

        System.out.println(artist);

        return artist;
    }


    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

}


//    public class UserLoginTask extends AsyncTask<Void, Void, String[]> {
//        @Override
//        protected String[] doInBackground(Void... params) {
//            String[] responses = new String[2];
//            responses[0]= "";
//            responses[1]= "";
//
//            try {
//                URL urlAlbums = new URL("http://api.musicgraph.com/api/v2/album/search?api_key=5600934a9ea5d2883faf6e78bc4d8307&artist_name=Adele");
//                URL urlTracks = new URL("http://api.musicgraph.com/api/v2/track/search?api_key=5600934a9ea5d2883faf6e78bc4d8307&artist_name=Zeljko+Joksimovic");
//                HttpURLConnection urlConnection = null;
//                try {
//                    urlConnection = (HttpURLConnection) urlAlbums.openConnection();
//
//                    try {
//
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                        StringBuilder stringBuilder = new StringBuilder();
//                        String line;
//                        while ((line = bufferedReader.readLine()) != null) {
//                            stringBuilder.append(line).append("\n");
//                        }
//                        bufferedReader.close();
//                       responses[0] = stringBuilder.toString();
//
//
//                    }
//                    finally{
//                        urlConnection.disconnect();
//                    }
//
//                    urlConnection = (HttpURLConnection) urlTracks.openConnection();
//
//                    try {
//
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                        StringBuilder stringBuilder = new StringBuilder();
//                        String line;
//                        while ((line = bufferedReader.readLine()) != null) {
//                            stringBuilder.append(line).append("\n");
//                        }
//                        bufferedReader.close();
//                        responses[1] = stringBuilder.toString();
//
//
//                    }
//                    finally{
//                        urlConnection.disconnect();
//                    }
//
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//
//
//            // TODO: register the new account here.
//            return responses;
//        }
//
//        @Override
//        protected void onPostExecute(String[] results) {
//
//
//            if (!results[0].equals("")) {
//
//                try {
//                    JSONObject object = (JSONObject) new JSONTokener(results[0]).nextValue();
//
//                    JSONObject pagination = object.getJSONObject("pagination");
//
//                    String count = pagination.getString("count");
//
//                    albums = new String[20];
//
//                    JSONArray data = object.getJSONArray("data");
//
//                    for(int i=0 ; i<data.length(); i++) {
//                       String albumTitle = data.getJSONObject(i).getString("title");
//                        String albumForm = data.getJSONObject(i).getString("product_form");
//                        String number_of_tracks =  data.getJSONObject(i).getString("number_of_tracks");
//
//                        albums[i] = albumTitle;
//                    }
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//            else {
////                mPasswordView.setError(getString(R.string.error_incorrect_password));
////                mPasswordView.requestFocus();
//            }
//
//
//            if (!results[0].equals("")) {
//
//                try {
//                    JSONObject object = (JSONObject) new JSONTokener(results[1]).nextValue();
//
//                    JSONObject pagination = object.getJSONObject("pagination");
//
//                    String count = pagination.getString("count");
//
//                    albums = new String[20];
//
//                    JSONArray data = object.getJSONArray("data");
//
//                    for(int i=0 ; i<data.length(); i++) {
//                        String albumTitle = data.getJSONObject(i).getString("title");
//                        String albumForm = data.getJSONObject(i).getString("product_form");
//                        String number_of_tracks =  data.getJSONObject(i).getString("number_of_tracks");
//
//                        albums[i] = albumTitle;
//                    }
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//            else {
////                mPasswordView.setError(getString(R.string.error_incorrect_password));
////                mPasswordView.requestFocus();
//            }
//
//
//
//        }
//
//
//    }
//}


