package rs.reviewer.favourite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import model.Concert;
import rs.reviewer.MainActivity;
import rs.reviewer.database.DBContentProvider;
import rs.reviewer.database.ReviewerSQLiteHelper;
import rs.reviewer.sync.SyncService;
import rs.reviewer.tools.ReviewerTools;

/**
 * Created by Danijela on 9/10/2016.
 */
public class FavTask extends AsyncTask<Void, Void, ArrayList<Concert> >{

    private Context context;

    public FavTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected ArrayList<Concert> doInBackground(Void... voids) {

        String[] allColumns = { ReviewerSQLiteHelper.COLUMN_FAV_ID,
                ReviewerSQLiteHelper.COLUMN_FAV_NAME, ReviewerSQLiteHelper.COLUMN_FAV_DESCRIPTION, ReviewerSQLiteHelper.COLUMN_FAV_AVATAR,
                ReviewerSQLiteHelper.COLUMN_FAV_FORMATTED_DATETIME, ReviewerSQLiteHelper.COLUMN_FAV_FORMATTED_LOCATION ,
                ReviewerSQLiteHelper.COLUMN_FAV_VENUE_DISPLAY_NAME, ReviewerSQLiteHelper.COLUMN_FAV_API_ID,  ReviewerSQLiteHelper.COLUMN_FAV_DATETIME,
                ReviewerSQLiteHelper.COLUMN_FAV_LATITUDE, ReviewerSQLiteHelper.COLUMN_FAV_LONGITUDE };

        Cursor cursor = context.getContentResolver().query(DBContentProvider.CONTENT_URI_FAV, allColumns, null, null,
                null);

        cursor.moveToFirst();

        ArrayList<Concert> concerts = new ArrayList<Concert>();

        Concert c = new Concert();

        while (!cursor.isAfterLast()) {
            c = createConcert(cursor);
           if(checkDate(c.getDatetime())) {
         //   if(true){

               concerts.add(c);

           }
            cursor.moveToNext();
        }

        cursor.close();




        return concerts;
    }


    @Override
    protected void onPostExecute(ArrayList<Concert> concerts) {

        if(concerts.size()>0) {


            for(Concert c: concerts) {
                Intent ints = new Intent(MainActivity.FAV_NOTIFICATION);
                ints.putExtra("title",c.getTitle() );
                ints.putExtra("avatar", c.getVenue_display_name());
                ints.putExtra("venue", c.getVenue_display_name());
                ints.putExtra("datetime", c.getFormatted_datetime());

                context.sendBroadcast(ints);
            }
        }



    }

    public Boolean checkDate(String date){

        //YYYY-MM-DDThh:mm:ss

        String parts [] = date.split("T");

        String concertDate = parts[0];

        Date today = new Date();

        //String today =
        Date d = Calendar.getInstance().getTime(); // Current time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Set your date format
        String currentDate = sdf.format(d);

        System.out.println("Concert date "+concertDate);
        System.out.println("Today date "+currentDate);

        String [] cd_parts = concertDate.split("-");

        String [] today_parts = currentDate.split("-");



        if(cd_parts[0].equals(today_parts[0]) && cd_parts[1].equals(today_parts[1]) && cd_parts[2].equals(today_parts[2])){
            return true;
        }


        return false;

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

      //  System.out.println("Concert "+concert.toString());



        return concert;
    }
}
