package com.example.databaseproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.time.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import android.os.Build;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;

import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class feed extends AppCompatActivity {

    // For interacting with fragment types, literal fragments
    FragmentManager manager;

    // for interacting with descendants of fragment, e.g feed_post
    androidx.fragment.app.FragmentManager sManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fragment fragment // only supports fragment type Fragment
        manager = getFragmentManager();

        // (post_reaction) fragment // supports all fragments that are extended from Fragment
        sManager = getSupportFragmentManager();

        setContentView(R.layout.activity_post_creation);
        clearFeed();
        new Thread(() -> createPostsFromRemote()).start();
    }

    /**
     * Gather users, post and reaction from remote and inserts into local database, then calls method for creating post in local database
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createPostsFromRemote() {

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        JSONArray remoteUsers = remote.getEverythingFromRemote("users");
        List<String> userIdsInRemote = new ArrayList<>();
        for(int i = 0; i < remoteUsers.length(); i++) {
            try {
                JSONObject entry = remoteUsers.getJSONObject(i);
                db.userDao().insert(new User(entry.getString("id"), entry.getString("name"),entry.getString("stamp")));
                userIdsInRemote.add(entry.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        db.userDao().removeAllNotInRemote(userIdsInRemote);

        JSONArray remotePosts = remote.getEverythingFromRemote("posts");
        PostDao postdao = db.PostDao();
        List<Integer> postIdsInRemote = new ArrayList<>();
        for(int i = 0; i < remotePosts.length(); i++) {
            try {
                JSONObject entry = remotePosts.getJSONObject(i);
                postdao.insertAll(new Post(entry.getInt("id"),entry.getString("user_id"),entry.getString("content"),entry.getString("stamp")));
                postIdsInRemote.add(entry.getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Remove post in local DB which have been removed in remote
        postdao.removeAllNotInRemote(postIdsInRemote);

        JSONArray remoteReactions = remote.getEverythingFromRemote("reactions");
        ReactionDao reactdao = db.ReactionDao();
        for(int i = 0; i < remoteReactions.length(); i++) {
            try {
                JSONObject entry = remoteReactions.getJSONObject(i);
                reactdao.insertReactions(new Reaction(entry.getInt("post_id"), entry.getString("user_id"),entry.getInt("type"),entry.getString("stamp")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new Thread(() -> {
            List<Post> localPosts = postdao.getAll();
            for (Post post : localPosts)
                makePostInFeed(post);
        }).start();
    }

    /**
     * Makes the given post in the feed
     * @param post Post which are created in feed
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makePostInFeed(Post post){
        int[] reactions = getReactions(post);
        feed_post fragment = feed_post.newInstance(post, reactions);
        sManager.beginTransaction().add(R.id.postFeed, fragment, String.valueOf(post.id)).commit();
    }

    /**
     * Action for button on feed for initiating creating a post
     * @param view a reference to the current view
     */
    public void showPostCreationForm(View view){
        // Hide button and feed for now
        findViewById(R.id.postButton).setVisibility(View.GONE);
        findViewById(R.id.postFeed).setVisibility(View.GONE);

        // Instantiates textInput fragment
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = textForm.newInstance(false, null);

        // Add fragment to view
        transaction.add(R.id.postCreation, fragment, "textInput");
        transaction.addToBackStack("back");
        transaction.commit();
    }

    /**
     * Inserts a post into local and remote database, with the user from the session, content and image if given
     * @param content The text content of the post
     * @param image Image name on remote server
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertPostintoDatabases(String content, String image){
        Log.d("Post creation", "Post is being made!");

        // Show button and feed once comment has been made
        showFeed();

        SessionHandler sh = new SessionHandler(this,"user");
        String user_id = sh.getString("user_id");
        String epoch = OffsetDateTime.now().toString();

        Post post = new Post(user_id,content,epoch);

        clearFeed();
        Log.d("Post creation", "Feed has been cleared");
        new Thread(()->{
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "User").fallbackToDestructiveMigration().build();
            PostDao postdao = db.PostDao();

            List<Long> result = postdao.insertAll(post);
            post.id = result.get(0).intValue();

            if(image != null)
                db.ImageAttachmentDao().insert(post.id,image);

            JSONObject jsonPost = new JSONObject();
                try {
                    jsonPost.put("id",result.get(0));
                    jsonPost.put("user_id", user_id);
                    jsonPost.put("content",content);
                    jsonPost.put("stamp",epoch);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            remote.insertRemote("posts",jsonPost);

            // Remove fragment again
            manager.popBackStack();

            createPostsFromRemote();

        }).start();

    }

    /**
     * Close method called by cancel button which removes the post creation and shows the feed
     */
    public void close(){
        manager.popBackStack();
        showFeed();
    }

    /**
     * Inserts comment into local database
     * @param comment comment which are inserted
     */
    public void insertCommentIntoDatabase(Comment comment){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        CommentDao commentdao = db.CommentDao();
        new Thread(() -> commentdao.insert(comment)).start();
    }

    /**
     * Makes post button and feed with post visible
     */
    public void showFeed(){
        findViewById(R.id.postButton).setVisibility(View.VISIBLE);
        findViewById(R.id.postFeed).setVisibility(View.VISIBLE);
    }

    /**
     * Inserts a reaction with the given parameters into local and remote or update if it already exists
     * @param post_id The id of the post of which the reaction is made
     * @param type The type of reaction made on the post
     * @param user_id The id of the user which made the reaction
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertReactionIntoDatabases(int post_id, int type, String user_id){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        ReactionDao reactiondao = db.ReactionDao();
        String stamp = OffsetDateTime.now().toString();

        Reaction reaction = new Reaction(post_id, user_id, type, stamp);

        //JSON version of reaction for remote database
        JSONObject JSONReaction = new JSONObject();
        try {
            JSONReaction.put("user_id",user_id);
            JSONReaction.put("post_id",post_id);
            JSONReaction.put("type",type);
            JSONReaction.put("stamp",stamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Count reactions from user on the post
        int dbReturn = reactiondao.getReactionIdById(post_id,user_id);

        if(dbReturn != 0){
            Log.d("Comment", "Updated existing reaction");
            reactiondao.updateReaction(post_id,user_id,type);

            //Update remote
            try {
                JSONReaction.remove("type");
                JSONReaction.remove("stamp");
                remote.updateRemote("reactions",JSONReaction,new JSONObject().put("type",type).put("stamp",stamp));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else{
            reactiondao.insertReactions(reaction);
            remote.insertRemote("reactions",JSONReaction);
            Log.d("Comment", "New reaction posted");
        }
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int[] getReactions(Post post){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        ReactionDao reactiondao = db.ReactionDao();

        CompletableFuture<List<Reaction>> reactions = CompletableFuture.supplyAsync(() -> reactiondao.getReactions(post.id));
        try{
            List<Reaction> list = reactions.get();
            // 4 types of reactions, same type as index, deleted are on index 0, type 1 is index 1 and so forth
            int[] reactionsList = {0,0,0,0};
            for(Reaction r:list)
                reactionsList[r.type]++;

            return reactionsList;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all comments to a post
     * @param post_id Post id which the comments are made on
     * @return A list of comments to the post
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Comment> getComments(int post_id){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        CommentDao commentdao = db.CommentDao();
        //This is used such main thread does not wait on result
        CompletableFuture<List<Comment>> result = CompletableFuture.supplyAsync(() -> commentdao.getAllFromPostId(post_id));
        try{
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * removes all post on the feed
     */
    public void clearFeed(){
        ((ViewGroup)findViewById(R.id.postFeed)).removeAllViews();
    }
}