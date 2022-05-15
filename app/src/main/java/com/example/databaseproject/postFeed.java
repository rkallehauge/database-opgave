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

public class postFeed extends AppCompatActivity {

    // For interacting with fragment types, literal fragments
    FragmentManager manager;


    // for interacting with descendants of fragment, e.g feed_post
    androidx.fragment.app.FragmentManager sManager;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fragment fragment
        manager = getFragmentManager();

        // feed_post
        // post_reaction
        sManager = getSupportFragmentManager();

        setContentView(R.layout.activity_post_creation);
        clearFeed();
        new Thread(() -> createPostFromRemote()).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createPostFromRemote() {
        JSONArray remoteUsers = remote.getEverythingFromRemote("users");
        for(int i = 0; i < remoteUsers.length(); i++) {
            try {
                JSONObject entry = remoteUsers.getJSONObject(i);
                AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "User").fallbackToDestructiveMigration().build();
                db.UserDao().insert(new User(entry.getString("id"), entry.getString("name"),entry.getString("stamp")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray remotePosts = remote.getEverythingFromRemote("posts");
        List<Post> postList = new ArrayList<>();
        for(int i = 0; i < remotePosts.length(); i++) {
            try {
                JSONObject entry = remotePosts.getJSONObject(i);
                postList.add(new Post(entry.getInt("id"),entry.getString("user_id"),entry.getString("content"),entry.getString("stamp")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        PostDao postdao = db.PostDao();
        for(Post p: postList)
            postdao.insertAll(p);

        List<Post> l  = getPosts(db);
        for(Post post:l)
            makePost(post);
    }

    //Main feed post button
    public void createPost(View view){

        // Hide button and feed for now
        hideFeed();

        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = textInput.newInstance(false, null);

        transaction.add(R.id.postCreation, fragment, "textInput");
        transaction.addToBackStack("back");
        transaction.commit();
    }

    // TODO : Find less scuffed way to handle text, possibly (hopefully ) through textInput
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void post(String content, String image){
        Log.d("Post creation", "Post is being made!");

        // Show button again
        showFeed();

        SessionHandler sh = new SessionHandler(this,"user");
        String user_id = sh.getString("user_id");
        String epoch = OffsetDateTime.now().toString();

        Post post = new Post(user_id,content,epoch, image);

        clearFeed();
        Log.d("Post creation", "Feed has been cleared");
        new Thread(()->{

            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "User").fallbackToDestructiveMigration().build();
            PostDao postdao = db.PostDao();

            List<Long> result = postdao.insertAll(post);
            post.id = result.get(0).intValue();

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

            createPostFromRemote();

        }).start();

    }

    // called from fragment
    public void close(String id){
        manager.popBackStack();
        // Show reactionContainer again

        showFeed();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Post> getPosts(AppDatabase db){
        PostDao postdao = db.PostDao();
        CompletableFuture<List<Post>> posts = CompletableFuture.supplyAsync(postdao::getAll);
        try {
            return posts.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Only inserts comment into db, doesn't create display
    public void makeComment(Comment comment){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        CommentDao commentdao = db.CommentDao();
        new Thread(() -> {
        commentdao.insertAll(comment);
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makePost(Post post){
        int[] reactions = getReactions(post);

        feed_post fragment = feed_post.newInstance(post, reactions);

        sManager.beginTransaction().add(R.id.postFeed, fragment, String.valueOf(post.id)).commit();
    }

    public void hideFeed(){
        // Show button again
        findViewById(R.id.postButton).setVisibility(View.GONE);
        findViewById(R.id.postFeed).setVisibility(View.GONE);
    }

    public void showFeed(){
        // Show button again
        findViewById(R.id.postButton).setVisibility(View.VISIBLE);
        findViewById(R.id.postFeed).setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeReaction(int post_id, int type, String user_id){
        System.out.println("Reaction attempt");
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        ReactionDao reactiondao = db.ReactionDao();
        String stamp = OffsetDateTime.now().toString();
        new Thread(()->{

            Reaction reaction = new Reaction();
            reaction.post_id = post_id;
            reaction.user_id = user_id;
            reaction.type = type;
            reaction.stamp = stamp;
            /*
                TODO : This works, but can sometimes crash the app after a clean wipe of DB,
                 it can possibly not actually be a crash, but the Layout reloads as if it were a crash
             */
            int dbReturn = reactiondao.getReactionById(post_id,user_id);
            System.out.println(dbReturn);
                // No current reaction exists on this post by this user
            System.out.println("post_id : " + post_id + " user_id: "+ user_id);
            if(dbReturn != 0){
                System.out.println("Existing reaction updated");
                reactiondao.updateReaction(post_id,user_id,type);
            // Update reac
            } else{
                System.out.println("New reaction posted");
                reactiondao.insertReactions(reaction);
            }
            db.close();
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int[] getReactions(Post post){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        ReactionDao reactiondao = db.ReactionDao();
        // hopefully this shitcode works
        CompletableFuture<List<Reaction>> reactions = CompletableFuture.supplyAsync(() -> reactiondao.getReactions(post.id));
        try{
            List<Reaction> list = reactions.get();
            int[] reactionsList = {0,0,0,0};
            for(Reaction r:list){
                reactionsList[r.type-1]++;
            }
            return reactionsList;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Comment> getComments(int post_id){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        CommentDao commentdao = db.CommentDao();
        CompletableFuture<List<Comment>> result = CompletableFuture.supplyAsync(() -> commentdao.getAllFromPostId(post_id));
        try{
            return result.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearFeed(){
        ((ViewGroup)findViewById(R.id.postFeed)).removeAllViews();
    }
}