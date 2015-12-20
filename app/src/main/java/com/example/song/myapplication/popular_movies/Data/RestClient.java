package com.example.song.myapplication.popular_movies.Data;

import android.content.Context;
import android.widget.ImageView;

import com.example.song.myapplication.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Song on 11/16/15.
 */
public class RestClient {

    public static void getPosterImage(String url, Context context, ImageView placeholder) {
        Picasso.with(context)
                .load(APIConstants.POSTER_IMG_BASE_URL + url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fit()
                .tag(context)
                .into(placeholder);
    }

}
