package com.example.buraksekilihomework3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView newsRecView;

    List<NewsItem> data;
    List<String> categories;
    HashMap<String, Integer>  categoriesById;

    ProgressDialog progressDialog;
    ProgressDialog progressDialog2;
    ProgressDialog progressDialog3;
    NewsAdapter adp;
    ArrayAdapter<String> categoriesAdapter;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsRecView = findViewById(R.id.newsrec);
        spinner = findViewById(R.id.spinner);

        setTitle("News");

        data = new ArrayList<>();
        categories = new ArrayList<>();
        categoriesById = new HashMap<>();
        getCategories();

        adp = new NewsAdapter(data, this, new NewsAdapter.NewsItemClickListener() {
            @Override
            public void newItemClicked(NewsItem selectedNewsItem, int position) {
                Intent i = new Intent(getApplicationContext(), NewsDetail.class);
                i.putExtra("newsObj", selectedNewsItem);
                startActivity(i);
            }
        });
        newsRecView.setLayoutManager(new LinearLayoutManager(this));
        newsRecView.setAdapter(adp);

        categoriesAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(categoriesAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currCategory = categories.get(position);
                if (!currCategory.equals("All")) {
                    filterNewsByCategory(currCategory);
                } else if (currCategory.equals("All")) {
                    NewsTask task = new NewsTask();
                    task.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void filterNewsByCategory(String categoryName) {
        FilterCategory task = new FilterCategory();
        int categoryId = categoriesById.get(categoryName);
        String url = "http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/" + categoryId;
        task.execute(url);
    }

    void getCategories() {
        CategoryTask task = new CategoryTask();
        task.execute("http://94.138.207.51:8080/NewsApp/service/news/getallnewscategories");
    }

    class CategoryTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog2 = new ProgressDialog(MainActivity.this);
            progressDialog2.setTitle("Loading");
            progressDialog2.setMessage("Please wait");
            progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog2.show();
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
            categories.clear();
            categories.add("All");
            try {
                JSONObject object = new JSONObject(s);

                if (object.getInt("serviceMessageCode") == 1) {

                    JSONArray arr = object.getJSONArray("items");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject current = (JSONObject) arr.get(i);
                        String currCategory = current.getString("name");
                        categories.add(currCategory);
                        if (!categoriesById.containsKey(currCategory)) {
                            categoriesById.put(currCategory, current.getInt("id"));
                        }
                    }

                } else {
                }
                adp.notifyDataSetChanged();
                categoriesAdapter.notifyDataSetChanged();
                progressDialog2.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }

    class NewsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
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
            data.clear();
            try {
                JSONObject object = new JSONObject(s);

                if (object.getInt("serviceMessageCode") == 1) {

                    JSONArray arr = object.getJSONArray("items");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject current = (JSONObject) arr.get(i);

                        long date = current.getLong("date");
                        Date dateObj = new Date(date);
                        String category = current.getString("categoryName");
                        NewsItem item = new NewsItem(current.getInt("id"), current.getString("title"),
                                current.getString("text"), current.getString("image"), dateObj, category);
                        data.add(item);
                    }

                } else {
                }
                adp.notifyDataSetChanged();

                categoriesAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }

    class FilterCategory extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog3 = new ProgressDialog(MainActivity.this);
            progressDialog3.setTitle("Loading");
            progressDialog3.setMessage("Please wait");
            progressDialog3.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog3.show();
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
            data.clear();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getInt("serviceMessageCode") == 1) {
                    JSONArray arr = object.getJSONArray("items");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject current = (JSONObject) arr.get(i);

                        long date = current.getLong("date");
                        Date dateObj = new Date(date);
                        String category = current.getString("categoryName");
                        NewsItem item = new NewsItem(current.getInt("id"), current.getString("title"),
                                current.getString("text"), current.getString("image"), dateObj, category);
                        data.add(item);
                    }

                } else {
                }
                adp.notifyDataSetChanged();
                categoriesAdapter.notifyDataSetChanged();
                progressDialog3.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }
}
