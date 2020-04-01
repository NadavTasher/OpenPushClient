package nadav.tasher.openpush.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import nadav.tasher.openpush.R;
import nadav.tasher.openpush.utils.API;
import nadav.tasher.openpush.utils.Notifier;
import nadav.tasher.openpush.utils.Preferences;

public class PullService extends IntentService {

    public PullService() {
        super(PullService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            // Fetch token
            String token = Preferences.getToken(this);
            // Send request
            API.send(this, "pull", null, null, new API.Call.Callback() {
                @Override
                public void success(Object result) {
                    try {
                        // Parse results
                        JSONArray array = (JSONArray) result;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            // Store title and message
                            String title = object.getString("title");
                            String message = object.getString("message");
                            // Send notification
                            Notifier.createNotification(PullService.this, title, message);
                        }
                    } catch (Exception ignored) {

                    }
                }

                @Override
                public void failure(String error) {

                }
            }, Collections.singletonList(new API.Call("authenticate", "authenticate", new JSONObject().put("token", token), null)));
        } catch (Exception ignored) {
        }
    }
}
