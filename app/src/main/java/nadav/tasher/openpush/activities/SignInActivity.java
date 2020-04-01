package nadav.tasher.openpush.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import nadav.tasher.openpush.R;

public class SignInActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup WebView
        WebView webView = new WebView(getApplicationContext());
        // Setup Javascript storage interface
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void setItem(String key, String value) {
                // Return result
                Intent data = new Intent();
                data.setData(Uri.parse(value));
                // Set the result
                setResult(RESULT_OK, data);
                // Finish
                finish();
            }

            @JavascriptInterface
            public String getItem(String key) {
                return null;
            }
        }, "localStorage");
        // Setup Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // Load url
        webView.loadUrl(getResources().getString(R.string.address));
        // Set content view
        setContentView(webView);
    }
}
