package com.example.databaseproject;


import java.util.List;
import androidx.room.*;

@Dao
public interface CommentDao {

    @Query("SELECT * FROM Comment WHERE post_id = :post_id")
    List<Comment> getAllFromPostId(int post_id);

    @Insert
    void insert(Comment comment);

}
