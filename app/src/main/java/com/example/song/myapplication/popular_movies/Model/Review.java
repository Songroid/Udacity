package com.example.song.myapplication.popular_movies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Song on 12/20/15.
 */
public class Review implements Parcelable {

    public static final String MOVIE_ID = "movieId";

    @DatabaseField(index = true)
    private String author;
    @DatabaseField
    private String content;
    @DatabaseField
    private String movieId;
    @DatabaseField(generatedId = true)
    private int id;

    public Review() {
        // needed by ormlite
    }

    private Review(Parcel in) {
        author = in.readString();
        content = in.readString();
        movieId = in.readString();
    }

    public Review(String author, String content, String movieId) {
        this.author = author;
        this.content = content;
        this.movieId = movieId;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Review{" +
                "movieId='" + movieId + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(author);
        out.writeString(content);
        out.writeString(movieId);
    }

    public static final Parcelable.Creator<Review> CREATOR
            = new Parcelable.Creator<Review>() {

        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
