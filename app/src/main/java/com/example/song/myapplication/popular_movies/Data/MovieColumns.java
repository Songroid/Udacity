package com.example.song.myapplication.popular_movies.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Song on 1/5/16.
 */
public interface MovieColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    String TITLE = "title";
    @DataType(DataType.Type.TEXT) @NotNull
    String POSTER_PATH = "poster_path";
    @DataType(DataType.Type.TEXT) @NotNull
    String DATE = "date";
    @DataType(DataType.Type.TEXT) @NotNull
    String RATING = "rating";
    @DataType(DataType.Type.TEXT) @NotNull
    String OVERVIEW = "overview";
    @DataType(DataType.Type.TEXT) @NotNull
    String MOVIE_ID = "movie_id";
    @DataType(DataType.Type.INTEGER) @NotNull
    String IS_FAVORITE = "is_favorite";
}
