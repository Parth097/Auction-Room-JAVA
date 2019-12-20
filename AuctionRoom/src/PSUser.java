import net.jini.core.entry.Entry;

public class PSUser implements Entry {
    public String username;
    public String password;

    public PSUser() {
    }

    public PSUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
