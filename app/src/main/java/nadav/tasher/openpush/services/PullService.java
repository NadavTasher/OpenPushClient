package nadav.tasher.openpush.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import nadav.tasher.openpush.R;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PullService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start foreground
        startForeground(1, buildNotification(null, "Idle pull service", getApplicationContext().getResources().getString(R.string.channel_foreground), NotificationManager.IMPORTANCE_NONE));
        // Start a timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pullMessages();
            }
        }, 1000, 1000 * 60 * 5);
        return START_NOT_STICKY;
    }

    private void pullMessages() {
        // Fetch notifications
        try {
            // Fetch token
            String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", null);
            // Check token
            if (token != null) {
                // Build client
                OkHttpClient client = new OkHttpClient.Builder().connectionSpecs(Arrays.asList(ConnectionSpec.RESTRICTED_TLS, ConnectionSpec.MODERN_TLS)).build();
                // Create the request
                Request request = new Request.Builder().url(getApplicationContext().getResources().getString(R.string.address) + "/apis/pull/?null&token=" + token).get().build();
                // Send the request
                Response response = client.newCall(request).execute();
                // Parse response
                // Decode JSON
                JSONObject object = new JSONObject(response.body().string());
                // Validate structure
                if (object.has("status") && object.has("result")) {
                    boolean success = object.getBoolean("status");
                    Object result = object.get("result");
                    // Check success
                    if (success) {
                        JSONArray array = (JSONArray) result;
                        // Loop over
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonMessage = array.getJSONObject(i);
                            // Store title and message
                            String title = jsonMessage.getString("title");
                            String message = jsonMessage.getString("message");
                            // Send notification
                            sendNotification(title, message);
                        }
                    } else {
                        // Notify failure
                        sendNotification("Error", (String) result);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private Notification buildNotification(String title, String message, String channel, int importance) {
        // Create builder
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), createChannel(channel, importance));
        // Set icon
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        // Set time
        builder.setShowWhen(true);
        // Set secret
        if (importance == NotificationManager.IMPORTANCE_NONE) {
            builder.setVisibility(Notification.VISIBILITY_SECRET);
        }
        // Set text
        if (title != null && !title.equals("null"))
            builder.setContentTitle(title);
        if (message != null && !message.equals("null"))
            builder.setContentText(message);
        // Build and return
        return builder.build();
    }

    private String createChannel(String id, int importance) {
        // Manager
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Create channel
        manager.createNotificationChannel(new NotificationChannel(id, id, importance));
        // Return the ID
        return id;
    }

    private void sendNotification(String title, String message) {
        // Fetch resources
        String name = getApplicationContext().getResources().getString(R.string.channel_pull);
        // Manager
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Send
        manager.notify(new Random().nextInt(), buildNotification(title, message, name, NotificationManager.IMPORTANCE_DEFAULT));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
