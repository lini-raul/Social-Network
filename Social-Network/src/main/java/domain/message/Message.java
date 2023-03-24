package domain.message;

import domain.user.User;
import repository.HasID;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Message implements HasID<Set<Object>>{

    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime dateTime;

    public Message(String sender, String receiver, String message, LocalDateTime dateTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.dateTime = dateTime;

    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(sender, message1.sender) && Objects.equals(receiver, message1.receiver) && Objects.equals(message, message1.message) && Objects.equals(dateTime, message1.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, message, dateTime);
    }

    @Override
    public Set<Object> getID() {
        Set<Object> id = new HashSet<>();
        id.add(this.sender);
        id.add(this.receiver);
        id.add(this.message);
        id.add(this.dateTime);
        return id;
    }

    @Override
    public void setID(Set<Object> objects) {

    }
}
