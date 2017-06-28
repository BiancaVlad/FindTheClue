package utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dissertation.findtheclue.QuestionActivity;
import com.dissertation.findtheclue.R;

/**
 * Created by Vlad on 26-Jun-17.
 */
public class ProximityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Key for determining whether user is leaving or entering
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        //Gives whether the user is entering or leaving in boolean form
        boolean state = intent.getBooleanExtra(key, false);

        if(state){
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder b = new NotificationCompat.Builder(context);

            b.setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.investi4)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.investi4))
                    .setContentTitle("Nearby Location")
                    .setContentText("You're getting close!")
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(1, b.build());

            Toast.makeText(context, "You're getting close!.", Toast.LENGTH_SHORT).show();
        //}else{
            //Other custom Notification
           // Log.i("MyTag", "Thank you for visiting my Area,come back again !!");
           // Toast.makeText(context, "Thank you for visiting my Area,come back again !!", Toast.LENGTH_SHORT).show();

            //Intent notifIntent = new Intent(context, QuestionActivity.class);

        }
    }


}
