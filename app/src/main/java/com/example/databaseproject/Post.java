package com.example.databaseproject;

import androidx.room.*;

@Entity(foreignKeys = @ForeignKey(entity = User.class,parentColumns = "name", childColumns = "username", onDelete = CASCADE))
public class Post {
    @PrimaryKey
    public int id;

    public String username;
    public String content;

}
