import net.jini.core.entry.Entry;

public class User implements Entry {
    public String username;
    public String password;

    public User(){}

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

}
