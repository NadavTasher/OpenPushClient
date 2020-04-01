package nadav.tasher.openpush.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import nadav.tasher.openpush.R;

public abstract class Preferences {

    private static String get(Context context, int keyId, int defaultId) {
        // Load resources
        Resources resources = context.getResources();
        // Load key-value datastore
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Try loading the value
        String key = resources.getString(keyId);
        String value = resources.getString(defaultId);
        return preferences.getString(key, value);
    }

    private static void set(Context context, int keyId, String value) {
        Resources resources = context.getResources();
        // Load key-value datastore
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Set
        preferences.edit().putString(resources.getString(keyId), value).apply();
    }

    public static String getURL(Context context) {
        return get(context, R.string.key_server_address, R.string.default_server_address);
    }

    public static String getToken(Context context){
        return get(context, R.string.key_authentication_token, R.string.default_authentication_token);
    }

}
