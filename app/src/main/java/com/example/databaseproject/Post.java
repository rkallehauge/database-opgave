package com.example.databaseproject;
import androidx.room.*;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))
public class Post {

    @PrimaryKey
    public int id;

    public String user_id;
    public String content;
    public Integer stamp;

}
