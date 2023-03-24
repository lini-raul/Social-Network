package domain.friendship;

import java.time.LocalDateTime;

public class FriendshipDto {
    private String username;

    private LocalDateTime friendsFrom;
    private Status status;

    public FriendshipDto(String username, LocalDateTime friendsFrom, Status status) {
        this.username = username;
        this.friendsFrom = friendsFrom;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }


    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public Status getStatus() {
        return status;
    }
}
