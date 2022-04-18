package com.example.databaseproject;
import androidx.room.*;


@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))

public class Post {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String user_id;
    public String content;
    public String stamp;

    public Post(){

    }

    public Post(String user_id, String content, String stamp){
        this.user_id = user_id;
        this.content = content;
        this.stamp = stamp;
    }


}
