package nadav.tasher.openpush.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import nadav.tasher.openpush.services.PullService;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restart service
        startForegroundService(new Intent(this, PullService.class));
        // Create and show layout
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        // Create TextView
        TextView view = new TextView(getApplicationContext());
        view.setText("Click here to sign out");
        view.setTextSize(24);
        view.setGravity(Gravity.CENTER);
        // Set on click
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SignInActivity.class), 0);
            }
        });
        // Check for token state
        if (PreferenceManager.getDefaultSharedPreferences(this).getString("token", null) == null)
            layout.performClick();
        // Add to layout
        layout.addView(view);
        setContentView(layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check result
        if (resultCode == RESULT_OK) {
            // Get token
            String token = data.getDataString();
            // Save the token
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("token", token).apply();
        }
    }
}
