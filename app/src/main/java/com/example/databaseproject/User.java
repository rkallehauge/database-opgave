@Entity
public class User {
    @PrimaryKey
    public String uid;

    @ColumnInfo(name = "username")
    public String username;

}