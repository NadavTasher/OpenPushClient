package nadav.tasher.openpush.activities;

import android.app.Activity;
import android.os.Bundle;

import nadav.tasher.openpush.utils.Starter;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restart service
        Starter.stopService(getApplicationContext());
        Starter.startService(getApplicationContext());
    }
}
