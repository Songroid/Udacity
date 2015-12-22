package com.example.song.myapplication.popular_movies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Util.Misc;

public class MoviesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    public void onStarClicked(View view) {
        ImageView star = (ImageView) view.findViewById(R.id.mark_as_favorite_star);
        if (star.getTag().equals(Misc.STAR_UNMARKED)) {
            star.setImageResource(R.drawable.ic_star_selected);
            star.setTag(Misc.STAR_MARKED);
        } else if (star.getTag().equals(Misc.STAR_MARKED)) {
            star.setImageResource(R.drawable.ic_star_unselected);
            star.setTag(Misc.STAR_UNMARKED);
        }
    }
}
