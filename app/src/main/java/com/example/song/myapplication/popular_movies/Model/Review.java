package com.example.song.myapplication.popular_movies.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Song on 12/20/15.
 */
public class Review implements Parcelable {

    public static final String MOVIE_ID = "movieId";

    private String author;
    private String content;
    private String movieId;
    private String reviewId;

    private Review(Parcel in) {
        author = in.readString();
        content = in.readString();
        movieId = in.readString();
        reviewId = in.readString();
    }

    public Review() {
    }

    public Review(String author, String content, String movieId, String reviewId) {
        this.author = author;
        this.content = content;
        this.movieId = movieId;
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Review{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", movieId='" + movieId + '\'' +
                ", reviewId='" + reviewId + '\'' +
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

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
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
        out.writeString(reviewId);
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
