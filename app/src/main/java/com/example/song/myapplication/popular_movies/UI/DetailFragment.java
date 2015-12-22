package com.example.song.myapplication.popular_movies.UI;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.song.myapplication.BuildConfig;
import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Adapter.TrailerAdapter;
import com.example.song.myapplication.popular_movies.Data.APIConstants;
import com.example.song.myapplication.popular_movies.Data.RestClient;
import com.example.song.myapplication.popular_movies.Model.Movie;
import com.example.song.myapplication.popular_movies.Model.Review;
import com.example.song.myapplication.popular_movies.Model.Trailer;
import com.example.song.myapplication.popular_movies.Util.Misc;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";

    private ProgressDialog mDialog;
    private List<Trailer> trailers;
    private TrailerAdapter mAdapter;

    private String reviewJsonString;

    private String title;
    private String posterUrl;
    private String releaseDate;
    private String ratings;
    private String synopsis;
    private String id;

    public static DetailFragment newInstance(Movie movie) {
        if (movie == null) return null;

        DetailFragment myFragment = new DetailFragment();

        Bundle args = new Bundle();
        args.putString(APIConstants.TITLE, movie.getTitle());
        args.putString(APIConstants.POSTER_PATH, movie.getPoster_path());
        args.putString(APIConstants.RELEASE_DATE, movie.getDate());
        args.putString(APIConstants.RATINGS, movie.getRating());
        args.putString(APIConstants.PLOT_SYNOPSIS, movie.getOverview());
        args.putString(APIConstants.ID, movie.getId());
        myFragment.setArguments(args);

        return myFragment;
    }

    public DetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        trailers = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(APIConstants.TITLE);
            posterUrl = bundle.getString(APIConstants.POSTER_PATH);
            releaseDate = bundle.getString(APIConstants.RELEASE_DATE);
            ratings = bundle.getString(APIConstants.RATINGS);
            synopsis = bundle.getString(APIConstants.PLOT_SYNOPSIS);
            id = bundle.getString(APIConstants.ID);
        }

        mDialog = new ProgressDialog(getActivity());
        mDialog = Misc.setUpDialog(mDialog,
                getString(R.string.fragment_movie_loading_title),
                getString(R.string.detail_loading_message));

        // get trailer urls
        getTrailer(id);
        getReviews(id);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.movie_detail);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        RecyclerView trailerView = (RecyclerView) view.findViewById(R.id.trailer_recyclerView);
        mAdapter = new TrailerAdapter(getActivity(), trailers);
        trailerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        trailerView.setLayoutManager(layoutManager);

        TextView titleView = (TextView) view.findViewById(R.id.detail_title);
        titleView.setText(title);

        ImageView posterView = (ImageView) view.findViewById(R.id.detail_poster);
        RestClient.getPosterImage(posterUrl, getActivity(), posterView);

        TextView dateView = (TextView) view.findViewById(R.id.detail_date);
        dateView.setText(releaseDate);

        TextView ratingView = (TextView) view.findViewById(R.id.detail_ratings);
        ratingView.setText(getString(R.string.rating_unit, ratings));

        TextView overview = (TextView) view.findViewById(R.id.detail_overview);
        overview.setText(synopsis);

        Button review = (Button) view.findViewById(R.id.review_button);
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewJsonString != null) {
                    Intent intent = new Intent(getActivity(), ReviewActivity.class);
                    intent.putExtra(Misc.REVIEW_INTENT, reviewJsonString);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public String toString() {
        return "DetailFragment{" +
                "title='" + title + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", ratings='" + ratings + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    private void getTrailer(String id) {
        mDialog.show();

        OkHttpClient client = new OkHttpClient();
        String url = APIConstants.BASE_URL +
                String.format(APIConstants.MOVIE_VIDEO_URL, id) +
                APIConstants.API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d(TAG, "getTrailer onFailure");
                if (mDialog.isShowing()) mDialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonData = response.body().string();
                Log.d(TAG, "getTrailer onResponse: " + jsonData);

                if (mDialog.isShowing()) mDialog.dismiss();
                try {
                    List<JSONObject> trailerJSONs = new ArrayList<>();
                    trailerJSONs = Misc.parseResult(new JSONObject(jsonData), trailerJSONs);
                    trailers.clear();

                    for (JSONObject json : trailerJSONs) {
                        Trailer trailer = new Trailer(json.getString(APIConstants.NAME),
                                json.getString(APIConstants.SITE),
                                json.getString(APIConstants.KEY));
                        trailers.add(trailer);
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

    private void getReviews(String id) {
        mDialog.show();

        OkHttpClient client = new OkHttpClient();
        String url = APIConstants.BASE_URL +
                String.format(APIConstants.MOVIE_REVIEW_URL, id) +
                APIConstants.API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d(TAG, "getReviews onFailure is called");
                if (mDialog.isShowing()) mDialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                if (mDialog.isShowing()) mDialog.dismiss();
                reviewJsonString = response.body().string();
            }
        });
    }
}
