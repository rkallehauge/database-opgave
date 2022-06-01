package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface PostDao {

    // Insert all posts
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertAll(Post... posts);

    // Update post
    @Update
    void update(Post... posts);

    // Select all Posts.
    @Query("SELECT * FROM Post ORDER BY stamp DESC")
    List<Post> getAll();
}
