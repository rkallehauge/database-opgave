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
    private static final String R_ID_CC = "param7";

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
        args.putInt(ARG_ID, post.id);
        args.putIntArray(ARG_REACTIONS, reactions); //Count of reactions, index is type of reaction
        args.putBoolean(STATE_COMMENTS, false); //Comment container opening state - true if open, false if closed
        args.putInt(R_ID_CC, 20557+post.id); //Comment container id
        //TODO find better method for finding an id for comment part of post

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
        View view = inflater.inflate(R.layout.fragment_feed_post, container, false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();

        //If arguments for post are initiated
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

        ((ViewGroup)(getView().findViewById(R.id.commentWrapper))).getChildAt(0).setId(args.getInt(R_ID_CC));
        int commentContainer = args.getInt(R_ID_CC);
        int post_id = args.getInt(ARG_ID);
        String user_id = args.getString(ARG_USERID);
        // REACT button pressed
        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        Button button = viewgroup.findViewById(R.id.postReact);
        // REACT button listener
        button.setOnClickListener((View view) -> {
                flipViewVisibility(viewgroup, R.id.reactionImageContainer);
                flipViewVisibility(viewgroup, R.id.postComment);
            }
        );

        //Make comment button listener
        button = viewgroup.findViewById(R.id.postComment);
        button.setOnClickListener((View view) -> {
                flipViewVisibility(viewgroup, R.id.postReact);
                flipViewVisibility(viewgroup, R.id.postComment);

                CloseUnClosedCommentInput();

                //Add text input for comment
                FragmentManager manager = getActivity().getFragmentManager();
                FragmentTransaction t = manager.beginTransaction();

                android.app.FragmentTransaction transaction = manager.beginTransaction();
                android.app.Fragment fragment = textInput.newInstance(true, String.valueOf(args.getInt(ARG_ID)));
                transaction.add(commentContainer, fragment, String.valueOf(post_id));

                transaction.addToBackStack(String.valueOf(post_id));
                transaction.commit();
            }
        );

        // Comments open button listener
        ImageButton b = getView().findViewById(R.id.openComments);
        b.setOnClickListener((View view) -> {


                if(args.getBoolean(STATE_COMMENTS)){
                    killComments(commentContainer);
                    args.putBoolean(STATE_COMMENTS, false);
                } else{
                    updateComments(commentContainer,post_id);
                    args.putBoolean(STATE_COMMENTS, true);
                }

                // Flip rotation of button
                float r = (view.getRotation() + 180) % 360;
                view.setRotation(r);
            }
        );

        int[] reactions = args.getIntArray(ARG_REACTIONS);
        for(int i = 0; i <= 2; i++){
            ViewGroup vg = (ViewGroup) viewgroup.findViewById(R.id.reactionImageContainer);

            View v = ((ViewGroup)vg.getChildAt(i)).getChildAt(0);
            TextView textview = (TextView) ((ViewGroup)vg.getChildAt(i)).getChildAt(1);
            textview.setText("Votes: " +reactions[i]);
            // Types are defined as 1 : 2 : 3
            int type = i+1;

            v.setOnClickListener((View view) -> {
                    ((postFeed)getActivity()).makeReaction(post_id, type, user_id);

                    // Hide Reactions after reaction has been made.
                    flipViewVisibility(viewgroup, R.id.reactionImageContainer);
                    flipViewVisibility(viewgroup, R.id.postComment);
                }
            );
        }
    }

    private void killComments(int commentContainerId) {
        ViewGroup commentContainer = getView().findViewById(commentContainerId);

        //Remove all existing comments except the comment creation
        int i = 0;
        while(i < commentContainer.getChildCount()) {
            ViewGroup commentElement = (ViewGroup)((ViewGroup) commentContainer.getChildAt(i)).getChildAt(0);
            //Remove element if not the comment input otherwise add 1 to i to ignore comment input
            if(commentElement.getId() != R.id.userTextFragment)
                commentContainer.removeView(commentContainer.getChildAt(i));
            else i++;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateComments(int commentContainerId, int postId) {
        //Insert all new comments
        List<Comment> comments = ((postFeed)getActivity()).getComments(postId);

        androidx.fragment.app.FragmentManager manager = getActivity().getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction t = manager.beginTransaction();
        for(Comment c:comments)
            t.add(
                    commentContainerId,
                    post_comment.newInstance(c.content,c.user_id,c.post_id,c.stamp),
                    null
            );
        t.commit();
    }

    private void CloseUnClosedCommentInput() {
        View otherOpenComment = ((ViewGroup) getView().getParent()).findViewById(R.id.userTextFragment);
        //If a comment is opened
        if(otherOpenComment != null ) {
            ViewGroup fragmentWithOpenComment = (ViewGroup) otherOpenComment.getParent().getParent();
            ViewGroup postWithOpenComment = (ViewGroup) otherOpenComment.getParent().getParent().getParent().getParent();

            //Show react and comment button
            flipViewVisibility(postWithOpenComment, R.id.postReact);
            flipViewVisibility(postWithOpenComment, R.id.postComment);

            //Remove the opened comment textInput
            fragmentWithOpenComment.removeView((View) otherOpenComment.getParent());
        }
    }

    private void flipViewVisibility(ViewGroup v, int viewId){
        if(v.findViewById(viewId).getVisibility() == View.GONE)
            v.findViewById(viewId).setVisibility(View.VISIBLE);
        else
            v.findViewById(viewId).setVisibility(View.GONE);
    }

    // This here is a comment post, not a post post, not to be confused with postFeed.post(), which is a post post
    @RequiresApi(api = Build.VERSION_CODES.O)
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

        killComments(args.getInt(R_ID_CC));
        updateComments(args.getInt(R_ID_CC),post_id);
    }

   public void close(){
        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        flipViewVisibility(viewgroup, R.id.postReact);
        flipViewVisibility(viewgroup, R.id.postComment);
        getActivity().getFragmentManager().popBackStack();
    }


}
