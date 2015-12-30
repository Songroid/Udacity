package com.example.song.myapplication.popular_movies.UI;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.Collections;
import java.util.List;

public class MoviesFragment extends Fragment {
    private static final String TAG = "MoviesFragment";
    private final static String MENU_SELECTED = "selected";
    private final static String FAVORITE_SELECTED = "selected";

    private static final int POPULARITY = 0;
    private static final int RATINGS = 1;
    private static final int FAVORITE = 2;

    private ProgressDialog mDialog;
    private List<Movie> movies;
    private MoviesAdapter mAdapter;

    private int selectDefault;
    private RuntimeExceptionDao<Movie, String> dao;

    private boolean isFavoriteChecked;
    private boolean isShowingFavorite;
    private AppCompatActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            selectDefault = savedInstanceState.getInt(MENU_SELECTED);
            isFavoriteChecked = savedInstanceState.getBoolean(FAVORITE_SELECTED);
        }

        setHasOptionsMenu(true);

        movies = new ArrayList<>();

        mDialog = new ProgressDialog(getActivity());
        mDialog = Misc.setUpDialog(mDialog,
                getString(R.string.fragment_movie_loading_title),
                getString(R.string.fragment_movie_loading_message));
        mDialog.show();

        getMoviesBySelection();
        onCreateDb();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        GridView moviesView = (GridView) view.findViewById(R.id.movie_gridView);
        mAdapter = new MoviesAdapter(getActivity(), movies);
        moviesView.setAdapter(mAdapter);

        moviesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Movie movie = movies.get(position);
                Log.d(TAG, movie.toString());

                DetailFragment details = DetailFragment.newInstance(movie);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.place_holder, details).addToBackStack(null).commit();

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_movies, menu);

        MenuItem favorite = menu.findItem(R.id.show_favorite);
        menu.setGroupVisible(R.id.sort_group, !isShowingFavorite);
        favorite.setIcon(isShowingFavorite ? R.drawable.ic_star_white_24dp :
                R.drawable.ic_star_border_white_24dp);
        activity.getSupportActionBar().setTitle(isShowingFavorite ?
                getString(R.string.my_favorite) : getString(R.string.main_spotify_streamer));

        switch (selectDefault) {
            case POPULARITY:
                menu.findItem(R.id.sort_by_popular).setChecked(true);
                break;
            case RATINGS:
                menu.findItem(R.id.sort_by_rated).setChecked(true);
                break;
            case FAVORITE:
                menu.findItem(R.id.sort_by_favorite).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        processSettingMenu(item);

        switch (id) {
            case R.id.sort_by_popular:
                getMovies(APIConstants.POPULARITY_DESC);
                selectDefault = POPULARITY;
                isFavoriteChecked = false;
                break;
            case R.id.sort_by_rated:
                getMovies(APIConstants.RATINGS_DESC);
                selectDefault = RATINGS;
                isFavoriteChecked = false;
                break;
            case R.id.sort_by_favorite:
                selectDefault = FAVORITE;
                isFavoriteChecked = true;
                getMovies(APIConstants.POPULARITY_DESC);
                break;
            case R.id.show_favorite:
                if (!isShowingFavorite) {
                    List<Movie> temp = dao.queryForEq(Movie.IS_FAVORITE, true);
                    movies.clear();
                    movies.addAll(temp);
                    mAdapter.notifyDataSetChanged();
                } else {
                    getMoviesBySelection();
                }
                isShowingFavorite = !isShowingFavorite;
                getActivity().invalidateOptionsMenu();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(MENU_SELECTED, selectDefault);
        savedInstanceState.putBoolean(FAVORITE_SELECTED, isFavoriteChecked);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void getMovies(String sortOrder) {
        OkHttpClient client = new OkHttpClient();
        String url = APIConstants.BASE_URL +
                APIConstants.DISCOVER_MOVIE_URL +
                APIConstants.SORT_BY + "=" + sortOrder + "&" +
                APIConstants.API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;
        Log.d(TAG, url);

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
                    movies.clear();
                    List<JSONObject> tmpList = new ArrayList<>();
                    tmpList = Misc.parseResult(new JSONObject(jsonData), tmpList);

                    // create or update database
                    for (JSONObject json : tmpList) {
                        Movie movie = getMoviefromJSON(json, true);
                        movies.add(movie);
                        dao.createOrUpdate(movie);

                        if (isFavoriteChecked) {
                            Collections.sort(movies);
                        }
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
        boolean isFavorite = false;

        String id = json.getString(APIConstants.ID);

        Movie movieFromDb = dao.queryForId(id);
        if (movieFromDb != null) isFavorite = movieFromDb.isFavorite();

        movie = new Movie(json.getString(APIConstants.TITLE),
                json.getString(APIConstants.POSTER_PATH),
                json.getString(APIConstants.RELEASE_DATE),
                json.getString(APIConstants.RATINGS),
                json.getString(APIConstants.PLOT_SYNOPSIS),
                id,
                isFavorite);

        if (forceUpdate) {
            dao.createOrUpdate(movie);
        } else {
            if (dao.idExists(id)) movie = dao.queryForId(id);
        }

        return movie;
    }

    private void getMoviesBySelection() {
        switch (selectDefault) {
            case POPULARITY: case FAVORITE:
                getMovies(APIConstants.POPULARITY_DESC);
                break;
            case RATINGS:
                getMovies(APIConstants.RATINGS_DESC);
                break;
        }
    }
}
