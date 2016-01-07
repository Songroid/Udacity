package com.example.song.myapplication.popular_movies.Data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Song on 1/5/16.
 */
@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {
    private MovieDatabase(){}

    public static final int VERSION = 1;

    @Table(MovieColumns.class) public static final String MOVIES = "movies";
    @Table(TrailerColumns.class) public static final String TRAILERS = "trailers";
    @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";
}
