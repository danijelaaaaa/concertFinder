package rs.reviewer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import model.NavItem;
import rs.reviewer.activities.ReviewerPreferenceActivity;
import rs.reviewer.activities.SplashActivity;
import rs.reviewer.adapters.DrawerListAdapter;
import rs.reviewer.database.ReviewerSQLiteHelper;
import rs.reviewer.dialogs.LocationDialog;
import rs.reviewer.dialogs.WifiDialog;
import rs.reviewer.favourite.FavService;
import rs.reviewer.fragments.BlankFragment;
import rs.reviewer.fragments.FavFragment;
import rs.reviewer.fragments.MyFragment;
import rs.reviewer.fragments.PlacesFragment;
import rs.reviewer.sync.SyncReceiver;
import rs.reviewer.sync.SyncService;
import rs.reviewer.sync.SyncTask;
import rs.reviewer.tools.FragmentTransition;
import rs.reviewer.tools.ReviewerTools;
import rs.reviewer.tools.Util;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private DrawerLayout mDrawerLayout;
    private double latitudeGPS;
    private double longitudeGPS;
    private LocationManager locationManager;
    private String provider;
    private String getLatLong;
    private String location;

    //Sync stuff
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    private SyncReceiver sync;
    public static String SYNC_DATA = "SYNC_DATA";
    public static String FAV_NOTIFICATION = "FAV_NOTIFICATION";

    private String synctime;
    private boolean allowSync;
    private String lookupRadius;


    private boolean allowReviewNotif;
    private boolean allowFav;
    private SharedPreferences sharedPreferences;

    private boolean radius_changed;
    private boolean location_changed;


    @Override
    public void onLocationChanged(Location location) {
        latitudeGPS = location.getLatitude();
        longitudeGPS = location.getLongitude();


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout mDrawerPane;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    private AlertDialog dialog;
    private ProgressBar pb1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb1 = (ProgressBar) findViewById(R.id.progressbar_loading);


        prepareMenu(mNavItems);

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.navList);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setAdapter(adapter);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setIcon(R.drawable.ic_launcher);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
        }


//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon

        // OVO NE MORA DA SE KORISTI, UKOLIKO SE NE KORISTI
        // ONDA SE NE MENJA TEKST PRILIKOM OPEN CLOSE DRAWERA POGLEDATI JOS
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(mTitle);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setTitle("ConcertFinder");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
//        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItemFromDrawer(0);
        }


        /////////////////////////////////////////////////////
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        consultPreferences();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        int status = ReviewerTools.getConnectivityStatus(getApplicationContext());


        //is table empty
        if (checkForTables()) { //ako je prazna

            if (status != ReviewerTools.TYPE_WIFI) {

                showWifiDialog();

            } else {

                startSync();

            }

        }


        setUpReceiver();


        if (allowFav) {

            checkFavourites();
        }


    }

    private void checkFavourites() {
        Intent intent = new Intent(this, FavService.class);
        startService(intent);

    }


    private void setUpReceiver() {


        sync = new SyncReceiver();


        consultPreferences();
        consultLocation();

        // Retrieve a PendingIntent that will perform a broadcast


        Intent alarmIntent = new Intent(this, SyncService.class);
        pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        pb1 = (ProgressBar) findViewById(R.id.progressbar_loading);

        int status = ReviewerTools.getConnectivityStatus(getApplicationContext());

        if (checkForTables()) { //ako je prazna

            if (status != ReviewerTools.TYPE_WIFI) {

                showWifiDialog();


            } else {

                startSync();

            }
        }


        if (ReviewerPreferenceActivity.radiusChanged) {
            consultPreferences();
            ReviewerPreferenceActivity.favChanged = false;
            standardStartSync();


        }


        if (ReviewerPreferenceActivity.favChanged) {
            consultPreferences();
            if (allowFav) {
                ReviewerPreferenceActivity.favChanged = false;
                checkFavourites();

            }

        }

        if (manager == null) {
            setUpReceiver();


        }

        consultPreferences();


        if (allowSync) {


            int interval = ReviewerTools.calculateTimeTillNextSync(Integer.parseInt(synctime));
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
          //  Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(SYNC_DATA);
        filter.addAction(FAV_NOTIFICATION);

        registerReceiver(sync, filter);

        //   locationManager.requestLocationUpdates(provider, 400, 1, this);


    }

    public void standardStartSync() {
        int status = ReviewerTools.getConnectivityStatus(getApplicationContext());
        if (status != ReviewerTools.TYPE_WIFI) {

            showWifiDialog();
        } else {

            consultPreferences();
            consultLocation();


//            Intent intent = new Intent(this, SyncService.class);
//            startService(intent);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar_loading);
            new SyncTask(getApplicationContext(), pb).execute();


        }
    }


    private void prepareMenu(ArrayList<NavItem> mNavItems) {
        mNavItems.add(new NavItem(getString(R.string.concerts), getString(R.string.concerts_long), R.drawable.ic_action_map));
        mNavItems.add(new NavItem(getString(R.string.favourites), getString(R.string.favourites_long), R.drawable.star_none));
        mNavItems.add(new NavItem(getString(R.string.setings), getString(R.string.setings_long), R.drawable.ic_action_settings));
        mNavItems.add(new NavItem(getString(R.string.sync_data), getString(R.string.sync_data_long), R.drawable.ic_action_refresh));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_itemdetail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, ReviewerPreferenceActivity.class);
                startActivity(i);

                return true;

            case R.id.action_location:
                FragmentTransition.to(BlankFragment.newInstance(), this, false);
                setTitle("Change location");
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }




    private void selectItemFromDrawer(int position) {
        if (position == 0) {
            FragmentTransition.to(MyFragment.newInstance(), this, false);
        } else if (position == 1) {

            FragmentTransition.to(FavFragment.newInstance(), this, false);

        } else if (position == 2) {

            Intent preference = new Intent(MainActivity.this, ReviewerPreferenceActivity.class);
            startActivity(preference);

        } else if (position == 3) {
            standardStartSync();

        } else {

            Log.e("DRAWER", "Nesto van opsega!");
        }

        mDrawerList.setItemChecked(position, true);
        if (position != 3 ) // za sve osim za sync
        {
            setTitle(mNavItems.get(position).getmTitle());
        }
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }

        //osloboditi resurse
        if (sync != null) {
            unregisterReceiver(sync);
        }

        locationManager.removeUpdates(this);


        super.onPause();

    }


    public void startSync() {


        if (checkGPS()) {

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("location", String.valueOf(latitudeGPS) + "," + String.valueOf(longitudeGPS));
            editor.putString("getLatLong", "false");
            editor.commit();
//            Intent intent = new Intent(this, SyncService.class);
//            startService(intent);

            ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar_loading);
            new SyncTask(getApplicationContext(), pb).execute();


        } else {

            showLocatonDialog();

            FragmentTransition.to(BlankFragment.newInstance(), this, false);


        }


    }


    public void startSyncFromFragment(String radius, String location) {


        ///potrebno ponovo podesiti sinhronizaciju za novu lokaciju

//        Intent intent = new Intent(this, SyncService.class);
//        startService(intent);


        // ProgressBar pb =(ProgressBar) findViewById(R.id.progressbar_loading);


        //   new SyncTask(getApplicationContext(),pb1).execute();
    }

    public Boolean checkGPS() {


        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);


        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);


        //getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (!isGPSEnabled && !isNetworkEnabled) {
            System.out.print("Cannot determine location - neither network nor gps is enabled.");
            Toast.makeText(getApplicationContext(), "Cannot determine location - neither network nor gps is enabled",
                    Toast.LENGTH_LONG).show();
        } else {
            //this.canGetLocation = true;
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000,
                        10, this);
                Log.d("Network", "Network Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000,
                            10, this);
                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);


                    }
                }
            }
        }

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");

            System.out.println(location);


            onLocationChanged(location);

            return true;

        } else {

            return false;


        }

    }


    public boolean checkForTables() {

        ReviewerSQLiteHelper dbHelper = new ReviewerSQLiteHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + ReviewerSQLiteHelper.TABLE_CONCERT, null);

        if (cursor != null) {

            cursor.moveToFirst();

            int count = cursor.getInt(0);

            if (count > 0) {
                return false;
            }

            cursor.close();
        }

        return true;
    }


    private void consultLocation() {
        location = sharedPreferences.getString("location", String.valueOf(latitudeGPS) + "," + String.valueOf(longitudeGPS));
        System.out.println("Lokacija " + location);
        getLatLong = sharedPreferences.getString("getLatLong", "false");


    }

    private void consultPreferences() {
        synctime = sharedPreferences.getString(getString(R.string.pref_sync_list), "1");// pola minuta


        allowSync = sharedPreferences.getBoolean(getString(R.string.pref_sync), false);



        lookupRadius = sharedPreferences.getString(getString(R.string.pref_radius), "5");



        allowFav = sharedPreferences.getBoolean(getString(R.string.notif_for_fav_concerts_key), false);


        //Toast.makeText(MainActivity.this, allowSync + " " + lookupRadius + " " + synctime, Toast.LENGTH_SHORT).show();
    }


    private void showLocatonDialog() {
        if (dialog == null) {
            dialog = new LocationDialog(MainActivity.this).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        dialog.show();
    }


    private void showWifiDialog() {
        if (dialog == null) {
            dialog = new WifiDialog(MainActivity.this).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        dialog.show();
    }

}
