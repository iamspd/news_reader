package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private SQLiteDatabase articleDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsArrayList = new ArrayList<>();
        mNewsList = findViewById(R.id.lvNewsHeadlines);

        newsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, newsArrayList);
        mNewsList.setAdapter(newsAdapter);

        articleDB = this.openOrCreateDatabase("Article", MODE_PRIVATE, null);
        articleDB.execSQL("CREATE TABLE IF NOT EXISTS articles " +
                "(id INT PRIMARY KEY, articleId INT, title VARCHAR, content VARCHAR)");

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
               /* Log.i("News", result);*/

                JSONArray newsIdJsonArray = new JSONArray(result);

                int totalCount = 20;

                if (newsIdJsonArray.length() < 20) {
                    totalCount = newsIdJsonArray.length();
                }

                articleDB.execSQL("DELETE FROM articles");

                for (int i = 0; i < totalCount; i++) {
                   // Log.i("ArrayItems", newsIdJsonArray.getString(i));

                    String newsItemId = newsIdJsonArray.getString(i);

                    url = new URL(" https://hacker-news.firebaseio.com/v0/item/" + newsItemId + ".json?print=pretty");
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    inputStream = httpURLConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);

                    int newsData = inputStreamReader.read();
                    String newsDataItems = "";

                    while (newsData != -1) {
                        char currentNewsData = (char) newsData;
                        newsDataItems += currentNewsData;
                        newsData = inputStreamReader.read();
                    }

                    // Log.i("News", newsDataItems);

                    JSONObject newsJsonObject = new JSONObject(newsDataItems);
                    if (!newsJsonObject.isNull("title") && !newsJsonObject.isNull("url")) {
                        String articleTitle = newsJsonObject.getString("title");
                        String articleURL = newsJsonObject.getString("url");

                        // Log.i("URLTitle", articleTitle + articleURL);

                        url = new URL(articleURL);
                        httpURLConnection = (HttpURLConnection) url.openConnection();

                        inputStream = httpURLConnection.getInputStream();
                        inputStreamReader = new InputStreamReader(inputStream);

                        data = inputStreamReader.read();
                        StringBuilder articleInfo = new StringBuilder();

                        while (data != -1) {
                            char currentArticleInfo = (char) data;
                            articleInfo.append(currentArticleInfo);

                            data = inputStreamReader.read();
                        }

                        Log.i("info", articleInfo.toString());

                        String sql = "INSERT INTO articles (articleId, title, content) VALUES (?, ?, ?)";

                        SQLiteStatement sqLiteStatement = articleDB.compileStatement(sql);
                        sqLiteStatement.bindString(1, newsItemId);
                        sqLiteStatement.bindString(2, articleTitle);
                        sqLiteStatement.bindString(3, articleInfo.toString());

                        sqLiteStatement.execute();
                    }

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}