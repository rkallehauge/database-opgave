package com.example.databaseproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link post_comment#newInstance} factory method to
 * create an instance of this fragment.
 */
//TODO: Is there not a better way than first create an emtpy comment and then insert text?
public class post_comment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CONTENT = "param1";
    private static final String ARG_USERID = "param2";
    private static final String ARG_POSTID = "param3";
    private static final String ARG_STAMP = "param4";

    public post_comment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static post_comment newInstance(String content, String user_id, int post_id, String stamp) {
        post_comment fragment = new post_comment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_USERID, user_id);
        args.putInt(ARG_POSTID, post_id);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_comment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getArguments() != null){

            // Get arguments set in constructor
            Bundle args = getArguments();
            String content = args.getString(ARG_CONTENT);
            String user_id = args.getString(ARG_USERID);
            String stamp = args.getString(ARG_STAMP);

            // Update placeholder text into actual content
            ((TextView) getView().findViewById(R.id.commentContent)).setText(content);
            ((TextView) getView().findViewById(R.id.commentCreator)).setText(user_id);
            ((TextView) getView().findViewById(R.id.commentStamp)).setText(stamp);

        }
    }
}