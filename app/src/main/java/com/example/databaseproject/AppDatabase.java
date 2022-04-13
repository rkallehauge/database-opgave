@Database(entities = {User.class}, version = 1)
// Post.class, Reaction.class to be added
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    // given for all Database Access Objects, so Reactions, posts aswell
}