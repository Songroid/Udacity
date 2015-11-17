package com.example.song.myapplication.popular_movies.Model;

/**
 * Created by Song on 11/16/15.
 */
public class Movie {
    private String title;
    private String poster_path;
    private String date;
    private String rating;
    private String overview;

    public Movie(String title, String poster_path, String date, String rating, String overview) {
        this.title = title;
        this.poster_path = poster_path;
        this.date = date;
        this.rating = rating;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
