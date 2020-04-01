package nadav.tasher.openpush.utils;

import android.content.Context;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class API {

    /**
     * Sends an API call.
     *
     * @param context    Context
     * @param endpoint   Target API
     * @param action     Request action
     * @param parameters Request parameters
     * @param callback   Request callback
     * @param APIs       API list
     */
    public static void send(Context context, String endpoint, String action, JSONObject parameters, Call.Callback callback, List<Call> APIs) throws JSONException {
        call(context, endpoint, hook(endpoint, action, parameters, callback, APIs));
    }

    /**
     * Sends an API list.
     *
     * @param context  Context
     * @param endpoint Target API
     * @param APIs     API list
     */
    public static void call(Context context, String endpoint, final List<Call> APIs) {
        // Fetch base URL
        String url = Preferences.getURL(context);
        if (!url.startsWith("https://")) {
            url = "https://" + url;
        }
        // Create the client (HTTPS only)
        OkHttpClient client = new OkHttpClient.Builder().connectionSpecs(Arrays.asList(ConnectionSpec.RESTRICTED_TLS, ConnectionSpec.MODERN_TLS)).build();
        // Build the API list
        JSONObject APIStack = new JSONObject();
        // Loop over APIs
        for (Call call : APIs) {
            try {
                // Push layer
                APIStack.put(call.API, call.request);
            } catch (Exception e) {
                if (call.callback != null) {
                    call.callback.failure(e.toString());
                }
            }
        }
        // Create the request
        Request request = new Request.Builder().url(url + "/apis/" + endpoint + "/").post(new FormBody.Builder().add("api", APIStack.toString()).build()).build();
        // Send the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                // Report error
                for (Call API : APIs) {
                    if (API.callback != null) {
                        API.callback.failure(e.toString());
                    }
                }
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                // Parse response
                try {
                    // Decode JSON
                    JSONObject object = new JSONObject(response.body().string());
                    // Loop over APIs
                    for (Call API : APIs) {
                        // Check existence
                        if (object.has(API.API)) {
                            // Store the layer
                            JSONObject layer = object.getJSONObject(API.API);
                            // Validate structure
                            if (layer.has("success") && layer.has("result")) {
                                if (API.callback != null) {
                                    if (layer.getBoolean("success"))
                                        API.callback.success(layer.get("result"));
                                    else
                                        API.callback.failure(layer.getString("result"));
                                }
                            } else {
                                if (API.callback != null) {
                                    API.callback.failure("API parameters not found");
                                }
                            }
                        } else {
                            if (API.callback != null) {
                                API.callback.failure("API not found");
                            }
                        }
                    }
                } catch (Exception e) {
                    // Report error
                    for (Call API : APIs) {
                        if (API.callback != null) {
                            API.callback.failure(e.toString());
                        }
                    }
                }
            }
        });
    }

    /**
     * Combines parameters into an API list.
     *
     * @param endpoint   Target API
     * @param action     Request action
     * @param parameters Request parameters
     * @param callback   Request callback
     * @param APIs       API list
     * @return API list
     */
    public static List<Call> hook(String endpoint, String action, JSONObject parameters, Call.Callback callback, List<Call> APIs) throws JSONException {
        // Validate list
        if (APIs == null) {
            APIs = new ArrayList<>();
        }
        // Append new call
        APIs.add(new Call(endpoint, action, parameters, callback));
        // Return list
        return APIs;
    }

    /**
     * Represents an API layer during a request.
     */
    public static class Call {

        private String API;
        private JSONObject request;
        private Callback callback;

        /**
         * Constructs a new call.
         *
         * @param API        API name
         * @param action     Request action
         * @param parameters Request parameters
         * @param callback   Request callback
         */
        public Call(String API, String action, JSONObject parameters, Callback callback) throws JSONException {
            this.API = API;
            this.request = new JSONObject();
            this.request.put("action", action);
            this.request.put("parameters", parameters);
            this.callback = callback;
        }

        /**
         * Used as a callback interface for API layers.
         */
        public interface Callback {
            /**
             * Success handler.
             *
             * @param result Result
             */
            void success(Object result);

            /**
             * Failure handler.
             *
             * @param error Error message
             */
            void failure(String error);
        }
    }

}
