package com.example.databaseproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReactionDao {

    @Insert
    void insertReactions(Reaction... reactions);

    @Query("INSERT INTO Reaction (post_id, user_id, stamp, type) VALUES( :post_id,:user_id,:stamp,:type)")
    void insert(int post_id, String user_id, String stamp, int type);

    // Get reactions of post
    @Query("SELECT * FROM Reaction WHERE 'post_id' = :post_id")
    List<Reaction> getReactions(int post_id);

    @Query("SELECT * FROM Reaction WHERE 'post_id' = :post_id AND 'user_id' = :user_id")
    List<Reaction> getReactionById(int post_id, String user_id);

    // TODO : make userid and postid get
    // Delete reaction
    @Delete
    void delete(Reaction reaction);

    // Delete all reactions from post
    @Query("DELETE FROM Reaction WHERE 'post_id' = :post_id")
    int deleteReactions(int post_id);

    // Change reaction
    @Query("UPDATE Reaction SET 'type' = :type WHERE 'post_id' = :post_id AND 'user_id' = :user_id")
    int updateReaction(int post_id, String user_id, int type);
}

