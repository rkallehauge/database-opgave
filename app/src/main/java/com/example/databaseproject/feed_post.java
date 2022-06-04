package com.example.databaseproject;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link feed_post#newInstance} factory method to
 * create an instance of this fragment.
 */
public class feed_post extends Fragment {

    private static final String IMAGE_URL = "https://kklokker.com/databaseApp/";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USERID = "param1";
    private static final String ARG_CONTENT = "param2";
    private static final String ARG_STAMP = "param3";
    private static final String ARG_ID = "param4";
    private static final String ARG_REACTIONS = "param5";
    private static final String STATE_COMMENTS = "param6";
    private static final String R_ID_CC = "param7";

    public feed_post() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of a post on the feed
     * @param post The post of which is represented
     * @param reactions The reactions to the post
     * @return A feed_post object with the given post and reactions and other default values
     */
    public static feed_post newInstance(Post post, int[] reactions) {

        feed_post fragment = new feed_post();
        Bundle args = new Bundle();

        args.putString(ARG_USERID, post.user_id);
        args.putString(ARG_CONTENT, post.content);
        args.putString(ARG_STAMP, post.stamp);
        args.putInt(ARG_ID, post.id);
        args.putIntArray(ARG_REACTIONS, reactions); //Count of reactions, index is type of reaction
        args.putBoolean(STATE_COMMENTS, false); //Comment container opening state - true if open, false if closed
        args.putInt(R_ID_CC, 20557+post.id); //Comment container id (an arbitrarily chosen number plus the post.id)

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

    /**
     * Initialize the post, with handlers, comment and reactions
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();

        //Init post and get post object
        Post post = initPost(args);

        ((ViewGroup)(getView().findViewById(R.id.commentWrapper))).getChildAt(0).setId(args.getInt(R_ID_CC));
        int commentContainer = args.getInt(R_ID_CC);
        int post_id = args.getInt(ARG_ID);

        // REACT button pressed
        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        Button button = viewgroup.findViewById(R.id.postReact);

        // REACT button listener
        button.setOnClickListener((View view) -> {
                flipViewVisibility(viewgroup, R.id.reactionImageContainer);
                flipViewVisibility(viewgroup, R.id.postComment);
        });

        //Make comment button listener
        viewgroup.findViewById(R.id.postComment).setOnClickListener(
                (View view) -> openCommentForm(viewgroup, post_id,commentContainer));

        // Comments open button listener
        ImageButton b = getView().findViewById(R.id.openComments);
        b.setOnClickListener((View view) -> {
                // If comments are visible, hide them
                if(args.getBoolean(STATE_COMMENTS)){
                    killComments(commentContainer);
                    args.putBoolean(STATE_COMMENTS, false);
                }
                //If not visible then make them visible
                else{
                    updateComments(commentContainer,post_id);
                    args.putBoolean(STATE_COMMENTS, true);
                }

                // Flip rotation of button
                view.setRotation((view.getRotation() + 180) % 360);
            }
        );

        // Standard reactions
        showReactions(args.getIntArray(ARG_REACTIONS),viewgroup, post);

        //Delete reaction handler on the bin button
        ((ViewGroup) ((ViewGroup) viewgroup.findViewById(R.id.reactionImageContainer))
                .getChildAt(3)).getChildAt(0).setOnClickListener((View view) -> {

            // Modal Dialog for confirmation of deletion of comment
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete your reaction?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes,
                            (DialogInterface dialogInterface, int i) -> makeReaction(0,post))
                                .setNegativeButton(android.R.string.no, null).show();
            // Close for good measure
            flipViewVisibility(viewgroup, R.id.reactionImageContainer);
            flipViewVisibility(viewgroup, R.id.postComment);
        });
    }

    /**
     * Initialize post from given arguments, if arguments exist the UI of post and post is created
     * @param args Arguments which the post is made of
     * @return Post made using the given arguments
     */
    private Post initPost(Bundle args) {
        if (getArguments() != null) {
            Log.d("Post creation", "Post " + args.getString(ARG_USERID) + "getting initialized");
            String uid, content, stamp;
            int id;

            uid = args.getString(ARG_USERID);
            content = args.getString(ARG_CONTENT);
            stamp = args.getString(ARG_STAMP);
            id = args.getInt(ARG_ID);

            //Create the UI for post
            TextView uidText = getView().findViewById(R.id.postCreator);
            uidText.setText(uid);

            TextView contentText = getView().findViewById(R.id.postContent);
            contentText.setText(content);

            TextView stampText = getView().findViewById(R.id.postStamp);
            stampText.setText(stamp);

            //Start thread for loading image
            new Thread(() -> {
                AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "User").fallbackToDestructiveMigration().build();
                String image = db.ImageAttachmentDao().path(id);
                if(image != null) {
                    ImageView imageView = getView().findViewById(R.id.postImage);
                    InputStream imageInput = null;
                    try {
                        imageInput = new URL(IMAGE_URL + image).openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(imageInput);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            return new Post(id,uid,content,stamp);
        } else{
            // To soothe the IDE and compiler ( Variable might not have been initialized )
            return new Post();
        }
    }

    /**
     * Method for submitting comment
     * @param viewgroup The viewgroup the comment exist in
     * @param post_id The id of the post the comment is made on
     * @param commentContainer The container which the comment exist in
     */
    private void openCommentForm(ViewGroup viewgroup, int post_id, int commentContainer) {
        flipViewVisibility(viewgroup, R.id.postReact);
        flipViewVisibility(viewgroup, R.id.postComment);

        CloseUnClosedCommentInput();

        // Fragment manager
        FragmentManager manager = getActivity().getFragmentManager();

        // Instantiate textInput fragment
        android.app.FragmentTransaction transaction = manager.beginTransaction();
        android.app.Fragment fragment = textInput.newInstance(true, String.valueOf(post_id));

        // Add fragment to view
        transaction.add(commentContainer, fragment, String.valueOf(post_id));
        transaction.addToBackStack(String.valueOf(post_id));
        transaction.commit();
    }

    /**
     * Creates the UI for reactions
     * @param reactions Amount of reaction on post
     * @param viewgroup The viewgroup the post exist
     * @param post The post the reaction is made on
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showReactions(int[] reactions, ViewGroup viewgroup, Post post) {
        ViewGroup vg = (ViewGroup) viewgroup.findViewById(R.id.reactionImageContainer);
        for(int i = 0; i <= 2; i++){
            //i is added with 1 due to 0 being deleted and therefore a shift is made
            View reactionButton = ((ViewGroup)vg.getChildAt(i)).getChildAt(0);

            ViewGroup reactionCountParent = ((ViewGroup) ((ViewGroup)vg.getChildAt(i)).getChildAt(1));
            TextView reactionCountCounter = (TextView) reactionCountParent.getChildAt(0);
            TextView reactionCountText = (TextView) reactionCountParent.getChildAt(1);

            //If reaction is larger than 1 or equal 0 then write "votes"
            String plural = ( reactions[i+1] > 1 || reactions[i+1] == 0) ? " votes" : " vote";

            reactionCountText.setText(plural);
            reactionCountCounter.setText(String.valueOf(reactions[i+1]));

            int type = i+1;

            reactionButton.setOnClickListener((View view) -> {
                // Hide Reactions after reaction has been made.
                flipViewVisibility(viewgroup, R.id.reactionImageContainer);
                flipViewVisibility(viewgroup, R.id.postComment);
                makeReaction(type,post);

            });
        }
    }

    /**
     * Removes all comment in a given container
     * @param commentContainerId The id of the container of which the comments are removed
     */
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

    /**
     * Get new comments and add them to the post
     * @param commentContainerId The container id of which the comments are updated
     * @param postId The post id which the comments belong
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateComments(int commentContainerId, int postId) {
        List<Comment> comments = ((feed)getActivity()).getComments(postId);

        androidx.fragment.app.FragmentManager manager = getActivity().getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction = manager.beginTransaction();
        for(Comment comment:comments)
            transaction.add(
                    commentContainerId,
                    post_comment.newInstance(comment.content,comment.user_id,comment.post_id,comment.stamp),
                    null
            );
        transaction.commit();
    }

    /**
     * Closes all comment inputs to prevent confusion on which post are being commented
     */
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

    /**
     * Flips the visibility of object with given id in viewgroup
     * @param v Viewgroup of which the flipped object exist
     * @param viewId The id of the flipped object
     */
    private void flipViewVisibility(ViewGroup v, int viewId){
        if(v.findViewById(viewId).getVisibility() == View.GONE)
            v.findViewById(viewId).setVisibility(View.VISIBLE);
        else
            v.findViewById(viewId).setVisibility(View.GONE);
    }

    /**
     * Creates the comment with the given input and inserts into databases and update comment container
     * @param input The input for the comment
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeComment(String input){
        Bundle args = getArguments();
        int post_id = args.getInt(ARG_ID);

        SessionHandler sh = new SessionHandler(getContext(),"user");
        String user_id = sh.getString("user_id");

        Comment comment = new Comment();
        comment.content = input;
        comment.user_id = user_id;
        comment.post_id = post_id;
        comment.stamp = OffsetDateTime.now().toString();

        ((feed) getActivity()).insertCommentIntoDatabase(comment);

        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        flipViewVisibility(viewgroup, R.id.postReact);
        flipViewVisibility(viewgroup, R.id.postComment);

        killComments(args.getInt(R_ID_CC));
        updateComments(args.getInt(R_ID_CC),post_id);
    }

    /**
     * Closes the comment after submitted or cancelled and shows reaction button again
     */
    public void closeCommentAndShowReaction(){
        ViewGroup viewgroup = getView().findViewById(R.id.reactionContainer);
        flipViewVisibility(viewgroup, R.id.postReact);
        flipViewVisibility(viewgroup, R.id.postComment);
        getActivity().getFragmentManager().popBackStack();
    }

    /**
     * Updates the post's reaction count
     * @param post The post of which the reactions are updated with new numbers
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateReactionCount(Post post){
        int[] reactions = ((feed) getActivity()).getReactions(post);
        ViewGroup vg = getView().findViewById(R.id.reactionImageContainer);
        for(int i = 1; i <= 3; i++){

            // Markup navigation
            ViewGroup reactionCountParent = ((ViewGroup) ((ViewGroup)vg.getChildAt(i-1)).getChildAt(1));
            TextView reactionCountCounter = (TextView) reactionCountParent.getChildAt(0);

            reactionCountCounter.setText(String.valueOf( reactions[i] ));

            // More markup navigation
            TextView reactionCountPlural = (TextView) reactionCountParent.getChildAt(1);
            String plural = ( reactions[i] > 1 || reactions[i] == 0) ? " votes" : " vote";

            reactionCountPlural.setText(plural);
        }
    }

    /**
     * Insert or update the reaction which is made, and then call to update the post reaction count
     * @param type The type of reaction made
     * @param post The post which the reaction is made upon
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeReaction(int type, Post post) {
        Bundle args = getArguments();
        int post_id = args.getInt(ARG_ID);
        SessionHandler sh = new SessionHandler(getContext(), "user");
        String user_id = sh.getString("user_id");
        //Wait for the database to synchronize then use return to update reaction counter in view
        CompletableFuture<Boolean> reactionIsMade = new CompletableFuture<>().supplyAsync(() -> {
            ((feed) getActivity()).insertReactionIntoDatabases(post_id, type, user_id);
            return true;
        });

        try {
            if(reactionIsMade.get()) updateReactionCount(post);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
