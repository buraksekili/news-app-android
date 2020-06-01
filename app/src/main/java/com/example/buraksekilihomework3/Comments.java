package com.example.buraksekilihomework3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Comments extends AppCompatActivity {

    RecyclerView commentsRecView;
    CommentsAdapter adapter;
    ProgressDialog progressDialog;
    List<CommentItem> allComments;
    private int newsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setTitle("Comments");

        newsId = (Integer) getIntent().getIntExtra("news_id", 0);
        allComments = new ArrayList<>();

        adapter = new CommentsAdapter(allComments, this);
        commentsRecView = findViewById(R.id.commentsrec);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentsRecView.setLayoutManager(linearLayoutManager);
        commentsRecView.setAdapter(adapter);

        ActionBar currentBar = getSupportActionBar();
        if (currentBar != null) {
            currentBar.setHomeButtonEnabled(true);
            currentBar.setDisplayHomeAsUpEnabled(true);
            currentBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px);
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_comment_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.newComment) {
            Intent intent = new Intent(this, InsertComment.class);
            intent.putExtra("news_id", newsId);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        CategoryTask task = new CategoryTask();
        String url = "http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid/" + newsId;
        task.execute(url);
    }

    class CategoryTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Comments.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
            StringBuilder buffer = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";

                while ( (line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            allComments.clear();
            try {
                JSONObject object = new JSONObject(s);

                if (object.getInt("serviceMessageCode") == 1) {

                    JSONArray arr = object.getJSONArray("items");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject current = (JSONObject) arr.get(i);
                        CommentItem currentComment = new CommentItem(current.getInt("news_id"),
                                current.getString("name"), current.getString("text"));
                        allComments.add(currentComment);
                    }

                } else {
                }
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }
}
