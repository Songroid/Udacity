package com.example.song.myapplication.popular_movies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Model.Movie;
import com.example.song.myapplication.popular_movies.Model.Review;
import com.example.song.myapplication.popular_movies.Model.Trailer;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Song on 12/23/15.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DatabaseHelper.class.getName();

    private static DatabaseHelper instance = null;
    private RuntimeExceptionDao<Movie, String> runtimeDao = null;
    private RuntimeExceptionDao<Trailer, String> trailersDao = null;
    private RuntimeExceptionDao<Review, String> reviewsDao = null;

    protected DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    public static DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.d(TAG, "onCreate");
            TableUtils.createTable(connectionSource, Movie.class);
            TableUtils.createTable(connectionSource, Trailer.class);
            TableUtils.createTable(connectionSource, Review.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "onUpgrade");
            TableUtils.dropTable(connectionSource, Movie.class, true);
            TableUtils.dropTable(connectionSource, Trailer.class, true);
            TableUtils.dropTable(connectionSource, Review.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public RuntimeExceptionDao<Movie, String> getMovieDao() {
        if (runtimeDao == null) {
            runtimeDao = getRuntimeExceptionDao(Movie.class);
        }
        return runtimeDao;
    }

    public RuntimeExceptionDao<Trailer, String> getTrailerDao() {
        if (trailersDao == null) {
            trailersDao = getRuntimeExceptionDao(Trailer.class);
        }
        return trailersDao;
    }

    public RuntimeExceptionDao<Review, String> getReviewDao() {
        if (reviewsDao == null) {
            reviewsDao = getRuntimeExceptionDao(Review.class);
        }
        return reviewsDao;
    }

    @Override
    public void close() {
        super.close();
        runtimeDao = null;
        trailersDao = null;
        reviewsDao = null;
    }
}
