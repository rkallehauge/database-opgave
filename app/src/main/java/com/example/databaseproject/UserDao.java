package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface UserDao {

    // Select all users.
    @Query("SELECT * FROM user")
    List<User> getAll();

    // Select all users by id.
    @Query("SELECT * FROM user WHERE name IN (:userIds)")
    List<User> loadAllByIds(String[] userIds);

    // Select all users where name is...
    @Query("SELECT * FROM user WHERE name LIKE :name LIMIT 1")
    User findByName(String name);

    // Insert all users.
    @Insert
    void insertAll(User... users);

    // Delete user.
    @Delete
    void delete(User user);

    @Query("SELECT id FROM User WHERE id = :userId")
    String findUserById(String userId);

    @Query("INSERT INTO User VALUES( :id,:user,:time)")
    void insert(String id, String user,String time);

}