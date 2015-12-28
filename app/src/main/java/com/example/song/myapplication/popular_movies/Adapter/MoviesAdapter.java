package com.example.song.myapplication.popular_movies.Adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.song.myapplication.popular_movies.Data.APIConstants;
import com.example.song.myapplication.popular_movies.Data.RestClient;
import com.example.song.myapplication.popular_movies.Model.Movie;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Song on 11/15/15.
 */
public class MoviesAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> movies;

    public MoviesAdapter(Context mContext, List<Movie> movies) {
        this.mContext = mContext;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return movies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView poster = (ImageView) view;

        if (poster == null) {
            poster = new ImageView(mContext);
            poster.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,
                    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 277,
                            mContext.getResources().getDisplayMetrics())));
            poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        RestClient.getPosterImage(movies.get(i).getPoster_path(), mContext, poster);

        return poster;
    }
}
