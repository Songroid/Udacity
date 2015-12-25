package com.example.song.myapplication.popular_movies.Model;

import com.example.song.myapplication.popular_movies.Data.APIConstants;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Song on 12/19/15.
 */
public class Trailer {
    @DatabaseField(index = true)
    private String name;
    @DatabaseField
    private String site;
    @DatabaseField(id = true)
    private String key;
    @DatabaseField(columnName = APIConstants.ID)
    private String id;

    public Trailer() {
        // needed by ormlite
    }

    public Trailer(String name, String site, String key, String id) {
        this.name = name;
        this.site = site;
        this.key = key;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
