package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface UserDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);

    @Query("SELECT id FROM User WHERE id = :userId")
    String findUserById(String userId);

    //Remove all users not in remote IE they have been deleted from another app
    @Query("DELETE FROM User WHERE id NOT IN (:userIdsInRemote)")
    void removeAllNotInRemote(List<String> userIdsInRemote);

}