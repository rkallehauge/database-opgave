package com.example.databaseproject;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Post.class, parentColumns = "id", childColumns = "post_id")
},
        primaryKeys = {
                "post_id"
        })

public class ImageAttachment {

    public int post_id;
    public String path;

}
