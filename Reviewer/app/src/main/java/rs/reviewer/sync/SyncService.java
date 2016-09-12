package rs.reviewer.sync;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import rs.reviewer.R;
import rs.reviewer.tools.ReviewerTools;

/**
 * Created by milossimic on 4/6/16.
 */
public class SyncService extends Service {

    public static String RESULT_CODE = "RESULT_CODE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Intent ints = new Intent(MainActivity.SYNC_DATA);
        int status = ReviewerTools.getConnectivityStatus(getApplicationContext());

        //ints.putExtra(RESULT_CODE, status);

        //ima konekcije ka netu skini sta je potrebno i sinhronizuj bazu
        if(status == ReviewerTools.TYPE_WIFI){

//            String radius = intent.getStringExtra("radius");
//            String location = intent.getStringExtra("location");
//            String getLatLong = intent.getStringExtra("getLatLong");

           // String latitude = intent.getStringExtra("latitude");


//             LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//            View layout = inflater.inflate(R.layout.map_layout, null);
//



            new SyncTask(getApplicationContext(),null).execute();




        }

        //sendBroadcast(ints);

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
