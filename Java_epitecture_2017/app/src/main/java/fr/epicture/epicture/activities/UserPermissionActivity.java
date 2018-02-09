package fr.epicture.epicture.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import fr.epicture.epicture.R;

public class UserPermissionActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_URL_RESPONSE = "url_response";
    public static final String EXTRA_SEARCHFOR = "search_for";

    public static void setExtraUrl(Intent intent, String url) {
        intent.putExtra(EXTRA_URL, url);
    }

    public static String getExtraUrl(Intent intent) {
        return intent.getStringExtra(EXTRA_URL);
    }

    public static void setExtraUrlResponse(Intent intent, String url) {
        intent.putExtra(EXTRA_URL_RESPONSE, url);
    }

    public static String getExtraUrlResponse(Intent intent) {
        return intent.getStringExtra(EXTRA_URL_RESPONSE);
    }

    public static void setExtraSearchfor(Intent intent, String searchFor) {
        intent.putExtra(EXTRA_SEARCHFOR, searchFor);
    }

    public static String getExtraSearchfor(Intent intent) {
        return intent.getStringExtra(EXTRA_SEARCHFOR);
    }

    private String url;
    private String searchFor;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_permission_activity);

        url = getExtraUrl(getIntent());
        searchFor = getExtraSearchfor(getIntent());

        webView = (WebView)findViewById(R.id.webview);
        //webView.clearCache(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setDomStorageEnabled(true);
        //webSettings.setSaveFormData(false);

        start();
    }

    private void start() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("url", url);
                if (url.contains(searchFor)) {
                    setExtraUrlResponse(getIntent(), url);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }
        });
        webView.loadUrl(url);
    }
}
