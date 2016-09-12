package rs.reviewer.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

/**
 * Created by skapl on 13-Apr-16.
 */
public class DBContentProvider extends ContentProvider {
    private ReviewerSQLiteHelper database;

    private static final int CONCERT = 10;
    private static final int CONCERT_ID = 20;
    private static final int ARTIST = 30;
    private static final int ARTIST_ID = 40;
    private static final int JOIN = 25;
    private static final int FAV = 50;
    private static final int FAV_ID = 55;
    private static final int TRACK =77;
    private static final int TRACK_ID =78;
    private static final int JOIN_TRACK = 79;
    private static final int SEARCH_ID = 15;
    private static final int SEARCH_FAV_ID = 63;

    private static final String AUTHORITY = "rs.reviewer";
    private static final String CONCERT_PATH = "concert";
    private static final String ARTIST_PATH = "artist";
    private static final String JOIN_PATH = "join_path";
    private static final String FAV_PATH = "fav_path";
    private static final String TRACK_PATH = "track_path";
    private static final String JOIN_TRACK_PATH = "join_track_path";
    private static final String SEARCH_PATH = "search_path";
    private static final String SEARCH_FAV_PATH = "search_fav_path";


    public static final Uri CONTENT_URI_CONCERT = Uri.parse("content://" + AUTHORITY + "/" + CONCERT_PATH);
    public static final Uri CONTENT_URI_SEARCH = Uri.parse("content://" + AUTHORITY + "/" + SEARCH_PATH);
    public static final Uri CONTENT_URI_SEARCH_FAV = Uri.parse("content://" + AUTHORITY + "/" + SEARCH_FAV_PATH);
    public static final Uri CONTENT_URI_ARTIST = Uri.parse("content://" + AUTHORITY + "/" + ARTIST_PATH);
    public static final Uri CONTENT_URI_JOIN = Uri.parse("content://" + AUTHORITY + "/" + JOIN_PATH);
    public static final Uri CONTENT_URI_FAV = Uri.parse("content://" + AUTHORITY + "/" + FAV_PATH);
    public static final Uri CONTENT_URI_TRACK = Uri.parse("content://" + AUTHORITY + "/" + TRACK_PATH);
    public static final Uri CONTENT_URI_TRACK_JOIN = Uri.parse("content://" + AUTHORITY + "/" + JOIN_TRACK_PATH);



    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, CONCERT_PATH, CONCERT);
        sURIMatcher.addURI(AUTHORITY, ARTIST_PATH, ARTIST);
        sURIMatcher.addURI(AUTHORITY, FAV_PATH, FAV);
        sURIMatcher.addURI(AUTHORITY, TRACK_PATH, TRACK);
        sURIMatcher.addURI(AUTHORITY, CONCERT_PATH + "/#", CONCERT_ID);
        sURIMatcher.addURI(AUTHORITY, SEARCH_PATH + "/*", SEARCH_ID);
        sURIMatcher.addURI(AUTHORITY, SEARCH_FAV_PATH + "/*", SEARCH_FAV_ID);
        sURIMatcher.addURI(AUTHORITY, ARTIST_PATH + "/#", ARTIST_ID);
        sURIMatcher.addURI(AUTHORITY, JOIN_PATH + "/#", JOIN);
        sURIMatcher.addURI(AUTHORITY, FAV_PATH + "/#", FAV_ID);
        sURIMatcher.addURI(AUTHORITY, TRACK_PATH + "/#", TRACK_ID);
        sURIMatcher.addURI(AUTHORITY, JOIN_TRACK_PATH + "/#", JOIN_TRACK);

    }

    @Override
    public boolean onCreate() {
        database = new ReviewerSQLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exist
        //checkColumns(projection);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CONCERT_ID:
                // Adding the ID to the original query

                queryBuilder.setTables(ReviewerSQLiteHelper.TABLE_CONCERT);

                queryBuilder.appendWhere(ReviewerSQLiteHelper.COLUMN_ID + "="
                        + uri.getLastPathSegment());
             //   queryBuilder.appendWhere(ReviewerSQLiteHelper.COLUMN_API_ID + "= ");
                //$FALL-THROUGH$
                break;

            case SEARCH_ID:
                // Adding the ID to the original query

                SQLiteDatabase db2 = database.getWritableDatabase();
                String[] args2 = {String.valueOf(uri.getLastPathSegment())};

                String argsParam1 = args2[0];


                Cursor cursor2 = db2.rawQuery("SELECT * FROM  concert " +
                        "WHERE name LIKE ?", new String[]{"%" + argsParam1 + "%"});

                cursor2.setNotificationUri(getContext().getContentResolver(), uri);

                System.out.println("Cursor count "+cursor2.getCount());

                //$FALL-THROUGH$
                return cursor2;
            case SEARCH_FAV_ID:
                // Adding the ID to the original query

                SQLiteDatabase db3 = database.getWritableDatabase();
                String[] args3 = {String.valueOf(uri.getLastPathSegment())};

                String argsParam3 = args3[0];


                Cursor cursor3 = db3.rawQuery("SELECT * FROM  favourites " +
                        "WHERE name_fav LIKE ?", new String[]{"%" + argsParam3 + "%"});

                cursor3.setNotificationUri(getContext().getContentResolver(), uri);

                System.out.println("Cursor count "+cursor3.getCount());

                //$FALL-THROUGH$
                return cursor3;
            case ARTIST_ID:
                // Adding the ID to the original query
                queryBuilder.setTables(ReviewerSQLiteHelper.TABLE_ARTIST);
                queryBuilder.appendWhere(ReviewerSQLiteHelper.COLUMN_ARTIST_ID + "="
                        + uri.getLastPathSegment());
                //$FALL-THROUGH$
                break;
            case TRACK_ID:
                // Adding the ID to the original query
                queryBuilder.setTables(ReviewerSQLiteHelper.TABLE_TRACKS);
                queryBuilder.appendWhere(ReviewerSQLiteHelper.COLUMN_TRACK_ID + "="
                        + uri.getLastPathSegment());
                //$FALL-THROUGH$
                break;
            case CONCERT:
                // Set the table
                queryBuilder.setTables(ReviewerSQLiteHelper.TABLE_CONCERT);

                break;
            case ARTIST:
                // Set the table
                queryBuilder.setTables(ReviewerSQLiteHelper.TABLE_ARTIST);
                break;
            case TRACK:
                // Set the table
                queryBuilder.setTables(ReviewerSQLiteHelper.TABLE_TRACKS);
                break;
            case FAV:
                // Set the table
                queryBuilder.setTables(ReviewerSQLiteHelper.TABLE_FAV);
                break;


            case JOIN:


                SQLiteDatabase db = database.getWritableDatabase();
                String[] args = {String.valueOf(uri.getLastPathSegment())};


                Cursor cursor = db.rawQuery(
                                "SELECT * FROM  artist a, concert c " +
                                "WHERE a.concert_id = c.api_id AND c._id = ?", args);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                System.out.println("Cursor count "+cursor.getCount());

                return cursor;

            case JOIN_TRACK:


                SQLiteDatabase db1 = database.getWritableDatabase();
                String[] args1 = {String.valueOf(uri.getLastPathSegment())};


                Cursor cursor1 = db1.rawQuery(
                        "SELECT * FROM  tracks t, artist a " +
                                "WHERE t.artist_track = a.concert_id AND a.artist_id = ?", args1);
                cursor1.setNotificationUri(getContext().getContentResolver(), uri);

                System.out.println("Cursor count "+cursor1.getCount());
                System.out.println("Args "+args1);
                System.out.println("Uri "+uri);

                return cursor1;



            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri retVal = null;
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case CONCERT:
                id = sqlDB.insert(ReviewerSQLiteHelper.TABLE_CONCERT, null, values);
                retVal = Uri.parse(CONCERT_PATH + "/" + id);
                break;
            case FAV:
                id = sqlDB.insert(ReviewerSQLiteHelper.TABLE_FAV, null, values);
                retVal = Uri.parse(FAV_PATH + "/" + id);
                break;
            case ARTIST:
                id = sqlDB.insert(ReviewerSQLiteHelper.TABLE_ARTIST, null, values);
                retVal = Uri.parse(ARTIST_PATH + "/" + id);
                break;
            case TRACK:
                id = sqlDB.insert(ReviewerSQLiteHelper.TABLE_TRACKS, null, values);
                retVal = Uri.parse(TRACK_PATH + "/" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        int rowsDeleted = 0;
        switch (uriType) {
            case CONCERT:
                rowsDeleted = sqlDB.delete(ReviewerSQLiteHelper.TABLE_CONCERT,
                        selection,
                        selectionArgs);
                break;
            case CONCERT_ID:
                String idCinema = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ReviewerSQLiteHelper.TABLE_CONCERT,
                            ReviewerSQLiteHelper.COLUMN_ID + "=" + idCinema,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ReviewerSQLiteHelper.TABLE_CONCERT,
                            ReviewerSQLiteHelper.COLUMN_ID + "=" + idCinema
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
                case FAV_ID:
                String idCinema1 = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ReviewerSQLiteHelper.TABLE_FAV,
                            ReviewerSQLiteHelper.COLUMN_FAV_ID + "=" + idCinema1,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ReviewerSQLiteHelper.TABLE_CONCERT,
                            ReviewerSQLiteHelper.COLUMN_ID + "=" + idCinema1
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        int rowsUpdated = 0;
        switch (uriType) {
            case CONCERT:
                rowsUpdated = sqlDB.update(ReviewerSQLiteHelper.TABLE_CONCERT,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CONCERT_ID:
                String idCinema = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ReviewerSQLiteHelper.TABLE_CONCERT,
                            values,
                            ReviewerSQLiteHelper.COLUMN_ID + "=" + idCinema,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ReviewerSQLiteHelper.TABLE_CONCERT,
                            values,
                            ReviewerSQLiteHelper.COLUMN_ID + "=" + idCinema
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }



}
