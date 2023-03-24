package service;

import domain.friendship.Friendship;
import domain.friendship.FriendshipDto;
import domain.friendship.Status;
import domain.message.Message;
import domain.user.User;
import repository.Repository;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.FriendshipValidator;
import validators.RepositoryException;
import validators.ValidationException;
import validators.Validator;
import utils.Graph;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Network implements Observable {
    private Repository<User,String> userRepo;
    private Repository<Friendship, Set<User>> friendshipRepo;
    private Repository<Message,Set<Object>> messagesRepo;
    private Validator<User> userValidator;
    private Validator<Friendship> friendshipValidator;

    public Network(Repository<User, String> userRepo, Repository<Friendship, Set<User>> friendshipRepo, Repository<Message,Set<Object>> messagesRepo, Validator<User> userValidator, Validator<Friendship> friendshipValidator) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.messagesRepo = messagesRepo;
        this.userValidator = userValidator;
        this.friendshipValidator = friendshipValidator;
    }

    /**
     * creates an user and adds it in the userRepo
     * @param username
     * @param name
     * @throws ValidationException if the user is not valid
     * @throws RepositoryException if the user is null or if it already exists in the userRepo
     */
    public void addUser(String username,String name, String password) throws ValidationException, RepositoryException {
        User user = new User(username,name,password);
        userValidator.validate(user);
        userRepo.save(user);
    }

    /**
     * deletes an user from the userRepo and all the friendships associated with
     * @param username
     * @throws RepositoryException if the user does not exist
     */
    public void deleteUser(String username) throws RepositoryException{
        User user = userRepo.findOne(username);
        for(Friendship f : friendshipRepo.findAll()){
            if(f.getUser1().equals(user) || f.getUser2().equals(user)){
                friendshipRepo.delete(f.getID());
            }
        }
        userRepo.delete(username);
    }

    /**
     *
     * @return the list of the users
     */
    public List<User> getAllUsers() { return userRepo.findAll();}

    /**
     *
     * @param userID
     * @return the user with ID userID
     * @throws RepositoryException if there is no user with ID userID
     */
    public User findUser(String userID) throws RepositoryException { return userRepo.findOne(userID);
    }

    /**
     *
     * @param oldUsername old user's username
     * @param username new user's username
     * @param name new user's name
     * @param password new user's password
     * @return the old user
     * @throws RepositoryException if the user with oldUsername does not exists
     * @throws ValidationException if new user is not valid
     */
    public User updateUser(String oldUsername,String username,String name, String password) throws RepositoryException, ValidationException{
        User oldUser = userRepo.findOne(oldUsername);
        User newUser = new User(username,name,password);
        userValidator.validate(newUser);
        userRepo.update(oldUser,newUser);
        return oldUser;

    }

    /**
     * updates the friendship after a user has been updated
     * @param oldUser
     * @param newUser
     * @throws RepositoryException
     */
    public void refreshFriendshipsAfterUserUpdate(User oldUser,User newUser) throws RepositoryException {
        List<Friendship> friendships = friendshipRepo.findAll();
        for(Friendship friendship : friendships)
        {
            if(friendship.getUser1().equals(oldUser)){
                Friendship newFriendship = new Friendship(newUser, friendship.getUser2(), friendship.getStatus());
                friendshipRepo.update(friendship,newFriendship);
            }
            if(friendship.getUser2().equals(oldUser)){
                Friendship newFriendship = new Friendship(friendship.getUser1(),newUser,friendship.getStatus());
                friendshipRepo.update(friendship,newFriendship);
            }
        }
    }

    /**
     * makes 2 users friends
     * @param username1
     * @param username2
     * @throws ValidationException if a user tries to be friends with himself
     * @throws RepositoryException if the users are not found
     */
    public void addFriendship(String username1, String username2,Status status) throws ValidationException, RepositoryException {
        User user1 = userRepo.findOne(username1);
        User user2 = userRepo.findOne(username2);
        Friendship friendship = new Friendship(user1,user2, status);
        friendshipValidator.validate(friendship);
        friendshipRepo.save(friendship);
        notifyObservers();
    }

    /**
     * removes the friendship between 2 users
     * @param username1
     * @param username2
     * @throws RepositoryException if the users do not exist
     */
    public void deleteFriendship(String username1,String username2) throws RepositoryException {
        User user1 = userRepo.findOne(username1);
        User user2 = userRepo.findOne(username2);
        Set<User> id = new HashSet<>();
        id.add(user1);
        id.add(user2);
        friendshipRepo.findOne(id);
        friendshipRepo.delete(id);
        notifyObservers();

    }

    public void updateFriendship(String oldUserID_1, String oldUserID_2, String newUserID_1, String newUserID_2) throws RepositoryException {
        User oldUser_1 = userRepo.findOne(oldUserID_1);
        User oldUser_2 = userRepo.findOne(oldUserID_2);
        User newUser_1 = userRepo.findOne(newUserID_1);
        User newUser_2 = userRepo.findOne(newUserID_2);
        Set<User> oldSet = new HashSet<>();
        oldSet.add(oldUser_1);
        oldSet.add(oldUser_2);
        Friendship oldFriendship = friendshipRepo.findOne(oldSet);
        Friendship newFriendship = new Friendship(newUser_1,newUser_2,oldFriendship.getStatus());

        friendshipRepo.update(oldFriendship,newFriendship);
    }

    //updates the status of a friendship taking care of the friendsFrom attribute
    public void updateFriendshipStatus(Friendship friendship,Status status) throws RepositoryException {
        Friendship newFriendship = friendship;
        if(status.equals(Status.ACCEPTED)){
            newFriendship.setStatus(status);
            newFriendship.setFriendsFrom(LocalDateTime.now());
        }
        else {
            newFriendship.setStatus(status);
            newFriendship.setFriendsFrom(null);
        }
        friendshipRepo.update(friendship,newFriendship);
        notifyObservers();
    }

    public Friendship findFriendship(User user1,User user2) throws RepositoryException {
        Set<User> uSet = new HashSet<>();
        uSet.add(user1);
        uSet.add(user2);
        Friendship friendship = friendshipRepo.findOne(uSet);

        return friendship;
    }

    /**
     *
     * @return the list of friendships
     * @throws RepositoryException
     */
    public List<Friendship> getAllFriendships()  { return friendshipRepo.findAll();}


    //returns a list with the FriendshipDtos of an user
    public List<FriendshipDto> getFriendshipsOfUser(User user){
        List<FriendshipDto> friendshipsDto = new ArrayList<>();
        for(Friendship friendship: friendshipRepo.findAll()){
            if(friendship.getUser1().equals(user) && friendship.getStatus().equals(Status.ACCEPTED) ){
                FriendshipDto friendshipDto = new FriendshipDto(friendship.getUser2().getUsername(),friendship.getFriendsFrom(),friendship.getStatus());
                friendshipsDto.add(friendshipDto);
            }
            if(friendship.getUser2().equals(user) && friendship.getStatus().equals(Status.ACCEPTED)){
                FriendshipDto friendshipDto = new FriendshipDto(friendship.getUser1().getUsername(),friendship.getFriendsFrom(),friendship.getStatus());
                friendshipsDto.add(friendshipDto);
            }
        }
        return friendshipsDto;
    }

    public void addMessage(Message message) throws RepositoryException {
        messagesRepo.save(message);
    }

    //returns a list with the SEND friend requests of an User
    public List<FriendshipDto> getRequestsSentOfUser(User user){
        List<FriendshipDto> sentRequests = new ArrayList<>();
        for(Friendship friendship : friendshipRepo.findAll()){
            if(friendship.getUser1().equals(user) && friendship.getStatus().equals(Status.SEND)) {
                FriendshipDto friendshipDto = new FriendshipDto(friendship.getUser2().getUsername(),friendship.getFriendsFrom(),friendship.getStatus());
                sentRequests.add(friendshipDto);
            }
        }
        return sentRequests;
    }

    //returns a list with all the users that are not friends with a specific user
    public List<User> getNonFriendsOfUser(User user) {
        List<User> users = new ArrayList<>();
        for(User u: getAllUsers()){
            if(!user.equals(u))
            {
                Set<User> uSet = new HashSet<>();
                uSet.add(u);
                uSet.add(user);
                try{
                    friendshipRepo.findOne(uSet);
                } catch (RepositoryException e) {
                    users.add(u);
                }
            }
        }
        return users;
    }

    public List<User> getFriendRequestsOfUser(User user) {
        List<User> users = new ArrayList<>();
        for(User u: getAllUsers()){
            if(!user.equals(u))
            {
                Set<User> uSet = new HashSet<>();
                uSet.add(u);
                uSet.add(user);
                try {
                    Friendship friendship = friendshipRepo.findOne(uSet);
                    if(friendship.getStatus().equals(Status.SEND) && friendship.getUser2().equals(user)){
                        users.add(u);
                    }
                } catch (RepositoryException e) {

                }
            }
        }
        return users;
    }

    public List<Message> getMessagesOfUsers(User user1,User user2){
        List<Message> messages = messagesRepo.findAll();
        List<Message> filteredMessages = new ArrayList<>();
        for(Message m : messages)
        {
            if( (m.getSender().equals(user1.getUsername()) && m.getReceiver().equals(user2.getUsername()))
            || (m.getSender().equals(user2.getUsername()) && m.getReceiver().equals(user1.getUsername())))
            {
                filteredMessages.add(m);
            }
        }

        return filteredMessages;
    }

    /**
     *
     * @return the number of communities between users
     */
    public int numberOfCommunities() {
        List<User> users = userRepo.findAll();
        int vCount = users.size();

        int[][] adj = new int[vCount][vCount];
        for (int i = 0; i < vCount; i++) {
            for (int j = 0; j < vCount; j++) {
                adj[i][j] = 0;
            }
        }

        boolean[] visited = new boolean[vCount];
        for (int i = 0; i < vCount; i++) {
            visited[i] = false;
        }

        friendshipsToGraph(adj,vCount);

        int communities = 0;
        for (int i = 0; i < vCount; i++) {
            if (!visited[i]) {
                Graph.dfs(adj, visited, vCount, i);
                communities++;
            }
        }
        return communities;
    }

    private void friendshipsToGraph(int[][] adj, int vCount) {
        List<User> users = userRepo.findAll();
        for (int i = 0; i < vCount; i++) {
            for (int j = 0; j < vCount; j++) {
                try {
                    Set<User> ID = new HashSet<>();
                    ID.add(users.get(i));
                    ID.add(users.get(j));
                    friendshipRepo.findOne(ID);
                    adj[i][j] = 1;
                    adj[j][i] = 1;
                } catch (RepositoryException exception) {}
            }
        }
    }


    private List<Observer> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers() {
        observers.stream().forEach(x->x.update());
    }


}
