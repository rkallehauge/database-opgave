package com.example.databaseproject;

import androidx.room.*;

@Entity
public class User {
    @PrimaryKey
    public String name;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "full_name")
    public String full_name;

}