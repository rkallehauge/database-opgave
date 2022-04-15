package com.example.databaseproject;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String name;

    public String username;

    public String full_name;

}