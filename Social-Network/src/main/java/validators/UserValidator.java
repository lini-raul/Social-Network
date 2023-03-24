package validators;

import domain.user.User;

public class UserValidator implements Validator<User>{


    /**
     * validates a user
     * @param entity
     * @throws ValidationException if the usernamer or the name are null
     */
    @Override
    public void validate(User entity) throws ValidationException {
        String errors = new String("");

        if(entity.getUsername().equals("") || entity.getUsername().trim().isEmpty()) {
            errors += "username cannot be null!\n";
        }
        if(entity.getName().equals("") || entity.getName().trim().isEmpty()){
            errors += "name cannot be null!\n";
        }
        if(entity.getPassword().equals("") || entity.getPassword().trim().isEmpty()){
            errors += "password cannot be null!\n";
        }

        if(!errors.equals(""))
            throw new ValidationException(errors);
    }
}
