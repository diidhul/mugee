package com.mugee;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private boolean isWhatsAppRedirected = false; // Flag untuk mengelola redirect ke WhatsApp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView.setWebViewClient(new MyWebClient());
        myWebView.loadUrl("https://www.mugee.id/");
    }

    private class MyWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.startsWith("https://wa.link/") || url.startsWith("https://api.whatsapp.com/send?phone=")) {
                if (!isWhatsAppRedirected) {
                    // Buka chat WhatsApp
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setPackage("com.whatsapp");
                        startActivity(intent);
                        isWhatsAppRedirected = true; // Tandai kalo WA udah di-redirect
                    } catch (android.content.ActivityNotFoundException e) {
                        // WhatsApp ga terinstall
                        Toast.makeText(MainActivity.this, "WhatsApp tidak terinstal di perangkat ini.", Toast.LENGTH_SHORT).show();
                    }
                }
                return true; // URL udah di handle ga perlu dimuat oleh WebView
            }
            isWhatsAppRedirected = false; // Reset flag untuk URL lain
            view.loadUrl(url); // URL ga perlu di-handle, muat dengan WebView
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            isWhatsAppRedirected = false; // Reset flag selepas halaman selesai dimuat
        }
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
