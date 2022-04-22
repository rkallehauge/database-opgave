package com.example.databaseproject;

import java.util.List;
import androidx.room.*;

@Dao
public interface UserDao {

    // Select all users.
    @Query("SELECT * FROM user")
    List<User> getAll();

    // Select all users by id.
    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    List<User> loadAllByIds(String[] userIds);

    // Select all users where name is...
    @Query("SELECT * FROM user WHERE id LIKE :name LIMIT 1")
    User findByName(String name);

    // Insert all users.
    @Insert
    void insertAll(User... users);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    // Delete user.
    @Delete
    void delete(User user);

    @Query("SELECT id FROM User WHERE id = :userId")
    String findUserById(String userId);



}