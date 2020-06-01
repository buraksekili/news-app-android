package com.example.buraksekilihomework3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;

public class NewsDetail extends AppCompatActivity {

    TextView newsTitle;
    TextView newsDate;
    ImageView newsImage;
    TextView newsDetail;
    NewsItem currNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        setTitle("News Details");

        currNews =  (NewsItem) getIntent().getSerializableExtra("newsObj");

        newsTitle = findViewById(R.id.detailTitle);
        newsDate = findViewById(R.id.detailDate);
        newsImage = findViewById(R.id.newsImage);
        newsDetail = findViewById(R.id.detailsNews);

        setScreenReady();
        ActionBar currentBar = getSupportActionBar();
        if (currentBar != null) {
            currentBar.setHomeButtonEnabled(true);
            currentBar.setDisplayHomeAsUpEnabled(true);
            currentBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_24px);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            finish();
        } else if (item.getItemId() == R.id.addComment) {
            Intent commentIntent = new Intent(this, Comments.class);
            commentIntent.putExtra("news_id", currNews.getId());
            startActivity(commentIntent);
        }
        return true;
    }

    public void setScreenReady() {
        newsTitle.setText(currNews.getTitle());
        newsDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(currNews.getNewsDate()));

        if (currNews.getBitmap() == null) {
            new ImageDownloadTask(newsImage).execute(currNews);
        } else {
            newsImage.setImageBitmap(currNews.getBitmap());
        }
        newsDetail.setText(currNews.getText());
    }
}
