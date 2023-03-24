package domain.friendship;

import domain.user.User;
import repository.HasID;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class Friendship implements HasID<Set<User>> {
    private User user1;
    private User user2;
    private LocalDateTime friendsFrom;
    private Status status;

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Friendship(User user1, User user2, Status status) {
        this.user1 = user1;
        this.user2 = user2;
        this.status = status;
        if(status==Status.ACCEPTED)
            friendsFrom = LocalDateTime.now();
    }
    public Friendship(User user1,User user2,Status status, LocalDateTime friendsFrom){
        this.user1 = user1;
        this.user2 = user2;
        this.status = status;
        this.friendsFrom =friendsFrom;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }


    @Override
    public String toString(){
        if(this.status==Status.ACCEPTED)
            return user1.getUsername()+" is friend with "+user2.getUsername()+" since "+friendsFrom.format(DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss6"))+"!";
        if(this.status==Status.REJECTED)
            return user2.getUsername()+" rejected the friend request from "+user1.toString()+"...";
        else
            return user1.getUsername()+" is waiting for "+user2.getUsername()+" to accept the request!";
    }
    @Override
    public Set getID() {
        Set<User> id = new HashSet<>();
        id.add(this.user1);
        id.add(this.user2);
        return id;
    }

    @Override
    public void setID(Set hashSet) {

    }
}
