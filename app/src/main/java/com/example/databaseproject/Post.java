package com.example.databaseproject;
import androidx.room.*;


@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))

public class Post {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String user_id;
    public String content;
    public String stamp;
    public String image;

    public Post(){

    }

    public Post(String user_id, String content, String stamp, String image){
        this.user_id = user_id;
        this.content = content;
        this.stamp = stamp;
        this.image = image;
    }


    // Initialize existing post with id
    public Post(int id, String user_id, String content, String stamp){

        this.id = id;
        this.user_id = user_id;
        this.content = content;
        this.stamp = stamp;
    }


}
