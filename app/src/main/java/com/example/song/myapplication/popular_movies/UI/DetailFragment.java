package com.example.song.myapplication.popular_movies.UI;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.example.song.myapplication.popular_movies.Data.DatabaseHelper;
import com.example.song.myapplication.popular_movies.Data.RestClient;
import com.example.song.myapplication.popular_movies.Model.Movie;
import com.example.song.myapplication.popular_movies.Model.Review;
import com.example.song.myapplication.popular_movies.Model.Trailer;
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


public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";

    private List<Trailer> trailers;
    private ArrayList<Review> reviews;
    private TrailerAdapter mAdapter;

    private String reviewJsonString;

    private String title;
    private String posterUrl;
    private String releaseDate;
    private String ratings;
    private String synopsis;
    private String id;

    private RuntimeExceptionDao<Movie, String> dao;
    private RuntimeExceptionDao<Trailer, String> trailerDao;
    private RuntimeExceptionDao<Review, String> reviewDao;

    private Movie movie;

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
        args.putBoolean(APIConstants.IS_FAVORITE, movie.isFavorite());
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
        reviews = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(APIConstants.TITLE);
            posterUrl = bundle.getString(APIConstants.POSTER_PATH);
            releaseDate = bundle.getString(APIConstants.RELEASE_DATE);
            ratings = bundle.getString(APIConstants.RATINGS);
            synopsis = bundle.getString(APIConstants.PLOT_SYNOPSIS);
            id = bundle.getString(APIConstants.ID);
            boolean isFavorite = bundle.getBoolean(APIConstants.IS_FAVORITE);

            movie = new Movie(title, posterUrl, releaseDate, ratings, synopsis, id, isFavorite);
        }

        // get trailer urls
        getTrailer(id);
        getReviews(id);

        onCreateDb();

        // read db
        List<Trailer> tmpTrailers = trailerDao.queryForEq(APIConstants.ID, movie.getId());
        List<Review> tmpReviews = reviewDao.queryForEq(Review.MOVIE_ID, movie.getId());
        if (tmpTrailers != null && !tmpTrailers.isEmpty()) trailers.addAll(tmpTrailers);
        if (tmpReviews != null && !tmpReviews.isEmpty()) reviews.addAll(tmpReviews);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.movie_detail);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        final Button review = (Button) view.findViewById(R.id.review_button);
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reviews.isEmpty()) {
                    Intent intent = new Intent(getActivity(), ReviewActivity.class);
                    intent.putParcelableArrayListExtra(Misc.REVIEW_INTENT, reviews);
                    startActivity(intent);
                }
            }
        });

        final ImageView star = (ImageView) view.findViewById(R.id.mark_as_favorite_star);

        if (movie.isFavorite()) {
            star.setImageResource(R.drawable.ic_star_selected);
            star.setTag(Misc.STAR_MARKED);
        } else {
            star.setImageResource(R.drawable.ic_star_unselected);
            star.setTag(Misc.STAR_UNMARKED);
        }

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (star.getTag().equals(Misc.STAR_UNMARKED)) {
                    star.setImageResource(R.drawable.ic_star_selected);
                    movie.setIsFavorite(true);
                    star.setTag(Misc.STAR_MARKED);
                } else if (star.getTag().equals(Misc.STAR_MARKED)) {
                    star.setImageResource(R.drawable.ic_star_unselected);
                    movie.setIsFavorite(false);
                    star.setTag(Misc.STAR_UNMARKED);
                }
                dao.update(movie);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_movie_detail, menu);

        MenuItem item = menu.findItem(R.id.movie_detail_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (!trailers.isEmpty()) {
            Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    APIConstants.YOUTUBE_BASE_URL + "v=" + trailers.get(0).getKey()));
            mShareActionProvider.setShareIntent(share);
        }
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
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonData = response.body().string();
                Log.d(TAG, "getTrailer onResponse: " + jsonData);

                try {
                    List<JSONObject> trailerJSONs = new ArrayList<>();
                    trailerJSONs = Misc.parseResult(new JSONObject(jsonData), trailerJSONs);
                    trailers.clear();

                    for (JSONObject json : trailerJSONs) {
                        Trailer trailer = new Trailer(json.getString(APIConstants.NAME),
                                json.getString(APIConstants.SITE),
                                json.getString(APIConstants.KEY),
                                movie.getId());

                        trailerDao.createOrUpdate(trailer);
                        trailers.add(trailer);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            getActivity().invalidateOptionsMenu();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getReviews(String id) {
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
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                reviewJsonString = response.body().string();

                try {
                    List<JSONObject> reviewJSONs = new ArrayList<>();
                    reviewJSONs = Misc.parseResult(new JSONObject(reviewJsonString), reviewJSONs);
                    reviews.clear();

                    for (JSONObject json : reviewJSONs) {
                        Review review = new Review(json.getString(APIConstants.AUTHOR),
                                json.getString(APIConstants.CONTENT),
                                movie.getId());

                        reviewDao.createOrUpdate(review);
                        reviews.add(review);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onCreateDb() {
        dao = DatabaseHelper.getInstance(getActivity()).getMovieDao();
        dao.createOrUpdate(movie);
        trailerDao = DatabaseHelper.getInstance(getActivity()).getTrailerDao();
        reviewDao = DatabaseHelper.getInstance(getActivity()).getReviewDao();
    }
}
