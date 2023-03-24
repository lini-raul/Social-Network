package repository.file;

import domain.user.User;
import validators.RepositoryException;
import validators.Validator;

import java.io.FileNotFoundException;
import java.util.List;

public class UserFileRepository extends AbstractFileRepository<User, String>{
    public UserFileRepository(String filename, Validator<User> validator) throws RepositoryException, FileNotFoundException {
        super(filename, validator);

    }

    @Override
    public User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(0), attributes.get(1), attributes.get(2));
        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getUsername()+";"+entity.getName()+";"+entity.getPassword();
    }
}
