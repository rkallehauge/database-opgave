package com.example.databaseproject;
import static androidx.room.ForeignKey.CASCADE;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = User.class, parentColumns = "id", childColumns = "user_id"),
        @ForeignKey(onDelete = CASCADE, entity = Post.class, parentColumns = "id", childColumns = "post_id")
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
    public String stamp;

    public Reaction(){}

    public Reaction(int pID,String uID, int type, String stamp) {
        this.post_id = pID;
        this.user_id = uID;
        this.type = type;
        this.stamp = stamp;
    }
}
