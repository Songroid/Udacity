package com.example.song.myapplication.popular_movies.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Song on 1/5/16.
 */
public interface TrailerColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    String NAME = "name";
    @DataType(DataType.Type.TEXT) @NotNull
    String SITE = "site";
    @DataType(DataType.Type.TEXT) @NotNull
    String KEY = "key";
    @DataType(DataType.Type.TEXT) @NotNull
    String ID = "id";
}
