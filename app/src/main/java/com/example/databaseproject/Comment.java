package com.example.databaseproject;


import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = User.class, parentColumns = "id", childColumns = "user_id"),
        @ForeignKey(onDelete = CASCADE, entity = Post.class, parentColumns = "id", childColumns = "post_id")
})

public class Comment {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String user_id;
    public int post_id;

    public String content;
    public String stamp;
}
