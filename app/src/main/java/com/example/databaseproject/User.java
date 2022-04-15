package com.example.databaseproject;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.sql.Timestamp;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String name;

    public String username;

    public String full_name;

    public long timestamp;
}