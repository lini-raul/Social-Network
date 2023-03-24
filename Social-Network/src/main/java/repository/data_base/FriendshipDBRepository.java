package repository.data_base;

import domain.friendship.Friendship;
import domain.friendship.Status;
import domain.user.User;
import repository.Repository;
import validators.RepositoryException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FriendshipDBRepository implements Repository<Friendship, Set<User>> {
    private String url;
    private String userName;
    private String password;

    public FriendshipDBRepository(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Friendship save(Friendship entity) throws RepositoryException {
        if(entity.getFriendsFrom()==null)
        {
            String sql = "INSERT INTO friendship (username1,name1,password1,username2,name2,password2,status) VALUES (?,?,?,?,?,?,?)";
            try(Connection connection = DriverManager.getConnection(url,userName,password);
                PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1,entity.getUser1().getUsername());
                ps.setString(2,entity.getUser1().getName());
                ps.setString(3,entity.getUser1().getPassword());
                ps.setString(4,entity.getUser2().getUsername());
                ps.setString(5,entity.getUser2().getName());
                ps.setString(6,entity.getUser2().getPassword());
                ps.setString(7,entity.getStatus().toString());

                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Entity already exists!");
            }
        }
        else{
            String sql = "INSERT INTO friendship (username1,name1,password1,username2,name2,password2,status,friendsFrom) VALUES (?,?,?,?,?,?,?,?)";
            try(Connection connection = DriverManager.getConnection(url,userName,password);
                PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1,entity.getUser1().getUsername());
                ps.setString(2,entity.getUser1().getName());
                ps.setString(3,entity.getUser1().getPassword());
                ps.setString(4,entity.getUser2().getUsername());
                ps.setString(5,entity.getUser2().getName());
                ps.setString(6,entity.getUser2().getPassword());
                ps.setString(7,entity.getStatus().toString());
                ps.setString(8,entity.getFriendsFrom().toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Entity already exists!");
            }
        }

        return null;
    }

    @Override
    public Friendship delete(Set<User> users) {
        Friendship deleted_friendship;
        try {
            deleted_friendship=findOne(users);
        } catch (RepositoryException e) {
            return null;
        }

        String sql = "DELETE FROM friendship F WHERE (F.username1=? AND F.username2=?) OR (F.username1=? AND F.username2=?)";
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement ps = connection.prepareStatement(sql)){
            List<User> list = new ArrayList<>(users);
            String username1 = list.get(0).getUsername();
            String username2 = list.get(1).getUsername();
            ps.setString(1,username1);
            ps.setString(2,username2);
            ps.setString(3,username2);
            ps.setString(4,username1);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return deleted_friendship;
    }

    @Override
    public int size() {
        int size;
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(friendship.username1) AS size FROM friendship");
            ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();       //need 'next()' to get to the first set
            size = resultSet.getInt("size");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return size;
    }

    @Override
    public Friendship findOne(Set<User> users) throws RepositoryException {
        Friendship found_friendship;
        List<User> list = new ArrayList<>(users);
        String username1 = list.get(0).getUsername();
        String username2 = list.get(1).getUsername();


        String sql = "SELECT * FROM friendship F WHERE (f.username1=? AND F.username2=?) OR (f.username1=? AND F.username2=?)";
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1,username1);
            ps.setString(2,username2);
            ps.setString(3,username2);
            ps.setString(4,username1);
            ResultSet  resultSet = ps.executeQuery();
            resultSet.next();   //need 'next()' to get to the first set
            username1 = resultSet.getString("username1");
            String name1 = resultSet.getString("name1");
            String password1 = resultSet.getString("password1");
            User user1 = new User(username1,name1,password1);

            username2 = resultSet.getString("username2");
            String name2 = resultSet.getString("name2");
            String password2 = resultSet.getString("password2");
            User user2 = new User(username2,name2,password2);

            Status status = Status.valueOf(resultSet.getString("status"));
            //System.out.println(resultSet.getString("friendsfrom"));
            if(resultSet.getString("friendsfrom")!=null){
                LocalDateTime friendsFrom = LocalDateTime.parse(resultSet.getString("friendsfrom"));
                found_friendship = new Friendship(user1,user2, status,friendsFrom);

            }
            else{
                found_friendship = new Friendship(user1,user2, status);
            }

        } catch (SQLException e) {
            //throw new RuntimeException(e);
            throw new RepositoryException("Entity does not exist!");
        }
        return found_friendship;
    }

    @Override
    public List<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        Friendship friendship;
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendship");
            ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()) {
                String username1 = resultSet.getString("username1");
                String name1 = resultSet.getString("name1");
                String password1 = resultSet.getString("password1");
                User user1 = new User(username1,name1,password1);

                String username2 = resultSet.getString("username2");
                String name2 = resultSet.getString("name2");
                String password2 = resultSet.getString("password2");
                User user2 = new User(username2,name2,password2);

                Status status = Status.valueOf(resultSet.getString("status"));

                if(resultSet.getString("friendsfrom")!=null){
                    LocalDateTime friendsFrom = LocalDateTime.parse(resultSet.getString("friendsfrom"));
                    friendship = new Friendship(user1,user2, status,friendsFrom);
                }
                else{
                    friendship = new Friendship(user1,user2, status);
                }
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships;
    }

    @Override
    public Friendship update(Friendship oldEntity, Friendship newEntity) throws RepositoryException {
        String sql = "UPDATE friendship F SET username1=?, name1=?, password1=?,username2=?, name2=?, password2=?, status=?,friendsfrom=? WHERE (f.username1=? AND F.username2=?) OR (f.username1=? AND F.username2=?)";
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,newEntity.getUser1().getUsername());
            ps.setString(2,newEntity.getUser1().getName());
            ps.setString(3,newEntity.getUser1().getPassword());
            ps.setString(4,newEntity.getUser2().getUsername());
            ps.setString(5,newEntity.getUser2().getName());
            ps.setString(6,newEntity.getUser2().getPassword());
            ps.setString(7,newEntity.getStatus().toString());
            ps.setString(8,newEntity.getFriendsFrom().toString());


            ps.setString(9,oldEntity.getUser1().getUsername());
            ps.setString(10,oldEntity.getUser2().getUsername());
            ps.setString(11,oldEntity.getUser2().getUsername());
            ps.setString(12,oldEntity.getUser1().getUsername());


            ps.executeUpdate();
        } catch (SQLException e) {
            //if(e.getMessage().equals("ERROR: duplicate key value violates unique constraint \"User_pk\""))
            throw new RepositoryException("Entity already exists!");
        }
        return newEntity;
    }
}
