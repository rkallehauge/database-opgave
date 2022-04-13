package com.example.databaseproject;
import java.sql.Timestamp;
import androidx.room.*;

@Entity(foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "name", childColumns = "user_id", onDelete = CASCADE),
        @ForeignKey(entity = Post.class, parentColumns = "id", childColumns = "post_id", onDelete = CASCADE)
})

public class Reaction {

    public int type;
    public Timestamp stamp;

}
