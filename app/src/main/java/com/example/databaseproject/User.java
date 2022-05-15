package com.example.databaseproject;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.sql.Timestamp;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String id;

    public String name;

    public String timestamp;

    public User(){
    }

    public User(String id, String name, String timestamp) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
    }

}