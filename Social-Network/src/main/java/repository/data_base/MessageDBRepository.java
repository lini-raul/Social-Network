package repository.data_base;

import domain.friendship.Friendship;
import domain.friendship.Status;
import domain.message.Message;
import domain.user.User;
import repository.Repository;
import validators.RepositoryException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageDBRepository implements Repository<Message, Set<Object>> {

    private String url;
    private String userName;
    private String password;

    public MessageDBRepository(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    /**
     * save a message into the database
     * @param entity
     * @return the saved entity
     * @throws RepositoryException when the message already exists
     */
    @Override
    public Message save(Message entity) throws RepositoryException{
        String sql = "INSERT INTO message(sender,receiver,message,\"dateTime\") VALUES (?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, entity.getSender());
            ps.setString(2, entity.getReceiver());
            ps.setString(3, entity.getMessage());
            ps.setString(4, entity.getDateTime().toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Entity already exists!");
        }
        return entity;
    }

    @Override
    public Message delete(Set<Object> message_id) {
        return null;
    }

    //DONE
    @Override
    public int size() {
        int size;
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(message.sender) AS size FROM message");
            ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();       //need 'next()' to get to the first set
            size = resultSet.getInt("size");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return size;
    }

    @Override
    public Message findOne(Set<Object> objects) throws RepositoryException {
        return null;
    }

    //DONE
    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        Message Message;
        try(Connection connection = DriverManager.getConnection(url,userName,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM message");
            ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()) {
                String sender = resultSet.getString("sender");
                String receiver = resultSet.getString("receiver");
                String message = resultSet.getString("message");
                LocalDateTime dateTime = LocalDateTime.parse(resultSet.getString("dateTime"));

                Message=new Message(sender,receiver,message,dateTime);

                messages.add(Message);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Message update(Message oldEntity, Message newEntity) throws RepositoryException {
        return null;
    }
}
