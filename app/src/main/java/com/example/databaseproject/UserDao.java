@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(String[] userIds);

    @Query("SELECT * FROM user WHERE name LIKE :name LIMIT 1")
    User findByName(String name);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}