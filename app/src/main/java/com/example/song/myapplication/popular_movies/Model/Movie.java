package com.example.song.myapplication.popular_movies.Model;

/**
 * Created by Song on 11/16/15.
 */
public class Movie implements Comparable {

    public static final String IS_FAVORITE = "isFavorite";

    private String title;
    private String poster_path;
    private String date;
    private String rating;
    private String overview;
    private String id;
    private boolean isFavorite;

    public Movie() {
    }

    public Movie(String title, String poster_path, String date, String rating, String overview, String id, boolean isFavorite) {
        this.title = title;
        this.poster_path = poster_path;
        this.date = date;
        this.rating = rating;
        this.overview = overview;
        this.id = id;
        this.isFavorite = isFavorite;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", date='" + date + '\'' +
                ", rating='" + rating + '\'' +
                ", overview='" + overview + '\'' +
                ", id='" + id + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }

    @Override
    public int compareTo(Object obj) {
        Movie compareObj = (Movie) obj;

        int value1 = isFavorite()? 1 : 0;
        int value2 = compareObj.isFavorite()? 1 : 0;

        return value2 - value1;
    }
}
