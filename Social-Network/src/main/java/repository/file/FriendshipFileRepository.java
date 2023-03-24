package repository.file;

import domain.friendship.Friendship;
import domain.friendship.Status;
import domain.user.User;
import validators.RepositoryException;
import validators.Validator;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class FriendshipFileRepository extends AbstractFileRepository<Friendship, Set<User>>{
    public FriendshipFileRepository(String filename, Validator<Friendship> validator) throws RepositoryException, FileNotFoundException {
        super(filename,validator);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {

        if(attributes.get(6).equals("ACCEPTED"))
        {
            User user1 = new User(attributes.get(0),attributes.get(1),attributes.get(2));
            User user2 = new User(attributes.get(3),attributes.get(4),attributes.get(5));
            String str = attributes.get(7);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss6");
            //LocalDateTime friendsFrom = LocalDateTime.parse(str, formatter);
            LocalDateTime friendsFrom = LocalDateTime.parse(str);
            Friendship friendship = new Friendship(user1, user2, Status.ACCEPTED, friendsFrom);
            return friendship;
        }
        User user1 = new User(attributes.get(0),attributes.get(1),attributes.get(2));
        User user2 = new User(attributes.get(3),attributes.get(4),attributes.get(5));
        if(attributes.get(6).equals("SEND"))
        {
            Friendship friendship = new Friendship(user1, user2, Status.SEND);
            return friendship;
        }
        Friendship friendship = new Friendship(user1, user2, Status.REJECTED);
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        if(entity.getStatus() == Status.ACCEPTED)
            return entity.getUser1().getUsername()+";"+entity.getUser1().getName()+";"+entity.getUser1().getPassword()+";"+entity.getUser2().getUsername()+";"+entity.getUser2().getName()+";"+entity.getUser2().getPassword()+";"+entity.getStatus()+";"+ entity.getFriendsFrom();
        return entity.getUser1().getUsername()+";"+entity.getUser1().getName()+";"+entity.getUser1().getPassword()+";"+entity.getUser2().getUsername()+";"+entity.getUser2().getName()+";"+entity.getUser2().getPassword()+";"+entity.getStatus();
    }
}
