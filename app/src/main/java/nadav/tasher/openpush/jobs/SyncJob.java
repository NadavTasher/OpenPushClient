package nadav.tasher.openpush.jobs;

import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import nadav.tasher.openpush.R;
import nadav.tasher.openpush.services.PullService;
import nadav.tasher.openpush.utils.Notifier;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SyncJob extends Job {

    public static final String TAG = "sync_job";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        // Fetch notifications
        try {
            // Fetch token
            String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token", null);
            // Check token
            if (token != null) {
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
                Request request = new Request.Builder().url(getContext().getResources().getString(R.string.address) + "/apis/pull/").post(new FormBody.Builder().add("api", APIs.toString()).build()).build();
                // Send the request
                Response response = client.newCall(request).execute();
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
                                    Notifier.createNotification(getContext(), title, message);
                                }
                            } else {
                                // Notify failure
                                Notifier.createNotification(getContext(), "Error", (String) result);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                // Return result
                return Result.SUCCESS;
            }
        } catch (Exception ignored) {
        }
        // Default result
        return Result.FAILURE;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(TAG).setExecutionWindow(300_000L, 10_000L).build().schedule();
    }
}