package com.example.song.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.song.myapplication.popular_movies.UI.MoviesActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toMoviesApp(View v) {
        Intent intent = new Intent(this, MoviesActivity.class);
        startActivity(intent);
    }

    //    public void showToast(View v) {
//        Toast.makeText(this, String.format("This button will launch my %s app!", v.getTag()), Toast.LENGTH_SHORT).show();
//    }
}
