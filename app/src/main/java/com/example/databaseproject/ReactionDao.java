package com.example.databaseproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReactionDao {

    // Get reactions of post
    @Query("SELECT * FROM reaction WHERE 'post_id' = " + post.id)
    List<Reaction> getReactions(Post post);

    // Delete reaction
    @Delete
    void delete(Reaction reaction);

    // Delete all reactions from post
    @Query("DELETE FROM reaction WHERE 'post_id' = " + post.id)
    boolean deleteReactions(Post post);

    // Change reaction
    @Query("UPDATE reaction SET 'type' = :type WHERE 'post_id' = " + reaction.post_id + " AND 'user_id' = " + reaction.user_id)
    boolean updateReaction(Reaction reaction, int type);
}

