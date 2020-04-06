package nadav.tasher.openpush.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import nadav.tasher.openpush.R;
import nadav.tasher.openpush.utils.Notifier;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PullService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start the timer
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    pullMessages();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 30);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void pullMessages() throws JSONException {
        // Fetch token
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString("token", null);
        // Build client
        OkHttpClient client = new OkHttpClient.Builder().connectionSpecs(Arrays.asList(ConnectionSpec.RESTRICTED_TLS, ConnectionSpec.MODERN_TLS)).build();
        // Build the API list
        JSONObject APIs = new JSONObject();
        // Add the pull API
        JSONObject pullAPI = new JSONObject();
        pullAPI.put("action", null);
        pullAPI.put("parameters", null);
        APIs.put("pull", pullAPI);
        // Add the authenticate API
        JSONObject authenticateAPI = new JSONObject();
        authenticateAPI.put("action", "authenticate");
        authenticateAPI.put("parameters", new JSONObject().put("token", token));
        APIs.put("authenticate", authenticateAPI);
        // Create the request
        Request request = new Request.Builder().url(getResources().getString(R.string.address) + "/apis/pull/").post(new FormBody.Builder().add("api", APIs.toString()).build()).build();
        // Send the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                // Parse response
                try {
                    // Decode JSON
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.has("pull")) {
                        // Store the layer
                        JSONObject layer = object.getJSONObject("pull");
                        // Validate structure
                        if (layer.has("success") && layer.has("result")) {
                            boolean success = layer.getBoolean("success");
                            Object result = layer.get("result");
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
                                    Notifier.createNotification(PullService.this, title, message);
                                }
                            } else {
                                // Notify failure
                                Notifier.createNotification(PullService.this, "Error", (String) result);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

}
