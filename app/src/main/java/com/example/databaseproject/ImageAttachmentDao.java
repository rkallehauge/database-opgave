package com.example.databaseproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageAttachmentDao {

    //Insert into the db a new image path
    @Query("INSERT INTO ImageAttachment VALUES(:postId,:path)")
    void insert(int postId, String path);

    //Get the path to an image
    @Query("SELECT path FROM ImageAttachment WHERE post_id = :postId")
    String path(int postId);
}

