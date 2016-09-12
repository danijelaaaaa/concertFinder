package rs.reviewer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Long2;

/**
 * Created by skapl on 13-Apr-16.
 */
public class ReviewerSQLiteHelper extends SQLiteOpenHelper {


    public static final String TABLE_CONCERT = "concert";
    public static final String TABLE_ARTIST = "artist";
    public static final String TABLE_FAV ="favourites";
    public static final String TABLE_TRACKS ="tracks";

    //columns for concerts
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_FORMATTED_DATETIME = "formatted_datetime";
    public static final String COLUMN_FORMATTED_LOCATION = "formatted_location";
    public static final String COLUMN_VENUE_DISPLAY_NAME = "venue_display_name";
    public static final String COLUMN_VENUE_NAME = "venue_name";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_REGION = "region";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_API_ID = "api_id";

    //columns for artists
    public static final String COLUMN_ARTIST_ID ="artist_id";
    public static final String COLUMN_ARTIST_NAME ="artist_name";
    public static final String COLUMN_IMAGE_URL="image_url";
    public static final String COLUMN_THUMB_URL = "thumb_url";
    public static final String COLUMN_CONCERT_ID = "concert_id";


    //columns for fav

    public static final String COLUMN_FAV_ID = "_id";
    public static final String COLUMN_FAV_NAME = "name_fav";
    public static final String COLUMN_FAV_DESCRIPTION = "description_fav";
    public static final String COLUMN_FAV_AVATAR = "avatar_fav";
    public static final String COLUMN_FAV_DATETIME = "datetime_fav";
    public static final String COLUMN_FAV_FORMATTED_DATETIME = "formatted_datetime_fav";
    public static final String COLUMN_FAV_FORMATTED_LOCATION = "formatted_location_fav";
    public static final String COLUMN_FAV_VENUE_DISPLAY_NAME = "venue_display_name_fav";
    public static final String COLUMN_FAV_VENUE_NAME = "venue_name_fav";
    public static final String COLUMN_FAV_CITY = "city_fav";
    public static final String COLUMN_FAV_REGION = "region_fav";
    public static final String COLUMN_FAV_COUNTRY = "country_fav";
    public static final String COLUMN_FAV_LONGITUDE = "longitude_fav";
    public static final String COLUMN_FAV_LATITUDE = "latitude_fav";
    public static final String COLUMN_FAV_API_ID = "api_id_fav";

    //columns for tracks

    public static final String COLUMN_TRACK_ID = "_id";
    public static final String COLUMN_TRACK_TITLE = "name_track";
    public static final String COLUMN_TRACK_YTID= "ytid_track";
    public static final String COLUMN_TRACK_ARTIST ="artist_track";

    public static final String DATABASE_NAME = "concert.db";
    public static final int DATABASE_VERSION = 1;

    private static final String DB_CREATE = "create table "
            + TABLE_CONCERT + "("
            + COLUMN_ID  + " integer primary key autoincrement , "
            + COLUMN_NAME + " text, "
            + COLUMN_DESCRIPTION + " text, "
            + COLUMN_AVATAR + " text, "
            + COLUMN_DATETIME + " text, "
            + COLUMN_FORMATTED_DATETIME + " text, "
            + COLUMN_FORMATTED_LOCATION + " text, "
            + COLUMN_VENUE_DISPLAY_NAME + " text, "
            + COLUMN_VENUE_NAME + " text, "
            + COLUMN_CITY + " text, "
            + COLUMN_REGION + " text, "
            + COLUMN_COUNTRY + " text, "
            + COLUMN_LONGITUDE + " real, "
            + COLUMN_LATITUDE + " real,"
            + COLUMN_API_ID + " integer"
            + ")";

    private static final String DB_ARTIST_CREATE = "create table "
            + TABLE_ARTIST + "("
            + COLUMN_ARTIST_ID  + " integer primary key autoincrement , "
            + COLUMN_ARTIST_NAME   + " text, "
            + COLUMN_IMAGE_URL + " text, "
            + COLUMN_THUMB_URL + " text, "
            + COLUMN_CONCERT_ID + " integer"
            + ")";

    private static final String DB_TRACK_CREATE = "create table "
            + TABLE_TRACKS + "("
            + COLUMN_TRACK_ID  + " integer primary key autoincrement , "
            + COLUMN_TRACK_TITLE   + " text, "
            + COLUMN_TRACK_YTID + " text,"
            + COLUMN_TRACK_ARTIST + " integer"
            + ")";


    private static final String DB_FAV_CREATE = "create table "
            + TABLE_FAV + "("
            + COLUMN_FAV_ID  + " integer primary key autoincrement , "
            + COLUMN_FAV_NAME + " text, "
            + COLUMN_FAV_DESCRIPTION + " text, "
            + COLUMN_FAV_AVATAR + " text, "
            + COLUMN_FAV_DATETIME + " text, "
            + COLUMN_FAV_FORMATTED_DATETIME + " text, "
            + COLUMN_FAV_FORMATTED_LOCATION + " text, "
            + COLUMN_FAV_VENUE_DISPLAY_NAME + " text, "
            + COLUMN_FAV_VENUE_NAME + " text, "
            + COLUMN_FAV_CITY + " text, "
            + COLUMN_FAV_REGION + " text, "
            + COLUMN_FAV_COUNTRY + " text, "
            + COLUMN_FAV_LONGITUDE + " real, "
            + COLUMN_FAV_LATITUDE + " real,"
            + COLUMN_FAV_API_ID + " integer"
            + ")";

    public ReviewerSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
        db.execSQL(DB_ARTIST_CREATE);
        db.execSQL(DB_FAV_CREATE);
        db.execSQL(DB_TRACK_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONCERT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        onCreate(db);


    }

    public void onSync(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONCERT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);

        db.execSQL(DB_CREATE);
        db.execSQL(DB_ARTIST_CREATE);
        db.execSQL(DB_TRACK_CREATE);
    }


}
