package rs.reviewer.favourite;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import rs.reviewer.sync.SyncTask;
import rs.reviewer.tools.ReviewerTools;

/**
 * Created by Danijela on 9/10/2016.
 */
public class FavService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        new FavTask(getApplicationContext()).execute();



        //sendBroadcast(ints);

        stopSelf();

        return START_NOT_STICKY;
    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
