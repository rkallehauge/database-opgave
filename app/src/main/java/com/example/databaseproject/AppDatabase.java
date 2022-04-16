package com.example.databaseproject;

import androidx.room.*;

@Database(entities = {User.class, Post.class, Reaction.class}, version = 1)
// Post.class, Reaction.class to be added
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao UserDao();
    public abstract PostDao PostDao();
    public abstract ReactionDao ReactionDao();

    // given for all Database Access Objects, so Reactions, posts aswell
}