package com.example.song.myapplication.popular_movies.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.BaseAdapter;

import com.example.song.myapplication.popular_movies.Data.APIConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Song on 12/14/15.
 */
public class Misc {
    public static final String STAR_MARKED = "marked";
    public static final String STAR_UNMARKED = "unmarked";
    public static final String REVIEW_INTENT = "review_intent";

    public static ProgressDialog setUpDialog(ProgressDialog dialog, String title, String content) {
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setIndeterminate(true);
        return dialog;
    }

    public static List<JSONObject> parseResult(JSONObject input,
                                   List<JSONObject> list) throws JSONException {
        JSONArray movieArray = input.getJSONArray(APIConstants.RESULTS);
        list.clear();
        for (int i=0; i<movieArray.length(); i++) {
            list.add(movieArray.getJSONObject(i));
        }
        return list;
    }
}
