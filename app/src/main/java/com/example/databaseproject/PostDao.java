package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface PostDao {

    // Insert all posts
    @Insert
    void insertAll(Post... posts);

    // Delete post
    @Delete
    void delete(Post posts);

    // Update post
    @Update
    void update(Post... posts);

    // Select all Posts.
    @Query("SELECT * FROM Post")
    List<Post> getAll();
}
