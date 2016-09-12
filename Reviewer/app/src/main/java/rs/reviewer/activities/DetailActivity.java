package rs.reviewer.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.Artist;
import model.Cinema;
import model.Concert;
import rs.reviewer.R;
import rs.reviewer.database.DBContentProvider;
import rs.reviewer.database.ReviewerSQLiteHelper;
import rs.reviewer.tools.ReviewerTools;
import rs.reviewer.tools.Util;

public class DetailActivity extends AppCompatActivity {
    private Uri todoUri;
    private SimpleCursorAdapter dataAdapter;
    private Concert concert;

    private android.support.design.widget.FloatingActionButton fav;

    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fav = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fav);


        fav.setOnClickListener(handlerFav);



        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();






        Bundle extras = getIntent().getExtras();

        todoUri = extras.getParcelable("id");
        fillData(todoUri);
    }

    View.OnClickListener handlerFav = new View.OnClickListener() {
        public void onClick(View v) {
            addToFavourites();

        }
    };


    private void addToFavourites(){
        if(checkUnique()) {


            ReviewerSQLiteHelper dbHelper = new ReviewerSQLiteHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues entry = new ContentValues();

            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_NAME, concert.getTitle());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_DESCRIPTION, concert.getDescription());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_DATETIME, concert.getDatetime());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_FORMATTED_DATETIME, concert.getFormatted_datetime());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_FORMATTED_LOCATION, concert.getFormatted_location());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_VENUE_DISPLAY_NAME, concert.getVenue_display_name());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_API_ID, (Integer) concert.getApiID());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_AVATAR, concert.getAvatar());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_LATITUDE, (Double) concert.getLatitude());
            entry.put(ReviewerSQLiteHelper.COLUMN_FAV_LONGITUDE, (Double) concert.getLongitude());


            getApplicationContext().getContentResolver().insert(DBContentProvider.CONTENT_URI_FAV, entry);
            db.close();

            Toast.makeText(getApplicationContext(),
                    "Added to favourites", Toast.LENGTH_SHORT).show();
        }else {

            Toast.makeText(getApplicationContext(),
                    "Already in your favourites", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkUnique(){

        String[] allColumns = { ReviewerSQLiteHelper.COLUMN_FAV_API_ID };

        Cursor cursor = getApplicationContext().getContentResolver().query(DBContentProvider.CONTENT_URI_FAV, allColumns, null, null,
                null);

        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {

            if((cursor.getInt(0) == (Integer) concert.getApiID())){
                return false;

            }
            cursor.moveToNext();
        }

        cursor.close();

        return true;

    }

    private void fillData(Uri todoUri) {

        String[] allColumns = { ReviewerSQLiteHelper.COLUMN_ID,
                ReviewerSQLiteHelper.COLUMN_NAME, ReviewerSQLiteHelper.COLUMN_DESCRIPTION, ReviewerSQLiteHelper.COLUMN_AVATAR,
                ReviewerSQLiteHelper.COLUMN_FORMATTED_DATETIME, ReviewerSQLiteHelper.COLUMN_FORMATTED_LOCATION ,
                ReviewerSQLiteHelper.COLUMN_VENUE_DISPLAY_NAME, ReviewerSQLiteHelper.COLUMN_API_ID,  ReviewerSQLiteHelper.COLUMN_DATETIME,
                ReviewerSQLiteHelper.COLUMN_LATITUDE, ReviewerSQLiteHelper.COLUMN_LONGITUDE };

        Cursor cursor = getContentResolver().query(todoUri, allColumns, null, null,
                null);




        cursor.moveToFirst();
        concert = createConcert(cursor);
       // Uri todoUri2 = Uri.parse(DBContentProvider.CONTENT_URI_JOIN + "/" + todoUri.getLastPathSegment());

        Uri todoUri2 = Uri.parse(DBContentProvider.CONTENT_URI_JOIN + "/" + todoUri.getLastPathSegment());
        Cursor cursor2 = getContentResolver().query(todoUri2,null, null, null, null);




        /*
        ArrayList<Artist> artists = new ArrayList<Artist>();



        while (!cursor2.isAfterLast()) {

            Artist artist = new Artist();
            artist.setName(cursor2.getString(0));
            artist.setImage_url(cursor2.getString(1));
            Log.i("Ime izvodjaca ",cursor2.getString(0));
            Log.i("Slika  ",cursor2.getString(1));

            artists.add(artist);
            cursor2.moveToNext();
        }

           */


        cursor2.moveToFirst();

        String[] columns = new String[] {
                ReviewerSQLiteHelper.COLUMN_ARTIST_NAME,



        };

        int[] to = new int[] {
                R.id.artist_name,


        };



        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.artists_layout,
                cursor2,
                columns,
                to,
                0);




        ListView listView = (ListView) findViewById(R.id.listViewArtists);
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
                String artistname =
                        cursor.getString(0);



                Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
                Uri todoUri = Uri.parse(DBContentProvider.CONTENT_URI_ARTIST + "/" + artistname);
                intent.putExtra("id", todoUri);
                startActivity(intent);



            }
        });


      //  cursor2.close(); on destroy, activity close them


        TextView tvName = (TextView)findViewById(R.id.tvName);
        TextView tvDescr = (TextView)findViewById(R.id.tvDescr);
        TextView tvDate = (TextView)findViewById(R.id.tvDate);
        TextView tvLocation = (TextView)findViewById(R.id.tvLocation);
        ImageView artistImage = (ImageView)findViewById(R.id.event_image);

        int status = ReviewerTools.getConnectivityStatus(getApplicationContext());

        if(status == ReviewerTools.TYPE_NOT_CONNECTED ){
           // artistImage.setImageDrawable();
            artistImage.setImageResource(R.drawable.artist_large);
        }else {
            Picasso.with(getApplicationContext()).load(concert.getAvatar()).into(artistImage);
        }


        tvName.setText(concert.getTitle());
        tvDescr.setText(concert.getDescription());
        tvDate.setText(concert.getFormatted_datetime());
        tvLocation.setText(concert.getVenue_display_name()+", "+concert.getFormatted_location());

       LatLng position = new LatLng(cursor.getDouble(9), cursor.getDouble(10));

        System.out.println(position.toString());

        Marker hamburg = map.addMarker(new MarkerOptions().position(position)
                .title(concert.getVenue_display_name()));


        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);






        cursor.close();
    }


    public static Concert createConcert(Cursor cursor) {
        Concert concert = new Concert();
        concert.setId(cursor.getLong(0));
        concert.setTitle(cursor.getString(1));
        concert.setDescription(cursor.getString(2));
        concert.setAvatar(cursor.getString(3));
        concert.setFormatted_datetime(cursor.getString(4));
        concert.setFormatted_location(cursor.getString(5));
        concert.setVenue_display_name(cursor.getString(6));
        concert.setApiID(cursor.getInt(7));
        concert.setDatetime(cursor.getString(8));
        concert.setLatitude(cursor.getDouble(9));
        concert.setLongitude(cursor.getDouble(10));
       // concert.setApiID(cursor);

        System.out.println("Concert "+concert.toString());



        return concert;
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
