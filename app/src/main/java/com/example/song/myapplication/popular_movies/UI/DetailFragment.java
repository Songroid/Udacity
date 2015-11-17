package com.example.song.myapplication.popular_movies.UI;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Data.ApiConstants;
import com.example.song.myapplication.popular_movies.Data.RestClient;
import com.example.song.myapplication.popular_movies.Model.Movie;


public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";

    private String title;
    private String posterUrl;
    private String releaseDate;
    private String ratings;
    private String synopsis;

    public static DetailFragment newInstance(Movie movie) {
        if (movie == null) return null;

        DetailFragment myFragment = new DetailFragment();

        Bundle args = new Bundle();
        args.putString(ApiConstants.TITLE, movie.getTitle());
        args.putString(ApiConstants.POSTER_PATH, movie.getPoster_path());
        args.putString(ApiConstants.RELEASE_DATE, movie.getDate());
        args.putString(ApiConstants.RATINGS, movie.getRating());
        args.putString(ApiConstants.PLOT_SYNOPSIS, movie.getOverview());
        myFragment.setArguments(args);

        return myFragment;
    }

    public DetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(ApiConstants.TITLE);
            posterUrl = bundle.getString(ApiConstants.POSTER_PATH);
            releaseDate = bundle.getString(ApiConstants.RELEASE_DATE);
            ratings = bundle.getString(ApiConstants.RATINGS);
            synopsis = bundle.getString(ApiConstants.PLOT_SYNOPSIS);
        }
        Log.d(TAG, toString());
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
                '}';
    }
}
