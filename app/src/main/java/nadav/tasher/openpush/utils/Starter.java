package nadav.tasher.openpush.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import nadav.tasher.openpush.receivers.PullReceiver;
import nadav.tasher.openpush.services.PullService;

public abstract class Starter {

    private static PendingIntent createIntent(Context context) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, PullReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void startService(Context context) {
        // Create the manager
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Schedule tasks //60 * 1000 * 5
        alarm.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60 * 1000, createIntent(context));
    }

}
