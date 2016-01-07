package com.example.song.myapplication.popular_movies.UI;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Model.Movie;

public class MoviesActivity extends AppCompatActivity implements MoviesFragment.OnListItemSelectedListener {

    private static final String TAG = "MoviesActivity";
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        determinePaneLayout();

        if (!isTwoPane) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.place_holder, MoviesFragment.newInstance(false));
            ft.commit();
        } else {
            MoviesFragment master = MoviesFragment.newInstance(true);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.masterContainer, master);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    private void determinePaneLayout() {
        FrameLayout fragmentItemDetail = (FrameLayout) findViewById(R.id.masterContainer);
        // If there is a second pane for details
        if (fragmentItemDetail != null) {
            isTwoPane = true;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (isTwoPane) {
//            Log.d(TAG, "onItemSelected is called: " + movie);
            DetailFragment details = DetailFragment.newInstance(isTwoPane, movie);
            getSupportFragmentManager().beginTransaction().replace(R.id.detailContainer, details).commit();
        }
    }
}
