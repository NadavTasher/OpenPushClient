package nadav.tasher.openpush.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import java.util.Random;

import nadav.tasher.openpush.R;

public abstract class Notifier {

    public static void createChannel(Context context) {
        // Fetch resources
        Resources resources = context.getResources();
        String name = resources.getString(R.string.channel_name);
        // Manager
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(name, name, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

    public static void createNotification(Context context, String title, String message) {
        // Fetch resources
        Resources resources = context.getResources();
        String name = resources.getString(R.string.channel_name);
        // Manager
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Build notification
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, name);
        } else {
            builder = new Notification.Builder(context);
        }
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle(title);
        builder.setContentText(message);
        // Send
        manager.notify(new Random().nextInt(), builder.build());
    }

}
