package com.example.song.myapplication.popular_movies.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.BaseAdapter;

import com.example.song.myapplication.popular_movies.Data.APIConstants;
import com.example.song.myapplication.popular_movies.Data.MovieColumns;
import com.example.song.myapplication.popular_movies.Data.MovieProvider;
import com.example.song.myapplication.popular_movies.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Song on 12/14/15.
 */
public class Misc {
    public static final String STAR_MARKED = "marked";
    public static final String STAR_UNMARKED = "unmarked";
    public static final String REVIEW_INTENT = "review_intent";

    public static ProgressDialog setUpDialog(ProgressDialog dialog, String title, String content) {
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setIndeterminate(true);
        return dialog;
    }

    public static List<JSONObject> parseResult(JSONObject input,
                                   List<JSONObject> list) throws JSONException {
        JSONArray movieArray = input.getJSONArray(APIConstants.RESULTS);
        list.clear();
        for (int i=0; i<movieArray.length(); i++) {
            list.add(movieArray.getJSONObject(i));
        }
        return list;
    }

    public static int updateData(Context context, Movie movie) {
        return context.getContentResolver().update(
                MovieProvider.Movies.CONTENT_URI,
                generateValuesFromMovie(movie),
                MovieColumns.MOVIE_ID + " = ?",
                new String[]{movie.getId()}
        );
    }

    public static ContentValues generateValuesFromMovie(Movie movie) {
        ContentValues values = new ContentValues();

        values.put(MovieColumns.DATE, movie.getDate());
        values.put(MovieColumns.IS_FAVORITE, movie.isFavorite());
        values.put(MovieColumns.MOVIE_ID, movie.getId());
        values.put(MovieColumns.OVERVIEW, movie.getOverview());
        values.put(MovieColumns.POSTER_PATH, movie.getPoster_path());
        values.put(MovieColumns.RATING, movie.getRating());
        values.put(MovieColumns.TITLE, movie.getTitle());

        return values;
    }

    public static Movie queryForId(Context context, String id) {
        Cursor c = context.getContentResolver()
                .query(MovieProvider.Movies.CONTENT_URI,
                        null,
                        MovieColumns.MOVIE_ID + " = ?",
                        new String[]{id},
                        null);
        Movie movie = new Movie();

        if (c != null) {
            while (c.moveToNext()) {
                for (int i=0; i<c.getColumnCount(); i++) {
//                    Log.d(TAG, "queryForId " + c.getColumnName(i) + ": " + c.getString(i));
                    parse(i, movie, c.getString(i));
                }
            }
            c.close();
        }

        return movie;
    }

    public static void parse(int i, Movie movie, String value) {
        switch (i) {
            case 1:
                movie.setTitle(value);
                break;
            case 2:
                movie.setPoster_path(value);
                break;
            case 3:
                movie.setDate(value);
                break;
            case 4:
                movie.setRating(value);
                break;
            case 5:
                movie.setOverview(value);
                break;
            case 6:
                movie.setId(value);
                break;
            case 7:
                if (value.equals("0")) {
                    movie.setIsFavorite(false);
                } else {
                    movie.setIsFavorite(true);
                }
                break;
        }
    }
}
