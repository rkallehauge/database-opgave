package com.example.databaseproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link feed_post#newInstance} factory method to
 * create an instance of this fragment.
 */
public class feed_post extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USERID  = "param1";
    private static final String ARG_CONTENT = "param2";
    private static final String ARG_STAMP   = "param3";

    // TODO: Rename and change types of parameters
    private String user_id;
    private String content;
    private String stamp;

    public feed_post() {
        // Required empty public constructor
    }


    public static feed_post newInstance(String userid, String content, String stamp) {

        feed_post fragment = new feed_post();
        Bundle args = new Bundle();

        args.putString(ARG_USERID, userid);
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_STAMP, stamp);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_feed_post, container, false);
    }

    public void setText(String userid, String content, String stamp){
        TextView uidText = (TextView) getView().findViewById(R.id.postCreator);
        uidText.setText(userid);

        TextView contentText = (TextView) getView().findViewById(R.id.postContent);
        contentText.setText(content);

        TextView stampText = (TextView) getView().findViewById(R.id.postStamp);
        stampText.setText(stamp);
    }
}