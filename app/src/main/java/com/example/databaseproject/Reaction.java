package com.example.databaseproject;
import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "name", childColumns = "user_id"),
        @ForeignKey(entity = Post.class, parentColumns = "id", childColumns = "post_id")
},
    primaryKeys = {
        "post_id",
        "user_id"
    }
)

public class Reaction {
    @NonNull
    public int post_id;
    @NonNull
    public String user_id;

    public int type;
    public Integer stamp;

}
