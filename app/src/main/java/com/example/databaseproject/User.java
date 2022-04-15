package com.example.databaseproject;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.sql.Timestamp;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String id;

    public String username;

    public long timestamp;
}