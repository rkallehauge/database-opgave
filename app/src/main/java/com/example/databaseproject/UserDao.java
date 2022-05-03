package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    @Query("SELECT id FROM User WHERE id = :userId")
    String findUserById(String userId);



}