package com.example.song.myapplication.popular_movies.Adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Data.ApiConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Song on 11/15/15.
 */
public class MoviesAdapter extends BaseAdapter {
    private Context mContext;
    private List<JSONObject> movies;

    public MoviesAdapter(Context mContext, List<JSONObject> posterUrls) {
        this.mContext = mContext;
        this.movies = posterUrls;
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

        String url;
        try {
            url = ApiConstants.POSTER_IMG_BASE_URL + movies.get(i).getString(ApiConstants.POSTER_PATH);

            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .fit()
                    .tag(mContext)
                    .into(poster);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return poster;
    }
}