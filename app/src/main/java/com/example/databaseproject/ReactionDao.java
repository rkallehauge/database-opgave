package com.example.databaseproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReactionDao {

    // Get reactions of post
    @Query("SELECT * FROM reaction WHERE 'post_id' = :post_id")
    List<Reaction> getReactions(int post_id);

    // Delete reaction
    @Delete
    void delete(Reaction reaction);

    // Delete all reactions from post
    @Query("DELETE FROM reaction WHERE 'post_id' = :post_id")
    int deleteReactions(int post_id);

    // Change reaction
    @Query("UPDATE reaction SET 'type' = :type WHERE 'post_id' = :post_id AND 'user_id' = :user_id")
    int updateReaction(int post_id, String user_id, int type);
}

