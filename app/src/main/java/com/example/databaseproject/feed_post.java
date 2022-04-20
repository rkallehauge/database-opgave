package com.example.databaseproject;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link feed_post#newInstance} factory method to
 * create an instance of this fragment.
 */
public class feed_post extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USERID = "param1";
    private static final String ARG_CONTENT = "param2";
    private static final String ARG_STAMP = "param3";
    private static final String ARG_ID = "param4";

    private String user_id;
    private String content;
    private String stamp;
    private Integer id;

    public feed_post() {
        // Required empty public constructor
    }


    public static feed_post newInstance(Post post) {

        feed_post fragment = new feed_post();

        Bundle args = new Bundle();

        args.putString(ARG_USERID, post.user_id);
        args.putString(ARG_CONTENT, post.content);
        args.putString(ARG_STAMP, post.stamp);
        args.putInt(ARG_ID, post.id);

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
        // Semi fix
        View view = inflater.inflate(R.layout.fragment_feed_post, container, false);


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO : Moved this out of the if-statement, not sure if the app crashes if getArguments() returns null, we'll see
        Bundle args = getArguments();

        if (getArguments() != null) {
            String uid, content, stamp;

            uid = args.getString(ARG_USERID);
            content = args.getString(ARG_CONTENT);
            stamp = args.getString(ARG_STAMP);

            TextView uidText = getView().findViewById(R.id.postCreator);
            uidText.setText(uid);

            TextView contentText = getView().findViewById(R.id.postContent);
            contentText.setText(content);

            TextView stampText = getView().findViewById(R.id.postStamp);
            stampText.setText(stamp);

        }

        // REACT button pressed
        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        Button button = (Button) getView().findViewById(R.id.postReact);

        int post_id = args.getInt(ARG_ID);
        String user_id = args.getString(ARG_USERID);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                /*
                 TODO : post_id only actually works when you open the app,
                   and the posts get loaded freshly, not when fragments are
                   loaded dynamically, would be nice if it did
                 */
                System.out.println("post_id: " + post_id);
                // TODO : Very scuffed, not good
                flipReaction(viewgroup);
            }
        });
        // Scuffed asf
        for(int i = 1; i <= 3; i++){
            View v = viewgroup.getChildAt(i);

            int type = i;

            v.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void onClick(View view) {

                    // Slightly scuffed
                    ((postCreation)getActivity()).makeReaction(post_id, type, user_id);

                    // Hide Reactions after reaction has been made.
                    flipReaction(viewgroup);
                }
            });
        }


    }

    private void flipReaction(ViewGroup v){
        for(int i = 1; i <= 3; i++){
            if(v.getChildAt(i).getVisibility() == View.VISIBLE)
                v.getChildAt(i).setVisibility(View.GONE);
            else
                v.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }
}
