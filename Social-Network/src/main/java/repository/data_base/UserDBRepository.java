package repository.data_base;

import domain.user.User;
import repository.Repository;
import validators.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDBRepository implements Repository<User, String> {

    private String url;
    private String userName;
    private String password;

    public UserDBRepository(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    /**
     *save an user into the database
     * @param entity
     * @return the saved user
     * @throws RepositoryException when the username is already used
     */
    @Override
    public User save(User entity) throws RepositoryException {
        String sql = "INSERT INTO public.\"user\" (username,name,password) VALUES (?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,entity.getUsername());
            ps.setString(2, entity.getName());
            ps.setString(3,entity.getPassword());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Entity already exists!");
        }
        return entity;
    }

    /**
     *
     * @param s the username of the user we want to delete
     * @return the deleted user from the database
     */
    @Override
    public User delete(String s) {
        User deleted_user;

        try {
            deleted_user=findOne(s);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }


        String sql = "DELETE FROM public.user U WHERE U.username=?";
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,s);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deleted_user;
    }

    /**
     *
     * @return the number of users from the database
     */
    @Override
    public int size() {
        int size;
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(public.user.username) AS size FROM public.user");
            ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();       //need 'next()' to get to the first set
            size = resultSet.getInt("size");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } ;
        return size;
    }


    /**
     *
     * @param s
     * @return the user which has the username==s
     * @throws RepositoryException when there is no user with username s
     */
    @Override
    public User findOne(String s) throws RepositoryException {
        User found_user;
        String sql = "SELECT * FROM public.user U WHERE U.username=?";
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1,s);
            ResultSet  resultSet = ps.executeQuery();
            resultSet.next();      //need 'next()' to get to the first set
            String username = resultSet.getString("username");
            String name = resultSet.getString("name");
            String password = resultSet.getString("password");
            found_user = new User(username,name,password);

        } catch (SQLException e) {
            //throw new RuntimeException(e);
            throw new RepositoryException("Entity does not exist!");
        } ;
        return found_user;
    }

    /**
     *
     * @return a list with all the users from the database
     */
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM public.user");
            ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()) {
                String username = resultSet.getString("username");
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                User new_user = new User(username,name,password);
                users.add(new_user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } ;
        return users;
    }

    /**
     * replaces the user oldEntity with user newEntity
     * @param oldEntity user to be updated
     * @param newEntity the new user
     * @return the new user
     * @throws RepositoryException when the newEntity already exists in the database
     */
    @Override
    public User update(User oldEntity, User newEntity) throws RepositoryException {
        String sql = "UPDATE public.user U SET username=?, name=?, password=? WHERE U.username=?";
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,newEntity.getUsername());
            ps.setString(2, newEntity.getName());
            ps.setString(3,newEntity.getPassword());
            ps.setString(4,oldEntity.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            //if(e.getMessage().equals("ERROR: duplicate key value violates unique constraint \"User_pk\""))
            throw new RepositoryException("Entity already exists!");
        }

        return newEntity;
    }
}
