package com.example.song.myapplication.popular_movies.Model;

/**
 * Created by Song on 12/19/15.
 */
public class Trailer {
    private String name;
    private String site;
    private String key;
    private String id;

    public Trailer() {
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
