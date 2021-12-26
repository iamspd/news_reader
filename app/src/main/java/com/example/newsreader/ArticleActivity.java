package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleActivity extends AppCompatActivity {

    // widgets
    private WebView articleWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        articleWebView = findViewById(R.id.webView);

        articleWebView.getSettings().setJavaScriptEnabled(true);
        articleWebView.setWebViewClient(new WebViewClient());

        Intent intent = getIntent();
        String html = intent.getStringExtra("content");

        articleWebView.loadData(html, "text/html", "UTF-8");
    }
}