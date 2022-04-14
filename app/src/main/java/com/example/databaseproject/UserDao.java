package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface UserDao {

    @Query("SELECT id FROM User WHERE id = :userId")
    String findUserById(String userId);

    @Query("INSERT INTO User VALUES( :id,:user,:time)")
    void insert(String id, String user,Long time);

}