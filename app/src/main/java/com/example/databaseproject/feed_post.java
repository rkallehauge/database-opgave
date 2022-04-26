package com.example.databaseproject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.time.OffsetDateTime;
import java.util.List;

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
    private static final String ARG_REACTIONS = "param5";
    private static final String STATE_COMMENTS = "param6";


    private String user_id;
    private String content;
    private String stamp;
    private Integer id;

    private int[] reactions;

    public feed_post() {
        // Required empty public constructor
    }


    public static feed_post newInstance(Post post, int[] reactions) {

        feed_post fragment = new feed_post();
        Bundle args = new Bundle();



        args.putString(ARG_USERID, post.user_id);
        args.putString(ARG_CONTENT, post.content);
        args.putString(ARG_STAMP, post.stamp);
        // System.out.println(" Fragment : :  " + post.id);
        args.putInt(ARG_ID, post.id);
        args.putIntArray(ARG_REACTIONS, reactions);
        args.putBoolean(STATE_COMMENTS, false);


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
        getView().setId(getArguments().getInt(ARG_ID));

        int post_id = args.getInt(ARG_ID);
        String user_id = args.getString(ARG_USERID);
        // REACT button pressed
        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        Button button = viewgroup.findViewById(R.id.postReact);
        // REACT button listener
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("post_id: " + post_id);
                flipViewVisibility(viewgroup, R.id.reactionImageContainer);
                flipViewVisibility(viewgroup, R.id.postComment);
            }
        });

        button =  viewgroup.findViewById(R.id.postComment);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                flipViewVisibility(viewgroup, R.id.postReact);
                flipViewVisibility(viewgroup, R.id.postComment);

                FragmentManager manager = getActivity().getFragmentManager();
                FragmentTransaction t = manager.beginTransaction();

                android.app.FragmentTransaction transaction = manager.beginTransaction();

                // Little bit scuffed, but it works
                android.app.Fragment fragment = textInput.newInstance(true, String.valueOf(args.getInt(ARG_ID)));
                System.out.println("inputText fragment started");

                transaction.add(id, fragment, "textInput");
                transaction.addToBackStack("idkbro");
                transaction.commit();


            }
        });

        // Comments open button listener
        ImageButton b = getView().findViewById(R.id.openComments);
        b.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                int count = ((ViewGroup) getView().findViewById(R.id.commentContainer)).getChildCount();
                System.out.println(count);
                if(count==0){
                String content = args.getString(ARG_CONTENT);
                String user_id = args.getString(ARG_USERID);
                int post_id = args.getInt(ARG_ID);
                String stamp = args.getString(ARG_STAMP);
                // childcount > 0 or something to not duplicate entries after init load
                List<Comment> comments = ((postFeed)getActivity()).getComments(post_id);

                androidx.fragment.app.FragmentManager manager = getActivity().getSupportFragmentManager();
                androidx.fragment.app.FragmentTransaction t = manager.beginTransaction();
                for(Comment c:comments){
                    post_comment comment = post_comment.newInstance(c.content,c.user_id,c.post_id,c.stamp);
                    t.add(R.id.commentContainer, comment,null);
                }
                t.commit();
                args.putBoolean(STATE_COMMENTS, true);
                } else{
                    if(args.getBoolean(STATE_COMMENTS)){
                        getView().findViewById(R.id.commentContainer).setVisibility(View.GONE);
                        args.putBoolean(STATE_COMMENTS, false);
                    } else{
                        getView().findViewById(R.id.commentContainer).setVisibility(View.VISIBLE);
                        args.putBoolean(STATE_COMMENTS, true);
                    }
                }
            }
        });

        // Scuffed asf

        int[] reactions = args.getIntArray(ARG_REACTIONS);
        for(int i = 0; i <= 2; i++){
            ViewGroup vg = (ViewGroup) viewgroup.findViewById(R.id.reactionImageContainer);

            View v = ((ViewGroup)vg.getChildAt(i)).getChildAt(0);

            TextView textview = (TextView) ((ViewGroup)vg.getChildAt(i)).getChildAt(1);
            textview.setText("Votes: " +reactions[i]);
            // Types are defined as 1 : 2 : 3
            int type = i+1;

            v.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void onClick(View view) {

                    // Slightly scuffed
                    ((postFeed)getActivity()).makeReaction(post_id, type, user_id);

                    // Hide Reactions after reaction has been made.
                    flipViewVisibility(viewgroup, R.id.reactionImageContainer);
                    flipViewVisibility(viewgroup, R.id.postComment);

                }
            });
        }


    }

    private void flipViewVisibility(ViewGroup v, int viewId){
        if(v.findViewById(viewId).getVisibility() == View.GONE){
            v.findViewById(viewId).setVisibility(View.VISIBLE);
        }
        else{
            v.findViewById(viewId).setVisibility(View.GONE);
        }
    }

    // This here is a comment post, not a post post, not to be confused with postFeed.post(), which is a post post
    public void post(String input){
        Bundle args = getArguments();
        int post_id = args.getInt(ARG_ID);
        String user_id = args.getString(ARG_USERID);

        Comment comment = new Comment();
        comment.content = input;
        comment.user_id = user_id;
        comment.post_id = post_id;
        comment.stamp = OffsetDateTime.now().toString();

        ((postFeed) getActivity()).makeComment(comment);

        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        flipViewVisibility(viewgroup, R.id.postReact);
        flipViewVisibility(viewgroup, R.id.postComment);


    }

    // This bugs when you try to comment on multiple things at once, perhaps a hideAllBut(int id) would save our lives
    public void close(){
        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        flipViewVisibility(viewgroup, R.id.postReact);
        flipViewVisibility(viewgroup, R.id.postComment);
        getActivity().getFragmentManager().popBackStack();
    }


    public List<Comment> getComments(){
        List<Comment> p = null;
        return p;
    }

}
