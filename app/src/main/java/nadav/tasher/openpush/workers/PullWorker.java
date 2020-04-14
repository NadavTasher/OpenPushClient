package nadav.tasher.openpush.workers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import nadav.tasher.openpush.R;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PullWorker extends Worker {

    public PullWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
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
                            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(new Random().nextInt(), buildNotification(title, message, getApplicationContext().getResources().getString(R.string.channel_pull)));
                        }
                    }
                }
            }
            return Result.success();
        } catch (Exception ignored) {
            return Result.failure();
        }
    }

    private Notification buildNotification(String title, String message, String channel) {
        // Create builder
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), createChannel(channel));
        // Set icon
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        // Set time
        builder.setShowWhen(true);
        // Set text
        if (title != null && !title.equals("null"))
            builder.setContentTitle(title);
        if (message != null && !message.equals("null"))
            builder.setContentText(message);
        // Build and return
        return builder.build();
    }

    private String createChannel(String id) {
        // Manager
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Create channel
        manager.createNotificationChannel(new NotificationChannel(id, id, NotificationManager.IMPORTANCE_HIGH));
        // Return the ID
        return id;
    }

    public static void enqueueWork(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWork();
        workManager.enqueue(new PeriodicWorkRequest.Builder(PullWorker.class, 15, TimeUnit.MINUTES).setInitialDelay(1, TimeUnit.MINUTES).setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build());
    }
}