package com.example.databaseproject;
import static androidx.room.ForeignKey.CASCADE;

import androidx.room.*;



@Entity(foreignKeys = @ForeignKey(onDelete = CASCADE, entity = User.class, parentColumns = "id", childColumns = "user_id"))

public class Post {

    @PrimaryKey
    public int id;

    public String user_id;
    public String content;
    public String stamp;

    //Empty constructor
    public Post(){}

    //Constructor
    public Post(String user_id, String content, String stamp){
        this.user_id = user_id;
        this.content = content;
        this.stamp = stamp;
    }


    // Initialize existing post with id
    public Post(int id, String user_id, String content, String stamp){

        this.id = id;
        this.user_id = user_id;
        this.content = content;
        this.stamp = stamp;
    }


}
