package com.example.databaseproject;
import java.sql.Timestamp;
import androidx.room.*;

@Entity(foreignKeys = @ForeignKey(entity = User.class,parentColumns = "name", childColumns = "username"))
public class Post {
    @PrimaryKey
    public int id;

    public String username;
    public String content;
    public Timestamp stamp;
}
