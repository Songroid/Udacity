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
import com.example.song.myapplication.popular_movies.Data.APIConstants;
import com.example.song.myapplication.popular_movies.Data.DatabaseHelper;
import com.example.song.myapplication.popular_movies.Model.Movie;
import com.example.song.myapplication.popular_movies.Util.Misc;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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
    private RuntimeExceptionDao<Movie, String> dao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            selectDefault = savedInstanceState.getBoolean(MENU_SELECTED);
        }

        setHasOptionsMenu(true);

        movies = new ArrayList<>();

        mDialog = new ProgressDialog(getActivity());
        mDialog = Misc.setUpDialog(mDialog,
                getString(R.string.fragment_movie_loading_title),
                getString(R.string.fragment_movie_loading_message));
        mDialog.show();

        if (selectDefault) {
            getMovies(APIConstants.POPULARITY_DESC);
        } else {
            getMovies(APIConstants.RATINGS_DESC);
        }

        onCreateDb();
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

                JSONObject movieJson = movies.get(position);

                try {
                    Movie movie = getMoviefromJSON(movieJson, false);
                    if (LOG_ENABLED) Log.d(TAG, movie.toString());

                    DetailFragment details = DetailFragment.newInstance(movie);
                    getFragmentManager().beginTransaction().replace(android.R.id.content, details).addToBackStack(null).commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                getMovies(APIConstants.POPULARITY_DESC);
                processSettingMenu(item);
                selectDefault = true;
                return true;
            case R.id.sort_by_rated:
                getMovies(APIConstants.RATINGS_DESC);
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
        String url = APIConstants.BASE_URL +
                APIConstants.DISCOVER_MOVIE_URL +
                APIConstants.SORT_BY + "=" + sortOrder + "&" +
                APIConstants.API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;

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
                    movies = Misc.parseResult(new JSONObject(jsonData), movies);

                    // create or update database
                    for (JSONObject json : movies) {
                        Movie movie = getMoviefromJSON(json, true);
                        dao.createOrUpdate(movie);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void processSettingMenu(MenuItem item) {
        if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);

        mAdapter.notifyDataSetChanged();
    }

    private void onCreateDb() {
        dao = DatabaseHelper.getInstance(getActivity()).getMovieDao();
    }

    private Movie getMoviefromJSON(JSONObject json, boolean forceUpdate) throws JSONException {
        Movie movie;
        String id = json.getString(APIConstants.ID);

        movie = new Movie(json.getString(APIConstants.TITLE),
                json.getString(APIConstants.POSTER_PATH),
                json.getString(APIConstants.RELEASE_DATE),
                json.getString(APIConstants.RATINGS),
                json.getString(APIConstants.PLOT_SYNOPSIS),
                id,
                false);

        if (forceUpdate) {
            dao.createOrUpdate(movie);
        } else {
            if (dao.idExists(id)) movie = dao.queryForId(id);
        }

        return movie;
    }
}
