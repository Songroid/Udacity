package com.example.song.myapplication.popular_movies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Adapter.ReviewAdapter;
import com.example.song.myapplication.popular_movies.Data.APIConstants;
import com.example.song.myapplication.popular_movies.Model.Review;
import com.example.song.myapplication.popular_movies.Util.Misc;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    public final static String TAG = "ReviewActivity";

    private List<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        reviews = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            String jsonData = intent.getStringExtra(Misc.REVIEW_INTENT);
            Log.d(TAG, "getIntent getStringExtra: " + jsonData);

            try {
                List<JSONObject> reviewJSONs = new ArrayList<>();
                reviewJSONs = Misc.parseResult(new JSONObject(jsonData), reviewJSONs);
                reviews.clear();

                for (JSONObject json : reviewJSONs) {
                    Review review = new Review(json.getString(APIConstants.AUTHOR),
                            json.getString(APIConstants.CONTENT));
                    reviews.add(review);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RecyclerView reviewsView = (RecyclerView) findViewById(R.id.review_list);
        ReviewAdapter adapter = new ReviewAdapter(reviews);
        reviewsView.setAdapter(adapter);
        reviewsView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
