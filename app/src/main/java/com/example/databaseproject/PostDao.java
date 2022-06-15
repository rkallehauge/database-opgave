package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface PostDao {

    // Insert post
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Post post);

    // Insert all posts
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Post> posts);

    // Select all Posts.
    @Query("SELECT * FROM Post ORDER BY stamp DESC")
    List<Post> getAll();

    //Remove all post not in remote IE they have been deleted from another app
    @Query("DELETE FROM Post WHERE id NOT IN (:postIdsInRemote)")
    void removeAllNotInRemote(List<Integer> postIdsInRemote);

    //Find new id
    @Query("SELECT MAX(id) FROM Post")
    int largestId();
}
