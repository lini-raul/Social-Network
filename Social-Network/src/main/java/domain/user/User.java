package domain.user;

import repository.HasID;

import java.util.Objects;

public class User implements HasID<String> {
    private String username;
    private String name;
    private String password;

    public User(String username, String name,String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {return password;}

    public void setPassword(String password) { this.password = password; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(name, user.name) && Objects.equals(password,user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name);
    }

    @Override
    public String toString() {
        return "User-ul " + username + " are numele " + name;
    }


    @Override
    public String getID() {
        return  username;
    }

    @Override
    public void setID(String s) {
        setUsername(s);
    }
}
