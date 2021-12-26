package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // variables
    private ListView mNewsList;
    private AsyncTask asyncTask;
    private ArrayList<String> newsArrayList;
    private ArrayAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsArrayList = new ArrayList<>();
        mNewsList = findViewById(R.id.lvNewsHeadlines);

        newsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, newsArrayList);
        mNewsList.setAdapter(newsAdapter);

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            String result = "";

            try {
                url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }
                Log.i("News", result);

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}