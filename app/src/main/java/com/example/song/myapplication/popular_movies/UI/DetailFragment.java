package com.example.song.myapplication.popular_movies.UI;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
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
import com.example.song.myapplication.popular_movies.Data.MovieColumns;
import com.example.song.myapplication.popular_movies.Data.MovieProvider;
import com.example.song.myapplication.popular_movies.Data.RestClient;
import com.example.song.myapplication.popular_movies.Data.ReviewColumns;
import com.example.song.myapplication.popular_movies.Data.TrailerColumns;
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

    private List<Trailer> trailers;
    private ArrayList<Review> reviews;
    private TrailerAdapter mAdapter;

    private String reviewJsonString;

    private String id;

    private Movie movie;
    private boolean isTwoPane;

    public static DetailFragment newInstance(boolean isTwoPane, Movie movie) {
        if (movie == null) return null;

        DetailFragment myFragment = new DetailFragment();

        Bundle args = new Bundle();
        args.putString(APIConstants.ID, movie.getId());
        args.putBoolean(Misc.IS_TWO_PANE, isTwoPane);
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
            id = bundle.getString(APIConstants.ID);
            movie = Misc.queryForId(getActivity(), id);
            isTwoPane = bundle.getBoolean(Misc.IS_TWO_PANE);
        }

        // get trailer urls
        getTrailer(id);
        getReviews(id);

        // read db
        List<Trailer> tmpTrailers = queryForTrailerId(movie.getId());
        List<Review> tmpReviews = queryForReviewId(movie.getId());
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
        titleView.setText(movie.getTitle());

        ImageView posterView = (ImageView) view.findViewById(R.id.detail_poster);
        RestClient.getPosterImage(movie.getPoster_path(), getActivity(), posterView);

        TextView dateView = (TextView) view.findViewById(R.id.detail_date);
        dateView.setText(movie.getDate());

        TextView ratingView = (TextView) view.findViewById(R.id.detail_ratings);
        ratingView.setText(getString(R.string.rating_unit, movie.getRating()));

        TextView overview = (TextView) view.findViewById(R.id.detail_overview);
        overview.setText(movie.getOverview());

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
                Misc.updateData(getActivity(), movie);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isTwoPane) {
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

                        createOrUpdateTrailer(trailer);
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
                                movie.getId(),
                                json.getString(APIConstants.ID));

                        createOrUpdateReview(review);
                        reviews.add(review);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createOrUpdateTrailer(Trailer trailer) {
        Cursor c = getActivity().getContentResolver()
                .query(MovieProvider.Trailers.CONTENT_URI,
                        null,
                        TrailerColumns.KEY + " = ?",
                        new String[]{trailer.getKey()},
                        null);
        if (c == null || c.getCount() == 0) {
            handleTrailer(trailer, false);
        } else {
            handleTrailer(trailer, true);
        }
        if (c != null) c.close();
    }

    private void createOrUpdateReview(Review review) {
        Cursor c = getActivity().getContentResolver()
                .query(MovieProvider.Reviews.CONTENT_URI,
                        null,
                        ReviewColumns.REVIEW_ID + " = ?",
                        new String[]{review.getReviewId()},
                        null);
        if (c == null || c.getCount() == 0) {
            handleReview(review, false);
        } else {
            handleReview(review, true);
        }
        if (c != null) c.close();
    }

    private void handleTrailer(Trailer trailer, boolean isUpdate) {
        if (isUpdate) {
            getActivity().getContentResolver().update(
                    MovieProvider.Trailers.CONTENT_URI,
                    generateValuesFromTrailer(trailer),
                    TrailerColumns.KEY + " = ?",
                    new String[]{trailer.getKey()}
            );
        } else {
            // insert
            getActivity().getContentResolver().insert(
                    MovieProvider.Trailers.CONTENT_URI,
                    generateValuesFromTrailer(trailer)
            );
        }
    }

    private void handleReview(Review review, boolean isUpdate) {
        if (isUpdate) {
            getActivity().getContentResolver().update(
                    MovieProvider.Reviews.CONTENT_URI,
                    generateValuesFromReview(review),
                    ReviewColumns.REVIEW_ID + " = ?",
                    new String[]{review.getReviewId()}
            );
        } else {
            // insert
            getActivity().getContentResolver().insert(
                    MovieProvider.Reviews.CONTENT_URI,
                    generateValuesFromReview(review)
            );
        }
    }

    private ContentValues generateValuesFromTrailer(Trailer trailer) {
        ContentValues values = new ContentValues();

        values.put(TrailerColumns.KEY, trailer.getKey());
        values.put(TrailerColumns.ID, trailer.getId());
        values.put(TrailerColumns.NAME, trailer.getName());
        values.put(TrailerColumns.SITE, trailer.getSite());

        return values;
    }

    private ContentValues generateValuesFromReview(Review review) {
        ContentValues values = new ContentValues();

        values.put(ReviewColumns.AUTHOR, review.getAuthor());
        values.put(ReviewColumns.CONTENT, review.getContent());
        values.put(ReviewColumns.MOVIE_ID, review.getMovieId());
        values.put(ReviewColumns.REVIEW_ID, review.getReviewId());

        return values;
    }

    private List<Trailer> queryForTrailerId(String id) {
        Cursor c = getActivity().getContentResolver()
                .query(MovieProvider.Trailers.CONTENT_URI,
                        null,
                        TrailerColumns.ID + " = ?",
                        new String[]{id},
                        null);
        List<Trailer> trailers = new ArrayList<>();

        if (c != null) {
            while (c.moveToNext()) {
                Trailer trailer = new Trailer();

                for (int i=0; i<c.getColumnCount(); i++) {
//                    Log.d(TAG, "queryForTrailerId " + c.getColumnName(i) + ": " + c.getString(i));
                    String value = c.getString(i);
                    switch (i) {
                        case 1:
                            trailer.setName(value);
                            break;
                        case 2:
                            trailer.setSite(value);
                            break;
                        case 3:
                            trailer.setKey(value);
                            break;
                        case 4:
                            trailer.setId(value);
                            break;
                    }
                }
                trailers.add(trailer);
            }
            c.close();
        }

        return trailers;
    }

    private List<Review> queryForReviewId(String id) {
        Cursor c = getActivity().getContentResolver()
                .query(MovieProvider.Reviews.CONTENT_URI,
                        null,
                        ReviewColumns.MOVIE_ID + " = ?",
                        new String[]{id},
                        null);
        List<Review> reviews = new ArrayList<>();

        if (c != null) {
            while (c.moveToNext()) {
                Review review = new Review();

                for (int i=0; i<c.getColumnCount(); i++) {
//                    Log.d(TAG, "queryForReviewId " + c.getColumnName(i) + ": " + c.getString(i));
                    String value = c.getString(i);
                    switch (i) {
                        case 1:
                            review.setAuthor(value);
                            break;
                        case 2:
                            review.setContent(value);
                            break;
                        case 3:
                            review.setMovieId(value);
                            break;
                        case 4:
                            review.setReviewId(value);
                            break;
                    }
                }
                reviews.add(review);
            }
            c.close();
        }

        return reviews;
    }
}
