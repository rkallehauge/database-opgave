package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface PostDao {
    @Insert
    void insertAll(Post... posts);

    @Delete
    void delete(Post posts);

    @Update
    void update(Post... posts);

    @Query("SELECT * FROM Post")
    List<Post> getAll();
}
