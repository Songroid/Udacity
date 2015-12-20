package com.example.song.myapplication.popular_movies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Data.APIConstants;
import com.example.song.myapplication.popular_movies.Model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Song on 12/19/15.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{

    private List<Trailer> mTrailerList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView trailerText;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerText = (TextView) itemView.findViewById(R.id.trailer_textview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Trailer trailer = mTrailerList.get(position);
            String key = trailer.getKey();

            if (trailer.getSite().equals(APIConstants.YOUTUBE)) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        APIConstants.YOUTUBE_BASE_URL + "v=" + key));
                context.startActivity(browserIntent);
            }
        }
    }

    public TrailerAdapter(Context context, List<Trailer> mTrailerList) {
        this.context = context;
        this.mTrailerList = mTrailerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.recyclerview_trailer_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = mTrailerList.get(position);

        String name = trailer.getName();
        String site = trailer.getSite();

        TextView textView = holder.trailerText;
        textView.setText(name);

        switch(site) {
            case APIConstants.YOUTUBE:
                textView.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.ic_youtube,
                        0,
                        0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }
}
