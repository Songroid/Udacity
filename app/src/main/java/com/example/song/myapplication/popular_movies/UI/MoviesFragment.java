package com.example.song.myapplication.popular_movies.UI;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.song.myapplication.BuildConfig;
import com.example.song.myapplication.R;
import com.example.song.myapplication.popular_movies.Data.ApiConstants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MoviesFragment extends Fragment {
    private static final String TAG = "MoviesFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ProgressDialog mDialog;

    public static MoviesFragment newInstance(String param1, String param2) {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mDialog = new ProgressDialog(getActivity());
        setUpDialog(mDialog);
        mDialog.show();

        getMovies(ApiConstants.POPULARITY_DESC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    private void getMovies(String sortOrder) {
        OkHttpClient client = new OkHttpClient();
        String url = ApiConstants.BASE_URL +
                ApiConstants.SORT_BY + "=" + sortOrder + "&" +
                ApiConstants.API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "getMovies onFailure");
                mDialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Log.d(TAG, response.body().string());
                mDialog.dismiss();
            }
        });

    }

    private void setUpDialog(ProgressDialog dialog) {
        dialog.setTitle(getString(R.string.fragment_movie_loading_title));
        dialog.setMessage(getString(R.string.fragment_movie_loading_message));
        dialog.setIndeterminate(true);
    }
}
