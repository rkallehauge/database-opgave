package com.example.databaseproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertReactions(Reaction... reactions);

    // Get reactions of post
    @Query("SELECT * FROM Reaction WHERE post_id = :post_id")
    List<Reaction> getReactions(int post_id);

    @Query("SELECT COUNT(type) FROM Reaction WHERE user_id = :user_id AND post_id = :post_id")
    int getReactionIdById(int post_id, String user_id);

    // Change reaction
    @Query("UPDATE Reaction SET type = :type WHERE post_id = :post_id AND user_id = :user_id")
    int updateReaction(int post_id, String user_id, int type);
}

