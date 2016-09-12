package rs.reviewer.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import rs.reviewer.MainActivity;
import rs.reviewer.R;
import rs.reviewer.activities.ReviewerPreferenceActivity;
import rs.reviewer.tools.ReviewerTools;

/**
 * Created by milossimic on 4/6/16.
 */

public class SyncReceiver extends BroadcastReceiver {

    private static int notificationID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean allowSyncNotif = sharedPreferences.getBoolean(context.getString(R.string.notif_on_sync_key), false);
        boolean allowFavNotif = sharedPreferences.getBoolean(context.getString(R.string.notif_for_fav_concerts_key), false);

        if(intent.getAction().equals(MainActivity.SYNC_DATA)){
            if(allowSyncNotif){
                int resultCode = intent.getExtras().getInt(SyncService.RESULT_CODE);

                Bitmap bm = null;

                Intent wiFiintent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, wiFiintent, 0);

                Intent settingsIntent = new Intent(context, ReviewerPreferenceActivity.class);
                PendingIntent pIntentSettings = PendingIntent.getActivity(context, 0, settingsIntent, 0);

                if(resultCode == ReviewerTools.TYPE_NOT_CONNECTED){
                    bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_network_wifi);
                    mBuilder.setSmallIcon(R.drawable.ic_action_error);
                    mBuilder.setContentTitle(context.getString(R.string.autosync_problem));
                    mBuilder.setContentText(context.getString(R.string.no_internet));
                    mBuilder.addAction(R.drawable.ic_action_network_wifi, context.getString(R.string.turn_wifi_on), pIntent);
                    mBuilder.addAction(R.drawable.ic_action_settings, context.getString(R.string.turn_notif_on), pIntentSettings);
                }else if(resultCode == ReviewerTools.TYPE_MOBILE){
                    bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_network_cell);
                    mBuilder.setSmallIcon(R.drawable.ic_action_warning);
                    mBuilder.setContentTitle(context.getString(R.string.autosync_warning));
                    mBuilder.setContentText(context.getString(R.string.connect_to_wifi));
                    mBuilder.addAction(R.drawable.ic_action_network_wifi, context.getString(R.string.turn_wifi_on), pIntent);
                    mBuilder.addAction(R.drawable.ic_action_settings, context.getString(R.string.turn_notif_on), pIntentSettings);
                }else{
                    bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
                    mBuilder.setSmallIcon(R.drawable.ic_action_refresh_w);
                    mBuilder.setContentTitle(context.getString(R.string.autosync));
                    mBuilder.setContentText(context.getString(R.string.good_news_sync));
                    mBuilder.addAction(R.drawable.ic_action_settings, context.getString(R.string.turn_notif_on), pIntentSettings);
                }


                mBuilder.setLargeIcon(bm);
                // notificationID allows you to update the notification later on.

                notificationID = notificationID +1;
                mNotificationManager.notify(notificationID, mBuilder.build());
            }
        }else if(intent.getAction().equals(MainActivity.FAV_NOTIFICATION)){
            if(allowFavNotif){





              //  int resultCode = intent.getExtras().getInt(SyncService.RESULT_CODE);

                String title = intent.getStringExtra("title");
                String venue = intent.getStringExtra("venue");
                String datetime = intent.getStringExtra("datetime");

                Intent settingsIntent = new Intent(context, ReviewerPreferenceActivity.class);
                PendingIntent pIntentSettings = PendingIntent.getActivity(context, 0, settingsIntent, 0);

                Bitmap bm = null;

                    bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.cf);
                    mBuilder.setSmallIcon(R.drawable.cf);
                    mBuilder.setContentTitle(context.getString(R.string.fav));
                    mBuilder.setContentText(title);


                ////

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();
                String[] events = new String[3];

                // Sets a title for the Inbox in expanded layout
                inboxStyle.setBigContentTitle("Favourite concert today!");

                // Moves events into the expanded layout
               events[0]= title;
                events[1] = "Place: "+venue;
                events[2] = "Time: "+datetime;
                for (int i=0; i < events.length; i++) {
                   // events[i]= "Trala";
                   inboxStyle.addLine(events[i]);
                }
                // Moves the expanded layout object into the notification object.
                mBuilder.setStyle(inboxStyle);



                  //  mBuilder.setSubText("Tap to view documentation about notifications.");
                    mBuilder.addAction(R.drawable.ic_action_settings, context.getString(R.string.turn_notif_on), pIntentSettings);

                    notificationID = notificationID +1;


                mBuilder.setLargeIcon(bm);
                // notificationID allows you to update the notification later on.
                mNotificationManager.notify(notificationID, mBuilder.build());

            }

        }

    }

}
