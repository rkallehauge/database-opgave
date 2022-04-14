package com.example.databaseproject;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.sql.Timestamp;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "timestamp")
    public Long timestamp; //Unix time
}