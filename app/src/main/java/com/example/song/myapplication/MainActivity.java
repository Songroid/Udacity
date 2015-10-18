package com.example.song.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button spotifyStreamerButton = (Button) findViewById(R.id.main_spotify_streamer);
        spotifyStreamerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(spotifyStreamerButton.getText().toString());
            }
        });

        final Button scoreButton = (Button) findViewById(R.id.main_football_score_app);
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(scoreButton.getText().toString());
            }
        });

        final Button libraryButton = (Button) findViewById(R.id.main_library_app);
        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(libraryButton.getText().toString());
            }
        });

        final Button buildItBiggerButton = (Button) findViewById(R.id.main_build_it_bigger);
        buildItBiggerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(buildItBiggerButton.getText().toString());
            }
        });

        final Button readerButton = (Button) findViewById(R.id.main_xyz_reader);
        readerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(readerButton.getText().toString());
            }
        });

        final Button capStoneButton = (Button) findViewById(R.id.main_capstone);
        capStoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(capStoneButton.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String buttonText) {
        Toast.makeText(this, String.format("This button will launch my %s app!", buttonText), Toast.LENGTH_SHORT).show();
    }
}
