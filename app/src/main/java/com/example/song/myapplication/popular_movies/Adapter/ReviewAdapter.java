package com.example.song.myapplication.popular_movies.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Model.Review;

import java.util.List;

/**
 * Created by Song on 12/21/15.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameView;
        public TextView commentView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.review_name);
            commentView = (TextView) itemView.findViewById(R.id.review_content);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View review = inflater.inflate(R.layout.recyclerview_review_item, parent, false);

        return new ViewHolder(review);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviews.get(position);

        TextView name = holder.nameView;
        TextView comment = holder.commentView;

        name.setText(review.getAuthor());
        comment.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

}
