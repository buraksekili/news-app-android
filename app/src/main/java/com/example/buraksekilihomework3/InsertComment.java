package com.example.buraksekilihomework3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InsertComment extends AppCompatActivity {

    EditText inputUserName;
    EditText inputComment;
    private int newsId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_comment);
        setTitle("Post Comment");

        inputUserName = findViewById(R.id.usernameInput);
        inputComment = findViewById(R.id.inputComment);
    }

    public void postComment(View view) {
        Intent res = getIntent();
        newsId = res.getIntExtra("news_id", -1);
        JsonTask task = new JsonTask();
        task.execute("http://94.138.207.51:8080/NewsApp/service/news/savecomment");
        finish();
    }

    class JsonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            String urlString = strings[0];
            JSONObject object = new JSONObject();
            try {
                object.put("name", inputUserName.getText().toString());
                object.put("text", inputComment.getText().toString());
                object.put("news_id", newsId);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(object.toString());

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line = "";

                    while ( (line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                } else {}

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  stringBuilder.toString();
        }
    }
}
