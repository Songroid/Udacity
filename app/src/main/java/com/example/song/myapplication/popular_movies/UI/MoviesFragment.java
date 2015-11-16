package com.example.song.myapplication.popular_movies.UI;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.song.myapplication.BuildConfig;
import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Adapter.MoviesAdapter;
import com.example.song.myapplication.popular_movies.Data.ApiConstants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {
    private static final String TAG = "MoviesFragment";
    private static final boolean LOG_ENABLED = false;
    private final static String MENU_SELECTED = "selected";

    private ProgressDialog mDialog;
    private List<JSONObject> movies;
    private MoviesAdapter mAdapter;

    private boolean selectDefault = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            selectDefault = savedInstanceState.getBoolean(MENU_SELECTED);
        }

        setHasOptionsMenu(true);

        movies = new ArrayList<>();

        mDialog = new ProgressDialog(getActivity());
        setUpDialog(mDialog);
        mDialog.show();

        if (selectDefault) {
            getMovies(ApiConstants.POPULARITY_DESC);
        } else {
            getMovies(ApiConstants.RATINGS_DESC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        GridView moviesView = (GridView) view.findViewById(R.id.movie_gridView);
        mAdapter = new MoviesAdapter(getActivity(), movies);
        moviesView.setAdapter(mAdapter);

        moviesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                JSONObject movie = movies.get(position);
                if (LOG_ENABLED) Log.d(TAG, movie.toString());

                DetailFragment details = null;
                try {
                    details = DetailFragment.newInstance(movie.getString(ApiConstants.TITLE),
                            movie.getString(ApiConstants.POSTER_PATH),
                            movie.getString(ApiConstants.RELEASE_DATE),
                            movie.getString(ApiConstants.RATINGS),
                            movie.getString(ApiConstants.PLOT_SYNOPSIS));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getFragmentManager().beginTransaction().replace(android.R.id.content, details).addToBackStack(null).commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (movies.size() != 0 && LOG_ENABLED) {
            for (JSONObject o : movies) {
                Log.d(TAG, "Movie: " + o);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies, menu);

        if (selectDefault) {
            menu.findItem(R.id.sort_by_popular).setChecked(true);
        } else {
            menu.findItem(R.id.sort_by_rated).setChecked(true);
        }

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(R.string.title_activity_movies);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_by_popular:
                getMovies(ApiConstants.POPULARITY_DESC);
                processSettingMenu(item);
                selectDefault = true;
                return true;
            case R.id.sort_by_rated:
                getMovies(ApiConstants.RATINGS_DESC);
                processSettingMenu(item);
                selectDefault = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(MENU_SELECTED, selectDefault);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void getMovies(String sortOrder) {
        OkHttpClient client = new OkHttpClient();
        String url = ApiConstants.BASE_URL +
                ApiConstants.SORT_BY + "=" + sortOrder + "&" +
                ApiConstants.API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "getMovies onFailure");
                if (mDialog.isShowing()) mDialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonData = response.body().string();

                Log.d(TAG, jsonData);
                if (mDialog.isShowing()) mDialog.dismiss();
                try {
                    parseResult(new JSONObject(jsonData));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setUpDialog(ProgressDialog dialog) {
        dialog.setTitle(getString(R.string.fragment_movie_loading_title));
        dialog.setMessage(getString(R.string.fragment_movie_loading_message));
        dialog.setIndeterminate(true);
    }

    private void parseResult(JSONObject input) throws JSONException {
        JSONArray movieArray = input.getJSONArray(ApiConstants.RESULTS);
        movies.clear();
        for (int i=0; i<movieArray.length(); i++) {
            movies.add(movieArray.getJSONObject(i));
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void processSettingMenu(MenuItem item) {
        if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);

        mAdapter.notifyDataSetChanged();
    }
}
