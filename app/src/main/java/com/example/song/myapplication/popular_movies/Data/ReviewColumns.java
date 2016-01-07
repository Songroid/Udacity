package com.example.song.myapplication.popular_movies.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Song on 1/5/16.
 */
public interface ReviewColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    String AUTHOR = "author";
    @DataType(DataType.Type.TEXT) @NotNull
    String CONTENT = "content";
    @DataType(DataType.Type.TEXT) @NotNull
    String MOVIE_ID = "movieId";
    @DataType(DataType.Type.TEXT) @NotNull
    String REVIEW_ID = "reviewId";
}
