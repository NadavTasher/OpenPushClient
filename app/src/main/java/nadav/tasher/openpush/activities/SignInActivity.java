package nadav.tasher.openpush.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import nadav.tasher.openpush.R;

public class SignInActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup WebView
        WebView webView = new WebView(this);
        // Setup client
        webView.setWebViewClient(new WebViewClient());
        // Setup Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        // Setup Javascript storage interface
        webView.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void setToken(String value) {
                // Return result
                Intent data = new Intent();
                data.setData(Uri.parse(value));
                // Set the result
                setResult(RESULT_OK, data);
                // Finish
                finish();
            }

        }, "android");
        // Load url
        webView.loadUrl(getResources().getString(R.string.address));
        // Set content view
        setContentView(webView);
    }
}
