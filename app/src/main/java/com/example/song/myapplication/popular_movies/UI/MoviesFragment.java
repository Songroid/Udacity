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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MoviesFragment extends Fragment {
    private static final String TAG = "MoviesFragment";
    private static final boolean LOG_ENABLED = true;

    private ProgressDialog mDialog;
    private List<JSONObject> movies;
    private MoviesAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        movies = new ArrayList<>();

        mDialog = new ProgressDialog(getActivity());
        setUpDialog(mDialog);
        mDialog.show();

        getMovies(ApiConstants.POPULARITY_DESC);
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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (LOG_ENABLED) Log.d(TAG, movies.get(i).toString());
                DetailFragment details = new DetailFragment();
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_by_popular:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                defaultSort();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_by_rated:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                Collections.sort(movies, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject movie1, JSONObject movie2) {
                        try {
                            double p1 = Double.parseDouble(movie1.getString(ApiConstants.RATINGS));
                            double p2 = Double.parseDouble(movie2.getString(ApiConstants.RATINGS));

                            if (p1 > p2) {
                                return -1;
                            } else if (p1 == p2) {
                                return 0;
                            } else {
                                return 1;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                mDialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonData = response.body().string();

                Log.d(TAG, jsonData);
                mDialog.dismiss();
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
        defaultSort();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void defaultSort() {
        Collections.sort(movies, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject movie1, JSONObject movie2) {
                try {
                    double p1 = Double.parseDouble(movie1.getString(ApiConstants.POPULARITY));
                    double p2 = Double.parseDouble(movie2.getString(ApiConstants.POPULARITY));

                    if (p1 > p2) {
                        return -1;
                    } else if (p1 == p2) {
                        return 0;
                    } else {
                        return 1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
